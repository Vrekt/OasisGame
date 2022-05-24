package me.vrekt.oasis.world.tutorial;

import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.tutorial.MaviaTutorial;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.world.OasisWorld;

/**
 * This world acts as a debug/tutorial level for now.
 */
public final class TutorialOasisWorld extends OasisWorld {

    private boolean nearbyEntities;
    private MaviaTutorial tutorial;

    public TutorialOasisWorld(OasisGame game, OasisPlayerSP player, World world) {
        super(game, player, world);

        getConfiguration().worldScale = OasisGameSettings.SCALE;
        getConfiguration().handlePhysics = true;
        getConfiguration().updateEngine = true;
        getConfiguration().updateEntities = false;
        getConfiguration().updateNetworkPlayers = true;
        getConfiguration().updatePlayer = true;
    }

    public MaviaTutorial getTutorial() {
        return tutorial;
    }

    @Override
    public void loadIntoWorld() {
        super.loadIntoWorld();
        loadWorld(Asset.get().getWorldMap(Asset.TUTORIAL_WORLD), OasisGameSettings.SCALE);
    }

    @Override
    public void handleInteraction() {
        final EntityInteractable closest = getClosest();
        if (closest != null && closest.isSpeakable() && !closest.isSpeakingTo()) {
            closest.setSpeakingTo(true);

            gui.showEntityDialog(closest);
            gui.showGui(GuiType.DIALOG);
        }
    }
}
