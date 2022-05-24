package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.tutorial.MaviaTutorial;
import me.vrekt.oasis.world.OasisWorld;

/**
 * All NPC entities within the game
 */
public enum EntityNPCType {

    MAVIA {
        @Override
        public <T extends EntityInteractable> T create(Vector2 position, OasisGame game, OasisWorld world) {
            return (T) new MaviaTutorial("Mavia", position, game.getPlayer(), world, game);
        }
    };

    public abstract <T extends EntityInteractable> T create(Vector2 position, OasisGame game, OasisWorld world);

}
