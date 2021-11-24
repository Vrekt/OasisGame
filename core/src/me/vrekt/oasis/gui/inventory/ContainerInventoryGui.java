package me.vrekt.oasis.gui.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.inventory.Inventory;
import me.vrekt.oasis.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Inventory for chests or anything else that isn't the players.
 */
public final class ContainerInventoryGui extends Gui {

    public static final int ID = 10;

    private final Table root, containerInventory, playerInventory;
    private final Label containerName;

    // actors by slot index
    private final Map<Integer, ContainerInventorySlot> slot = new HashMap<>();

    public ContainerInventoryGui(GameGui gui) {
        super(gui);

        root = new Table();
        root.setVisible(false);
        gui.createContainer(root).fill();

        // create right/left tables for content
        final Table right = new Table().center().top();
        final Table left = new Table().center().top();

        left.setBackground(new TextureRegionDrawable(gui.getAsset().get("quest_background")));
        right.setBackground(new TextureRegionDrawable(gui.getAsset().get("quest_background")));

        // expand left and right to fill space.
        root.add(left).grow();
        root.add(right).grow();

        left.add(containerName = new Label("", gui.getSkin(), "big", Color.BLACK));
        left.row();

        this.containerInventory = new Table();
        this.playerInventory = new Table();

        left.add(containerInventory);

        right.add(new Label("Inventory", gui.getSkin(), "big", Color.BLACK));
        right.row();
        right.add(playerInventory);
    }

    @Override
    public void showGui() {
        super.showGui();
        root.setVisible(true);
    }

    @Override
    public void hideGui() {
        super.hideGui();
        root.setVisible(false);
    }

    public void openContainer(Player player, Inventory inventory) {
        populateContainerInventory(inventory);
        populatePlayerInventory(player);
    }

    private void populatePlayerInventory(Player player) {
        final TextureRegionDrawable slot = new TextureRegionDrawable(gui.getAsset().get("inventory_slot"));
        final TextureRegionDrawable rare = new TextureRegionDrawable(gui.getAsset().get("inventory_slot_rare"));

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (i % 4 == 0 && i != 0) playerInventory.row();

            final Stack stack = new Stack();
            final Item item = player.getInventory().getItemAt(i);

            if (item != null) {
                switch (item.getRarity()) {
                    case EPIC:
                        stack.add(new Image(rare));
                        break;
                    default:
                        stack.add(new Image(slot));
                }
                stack.add(new Image(item.getTexture()));
            } else {
                stack.add(new Image(slot));
            }

            playerInventory.add(stack).size(64, 64).padLeft(2f).padBottom(2f);
        }
    }

    private void populateContainerInventory(Inventory container) {
        containerName.setText(container.getName());
        containerInventory.clear();
        slot.clear();

        final TextureRegionDrawable slot = new TextureRegionDrawable(gui.getAsset().get("inventory_slot"));
        final TextureRegionDrawable epic = new TextureRegionDrawable(gui.getAsset().get("inventory_slot_epic"));

        for (int i = 0; i < container.getSize(); i++) {
            if (i % 4 == 0 && i != 0) containerInventory.row();

            final Stack stack = new Stack();
            final Item item = container.getItemAt(i);

            Image si, ii = null;
            if (item != null) {
                switch (item.getRarity()) {
                    case EPIC:
                        stack.add(si = new Image(epic));
                        break;
                    default:
                        stack.add(si = new Image(slot));
                }
                stack.add(ii = new Image(item.getTexture()));
            } else {
                stack.add(si = new Image(slot));
            }

            this.slot.put(i, new ContainerInventorySlot(si, ii));
            containerInventory.add(stack).size(64, 64).padLeft(2f).padBottom(2f);
        }
    }

    private final class ContainerInventorySlot {

        // item of the slot
        private Image itemSlot, itemImage;

        public ContainerInventorySlot(Image itemSlot, Image itemImage) {
            this.itemSlot = itemSlot;
            this.itemImage = itemImage;
        }
    }

}
