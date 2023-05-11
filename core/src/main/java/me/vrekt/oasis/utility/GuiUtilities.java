package me.vrekt.oasis.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.entity.inventory.Inventory;
import me.vrekt.oasis.gui.GameGui;

import java.util.function.Consumer;

public class GuiUtilities {

    public static void populateInventoryComponents(Inventory inventory,
                                                   TextureRegionDrawable drawable,
                                                   GameGui gui,
                                                   Consumer<UiSlotAcceptor> consumer) {
        for (int i = 1; i < inventory.getInventorySize(); i++) {
            // background image of the actual slot
            final Image slot = new Image(drawable);
            // the container for our item image
            final Image item = new Image();
            item.setOrigin(16 / 2f, 16 / 2f);

            // just holds our item image container
            final VisTable itemTable = new VisTable(false);
            itemTable.add(item);

            final VisTable itemAmount = new VisTable(true);
            final VisLabel amountLabel = new VisLabel("", new Label.LabelStyle(gui.getSmall(), Color.LIGHT_GRAY));
            amountLabel.setVisible(false);

            itemAmount.bottom().right();
            itemAmount.add(amountLabel).bottom().right().padBottom(4).padRight(4);

            // create a separate container for the item image... so it doesn't get stretched.
            final Stack overlay = new Stack(slot, itemTable);
            overlay.add(itemAmount);

            consumer.accept(new UiSlotAcceptor(overlay, item, gui.getStyles().getTooltipStyle(), amountLabel, i));
        }
    }

    public static final class UiSlotAcceptor {
        public final Stack overlay;
        public final Image item;
        public final Tooltip.TooltipStyle style;
        public final VisLabel amountLabel;
        public final int index;

        public UiSlotAcceptor(Stack overlay, Image item, Tooltip.TooltipStyle style, VisLabel amountLabel, int index) {
            this.overlay = overlay;
            this.item = item;
            this.style = style;
            this.amountLabel = amountLabel;
            this.index = index;
        }
    }

}
