package me.vrekt.oasis.ui.book.pages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ui.book.BookPage;

import java.util.ArrayList;
import java.util.List;

/**
 * The players inventory page UI.
 */
public final class InventoryBookPage extends BookPage {

    private GlyphLayout layout;
    private List<Rectangle> rectangles = new ArrayList<>();

    public InventoryBookPage(OasisGame game, TextureAtlas atlas) {
        super(1, "Inventory");

        this.currentTabTexture = atlas.findRegion("book_tab", 2);

        //    final float startingX = bookEdgeX * 1.55f;
        final float startingY = currentTabTexture.getRegionHeight() * 1.4f;


    }

    @Override
    public void render(Batch batch, BitmapFont big, BitmapFont small, float x, float y) {
        if (layout == null) layout = new GlyphLayout(big, title);
        big.setColor(Color.GRAY);
        //  big.draw(batch, title, bookEdgeX * 1.5f, currentTabTexture.getRegionHeight() * 1.7f);


    }

    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    @Override
    public void hide() {

    }

    @Override
    public void handleClick(float x, float y) {

    }

}
