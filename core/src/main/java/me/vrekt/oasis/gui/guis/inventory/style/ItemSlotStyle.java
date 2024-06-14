package me.vrekt.oasis.gui.guis.inventory.style;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.item.Item;

public enum ItemSlotStyle {

    NORMAL {
        @Override
        public Drawable get(GuiManager manager) {
            return manager.style().slots().normal();
        }

        @Override
        public Drawable down(GuiManager manager) {
            return manager.style().slots().normalDown();
        }
    },

    COMMON {
        @Override
        public Drawable get(GuiManager manager) {
            return manager.style().slots().common();
        }

        @Override
        public Drawable down(GuiManager manager) {
            return manager.style().slots().commonDown();
        }
    },

    COSMIC {
        @Override
        public Drawable get(GuiManager manager) {
            return manager.style().slots().cosmic();
        }

        @Override
        public Drawable down(GuiManager manager) {
            return manager.style().slots().cosmicDown();
        }
    };

    /**
     * Get the default slot style
     *
     * @param manager manager
     * @return the drawable
     */
    public abstract Drawable get(GuiManager manager);

    /**
     * Get the mouse over style
     *
     * @param manager manager
     * @return the drawable
     */
    public abstract Drawable down(GuiManager manager);

    /**
     * Convert an item rarity to this
     *
     * @param item the item
     * @return the rarity slot style
     */
    public static ItemSlotStyle of(Item item) {
        return switch (item.rarity()) {
            case COMMON -> COMMON;
            case COSMIC -> COSMIC;
            case VOID, DIVINE -> COSMIC;
        };
    }

}
