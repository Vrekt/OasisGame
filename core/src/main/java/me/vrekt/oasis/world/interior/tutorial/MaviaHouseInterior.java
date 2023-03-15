package me.vrekt.oasis.world.interior.tutorial;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.item.tools.TutorialWand;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instanced;
import me.vrekt.oasis.world.obj.interaction.chest.ChestInventoryInteraction;

/**
 * Mavia's tutorial house on Tutorial Island
 */
public final class MaviaHouseInterior extends Instanced {

    // tutorial chest runtime ID
    public static final int TUTORIAL_CHEST_RUNTIME_ID = 1;

    public MaviaHouseInterior(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
        super(game, player, world, name, cursor, bounds);
        // not enterable until part 1 of tutorial is complete
        this.enterable = true;
    }

    @Override
    public void enter() {
        super.enter();

        // populate tutorial chests
        final ChestInventoryInteraction interaction = (ChestInventoryInteraction) getByRuntimeId(TUTORIAL_CHEST_RUNTIME_ID);
        interaction.getInventory().addItem(LucidTreeFruitItem.class, 1);
        interaction.getInventory().addItem(TutorialWand.class, 1);
        interaction.getInventory().addItem(LucidTreeHarvestingToolItem.class, 1);
    }
}
