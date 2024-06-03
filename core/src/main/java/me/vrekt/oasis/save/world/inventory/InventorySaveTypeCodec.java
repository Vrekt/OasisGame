package me.vrekt.oasis.save.world.inventory;

import com.badlogic.gdx.utils.IntMap;
import com.google.gson.*;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.entity.inventory.InventoryType;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.entity.player.sp.inventory.PlayerInventory;
import me.vrekt.oasis.item.Item;

import java.lang.reflect.Type;

/**
 * Adapters for {@link InventorySave}
 */
public final class InventorySaveTypeCodec {

    /**
     * Adapt this inventory to JSON
     */
    public static final class InventoryPropertiesSerializer implements JsonSerializer<InventorySave> {
        @Override
        public JsonElement serialize(InventorySave src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject base = new JsonObject();
            final JsonArray items = new JsonArray();

            base.addProperty("type", src.inventory.type().name());
            base.addProperty("size", src.inventory.getSize());

            for (IntMap.Entry<Item> entry : src.inventory.items()) {
                items.add(context.serialize(new ItemSave(entry.key, entry.value)));
            }

            base.add("contents", items);
            return base;
        }
    }

    /**
     * Load a player inventory from a save game
     */
    public static final class InventoryPropertiesDeserializer implements JsonDeserializer<InventorySave> {
        @Override
        public InventorySave deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final InventorySave save = new InventorySave();
            final JsonObject src = json.getAsJsonObject();

            final InventoryType type = InventoryType.valueOf(src.get("type").getAsString());
            final int size = src.get("size").getAsInt();

            final AbstractInventory inventory = (type == InventoryType.PLAYER) ? new PlayerInventory() : new ContainerInventory(size);
            save.inventory = inventory;

            final JsonArray contents = src.getAsJsonArray("contents");
            for (JsonElement element : contents) {
                final ItemSave item = context.deserialize(element, ItemSave.class);
                inventory.putSavedItem(item.type, item.name, item.slot, item.amount);
            }

            return save;
        }
    }

}
