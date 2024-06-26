package me.vrekt.oasis.utility.collision;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Basic utility class to create collision shapes
 */
public final class CollisionShapeCreator {

    public static final BodyDef STATIC_BODY = new BodyDef();

    public static Array<Rectangle> rectangles = new Array<>();

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
        switch (object) {
            case PolylineMapObject polylineMapObject -> createPolylineShapeInWorld(polylineMapObject, scale, bw);
            case PolygonMapObject polygonMapObject -> createPolygonShapeInWorld(polygonMapObject, scale, bw);
            case RectangleMapObject rectangleMapObject -> createPolygonShapeInWorld(rectangleMapObject, scale, true, bw);
            default -> throw new IllegalStateException("Unexpected value: " + object);
        }
    }

    /**
     * Create a polygon shape and add it to the world using the {@code STATIC_BODY}
     * <a href="https://stackoverflow.com/questions/45805732/libgdx-tiled-map-box2d-collision-with-polygon-map-object">...</a>
     *
     * @param object the object
     * @param scale  the scale
     * @param bw     the box2d world.
     */
    public static Body createPolygonShapeInWorld(PolygonMapObject object, float scale, World bw) {
        final float[] vertices = object.getPolygon().getTransformedVertices();
        final float[] world = new float[vertices.length];

        for (int i = 0; i < world.length; i++) {
            world[i] = vertices[i] * scale;
        }

        final PolygonShape shape = new PolygonShape();
        shape.set(world);

        final Body body = bw.createBody(STATIC_BODY);
        body.createFixture(shape, 1.0f);
        shape.dispose();
        return body;
    }

    /**
     * Create a polyline shape and add it to the world using the {@code STATIC_BODY}
     * <a href="https://stackoverflow.com/questions/45805732/libgdx-tiled-map-box2d-collision-with-polygon-map-object">...</a>
     *
     * @param object the object
     * @param scale  the scale
     * @param bw     the box2d world.
     */
    public static Body createPolylineShapeInWorld(PolylineMapObject object, float scale, World bw) {
        final float[] vertices = object.getPolyline().getTransformedVertices();
        final Vector2[] world = new Vector2[vertices.length / 2];

        for (int i = 0; i < world.length; i++) {
            final Vector2 vec = new Vector2(vertices[i * 2] * scale, vertices[i * 2 + 1] * scale);
            world[i] = vec;
        }

        final ChainShape shape = new ChainShape();
        shape.createChain(world);

        final Body body = bw.createBody(STATIC_BODY);
        body.createFixture(shape, 1.0f);
        shape.dispose();
        return body;
    }

    /**
     * Create individual path points along a poly-line map object
     *
     * @param object object
     * @param scale  scale
     * @return the points
     */
    public static Vector2[] createPathPoints(PolylineMapObject object, float scale) {
        final float[] vertices = object.getPolyline().getTransformedVertices();
        final Vector2[] world = new Vector2[vertices.length / 2];

        for (int i = 0; i < world.length; i++) {
            final Vector2 vec = new Vector2(vertices[i * 2] * scale, vertices[i * 2 + 1] * scale);
            world[i] = vec;
        }

        return world;
    }

    /**
     * Create a polygon shape and add it to the world using the {@code STATIC_BODY}
     *
     * @param object the object
     * @param scale  the scale
     * @param ds     if you should scale
     * @param bw     the box2d world.
     */
    public static Body createPolygonShapeInWorld(RectangleMapObject object, float scale, boolean ds, World bw) {
        final PolygonShape shape = new PolygonShape();
        final Rectangle rectangle = new Rectangle(object.getRectangle());

        if (ds) {
            rectangle.x *= scale;
            rectangle.y *= scale;
            rectangle.width *= scale;
            rectangle.height *= scale;
        }

        rectangles.add(rectangle);

        final Vector2 center = new Vector2(rectangle.x + rectangle.width / 2f, rectangle.y + rectangle.height / 2f);
        shape.setAsBox(rectangle.width / 2, rectangle.height / 2, center, 0.0f);

        final Body body = bw.createBody(new BodyDef());

        body.createFixture(shape, 1.0f);
        shape.dispose();
        return body;
    }

    public static Body createPolygonShapeInWorld(float x, float y, float width, float height, float scale, boolean ds, World bw) {
        final PolygonShape shape = new PolygonShape();

        if (ds) {
            width *= scale;
            height *= scale;
        }

        final Vector2 center = new Vector2(x + width / 2f, y + height / 2f);
        shape.setAsBox(width / 2, height / 2, center, 0.0f);

        final Body body = bw.createBody(STATIC_BODY);
        body.createFixture(shape, 1.0f);
        shape.dispose();
        return body;
    }

}
