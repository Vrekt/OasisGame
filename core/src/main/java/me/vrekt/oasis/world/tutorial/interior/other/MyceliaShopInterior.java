package me.vrekt.oasis.world.tutorial.interior.other;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.input.Cursor;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.oasis.world.tiled.TiledMapCache;
import me.vrekt.oasis.world.tutorial.MyceliaWorld;

/**
 * A shop inside {@link MyceliaWorld}
 */
public final class MyceliaShopInterior extends GameWorldInterior {

    public static final int WORLD_ID = 5;

    public MyceliaShopInterior(GameWorld parentWorld, String interiorMap, Interior type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld, interiorMap, type, cursor, entranceBounds);

        this.worldName = "MyceliaShop";
        this.worldMap = Asset.MYCELIA_SHOP;
        this.requiresNearUpdating = false;
        this.worldId = WORLD_ID;
    }

    @Override
    public void loadWorldTiledMap(boolean isGameSave) {
        super.loadWorldTiledMap(isGameSave);
    }

    @Override
    public void loadTiledMap(TiledMap worldMap, float worldScale) {
        super.loadTiledMap(worldMap, worldScale);
        this.mapCache = new TiledMapCache(worldMap);
    }
}
