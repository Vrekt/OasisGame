package me.vrekt.oasis.world.interior.wrynn;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.instance.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;

/**
 * The basement of wrynns house, first real quest location.
 */
public final class WrynnBasementInterior extends GameWorldInterior {

    public WrynnBasementInterior(GameWorld parentWorld, String interiorMap, InteriorWorldType type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld, interiorMap, type, cursor, entranceBounds);
    }
}
