package me.vrekt.oasis.ui.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.quest.type.QuestRewards;
import me.vrekt.oasis.ui.gui.dialog.DialogGui;
import me.vrekt.oasis.ui.gui.hud.PlayerHudGui;
import me.vrekt.oasis.ui.gui.inventory.PlayerInventoryHudGui;
import me.vrekt.oasis.ui.gui.notification.NotificationGui;
import me.vrekt.oasis.ui.gui.notification.QuestNotificationGui;
import me.vrekt.oasis.ui.world.Gui;
import me.vrekt.oasis.world.AbstractWorld;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all GUI elements
 */
public final class GameGui {

    // root table
    private final Stage stage;
    private final Stack stack = new Stack();
    private final Batch batch;

    // world in
    private final Asset asset;
    private final AbstractWorld world;

    // fonts
    private final Skin skin;
    private final BitmapFont romulusSmall, romulusBig;
    private final GlyphLayout layout;

    private final Vector3 touch = new Vector3(0, 0, 0);
    private final Map<Integer, Gui> guis = new HashMap<>();

    public GameGui(OasisGame game, Asset asset, AbstractWorld world, InputMultiplexer multiplexer) {
        this.stage = new Stage(new ScreenViewport());
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        stack.setFillParent(true);
        root.add(stack).grow();

        this.asset = asset;
        this.world = world;
        this.skin = new Skin(Gdx.files.internal("ui/skin/default/uiskin.json"), new TextureAtlas("ui/skin/default/uiskin.atlas"));
        this.romulusSmall = asset.getRomulusSmall();
        this.romulusBig = asset.getRomulusBig();
        this.layout = new GlyphLayout(romulusSmall, "");

        skin.add("small", asset.getRomulusSmall());
        skin.add("big", romulusBig);
        batch = stage.getBatch();

        multiplexer.addProcessor(stage);

        createGui(NotificationGui.ID, new NotificationGui(this));
        createGui(DialogGui.ID, new DialogGui(this));
        createGui(PlayerHudGui.ID, new PlayerHudGui(this));
        createGui(PlayerInventoryHudGui.ID, new PlayerInventoryHudGui(this, game));
        createGui(QuestNotificationGui.ID, new QuestNotificationGui(this));
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
     * Show quest rewards player received
     *
     * @param name    the name
     * @param rewards rewards
     */
    public void showQuestRewards(String name, Map<QuestRewards, Integer> rewards) {
        ((QuestNotificationGui) guis.get(QuestNotificationGui.ID)).showQuestReward(name, rewards);
    }

    public void showGui(int id) {
        guis.get(id).showGui();
    }

    public void hideGui(int id) {
        guis.get(id).hideGui();
    }

    public DialogGui getDialog() {
        return ((DialogGui) guis.get(DialogGui.ID));
    }

    /**
     * Render all active GUI elements.
     */
    public void render() {
        stage.getViewport().apply();

        stage.act();
        stage.getCamera().update();

        for (Gui gui : guis.values()) gui.update();

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();

        stage.getRoot().draw(batch, 1);
        batch.end();
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
