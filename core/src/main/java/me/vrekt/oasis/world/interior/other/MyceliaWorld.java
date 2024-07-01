package me.vrekt.oasis.world.interior.other;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;

/**
 * Mycelia world
 */
public final class MyceliaWorld extends GameWorldInterior {

    public MyceliaWorld(GameWorld parentWorld, String interiorMap, InteriorWorldType type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld, interiorMap, type, cursor, entranceBounds);

        this.worldMap = Asset.WRYNN_BASEMENT;
    }

    @Override
    public void loadWorld(boolean isGameSave) {
        super.loadWorld(isGameSave);

        renderer.getCamera().zoom = 0.8f;
    }

    @Override
    protected void exit() {
        super.exit();

        renderer.getCamera().zoom = 1.0f;
    }
}
