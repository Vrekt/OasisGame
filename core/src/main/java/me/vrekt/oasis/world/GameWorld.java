package me.vrekt.oasis.world;

import com.badlogic.ashley.core.PooledEngine;
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
import gdx.lunar.world.AbstractGameWorld;
import gdx.lunar.world.WorldConfiguration;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.enemy.EntityEnemyType;
import me.vrekt.oasis.entity.enemy.projectile.ProjectileManager;
import me.vrekt.oasis.entity.enemy.projectile.ProjectileResult;
import me.vrekt.oasis.entity.enemy.projectile.ProjectileType;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.entity.system.EntityInteractableAnimationSystem;
import me.vrekt.oasis.entity.system.EntityUpdateSystem;
import me.vrekt.oasis.graphics.tiled.MapRenderer;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.save.loading.SaveStateLoader;
import me.vrekt.oasis.save.world.WorldSaveProperties;
import me.vrekt.oasis.utility.collision.BasicEntityCollisionHandler;
import me.vrekt.oasis.utility.collision.CollisionShapeCreator;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.effects.AreaEffectCloud;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.network.WorldNetworkHandler;
import me.vrekt.oasis.world.obj.SimpleWorldObject;
import me.vrekt.oasis.world.obj.WorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractionManager;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.systems.AreaEffectUpdateSystem;
import me.vrekt.oasis.world.systems.SystemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents a base world within the game
 */
