package me.vrekt.oasis.ui.book;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.quest.type.QuestType;

import java.util.HashMap;
import java.util.Map;

/**
 * A base single page within the {@link PlayerBook}
 */
public abstract class BookPage {

    // x,y margins
    protected final float marginX = 44 * 1.5f;
    protected final float marginY = 37 * 1.5f;

    protected final float inventoryMarginX = 52 * 1.5f;
    protected final float inventoryMarginY = 31 * 1.5f;

    // next page
    protected final float innerMargin = 131 * 1.5f;
    protected final float pageSize = 82 * 1.5f;

    protected final int tabNumber;
    protected TextureRegion currentTabTexture;

    // button registry
    protected Map<Rectangle, QuestType> buttons = new HashMap<>();

    public BookPage(int tabNumber) {
        this.tabNumber = tabNumber;
    }

    /**
     * Retrieve the button value that was clicked
     *
     * @param x X
     * @param y Y
     * @return the value, or {@code null}
     */
    protected QuestType getButtonClicked(float x, float y) {
        for (Rectangle rectangle : buttons.keySet()) {
            if (rectangle.contains(x, y)) {
                return buttons.get(rectangle);
            }
        }
        return null;
    }

    /**
     * Render this page
     *
     * @param batch batch
     * @param font  the font
     * @param x     X
     * @param y     Y
     */
    public abstract void render(Batch batch, BitmapFont font, float x, float y);

    /**
     * Handle a click within this page.
     *
     * @param x X
     * @param y Y
     */
    public abstract void handleClick(float x, float y);

    // hide the book in general.
    public abstract void hide();

}
