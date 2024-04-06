package me.vrekt.oasis.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisTextField;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;

public class Styles {

    private final NinePatchDrawable theme;
    private final Tooltip.TooltipStyle tooltipStyle;
    private final VisTextField.VisTextFieldStyle fieldStyle;

    private final Label.LabelStyle smallWhite, mediumWhite, largeWhite,
            smallBlack, mediumBlack, largeBlack, smallerWhite;

    public Styles(Asset asset) {
        final NinePatch themePatch = new NinePatch(asset.get("theme"), 4, 4, 4, 4);
        theme = new NinePatchDrawable(themePatch);
        tooltipStyle = new Tooltip.TooltipStyle(theme);
        fieldStyle = new VisTextField.VisTextFieldStyle(asset.getMedium(), Color.WHITE, new TextureRegionDrawable(GameManager.getCursor()), theme, theme);

        smallWhite = new Label.LabelStyle(asset.getSmall(), Color.WHITE);
        mediumWhite = new Label.LabelStyle(asset.getMedium(), Color.WHITE);
        largeWhite = new Label.LabelStyle(asset.getLarge(), Color.WHITE);
        smallBlack = new Label.LabelStyle(asset.getSmall(), Color.BLACK);
        mediumBlack = new Label.LabelStyle(asset.getMedium(), Color.BLACK);
        largeBlack = new Label.LabelStyle(asset.getLarge(), Color.BLACK);
        smallerWhite = new Label.LabelStyle(asset.getSmaller(), Color.WHITE);
    }

    public NinePatchDrawable getTheme() {
        return theme;
    }

    public Tooltip.TooltipStyle getTooltipStyle() {
        return tooltipStyle;
    }

    public VisTextField.VisTextFieldStyle getFieldStyle() {
        return fieldStyle;
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
}
