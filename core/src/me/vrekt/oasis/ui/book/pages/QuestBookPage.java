package me.vrekt.oasis.ui.book.pages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.ui.book.BookPage;
import me.vrekt.oasis.ui.components.BasicButton;
import org.apache.commons.text.WordUtils;

import java.util.Map;

/**
 * A players quest book.
 */
public final class QuestBookPage extends BookPage {

    private final QuestManager quests;
    private GlyphLayout layout;

    private String questInformation;
    private String questDifficulty;

    public QuestBookPage(OasisGame game, TextureAtlas atlas) {
        super(1, "Quests");

        this.quests = game.getQuestManager();
        this.currentTabTexture = atlas.findRegion("book_tab", 1);
    }

    @Override
    public void render(Batch batch, BitmapFont big, BitmapFont small, float x, float y) {
        if (layout == null) layout = new GlyphLayout(big, title);

        big.setColor(Color.GRAY);
        big.draw(batch, title, bookCenterX * 1.5f, currentTabTexture.getRegionHeight() * 1.7f);
        big.draw(batch, "Prog.", bookCenterX * 2.65f, currentTabTexture.getRegionHeight() * 1.7f);

        //  font.getData().setScale(0.11f);
        float qy = currentTabTexture.getRegionHeight() * 1.7f - (layout.height / 2f);
        final float sqy = qy - layout.height;
        // draw all quests
        for (Map.Entry<String, Quest> entry : quests.getQuests().entrySet()) {
            // set color depending on if the quest is start/completed.
            if (entry.getValue().isStarted()) {
                small.setColor(Color.YELLOW);
            } else {
                small.setColor(Color.RED);
            }

            layout.setText(small, entry.getKey());
            qy -= layout.height;

            small.draw(batch, entry.getKey(), bookCenterX * 1.5f, qy);

            // set button for the quest.
            if (entry.getValue().getButton() == null) {
                entry.getValue().setButton(new BasicButton(bookCenterX * 1.35f, qy - layout.height, layout.width, layout.height));
            }
        }

        // draw quest information
        if (questInformation != null) {
            //   font.getData().setScale(0.12f);
            small.setColor(Color.DARK_GRAY);
            small.draw(batch, questInformation, bookCenterX * 2.6f, sqy);

            // draw quest difficulty
            small.draw(batch, "Difficulty: \n" + questDifficulty, bookCenterX * 2.6f, currentTabTexture.getRegionHeight());
        }
    }

    @Override
    public void hide() {
        this.questInformation = null;
        this.questDifficulty = null;
    }

    @Override
    public void handleClick(float x, float y) {
        for (Quest quest : quests.getQuests().values()) {
            if (quest.getButton().wasClicked(x, y)) {
                this.questInformation = WordUtils.wrap(quest.getQuestInformation(), 10);
                this.questDifficulty = quest.getDifficulty().getName();
            }
        }
    }

    @Override
    public void resetState() {
        // reset button states
        for (Quest quest : quests.getQuests().values()) {
            quest.setButton(null);
        }
    }
}
