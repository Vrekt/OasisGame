package me.vrekt.oasis.save.world;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.save.world.entity.AbstractEntitySaveState;
import me.vrekt.oasis.save.world.entity.EnemyEntitySave;
import me.vrekt.oasis.save.world.entity.GenericEntitySave;
import me.vrekt.oasis.save.world.entity.InteractableEntitySave;
import me.vrekt.oasis.save.world.mp.NetworkPlayerSave;
import me.vrekt.oasis.save.world.obj.AbstractWorldObjectSaveState;
import me.vrekt.oasis.save.world.obj.DefaultWorldObjectSave;
import me.vrekt.oasis.save.world.obj.InteractableWorldObjectSave;
import me.vrekt.oasis.save.world.obj.objects.ContainerWorldObjectSave;
import me.vrekt.oasis.save.world.obj.objects.ItemInteractionObjectSave;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;
import me.vrekt.oasis.world.obj.interaction.impl.items.MapItemInteraction;

import java.util.ArrayList;
import java.util.List;

/**
 * The data of a generic world or interior
 */
public abstract class AbstractWorldSaveState {

    @Expose
    protected String map;
    @Expose
    protected int worldId;
    @Expose
    protected boolean interior;
    @Expose
    List<AbstractEntitySaveState> entities = new ArrayList<>();
    @Expose
    List<InteriorWorldSave> interiors;
    @Expose
    List<AbstractWorldObjectSaveState> objects;
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

        writeEntities(world);
        writeInteriors(world);
        writeObjects(world);
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
    }

    /**
     * Write all entities within the world
     *
     * @param world the world
     */
    protected void writeEntities(GameWorld world) {
        for (GameEntity entity : world.entities().values()) {
            if (entity.type().interactable()) {
                entities.add(new InteractableEntitySave(entity.asInteractable()));
            } else if (entity.type().enemy()) {
                entities.add(new EnemyEntitySave(entity.asEnemy()));
            } else if (entity.type().generic()) {
                entities.add(new GenericEntitySave(entity));
            }
        }

        // save dead enemies
        for (int i = 0; i < world.deadEnemies().size(); i++) {
            final EntityType type = world.deadEnemies().get(i);
            entities.add(new EnemyEntitySave(type));
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
            // != null fixes EM-91
            if (excludedInteriorId == -1) {
                // debugging purposes, remove later 6/12/24
                GameLogging.warn(this, "Excluded was null, debugging: interior=%s, caller=%s", interior.type(), world.getWorldName());
            }

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

        for (int i = 0; i < world.destroyedWorldObjects().size(); i++) {
            objects.add(new DefaultWorldObjectSave(world.destroyedWorldObjects().get(i)));
        }

        for (AbstractWorldObject object : world.worldObjects()) {
            final DefaultWorldObjectSave save = new DefaultWorldObjectSave(object);
            objects.add(save);
        }

        for (AbstractInteractableWorldObject object : world.interactableWorldObjects()) {
            if (object.getType() == WorldInteractionType.CONTAINER) {
                final ContainerWorldObjectSave container = new ContainerWorldObjectSave(world, object, ((OpenableContainerInteraction) object).inventory());
                objects.add(container);
            } else if (object.getType() == WorldInteractionType.MAP_ITEM) {
                final ItemInteractionObjectSave itemDrop = new ItemInteractionObjectSave((MapItemInteraction) object);
                objects.add(itemDrop);
            } else {
                final InteractableWorldObjectSave save = new InteractableWorldObjectSave(world, object);
                objects.add(save);
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
     * @return entities
     */
    public List<AbstractEntitySaveState> entities() {
        return entities;
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
    public List<AbstractWorldObjectSaveState> objects() {
        return objects;
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

}
