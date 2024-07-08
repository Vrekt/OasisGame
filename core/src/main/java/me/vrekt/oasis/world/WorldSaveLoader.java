package me.vrekt.oasis.world;

import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.save.Savable;
import me.vrekt.oasis.save.world.AbstractWorldSaveState;
import me.vrekt.oasis.save.world.InteriorWorldSave;
import me.vrekt.oasis.save.world.entity.AbstractEntitySaveState;
import me.vrekt.oasis.save.world.entity.EnemyEntitySave;
import me.vrekt.oasis.save.world.entity.GenericEntitySave;
import me.vrekt.oasis.save.world.entity.InteractableEntitySave;
import me.vrekt.oasis.save.world.obj.AbstractWorldObjectSaveState;
import me.vrekt.oasis.save.world.obj.InteractableWorldObjectSave;
import me.vrekt.oasis.save.world.obj.objects.ContainerWorldObjectSave;
import me.vrekt.oasis.save.world.obj.objects.ItemInteractionObjectSave;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;

/**
 * Handles loading a world
 */
public final class WorldSaveLoader implements Savable<AbstractWorldSaveState>, Disposable {

    private GameWorld world;

    public WorldSaveLoader(GameWorld world) {
        this.world = world;
    }

    @Override
    public void load(AbstractWorldSaveState worldSave) {
        loadWorldEntities(worldSave);
        loadContainersAndObjects(worldSave);

        if (worldSave instanceof InteriorWorldSave save) {
            final GameWorldInterior interior = (GameWorldInterior) world;

            interior.setHasVisited(true);
            interior.setEnterable(save.enterable());
            interior.setLocked(save.locked());
            interior.setLockDifficulty(save.difficulty());
            world.getGame().getWorldManager().setParentWorldPosition(save.enteredPosition());
        } else {
            for (InteriorWorldSave interior : worldSave.interiors()) {
                loadInterior(interior);
            }
        }
        world.hasVisited = true;
    }

    /**
     * Load all entities
     *
     * @param worldSave save
     */
    private void loadWorldEntities(AbstractWorldSaveState worldSave) {
        if (worldSave.entities() == null) return;

        var interactable = 0;
        var enemy = 0;

        // TODO: EntityId, maybe only if multiplayer
        for (AbstractEntitySaveState entitySave : worldSave.entities()) {
            if (entitySave.type().interactable()) {
                final InteractableEntitySave ies = (InteractableEntitySave) entitySave;
                world.findInteractableEntity(ies.type()).load(ies);

                interactable++;
            } else if (entitySave.type().enemy()) {
                final EnemyEntitySave ees = (EnemyEntitySave) entitySave;
                if (ees.isDead()) {
                    final EntityEnemy e = world.findEnemy(ees.type());
                    world.entities.remove(e.entityId());
                    world.removeAndDestroyDeadEntityNow(e);
                } else {
                    world.findEnemy(ees.type()).load(ees);
                }

                enemy++;
            } else if (entitySave.type().generic()) {
                final GenericEntitySave save = (GenericEntitySave) entitySave;
                world.findEntity(save.type()).loadSavedEntity(save);
            }
        }

        GameLogging.info(world.worldName, "Loaded %d interactable entities and %d enemies", interactable, enemy);
    }

    /**
     * Load all objects, interactable objects, and containers
     *
     * @param save save
     */
    private void loadContainersAndObjects(AbstractWorldSaveState save) {
        if (save.objects() == null) return;

        var interactable = 0;
        var normal = 0;

        for (AbstractWorldObjectSaveState object : save.objects()) {
            if (object.interactable()) {
                loadInteractableObject((InteractableWorldObjectSave) object);
                interactable++;
            } else {
                loadNormalWorldObject(object);
                normal++;
            }
        }
        GameLogging.info(world.worldName, "Loaded %d interactable objects and %d normal objects", interactable, normal);
    }

    /**
     * Load a default world object
     *
     * @param save save
     */
    private void loadNormalWorldObject(AbstractWorldObjectSaveState save) {
        if (save.destroyed()) {
            world.removeDestroyedSaveObject(save.key());
        }
    }

    /**
     * Load all interactable objects
     *
     * @param save save
     */
    private void loadInteractableObject(InteractableWorldObjectSave save) {
        if (save.type() == WorldInteractionType.CONTAINER) {
            loadContainer((ContainerWorldObjectSave) save);
        } else if (save.type() == WorldInteractionType.MAP_ITEM) {
            final ItemInteractionObjectSave drop = (ItemInteractionObjectSave) save;
            world.spawnWorldDrop(drop.item().type(), drop.item().amount(), drop.position());
        } else {
            final AbstractInteractableWorldObject object = world.findInteraction(save.type(), save.key());
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
            } else {
                GameLogging.warn(world.worldName, "Failed to find a container: %s", save.key());
            }
        }
    }

    /**
     * Load an interior
     *
     * @param save save world interior
     */
    private void loadInterior(InteriorWorldSave save) {
        final GameWorldInterior interior = world.findInteriorByType(save.interiorType());
        interior.loadWorld(true);
        interior.loader().load(save);
    }

    @Override
    public void dispose() {
        world = null;
    }
}
