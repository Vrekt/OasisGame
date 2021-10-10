package me.vrekt.oasis.ui.book.pages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.quest.type.QuestType;
import me.vrekt.oasis.ui.book.BookPage;
import org.apache.commons.text.WordUtils;

/**
 * A players quest book.
 */
public final class QuestBookPage extends BookPage {

    private final QuestManager quests;
    private GlyphLayout layout;

    // render current quest information
    private String questInformation;

    public QuestBookPage(OasisGame game, TextureAtlas atlas) {
        super(1, "Quests");

        this.quests = game.getQuestManager();
        this.currentTabTexture = atlas.findRegion("book_tab", 1);
    }

    @Override
    public void render(Batch batch, BitmapFont font, float x, float y) {
        if (layout == null) layout = new GlyphLayout(font, title);
        font.setColor(Color.GRAY);

        // margins
        x += marginX;
        y -= marginY;

        // draw quest information before Y is modified
        if (questInformation != null) {
            layout.setText(font, questInformation);
            font.draw(batch, questInformation, x - marginX + innerMargin + (layout.width / 2f), y);
        }

        // draw each quest
        for (Quest quest : quests.getQuests().values()) {
            if (quest.isStarted()) {
                font.setColor(Color.YELLOW);
            } else if (!quest.isStarted() && !quest.isCompleted()) {
                font.setColor(Color.RED);
            } else if (quest.isCompleted()) {
                font.setColor(Color.GREEN);
            }

            layout.setText(font, quest.getName());
            font.draw(batch, quest.getName(), x + (layout.width / 2f), y);

            // add button.
            this.buttons.put(new Rectangle(x + (layout.width / 2f), y - layout.height, layout.width, layout.height), quest.getType());
            y += layout.height;
        }

    }

    @Override
    public void handleClick(float x, float y) {
        // check if we clicked on a quest.
        final QuestType clicked = getButtonClicked(x, y);
        if (clicked != null) {
            // split information to fit within page.
            this.questInformation = WordUtils.wrap(quests.getQuests().get(clicked).getQuestInformation(), 10);
        }
    }

    @Override
    public void hide() {

    }
}
