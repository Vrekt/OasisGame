package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.instance.OasisInstance;

/**
 * Basic implementation of {@link OasisInstance}.
 */
public class Instanced extends OasisInstance {

    protected final Vector3 cursorInInstance = new Vector3();
    protected final Vector2 worldExitSpawn = new Vector2();
    protected final Rectangle bounds;
    protected boolean enterable = true, cursorChanged;
    protected float distance = 2.5f;
    protected final String cursor, instanceName;

    public Instanced(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
        super(game, player, new World(Vector2.Y, false), world, name);
        this.instanceName = name;
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
        Logging.info(instanceName, "Entering instance: " + instanceName);
        worldExitSpawn.set(player.getPosition());
        super.enter();
    }

    /**
     * Unload
     * TODO
     */
    public void unload() {
        Logging.info(instanceName, "Unloading instance from memory");
        final Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        for (Body body : new Array.ArrayIterator<>(bodies)) {
            world.destroyBody(body);
        }
        this.dispose();
        isWorldLoaded = false;
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
            Logging.info(instanceName, "Exiting instance: " + instanceName);
            player.setPosition(worldExitSpawn.x - 0.5f, worldExitSpawn.y - 1.0f, true);
            worldIn.enterWorld(true);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        processLeftClickDown();
        if (interactWithEntity()) return true;
        if (interactWithObject()) return true;
        return false;
    }

}
