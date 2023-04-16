package me.vrekt.oasis.gui.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
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
import me.vrekt.oasis.item.ItemEquippable;
import me.vrekt.oasis.item.consumables.ItemConsumable;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * Inventory Gui
 */
public final class InventoryGui extends Gui {
    private final VisTable rootTable;
    private final OasisPlayerSP player;
    final VisSplitPane splitPane;

    private final LinkedList<InventoryUiSlot> slots = new LinkedList<>();

    // the item description of whatever item is clicked
    private final TypingLabel itemDescription;
    private final VisTextButton useItemButton;

    private final VisImage weaponRangeImage, weaponDamageIcon, weaponCritIcon;
    private final Tooltip weaponRangeTooltip, weaponDamageTooltip, weaponCritTooltip;

    // the current item clicked on
    private Item clickedItem;

    public InventoryGui(GameGui gui, Asset asset) {
        super(gui, asset, "inventory");
        this.player = gui.getGame().getPlayer();

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        TableUtils.setSpacingDefaults(rootTable);

        rootTable.setBackground(new TextureRegionDrawable(asset.get("inventory")));
        final VisTable primary = new VisTable(true);
        final VisTable secondary = new VisTable(true);

        secondary.top().padTop(52).padLeft(32).left();
        this.itemDescription = new TypingLabel("", new Label.LabelStyle(gui.getMedium(), Color.BLACK));
        this.itemDescription.setVisible(false);
        this.itemDescription.setWrap(true);
        this.itemDescription.setWidth(175);
        secondary.add(itemDescription).width(175).left();
        secondary.row().padTop(16);

        final VisTable attributes = new VisTable(true);
        attributes.left();

        attributes.add(weaponRangeImage = new VisImage(asset.get("weapon_range_icon"))).size(36, 36);
        attributes.add(weaponDamageIcon = new VisImage(asset.get("weapon_damage_icon"))).size(36, 36);
        attributes.add(weaponCritIcon = new VisImage(asset.get("weapon_crit_icon"))).size(36, 36);
        secondary.add(attributes).left();
        secondary.row();

        final NinePatch patch = new NinePatch(asset.get("artifact_slot"), 4, 4, 4, 4);
        final NinePatchDrawable drawable = new NinePatchDrawable(patch);
        final Tooltip.TooltipStyle style = new Tooltip.TooltipStyle(drawable);

        weaponRangeTooltip = new Tooltip.Builder(StringUtils.EMPTY).target(weaponRangeImage).style(style).build();
        weaponDamageTooltip = new Tooltip.Builder(StringUtils.EMPTY).target(weaponDamageIcon).style(style).build();
        weaponCritTooltip = new Tooltip.Builder(StringUtils.EMPTY).target(weaponCritIcon).style(style).build();

        hideWeaponStats();

        useItemButton = new VisTextButton(StringUtils.EMPTY);
        useItemButton.setLabel(new VisLabel(StringUtils.EMPTY, new Label.LabelStyle(gui.getMedium(), Color.WHITE)));
        useItemButton.setStyle(new TextButton.TextButtonStyle(drawable, drawable, drawable, gui.getMedium()));
        useItemButton.setVisible(false);
        secondary.add(useItemButton).padTop(16).left();

        // add use item button listener
        useItemButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (clickedItem instanceof ItemConsumable) {
                    clickedItem.useItem(player);
                } else if (clickedItem instanceof ItemEquippable) {
                    ((ItemEquippable) clickedItem).equip(player);
                }
            }
        });

        // populate a total of 21 inventory slots
        primary.top().padTop(52).padLeft(84);
        final TextureRegionDrawable slotDrawable = new TextureRegionDrawable(asset.get("artifact_slot"));
        for (int i = 1; i < player.getInventory().getInventorySize(); i++) {
            // background image of the actual slot
            final Image slot = new Image(slotDrawable);
            // the container for our item image
            final Image item = new Image();
            item.setOrigin(16 / 2f, 16 / 2f);

            // just holds our item image container
            final VisTable itemTable = new VisTable(false);
            itemTable.add(item);

            // create a separate container for the item image... so it doesn't get stretched.
            final Stack overlay = new Stack(slot, itemTable);

            // add all of this to a list that stores our UI slots.
            this.slots.add(new InventoryUiSlot(overlay, item, style));
            primary.add(overlay).size(48, 48);
            if (i % 3 == 0) primary.row();
        }

        splitPane = new VisSplitPane(primary, secondary, false);
        rootTable.add(splitPane).fill().expand();

        rootTable.pack();
        gui.createContainer(rootTable).fill();
    }

    /**
     * Update the text for selected items
     *
     * @param slotItem the slot item selected.
     */
    private void updateSelectedItem(InventoryUiSlot slotItem) {
        hideItemOptionals();
        hideWeaponStats();

        itemDescription.setText(slotItem.itemDescription);
        itemDescription.restart();
        itemDescription.setVisible(true);

        // populate consume item button
        if (slotItem.item instanceof ItemConsumable
                && ((ItemConsumable) slotItem.item).isAllowedToConsume()) {
            useItemButton.setVisible(true);
            useItemButton.setText("Eat");
        } else if (slotItem.item instanceof ItemEquippable) {
            populateEquipmentButtons();
        }

        if (slotItem.item instanceof ItemWeapon) populateWeaponStats(((ItemWeapon) slotItem.item));
        this.clickedItem = slotItem.item;
    }

    private void hideItemOptionals() {
        useItemButton.setVisible(false);
    }

    private void populateWeaponStats(ItemWeapon item) {
        weaponRangeTooltip.setText("Range: " + item.getRange());
        weaponDamageTooltip.setText("Damage: " + item.getBaseDamage());
        weaponCritTooltip.setText("Critical hit chance: " + Math.round(item.getCriticalHitChance()) + "%");

        weaponRangeImage.setVisible(true);
        weaponRangeImage.getColor().a = 0.0f;
        weaponRangeImage.addAction(Actions.fadeIn(1.5f));

        weaponDamageIcon.setVisible(true);
        weaponDamageIcon.getColor().a = 0.0f;
        weaponDamageIcon.addAction(Actions.fadeIn(1.5f));

        weaponCritIcon.setVisible(true);
        weaponCritIcon.getColor().a = 0.0f;
        weaponCritIcon.addAction(Actions.fadeIn(1.5f));
    }

    private void populateEquipmentButtons() {
        useItemButton.setVisible(true);
        useItemButton.setText("Equip");

        useItemButton.getColor().a = 0.0f;
        useItemButton.addAction(Actions.fadeIn(1.0f));
    }

    private void hideWeaponStats() {
        weaponRangeImage.setVisible(false);
        weaponDamageIcon.setVisible(false);
        weaponCritIcon.setVisible(false);
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
        gui.hideGui(GuiType.CONTAINER);
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
        itemDescription.setVisible(false);
    }

    /**
     * Handles data required for the UI inventory slot
     */
    final class InventoryUiSlot extends AbstractInventoryUiSlot {

        // item description of whatever is in this slot
        private String itemDescription = StringUtils.EMPTY;
        // the last item in this slot, for comparison when updating
        private long lastItemId = -1;

        public InventoryUiSlot(Stack stack, Image item, Tooltip.TooltipStyle style) {
            super(stack, item, style);

            // add click action
            stack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!occupied) return;
                    handleClickActionListener();
                }
            });

        }

        /**
         * Handle actions and updating when clicking an item in this inventory GUI
         */
        private void handleClickActionListener() {
            updateSelectedItem(this);
        }

        /**
         * Set all data about the given item
         *
         * @param item the item
         */
        @Override
        protected void setItem(Item item) {
            super.setItem(item);
            this.imageItem.setScale(item.getSprite().getScaleX(), item.getSprite().getScaleY());
            this.lastItemId = item.getItemId();
            this.itemDescription = item.getDescription();
        }

        /**
         * Remove all data about this item
         */
        @Override
        protected void removeItem() {
            super.reset();
            this.itemDescription = StringUtils.EMPTY;
            this.lastItemId = -1;
        }
    }

}
