package me.vrekt.oasis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

/**
 * Set of default assets for UI.
 */
public final class InterfaceAssets implements Disposable {

    /**
     * Default LibGDX skin.
     */
    private final Skin defaultUiSkin;

    public InterfaceAssets() {
        this.defaultUiSkin = new Skin(Gdx.files.internal("ui/skin/default/uiskin.json"), new TextureAtlas("ui/skin/default/uiskin.atlas"));
    }

    public Skin getDefaultUiSkin() {
        return defaultUiSkin;
    }

    @Override
    public void dispose() {
        defaultUiSkin.dispose();
    }
}
