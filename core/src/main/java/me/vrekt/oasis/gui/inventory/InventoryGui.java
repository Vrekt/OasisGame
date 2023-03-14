package me.vrekt.oasis.gui.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.consumables.ItemConsumable;
import me.vrekt.oasis.item.attribute.ItemAttribute;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * Inventory Gui
 */
public final class InventoryGui extends Gui {
    private final VisTable rootTable;
    private final OasisPlayerSP player;

    private final LinkedList<InventoryUiSlot> slots = new LinkedList<>();

    // the item description of whatever item is clicked + attrs
    private final TypingLabel itemDescription, itemAttributesText, itemAttributes;
    private final VisTextButton useItemButton;
    // the current item clicked on
    private Item clickedItem;

    public InventoryGui(GameGui gui, Asset asset) {
        super(gui, asset, "inventory");
        this.player = gui.getGame().getPlayer();

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        TableUtils.setSpacingDefaults(rootTable);

        rootTable.setBackground(new TextureRegionDrawable(asset.get("book_tab")));
        final VisTable primary = new VisTable(true);
        final VisTable secondary = new VisTable(true);

        secondary.top().padTop(52).padLeft(32).left();
        this.itemDescription = new TypingLabel("", new Label.LabelStyle(gui.getMedium(), Color.BLACK));
        this.itemDescription.setVisible(false);
        this.itemDescription.setWrap(true);
        this.itemDescription.setWidth(175);
        secondary.add(itemDescription).width(175).left();
        secondary.row();

        this.itemAttributes = new TypingLabel(StringUtils.EMPTY, new Label.LabelStyle(gui.getMedium(), Color.BLACK));
        this.itemAttributes.setVisible(false);
        this.itemAttributesText = new TypingLabel("[BLACK]-- Attributes --", new Label.LabelStyle(gui.getMedium(), Color.BLACK));
        this.itemAttributesText.setVisible(false);

        secondary.add(itemAttributesText).padTop(16);
        secondary.row();
        secondary.add(itemAttributes).padTop(16).left();
        secondary.row();

        useItemButton = new VisTextButton(StringUtils.EMPTY);
        useItemButton.setLabel(new VisLabel(StringUtils.EMPTY, new Label.LabelStyle(gui.getMedium(), Color.WHITE)));
        useItemButton.setVisible(false);
        secondary.add(useItemButton).padTop(16).left();

        // add use item button listener
        useItemButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (clickedItem != null) {
                    clickedItem.useItem(player);
                }
            }
        });

        // populate a total of 21 inventory slots
        primary.top().padTop(52).padLeft(84);
        final TextureRegionDrawable drawable = new TextureRegionDrawable(asset.get("inventory_slot"));
        for (int i = 1; i < 22; i++) {
            final VisImage slot = new VisImage(drawable);
            final VisImage item = new VisImage();

            // create a separate container for the item image... so it doesn't get stretched.
            final VisTable itemTable = new VisTable(true);
            itemTable.add(item);
            final Stack overlay = new Stack(slot, new Container<Table>(itemTable));

            this.slots.add(new InventoryUiSlot(overlay, item));
            primary.add(overlay).size(48, 48);
            if (i % 3 == 0) primary.row();
        }

        VisSplitPane splitPane = new VisSplitPane(primary, secondary, false);
        rootTable.add(splitPane).fill().expand();

        rootTable.pack();
        gui.createContainer(rootTable).fill();
    }

    @Override
    public void update() {
        // update ui based on player inventory status
        player.getInventory().getSlots().forEach((slot, item) -> {
            final InventoryUiSlot ui = slots.get(slot);
            // only update this inventory slot IF the last item does not match the current
            if (ui.lastItemId != item.getItem().getItemId()) ui.setItem(item.getItem());
        });
    }

    @Override
    public void show() {
        super.show();
        gui.hideGui(GuiType.HUD);
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        gui.showGui(GuiType.HUD);
        rootTable.setVisible(false);
    }

    /**
     * Remove an item from the UI
     *
     * @param slot slot number
     */
    public void removeItemSlot(int slot) {
        slots.get(slot).removeItem();
        useItemButton.setVisible(false);
        itemAttributesText.setVisible(false);
        itemAttributes.setVisible(false);
        itemDescription.setVisible(false);
    }

    /**
     * Handles data required for the UI inventory slot
     */
    private final class InventoryUiSlot {
        private final VisImage imageItem;
        private final Tooltip tooltip;

        private boolean occupied;
        // item description of whatever is in this slot
        private String itemDescription = StringUtils.EMPTY;
        // the last item in this slot, for comparison when updating
        private long lastItemId = -1;
        private Item item;

        public InventoryUiSlot(Stack stack, VisImage item) {
            this.imageItem = item;
            tooltip = new Tooltip.Builder("Empty Slot").target(stack).build();
            tooltip.setAppearDelayTime(0.35f);

            // add click action
            stack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!occupied) return;

                    // hide optional
                    useItemButton.setVisible(false);
                    itemAttributesText.setVisible(false);
                    itemAttributes.setVisible(false);

                    InventoryGui.this.itemDescription.setText("[BLACK]" + itemDescription);
                    InventoryGui.this.itemDescription.restart();
                    if (!InventoryGui.this.itemDescription.isVisible()) {
                        InventoryGui.this.itemDescription.setVisible(true);
                    }

                    // update consume buttons, only if allowed and an actual item to eat
                    if (InventoryUiSlot.this.item instanceof ItemConsumable
                            && ((ItemConsumable) InventoryUiSlot.this.item).isAllowedToConsume()) {
                        useItemButton.setVisible(true);
                        useItemButton.setText("Consume");
                    }

                    // add attributes
                    if (InventoryUiSlot.this.item.hasAttributes()) {
                        itemAttributesText.setVisible(true);
                        itemAttributesText.restart();
                        itemAttributes.setVisible(true);
                        for (ItemAttribute attribute : InventoryUiSlot.this.item.getAttributes().values()) {
                            itemAttributes.setText(attribute.getAttributeName() + "\n" + attribute.getDescription());
                        }
                        itemAttributes.restart();
                    }

                    InventoryGui.this.clickedItem = InventoryUiSlot.this.item;
                }
            });

        }

        /**
         * Set all data about the given item
         *
         * @param item the item
         */
        void setItem(Item item) {
            this.imageItem.setDrawable(new TextureRegionDrawable(item.getTexture()));
            this.lastItemId = item.getItemId();
            this.item = item;

            this.tooltip.setText(item.getItemName());
            this.itemDescription = item.getDescription();
            this.occupied = true;
        }

        /**
         * Remove all data about this item
         */
        void removeItem() {
            this.occupied = false;
            this.imageItem.setDrawable((Drawable) null);
            this.itemDescription = StringUtils.EMPTY;
            this.tooltip.setText("Empty Slot");
            this.lastItemId = -1;
            this.item = null;
        }

    }

}
