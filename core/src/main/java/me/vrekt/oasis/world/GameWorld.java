package me.vrekt.oasis.world;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.PerformanceCounter;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.combat.DamageType;
import me.vrekt.oasis.combat.EntityDamageAnimator;
import me.vrekt.oasis.entity.Entities;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.enemy.projectile.ProjectileManager;
import me.vrekt.oasis.entity.enemy.projectile.ProjectileResult;
import me.vrekt.oasis.entity.enemy.projectile.ProjectileType;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.entity.system.EntityInteractableAnimationSystem;
import me.vrekt.oasis.entity.system.EntityUpdateSystem;
import me.vrekt.oasis.graphics.tiled.MapRenderer;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.gui.cursor.MouseListener;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.utility.collision.BasicEntityCollisionHandler;
import me.vrekt.oasis.utility.collision.CollisionShapeCreator;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.effects.AreaEffectCloud;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.interior.misc.LockDifficulty;
import me.vrekt.oasis.world.network.WorldNetworkRenderer;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.SimpleWorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractionManager;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.items.DroppedItemInteraction;
import me.vrekt.oasis.world.systems.AreaEffectCloudManager;
import me.vrekt.oasis.world.systems.AreaEffectUpdateSystem;
import me.vrekt.oasis.world.systems.SystemManager;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Represents a base world within the game
 */
public abstract class GameWorld extends Box2dGameWorld implements WorldInputAdapter, Screen {

    protected final OasisGame game;
    protected final PlayerSP player;

    protected TiledMap map;
    protected String worldName, worldMap;

    protected final SpriteBatch batch;
    protected final MapRenderer renderer;
    protected final Vector3 cursorInWorld = new Vector3();
    protected boolean isWorldLoaded, isGameSave;
    protected boolean paused;

    protected GuiManager guiManager;
    protected final WorldNetworkRenderer networkRenderer;

    protected final IntMap<GameEntity> nearbyEntities = new IntMap<>();
    protected final Array<ParticleEffect> effects = new Array<>();

    protected final SystemManager systemManager;
    protected AreaEffectUpdateSystem effectUpdateSystem;
    protected AreaEffectCloudManager effectCloudManager;

    // destroyed world objects/entities used for saving
    protected final Bag<EntityType> deadEnemies = new Bag<>();
    protected final Bag<String> destroyedWorldObjects = new Bag<>();

    // objects within this world
    protected final Map<String, AbstractWorldObject> worldObjects = new HashMap<>();
    protected final Array<AbstractInteractableWorldObject> interactableWorldObjects = new Array<>();
    protected final Array<Vector2> paths = new Array<>();

    protected final EnumMap<InteriorWorldType, GameWorldInterior> interiorWorlds = new EnumMap<>(InteriorWorldType.class);

    // all mouse listeners
    protected final HashMap<MouseListener, Boolean> mouseListeners = new HashMap<>();

    protected final InteractionManager interactionManager;
    // last tick update, 50ms = 1 tick
    protected long lastTick;

    protected final EntityDamageAnimator worldDamageAnimator;
    protected final PerformanceCounter performanceCounter;
    protected WorldSaveLoader saveLoader;

    protected ProjectileManager projectileManager;
    protected ShapeRenderer debugRenderer;

    // indicates this world should be saved
    protected boolean hasVisited;

    public GameWorld(OasisGame game, PlayerSP player, World world) {
        super(world, new PooledEngine());

        this.player = player;
        this.game = game;
        this.renderer = game.getRenderer();
        this.batch = game.getBatch();
        this.guiManager = game.guiManager;
        this.interactionManager = new InteractionManager();
        this.networkRenderer = new WorldNetworkRenderer();
        this.projectileManager = new ProjectileManager();
        this.systemManager = new SystemManager();
        this.worldDamageAnimator = new EntityDamageAnimator();
        this.performanceCounter = new PerformanceCounter("GameWorldPerformanceCounter");
    }

    public String getWorldName() {
        return worldName;
    }

    public String getWorldMap() {
        return worldMap;
    }

