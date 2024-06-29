package me.vrekt.oasis.gui.guis.inventory.utility;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.gui.guis.inventory.InventoryGui;
import me.vrekt.oasis.gui.guis.inventory.actions.InventorySlotTarget;
import me.vrekt.oasis.gui.guis.inventory.style.ItemSlotStyle;
import me.vrekt.oasis.item.Item;

/**
 * Represents a gui slot that should be extended depending on if it's a container or player inventory
 */
public final class InventoryGuiSlot {

    private static final String EMPTY_SLOT = "Empty Slot";
    private static final float APPEAR_DELAY = 0.35f;

    private final Stack container;
    private final VisImage background;
    private final VisImage item;
    private final VisLabel amountText;
    private final Tooltip tooltip;

    private Item itemInSlot;
    private boolean occupied, isContainerSlot;
    private String lastItemKey;
    private final int slotNumber;

    private InventorySlotTarget target;
    private ItemSlotStyle slotStyle = ItemSlotStyle.NORMAL;

    /**
     * Initialize
     *
     * @param guiManager gui manager
     * @param owner      the owner of this slot
     * @param container  the parent container
     * @param background the background slot image
     * @param item       the slot item image
     * @param amountText the amount text label
     * @param slotNumber the slot number
     */
    private InventoryGuiSlot(GuiManager guiManager,
                             InventoryGui owner,
                             Stack container,
                             VisImage background,
                             VisImage item,
                             VisLabel amountText,
                             int slotNumber) {
        this.container = container;
        this.item = item;
        this.background = background;
        this.amountText = amountText;
        this.tooltip = new Tooltip.Builder(EMPTY_SLOT)
                .style(Styles.getTooltipStyle())
                .target(container)
                .build();
        this.tooltip.setAppearDelayTime(APPEAR_DELAY);
        container.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (occupied) owner.handleSlotClicked(InventoryGuiSlot.this);
            }
        });

        this.slotNumber = slotNumber;
        item.setUserObject(slotNumber);
    }

    public InventoryGuiSlot(GuiManager manager, InventoryGui owner, InventoryGui.InventoryUiComponent component, int slot) {
        this(manager, owner, component.container(), component.background(), component.item(), component.amountLabel(), slot);
    }

    public void setTarget(InventorySlotTarget target) {
        this.target = target;
    }

    public InventorySlotTarget getTarget() {
        return target;
    }

    /**
     * Set if this slot is a container slot
     *
     * @param containerSlot state
     */
    public void setContainerSlot(boolean containerSlot) {
        isContainerSlot = containerSlot;
    }

    /**
     * @return {@code true} if this slot is within a container
     */
    public boolean isContainerSlot() {
        return isContainerSlot;
    }

    public boolean isEmpty() {
        return this.itemInSlot == null;
    }

    public Stack getContainer() {
        return container;
    }

    public VisImage getSlotIcon() {
        return item;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public Item getItem() {
        return itemInSlot;
    }

    public String getLastItemKey() {
        return lastItemKey;
    }

    public Drawable getSlotStyle(boolean isMouseOver) {
        return isMouseOver ? slotStyle.down() : slotStyle.get();
    }

    /**
     * Update this slot when a transfer has occurred
     *
     * @param owner the owner inventory
     * @param slot  the new slot
     */
    public void updateTransfer(AbstractInventory owner, int slot) {
        final Item item = owner.get(slot);
        if (item == null || item.amount() <= 0) {
            resetSlot();
            return;
        } else if (this.itemInSlot == null || !item.compare(this.itemInSlot)) {
            // this item is new, so just reset entirely.
            // not sure if this will ever happen but maybe.
            setOccupiedItem(item);
            return;
        }

        amountText.setText(item.amount());
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

        this.itemInSlot = item;
        this.lastItemKey = item.key();

        // task: (TODO-26) Cache drawables
        this.item.setDrawable(new TextureRegionDrawable(item.sprite()));
        this.item.setScale(item.scale());

        // EM-103, items that are smaller that require scaling to look right will be offset out of the slot
        this.item.setOrigin(item.sprite().getRegionWidth() / 2f, item.sprite().getRegionHeight() / 2f);

        updateBackground();

        if (item.isStackable()) {
            amountText.setVisible(true);
            amountText.setText(item.amount());
        } else {
            amountText.setVisible(false);
        }

        tooltip.setText(item.name());
        occupied = true;
    }

    /**
     * Update background slot based on the item
     */
    private void updateBackground() {
        this.slotStyle = ItemSlotStyle.of(itemInSlot);
        background.setDrawable(slotStyle.get());
    }

    /**
     * Reset item slot  image
     */
    private void resetItemSlot() {
        item.setDrawable((Drawable) null);
    }

    /**
     * Reset background slot
     */
    private void resetBackground() {
        this.slotStyle = ItemSlotStyle.NORMAL;
        background.setDrawable(slotStyle.get());
    }

    /**
     * Reset this slot
     */
    public void resetSlot() {
        occupied = false;
        lastItemKey = null;

        resetItemSlot();
        resetBackground();

        tooltip.setText(EMPTY_SLOT);
        itemInSlot = null;

        amountText.setVisible(false);
    }

}
