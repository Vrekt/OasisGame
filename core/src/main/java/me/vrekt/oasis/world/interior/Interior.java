package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.instance.Instanced;

/**
 * Represents an interior within
 */
public final class Interior extends Instanced {

    private final Rectangle bounds;
    private boolean enterable = true;
    private float distance = 2.5f;

    public Interior(OasisWorld world, String name, Rectangle bounds) {
        super(world.getLocalPlayer(), world.getWorld(), world, name);
        this.bounds = bounds;
    }

    public boolean clickedOn(Vector3 vector3) {
        return bounds.contains(vector3.x, vector3.y);
    }

    public boolean enterable() {
        return enterable;
    }

    public void setEnterable(boolean enterable) {
        this.enterable = enterable;
    }

    public boolean isWithinEnteringDistance(Vector2 position) {
        return position.dst2(bounds.x, bounds.y) <= distance;
    }

}
