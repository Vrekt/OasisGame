package me.vrekt.oasis.entity.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;

/**
 * Manages rendering the players hud inventory or both.
 */
public final class InventoryRenderer {

    private final OasisPlayerSP player;
    private final Image[] slots;
    private final int size;

    public InventoryRenderer(int size, OasisPlayerSP player) {
        slots = new Image[size];
        this.size = size;
        this.player = player;
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new Image();
        }
    }

    /**
     * Initialize the renderer.
     *
     * @param parent the parent
     * @param slot   the slot
     */
    public void initialize(Table parent, TextureRegionDrawable slot) {
        for (int i = 0; i < size; i++) {
            final Stack stack = new Stack();
            stack.add(new Image(slot));
            stack.add(slots[i]);
            parent.add(stack).size(48, 48).padRight(2f);
        }
    }

    /**
     * Update images of occupied slots
     */
    public void update() {
        player.getInventory().getSlots().forEach((slot, item) -> {
            if (item != null && item.isOccupied()) {
                // valid item in this slot #
                if (slots[slot].getDrawable() == null) {
                    slots[slot].setDrawable(new TextureRegionDrawable(item.getItem().getTexture()));
                }
            } else {
                slots[slot].setDrawable(null);
            }
        });
    }

}
