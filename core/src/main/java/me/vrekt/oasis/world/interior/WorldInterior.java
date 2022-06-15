package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.instance.SeparateWorldInstance;

public class WorldInterior {

    private final Rectangle bounds;
    private boolean enterable = true;

    // the instance this interior belongs to
    private SeparateWorldInstance worldInstance;

    public WorldInterior(Rectangle bounds) {
        this.bounds = bounds;
    }

    /**
     * Enter this interior
     *
     * @param world the parent worldIn
     * @return {@code  true} if successful
     */
    public boolean create(OasisWorld world) {
        world.getLocalPlayer().setInInterior(true);
        world.getLocalPlayer().setInterior(this);
        this.worldInstance = new SeparateWorldInstance(world.getGame(), world.getLocalPlayer(), Asset.HOUSE1);
        return true;
    }

    public SeparateWorldInstance getWorldInstance() {
        return worldInstance;
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
}
