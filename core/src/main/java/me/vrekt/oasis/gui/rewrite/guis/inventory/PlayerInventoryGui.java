package me.vrekt.oasis.gui.rewrite.guis.inventory;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.rewrite.GuiManager;
import me.vrekt.oasis.gui.rewrite.guis.inventory.actions.InventorySlotSource;
import me.vrekt.oasis.gui.rewrite.guis.inventory.actions.InventorySlotTarget;
import me.vrekt.oasis.gui.rewrite.guis.inventory.utility.InventoryGuiSlot;
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

    private final OasisPlayer player;
    private final LinkedList<InventoryGuiSlot> guiSlots = new LinkedList<>();
    private final Map<ItemRarity, TextureRegionDrawable> rarityIcons = new HashMap<>();
    private final TypingLabel itemNameHeader, itemDescriptionHeader;

    private final VisImage itemRarityIcon;
    private final VisImage[] itemStats;
    private final VisTextButton itemActionButton;
    private final Tooltip[] itemStatTooltips;

    private TextureRegionDrawable draggingItem;
    private float dragX, dragY, dragWidth, dragHeight;

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
        itemNameHeader = new TypingLabel(StringUtils.EMPTY, guiManager.getStyle().getMediumBlack());
        itemNameHeader.setVisible(true);
        itemNameHeader.setWrap(true);
        itemNameHeader.setWidth(140);
        itemDescriptionHeader = new TypingLabel(StringUtils.EMPTY, guiManager.getStyle().getMediumBlack());
        itemDescriptionHeader.setVisible(true);
        itemDescriptionHeader.setWrap(true);
        itemDescriptionHeader.setWidth(175);

        // Headers: item name and rarity icons
        final VisTable headerTable = new VisTable();
        headerTable.left();
        headerTable.add(itemNameHeader).width(140).left();
        headerTable.add(itemRarityIcon = new VisImage())
                .size(36, 36)
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
        final VisTable itemStatsTable = new VisTable();
        itemInformationTable.left();
        itemStatsTable.left();

        // Item info: init drawables for icons
        final TextureRegionDrawable rangeIcon = new TextureRegionDrawable(guiManager.getAsset().get("weapon_range_icon"));
        final TextureRegionDrawable damageIcon = new TextureRegionDrawable(guiManager.getAsset().get("weapon_damage_icon"));
        final TextureRegionDrawable critIcon = new TextureRegionDrawable(guiManager.getAsset().get("weapon_crit_icon"));

        // Item info: init item stat icons
        itemStats = new VisImage[3];
        itemStatsTable.add(itemStats[0] = new VisImage(rangeIcon)).size(36, 36).padRight(4);
        itemStatsTable.add(itemStats[1] = new VisImage(damageIcon)).size(36, 36).padRight(4);
        itemStatsTable.add(itemStats[2] = new VisImage(critIcon)).size(36, 36);
        for (VisImage itemStat : itemStats) itemStat.setVisible(false);

        // Item info: add to table
        itemInformationTable.add(itemStatsTable).left();
        itemInformationTable.row();

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

        // Init item stat tooltips
        itemStatTooltips = new Tooltip[3];
        itemStatTooltips[0] = new Tooltip.Builder(StringUtils.EMPTY)
                .target(itemStats[0])
                .style(guiManager.getStyle().getTooltipStyle())
                .build();
        itemStatTooltips[1] = new Tooltip.Builder(StringUtils.EMPTY)
                .target(itemStats[1])
                .style(guiManager.getStyle().getTooltipStyle())
                .build();
        itemStatTooltips[2] = new Tooltip.Builder(StringUtils.EMPTY)
                .target(itemStats[2])
                .style(guiManager.getStyle().getTooltipStyle())
                .build();

        // hide tooltips by default
        for (Tooltip tooltip : itemStatTooltips) tooltip.setVisible(true);
        addItemActionButtonListener();

        left.top().padTop(52).padLeft(84);
        final TextureRegionDrawable slotDrawable = new TextureRegionDrawable(guiManager.getAsset().get("theme"));

        final AtomicInteger slotTracker = new AtomicInteger();
        // Inventory: populate each individual UI component
        populateInventoryUiComponents(guiManager, player.getInventory(), slotDrawable, component -> {
            // add to list of slots
            guiSlots.add(new InventoryGuiSlot(guiManager,
                    this,
                    component.overlay(),
                    component.item(),
                    component.amountLabel(),
                    component.index() <= 6,
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
        player.getInventory().getSlots().forEach((slot, item) -> {
            final InventoryGuiSlot guiSlot = guiSlots.get(slot);
            if (!item.getItem().is(guiSlot.getLastItemKey())) {
                guiSlot.setOccupiedItem(item.getItem());
            }
        });
    }

    /**
     * Initialize components for the drag and drop actions within a slot
     */
    private void initializeSlotActions() {
        final DragAndDrop action = new DragAndDrop();

        for (InventoryGuiSlot guiSlot : guiSlots) {
            action.addSource(new InventorySlotSource(this, guiSlot, action));
            action.addTarget(new InventorySlotTarget(this, guiSlot, player.getInventory(), action));
        }
    }

    /**
     * Set the current item being dragged
     *
     * @param slot the slot being dragged
     * @param x    initial X
     * @param y    initial Y
     */
    public void setDraggingItem(InventoryGuiSlot slot, float x, float y) {
        draggingItem = new TextureRegionDrawable((TextureRegionDrawable) slot.getSlotIcon().getDrawable());
        dragWidth = slot.getSlotIcon().getImageWidth() * slot.getSlotIcon().getScaleX();
        dragHeight = slot.getSlotIcon().getImageHeight() * slot.getSlotIcon().getScaleY();
        dragX = x - (dragWidth / 2f);
        dragY = y - (dragHeight / 2f);
    }

    /**
     * Stop the dragging action
     */
    public void setStoppedDragging() {
        draggingItem = null;
    }

    /**
     * Update drag position of the item that is being dragged
     *
     * @param x the X
     * @param y the Y
     */
    public void updateDragPosition(float x, float y) {
        dragX = Math.round(x - (dragWidth / 2f));
        dragY = Math.round(y - (dragHeight / 2f));
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
                if (selectedItem instanceof ItemConsumable) {
                    selectedItem.useItem(player);
                    if (selectedItem.getAmount() <= 0)
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

        itemNameHeader.setText(selectedItem.getItemRarity().getColorName() + selectedItem.getItemName());
        itemNameHeader.setVisible(true);
        itemNameHeader.restart();

        itemDescriptionHeader.setText(selectedItem.getDescription());
        itemDescriptionHeader.setVisible(true);
        itemDescriptionHeader.restart();
        enableItemActionButton(selectedItem);
        populateWeaponOrArtifactStats(selectedItem);

        itemRarityIcon.setDrawable((Drawable) null);
        final ItemRarity rarity = selectedItem.getItemRarity();
        if (rarity != ItemRarity.BASIC && rarityIcons.containsKey(rarity)) {
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
        } else if (item instanceof ItemWeapon) {
            populateItemStats((ItemWeapon) item);
            itemActionButton.setVisible(true);
            itemActionButton.setText("Equip");
        } else if (item instanceof ItemArtifact) {
            itemActionButton.setVisible(true);
            itemActionButton.setText("Activate Artifact");
        }
    }

    /**
     * Choose whether to populate weapon stats, or, artifact stats.
     *
     * @param item the item
     */
    private void populateWeaponOrArtifactStats(Item item) {
        if (item instanceof ItemWeapon) {
            populateItemStats((ItemWeapon) item);
        } else if (item instanceof ItemArtifact) {
            populateArtifactStats((ItemArtifact) item);
        }
    }

    /**
     * Populate item stats for a weapon
     *
     * @param item the item
     */
    private void populateItemStats(ItemWeapon item) {
        for (Tooltip tooltip : itemStatTooltips) tooltip.setVisible(true);

        itemStatTooltips[0].setText("Range ~=~ " + item.getRange());
        itemStatTooltips[1].setText("Damage ~=~ " + item.getBaseDamage());
        itemStatTooltips[2].setText("Critical hit chance ~=~ " + item.getCriticalHitChance() + "%");

        for (VisImage itemStat : itemStats) {
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
        final Tooltip tooltip = itemStatTooltips[0];
        final VisImage itemStat = itemStats[0];

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
        for (VisImage itemStat : itemStats) itemStat.setVisible(false);
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
        for (VisImage image : itemStats) image.setVisible(false);
        for (Tooltip tooltip : itemStatTooltips) tooltip.setVisible(false);
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
    public void draw(Batch batch) {
        if (draggingItem != null) {
            draggingItem.draw(batch, dragX, dragY, 0, 0, dragWidth, dragHeight, 2.0f, 2.0f, 1f);
        }
    }

    @Override
    public void hideRelatedGuis() {
        guiManager.hideGui(GuiType.QUEST);
        guiManager.hideGui(GuiType.QUEST_ENTRY);
    }
}
