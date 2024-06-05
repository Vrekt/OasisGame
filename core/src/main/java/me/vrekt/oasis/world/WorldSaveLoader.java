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
            GameLogging.info(world.worldName, "Loading interior WorldSave %s", worldSave.name());

            interior.setEnterable(save.enterable());
            world.getGame().getWorldManager().setParentWorldPosition(save.enteredPosition());
        } else {
            for (InteriorSave interior : worldSave.interiors()) {
                loadInterior(interior);
            }
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
        GameLogging.info(world.worldName, "Loaded %d interactable entities and %d enemies", interactable, enemy);
    }

    /**
     * Load all objects, interactable objects, and containers
     *
     * @param save save
     */
    private void loadContainersAndObjects(WorldSave save) {
        GameLogging.info(this, "HELLO22132 %s", world.worldName);
        if (save.objects() == null) return;

        var interactable = 0;
        var normal = 0;

        GameLogging.info(this, "HELLO 1 %s", world.worldName);

        for (WorldObjectSave object : save.objects()) {
            GameLogging.info(this, "object: %s in world %s is %b", object.key(), world.worldName, object.destroyed());
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
    private void loadNormalWorldObject(WorldObjectSave save) {
        if (save.destroyed()) {
            GameLogging.info(world.worldName, "Destroyed object %s", save.key());
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

                GameLogging.info(world.worldName, "Loaded container: %s", save.key());
            } else {
                GameLogging.warn(world.worldName, "Failed to find a container: %s", save.key());
            }
        }
    }

    private void loadInterior(InteriorSave save) {
        System.err.println(world.worldName + ":" + save.interiorType());
        final GameWorldInterior interior = world.findInteriorByType(save.interiorType());
        interior.loadWorld(true);
        interior.loader().load(save);
    }

    @Override
    public void dispose() {
        world = null;
    }
}
