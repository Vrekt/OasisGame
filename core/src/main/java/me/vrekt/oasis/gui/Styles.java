package me.vrekt.oasis.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.cursor.Cursor;

/**
 * Various UI styles and configurations
 */
public class Styles {

    private final NinePatchDrawable theme, themePadded;
    private final Tooltip.TooltipStyle tooltipStyle;
    private final VisTextField.VisTextFieldStyle fieldStyle;

    private final Label.LabelStyle smallWhite, mediumWhite, largeWhite,
            smallBlack, mediumBlack, largeBlack, smallerWhite;

    private final VisImageTextButton.VisImageTextButtonStyle imageTextButtonStyle;
    private final Texture defaultCursorTexture;

    private final VisCheckBox.VisCheckBoxStyle checkBoxStyle;
    private final TextureRegionDrawable themeDownSelected;

    private final TextureRegionDrawable weaponDamageIcon, weaponRangeIcon, weaponCriticalIcon;
    private final TextureRegionDrawable satisfactionAttributeIcon, healingAttributeIcon;

    public Styles(Asset asset) {
        final NinePatch themePatch = new NinePatch(asset.get("theme"), 4, 4, 4, 4);
        final NinePatch themePatchPadded = new NinePatch(asset.get("theme"), 6, 6, 6, 6);

        theme = new NinePatchDrawable(themePatch);
        themePadded = new NinePatchDrawable(themePatchPadded);
        tooltipStyle = new Tooltip.TooltipStyle(theme);

        defaultCursorTexture = new Texture(Gdx.files.internal(Cursor.DEFAULT.getFile()));
        fieldStyle = new VisTextField.VisTextFieldStyle(asset.getMedium(), Color.WHITE, new TextureRegionDrawable(defaultCursorTexture), theme, theme);

        final VisCheckBox.VisCheckBoxStyle providedCheckBoxStyle = VisUI.getSkin().get(VisCheckBox.VisCheckBoxStyle.class);
        checkBoxStyle = new VisCheckBox.VisCheckBoxStyle(providedCheckBoxStyle);

        final NinePatch tickedPatch = new NinePatch(asset.get("ticked"), 4, 4, 4, 4);
        final NinePatchDrawable tickedDrawable = new NinePatchDrawable(tickedPatch);

        checkBoxStyle.checkBackground = theme;
        checkBoxStyle.tick = tickedDrawable;
        checkBoxStyle.font = asset.getMedium();
        checkBoxStyle.fontColor = Color.WHITE;
        checkBoxStyle.focusBorder = null;
        checkBoxStyle.over = null;
        checkBoxStyle.down = null;
        checkBoxStyle.checkBackgroundDown = theme;
        checkBoxStyle.checkBackgroundOver = theme;

        smallWhite = new Label.LabelStyle(asset.getSmall(), Color.WHITE);
        mediumWhite = new Label.LabelStyle(asset.getMedium(), Color.WHITE);
        largeWhite = new Label.LabelStyle(asset.getLarge(), Color.WHITE);
        smallBlack = new Label.LabelStyle(asset.getSmall(), Color.BLACK);
        mediumBlack = new Label.LabelStyle(asset.getMedium(), Color.BLACK);
        largeBlack = new Label.LabelStyle(asset.getLarge(), Color.BLACK);
        smallerWhite = new Label.LabelStyle(asset.getSmaller(), Color.WHITE);

        themeDownSelected = new TextureRegionDrawable(asset.get("theme_down"));

        final NinePatch themeOverPatch = new NinePatch(asset.get("theme_over"), 6, 6, 6, 6);
        final NinePatch themeDownPatch = new NinePatch(asset.get("theme_down"), 6, 6, 6, 6);

        final NinePatchDrawable themeOver = new NinePatchDrawable(themeOverPatch);
        final NinePatchDrawable themeDown = new NinePatchDrawable(themeDownPatch);

        final VisImageTextButton.VisImageTextButtonStyle provided = VisUI.getSkin().get(VisImageTextButton.VisImageTextButtonStyle.class);
        imageTextButtonStyle = new VisImageTextButton.VisImageTextButtonStyle(provided);

        imageTextButtonStyle.focusBorder = null;
        imageTextButtonStyle.down = themeDown;
        imageTextButtonStyle.over = themeOver;
        imageTextButtonStyle.up = themePadded;
        imageTextButtonStyle.font = asset.getMedium();

        weaponRangeIcon = new TextureRegionDrawable(asset.get("weapon_range_icon"));
        weaponDamageIcon = new TextureRegionDrawable(asset.get("weapon_damage_icon"));
        weaponCriticalIcon = new TextureRegionDrawable(asset.get("weapon_crit_icon"));

        satisfactionAttributeIcon = new TextureRegionDrawable(asset.get("satisfaction_attribute3"));
        healingAttributeIcon = new TextureRegionDrawable(asset.get("healing_attribute2"));
    }

    public NinePatchDrawable getTheme() {
        return theme;
    }

    public NinePatchDrawable getThemePadded() {
        return themePadded;
    }

    public Texture getDefaultCursorTexture() {
        return defaultCursorTexture;
    }

    public Tooltip.TooltipStyle getTooltipStyle() {
        return tooltipStyle;
    }

    public VisTextField.VisTextFieldStyle getFieldStyle() {
        return fieldStyle;
    }

    public VisCheckBox.VisCheckBoxStyle getCheckBoxStyle() {
        return checkBoxStyle;
    }

    public Label.LabelStyle getSmallWhite() {
        return smallWhite;
    }

    public Label.LabelStyle getSmallerWhite() {
        return smallerWhite;
    }

    public Label.LabelStyle getMediumWhite() {
        return mediumWhite;
    }

    public Label.LabelStyle getLargeWhite() {
        return largeWhite;
    }

    public Label.LabelStyle getSmallBlack() {
        return smallBlack;
    }

    public Label.LabelStyle getMediumBlack() {
        return mediumBlack;
    }

    public Label.LabelStyle getLargeBlack() {
        return largeBlack;
    }

    public TextureRegionDrawable getThemeDownSelected() {
        return themeDownSelected;
    }

    public VisImageTextButton.VisImageTextButtonStyle getImageTextButtonStyle() {
        return imageTextButtonStyle;
    }

    public TextureRegionDrawable getWeaponRangeIcon() {
        return weaponRangeIcon;
    }

    public TextureRegionDrawable getWeaponDamageIcon() {
        return weaponDamageIcon;
    }

    public TextureRegionDrawable getWeaponCriticalIcon() {
        return weaponCriticalIcon;
    }

    public TextureRegionDrawable getSatisfactionAttributeIcon() {
        return satisfactionAttributeIcon;
    }

    public TextureRegionDrawable getHealingAttributeIcon() {
        return healingAttributeIcon;
    }
}
