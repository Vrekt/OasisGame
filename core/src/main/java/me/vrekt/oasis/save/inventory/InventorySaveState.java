package me.vrekt.oasis.save.inventory;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.inventory.BasicInventory;
import me.vrekt.oasis.entity.inventory.InventoryType;
import me.vrekt.oasis.entity.player.sp.inventory.PlayerInventory;
import me.vrekt.oasis.save.keys.HasInventory;
import me.vrekt.oasis.utility.logging.Logging;

import java.lang.reflect.Type;

/**
 * Represents the players inventory in the game save
 */
public class InventorySaveState implements HasInventory {

    @Expose
    private BasicInventory inventory;

    public InventorySaveState(BasicInventory inventory) {
        this.inventory = inventory;
    }

    private InventorySaveState() {

    }

    @Override
    public BasicInventory getInventory() {
        return inventory;
    }

    /**
     * Adapt this inventory to JSON
     */
    public static final class InventorySaver implements JsonSerializer<InventorySaveState> {
        @Override
        public JsonElement serialize(InventorySaveState src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject base = new JsonObject();
            final JsonArray items = new JsonArray();
            src.inventory.getSlots().forEach((index, slot) -> {
                final JsonObject object = new JsonObject();
                object.addProperty("slotNumber", index);
                object.addProperty("itemName", slot.getItem().getItemName());
                object.addProperty("itemId", slot.getItem().getItemId());
                object.addProperty("amount", slot.getItem().getAmount());

                items.add(object);
            });

            base.addProperty("size", src.inventory.getInventorySize());
            base.addProperty("type", src.inventory.getType().name());
            base.add("items", items);

            return base;
        }
    }

    /**
     * Load a player inventory from a save game
     */
    public static final class InventoryLoader implements JsonDeserializer<InventorySaveState> {
        @Override
        public InventorySaveState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final InventorySaveState save = new InventorySaveState();
            final JsonObject src = json.getAsJsonObject();

            final int size = src.get("size").getAsInt();
            final String typeStr = src.get("type").getAsString();

            // Ensure we have a proper type of inventory to parse
            BasicInventory inventory = null;
            try {
                final InventoryType type = InventoryType.valueOf(typeStr);
                if (type == InventoryType.PLAYER) {
                    final PlayerInventory playerInventory = new PlayerInventory();
                    playerInventory.setSize(size);
                    inventory = playerInventory;
                } else if (type == InventoryType.CONTAINER) {
                    // TODO
                } else {
                    Logging.warn("SaveSystem", "Unknown inventory type was not handled! (" + typeStr + ")");
                }
            } catch (IllegalArgumentException notFound) {
                Logging.error("SaveSystem", "Failed to find an inventory by type: " + typeStr + ", ignoring.");
            }

            // load items from inventory JSON
            if (inventory != null) {
                save.inventory = inventory;

                final JsonArray itemsArray = src.getAsJsonArray("items");
                for (JsonElement element : itemsArray) {
                    final JsonObject base = element.getAsJsonObject();
                    final int slot = base.get("slotNumber").getAsInt();
                    final String itemName = base.get("itemName").getAsString();
                    final int itemId = base.get("itemId").getAsInt();
                    final int amount = base.get("amount").getAsInt();
                    inventory.addItemFromSave(slot, itemName, itemId, amount);
                }
            }

            // parse inventory type
            return save;
        }
    }

}
