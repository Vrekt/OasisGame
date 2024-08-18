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
import me.vrekt.oasis.gui.input.Cursor;
import me.vrekt.oasis.gui.guis.inventory.style.ItemSlotStyles;
import me.vrekt.oasis.utility.EmptyDrawable;

/**
 * Various UI styles and configurations
 */
public final class Styles {

    private static NinePatch themePatchPadded;
    private static NinePatchDrawable theme, themePadded;
    private static NinePatchDrawable border;

    private static Tooltip.TooltipStyle tooltipStyle;
    private static VisTextField.VisTextFieldStyle fieldStyle;
    private static VisTextField.VisTextFieldStyle transparentFieldStyle;

    private static Label.LabelStyle smallWhite, mediumWhite, largeWhite,
            smallBlack, mediumBlack, largeBlack, smallerWhite, mediumWhiteMipMapped;

    private static VisImageTextButton.VisImageTextButtonStyle imageTextButtonStyle;
    private static Texture defaultCursorTexture;

    private static VisCheckBox.VisCheckBoxStyle checkBoxStyle;

    private static TextureRegionDrawable weaponDamageIcon, weaponRangeIcon, weaponCriticalIcon;
    private static TextureRegionDrawable satisfactionAttributeIcon, healingAttributeIcon;

    private static ItemSlotStyles slotStyles;

    public static void load(Asset asset) {
        final NinePatch themePatch = new NinePatch(asset.get("theme"), 4, 4, 4, 4);
        themePatchPadded = new NinePatch(asset.get("theme"), 6, 6, 6, 6);

        theme = new NinePatchDrawable(themePatch);
        themePadded = new NinePatchDrawable(themePatchPadded);
        tooltipStyle = new Tooltip.TooltipStyle(theme);

        border = new NinePatchDrawable(new NinePatch(asset.get("border"), 6, 6, 6, 6));

        defaultCursorTexture = new Texture(Gdx.files.internal(Cursor.DEFAULT.getFile()));

        fieldStyle = new VisTextField.VisTextFieldStyle(asset.getMedium(), Color.WHITE, new TextureRegionDrawable(defaultCursorTexture), theme, theme);
        transparentFieldStyle = new VisTextField.VisTextFieldStyle(fieldStyle);
        transparentFieldStyle.background = new EmptyDrawable();

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
        mediumWhiteMipMapped = new Label.LabelStyle(asset.getMediumMipMapped(), Color.WHITE);

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

        slotStyles = new ItemSlotStyles(asset);
    }

    /**
     * @return the padded theme patch.
     */
    public static NinePatch paddedTheme() {
        return themePatchPadded;
    }

    public static NinePatchDrawable getTheme() {
        return theme;
    }

    public static NinePatchDrawable getThemePadded() {
        return themePadded;
    }

    public static NinePatchDrawable border() {
        return border;
    }

    public static Texture getDefaultCursorTexture() {
        return defaultCursorTexture;
    }

    public static Tooltip.TooltipStyle getTooltipStyle() {
        return tooltipStyle;
    }

    public static VisTextField.VisTextFieldStyle getFieldStyle() {
        return fieldStyle;
    }

    public static VisTextField.VisTextFieldStyle getTransparentFieldStyle() {
        return transparentFieldStyle;
    }

    public static VisCheckBox.VisCheckBoxStyle getCheckBoxStyle() {
        return checkBoxStyle;
    }

    public static Label.LabelStyle getSmallWhite() {
        return smallWhite;
    }

    public static Label.LabelStyle getSmallerWhite() {
        return smallerWhite;
    }

    public static Label.LabelStyle getMediumWhite() {
        return mediumWhite;
    }

    /**
     * @return less blocky, weird looking font for dialogs, slightly blurry.
     */
    public static Label.LabelStyle getMediumWhiteMipMapped() {
        return mediumWhiteMipMapped;
    }

    public static Label.LabelStyle getLargeWhite() {
        return largeWhite;
    }

    public static Label.LabelStyle getSmallBlack() {
        return smallBlack;
    }

    public static Label.LabelStyle getMediumBlack() {
        return mediumBlack;
    }

    public static Label.LabelStyle getLargeBlack() {
        return largeBlack;
    }

    public static VisImageTextButton.VisImageTextButtonStyle getImageTextButtonStyle() {
        return imageTextButtonStyle;
    }

    public static TextureRegionDrawable getWeaponRangeIcon() {
        return weaponRangeIcon;
    }

    public static TextureRegionDrawable getWeaponDamageIcon() {
        return weaponDamageIcon;
    }

    public static TextureRegionDrawable getWeaponCriticalIcon() {
        return weaponCriticalIcon;
    }

    public static TextureRegionDrawable getSatisfactionAttributeIcon() {
        return satisfactionAttributeIcon;
    }

    public static TextureRegionDrawable getHealingAttributeIcon() {
        return healingAttributeIcon;
    }

    /**
     * @return slot styles
     */
    public static ItemSlotStyles slots() {
        return slotStyles;
    }
}
