package me.vrekt.oasis.gui.guis.inventory.utility;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.guis.inventory.InventoryGui;
import me.vrekt.oasis.item.Item;

/**
 * Inventory slot for representing slots within the GUI
 */
public class InventoryGuiSlot {

    private static final String EMPTY_SLOT = "Empty Slot";
    private static final float APPEAR_DELAY = 0.35f;

    protected Stack parent;
    protected VisImage slotIcon;
    protected VisLabel amountText;
    protected Tooltip tooltip;
    protected Item item;
    protected boolean occupied, isHotbarSlot;
    private String lastItemKey;
    private final int slotNumber;

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

    public boolean isHotbarSlot() {
        return isHotbarSlot;
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
     * Set the occupied item that is in this slot
     *
     * @param item the item
     */
    public void setOccupiedItem(Item item) {
        this.item = item;
        this.lastItemKey = item.getKey();

        slotIcon.setDrawable(new TextureRegionDrawable(item.getSprite()));
        slotIcon.setScale(item.getSprite().getScaleX(), item.getSprite().getScaleY());

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
