package me.vrekt.oasis.asset.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public final class Asset implements Disposable {

    public static final String TUTORIAL_WORLD = "world/worlds/OasisTutorialWorld.tmx";
    public static final String ASSETS = "Assets.atlas";

    private final AssetManager assetManager = new AssetManager();

    private Skin defaultLibgdxSkin;
    private BitmapFont small, medium, large;

    private TextureAtlas atlasAssets;

    public void load() {
        loadSkins();

        final TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
        assetManager.setLoader(TiledMap.class, mapLoader);
        assetManager.load(ASSETS, TextureAtlas.class);

        loadFonts();
        defaultLibgdxSkin.add("large", large);
        defaultLibgdxSkin.add("medium", medium);
        defaultLibgdxSkin.add("small", small);

        loadWorlds();

        assetManager.finishLoading();
        this.atlasAssets = assetManager.get(ASSETS);
    }

    private void loadWorlds() {
        assetManager.load(TUTORIAL_WORLD, TiledMap.class, new TmxMapLoader.Parameters());
    }

    private void loadSkins() {
        defaultLibgdxSkin = new Skin(Gdx.files.internal("ui/styles/gdx/uiskin.json"), new TextureAtlas("ui/styles/gdx/uiskin.atlas"));
    }

    /**
     * Load fonts
     */
    private void loadFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font/Romulus.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.06);
        large = generator.generateFont(parameter);

        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.04);
        medium = generator.generateFont(parameter);

        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.02);
        small = generator.generateFont(parameter);
        generator.dispose();
    }

    public Skin getDefaultLibgdxSkin() {
        return defaultLibgdxSkin;
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

    public TextureRegion get(String name) {
        return atlasAssets.findRegion(name);
    }

    public TextureRegion get(String name, int index) {
        return atlasAssets.findRegion(name, index);
    }

    public TextureAtlas getAtlasAssets() {
        return atlasAssets;
    }

    public <T> T getType(String name) {
        return assetManager.get(name);
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
