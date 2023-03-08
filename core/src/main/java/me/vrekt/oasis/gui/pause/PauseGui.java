package me.vrekt.oasis.gui.pause;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiType;

/**
 * Pause gui
 */
public final class PauseGui extends Gui {

    private final VisTable rootTable;

    public PauseGui(GameGui gui, Asset asset) {
        super(gui, asset, "pause");

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(asset.get("pause")));
        TableUtils.setSpacingDefaults(rootTable);

        final VisTable primary = new VisTable();
        primary.padTop(-64);
        final TypingLabel resume = new TypingLabel("Resume", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        final TypingLabel settings = new TypingLabel("Settings", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        final TypingLabel exit = new TypingLabel("Exit", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        addDefaultActions(resume, () -> gui.getGame().getPlayer().getGameWorldIn().resume());
        addDefaultActions(settings, () -> {
            hide();
            gui.showGui(GuiType.SETTINGS);
        });
        addDefaultActions(exit, () -> Gdx.app.exit());

        primary.add(new TypingLabel("Game Paused", new Label.LabelStyle(gui.getLarge(), Color.WHITE))).top();
        primary.row().padTop(16);
        primary.add(resume);
        primary.row().padTop(6);
        primary.add(settings);
        primary.row().padTop(6);
        primary.add(exit);

        rootTable.add(primary).top();
        gui.createContainer(rootTable).fill().top();
    }

    private void addDefaultActions(TypingLabel label, Runnable action) {
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
