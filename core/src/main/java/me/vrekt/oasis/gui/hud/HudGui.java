package me.vrekt.oasis.gui.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;

/**
 * Hud GUI includes on screen inventory, class, mini-map, etc.
 */
public final class HudGui extends Gui {

    private final Table rootTable, classTable, healthTable;
    private final Image[] inventorySlotImages = new Image[6];
    private final Stack[] slots = new Stack[6];

    public HudGui(GameGui gui, Asset asset) {
        super(gui, asset);

        rootTable = new Table();
        rootTable.setVisible(true);
        rootTable.left();

        gui.createContainer(rootTable).bottom();

        // the actual table that holds the inventory slots
        final Table inventory = new Table();

        // the table holding player class information
        classTable = new Table();
        classTable.setVisible(true);
        classTable.left();

        gui.createContainer(classTable).bottom().left().pad(8);

        healthTable = new Table();
        healthTable.setVisible(true);
        healthTable.left();

        gui.createContainer(healthTable).top().left().pad(4);
        healthTable.add(new Image(asset.get("health_bar"))).size(256, 24);

        final TextureRegionDrawable slot = new TextureRegionDrawable(asset.get("inventory_slot"));

        for (int i = 0; i < 6; i++) {
            inventorySlotImages[i] = new Image();
            slots[i] = createStackedSlot(slot);
        }

        // init table with all stacks
        for (Stack stack : slots) {
            inventory.add(stack).size(48, 48).padRight(2f);
        }

        classTable.add(new Image(asset.get("nature_class"))).size(48, 48);
        rootTable.add(inventory).padBottom(8);
    }

    @Override
    public void update() {

    }

    private Stack createStackedSlot(TextureRegionDrawable slot) {
        final Stack stack = new Stack();
        stack.add(new Image(slot));
        return stack;
    }

    @Override
    public void showGui() {
        rootTable.setVisible(true);
        classTable.setVisible(true);
        isShowing = true;
    }

    @Override
    public void hideGui() {
        rootTable.setVisible(false);
        classTable.setVisible(false);
        isShowing = false;
    }

}
