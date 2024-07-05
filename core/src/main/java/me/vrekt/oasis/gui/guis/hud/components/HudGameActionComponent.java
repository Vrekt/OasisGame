package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kotcrab.vis.ui.widget.VisImage;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;

/**
 * Shows game actions like saving
 */
public final class HudGameActionComponent extends HudComponent {

    private final VisImage gameActionImage;

    public HudGameActionComponent(GuiManager manager) {
        super(HudComponentType.GAME_ACTION, manager);

        rootTable.top().padTop(48).right().padTop(8).padRight(8);

        gameActionImage = new VisImage(asset("saving_icon"));
        gameActionImage.setVisible(false);
        rootTable.add(gameActionImage).size(32, 32);
        guiManager.addGui(rootTable);
    }

    /**
     * Show saving icon
     */
    public void showSavingIcon() {
        show();

        gameActionImage.setVisible(true);
        gameActionImage.getColor().a = 0.0f;
        // add a sort of "fake" saving animation, since saving only takes very few ms.
        gameActionImage.addAction(Actions.sequence(
                Actions.alpha(1.0f, 1f),
                Actions.delay(0.25f),
                Actions.alpha(0.1f, 1f),
                Actions.delay(0.25f),
                Actions.alpha(1.0f, 1f),
                Actions.delay(0.25f),
                Actions.alpha(0.1f, 1.0f),
                Actions.visible(false),
                Actions.run(this::hide)));
    }

}
