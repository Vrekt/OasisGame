package me.vrekt.oasis.asset.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.utility.logging.GameLogging;

public final class Asset implements Disposable {

    public static final String TUTORIAL_WORLD = "world/worlds/TutorialWorld - Copy.tmx";
    public static final String WRYNN_BASEMENT = "world/interiors/WrynnBasement.tmx";
    public static final String WRYNN_OFFICE = "world/interiors/Office.tmx";
    public static final String MYCELIA_WORLD = "world/worlds/MyceliaWorld.tmx";
    public static final String MYCELIA_SHOP = "world/interiors/MyceliaShop.tmx";
    public static final String HOUSE_2 = "world/interiors/House2.tmx";
    public static final String ASSETS = "OasisAssets.atlas";
    public static final String LP = "Lockpicking.atlas";
    public static final String UI = "Ui.atlas";

    private final AssetManager assetManager = new AssetManager();

    private TextureAtlas normal;

    private BitmapFont smaller, small, medium, large, boxy, mediumMipMapped;

    public Asset() {
    }

    public void load() {
        final long now = System.currentTimeMillis();

        assetManager.load(ASSETS, TextureAtlas.class);
        loadFonts();
        loadWorlds();

        assetManager.finishLoading();

        normal = assetManager.get(ASSETS);

        final long time = System.currentTimeMillis() - now;
        GameLogging.info("AssetManager", "Finished loading assets in %sms", time);
    }

    /**
     * TODO: (TODO-8/Maybe) Load on contact
     */
    private void loadWorlds() {
        final TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
        assetManager.setLoader(TiledMap.class, mapLoader);

        assetManager.load(TUTORIAL_WORLD, TiledMap.class, new TmxMapLoader.Parameters());
        assetManager.load(WRYNN_BASEMENT, TiledMap.class, new TmxMapLoader.Parameters());
        assetManager.load(WRYNN_OFFICE, TiledMap.class, new TmxMapLoader.Parameters());
        assetManager.load(MYCELIA_WORLD, TiledMap.class, new TmxMapLoader.Parameters());
        assetManager.load(MYCELIA_SHOP, TiledMap.class, new TmxMapLoader.Parameters());
        assetManager.load(HOUSE_2, TiledMap.class, new TmxMapLoader.Parameters());
    }

    /**
     * Load fonts
     */
    private void loadFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font/Romulus.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.genMipMaps = true;

        large = of(generator, parameter, Math.ceil(640 * 0.06));
        medium = of(generator, parameter, Math.ceil(640 * 0.04));

        mediumMipMapped = ofMipMapped(generator, parameter, Math.ceil(640 * 0.04));

        small = ofMipMapped(generator, parameter, Math.ceil(640 * 0.033));
        smaller = of(generator, parameter, Math.ceil(640 * 0.02));
        generator.dispose();

        large.setUseIntegerPositions(false);
        medium.setUseIntegerPositions(false);
        small.setUseIntegerPositions(false);
        smaller.setUseIntegerPositions(false);

        // Damage numbers font, task: (TODO-3)
        generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font/Boxy-Bold.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) Math.ceil((Gdx.graphics.getWidth() * 0.025));
        boxy = generator.generateFont(parameter);
        boxy.setUseIntegerPositions(false);
        generator.dispose();
    }

    /**
     * Generate a font
     *
     * @param generator generator
     * @param parameter parameter
     * @param size      size
     * @return the new font
     */
    private BitmapFont of(FreeTypeFontGenerator generator, FreeTypeFontGenerator.FreeTypeFontParameter parameter, double size) {
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.magFilter = Texture.TextureFilter.Nearest;

        parameter.size = (int) size;
        return generator.generateFont(parameter);
    }

    /**
     * Generate a font mip-mapped
     *
     * @param generator generator
     * @param parameter parameter
     * @param size      size
     * @return the new font
     */
    private BitmapFont ofMipMapped(FreeTypeFontGenerator generator, FreeTypeFontGenerator.FreeTypeFontParameter parameter, double size) {
        parameter.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        parameter.size = (int) size;
        return generator.generateFont(parameter);
    }

    public BitmapFont getLarge() {
        return large;
    }

    public BitmapFont getMedium() {
        return medium;
    }

    public BitmapFont getSmall() {
        return small;
    }

    public BitmapFont getSmaller() {
        return smaller;
    }

    public BitmapFont getMediumMipMapped() {
        return mediumMipMapped;
    }

    public BitmapFont getBoxy() {
        return boxy;
    }

    /**
     * Get a texture
     *
     * @param resource the resource type
     * @param name     the name
     * @param index    the index
     * @return the resource
     */
    public TextureRegion get(Resource resource, String name, int index) {
        return normal.findRegion(name, index);
    }

    /**
     * Get a texture
     *
     * @param resource the resource type
     * @param name     the name
     * @return the resource
     */
    public TextureRegion get(Resource resource, String name) {
        return normal.findRegion(name);
    }

    /**
     * task: (TODO-19)
     */
    public TextureRegion get(String name) {
        return get(Resource.NORMAL, name);
    }

    /**
     * task: (TODO-19)
     */
    public TextureRegion get(String name, int index) {
        return get(Resource.NORMAL, name, index);
    }

    /**
     * task: (TODO-19)
     */
    public TextureAtlas getAtlasAssets() {
        return normal;
    }

    public TiledMap getWorldMap(String name) {
        return assetManager.get(name);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        small.dispose();
        medium.dispose();
        large.dispose();
    }

}
