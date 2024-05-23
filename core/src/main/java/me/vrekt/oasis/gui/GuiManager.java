package me.vrekt.oasis.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.gui.guis.dialog.EntityDialogGui;
import me.vrekt.oasis.gui.guis.hud.GameHudGui;
import me.vrekt.oasis.gui.guis.inventory.ContainerInventoryGui;
import me.vrekt.oasis.gui.guis.inventory.PlayerInventoryGui;
import me.vrekt.oasis.gui.guis.quest.QuestEntryGui;
import me.vrekt.oasis.gui.guis.quest.QuestGui;
import me.vrekt.oasis.gui.guis.sign.ReadableSignGui;
import me.vrekt.oasis.gui.windows.PauseWindowGui;
import me.vrekt.oasis.gui.windows.SaveGameWindowGui;
import me.vrekt.oasis.gui.windows.SettingsWindowGui;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles most functions related to guis
 */
public class GuiManager {

    private final OasisGame game;
    private final Asset asset;
    private final Skin skin;
    private final Styles styles;

    private final Stage stage;
    private final Stack stack;
    private final Map<GuiType, Gui> guis = new HashMap<>();

    // commonly used GUIs
    private final GameHudGui hudGui;
    private final PlayerInventoryGui inventoryGui;
    private final EntityDialogGui dialogGui;
    private final ReadableSignGui signGui;
    private final ContainerInventoryGui containerGui;

    private Cursor cursorState;
    private boolean wasCursorChanged;

    public GuiManager(OasisGame game, Asset asset, InputMultiplexer multiplexer) {
        this.game = game;
        this.asset = asset;
        this.skin = asset.getDefaultLibgdxSkin();
        this.styles = game.getStyle();

        // fit this stage to always respect the general constraints we want
        stage = new Stage(new FitViewport(640, 480));
        stack = new Stack();
        stack.setFillParent(true);
        stage.addActor(stack);

        guis.put(GuiType.INVENTORY, inventoryGui = new PlayerInventoryGui(this));
        guis.put(GuiType.HUD, hudGui = new GameHudGui(this));
        guis.put(GuiType.SETTINGS, new SettingsWindowGui(this));
        guis.put(GuiType.PAUSE, new PauseWindowGui(this));
        guis.put(GuiType.SAVE_GAME, new SaveGameWindowGui(this));
        guis.put(GuiType.QUEST, new QuestGui(this));
        guis.put(GuiType.QUEST_ENTRY, new QuestEntryGui(this));
        guis.put(GuiType.DIALOG, dialogGui = new EntityDialogGui(this));
        guis.put(GuiType.SIGN, signGui = new ReadableSignGui(this));
        guis.put(GuiType.CONTAINER, containerGui = new ContainerInventoryGui(this));

        multiplexer.addProcessor(stage);
    }

    public OasisGame getGame() {
        return game;
    }

    public Asset getAsset() {
        return asset;
    }

    public Skin getSkin() {
        return skin;
    }

    public Styles getStyle() {
        return styles;
    }

    public Stage getStage() {
        return stage;
    }

    public Stack getStack() {
        return stack;
    }

    public Camera getCamera() {
        return stage.getCamera();
    }

    public BitmapFont getSmallFont() {
        return asset.getSmall();
    }

    public BitmapFont getMediumFont() {
        return asset.getMedium();
    }

    public GameHudGui getHudComponent() {
        return hudGui;
    }

    public PlayerInventoryGui getInventoryComponent() {
        return inventoryGui;
    }

    public EntityDialogGui getDialogComponent() {
        return dialogGui;
    }

    public ReadableSignGui getSignComponent() {
        return signGui;
    }

    public ContainerInventoryGui getContainerComponent() {
        return containerGui;
    }

    /**
     * Set the cursor to something different
     *
     * @param cursor the cursor to use
     */
    public void setCursorInGame(Cursor cursor) {
        if (cursor == Cursor.DEFAULT) return;
        this.cursorState = cursor;

        Pixmap pm = new Pixmap(Gdx.files.internal(cursor.getFile()));

        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();

        wasCursorChanged = true;
    }

    /**
     * @return {@code true} if the cursor was changed recently
     */
    public boolean wasCursorChanged() {
        return wasCursorChanged;
    }

