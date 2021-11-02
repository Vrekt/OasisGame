package me.vrekt.oasis.ui.gui.quest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.ui.gui.GameGui;
import me.vrekt.oasis.ui.world.Gui;
import org.apache.commons.text.WordUtils;

/**
 * Players quest log
 */
public final class QuestGui extends Gui {

    public static final int ID = 6;
    private final Table root, questChapterContent1, questChapterContent2;

    // active colors
    private final TextureRegionDrawable base, darker;

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
        final Table right = new Table();
        right.top().padTop(32f);

        right.setBackground(new TextureRegionDrawable(gui.getAsset("quest_background")));
        gui.createContainer(right);

        // expand left and right to fill space.
        root.add(left).grow();
        root.add(right).grow();

        // Root table
        final Table questChapter = new Table();

        base = new TextureRegionDrawable(gui.getAsset("quest_chapter"));
        darker = new TextureRegionDrawable(gui.getAsset("quest_chapter_dark"));

        // First active quest
        questChapterContent1 = new Table();
        questChapterContent1.addListener(new QuestChapterHandler(questChapterContent1));
        questChapterContent1.setBackground(base);
        questChapterContent1.add(new Label("Origins of Hunnewell", gui.getSkin(), "small", Color.WHITE))
                .padLeft(16f)
                .padRight(16f);
        questChapterContent1.row();
        questChapterContent1.add(new Label("Chapter I: Act I", gui.getSkin(), "smaller", Color.BLACK)).padTop(6f);
        questChapterContent1.row();

        // Basic title
        final Table questChapterTitle1 = new Table();
        questChapterTitle1.setBackground(new TextureRegionDrawable(gui.getAsset("quest_title")));
        questChapterTitle1.add(new Label("Welcome to Hunnewell", gui.getSkin(), "smaller", Color.BLACK))
                .padLeft(16f)
                .padRight(16f)
                .padTop(8f);

        final Table questChapterTitle2 = new Table();
        questChapterTitle2.setBackground(new TextureRegionDrawable(gui.getAsset("quest_title")));
        questChapterTitle2.add(new Label("Exploring the Domains", gui.getSkin(), "smaller", Color.BLACK))
                .padLeft(16f)
                .padRight(16f)
                .padTop(8f);

        // Second active quest
        questChapterContent2 = new Table();
        questChapterContent2.setBackground(base);
        questChapterContent2.addListener(new QuestChapterHandler(questChapterContent2));
        questChapterContent2.add(new Label("Origins of Hunnewell", gui.getSkin(), "small", Color.WHITE))
                .padLeft(16f)
                .padRight(16f);
        questChapterContent2.row();
        questChapterContent2.add(new Label("Chapter I: Act II", gui.getSkin(), "smaller", Color.BLACK)).padTop(6f);
        questChapterContent2.row();

        questChapter.add(questChapterContent1);
        questChapter.row();
        questChapter.add(questChapterTitle1);
        questChapter.row().padTop(32f);
        questChapter.add(questChapterContent2);
        questChapter.row();
        questChapter.add(questChapterTitle2);

        // Quest information table
        final Table information = new Table();
        final Table navigation = new Table();
        navigation.center();

        information.add(new Label(WordUtils.wrap("Start by talking to Ino to learn about Hunnewell and its origins.", 20),
                gui.getSkin(),
                "small",
                Color.WHITE));
        information.padLeft(32f);
        information.row();

        // navigation button
        navigation.setBackground(new TextureRegionDrawable(gui.getAsset("green_button")));
        navigation.add(new Label("Navigate", gui.getSkin(), "small", Color.BLACK)).center();
        information.add(navigation).padTop(96f).center();

        left.add(new Label("Quests", gui.getSkin(), "big", Color.WHITE));
        left.row();
        left.add(questChapter);
        right.add(information);

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
