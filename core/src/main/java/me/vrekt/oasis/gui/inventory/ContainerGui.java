package me.vrekt.oasis.gui.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.entity.inventory.container.containers.ChestInventory;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.utility.GuiUtilities;

import java.util.LinkedList;

/**
 * A gui for chests/containers
 */
public final class ContainerGui extends Gui {

    private final VisTable rootTable;
    private final LinkedList<ContainerUiSlot> chestSlots = new LinkedList<>();
    private final LinkedList<ContainerPlayerInventoryUiSlot> inventorySlots = new LinkedList<>();

    private final OasisPlayer player;
    private ContainerInventory inventory;

    private boolean containerContentsChanged;

    public ContainerGui(GameGui gui, Asset asset) {
        super(gui, asset, "Container");
        this.player = gui.getGame().getPlayer();

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(asset.get("pause")));
        TableUtils.setSpacingDefaults(rootTable);

        final VisTable primary = new VisTable(true);
        final VisTable secondary = new VisTable(true);
        primary.top();

        primary.add(new VisLabel("Chest", new Label.LabelStyle(gui.getMedium(), Color.WHITE))).size(48, 48);
        primary.row().padTop(-8);

        final TextureRegionDrawable drawable = new TextureRegionDrawable(asset.get("theme"));
        this.inventory = new ChestInventory(12);

        // populate inventory components
        // the provided inventory context is temporary and may be modified
        GuiUtilities.populateInventoryComponents(inventory, drawable, gui, consumer -> {
            chestSlots.add(new ContainerUiSlot(consumer.overlay, consumer.item, consumer.amountLabel));
            primary.add(consumer.overlay).size(48, 48);
            if (consumer.index % 3 == 0) primary.row();
        });

        secondary.top();
        secondary.add(new VisLabel("Your Inventory", new Label.LabelStyle(gui.getMedium(), Color.WHITE))).size(48, 48);
        secondary.row().padTop(-8);

        // populate player inventory components
        GuiUtilities.populateInventoryComponents(player.getInventory(), drawable, gui, consumer -> {
            inventorySlots.add(new ContainerPlayerInventoryUiSlot(consumer.overlay, consumer.item, consumer.amountLabel));
            secondary.add(consumer.overlay).size(48, 48);
            if (consumer.index % 3 == 0) secondary.row();
        });

        VisSplitPane splitPane = new VisSplitPane(primary, secondary, false);
        rootTable.add(splitPane).fill().expand();
        gui.createContainer(rootTable).fill();
    }

    @Override
    public void update() {
        // update player context
        player.getInventory().getSlots().forEach((index, slot) -> {
            final ContainerPlayerInventoryUiSlot ui = inventorySlots.get(index);
            if (slot.isOccupied() && !ui.occupied) {
                ui.setItem(slot.getItem());
                ui.setStackableState();
                ui.setSlot(index);
            }
        });

        // update container context if changed
        if (containerContentsChanged) {
            populate(inventory);
        }
    }

    /**
     * Populate the GUI
     * TODO: Bigger container support for containers > 12 slots in size
     *
     * @param inventory the inventory
     */
    public void populate(ContainerInventory inventory) {
        this.inventory = inventory;

        inventory.getSlots().forEach((slot, item) -> {
            final ContainerUiSlot ui = chestSlots.get(slot);
            if (!ui.occupied) {
                ui.setItem(item.getItem());
                ui.setStackableState();
                ui.setSlot(slot);
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
     * Handles the player's inventory context and when they transfer an item from -> to
     */
    private final class ContainerPlayerInventoryUiSlot extends AbstractInventoryUiSlot {
        private int slot;

        public ContainerPlayerInventoryUiSlot(Stack stack, Image item, VisLabel amountLabel) {
            super(stack, item, amountLabel, gui.getStyles().getTooltipStyle());

            // transfer this item into the container
            stack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleClickActionListener();
                }
            });
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        private void handleClickActionListener() {
            // if this slot isn't occupied or our container inventory is full, ignore
            if (!occupied || inventory.isInventoryFull()) return;

            // update this container GUI
            player.getInventory().transferItemTo(slot, inventory);
            containerContentsChanged = true;
            removeItem();
        }

        @Override
        protected void removeItem() {
            this.slot = -1;
            reset();
        }
    }

    private class ContainerUiSlot extends AbstractInventoryUiSlot {
        private int slot;

        public ContainerUiSlot(Stack stack, Image item, VisLabel amountLabel) {
            super(stack, item, amountLabel, gui.getStyles().getTooltipStyle());

            stack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleClickActionListener();
                }
            });
        }

        private void handleClickActionListener() {
            if (!occupied || player.getInventory().isInventoryFull()) return;

            inventory.transferItemTo(slot, player.getInventory());
            removeItem();
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        @Override
        protected void removeItem() {
            this.slot = -1;
            reset();
        }

    }

}
