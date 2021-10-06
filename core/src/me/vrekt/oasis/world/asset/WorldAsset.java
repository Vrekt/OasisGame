package me.vrekt.oasis.world.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Load assets required for a world.
 */
public abstract class WorldAsset {

    public static final String INTERACTIONS = "ui/interaction/Interactions.atlas";
    public static final String BOOK = "ui/book/Book.atlas";
    public static final String RAKE = "farm/animations/rake.png";
    public static final String PARTICLE_DIR = "farm/effects";
    public static final String PARTICLE_ATLAS = "farm/effects/farm_particle.atlas";
    public static final String PARTICLE_FILE = "farm/effects/farm_particle.p";
    public static final String PLANTS = "farm/plants/Plants.atlas";

    protected ParticleEffect farmParticles;

    protected final AssetManager assetManager = new AssetManager();

    /**
     * Loads general assets used by all worlds.
     */
    public void loadAssets() {
        assetManager.load(INTERACTIONS, TextureAtlas.class);
        assetManager.load(BOOK, TextureAtlas.class);
        assetManager.load(RAKE, Texture.class);
        assetManager.load(PLANTS, TextureAtlas.class);

        assetManager.setLoader(ParticleEffect.class, new ParticleEffectLoader(new InternalFileHandleResolver()));
        assetManager.finishLoading();

        final ParticleEffectLoader.ParticleEffectParameter parameters = new ParticleEffectLoader.ParticleEffectParameter();
        parameters.atlasFile = PARTICLE_ATLAS;
        parameters.imagesDir = Gdx.files.internal(PARTICLE_DIR + "/farm_particle.png");

        // assetManager.load(PARTICLE_FILE, ParticleEffect.class, parameters);
        // assetManager.finishLoading();
        //  farmParticles = assetManager.get(PARTICLE_FILE);
        //  farmParticles.start();

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

    public ParticleEffect getFarmParticles() {
        return farmParticles;
    }
}
