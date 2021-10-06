package me.vrekt.oasis.world;

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
import gdx.lunar.world.LunarWorld;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.utilities.collision.CollisionShapeCreator;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.utilities.logging.Taggable;
import me.vrekt.oasis.world.farm.FarmingAllotment;
import me.vrekt.oasis.world.renderer.WorldRenderer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A game world, extended from Lunar
 */
public abstract class AbstractWorld extends LunarWorld implements Taggable {

    /**
     * Static body
     */
    private static final BodyDef STATIC_BODY = new BodyDef();

    static {
        STATIC_BODY.type = BodyDef.BodyType.StaticBody;
    }

    protected final Array<FarmingAllotment> allotments = new Array<>();

    protected final Vector2 spawn = new Vector2();
    protected WorldRenderer renderer;
    protected Player thePlayer;
    protected SpriteBatch batch;
    protected float scale;

    public AbstractWorld(Player player, World world, SpriteBatch batch) {
        super(player, world);

        this.batch = batch;
        this.thePlayer = player;
    }

    public WorldRenderer getRenderer() {
        return renderer;
    }

    public Player getPlayer() {
        return thePlayer;
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
     * Load the local player into this world.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    public void loadIntoWorld(TiledMap worldMap, float worldScale) {
        this.scale = worldScale;

        loadMapActions(worldMap, worldScale);
        loadMapCollision(worldMap, worldScale);

        this.worldScale = worldScale;
        this.renderer = new WorldRenderer(worldMap, worldScale, spawn, batch, thePlayer);

        loadWorldAllotments(worldMap, worldScale);
        loadWorld(worldMap, worldScale);

        // initialize player in this world.
        thePlayer.spawnEntityInWorld(this, spawn.x, spawn.y);
    }

    /**
     * Load map actions like spawn points.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    protected void loadMapActions(TiledMap worldMap, float worldScale) {
        final MapLayer layer = worldMap.getLayers().get("Actions");
        if (layer == null) {
            Logging.warn(WORLD, "Failed to load actions layer for a world map.");
            return;
        }

        // load world spawn.
        final RectangleMapObject worldSpawn = (RectangleMapObject) layer.getObjects().get("WorldSpawn");
        if (worldSpawn == null) {
            Logging.warn(WORLD, "Failed to find world spawn.");
            spawn.set(0.0f, 0.0f);
            return;
        }

        // set spawn, scale vec by world scale to correctly set.
        spawn.set(worldSpawn.getRectangle().x, worldSpawn.getRectangle().y).scl(worldScale);
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

        final AtomicInteger objectsLoaded = new AtomicInteger();
        layer.getObjects().forEach(object -> {
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

            objectsLoaded.addAndGet(1);
        });

        Logging.info(WORLD, "Loaded a total of " + objectsLoaded.get() + " collision objects.");
    }

    /**
     * Load all farm allotments within this world
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadWorldAllotments(TiledMap worldMap, float worldScale) {
        final MapLayer farmLayer = worldMap.getLayers().get("Allotments");
        if (farmLayer == null) {
            Logging.warn(WORLD, "Failed to find farm allotments in world.");
        } else {
            for (MapObject object : farmLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    // ensure we have a valid land of plot.
                    final RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
                    final Rectangle bounds = new Rectangle();

                    bounds.x = rectangleMapObject.getRectangle().x * worldScale;
                    bounds.y = rectangleMapObject.getRectangle().y * worldScale;
                    bounds.width = rectangleMapObject.getRectangle().width * worldScale;
                    bounds.height = rectangleMapObject.getRectangle().height * worldScale;
                    this.allotments.add(new FarmingAllotment(bounds));
                }
            }
        }

        Logging.info(WORLD, "Loaded " + (allotments.size) + " allotments.");
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

    @Override
    public void update(float d) {
        super.update(d);
        for (FarmingAllotment allotment : allotments) allotment.update(thePlayer);
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        renderer.prepareBatchWithCamera(batch);
        batch.begin();

        renderer.render(delta, batch, this.players.values());
        for (FarmingAllotment allotment : allotments) allotment.render(batch, worldScale);

        thePlayer.render(batch, delta);
    }
}
