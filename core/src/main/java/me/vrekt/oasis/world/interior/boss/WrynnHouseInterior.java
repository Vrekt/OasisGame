package me.vrekt.oasis.world.interior.boss;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.wrynn.WrynnEntity;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instance;

import java.util.Random;

/**
 * The 'boss', teff assigns objectives and more.
 */
public final class WrynnHouseInterior extends Instance {

    private WrynnEntity wrynn;
    private final Random random;

    public WrynnHouseInterior(OasisGame game, OasisPlayer player, OasisWorld world, String name, Cursor cursor, Rectangle bounds) {
        super(game, player, world, name, cursor, bounds);
        this.enterable = true;
        random = new Random();
    }

    @Override
    public float update(float d) {
        super.update(d);
        if (wrynn.wantsNextPath()) {
            GameLogging.info(this, "Set next wrynn path");
            wrynn.setNextPathPoint(paths.get(random.nextInt(paths.size())));
        }
        return d;
    }

    @Override
    public void enter(boolean setScreen) {
        super.enter(setScreen);
        wrynn = (WrynnEntity) getEntityByType(EntityNPCType.WRYNN);


        //   game.getGui().getHud().showHint("Talk to Wrynn to get started with your adventure.", 10.0f);
    }


}