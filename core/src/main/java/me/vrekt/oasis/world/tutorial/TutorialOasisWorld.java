package me.vrekt.oasis.world.tutorial;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.questing.quests.tutorial.TutorialIslandQuest;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.instance.GameWorldInterior;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.sign.WrynnBasementWarningSign;

/**
 * This world acts as a debug/tutorial level for now.
 */
public final class TutorialOasisWorld extends GameWorld {

    public TutorialOasisWorld(OasisGame game, PlayerSP player) {
        super(game, player, new World(Vector2.Zero, true));

        getConfiguration().worldScale = OasisGameSettings.SCALE;
        getConfiguration().handlePhysics = true;
        getConfiguration().updateEntityEngine = true;
        getConfiguration().updateEntities = false;
        getConfiguration().updateNetworkPlayers = true;
        this.worldName = "TutorialWorld";
    }

    @Override
    protected void preLoad() {
        interactionManager.registerInteraction(WorldInteractionType.READABLE_SIGN, "oasis:basement_sign", WrynnBasementWarningSign::new);
    }

    @Override
    protected void loadNetworkComponents() {
        networkHandler.registerStartGameHandler();
        networkHandler.registerCreatePlayerHandler();
    }

    @Override
    public void enter() {
        super.enter();

        if (!isWorldLoaded) create(game.getAsset().getWorldMap(Asset.TUTORIAL_WORLD), OasisGameSettings.SCALE);

        if (game.isNewGame()) {
            player.getInventory().add(Items.TEMPERED_BLADE, 1);
            player.getInventory().add(Items.PIG_HEART, 1);

            player.getQuestManager().addActiveQuest(QuestType.TUTORIAL_ISLAND, new TutorialIslandQuest());
            guiManager.getHudComponent().showPlayerHint(PlayerHints.WELCOME_HINT, GameManager.secondsToTicks(8));
            game.setNewGame(false);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (super.touchDown(screenX, screenY, pointer, button)) {
            return true;
        }

        final GameWorldInterior interior = getInteriorToEnter();
        if (interior != null) {
            enterInterior(interior);
            guiManager.resetCursor();
            return true;
        }

        return false;
    }

}