    public PlayerSP player() {
        return player;
    }

    public OasisGame getGame() {
        return game;
    }

    public MapRenderer getRenderer() {
        return renderer;
    }

    public boolean isWorldLoaded() {
        return isWorldLoaded;
    }

    public boolean isInterior() {
        return false;
    }

    public Vector3 getCursorInWorld() {
        return cursorInWorld;
    }

    public PerformanceCounter getPerformanceCounter() {
        return performanceCounter;
    }

    public void setGameSave(boolean gameSave) {
        isGameSave = gameSave;
    }

    /**
     * @return save loader
     */
    public WorldSaveLoader loader() {
        if (saveLoader == null) this.saveLoader = new WorldSaveLoader(this);
        return saveLoader;
    }

    public void setHasVisited(boolean hasVisited) {
        this.hasVisited = hasVisited;
    }

    public boolean hasVisited() {
        return hasVisited;
    }

    /**
     * Check if a player is visible
     *
     * @param player player
     * @return {@code true} if the player is visible and in this world
     */
    public boolean isPlayerVisible(NetworkPlayer player) {
        return players.containsKey(player.entityId()) && player.isInView(getRenderer().getCamera());
    }

    /**
     * Initialize before loading
     */
    protected void init() {

    }

    /**
     * Invoked after loading is finished
     */
    protected void finalizeWorld() {

    }

    /**
     * Load network components if the world supports it.
     */
    protected void loadNetworkComponents() {

    }

    /**
     * Load this world
     * Should be overridden to provide the correct map and scaling
     */
    public void loadWorld(boolean isGameSave) {
        this.isGameSave = isGameSave;
    }

    /**
     * Create and initialize this world.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    protected void loadTiledMap(TiledMap worldMap, float worldScale) {
        this.map = worldMap;
        debugRenderer = new ShapeRenderer();

        init();

        TiledMapLoader.loadMapActions(worldMap, worldScale, worldOrigin, new Rectangle());
        TiledMapLoader.loadMapCollision(worldMap, worldScale, world);
        buildEntityPathing(worldMap, worldScale);
        createEntities(game.getAsset(), worldMap, worldScale);
        loadParticleEffects(worldMap, game.getAsset(), worldScale);
        createWorldObjects(worldMap, game.getAsset(), worldScale);
        createInteriors(worldMap, worldScale);

        updateRendererMap();
        game.getMultiplexer().addProcessor(this);

        addDefaultWorldSystems();
        world.setContactListener(new BasicEntityCollisionHandler());

        if (isGameSave) {
            player.createBoxBody(world);
        } else {
            player.createBoxBody(world);
            player.setPosition(worldOrigin, true);
        }

        player.updateWorldState(this);
        if (game.isAnyMultiplayer()) loadNetworkComponents();

        finalizeWorld();

        isWorldLoaded = true;
    }

    /**
     * Adds the default world systems
     */
    protected void addDefaultWorldSystems() {
        engine.addSystem(new EntityInteractableAnimationSystem(engine));
        engine.addSystem(new EntityUpdateSystem(game, this));

        effectCloudManager = new AreaEffectCloudManager();
        systemManager.add(effectUpdateSystem = new AreaEffectUpdateSystem(effectCloudManager));
    }

    /**
     * Update the tiled map renderer
     */
    public void updateRendererMap() {
        renderer.setTiledMap(map, worldOrigin.x, worldOrigin.y);
    }

    /**
     * Enter an interior
     *
     * @param interior the interior
     */
    public void enterInterior(GameWorldInterior interior) {
        player.getConnection().updateNetworkInteriorWorldEntered(interior);
        GameManager.getWorldManager().transferIn(player, this, interior);
    }

    /**
     * Enter an interior
     *
     * @param interior the type
     */
    public void enterInterior(InteriorWorldType interior) {
        enterInterior(interiorWorlds.get(interior));
    }

