package me.vrekt.oasis.world.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;

import java.util.function.Supplier;

/**
 * Handles keys being pressed.
 */
public final class Keybindings {

    private static final IntMap<Runnable> KEYS = new IntMap<>();

    /**
     * @return gui manager, convenience method.
     */
    private static GuiManager gui() {
        return GameManager.gui();
    }

    /**
     * @return game, convenience method.
     */
    private static OasisGame game() {
        return GameManager.game();
    }

    /**
     * Register all key actions
     */
    public static void initialize() {
        registerHotBarKeys();
        registerKeyAction(OasisKeybindings.INVENTORY_KEY, GuiType.INVENTORY);
        registerKeyAction(OasisKeybindings.QUEST_KEY, GuiType.QUEST);
        registerKeyActionTask(Input.Keys.F4, () -> Gdx.app.exit());
        // only toggle chat if it's a multiplayer game.
        registerKeyActionWithPredicate(OasisKeybindings.CHAT, GuiType.CHAT, () -> !GameManager.game().isSingleplayerGame());

        // will toggle box2d debug and stage debugging.
        registerKeyActionTask(OasisKeybindings.DEBUG_MENU_KEY, () -> {
            OasisGameSettings.DRAW_DEBUG = !OasisGameSettings.DRAW_DEBUG;
            gui().toggleDrawDebug();
            game().player().getWorldState().entities().values().forEach(GameEntity::debug);
        });

        registerKeyActionTask(OasisKeybindings.SKIP_DIALOG_KEY, () -> GameManager.player().handleDialogKeyPress());
        registerKeyActionTask(OasisKeybindings.ARTIFACT_ONE, () -> GameManager.player().activateArtifact(0));
    }

    /**
     * Register a key action, will toggle a GUI
     *
     * @param key        key
     * @param toggleable the type of gui
     */
    private static void registerKeyAction(int key, GuiType toggleable) {
        KEYS.put(key, () -> gui().toggleGui(toggleable));
    }

    /**
     * Register a key action, will toggle a GUI
     *
     * @param key        key
     * @param toggleable the type of gui
     */
    private static void registerKeyActionWithPredicate(int key, GuiType toggleable, Supplier<Boolean> predicate) {
        KEYS.put(key, () -> {
            if (predicate.get()) {
                gui().toggleGui(toggleable);
            }
        });
    }

    /**
     * Register a task associated with a key
     *
     * @param key    the key
     * @param action the action
     */
    private static void registerKeyActionTask(int key, Runnable action) {
        KEYS.put(key, action);
    }

    /**
     * Register all hotbar keys.
     */
    private static void registerHotBarKeys() {
        KEYS.put(OasisKeybindings.SLOT_1, () -> gui().getHotbarComponent().hotbarItemSelected(0));
        KEYS.put(OasisKeybindings.SLOT_2, () -> gui().getHotbarComponent().hotbarItemSelected(1));
        KEYS.put(OasisKeybindings.SLOT_3, () -> gui().getHotbarComponent().hotbarItemSelected(2));
        KEYS.put(OasisKeybindings.SLOT_4, () -> gui().getHotbarComponent().hotbarItemSelected(3));
        KEYS.put(OasisKeybindings.SLOT_5, () -> gui().getHotbarComponent().hotbarItemSelected(4));
        KEYS.put(OasisKeybindings.SLOT_6, () -> gui().getHotbarComponent().hotbarItemSelected(5));
    }

    /**
     * Handle a world key press.
     *
     * @param world   world
     * @param keycode the key
     */
    public static void handleKeyInWorld(GameWorld world, int keycode) {
        if (keycode == OasisKeybindings.ESCAPE) {
            final boolean canPause = game().isSingleplayerGame();
            if (!canPause) {
                // game cannot be paused in a multiplayer game, only GUI.
                gui().toggleGui(GuiType.PAUSE);
                return;
            } else {
                // otherwise, handle pause resume of the active world.
                if (world.isPaused() && gui().isGuiVisible(GuiType.PAUSE)) {
                    gui().hideGui(GuiType.PAUSE);
                    world.resume();
                    return;
                } else if (!world.isPaused() && !gui().isAnyGuiVisible(GuiType.HUD)) {
                    // world is not paused, escape was pressed, and NO gui open, obviously pause
                    gui().showGui(GuiType.PAUSE);
                    world.pause();
                    return;
                }
            }

            // unknown key press, log for debug purposes
            if (!gui().hideOrShowParentGuis())
                GameLogging.warn("GameManagerKeyPress", "Unhandled escape key press, what were you doing?");
        }

        // handle individual key presses now
        handleKey(keycode);
    }

    /**
     * Handle key press, delegate to list of actions
     *
     * @param key key
     */
    public static void handleKey(int key) {
        if (KEYS.containsKey(key)) KEYS.get(key).run();
    }

}
