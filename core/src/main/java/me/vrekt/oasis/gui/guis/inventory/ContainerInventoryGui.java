package me.vrekt.oasis.gui.guis.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Resource;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.gui.guis.inventory.actions.InventorySlotSource;
import me.vrekt.oasis.gui.guis.inventory.actions.InventorySlotTarget;
import me.vrekt.oasis.gui.guis.inventory.utility.InventoryGuiSlot;
import me.vrekt.oasis.item.Item;

/**
 * Container GUI
 */
public final class ContainerInventoryGui extends InventoryGui {

    private final PlayerSP player;

    private final Array<InventoryGuiSlot> containerSlots = new Array<>();
    private final Array<InventoryGuiSlot> playerSlots = new Array<>();

    private final DragAndDrop dragAndDrop;

    private ContainerInventory activeContainerInventory;
    private InventoryGuiSlot selectedContainerSlot;
    private InventoryGuiSlot selectedPlayerSlot;
    private boolean isOnContainer;

    public ContainerInventoryGui(GuiManager guiManager) {
        super(GuiType.CONTAINER, guiManager);
        this.player = guiManager.getGame().getPlayer();

        hideWhenVisible.add(GuiType.HUD);
        disablePlayerMovement = true;

        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get(Resource.UI, "container", 2)));
        final VisTable topTable = new VisTable(true), bottomTable = new VisTable(true);

        bottomTable.top();
        topTable.top();

        populateContainerInventoryComponents(topTable);
        populatePlayerInventoryComponents(bottomTable);

        dragAndDrop = new DragAndDrop();
        //  register drag handlers
        for (InventoryGuiSlot containerSlot : containerSlots) {
            dragAndDrop.addSource(new InventorySlotSource(this, containerSlot));
            final InventorySlotTarget target = new InventorySlotTarget(this, containerSlot, player.getInventory());
            containerSlot.setTarget(target);

            dragAndDrop.addTarget(target);
        }

        for (InventoryGuiSlot playerSlot : playerSlots) {
            dragAndDrop.addSource(new InventorySlotSource(this, playerSlot));
            // no active container yet so null for now
            final InventorySlotTarget target = new InventorySlotTarget(this, playerSlot, null);
            playerSlot.setTarget(target);
            dragAndDrop.addTarget(target);
        }

        final VisImageTextButton takeAllButton = new VisImageTextButton("Take All", Styles.getImageTextButtonStyle());
        handleMoveAllButton(takeAllButton);

        final VisTable buttonTable = new VisTable();
        buttonTable.add(takeAllButton).width(120);

        rootTable.add(topTable);
        rootTable.row();
        rootTable.add(buttonTable);
        rootTable.row();
        rootTable.add(bottomTable);

        guiManager.addGui(rootTable);
    }

    @Override
    protected InventoryGuiSlot getPlayerSlot(int index) {
        return playerSlots.get(index);
    }

    @Override
    protected InventoryGuiSlot getContainerSlot(int index) {
        return containerSlots.get(index);
    }

    /**
     * Handle clicking of the move all button
     *
     * @param button the button
     */
    private void handleMoveAllButton(VisImageTextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isOnContainer) {
                    transferAllItems(activeContainerInventory, player.getInventory());
                }
            }
        });
    }

    /**
     * Transfer all items within a slot
     *
     * @param from        the from inventory
     * @param to          the to inventory
     * @param slot        the slot to transfer
     * @param isContainer if the from inventory is a container
     */
    private void transferAllFromSlot(AbstractInventory from, AbstractInventory to, int slot, boolean isContainer) {
        final int newSlot = from.transferAll(slot, to);
        if (isContainer) {
            updateContainerSlotTransfer(slot, newSlot);
        } else {
            updatePlayerSlotTransfer(slot, newSlot);
        }
    }

    /**
     * Transfer all items
     *
     * @param from from
     * @param to   to
     */
    private void transferAllItems(AbstractInventory from, AbstractInventory to) {
        for (IntMap.Entry<Item> entry : from.items()) {
            final Item item = entry.value;
            final int add = to.add(item);
            updateContainerSlotTransfer(entry.key, add);
        }

        from.clear();
    }

    /**
     * Update a player slot transfer
     *
     * @param playerSlot       the player slot transferred from
     * @param newContainerSlot the container slot transferred to
     */
    private void updatePlayerSlotTransfer(int playerSlot, int newContainerSlot) {
        // there is no longer an item here!
        selectedPlayerSlot = null;
        // item is fully removed, just reset slot.
        playerSlots.get(playerSlot).resetSlot();
        containerSlots.get(newContainerSlot).updateTransfer(activeContainerInventory, newContainerSlot);
    }

    /**
     * Update a container transfer where all items were removed/taken
     *
     * @param containerSlot the container slot transferred from
     * @param newPlayerSlot the player slot transferred to
     */
    private void updateContainerSlotTransfer(int containerSlot, int newPlayerSlot) {
        // there is no longer an item here!
        selectedContainerSlot = null;
        // item is fully removed, just reset slot.
        containerSlots.get(containerSlot).resetSlot();
        playerSlots.get(newPlayerSlot).updateTransfer(player.getInventory(), newPlayerSlot);
    }

    /**
     * Update a slot transfer from dragging
     *
     * @param from the from slot
     * @param to   the to slot
     */
    public void updateSlotTransfer(int from, int to) {
        containerSlots.get(from).resetSlot();
        containerSlots.get(to).setOccupiedItem(activeContainerInventory.get(to));
    }

    @Override
    public void itemTransferred(int from, int to) {
        super.itemTransferred(from, to);
        updateSlotTransfer(from, to);
    }

    @Override
    public void itemTransferredBetweenInventories(boolean isContainerTransfer, int from, int to) {
        super.itemTransferredBetweenInventories(isContainerTransfer, from, to);
        if (isContainerTransfer) {
            containerSlots.get(from).resetSlot();
            playerSlots.get(to).setOccupiedItem(player.getInventory().get(to));
        } else {
            playerSlots.get(from).resetSlot();
            containerSlots.get(to).setOccupiedItem(activeContainerInventory.get(to));
        }
    }

    @Override
    public void itemSwappedBetweenInventories(boolean isContainerTransfer, int from, int to) {
        super.itemSwappedBetweenInventories(isContainerTransfer, from, to);
        if (isContainerTransfer) {
            containerSlots.get(from).setOccupiedItem(activeContainerInventory.get(from));
            playerSlots.get(to).setOccupiedItem(player.getInventory().get(to));
        } else {
            containerSlots.get(to).setOccupiedItem(activeContainerInventory.get(to));
            playerSlots.get(from).setOccupiedItem(player.getInventory().get(from));
        }
    }

    @Override
    public void handleSlotClicked(InventoryGuiSlot slot) {
        final boolean isShiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        if (slot.isContainerSlot()) {
            selectedContainerSlot = slot.isEmpty() ? null : slot;
            if (selectedContainerSlot != null && isShiftPressed) {
                // transfer instantly since the player shift clicked this slot
                transferAllFromSlot(activeContainerInventory, player.getInventory(), selectedContainerSlot.getSlotNumber(), true);
                selectedContainerSlot = null;
            }
            selectedPlayerSlot = null;
            isOnContainer = true;
        } else {
            selectedPlayerSlot = slot.isEmpty() ? null : slot;
            if (selectedPlayerSlot != null && isShiftPressed) {
                // transfer instantly since the player shift clicked this slot
                transferAllFromSlot(player.getInventory(), activeContainerInventory, selectedPlayerSlot.getSlotNumber(), false);
                selectedPlayerSlot = null;
            }
            selectedContainerSlot = null;
            isOnContainer = false;
        }
    }

    /**
     * Populate container slots
     *
     * @param topTable the top table
     */
    private void populateContainerInventoryComponents(VisTable topTable) {
        for (int i = 1; i < 16; i++) {
            final InventoryUiComponent component = createSlotComponents(guiManager, (i - 1), false, true);
            final InventoryGuiSlot slot = new InventoryGuiSlot(guiManager, this, component, (i - 1));

            slot.setContainerSlot(true);
            containerSlots.add(slot);

            topTable.add(component.container()).size(48, 48);
            if (i % 6 == 0) topTable.row();
        }
    }

    /**
     * Populate the components for the players inventory
     *
     * @param bottomTable bottom table
     */
    private void populatePlayerInventoryComponents(VisTable bottomTable) {
        for (int i = 1; i < player.getInventory().getSize(); i++) {
            final InventoryUiComponent component = createSlotComponents(guiManager, (i - 1), false, false);
            playerSlots.add(new InventoryGuiSlot(guiManager, this, component, (i - 1)));

            bottomTable.add(component.container()).size(48, 48);
            if (i % 6 == 0) bottomTable.row();
        }
    }

    /**
     * Populate the players inventory
     */
    private void populatePlayerInventoryItems() {
        for (IntMap.Entry<Item> entry : player.getInventory().items()) {
            final InventoryGuiSlot guiSlot = playerSlots.get(entry.key);
            guiSlot.setOccupiedItem(entry.value);
        }
    }

    /**
     * Populate items from the container inventory
     *
     * @param inventory the inventory
     */
    public void populateContainerItemsAndShow(ContainerInventory inventory) {
        activeContainerInventory = inventory;

        // container slots do not have a source inventory yet, but the players inventory as a receiver
        containerSlots.forEach(slot -> slot.getTarget().setSourceInventory(player.getInventory()));
        containerSlots.forEach(slot -> slot.getTarget().setTargetInventory(activeContainerInventory));
        // player slots have their own inventory but no receiver for dragging of items
        playerSlots.forEach(slot -> slot.getTarget().setSourceInventory(activeContainerInventory));
        playerSlots.forEach(slot -> slot.getTarget().setTargetInventory(player.getInventory()));

        for (IntMap.Entry<Item> entry : inventory.items()) {
            final InventoryGuiSlot guiSlot = containerSlots.get(entry.key);
            guiSlot.setOccupiedItem(entry.value);
        }
        show();
    }

    @Override
    public void show() {
        super.show();
        isOnContainer = true;
        populatePlayerInventoryItems();
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
    }

    @Override
    public void hideRelatedGuis() {
        guiManager.hideGui(GuiType.INVENTORY);
    }
}
