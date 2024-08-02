package me.vrekt.oasis.save.world;

import com.badlogic.gdx.utils.Disposable;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.save.world.entity.EntitySaveState;
import me.vrekt.oasis.save.world.mp.NetworkPlayerSave;
import me.vrekt.oasis.save.world.obj.WorldObjectSaveState;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.DestroyedObject;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The data of a generic world or interior
 */
public abstract class AbstractWorldSaveState implements Disposable {

    @Expose
    protected String map;
    @Expose
    protected int worldId;
    @Expose
    protected boolean interior;
    @Expose
    protected int parentWorld = -1;
    @Expose
    List<EntitySaveState> entities = new ArrayList<>();
    @Expose
    List<String> deadEntities = new ArrayList<>();
    @Expose
    List<InteriorWorldSave> interiors;
    @Expose
    List<WorldObjectSaveState> objects;
    @Expose
    List<DestroyedObject> destroyedObjects;
    @Expose
    List<String> lootGroveParents;
    @Expose
    @SerializedName("network_players")
    List<NetworkPlayerSave> networkPlayers;

    // the interior to exclude when writing this save
    private transient final int excludedInteriorId;

    public AbstractWorldSaveState() {
        this.excludedInteriorId = -1;
    }

    public AbstractWorldSaveState(GameWorld world, int excludedInteriorId) {
        this.map = world.getWorldMap();
        this.worldId = world.worldId();
        this.interior = world.isInterior();
        this.excludedInteriorId = excludedInteriorId;
        if (interior) {
            this.parentWorld = ((GameWorldInterior) world).getParentWorld().worldId();
        }

        writeEntities(world);
        writeInteriors(world);
        writeObjects(world);

        // write loot-grove parents saved
        this.lootGroveParents = new ArrayList<>();
        for (int i = 0; i < world.lootGroveParents().size; i++) {
            lootGroveParents.add(world.lootGroveParents().get(i));
        }
    }

    public AbstractWorldSaveState(GameWorld world) {
        this.map = world.getWorldMap();
        this.worldId = world.worldId();
        this.interior = world.isInterior();
        this.excludedInteriorId = -1;

        writeEntities(world);
        writeInteriors(world);
        writeObjects(world);
        if (GameManager.game().isLocalMultiplayer()) {
            writePlayers(world);
        }

        // write loot-grove parents saved
        this.lootGroveParents = new ArrayList<>();
        for (int i = 0; i < world.lootGroveParents().size; i++) {
            lootGroveParents.add(world.lootGroveParents().get(i));
        }

    }

    /**
     * Write all entities within the world
     *
     * @param world the world
     */
    protected void writeEntities(GameWorld world) {
        for (GameEntity entity : world.entities().values()) {
            final JsonObject data = new JsonObject();

            if (entity.type().interactable()) {
                entities.add(entity.asInteractable().save(data, SaveManager.SAVE_GAME_GSON));
            } else if (entity.type().enemy()) {
                entities.add(entity.asEnemy().save(data, SaveManager.SAVE_GAME_GSON));
            } else if (entity.type().generic()) {
                entities.add(new EntitySaveState(entity));
            }
        }

        // save dead enemies, they were removed from entity list long ago.
        for (String key : world.deadEnemies()) {
            deadEntities.add(key);
        }
    }

    /**
     * Write all interiors within the world
     *
     * @param world the world
     */
    private void writeInteriors(GameWorld world) {
        this.interiors = new ArrayList<>();

        // TODO: World will be unloaded, thus losing everything inside
        // TODO: Ideally, save it to a file before unloading
        // TODO: So then it can be reloaded from disk
        for (GameWorldInterior interior : world.interiorWorlds().values()) {
            if (interior.isWorldLoaded() && excludedInteriorId != interior.worldId()) {
                interiors.add(new InteriorWorldSave(interior));
            }
        }
    }

    /**
     * Write objects
     *
     * @param world world
     */
    protected void writeObjects(GameWorld world) {
        this.objects = new ArrayList<>();
        this.destroyedObjects = new ArrayList<>();

        // save any destroyed objects that will be removed upon loading
        for (int i = 0; i < world.destroyedWorldObjects().size(); i++) {
            destroyedObjects.add(world.destroyedWorldObjects().get(i));
        }


        // write any regular objects that have no functionality
        for (AbstractWorldObject object : world.worldObjects()) {
            objects.add(new WorldObjectSaveState(object));
        }

        // finally save all other objects that have interactions
        for (AbstractInteractableWorldObject object : world.interactableWorldObjects().values()) {
            if (!object.shouldSave()) continue;

            if (object.hasSaveSerialization()) {
                final JsonObject data = new JsonObject();
                objects.add(object.save(data, SaveManager.SAVE_GAME_GSON));
            } else {
                // otherwise, the object is a normal object with no extra data.
                objects.add(new WorldObjectSaveState(world, object));
            }
        }
    }

    /**
     * Write MP players
     *
     * @param world world
     */
    protected void writePlayers(GameWorld world) {
        for (NetworkPlayer player : world.players().values()) {
            if (networkPlayers == null) networkPlayers = new ArrayList<>();
            networkPlayers.add(new NetworkPlayerSave(player));
        }
    }

    /**
     * @return id
     */
    public int worldId() {
        return worldId;
    }

    /**
     * @return parent world if any
     */
    public int parentWorld() {
        return parentWorld;
    }

    /**
     * @return entities
     */
    public List<EntitySaveState> entities() {
        return entities;
    }

    /**
     * @return a keyed list of dead entities.
     */
    public List<String> deadEntities() {
        return deadEntities;
    }

    /**
     * @return interiors
     */
    public List<InteriorWorldSave> interiors() {
        return interiors;
    }

    /**
     * @return objects
     */
    public List<WorldObjectSaveState> objects() {
        return objects;
    }

    /**
     * @return destroyed objects
     */
    public List<DestroyedObject> destroyedObjects() {
        return destroyedObjects;
    }

    /**
     * @return all loot groves generated
     */
    public List<String> lootGroveParents() {
        return lootGroveParents;
    }

    /**
     * @return all network players
     */
    public List<NetworkPlayerSave> networkPlayers() {
        return networkPlayers;
    }

    /**
     * @return if this world is an interior
     */
    public boolean interior() {
        return interior;
    }

    @Override
    public void dispose() {
        entities.forEach(EntitySaveState::dispose);
        entities.clear();
        entities = null;
        deadEntities.clear();
        deadEntities = null;

        if (interiors != null) {
            interiors.forEach(AbstractWorldSaveState::dispose);
            interiors.clear();
            interiors = null;
        }

        if (objects != null) {
            objects.forEach(WorldObjectSaveState::dispose);
            objects.clear();
            objects = null;
        }

        if (destroyedObjects != null) {
            destroyedObjects.clear();
            destroyedObjects = null;
        }

        if (lootGroveParents != null) {
            lootGroveParents.clear();
            lootGroveParents = null;
        }

        if (networkPlayers != null) {
            networkPlayers.forEach(NetworkPlayerSave::dispose);
            networkPlayers.clear();
            networkPlayers = null;
        }
    }
}
