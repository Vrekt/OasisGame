package me.vrekt.oasis.gui.quest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.type.QuestSection;
import org.apache.commons.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Players quest log
 */
public final class QuestGui extends Gui {

    private final Table root;
    private final Table left;

    // table for when player has no quests.
    private final Table noQuestsTable;

    // active colors
    private final TextureRegionDrawable base, darker;

    // quests tracking.
    private final Array<Quest> quests = new Array<>();

    // map of sections that contain the parent table where quests should go.
    private final Map<QuestSection, Table> questTableSections = new HashMap<>();
    // map of quests tracking under sections
    private final Map<Quest, Table> questTables = new HashMap<>();

    private final Label questTitle, questInformation;
    private Quest selectedQuest;

    public QuestGui(GameGui gui) {
        super(gui);

        root = new Table();
        root.setVisible(true);
        gui.createContainer(root).fill();

        left = new Table().top().padLeft(16f);
        Table right = new Table().top();

        final TextureRegionDrawable background = new TextureRegionDrawable(gui.getAsset().get("quest_background"));
        left.setBackground(background);
        right.setBackground(background);

        // expand left and right to fill space.
        root.add(left).growY();
        root.add(right).grow();

        base = new TextureRegionDrawable(gui.getAsset().get("quest_title"));
        darker = new TextureRegionDrawable(gui.getAsset().get("quest_title_dark"));

        left.add(new Label("Quests", gui.getSkin(), "big", Color.WHITE)).left();
        left.row();

        // player has no tracking quests at the moment
        this.noQuestsTable = new Table();
        noQuestsTable.add(new Label("No quests added yet!", gui.getSkin(), "big", Color.WHITE)).left();
        noQuestsTable.row();
        noQuestsTable.add(new Label("(Go out and see what the world of Athena has to offer!)",
                gui.getSkin(), "smaller", Color.WHITE)).left();

        this.questTitle = new Label("", gui.getSkin(), "big", Color.WHITE);
        this.questInformation = new Label("", gui.getSkin(), "small", Color.WHITE);

        final Table questInformation = new Table();
        questInformation.add(this.questTitle).left();
        questInformation.row();
        questInformation.add(this.questInformation).left();
        right.add(questInformation).padLeft(16).padTop(32).left();

        left.add(noQuestsTable);
    }

    /**
     * Start tracking a quest in the GUI
     *
     * @param quest the quest
     */
    public void addQuest(Quest quest) {
        this.quests.add(quest);
        this.left.removeActor(noQuestsTable);

        if (!questTableSections.containsKey(quest.getSection())) {
            startTrackingQuestSection(quest.getSection(), quest.getChapter());
        }
        startTrackingQuestUnderSection(quest.getSection(), quest);
    }

    /**
     * Start tracking a new quest section
     *
     * @param section section
     */
    private void startTrackingQuestSection(QuestSection section, String chapter) {
        final Table parent = new Table();

        final Table table = new Table();
        table.setBackground(new TextureRegionDrawable(gui.getAsset().get("quest_chapter")));
        table.add(new Label(section.getName(), gui.getSkin(), "small", Color.BLACK)).padLeft(16).padRight(16);
        table.row();
        table.add(new Label(chapter, gui.getSkin(), "smaller", Color.BLACK));

        parent.add(table);

        left.row();
        left.add(parent).padTop(32);
        this.questTableSections.put(section, parent);
    }

    /**
     * Start tracking a quest under an existing section
     *
     * @param section section
     */
    private void startTrackingQuestUnderSection(QuestSection section, Quest quest) {
        final Table parent = this.questTableSections.get(section);
        final Table table = new Table();

        table.setBackground(new TextureRegionDrawable(gui.getAsset().get("quest_title")));
        table.add(new Label(quest.getName(), gui.getSkin(), "smaller", Color.BLACK))
                .padLeft(16).padRight(16).padTop(8);
        table.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                table.setBackground(darker);
                table.invalidate();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                table.setBackground(base);
                table.invalidate();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                questTitle.setText(quest.getName());
                questInformation.setText(WordUtils.wrap(quest.getQuestInformation(), 25));
                selectedQuest = quest;
                return true;
            }
        });

        parent.row();
        parent.add(table);

        this.questTables.put(quest, table);
    }

    @Override
    public void showGui() {
        if (selectedQuest != null) {
            questTitle.setText(selectedQuest.getName());
            questInformation.setText(WordUtils.wrap(selectedQuest.getQuestInformation(), 25));
        }

        super.showGui();
        root.setVisible(true);
    }

    @Override
    public void hideGui() {
        super.hideGui();
        root.setVisible(false);
    }

}
