package me.vrekt.oasis.world.tutorial;

import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;

/**
 * This world acts as a debug/tutorial level for now.
 */
public final class TutorialOasisWorld extends OasisWorld {

    public TutorialOasisWorld(OasisGame game, OasisPlayerSP player, World world) {
        super(game, player, world);

        getConfiguration().worldScale = OasisGameSettings.SCALE;
        getConfiguration().handlePhysics = true;
        getConfiguration().updateEngine = true;
        getConfiguration().updateEntities = false;
        getConfiguration().updateNetworkPlayers = true;
        getConfiguration().updatePlayer = true;
    }

    @Override
    public void loadIntoWorld() {
        super.loadIntoWorld();
        loadWorld(game.getAsset().getWorldMap(Asset.TUTORIAL_WORLD), OasisGameSettings.SCALE);
    }

    private void updateEnvironmentInteractions() {
        //   float distance = OasisGameSettings.OBJECT_UPDATE_DISTANCE;
        //  WorldInteraction interact = null;
        //   for (WorldInteraction interaction : interactions) {
        //       if (interaction.isWithinInteractionDistance(player.getPosition())
        //              && interaction.isInteractable()
        //              && !interaction.isInteractedWith()) {
        //         if (interaction.getDistance() < distance) {
        //             distance = interaction.getDistance();
        //             interact = interaction;
        //         }
        //      }
        //    }

        //    if (interact != null) interact.interact(player);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        // no entity to interact with, get environment object
        if (!interactWithEntity()) {
            return interactWithEnvironment();
        }
        return false;
    }
}
