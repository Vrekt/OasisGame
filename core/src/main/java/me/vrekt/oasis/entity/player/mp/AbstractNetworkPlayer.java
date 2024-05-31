package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.entity.player.AbstractPlayer;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;

/**
 * Network player base
 * Adapted from LunarGdx
 */
public abstract class AbstractNetworkPlayer extends AbstractPlayer implements Viewable, Drawable {

    private static final float INTERACTION_DISTANCE = 4.0f;

    protected boolean interpolatePosition, doPositionInterpolation;
    protected float interpolateToX, interpolateToY;

    // snap player to the correct position if distance >= interpolateDesyncDistance
    protected boolean snapToPositionIfDesync;
    // Default distance that player will interpolate to if they are too far away from server position.
    protected float interpolateDesyncDistance = 3.0f;

    /**
     * Enable or disable interpolating of a network players position
     *
     * @param interpolatePosition the state
     */
    public void setInterpolatePosition(boolean interpolatePosition) {
        this.interpolatePosition = interpolatePosition;
    }

    /**
     * If {@code true} once a certain threshold is met of de-sync between positions
     * This player will be snapped to back to its intended position
     *
     * @param snap the state
     */
    public void setSnapToPositionIfDesynced(boolean snap) {
        this.snapToPositionIfDesync = snap;
    }

    /**
     * Set the distance required for de-sync between positions for the player to
     * snap-back to its intended or server position
     * Only required if {@code setSnapToPositionIfDesynced} if {@code true}
     * Usual values of this would be between 1.0 (harsh) and 3.0 (more lenient)
     *
     * @param distance the distance
     */
    public void setDesyncDistanceToInterpolate(float distance) {
        this.interpolateDesyncDistance = distance;
    }

    /**
     * Update position of this player from the server
     *
     * @param x     the X
     * @param y     the Y
     * @param angle angle or rotation
     */
    public void updatePositionFromNetwork(float x, float y, float angle) {
        final float dst = getPosition().dst2(x, y);

        // interpolate to position if too far away (de sync)
        if (dst >= interpolateDesyncDistance) {
            if (snapToPositionIfDesync) {
                setAngle(angle);
                setPosition(x, y, true);
            } else {
                doPositionInterpolation = true;
                interpolateToX = x;
                interpolateToY = y;
            }
        }
        setAngle(angle);
    }

    /**
     * Update velocity of this player from the server
     *
     * @param x     the X
     * @param y     the Y
     * @param angle angle or rotation
     */
    public void updateVelocityFromNetwork(float x, float y, float angle) {
        getVelocity().set(x, y);
        setAngle(angle);
    }

    @Override
    public void update(float delta) {
        if (interpolatePosition && doPositionInterpolation) {
            final Vector2 interpolated = getInterpolatedPosition();

            interpolated.x = Interpolation.linear.apply(getPosition().x, interpolateToX, 1.0f);
            interpolated.y = Interpolation.linear.apply(getPosition().y, interpolateToY, 1.0f);

            // update body position.
            final float diffX = getPosition().x - interpolateToX;
            final float diffY = getPosition().y - interpolateToY;

            body.setLinearVelocity(diffX * 1.0f, diffY * 1.0f);
            setPosition(body.getPosition().x, body.getPosition().y, false);

            doPositionInterpolation = false;
            return;
        }

        // update velocity and set player position.
        body.setLinearVelocity(getVelocity());
        setPosition(body.getPosition().x, body.getPosition().y, false);
    }

    @Override
    public boolean isInView(Camera camera) {
        return true;
    }

    /**
     * Check if this entity was clicked on
     *
     * @param clicked the vector3 click
     * @return {@code  true} if so
     */
    public boolean isMouseInEntityBounds(Vector3 clicked) {
        return clicked.x > getX() && clicked.x < (getX() + getScaledWidth()) && clicked.y > getY() && clicked.y < (getY() + getScaledHeight());
    }

    public boolean isWithinInteractionDistance(Vector2 other) {
        return other.dst2(getPosition()) <= INTERACTION_DISTANCE;
    }

    protected void draw(SpriteBatch batch, TextureRegion region, float width, float height) {
        batch.draw(region, getInterpolatedPosition().x, getInterpolatedPosition().y, width, height);
    }

}
