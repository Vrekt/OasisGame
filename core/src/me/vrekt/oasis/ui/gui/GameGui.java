package me.vrekt.oasis.ui.gui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.ui.gui.dialog.DialogGui;
import me.vrekt.oasis.ui.gui.hud.PlayerHudGui;
import me.vrekt.oasis.ui.gui.inventory.PlayerInventoryHudGui;
import me.vrekt.oasis.ui.gui.notification.NotificationGui;
import me.vrekt.oasis.ui.gui.notification.QuestNotificationGui;
import me.vrekt.oasis.ui.gui.quest.QuestGui;
import me.vrekt.oasis.ui.gui.settings.SettingsGui;
import me.vrekt.oasis.ui.world.Gui;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all GUI elements
 */
public final class GameGui {

    private final Stage stage;
    private final Stack stack = new Stack();
    private final Asset asset;
    private final Skin skin;
    private final BitmapFont romulusSmall, romulusBig;
    private final GlyphLayout layout;

    private final Map<Integer, Gui> guis = new HashMap<>();

    public GameGui(OasisGame game, Asset asset, InputMultiplexer multiplexer) {
        this.stage = new Stage(new ScreenViewport());
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        stack.setFillParent(true);
        root.add(stack).grow();

        this.asset = asset;
        this.skin = asset.getSkin();
        this.romulusSmall = asset.getRomulusSmall();
        this.romulusBig = asset.getRomulusBig();
        this.layout = new GlyphLayout(romulusSmall, "");

        skin.add("small", asset.getRomulusSmall());
        skin.add("smaller", asset.getRomulusSmaller());
        skin.add("big", romulusBig);

        multiplexer.addProcessor(stage);

        createGui(NotificationGui.ID, new NotificationGui(this));
        createGui(DialogGui.ID, new DialogGui(this));
        createGui(PlayerHudGui.ID, new PlayerHudGui(this));
        createGui(PlayerInventoryHudGui.ID, new PlayerInventoryHudGui(this, game));
        createGui(QuestNotificationGui.ID, new QuestNotificationGui(this));
        createGui(QuestGui.ID, new QuestGui(this));
        createGui(99, new SettingsGui(this));
    }

    private void createGui(int id, Gui any) {
        guis.put(id, any);
    }

    public Camera getCamera() {
        return stage.getCamera();
    }

    public Asset getAsset() {
        return asset;
    }

    public TextureRegion getAsset(String texture) {
        return asset.getAssets().findRegion(texture);
    }

    public Skin getSkin() {
        return skin;
    }

    public BitmapFont getRomulusSmall() {
        return romulusSmall;
    }

    public BitmapFont getRomulusBig() {
        return romulusBig;
    }

    public GlyphLayout getLayout() {
        return layout;
    }

    public void apply() {
        stage.getViewport().apply();
    }

    /**
     * Send a notification to the player
     *
     * @param text     text
     * @param duration time on screen
     */
    public void sendPlayerNotification(String text, float duration) {
        ((NotificationGui) guis.get(NotificationGui.ID)).sendPlayerNotification(text, duration);
    }

    /**
     * Show that a quest has started tracking.
     */
    public void showQuestTracking() {
        ((QuestNotificationGui) guis.get(QuestNotificationGui.ID)).showQuestTracking();
    }

    public void showGui(int id) {
        guis.get(id).showGui();
    }

    public void hideGui(int id) {
        guis.get(id).hideGui();
    }

    public boolean isGuiVisible(int id) {
        return guis.get(id).isVisible();
    }

    public DialogGui getDialog() {
        return ((DialogGui) guis.get(DialogGui.ID));
    }

    public QuestGui getQuest() {
        return ((QuestGui) guis.get(QuestGui.ID));
    }

    /**
     * Render all active GUI elements.
     */
    public void render() {
        stage.getViewport().apply();

        stage.act();
        stage.getCamera().update();

        for (Gui gui : guis.values()) gui.update();

        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getCamera().update();

        for (Gui gui : guis.values()) gui.resize(width, height);
    }

    public boolean updateDialogState(EntityInteractable interactingWith) {

        if (getDialog().isVisible()
                && interactingWith != null
                && interactingWith.isSpeakingTo()
                && !interactingWith.isSpeakable()) {

            getDialog().hideGui();
            interactingWith.setSpeakingTo(false);
            return true;
        }
        return false;
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
