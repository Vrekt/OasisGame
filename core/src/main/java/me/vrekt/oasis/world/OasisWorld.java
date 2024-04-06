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
import com.badlogic.gdx.utils.Pools;
import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.v2.packet.server.ServerPacketCreatePlayer;
import gdx.lunar.world.AbstractGameWorld;
import gdx.lunar.world.WorldConfiguration;
import lunar.shared.entity.LunarEntity;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.OasisEntity;
import me.vrekt.oasis.entity.npc.EntityEnemy;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.system.EntityInteractableAnimationSystem;
import me.vrekt.oasis.entity.npc.system.EntityUpdateSystem;
import me.vrekt.oasis.entity.player.mp.OasisNetworkPlayer;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.graphics.tiled.OasisTiledRenderer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.rewrite.GuiManager;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.save.entity.EntitySaveProperties;
import me.vrekt.oasis.save.loading.SaveStateLoader;
import me.vrekt.oasis.save.world.WorldSaveProperties;
import me.vrekt.oasis.utility.collision.BasicPlayerCollisionHandler;
import me.vrekt.oasis.utility.collision.CollisionShapeCreator;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.interior.Instance;
import me.vrekt.oasis.world.interior.InstanceType;
import me.vrekt.oasis.world.obj.WorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents a base world within the game
 */
