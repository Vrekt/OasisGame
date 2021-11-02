package me.vrekt.oasis.ui.gui.quest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.type.QuestType;
import me.vrekt.oasis.ui.gui.GameGui;
import me.vrekt.oasis.ui.world.Gui;

import java.util.HashMap;
import java.util.Map;

/**
 * Players quest log
 */
public final class QuestGui extends Gui {

    public static final int ID = 6;
    private final Table root;
    private final Table content;
    private final Table right;

    // active colors
    private final TextureRegionDrawable base, darker;

    // quest elements tracked
    // TODO: Remove these elements
    private final Map<QuestType, QuestElement> elements = new HashMap<>();

    public QuestGui(GameGui gui) {
        super(gui);

        root = new Table();
        root.setVisible(false);
        gui.createContainer(root).fill();

        // left table content, quest chapters and titles.
        final Table left = new Table();
        left.top().padTop(32f);

        left.setBackground(new TextureRegionDrawable(gui.getAsset("quest_background")));
        gui.createContainer(left);

        // right table content, quest information.
        right = new Table();
        right.top().padTop(32f);

        right.setBackground(new TextureRegionDrawable(gui.getAsset("quest_background")));
        gui.createContainer(right);

        // expand left and right to fill space.
        root.add(left).grow();
        root.add(right).grow();

        // Root table
        content = new Table();

        base = new TextureRegionDrawable(gui.getAsset("quest_chapter"));
        darker = new TextureRegionDrawable(gui.getAsset("quest_chapter_dark"));

        left.add(new Label("Quests", gui.getSkin(), "big", Color.WHITE));
        left.row();
        left.add(content);
    }

    /**
     * Start tracking a quest in the GUI
     *
     * @param quest the quest
     */
    public void startTrackingQuest(Quest quest) {
        setQuestTracked(quest.getChapter(), quest.getSection(), quest.getName(), quest.getQuestInformation(), quest.getType());
    }

    private void setQuestTracked(String chapter, String section, String title, String information, QuestType type) {
        final QuestElement element = new QuestElement(gui, chapter, section, title, information);
        final Table table = new Table();
        element.setContent(table);

        table.addListener(new QuestChapterHandler(table));
        table.setBackground(base);

        table.add(element.getQuestChapter()).padLeft(16f).padRight(16f);
        table.row();
        table.add(element.getQuestSection()).padTop(6f);

        final Table titleTable = new Table();
        element.setTitleContent(titleTable);

        titleTable.setBackground(new TextureRegionDrawable(gui.getAsset("quest_title")));
        titleTable.add(element.getQuestTitle()).padLeft(16f).padRight(16f).padTop(8f);

        final Table informationTable = new Table();
        informationTable.add(element.getQuestInformation());
        informationTable.padLeft(32f);
        informationTable.row();

        final Table navigationTable = new Table();
        navigationTable.setBackground(new TextureRegionDrawable(gui.getAsset("green_button")));
        navigationTable.add(new Label("Navigate", gui.getSkin(), "small", Color.BLACK));
        informationTable.add(navigationTable).padTop(96f).center();

        content.add(table);
        content.row();
        content.add(titleTable);
        content.row().padTop(32f);
        right.add(informationTable);

        elements.put(type, element);
    }

    @Override
    public void showGui() {
        super.showGui();
        root.setVisible(true);
    }

    @Override
    public void hideGui() {
        super.hideGui();
        root.setVisible(false);
    }

    /**
     * Handles actions within the quest table.
     */
    private final class QuestChapterHandler extends ClickListener {

        private final Table table;

        public QuestChapterHandler(Table table) {
            this.table = table;
        }

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            table.setBackground(darker);
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            table.setBackground(base);
        }
    }

}
