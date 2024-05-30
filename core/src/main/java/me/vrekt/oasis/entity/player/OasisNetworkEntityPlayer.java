package me.vrekt.oasis.entity.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.LunarEntityNetworkPlayer;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;

/**
 * A multiplayer player entity
 */
public abstract class OasisNetworkEntityPlayer extends LunarEntityNetworkPlayer implements Viewable, Drawable {

    private static final float INTERACTION_DISTANCE = 4.0f;

    public OasisNetworkEntityPlayer(boolean initializeComponents) {
        super(initializeComponents);
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

    @Override
    public void spawnInWorld(LunarWorld world, Vector2 position) {
        super.spawnInWorld(world, position);
    }
}
