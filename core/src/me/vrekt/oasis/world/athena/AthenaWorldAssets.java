package me.vrekt.oasis.world.athena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import me.vrekt.oasis.world.asset.WorldAsset;

/**
 * Handles all assets required for {@link AthenaWorld}
 */
public final class AthenaWorldAssets extends WorldAsset {

    /**
     * Font for the world
     */
    BitmapFont fontBig;
    BitmapFont fontSmall;

    @Override
    protected void loadAssets0() {
        assetManager.finishLoading();
        loadFont();
    }

    /**
     * Acquire font
     */
    private void loadFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font/sdss.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // big text for context or titles
        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.04);
        fontBig = generator.generateFont(parameter);

        // generate a smaller text for sub title information
        parameter.size = (int) Math.ceil(Gdx.graphics.getWidth() * 0.02);
        fontSmall = generator.generateFont(parameter);

        generator.dispose();
    }



}
