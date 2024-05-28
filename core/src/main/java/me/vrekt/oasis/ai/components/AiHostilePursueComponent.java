package me.vrekt.oasis.ai.components;

import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.ai.PlayerSteerableTarget;
import me.vrekt.oasis.ai.behaviour.ApplyBehavior;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.player.sp.PlayerSP;

/**
 * Default seeking behaviour for a hostile entity
 */
public final class AiHostilePursueComponent extends AiComponent {

    private final Pursue<Vector2> pursue;
    private final PlayerSP player;
    private final PlayerSteerableTarget playerTarget;

    private float hostileAttackRange;

    public AiHostilePursueComponent(Entity entity, PlayerSP player) {
        super(entity, AiComponentType.PURSUE, ApplyBehavior.VELOCITY_ONLY);
        this.player = player;

        playerTarget = new PlayerSteerableTarget(player);
        pursue = new Pursue<>(steering, playerTarget, 0.0f);
        steering.setBehavior(pursue);
    }

    /**
     * Set the range where the entity becomes hostile and can attack
     *
     * @param range range
     */
    public void setHostileAttackRange(float range) {
        this.hostileAttackRange = range;
    }

    /**
     * @return if current hostile, basically within attack range
     */
    public boolean isWithinAttackRange() {
        return entity.getPosition().dst2(player.getPosition()) <= hostileAttackRange;
    }

    /**
     * @return if we are within the players bounds
     */
    public boolean isWithinPlayer() {
        return entity.bb().contains(player.getPosition()) || entity.getPosition().dst2(player.getPosition()) <= 0.5f;
    }

}
