package me.vrekt.oasis.gui.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.utility.logging.Logging;

import java.util.LinkedList;

/**
 * A gui for chests/containers
 */
public final class ContainerGui extends Gui {

    private final VisTable rootTable;
    private final LinkedList<ContainerUiSlot> chestSlots = new LinkedList<>();
    private final LinkedList<ContainerUiSlot> inventorySlots = new LinkedList<>();

    private final OasisPlayerSP player;
    private ContainerInventory inventory;

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

        final TextureRegionDrawable drawable = new TextureRegionDrawable(asset.get("artifact_slot"));
        for (int i = 1; i < 12; i++) {
            final VisImage slot = new VisImage(drawable);
            final VisImage item = new VisImage();

            // create a separate container for the item image... so it doesn't get stretched.
            final VisTable itemTable = new VisTable(true);
            itemTable.add(item);

            final Stack overlay = new Stack(slot, new Container<Table>(itemTable));
            chestSlots.add(new ContainerUiSlot(overlay, item));
            primary.add(overlay).size(48, 48);
            if (i % 3 == 0) primary.row();
        }

        secondary.top();
        secondary.add(new VisLabel("Your Inventory", new Label.LabelStyle(gui.getMedium(), Color.WHITE))).size(48, 48);
        secondary.row().padTop(-8);
        for (int i = 1; i < player.getInventory().getInventorySize(); i++) {
            final VisImage slot = new VisImage(drawable);
            final VisImage item = new VisImage();

            // create a separate container for the item image... so it doesn't get stretched.
            final VisTable itemTable = new VisTable(true);
            itemTable.add(item);
            final Stack overlay = new Stack(slot, new Container<Table>(itemTable));
            inventorySlots.add(new ContainerUiSlot(overlay, item));
            secondary.add(overlay).size(48, 48);
            if (i % 3 == 0) secondary.row();
        }

        VisSplitPane splitPane = new VisSplitPane(primary, secondary, false);
        rootTable.add(splitPane).fill().expand();
        gui.createContainer(rootTable).fill();
    }

    @Override
    public void update() {
        // update ui based on player inventory status
        player.getInventory().getSlots().forEach((slot, item) -> {
            final ContainerUiSlot ui = inventorySlots.get(slot);
            if (!ui.occupied) {
                ui.setItem(item.getItem());
            }
        });
    }

    /**
     * Populate the GUI
     *
     * @param inventory the inventory
     */
    public void populate(ContainerInventory inventory) {
        inventory.getSlots().forEach((slot, item) -> {
            final ContainerUiSlot ui = chestSlots.get(slot);
            ui.setItem(item.getItem());
            ui.setSlot(slot);
        });
        this.inventory = inventory;
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

    private final class ContainerUiSlot extends AbstractInventoryUiSlot {
        private int slot;

        public ContainerUiSlot(Stack stack, VisImage item) {
            super(stack, item, new Tooltip.TooltipStyle());

            stack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleClickActionListener();
                }
            });
        }

        private void handleClickActionListener() {
            if (!occupied) return;
            if (player.getInventory().isInventoryFull()) return;
            if (inventory == null) {
                Logging.error(this, "Inventory is null?");
                return;
            }
            inventory.transferItemTo(slot, player.getInventory());
            this.removeItem();
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        @Override
        protected void removeItem() {
            super.reset();
        }

    }

}