    /**
     * Enter this world
     */
    public void enter() {
        if (!isWorldLoaded) {
            throw new UnsupportedOperationException("Cannot enter world without it being loaded, this is a bug. fix please!");
        }

        this.hasVisited = true;

        guiManager.resetCursor();
        game.setScreen(this);
        game.setGameReady(true);
    }

    /**
     * Spawn a projectile in this world
     *
     * @param type   type
     * @param origin origin
     * @param target target
     * @param result the result callback
     */
    public void spawnProjectile(ProjectileType type,
                                Vector2 origin,
                                Vector2 target,
                                ProjectileResult result) {
        projectileManager.spawnProjectile(type, origin, target, result);
    }

    /**
     * Spawn a projectile that has a 'death' animation
     *
     * @param type      type
     * @param animation animation
     * @param origin    origin
     * @param target    target
     * @param result    result
     */
    public void spawnAnimatedProjectile(ProjectileType type,
                                        Animation<TextureRegion> animation,
                                        Vector2 origin,
                                        Vector2 target,
                                        ProjectileResult result) {
        projectileManager.spawnAnimatedProjectile(type, animation, origin, target, result);
    }

    /**
     * Spawn an effect cloud
     * TODO: Better system manager I guess
     *
     * @param effectCloud effect cloud
     */
    public void spawnEffectCloud(AreaEffectCloud effectCloud) {
        effectCloudManager.create(effectCloud);
    }

    /**
     * Check the entities status within an area effect
     *
     * @param entity the entity
     */
    public void checkAreaEffects(GameEntity entity) {
        effectCloudManager.processEntity(entity);
    }

    /**
     * Register damage to be animated
     *
     * @param entity entity
     * @param amount amount
     * @param type   type
     */
    public void registerEntityDamage(GameEntity entity, float amount, DamageType type) {
        worldDamageAnimator.store(entity, amount, type);
    }

    /**
     * Unload the collision within this world
     */
    public void unloadBox2dWorld() {
        final Array<Body> bodies = new Array<>();
        world.getBodies(bodies);

        for (int i = 0; i < bodies.size; i++) {
            if (!world.isLocked())
                world.destroyBody(bodies.get(i));
        }

        world.dispose();
        world = null;
    }

    /**
     * Creates and loads all entities within this world
     *
     * @param asset      asset
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void createEntities(Asset asset, TiledMap worldMap, float worldScale) {
        TiledMapLoader.loadMapObjects(worldMap, worldScale, "Entities", (object, rectangle) -> {
            final String key = TiledMapLoader.ofString(object, "key");
            if (key == null) {
                GameLogging.warn(this, "No key for an entity @ %f,%f", rectangle.x, rectangle.y);
                return;
            }

            final boolean enemy = TiledMapLoader.ofBoolean(object, "enemy");
            final boolean interactable = TiledMapLoader.ofBoolean(object, "interactable");

            if (enemy) {
                createEnemy(key, rectangle, asset);
            } else if (interactable) {
                createInteractableEntity(key, rectangle, asset);
            } else {
                createRegularEntity(key, rectangle, asset);
            }
        });

        GameLogging.info(this, "Loaded %d entities.", entities.size);
    }

    /**
     * Create an enemy
     *
     * @param key       entity key type
     * @param rectangle bounds
     * @param asset     asset
     */
    protected void createEnemy(String key, Rectangle rectangle, Asset asset) {
        final EntityEnemy enemy = Entities.enemy(key, this, new Vector2(rectangle.x, rectangle.y), game);
        enemy.load(asset);
        populateEntity(enemy);

        GameLogging.info(this, "Loaded enemy: %s", enemy.name());
    }

    /**
     * Create an interactable entity
     *
     * @param key       entity key type
     * @param rectangle bounds
     * @param asset     asset
     */
    protected void createInteractableEntity(String key, Rectangle rectangle, Asset asset) {
        final EntityInteractable entity = Entities.interactable(key, this, new Vector2(rectangle.x, rectangle.y), game);
        entity.load(asset);
        populateEntity(entity);

        GameLogging.info(this, "Loaded interactable entity: %s", entity.name());
    }

