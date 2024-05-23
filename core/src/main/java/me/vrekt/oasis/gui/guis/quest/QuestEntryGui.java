package me.vrekt.oasis.gui.guis.quest;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.item.utility.ItemDescriptor;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.questing.Quest;
import me.vrekt.oasis.questing.QuestObjective;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class QuestEntryGui extends Gui {

    private final PlayerQuestManager manager;
    private final TypingLabel completeness;
    private final VisLabel questNameLabel;
    private final VisTable objectivesTable;
    private final VisTable itemsRequiredTable;
    private final VisTable questRewardsTable;
    private final VisImage questDifficultyIcon;
    private final Tooltip questDifficultyTooltip;
    private final Map<Integer, TypingLabel> questObjectiveLabels = new HashMap<>();
    private Quest activeQuest;

    public QuestEntryGui(GuiManager guiManager) {
        super(GuiType.QUEST_ENTRY, guiManager);

        manager = guiManager.getGame().getPlayer().getQuestManager();

        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        hasParent = true;
        parent = GuiType.QUEST;
        inheritParentBehaviour = true;

        final TextureRegionDrawable drawable = new TextureRegionDrawable(guiManager.getAsset().get("quest_entry"));
        rootTable.setBackground(drawable);

        questNameLabel = new VisLabel(StringUtils.EMPTY, guiManager.getStyle().getLargeBlack());
        completeness = new TypingLabel("(25% complete)", guiManager.getStyle().getSmallWhite());
        completeness.setColor(44 / 255f, 61 / 255f, 66 / 255f, 1.0f);

        questDifficultyIcon = new VisImage();
        questDifficultyTooltip = new Tooltip.Builder(StringUtils.EMPTY)
                .target(questDifficultyIcon)
                .style(guiManager.getStyle().getTooltipStyle())
                .build();
        ((VisLabel) questDifficultyTooltip.getContent()).setStyle(guiManager.getStyle().getSmallWhite());

        final VisLabel itemsRequiredLabel = new VisLabel("Items Required", guiManager.getStyle().getLargeBlack());
        final VisLabel questRewardsLabel = new VisLabel("Rewards", guiManager.getStyle().getLargeBlack());

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
        components.add(completeness).left().padTop(-12);

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

    public void setActiveQuest(Quest quest) {
        // prevent duplication
        if (this.activeQuest != null
                && (this.activeQuest.getName().equals(quest.getName()))) return;
        this.activeQuest = quest;

        // text and icons
        questNameLabel.setText(quest.getName());
        completeness.setText(quest.getCompleteness() + "% complete");
        completeness.restart();

        // FIXME: When I figure out quest difficulty icons EM-23
        final TextureRegion difficultyIconAsset = guiManager.getAsset().get(quest.getDifficulty().getAsset());
        if (difficultyIconAsset != null) {
            questDifficultyIcon.setDrawable(new TextureRegionDrawable(difficultyIconAsset));
        }

        questDifficultyTooltip.setText(quest.getDifficulty().getPrettyName());

        // populate each individual quest objective
        for (int i = 0; i < quest.getObjectives().size(); i++) {
            final QuestObjective objective = quest.getObjectives().get(i);
            if (!objective.isUnlocked()) continue;

            TypingLabel label;
            boolean wasExistingLabel = false;

            // check if we already have a label stored
            if (questObjectiveLabels.containsKey(i)) {
                label = questObjectiveLabels.get(i);
                wasExistingLabel = true;
            } else {
                label = new TypingLabel(StringUtils.EMPTY, guiManager.getStyle().getMediumWhite());
                questObjectiveLabels.put(i, label);
            }

            // populate completeness text
            if (objective.isCompleted()) {
                label.setText("[#2c3d42][~]" + (i + 1) + ". " + objective.getDescription());
            } else {
                label.setText("[#2c3d42]" + (i + 1) + ". " + objective.getDescription() + "[%50][RED] (!)");
            }
            if (wasExistingLabel) label.restart();

            if (!wasExistingLabel) {
                label.setWrap(true);
                label.setWidth(400);
                objectivesTable.add(label).width(400).padTop(6);
                objectivesTable.row();
            }
        }

        // populate items required table
        populateQuestItemComponents(itemsRequiredTable, quest.getItemsRequired());
        populateQuestItemComponents(questRewardsTable, quest.getRewards());
    }

    /**
     * Populate items required/rewards components
     *
     * @param owner       the table owner
     * @param descriptors the list of items
     */
    private void populateQuestItemComponents(Table owner, LinkedList<ItemDescriptor> descriptors) {
        for (ItemDescriptor itemDescriptor : descriptors) {
            final Stack stack = new Stack();
            final VisImage background = new VisImage(guiManager.getStyle().getTheme());
            final VisImage icon = new VisImage(new TextureRegionDrawable(guiManager.getAsset().get(itemDescriptor.itemTexture)));
            stack.add(background);
            stack.add(icon);

            new Tooltip.Builder(itemDescriptor.itemName)
                    .target(stack)
                    .style(guiManager.getStyle().getTooltipStyle())
                    .build()
                    .setAppearDelayTime(0.1f);

            owner.add(stack).padRight(6);
        }
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
