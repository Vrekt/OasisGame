package me.vrekt.oasis.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
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
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.gui.dialog.DialogGui;
import me.vrekt.oasis.gui.inventory.ContainerInventoryGui;
import me.vrekt.oasis.gui.inventory.PlayerInventoryGui;
import me.vrekt.oasis.gui.notification.QuestNotificationGui;
import me.vrekt.oasis.gui.quest.QuestGui;
import me.vrekt.oasis.quest.Quest;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all GUI elements
 */
public final class GameGui {

    private final Stage stage;
    private final Stack stack;
    private final Asset asset;
    private final Skin skin;
    private final BitmapFont romulusSmall, romulusBig;
    private final GlyphLayout layout;
    private final OasisGame game;

    private final Map<GuiType, Gui> guis = new HashMap<>();

    public GameGui(OasisGame game, Asset asset, InputMultiplexer multiplexer) {
        this.stage = new Stage(new ScreenViewport());
        this.game = game;
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        stack = new Stack();
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

        //   guis.put(NotificationGui.ID, new NotificationGui(this));
        guis.put(GuiType.DIALOG, new DialogGui(this));
        //guis.put(PlayerHudGui.ID, new PlayerHudGui(this));
        // guis.put(PlayerInventoryHudGui.ID, new PlayerInventoryHudGui(this, game));
        guis.put(GuiType.QUEST_NOTIFICATION, new QuestNotificationGui(this));
        guis.put(GuiType.QUEST, new QuestGui(this));
        //guis.put(99, new SettingsGui(this));
        // guis.put(98, new DomainEntranceMenuGui(this));
        //  guis.put(38, new PlayerInventoryGui(this));
        guis.put(GuiType.CONTAINER, new ContainerInventoryGui(this));
        guis.put(GuiType.INVENTORY, new PlayerInventoryGui(this));
    }

    public Camera getCamera() {
        return stage.getCamera();
    }

    public Asset getAsset() {
        return asset;
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

    public OasisGame getGame() {
        return game;
    }

    public void applyStageViewport() {
        stage.getViewport().apply();
    }

    /**
     * Show that a quest has started tracking.
     */
    public void showQuestAdded() {
        ((QuestNotificationGui) guis.get(GuiType.QUEST_NOTIFICATION)).showQuestAdded();
    }

    /**
     * Show dialog for an entity
     *
     * @param entity the entity
     */
    public void showEntityDialog(EntityInteractable entity) {
        ((DialogGui) getGui(GuiType.DIALOG)).setDialogToRender(entity, entity.getDialogSection(), entity.getDisplay());
    }

    /**
     * Start tracking a new quest
     *
     * @param quest the quest
     */
    public void addQuest(Quest quest) {
        ((QuestGui) getGui(GuiType.QUEST)).addQuest(quest);
    }

    public void showGui(GuiType type) {
        guis.get(type).showGui();
    }

    public void hideGui(GuiType type) {
        guis.get(type).hideGui();
    }

    public boolean isGuiVisible(GuiType type) {
        return guis.get(type).isVisible();
    }

    public <T extends Gui> T getGui(GuiType type) {
        return (T) guis.get(type);
    }

    /**
     * Render all active GUI elements.
     */
    public void render() {
        stage.getViewport().apply();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.getCamera().update();

        for (Gui gui : guis.values()) gui.update();

        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getCamera().update();

        for (Gui gui : guis.values()) gui.resize(width, height);
    }

    /**
     * Update dialog rendering
     *
     * @param interactingWith the entity interacting with
     * @return if the gui has been hidden
     */
    public boolean updateDialogState(EntityInteractable interactingWith) {
        if (getGui(GuiType.DIALOG).isVisible()
                && interactingWith != null
                && interactingWith.isSpeakingTo()
                && !interactingWith.isSpeakable()) {

            hideGui(GuiType.DIALOG);
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
