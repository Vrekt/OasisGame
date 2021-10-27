package me.vrekt.oasis.entity.npc.ino;

import gdx.lunar.entity.drawing.Rotation;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.quest.type.QuestRewards;
import me.vrekt.oasis.world.AbstractWorld;

public final class EntityIno extends EntityInteractable {

    private boolean hasPlayerClearedRocks, hasPushedNotification;
    private boolean hasAwardedBeginnerQuest;

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
                if (!hasPushedNotification) {
                    // push a notification to have the player equip their pickaxe.
                    this.hasPushedNotification = true;
                    worldIn.getGui()
                            .getNotificationGui()
                            .sendPlayerNotification("Press 1 to equip your pickaxe", 4.0f);
                }

                dialogSection = dialog.sections.get("ino_option_2");
            } else {
                if (!hasAwardedBeginnerQuest) {
                    hasAwardedBeginnerQuest = true;

                    worldIn.getPlayer().givePlayerQuestReward(QuestRewards.ETHE, 1000);
                    worldIn.getPlayer().givePlayerQuestReward(QuestRewards.COMMON_XP_BOOK, 10);
                    worldIn.getPlayer().award("Hunnewell");
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
        this.speakable = player.getPosition().dst2(position.x, position.y) <= 35f;

        // check if rocks were cleared for first part of dialog
        if (worldIn.getObjectByRelation("ino") == null && !hasPlayerClearedRocks) {
            // rocks were clear, advance dialog.
            setSpeakingTo(false);
            this.dialogSection = dialog.sections.get("ino_option_3");
            this.hasPlayerClearedRocks = true;
        }

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
