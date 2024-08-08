package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;
import me.vrekt.oasis.questing.Quest;

/**
 * Shown when the player has a new quest
 */
public final class HudNewQuestComponent extends HudComponent {

    private final TypingLabel header, content;
    private final VisImage difficultyIcon;

    public HudNewQuestComponent(GuiManager manager) {
        super(HudComponentType.NEW_QUEST, manager);

        rootTable.setVisible(false);
        rootTable.bottom().padBottom(64);

        final VisTable parent = new VisTable();
        parent.setBackground(Styles.border());

        final VisTable labelContainer = new VisTable();
        final VisTable difficultyContainer = new VisTable();

        header = new TypingLabel("NEW QUEST ADDED", Styles.getMediumWhiteMipMapped());
        header.setColor(Color.valueOf("#072B36"));

        content = new TypingLabel("Getting Away", Styles.getLargeBlack());
        content.setColor(Color.BLACK);

        difficultyIcon = new VisImage();
        difficultyContainer.add(difficultyIcon).size(48, 48);

        labelContainer.add(header);
        labelContainer.row();
        labelContainer.add(content).padLeft(4);

        parent.add(labelContainer).padBottom(10).padRight(16);
        parent.add(difficultyContainer);
        rootTable.add(parent);

        guiManager.addGui(rootTable);
    }

    /**
     * Show a quest was added
     *
     * @param quest the quest
     */
    public void showQuestAdded(Quest quest) {
        GameManager.playSound(Sounds.QUEST_ADDED, 0.66f, 1.0f, 0.0f);

        header.restart();
        content.setText(quest.getName());
        content.restart();

        difficultyIcon.setDrawable(new TextureRegionDrawable(asset(quest.difficulty().getAsset())));
        fadeIn(rootTable, 1.55f);

        GameManager.game().tasks().schedule(() -> fadeOut(rootTable, 1.55f), 4);
    }

}
