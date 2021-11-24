package me.vrekt.oasis.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;
import gdx.lunar.world.LunarWorld;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.entity.player.network.NetworkPlayer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.settings.GameSettings;
import me.vrekt.oasis.utilities.collision.CollisionShapeCreator;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.world.common.Enterable;
import me.vrekt.oasis.world.common.InputHandler;
import me.vrekt.oasis.world.common.Interactable;
import me.vrekt.oasis.world.domains.AbstractDomain;
import me.vrekt.oasis.world.domains.DomainType;
import me.vrekt.oasis.world.interior.AbstractInterior;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * A game world, extended from Lunar
 */
public abstract class AbstractWorld extends LunarWorld implements InputHandler, Interactable, Enterable, Screen {
    protected TiledMap map;

    protected final ConcurrentMap<Integer, NetworkPlayer> networkPlayers = new ConcurrentHashMap<>();

    protected final Map<Interior, AbstractInterior> interiors = new HashMap<>();
    protected final Map<DomainType, AbstractDomain> domains = new HashMap<>();

    protected final ConcurrentMap<Integer, EntityInteractable> entities = new ConcurrentHashMap<>();
    protected final Map<EntityNPCType, EntityInteractable> entityTypes = new HashMap<>();

    protected final Asset asset;
    protected final Vector2 spawn = new Vector2(0, 0);

    protected GameGui gui;

    protected GlobalGameRenderer renderer;
    protected Player thePlayer;
    protected SpriteBatch batch;
    protected float scale;

    protected Runnable worldLoadedCallback;
    protected EntityInteractable entityInteractingWith;

    // pause state
    protected FrameBuffer fbo;
    protected TextureRegion fboTexture;
    protected boolean paused, hasFbo;

    protected OasisGame game;

    public AbstractWorld(OasisGame game, Player player, World world, SpriteBatch batch, Asset asset) {
        super(player, world);

        this.register(game.getMultiplexer());
        this.gui = game.getGui();
        this.batch = batch;
        this.thePlayer = player;
        this.asset = asset;
        this.fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        setPlayersCollection(networkPlayers);
    }

    public Player getPlayer() {
        return thePlayer;
    }

    public GameGui getGui() {
        return gui;
    }

    public TiledMap getMap() {
        return map;
    }

    public void setWorldLoadedCallback(Runnable action) {
        this.worldLoadedCallback = action;
    }

    /**
     * Load this world.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    protected abstract void loadWorld(TiledMap worldMap, float worldScale);

    /**
     * Pre-Load this world.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    protected abstract void preLoadWorld(TiledMap worldMap, float worldScale);

    /**
     * Render UI
     */
    public abstract void renderUi();

