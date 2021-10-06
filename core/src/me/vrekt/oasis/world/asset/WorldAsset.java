package me.vrekt.oasis.world.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import me.vrekt.oasis.world.farm.AllotmentStatus;

/**
 * Load assets required for a world.
 */
public abstract class WorldAsset {

    public static final String INTERACTIONS = "ui/interaction/Interactions.atlas";
    public static final String BOOK = "ui/book/Book.atlas";

    protected final AssetManager assetManager = new AssetManager();

    /**
     * Loads general assets used by all worlds.
     */
    public void loadAssets() {
        assetManager.load(INTERACTIONS, TextureAtlas.class);
        assetManager.load(BOOK, TextureAtlas.class);

        for (AllotmentStatus status : AllotmentStatus.values()) {
            if (status.getAsset() == null) continue;
            assetManager.load(status.getAsset(), Texture.class);
        }

        loadAssets0();
    }

    /**
     * Load required assets.
     */
    protected abstract void loadAssets0();

    public <T> T get(String name) {
        return assetManager.get(name);
    }

    public Texture getTexture(String name) {
        return assetManager.get(name);
    }

    public TextureAtlas getAtlas(String name) {
        return assetManager.get(name);
    }

}
