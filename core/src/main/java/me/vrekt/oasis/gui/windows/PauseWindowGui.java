package me.vrekt.oasis.gui.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;

public final class PauseWindowGui extends Gui {

    public PauseWindowGui(GuiManager guiManager) {
        super(GuiType.PAUSE, guiManager);

        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("pause")));
        hideWhenVisible.add(GuiType.HUD);

        final VisTable table = new VisTable();
        table.top();

        final VisImage logoImage = new VisImage(guiManager.getGame().getLogoTexture());

        final VisImageTextButton resume = new VisImageTextButton("Resume", guiManager.getStyle().getImageTextButtonStyle());
        final VisImageTextButton settings = new VisImageTextButton("Settings", guiManager.getStyle().getImageTextButtonStyle());
        final VisImageTextButton saveGame = new VisImageTextButton("Save Game", guiManager.getStyle().getImageTextButtonStyle());
        final VisImageTextButton exit = new VisImageTextButton("Exit Game", guiManager.getStyle().getImageTextButtonStyle());

        addHoverComponents(resume, Color.LIGHT_GRAY, Color.WHITE, this::handleResumeGameComponentAction);
        addHoverComponents(resume, Color.LIGHT_GRAY, Color.WHITE, this::handleResumeGameComponentAction);
        addHoverComponents(settings, Color.LIGHT_GRAY, Color.WHITE, this::handleSettingsComponentAction);
        addHoverComponents(saveGame, Color.LIGHT_GRAY, Color.WHITE, this::handleSaveGameComponentAction);
        // TODO: Exit and save, Exit without saving
        addHoverComponents(exit, Color.LIGHT_GRAY, Color.WHITE, () -> Gdx.app.exit());

        table.add(logoImage);
        table.row();

        final TypingLabel header = new TypingLabel("Game Paused", guiManager.getStyle().getLargeWhite());
        table.add(header);
        table.row().padTop(16);
        table.add(resume).fillX();
        table.row().padTop(6);
        table.add(settings).fillX();
        table.row().padTop(6);
        table.add(saveGame).fillX();
        table.row().padTop(6);
        table.add(exit).fillX();

        rootTable.add(table);
        guiManager.addGui(rootTable);
    }

    private void handleResumeGameComponentAction() {
        hide();
        GameManager.resumeGame();
    }

    private void handleSettingsComponentAction() {
        guiManager.showChildGui(this, GuiType.SETTINGS);
    }

    private void handleSaveGameComponentAction() {
        guiManager.showChildGui(this, GuiType.SAVE_GAME);
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
