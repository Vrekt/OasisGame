package me.vrekt.oasis.entity.npc.ino;

import gdx.lunar.entity.drawing.Rotation;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.quest.type.QuestType;
import me.vrekt.oasis.world.AbstractWorld;

public final class EntityIno extends EntityInteractable {

    private boolean questAssigned;

    public EntityIno(float x, float y, OasisGame game, AbstractWorld worldIn) {
        super("Ino", x, y, game, worldIn);

        this.dialog = new InoDialog();
        this.dialog.starting = "ino_option_1";
        this.dialog.ending = "ino_option_0";

        this.dialogSection = this.dialog.getStarting();
        this.display = game.getAsset().getAssets().findRegion("ino_face");
        this.type = EntityNPCType.INO;
    }

    @Override
    public boolean nextOrEnd(String option) {
        if (dialog.isEnd(option)) {
            setSpeakingTo(false);

            if (!questAssigned) {
                // assign the players first quest.
                worldIn.getGui().showQuestTracking();
                worldIn.getGui().getQuest().startTrackingQuest(questManager.getQuest(QuestType.HUNNEWELL));
                questAssigned = true;
            }

            // advance too afterwards.
            dialogSection = dialog.sections.get("ino_option_10");
            return true;
        }

        dialogSection = dialog.sections.get(option);
        return false;
    }

    @Override
    public void update(Player player, float delta) {
        this.speakable = distance <= 8f;
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
