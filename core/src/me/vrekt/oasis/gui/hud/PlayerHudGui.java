package me.vrekt.oasis.gui.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;

/**
 * Ethe/Coins display
 * Health
 * etc.
 */
public final class PlayerHudGui extends Gui {

    public static final int ID = 3;

    public PlayerHudGui(GameGui gui) {
        super(gui);

        isShowing = true;
        final Table root = new Table();
        gui.createContainer(root).right();
     //   root.add(new Label("Hello", gui.getSkin()));

    }
}
