package me.vrekt.oasis.gui.guis.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
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

    protected TextureRegionDrawable draggingItem;
    protected int draggingSlot;
    protected float dragX, dragY, dragWidth, dragHeight;

    public abstract void handleSlotClicked(InventoryGuiSlot slot);

    public InventoryGui(GuiType type, GuiManager guiManager) {
        super(type, guiManager);
    }

    /**
     * Item started being dragged
     *
     * @param slot slot
     * @param x    first X
     * @param y    first Y
     */
    public void itemDragStarted(InventoryGuiSlot slot, float x, float y) {
        draggingItem = new TextureRegionDrawable((TextureRegionDrawable) slot.getSlotIcon().getDrawable());
        draggingSlot = slot.getSlotNumber();
        dragWidth = slot.getSlotIcon().getImageWidth() * slot.getSlotIcon().getScaleX();
        dragHeight = slot.getSlotIcon().getImageHeight() * slot.getSlotIcon().getScaleY();
        dragX = x - (dragWidth / 2f);
        dragY = y - (dragHeight / 2f);
    }

    /**
     * Update drag position
     *
     * @param x x
     * @param y y
     */
    public void updateItemDragPosition(float x, float y) {
        dragX = Math.round(x - (dragWidth / 2f));
        dragY = Math.round(y - (dragHeight / 2f));
    }

    /**
     * An item drag was cancelled
     */
    public void itemDragCancelled() {
        draggingItem = null;
    }

    /**
     * An item was transferred between slots
     *
     * @param from the from slot
     * @param to   the to slot
     */
    public void itemTransferred(int from, int to) {
        draggingItem = null;
    }

    /**
     * Item was transferred between inventories
     *
     * @param isContainerTransfer if this was a container -> player transfer
     * @param from                from slot
     * @param to                  to slot
     */
    public void itemTransferredBetweenInventories(boolean isContainerTransfer, int from, int to) {
        draggingItem = null;
    }

    /**
     * An item stack was transferred between inventories
     *
     * @param isContainerTransfer if this was a container -> player transfer
     * @param from                from slot
     * @param to                  to slot
     */
    public void itemSwappedBetweenInventories(boolean isContainerTransfer, int from, int to) {
        draggingItem = null;
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

    @Override
    public void draw(Batch batch) {
        if (draggingItem != null) {
            draggingItem.draw(batch, dragX, dragY, 0, 0, dragWidth, dragHeight, 2.0f, 2.0f, 1f);
        }
    }

    /**
     * Represents the data within a slot... within an inventory ui
     */
    public record InventoryUiComponent(Stack overlay, VisImage item, Tooltip.TooltipStyle style, VisLabel amountLabel,
                                       int index) {
    }

}
