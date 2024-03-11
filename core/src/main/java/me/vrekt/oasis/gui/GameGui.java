package me.vrekt.oasis.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.entity.npc.EntitySpeakable;
import me.vrekt.oasis.entity.player.mp.OasisNetworkPlayer;
import me.vrekt.oasis.gui.dialog.DialogGui;
import me.vrekt.oasis.gui.hints.HintGui;
import me.vrekt.oasis.gui.hud.HudGui;
import me.vrekt.oasis.gui.inventory.ContainerGui;
import me.vrekt.oasis.gui.inventory.InventoryGui;
import me.vrekt.oasis.gui.pause.PauseGui;
import me.vrekt.oasis.gui.quest.QuestingGui;
import me.vrekt.oasis.gui.save.SaveGameGui;
import me.vrekt.oasis.gui.select.ClassSelectorGui;
import me.vrekt.oasis.gui.settings.SettingsGui;
import me.vrekt.oasis.gui.utility.DebugMenuGui;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all GUI elements
 */
public final class GameGui {

    private final Stage stage;
    private final Stack stack;
    private final Skin skin;
    private final BitmapFont small, medium, large;
    private final GlyphLayout layout;
    private final OasisGame game;

    private final Map<GuiType, Gui> guis = new HashMap<>();
    private final InputMultiplexer multiplexer;

    private final Styles styles;

    public GameGui(OasisGame game, Asset asset, InputMultiplexer multiplexer) {
        this.stage = new Stage(new ScreenViewport());
        this.game = game;
        this.multiplexer = multiplexer;
        Table root = new Table();


        root.setFillParent(true);
        stage.addActor(root);


        stack = new Stack();
        stack.setFillParent(true);
        root.add(stack).grow();

        this.skin = asset.getDefaultLibgdxSkin();
        this.small = asset.getSmall();
        this.medium = asset.getMedium();
        this.large = asset.getLarge();
        this.layout = new GlyphLayout(medium, "");

        skin.add("smaller", asset.getSmaller());
        skin.add("small", small);
        skin.add("medium", medium);
        skin.add("large", large);

        this.styles = new Styles(asset);

        multiplexer.addProcessor(stage);
        guis.put(GuiType.DIALOG, new DialogGui(this, asset));
        guis.put(GuiType.CLASS, new ClassSelectorGui(this, asset));
        guis.put(GuiType.HUD, new HudGui(this, asset));
        guis.put(GuiType.QUEST, new QuestingGui(this, asset));
        guis.put(GuiType.INVENTORY, new InventoryGui(this, asset));
        guis.put(GuiType.PAUSE, new PauseGui(this, asset));
        guis.put(GuiType.SETTINGS, new SettingsGui(this, asset));
        guis.put(GuiType.CONTAINER, new ContainerGui(this, asset));
        guis.put(GuiType.SAVE_GAME, new SaveGameGui(this, asset));
        guis.put(GuiType.DEBUG_MENU, new DebugMenuGui(this, asset));
        guis.put(GuiType.HINT, new HintGui(this, asset));
    }

