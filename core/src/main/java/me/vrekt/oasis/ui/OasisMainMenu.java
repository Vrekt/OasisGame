package me.vrekt.oasis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.save.GameSaveProperties;
import me.vrekt.oasis.save.GameSaveSlotProperty;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.ui.dialog.DialogCreator;

/**
 * The main menu of the game
 */
public final class OasisMainMenu extends ScreenAdapter {

    private static final Color HOVER_COLOR = new Color(64 / 255f, 64 / 255f, 64 / 255f, 1);

    private final SpriteBatch parallaxBatch;
    private final OrthographicCamera parallaxCamera;
    private final Texture parallaxTexture;

    private final OasisGame game;
    private final Stage stage;
    // the main UI components
    private final VisTable rootTable;
    // holds elements of the loading game UI
    private final VisTable loadGameTable;

    public OasisMainMenu(OasisGame game) {
        this.game = game;

        parallaxBatch = new SpriteBatch();
        parallaxCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        parallaxTexture = new Texture(Gdx.files.internal("ui/parallax/main_menu_background.png"));
        parallaxTexture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);

        // offset camera position, so we can see the higher part of the parallax demo
        parallaxCamera.position.y = -100;

        this.stage = new Stage(new ScreenViewport());
        this.rootTable = new VisTable();
        this.loadGameTable = new VisTable();
        this.rootTable.setFillParent(true);
        this.loadGameTable.setFillParent(true);

        // add UI components
        rootTable.add(new VisImage(game.getLogoTexture()));
        rootTable.row();

        initializeMainComponents(game.getStyle().getImageTextButtonStyle());
        initializeLoadGameComponents(game.getStyle().getThemePadded(), game.getStyle().getImageTextButtonStyle());

        stage.addActor(rootTable);
    }

    @Override
    public void show() {
        game.getMultiplexer().addProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void hide() {
        game.getMultiplexer().removeProcessor(stage);
        dispose();
    }

    /**
     * Initialize main UI components
     */
    private void initializeMainComponents(VisImageTextButton.VisImageTextButtonStyle style) {
        final VisImageTextButton newGameButton = new VisImageTextButton("New Game", style);
        final VisImageTextButton loadGameButton = new VisImageTextButton("Load Game", style);
        final VisImageTextButton quitGameButton = new VisImageTextButton("Quit Game", style);

        rootTable.add(newGameButton).width(150).padBottom(4f);
        rootTable.row();
        rootTable.add(loadGameButton).width(150).padBottom(4f);
        rootTable.row();
        rootTable.add(quitGameButton).width(150);

        assignClickAction(newGameButton, game::loadNewGame);

        // show load game UI
        assignClickAction(loadGameButton, () -> {
            stage.clear();
            stage.addActor(loadGameTable);
        });

        // FIXME: Temporary multiplayer testing
        assignClickAction(quitGameButton, () -> game.setScreen(new DialogCreator(game)));
    }

    /**
     * Initialize load game component table
     *
     * @param theme the theme
     * @param style the style
     */
    private void initializeLoadGameComponents(NinePatchDrawable theme, VisImageTextButton.VisImageTextButtonStyle style) {
        SaveManager.readSaveGameProperties(); // read if we haven't already

        final Label.LabelStyle labelStyle = new Label.LabelStyle(game.getAsset().getMedium(), Color.WHITE);
        final GameSaveProperties properties = SaveManager.getProperties();

        if (properties.hasSaveSlot(1)) addSlotTableComponent(properties.getSaveSlotProperty(1), theme, labelStyle);
        if (properties.hasSaveSlot(2)) addSlotTableComponent(properties.getSaveSlotProperty(2), theme, labelStyle);
        if (properties.hasSaveSlot(3)) addSlotTableComponent(properties.getSaveSlotProperty(3), theme, labelStyle);

        // if no saves just display that.
        if (!properties.hasAnySaveSlots()) {
            loadGameTable.add(new VisLabel("No Save Games Available!", new Label.LabelStyle(game.getAsset().getLarge(), Color.WHITE)));
            loadGameTable.row();
        }

        final VisImageTextButton backButton = new VisImageTextButton("Go Back", style);
        loadGameTable.add(backButton).fillX();

        // go back
        assignClickAction(backButton, () -> {
            stage.clear();
            stage.addActor(rootTable);
        });
    }

    /**
     * Add a save slot table component to the root
     *
     * @param properties the properties
     * @param theme      the theme
     * @param style      the style
     */
    private void addSlotTableComponent(GameSaveSlotProperty properties, NinePatchDrawable theme, Label.LabelStyle style) {
        final VisTable root = new VisTable();
        final VisImage saveImage = new VisImage(game.getAsset().get("secondwind_artifact"));
        final VisLabel saveName = new VisLabel(properties.getName(), style);
        final VisLabel progress = new VisLabel(properties.getProgress(), style);
        final VisLabel dateSaved = new VisLabel(properties.getDate(), style);

        root.setBackground(theme);
        root.add(saveImage);

        final VisTable table = new VisTable();
        table.add(saveName).left();
        table.row();
        table.add(progress);
        table.row();
        table.add(dateSaved);
        root.add(table).padLeft(12f);

        loadGameTable.add(root).padBottom(6f);
        loadGameTable.row();

        root.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.loadSaveGame(properties.getSlot());
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // TODO: Does not work correctly, but for now its fine.
                root.setColor(HOVER_COLOR);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                root.setColor(Color.WHITE);
            }
        });

    }

    /**
     * Assign click action to an actor
     *
     * @param actor  the actor
     * @param action the action to run
     */
    private void assignClickAction(Actor actor, Runnable action) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        // increment camera position and scroll along
        parallaxCamera.position.x += 100 * delta;
        parallaxCamera.update();

        // render parallax
        parallaxBatch.setProjectionMatrix(parallaxCamera.combined);
        parallaxBatch.begin();
        renderBackground();
        parallaxBatch.end();

        // render stage afterward so elements are on top
        stage.act();
        stage.draw();
    }

    /**
     * Render the background of the main menu
     * Partially adapted from <a href="https://ailurux.github.io/ParallaxLibgdx/">...</a>
     */
    private void renderBackground() {
        final float originX = parallaxCamera.position.x - parallaxCamera.viewportWidth / 2f;
        final float originY = parallaxCamera.position.y - parallaxCamera.viewportHeight / 2f;

        final int x = (int) (parallaxCamera.position.x) % parallaxTexture.getWidth();
        final int y = (int) (parallaxCamera.position.y) % parallaxTexture.getHeight();

        parallaxBatch.draw(parallaxTexture,
                originX, originY, originX, originY,
                parallaxCamera.viewportWidth, parallaxTexture.getHeight(),
                1.0f,
                1.0f,
                0.0f,
                x,
                y,
                (int) parallaxCamera.viewportWidth,
                parallaxTexture.getHeight(),
                false,
                false);
    }

    @Override
    public void dispose() {
        stage.dispose();
        parallaxBatch.dispose();
        parallaxTexture.dispose();
    }
}
