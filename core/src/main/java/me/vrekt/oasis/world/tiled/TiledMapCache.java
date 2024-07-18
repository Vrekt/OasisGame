package me.vrekt.oasis.world.tiled;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import me.vrekt.oasis.utility.logging.GameLogging;

/**
 * Loads the tiles of all relevant layers for use later.
 */
public final class TiledMapCache {

    private final StaticSoundMapTile[][] tiles;

    public TiledMapCache(TiledMap map) {
        final long now = System.currentTimeMillis();
        final TiledMapTileLayer theGround = (TiledMapTileLayer) map.getLayers().get("TheGround");
        tiles = new StaticSoundMapTile[theGround.getWidth()][theGround.getHeight()];

        int tracking = 0;
        for (int x = 0; x < theGround.getWidth(); x++) {
            for (int y = 0; y < theGround.getHeight(); y++) {
                final TiledMapTileLayer.Cell cell = theGround.getCell(x, y);
                if (cell != null) {
                    tiles[x][y] = new StaticSoundMapTile(cell.getTile());
                    tracking++;
                }
            }
        }

        GameLogging.info(this, "Finished loading %d tiles in %d ms", tracking, (System.currentTimeMillis() - now));
    }

    /**
     * Get a material in a certain tile
     *
     * @param x x
     * @param y y
     * @return the sound
     */
    public TileMaterialType getMaterialAt(int x, int y) {
        return get(tiles[x][y]);
    }

    public TileMaterialType get(StaticSoundMapTile tile) {
        return tile == null ? TileMaterialType.NONE : tile.material();
    }
}
