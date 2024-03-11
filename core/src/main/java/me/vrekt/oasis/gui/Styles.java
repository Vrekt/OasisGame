package me.vrekt.oasis.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
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

    public Styles(Asset asset) {
        final NinePatch themePatch = new NinePatch(asset.get("theme"), 4, 4, 4, 4);
        theme = new NinePatchDrawable(themePatch);
        tooltipStyle = new Tooltip.TooltipStyle(theme);
        fieldStyle = new VisTextField.VisTextFieldStyle(asset.getMedium(), Color.BLACK, new TextureRegionDrawable(GameManager.getCursor()), theme, theme);
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
}
