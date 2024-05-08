package me.vrekt.oasis.gui.guis.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.guis.inventory.utility.InventoryGuiSlot;
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
     * @param inventorySize        the inventory size
     * @param drawable             the theme
     * @param consumer             the acceptor
     * @param drawHotBarIndicators if the numbered hotbar should be labeled.
     */
    protected void populateInventoryUiComponents(GuiManager manager, int inventorySize,
                                                 TextureRegionDrawable drawable,
                                                 boolean drawHotBarIndicators,
                                                 Consumer<InventoryUiComponent> consumer) {
        final Label.LabelStyle style = new Label.LabelStyle(manager.getSmallFont(), Color.LIGHT_GRAY);

        for (int i = 0; i < inventorySize; i++) {
            // background image of the actual slot
            final VisImage slot = new VisImage(drawable);
            // the container for our item image
            final VisImage item = new VisImage();
            item.setOrigin(16 / 2f, 16 / 2f);

            // just holds our item image container
            final VisTable itemTable = new VisTable(false);
            itemTable.add(item).size(32, 32);

            final VisTable itemAmount = new VisTable(true);
            final VisLabel amountLabel = new VisLabel(StringUtils.EMPTY, style);

            amountLabel.setVisible(false);

            itemAmount.bottom().right();
            itemAmount.add(amountLabel).bottom().right().padBottom(4).padRight(4);

            // create a separate container for the item image... so it doesn't get stretched.
            final Stack overlay = new Stack(slot, itemTable);
            addParentListener(overlay, slot, drawable);

            // hotbar components
            if (i < 6 && drawHotBarIndicators) {
                final VisTable slotNumber = new VisTable(true);
                // i + 1 to represents slots 1-6 instead of 0-5
                final VisLabel slotNumberLabel = new VisLabel(Integer.toString(i + 1), style);
                slotNumber.top().left();
                slotNumber.add(slotNumberLabel).top().left().padLeft(2);
                overlay.add(slotNumber);
            }

            overlay.add(itemAmount);

            consumer.accept(new InventoryUiComponent(overlay, item, guiManager.getStyle().getTooltipStyle(), amountLabel, i));
        }
    }

    /**
     * Adds a listener to the parent stack to change the state of the slot image indicating when the mouse is over
     *
     * @param parent   the parent, used for listening
     * @param slot     the slot, used to change the drawable
     * @param drawable the initial drawable
     */
    private void addParentListener(Stack parent, VisImage slot, TextureRegionDrawable drawable) {
        parent.addListener(new ClickListener() {
            private boolean drawableChanged;

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!drawableChanged) {
                    slot.setDrawable(guiManager.getStyle().getThemeDownSelected());
                    drawableChanged = true;
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (drawableChanged) slot.setDrawable(drawable);
                drawableChanged = false;
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (guiManager.getHudComponent().isHintActive()) guiManager.getHudComponent().pauseCurrentHint();
    }

    @Override
    public void hide() {
        super.hide();
        if (guiManager.getHudComponent().isHintActive()) guiManager.getHudComponent().resumeCurrentHint();
    }

    /**
     * Represents the data within a slot... within an inventory ui
     */
    public record InventoryUiComponent(Stack overlay, VisImage item, Tooltip.TooltipStyle style, VisLabel amountLabel,
                                       int index) {
    }

}
