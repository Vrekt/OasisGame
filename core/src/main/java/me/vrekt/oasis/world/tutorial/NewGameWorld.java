package me.vrekt.oasis.world.tutorial;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.questing.quests.tutorial.ANewHorizonQuest;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.sign.WrynnBasementWarningSign;

/**
 * The new game world, intended to teach players the game
 */
public final class NewGameWorld extends GameWorld {

    public NewGameWorld(OasisGame game, PlayerSP player) {
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
        networkHandler.registerPlayerHandlers();
    }

    @Override
    public void enter() {
        super.enter();

        if (!isWorldLoaded) create(game.getAsset().getWorldMap(Asset.TUTORIAL_WORLD), OasisGameSettings.SCALE);

        if (game.isNewGame()) {
            player.getInventory().add(Items.TEMPERED_BLADE, 1);
            player.getInventory().add(Items.PIG_HEART, 1);

            player.getQuestManager().addActiveQuest(QuestType.A_NEW_HORIZON, new ANewHorizonQuest());
            // TODO: Better start hint
            //  guiManager.getHudComponent().showPlayerHint(PlayerHints.WELCOME_HINT, GameManager.secondsToTicks(8));
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
