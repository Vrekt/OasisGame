package me.vrekt.oasis.ui.gui.notification;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.ui.gui.GameGui;
import me.vrekt.oasis.ui.world.Gui;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class QuestNotificationGui extends Gui {

    public static final int ID = 5;

    // tables to fade out, like notifications.
    private final Map<Table, Pair<Float, Long>> fadeTables = new ConcurrentHashMap<>();
    private final Table rootTable = new Table();

    public QuestNotificationGui(GameGui gui) {
        super(gui);
        isShowing = true;

        // align notification content to the bottom of the screen above the players inventory
        final Container<Table> container = gui.createContainer(rootTable);
        container.top().padTop(96f);
    }

    /**
     * Show a quest has started tracking
     */
    public void showQuestTracking() {
        final Table table = new Table();
        table.addAction(Actions.fadeIn(.6f, Interpolation.linear));
        table.getColor().a = 0f;
        table.center();

        table.setBackground(new TextureRegionDrawable(gui.getAsset("quest_chapter")));
        table.add(new Label("New quest added!", skin, "small", Color.WHITE))
                .padLeft(16f)
                .padRight(16f);

        // stay on the screen for 1.5 seconds.
        this.fadeTables.put(table, Pair.of(1.5f, System.currentTimeMillis()));
        this.rootTable.add(table);
    }

    @Override
    public void update() {
        final long now = System.currentTimeMillis();
        for (Table table : fadeTables.keySet()) {
            final Pair<Float, Long> time = fadeTables.get(table);
            final boolean isFaded = !table.getActions().isEmpty()
                    && ((AlphaAction) table.getActions().get(0)).isComplete();

            if (now - time.getValue() >= (time.getLeft() * 1000) && !isFaded) {
                // fade this table out.
                table.addAction(Actions.fadeOut(.6f, Interpolation.linear));
            } else if (isFaded) {
                this.rootTable.removeActor(table);
                this.fadeTables.remove(table);
            }
        }
    }

}
