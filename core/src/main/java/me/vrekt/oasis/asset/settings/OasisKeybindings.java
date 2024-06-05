package me.vrekt.oasis.asset.settings;

import com.badlogic.gdx.Input;
import me.vrekt.oasis.save.settings.GameSettingsSave;

public final class OasisKeybindings {

    public static int WALK_UP_KEY = Input.Keys.W;
    public static int WALK_DOWN_KEY = Input.Keys.S;
    public static int WALK_LEFT_KEY = Input.Keys.A;
    public static int WALK_RIGHT_KEY = Input.Keys.D;
    public static int QUEST_KEY = Input.Keys.Q;
    public static int INVENTORY_KEY = Input.Keys.I;
    public static int SKIP_DIALOG_KEY = Input.Keys.F;
    public static int ESCAPE = Input.Keys.ESCAPE;

    public static int ARTIFACT_ONE = Input.Keys.NUM_9;

    public static int DEBUG_MENU_KEY = Input.Keys.F2;

    public static int SLOT_1 = Input.Keys.NUM_1;
    public static int SLOT_2 = Input.Keys.NUM_2;
    public static int SLOT_3 = Input.Keys.NUM_3;
    public static int SLOT_4 = Input.Keys.NUM_4;
    public static int SLOT_5 = Input.Keys.NUM_5;
    public static int SLOT_6 = Input.Keys.NUM_6;

    public static int CHAT = Input.Keys.T;
    public static int MAP = Input.Keys.M;

    /**
     * Load keybindings from a save
     *
     * @param save save state
     */
    public static void fromSave(GameSettingsSave save) {
        QUEST_KEY = save.questKey();
        INVENTORY_KEY = save.inventoryKey();
        SKIP_DIALOG_KEY = save.skipDialogKey();
        CHAT = save.chatKey();
        MAP = save.mapKey();
    }

}
