package me.vrekt.oasis.save.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.settings.OasisKeybindings;

/**
 * Saves keybindings and game settings
 * TODO: slots and artifact keys
 */
public final class GameSettingsSave {

    @Expose
    @SerializedName("entity_update_distance")
    private float entityUpdateDistance;

    @Expose
    @SerializedName("show_fps")
    private boolean showFps;

    @Expose
    @SerializedName("mp_lan")
    private boolean multiplayerLan;

    @Expose
    @SerializedName("v_sync")
    private boolean vsync;

    @Expose
    @SerializedName("keybinding_quest")
    private int questKey;

    @Expose
    @SerializedName("keybinding_inventory")
    private int inventoryKey;

    @Expose
    @SerializedName("keybinding_skip_dialog")
    private int skipDialogKey;

    @Expose
    @SerializedName("keybinding_chat")
    private int chatKey;

    @Expose
    @SerializedName("keybinding_map")
    private int mapKey;

    public GameSettingsSave() {
        this.entityUpdateDistance = OasisGameSettings.ENTITY_UPDATE_DISTANCE;
        this.showFps = OasisGameSettings.SHOW_FPS;
        this.multiplayerLan = OasisGameSettings.ENABLE_MP_LAN;
        this.vsync = OasisGameSettings.V_SYNC;
        this.questKey = OasisKeybindings.QUEST_KEY;
        this.inventoryKey = OasisKeybindings.INVENTORY_KEY;
        this.skipDialogKey = OasisKeybindings.SKIP_DIALOG_KEY;
        this.chatKey = OasisKeybindings.CHAT;
        this.mapKey = OasisKeybindings.MAP;
    }

    public float entityUpdateDistance() {
        return entityUpdateDistance;
    }

    public boolean showFps() {
        return showFps;
    }

    public boolean multiplayerLan() {
        return multiplayerLan;
    }

    public boolean vsync() {
        return vsync;
    }

    public int questKey() {
        return questKey;
    }

    public int inventoryKey() {
        return inventoryKey;
    }

    public int skipDialogKey() {
        return skipDialogKey;
    }

    public int chatKey() {
        return chatKey;
    }

    public int mapKey() {
        return mapKey;
    }
}
