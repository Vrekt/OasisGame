package me.vrekt.oasis.save.world;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.save.world.entity.EnemyEntitySave;
import me.vrekt.oasis.save.world.entity.GameEntitySave;
import me.vrekt.oasis.save.world.entity.InteractableEntitySave;
import me.vrekt.oasis.save.world.obj.ContainerWorldObjectSave;
import me.vrekt.oasis.save.world.obj.DefaultWorldObjectSave;
import me.vrekt.oasis.save.world.obj.InteractableWorldObjectSave;
import me.vrekt.oasis.save.world.obj.WorldObjectSave;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.obj.WorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * The data of a generic world or interior
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
    List<WorldObjectSave> objects;

    public WorldSave() {
    }

    public WorldSave(GameWorld world) {
        this.map = world.getWorldMap();
        this.name = world.getWorldName();
        this.interior = world.isInterior();

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
     * Write objects
     *
     * @param world world
     */
    protected void writeObjects(GameWorld world) {
        this.objects = new ArrayList<>();

        for (int i = 0; i < world.destroyedWorldObjects().size(); i++) {
            objects.add(new DefaultWorldObjectSave(world.destroyedWorldObjects().get(i)));
        }

        for (WorldObject object : world.worldObjects()) {
            final DefaultWorldObjectSave save = new DefaultWorldObjectSave(world, object);
            objects.add(save);
        }

        for (InteractableWorldObject object : world.interactableWorldObjects()) {
            if (object.getType() == WorldInteractionType.CONTAINER) {
                final ContainerWorldObjectSave container = new ContainerWorldObjectSave(world, object, ((OpenableContainerInteraction) object).inventory());
                objects.add(container);
            } else {
                final InteractableWorldObjectSave save = new InteractableWorldObjectSave(world, object);
                objects.add(save);
            }
        }
    }

    public String name() {
        return name;
    }

    public List<GameEntitySave> entities() {
        return entities;
    }

    public List<InteriorSave> interiors() {
        return interiors;
    }

    public List<WorldObjectSave> objects() {
        return objects;
    }

    public boolean interior() {
        return interior;
    }

    /**
     * Handle deserializing world saves
     */
    public static final class WorldSaveAdapter implements JsonDeserializer<WorldSave> {
        @Override
        public WorldSave deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject src = json.getAsJsonObject();
            final boolean interior = src.get("interior").getAsBoolean();
            if (interior) {
                return context.deserialize(json, InteriorSave.class);
            } else {
                return context.deserialize(json, DefaultWorldSave.class);
            }
        }
    }

}
