package me.vrekt.oasis.utility.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.utility.collision.CollisionShapeCreator;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;

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
        if (layer == null) {
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
                Logging.warn("TiledMapLoader", "Unknown map object in layer: " + layerName + " {" + object.getName() + "}");
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
    public static void loadMapActions(TiledMap worldMap, float worldScale, Vector2 worldSpawn, Rectangle worldExitArea) {
        loadMapObjects(worldMap, worldScale, "Actions", (object, rectangle) -> {
            if (object.getName().equalsIgnoreCase("WorldSpawn")
                    || object.getName().equalsIgnoreCase("Spawn")) {
                Logging.info("TiledMapLoader", "Found WorldSpawn @ " + rectangle.x + ":" + rectangle.y);
                worldSpawn.set(rectangle.x, rectangle.y);
            } else if (object.getName().equalsIgnoreCase("Exit")) {
                Logging.info("TiledMapLoader", "Found WorldExitArea @ " + rectangle.x + ":" + rectangle.y);
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
    public static void loadMapCollision(TiledMap worldMap, float worldScale, World world, OasisWorld worldIn) {
        final MapLayer layer = worldMap.getLayers().get("Collision");
        if (layer == null) {
            return;
        }

        int loaded = 0;
        for (MapObject object : layer.getObjects()) {
            CollisionShapeCreator.createCollisionInWorld(object, worldScale, world);
            loaded++;
        }

        Logging.info("TiledMapLoader", "Loaded a total of " + loaded + " collision objects.");
    }

}
