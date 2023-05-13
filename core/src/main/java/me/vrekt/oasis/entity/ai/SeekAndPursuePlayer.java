package me.vrekt.oasis.entity.ai;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.limiters.LinearLimiter;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.ai.agent.BasicSteeringAgent;
import me.vrekt.oasis.entity.ai.utilities.PlayerAsSteerable;
import me.vrekt.oasis.entity.ai.utilities.PlayerSteeringLocation;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.npc.EntityEnemy;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;

/**
 * Seek the player out then pursue them
 */
public final class SeekAndPursuePlayer extends BasicSteeringAgent {

    private final SteeringAcceleration<Vector2> seekOutput =
            new SteeringAcceleration<>(new Vector2());

    private final SteeringAcceleration<Vector2> pursueOutput =
            new SteeringAcceleration<>(new Vector2());

    private final EntityEnemy owner;
    private final OasisPlayerSP player;

    private final SteeringBehavior<Vector2> seek;
    private final SteeringBehavior<Vector2> pursue;

    private float nearPlayerDistance, closeToPlayerDistance;

    public SeekAndPursuePlayer(EntityEnemy owner, OasisPlayerSP player, PlayerSteeringLocation target, Vector2 position) {
        super(position);

        this.player = player;
        this.owner = owner;

        seek = new Seek<>(this, target);
        pursue = new Pursue<>(this, new PlayerAsSteerable(player));

        seek.setLimiter(new LinearLimiter(2.0f, 3.0f));
        pursue.setLimiter(new LinearLimiter(2.0f, 3.0f));

        this.nearPlayerDistance = 4.0f;
        this.closeToPlayerDistance = 1.0f;
    }

    @Override
    public float getBoundingRadius() {
        return owner.getScaledWidth() * owner.getScaledHeight();
    }

    @Override
    public void update(float delta) {

        if (!isCloseToPlayer()) {
            if (isNearPlayer()) {
                pursue.calculateSteering(pursueOutput);

                position.mulAdd(velocity, delta);
                velocity.mulAdd(pursueOutput.linear, delta).limit(getMaxLinearSpeed());
                owner.getBody().setLinearVelocity(velocity.x, velocity.y);
            } else {
                seek.calculateSteering(seekOutput);

                position.mulAdd(velocity, delta);
                velocity.mulAdd(seekOutput.linear, delta).limit(getMaxLinearSpeed());
                owner.getBody().setLinearVelocity(velocity.x, velocity.y);
            }
        } else {
            velocity.set(0.0f, 0.0f);
            owner.getBody().setLinearVelocity(0.0f, 0.0f);
        }

        updateDirection(velocity);
    }

    /**
     * Update owner direction
     *
     * @param direction the direction moving
     */
    private void updateDirection(Vector2 direction) {
        if (direction.isZero()) return;

        final float x = Math.abs(direction.x);
        final float y = Math.abs(direction.y);

        if (x > y) {
            if (x > 0.25f) {
                owner.setEntityRotation((direction.x > 0) ? EntityRotation.RIGHT : EntityRotation.LEFT);
            }
        } else {
            if (y > 0.25f) {
                owner.setEntityRotation((direction.y > 0) ? EntityRotation.UP : EntityRotation.DOWN);
            }
        }
    }

    /**
     * @return {@code  true} if {@code owner} is the near the player
     */
    private boolean isNearPlayer() {
        float distance = player.getInterpolatedPosition().dst2(owner.getPosition());
        return distance < nearPlayerDistance;
    }

    /**
     * @return {@code  true} if {@code owner} is close to the player
     */
    private boolean isCloseToPlayer() {
        float distance = player.getInterpolatedPosition().dst2(owner.getPosition());
        return distance < closeToPlayerDistance;
    }

}
