package me.vrekt.oasis.save;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.StringUtils;

/**
 * Stores game save times
 */
public final class SaveGameTimes {

    @Expose
    private String slot1SaveTime;
    @Expose
    private String slot2SaveTime;
    @Expose
    private String slot3SaveTime;

    public boolean hasSaveTime(int slot) {
        switch (slot) {
            case 1:
                return slot1SaveTime != null;
            case 2:
                return slot2SaveTime != null;
            case 3:
                return slot3SaveTime != null;
        }
        return false;
    }

    public void setSaveTimeFor(int slot, String now) {
        switch (slot) {
            case 1:
                slot1SaveTime = now;
                break;
            case 2:
                slot2SaveTime = now;
                break;
            case 3:
                slot3SaveTime = now;
                break;
        }
    }

    public String getSaveTimeFor(int slot) {
        switch (slot) {
            case 1:
                return slot1SaveTime == null ? "Never" : slot1SaveTime;
            case 2:
                return slot2SaveTime == null ? "Never" : slot2SaveTime;
            case 3:
                return slot3SaveTime == null ? "Never" : slot3SaveTime;
        }
        return StringUtils.EMPTY;
    }

}
