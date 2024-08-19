package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.gui.input.Cursor;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;
import me.vrekt.oasis.world.tutorial.interior.other.LyraHouse;
import me.vrekt.oasis.world.tutorial.interior.other.MyceliaShopInterior;
import me.vrekt.oasis.world.tutorial.interior.wrynn.WrynnBasementInterior;
import me.vrekt.oasis.world.tutorial.interior.wrynn.WrynnHouseInterior;

/**
 * List of all interior types
 */
public enum Interior {

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
    },
    MYCELIA_SHOP {
        @Override
        public GameWorldInterior createInterior(GameWorld world, String asset, Cursor cursor, Rectangle bounds) {
            return new MyceliaShopInterior(world, asset, MYCELIA_SHOP, cursor, bounds);
        }
    },
    HOUSE2 {
        @Override
        public GameWorldInterior createInterior(GameWorld world, String asset, Cursor cursor, Rectangle bounds) {
            return new LyraHouse(world, asset, HOUSE2, cursor, bounds);
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

    public static Interior of(int index) {
        if (index >= values().length) return Interior.NONE;
        return values()[index];
    }

    /**
     * Find the type
     *
     * @param key the key
     * @return the type
     */
    public static Interior of(String key) {
        try {
            return valueOf(key.toUpperCase());
        } catch (IllegalArgumentException exception) {
            GameLogging.exceptionThrown("InteriorWorldType", "Failed to find of interior key! key=%s", exception, key);
            return NONE;
        }
    }
}
