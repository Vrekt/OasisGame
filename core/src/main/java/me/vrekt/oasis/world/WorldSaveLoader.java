package me.vrekt.oasis.world;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.vrekt.oasis.entity.Entities;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.save.Loadable;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.save.inventory.InventorySave;
import me.vrekt.oasis.save.world.AbstractWorldSaveState;
import me.vrekt.oasis.save.world.InteriorWorldSave;
import me.vrekt.oasis.save.world.entity.EntitySaveState;
import me.vrekt.oasis.save.world.mp.NetworkPlayerSave;
import me.vrekt.oasis.save.world.obj.WorldObjectSaveState;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.obj.DestroyedObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;

/**
 * Handles loading a world
 */
public final class WorldSaveLoader implements Loadable<AbstractWorldSaveState>, Disposable {

    private GameWorld world;

    private final Array<AbstractInteractableWorldObject> preLoadedObjects = new Array<>();
    private final Array<OpenableContainerInteraction> preLoadedContainers = new Array<>();

    public WorldSaveLoader(GameWorld world) {
        this.world = world;
    }

    @Override
    public void load(AbstractWorldSaveState worldSave, Gson gson) {
        loadWorldEntities(worldSave);
        loadNetworkPlayers(worldSave);
        loadContainersAndObjects(worldSave);

        // now add preloaded objects
        preLoadedObjects.forEach(object -> world.addInteraction(object));
        preLoadedObjects.clear();

        // add pre loaded containers
        for (OpenableContainerInteraction container : preLoadedContainers) {
            world.spawnWorldObject(container, container.activeTexture(), container.getPosition());
        }
        preLoadedContainers.clear();

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
        world.postLoad(worldSave);
    }

    /**
     * Load all entities
     *
     * @param worldSave save
     */
    private void loadWorldEntities(AbstractWorldSaveState worldSave) {
        if (worldSave.entities() == null) return;

        var interactableCount = 0;
        var enemyCount = 0;

        for (EntitySaveState save : worldSave.entities()) {
            if (save.type().interactable()) {
                EntityInteractable interactable = world.findInteractableEntity(save.type());
                if (interactable != null) {
                    interactable.load(save, SaveManager.LOAD_GAME_GSON);
                } else {
                    // no entity, create it.
                    interactable = Entities.interactable(save.key(), world, save.position(), world.game);
                    interactable.load(save, SaveManager.LOAD_GAME_GSON);
                    interactable.load(world.game.getAsset());

                    world.populateEntity(interactable);
                }

                interactableCount++;
            } else if (save.type().enemy()) {
                final EntityEnemy enemy = world.findEnemy(save.type());
                enemy.load(save, SaveManager.LOAD_GAME_GSON);

                // indicates this entity is dead.
                if (worldSave.deadEntities().contains(enemy.key())) {
                    world.entities.remove(enemy.entityId());
                    world.removeAndDestroyDeadEntityNow(enemy);
                }
            } else if (save.type().generic()) {
                world.findEntity(save.type()).loadSavedEntity(save);
            }
        }

        GameLogging.info(world.worldName, "Loaded %d interactable entities and %d enemies", interactableCount, enemyCount);
    }

    /**
     * Load network players
     *
     * @param save the save
     */
    private void loadNetworkPlayers(AbstractWorldSaveState save) {
        if (save.networkPlayers() == null) return;
        for (NetworkPlayerSave networkPlayer : save.networkPlayers()) {
            world.playerStorage().put(networkPlayer.name(), networkPlayer);
        }
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

        for (WorldObjectSaveState object : save.objects()) {
            if (object.interactable()) {
                loadInteractableObject(object);
                interactable++;
            } else {
                loadNormalWorldObject(object);
                normal++;
            }
        }

        // remove destroyed objects
        for (DestroyedObject object : save.destroyedObjects()) {
            world.removeDestroyedSaveObject(object);
        }

        GameLogging.info(world.worldName, "Loaded %d interactable objects and %d normal objects", interactable, normal);
    }

    /**
     * Load a default world object
     *
     * @param save save
     */
    private void loadNormalWorldObject(WorldObjectSaveState save) {
        // Currently un-used.
    }

    /**
     * Load all interactable objects
     *
     * @param save save
     */
    private void loadInteractableObject(WorldObjectSaveState save) {
        // find the interaction and load its data.
        final AbstractInteractableWorldObject object = world.findInteraction(save.type(), save.key());
        if (object == null) {
            // no object, just create it.
            createMissingObject(save, SaveManager.LOAD_GAME_GSON);
        } else {
            object.load(save, SaveManager.LOAD_GAME_GSON);

            if (save.enabled()) {
                object.enable();
            } else {
                object.disable();
            }
        }
    }

    /**
     * Create missing objects that are randomly generated, like loot grove objects.
     *
     * @param state the state
     */
    private void createMissingObject(WorldObjectSaveState state, Gson gson) {
        if (state.data() == null) {
            GameLogging.warn(this, "Cannot create missing object, data is missing!");
            return;
        }

        if (state.type() == WorldInteractionType.MAP_ITEM) {
            final JsonObject parent = state.data().getAsJsonObject("dropped_item");
            final Items typeOf = Items.valueOf(parent.get("type").getAsString());
            final int amount = parent.get("amount").getAsInt();
            preLoadedObjects.add(world.createWorldDrop(typeOf, amount, state.position()));
        } else if (state.type() == WorldInteractionType.CONTAINER) {
            final InventorySave fromJson = gson.fromJson(state.data().get("container_inventory"), InventorySave.class);
            final ContainerInventory inventory = (ContainerInventory) fromJson.inventory();

            // may cause future issues
            final String texture = state.data().get("active_texture").getAsString();

            final OpenableContainerInteraction interaction = new OpenableContainerInteraction(state.key(), inventory);
            interaction.setPosition(state.position().x, state.position().y);
            if (state.enabled()) {
                interaction.enable();
            } else {
                interaction.disable();
            }

            interaction.setActiveTexture(texture);
            preLoadedContainers.add(interaction);
        } else {
            GameLogging.warn(this, "Failed to find a pre-loaded interaction! type=%s, key=%s", state.type(), state.key());
        }
    }

    /**
     * Load an interior
     *
     * @param save save world interior
     */
    private void loadInterior(InteriorWorldSave save) {
        final GameWorldInterior interior = world.findInteriorByType(save.interiorType());
        interior.loadWorldTiledMap(true);
        interior.loader().load(save, SaveManager.LOAD_GAME_GSON);
    }

    private void loadLootGroves(AbstractWorldSaveState state) {
        // only after we load do we generate loot-groves, if not loaded.


    }

    @Override
    public void dispose() {
        world = null;
    }
}
