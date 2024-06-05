package me.vrekt.oasis.save.world.obj;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import me.vrekt.oasis.world.obj.WorldObject;

import java.lang.reflect.Type;

/**
 * Save a world object
 */
public abstract class WorldObjectSave {

    @Expose
    protected String key;

    @Expose
    protected boolean interactable;

    public WorldObjectSave(WorldObject object) {
        this.key = object.getKey();
        this.interactable = false;
    }

    public WorldObjectSave() {

    }

    public String key() {
        return key;
    }

    public boolean interactable() {
        return interactable;
    }

    /**
     * Handles the types of objects interactable or normal
     */
    public static final class WorldObjectSaveAdapter implements JsonDeserializer<WorldObjectSave> {
        @Override
        public WorldObjectSave deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject src = json.getAsJsonObject();
            if (src.get("interactable").getAsBoolean()) {
                return new InteractableWorldObjectSave();
            } else {
                return new DefaultWorldObjectSave();
            }
        }
    }

}
