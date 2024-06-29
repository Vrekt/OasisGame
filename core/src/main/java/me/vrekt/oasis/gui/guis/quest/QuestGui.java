package me.vrekt.oasis.gui.guis.quest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Resource;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.questing.Quest;
import org.apache.commons.lang3.StringUtils;

/**
 * The quest GUI
 */
public final class QuestGui extends Gui {

    private final PlayerQuestManager manager;

    private final VisTable left;

    public QuestGui(GuiManager guiManager) {
        super(GuiType.QUEST, guiManager);
        this.manager = guiManager.getGame().getPlayer().getQuestManager();

        hideWhenVisible.add(GuiType.HUD);

        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        final VisLabel label = new VisLabel("Active Quests", Styles.getLargeBlack());
        final TextureRegionDrawable drawable = new TextureRegionDrawable(guiManager.getAsset().get(Resource.UI, "quest", 2));

        rootTable.setBackground(drawable);
        left = new VisTable(true);
        left.top().padTop(52).padLeft(116).left();

        left.add(label).left();
        left.row();

        rootTable.add(left).fill().expand();

        guiManager.addGui(rootTable);
    }

    /**
     * Reset entries and populate quest components
     * TODO: Maybe instead of calculating content everytime we can store it
     * TODO: But for now this is fine
     */
    private void updateAndPopulateQuestComponents() {
        resetEntries();

        int index = 0;
        for (Quest quest : manager.getActiveQuests().values()) {
            final VisTable parent = new VisTable();
            final VisLabel label = new VisLabel((index + 1) + ". " + StringUtils.EMPTY + quest.getName(), Styles.getMediumWhiteMipMapped());
            label.setColor(Color.DARK_GRAY);

            final VisLabel completeness = new VisLabel("(" + quest.getCompleteness() + "% complete)", Styles.getSmallWhite());
            completeness.setColor(Color.DARK_GRAY);

            parent.add(label).left();
            parent.add(completeness).padLeft(8f);

            left.add(parent);
            left.row();
            index++;

            addHoverComponents(label, Color.BLACK, Color.DARK_GRAY, () -> handleQuestComponentClicked(quest));
        }
    }

    /**
     * Reset the table so we can populate them again
     */
    private void resetEntries() {
        left.clearChildren();
        left.invalidate();
    }

    /**
     * Show the child gui
     *
     * @param quest the active quest to set
     */
    private void handleQuestComponentClicked(Quest quest) {
        ((QuestEntryGui) guiManager.showChildGui(this, GuiType.QUEST_ENTRY)).populateQuestComponent(quest);
    }

    @Override
    public void show() {
        super.show();

        updateAndPopulateQuestComponents();
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
