package me.vrekt.oasis.gui.guis.sign;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import org.apache.commons.lang3.StringUtils;

public final class ReadableSignGui extends Gui {

    private final TypingLabel signTextLabel;

    public ReadableSignGui(GuiManager guiManager) {
        super(GuiType.SIGN, guiManager);

        hideWhenVisible.add(GuiType.HUD);

        rootTable.setVisible(false);
        rootTable.setFillParent(true);
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("pause")));

        final VisLabel label = new VisLabel("Sign", guiManager.getStyle().getMediumBlack());
        rootTable.add(label);
        rootTable.row();

        final VisTable table = new VisTable();
        final NinePatch patch = new NinePatch(guiManager.getAsset().get("sign_scale"), 2, 2, 2, 2);
        table.setBackground(new NinePatchDrawable(patch));

        signTextLabel = new TypingLabel(StringUtils.EMPTY, guiManager.getStyle().getLargeBlack());
        signTextLabel.setWrap(true);
        signTextLabel.setColor(Color.BLACK);

        table.add(signTextLabel)
                .width(448)
                .padTop(-96)
                .padLeft(12);

        rootTable.add(table).center();

        guiManager.addGui(rootTable);
    }

    public void setSignText(String text) {
        signTextLabel.setText(text);
        signTextLabel.restart();
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
