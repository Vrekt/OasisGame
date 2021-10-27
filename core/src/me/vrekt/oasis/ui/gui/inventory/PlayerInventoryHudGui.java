package me.vrekt.oasis.ui.gui.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.inventory.PlayerInventory;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.ui.gui.GameGui;
import me.vrekt.oasis.ui.world.Gui;

/**
 * The players hud inventory
 */
public final class PlayerInventoryHudGui extends Gui {

    private final Table root;

    private final Array<InventoryImageSlot> slots = new Array<>();
    private final TextureRegion background, equippedBackground;
    private final PlayerInventory inventory;

    public PlayerInventoryHudGui(GameGui gui, OasisGame game) {
        super(gui);
        this.inventory = game.thePlayer.getInventory();

        // root container and positioning, bottom most of screen
        root = new Table();
        final Container<Table> container = gui.createContainer(root);
        container.bottom().padBottom(2f);

        // basic inventory slot background texture.
        background = gui.getAsset().getAssets().findRegion("inventory_slot");
        equippedBackground = gui.getAsset().getAssets().findRegion("equipped_inventory_slot");

        for (int i = 0; i < 6; i++) {
            final InventoryImageSlot slot = new InventoryImageSlot(new Image(), new Image(), i);
            final Item item = inventory.getItemAt(i);
            if (item != null) slot.itemImage.setDrawable(new TextureRegionDrawable(item.getTexture()));

            if (inventory.getEquippedSlot() == i) {
                slot.background.setDrawable(new TextureRegionDrawable(equippedBackground));
            } else {
                slot.background.setDrawable(new TextureRegionDrawable(background));
            }

            root.add(slot.content);
            slots.add(slot);
        }
    }

    @Override
    public void update() {
        // update inventory if needed

        if (inventory.isInvalid()) {
            inventory.setInvalid(false);

            root.clear();
            for (int i = 0; i < 6; i++) {
                slots.get(i).dispose();

                final InventoryImageSlot slot = new InventoryImageSlot(new Image(), new Image(), i);
                final Item item = inventory.getItemAt(i);
                if (item != null) slot.itemImage.setDrawable(new TextureRegionDrawable(item.getTexture()));

                if (inventory.getEquippedSlot() == i) {
                    slot.background.setDrawable(new TextureRegionDrawable(equippedBackground));
                } else {
                    slot.background.setDrawable(new TextureRegionDrawable(background));
                }

                root.add(slot.content);
                slots.add(slot);
            }
        }
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

    private final class InventoryImageSlot implements Disposable {

        private final Table content = new Table();
        private final Image itemImage, background;

        public InventoryImageSlot(Image background, Image item, int index) {
            this.itemImage = item;
            this.background = background;
            final Stack stack = new Stack();
            final Label indexLabel = new Label(String.valueOf(index + 1), PlayerInventoryHudGui.this.skin, "small", Color.BLACK);
            content.add(indexLabel).padBottom(-6f);
            content.row();

            stack.add(background);
            stack.add(item);
            content.add(stack).size(64, 64).padRight(6f).padBottom(2f);
        }

        @Override
        public void dispose() {
            content.clear();
        }
    }

}
