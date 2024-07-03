package me.vrekt.oasis.gui.guis.quest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.questing.Quest;
import me.vrekt.oasis.questing.QuestReward;
import org.apache.commons.lang3.StringUtils;

/**
 * Shown when a quest is completed
 */
public final class QuestCompletedGui extends Gui {

    private static final String COLOR = "#0E2F00";

    private final TypingLabel header, questName, rewardsHeader;
    private final RewardContainer[] rewardContainers = new RewardContainer[3];

    public QuestCompletedGui(GuiManager guiManager) {
        super(GuiType.QUEST_COMPLETED, guiManager);

       disablePlayerMovement = true;
        hideWhenVisible.add(GuiType.HUD);

        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("quest_completed_background")));
        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        header = new TypingLabel("Quest completed!", Styles.getMediumWhiteMipMapped());
        questName = new TypingLabel(StringUtils.EMPTY, Styles.getLargeBlack());
        header.setColor(Color.BLACK);

        final VisTable itemParentContainer = new VisTable();

        for (int i = 0; i < 3; i++) {
            final Stack stack = new Stack();

            final VisImage image = new VisImage();
            final Tooltip tooltip = new Tooltip.Builder(StringUtils.EMPTY)
                    .target(stack)
                    .style(Styles.getTooltipStyle())
                    .build();
            tooltip.setAppearDelayTime(0.1f);

            stack.add(new VisImage(Styles.getTheme()));
            stack.add(image);
            stack.setVisible(false);

            rewardContainers[i] = new RewardContainer(stack, image, tooltip, new VisLabel());
            itemParentContainer.add(stack).padRight(8);
        }

        final VisTable escapeContainer = new VisTable();
        escapeContainer.add(new VisImage(guiManager.getAsset().get("escape_key")));
        escapeContainer.add(new VisLabel("Continue", Styles.getSmallWhite())).padLeft(8);

        final VisTable container = new VisTable();

        container.add(header).top();
        container.row();
        container.add(questName);
        container.row().padTop(32);
        rewardsHeader = new TypingLabel("[BLACK]Rewards", Styles.getLargeBlack());
        container.add(rewardsHeader).padBottom(16);
        container.row();
        container.add(itemParentContainer);
        container.row();
        container.add(escapeContainer).padTop(16);

        rootTable.add(container).fill().expand();

        guiManager.addGui(rootTable);
    }

    /**
     * Show a quest that was completed
     *
     * @param quest quest
     */
    public void showQuestCompleted(Quest quest) {
        show();

        header.restart();
        questName.setText("[" + COLOR + "]" + quest.getName());
        questName.restart();
        rewardsHeader.restart();

        float delay = 0.1f;
        for (int i = 0; i < quest.getRewards().size(); i++) {
            final QuestReward reward = quest.getRewards().get(i);
            RewardContainer container = rewardContainers[i];

            container.image.setDrawable(new TextureRegionDrawable(guiManager.getAsset().get(reward.descriptor().texture())));
            container.tooltip.setText(reward.descriptor().name() + " x" + reward.amount());

            container.parent.addAction(Actions.sequence(Actions.alpha(0.0f), Actions.visible(true), Actions.fadeIn(1.2f + delay)));

            delay += 0.65f;
        }
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();

        for (RewardContainer container : rewardContainers) {
            container.parent.setVisible(false);
        }

        rootTable.setVisible(false);
    }

    private record RewardContainer(Stack parent, VisImage image, Tooltip tooltip, VisLabel text) {
    }

}