    /**
     * Create a regular entity
     *
     * @param key       entity key type
     * @param rectangle bounds
     * @param asset     asset
     */
    protected void createRegularEntity(String key, Rectangle rectangle, Asset asset) {
        final GameEntity entity = Entities.generic(key, this, new Vector2(rectangle.x, rectangle.y), game);
        entity.load(asset);
        populateEntity(entity);

        GameLogging.info(this, "Loaded regular entity: %s", entity.name());
    }

    /**
     * Populate this entity to the engine and list
     *
     * @param entity the entity
     */
    protected void populateEntity(GameEntity entity) {
        entity.setEntityId(entities.size + 1);
        entities.put(entity.entityId(), entity);
        engine.addEntity(entity.getEntity());

        mouseListeners.put(entity, false);
    }

    /**
     * Remove a dead entity now.
     * Should be removed from the {@code entities} list from whoever calls the function
     * Usually, via iterator.
     *
     * @param entity entity
     */
    public void removeDeadEntityNow(GameEntity entity) {
        engine.removeEntity(entity.getEntity());
        nearbyEntities.remove(entity.entityId());
        deadEnemies.add(entity.type());
        mouseListeners.remove(entity);
        entity.dispose();
    }

    /**
     * Queue for a dead entity to be removed from this world.
     * Entity will be removed next tick.
     *
     * @param entity the entity
     */
    public void queueRemoveDeadEntity(GameEntity entity) {
        entity.queueForRemoval();
    }

    /**
     * TODO: Causes issues with multiple types of same enemy EM-86
     *
     * @return dead enemies
     */
    public Bag<EntityType> deadEnemies() {
        return deadEnemies;
    }

    /**
     * Load particles
     *
     * @param worldMap   the map of the world
     * @param asset      asset
     * @param worldScale the scale of the world
     */
    protected void loadParticleEffects(TiledMap worldMap, Asset asset, float worldScale) {
        final boolean result = TiledMapLoader.loadMapObjects(worldMap, worldScale, "Particles", (object, rectangle) -> {
            final ParticleEffect effect = new ParticleEffect();
            effect.load(Gdx.files.internal("world/asset/" + object.getName()), asset.getAtlasAssets());
            effect.setPosition(rectangle.x, rectangle.y);
            effect.start();
        });

        if (result) GameLogging.info(this, "Loaded %d particle effects.", effects.size);
    }

