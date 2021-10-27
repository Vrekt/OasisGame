package me.vrekt.oasis.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public final class Asset {

    public static final String ASSETS = "items/Assets.atlas";
    public static final String PARTICLE_ATLAS = "farm/effects/farm_particle.atlas";
    public static final String PARTICLE_FILE = "farm/effects/farm_particle.p";
    public static final String ATHENA_WORLD = "worlds/athena/Athena.tmx";

    private final AssetManager assetManager = new AssetManager();

    private BitmapFont romulusBig, romulusSmall, romulusClone;

    /**
     * Load general assets needed by each world.
     */
    public void load() {
        assetManager.load(ASSETS, TextureAtlas.class);

        // particle effect handler
        assetManager.setLoader(ParticleEffect.class, new ParticleEffectLoader(new InternalFileHandleResolver()));
        loadParticleEffects();
        loadFonts();

        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(ATHENA_WORLD, TiledMap.class, new TmxMapLoader.Parameters());
    }

    /**
     * Load all particle effects
     */
    private void loadParticleEffects() {
        final ParticleEffectLoader.ParticleEffectParameter parameters = new ParticleEffectLoader.ParticleEffectParameter();
        parameters.atlasFile = PARTICLE_ATLAS;

        assetManager.load(PARTICLE_FILE, ParticleEffect.class, parameters);
    }

    /**
     * Load fonts
     */
    private void loadFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font/sdss.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // big text for context or titles
        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.06);
        romulusBig = generator.generateFont(parameter);

        // generate a smaller text for sub title information
        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.04);
        romulusSmall = generator.generateFont(parameter);
        romulusClone = generator.generateFont(parameter);
        generator.dispose();
    }

    public TextureAtlas getAssets() {
        return assetManager.get(ASSETS);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public <T> T get(String name) {
        return assetManager.get(name);
    }

    public TextureAtlas getAtlas(String name) {
        return assetManager.get(name);
    }

    public BitmapFont getRomulusBig() {
        return romulusBig;
    }

    public BitmapFont getRomulusSmall() {
        return romulusSmall;
    }

    public BitmapFont getRomulusClone() {
        return romulusClone;
    }
}
