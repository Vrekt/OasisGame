package me.vrekt.oasis.entity.inventory.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.item.Item;
import org.apache.commons.lang3.StringUtils;

/**
 * Manages rendering the players hud inventory or both.
 */
public final class HudInventoryHandler {

    private final GameGui gui;
    private final OasisPlayerSP player;
    private final InventoryUiSlot[] slots;
    private final int size;

    public HudInventoryHandler(int size, GameGui gui, OasisPlayerSP player) {
        slots = new InventoryUiSlot[6];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new InventoryUiSlot();
        }

        this.size = 6;
        this.gui = gui;
        this.player = player;

        for (InventoryUiSlot slot : slots) {
            slot.slot = new Image();
            slot.name = new Label("", player.getGame().getAsset().getDefaultLibgdxSkin());
        }
    }

    /**
     * Initialize the renderer.
     *
     * @param parent the parent
     * @param slot   the slot
     */
    public void initialize(Table parent, TextureRegionDrawable slot) {
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = gui.getMedium();
        style.fontColor = Color.WHITE;

        for (int i = 0; i < size; i++) {
            final Stack stack = new Stack();
            final Image image = new Image(slot);

            final Label label = slots[i].name;
            label.setStyle(style);
            label.setVisible(false);

            stack.add(image);

            // logic for mouse over slot items
            stack.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (!label.getText().isEmpty()) {
                        label.setVisible(true);
                    }
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    label.setVisible(false);
                }
            });

            final Table tooltips = new Table();
            tooltips.add(label);

            final Table other = new Table();
            other.add(slots[i].slot);

            final Container<Table> overlay = new Container<>(other);
            final Container<Table> tips = new Container<>(tooltips).padBottom((48 * 1.5f));

            stack.add(tips);
            stack.add(overlay);
            parent.add(stack).size(48, 48).padRight(2f);
        }
    }


    /**
     * Update images of occupied slots
     */
    public void update() {
        // update what we have for each slot
        for (int i = 0; i < slots.length; i++) {
            final InventoryUiSlot slot = slots[i];
            final Item item = player.getInventory().getItem(i);
            if (item == null) {
                // reset the slot data if it is still there
                if (slot.isOccupied()) slot.reset();
            } else {
                if (!slot.isOccupied()) {
                    slot.occupy(item);
                }
            }
        }
    }

    private static final class InventoryUiSlot {
        private Image slot;
        private Label name;
        private boolean occupied;

        private void occupy(Item item) {
            slot.setDrawable(new TextureRegionDrawable(item.getTexture()));
            name.setText(item.getItemName());
            occupied = true;
        }

        private boolean isOccupied() {
            return occupied;
        }

        private void reset() {
            slot.setDrawable(null);
            name.setText(StringUtils.EMPTY);
            occupied = false;
        }

    }

}
