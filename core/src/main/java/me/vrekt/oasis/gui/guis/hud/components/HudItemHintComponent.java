package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;
import me.vrekt.oasis.item.utility.ItemDescriptor;

/**
 * Shows a hint when an item is required
 */
public final class HudItemHintComponent extends HudComponent {

    private final VisImage itemHintImage;

    public HudItemHintComponent(GuiManager manager) {
        super(HudComponentType.ITEM_HINT, manager);

        rootTable.setVisible(false);
        rootTable.bottom().padBottom(64);

        final VisImage keyImage = new VisImage(asset("ekey"));

        final VisImage slot = new VisImage(Styles.getTheme());
        itemHintImage = new VisImage();
        itemHintImage.setOrigin(16 / 2f, 16 / 2f);

        final VisTable itemTable = new VisTable(false);
        itemTable.add(itemHintImage).size(32, 32);

        final Stack overlay = new Stack(slot, itemTable);
        rootTable.add(keyImage);
        rootTable.row();
        rootTable.add(overlay).size(48, 48);

        guiManager.addGui(rootTable);
    }

    /**
     * Show an item hint required
     *
     * @param descriptor descriptor for the image
     */
    public void showItemHint(ItemDescriptor descriptor) {
        itemHintImage.setDrawable(new TextureRegionDrawable(asset(descriptor.texture())));
        rootTable.addAction(Actions.sequence(
                Actions.run(this::show),
                Actions.fadeIn(0.65f, Interpolation.linear)));
    }

    /**
     * Remove active item hint
     */
    public void removeItemHint() {
        rootTable.addAction(Actions.sequence(
                Actions.fadeOut(0.65f, Interpolation.linear),
                Actions.run(this::hide)));
    }


}
