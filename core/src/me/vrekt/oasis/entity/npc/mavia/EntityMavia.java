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
        this.currentDialog = dialog.links.get("mavia_option_1");
        this.speakingRotation = Rotation.FACING_UP;
    }

    @Override
    public void nextDialog(String option) {
        if (option.contains("0")) {
            // indicates dialog is finished.
            this.worldIn.setNpcSpeakable(null);
            // TODO: Start quest
        }
        this.currentDialog = dialog.links.get(option);
        this.worldIn.dialogChanged();
    }

    @Override
    public void update(Player player, float delta) {
        if (player.getPosition().dst2(position.x, position.y) <= 5) {
            this.speakable = true;
            this.worldIn.setNpcSpeakable(this);
        } else {
            this.speakable = false;
            this.worldIn.setNpcSpeakable(null);
        }
    }

    @Override
    public void loadNPC(Asset asset) {
        this.entityTexture = asset.getAtlas(Asset.MAVIA_NPC).findRegion("mavia_idle");
        this.width = entityTexture.getRegionWidth();
        this.height = entityTexture.getRegionHeight();
    }
}
