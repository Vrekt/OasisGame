package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.tutorial.dialog.MaviaTutorialDialog;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingTool;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents a tutorial/debug NPC.1
 */
public final class MaviaTutorial extends EntityInteractable {

    private boolean atEnd;

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
            atEnd = true;

            // give the player the harvesting tool.
            game.getGui().showHud();
            final Item item = game
                    .getPlayer()
                    .getInventory()
                    .giveEntityItem(LucidTreeHarvestingTool.class, 1);

            // mavia should face towards the tree.
            currentRegionState = getRegion("facing_left");

            if (item == null) {
                Logging.error(this, "Failed to give player harvesting item??");
            } else {
                game.getGui().showItemCollected(item);
            }
        }

        // class selector GUI
        if (option.equals("mavia_dialog_2")) {
            game.getGui().hideGui(GuiType.DIALOG);
            game.getGui().showGui(GuiType.CLASS);
        }

        this.dialog = entityDialog.sections.get(option);
        return false;
    }

    @Override
    public boolean isSpeakable() {
        return !atEnd && super.isSpeakable();
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

    @Override
    public void facePlayer() {
        // not required for this entity.
        // currentRegionState = getRegion(Rotation.getOppositeRotation(player.getRotation()).toString());
        // setConfig(currentRegionState.getRegionWidth(), currentRegionState.getRegionHeight(), OasisGameSettings.SCALE);
    }

    @Override
    public void update(float v) {
        super.update(v);

        if (getDistanceFromPlayer() <= 50) {
            setDrawDialogAnimationTile(true);
        } else if (getDistanceFromPlayer() > 50 && drawDialogAnimationTile()) {
            setDrawDialogAnimationTile(false);
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        super.render(batch, delta);
        if (drawDialogAnimationTile()) {
            renderCurrentDialogFrame(batch, dialogFrames[getCurrentDialogFrame() - 1]);
        }
    }

    /**
     * @param batch   -
     * @param region-
     */
    private void renderCurrentDialogFrame(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, getX() + 0.2f, getY() + getHeightScaled() + 0.1f,
                region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);
    }

}
