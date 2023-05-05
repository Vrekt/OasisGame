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
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.item.weapons.EnchantedVioletItem;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instance;
import me.vrekt.oasis.world.obj.interaction.chest.ChestInventoryInteraction;

import java.util.List;

/**
 * This world acts as a debug/tutorial level for now.
 */
public final class TutorialOasisWorld extends OasisWorld {

    public static final int TUTORIAL_CHEST_RUNTIME_ID = 2;
    public static final int TUTORIAL_CHEST_RUNTIME_ID_2 = 3;
    public static final int TUTORIAL_CHEST_RUNTIME_ID_3 = 4;

    private MaviaTutorial tutorialEntity;
    private List<ChestInventoryInteraction> chests;

    public TutorialOasisWorld(OasisGame game, OasisPlayerSP player, World world) {
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
        chests = getByRuntimeIds(ChestInventoryInteraction.class, TUTORIAL_CHEST_RUNTIME_ID, TUTORIAL_CHEST_RUNTIME_ID_2, TUTORIAL_CHEST_RUNTIME_ID_3);
        for (ChestInventoryInteraction chest : chests) chest.setInteractable(false);

        chests.get(0).getInventory().addItem(LucidTreeHarvestingToolItem.class, 1);
        chests.get(0).getInventory().addItem(QuickStepItemArtifact.class, 1);

        if (game.isNewGame()) {
            player.getInventory().addItem(EnchantedVioletItem.class, 1);
            player.getInventory().addItem(QuickStepItemArtifact.class, 1);
            player.getInventory().addItem(LucidTreeFruitItem.class, 1);
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
            GameManager.resetCursor();
            this.cursorChanged = false;
            instance.enter();

            if (tutorialEntity != null) {
                // spawn mavia in this instance
                tutorialEntity.setPosition(instance.getSpawn().x, instance.getSpawn().y + 4.0f, false);
                instance.addInteractableEntity(tutorialEntity);
            }
            return true;
        }

        return false;
    }

    public void unlockTutorialChests() {
        for (ChestInventoryInteraction chest : chests) chest.setInteractable(true);
        chests.clear();
    }

}