    /**
     * @return {@code true} if the cursor is unchanged.
     */
    public boolean isDefaultCursorState() {
        return cursorState == Cursor.DEFAULT;
    }

    /**
     * Reset the cursor to its default state
     */
    public void resetCursor() {
        if (cursorState == Cursor.DEFAULT) return;
        final Texture texture = styles.getDefaultCursorTexture();

        if (!texture.getTextureData().isPrepared())
            texture.getTextureData().prepare();

        final Pixmap map = texture.getTextureData().consumePixmap();
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(map, 0, 0));
        if (texture.getTextureData().disposePixmap()) {
            map.dispose();
        }
        this.cursorState = Cursor.DEFAULT;
        wasCursorChanged = false;
    }

    public void updateAndDrawStage() {
        stage.getViewport().apply();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 60));

        final long now = System.currentTimeMillis();
        for (Gui value : guis.values()) {
            value.update();
            if (value.updateInterval != 0
                    && (now - value.lastUpdate >= value.updateInterval)) {
                value.lastUpdate = now;
                value.timedUpdate(now);
            }
        }

        stage.getViewport().getCamera().update();
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        stage.getBatch().begin();
        stage.getRoot().draw(stage.getBatch(), 1);
        guis.values().forEach(gui -> gui.draw(stage.getBatch()));
        stage.getBatch().end();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getCamera().update();

        guis.values().forEach(gui -> gui.resize(width, height));
    }

    /**
     * Render a players nametag
     *
     * @param player the player
     * @param camera the game camera
     * @param batch  batch to draw with
     */
    public void renderPlayerNametag(NetworkPlayer player, Camera camera, Batch batch) {
        player.renderNametag(asset.getSmall(), batch, camera, stage.getCamera());
    }

    /**
     * Render damage animations for the player and enemies the player is attacking
     *
     * @param camera the world camera
     * @param batch  the batch
     * @param player local player
     */
    public void renderDamageAmountAnimations(Camera camera, SpriteBatch batch, PlayerSP player) {
        //player.drawDamage(batch, camera, getCamera());
        player.getEnemiesAttacking().forEach(enemy -> enemy.renderDamageAmountAnimation(batch, asset.getBoxy(), camera, getCamera()));
    }

    /**
     * Show a gui
     *
     * @param type the type
     */
    public void showGui(GuiType type) {
        guis.get(type).show();
    }

    public Gui showChildGui(Gui parent, GuiType child) {
        final Gui c = guis.get(child);
        if (c.inheritParentBehaviour) {
            parent.hiddenForChild();
        } else {
            parent.hide();
        }
        c.show();

        return c;
    }

    public void hideGui(GuiType type) {
        guis.get(type).hide();
    }

    public boolean isGuiVisible(GuiType type) {
        return guis.get(type).isGuiVisible();
    }

    public boolean isAnyGuiVisible(GuiType exclusion) {
        for (Gui gui : guis.values()) {
            if (gui.isGuiVisible() && gui.type != exclusion) return true;
        }
        return false;
    }

    public boolean isAnyGuiVisible() {
        for (Gui gui : guis.values()) {
            if (gui.isGuiVisible()) return true;
        }
        return false;
    }

    public Gui getGui(GuiType type) {
        return guis.get(type);
    }

    /**
     * Hide a GUI that's visible if there is no parent, otherwise hide the gui and show the parent
     *
     * @return {@code true} if a gui was hidden, or a parent was made visible
     */
    public boolean hideOrShowParentGuis() {
        for (Gui gui : guis.values()) {
            if (gui.isGuiVisible()
                    && gui.type != GuiType.HUD) {
                if (gui.hasParent) {
                    showParentGui(gui);
                } else {
                    gui.hide();
                }
                return true;
            }
        }
        return false;
    }

    public void toggleGui(GuiType gui) {
        if (isGuiVisible(gui)) {
            hideGui(gui);
        } else {
            showGui(gui);
        }
    }

    /**
     * Show parent gui
     *
     * @param gui the current gui
     */
    public void showParentGui(Gui gui) {
        gui.hide();
        showGui(gui.parent);
    }

    public void addGui(VisTable gui) {
        stack.add(gui);
    }

}
