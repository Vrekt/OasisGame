package me.vrekt.oasis.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import me.vrekt.oasis.asset.Asset;

/**
 * Represents a basic gui element.
 */
public abstract class Gui {

    protected final GameGui gui;
    protected final Asset asset;
    protected final Skin skin;

    protected final GlyphLayout layout;
    protected final BitmapFont romulusSmall, romulusBig;

    protected boolean isShowing;

    public Gui(GameGui gui) {
        this.gui = gui;

        this.asset = gui.getAsset();
        this.skin = gui.getSkin();
        this.layout = gui.getLayout();
        this.romulusSmall = gui.getRomulusSmall();
        this.romulusBig = gui.getRomulusBig();
    }

    /**
     * Update this GUI element
     */
    public void update() {

    }

    /**
     * Resize the element
     */
    public void resize(int width, int height) {

    }

    /**
     * Show this GUI element
     */
    public void showGui() {
        isShowing = true;
    }

    /**
     * Hide this GUI element
     */
    public void hideGui() {
        isShowing = false;
    }

    /**
     * @return if this element is visible.
     */
    public boolean isVisible() {
        return isShowing;
    }
}
