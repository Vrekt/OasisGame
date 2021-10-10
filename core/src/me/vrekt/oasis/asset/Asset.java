package me.vrekt.oasis.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public final class Asset {

    public static final String INTERACTIONS = "ui/interaction/Interactions.atlas";
    public static final String BOOK = "ui/book/Book.atlas";
    public static final String RAKE = "farm/animations/rake.png";
    public static final String PARTICLE_DIR = "farm/effects";
    public static final String PARTICLE_ATLAS = "farm/effects/farm_particle.atlas";
    public static final String PARTICLE_FILE = "farm/effects/farm_particle.p";
    public static final String PLANTS = "farm/plants/Plants.atlas";
    public static final String SEED_ITEMS = "items/seeds/Seeds.atlas";
    public static final String MAVIA_NPC = "npc/mavia/Mavia.atlas";

    public static final String ATHENA_WORLD = "worlds/athena/Athena.tmx";

    private final AssetManager assetManager = new AssetManager();

    private BitmapFont romulusBig, romulusSmall;

    /**
     * Load general assets needed by each world.
     */
    public void load() {
        assetManager.load(INTERACTIONS, TextureAtlas.class);
        assetManager.load(BOOK, TextureAtlas.class);
        assetManager.load(RAKE, Texture.class);
        assetManager.load(PLANTS, TextureAtlas.class);
        assetManager.load(SEED_ITEMS, TextureAtlas.class);
        assetManager.load(MAVIA_NPC, TextureAtlas.class);

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
        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.04);
        romulusBig = generator.generateFont(parameter);

        // generate a smaller text for sub title information
        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.02);
        romulusSmall = generator.generateFont(parameter);
        generator.dispose();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public <T> T get(String name) {
        return assetManager.get(name);
    }

    public Texture getTexture(String name) {
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
}
