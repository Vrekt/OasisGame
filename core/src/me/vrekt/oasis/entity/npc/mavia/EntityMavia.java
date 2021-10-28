package me.vrekt.oasis.entity.npc.mavia;

import gdx.lunar.entity.drawing.Rotation;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.ino.InoDialog;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * Mavia.
 */
public final class EntityMavia extends EntityInteractable {

    public EntityMavia(float x, float y, OasisGame game, AbstractWorld worldIn) {
        super("Mavia", x, y, game, worldIn);

        this.dialog = new InoDialog();
        this.dialog.starting = "ino_option_1";
        this.dialog.ending = "ino_option_0";

        this.dialogSection = this.dialog.getStarting();
        this.speakingDialogName = "ino_dialog";
        this.speakingRotation = Rotation.FACING_UP;
        this.display = game.asset.getAssets().findRegion("mavia_face");
    }

    @Override
    public void update(Player player, float delta) {
        this.speakable = distance <= 6f;

        super.update(player, delta);
    }

    @Override
    public boolean nextOrEnd(String option) {
        return false;
    }

    @Override
    public void load(Asset asset) {
        this.entityTexture = asset.getAssets().findRegion("mavia_facing_down");
        this.width = entityTexture.getRegionWidth();
        this.height = entityTexture.getRegionHeight();
    }
}
