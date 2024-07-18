package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.player.AbstractPlayer;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;
import me.vrekt.oasis.world.GameWorld;

/**
 * Network player base
 * Adapted from LunarGdx
 */
public abstract class AbstractNetworkPlayer extends AbstractPlayer implements Viewable, Drawable {

    // TODO: NET-3 Fix snapping issues
    private static final float DE_SYNC_DISTANCE = 0.25f;

    protected final Vector2 incomingNetworkVelocity = new Vector2();

    public AbstractNetworkPlayer(GameWorld world) {
        this.worldIn = world;
    }

    /**
     * Update position from the network
     *
     * @param x     x
     * @param y     y
     * @param angle rotation
     */
    public void updateNetworkPosition(float x, float y, float angle) {
        incomingNetworkPosition.set(x, y);
        setAngle(angle);
    }

    /**
     * Update velocity of this player from the server
     *
     * @param x     the X
     * @param y     the Y
     * @param angle angle or rotation
     */
    public void updateNetworkVelocity(float x, float y, float angle) {
        incomingNetworkVelocity.set(x, y);
        setAngle(angle);
    }

    @Override
    public void update(float delta) {
        final boolean moving = !incomingNetworkVelocity.isZero(0.01f);
        if (moving) {
            body.setLinearVelocity(incomingNetworkVelocity);
        } else {
            if (body.getPosition().dst2(incomingNetworkPosition) > DE_SYNC_DISTANCE) {
                // lerp to final position
                velocity.set(body.getLinearVelocity());
                predicted.set(body.getPosition()).add(velocity.scl(delta));
                lerped.set(predicted).lerp(incomingNetworkPosition, 1.0f);
                trajectory.set(lerped).sub(body.getPosition()).scl(1f / (6.0f * delta));
                smoothed.set(trajectory).add(velocity).scl(1.0f);

                body.setLinearVelocity(smoothed);
            } else {
                body.setLinearVelocity(0, 0);
            }
        }
    }

    @Override
    public boolean isInView(Camera camera) {
        return true;
    }

}
