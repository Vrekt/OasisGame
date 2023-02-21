package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.instance.Instance;

/**
 * Basic implementation of {@link Instance}.
 */
public class Instanced extends Instance implements Pool.Poolable {

    public static <T extends Instanced> T get(Class<T> type) {
        return Pools.obtain(type);
    }

    private final Vector3 cursorInInstance = new Vector3();
    private final Vector2 worldExitSpawn = new Vector2();
    private final Rectangle bounds;
    private boolean enterable = true, cursorChanged, dispose;
    private float distance = 2.5f;
    private final String cursor;

    public Instanced(OasisWorld world, String name, String cursor, Rectangle bounds) {
        super(world.getLocalPlayer(), world.getWorld(), world, name);
        this.cursor = cursor;
        this.bounds = bounds;
    }

    public String getCursor() {
        return cursor;
    }

    public boolean clickedOn(Vector3 vector3) {
        return bounds.contains(vector3.x, vector3.y);
    }

    public boolean isMouseWithinBounds(Vector3 vector3) {
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


    @Override
    public void enter() {
        worldExitSpawn.set(player.getPosition());
        super.enter();
    }

    /**
     * Destroy this instance and reset
     */
    private void destroy() {
        this.reset();
    }

    @Override
    public float update(float delta) {
        super.update(delta);

        // set our mouse within world space
        GameManager.getRenderer().getCamera().unproject(cursorInInstance.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        // check if mouse is over exit
        if (exit.contains(cursorInInstance.x, cursorInInstance.y) && !cursorChanged) {
            GameManager.setCursorInGame(getCursor());
            this.cursorChanged = true;
        } else if (!exit.contains(cursorInInstance.x, cursorInInstance.y) && cursorChanged) {
            GameManager.resetCursor();
            this.cursorChanged = false;
        }

        return delta;
    }

    /**
     * Left mouse button was clicked
     */
    public void processLeftClickDown() {
        // check if player is close enough to exit
        if (cursorChanged // indicates mouse is over exit
                && player.getPosition().dst(exit.x, exit.y) <= 4.5f) {
            Logging.info(this, "Exiting instance: " + instanceName);
            destroy();

            player.removeEntityInWorld(this);
            worldIn.enterWorldFromInstance(worldExitSpawn);
            worldExitSpawn.set(0, 0);
        }
    }

    @Override
    public void reset() {
        // TODO: Maybe use pools.
    }
}
