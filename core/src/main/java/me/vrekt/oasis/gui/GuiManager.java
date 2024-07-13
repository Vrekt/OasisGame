package me.vrekt.oasis.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.gui.guis.dialog.EntityDialogGui;
import me.vrekt.oasis.gui.guis.hud.GameChatGui;
import me.vrekt.oasis.gui.guis.hud.GameHudGui;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;
import me.vrekt.oasis.gui.guis.hud.components.*;
import me.vrekt.oasis.gui.guis.inventory.ContainerInventoryGui;
import me.vrekt.oasis.gui.guis.inventory.PlayerInventoryGui;
import me.vrekt.oasis.gui.guis.lockpicking.LockpickingGui;
import me.vrekt.oasis.gui.guis.map.WorldMapGui;
import me.vrekt.oasis.gui.guis.other.MagicBookGui;
import me.vrekt.oasis.gui.guis.quest.QuestCompletedGui;
import me.vrekt.oasis.gui.guis.quest.QuestEntryGui;
import me.vrekt.oasis.gui.guis.quest.QuestGui;
import me.vrekt.oasis.gui.guis.sign.ReadableSignGui;
import me.vrekt.oasis.gui.windows.PauseWindowGui;
import me.vrekt.oasis.gui.windows.SaveGameWindowGui;
import me.vrekt.oasis.gui.windows.SettingsWindowGui;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles most functions related to guis
 */
public final class GuiManager implements Disposable {

    private final OasisGame game;
    private final Asset asset;
    private final PlayerSP player;

    private final Stage stage;
    private final Stack stack;
    private final Map<GuiType, Gui> guis = new HashMap<>();

    // commonly used GUIs
    private final GameHudGui hudGui;
    private final PlayerInventoryGui inventoryGui;
    private final EntityDialogGui dialogGui;
    private final ReadableSignGui signGui;
    private final ContainerInventoryGui containerGui;
    private final GameChatGui chatGui;
    private final LockpickingGui lockPickingGui;
    private final QuestCompletedGui completedGui;
    private final MagicBookGui bookGui;

    private Cursor cursorState;
    private boolean wasCursorChanged;

    private final GlyphLayout layout;
    private final Vector3 worldPosition = new Vector3();
    private final Vector3 screenPosition = new Vector3();

    public GuiManager(OasisGame game, Asset asset, InputMultiplexer multiplexer) {
        this.game = game;
        this.asset = asset;
        this.player = game.getPlayer();
        this.layout = new GlyphLayout();

        // fit this stage to always respect the general constraints we want
        final FitViewport viewport = new FitViewport(640, 480);
        viewport.setScaling(Scaling.contain);

        stage = new Stage(viewport);
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
        guis.put(GuiType.CHAT, chatGui = new GameChatGui(this));
        guis.put(GuiType.WORLD_MAP, new WorldMapGui(this));
        guis.put(GuiType.LOCK_PICKING, lockPickingGui = new LockpickingGui(this));
        guis.put(GuiType.QUEST_COMPLETED, completedGui = new QuestCompletedGui(this));
        guis.put(GuiType.MAGIC_BOOK, bookGui = new MagicBookGui(this));

        multiplexer.addProcessor(stage);
    }

    public OasisGame getGame() {
        return game;
    }

    /**
     * @return local player
     */
    public PlayerSP player() {
        return player;
    }

    public Asset getAsset() {
        return asset;
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

    public BitmapFont getMediumMipMap() {
        return asset.getMediumMipMapped();
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

    public GameChatGui getChatComponent() {
        return chatGui;
    }

    public LockpickingGui getLockpickingComponent() {
        return lockPickingGui;
    }

    public QuestCompletedGui getCompletedQuestComponent() {
        return completedGui;
    }

    public HudArtifactComponent getArtifactComponent() {
        return hudGui.getComponent(HudComponentType.ARTIFACT);
    }

    public HudAttributeComponent getAttributeComponent() {
        return hudGui.getComponent(HudComponentType.ATTRIBUTE);
    }

    public HudGameActionComponent getGameActionComponent() {
        return hudGui.getComponent(HudComponentType.GAME_ACTION);
    }

    public HudHotbarComponent getHotbarComponent() {
        return hudGui.getComponent(HudComponentType.HOT_BAR);
    }

    public HudItemHintComponent getItemHintComponent() {
        return hudGui.getComponent(HudComponentType.ITEM_HINT);
    }

    public HudPlayerHintComponent getHintComponent() {
        return hudGui.getComponent(HudComponentType.HINT);
    }

    public MagicBookGui getMagicBookComponent() {
        return bookGui;
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
        final Texture texture = Styles.getDefaultCursorTexture();

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

    /**
     * @return active cursor
     */
    public Cursor cursor() {
        return cursorState;
    }

    public void updateAndDrawStage() {
        stage.getViewport().apply();
        stage.act(Gdx.graphics.getDeltaTime());

        for (Gui value : guis.values()) {
            if (value.isShowing) {
                value.preDraw(stage.getBatch());
            }

            if (!value.updateWhileHidden && !value.isShowing) continue;

            value.update();
            if (value.timedUpdateInterval != 0
                    && GameManager.hasTimeElapsed(value.lastUpdate, value.timedUpdateInterval)) {
                value.lastUpdate = GameManager.getTick();
                value.timedUpdate(value.lastUpdate);
            }
        }

        stage.getViewport().getCamera().update();
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        stage.getBatch().begin();
        stage.getRoot().draw(stage.getBatch(), 1);
        for (Gui gui : guis.values()) if (gui.isGuiVisible()) gui.draw(stage.getBatch());
        stage.getBatch().end();

    }

    /**
     * Resize, updates each GUI and stage.
     *
     * @param width  width
     * @param height height
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

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
        worldPosition.set(camera.project(worldPosition.set(player.getX(), player.getY() + 2.25f, 0)));
        screenPosition.set(getCamera().project(worldPosition));
        player.renderNametag(asset.getSmall(), batch, screenPosition);
    }

    /**
     * Assumes using small font, maybe in the future provide font.
     *
     * @param text text
     * @return the string width
     */
    public float getStringWidth(String text) {
        layout.reset();
        layout.setText(asset.getSmall(), text);
        return layout.width;
    }

    /**
     * Assumes using small font, maybe in the future provide font.
     *
     * @param text text
     * @return the string height
     */
    public float getStringHeight(String text) {
        layout.reset();
        layout.setText(asset.getSmall(), text);
        return layout.height;
    }

    /**
     * Render object components
     *
     * @param worldObject object
     * @param camera      world camera
     * @param batch       world batch
     */
    public void renderWorldObjectComponents(AbstractInteractableWorldObject worldObject, Camera camera, SpriteBatch batch) {
        worldPosition.set(camera.project(worldPosition.set(worldObject.getPosition().x, worldObject.getPosition().y + 0.55f, 0)));
        screenPosition.set(getCamera().project(worldPosition));
        worldObject.renderUiComponents(batch, this, asset.getMediumMipMapped(), screenPosition);
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
     * Show a gui
     *
     * @param type the type
     */
    public void showGui(GuiType type, boolean resetCursor) {
        guis.get(type).show();
        if (resetCursor) resetCursor();
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
        if (guis.get(type).isGuiVisible()) {
            guis.get(type).hide();
        }
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
            showGui(gui, true);
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

    @Override
    public void dispose() {
        guis.values().forEach(Gui::dispose);
        guis.clear();
    }
}
