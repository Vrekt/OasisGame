package me.vrekt.oasis.gui.guis.other;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Styles;

public final class ShopGui extends Gui {

    public ShopGui(GuiManager guiManager) {
        super(GuiType.SHOP, guiManager);

        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(drawable("shop_ui"));

        final VisTable left = new VisTable(true);
        final VisTable right = new VisTable(true);
        right.top().padTop(36).padLeft(36).left();
        left.top().padTop(52).padLeft(24).left();

        createArtifactContainer(Styles.getTheme(), left);
        createArtifactContainer(Styles.getTheme(), left);
        createArtifactContainer(Styles.getTheme(), left);

        // Add split pane for left and right tables
        final VisSplitPane pane = new VisSplitPane(left, right, false);
        // only allow elements to be touched, since split pane
        // is programming choice for easier UI, the split pane should not be used
        pane.setTouchable(Touchable.enabled);
        rootTable.add(pane).fill().expand();

        guiManager.addGui(rootTable);
    }

    /**
     * Create separate containers for each artifact slot
     *
     * @param theme  the theme
     * @param parent the parent owner
     */
    private void createArtifactContainer(NinePatchDrawable theme, VisTable parent) {
        final VisImage background = new VisImage(theme);
        final VisTable table = new VisTable(true);
        final VisImage icon = new VisImage(asset("shop_potion_category"));
        table.add(icon).size(32, 32);

        final Stack stack = new Stack(background, new Container<Table>(table));
        parent.add(stack).size(48, 48);
        parent.row();
    }

}
