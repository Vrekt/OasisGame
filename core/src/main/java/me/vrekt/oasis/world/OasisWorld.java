package me.vrekt.oasis.world;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import gdx.lunar.protocol.packet.server.S2CPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.S2CPacketStartGame;
import gdx.lunar.world.AbstractGameWorld;
import gdx.lunar.world.WorldConfiguration;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.system.EntityInteractableAnimationSystem;
import me.vrekt.oasis.entity.npc.system.EntityUpdateSystem;
import me.vrekt.oasis.entity.player.mp.OasisNetworkPlayer;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.graphics.tiled.GameTiledMapRenderer;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.oasis.save.entity.EntitySaveProperties;
import me.vrekt.oasis.save.loading.SaveStateLoader;
import me.vrekt.oasis.save.world.WorldSaveProperties;
import me.vrekt.oasis.utility.collision.BasicEntityCollisionHandler;
import me.vrekt.oasis.utility.collision.CollisionShapeCreator;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.interior.Instance;
import me.vrekt.oasis.world.interior.InstanceType;
import me.vrekt.oasis.world.obj.SimpleWorldObject;
import me.vrekt.oasis.world.obj.WorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractionManager;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * Represents a base world within the game
 */
public abstract class OasisWorld extends AbstractGameWorld<OasisNetworkPlayer, Entity>
        implements InputProcessor, SaveStateLoader<WorldSaveProperties>, Screen {

    protected final OasisGame game;
    protected final OasisPlayer player;

    protected TiledMap map;
    protected String worldName;

    protected final SpriteBatch batch;
    protected final GameTiledMapRenderer renderer;
    protected final Vector3 cursorInWorld = new Vector3();
    protected boolean isWorldLoaded;

    protected int width, height;

    // pause state
    protected boolean paused, showPausedScreen;

    protected GuiManager guiManager;

    protected final ConcurrentHashMap<EntityInteractable, Float> nearbyEntities = new ConcurrentHashMap<>();
    protected final Array<ParticleEffect> effects = new Array<>();

    // objects within this world
    protected final CopyOnWriteArraySet<WorldObject> worldObjects = new CopyOnWriteArraySet<>();
    protected final List<InteractableWorldObject> interactableWorldObjects = new ArrayList<>();
    protected final Array<Vector2> paths = new Array<>();

    protected final Map<InstanceType, Instance> instances = new HashMap<>();

    // TODO: Maybe global? Or just per world.
    protected final InteractionManager interactionManager;
    // the current tick of this world.

    protected long lastTick;

    protected ShapeRenderer shapes;
    protected NinePatch gradient;

    public OasisWorld(OasisGame game, OasisPlayer player, World world) {
        super(player, world, new WorldConfiguration(), new PooledEngine());

        this.player = player;
        this.game = game;
        this.renderer = game.getRenderer();
        this.batch = game.getBatch();
        this.guiManager = game.guiManager;
        this.interactionManager = new InteractionManager();

        configuration.stepTime = 1 / 240f;
    }

    @Override
    public void loadFromSave(WorldSaveProperties state) {
        loadWorld(game.getAsset().getWorldMap(Asset.TUTORIAL_WORLD), OasisGameSettings.SCALE);

        for (EntitySaveProperties entityState : state.getEntities()) {
            if (entityState.getType() != null) {
                final EntityInteractable interactable = getEntityByType(entityState.getType());
                if (interactable != null) {
                    interactable.setName(entityState.getName());
                    interactable.setEntityId(entityState.getEntityId());
                    // FIXME: save entity rotation values
                    interactable.setBodyPosition(entityState.getPosition(), true);
                    interactable.setHealth(entityState.getHealth());
                    // FIXME: enemies  interactable.setEnemy(entityState.isEnemy());

                    GameLogging.info(this, "Found an entity from save and loaded it: %s", entityState.getType());
                } else {
                    GameLogging.warn(this, "Failed to find an entity from save by type: " + entityState.getType());
                }
            }
        }
    }

    public String getWorldName() {
        return worldName;
    }

    public OasisPlayer getLocalPlayer() {
        return player;
    }

    public OasisGame getGame() {
        return game;
    }

    public boolean isWorldLoaded() {
        return isWorldLoaded;
    }

    /**
     * Handle world related network packets
     *
     * @param connection the player connection
     */
    public void registerWorldRelatedNetworkOptions(PlayerConnection connection) {
        GameLogging.info(this, "Registering world network handlers");
        connection.registerHandlerSync(S2CPacketStartGame.PACKET_ID, packet -> handleNetworkStartGame((S2CPacketStartGame) packet));
        connection.registerHandlerSync(S2CPacketCreatePlayer.PACKET_ID, packet -> handleNetworkCreatePlayer((S2CPacketCreatePlayer) packet));
    }

    /**
     * Create a player from the server network
     *
     * @param username their username
     * @param entityId their ID
     * @param position and their position
     */
    public void createPlayerFromNetwork(String username, int entityId, Vector2 position) {
        if (!player.isInWorld()) return;

        GameLogging.info(this, "Spawning new network player with ID %d and username %s", entityId, username);
        final OasisNetworkPlayer player = new OasisNetworkPlayer(true);
        player.load(game.getAsset());

        player.setProperties(username, entityId);
        player.setSize(15, 25, OasisGameSettings.SCALE);

        player.spawnInWorld(this);
        player.setGameWorldIn(this);
        player.setBodyPosition(spawn, player.getAngle(), true);
    }

    /**
     * Handle creating a singular player
     *
     * @param packet the packet
     */
    public void handleNetworkCreatePlayer(S2CPacketCreatePlayer packet) {
        if (packet.getEntityId() == player.getEntityId()) return;
        createPlayerFromNetwork(packet.getUsername(), packet.getEntityId(), Vector2.Zero);
    }

    /**
     * Handle the start game packet from the server
     *
     * @param packet the packet
     */
    public void handleNetworkStartGame(S2CPacketStartGame packet) {
        GameLogging.info(this, "Starting network game");

        if (packet.hasPlayers()) {
            for (S2CPacketStartGame.BasicServerPlayer serverPlayer : packet.getPlayers()) {
                createPlayerFromNetwork(serverPlayer.username, serverPlayer.entityId, serverPlayer.position);
            }
        }
    }

    /**
     * Enter this world
     */
    public void enterWorld() {
        // ignore entering this world if we are already in it.
        if (player.getGameWorldIn() != null
                && player.getGameWorldIn().getWorldName().equals(worldName)) return;
        if (player.getGameWorldIn() != null) player.removeFromWorld();
        if (player.isInInteriorWorld()) player.removeFromInteriorWorld();
        guiManager.resetCursor();
    }

    /**
     * Fade in when entering an instance
     */
    public void enterInstanceAndFadeIn(Instance entering) {
        guiManager.resetCursor();
        GameManager.transitionScreen(this, entering, () -> entering.enter(false));
    }

    public void spawnEntityTypeAt(EntityNPCType type, Vector2 position) {
        GameLogging.info(this, "Spawning a new entity %s", type);
        final EntityInteractable entity = type.create(position, game, this);
        entity.load(game.getAsset());
        addInteractableEntity(entity);
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
    }

    /**
     * Load the local player into this world.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    public void loadWorld(TiledMap worldMap, float worldScale) {
        this.map = worldMap;

        preLoad();

        TiledMapLoader.loadMapActions(worldMap, worldScale, spawn, new Rectangle());
        TiledMapLoader.loadMapCollision(worldMap, worldScale, world);
        loadInteractableEntities(game, game.getAsset(), worldMap, worldScale);
        loadParticleEffects(worldMap, game.getAsset(), worldScale);
        loadWorldObjects(worldMap, game.getAsset(), worldScale);
        loadWorldInteriors(worldMap, worldScale);
        loadEntityPaths(worldMap, worldScale);

        configuration.worldScale = worldScale;

        this.renderer.setTiledMap(worldMap, spawn.x, spawn.y);
        this.width = renderer.getWidth();
        this.height = renderer.getHeight();
        game.getMultiplexer().addProcessor(this);

        addDefaultWorldSystems();
        world.setContactListener(new BasicEntityCollisionHandler());

        // player was NOT loaded from a save
        if (player.getPosition().isZero()) {
            player.defineEntity(world, spawn.x, spawn.y);
            player.setBodyPosition(spawn, true);
        } else {
            // player was loaded from a save, reference their current position
            player.defineEntity(world, player.getX(), player.getY());
        }

        player.setWorld(this);
        player.setInWorld(true);
        player.setGameWorldIn(this);

        // load implementation
        load();

        // register network handlers if any multiplayer instance
        if (game.isLocalMultiplayer() || game.isMultiplayer())
            registerWorldRelatedNetworkOptions(game.getConnectionHandler());

        isWorldLoaded = true;
        shapes = new ShapeRenderer();
    }

    protected void addDefaultWorldSystems() {
        engine.addSystem(new EntityInteractableAnimationSystem(engine));
        engine.addSystem(new EntityUpdateSystem(game, this));
    }

    /**
     * Invoked before world is loaded
     * Useful for registering things that are already defined or known
     */
    protected abstract void preLoad();

    protected abstract void load();

    /**
     * Load world entities that can be interacted with
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadInteractableEntities(OasisGame game, Asset asset, TiledMap worldMap, float worldScale) {
        TiledMapLoader.loadMapObjects(worldMap, worldScale, "Entities", (object, rectangle) -> {
            final EntityNPCType type = EntityNPCType.findType(object);
            if (type != null) {
                final EntityInteractable entity = type.create(new Vector2(rectangle.x, rectangle.y), game, this);
                entity.load(asset);
                addInteractableEntity(entity);
            } else {
                GameLogging.warn(this, "Found invalid entity: " + object);
            }
        });

        GameLogging.info(this, "Loaded %d entities.", entities.size());
    }

    /**
     * Add an interactable entity to this world
     * Automatically assigns ID and to engine
     *
     * @param entity the entity
     */
    public void addInteractableEntity(EntityInteractable entity) {
        entity.setEntityId(this.entities.size() + 1);
        this.entities.put(entity.getEntityId(), entity);
        // allows the entity update system to add this to the nearby entities list
        entity.setNearby(false);
        engine.addEntity(entity.getEntity());
    }

    /**
     * Remove an interactable entity from this world
     * TODO: In update system maybe check for a state within the entity to queue to remove from world
     *
     * @param entity the entity
     */
    public void removeInteractableEntity(EntityInteractable entity) {
        this.entities.remove(entity.getEntityId());
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
    protected void loadWorldObjects(TiledMap worldMap, Asset asset, float worldScale) {
        TiledMapLoader.loadMapObjects(worldMap, worldScale, "WorldObjects", (object, rectangle) -> {
            try {
                final boolean hasCollision = TiledMapLoader.ofBoolean(object, "hasCollision");
                final boolean interactable = TiledMapLoader.ofBoolean(object, "interactable");
                final float distance = TiledMapLoader.ofFloat(object, "interaction_distance");
                final String key = TiledMapLoader.ofString(object, "key");
                final String type = TiledMapLoader.ofString(object, "interaction_type");

                if (interactable) {
                    // find interaction and create world object either from key or type
                    final WorldInteractionType interactionType = WorldInteractionType.of(type);
                    final InteractableWorldObject worldObject = interactionType.get(key, interactionManager);

                    worldObject.setWorldIn(this);
                    worldObject.setPosition(rectangle.x, rectangle.y);
                    worldObject.setSize(rectangle.width, rectangle.height);
                    worldObject.load(asset);

                    loadWorldObjectParticles(worldObject, object, asset);

                    // load collision for this object
                    if (hasCollision) createObjectCollisionBody(worldObject, rectangle);
                    interactableWorldObjects.add(worldObject);
                } else {
                    // load base object
                    final WorldObject worldObject = new SimpleWorldObject();
                    worldObject.load(asset);

                    // find texture of this object
                    final String textureKey = TiledMapLoader.ofString(object, "texture");
                    final TextureRegion texture = asset.get(textureKey);

                    if (hasCollision) createObjectCollisionBodyFromTexture(worldObject, rectangle, texture);

                    worldObject.setTexture(texture);
                    worldObject.setPosition(rectangle.x - rectangle.width, rectangle.y - rectangle.height);
                    worldObject.setSize(texture.getRegionWidth() * OasisGameSettings.SCALE, texture.getRegionHeight() * OasisGameSettings.SCALE);
                    loadWorldObjectParticles(worldObject, object, asset);

                    worldObjects.add(worldObject);
                }

            } catch (Exception any) {
                GameLogging.exceptionThrown(this, "Failed to load a world object", any);
            }
        });

        GameLogging.info(this, "Loaded %d interactable objects.", interactableWorldObjects.size());
        GameLogging.info(this, "Loaded %d world objects.", worldObjects.size());
    }

    /**
     * Load particle effects for a world object, interactable or not.
     *
     * @param wb     wb
     * @param object obj
     * @param asset  assets
     */
    private void loadWorldObjectParticles(WorldObject wb, MapObject object, Asset asset) {
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
     * @param wb        wb - unused
     * @param rectangle the rectangle shape
     */
    private void createObjectCollisionBody(WorldObject wb, Rectangle rectangle) {
        // create collision body, offset position to fit within bounds.
        // TODO: Store collision body for disposal later.
        CollisionShapeCreator
                .createPolygonShapeInWorld(
                        rectangle.x,
                        rectangle.y,
                        rectangle.width,
                        rectangle.height,
                        OasisGameSettings.SCALE,
                        true,
                        world);
    }

    /**
     * Create a collision body for the object using the textures bounds
     *
     * @param wb        object - unused
     * @param rectangle rectangle shape
     * @param texture   texture
     */
    private void createObjectCollisionBodyFromTexture(WorldObject wb, Rectangle rectangle, TextureRegion texture) {
        if (texture == null) return;

        // create collision body, offset position to fit within bounds.
        final Body body = CollisionShapeCreator.createPolygonShapeInWorld(
                rectangle.x - ((texture.getRegionWidth() / 2f) * OasisGameSettings.SCALE),
                rectangle.y - ((texture.getRegionHeight() / 3f) * OasisGameSettings.SCALE),
                texture.getRegionWidth(),
                texture.getRegionHeight(),
                OasisGameSettings.SCALE,
                true,
                world
        );
    }

    /**
     * Load interiors within this world
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    public void loadWorldInteriors(TiledMap worldMap, float worldScale) {
        final boolean result = TiledMapLoader.loadMapObjects(worldMap, worldScale, "Interior", (object, bounds) -> {
            final boolean enterable = object.getProperties().get("enterable", true, Boolean.class);
            final String interiorName = object.getProperties().get("instance_name", null, String.class);
            final String interiorType = object.getProperties().get("instance_type", null, String.class);
            final InstanceType type = interiorType == null ? InstanceType.DEFAULT : InstanceType.valueOf(interiorType.toUpperCase());
            if (interiorName != null && enterable) {
                final Cursor instanceCursor = Cursor.valueOf(object.getProperties().get("cursor", "default", String.class).toUpperCase());
                this.instances.put(type, type.createInstance(game, player, this, interiorName, instanceCursor, bounds));
                GameLogging.info(this, "Loaded interior: %s", type);
            }
        });

        if (result) GameLogging.info(this, "Loaded %d instances.", instances.size());
    }

    public void loadEntityPaths(TiledMap worldMap, float worldScale) {
        final boolean result = TiledMapLoader.loadMapObjects(worldMap, worldScale, "Paths", (object, rectangle) -> {
            GameLogging.info(this, "Path point: " + rectangle);
            paths.add(new Vector2(rectangle.x, rectangle.y));
        });
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
        preRender(delta);
    }

    @Override
    public float update(float d) {
        d = super.update(d);
        updateCursorState();
        return d;
    }

    /**
     * Handle pausing states before actually rendering or updating
     *
     * @param delta the delta
     */
    protected void preRender(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (paused) {
            // render, no update
            renderWorld(delta);
        } else {
            if (showPausedScreen) showPausedScreen = false;
            // no pause state, render+update normally
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

        delta = this.update(delta);
        this.renderWorld(delta);
    }

    /**
     * Update cursor state
     */
    protected void updateCursorState() {
        if (guiManager.isAnyGuiVisible(GuiType.HUD)) return;

        // update world cursor
        renderer.getCamera().unproject(cursorInWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        boolean hasEntity = false;
        for (EntityInteractable entityInteractable : nearbyEntities.keySet()) {
            if (entityInteractable.isMouseInEntityBounds(cursorInWorld)) {
                // mouse is over this entity
                if (!guiManager.wasCursorChanged()) guiManager.setCursorInGame(Cursor.DIALOG);
                hasEntity = true;
                break;
            }
        }

        boolean hasInterior = false;
        for (Instance interior : instances.values()) {
            if (interior.isMouseWithinBounds(cursorInWorld)) {
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
        for (OasisNetworkPlayer player : players.values()) {
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
        for (WorldObject object : worldObjects) object.render(batch, delta);

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

        // render local player next
        player.render(batch, delta);
    }

    /**
     * End world rendering and render GUI(s)
     */
    public void endRender() {
        // render player name-tags last
        batch.setProjectionMatrix(guiManager.getStage().getCamera().combined);
        for (OasisNetworkPlayer player : players.values()) {
            if (player.shouldRenderNametag()) {
                guiManager.renderPlayerNametag(player, renderer.getCamera(), batch);
            }
        }

        // FIXME: Implement enemies
       /* EntityEnemy entityEnemy = null;
        for (Entity entity : entities.values()) {
            if (entity instanceof EntityEnemy) {
                entityEnemy = (EntityEnemy) entity;
                ((EntityEnemy) entity).drawDamageIndicator(batch);
            }
        }*/

        batch.end();

       /* if (entityEnemy != null) {
            shapes.setProjectionMatrix(renderer.getCamera().combined);
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.box(entityEnemy.getBounds().x, entityEnemy.getBounds().y, 0.0f, entityEnemy.getBounds().width, entityEnemy.getBounds().getHeight(), 1.0f);
            if (player.getEquippedItem() != null) {
                shapes.box(player.getEquippedItem().getBounds().x, player.getEquippedItem().getBounds().y, 0.0f, player.getEquippedItem().getBounds().getWidth(), player.getEquippedItem().getBounds().getHeight(), 1.0f);
            }
            shapes.end();
        }*/

        // gui.updateAndRender();
        game.guiManager.updateAndDrawStage();
    }

    /**
     * Get an {@link EntityInteractable} by their NPC type
     *
     * @param type the type
     * @return the {@link EntityInteractable} or {@code  null} if not found
     */
    public EntityInteractable getEntityByType(EntityNPCType type) {
        return (EntityInteractable) entities.values()
                .stream()
                .filter(entity -> entity instanceof EntityInteractable && ((EntityInteractable) entity).getType() == type)
                .findFirst()
                .orElse(null);
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

    public ConcurrentHashMap<EntityInteractable, Float> getNearbyEntities() {
        return nearbyEntities;
    }

    public EntityEnemy hasHitEntity(ItemWeapon item) {
       /* for (Entity value : entities.values()) {
            if (value instanceof EntityEnemy interactable) {
                if (interactable.isFacingEntity(player.getAngle())
                        && interactable.getBounds().overlaps(item.getBounds())) {
                    return interactable;
                }
            }
        }*/
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Instance> T getInstance(InstanceType type) {
        return (T) instances.get(type);
    }

    /**
     * Interact with an entity if they were clicked on.
     * Finds the first entity and does not allow multiple
     */
    protected boolean interactWithEntity() {
        // find entity clicked on
        final EntityInteractable closest = getNearbyEntities()
                .keySet()
                .stream()
                .filter(entity -> entity.isMouseInEntityBounds(cursorInWorld))
                .findFirst()
                .orElse(null);

        if (closest == null) {
            // check for network players

        }

        if (closest != null
                && closest.isSpeakable()
                && !closest.isSpeakingTo()) {
            closest.setSpeakingTo(true);
            guiManager.showGui(GuiType.DIALOG);
            guiManager.getDialogComponent().showEntityDialog(closest);
            return true;
        }
        return false;
    }

    /**
     * Interact with the environment
     */
    protected boolean interactWithObject() {
        final InteractableWorldObject worldObject = interactableWorldObjects
                .stream()
                .filter(InteractableWorldObject::isInInteractionRange)
                .filter(wb -> wb.isMouseOver(cursorInWorld))
                .filter(InteractableWorldObject::isEnabled)
                .filter(wb -> !wb.wasInteractedWith())
                .findFirst()
                .orElse(null);

        if (worldObject != null) {
            worldObject.interact();
            return true;
        }
        return false;
    }

    protected boolean interactWithOtherPlayer() {
        players
                .values()
                .stream()
                .filter(np -> np.isMouseInEntityBounds(cursorInWorld) && np.isWithinInteractionDistance(player.getPosition()))
                .findFirst().ifPresent(otherPlayer -> GameLogging.info(this, "Trade"));

        return false;
    }

    protected Instance getInstanceToEnterIfAny() {
        final Instance interior = instances
                .values()
                .stream()
                .filter(e -> e.clickedOn(cursorInWorld))
                .findFirst()
                .orElse(null);

        if (interior != null
                && interior.enterable()
                && interior.isWithinEnteringDistance(player.getPosition())) {
            return interior;
        }
        return null;
    }

    @Override
    public boolean keyDown(int keycode) {
        return GameManager.handleWorldKeyPress(this, keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (interactWithEntity()) return true;
        if (interactWithObject()) return true;
        if (interactWithOtherPlayer()) return true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public void dispose() {
        unloadBox2dWorld();
        super.dispose();
    }
}
