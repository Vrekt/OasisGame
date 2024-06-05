package me.vrekt.oasis.save.world.obj.adapters;

import com.google.gson.*;
import me.vrekt.oasis.save.world.obj.DefaultWorldObjectSave;
import me.vrekt.oasis.save.world.obj.InteractableWorldObjectSave;
import me.vrekt.oasis.save.world.obj.AbstractWorldObjectSaveState;
import me.vrekt.oasis.save.world.obj.objects.ContainerWorldObjectSave;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

import java.lang.reflect.Type;

/**
 * Handles deserializing world object types.
 */
public final class WorldObjectAdapter implements JsonDeserializer<AbstractWorldObjectSaveState> {
    @Override
    public AbstractWorldObjectSaveState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject src = json.getAsJsonObject();

        final boolean interactable = src.get("interactable").getAsBoolean();
        if (!interactable) return context.deserialize(json, DefaultWorldObjectSave.class);

        final String type = src.get("type").getAsString();
        try {
            final WorldInteractionType of = WorldInteractionType.of(type);
            if (of == WorldInteractionType.CONTAINER) {
                return context.deserialize(json, ContainerWorldObjectSave.class);
            } else {
                return context.deserialize(json, InteractableWorldObjectSave.class);
            }
        } catch (IllegalArgumentException ex) {
            GameLogging.error(this, "Failed to find an interaction type: %s", ex, type);
        }

        return null;
    }
}
