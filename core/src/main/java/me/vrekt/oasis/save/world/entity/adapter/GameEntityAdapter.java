package me.vrekt.oasis.save.world.entity.adapter;

import com.google.gson.*;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.save.world.entity.AbstractEntitySaveState;
import me.vrekt.oasis.save.world.entity.EnemyEntitySave;
import me.vrekt.oasis.save.world.entity.InteractableEntitySave;

import java.lang.reflect.Type;

/**
 * Handles assigning a type to a deserialized entity
 */
public final class GameEntityAdapter implements JsonDeserializer<AbstractEntitySaveState> {
    @Override
    public AbstractEntitySaveState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject src = json.getAsJsonObject();
        final EntityType type = EntityType.valueOf(src.get("type").getAsString());
        if (type.interactable()) {
            return context.deserialize(json, InteractableEntitySave.class);
        } else if (type.enemy()) {
            return context.deserialize(json, EnemyEntitySave.class);
        } else if (type.generic()) {
            // TODO: Generic enemies
        }
        return null;
    }
}