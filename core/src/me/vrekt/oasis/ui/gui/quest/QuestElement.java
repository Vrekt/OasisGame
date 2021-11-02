package me.vrekt.oasis.ui.gui.quest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.vrekt.oasis.ui.gui.GameGui;
import org.apache.commons.text.WordUtils;

public final class QuestElement {

    private final Label questChapter, questSection, questTitle, questInformation;
    private Table content, titleContent;

    public QuestElement(GameGui gui, String chapter, String section, String title, String information) {
        questChapter = new Label(chapter, gui.getSkin(), "small", Color.BLACK);
        questSection = new Label(section, gui.getSkin(), "smaller", Color.BLACK);
        questTitle = new Label(title, gui.getSkin(), "smaller", Color.BLACK);
        questInformation = new Label(WordUtils.wrap(information, 25), gui.getSkin(), "small", Color.BLACK);
    }

    public void setContent(Table content) {
        this.content = content;
    }

    public void setTitleContent(Table titleContent) {
        this.titleContent = titleContent;
    }

    public void setQuestChapter(String chapter) {
        questChapter.setText(chapter);
    }

    public void setQuestTitle(String title) {
        questTitle.setText(title);
    }

    public void setQuestInformation(String information) {
        questInformation.setText(information);
    }

    public Label getQuestChapter() {
        return questChapter;
    }

    public Label getQuestSection() {
        return questSection;
    }

    public Label getQuestInformation() {
        return questInformation;
    }

    public Label getQuestTitle() {
        return questTitle;
    }
}
