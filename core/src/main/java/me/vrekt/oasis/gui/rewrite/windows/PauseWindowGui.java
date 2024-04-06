package me.vrekt.oasis.gui.rewrite.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.rewrite.Gui;
import me.vrekt.oasis.gui.rewrite.GuiManager;

public final class PauseWindowGui extends Gui {

    public PauseWindowGui(GuiManager guiManager) {
        super(GuiType.PAUSE, guiManager);

        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("pause")));
        hideWhenVisible.add(GuiType.HUD);

        final VisTable table = new VisTable();
        final TypingLabel resume = new TypingLabel("Resume", guiManager.getStyle().getMediumWhite());
        final TypingLabel settings = new TypingLabel("Settings", guiManager.getStyle().getMediumWhite());
        final TypingLabel saveGame = new TypingLabel("Save Game", guiManager.getStyle().getMediumWhite());
        final TypingLabel exit = new TypingLabel("Exit Game", guiManager.getStyle().getMediumWhite());

        addActionsToComponent(resume, this::handleResumeGameComponentAction);
        addActionsToComponent(settings, this::handleSettingsComponentAction);
        addActionsToComponent(saveGame, this::handleSaveGameComponentAction);
        addActionsToComponent(exit, () -> Gdx.app.exit());

        final TypingLabel header = new TypingLabel("Game Paused", guiManager.getStyle().getLargeWhite());
        table.add(header);
        table.row().padTop(16);
        table.add(resume);
        table.row().padTop(6);
        table.add(settings);
        table.row().padTop(6);
        table.add(saveGame);
        table.row().padTop(6);
        table.add(exit);

        rootTable.add(table);
        guiManager.addGui(rootTable);
    }

    private void addActionsToComponent(TypingLabel label, Runnable action) {
        label.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                label.setColor(Color.LIGHT_GRAY);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                label.setColor(Color.WHITE);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
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
