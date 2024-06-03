package me.vrekt.oasis.save.world;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.save.world.container.WorldContainerSave;
import me.vrekt.oasis.save.world.entity.EnemyEntitySave;
import me.vrekt.oasis.save.world.entity.GameEntitySave;
import me.vrekt.oasis.save.world.entity.InteractableEntitySave;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;

import java.util.ArrayList;
import java.util.List;

/**
 * The data of a generic world or interior
 *
 * TODO: World objects
 */
public abstract class WorldSave {

    @Expose
    protected String map;
    @Expose
    protected String name;
    @Expose
    protected boolean interior;
    @Expose
    List<GameEntitySave> entities = new ArrayList<>();
    @Expose
    List<InteriorSave> interiors;
    @Expose
    List<WorldContainerSave> containers;

    public WorldSave() {
    }

    public WorldSave(GameWorld world) {
        this.map = world.getWorldMap();
        this.name = world.getWorldName();
        this.interior = world.isInterior();

        writeEntities(world);
        writeInteriors(world);
        writeContainers(world);
    }

    /**
     * Write all entities within the world
     *
     * @param world the world
     */
    protected void writeEntities(GameWorld world) {
        for (GameEntity entity : world.entities().values()) {
            if (entity.isInteractable()) {
                entities.add(new InteractableEntitySave(entity.asInteractable()));
            } else if (entity.isEnemy()) {
                entities.add(new EnemyEntitySave(entity.asEnemy()));
            }
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
            if (interior.isWorldLoaded()) {
                interiors.add(new InteriorSave(interior));
            }
        }
    }

    /**
     * Write all containers and their contents
     *
     * @param world world
     */
    protected void writeContainers(GameWorld world) {
        this.containers = new ArrayList<>();

        for (InteractableWorldObject object : world.interactableWorldObjects()) {
            if (object.getType() == WorldInteractionType.CONTAINER) {
                final WorldContainerSave save = new WorldContainerSave((OpenableContainerInteraction) object);
                containers.add(save);
            }
        }
    }

}
