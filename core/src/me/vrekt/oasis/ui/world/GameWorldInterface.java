package me.vrekt.oasis.ui.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.dialog.DialogRenderer;
import me.vrekt.oasis.dialog.entity.EntityDialogSection;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.settings.GameSettings;
import me.vrekt.oasis.ui.book.PlayerBook;
import me.vrekt.oasis.ui.book.pages.InventoryBookPage;
import me.vrekt.oasis.world.AbstractWorld;

/**
 * General UI within worlds
 */
public class GameWorldInterface extends InputAdapter {

    // root table
    protected final Stage stage;
    protected final Table root = new Table();
    private final InputMultiplexer multiplexer = new InputMultiplexer();

    // for stacking UI elements in the same position
    protected final Stack stack = new Stack();

    protected final OasisGame game;
    protected final PlayerBook book;
    protected final AbstractWorld world;
    private final BitmapFont font, bigFont;
    private final GlyphLayout layout;

    // UI elements like interactions and dialog.
    protected Image interaction, dialogInteraction, otherInteraction, shop, pause, p2;

    // stage batch
    protected Batch batch;

    // if various elements should be rendered
    private boolean renderPlayerBook, renderDialog, renderInteraction, renderOther;

    private Sprite sprite;
    private Texture t;

    private final Vector2 interactionCoordinates = new Vector2(0, 0);
    private boolean interactionHidden, interactionInitialized;
    private String interactionText;

    private final DialogRenderer dialogRenderer;
    private final Vector2 dialogCoordinates = new Vector2(0, 0);
    private boolean dialogInitialized;

    // unprojection
    private final Vector3 vector3 = new Vector3();
    private ShapeRenderer renderer;

    /**
     * Initializes all UI elements
     *
     * @param asset asset
     */
    public GameWorldInterface(OasisGame game, Asset asset, AbstractWorld world) {
        this.world = world;

        this.stage = new Stage();
        final FillViewport viewport = new FillViewport(600, 400);
        this.stage.setViewport(viewport);

        this.root.setFillParent(true);
        this.stage.addActor(root);
        this.renderer = new ShapeRenderer();

        // input processing
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);

        this.game = game;
        this.font = asset.getRomulusSmall();
        this.bigFont = asset.getRomulusBig();
        this.layout = new GlyphLayout(font, "");

        this.book = new PlayerBook(game, asset);
        this.dialogRenderer = new DialogRenderer(font, stage.getCamera());
        this.multiplexer.addProcessor(dialogRenderer);

        final TextureAtlas interactions = asset.getAtlas(Asset.INTERACTIONS);
        final TextureRegion interaction = interactions.findRegion("interaction");
        final TextureRegion dialog = interactions.findRegion("interaction_dialog");
        final TextureRegion shop = asset.getAtlas(Asset.SHOP).findRegion("shop_buy_tab");
        t = new Texture("ui/pause.png");

