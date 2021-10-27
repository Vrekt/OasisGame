package me.vrekt.oasis.world.region;

import com.badlogic.gdx.math.Rectangle;

public final class WorldRegion {

    private final String regionName;
    private final Rectangle regionBounds;

    public WorldRegion(String regionName, Rectangle regionBounds) {
        this.regionName = regionName;
        this.regionBounds = regionBounds;
    }

    public boolean isIn(float x, float y) {
        return regionBounds.contains(x, y);
    }

    public String getRegionName() {
        return regionName;
    }

    public Rectangle getRegionBounds() {
        return regionBounds;
    }
}
