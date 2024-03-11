package me.vrekt.oasis.gui.hints;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;

/**
 * Hints or information related to the game
 */
public final class HintGui extends Gui {

    private final Table rootTable;
    private final TextureRegionDrawable start, middle, end;

    private final TypingLabel hintLabel;

    public HintGui(GameGui gui, Asset asset) {
        super(gui, asset);

        rootTable = new Table();
        rootTable.setVisible(false);
        rootTable.left();

        gui.createContainer(rootTable).top();

        this.start = new TextureRegionDrawable(asset.get("dialog_picture"));
        this.middle = new TextureRegionDrawable(asset.get("dialog_option"));
        this.end = new TextureRegionDrawable(asset.get("dialog_end_picture"));

        final Table table = new Table();
        table.setBackground(gui.getStyles().getTheme());

        table.add(this.hintLabel = new TypingLabel("HintLabel", new Label.LabelStyle(asset.getMedium(), Color.WHITE)))
                .width(448)
                .padBottom(16)
                .padRight(8)
                .padLeft(8);
        this.hintLabel.setWrap(true);

        rootTable.add(table).padTop(2);
    }

    public void setHint() {
        hintLabel.setText("Testing Testing Testing");
        hintLabel.restart();
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
    }
}
