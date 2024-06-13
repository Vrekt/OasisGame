package me.vrekt.oasis.gui.guis.inventory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.guis.inventory.actions.InventorySlotSource;
import me.vrekt.oasis.gui.guis.inventory.actions.InventorySlotTarget;
import me.vrekt.oasis.gui.guis.inventory.utility.InventoryGuiSlot;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemEquippable;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.artifact.ItemArtifact;
import me.vrekt.oasis.item.consumables.ItemConsumable;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class PlayerInventoryGui extends InventoryGui {

    private final PlayerSP player;
    private final LinkedList<InventoryGuiSlot> guiSlots = new LinkedList<>();
    private final Map<ItemRarity, TextureRegionDrawable> rarityIcons = new HashMap<>();
    private final TypingLabel itemNameHeader, itemDescriptionHeader;

    // attributes and stats of the item
    private final Array<VisImage> itemInformationComponents = new Array<>();
    private final Array<Tooltip> itemInformationTooltipComponents = new Array<>();

    private final VisImage itemRarityIcon;
    private final VisTextButton itemActionButton;

    // currently selected item
    private Item selectedItem;

    public PlayerInventoryGui(GuiManager guiManager) {
        super(GuiType.INVENTORY, guiManager);
        this.player = guiManager.getGame().getPlayer();

        hideWhenVisible.add(GuiType.HUD);

        initializeRarityIcons();

        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("inventory")));
        final VisTable left = new VisTable(true), right = new VisTable(true);
        right.top().padTop(36).padLeft(32).left();

        // Headers: init item name and description labels
        itemNameHeader = new TypingLabel(StringUtils.EMPTY, guiManager.getStyle().getLargeBlack());
        itemNameHeader.setVisible(true);
        itemNameHeader.setWrap(true);
        itemNameHeader.setWidth(150);

        itemDescriptionHeader = new TypingLabel(StringUtils.EMPTY, guiManager.getStyle().getMediumWhiteMipMapped());
        itemDescriptionHeader.setVisible(true);
        itemDescriptionHeader.setWrap(true);
        itemDescriptionHeader.setWidth(175);

        // Headers: item name and rarity icons
        final VisTable headerTable = new VisTable();
        headerTable.left();
        headerTable.add(itemNameHeader).width(150).left();
        headerTable.row();
        headerTable.add(itemRarityIcon = new VisImage())
                .size(36, 36)
                .bottom()
                .left()
                .padTop(10);

        // Headers: add to right hand size
        right.add(headerTable).left();
        right.row();
        right.add(itemDescriptionHeader)
                .width(175)
                .padTop(8)
                .left();
        right.row().padTop(16);

        // Item info: Info and stats
        final VisTable itemInformationTable = new VisTable();
        final VisTable itemInformationImageComponents = new VisTable();
        itemInformationTable.left();
        itemInformationImageComponents.left();

        // stores images in an array
        itemInformationComponents.add(new VisImage(), new VisImage(), new VisImage());
        // add them to the table
        itemInformationImageComponents.add(itemInformationComponents.get(0)).size(36, 36).padRight(4);
        itemInformationImageComponents.add(itemInformationComponents.get(1)).size(36, 36).padRight(4);
        itemInformationImageComponents.add(itemInformationComponents.get(2)).size(36, 36).padRight(4);
        itemInformationComponents.forEach(image -> image.setVisible(false));

        // Item info: add to table
        itemInformationTable.add(itemInformationImageComponents).left();
        itemInformationTable.row();

        // populate tooltips
        for (VisImage component : itemInformationComponents) {
            final Tooltip tooltip = new Tooltip.Builder(StringUtils.EMPTY)
                    .target(component)
                    .style(guiManager.getStyle().getTooltipStyle())
                    .build();

            tooltip.setVisible(false);
            itemInformationTooltipComponents.add(tooltip);
        }

        // Use item button
        final VisTable buttonTable = new VisTable();
        buttonTable.left();

        // Init use item buttons and styles
        itemActionButton = new VisTextButton(StringUtils.EMPTY);
        itemActionButton.setLabel(new VisLabel(StringUtils.EMPTY, guiManager.getStyle().getMediumWhite()));
        itemActionButton.setStyle(new TextButton.TextButtonStyle(
                guiManager.getStyle().getTheme(),
                guiManager.getStyle().getTheme(),
                guiManager.getStyle().getTheme(),
                guiManager.getMediumFont()));
        itemActionButton.setVisible(false);
        buttonTable.add(itemActionButton);
        itemInformationTable.add(buttonTable).padTop(16).left();

        // Finally, add item information to right table
        right.add(itemInformationTable).left();
        right.row();

        addItemActionButtonListener();

        left.top().padTop(52).padLeft(84);
        final TextureRegionDrawable slotDrawable = new TextureRegionDrawable(guiManager.getAsset().get("theme"));

        final AtomicInteger slotTracker = new AtomicInteger();
        // Inventory: populate each individual UI component
        populateInventoryUiComponents(guiManager, player.getInventory().getSize(), slotDrawable, true, component -> {
            // add to list of slots
            guiSlots.add(new InventoryGuiSlot(guiManager,
                    this,
                    component.overlay(),
                    component.item(),
                    component.amountLabel(),
                    component.index() < 6,
                    component.index()));
            final int progress = slotTracker.incrementAndGet();
            // add to left primary table with correct sizes
            left.add(component.overlay()).size(48, 48);

            // line break for hotbar components
            // TODO: Find out why cell resizing when adding longer separator
            if (progress == 6) {
                final VisTable separator = new VisTable();
                separator.addSeparator();
                // TODO: Unable do it any other way because first column of,
                // TODO: slots will expand to fit the separator size.
                left.row();
                left.add(separator);
            }

            // split table in rows of 3
            if (progress % 3 == 0) left.row();
        });

        // Add split pane for left and right tables
        final VisSplitPane pane = new VisSplitPane(left, right, false);
        // only allow elements to be touched, since split pane
        // is programming choice for easier UI, the split pane should not be used
        pane.setTouchable(Touchable.enabled);
        rootTable.add(pane).fill().expand();

        initializeSlotActions();

        guiManager.addGui(rootTable);
    }

    @Override
    public void update() {
        for (IntMap.Entry<Item> entry : player.getInventory().items()) {
            final InventoryGuiSlot slot = guiSlots.get(entry.key);
            if (!entry.value.compare(slot.getLastItemKey())) {
                slot.setOccupiedItem(entry.value);
            }
        }
    }

    /**
     * Initialize components for the drag and drop actions within a slot
     */
    private void initializeSlotActions() {
        final DragAndDrop action = new DragAndDrop();
        action.setDragTime(100);

        for (InventoryGuiSlot guiSlot : guiSlots) {
            action.addSource(new InventorySlotSource(this, guiSlot));
            action.addTarget(new InventorySlotTarget(this, guiSlot, player.getInventory()));
        }
    }

    /**
     * Initialize icon drawables
     */
    private void initializeRarityIcons() {
        for (ItemRarity rarity : ItemRarity.values()) {
            if (rarity.getTexture() != null) {
                rarityIcons.put(rarity, new TextureRegionDrawable(guiManager.getAsset().get(rarity.getTexture())));
            }
        }
    }

    /**
     * Handle assigning actions to the item action button.
     */
    private void addItemActionButtonListener() {
        itemActionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedItem instanceof ItemConsumable consumable) {
                    consumable.useItem(player);
                    if (selectedItem.amount() <= 0)
                        itemActionButton.setVisible(false);
                } else if (selectedItem instanceof ItemEquippable equippable) {
                    if (equippable.canEquip(player)) {
                        equippable.equip(player);
                        itemActionButton.setText("Unequip");
                    } else if (equippable.isEquipped()) {
                        // TODO: Un-equip
                    } else if (selectedItem instanceof ItemArtifact) {
                        // item was activated
                        // TODO: De-activate maybe.
                        itemActionButton.setVisible(false);
                    }
                }

                // indicates it has been enabled by the above statements
                if (itemActionButton.isVisible()) fadeIn(itemActionButton, 1.0f);
            }
        });
    }

    @Override
    public void handleSlotClicked(InventoryGuiSlot slot) {
        hideItemOptionals();
        selectedItem = slot.getItem();

        itemNameHeader.setText(selectedItem.rarity().getColorName() + selectedItem.name());
        itemNameHeader.setVisible(true);
        itemNameHeader.restart();

        itemDescriptionHeader.setText(selectedItem.description());
        itemDescriptionHeader.setVisible(true);
        itemDescriptionHeader.restart();

        enableItemActionButton(selectedItem);
        populateWeaponOrArtifactStats(selectedItem);

        itemRarityIcon.setDrawable((Drawable) null);
        final ItemRarity rarity = selectedItem.rarity();
        if (rarityIcons.containsKey(rarity)) {
            itemRarityIcon.setDrawable(rarityIcons.get(rarity));
        }

    }

    /**
     * Enable the button for item actions
     *
     * @param item the item
     */
    private void enableItemActionButton(Item item) {
        if (item instanceof ItemConsumable) {
            itemActionButton.setVisible(true);
            itemActionButton.setText("Eat");
        } else if (item instanceof ItemWeapon weapon) {
            populateItemStats(weapon);
            itemActionButton.setVisible(true);
            itemActionButton.setText("Equip");
        } else if (item instanceof ItemArtifact) {
            itemActionButton.setVisible(true);
            itemActionButton.setText("Activate");
        }
    }

    /**
     * Choose whether to populate weapon stats, or, artifact stats.
     *
     * @param item the item
     */
    private void populateWeaponOrArtifactStats(Item item) {
        if (item instanceof ItemWeapon weapon) {
            populateItemStats(weapon);
        } else if (item instanceof ItemArtifact artifact) {
            populateArtifactStats(artifact);
        } else if (item instanceof ItemConsumable) {
            int index = 0;
            for (Attribute attribute : item.getItemAttributes().values()) {
                if (attribute.texture() == null) continue;
                populateAttributeInformation(attribute, index);
                index++;
            }
        }
    }

    /**
     * Populate attribute information
     *
     * @param attribute attribute
     * @param index     current index, should not exceed 2
     */
    private void populateAttributeInformation(Attribute attribute, int index) {

        itemInformationComponents.get(index).setDrawable(attribute.subType().get(guiManager));
        itemInformationComponents.get(index).setVisible(true);

        itemInformationTooltipComponents.get(index).setText(attribute.name() + StringUtils.LF + attribute.description());
        itemInformationTooltipComponents.get(index).setVisible(true);

        fadeIn(itemInformationComponents.get(index), 1.5f);
    }

    /**
     * Populate item stats for a weapon
     *
     * @param item the item
     */
    private void populateItemStats(ItemWeapon item) {
        for (Tooltip tooltip : itemInformationTooltipComponents) tooltip.setVisible(true);

        itemInformationComponents.get(0).setDrawable(guiManager.getStyle().getWeaponRangeIcon());
        itemInformationComponents.get(1).setDrawable(guiManager.getStyle().getWeaponDamageIcon());
        itemInformationComponents.get(2).setDrawable(guiManager.getStyle().getWeaponCriticalIcon());

        itemInformationTooltipComponents.get(0).setText("Range ~=~ " + item.getRange());
        itemInformationTooltipComponents.get(1).setText("Damage ~=~ " + item.getBaseDamage());
        itemInformationTooltipComponents.get(2).setText("Critical hit chance ~=~ " + item.getCriticalHitChance() + "%");

        for (VisImage itemStat : itemInformationComponents) {
            itemStat.setVisible(true);
            fadeIn(itemStat, 1.5f);
        }
    }

    /**
     * Populate artifact stats
     *
     * @param item the artifact
     */
    private void populateArtifactStats(ItemArtifact item) {
        final Tooltip tooltip = itemInformationTooltipComponents.get(0);
        final VisImage itemStat = itemInformationComponents.get(0);

        tooltip.setText(
                "Artifact Level: " + item.getArtifact().getArtifactLevel()
                        + "\n" + "Artifact Duration: " + item.getArtifact().getArtifactDuration()
                        + " \n" + "Artifact Cooldown: " + item.getArtifact().getArtifactCooldown());
        tooltip.setVisible(true);

        itemStat.setDrawable(new TextureRegionDrawable(item.getIcon()));
        itemStat.setVisible(true);
        fadeIn(itemStat, 1.5f);
    }

    private void hideItemOptionals() {
        itemActionButton.setVisible(false);
        for (VisImage itemStat : itemInformationComponents) itemStat.setVisible(false);
    }

    /**
     * Remove this item from the provided slot.
     *
     * @param slot the slot number
     */
    public void removeItemFromSlot(int slot) {
        guiSlots.get(slot).resetSlot();

        itemNameHeader.setVisible(false);
        itemDescriptionHeader.setVisible(false);
        itemActionButton.setVisible(false);
        for (VisImage image : itemInformationComponents) image.setVisible(false);
        for (Tooltip tooltip : itemInformationTooltipComponents) tooltip.setVisible(false);
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

    @Override
    public void hideRelatedGuis() {
        guiManager.hideGui(GuiType.QUEST);
        guiManager.hideGui(GuiType.QUEST_ENTRY);
        guiManager.hideGui(GuiType.CONTAINER);
    }
}
