package me.vrekt.oasis.world.lp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.guis.lockpicking.LockpickingGui;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.misc.LockpickItem;
import me.vrekt.oasis.utility.hints.PlayerHints;

/**
 * Main lockpick activity, randomized.
 */
public final class LockpickActivity {

    private static final float WIDTH_MODIFIER = 34.4f;
    private static final float HEIGHT_MODIFIER = 31.0f;

    private static final float END_PROGRESS_HEIGHT = 249.0f;
    private static final float END_PROGRESS = 272.0f;

    private final LockpickingGui gui;
    private final PlayerSP player;

    private int activeKey;

    private float circleWidth = 0, circleHeight = 0;
    private float updateTime;
    private float difficulty, latenessDifficulty;
    private float lateness;

    private boolean end;
    private boolean isFinished;

    private Runnable failure, success;

    public LockpickActivity(LockpickingGui gui, PlayerSP player) {
        this.gui = gui;
        this.player = player;
    }

    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Start the activity
     *
     * @param success runs if the player succeeded.
     * @param failure runs if the player fails
     */
    public void start(Runnable success, Runnable failure) {
        reset();
        player.disableMovement();

        this.failure = failure;
        this.success = success;

        gui.show();
    }

    /**
     * Update the circle dimensions and relevant progress + key updates
     */
    public void update() {
        if (isFinished) return;

        if (updateTime == 0.0f) {
            updateTime = GameManager.tick();
        } else if (GameManager.hasTimeElapsed(updateTime, difficulty)) {
            updateTime = GameManager.tick();
            circleWidth += WIDTH_MODIFIER;
            circleHeight += HEIGHT_MODIFIER;

            circleWidth = Math.min(circleWidth, END_PROGRESS);
            circleHeight = Math.min(circleHeight, END_PROGRESS_HEIGHT);
        }

        gui.updateProgress(circleWidth, circleHeight);

        // any main key pressed? fail
        final boolean isAnyKeyPressed = Gdx.input.isKeyJustPressed(Input.Keys.W)
                || Gdx.input.isKeyJustPressed(Input.Keys.A)
                || Gdx.input.isKeyJustPressed(Input.Keys.S)
                || Gdx.input.isKeyJustPressed(Input.Keys.D);

        final boolean isKeyPressed = Gdx.input.isKeyJustPressed(activeKey);
        if (circleWidth >= END_PROGRESS && !end) {
            end = true;
            click();

            gui.showPressDownHint(activeKey);
            // player only has X time to press before failing
            lateness = GameManager.tick();
        }

        if (!isKeyPressed && circleWidth >= END_PROGRESS && GameManager.hasTimeElapsed(lateness, latenessDifficulty)) {
            // player failed.
            failed();
            reset();
        } else if (isKeyPressed && circleWidth >= END_PROGRESS) {
            // player succeeded.
            activeKey = next();
            if (activeKey != 0) gui.next(activeKey);
        } else if ((isAnyKeyPressed || isKeyPressed) && circleWidth < END_PROGRESS - latenessDifficulty) {
            // player pressed the key too early.
            failed();
            reset();
        }
    }

    /**
     * Randomize the challenge
     */
    private void randomize() {
        latenessDifficulty = MathUtils.random(0.55f, 0.75f);
        difficulty = MathUtils.random(0.25f, 0.55f);
    }

    /**
     * Get the next key, or play the success if the player is on the final key
     *
     * @return the key or {@code 0} if the player succeeded.
     */
    private int next() {
        // play sound
        success();
        randomize();

        if (activeKey == Input.Keys.D) {
            isFinished = true;

            // player finished
            GameManager.game().tasks().schedule(() -> {
                gui.hide();
                success.run();
            }, .5f);
            return 0;
        }

        circleWidth = 0.0f;
        circleHeight = 0.0f;
        updateTime = 0.0f;
        end = false;

        return switch (activeKey) {
            case Input.Keys.W -> Input.Keys.A;
            case Input.Keys.A -> Input.Keys.S;
            case Input.Keys.S -> Input.Keys.D;
            default -> throw new UnsupportedOperationException();
        };

    }

    /**
     * Activity failed
     */
    private void failed() {
        // break the players lockpick if they failed
        final LockpickItem item = player.getInventory().getOf(Items.LOCK_PICK);
        if (item != null && item.shouldBreak()) item.destroy(player);

        // if the player has no more lockpicks, then stop this activity
        if (!player.getInventory().containsItem(Items.LOCK_PICK)) {
            GameManager.gui().getHintComponent().showPlayerHint(PlayerHints.NO_MORE_LOCKPICKS, 3.5f, 10.0f);
            failure.run();
            gui.hide();
        } else {
            GameManager.playSound(Sounds.LOCKPICK_FAIL, 1.0f, 1.0f, 0.0f);
            reset();
        }
    }

    /**
     * cancelled due to escape key press.
     */
    public void cancelled() {
        if (failure != null) failure.run();
    }

    /**
     * Reset the activity, will also re-randomize again.
     */
    private void reset() {
        randomize();

        gui.resetAll();
        circleWidth = 0.0f;
        circleHeight = 0.0f;
        activeKey = Input.Keys.W;
        updateTime = 0.0f;
        end = false;
    }

    /**
     * Play the hit sound (in range)
     */
    public void click() {
        GameManager.playSound(Sounds.LOCK_CLICK, 0.15f, MathUtils.random(0.88f, 1.5f), 0.0f);
    }

    /**
     * Play the success sound
     */
    public void success() {
        GameManager.playSound(Sounds.LOCK_SUCCESS, 1f, 1.0f, 0.0f);
    }

}
