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
import me.vrekt.oasis.ui.book.BookPage;
import org.apache.commons.text.WordUtils;

import java.util.Map;

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
    public void render(Batch batch, BitmapFont big, BitmapFont small, float x, float y) {
        if (layout == null) layout = new GlyphLayout(big, title);
        small.setColor(Color.GRAY);

        // margins
        x += marginX;
        y -= marginY;

        // draw quest information before Y is modified
        if (questInformation != null) {
            layout.setText(small, questInformation);
            small.draw(batch, questInformation, ((x - marginX) + innerMargin) + (layout.width / 2f), y);
        }

        // draw each quest
        for (Map.Entry<String, Quest> e : quests.getQuests().entrySet()) {
            if (e.getValue().isStarted()) {
                small.setColor(Color.YELLOW);
            } else {
                small.setColor(Color.RED);
            }

            layout.setText(small, e.getKey());
            small.draw(batch, e.getKey(), x + (layout.width / 2f), y);

            // add button.
            this.buttons.put(new Rectangle(x + (layout.width / 2f), y - layout.height, layout.width, layout.height), e.getKey());
            y += layout.height;
        }

    }

    @Override
    public void handleClick(float x, float y) {
        // check if we clicked on a quest.
        final String clicked = getButtonClicked(x, y);
        if (clicked != null) {
            // split information to fit within page.
            this.questInformation = WordUtils.wrap(quests.getQuests().get(clicked).getQuestInformation(), 10);
        }
    }

    @Override
    public void hide() {

    }
}
