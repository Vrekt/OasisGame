package me.vrekt.oasis.gui.guis.hud.components;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.gui.guis.hud.HudComponent;
import me.vrekt.oasis.gui.guis.hud.HudComponentType;
import me.vrekt.oasis.utility.hints.PlayerHints;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumMap;

/**
 * Player hints
 */
public final class HudPlayerHintComponent extends HudComponent {

    private final TypingLabel hintComponentText;

    private boolean hintVisibilityOverridden, hintPaused;

    private float lastHintTime, currentHintDuration;
    private final EnumMap<PlayerHints, Float> hintTimes = new EnumMap<>(PlayerHints.class);

    public HudPlayerHintComponent(GuiManager manager) {
        super(HudComponentType.HINT, manager);

        rootTable.setVisible(false);
        rootTable.top();

        hintComponentText = new TypingLabel(StringUtils.EMPTY, Styles.getMediumWhiteMipMapped());

        final VisTable hint = new VisTable();
        hint.setBackground(Styles.getTheme());
        hint.add(new VisImage(asset("hint_icon")))
                .padLeft(4f)
                .padRight(4f)
                .padBottom(1f);

        hint.add(hintComponentText)
                .width(448)
                .padBottom(16)
                .padRight(8)
                .padLeft(8);
        hintComponentText.setWrap(true);
        rootTable.add(hint);

        guiManager.addGui(rootTable);
    }

    @Override
    public void update(float tick) {
        updatePlayerHintComponent(tick);
    }

    /**
     * Update the hint alpha actions
     *
     * @param now current tick
     */
    private void updatePlayerHintComponent(float now) {
        if (hintPaused) {
            // reset hint time since its being paused
            // we don't want it to instantly expire
            // when the hint is resumed
            lastHintTime = now;
        } else {
            // ensure hint component is visible, not indefinite (0) and hint has expired.
            if (rootTable.getColor().a == 1.0f
                    && currentHintDuration != 0.0f
                    && GameManager.hasTimeElapsed(lastHintTime, currentHintDuration)
                    && hintComponentText.hasEnded()) {
                rootTable.addAction(Actions.sequence(Actions.fadeOut(1.0f), Actions.run(this::hide)));
            }
        }
    }

    /**
     * Show a player hint
     *
     * @param hint     the hint
     * @param duration the duration in ticks
     * @param cooldown the cooldown before the same hint can be shown again
     */
    public void showPlayerHint(PlayerHints hint, float duration, float cooldown) {
        // do not show this hint if the cooldown has not been met yet.
        if (hintTimes.containsKey(hint) && !GameManager.hasTimeElapsed(hintTimes.get(hint), cooldown)) return;

        hintTimes.put(hint, GameManager.tick());

        if (!rootTable.isVisible()) {
            rootTable.getColor().a = 0.0f;
            rootTable.setVisible(true);
            hintVisibilityOverridden = true;
            hintPaused = false;
        }

        final float now = GameManager.tick();
        // don't show the hint if one is already active
        // TODO: Maybe in the future some method to check if any hints, if so expire?
        if (lastHintTime != 0.0f && now - lastHintTime < 32 /* 1.5ish seconds */) {
            return;
        }

        currentHintDuration = duration;
        show();
        fadeIn(rootTable, 1.0f);
        hintComponentText.setText(hint.text());
        hintComponentText.restart();
        lastHintTime = now;
    }

    /**
     * @return {@code true} if a hint is currently being shown
     */
    public boolean isHintActive() {
        return rootTable.isVisible() && rootTable.getColor().a > 0.0f;
    }

    public boolean isHintPaused() {
        return hintPaused;
    }

    /**
     * Pauses the current hint and hides the element.
     */
    public void pauseCurrentHint() {
        rootTable.setVisible(false);
        hintPaused = true;
    }

    /**
     * Resumes the current hint and shows the element
     */
    public void resumeCurrentHint() {
        rootTable.setVisible(true);
        hintPaused = false;
    }

    /**
     * Clear hints
     */
    public void clearHints() {
        rootTable.setVisible(false);
    }

}
