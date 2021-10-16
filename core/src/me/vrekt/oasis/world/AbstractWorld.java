package me.vrekt.oasis.world;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import me.vrekt.oasis.ui.world.GameWorldInterface;
import me.vrekt.oasis.utilities.collision.CollisionShapeCreator;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.utilities.logging.Taggable;
import me.vrekt.oasis.world.farm.FarmingAllotment;
import me.vrekt.oasis.world.renderer.WorldRenderer;
import me.vrekt.oasis.world.shop.Shop;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * A game world, extended from Lunar
 */
public abstract class AbstractWorld extends LunarWorld implements Screen, Taggable {

    /**
     * Static body
     */
    private static final BodyDef STATIC_BODY = new BodyDef();

    static {
        STATIC_BODY.type = BodyDef.BodyType.StaticBody;
    }

    protected final ConcurrentMap<Integer, NetworkPlayer> networkPlayers = new ConcurrentHashMap<>();
    protected final Array<FarmingAllotment> allotments = new Array<>();

    // all NPCs
    protected final Map<String, EntityInteractable> entities = new HashMap<>();
    // entities close to the player
    protected final Map<EntityInteractable, Float> entitiesInVicinity = new ConcurrentHashMap<>();

    protected final Array<Shop> shops = new Array<>();

    protected final Asset asset;
    protected final Vector2 spawn = new Vector2(0, 0);

    protected WorldRenderer renderer;
    protected Player thePlayer;
    protected SpriteBatch batch;
    protected float scale;

    protected Runnable worldLoadedCallback;
    protected EntityInteractable entityInteractingWith;

    public AbstractWorld(Player player, World world, SpriteBatch batch, Asset asset) {
        super(player, world);

        this.batch = batch;
        this.thePlayer = player;
        this.asset = asset;

        setPlayersCollection(networkPlayers);
    }

    public Player getPlayer() {
        return thePlayer;
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
    public abstract void handleInteractionKeyPressed();

    /**
     * Render UI
     */
    public abstract void renderUi();

    /**
     * Local world UI
     *
     * @return the UI
     */
    public abstract GameWorldInterface getUi();

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

        loadWorldAllotments(worldMap, worldScale);
        loadWorldNPC(game, worldMap, worldScale);
        loadWorldShops(worldMap, worldScale);
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
                final PolygonShape shape = CollisionShapeCreator.createPolygonShape((RectangleMapObject) object, worldScale);
                createStaticBody(shape);
            } else {
                Logging.warn(WORLD, "Unknown map object collision type: " + object.getName() + ":" + object.getClass());
            }

            loaded++;
        }

        Logging.info(WORLD, "Loaded a total of " + loaded + " collision objects.");
    }

    /**
     * Load all farm allotments within this world
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadWorldAllotments(TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Allotments", (o, rectangle) -> this.allotments.add(new FarmingAllotment(rectangle)));
        if (result) Logging.info(WORLD, "Loaded " + (allotments.size) + " allotments.");
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

            this.entities.put(object.getName(), entity);
        });
        if (result) Logging.info(WORLD, "Loaded " + (entities.size()) + " NPCs.");
    }

    /**
     * Load shops within this world
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadWorldShops(TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Shop", (o, rectangle) -> this.shops.add(new Shop(rectangle)));
        if (result) Logging.info(WORLD, "Loaded " + (this.shops.size) + " Shops.");
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

    public EntityInteractable getClosestEntity() {
        float min = 10f;
        EntityInteractable entity = null;
        for (Map.Entry<EntityInteractable, Float> e : entitiesInVicinity.entrySet()) {
            if (e.getValue() <= min) {
                min = e.getValue();
                entity = e.getKey();
            }
        }

        return entity;
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

        for (EntityInteractable entity : entities.values()) {
            entity.update(thePlayer, d);

            final float dstTo = entity.dst2(thePlayer);
            if (dstTo <= 10f) {
                entitiesInVicinity.put(entity, dstTo);
            } else {
                entitiesInVicinity.remove(entity);
            }
        }

        for (Shop shop : shops) shop.update(thePlayer);
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
        for (EntityInteractable entity : entities.values())
            if (entity.isInView(renderer.getCamera())) entity.render(batch, scale);
    }

    @Override
    public void render(float delta) {
        update(delta);

        renderWorld(batch, delta);
        thePlayer.render(batch, delta);

        batch.end();
        renderUi();
    }

    @Override
    public void show() {
        Logging.info(WORLD, "Showing world...");
    }

    @Override
    public void pause() {
        Logging.info(WORLD, "Pausing world...");
    }

    @Override
    public void resume() {
        Logging.info(WORLD, "Resuming world...");
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose(); // TODO
    }
}
