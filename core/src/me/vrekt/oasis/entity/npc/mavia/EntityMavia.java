package me.vrekt.oasis.entity.npc.mavia;

import gdx.lunar.entity.drawing.Rotation;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityNPC;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * Mavia quest NPC
 */
public final class EntityMavia extends EntityNPC {

    public EntityMavia(float x, float y, OasisGame game, AbstractWorld worldIn) {
        super("Mavia", x, y, game, worldIn);

        this.dialog = new MaviaDialog();
        this.dialog.starting = "mavia_option_1";
        this.dialog.ending = "mavia_option_0";

        this.dialogSection = this.dialog.getStarting();
        this.speakingRotation = Rotation.FACING_UP;
    }

    @Override
    public void nextDialog(String option) {
        if (dialog.isEnd(option)) {
            // TODO: start quest
            worldIn.getUi().hideDialog();
            this.dialogSection = this.dialog.getStarting();
            return;
        }

        dialogSection = dialog.sections.get(option);
        this.worldIn.getUi().showDialog(this, dialogSection);
    }

    @Override
    public void update(Player player, float delta) {
        if (player.getPosition().dst2(position.x, position.y) <= 5) {
            this.speakable = true;
        } else {
            this.speakable = false;
        }
    }

    @Override
    public void loadNPC(Asset asset) {
        this.entityTexture = asset.getAtlas(Asset.MAVIA_NPC).findRegion("mavia_idle");
        this.width = entityTexture.getRegionWidth();
        this.height = entityTexture.getRegionHeight();
    }
}
