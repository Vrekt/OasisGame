package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;

public abstract class EntityDamageable extends EntityInteractable {

    public EntityDamageable(String name, Vector2 position, OasisPlayerSP player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(name, position, player, worldIn, game, type);
    }

    @Override
    public void update(float v) {
        super.update(v);
    }

    public void damage(float tick, float amount, boolean isCritical) {

    }

}
