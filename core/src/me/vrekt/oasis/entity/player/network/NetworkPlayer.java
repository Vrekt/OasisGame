package me.vrekt.oasis.entity.player.network;

import com.badlogic.gdx.graphics.Camera;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.impl.LunarNetworkPlayer;
import me.vrekt.oasis.utilities.render.Viewable;

/**
 * A MP entity player
 */
public final class NetworkPlayer extends LunarNetworkPlayer implements Viewable {

    public NetworkPlayer(int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId, playerScale, playerWidth, playerHeight, rotation);

        setInterpolateDesyncDistance(1.0f);
        setSnapToPositionIfDesync(true);
    }

    @Override
    public void setPosition(float x, float y) {
        body.setTransform(x, y, 0.0f);
        super.setPosition(x, y);
    }

    @Override
    public boolean isInView(Camera camera) {
        return camera.frustum.pointInFrustum(position.x, position.y, 0.0f);
    }
}
