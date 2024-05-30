package me.vrekt.oasis.world.effects;

import me.vrekt.oasis.combat.DamageType;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.player.sp.PlayerSP;

/**
 * Area effect types
 */
public enum EffectType {

    POISON {
        @Override
        public void applyEffect(Entity entity, float strength) {
            entity.damage(1.6f * strength);
        }

        @Override
        public void applyToPlayer(PlayerSP player, float strength) {
            player.hurt(1.6f * strength, DamageType.POISON);
        }
    };

    /**
     * Apply this effect to an entity
     *
     * @param entity   the entity
     * @param strength the strength
     */
    public abstract void applyEffect(Entity entity, float strength);

    /**
     * Apply this effect to the player
     *
     * @param player   the player
     * @param strength strength
     */
    public void applyToPlayer(PlayerSP player, float strength) {

    }

}
