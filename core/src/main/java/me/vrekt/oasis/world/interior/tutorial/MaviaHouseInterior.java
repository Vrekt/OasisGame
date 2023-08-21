package me.vrekt.oasis.world.interior.tutorial;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.weapons.EnchantedVioletItem;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instance;
import me.vrekt.oasis.world.obj.interaction.chest.ChestInventoryInteraction;

/**
 * Mavia's tutorial house on Tutorial Island
 */
public final class MaviaHouseInterior extends Instance {

    // tutorial chest runtime ID
    public static final int TUTORIAL_CHEST_RUNTIME_ID = 1;
    private boolean chestPopulated;

    public MaviaHouseInterior(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
        super(game, player, world, name, cursor, bounds);
        // not enterable until part 1 of tutorial is complete
        this.enterable = true;
    }

    @Override
    public void enter(boolean setScreen) {
        super.enter(setScreen);
        // prevent duplicating of items when entering/exiting
        if (!chestPopulated) {
            // populate tutorial chests
            final ChestInventoryInteraction interaction = (ChestInventoryInteraction) getByRuntimeId(TUTORIAL_CHEST_RUNTIME_ID);
            interaction.getInventory().addItem(EnchantedVioletItem.ID, 1);
            chestPopulated = true;
        }
    }
}