public abstract class GameWorld extends AbstractGameWorld<NetworkPlayer, Entity>
        implements WorldInputAdapter, SaveStateLoader<WorldSaveProperties>, Screen {

    protected final OasisGame game;
    protected final PlayerSP player;

    protected TiledMap map;
    protected String worldName;

    protected final SpriteBatch batch;
    protected final MapRenderer renderer;
    protected final Vector3 cursorInWorld = new Vector3();
    protected final Vector2 enteredInteriorPosition = new Vector2();
    protected boolean isWorldLoaded, isGameSave;

    protected boolean paused;

    protected GuiManager guiManager;
    protected final WorldNetworkHandler networkHandler;

    protected final ConcurrentHashMap<Entity, Float> nearbyEntities = new ConcurrentHashMap<>();
    protected final Array<ParticleEffect> effects = new Array<>();

    // systems
    protected final SystemManager systemManager;
    protected AreaEffectUpdateSystem effectUpdateSystem;

    // objects within this world
    protected final Map<String, WorldObject> worldObjects = new HashMap<>();
    protected final List<InteractableWorldObject> interactableWorldObjects = new ArrayList<>();
    protected final Array<Vector2> paths = new Array<>();

    protected final Map<InteriorWorldType, GameWorldInterior> interiorWorlds = new HashMap<>();

    // TODO: Maybe global? Or just per world.
    protected final InteractionManager interactionManager;
    // the current tick of this world.
    protected long lastTick;

    protected ProjectileManager projectileManager;
    protected ShapeRenderer debugRenderer;

    public GameWorld(OasisGame game, PlayerSP player, World world) {
        super(world, new WorldConfiguration(), new PooledEngine());

        this.player = player;
        this.game = game;
        this.renderer = game.getRenderer();
        this.batch = game.getBatch();
        this.guiManager = game.guiManager;
        this.interactionManager = new InteractionManager();
        this.networkHandler = new WorldNetworkHandler(game, this);
        this.projectileManager = new ProjectileManager();
        this.systemManager = new SystemManager();

        configuration.stepTime = 1 / 240f;
    }

    public String getWorldName() {
        return worldName;
    }

    public PlayerSP getLocalPlayer() {
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

    /**
     * Invoked before world is loaded
     * Useful for registering things that are already defined or known
     */
    protected void preLoad() {

    }

    /**
     * Load this world, implementing function.
     */
    protected void load() {

    }

    /**
     * Load network components if the world supports it.
     */
    protected void loadNetworkComponents() {

    }

    @Override
    public void loadFromSave(WorldSaveProperties state) {

    }

    /**
     * Create and initialize this world.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    public void create(TiledMap worldMap, float worldScale) {
        this.map = worldMap;

        debugRenderer = new ShapeRenderer();

        preLoad();

        TiledMapLoader.loadMapActions(worldMap, worldScale, worldOrigin, new Rectangle());
        TiledMapLoader.loadMapCollision(worldMap, worldScale, world);
        buildEntityPathing(worldMap, worldScale);
        createEntities(game, game.getAsset(), worldMap, worldScale);
        loadParticleEffects(worldMap, game.getAsset(), worldScale);
        createWorldObjects(worldMap, game.getAsset(), worldScale);
        createInteriors(worldMap, worldScale);

        configuration.worldScale = worldScale;

        updateRendererMap();
        game.getMultiplexer().addProcessor(this);

        addDefaultWorldSystems();
        world.setContactListener(new BasicEntityCollisionHandler());

        if (isGameSave) {
            player.defineEntity(world, player.getX(), player.getY());
        } else {
            player.defineEntity(world, worldOrigin.x, worldOrigin.y);
            player.setPosition(worldOrigin, true);
        }

        player.updateWorldState(this);
        if (game.isAnyMultiplayer()) loadNetworkComponents();

        load();

        isWorldLoaded = true;
    }

    /**
     * Adds the default world systems
     */
    protected void addDefaultWorldSystems() {
        engine.addSystem(new EntityInteractableAnimationSystem(engine));
        engine.addSystem(new EntityUpdateSystem(game, this));

        systemManager.add(effectUpdateSystem = new AreaEffectUpdateSystem());
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
    protected void enterInterior(GameWorldInterior interior) {
        GameManager.getWorldManager().transfer(player, this, interior);
    }

    /**
     * Enter this world
     */
    public void enter() {
        guiManager.resetCursor();
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
        effectUpdateSystem.create(effectCloud);
    }

    /**
     * Check the entities status within an area effect
     *
     * @param entity the entity
     */
    public void checkAreaEffects(Entity entity) {
        effectUpdateSystem.processEntity(entity);
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
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void createEntities(OasisGame game, Asset asset, TiledMap worldMap, float worldScale) {
        TiledMapLoader.loadMapObjects(worldMap, worldScale, "Entities", (object, rectangle) -> {
            final boolean enemy = TiledMapLoader.ofBoolean(object, "enemy");
            if (enemy) {
                createEnemy(object, rectangle, asset);
            } else {
                final EntityNPCType type = EntityNPCType.findType(object);
                if (type != null) {
                    final EntityInteractable entity = type.create(new Vector2(rectangle.x, rectangle.y), game, this);
                    entity.load(asset);

                    populateEntity(entity);
                } else {
                    GameLogging.warn(this, "Found invalid entity: " + object);
                }
            }
        });

        GameLogging.info(this, "Loaded %d entities.", entities.size);
    }

    /**
     * Create an enemy
     *
     * @param object    map object
     * @param rectangle bounds
     * @param asset     asset
     */
    protected void createEnemy(MapObject object, Rectangle rectangle, Asset asset) {
        final EntityEnemyType type = EntityEnemyType.valueOf(TiledMapLoader.ofString(object, "entity_type"));
        final String variety = TiledMapLoader.ofString(object, "variety");

        final EntityEnemy enemy = type.create(new Vector2(rectangle.x, rectangle.y), game, this, variety);
        enemy.load(asset);
        populateEntity(enemy);

        GameLogging.info(this, "Loaded an enemy %s", type);
    }


    /**
     * Populate this entity to the engine and list
     *
     * @param entity the entity
     */
    protected void populateEntity(Entity entity) {
        entity.setEntityId(entities.size + 1);
        entities.put(entity.getEntityId(), entity);
        engine.addEntity(entity.getEntity());
    }

    /**
     * Remove an interactable entity from this world
     * TODO: In update system maybe check for a state within the entity to queue to remove from world
     *
     * @param entity the entity
     */
    public void removeEntity(EntityInteractable entity) {
        entities.remove(entity.getEntityId());
        engine.removeEntity(entity.getEntity());
        nearbyEntities.remove(entity);
    }

    /**
     * Remove a dead entity from this world
     *
     * @param entity the entity
     */
    public void removeDeadEntity(Entity entity) {
        entities.remove(entity.getEntityId());
        engine.removeEntity(entity.getEntity());
        nearbyEntities.remove(entity);
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

                if (interactable) {
                    // find interaction and create world object either from key or type
                    final WorldInteractionType interactionType = WorldInteractionType.of(type);
                    final InteractableWorldObject worldObject = interactionType.get(key, interactionManager);

                    worldObject.setWorldIn(this);
                    worldObject.setPosition(rectangle.x, rectangle.y);
                    worldObject.setSize(rectangle.width, rectangle.height);
                    worldObject.setInteractionRange(range);
                    worldObject.load(asset);

                    createObjectParticles(worldObject, object, asset);

                    // load collision for this object
                    if (hasCollision) createObjectCollisionBody(worldObject, rectangle);
                    interactableWorldObjects.add(worldObject);
                } else {
                    // load base object
                    final WorldObject worldObject = new SimpleWorldObject(key);
                    worldObject.load(asset);
                    worldObject.setWorldIn(this);

                    // find texture of this object
                    final String textureKey = TiledMapLoader.ofString(object, "texture");
                    final TextureRegion texture = asset.get(textureKey);


                    if (texture != null) {

                        // check if this object needs to be offset
                        // depends on the case of the object, the position and texture size
                        final float x = TiledMapLoader.ofBoolean(object, "offset_x")
                                ? rectangle.x - (texture.getRegionWidth() * worldScale)
                                : rectangle.x;

                        final float y = TiledMapLoader.ofBoolean(object, "offset_y")
                                ? rectangle.y - (texture.getRegionHeight() * worldScale)
                                : rectangle.y;

                        // TODO: Will not activate if object does not have texture
                        if (hasCollision)
                            createObjectCollisionBodyFromTexture(worldObject, rectangle, texture, x, y);

                        worldObject.setTexture(texture);
                        worldObject.setPosition(x, y);
                        worldObject.setSize(texture.getRegionWidth() * worldScale, texture.getRegionHeight() * worldScale);
                    }

                    createObjectParticles(worldObject, object, asset);
                    worldObjects.put(key, worldObject);
                }

            } catch (Exception any) {
                GameLogging.exceptionThrown(this, "Failed to load a world object", any);
            }
        });

        GameLogging.info(this, "Loaded %d interactable objects.", interactableWorldObjects.size());
        GameLogging.info(this, "Loaded %d world objects.", worldObjects.size());
    }

    /**
     * Remove an object from the map
     *
     * @param key the key
     */
    public void removeSimpleObject(String key) {
        final SimpleWorldObject object = (SimpleWorldObject) worldObjects.get(key);
        if (object == null) return;

        object.destroyCollision();
        object.dispose();

        worldObjects.remove(key);
    }

    /**
     * Load particle effects for a world object, interactable or not.
     *
     * @param wb     wb
     * @param object obj
     * @param asset  assets
     */
    protected void createObjectParticles(WorldObject wb, MapObject object, Asset asset) {
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
    protected void createObjectCollisionBody(WorldObject wb, Rectangle rectangle) {
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
    protected void createObjectCollisionBodyFromTexture(WorldObject wb, Rectangle rectangle, TextureRegion texture, float x, float y) {
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
                final GameWorldInterior interior = type.createInterior(this, asset, cursor, bounds);
                interior.setEnterable(enterable);

                interiorWorlds.put(type, interior);
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
            GameLogging.info(this, "Loaded %d paths", paths.length);
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

        delta = update(delta);


        GdxAI.getTimepiece().update(delta);
        systemManager.update(delta);
        projectileManager.update(delta);

        // added back since it was removed from lunar
        // may be added back
        // but ideally, user should implement updating player
        // not the library
        player.setPosition(player.getBody().getPosition(), false);
        player.interpolatePosition();
        player.update(delta);
        updateCursorState();
        renderWorld(delta);

        // always last
        GameManager.getTaskManager().update();
    }

    /**
     * Update cursor state
     */
    protected void updateCursorState() {
        if (guiManager.isAnyGuiVisible(GuiType.HUD)) return;

        // update world cursor
        renderer.getCamera().unproject(cursorInWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        boolean hasEntity = false;
        for (Entity entityInteractable : nearbyEntities.keySet()) {
            if (entityInteractable.isMouseInEntityBounds(cursorInWorld)) {
                // mouse is over this entity
                if (!guiManager.wasCursorChanged()) guiManager.setCursorInGame(Cursor.DIALOG);
                hasEntity = true;
                break;
            }
        }

        boolean hasInterior = false;
        for (GameWorldInterior interior : interiorWorlds.values()) {
            if (interior.isEnterable() && interior.isMouseWithinBounds(cursorInWorld)) {
                if (!guiManager.wasCursorChanged()) guiManager.setCursorInGame(interior.getCursor());
                hasInterior = true;
                break;
            }
        }

        // check for environment objects
        boolean hasObj = false;
        if (!hasEntity) {
            for (InteractableWorldObject worldObject : interactableWorldObjects) {
                if (worldObject.isMouseOver(cursorInWorld)
                        && worldObject.getCursor() != null
                        && worldObject.isEnabled()) {
                    guiManager.setCursorInGame(worldObject.getCursor());
                    hasObj = true;
                    break;
                }
            }
        }

        // only reset cursor to default state if not currently active
        // and no interactions are being made
        if (!guiManager.isDefaultCursorState()
                && !(hasEntity || hasObj || hasInterior)
                && guiManager.wasCursorChanged()) {
            guiManager.resetCursor();
        }
    }

    /**
     * Render this world
     *
     * @param delta delta
     */
    public void renderWorld(float delta) {
        renderer.beginRendering();
        renderer.render();

        // render MP players first,
        for (NetworkPlayer player : players.values()) {
            if (player.isInView(renderer.getCamera())) {
                player.render(batch, delta);
                player.setRenderNametag(true);
            } else {
                player.setRenderNametag(false);
            }
        }

        for (Entity entity : entities.values()) {
            if (entity.isInView(renderer.getCamera())) {
                entity.render(batch, delta);
                entity.renderHealthBar(batch);
            }
        }

        // general world objects
        for (WorldObject object : worldObjects.values()) object.render(batch, delta);

        // interactions
        for (InteractableWorldObject worldObject : interactableWorldObjects) {
            if (worldObject.isUpdatable() && worldObject.wasInteractedWith()) worldObject.update();
            worldObject.render(batch, delta);
        }

        // render particles
        for (ParticleEffect effect : effects) {
            effect.update(delta);
            effect.draw(batch);
        }

        projectileManager.render(batch, delta);

        // render local player next
        player.render(batch, delta);
        endRender();
    }

    /**
     * End world rendering and render GUI(s)
     */
    protected void endRender() {
        // render player name-tags last
        batch.setProjectionMatrix(guiManager.getCamera().combined);
        for (NetworkPlayer player : players.values()) {
            if (player.shouldRenderNametag()) {
                guiManager.renderPlayerNametag(player, renderer.getCamera(), batch);
            }
        }

        // render damage amount animations for the player and enemies attacking
        guiManager.renderDamageAmountAnimations(renderer.getCamera(), batch, player);
        batch.end();

        game.guiManager.updateAndDrawStage();
    }

    /**
     * Get an {@link EntityInteractable} by their NPC type
     *
     * @param type the type
     * @return the {@link EntityInteractable} or {@code  null} if not found
     */
    public EntityInteractable getEntityByType(EntityNPCType type) {
        for (Entity entity : entities.values()) {
            if (entity.isInteractable()
                    && entity.asInteractable().getType() == type) {
                return entity.asInteractable();
            }
        }
        return null;
    }

    /**
     * Add a nearby entity
     *
     * @param entity   the entity
     * @param distance the distance
     */
    public void addNearbyEntity(EntityInteractable entity, float distance) {
        nearbyEntities.put(entity, distance);
    }

    /**
     * Remove a nearby entity
     *
     * @param entity the entity
     */
    public void removeNearbyEntity(EntityInteractable entity) {
        nearbyEntities.remove(entity);
    }

    /**
     * Enable an interaction that is currently disabled.
     * Used for instances where an item or an entity needs to be used/spoken to first.
     *
     * @param type the type
     * @param key  the key
     */
    public void enableWorldInteraction(WorldInteractionType type, String key) {
        for (InteractableWorldObject interaction : interactableWorldObjects) {
            if (interaction.matches(type, key)) {
                interaction.enable();
                break;
            }
        }
    }

    /**
     * Check if the provided item hit an entity when swung
     * TODO: Implement this!
     *
     * @param item the item
     * @return the entity if hit
     */
    public EntityEnemy hasHitEntity(ItemWeapon item) {
        for (Entity entity : entities.values()) {
            if (entity instanceof EntityEnemy enemy) {
                if (enemy.bb().overlaps(item.getBounds())) {
                    System.err.println("HIT");
                    return enemy;
                }
            }
        }
        return null;
    }

    /**
     * Interact with an entity if they were clicked on.
     * Finds the first entity and does not allow multiple
     */
    protected boolean didInteractWithEntity() {
        EntityInteractable entity = null;
        for (Map.Entry<Entity, Float> entry : nearbyEntities.entrySet()) {
            if (entry.getKey() instanceof EntityInteractable interactable) {
                if (interactable.isSpeakable()
                        && !interactable.isSpeakingTo()
                        && interactable.isMouseInEntityBounds(cursorInWorld)) {
                    entity = interactable;
                    break;
                }
            }
        }

        if (entity != null) {
            entity.speak(true);
            guiManager.showGui(GuiType.DIALOG);
            guiManager.getDialogComponent().showEntityDialog(entity);
            return true;
        }

        return false;
    }

    /**
     * Check if the player interacted with a world object
     */
    protected boolean didInteractWithWorldObject() {
        InteractableWorldObject interaction = null;
        for (InteractableWorldObject object : interactableWorldObjects) {
            if (object.isEnabled()
                    && object.isInInteractionRange()
                    && object.isMouseOver(cursorInWorld)
                    && !object.wasInteractedWith()) {
                interaction = object;
                break;
            }
        }

        if (interaction != null) {
            interaction.interact();
            return true;
        }
        return false;
    }

    /**
     * @return an interior if the entrance was clicked on
     */
    protected GameWorldInterior getInteriorToEnter() {
        final GameWorldInterior interior = interiorWorlds
                .values()
                .stream()
                .filter(e -> e.clickedOn(cursorInWorld))
                .findFirst()
                .orElse(null);

        if (interior != null && interior.isEnterable() && interior.isWithinEnteringDistance(player.getPosition())) {
            return interior;
        }
        return null;
    }

    @Override
    public boolean keyDown(int keycode) {
        return GameManager.handleWorldKeyPress(this, keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return didInteractWithEntity() || didInteractWithWorldObject();
    }

    @Override
    public void dispose() {
        unloadBox2dWorld();

        entities.forEach(entity -> entity.value.dispose());
        worldObjects.values().forEach(Disposable::dispose);
        interactableWorldObjects.forEach(Disposable::dispose);

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