public abstract class OasisWorld extends AbstractGameWorld<OasisNetworkPlayer, OasisEntity>
        implements InputProcessor, SaveStateLoader<WorldSaveProperties>, Screen {

    protected final OasisGame game;
    protected final OasisPlayer player;

    protected TiledMap map;
    protected String mapName, worldName;

    protected final SpriteBatch batch;
    protected final OasisTiledRenderer renderer;
    protected final Vector3 cursorInWorld = new Vector3();
    protected boolean isWorldLoaded;

    protected int width, height;

    // pause state
    protected boolean paused, showPausedScreen;

    protected GameGui gui;
    protected GuiManager guiManager;

    protected final ConcurrentHashMap<EntityInteractable, Float> nearbyEntities = new ConcurrentHashMap<>();
    protected final List<ParticleEffect> effects = new ArrayList<>();

    // objects within this world
    protected final CopyOnWriteArraySet<WorldObject> worldObjects = new CopyOnWriteArraySet<>();
    protected final CopyOnWriteArraySet<InteractableWorldObject> interactableWorldObjects = new CopyOnWriteArraySet<>();

    protected final Map<InstanceType, Instance> instances = new HashMap<>();

    // the current tick of this world.
    protected float currentWorldTick;

    protected ShapeRenderer shapes;
    protected NinePatch gradient;
    protected float lastWorldTickUpdate;

    public OasisWorld(OasisGame game, OasisPlayer player, World world) {
        super(player, world, new WorldConfiguration(), new PooledEngine());
        this.player = player;
        this.game = game;
        this.renderer = game.getRenderer();
        this.batch = game.getBatch();
        this.gui = game.getGui();
        this.guiManager = game.guiManager;

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
                    interactable.setPosition(entityState.getPosition(), true);
                    interactable.setHealth(entityState.getHealth());
                    interactable.setEnemy(entityState.isEnemy());

                    GameLogging.info(this, "Found an entity from save and loaded it: " + entityState.getType());
                } else {
                    GameLogging.warn(this, "Failed to find an entity from save by type: " + entityState.getType());
                }
            }
        }

    }

    public String getMapName() {
        return mapName;
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

    public float getCurrentWorldTick() {
        // check if player is in instance to avoid
        // ui problems. Parent world isn't updated so
        // must return instance tick.
        if (player.isInInstance()) {
            return player.getInstanceIn().currentWorldTick;
        }
        return currentWorldTick;
    }

    /**
     * Handle registering custom connection options for a world
     *
     * @param connection the player connection
     */
    private void handleConnectionOptions(PlayerConnectionHandler connection) {
        connection.registerHandlerSync(ServerPacketCreatePlayer.PACKET_ID, packet -> handleCreatePlayerInWorld((ServerPacketCreatePlayer) packet));
    }


    /**
     * Handle creating new players within this world
     *
     * @param packet the create layer packet
     */
    public void handleCreatePlayerInWorld(ServerPacketCreatePlayer packet) {
        if (packet.getEntityId() == player.getEntityId()) return;

        GameLogging.info(this, "Spawning new network player with ID " + packet.getEntityId() + " and username " + packet.getUsername());
        if (player.isInWorld()) {
            final OasisNetworkPlayer player = new OasisNetworkPlayer(true);
            player.load(game.getAsset());

            player.setProperties(packet.getUsername(), packet.getEntityId());
            player.setSize(15, 25, OasisGameSettings.SCALE);

            player.spawnInWorld(this);
        } else {
            GameLogging.warn(this, "Attempted to spawn player while not in world.");
        }
    }

    /**
     * Enter this world.
     */
    public void enterWorld(boolean fromInstance) {

        // remove player from a world they could already be in
        // ensure we are not already in this world
        if (player.getGameWorldIn() != null
                && !player.getGameWorldIn().getWorldName().equals(worldName)) {
            player.removeFromWorld(this);
            player.setGameWorldIn(null);
        }

        if (fromInstance) {
            player.setInInstance(false);
            player.setInstanceIn(null);
            player.setGameWorldIn(this);
            player.setInWorld(true);


            renderer.setTiledMap(map, spawn.x, spawn.y);

            player.spawnInWorld(this, player.getPosition());
            game.getMultiplexer().addProcessor(this);
            // game.setScreen(this);
        }

        GameManager.resetCursor();
    }

    /**
     * Fade in when entering an instance
     */
    public void enterInstanceAndFadeIn(Instance entering) {
        GameManager.resetCursor();
        GameManager.transitionScreen(this, entering, () -> entering.enter(false));
    }

    public void spawnEntityTypeAt(EntityNPCType type, Vector2 position) {
        GameLogging.info(this, "Spawning a new entity: " + type);
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
     * Pause this world while we are saving the game
     */
    public void pauseGameWhileSaving() {
        this.pause();
    }

    /**
     * Saving game is finished.
     */
    public void saveGameFinished() {
        if (!GameManager.isSaving()) {
            this.resume();
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

        TiledMapLoader.loadMapActions(worldMap, worldScale, spawn, new Rectangle());
        TiledMapLoader.loadMapCollision(worldMap, worldScale, world, this);
        loadInteractableEntities(game, game.getAsset(), worldMap, worldScale);
        loadParticleEffects(worldMap, game.getAsset(), worldScale);
        loadWorldObjects(worldMap, game.getAsset(), worldScale);
        loadWorldInteriors(worldMap, worldScale);

        configuration.worldScale = worldScale;

        this.renderer.setTiledMap(worldMap, spawn.x, spawn.y);
        this.width = renderer.getWidth();
        this.height = renderer.getHeight();
        game.getMultiplexer().addProcessor(this);

        addDefaultWorldSystems();
        world.setContactListener(new BasicPlayerCollisionHandler());

        // player was NOT loaded from a save
        if (player.getPosition().isZero()) {
            player.spawnInWorld(this, spawn);
        } else {
            // player was loaded from a save, reference their current position
            player.spawnInWorld(this, player.getPosition());
        }

        player.setWorld(this);
        player.setGameWorldIn(this);

        handleConnectionOptions(game.getHandler());
        // this.gradient = new NinePatch(game.getAsset().get("health_gradient"), 0, 0, 0, 0);
        this.isWorldLoaded = true;

        shapes = new ShapeRenderer();
    }

    protected void addDefaultWorldSystems() {
        engine.addSystem(new EntityInteractableAnimationSystem(engine));
        engine.addSystem(new EntityUpdateSystem(game, this));
    }

    public void skipCurrentDialog() {
        if (player.isSpeakingToEntity() && player.getEntitySpeakingTo() != null) {
            // advance current dialog stage
            if (player.getEntitySpeakingTo().getDialog().hasOptions()) {
                // return since we can't skip without selecting an option
                return;
            }

            final boolean result = player.getEntitySpeakingTo().advanceDialogStage();
            if (!result) {
                // hide
                gui.hideGui(GuiType.DIALOG);
                return;
            }

            gui.updateDialogKeyPress();
            gui.showEntityDialog(player.getEntitySpeakingTo());
        }
    }

    @Override
    public <T extends LunarEntity> void spawnEntityInWorld(T entity, Vector2 position) {
        if (entity instanceof OasisPlayer) return;
        entity.getInterpolatedPosition().set(position.x, position.y);
        entity.getPreviousPosition().set(position.x, position.y);
        super.spawnEntityInWorld(entity, position);
    }

    /**
     * Load world NPCs
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

        GameLogging.info(this, "Loaded " + (entities.size()) + " entities.");
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

    public EntityInteractable getLoadedEntity(EntityNPCType type) {
        for (OasisEntity entity : entities.values()) {
            if (entity.isInteractable() && entity.asInteractable().getType() == type) {
                return entity.asInteractable();
            }
        }
        return null;
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

        if (result) GameLogging.info(this, "Loaded " + (effects.size()) + " particle effects.");
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
                // assign n find texture
                final boolean hasTexture = object.getProperties().get("hasTexture", false, Boolean.class);
                final TextureRegion texture = hasTexture ? asset.get(object.getProperties().get("texture", String.class)) : null;
                final boolean interactable = object.getProperties().get("interactable", Boolean.class);
                final boolean hasCollision = object.getProperties().get("hasCollision", false, Boolean.class);
                final int runtimeId = object.getProperties().get("runtimeId", -1, Integer.class);

                if (interactable) {
                    final InteractableWorldObject worldObject = Pools.obtain(WorldInteractionType.getInteractionFromName(object.getProperties().get("interaction_type", String.class)));
                    worldObject.load(asset);
                    if (hasTexture) {
                        worldObject.initialize(this,
                                rectangle.x - ((texture.getRegionWidth() / 2f) * OasisGameSettings.SCALE),
                                rectangle.y - ((texture.getRegionHeight() / 3f) * OasisGameSettings.SCALE),
                                texture.getRegionWidth() * OasisGameSettings.SCALE,
                                texture.getRegionHeight() * OasisGameSettings.SCALE);
                        worldObject.setTexture(texture);
                    } else {
                        worldObject.initialize(this, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                    }

                    loadWorldObjectEffects(worldObject, object, rectangle);
                    if (hasCollision && hasTexture) {
                        loadWorldObjectBody(worldObject, rectangle, texture);
                    } else if (!hasTexture) {
                        loadWorldObjectBody(worldObject, rectangle);
                    }
                    worldObject.setRuntimeId(runtimeId);
                    this.interactableWorldObjects.add(worldObject);
                } else {
                    final WorldObject wb = Pools.obtain(WorldObject.class);
                    wb.load(asset);
                    // TODO: init WorldObjectType

                    loadWorldObjectEffects(wb, object, rectangle);
                    if (hasTexture) {
                        loadWorldObjectBody(wb, rectangle, texture);
                    } else {
                        loadWorldObjectBody(wb, rectangle);
                    }
                    wb.setRuntimeId(runtimeId);
                    this.worldObjects.add(wb);
                }
            } catch (Exception any) {
                GameLogging.error(this, "Failed to load a world object with error: \n");
                any.printStackTrace();
            }
        });

        GameLogging.info(this, "Loaded " + (interactableWorldObjects.size()) + " interactable objects.");
        GameLogging.info(this, "Loaded " + (worldObjects.size()) + " world objects.");
    }

    /**
     * Load particle effects for a world object, interactable or not.
     *
     * @param wb        wb
     * @param object    obj
     * @param rectangle rect
     */
    private void loadWorldObjectEffects(WorldObject wb, MapObject object, Rectangle rectangle) {
        // load each particle within properties
        object.getProperties().getKeys().forEachRemaining(key -> {
            // find a particle + int identifier
            if (key.contains("particle")) {
                final ParticleEffect effect = new ParticleEffect();
                effect.load(Gdx.files.internal("world/asset/" + object.getProperties().get(key, String.class)), game.getAsset().getAtlasAssets());
                effect.setPosition(rectangle.x, rectangle.y + object.getProperties().get("offset" + (StringUtils.getDigits(key)), 0.0f, float.class));
                effect.start();

                // add this new effect to environ object
                wb.getEffects().add(effect);
            }
        });
    }

    private void loadWorldObjectBody(WorldObject wb, Rectangle rectangle) {
        // create collision body, offset position to fit within bounds.
        final Body body = CollisionShapeCreator
                .createPolygonShapeInWorld(
                        rectangle.x,
                        rectangle.y,
                        rectangle.width,
                        rectangle.height,
                        OasisGameSettings.SCALE,
                        true,
                        world);
        wb.setCollisionBody(body);
    }

    private void loadWorldObjectBody(WorldObject wb, Rectangle rectangle, TextureRegion texture) {
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

        wb.setCollisionBody(body);
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
                final String instanceCursor = object.getProperties().get("cursor", String.class);
                this.instances.put(type, type.createInstance(game, player, this, interiorName, instanceCursor, bounds));
                GameLogging.info(this, "Loaded instance: " + type);
            }
        });

        if (result) GameLogging.info(this, "Loaded " + (instances.size()) + " instances.");
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
            renderer.getViewport().apply();
            renderWorld(game.getBatch(), delta);
        } else {
            if (showPausedScreen) showPausedScreen = false;
            renderer.getViewport().apply();
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
        delta = this.update(delta);
        this.renderWorld(game.getBatch(), delta);

        if (currentWorldTick - lastWorldTickUpdate >= 6.0) {
            lastWorldTickUpdate = currentWorldTick;
        }

        currentWorldTick += delta;
    }

    /**
     * Update cursor state
     */
    protected void updateCursorState() {
        if (gui.isAnyInterfaceOpen()) return;

        // update world cursor
        renderer.getCamera().unproject(cursorInWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        boolean hasEntity = false;
        for (EntityInteractable entityInteractable : nearbyEntities.keySet()) {
            if (!entityInteractable.isEnemy() && entityInteractable.isMouseInEntityBounds(cursorInWorld)) {
                // mouse is over this entity
                if (!GameManager.hasCursorChanged()) GameManager.setCursorInGame(GameManager.DIALOG_CURSOR);
                hasEntity = true;
                break;
            }
        }

        boolean hasInterior = false;
        for (Instance interior : instances.values()) {
            if (interior.isMouseWithinBounds(cursorInWorld)) {
                if (!GameManager.hasCursorChanged()) GameManager.setCursorInGame(interior.getCursor());
                hasInterior = true;
                break;
            }
        }

        // check for environment objects
        boolean hasObj = false;
        if (!hasEntity) {
            for (InteractableWorldObject worldObject : interactableWorldObjects) {
                if (worldObject.clickedOn(cursorInWorld)
                        && worldObject.getCursor() != null
                        && worldObject.isInteractable()) {
                    GameManager.setCursorInGame(worldObject.getCursor());
                    hasObj = true;
                    break;
                }
            }
        }

        // only reset cursor to default state if not currently active
        // and no interactions are being made
        if (!GameManager.isCursorActive()
                && !(hasEntity || hasObj || hasInterior)
                && GameManager.hasCursorChanged()) {
            GameManager.resetCursor();
        }

    }

    /**
     * Render this world
     *
     * @param batch batch
     * @param delta delta
     */
    public void renderWorld(SpriteBatch batch, float delta) {
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

        for (OasisEntity entity : entities.values()) {
            if (entity.isInView(renderer.getCamera())) {
                entity.render(batch, delta);
                entity.renderHealthBar(batch);
            }
        }

        // general world objects
        for (WorldObject object : worldObjects) {
            object.render(batch);
            if (object.playEffects()) object.renderEffects(batch, delta);
        }

        // interactions
        for (InteractableWorldObject worldObject : interactableWorldObjects) {
            if (worldObject.isWithinUpdateDistance(player.getPosition())
                    && worldObject.isInteractedWith()) {
                worldObject.update();
            }

            worldObject.render(batch);
            if (worldObject.playEffects()) worldObject.renderEffects(batch, delta);
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
        batch.setProjectionMatrix(gui.getCamera().combined);
        for (OasisNetworkPlayer player : players.values()) {
            if (player.shouldRenderNametag()) {
                gui.renderPlayerNametag(player, renderer.getCamera(), batch);
            }
        }

        // draw damage indicators
        // use the stage batch to correctly scale the font.
        EntityEnemy entityEnemy = null;
        for (OasisEntity entity : entities.values()) {
            if (entity instanceof EntityEnemy) {
                entityEnemy = (EntityEnemy) entity;
                ((EntityEnemy) entity).drawDamageIndicator(batch);
            }
        }

        //game.guiManager.renderExclusiveGuis(batch);

        batch.end();

        if (entityEnemy != null) {
            shapes.setProjectionMatrix(renderer.getCamera().combined);
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.box(entityEnemy.getBounds().x, entityEnemy.getBounds().y, 0.0f, entityEnemy.getBounds().width, entityEnemy.getBounds().getHeight(), 1.0f);
            if (player.getEquippedItem() != null) {
                shapes.box(player.getEquippedItem().getBounds().x, player.getEquippedItem().getBounds().y, 0.0f, player.getEquippedItem().getBounds().getWidth(), player.getEquippedItem().getBounds().getHeight(), 1.0f);
            }
            shapes.end();
        }

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

    public ConcurrentHashMap<EntityInteractable, Float> getNearbyEntities() {
        return nearbyEntities;
    }

    public EntityEnemy hasHitEntity(ItemWeapon item) {
        for (OasisEntity value : entities.values()) {
            if (value instanceof EntityEnemy) {
                final EntityEnemy interactable = (EntityEnemy) value;
                if (interactable.isFacingEntity(player.getAngle())
                        && interactable.getBounds().overlaps(item.getBounds())) {
                    return interactable;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Instance> T getInstance(InstanceType type) {
        return (T) instances.get(type);
    }

    /**
     * Get a world object by its runtimeId
     * TODO: Randomly generate runtime IDs so objects with no ID get picked instead
     *
     * @param id the ID
     * @return the object if any or {@code null}
     */
    public InteractableWorldObject getByRuntimeId(int id) {
        for (InteractableWorldObject worldObject : interactableWorldObjects) {
            if (worldObject.getRuntimeId() == id) {
                return worldObject;
            }
        }
        return null;
    }

    public <T extends InteractableWorldObject> List<T> getByRuntimeIds(int... id) {
        final List<T> objects = new ArrayList<>();

        for (int i : id) {
            final T t = (T) getByRuntimeId(i);
            if (t != null) objects.add(t);
        }
        return objects;
    }

    public <T extends InteractableWorldObject> Map<Integer, T> getByRuntimeIdsMap(int... id) {
        final Map<Integer, T> map = new HashMap<>();
        for (int i : id) {
            final T t = (T) getByRuntimeId(i);
            if (t != null) map.put(i, t);
        }
        return map;
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
        // only check objects that are within our update distance
        final InteractableWorldObject worldObject = interactableWorldObjects
                .stream()
                .filter(wb -> wb.isWithinInteractionDistance(player.getPosition()))
                .filter(wb -> wb.clickedOn(cursorInWorld))
                .findFirst()
                .orElse(null);

        if (worldObject != null
                && worldObject.isWithinInteractionDistance(player.getPosition())
                && worldObject.isInteractable()
                && !worldObject.isInteractedWith()) {
            if (worldObject.hasRequiredItem()) {
                worldObject.interact();
            } else {
                gui.getHud().showMissingItemWarning(worldObject.getRequiredItemTexture());
            }
            return true;
        }
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

    /**
     * Destroy a world object within this world. Removes box2d collision and object from list
     *
     * @param worldObject the object
     */
    public void destroyWorldObject(InteractableWorldObject worldObject) {
        world.destroyBody(worldObject.getCollisionBody());
        interactableWorldObjects.remove(worldObject);
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
