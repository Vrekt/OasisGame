package me.vrekt.oasis.save;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class GameSaveProperties {

    @Expose
    @SerializedName("slot_one")
    private GameSaveSlotProperty slot1;

    @Expose
    @SerializedName("slot_two")
    private GameSaveSlotProperty slot2;

    @Expose
    @SerializedName("slot_three")
    private GameSaveSlotProperty slot3;

    /**
     * Set properties of a slot
     *
     * @param slot the slot
     * @param save the save
     */
    public void setSlotProperty(int slot, GameSave save) {
        switch (slot) {
            case 1:
                slot1 = new GameSaveSlotProperty(save.name(), save.progress(), save.date(), save.isMultiplayer(), slot);
                break;
            case 2:
                slot2 = new GameSaveSlotProperty(save.name(), save.progress(), save.date(), save.isMultiplayer(), slot);
                break;
            case 3:
                slot3 = new GameSaveSlotProperty(save.name(), save.progress(), save.date(), save.isMultiplayer(), slot);
                break;
        }
    }

    public String getSlotName(int slot) {
        return switch (slot) {
            case 1 -> slot1.name();
            case 2 -> slot2.name();
            case 3 -> slot3.name();
            default -> StringUtils.EMPTY;
        };
    }

    public GameSaveSlotProperty getSaveSlotProperty(int slot) {
        return switch (slot) {
            case 1 -> slot1;
            case 2 -> slot2;
            case 3 -> slot3;
            default -> null;
        };
    }

    /**
     * Check if a save slot exists
     *
     * @param slot the slot
     * @return {@code true} if so
     */
    public boolean hasSaveSlot(int slot) {
        return switch (slot) {
            case 1 -> slot1 != null;
            case 2 -> slot2 != null;
            case 3 -> slot3 != null;
            default -> false;
        };
    }

    /**
     * @return {@code true} if there are any saves available
     */
    public boolean hasAnySaveSlots() {
        return slot1 != null || slot2 != null || slot3 != null;
    }

}
