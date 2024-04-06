package me.vrekt.oasis.world.interior.boss;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instance;

/**
 * The 'boss', teff assigns objectives and more.
 */
public final class WrynnHouseInterior extends Instance {

    public WrynnHouseInterior(OasisGame game, OasisPlayer player, OasisWorld world, String name, String cursor, Rectangle bounds) {
        super(game, player, world, name, cursor, bounds);
        this.enterable = true;
    }

    @Override
    public float update(float d) {
        return super.update(d);
    }

    @Override
    public void enter(boolean setScreen) {
        super.enter(setScreen);
     //   game.getGui().getHud().showHint("Talk to Wrynn to get started with your adventure.", 10.0f);
    }


}