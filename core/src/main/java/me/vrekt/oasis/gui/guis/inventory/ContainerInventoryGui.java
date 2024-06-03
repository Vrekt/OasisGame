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
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.guis.inventory.actions.InventorySlotSource;
import me.vrekt.oasis.gui.guis.inventory.actions.InventorySlotTarget;
import me.vrekt.oasis.gui.guis.inventory.utility.InventoryGuiSlot;
import me.vrekt.oasis.item.Item;

import java.util.concurrent.atomic.AtomicInteger;

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

        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("container")));
        final VisTable topTable = new VisTable(true), bottomTable = new VisTable(true);

        bottomTable.top();
        topTable.top();

        final TextureRegionDrawable slotDrawable = new TextureRegionDrawable(guiManager.getAsset().get("theme"));
        populateContainerInventoryComponents(topTable, slotDrawable);
        populatePlayerInventoryComponents(bottomTable, slotDrawable);

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

        final VisImageTextButton moveAllButton = new VisImageTextButton("Move All", guiManager.getStyle().getImageTextButtonStyle());
        final VisImageTextButton moveOneButton = new VisImageTextButton("Move One", guiManager.getStyle().getImageTextButtonStyle());
        final VisImageTextButton moveXButton = new VisImageTextButton("...", guiManager.getStyle().getImageTextButtonStyle());

        handleMoveAllButton(moveAllButton);
        handleMoveOneButton(moveOneButton);

        final VisTable buttonTable = new VisTable();
        buttonTable.add(moveAllButton).width(120);
        buttonTable.add(moveOneButton).padLeft(4f).width(120);
        buttonTable.add(moveXButton).padLeft(4f).width(120);

        rootTable.add(topTable);
        rootTable.row();
        rootTable.add(buttonTable);
        rootTable.row();
        rootTable.add(bottomTable);

        guiManager.addGui(rootTable);
    }

    /**
     * Handle the clicking of the move one button
     *
     * @param button the button
     */
    private void handleMoveOneButton(VisImageTextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isOnContainer && selectedContainerSlot != null) {
                    doTransferSingle(activeContainerInventory, player.getInventory(), selectedContainerSlot.getSlotNumber(), true);
                } else if (!isOnContainer && selectedPlayerSlot != null) {
                    doTransferSingle(player.getInventory(), activeContainerInventory, selectedPlayerSlot.getSlotNumber(), false);
                }
            }
        });
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
                if (isOnContainer && selectedContainerSlot != null) {
                    doTransferAll(activeContainerInventory, player.getInventory(), selectedContainerSlot.getSlotNumber(), true);
                } else if (!isOnContainer && selectedPlayerSlot != null) {
                    doTransferAll(player.getInventory(), activeContainerInventory, selectedPlayerSlot.getSlotNumber(), false);
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
    private void doTransferAll(AbstractInventory from, AbstractInventory to, int slot, boolean isContainer) {
        final int newSlot = from.transferAll(slot, to);
        if (isContainer) {
            updateContainerSlotTransfer(slot, newSlot);
        } else {
            updatePlayerSlotTransfer(slot, newSlot);
        }
    }

    /**
     * Do a single item transfer
     *
     * @param from        the from inventory
     * @param to          the to inventory
     * @param slot        the slot to transfer
     * @param isContainer if the from inventory is a container
     */
    private void doTransferSingle(AbstractInventory from, AbstractInventory to, int slot, boolean isContainer) {
        final int newSlot = from.transferAmount(slot, 1, to);
        if (isContainer) {
            updateSingleContainerSlotTransfer(slot, newSlot);
        } else {
            updateSinglePlayerSlotTransfer(slot, newSlot);
        }
    }

    /**
     * Update a single player item transfer
     *
     * @param playerSlot       the player slot transferred from
     * @param newContainerSlot the container slot transferred to
     */
    private void updateSinglePlayerSlotTransfer(int playerSlot, int newContainerSlot) {
        containerSlots.get(newContainerSlot).updateTransfer(activeContainerInventory, newContainerSlot);
        final boolean result = playerSlots.get(playerSlot).updateTransfer(player.getInventory(), playerSlot);
        // item was fully transferred
        if (result) selectedPlayerSlot = null;
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
     * Update a single container slot transfer
     *
     * @param containerSlot the container slot transferred from
     * @param newPlayerSlot the player slot transferred to
     */
    private void updateSingleContainerSlotTransfer(int containerSlot, int newPlayerSlot) {
        final boolean result = containerSlots.get(containerSlot).updateTransfer(activeContainerInventory, containerSlot);
        // item was fully transferred
        if (result) selectedContainerSlot = null;
        playerSlots.get(newPlayerSlot).updateTransfer(player.getInventory(), newPlayerSlot);
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
                doTransferAll(activeContainerInventory, player.getInventory(), selectedContainerSlot.getSlotNumber(), true);
                selectedContainerSlot = null;
            }
            selectedPlayerSlot = null;
            isOnContainer = true;
        } else {
            selectedPlayerSlot = slot.isEmpty() ? null : slot;
            if (selectedPlayerSlot != null && isShiftPressed) {
                // transfer instantly since the player shift clicked this slot
                doTransferAll(player.getInventory(), activeContainerInventory, selectedPlayerSlot.getSlotNumber(), false);
                selectedContainerSlot = null;
            }
            selectedContainerSlot = null;
            isOnContainer = false;
        }
    }

    /**
     * Populate container slots
     *
     * @param topTable     the top table
     * @param slotDrawable drawable
     */
    private void populateContainerInventoryComponents(VisTable topTable, TextureRegionDrawable slotDrawable) {
        final AtomicInteger slotTracker = new AtomicInteger();
        populateInventoryUiComponents(guiManager, 16, slotDrawable, false, component -> {
            final InventoryGuiSlot slot = new InventoryGuiSlot(guiManager,
                    this,
                    component.overlay(),
                    component.item(),
                    component.amountLabel(),
                    component.index());
            containerSlots.add(slot);

            final int progress = slotTracker.incrementAndGet();
            topTable.add(component.overlay()).size(48, 48);
            if (progress % 6 == 0) topTable.row();
        });
    }

    /**
     * Populate the components for the players inventory
     *
     * @param bottomTable  bottom table
     * @param slotDrawable drawable
     */
    private void populatePlayerInventoryComponents(VisTable bottomTable, TextureRegionDrawable slotDrawable) {
        final AtomicInteger slotTracker = new AtomicInteger();
        populateInventoryUiComponents(guiManager, player.getInventory().getSize(), slotDrawable, true, component -> {
            playerSlots.add(new InventoryGuiSlot(guiManager,
                    this,
                    component.overlay(),
                    component.item(),
                    component.amountLabel(),
                    component.index() < 6,
                    component.index()));

            final int progress = slotTracker.incrementAndGet();
            bottomTable.add(component.overlay()).size(48, 48);
            if (progress % 6 == 0) bottomTable.row();
        });
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
