package me.vrekt.oasis.entity.npc;

import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.ino.EntityIno;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * All NPC entities within the game
 */
public enum EntityNPCType {

    INO {
        @Override
        public <T extends EntityInteractable> T create(float x, float y, OasisGame game, AbstractWorld worldIn) {
            return (T) new EntityIno(x, y, game, worldIn);
        }
    };

    public abstract <T extends EntityInteractable> T create(float x, float y, OasisGame game, AbstractWorld worldIn);

}
