package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.tutorial.dialog.MaviaTutorialDialog;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.hud.HudGui;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents a tutorial/debug NPC.1
 */
public final class MaviaTutorial extends EntityInteractable {

    public MaviaTutorial(String name, Vector2 position, OasisPlayerSP player, OasisWorld worldIn, OasisGame game) {
        super(name, position, player, worldIn, game, EntityNPCType.MAVIA);

        entityDialog = new MaviaTutorialDialog();
        dialog = entityDialog.getStarting();
    }

    @Override
    public boolean advanceDialogStage(String option) {
        // first part of player tutorial, give the player an item.

        if (option.equals("mavia_dialog_end_1")) {
            setSpeakingTo(false);
            speakable = false;

            if (!player.getInventory().hasItem(LucidTreeHarvestingToolItem.class)) {
                // give the player the harvesting tool.
                game.getGui().showHud();
                final Item item = game
                        .getPlayer()
                        .getInventory()
                        .giveEntityItem(LucidTreeHarvestingToolItem.class, 1);
                ((HudGui) game.getGui().getGui(GuiType.HUD)).showItemCollected(item);
            } else {
                Logging.error(this, "Failed to give player their tutorial item.");
            }
        }

        // player should select their class
        if (option.equals("mavia_dialog_select_class")) {
            game.getGui().hideThenShowGui(GuiType.DIALOG, GuiType.CLASS);
        }

        this.dialog = entityDialog.sections.get(option);
        return false;
    }

    @Override
    public void load(Asset asset) {
        putRegion("face", asset.get("mavia_face"));
        putRegion("facing_up", asset.get("mavia_facing_up"));
        putRegion("facing_down", asset.get("mavia_facing_down"));
        putRegion("facing_left", asset.get("mavia_facing_left"));
        putRegion("facing_right", asset.get("mavia_facing_right"));

        currentRegionState = getRegion("facing_down");
        setConfig(currentRegionState.getRegionWidth(), currentRegionState.getRegionHeight(), OasisGameSettings.SCALE);

        dialogFrames[0] = asset.get("dialog", 1);
        dialogFrames[1] = asset.get("dialog", 2);
        dialogFrames[2] = asset.get("dialog", 3);
    }
}
