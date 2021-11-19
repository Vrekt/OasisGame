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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import me.vrekt.oasis.world.interior.Interior;

import java.util.HashMap;
import java.util.Map;

public final class Asset {

    public static final String ASSETS = "items/Assets.atlas";
    public static final String PARTICLE_ATLAS = "farm/effects/farm_particle.atlas";
    public static final String PARTICLE_FILE = "farm/effects/farm_particle.p";
    public static final String ATHENA_WORLD = "worlds/athena/Athena.tmx";

    private final AssetManager assetManager = new AssetManager();

    private BitmapFont romulusBig, romulusSmall, romulusSmaller;
    private Skin skin;

    private final Map<Interior, TiledMap> cachedMaps = new HashMap<>();
    private TmxMapLoader mapLoader;

    /**
     * Load general assets needed by each world.
     */
    public void load() {
        mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
        assetManager.load(ASSETS, TextureAtlas.class);

        // particle effect handler
        assetManager.setLoader(ParticleEffect.class, new ParticleEffectLoader(new InternalFileHandleResolver()));
        loadParticleEffects();
        loadFonts();

        assetManager.setLoader(TiledMap.class, mapLoader);
        assetManager.load(ATHENA_WORLD, TiledMap.class, new TmxMapLoader.Parameters());
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
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
        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.08);
        romulusBig = generator.generateFont(parameter);

        // generate a smaller text for sub title information
        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.04);
        romulusSmall = generator.generateFont(parameter);

        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.02);
        romulusSmaller = generator.generateFont(parameter);
        generator.dispose();

    }

    /**
     * Load an interior
     *
     * @param interior  the interior
     * @param cacheTime the time to cache the map
     * @return the tiled map.
     */
    public TiledMap loadInterior(Interior interior, float cacheTime) {
        if (cachedMaps.containsKey(interior)) {
            return cachedMaps.get(interior);
        }
        final TiledMap map = mapLoader.load(interior.getInteriorName());
        this.cachedMaps.put(interior, map);
        return map;
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

    public BitmapFont getRomulusSmaller() {
        return romulusSmaller;
    }
}
