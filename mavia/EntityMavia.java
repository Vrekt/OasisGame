package me.vrekt.oasis.entity.npc.mavia;

import gdx.lunar.entity.drawing.Rotation;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.quest.type.QuestType;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * Mavia quest NPC
 */
public final class EntityMavia extends EntityInteractable {

    public EntityMavia(float x, float y, OasisGame game, AbstractWorld worldIn) {
        super("Mavia", x, y, game, worldIn);

        this.dialog = new MaviaDialog();
        this.dialog.starting = "mavia_option_1";
        this.dialog.ending = "mavia_option_0";

        this.dialogSection = this.dialog.getStarting();
        this.speakingRotation = Rotation.FACING_UP;
        this.questRelatedTo = QuestType.MAVIA_RINGFRUIT_QUEST;

        this.display = game.asset.getAtlas(Asset.MAVIA_NPC).findRegion("mavia_face");
    }

    @Override
    public boolean nextDialog(String option) {
        if (dialog.isEnd(option)) {
            // give the player the ingredients scroll.

            // start the mavia quest
            dialogSection = dialog.sections.get("mavia_option_16");
            game.getQuestManager().getQuest(questRelatedTo).setStarted(true);
            game.getQuestManager().getQuest(questRelatedTo).
                    setQuestInformation("Collect all the ingredients from the list Mavia gave you.");
            return true;
        }

        dialogSection = dialog.sections.get(option);
        return false;
    }

    @Override
    public void update(Player player, float delta) {
        this.speakable = player.getPosition().dst2(position.x, position.y) <= 5f;
    }

    @Override
    public void load(Asset asset) {
        this.entityTexture = asset.getAtlas(Asset.MAVIA_NPC).findRegion("mavia_idle");
        this.width = entityTexture.getRegionWidth();
        this.height = entityTexture.getRegionHeight();
    }
}
