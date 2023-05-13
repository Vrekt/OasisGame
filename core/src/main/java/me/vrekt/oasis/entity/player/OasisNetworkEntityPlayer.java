package me.vrekt.oasis.entity.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lunar.shared.entity.player.mp.AbstractLunarEntityNetworkPlayer;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;

/**
 * A multiplayer player entity
 */
public abstract class OasisNetworkEntityPlayer extends AbstractLunarEntityNetworkPlayer implements Viewable, Drawable {

    public OasisNetworkEntityPlayer(boolean initializeComponents) {
        super(initializeComponents);
    }


    @Override
    public boolean isInView(Camera camera) {
        return camera.frustum.pointInFrustum(getX(), getY(), 0.0f);
    }

    protected void draw(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, getInterpolatedPosition().x, getInterpolatedPosition().y, region.getRegionWidth() * getWorldScale(), region.getRegionHeight() * getWorldScale());
    }

}
