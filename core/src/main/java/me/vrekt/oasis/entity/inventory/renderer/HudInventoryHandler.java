package me.vrekt.oasis.entity.inventory.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;

import java.util.stream.IntStream;

/**
 * Manages rendering the players hud inventory or both.
 */
public final class HudInventoryHandler {

    private final OasisPlayerSP player;
    private final InventoryUiSlot[] slots;
    private final int size;

    public HudInventoryHandler(int size, OasisPlayerSP player) {
        slots = new InventoryUiSlot[size];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new InventoryUiSlot();
        }

        this.size = size;
        this.player = player;

        for (InventoryUiSlot slot : slots) {
            slot.slot = new Image();
            slot.itemName = new Label("", player.getGame().getAsset().getDefaultLibgdxSkin());
        }
    }

    /**
     * Initialize the renderer.
     *
     * @param parent the parent
     * @param slot   the slot
     */
    public void initialize(Table parent, TextureRegionDrawable slot) {

        IntStream.range(0, size)
                .forEach(i -> {
                    final Stack stack = new Stack();
                    final Image image = new Image(slot);

                    final Label label = slots[i].itemName;
                    label.setStyle(initializeLabelStyle());
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
                });
    }

    private Label.LabelStyle initializeLabelStyle() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = player.getGame().getAsset().getMedium();
        labelStyle.fontColor = Color.WHITE;
        return labelStyle;
    }


    /**
     * Update images of occupied slots
     */
    public void update() {
        player.getInventory().getSlots().forEach((slot, item) -> {
            if (item != null && item.isOccupied()) {
                // valid item in this slot #
                if (slots[slot].slot.getDrawable() == null) {
                    slots[slot].slot.setDrawable(new TextureRegionDrawable(item.getItem().getTexture()));
                    slots[slot].itemName.setText(item.getItem().getItemName());
                }
            } else {
                slots[slot].reset();
            }
        });
    }

    private static final class InventoryUiSlot {
        private Image slot;
        private Label itemName;

        private void reset() {
            slot.setDrawable(null);
            itemName.setText("");
        }

    }

}
