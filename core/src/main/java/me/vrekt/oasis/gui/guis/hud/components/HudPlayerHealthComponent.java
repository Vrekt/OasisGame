package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.ui.widget.VisImage;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;

/**
 * The players health bar
 */
public final class HudPlayerHealthComponent extends HudComponent {

    public HudPlayerHealthComponent(GuiManager manager) {
        super(HudComponentType.HEALTH, manager);

        rootTable.setVisible(true);
        rootTable.top().right();

        final TextureRegion region = asset("heart", 1);
        for (int i = 0; i < 5; i++) {
            final VisImage heart = new VisImage(region);
            rootTable.add(heart).size(region.getRegionWidth() * 2f, region.getRegionHeight() * 2f).padLeft(2);
        }

        guiManager.addGui(rootTable);
    }
}
