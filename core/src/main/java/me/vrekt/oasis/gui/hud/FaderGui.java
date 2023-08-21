package me.vrekt.oasis.gui.hud;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;


public final class FaderGui extends Gui {

    private final VisTable rootTable;

    public FaderGui(GameGui gui, Asset asset) {
        super(gui, asset, "fader");

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(asset.get("fade")));
        TableUtils.setSpacingDefaults(rootTable);

        gui.createContainer(rootTable).fill();
    }

    /**
     * Fade in
     */
    public void in(Runnable runWhenCompleted) {
        rootTable.getColor().a = 0.0f;
        rootTable.addAction(Actions.sequence(Actions.fadeIn(1.0f), Actions.run(runWhenCompleted)));
    }

    /**
     * Fade out
     */
    public void out() {

    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);
        //rootTable.getColor().a = 0.0f;
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
        // rootTable.getColor().a = 1.0f;
    }

}