    /**
     * Load the local player into this world.
     *
     * @param game       the game
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    public void loadIntoWorld(OasisGame game, TiledMap worldMap, float worldScale) {
        this.scale = worldScale;
        this.map = worldMap;
        this.game = game;
        this.preLoadWorld(worldMap, worldScale);

        loadMapActions(worldMap, worldScale);
        loadMapCollision(worldMap, worldScale);

        this.worldScale = worldScale;
        this.renderer = game.getRenderer();
        this.renderer.setDrawingMap(map, spawn.x, spawn.y);

        loadWorldEntities(game, worldMap, worldScale);
        loadWorldInteriors(worldMap, worldScale);
        loadWorldDomains(worldMap, worldScale);
        loadWorld(worldMap, worldScale);

        // initialize player in this world.
        thePlayer.spawnEntityInWorld(this, spawn.x, spawn.y);
        this.worldLoadedCallback.run();
    }

    /**
     * General functions for collecting rectangle objects and handling them
     *
     * @param worldMap   map
     * @param worldScale scale
     * @param layerName  layer to get
     * @param handler    handler
     * @return the result (if the layer was found)
     */
    protected boolean loadMapObjects(TiledMap worldMap, float worldScale, String layerName, BiConsumer<MapObject, Rectangle> handler) {
        final MapLayer layer = worldMap.getLayers().get(layerName);
        if (layer == null) {
            Logging.warn(this, "Failed to load layer: " + layerName);
            return false;
        }

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                final Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                rectangle.x = rectangle.x * worldScale;
                rectangle.y = rectangle.y * worldScale;
                rectangle.width = rectangle.width * worldScale;
                rectangle.height = rectangle.height * worldScale;
                handler.accept(object, rectangle);
            } else {
                Logging.warn(this, "Unknown map object in layer: " + layerName + " {" + object.getName() + "}");
            }
        }

        return true;
    }

    /**
     * Load map actions like spawn points.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    protected void loadMapActions(TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Actions", (object, rectangle) -> {
            if (object.getName().equalsIgnoreCase("WorldSpawn")) {
                // world spawn
                spawn.set(rectangle.x, rectangle.y);
            }

            // others...
        });

        if (!result) Logging.warn(this, "Failed to find world spawn.");
    }

    /**
     * Load map collision
     *
     * @param worldMap   the map
     * @param worldScale the scale
     */
    protected void loadMapCollision(TiledMap worldMap, float worldScale) {
        final MapLayer layer = worldMap.getLayers().get("Collision");
        if (layer == null) {
            Logging.warn(this, "Failed to find collision layer.");
            return;
        }

        int loaded = 0;
        for (MapObject object : layer.getObjects()) {
            CollisionShapeCreator.createCollisionInWorld(object, worldScale, world);
            loaded++;
        }

        Logging.info(this, "Loaded a total of " + loaded + " collision objects.");
    }

    /**
     * Load world NPCs
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadWorldEntities(OasisGame game, TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "NPC", (object, rectangle) -> {
            // find who this NPC is.
            final EntityNPCType type = EntityNPCType.valueOf(object.getName().toUpperCase());
            // create it and load
            final EntityInteractable entity = type.create(rectangle.x, rectangle.y, game, this);
            entity.loadEntity(asset);

            this.entityTypes.put(type, entity);
            this.entities.put(entity.getEntityId(), entity);
        });
        if (result) Logging.info(this, "Loaded " + (entities.size()) + " entities.");
    }

    /**
     * Load enterable houses and buildings within the world
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadWorldInteriors(TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Interiors", (object, rectangle) -> {
            final boolean enterable = object.getProperties().get("enterable", false, Boolean.class);
            final String name = object.getProperties().get("interior", null, String.class);
            if (name != null && enterable) {
                final Interior interior = Interior.valueOf(name.toUpperCase(Locale.ROOT));
                this.interiors.put(interior, interior.createInterior(new Vector2(rectangle.x, rectangle.y), this));
                Logging.info("Objects", "Loaded interior: " + interior);
            } else {
                Logging.warn(this, "Failed to find interior for " + object.getName());
            }
        });
        if (result) Logging.info(this, "Loaded " + (this.interiors.size()) + " interiors.");
    }

    /**
     * Load domains within this world
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadWorldDomains(TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Domains", (object, rectangle) -> {
            final String name = object.getProperties().get("domain", null, String.class);
            if (name != null) {
                final DomainType domainType = DomainType.valueOf(name.toUpperCase(Locale.ROOT));
                this.domains.put(domainType, domainType.createDomain(game, new Vector2(rectangle.x, rectangle.y), this));
                Logging.info("Objects", "Loaded domain: " + domainType);
            } else {
                Logging.warn(this, "Failed to find domain for " + object.getName());
            }
        });
        if (result) Logging.info(this, "Loaded " + (this.domains.size()) + " domains.");
    }

    /**
     * Retrieve the closest entity to the player
     * Minimum distance is 10.0f;
     *
     * @return the entity, or {@code null} if not found.
     */
    public EntityInteractable getClosestEntity() {
        float min = 10.0f;

        EntityInteractable closest = null;
        for (EntityInteractable entity : entities.values()) {
            if (entity.getDistance() <= min) {
                min = entity.getDistance();
                closest = entity;
            }
        }
        return closest;
    }

    public void removeInteractableEntityFromWorld(EntityInteractable entity) {
        this.entities.remove(entity.getEntityId());
    }

    @SuppressWarnings("unchecked")
    public <T extends EntityInteractable> T getEntity(EntityNPCType type) {
        return (T) entityTypes.get(type);
    }

    public <T extends AbstractInterior> T getInterior(Interior interior) {
        return (T) interiors.get(interior);
    }

    @Override
    public void register(InputMultiplexer multiplexer) {
        multiplexer.addProcessor(this);
    }

    /**
     * Unregister listeners and input stuff for this world.
     */
    @Override
    public void unregister(InputMultiplexer multiplexer) {
        multiplexer.removeProcessor(this);
    }

    @Override
    public void exit() {
        this.unregister(game.getMultiplexer());
    }

    @Override
    public void setPlayerInWorld(LunarNetworkEntityPlayer player) {
        networkPlayers.put(player.getEntityId(), (NetworkPlayer) player);
    }

    @Override
    public void update(float d) {
        super.update(d);

        // update our player
        thePlayer.update(d);
        thePlayer.interpolate(1.0f);

        // update all entities within the world, that are within ticking distance
        for (EntityInteractable entity : entities.values()) {
            final double distance = entity.getDistance(thePlayer);
            if (distance <= GameSettings.ENTITY_UPDATE_DISTANCE) {
                entity.update(thePlayer, d);
                entity.setWithinDistance(true);

                // draw interaction dialog animation if near and speakable.
                entity.setDrawDialogAnimationTile(entity.isInView() && entity.isSpeakable());
            } else {
                entity.invalidate();
            }
        }
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        batch.setProjectionMatrix(renderer.getCamera().combined);
        batch.begin();

        // map
        renderer.render();

        // networked players
        for (NetworkPlayer player : networkPlayers.values()) {
            if (player.isInView(renderer.getCamera())) {
                player.render(batch, delta);
            }
        }

        // entities
        for (EntityInteractable entity : entities.values()) {
            if (entity.isInView(renderer.getCamera())) entity.render(batch, scale);
        }

    }

    @Override
    public void render(float delta) {
        if (paused && !hasFbo) fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (paused && !hasFbo) {
            // capture fbo.
            renderInternal(delta);
            hasFbo = true;
        } else if (paused) {
            // draw fbo.
            gui.applyStageViewport();
            batch.setProjectionMatrix(gui.getCamera().combined);
            batch.begin();

            if (fboTexture == null) {
                fboTexture = new TextureRegion(fbo.getColorBufferTexture());
                fboTexture.flip(false, true);
            } else if (fboTexture.getTexture() == null) {
                fboTexture.setTexture(fbo.getColorBufferTexture());
            }

            // draw FBO
            batch.draw(fboTexture, 0, 0);
        } else {
            if (fboTexture != null) fboTexture.setTexture(null);
            renderer.getViewport().apply();
            // no pause state, render normally.
            renderInternal(delta);
        }

        // end
        if (paused && hasFbo) fbo.end();
        if (paused && batch.isDrawing()) batch.end();
    }

    private void renderInternal(float delta) {
        update(delta);
        renderWorld(batch, delta);
        thePlayer.render(batch, delta);
        batch.end();

        renderUi();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == GameSettings.INTERACTION_KEY) {
            handleInteractionKeyPressed();
            return true;
        } else if (keycode == GameSettings.PAUSE_GAME_KEY) {
            if (paused) {
                resume();
            } else {
                pause();
            }
        } else if (keycode == GameSettings.QUEST_KEY) {
            if (gui.isGuiVisible(GuiType.QUEST)) {
                gui.hideGui(GuiType.QUEST);
            } else {
                gui.showGui(GuiType.QUEST);
            }
        }
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {
        this.paused = true;
    }

    @Override
    public void resume() {
        this.paused = false;
        this.hasFbo = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        fbo.dispose();
        fboTexture = null;
        super.dispose();
    }
}
