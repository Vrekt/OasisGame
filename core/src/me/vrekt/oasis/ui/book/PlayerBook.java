package me.vrekt.oasis.ui.book;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.ui.book.pages.InventoryBookPage;
import me.vrekt.oasis.ui.book.pages.QuestBookPage;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the players book.
 */
public final class PlayerBook {

    // pages sorted by their button bounds.
    private final Map<Rectangle, BookPage> pageButtons = new HashMap<>();

    // used for translating stage coordinates
    private final Vector2 stageCoordinates = new Vector2(0, 0);

    // atlas of all textures
    private final OasisGame game;
    private final TextureAtlas bookAtlas;
    private final Image image;

    // current page player is on
    private BookPage currentPage;
    private boolean buttonsInitialized;

    public PlayerBook(OasisGame game, Asset asset) {
        this.game = game;
        this.bookAtlas = asset.get(Asset.BOOK);
        this.currentPage = new QuestBookPage(game, bookAtlas);
        this.image = new Image(currentPage.currentTabTexture);
        resize();
    }

    public Image getImage() {
        return image;
    }

    /**
     * Initialize buttons
     */
    private void initializeButtons(float x, float y) {
        // margins to tabs/buttons
        float marginToButtonsX = 29 * 1.5f;
        x += image.getWidth() - marginToButtonsX;
        float marginToButtonsY = 24 * 1.5f;
        y -= marginToButtonsY;

        // sizes
        float buttonSizeX = 15 * 1.5f;
        float buttonSizeY = 32 * 1.5f;
        pageButtons.put(new Rectangle(x - buttonSizeX, y - buttonSizeY, buttonSizeX, buttonSizeY), currentPage);
        float buttonSpacingY = 9 * 1.5f;
        y -= (buttonSizeY + buttonSpacingY);
        pageButtons.put(new Rectangle(x - buttonSizeX, y - buttonSizeY, buttonSizeX, buttonSizeY), new InventoryBookPage(game, bookAtlas));
    }

    /**
     * Render the player book
     *
     * @param batch batch
     * @param big   big font
     * @param small small font
     */
    public void render(Batch batch, BitmapFont big, BitmapFont small) {
        // ensure buttons are initialized once to get right coordinates
        if (!buttonsInitialized) {
            this.image.localToStageCoordinates(stageCoordinates);
            buttonsInitialized = true;
            initializeButtons(stageCoordinates.x, stageCoordinates.y + image.getHeight());
        } else {
            this.image.localToStageCoordinates(stageCoordinates);
        }
        currentPage.render(batch, big, small, stageCoordinates.x, stageCoordinates.y + image.getHeight());
        stageCoordinates.set(0, 0);
    }

    /**
     * Handle resizing of the window to update elements positions.
     */
    public void resize() {
        this.image.localToStageCoordinates(stageCoordinates);
    }

    /**
     * Handle clicking within the book UI
     *
     * @param x x
     * @param y y
     */
    public void handleClick(float x, float y) {

    }

}
