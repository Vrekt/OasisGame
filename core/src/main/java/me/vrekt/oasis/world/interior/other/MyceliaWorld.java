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

        this.worldMap = Asset.MYCELIA_WORLD;
        this.requiresNearUpdating = false;
        // this map is flipped, for whatever reason. Changes the origin of the
        // circle collision box
        this.isFlipped = true;
    }

    @Override
    public void loadWorld(boolean isGameSave) {
        // scale this player before we create their new box body
        player.scalePlayerBy(0.8f);
        super.loadWorld(isGameSave);

        renderer.getCamera().zoom = 0.8f;
    }

    @Override
    protected void exit() {
        // scale this player before their new box is created
        player.scalePlayerBy(1.0f);
        super.exit();

        renderer.getCamera().zoom = 1.0f;
    }
}
