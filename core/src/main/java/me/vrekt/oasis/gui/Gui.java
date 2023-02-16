package me.vrekt.oasis.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.widget.VisWindow;
import me.vrekt.oasis.asset.game.Asset;

/**
 * Represents a basic gui element.
 */
public abstract class Gui extends VisWindow {

    protected final GameGui gui;
    protected final Asset asset;
    protected final Skin skin;

    protected final GlyphLayout layout;
    protected final BitmapFont romulusSmall, romulusBig;

    protected boolean isShowing;

    public Gui(GameGui gui, Asset asset) {
        super("test");
        this.gui = gui;

        this.asset = asset;
        this.skin = gui.getSkin();
        this.layout = gui.getLayout();
        this.romulusSmall = gui.getMedium();
        this.romulusBig = gui.getLarge();
    }

    public Gui(GameGui gui, Asset asset, String title) {
        super(title);
        this.gui = gui;

        this.asset = asset;
        this.skin = gui.getSkin();
        this.layout = gui.getLayout();
        this.romulusSmall = gui.getMedium();
        this.romulusBig = gui.getLarge();
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
