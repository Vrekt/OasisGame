package me.vrekt.oasis.gui.quest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.questing.Quest;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GUI for the players questing manager
 */
public final class QuestingGui extends Gui {

    private final Stage stage = new Stage();

    private final VisTable table;
    private final PlayerQuestManager manager;

    // the title label for the quest name
    private TypingLabel questTitleLabel, itemsRequiredLabel, rewardsLabel;
    // the active table that shows quest objectives
    private final VisTable activeQuestObjectives, activeQuestItems, activeQuestRewards;

    public QuestingGui(GameGui gui, Asset asset) {
        super(gui, asset, "Quest Log");
        manager = gui.getGame().getPlayer().getQuestManager();

        table = new VisTable(true);
        table.setFillParent(true);
        table.add(this).expand().fill();

        activeQuestObjectives = new VisTable(true);
        activeQuestItems = new VisTable(true);
        activeQuestRewards = new VisTable(true);

        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();
        populateQuests();
        table.pack();

        stage.addActor(table);
        gui.getMultiplexer().addProcessor(stage);
    }

    @Override
    public void update() {
        stage.act();
        stage.draw();
    }

    private void populateQuests() {

        final VisTable table = new VisTable(true);
        table.top().padTop(16);
        final VisTable other = new VisTable(true);
        other.top().left().padLeft(25);

        // info: Populate quest names on left side
        final Label.LabelStyle defaultFontStyle = new Label.LabelStyle(gui.getMedium(), Color.WHITE);
        questTitleLabel = new TypingLabel("", defaultFontStyle);

        itemsRequiredLabel = new TypingLabel("Items Required", defaultFontStyle);
        rewardsLabel = new TypingLabel("[GREEN]Rewards", defaultFontStyle);
        itemsRequiredLabel.setVisible(false);
        rewardsLabel.setVisible(false);

        for (Quest quest : manager.getActiveQuests().values()) {
            final Label.LabelStyle style = new Label.LabelStyle(gui.getMedium(), Color.WHITE);
            VisLabel label = new VisLabel(quest.getName(), style);
            table.add(label).top().left();
            table.row();

            // a map of all labels for quest objectives.
            final LinkedList<TypingLabel> labels = new LinkedList<>();
            final AtomicInteger objectiveNumber = new AtomicInteger(1);

            // populate objectives with the correct typing labels.
            quest.getObjectives().forEach((objective, status) -> {
                final TypingLabel typingLabel = new TypingLabel(objective, defaultFontStyle);
                if (status) {
                    // objective is complete, so strikethrough the text instead.
                    typingLabel.setText("[GRAY][~]" + objectiveNumber.getAndIncrement() + ". " + objective);
                } else {
                    typingLabel.setText("[YELLOW]" + objectiveNumber.getAndIncrement() + ". " + objective + "[RED] (!)");
                }

                labels.add(typingLabel);
            });

            // add click listener, remove previous typing labels
            label.addListener(getQuestNameClickListener(quest, style, labels));
        }

        populateQuestInformation(other);

        questTitleLabel.setWrap(true);
        questTitleLabel.setWidth(275);
        other.add(questTitleLabel).width(275).top().left();
        other.row().padTop(16);
        other.add(activeQuestObjectives);
        other.row().padTop(16);
        other.add(itemsRequiredLabel).left();
        other.row();
        other.add(activeQuestItems).padTop(4).left();
        other.row().padTop(16);
        other.add(rewardsLabel).left();
        other.row();
        other.add(activeQuestRewards).padTop(4).left();

        VisSplitPane splitPane = new VisSplitPane(table, other, false);
        splitPane.setSplitAmount(0.3f);
        add(splitPane).fill().expand();
    }

    private ClickListener getQuestNameClickListener(Quest quest,
                                                    Label.LabelStyle style,
                                                    LinkedList<TypingLabel> labels) {
        return new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                style.fontColor = Color.GRAY;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                style.fontColor = Color.WHITE;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                questTitleLabel.setText(quest.getDescription());
                itemsRequiredLabel.restart();
                itemsRequiredLabel.setVisible(true);
                rewardsLabel.restart();
                rewardsLabel.setVisible(true);

                activeQuestObjectives.clear();
                activeQuestItems.clear();
                activeQuestRewards.clear();

                for (TypingLabel label : labels) {
                    label.restart();

                    activeQuestObjectives.add(label).left();
                    activeQuestObjectives.row();
                }

                for (ItemRegistry.Item item : quest.getItemsRequired()) {

                    System.err.println(item);
                    final VisImageButton image = new VisImageButton(new TextureRegionDrawable(asset.get(item.texture)));
                    new Tooltip.Builder(item.itemName).target(image).build().setAppearDelayTime(0.1f);
                    activeQuestItems.add(image).size(48, 48);
                }

                for (ItemRegistry.Item item : quest.getRewards()) {
                    System.err.println(item);
                    final VisImageButton image = new VisImageButton(new TextureRegionDrawable(asset.get(item.texture)));
                    new Tooltip.Builder(item.itemName).target(image).build().setAppearDelayTime(0.1f);
                    activeQuestRewards.add(image).size(48, 48);
                }

            }
        };
    }

    private void populateQuestInformation(Table table) {

    }

    @Override
    public void showGui() {
        super.showGui();
        super.gui.hideGui(GuiType.HUD);
        table.setVisible(true);
    }

    @Override
    public void hideGui() {
        super.hideGui();
        super.gui.showGui(GuiType.HUD);
        table.setVisible(false);
    }
}
