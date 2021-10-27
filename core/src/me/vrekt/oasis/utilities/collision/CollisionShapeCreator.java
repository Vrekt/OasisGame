package me.vrekt.oasis.utilities.collision;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Basic utility class to create collision shapes
 */
public final class CollisionShapeCreator {

    /**
     * Create a polygon shape
     * https://stackoverflow.com/questions/45805732/libgdx-tiled-map-box2d-collision-with-polygon-map-object
     *
     * @param object the object
     * @param scale  the scale
     * @return the shape
     */
    public static PolygonShape createPolygonShape(PolygonMapObject object, float scale) {
        final float[] vertices = object.getPolygon().getTransformedVertices();
        final float[] world = new float[vertices.length];

        for (int i = 0; i < world.length; i++) {
            world[i] = vertices[i] * scale;
        }

        final PolygonShape shape = new PolygonShape();
        shape.set(world);
        return shape;
    }

    /**
     * Create a polyline shape
     * https://stackoverflow.com/questions/45805732/libgdx-tiled-map-box2d-collision-with-polygon-map-object
     *
     * @param object the object
     * @param scale  the scale
     * @return the chained shape
     */
    public static ChainShape createPolylineShape(PolylineMapObject object, float scale) {
        final float[] vertices = object.getPolyline().getTransformedVertices();
        final Vector2[] world = new Vector2[vertices.length / 2];

        for (int i = 0; i < world.length; i++) {
            final Vector2 vec = new Vector2(vertices[i * 2] * scale, vertices[i * 2 + 1] * scale);
            world[i] = vec;
        }

        final ChainShape shape = new ChainShape();
        shape.createChain(world);
        return shape;
    }

    /**
     * Create a polygon shape
     *
     * @param object the object
     * @param scale  the scale
     * @param ds     if should scale
     * @return the shape
     */
    public static PolygonShape createPolygonShape(RectangleMapObject object, float scale, boolean ds) {
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
        return shape;
    }

}
