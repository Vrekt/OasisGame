package me.vrekt.oasis.entity.npc.ino;

import com.badlogic.gdx.Input;
import gdx.lunar.entity.drawing.Rotation;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.quest.type.QuestType;
import me.vrekt.oasis.world.AbstractWorld;

public final class EntityIno extends EntityInteractable {

    private boolean hasPlayerClearedRocks, hasPushedNotification;

    public EntityIno(float x, float y, OasisGame game, AbstractWorld worldIn) {
        super("Ino", x, y, game, worldIn);

        this.dialog = new InoDialog();
        this.dialog.starting = "ino_option_1";
        this.dialog.ending = "ino_option_0";

        this.dialogSection = this.dialog.getStarting();
        this.speakingDialogName = "ino_dialog";
        this.speakingRotation = Rotation.FACING_LEFT;
        this.display = game.asset.getAssets().findRegion("ino_face");
    }

    @Override
    public boolean nextOrEnd(String option) {
        if (dialog.isEnd(option)) {
            setSpeakingTo(false);
            if (worldIn.getPlayer().isPickaxeLocked()) worldIn.getPlayer().setPickaxeLocked(false);

            if (!hasPlayerClearedRocks) {
                // player hasn't cleared section yet, so reverse dialog
                dialogSection = dialog.sections.get("ino_option_2");
            } else {
                // award the player for their first beginner quest.
                if (!questManager.isQuestCompleted(QuestType.HUNNEWELL)) {
                    questManager.getQuest(QuestType.HUNNEWELL).awardPlayer(game.thePlayer);
                }

                dialogSection = dialog.sections.get("ino_option_10");
            }
            return true;
        }

        dialogSection = dialog.sections.get(option);
        return false;
    }

    @Override
    public void update(Player player, float delta) {
        this.speakable = distance <= 35f;

        if (speakingTo) {
            player.disableInputs(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S, Input.Keys.E);
        } else if (!player.hasDisabledInputs()) {
            player.enableInputs(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S, Input.Keys.E);
        }

        // check if rocks were cleared for first part of dialog
        if (worldIn.getObjectByRelation("ino") == null
                && !hasPlayerClearedRocks) {
            // rocks were clear, advance dialog.
            setSpeakingTo(true);
            this.dialogSection = dialog.sections.get("ino_option_3");
            this.hasPlayerClearedRocks = true;

            // automatically talk once rocks have been cleared.
            worldIn.getGui().getDialog().setDialogToRender(this, dialogSection, display);
            worldIn.getGui().getDialog().showGui();
        }

        super.update(player, delta);
    }

    @Override
    public void load(Asset asset) {
        this.entityTexture = asset.getAssets().findRegion("ino_idle");
        this.width = entityTexture.getRegionWidth();
        this.height = entityTexture.getRegionHeight();

        this.rotations.put(Rotation.FACING_UP, "ino_facing_up");
        this.rotations.put(Rotation.FACING_DOWN, "ino_facing_down");
        this.rotations.put(Rotation.FACING_LEFT, "ino_facing_left");
        this.rotations.put(Rotation.FACING_RIGHT, "ino_facing_right");
    }
}
