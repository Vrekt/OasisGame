package me.vrekt.oasis.world.athena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.settings.GameSettings;
import me.vrekt.oasis.ui.book.PlayerBook;
import me.vrekt.oasis.world.screen.AbstractWorldScreen;

/**
 * Screen for {@link AthenaWorld}
 */
public final class AthenaWorldScreen extends AbstractWorldScreen {

    private final SpriteBatch batch;
    private final AthenaWorld world;
    private final PlayerBook book;

    // player book + if interaction is hidden because other UI elements.
    private boolean showPlayerBook, interactionHidden;

    // interaction drawable
    private final Image interaction;

    // table for the players book.
    private final Table bookTable;
    // handles mouse clicking
    private final Vector3 vector3 = new Vector3();
    private ShapeRenderer debugRenderer;

    public AthenaWorldScreen(OasisGame game, SpriteBatch batch, AthenaWorld world) {
        super(game, world.getAssets());

        this.world = world;
        this.batch = batch;
        this.book = new PlayerBook(game, world.getAssets());
        this.debugRenderer = new ShapeRenderer();

        final TextureRegion emptyInteraction = asset.getAtlas("ui/interaction/Interactions.atlas").findRegion("interaction");
        this.interaction = new Image();
        this.interaction.setVisible(false);

        final Stack stack = new Stack();
        final Table interactionTable = new Table();
        bookTable = new Table();

        interactionTable.add(this.interaction)
                .size(.5f * Gdx.graphics.getWidth(), emptyInteraction.getRegionHeight())
                .padTop((Gdx.graphics.getHeight() + (emptyInteraction.getRegionHeight() * 2f)) / 2f);

        bookTable.add(book.getImage()).size(.5f * Gdx.graphics.getWidth(), .5f * Gdx.graphics.getHeight());
        bookTable.setVisible(false);

        stack.add(interactionTable);
        stack.add(bookTable);
        root.add(stack);
    }

    @Override
    public void render(float delta) {

        // update and draw world.
        world.update(delta);
        world.renderWorld(batch, delta);
        batch.end();

        // draw stage elements
        stage.act();
        stage.getCamera().update();

        uiBatch.setProjectionMatrix(stage.getCamera().combined);
        uiBatch.begin();

        stage.getRoot().draw(uiBatch, 1);

        if (showPlayerBook)
            book.render(uiBatch, world.getAssets().getRomulusBig(), world.getAssets().getRomulusSmall());

        uiBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        world.getRenderer().resize(width, height);
        stage.getViewport().update(width, height, true);
        book.resize();
    }

    /**
     * Show an interaction texture
     *
     * @param region the region to draw
     */
    public void showInteractionTexture(TextureRegion region) {
        this.interaction.setDrawable(new TextureRegionDrawable(region));
        if (!showPlayerBook) {
            this.interaction.setVisible(true);
        } else {
            this.interactionHidden = true;
        }
    }

    /**
     * hide it.
     */
    public void hideInteraction() {
        this.interaction.setVisible(false);
        this.interactionHidden = false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer == Input.Buttons.LEFT) {
            vector3.set(screenX, screenY, 0.0f);
            vector3.set(stage.getCamera().unproject(vector3));
            if (showPlayerBook) {
                book.handleClick(vector3.x, vector3.y);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == GameSettings.BOOK_KEY) {
            this.showPlayerBook = !showPlayerBook;

            if (showPlayerBook) {
                this.bookTable.setVisible(true);
                this.interactionHidden = true;
                this.interaction.setVisible(false);
                world.getPlayer().disableInputs(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);
            } else {
                this.bookTable.setVisible(false);
                world.getPlayer().enableInputs(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);

                if (this.interactionHidden) {
                    this.interaction.setVisible(true);
                    this.interactionHidden = false;
                }
            }
            return true;
        } else if (keycode == GameSettings.INTERACTION_KEY) {
            world.checkInteraction();
        }
        return false;
    }
}
