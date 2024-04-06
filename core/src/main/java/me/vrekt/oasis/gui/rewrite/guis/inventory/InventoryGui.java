package me.vrekt.oasis.gui.rewrite.guis.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.entity.inventory.Inventory;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.rewrite.Gui;
import me.vrekt.oasis.gui.rewrite.GuiManager;
import me.vrekt.oasis.gui.rewrite.guis.inventory.utility.InventoryGuiSlot;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

/**
 * Inventory GUi for players or containers
 */
public abstract class InventoryGui extends Gui {

    public abstract void handleSlotClicked(InventoryGuiSlot slot);

    public InventoryGui(GuiType type, GuiManager guiManager) {
        super(type, guiManager);
    }

    /**
     * Populate inventory UI (slots) components
     *
     * @param inventory the inventory
     * @param drawable  the theme
     * @param consumer  the acceptor
     */
    public void populateInventoryUiComponents(GuiManager manager, Inventory inventory,
                                              TextureRegionDrawable drawable,
                                              Consumer<InventoryUiComponent> consumer) {
        final Label.LabelStyle style = new Label.LabelStyle(manager.getSmallFont(), Color.LIGHT_GRAY);

        for (int i = 0; i < inventory.getInventorySize(); i++) {
            // background image of the actual slot
            final VisImage slot = new VisImage(drawable);
            // the container for our item image
            final VisImage item = new VisImage();
            item.setOrigin(16 / 2f, 16 / 2f);

            // just holds our item image container
            final VisTable itemTable = new VisTable(false);
            itemTable.add(item);

            final VisTable itemAmount = new VisTable(true);
            final VisLabel amountLabel = new VisLabel(StringUtils.EMPTY, style);

            amountLabel.setVisible(false);

            itemAmount.bottom().right();
            itemAmount.add(amountLabel).bottom().right().padBottom(4).padRight(4);

            // create a separate container for the item image... so it doesn't get stretched.
            final Stack overlay = new Stack(slot, itemTable);
            // hotbar components
            if (i <= 6) {
                final VisTable slotNumber = new VisTable(true);
                final VisLabel slotNumberLabel = new VisLabel(Integer.toString(i), style);
                slotNumber.top().left();
                slotNumber.add(slotNumberLabel).top().left().padLeft(2);
                overlay.add(slotNumber);
            }

            overlay.add(itemAmount);

            consumer.accept(new InventoryUiComponent(overlay, item, guiManager.getStyle().getTooltipStyle(), amountLabel, i));
        }
    }

    /**
         * Represents the data within a slot... within an inventory ui
         */
        public record InventoryUiComponent(Stack overlay, VisImage item, Tooltip.TooltipStyle style, VisLabel amountLabel,
                                           int index) {
    }

}
