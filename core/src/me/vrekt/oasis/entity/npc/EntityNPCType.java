package me.vrekt.oasis.entity.npc;

import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.mavia.EntityMavia;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * All NPC entities within the game
 */
public enum EntityNPCType {

    MAVIA {
        @Override
        public <T extends EntityInteractable> T create(float x, float y, OasisGame game, AbstractWorld worldIn) {
            return (T) new EntityMavia(x, y, game, worldIn);
        }
    };

    public abstract <T extends EntityInteractable> T create(float x, float y, OasisGame game, AbstractWorld worldIn);

}
