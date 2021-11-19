package me.vrekt.oasis.utilities.collision;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.utilities.logging.Logging;

/**
 * Basic utility class to create collision shapes
 */
public final class CollisionShapeCreator {

    public static final BodyDef STATIC_BODY = new BodyDef();

    static {
        STATIC_BODY.type = BodyDef.BodyType.StaticBody;
    }

    /**
     * Create a collision object within a box2d world.
     *
     * @param object object
     * @param scale  scale
     * @param bw     box2d world
     */
    public static void createCollisionInWorld(MapObject object, float scale, World bw) {
        if (object instanceof PolylineMapObject) {
            createPolylineShapeInWorld((PolylineMapObject) object, scale, bw);
        } else if (object instanceof PolygonMapObject) {
            createPolygonShapeInWorld((PolygonMapObject) object, scale, bw);
        } else if (object instanceof RectangleMapObject) {
            createPolygonShapeInWorld((RectangleMapObject) object, scale, true, bw);
        } else {
            Logging.warn("CollisionShapeCreator", "Unknown map object collision type: " + object.getName() + ":" + object.getClass());
        }
    }

    /**
     * Create a polygon shape and add it to the world using the {@code STATIC_BODY}
     * https://stackoverflow.com/questions/45805732/libgdx-tiled-map-box2d-collision-with-polygon-map-object
     *
     * @param object the object
     * @param scale  the scale
     * @param bw     the box2d world.
     */
    public static void createPolygonShapeInWorld(PolygonMapObject object, float scale, World bw) {
        final float[] vertices = object.getPolygon().getTransformedVertices();
        final float[] world = new float[vertices.length];

        for (int i = 0; i < world.length; i++) {
            world[i] = vertices[i] * scale;
        }

        final PolygonShape shape = new PolygonShape();
        shape.set(world);

        bw.createBody(STATIC_BODY).createFixture(shape, 1.0f);
        shape.dispose();
    }

    /**
     * Create a polyline shape and add it to the world using the {@code STATIC_BODY}
     * https://stackoverflow.com/questions/45805732/libgdx-tiled-map-box2d-collision-with-polygon-map-object
     *
     * @param object the object
     * @param scale  the scale
     * @param bw     the box2d world.
     */
    public static void createPolylineShapeInWorld(PolylineMapObject object, float scale, World bw) {
        final float[] vertices = object.getPolyline().getTransformedVertices();
        final Vector2[] world = new Vector2[vertices.length / 2];

        for (int i = 0; i < world.length; i++) {
            final Vector2 vec = new Vector2(vertices[i * 2] * scale, vertices[i * 2 + 1] * scale);
            world[i] = vec;
        }

        final ChainShape shape = new ChainShape();
        shape.createChain(world);

        bw.createBody(STATIC_BODY).createFixture(shape, 1.0f);
        shape.dispose();
    }

    /**
     * Create a polygon shape and add it to the world using the {@code STATIC_BODY}
     *
     * @param object the object
     * @param scale  the scale
     * @param ds     if you should scale
     * @param bw     the box2d world.
     */
    public static void createPolygonShapeInWorld(RectangleMapObject object, float scale, boolean ds, World bw) {
        final PolygonShape shape = new PolygonShape();
        final Rectangle rectangle = object.getRectangle();

        if (ds) {
            rectangle.x *= scale;
            rectangle.y *= scale;
            rectangle.width *= scale;
            rectangle.height *= scale;
        }

        final Vector2 center = new Vector2(rectangle.x + rectangle.width / 2f, rectangle.y + rectangle.height / 2f);
        shape.setAsBox(rectangle.width / 2, rectangle.height / 2, center, 0.0f);

        bw.createBody(STATIC_BODY).createFixture(shape, 1.0f);
        shape.dispose();
    }

}
