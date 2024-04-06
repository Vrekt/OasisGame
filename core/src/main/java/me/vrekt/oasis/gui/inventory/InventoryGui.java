package me.vrekt.oasis.gui.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemEquippable;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.artifact.ItemArtifact;
import me.vrekt.oasis.item.consumables.ItemConsumable;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.utility.GuiUtilities;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Inventory Gui
 */
public final class InventoryGui extends Gui {
    private final VisTable rootTable;
    private final OasisPlayer player;
    final VisSplitPane splitPane;

    private final LinkedList<InventoryUiSlot> slots = new LinkedList<>();
    private final Map<ItemRarity, TextureRegionDrawable> rarityIcons = new HashMap<>();

    // the item description of whatever item is clicked
    private final TypingLabel itemName, itemDescription;
    private final VisImage itemRarityIcon;
    private final VisTextButton useItemButton;
    private final Tooltip itemNameTooltip;

    private final VisImage statOne, statTwo, statThree;
    private final Tooltip statOneTooltip, statTwoTooltip, statThreeTooltip;

    private final InventoryIcons icons;

    // the current item clicked on
    private Item clickedItem;

    public InventoryGui(GameGui gui, Asset asset) {
        super(gui, asset, "inventory");
        this.player = gui.getGame().getPlayer();
        this.icons = new InventoryIcons();

        rarityIcons.put(ItemRarity.VOID, new TextureRegionDrawable(asset.get("void_rarity")));

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        TableUtils.setSpacingDefaults(rootTable);

        rootTable.setBackground(new TextureRegionDrawable(asset.get("inventory")));
        final VisTable primary = new VisTable(true);
        final VisTable secondary = new VisTable(true);

        secondary.top().padTop(36).padLeft(32).left();
        itemName = new TypingLabel(StringUtils.EMPTY, new Label.LabelStyle(gui.getMedium(), Color.BLACK));
        itemNameTooltip = new Tooltip.Builder(StringUtils.EMPTY).target(itemName).style(gui.getStyles().getTooltipStyle()).build();

        final Table itemNameAndRarityTable = new Table();
        itemNameAndRarityTable.left();

        itemName.setVisible(false);
        itemName.setWrap(true);
        itemName.setWidth(140);
        itemDescription = new TypingLabel(StringUtils.EMPTY, new Label.LabelStyle(gui.getMedium(), Color.BLACK));
        itemDescription.setVisible(false);
        itemDescription.setWrap(true);
        itemDescription.setWidth(175);

        itemNameAndRarityTable.add(itemName).width(140).left();
        itemNameAndRarityTable.add(itemRarityIcon = new VisImage()).size(36, 36).left().padTop(10);

        secondary.add(itemNameAndRarityTable).left();
        secondary.row();
        secondary.add(itemDescription).width(175).padTop(8).left();
        secondary.row().padTop(16);

        final VisTable itemInformationTable = new VisTable(true);
        itemInformationTable.left();

        final VisTable attributesTable = new VisTable(true);
        attributesTable.left();

        attributesTable.add(statOne = new VisImage(icons.rangeIcon)).size(36, 36);
        attributesTable.add(statTwo = new VisImage(icons.damageIcon)).size(36, 36);
        attributesTable.add(statThree = new VisImage(icons.critIcon)).size(36, 36);

        itemInformationTable.add(attributesTable).left();
        itemInformationTable.row();

        final VisTable buttonTable = new VisTable(true);
        buttonTable.left();

        useItemButton = new VisTextButton(StringUtils.EMPTY);
        useItemButton.setLabel(new VisLabel(StringUtils.EMPTY, new Label.LabelStyle(gui.getMedium(), Color.WHITE)));
        useItemButton.setStyle(new TextButton.TextButtonStyle(gui.getStyles().getTheme(), gui.getStyles().getTheme(), gui.getStyles().getTheme(), gui.getMedium()));
        useItemButton.setVisible(false);
        buttonTable.add(useItemButton);

        itemInformationTable.add(buttonTable).left();

        secondary.add(itemInformationTable).left();
        secondary.row();

        statOneTooltip = new Tooltip.Builder(StringUtils.EMPTY).target(statOne).style(gui.getStyles().getTooltipStyle()).build();
        statTwoTooltip = new Tooltip.Builder(StringUtils.EMPTY).target(statTwo).style(gui.getStyles().getTooltipStyle()).build();
        statThreeTooltip = new Tooltip.Builder(StringUtils.EMPTY).target(statThree).style(gui.getStyles().getTooltipStyle()).build();

        hideWeaponStats();

        // secondary.add(useItemButton).padTop(16).left();

        // add use item button listener
        useItemButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (clickedItem instanceof ItemConsumable) {
                    clickedItem.useItem(player);
                    if (clickedItem.getAmount() <= 0) {
                        useItemButton.setVisible(false);
                    }
                } else if (clickedItem instanceof ItemEquippable) {
                    ((ItemEquippable) clickedItem).equip(player);
                    useItemButton.setVisible(false);
                    // TODO: Un-equip button
                }
            }
        });

        // populate a total of 21 inventory slots
        primary.top().padTop(52).padLeft(84);
        final TextureRegionDrawable slotDrawable = new TextureRegionDrawable(asset.get("theme"));

        GuiUtilities.populateInventoryComponents(player.getInventory(), slotDrawable, gui, consumer -> {
            slots.add(new InventoryUiSlot(consumer.overlay, consumer.item, consumer.style, consumer.amountLabel));
            primary.add(consumer.overlay).size(48, 48);
            if (consumer.index % 3 == 0) primary.row();
        });

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

        itemName.setText(slotItem.item.getItemRarity().getColorName() + slotItem.item.getItemName());
        itemNameTooltip.setText(slotItem.item.getItemRarity().getColoredRarityName());
        itemName.setVisible(true);
        itemName.restart();

        itemRarityIcon.setDrawable((com.badlogic.gdx.scenes.scene2d.utils.Drawable) null);

        itemDescription.setText(slotItem.itemDescription);
        itemDescription.restart();
        itemDescription.setVisible(true);

        // populate consume item button
        if (slotItem.item instanceof ItemConsumable
                && ((ItemConsumable) slotItem.item).isAllowedToConsume()) {
            useItemButton.setVisible(true);
            useItemButton.setText("Eat");
        } else if (slotItem.item instanceof ItemEquippable) {
            populateEquipmentButtons(slotItem.item instanceof ItemArtifact);
        }

        if (slotItem.item instanceof ItemWeapon) {
            populateStats(((ItemWeapon) slotItem.item));
        } else if (slotItem.item instanceof ItemArtifact) {
            populateArtifactStats(((ItemArtifact) slotItem.item));
        }

        final ItemRarity rarity = slotItem.item.getItemRarity();
        if (rarity != ItemRarity.BASIC
                && rarityIcons.containsKey(rarity)) {
            itemRarityIcon.setDrawable(rarityIcons.get(rarity));
        }

        this.clickedItem = slotItem.item;
    }

    private void hideItemOptionals() {
        useItemButton.setVisible(false);
    }

    private void populateArtifactStats(ItemArtifact artifact) {
        statOneTooltip.setText("Artifact Level: " + artifact.getArtifact().getArtifactLevel() + "\n" +
                "Artifact Duration: " + artifact.getArtifact().getArtifactDuration() + " \n" +
                "Artifact Cooldown: " + artifact.getArtifact().getArtifactCooldown());

        statOne.setUserObject(true);
        statOne.setDrawable(new TextureRegionDrawable(artifact.getIcon()));

        statOne.setVisible(true);
        statOne.getColor().a = 0.0f;
        statOne.addAction(Actions.fadeIn(1.5f));
    }

    private void populateStats(ItemWeapon item) {
        statOneTooltip.setText("Range: " + item.getRange());
        statTwoTooltip.setText("Damage: " + item.getBaseDamage());
        statThreeTooltip.setText("Critical hit chance: " + Math.round(item.getCriticalHitChance()) + "%");

        if (statOne.getUserObject() != null && (boolean) statOne.getUserObject()) {
            // indicates this image needs to be reset to the other default icon
            statOne.setDrawable(icons.rangeIcon);
            statOne.setUserObject(false);
        }

        statOne.setVisible(true);
        statOne.getColor().a = 0.0f;
        statOne.addAction(Actions.fadeIn(1.5f));

        statTwo.setVisible(true);
        statTwo.getColor().a = 0.0f;
        statTwo.addAction(Actions.fadeIn(1.5f));

        statThree.setVisible(true);
        statThree.getColor().a = 0.0f;
        statThree.addAction(Actions.fadeIn(1.5f));
    }

    private void populateEquipmentButtons(boolean isArtifact) {
        if (isArtifact) {
            // TODO: In the future, ability to replace artifact slots
            useItemButton.setVisible(true);
            useItemButton.setText("Equip Artifact");
        } else {
            if (player.canEquipItem()) {
                useItemButton.setVisible(true);
                useItemButton.setText("Equip");
            }
        }

        if (useItemButton.isVisible()) {
            useItemButton.getColor().a = 0.0f;
            useItemButton.addAction(Actions.fadeIn(1.0f));
        }
    }

    private void hideWeaponStats() {
        statOne.setVisible(false);
        statTwo.setVisible(false);
        statThree.setVisible(false);
    }


    @Override
    public void update() {
        // update ui based on player inventory status
        player.getInventory().getSlots().forEach((slot, item) -> {
            final InventoryUiSlot ui = slots.get(slot);
            // only update this inventory slot IF the last item does not match the current
            if (!item.getItem().is(ui.lastItemKey)) {
                ui.setItem(item.getItem());
            } else {
                // update amount label otherwise
                ui.setStackableState();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
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
        statOne.setVisible(false);
        statTwo.setVisible(false);
        statThree.setVisible(false);
    }

    /**
     * Handles data required for the UI inventory slot
     */
    final class InventoryUiSlot extends AbstractInventoryUiSlot {

        // item description of whatever is in this slot
        private String itemDescription = StringUtils.EMPTY;
        // the last item in this slot, for comparison when updating
        private String lastItemKey = null;

        public InventoryUiSlot(Stack stack, Image item, Tooltip.TooltipStyle style, VisLabel amountLabel) {
            super(stack, item, amountLabel, style);

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
            this.lastItemKey = item.getKey();
            this.itemDescription = item.getDescription();
        }

        /**
         * Remove all data about this item
         */
        @Override
        protected void removeItem() {
            super.reset();
            this.itemDescription = StringUtils.EMPTY;
            this.lastItemKey = null;
        }
    }

    private final class InventoryIcons {

        final TextureRegionDrawable rangeIcon, damageIcon, critIcon;

        public InventoryIcons() {
            rangeIcon = new TextureRegionDrawable(asset.get("weapon_range_icon"));
            damageIcon = new TextureRegionDrawable(asset.get("weapon_damage_icon"));
            critIcon = new TextureRegionDrawable(asset.get("weapon_crit_icon"));
        }
    }

}
