package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.wrynn.WrynnBasementInterior;
import me.vrekt.oasis.world.interior.wrynn.WrynnHouseInterior;

/**
 * List of all interior types
 */
public enum InteriorWorldType {

    NONE {
        @Override
        public GameWorldInterior createInterior(GameWorld world, String asset, Cursor cursor, Rectangle bounds) {
            throw new UnsupportedOperationException("No instance registered with name " + asset);
        }
    },
    WRYNN_HOUSE {
        @Override
        public GameWorldInterior createInterior(GameWorld world, String asset, Cursor cursor, Rectangle bounds) {
            return new WrynnHouseInterior(world, asset, WRYNN_HOUSE, cursor, bounds);
        }
    },
    WRYNN_BASEMENT {
        @Override
        public GameWorldInterior createInterior(GameWorld world, String asset, Cursor cursor, Rectangle bounds) {
            return new WrynnBasementInterior(world, asset, WRYNN_BASEMENT, cursor, bounds);
        }
    };

    /**
     * Create the interior
     *
     * @param world  the world
     * @param asset  the asset map
     * @param cursor the cursor
     * @param bounds the entrance bounds
     * @return the new interior
     */
    public abstract GameWorldInterior createInterior(GameWorld world, String asset, Cursor cursor, Rectangle bounds);

    /**
     * Find the type
     *
     * @param key the key
     * @return the type
     */
    public static InteriorWorldType of(String key) {
        try {
            return valueOf(key.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return NONE;
        }
    }
}
