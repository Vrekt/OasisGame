package me.vrekt.oasis.ui.world.pause;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import me.vrekt.oasis.ui.world.Gui;
import me.vrekt.oasis.ui.world.WorldGui;

public final class PauseGui extends Gui {

    private final Sprite background;
    private final Image pauseMenu;
    private boolean isPaused;

    public PauseGui(WorldGui gui) {
        super(gui);

        final Pixmap background = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB888);
        background.setColor(64 / 255f, 64 / 255f, 64 / 255f, 170 / 255f);
        background.fill();

        this.background = new Sprite(new Texture(background));
        this.background.setPosition(0, 0);
        this.background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.background.setColor(64 / 255f, 64 / 255f, 64 / 255f, 170 / 255f);
        background.dispose();

        pauseMenu = new Image(new Texture("ui/pause.png"));
        pauseMenu.setAlign(Align.left);
        pauseMenu.setVisible(false);
        gui.addElementToStack(pauseMenu, .5f * Gdx.graphics.getWidth(), .5f * Gdx.graphics.getWidth(), -128, 256);
    }

    @Override
    public void render(BitmapFont font, BitmapFont big, Batch batch, GlyphLayout layout) {
        if (isPaused) background.draw(batch);
    }

    @Override
    public void show() {
        isPaused = true;
        pauseMenu.setVisible(true);
    }

    @Override
    public void hide() {
        isPaused = false;
        pauseMenu.setVisible(false);
    }

    @Override
    public boolean isShowing() {
        return isPaused;
    }
}
