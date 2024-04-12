package me.vrekt.oasis.entity.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lunar.shared.entity.player.mp.AbstractLunarEntityNetworkPlayer;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;

/**
 * A multiplayer player entity
 */
public abstract class OasisNetworkEntityPlayer extends AbstractLunarEntityNetworkPlayer implements Viewable, Drawable {

    private static final float INTERACTION_DISTANCE = 4.0f;

    public OasisNetworkEntityPlayer(boolean initializeComponents) {
        super(initializeComponents);
    }

    @Override
    public boolean isInView(Camera camera) {
        return camera.frustum.pointInFrustum(getX(), getY(), 0.0f);
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

    protected void draw(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, getInterpolatedPosition().x, getInterpolatedPosition().y, region.getRegionWidth() * getWorldScale(), region.getRegionHeight() * getWorldScale());
    }

}
