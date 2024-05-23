package me.vrekt.oasis.gui.guis.quest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.questing.Quest;
import me.vrekt.oasis.questing.quests.QuestType;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The quest GUI
 */
public final class QuestGui extends Gui {

    private final LinkedList<VisLabel> questNameLabels = new LinkedList<>();
    private final Map<QuestType, VisTable> questComponents = new HashMap<>();
    private final PlayerQuestManager manager;
    private int activeIndex = 0;

    private final VisTable left;

    public QuestGui(GuiManager guiManager) {
        super(GuiType.QUEST, guiManager);
        this.manager = guiManager.getGame().getPlayer().getQuestManager();

        hideWhenVisible.add(GuiType.HUD);

        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        final VisLabel label = new VisLabel("Active Quests", guiManager.getStyle().getLargeBlack());
        final TextureRegionDrawable drawable = new TextureRegionDrawable(guiManager.getAsset().get("quest"));

        rootTable.setBackground(drawable);
        left = new VisTable(true);
        left.top().padTop(52).padLeft(116).left();

        left.add(label).left();
        left.row();

        rootTable.add(left).fill().expand();

        guiManager.addGui(rootTable);
    }

    private void populateActiveQuests() {
        for (Quest quest : manager.getActiveQuests().values()) {
            if (questComponents.containsKey(quest.getType())) continue;

            final VisTable parent = new VisTable();
            final VisLabel label = new VisLabel((activeIndex + 1) + ". " + StringUtils.EMPTY + quest.getName(), guiManager.getStyle().getMediumBlack());
            final VisLabel completeness = new VisLabel("(" + quest.getCompleteness() + "% complete)", guiManager.getStyle().getSmallWhite());
            completeness.setColor(Color.LIGHT_GRAY);

            parent.add(label).left();
            parent.add(completeness);
            questComponents.put(quest.getType(), parent);

            left.add(parent);
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
        populateActiveQuests();
        rootTable.setVisible(true);
        if (guiManager.getHudComponent().isHintActive()) guiManager.getHudComponent().pauseCurrentHint();
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
        if (guiManager.getHudComponent().isHintActive()) guiManager.getHudComponent().resumeCurrentHint();
    }

    @Override
    public void hideRelatedGuis() {
        guiManager.hideGui(GuiType.QUEST_ENTRY);
        guiManager.hideGui(GuiType.INVENTORY);
    }

}
