package me.vrekt.oasis.gui.rewrite.guis.quest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.rewrite.Gui;
import me.vrekt.oasis.gui.rewrite.GuiManager;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.questing.Quest;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * The quest GUI
 */
public final class QuestGui extends Gui {

    private final LinkedList<VisLabel> questNameLabels = new LinkedList<>();
    private final PlayerQuestManager manager;
    private int activeIndex = 0;

    public QuestGui(GuiManager guiManager) {
        super(GuiType.QUEST, guiManager);
        this.manager = guiManager.getGame().getPlayer().getQuestManager();

        hideWhenVisible.add(GuiType.HUD);

        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        final VisLabel label = new VisLabel("Active Quests", guiManager.getStyle().getLargeBlack());

        final TextureRegionDrawable drawable = new TextureRegionDrawable(guiManager.getAsset().get("quest"));

        rootTable.setBackground(drawable);
        final VisTable left = new VisTable(true), right = new VisTable(true);
        right.top().padTop(36).padLeft(32).left();
        left.top().padTop(52).padLeft(116).left();

        left.add(label).left();
        left.row();
        populateQuestComponents(left);

        rootTable.add(left).fill().expand();

        guiManager.addGui(rootTable);
    }

    private void populateQuestComponents(Table left) {
        final Color color = new Color(0.132f, 0.220f, 0.198f, 1f);
        for (Quest quest : manager.getActiveQuests().values()) {
            final VisLabel label = new VisLabel((activeIndex + 1) + ". " + StringUtils.EMPTY + quest.getName(), guiManager.getStyle().getMediumBlack());
            final VisLabel completeness = new VisLabel("(" + quest.getCompleteness() + "% complete)", guiManager.getStyle().getSmallWhite());
            completeness.setColor(Color.LIGHT_GRAY);
            left.add(label).left();
            left.add(completeness);
            left.row();
            activeIndex++;

            addHoverComponents(label, Color.GRAY, Color.BLACK, () -> handleQuestComponentClicked(quest));
        }
    }

    /**
     * Show the child gui
     *
     * @param quest the active quest to set
     */
    private void handleQuestComponentClicked(Quest quest) {
        ((QuestEntryGui) guiManager.showChildGui(this, GuiType.QUEST_ENTRY)).setActiveQuest(quest);
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

    @Override
    public void hideRelatedGuis() {
        guiManager.hideGui(GuiType.QUEST_ENTRY);
        guiManager.hideGui(GuiType.INVENTORY);
    }

}
