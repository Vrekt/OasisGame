package me.vrekt.oasis.utility;

import me.vrekt.oasis.asset.game.Asset;

/**
 * Indicates the object needs some type of resource to load.
 */
public interface ResourceLoader {

    /**
     * Load whatever asset is required.
     */
    void load(Asset asset);

}
