package me.vrekt.oasis.ui.world.book;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ui.book.PlayerBook;
import me.vrekt.oasis.ui.world.Gui;
import me.vrekt.oasis.ui.world.WorldGui;

public final class PlayerBookGui extends Gui {

    private final PlayerBook book;

    public PlayerBookGui(OasisGame game, WorldGui gui) {
        super(gui);
        this.book = new PlayerBook(game, gui.getAsset());

        gui.addElementToStack(book.getImage(), .5f * Gdx.graphics.getWidth(), .5f * Gdx.graphics.getHeight());
    }

    @Override
    public void render(BitmapFont font, BitmapFont big, Batch batch, GlyphLayout layout) {
        book.render(batch, font);
    }

    @Override
    public void clicked(float x, float y) {
        book.handleClick(x, y);
    }

    @Override
    public void resize() {
        book.resize();
    }

    @Override
    public void show() {
        book.getImage().setVisible(true);
    }

    @Override
    public void hide() {
        book.getImage().setVisible(false);
    }

    @Override
    public boolean isShowing() {
        return book.getImage().isVisible();
    }
}
