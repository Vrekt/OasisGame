package me.vrekt.oasis.world;

import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.save.Savable;
import me.vrekt.oasis.save.world.InteriorSave;
import me.vrekt.oasis.save.world.WorldSave;
import me.vrekt.oasis.save.world.entity.EnemyEntitySave;
import me.vrekt.oasis.save.world.entity.GameEntitySave;
import me.vrekt.oasis.save.world.entity.InteractableEntitySave;
import me.vrekt.oasis.save.world.obj.ContainerWorldObjectSave;
import me.vrekt.oasis.save.world.obj.InteractableWorldObjectSave;
import me.vrekt.oasis.save.world.obj.WorldObjectSave;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;
import org.apache.commons.lang3.StringUtils;

/**
 * Handles loading a world
 */
public final class WorldSaveLoader implements Savable<WorldSave>, Disposable {

    private GameWorld world;

    public WorldSaveLoader(GameWorld world) {
        this.world = world;
    }

    @Override
    public void load(WorldSave worldSave) {
        loadWorldEntities(worldSave);
        loadContainersAndObjects(worldSave);

        if (worldSave instanceof InteriorSave save) {
            final GameWorldInterior interior = (GameWorldInterior) world;
            GameLogging.info(this, "Loading interior WorldSave");

            interior.setEnterable(save.enterable());
            world.getGame().getWorldManager().setParentWorldPosition(save.enteredPosition());
        }

    }

    /**
     * Load all entities
     *
     * @param worldSave save
     */
    private void loadWorldEntities(WorldSave worldSave) {
        if (worldSave.entities() == null) return;

        var interactable = 0;
        var enemy = 0;

        // TODO: EntityId, maybe only if multiplayer
        for (GameEntitySave entitySave : worldSave.entities()) {
            if (StringUtils.equals(entitySave.is(), "interactable")) {
                final InteractableEntitySave ies = (InteractableEntitySave) entitySave;
                world.getEntityByType(ies.type()).load(ies);

                interactable++;
            } else {
                final EnemyEntitySave ees = (EnemyEntitySave) entitySave;
                world.getEnemyByType(ees.type()).load(ees);

                enemy++;
            }
        }
        GameLogging.info(this, "Loaded %d interactable entities and %d enemies", interactable, enemy);
    }

    /**
     * Load all objects, interactable objects, and containers
     *
     * @param save save
     */
    private void loadContainersAndObjects(WorldSave save) {
        if (save.objects() == null) return;

        var interactable = 0;
        var normal = 0;
        for (WorldObjectSave object : save.objects()) {
            if (object.interactable()) {
                loadInteractableObject((InteractableWorldObjectSave) object);
                interactable++;
            } else {
                loadNormalWorldObject(object);
                normal++;
            }
        }
        GameLogging.info(this, "Loaded %d interactable objects and %d normal objects", interactable, normal);
    }

    /**
     * Load a default world object
     *
     * @param save save
     */
    private void loadNormalWorldObject(WorldObjectSave save) {
        // TODO: Unused currently.
    }

    /**
     * Load all interactable objects
     *
     * @param save save
     */
    private void loadInteractableObject(InteractableWorldObjectSave save) {
        if (save.type() == WorldInteractionType.CONTAINER) {
            loadContainer((ContainerWorldObjectSave) save);
        } else {
            final InteractableWorldObject object = world.findInteraction(save.type(), save.key());
            if (save.enabled()) object.enable();
        }
    }

    /**
     * Load a container and its contents
     *
     * @param save save
     */
    private void loadContainer(ContainerWorldObjectSave save) {
        if (save.inventory() != null) {
            final OpenableContainerInteraction interaction = (OpenableContainerInteraction) world.findInteraction(save.type(), save.key());
            if (interaction != null) {
                interaction.inventory().transferFrom(save.inventory().inventory());
                if (save.enabled()) interaction.enable();

                GameLogging.info(this, "Loaded container: %s", save.key());
            } else {
                GameLogging.warn(this, "Failed to find a container: %s", save.key());
            }
        }
    }

    @Override
    public void dispose() {
        world = null;
    }
}
