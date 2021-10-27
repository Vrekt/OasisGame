package me.vrekt.oasis.ui.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
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
import me.vrekt.oasis.ui.gui.dialog.DialogGui;
import me.vrekt.oasis.ui.gui.inventory.PlayerInventoryHudGui;
import me.vrekt.oasis.ui.gui.notification.NotificationGui;
import me.vrekt.oasis.ui.gui.notification.QuestNotificationGui;
import me.vrekt.oasis.world.AbstractWorld;

public final class GameGui extends InputAdapter {

    // root table
    private final Stage stage;
    private final Stack stack = new Stack();

    // world in
    private final Asset asset;
    private final AbstractWorld world;

    // fonts
    private final Skin skin;
    private final BitmapFont romulusSmall, romulusBig;
    private final GlyphLayout layout;

    private final Vector3 touch = new Vector3(0, 0, 0);
    private final PlayerInventoryHudGui inventoryHudGui;
    private final QuestNotificationGui questNotificationGui;
    private final NotificationGui notificationGui;
    private final DialogGui dialogGui;

    private Batch batch;

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

        skin.add("small", asset.getRomulusClone());
        skin.add("big", romulusBig);
        batch = stage.getBatch();

        multiplexer.addProcessor(this);
        multiplexer.addProcessor(stage);
        notificationGui = new NotificationGui(this);
        dialogGui = new DialogGui(this);
        inventoryHudGui = new PlayerInventoryHudGui(this, game);
        questNotificationGui = new QuestNotificationGui(this);
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

    public NotificationGui getNotificationGui() {
        return notificationGui;
    }

    public DialogGui getDialogGui() {
        return dialogGui;
    }

    public PlayerInventoryHudGui getInventoryHudGui() {
        return inventoryHudGui;
    }

    public QuestNotificationGui getQuestNotificationGui() {
        return questNotificationGui;
    }

    /**
     * Render all active GUI elements.
     */
    public void render() {
        stage.getViewport().apply();

        stage.act();
        stage.getCamera().update();

        notificationGui.update();
        inventoryHudGui.update();

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();

        stage.getRoot().draw(batch, 1);

        if (notificationGui.isVisible()) notificationGui.render(batch);
        if (dialogGui.isVisible()) dialogGui.render(batch);

        batch.end();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getCamera().update();

        dialogGui.resize(width, height);
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

    public Table addTableElement() {
        final Table table = new Table();
        final Container<Table> container = new Container<>(table);
        container.top().left().padTop(Gdx.graphics.getHeight() / 4f).padLeft(16f);
        stack.addActor(container);
        return table;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        stage.getCamera().unproject(touch.set(screenX, screenY, 0.0f));
        return false;
    }
}
