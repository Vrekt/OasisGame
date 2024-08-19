package me.vrekt.oasis.world.tutorial.interior.wrynn;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.input.Cursor;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;
import me.vrekt.oasis.world.interior.Interior;

/**
 * The basement of wrynn's house, first real quest location.
 */
public final class WrynnBasementInterior extends GameWorldInterior {

    public static final int WORLD_ID = 3;

    public WrynnBasementInterior(GameWorld parentWorld, String interiorMap, Interior type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld, interiorMap, type, cursor, entranceBounds);

        this.worldMap = Asset.WRYNN_BASEMENT;
        this.worldId = WORLD_ID;
    }

    @Override
    public void enterWorld() {
        super.enterWorld();

        player.scalePlayerBy(0.8f);
        getRenderer().getCamera().zoom = 0.8f;
    }

    @Override
    public void exit() {
        super.exit();

        player.scalePlayerBy(1.0f);
        getRenderer().getCamera().zoom = 1.0f;
    }
}
