package me.vrekt.oasis.world.tiled;

import com.badlogic.gdx.maps.tiled.TiledMapTile;

/**
 * Stores basic material data about a tile
 */
public final class StaticSoundMapTile {

    private final TileMaterialType material;

    public StaticSoundMapTile(TiledMapTile copy) {
        final String resource = copy.getProperties().get("sound", String.class);
        if (resource == null) {
            material = TileMaterialType.NONE;
        } else {
            material = TileMaterialType.of(resource);
        }
    }

    /**
     * @return the material type or {@code NONE} if none.
     */
    public TileMaterialType material() {
        return material;
    }
}
