package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.world.OasisWorld;

public abstract class EntityDamageable extends EntityInteractable {

    public EntityDamageable(String name, Vector2 position, OasisPlayer player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(name, position, player, worldIn, game, type);
    }

    @Override
    public void update(float v) {
        super.update(v);
    }

    /**
     * Damage this entity
     *
     * @param tick       the current world tick
     * @param amount     the amount of damage
     * @param knockback  the knockback multiplier
     * @param isCritical if this damage was a critical
     */
    public void damage(float tick, float amount, float knockback, boolean isCritical) {

    }

}
