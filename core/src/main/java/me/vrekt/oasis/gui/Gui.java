package me.vrekt.oasis.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.widget.VisWindow;
import me.vrekt.oasis.asset.game.Asset;
import org.apache.commons.lang3.StringUtils;

/**
 * A base for all new gui types
 */
public abstract class Gui extends VisWindow {


    protected final GameGui gui;
    protected final Asset asset;
    protected final Skin skin;

    protected boolean isShowing;

    public Gui(GameGui gui, Asset asset, String windowTitle) {
        super(windowTitle);
        this.gui = gui;

        this.asset = asset;
        this.skin = gui.getSkin();
    }

    public Gui(GameGui gui, Asset asset) {
        super(StringUtils.EMPTY);
        this.gui = gui;

        this.asset = asset;
        this.skin = gui.getSkin();
    }

    public void update() {

    }

    public void draw() {

    }

    /**
     * Resize the element
     */
    public void resize(int width, int height) {

    }

    /**
     * Show this GUI element
     */
    public void show() {
        isShowing = true;
    }

    /**
     * Hide this GUI element
     */
    public void hide() {
        isShowing = false;
    }

    public void hideRelatedGuis() {

    }

    /**
     * @return if this element is visible.
     */
    public boolean isGuiVisible() {
        return isShowing;
    }
}
