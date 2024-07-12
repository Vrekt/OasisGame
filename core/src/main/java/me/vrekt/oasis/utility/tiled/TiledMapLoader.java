package me.vrekt.oasis.utility.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.utility.collision.CollisionShapeCreator;
import me.vrekt.oasis.utility.logging.GameLogging;

import java.util.function.BiConsumer;

/**
 * Basic utility class for loading objects and layers from a tiled map.
 */
public final class TiledMapLoader {

    /**
     * General functions for collecting rectangle objects and handling them
     *
     * @param worldMap   map
     * @param worldScale scale
     * @param layerName  layer to get
     * @param handler    handler
     * @return the result (if the layer was found)
     */
    public static boolean loadMapObjects(TiledMap worldMap, float worldScale, String layerName, BiConsumer<MapObject, Rectangle> handler) {
        final MapLayer layer = worldMap.getLayers().get(layerName);
        if (layer == null) return false;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                final Rectangle rectangle = new Rectangle(((RectangleMapObject) object).getRectangle());
                rectangle.x = rectangle.x * worldScale;
                rectangle.y = rectangle.y * worldScale;
                rectangle.width = rectangle.width * worldScale;
                rectangle.height = rectangle.height * worldScale;
                handler.accept(object, rectangle);
            } else {
                GameLogging.warn("TiledMapLoader", "Unknown map object in layer: " + layerName + " {" + object.getClass() + "}");
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
    public static void loadMapActions(TiledMap worldMap, float worldScale, Vector2 worldSpawn, Rectangle
            worldExitArea) {
        loadMapObjects(worldMap, worldScale, "Actions", (object, rectangle) -> {
            if (object.getName().equalsIgnoreCase("WorldSpawn")
                    || object.getName().equalsIgnoreCase("Spawn")) {
                GameLogging.info("TiledMapLoader", "Found WorldSpawn %s", rectangle);
                worldSpawn.set(rectangle.x, rectangle.y);
            } else if (object.getName().equalsIgnoreCase("Exit") && worldExitArea != null) {
                GameLogging.info("TiledMapLoader", "Found WorldExitArea %s ", rectangle);
                worldExitArea.set(rectangle);
            }
        });
    }

    /**
     * Load map collision
     *
     * @param worldMap   the map
     * @param worldScale the scale
     */
    public static void loadMapCollision(TiledMap worldMap, float worldScale, World world) {
        final MapLayer layer = worldMap.getLayers().get("Collision");
        if (layer == null) {
            return;
        }

        int loaded = 0;
        for (MapObject object : layer.getObjects()) {
            CollisionShapeCreator.createCollisionInWorld(object, worldScale, world);
            loaded++;
        }

        GameLogging.info("TiledMapLoader", "Loaded a total of %d collision objects.", loaded);
    }

    /**
     * Load poly path
     *
     * @param layer      the layer
     * @param worldScale scale
     * @return the points or {@code null} if none
     */
    public static Vector2[] loadPolyPath(MapLayer layer, float worldScale) {
        for (MapObject object : layer.getObjects()) {
            if (object instanceof PolylineMapObject poly) {
                return CollisionShapeCreator.createPathPoints(poly, worldScale);
            }
        }
        return null;
    }

    public static boolean ofBoolean(MapObject object, String key) {
        return object.getProperties().get(key, false, Boolean.class);
    }

    public static String ofString(MapObject object, String key) {
        return object.getProperties().get(key, null, String.class);
    }

    public static float ofFloat(MapObject object, String key) {
        return object.getProperties().get(key, 1.0f, Float.class);
    }

    public static float ofFloat(MapObject object, String key, float defaultValue) {
        return object.getProperties().get(key, defaultValue, Float.class);
    }

    public static int ofInt(MapObject object, String key, int defaultValue) {
        return object.getProperties().get(key, defaultValue, Integer.class);
    }

}
