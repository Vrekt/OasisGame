package me.vrekt.oasis.gui.guis.inventory.style;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.item.Item;

public enum ItemSlotStyle {

    NORMAL {
        @Override
        public Drawable get() {
            return Styles.slots().normal();
        }

        @Override
        public Drawable down() {
            return Styles.slots().normalDown();
        }
    },

    COMMON {
        @Override
        public Drawable get() {
            return Styles.slots().common();
        }

        @Override
        public Drawable down() {
            return Styles.slots().commonDown();
        }
    },

    UN_COMMON {
        @Override
        public Drawable get() {
            return Styles.slots().uncommon();
        }

        @Override
        public Drawable down() {
            return Styles.slots().uncommonDown();
        }
    },

    COSMIC {
        @Override
        public Drawable get() {
            return Styles.slots().cosmic();
        }

        @Override
        public Drawable down() {
            return Styles.slots().cosmicDown();
        }
    },
    VOID {
        @Override
        public Drawable get() {
            return Styles.slots().vd();
        }

        @Override
        public Drawable down() {
            return Styles.slots().voidDown();
        }
    };

    /**
     * Get the default slot style
     *
     * @return the drawable
     */
    public abstract Drawable get();

    /**
     * Get the mouse over style
     *
     * @return the drawable
     */
    public abstract Drawable down();

    /**
     * Convert an item rarity to this
     *
     * @param item the item
     * @return the rarity slot style
     */
    public static ItemSlotStyle of(Item item) {
        return switch (item.rarity()) {
            case COMMON -> COMMON;
            case UN_COMMON -> UN_COMMON;
            case COSMIC -> COSMIC;
            case VOID -> VOID;
        };
    }

}
