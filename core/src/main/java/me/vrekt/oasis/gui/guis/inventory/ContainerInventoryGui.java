package me.vrekt.oasis.gui.guis.inventory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.guis.inventory.utility.InventoryGuiSlot;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Container GUI
 */
public final class ContainerInventoryGui extends InventoryGui {

    private final OasisPlayer player;

    private final Array<InventoryGuiSlot> containerSlots = new Array<>();
    private final Array<InventoryGuiSlot> playerSlots = new Array<>();

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
                    final int newSlot = activeContainerInventory.transferItemTo(selectedContainerSlot.getSlotNumber(), 1, player.getInventory());
                    updateSingleContainerSlotTransfer(selectedContainerSlot.getSlotNumber(), newSlot);
                } else if (!isOnContainer && selectedPlayerSlot != null) {
                    final int newSlot = player.getInventory().transferItemTo(selectedPlayerSlot.getSlotNumber(), 1, activeContainerInventory);
                    updateSinglePlayerSlotTransfer(selectedPlayerSlot.getSlotNumber(), newSlot);
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
                    final int newSlot = activeContainerInventory.transferItemsTo(selectedContainerSlot.getSlotNumber(), player.getInventory());
                    updateContainerSlotTransfer(selectedContainerSlot.getSlotNumber(), newSlot);
                } else if (!isOnContainer && selectedPlayerSlot != null) {
                    final int newSlot = player.getInventory().transferItemsTo(selectedPlayerSlot.getSlotNumber(), activeContainerInventory);
                    updatePlayerSlotTransfer(selectedPlayerSlot.getSlotNumber(), newSlot);
                }
            }
        });
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

    @Override
    public void handleSlotClicked(InventoryGuiSlot slot) {
        if (slot.isContainerSlot()) {
            selectedContainerSlot = slot.isEmpty() ? null : slot;
            selectedPlayerSlot = null;
            isOnContainer = true;
        } else {
            selectedPlayerSlot = slot.isEmpty() ? null : slot;
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
        populateInventoryUiComponents(guiManager, player.getInventory().getInventorySize(), slotDrawable, true, component -> {
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
        player.getInventory().getSlots().forEach((slotNumber, slot) -> {
            final InventoryGuiSlot guiSlot = playerSlots.get(slotNumber);
            guiSlot.setOccupiedItem(slot.getItem());
        });
    }

    /**
     * Populate items from the container inventory
     *
     * @param inventory the inventory
     */
    public void populateContainerItemsAndShow(ContainerInventory inventory) {
        activeContainerInventory = inventory;

        inventory.getSlots().forEach((slotNumber, slot) -> {
            final InventoryGuiSlot guiSlot = containerSlots.get(slotNumber);
            guiSlot.setOccupiedItem(slot.getItem());
        });
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
}
