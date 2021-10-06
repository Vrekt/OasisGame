package me.vrekt.oasis.ui.book;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A base single page within the {@link PlayerBook}
 */
public abstract class BookPage {

    protected final float bookCenterX = 128;
    // margin left/right to book page.
    protected final float bookMargin = 44;
    // inner page width
    protected final float pageWidth = 73;

    protected final int tabNumber;
    protected final String title;
    protected TextureRegion currentTabTexture;

    public BookPage(int tabNumber, String title) {
        this.tabNumber = tabNumber;
        this.title = title;
    }

    public int getTabNumber() {
        return tabNumber;
    }

    public TextureRegion getTabTexture() {
        return currentTabTexture;
    }

    public abstract void render(Batch batch, BitmapFont big, BitmapFont small, float x, float y);

    // hide the book in general.
    public abstract void hide();

    public abstract void handleClick(float x, float y);

    /**
     * Reset any state within this page.
     * <p>
     * Invoked upon resizing the window or changing pages within the book
     */
    public abstract void resetState();

}
