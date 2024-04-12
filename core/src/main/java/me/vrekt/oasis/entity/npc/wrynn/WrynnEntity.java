package me.vrekt.oasis.entity.npc.wrynn;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.wrynn.dialog.WrynnDialog;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Wrynn.
 */
public final class WrynnEntity extends EntityInteractable {

    private boolean hintShown;

    public WrynnEntity(String name, Vector2 position, OasisPlayer player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(name, position, player, worldIn, game, type);

        entityDialog = WrynnDialog.create();
        dialog = entityDialog.getStarting();
    }

    @Override
    public void load(Asset asset) {
        putRegion("face", asset.get("wrynn_face"));
        putRegion("facing_down", asset.get("wrynn_facing_down"));
        putRegion("facing_up", asset.get("wrynn_facing_up"));

        this.dialogFaceAsset = "face";
        currentRegionState = getRegion("facing_up");
        setSize(currentRegionState.getRegionWidth(), currentRegionState.getRegionHeight(), OasisGameSettings.SCALE);

        dialogFrames[0] = asset.get("dialog", 1);
        dialogFrames[1] = asset.get("dialog", 2);
        dialogFrames[2] = asset.get("dialog", 3);
    }

    @Override
    public void setSpeakingTo(boolean speakingTo) {
        super.setSpeakingTo(speakingTo);

        if (speakingTo && !hintShown) {
            // show player hint about how to interact with the dialog system
            game.guiManager.getHudComponent().showPlayerHint(PlayerHints.DIALOG_TUTORIAL_HINT, 12.0f);
            hintShown = true;
        }
    }
}
