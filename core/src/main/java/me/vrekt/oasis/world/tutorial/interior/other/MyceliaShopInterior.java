package me.vrekt.oasis.world.tutorial.interior.other;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.tiled.TiledMapCache;
import me.vrekt.oasis.world.tutorial.MyceliaWorld;

/**
 * A shop inside {@link MyceliaWorld}
 */
public final class MyceliaShopInterior extends GameWorldInterior {

    public static final int WORLD_ID = 5;

    public MyceliaShopInterior(GameWorld parentWorld, String interiorMap, InteriorWorldType type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld, interiorMap, type, cursor, entranceBounds);

        this.worldName = "MyceliaShop";
        this.worldMap = Asset.MYCELIA_SHOP;
        this.requiresNearUpdating = false;
        this.worldId = WORLD_ID;
    }

    @Override
    public void loadWorld(boolean isGameSave) {
        super.loadWorld(isGameSave);
    }

    @Override
    public void loadTiledMap(TiledMap worldMap, float worldScale) {
        super.loadTiledMap(worldMap, worldScale);
        this.mapCache = new TiledMapCache(worldMap);
    }
}
