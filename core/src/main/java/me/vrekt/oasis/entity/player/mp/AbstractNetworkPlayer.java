package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.graphics.Camera;
import me.vrekt.oasis.entity.player.AbstractPlayer;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;
import me.vrekt.oasis.world.GameWorld;

/**
 * Network player base
 * Adapted from LunarGdx
 */
public abstract class AbstractNetworkPlayer extends AbstractPlayer implements Viewable, Drawable {

    public AbstractNetworkPlayer(GameWorld world) {
        this.worldIn = world;
    }

    @Override
    public GameWorld getWorldState() {
        return worldIn;
    }

    /**
     * Update position from the network
     *
     * @param x     x
     * @param y     y
     * @param angle rotation
     */
    public void updateNetworkPosition(float x, float y, float angle) {
        setPosition(x, y, false);
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
        getVelocity().set(x, y);
        setAngle(angle);
    }

    @Override
    public void update(float delta) {
        body.setLinearVelocity(getVelocity());

        final float difference = getPosition().dst2(body.getPosition());
        if (difference > 3.0f) {
            // de-sync, basic fix.
            body.setTransform(getPosition().x, getPosition().y, getAngle());
        }

        setPosition(body.getPosition().x, body.getPosition().y, false);
    }

    @Override
    public boolean isInView(Camera camera) {
        return true;
    }

}
