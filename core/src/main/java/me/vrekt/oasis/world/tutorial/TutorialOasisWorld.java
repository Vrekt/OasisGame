package me.vrekt.oasis.world.tutorial;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instance;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.sign.OasisTutorialWorldSign;

/**
 * This world acts as a debug/tutorial level for now.
 */
public final class TutorialOasisWorld extends OasisWorld {

    public TutorialOasisWorld(OasisGame game, OasisPlayer player) {
        super(game, player, new World(Vector2.Zero, true));

        getConfiguration().worldScale = OasisGameSettings.SCALE;
        getConfiguration().handlePhysics = true;
        getConfiguration().updateEntityEngine = true;
        getConfiguration().updateEntities = false;
        getConfiguration().updateNetworkPlayers = true;
        getConfiguration().updateLocalPlayer = true;
        this.worldName = "TutorialWorld";
    }

    @Override
    protected void preLoad() {
        interactionManager.registerInteraction(WorldInteractionType.READABLE_SIGN, "oasis:tutorial_sign", OasisTutorialWorldSign::new);
    }

    @Override
    protected void load() {

    }

    @Override
    public void removeInteractableEntity(EntityInteractable entity) {
        super.removeInteractableEntity(entity);
    }

    @Override
    public void enterWorld() {
        super.enterWorld();

        // load this world if we haven't already
        if (!isWorldLoaded) loadWorld(game.getAsset().getWorldMap(Asset.TUTORIAL_WORLD), OasisGameSettings.SCALE);


        // if new game, spawn with a few debug items... for now
        if (game.isNewGame()) {
         //   player.getInventory().addItem(Items.TEMPERED_BLADE, 1);

            guiManager.getHudComponent().showPlayerHint(PlayerHints.WELCOME_HINT, GameManager.secondsToTicks(8));
//            gui.getHud().showHintWithNoFade("Welcome to Oasis! Follow the path and enter the house at the end.");
            // TODO
            // player.getInventory().addItem(EnchantedVioletItem.ID, 1);
            // player.getInventory().addItem(QuickStepItemArtifact.ID, 1);
            //  player.getInventory().addItem(LucidTreeFruitItem.ID, 1);
            game.setNewGame(false); // prevent duplication of items when coming back from instances
            // player.getConnection().send(new ClientSpawnEntity(EntityType.TUTORIAL_COMBAT_DUMMY, player.getPosition()));
        }

    }

    @Override
    public void renderWorld(float delta) {
        super.renderWorld(delta);
        endRender();
    }

    @Override
    public float update(float d) {
        return super.update(d);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (super.touchDown(screenX, screenY, pointer, button)) {
            return true;
        }

        final Instance instance = getInstanceToEnterIfAny();
        if (instance != null) {
            enterInstanceAndFadeIn(instance);
            guiManager.resetCursor();
            return true;
        }

        return false;
    }

}
