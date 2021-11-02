package me.vrekt.oasis.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;
import gdx.lunar.world.LunarWorld;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.entity.player.network.NetworkPlayer;
import me.vrekt.oasis.settings.GameSettings;
import me.vrekt.oasis.ui.gui.GameGui;
import me.vrekt.oasis.ui.gui.quest.QuestGui;
import me.vrekt.oasis.utilities.array.EntityComparableArray;
import me.vrekt.oasis.utilities.collision.CollisionShapeCreator;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.utilities.logging.Taggable;
import me.vrekt.oasis.world.obj.InteractableWorldObject;
import me.vrekt.oasis.world.region.WorldRegion;
import me.vrekt.oasis.world.renderer.WorldRenderer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * A game world, extended from Lunar
 */
public abstract class AbstractWorld extends LunarWorld implements InputProcessor, Screen, Taggable {

    /**
     * Static body
     */
    public static final BodyDef STATIC_BODY = new BodyDef();

    static {
        STATIC_BODY.type = BodyDef.BodyType.StaticBody;
    }

    protected final InputMultiplexer multiplexer = new InputMultiplexer();

    protected final ConcurrentMap<Integer, NetworkPlayer> networkPlayers = new ConcurrentHashMap<>();
    protected final Array<WorldRegion> regions = new Array<>();

    // world objects sorted by the entity the object could be related to.
    protected final ConcurrentMap<String, InteractableWorldObject> objectsByRelation = new ConcurrentHashMap<>();
    protected final Array<InteractableWorldObject> objects = new Array<>();

    protected final Array<EntityInteractable> entities = new Array<>();
    protected final EntityComparableArray entitiesInVicinity = new EntityComparableArray();

    protected WorldRegion regionIn;

    protected final Asset asset;
    protected final Vector2 spawn = new Vector2(0, 0);

    protected GameGui gui;

    protected WorldRenderer renderer;
    protected Player thePlayer;
    protected SpriteBatch batch;
    protected float scale;

    protected Runnable worldLoadedCallback;
    protected EntityInteractable entityInteractingWith;

    // pause state
    protected FrameBuffer fbo;
    protected TextureRegion fboTexture;
    protected boolean paused, hasFbo;

    public AbstractWorld(Player player, World world, SpriteBatch batch, Asset asset) {
        super(player, world);

        this.batch = batch;
        this.thePlayer = player;
        this.asset = asset;
        this.multiplexer.addProcessor(this);
        this.fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        setPlayersCollection(networkPlayers);
    }

    public Player getPlayer() {
        return thePlayer;
    }

    public GameGui getGui() {
        return gui;
    }

    public InteractableWorldObject getObjectByRelation(String relation) {
        return objectsByRelation.get(relation);
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
     * The interaction key was pressed.
     */
    protected void handleInteractionKeyPressed() {

    }

    /**
     * Player is clicking
     */
    protected void clicked() {

    }

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
        this.preLoadWorld(worldMap, worldScale);

        loadMapActions(worldMap, worldScale);
        loadMapCollision(worldMap, worldScale);

        this.worldScale = worldScale;
        this.renderer = new WorldRenderer(worldMap, worldScale, spawn, batch, thePlayer);

        loadWorldNPC(game, worldMap, worldScale);
        loadWorldRegions(worldMap, worldScale);
        loadWorldObjects(worldMap, worldScale);
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
            Logging.warn(WORLD, "Failed to load layer: " + layerName);
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
                Logging.warn(WORLD, "Unknown map object in layer: " + layerName + " {" + object.getName() + "}");
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

        if (!result) Logging.warn(WORLD, "Failed to find world spawn.");
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
            Logging.warn(WORLD, "Failed to find collision layer.");
            return;
        }

        int loaded = 0;
        for (MapObject object : layer.getObjects()) {
            if (object instanceof PolylineMapObject) {
                final ChainShape shape = CollisionShapeCreator.createPolylineShape((PolylineMapObject) object, worldScale);
                createStaticBody(shape);
            } else if (object instanceof PolygonMapObject) {
                final PolygonShape shape = CollisionShapeCreator.createPolygonShape((PolygonMapObject) object, worldScale);
                createStaticBody(shape);
            } else if (object instanceof RectangleMapObject) {
                final PolygonShape shape = CollisionShapeCreator.createPolygonShape((RectangleMapObject) object, worldScale, true);
                createStaticBody(shape);
            } else {
                Logging.warn(WORLD, "Unknown map object collision type: " + object.getName() + ":" + object.getClass());
            }

            loaded++;
        }

