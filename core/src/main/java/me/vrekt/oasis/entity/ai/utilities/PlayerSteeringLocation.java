package me.vrekt.oasis.entity.ai.utilities;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;

/**
 * Converts a basic Vector2 into a {@link  com.badlogic.gdx.ai.utils.Location}
 */
public final class PlayerSteeringLocation implements Location<Vector2> {

    private final OasisPlayerSP player;

    public PlayerSteeringLocation(OasisPlayerSP player) {
        this.player = player;
    }

    @Override
    public Vector2 getPosition() {
        return player.getInterpolated();
    }

    @Override
    public float getOrientation() {
        return player.getPlayerRotation().ordinal();
    }

    @Override
    public void setOrientation(float orientation) {

    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return 0.0f;
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        System.err.println("NEED");
        return Vector2.Zero;
    }

    @Override
    public Location<Vector2> newLocation() {
        System.err.println("NEED2");
        return null;
    }
}
