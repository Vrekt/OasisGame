package me.vrekt.oasis.save.world;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.enemy.EntityEnemyType;
import me.vrekt.oasis.save.world.entity.AbstractEntitySaveState;
import me.vrekt.oasis.save.world.entity.EnemyEntitySave;
import me.vrekt.oasis.save.world.entity.InteractableEntitySave;
import me.vrekt.oasis.save.world.obj.AbstractWorldObjectSaveState;
import me.vrekt.oasis.save.world.obj.DefaultWorldObjectSave;
import me.vrekt.oasis.save.world.obj.InteractableWorldObjectSave;
import me.vrekt.oasis.save.world.obj.objects.ContainerWorldObjectSave;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;

import java.util.ArrayList;
import java.util.List;

/**
 * The data of a generic world or interior
 */
public abstract class AbstractWorldSaveState {

    @Expose
    protected String map;
    @Expose
    protected String name;
    @Expose
    protected boolean interior;
    @Expose
    List<AbstractEntitySaveState> entities = new ArrayList<>();
    @Expose
    List<InteriorWorldSave> interiors;
    @Expose
    List<AbstractWorldObjectSaveState> objects;

    // the interior to exclude when writing this save
    private transient final String excludeInteriorName;

    public AbstractWorldSaveState() {
        this.excludeInteriorName = null;
    }

    public AbstractWorldSaveState(GameWorld world, String excludeInteriorName) {
        this.map = world.getWorldMap();
        this.name = world.getWorldName();
        this.interior = world.isInterior();
        this.excludeInteriorName = excludeInteriorName;

        writeEntities(world);
        writeInteriors(world);
        writeObjects(world);
    }

    public AbstractWorldSaveState(GameWorld world) {
        this.map = world.getWorldMap();
        this.name = world.getWorldName();
        this.interior = world.isInterior();
        this.excludeInteriorName = null;

        writeEntities(world);
        writeInteriors(world);
        writeObjects(world);
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

        // save dead enemies
        for (int i = 0; i < world.deadEnemies().size(); i++) {
            final EntityEnemyType type = world.deadEnemies().get(i);
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
            if (interior.isWorldLoaded() && !excludeInteriorName.equals(interior.getWorldName())) {
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
            } else {
                final InteractableWorldObjectSave save = new InteractableWorldObjectSave(world, object);
                objects.add(save);
            }
        }
    }

    /**
     * @return name
     */
    public String name() {
        return name;
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
     * @return if this world is an interior
     */
    public boolean interior() {
        return interior;
    }

}