        Logging.info(WORLD, "Loaded a total of " + loaded + " collision objects.");
    }

    /**
     * Load world NPCs
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadWorldNPC(OasisGame game, TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "NPC", (object, rectangle) -> {
            // find who this NPC is.
            final EntityNPCType type = EntityNPCType.valueOf(object.getName().toUpperCase());
            // create it and load
            final EntityInteractable entity = type.create(rectangle.x, rectangle.y, game, this);
            entity.load(asset);

            this.entities.add(entity);
        });
        if (result) Logging.info(WORLD, "Loaded " + (entities.size) + " NPCs.");
    }

    /**
     * Load world regions
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadWorldRegions(TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Region", (o, rectangle) -> this.regions.add(new WorldRegion(o.getName(), rectangle)));
        if (result) Logging.info(WORLD, "Loaded " + (this.regions.size) + " Regions.");
    }

    /**
     * Load interactable objects within this world
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadWorldObjects(TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Objects", (o, rectangle) -> {
            final InteractableWorldObject object = new InteractableWorldObject(o, rectangle, asset, world, worldScale);
            if (object.getRelatedTo() != null) {
                objectsByRelation.put(object.getRelatedTo(), object);
            }

            objects.add(object);
        });
        if (result) Logging.info(WORLD, "Loaded " + (this.objects.size) + " Objects.");
    }

    /**
     * Create a static body from shape
     *
     * @param shape the shape
     */
    private void createStaticBody(Shape shape) {
        world.createBody(STATIC_BODY).createFixture(shape, 1.0f);
        shape.dispose();
    }

    /**
     * Retrieve the closest entity to the player
     * Minimum distance if 40.0f;
     *
     * @return the entity, or {@code null} if not found.
     */
    public EntityInteractable getClosestEntity() {
        float min = 40.0f;

        EntityInteractable closest = null;
        for (EntityInteractable entity : entitiesInVicinity) {
            if (entity.getDistance() <= min) {
                min = entity.getDistance();
                closest = entity;
            }
        }
        return closest;
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
        thePlayer.interpolate(0.5f);

        // update all entities within the world, that the player can see.
        for (EntityInteractable entity : entities) {
            final double distance = entity.getDistance(thePlayer);
            if (distance <= GameSettings.ENTITY_UPDATE_DISTANCE) {
                entity.update(thePlayer, d);

                if (entity.isInView()) {
                    // is in view, so do rendering updates.
                    entity.setDrawDialogAnimationTile(entity.isSpeakable());
                } else {
                    entity.setDrawDialogAnimationTile(false);
                }

                if (!entitiesInVicinity.contains(entity))
                    entitiesInVicinity.add(entity);
            } else {
                // entity is not near us, remove them and invalidate their state.
                entity.setDrawDialogAnimationTile(false);
                entity.setSpeakingTo(false);

                if (entitiesInVicinity.contains(entity))
                    entitiesInVicinity.removeValue(entity);
            }
        }

        // update world objects
        final Array.ArrayIterator<InteractableWorldObject> iterator = objects.iterator();
        while (iterator.hasNext()) {
            final InteractableWorldObject object = iterator.next();
            if (object.isFinished()) {
                iterator.remove(); // remove from list
                world.destroyBody(object.getBody()); // destroy collision
                if (object.getRelatedTo() != null) objectsByRelation.remove(object.getRelatedTo());
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
        for (NetworkPlayer player : networkPlayers.values())
            if (player.isInView(renderer.getCamera())) player.render(batch, delta);

        // entities
        for (EntityInteractable entity : entities)
            if (entity.isInView(renderer.getCamera())) entity.render(batch, scale);

        // objects
        for (InteractableWorldObject object : objects) {
            if (object.isRender()) object.render(batch);
        }
    }

    @Override
    public void render(float delta) {
        if (paused && !hasFbo) fbo.begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (paused && !hasFbo) {
            // capture fbo.
            renderInternal(delta);
            hasFbo = true;
        } else if (paused) {
            // draw fbo.
            gui.apply();
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
            if (gui.isGuiVisible(QuestGui.ID)) {
                gui.hideGui(QuestGui.ID);
            } else {
                gui.showGui(QuestGui.ID);
            }
        }
        return false;
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
        clicked();
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
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
        Logging.info(WORLD, "Showing world...");
    }

    @Override
    public void pause() {
        this.paused = true;
        Logging.info(WORLD, "Pausing world...");
    }

    @Override
    public void resume() {
        this.paused = false;
        this.hasFbo = false;
        Logging.info(WORLD, "Resuming world...");
    }

    @Override
    public void hide() {
        Logging.info(WORLD, "Hiding world...");
    }

    @Override
    public void dispose() {
        fbo.dispose();
        fboTexture = null;
        super.dispose();
    }
}
