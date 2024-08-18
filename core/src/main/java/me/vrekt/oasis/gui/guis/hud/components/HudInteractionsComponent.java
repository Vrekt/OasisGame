package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;
import org.apache.commons.lang3.StringUtils;

/**
 * Interactions within the world.
 * E to enter, etc.
 */
public final class HudInteractionsComponent extends HudComponent {

    private final VisImage keyImage;
    private final VisLabel interaction;
    private int activeKeyImage;

    public HudInteractionsComponent(GuiManager manager) {
        super(HudComponentType.INTERACTIONS, manager);

        rootTable.setVisible(false);
        rootTable.bottom().left().padLeft(8).padBottom(8);

        final NinePatch patch = new NinePatch(asset("theme_transparent"), 6, 6, 6, 6);
        final NinePatchDrawable transparent = new NinePatchDrawable(patch);

        final VisTable container = new VisTable();
        container.setBackground(transparent);

        keyImage = new VisImage(asset("ekey"));
        activeKeyImage = Input.Keys.E;
        interaction = new VisLabel(StringUtils.EMPTY, Styles.getMediumWhiteMipMapped());

        container.add(keyImage);
        container.add(interaction).padBottom(2).padLeft(5f);

        rootTable.add(container).center();
        guiManager.addGui(rootTable);
    }


    /**
     * Show the enter interaction for interiors.
     */
    public void showEnterInteraction() {
        if (activeKeyImage != Input.Keys.E) {
            keyImage.setDrawable(new TextureRegionDrawable(asset("ekey")));
        }

        activeKeyImage = Input.Keys.E;
        interaction.setText("Enter");

        fadeIn(rootTable, 0.5f);
        isShowing = true;
    }

    /**
     * Show lockpicking interaction
     */
    public void showLockpickInteraction() {
        if (activeKeyImage != Input.Keys.E) {
            keyImage.setDrawable(new TextureRegionDrawable(asset("ekey")));
        }
        activeKeyImage = Input.Keys.E;
        interaction.setText("Lockpick");
        fadeIn(rootTable, 0.5f);
        isShowing = true;
    }

    /**
     * Show the speaking interaction for an entity.
     */
    public void showSpeakingInteraction() {
        if (activeKeyImage != Input.Keys.E) {
            keyImage.setDrawable(new TextureRegionDrawable(asset("ekey")));
        }

        activeKeyImage = Input.Keys.E;
        interaction.setText("Talk");
        fadeIn(rootTable, 0.5f);
        isShowing = true;
    }

    @Override
    public void hide() {
        fadeOut(rootTable, 0.5f);
        isShowing = false;
    }
}
