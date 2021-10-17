package me.vrekt.oasis.ui.world;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Disposable;

public abstract class Gui extends InputAdapter implements Disposable {

    protected final WorldGui gui;

    public Gui(WorldGui gui) {
        this.gui = gui;
    }

    public void show(String text) {

    }

    public void show() {

    }

    public void clicked(float x, float y) {

    }

    public abstract void render(BitmapFont font, BitmapFont big, Batch batch, GlyphLayout layout);

    public void resize() {

    }

    public abstract void hide();

    public abstract boolean isShowing();

    @Override
    public void dispose() {

    }
}
