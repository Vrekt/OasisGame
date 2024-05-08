package me.vrekt.oasis.gui.guis.inventory.utility;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import me.vrekt.oasis.entity.inventory.Inventory;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.guis.inventory.InventoryGui;
import me.vrekt.oasis.item.Item;

/**
 * Represents a gui slot that should be extended depending on if it's a container or player inventory
 */
public final class InventoryGuiSlot {

    private static final String EMPTY_SLOT = "Empty Slot";
    private static final float APPEAR_DELAY = 0.35f;

    private final Stack parent;
    private final VisImage slotIcon;
    private final VisLabel amountText;
    private final Tooltip tooltip;
    private Item item;
    private boolean occupied, isHotbarSlot, isContainerSlot;
    private String lastItemKey;
    private final int slotNumber;

    public InventoryGuiSlot(GuiManager manager, InventoryGui owner, Stack parent, VisImage slotIcon, VisLabel amountText, int slotNumber) {
        this(manager, owner, parent, slotIcon, amountText, false, slotNumber);
        this.isContainerSlot = true;
    }

    public InventoryGuiSlot(GuiManager guiManager, InventoryGui owner, Stack parent, VisImage slotIcon, VisLabel amountText, boolean isHotbarSlot, int slotNumber) {
        this.parent = parent;
        this.slotIcon = slotIcon;
        this.amountText = amountText;
        this.tooltip = new Tooltip.Builder(EMPTY_SLOT)
                .style(guiManager.getStyle().getTooltipStyle())
                .target(parent)
                .build();
        this.tooltip.setAppearDelayTime(APPEAR_DELAY);
        parent.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (occupied) owner.handleSlotClicked(InventoryGuiSlot.this);
            }
        });
        this.isHotbarSlot = isHotbarSlot;
        this.slotNumber = slotNumber;
        slotIcon.setUserObject(slotNumber);
    }

    public boolean isContainerSlot() {
        return isContainerSlot;
    }

    public boolean isEmpty() {
        return this.item == null;
    }

    public Stack getParent() {
        return parent;
    }

    public VisImage getSlotIcon() {
        return slotIcon;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public Item getItem() {
        return item;
    }

    public String getLastItemKey() {
        return lastItemKey;
    }

    /**
     * Update this slot when a transfer has occurred
     *
     * @param owner the owner inventory
     * @param slot  the new slot
     * @return if the item was fully transferred and thus removed
     */
    public boolean updateTransfer(Inventory owner, int slot) {
        final Item item = owner.getItem(slot);
        if (item == null || item.getAmount() <= 0) {
            resetSlot();
            return true;
        } else if (this.item == null || !item.is(this.item)) {
            // this item is new, so just reset entirely.
            // not sure if this will ever happen but maybe.
            setOccupiedItem(item);
            return false;
        }

        amountText.setText(item.getAmount());
        return false;
    }


    /**
     * Set the occupied item that is in this slot
     *
     * @param item the item
     */
    public void setOccupiedItem(Item item) {
        if (item == null) {
            resetSlot();
            return;
        }

        this.item = item;
        this.lastItemKey = item.getKey();

        // TODO: Cache drawables?
        slotIcon.setDrawable(new TextureRegionDrawable(item.getSprite()));
        slotIcon.setScale(item.getScaleSize());

        if (item.isStackable()) {
            amountText.setVisible(true);
            amountText.setText(item.getAmount());
        } else {
            amountText.setVisible(false);
        }

        tooltip.setText(item.getItemName());
        occupied = true;
    }

    /**
     * Reset this slot
     */
    public void resetSlot() {
        occupied = false;
        lastItemKey = null;
        slotIcon.setDrawable((Drawable) null);
        tooltip.setText(EMPTY_SLOT);
        item = null;

        amountText.setVisible(false);
    }

}
