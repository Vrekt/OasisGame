package me.vrekt.oasis.world.dungeon;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instance;

/**
 * Tutorial island dungeon
 */
public final class LairOfHopelessWidow extends Instance {

    public LairOfHopelessWidow(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
        super(game, player, world, name, cursor, bounds);
    }


    @Override
    public void enter() {
        super.enter();
        GameManager.getGui().getHud().showDungeonIntroduction("Lair Of The Hopeless Widow");
    }
}