    /**
     * Load world objects
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    protected void createWorldObjects(TiledMap worldMap, Asset asset, float worldScale) {
        TiledMapLoader.loadMapObjects(worldMap, worldScale, "WorldObjects", (object, rectangle) -> {
            try {
                final boolean hasCollision = TiledMapLoader.ofBoolean(object, "hasCollision");
                final boolean interactable = TiledMapLoader.ofBoolean(object, "interactable");
                final float range = TiledMapLoader.ofFloat(object, "interaction_range", 3.5f);
                final String key = TiledMapLoader.ofString(object, "key");
                final String type = TiledMapLoader.ofString(object, "interaction_type");

                final float sizeX = TiledMapLoader.ofFloat(object, "size_x", 1.0f);
                final float sizeY = TiledMapLoader.ofFloat(object, "size_y", 1.0f);
                final boolean offsetX = TiledMapLoader.ofBoolean(object, "offset_x");
                final boolean offsetY = TiledMapLoader.ofBoolean(object, "offset_y");

                if (interactable) {
                    // find interaction and create world object either from key or type
                    final WorldInteractionType interactionType = WorldInteractionType.of(type);
                    // TODO: Keyless world objects
                    final AbstractInteractableWorldObject worldObject = interactionType.get(key, interactionManager);

                    final float positionX = offsetX ? rectangle.x - (sizeX * worldScale) : rectangle.x;
                    final float positionY = offsetY ? rectangle.y - (sizeY * worldScale) : rectangle.y;

                    worldObject.setWorldIn(this);
                    worldObject.setPosition(positionX, positionY);
                    worldObject.setSize(rectangle.width, rectangle.height);
                    worldObject.setInteractionRange(range);
                    worldObject.setObject(object);
                    worldObject.load(asset);

                    createObjectParticles(worldObject, object, asset);

                    // load collision for this object
                    if (hasCollision) createObjectCollisionBody(worldObject, rectangle);
                    interactableWorldObjects.add(worldObject);
                    mouseListeners.put(worldObject, false);
                } else {
                    // load base object
                    final AbstractWorldObject worldObject = new SimpleWorldObject(key);

                    worldObject.load(asset);
                    worldObject.setWorldIn(this);
                    worldObject.setObject(object);

                    // find texture of this object
                    final String textureKey = TiledMapLoader.ofString(object, "texture");
                    final TextureRegion texture = asset.get(textureKey);
                    final boolean drawable = texture != null;

                    final float positionX = offsetX ? (drawable ? rectangle.x - (texture.getRegionWidth() * worldScale) : rectangle.x - (sizeX * worldScale)) : rectangle.x;
                    final float positionY = offsetY ? (drawable ? rectangle.y - (texture.getRegionHeight() * worldScale) : rectangle.y - (sizeY * worldScale)) : rectangle.y;

                    if (hasCollision && drawable)
                        createObjectCollisionBodyFromTexture(worldObject, rectangle, texture, positionX, positionY);

                    worldObject.setTexture(texture);
                    worldObject.setPosition(positionX, positionY);
                    if (drawable)
                        worldObject.setSize(texture.getRegionWidth() * worldScale, texture.getRegionHeight() * worldScale);

                    createObjectParticles(worldObject, object, asset);
                    worldObjects.put(key, worldObject);
                }

            } catch (Exception any) {
                GameLogging.exceptionThrown(this, "Failed to load a world object", any);
            }
        });

        GameLogging.info(this, "Loaded %d interactable objects.", interactableWorldObjects.size);
        GameLogging.info(this, "Loaded %d world objects.", worldObjects.size());
    }

    /**
     * Spawn a world drop interaction
     *
     * @param type item
     */
    public void spawnWorldDrop(Items type, int amount, Vector2 position) {
        final Item item = ItemRegistry.createItem(type, amount);
        final DroppedItemInteraction interaction = new DroppedItemInteraction(this, item, position);
        interaction.load(game.getAsset());
        interactableWorldObjects.add(interaction);
    }

    /**
     * Remove an object from the map
     *
     * @param key the key
     */
    public void removeSimpleObject(String key) {
        final SimpleWorldObject object = (SimpleWorldObject) worldObjects.get(key);
        if (object == null) return;

        destroyedWorldObjects.add(key);

        object.destroyCollision();
        object.dispose();

        worldObjects.remove(key);
    }

    /**
     * Remove an object that was saved as destroyed
     *
     * @param key key
     */
    protected void removeDestroyedSaveObject(String key) {
        final SimpleWorldObject object = (SimpleWorldObject) worldObjects.get(key);
        if (object == null) return;

        // will need to be saved again.
        // fixes: EM-74
        destroyedWorldObjects.add(key);

        object.destroyCollision();
        object.dispose();
        worldObjects.remove(key);
    }

    /**
     * Remove an interaction
     * Not required to save, if an item;
     * TODO: If not item, save
     *
     * @param object object
     */
    public void removeInteraction(AbstractInteractableWorldObject object) {
        interactableWorldObjects.removeValue(object, true);
    }

    /**
     * @return all destroyed objects
     */
    public Bag<String> destroyedWorldObjects() {
        return destroyedWorldObjects;
    }