        this.interaction = new Image(interaction);
        this.dialogInteraction = new Image(dialog);
        this.otherInteraction = new Image();
        this.shop = new Image(shop);
        this.interaction.setVisible(false);
        this.dialogInteraction.setVisible(false);
        this.otherInteraction.setVisible(false);
        this.shop.setVisible(false);
        this.pause = new Image(t);
        this.pause.setVisible(false);

        Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB888);
        pixmap.setColor(160 / 255f, 160 / 255f, 160 / 255f, 0f);
        pixmap.fill();

        sprite = new Sprite(new Texture(pixmap));
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sprite.setColor(160 / 255f, 160 / 255f, 160 / 255f, 170 / 255f);

        p2 = new Image();
        p2.setVisible(false);

        // add interactions in general
        this.stack.add(addElementToTable(this.interaction, .5f * Gdx.graphics.getWidth(), interaction.getRegionHeight(),
                (Gdx.graphics.getHeight() + (interaction.getRegionHeight() * 2f)) / 2f));
        // add dialog interactions
        this.stack.add(addElementToTable(dialogInteraction, dialog.getRegionWidth(), dialog.getRegionHeight(),
                Gdx.graphics.getHeight() - (dialog.getRegionHeight() * 2.5f)));
        // add any other UI interaction element
        this.stack.add(addElementToTable(otherInteraction, .25f * Gdx.graphics.getWidth(), .25f * Gdx.graphics.getHeight()));
        // add player book UI
        this.stack.add(addElementToTable(book.getImage(), .5f * Gdx.graphics.getWidth(), .5f * Gdx.graphics.getHeight()));
        // add shop UI
        this.stack.add(addElementToTable(this.shop, .5f * Gdx.graphics.getWidth(), .5f * Gdx.graphics.getHeight()));
        // pause menu
        this.stack.add(addElementToTable(this.pause, .5f * Gdx.graphics.getWidth(), .5f * Gdx.graphics.getHeight()));
        // done
        this.root.add(stack);
        this.batch = stage.getBatch();
    }

    /**
     * Render/update any UI elements
     */
    public void render() {
        stage.act();
        stage.getCamera().update();

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();

        stage.getRoot().draw(batch, 1);

        // book
        if (renderPlayerBook) book.render(batch, font);

        // dialog
        if (renderDialog) {
            if (!dialogInitialized) {
                this.dialogInteraction.localToStageCoordinates(dialogCoordinates);
                dialogInitialized = true;
            }

            this.dialogRenderer.renderDialog(batch, dialogCoordinates.x, dialogCoordinates.y, dialogInteraction.getWidth(), dialogInteraction.getHeight());
        }

        // interaction text
        if (interactionText != null && interaction.isVisible()) {
            if (!interactionInitialized) {
                interaction.localToStageCoordinates(interactionCoordinates);
                interactionInitialized = true;
            }

            bigFont.setColor(Color.BLACK);
            layout.setText(bigFont, interactionText);
            bigFont.draw(batch, interactionText, interactionCoordinates.x + (interaction.getWidth() - layout.width) / 2f, interactionCoordinates.y + (interaction.getHeight() - (layout.height / 2f)));
        }

        if (p2.isVisible()) {
            //   batch.draw(t, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        batch.end();
        //    if (renderPlayerBook) renderDebug();
    }

    private void renderDebug() {
        renderer.setProjectionMatrix(stage.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        if (book.getCurrentPage() instanceof InventoryBookPage) {
            for (Rectangle value : game.thePlayer.getInventory().getItemLocations().values()) {
                renderer.rect(value.x, value.y, value.width, value.height);
            }

        }

        renderer.end();
    }

    /**
     * Resize UI
     *
     * @param x x
     * @param y y
     */
    public void resize(int x, int y) {
        stage.getViewport().update(x, y, true);
        book.resize();

        // handle dialog coordinates as-well
        dialogCoordinates.set(0, 0);
        dialogInteraction.localToStageCoordinates(dialogCoordinates);

        // interaction coordinates
        interactionCoordinates.set(0, 0);
        interaction.localToStageCoordinates(interactionCoordinates);
    }

    public void pause() {

    }

    public void unpause() {
        this.pause.setVisible(false);
    }

    public void showDialog(EntityInteractable entity, EntityDialogSection dialog, TextureRegion display) {
        this.dialogRenderer.setDialogToUse(entity, dialog, display);
        this.renderDialog = true;
        this.dialogInteraction.setVisible(true);
    }

    public boolean isShowingDialog() {
        return renderDialog;
    }

    public void hideDialog() {
        this.renderDialog = false;
        this.dialogInteraction.setVisible(false);
    }

    /**
     * Show an interaction
     *
     * @param text the text to render
     */
    public void showInteraction(String text) {
        this.interactionText = text;

        if (!renderPlayerBook) {
            this.interaction.setVisible(true);
        } else {
            this.interactionHidden = true;
        }
    }

    public void hideInteraction() {
        this.interaction.setVisible(false);
        this.interactionHidden = false;
        this.interactionText = null;
    }

    public void showOtherInteraction(TextureRegion texture) {
        this.renderPlayerBook = false;
        this.otherInteraction.setDrawable(new TextureRegionDrawable(texture));
        this.otherInteraction.setVisible(true);
    }

    public void showShop() {
        this.shop.setVisible(true);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer == Input.Buttons.LEFT) {
            vector3.set(screenX, screenY, 0.0f);
            vector3.set(stage.getCamera().unproject(vector3));
            if (renderPlayerBook) {
                book.handleClick(vector3.x, vector3.y);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == GameSettings.BOOK_KEY) {
            this.renderPlayerBook = !renderPlayerBook;

            if (renderPlayerBook) {
                book.getImage().setVisible(true);

                interactionHidden = interaction.isVisible();
                interaction.setVisible(false);
                world.getPlayer().disableInputs(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);
            } else {
                book.getImage().setVisible(false);
                world.getPlayer().enableInputs(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);

                if (interactionHidden) {
                    interaction.setVisible(true);
                    interactionHidden = false;
                }
            }
            return true;
        } else if (keycode == GameSettings.INTERACTION_KEY) {
            world.handleInteractionKeyPressed();
        } else if (keycode == Input.Keys.ESCAPE) {
            this.pause();
        }
        return false;
    }

    /**
     * Add an element to a new table
     *
     * @param image img
     * @param sizeX x
     * @param sizeY y
     * @param pt    pad top
     * @return the new table
     */
    private Table addElementToTable(Image image, float sizeX, float sizeY, float pt) {
        final Table table = new Table();
        table.add(image).size(sizeX, sizeY).padTop(pt);
        return table;
    }

    /**
     * Add an element to a new table
     *
     * @param image img
     * @param sizeX x
     * @param sizeY y
     * @return the new table
     */
    private Table addElementToTable(Image image, float sizeX, float sizeY) {
        return addElementToTable(image, sizeX, sizeY, 0.0f);
    }

}
