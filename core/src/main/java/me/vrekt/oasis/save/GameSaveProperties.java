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
                slot1 = new GameSaveSlotProperty(save.getName(), save.getProgress(), save.getDate(), slot);
                break;
            case 2:
                slot2 = new GameSaveSlotProperty(save.getName(), save.getProgress(), save.getDate(), slot);
                break;
            case 3:
                slot3 = new GameSaveSlotProperty(save.getName(), save.getProgress(), save.getDate(), slot);
                break;
        }
    }

    public String getSlotName(int slot) {
        switch (slot) {
            case 1:
                return slot1.getName();
            case 2:
                return slot2.getName();
            case 3:
                return slot3.getName();
        }
        return StringUtils.EMPTY;
    }

    public GameSaveSlotProperty getSaveSlotProperty(int slot) {
        switch (slot) {
            case 1:
                return slot1;
            case 2:
                return slot2;
            case 3:
                return slot3;
        }
        return null;
    }

}