package me.vrekt.oasis.gui.guis.quest;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Resource;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.item.utility.ItemDescriptor;
import me.vrekt.oasis.questing.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class QuestEntryGui extends Gui {

    private final TypingLabel completeness;
    private final VisLabel questNameLabel;
    private final VisLabel itemsRequiredLabel;
    private final VisLabel questRewardsLabel;
    private final VisTable objectivesTable;
    private final VisTable itemsRequiredTable;
    private final VisTable questRewardsTable;
    private final VisImage questDifficultyIcon;
    private final Tooltip questDifficultyTooltip;

    private final Map<Integer, TypingLabel> questObjectiveLabels = new HashMap<>();
    private final Array<QuestObjective> populatedObjectives = new Array<>();

    private Quest activeQuest;

    public QuestEntryGui(GuiManager guiManager) {
        super(GuiType.QUEST_ENTRY, guiManager);

        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        hasParent = true;
        parent = GuiType.QUEST;
        inheritParentBehaviour = true;
        disablePlayerMovement = true;

        final TextureRegionDrawable drawable = new TextureRegionDrawable(guiManager.getAsset().get(Resource.UI, "quest_entry", 2));
        rootTable.setBackground(drawable);

        questNameLabel = new VisLabel(StringUtils.EMPTY, Styles.getLargeBlack());
        completeness = new TypingLabel("(25% complete)", Styles.getMediumWhiteMipMapped());
        completeness.setColor(44 / 255f, 61 / 255f, 66 / 255f, 1.0f);

        questDifficultyIcon = new VisImage();
        questDifficultyTooltip = new Tooltip.Builder(StringUtils.EMPTY)
                .target(questDifficultyIcon)
                .style(Styles.getTooltipStyle())
                .build();
        ((VisLabel) questDifficultyTooltip.getContent()).setStyle(Styles.getSmallWhite());

        itemsRequiredLabel = new VisLabel("Items Required", Styles.getLargeBlack());
        questRewardsLabel = new VisLabel("Rewards", Styles.getLargeBlack());

        itemsRequiredLabel.setVisible(false);
        questRewardsLabel.setVisible(false);

        objectivesTable = new VisTable();
        itemsRequiredTable = new VisTable();
        questRewardsTable = new VisTable();
        itemsRequiredTable.left();
        questRewardsTable.left();

        final VisTable components = new VisTable();
        components.top().left().padLeft(116).padTop(40);

        components.add(questNameLabel).left();
        components.add(questDifficultyIcon).size(36, 36).padLeft(-24).padTop(8);
        components.row();
        components.add(completeness).left().padTop(-8);

        components.row();
        components.add(objectivesTable);
        components.row();
        components.add(itemsRequiredLabel).left().padTop(16);
        components.row();
        components.add(itemsRequiredTable).left();
        components.row();
        components.add(questRewardsLabel).left().padTop(16);
        components.row();
        components.add(questRewardsTable).left();

        rootTable.top().left();
        rootTable.add(components).top().left();

        guiManager.addGui(rootTable);
    }


    private void updateExistingQuestComponents(Quest quest) {
        for (int i = 0; i < quest.getObjectives().size(); i++) {
            final QuestObjective objective = quest.getObjectives().get(i);
            // we already populated this or its not unlocked.
            if (!objective.isUnlocked()) continue;

            // update existing components instead
            if (populatedObjectives.contains(objective, true)) {
                updateExistingObjectiveComponent(objective, i);
                continue;
            }

            populateObjectiveComponent(objective, i);
        }
    }

    public void populateQuestComponent(Quest quest) {
        if (activeQuest != null
                && StringUtils.equals(quest.getName(), activeQuest.getName())) {
            updateExistingQuestComponents(quest);
            return;
        }

        populatedObjectives.clear();
        activeQuest = quest;

        // text and icons
        questNameLabel.setText(quest.getName());
        completeness.setText(quest.getCompleteness() + "% complete");
        completeness.restart();

        final TextureRegion difficultyIconAsset = guiManager.getAsset().get(quest.getDifficulty().getAsset());
        questDifficultyIcon.setDrawable(new TextureRegionDrawable(difficultyIconAsset));
        questDifficultyTooltip.setText(quest.getDifficulty().getPrettyName());

        // populate each individual quest objective
        for (int i = 0; i < quest.getObjectives().size(); i++) {
            final QuestObjective objective = quest.getObjectives().get(i);
            if (!objective.isUnlocked()) continue;

            populateObjectiveComponent(objective, i);
            populatedObjectives.add(objective);
        }

        // populate items required table
        if (quest.hasItemRequirements())
            populateQuestItemsRequired(itemsRequiredTable, quest.getItemsRequired(), itemsRequiredLabel);
        if (quest.hasRewards()) populateQuestRewards(questRewardsTable, quest.getRewards(), questRewardsLabel);
    }

    private void updateExistingObjectiveComponent(QuestObjective objective, int i) {
        if (objective.isCompleted()) {
            questObjectiveLabels.get(i).setText("[#2c3d42][~]" + (i + 1) + ". " + objective.getDescription());
        }
    }

    private void populateObjectiveComponent(QuestObjective objective, int i) {
        TypingLabel label;
        boolean wasExistingLabel = false;

        // check if we already have a label stored
        if (questObjectiveLabels.containsKey(i)) {
            label = questObjectiveLabels.get(i);
            wasExistingLabel = true;
        } else {
            label = new TypingLabel(StringUtils.EMPTY, Styles.getMediumWhiteMipMapped());
            questObjectiveLabels.put(i, label);
        }

        // populate completeness text
        if (objective.isCompleted()) {
            label.setText("[#2c3d42][~]" + (i + 1) + ". " + objective.getDescription());
        } else {
            label.setText("[BLACK]" + (i + 1) + ". " + objective.getDescription() + "[%50][RED] (!)");
        }

        if (wasExistingLabel) label.restart();
        if (!wasExistingLabel) {
            label.setWrap(true);
            label.setWidth(400);
            objectivesTable.add(label).width(400).padTop(6);
            objectivesTable.row();
        }
    }

    /**
     * Populate items required
     *
     * @param owner       the table owner
     * @param descriptors the list of items
     */
    private void populateQuestItemsRequired(Table owner, LinkedList<ItemDescriptor> descriptors, VisLabel labelOwner) {
        for (ItemDescriptor itemDescriptor : descriptors) {
            final Stack stack = new Stack();
            final VisImage background = new VisImage(Styles.getTheme());
            final VisImage icon = new VisImage(new TextureRegionDrawable(guiManager.getAsset().get(itemDescriptor.texture())));
            stack.add(background);
            stack.add(icon);

            new Tooltip.Builder(itemDescriptor.name())
                    .target(stack)
                    .style(Styles.getTooltipStyle())
                    .build()
                    .setAppearDelayTime(0.1f);

            owner.add(stack).padRight(6);
        }

        labelOwner.setVisible(true);
    }

    /**
     * Populate items required/rewards components
     *
     * @param owner   the table owner
     * @param rewards the list of rewards
     */
    private void populateQuestRewards(Table owner, LinkedList<QuestReward> rewards, VisLabel labelOwner) {
        for (QuestReward reward : rewards) {
            final Stack stack = new Stack();
            final VisImage background = new VisImage(Styles.getTheme());

            final VisImage icon = new VisImage(new TextureRegionDrawable(guiManager.getAsset().get(reward.descriptor().texture())));
            final String text = reward.descriptor().name();

            stack.add(background);
            stack.add(icon);

            new Tooltip.Builder(text)
                    .target(stack)
                    .style(Styles.getTooltipStyle())
                    .build()
                    .setAppearDelayTime(0.1f);

            owner.add(stack).padRight(6);
        }

        labelOwner.setVisible(true);
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);

        if (activeQuest != null) {
            completeness.restart();
            questObjectiveLabels.values().forEach(TypingLabel::restart);
        }
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
    }

    @Override
    public void hideRelatedGuis() {
        guiManager.hideGui(GuiType.QUEST);
        guiManager.hideGui(GuiType.INVENTORY);
    }

}
