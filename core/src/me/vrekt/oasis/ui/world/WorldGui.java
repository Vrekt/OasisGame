package me.vrekt.oasis.ui.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.ui.world.book.PlayerBookGui;
import me.vrekt.oasis.ui.world.interactions.WorldDialogGui;
import me.vrekt.oasis.ui.world.interactions.WorldInteractionGui;
import me.vrekt.oasis.ui.world.pause.PauseGui;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * Represents the base world GUI.
 */
public class WorldGui extends InputAdapter implements Disposable {

    // root table
    protected final Stage stage;
    protected final Table root = new Table();

    // for stacking UI elements in the same position
    protected final Stack stack = new Stack();

    // world in
    protected final Asset asset;
    protected final AbstractWorld world;

    // fonts
    public final BitmapFont font, bigFont;
    public final GlyphLayout layout;

    // stage batch
    public Batch batch;

    protected final WorldInteractionGui worldInteractionGui;
    protected final WorldDialogGui dialogGui;
    private final PlayerBookGui bookGui;
    protected final PauseGui pauseGui;

    public WorldGui(OasisGame game, Asset asset, AbstractWorld world, InputMultiplexer multiplexer) {
        this.asset = asset;
        this.world = world;

        // ?
        stage = new Stage(new ScalingViewport(Scaling.fill, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        root.setFillParent(true);
        stage.addActor(root);
        batch = stage.getBatch();
        root.add(stack);

        // input processing
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);

        this.font = asset.getRomulusSmall();
        this.bigFont = asset.getRomulusBig();
        this.layout = new GlyphLayout(font, "");

        worldInteractionGui = new WorldInteractionGui(this);
        dialogGui = new WorldDialogGui(this);
        bookGui = new PlayerBookGui(game, this);
        pauseGui = new PauseGui(this);

        multiplexer.addProcessor(worldInteractionGui);
        multiplexer.addProcessor(dialogGui);
        multiplexer.addProcessor(bookGui);
        multiplexer.addProcessor(pauseGui);
    }

    public Asset getAsset() {
        return asset;
    }

    public BitmapFont getBigFont() {
        return bigFont;
    }

    public BitmapFont getFont() {
        return font;
    }

    public Stage getStage() {
        return stage;
    }

    public WorldInteractionGui getInteractions() {
        return worldInteractionGui;
    }

    public WorldDialogGui getDialog() {
        return dialogGui;
    }

    public PlayerBookGui getBook() {
        return bookGui;
    }

    public PauseGui getPause() {
        return pauseGui;
    }

    public void render() {
        stage.getViewport().apply();

        stage.act();
        stage.getCamera().update();

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();

        // base stage.
        stage.getRoot().draw(batch, 1);

        // render gui(s)
        if (worldInteractionGui.isShowing()) worldInteractionGui.render(font, bigFont, batch, layout);
        if (dialogGui.isShowing()) dialogGui.render(font, bigFont, batch, layout);
        if (bookGui.isShowing()) bookGui.render(font, bigFont, batch, layout);
        if (pauseGui.isShowing()) pauseGui.render(font, bigFont, batch, layout);
        batch.end();
    }

    public void click(float x, float y) {
        if (worldInteractionGui.isShowing()) worldInteractionGui.clicked(x, y);
        if (dialogGui.isShowing()) dialogGui.clicked(x, y);
        if (bookGui.isShowing()) bookGui.clicked(x, y);
        if (pauseGui.isShowing()) pauseGui.clicked(x, y);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getCamera().update();

        worldInteractionGui.resize();
        dialogGui.resize();
        bookGui.resize();
        pauseGui.resize();
    }

    /**
     * Add an element to the stack
     *
     * @param image img
     * @param sizeX x
     * @param sizeY y
     * @param pt    pad top
     */
    public void addElementToStack(Image image, float sizeX, float sizeY, float pt) {
        final Table table = new Table();
        table.add(image).size(sizeX, sizeY).padTop(pt);
        this.stack.add(table);
    }

    /**
     * Add an element to the stack
     *
     * @param image img
     * @param sizeX x
     * @param sizeY y
     */
    public void addElementToStack(Image image, float sizeX, float sizeY) {
        addElementToStack(image, sizeX, sizeY, 0.0f);
    }


    /**
     * Add an element to the stack
     *
     * @param image img
     * @param sizeX x
     * @param sizeY y
     * @param pt    pad top
     * @param pr    pad right
     */
    public void addElementToStack(Image image, float sizeX, float sizeY, float pt, float pr) {
        final Table table = new Table();
        table.add(image).size(sizeX, sizeY).padTop(pt).padRight(pr);
        this.stack.add(table);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