    public Styles getStyles() {
        return styles;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public Camera getCamera() {
        return stage.getCamera();
    }

    public Skin getSkin() {
        return skin;
    }

    public BitmapFont getSmall() {
        return small;
    }

    public BitmapFont getMedium() {
        return medium;
    }

    public BitmapFont getLarge() {
        return large;
    }

    public GlyphLayout getLayout() {
        return layout;
    }

    public OasisGame getGame() {
        return game;
    }

    public Stage getStage() {
        return stage;
    }

    public void updateDialogKeyPress() {
        ((DialogGui) getGui(GuiType.DIALOG)).updateKeyPress();
    }

    /**
     * Avoid changing cursors if any interface is open
     *
     * @return {@code  true} if so
     */
    public boolean isAnyInterfaceOpen() {
        return isGuiVisible(GuiType.CONTAINER)
                || isGuiVisible(GuiType.INVENTORY)
                || isGuiVisible(GuiType.QUEST)
                || isGuiVisible(GuiType.PAUSE)
                || isGuiVisible(GuiType.DIALOG)
                || isGuiVisible(GuiType.SETTINGS)
                || isGuiVisible(GuiType.SAVE_GAME)
                || isGuiVisible(GuiType.DEBUG_MENU);
    }

    /**
     * Show dialog for an entity
     *
     * @param entity the entity
     */
    public void showEntityDialog(EntitySpeakable entity) {
        ((DialogGui) getGui(GuiType.DIALOG)).setShowingDialog(entity);
        showGui(GuiType.DIALOG);
    }

    /**
     * Show a gui
     *
     * @param type the type
     */
    public void showGui(GuiType type) {
        guis.get(type).show();
    }

    /**
     * Hide any amount of GUIs
     *
     * @param type the type
     */
    public void hideGui(GuiType... type) {
        for (GuiType guiType : type) {
            guis.get(guiType).hide();
        }
    }

    /**
     * Toggle a gui
     *
     * @param gui the gui
     */
    public boolean toggleGui(GuiType gui) {
        if (isGuiVisible(gui)) {
            hideGui(gui);
            gui.showOtherGuis(this);
            return true;
        } else {
            showGui(gui);
            gui.hideOtherGuis(this);
            return false;
        }
    }

    /**
     * Check if a GUI is currently visible
     *
     * @param type the type
     * @return {@code true} if so
     */
    public boolean isGuiVisible(GuiType type) {
        return guis.get(type).isGuiVisible();
    }

    /**
     * Hide a gui and then show another
     *
     * @param hide the one to hide
     * @param show the one to show
     */
    public void hideThenShowGui(GuiType hide, GuiType show) {
        hideGui(hide);
        showGui(show);
    }

   /* public void showGuiType(GuiType type, GuiType hideOther) {
        if (isGuiVisible(type)) {
            hideGui(type);
        } else {
            hideGuiType(hideOther);
            showGui(type);
        }
    }

    public boolean hideGuiType(GuiType type) {
        if (isGuiVisible(type)) {
            hideGui(type);
            return true;
        }
        return false;
    }



    public void showGuiIfNotOpen(GuiType type) {
        if (!isGuiVisible(type)) showGui(type);
    }*/

    public <T extends Gui> T getGui(GuiType type) {
        return (T) guis.get(type);
    }

    public void showHud() {
        showGui(GuiType.HUD);
    }

    public void hideHud() {
        hideGui(GuiType.HUD);
    }

    public HudGui getHud() {
        return (HudGui) guis.get(GuiType.HUD);
    }

    public InventoryGui getInventoryGui() {
        return (InventoryGui) guis.get(GuiType.INVENTORY);
    }

    public void populateContainerGui(ContainerInventory inventory) {
        getContainerGui().populate(inventory);
    }

    public ContainerGui getContainerGui() {
        return (ContainerGui) guis.get(GuiType.CONTAINER);
    }

    /**
     * Render a players nametag
     *
     * @param player the player
     * @param camera the game camera
     * @param batch  batch to draw with
     */
    public void renderPlayerNametag(OasisNetworkPlayer player, Camera camera, Batch batch) {
        player.renderNametag(small, batch, camera, getCamera());
    }

    /**
     * Render all active GUI elements.
     */
    public void updateAndRender() {
        stage.getViewport().apply();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.getCamera().update();

        for (Gui gui : guis.values()) {
            if (gui.isGuiVisible()) gui.update();
        }
        stage.draw();

        // draw VisUIs
        for (Gui gui : guis.values()) {
            if (gui.isGuiVisible()) gui.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getCamera().update();

        for (Gui gui : guis.values()) gui.resize(width, height);
    }

    /**
     * Create a container inside the stack.
     *
     * @param actor the actor
     * @param <T>   type
     * @return the container type
     */
    public <T extends Actor> Container<T> createContainer(T actor) {
        final Container<T> container = new Container<>(actor);
        stack.addActor(container);
        return container;
    }
}
