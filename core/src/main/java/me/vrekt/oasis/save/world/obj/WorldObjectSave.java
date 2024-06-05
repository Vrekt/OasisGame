package me.vrekt.oasis.save.world.obj;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.WorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

import java.lang.reflect.Type;

/**
 * Save a world object
 */
public abstract class WorldObjectSave {

    @Expose
    protected String key;

    @Expose
    protected boolean interactable;

    @Expose
    protected boolean destroyed;

    public WorldObjectSave(GameWorld world, WorldObject object) {
        this.key = object.getKey();
        this.interactable = false;
    }

    public WorldObjectSave(String destroyedKey) {
        this.key = destroyedKey;
        this.interactable = false;
        this.destroyed = true;
    }

    public WorldObjectSave() {

    }

    public String key() {
        return key;
    }

    public boolean interactable() {
        return interactable;
    }

    public boolean destroyed() {
        return destroyed;
    }

    /**
     * Handles the types of objects interactable or normal
     */
    public static final class WorldObjectSaveAdapter implements JsonDeserializer<WorldObjectSave> {
        @Override
        public WorldObjectSave deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject src = json.getAsJsonObject();

            final boolean interactable = src.get("interactable").getAsBoolean();
            if (!interactable) return context.deserialize(json, DefaultWorldObjectSave.class);

            final String type = src.get("type").getAsString();

            // TODO: Error catching
            final WorldInteractionType of = WorldInteractionType.of(type);
            if (of == WorldInteractionType.CONTAINER) {
                return context.deserialize(json, ContainerWorldObjectSave.class);
            } else {
                return context.deserialize(json, InteractableWorldObjectSave.class);
            }
        }
    }

}
