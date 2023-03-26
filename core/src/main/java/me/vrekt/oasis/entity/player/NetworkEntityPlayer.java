package me.vrekt.oasis.entity.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lunar.shared.player.mp.LunarNetworkEntityPlayer;
import me.vrekt.oasis.graphics.Renderable;

/**
 * A multiplayer player entity
 */
public abstract class NetworkEntityPlayer extends LunarNetworkEntityPlayer implements Renderable {

    protected boolean inView;

    public NetworkEntityPlayer(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public NetworkEntityPlayer(boolean initializeComponents) {
        super(initializeComponents);
    }

    @Override
    public boolean isInView(Camera camera) {
        return inView = camera.frustum.pointInFrustum(getX(), getY(), 0.0f);
    }

    protected void draw(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, getInterpolated().x, getInterpolated().y, region.getRegionWidth() * getScaling(), region.getRegionHeight() * getScaling());
    }

}
