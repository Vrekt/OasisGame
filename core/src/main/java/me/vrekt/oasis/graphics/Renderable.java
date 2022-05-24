package me.vrekt.oasis.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;

/**
 * An interface describing the object is renderable if {@code  isInView}
 */
public interface Renderable {

    /**
     * Check if the object or entity is in view
     *
     * @param camera the camera
     * @return {@code true} if so
     */
    boolean isInView(Camera camera);

    void render(SpriteBatch batch, float delta);

    /**
     * Test if a point is in view, extended a little above LibGDX default.
     *
     * @param view    vec3 view
     * @param x       pos x
     * @param y       pos y
     * @param frustum camera
     * @return {@code  true} if so
     */
    static boolean isInViewExtended(Vector3 view, float x, float y, Frustum frustum) {
        view.set(x, y, 0.0f);
        for (int i = 0; i < frustum.planes.length; i++) {
            Plane.PlaneSide result = testExtendedPoint(frustum.planes[i].normal, view, frustum.planes[i].d);
            if (result == Plane.PlaneSide.Back) return false;
        }
        return true;
    }

    static Plane.PlaneSide testExtendedPoint(Vector3 normal, Vector3 point, float d) {
        float dist = normal.dot(point) + d;

        if (dist > -10 && dist <= 0)
            return Plane.PlaneSide.OnPlane;
        else if (dist < 0)
            return Plane.PlaneSide.Back;
        else
            return Plane.PlaneSide.Front;
    }

}
