package me.vrekt.oasis.ui.book;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ui.book.pages.QuestBookPage;
import me.vrekt.oasis.world.asset.WorldAsset;

/**
 * Represents the players book.
 */
public final class PlayerBook {

    // atlas of all textures
    private final TextureAtlas bookAtlas;
    private final Image image;

    // current page player is on
    private BookPage currentPage;

    public PlayerBook(OasisGame game, WorldAsset asset) {
        this.bookAtlas = asset.get(WorldAsset.BOOK);
        this.currentPage = new QuestBookPage(game, bookAtlas);
        this.image = new Image(currentPage.currentTabTexture);
    }

    public Image getImage() {
        return image;
    }

    // render player book page
    public void render(Batch batch, BitmapFont big, BitmapFont small, float x, float y) {
        currentPage.render(batch, big, small, x, y);
    }

    // reset state when resizing or changing book pages
    public void resetState() {
        currentPage.resetState();
    }

    // handle clicking within the page UI
    public void handleClick(float x, float y) {
        currentPage.handleClick(x, y);
    }

}
