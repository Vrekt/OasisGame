package me.vrekt.oasis.ui.gui.notification;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.quest.type.QuestRewards;
import me.vrekt.oasis.ui.gui.GameGui;
import me.vrekt.oasis.ui.world.Gui;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class QuestNotificationGui extends Gui {

    public static final int ID = 5;

    private final Map<Table, Pair<Float, Long>> notifications = new ConcurrentHashMap<>();
    private final Table rootTable = new Table();

    public QuestNotificationGui(GameGui gui) {
        super(gui);
        isShowing = true;

        // align notification content to the bottom of the screen above the players inventory
        final Container<Table> container = gui.createContainer(rootTable);
        container.top().padTop(96f);
    }

    public void showQuestReward(String name, Map<QuestRewards, Integer> rewards) {
        final Table table = new Table();

        table.setBackground(new TextureRegionDrawable(gui.getAsset().getAssets().findRegion("interaction")));
        table.getColor().a = 0f;
        table.addAction(Actions.fadeIn(.6f, Interpolation.linear));

        table.add(new Label("Quest Complete: " + name, skin, "small", Color.BLACK))
                .center()
                .padLeft(16f)
                .padRight(16f)
                .padTop(6f);

        table.row();

        final Table rewardsTable = new Table();
        rewardsTable.pad(12f);

        rewards.forEach((reward, amount) -> {
            rewardsTable.add(new Image(asset.getAssets().findRegion(reward.getTexture())))
                    .size(32, 32)
                    .padRight(6f);
            rewardsTable.add(new Label("x" + amount, skin, "small", Color.BLACK))
                    .padRight(12f);
        });

        table.add(rewardsTable);
        this.rootTable.add(table);
    }

}
