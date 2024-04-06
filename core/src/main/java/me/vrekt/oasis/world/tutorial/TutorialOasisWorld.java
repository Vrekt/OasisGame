package me.vrekt.oasis.world.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.tutorial.MaviaTutorial;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instance;
import me.vrekt.oasis.world.obj.interaction.chest.ChestInventoryInteraction;

import java.util.HashMap;
import java.util.Map;

/**
 * This world acts as a debug/tutorial level for now.
 */
public final class TutorialOasisWorld extends OasisWorld {

    public static final int TUTORIAL_CHEST_RUNTIME_ID = 2;
    public static final int TUTORIAL_CHEST_RUNTIME_ID_2 = 3;
    public static final int TUTORIAL_CHEST_RUNTIME_ID_3 = 4;

    private MaviaTutorial tutorialEntity;
    private Map<Integer, ChestInventoryInteraction> tutorialChestInteractions = new HashMap<>();

    public TutorialOasisWorld(OasisGame game, OasisPlayer player, World world) {
        super(game, player, world);

        getConfiguration().worldScale = OasisGameSettings.SCALE;
        getConfiguration().handlePhysics = true;
        getConfiguration().updateEntityEngine = true;
        getConfiguration().updateEntities = false;
        getConfiguration().updateNetworkPlayers = true;
        getConfiguration().updateLocalPlayer = true;
        this.worldName = "TutorialWorld";
    }

    @Override
    public void removeInteractableEntity(EntityInteractable entity) {
        super.removeInteractableEntity(entity);
        if (entity.getType() == EntityNPCType.MAVIA) {
            // we need to know she is finished so can spawn in instance
            this.tutorialEntity = (MaviaTutorial) entity;
        }
    }

    @Override
    public void enterWorld(boolean fromInstance) {
        super.enterWorld(fromInstance);
        if (!isWorldLoaded) {
            loadWorld(game.getAsset().getWorldMap(Asset.TUTORIAL_WORLD), OasisGameSettings.SCALE);
        }

        // populate tutorial chests, lock them as-well until a certain point in the tutorial
        tutorialChestInteractions = getByRuntimeIdsMap(TUTORIAL_CHEST_RUNTIME_ID, TUTORIAL_CHEST_RUNTIME_ID_2, TUTORIAL_CHEST_RUNTIME_ID_3);
        if (tutorialChestInteractions.isEmpty()) {
            GameLogging.error(this, "Failed to find {isWorldLoaded} tutorial chests to populate!");
        } else {

            tutorialChestInteractions.values().forEach(interaction -> interaction.setInteractable(true));

            // TODO: Change these items later
            tutorialChestInteractions.get(TUTORIAL_CHEST_RUNTIME_ID)
                    .getInventory()
                    .addItems(Items.ENCHANTED_VIOLET_ITEM, 1)
                    .addItems(Items.QUICKSTEP_ARTIFACT, 1);
        }

        // if new game, spawn with a few debug items... for now
        if (game.isNewGame()) {
            player.getInventory().addItem(Items.ENCHANTED_VIOLET_ITEM, 1);
            player.getInventory().addItem(Items.QUICKSTEP_ARTIFACT, 1);
            player.getInventory().addItem(Items.LUCID_FRUIT_TREE_ITEM, 1);
            GameManager.getOasis().guiManager.getHudComponent().showPlayerHint(PlayerHints.WELCOME_HINT);
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
    public void renderWorld(SpriteBatch batch, float delta) {
        super.renderWorld(batch, delta);
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

            if (tutorialEntity != null) {
                // spawn mavia in this instance
                tutorialEntity.setPosition(instance.getWorldSpawn().x, instance.getWorldSpawn().y + 4.0f, false);
                instance.addInteractableEntity(tutorialEntity);
            }

            GameManager.resetCursor();
            return true;
        }

        return false;
    }

    public void unlockTutorialChests() {
        tutorialChestInteractions.values().forEach(interaction -> interaction.setInteractable(true));
        tutorialChestInteractions.clear();
    }

}