    /**
     * Load particle effects for a world object, interactable or not.
     *
     * @param wb     wb
     * @param object obj
     * @param asset  assets
     */
    protected void createObjectParticles(AbstractWorldObject wb, MapObject object, Asset asset) {
        final String particleKey = TiledMapLoader.ofString(object, "particle");
        if (particleKey == null) return; // this object has no particles

        final float xOffset = TiledMapLoader.ofFloat(object, "x_offset");
        final float yOffset = TiledMapLoader.ofFloat(object, "y_offset");

        final ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal(particleKey), asset.getAtlasAssets());
        effect.setPosition(wb.getPosition().x + (wb.getSize().x / 2f) + xOffset, wb.getPosition().y + (wb.getSize().y / 2f) + yOffset);
        wb.addEffect(effect);
    }

    /**
     * Create a collision body for the provided object
     *
     * @param wb        wb
     * @param rectangle the rectangle shape
     */
    protected void createObjectCollisionBody(AbstractInteractableWorldObject wb, Rectangle rectangle) {
        final Body body = CollisionShapeCreator
                .createPolygonShapeInWorld(
                        rectangle.x,
                        rectangle.y,
                        rectangle.width,
                        rectangle.height,
                        OasisGameSettings.SCALE,
                        true,
                        world);
        wb.setBody(body);
    }

    /**
     * Create a collision body for the object using the textures bounds
     *
     * @param wb        object
     * @param rectangle rectangle shape - unused, for now,
     * @param texture   texture
     * @param x         x
     * @param y         y
     */
    protected void createObjectCollisionBodyFromTexture(AbstractWorldObject wb, Rectangle rectangle, TextureRegion texture, float x, float y) {
        if (texture == null) return;

        // create collision body, offset position to fit within bounds.
        final Body body = CollisionShapeCreator.createPolygonShapeInWorld(
                x,
                y,
                texture.getRegionWidth(),
                texture.getRegionHeight(),
                OasisGameSettings.SCALE,
                true,
                world);

        wb.setBody(body);
    }

    /**
     * Load interiors within this world
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void createInteriors(TiledMap worldMap, float worldScale) {
        final boolean result = TiledMapLoader.loadMapObjects(worldMap, worldScale, "Interior", (object, bounds) -> {
            final boolean enterable = object.getProperties().get("enterable", false, Boolean.class);
            final String asset = object.getProperties().get("interior_asset", null, String.class);
            final String typeString = object.getProperties().get("interior_type", null, String.class);
            final InteriorWorldType type = InteriorWorldType.of(typeString);

            if (asset != null) {
                final String cursorType = object.getProperties().get("cursor", "default", String.class).toUpperCase();
                final Cursor cursor = Cursor.valueOf(cursorType);

                // if this interior is locked/requires lock picking
                final boolean locked = TiledMapLoader.ofBoolean(object, "locked");
                final LockDifficulty difficulty = locked ? LockDifficulty.valueOf(TiledMapLoader.ofString(object, "lock_difficulty")) : null;

                final GameWorldInterior interior = type.createInterior(this, asset, cursor, bounds);
                interior.setEnterable(enterable);

                interior.setLocked(locked);
                interior.setLockDifficulty(difficulty);

                interiorWorlds.put(type, interior);
                mouseListeners.put(interior, false);
                GameLogging.info(this, "Loaded interior: %s", type);
            }
        });

        if (result) GameLogging.info(this, "Loaded %d instances.", interiorWorlds.size());
    }

    /**
     * Get an interior world by type
     *
     * @param type the type
     * @return the interior or {@code null} if none
     */
    public GameWorldInterior findInteriorByType(InteriorWorldType type) {
        return interiorWorlds.get(type);
    }

    /**
     * @return map of all interior worlds
     */
    public EnumMap<InteriorWorldType, GameWorldInterior> interiorWorlds() {
        return interiorWorlds;
    }

    /**
     * Find and create the entity paths for their AI
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void buildEntityPathing(TiledMap worldMap, float worldScale) {
        final MapLayer layer = worldMap.getLayers().get("Paths");
        if (layer == null) return;

        final Vector2[] paths = TiledMapLoader.loadPolyPath(layer, worldScale);

        if (paths == null) {
            GameLogging.warn(this, "Failed to load paths!");
        } else {
            this.paths.addAll(paths);
        }
    }

    /**
     * @return map of pathing for entities
     */
    public Array<Vector2> getPaths() {
        return paths;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (paused) {
            // render, no update
            renderWorld(delta);
        } else {
            //render+update normally
            updateAndRender(delta);
        }
    }

    /**
     * Update and render this world
     *
     * @param delta the delta
     */
    protected void updateAndRender(float delta) {
        final long now = System.nanoTime();
        final long elapsed = TimeUnit.NANOSECONDS.toMillis(now - lastTick);

        if (elapsed >= 50) {
            lastTick = now;
            GameManager.tick++;
        }

        performanceCounter.start();
        delta = update(delta);

        GdxAI.getTimepiece().update(delta);
        systemManager.update(delta);
        projectileManager.update(delta);
        worldDamageAnimator.update(delta);

        player.setPosition(player.getBody().getPosition(), false);
        player.interpolatePosition();
        player.update(delta);

        networkRenderer.update(this, delta);

        renderWorld(delta);

        performanceCounter.stop();
        performanceCounter.tick(delta);

        // always last
        GameManager.getTaskManager().update();
    }

    /**
     * Render this world
     *
     * @param delta delta
     */
    public void renderWorld(float delta) {
        renderer.beginRendering();
        renderer.render();

        if (game.isAnyMultiplayer()) {
            networkRenderer.render(this, batch, delta);
        }

        for (GameEntity entity : entities.values()) {
            if (entity.isInView(renderer.getCamera())) {
                entity.render(batch, delta);
                entity.renderHealthBar(batch);
            }
        }

        // general world objects
        for (AbstractWorldObject object : worldObjects.values()) object.render(batch, delta);

        // interactions
        for (AbstractInteractableWorldObject worldObject : interactableWorldObjects) {
            if (worldObject.isUpdatable() && worldObject.wasInteractedWith()) worldObject.update();
            worldObject.render(batch, delta);
        }

        // render particles
        for (ParticleEffect effect : effects) {
            effect.update(delta);
            effect.draw(batch);
        }

        updateNearbyInteriors();
        projectileManager.render(batch, delta);

        // render local player next
        player.render(batch, delta);

        endRender();
    }

    /**
     * End world rendering and render GUI(s)
     */
    protected void endRender() {
        batch.setProjectionMatrix(guiManager.getCamera().combined);

        // render object UI elements
        for (AbstractInteractableWorldObject worldObject : interactableWorldObjects) {
            if (worldObject.isUiComponent()) {
                guiManager.renderWorldObjectComponents(worldObject, renderer.getCamera(), batch);
            }
        }

        // render entity UI elements
        for (GameEntity entity : entities.values()) {
            entity.renderDamageAnimation(renderer.getCamera(), guiManager.getCamera(), batch, worldDamageAnimator);
        }

        // draw name tags on top of everything
        networkRenderer.postRender(this, batch, guiManager);

        batch.end();

        updateMouseListeners();
        guiManager.updateAndDrawStage();
    }

    protected void updateMouseListeners() {
        // do not update cursor state if any GUI is visible besides the HUD
        if (guiManager.isAnyGuiVisible(GuiType.HUD)) return;

        renderer.getCamera().unproject(cursorInWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        // update all mouse listeners, if found one then break
        // later: task: (TODO-28) priority
        for (Map.Entry<MouseListener, Boolean> entry : mouseListeners.entrySet()) {
            if (!entry.getValue() && entry.getKey().within(cursorInWorld)) {
                guiManager.setCursorInGame(entry.getKey().enter(cursorInWorld));
                entry.setValue(true);
                break;
            } else if (entry.getValue() && !entry.getKey().within(cursorInWorld)) {
                entry.getKey().exit(cursorInWorld);

                guiManager.resetCursor();
                entry.setValue(false);
                break;
            }
        }
    }

    /**
     * Get an {@link EntityInteractable} by their NPC type
     *
     * @param type the type
     * @return the {@link EntityInteractable} or {@code  null} if not found
     */
    public EntityInteractable getEntityByKey(EntityType type) {
        for (GameEntity entity : entities.values()) {
            if (entity.asInteractable().type() == type) {
                return entity.asInteractable();
            }
        }
        return null;
    }

    public EntityEnemy getEnemyByType(EntityType type) {
        for (GameEntity entity : entities.values()) {
            if (entity instanceof EntityEnemy enemy && enemy.type() == type) {
                return enemy;
            }
        }
        return null;
    }

    /**
     * Add a nearby entity
     *
     * @param entity the entity
     */
    public void addNearbyEntity(EntityInteractable entity) {
        nearbyEntities.put(entity.entityId(), entity);
    }

    /**
     * Remove a nearby entity
     *
     * @param entity the entity
     */
    public void removeNearbyEntity(EntityInteractable entity) {
        nearbyEntities.remove(entity.entityId());
    }

    /**
     * Enable an interaction that is currently disabled.
     * Used for instances where an item or an entity needs to be used/spoken to first.
     *
     * @param type the type
     * @param key  the key
     */
    public void enableWorldInteraction(WorldInteractionType type, String key) {
        for (AbstractInteractableWorldObject interaction : interactableWorldObjects) {
            if (interaction.matches(type, key)) {
                interaction.enable();
                break;
            }
        }
    }

    /**
     * Find an interaction
     *
     * @param type type
     * @param key  key
     * @return the object or {@code null} if not found
     */
    public AbstractInteractableWorldObject findInteraction(WorldInteractionType type, String key) {
        for (AbstractInteractableWorldObject interaction : interactableWorldObjects) {
            if (interaction.matches(type, key)) {
                return interaction;
            }
        }
        return null;
    }

    /**
     * @return all interactable objects
     */
    public Array<AbstractInteractableWorldObject> interactableWorldObjects() {
        return interactableWorldObjects;
    }

    /**
     * @return all (non) interactable world objects
     */
    public Collection<AbstractWorldObject> worldObjects() {
        return worldObjects.values();
    }

    /**
     * Check if the provided item hit an entity when swung
     *
     * @param item the item
     * @return the entity if hit
     */
    public EntityEnemy hasHitEntity(ItemWeapon item) {
        for (GameEntity entity : entities.values()) {
            if (entity instanceof EntityEnemy enemy) {
                if (enemy.bb().overlaps(item.getBounds())) {
                    GameLogging.info(this, "Hit entity " + enemy.type());
                    return enemy;
                }
            }
        }
        return null;
    }

    /**
     * Update interiors the player is near
     */
    protected void updateNearbyInteriors() {
        for (GameWorldInterior interior : interiorWorlds.values()) {
            if (interior.isWithinEnteringDistance(player.getPosition())) {
                interior.updateWhilePlayerIsNear();
                interior.setNear(true);
            } else if (interior.isNear()) {
                interior.invalidatePlayerNearbyState();
                interior.setNear(false);
            }
        }
    }

    @Override
    public void show() {
        GameLogging.info(this, "Showing world.");
    }

    @Override
    public void hide() {
        GameLogging.info(this, "Hiding world.");
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void pause() {
        GameLogging.info(this, "Pausing game.");
        paused = true;
    }

    @Override
    public void resume() {
        GameLogging.info(this, "Resuming game.");
        paused = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return GameManager.handleWorldKeyPress(this, keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (guiManager.isAnyGuiVisible(GuiType.HUD)) return true;

        for (Map.Entry<MouseListener, Boolean> entry : mouseListeners.entrySet()) {
            if (entry.getValue()) {
                // mouse is within, we clicked it.
                return entry.getKey().clicked(cursorInWorld);
            }
        }
        return false;
    }

    @Override
    public void dispose() {
        entities.forEach(entity -> entity.value.dispose());
        worldObjects.values().forEach(Disposable::dispose);
        interactableWorldObjects.forEach(Disposable::dispose);

        game.getMultiplexer().removeProcessor(this);

        unloadBox2dWorld();

        nearbyEntities.clear();
        entities.clear();
        worldObjects.clear();
        interactableWorldObjects.clear();
        effects.clear();
        paths.clear();

        systemManager.dispose();
        engine.removeAllSystems();
    }
}
