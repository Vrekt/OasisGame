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
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.questing.quests.tutorial.TutorialIslandQuest;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instance;
import me.vrekt.oasis.world.interior.InstanceType;
import me.vrekt.oasis.world.obj.interaction.tutorial.TutorialTreeInteraction;
import me.vrekt.oasis.world.tutorial.TutorialOasisWorld;

/**
 * Represents a tutorial/debug NPC.1
 */
public final class MaviaTutorial extends EntityInteractable {

    private boolean tutorialUnlocked, didUseFruitItem, allowedToChopTree, didChopTree;

    public MaviaTutorial(String name, Vector2 position, OasisPlayerSP player, OasisWorld worldIn, OasisGame game) {
        super(name, position, player, worldIn, game, EntityNPCType.MAVIA);

        entityDialog = MaviaTutorialDialog.create();
        dialog = entityDialog.getStarting();

        // first part of player tutorial, give the player an item.
        addDialogAction("mavia_dialog_end_1", this::updateEndStage1);
        addDialogAction("mavia_dialog_end_2", () -> {
            setSpeakingTo(false);
            allowedToChopTree = true;
        });
        addDialogAction("mavia_dialog_end_3", this::updateEndStage3);
        addDialogAction("mavia_dialog_end_4", this::updateEndStage4);
        // player should select their class
        addDialogAction("mavia_dialog_select_class", () -> game.getGui().hideThenShowGui(GuiType.DIALOG, GuiType.CLASS));
    }

    @Override
    public boolean advanceDialogStage(String option) {
        if (executeDialogAction(option)) return false;
        this.dialog = entityDialog.getSectionDefault(option, dialog);
        return true;
    }

    private void updateEndStage1() {
        setSpeakingTo(false);
        speakable = false;

        // unlock quest, obj1: speak obj2: player class completed both
        if (!tutorialUnlocked) {
            this.tutorialUnlocked = true;
            // unlock the next 2 objectives
            player.getQuestManager().updateQuestObjectiveAndUnlockNext(TutorialIslandQuest.class, 2);
            game.getGui().hideGui(GuiType.DIALOG);
            game.getGui().showHud();

            // unlock tutorial chests within the tutorial world.
            if (player.isInTutorialWorld()) {
                ((TutorialOasisWorld) player.getGameWorldIn()).unlockTutorialChests();
            }
        }
    }

    private void updateEndStage3() {
        final LucidTreeFruitItem item = (LucidTreeFruitItem) player.getInventory().getItemById(LucidTreeFruitItem.ID);
        if (item != null) {
            item.setAllowedToConsume(true);
        }
    }

    private void updateEndStage4() {
        setSpeakingTo(false);
        // at this point dialog is finished and NPC needs to be moved
        gameWorldIn.removeInteractableEntity(this);
        // TODO: this.worldIN = null
        // set instance allowed to enter
        final Instance house = gameWorldIn.getInstance(InstanceType.MAVIA_TUTORIAL_HOUSE);
        if (house != null) {
            house.setEnterable(true);
        } else {
            Logging.error(this, "Mavia's house not found !!");
        }

        this.dialog = entityDialog.getSection("mavia_dialog_10");
    }

    @Override
    public boolean advanceDialogStage() {
        return advanceDialogStage(dialog.getNextKey());
    }

    @Override
    public void load(Asset asset) {
        putRegion("face", asset.get("mavia_face"));
        putRegion("facing_up", asset.get("mavia_facing_up"));
        putRegion("facing_down", asset.get("mavia_facing_down"));
        putRegion("facing_left", asset.get("mavia_facing_left"));
        putRegion("facing_right", asset.get("mavia_facing_right"));

        this.dialogFaceAsset = "face";
        currentRegionState = getRegion("facing_down");
        setSize(currentRegionState.getRegionWidth(), currentRegionState.getRegionHeight(), OasisGameSettings.SCALE);

        dialogFrames[0] = asset.get("dialog", 1);
        dialogFrames[1] = asset.get("dialog", 2);
        dialogFrames[2] = asset.get("dialog", 3);
    }

    @Override
    public void update(float v) {
        super.update(v);

        // player has collected what they needed, advance dialog stage.
        if (tutorialUnlocked && player.getInventory().hasItem(LucidTreeHarvestingToolItem.ID)) {
            // also reset because we don't care anymore
            this.tutorialUnlocked = false;
            this.dialog = entityDialog.getSection("mavia_dialog_6");
        }

        if (allowedToChopTree) {
            player.getGameWorldIn().getByRuntimeId(TutorialTreeInteraction.RUNTIME_ID).setInteractable(true);
            allowedToChopTree = false;
        }

        if (player.didChopTree()) {
            player.setDidChopTree(false);
            this.dialog = entityDialog.getSection("mavia_dialog_8");
        }

        if (!didUseFruitItem && player.didUseTutorialFruit()) {
            didUseFruitItem = true;
            this.dialog = entityDialog.getSection("mavia_dialog_9");
        }
    }

}
