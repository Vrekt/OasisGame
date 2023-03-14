package me.vrekt.oasis.gui.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
import me.vrekt.oasis.questing.QuestObjective;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * GUI for the players questing manager
 */
public final class QuestingGui extends Gui {

    private final VisTable table;
    private final PlayerQuestManager manager;

    // the title label for the quest name
    private TypingLabel questTitleLabel, itemsRequiredLabel, rewardsLabel;
    // the active table that shows quest objectives
    private final VisTable activeQuestObjectives, activeQuestItems, activeQuestRewards;

    // keeps track of all labels required for a certain quest.
    private final Map<Quest, LinkedList<TypingLabel>> respectiveObjectiveLabels = new HashMap<>();
    private Quest currentSelectedQuest;

    public QuestingGui(GameGui gui, Asset asset) {
        super(gui, asset, "Quest Log");
        manager = gui.getGame().getPlayer().getQuestManager();

        table = new VisTable(true);
        table.setFillParent(true);
        table.setVisible(false);
        table.setBackground(new TextureRegionDrawable(asset.get("quest_background")));

        activeQuestObjectives = new VisTable(true);
        activeQuestItems = new VisTable(true);
        activeQuestRewards = new VisTable(true);

        TableUtils.setSpacingDefaults(table);
        populateQuests();
        table.pack();
        gui.createContainer(table).fill();
    }

    /**
     * Populate all quest information into the {@link  VisSplitPane}
     */
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
            int objectiveNumber = 1;

            // populate objectives with the correct typing labels.
            for (QuestObjective objective : quest.getObjectives()) {
                final TypingLabel typingLabel = new TypingLabel(objective.getDescription(), defaultFontStyle);
                if (objective.isCompleted()) {
                    // objective is complete, so strikethrough the text instead.
                    typingLabel.setText("[GRAY][~]" + objectiveNumber + ". " + objective.getDescription());
                } else {
                    typingLabel.setText("[YELLOW]" + objectiveNumber + ". " + objective.getDescription() + "[RED] (!)");
                }

                // info: indicates if this label should be shown or not
                typingLabel.setVariable("unlocked", (objective.isUnlocked() || objective.isCompleted()) ? "true" : "false");
                objectiveNumber++;
                labels.add(typingLabel);
            }
            // add click listener, remove previous typing labels
            // info -1 because index starts at 0 but quest starts at 1.
            label.addListener(registerNameClickListener(quest, style, labels));
            respectiveObjectiveLabels.put(quest, labels);
        }

        questTitleLabel.setWrap(true);
        questTitleLabel.setWidth(275);
        other.add(questTitleLabel).width(275).top().left();
        other.row().padTop(16);
        other.add(activeQuestObjectives).left();
        other.row().padTop(16);
        other.add(itemsRequiredLabel).left();
        other.row();
        other.add(activeQuestItems).padTop(6).left();
        other.row().padTop(16);
        other.add(rewardsLabel).left();
        other.row();
        other.add(activeQuestRewards).padTop(6).left();

        VisSplitPane splitPane = new VisSplitPane(table, other, false);
        splitPane.setSplitAmount(0.3f);
        splitPane.setMinSplitAmount(0.29f);
        splitPane.setMaxSplitAmount(0.4f);
        this.table.add(splitPane).fill().expand();
    }

    /**
     * Register a new {@link  ClickListener} for when the player clicks on the quest name label.
     *
     * @param quest  the quest clicked on
     * @param style  the general style
     * @param labels a list of labels for that quest
     * @return the new {@link ClickListener}
     */
    private ClickListener registerNameClickListener(Quest quest,
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
                updateQuestObjectivesUi(quest, labels);
            }
        };
    }

    /**
     * Update the quest objectives UI.
     *
     * @param quest  quest
     * @param labels the labels for that quest
     */
    private void updateQuestObjectivesUi(Quest quest, LinkedList<TypingLabel> labels) {
        this.currentSelectedQuest = quest;
        questTitleLabel.setText(quest.getDescription());
        itemsRequiredLabel.restart();
        itemsRequiredLabel.setVisible(true);
        rewardsLabel.restart();
        rewardsLabel.setVisible(true);

        activeQuestObjectives.clear();
        activeQuestItems.clear();
        activeQuestRewards.clear();

        for (int i = 0; i < labels.size(); i++) {
            final TypingLabel label = labels.get(i);
            final QuestObjective objective = quest.getObjectives().get(i);
            label.restart();

            // update label text based on quest state.
            if (objective.isCompleted()) {
                // objective is complete, so strikethrough the text instead.
                label.setText("[GRAY][~]" + (i + 1) + ". " + objective.getDescription());
            } else {
                label.setText("[YELLOW]" + (i + 1) + ". " + objective.getDescription() + "[RED] (!)");
            }

            // info: check if this label is locked, uppercase UNLOCKED is required
            // info: but also check index objective if unlocked
            if (label.getVariables()
                    .get("UNLOCKED")
                    .equalsIgnoreCase("true")
                    || quest.getObjectives().get(i).isUnlocked()) {
                activeQuestObjectives.add(label).left();
                activeQuestObjectives.row();
            }
        }

        for (ItemRegistry.Item item : quest.getItemsRequired()) {
            final VisImageButton image = new VisImageButton(new TextureRegionDrawable(asset.get(item.texture)));
            new Tooltip.Builder(item.itemName).target(image).build().setAppearDelayTime(0.1f);
            activeQuestItems.add(image).size(48, 48);
        }

        for (ItemRegistry.Item item : quest.getRewards()) {
            final VisImageButton image = new VisImageButton(new TextureRegionDrawable(asset.get(item.texture)));
            new Tooltip.Builder(item.itemName).target(image).build().setAppearDelayTime(0.1f);
            activeQuestRewards.add(image).size(48, 48);
        }

    }

    @Override
    public void show() {
        super.show();
        super.gui.hideGui(GuiType.HUD);

        // update quest objectives once this window is re-opened.
        if (currentSelectedQuest != null) {
            updateQuestObjectivesUi(currentSelectedQuest, respectiveObjectiveLabels.get(currentSelectedQuest));
        }
        table.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        super.gui.showGui(GuiType.HUD);
        table.setVisible(false);
    }
}
