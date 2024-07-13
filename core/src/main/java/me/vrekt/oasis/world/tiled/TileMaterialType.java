package me.vrekt.oasis.world.tiled;

import me.vrekt.oasis.utility.logging.GameLogging;

/**
 * All different material types that have sounds.
 */
public enum TileMaterialType {

    NONE, GRASS, GRASSY, DIRT, MUD, TILE;

    public static TileMaterialType of(String resource) {
        try {
            return valueOf(resource.toUpperCase());
        } catch (IllegalArgumentException exception) {
            GameLogging.warn("TileMaterialType", "Failed to find material for %s", resource);
            return NONE;
        }
    }

}
