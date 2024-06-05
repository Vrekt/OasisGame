package me.vrekt.oasis.save.world.entity.adapter;

import com.google.gson.*;
import me.vrekt.oasis.save.world.entity.EnemyEntitySave;
import me.vrekt.oasis.save.world.entity.AbstractEntitySaveState;
import me.vrekt.oasis.save.world.entity.InteractableEntitySave;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

/**
 * Handles assigning a type to a deserialized entity
 */
public final class GameEntityAdapter implements JsonDeserializer<AbstractEntitySaveState> {
    @Override
    public AbstractEntitySaveState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject src = json.getAsJsonObject();
        final String is = src.get("is").getAsString();
        if (StringUtils.equals(is, "interactable")) {
            return context.deserialize(json, InteractableEntitySave.class);
        } else {
            return context.deserialize(json, EnemyEntitySave.class);
        }
    }
}