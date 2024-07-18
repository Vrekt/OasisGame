package me.vrekt.oasis.ai;

import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import me.vrekt.oasis.ai.utility.AiVectorUtility;
import me.vrekt.oasis.ai.utility.SimpleVectorLocation;
import me.vrekt.oasis.entity.player.sp.PlayerSP;

/**
 * A steerable that represents the current player
 * No behaviour, obviously.
 */
public final class PlayerSteerableTarget extends SteerableAdapter<Vector2> {

    private final PlayerSP player;
    private final Body body;

    public PlayerSteerableTarget(PlayerSP player) {
        this.player = player;
        this.body = player.body();
    }

    @Override
    public Vector2 getLinearVelocity() {
        return player.getVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public Vector2 getPosition() {
        return player.getPosition();
    }

    @Override
    public float getOrientation() {
        return player.getAngle();
    }

    @Override
    public float vectorToAngle(Vector2 vector2) {
        return AiVectorUtility.vectorToAngle(vector2);
    }

    @Override
    public Vector2 angleToVector(Vector2 vector2, float angle) {
        return AiVectorUtility.angleToVector(vector2, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return new SimpleVectorLocation().set(player.getPosition());
    }
}
