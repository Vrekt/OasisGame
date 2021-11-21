package me.vrekt.oasis.gui.notification;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Notification/hint gui
 */
public final class NotificationGui extends Gui {

    public static final int ID = 1;

    private final Map<Table, Pair<Float, Long>> notifications = new ConcurrentHashMap<>();
    private final Table rootTable = new Table();

    public NotificationGui(GameGui gui) {
        super(gui);
        isShowing = true;

        // align notification content to the bottom of the screen above the players inventory
        final Container<Table> container = gui.createContainer(rootTable);
        container.bottom().padBottom(96f);
    }

    /**
     * Show a game notification.
     *
     * @param text     the text to show
     * @param duration notification duration
     */
    public void sendPlayerNotification(String text, float duration) {
        final Table table = new Table();
        final Stack stack = new Stack();
        table.add(stack);

        table.getColor().a = 0f;
        table.addAction(Actions.fadeIn(.6f, Interpolation.linear));

        stack.add(new Image(gui.getAsset().getAssets().findRegion("interaction")));
        stack.add(new Label("  " + text + "  ", skin, "small", Color.BLACK));
        this.rootTable.add(table);

        // paired by duration and time of post
        this.notifications.put(table, Pair.of(duration, System.currentTimeMillis()));
    }

    @Override
    public void update() {
        final long now = System.currentTimeMillis();
        for (Table content : notifications.keySet()) {
            final Pair<Float, Long> pair = notifications.get(content);
            final long deltaTime = now - pair.getValue();
            if (deltaTime >= (pair.getLeft() * 1000)) {
                content.addAction(Actions.fadeOut(.6f));
            } else if (content.getActions().size > 0
                    && ((AlphaAction) content.getActions().get(0)).isComplete()) {
                this.rootTable.removeActor(content);
                notifications.remove(content);
            }
        }
    }
}
