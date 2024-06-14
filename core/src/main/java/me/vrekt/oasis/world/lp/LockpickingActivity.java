package me.vrekt.oasis.world.lp;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.guis.lockpicking.LockPickingGui;
import me.vrekt.oasis.gui.guis.lockpicking.LockpickUiComponent;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.misc.LockpickItem;
import me.vrekt.oasis.utility.hints.PlayerHints;

/**
 * Lockpicking mechanic
 */
public final class LockpickingActivity {

    // map of all keys that are done
    private final IntMap<Boolean> done = new IntMap<>();
    // all components
    private final IntMap<LockpickUiComponent> components = new IntMap<>();

    private final PlayerSP player;
    private Runnable success, failure;

    private boolean isDone, isWaiting, isFinished, inProgress;
    private float waitTime, progress;
    private LockpickUiComponent component;

    public LockpickingActivity() {
        create();

        this.player = GameManager.getPlayer();
    }

    public void add(int key, LockpickUiComponent component) {
        this.components.put(key, component);
    }

    /**
     * Set the current active stage
     *
     * @param key key
     */
    public void setActiveStage(int key) {
        this.component = components.get(key);
        this.isWaiting = false;
        this.isDone = false;
    }

    /**
     * Reset the map for all keys not being done
     */
    private void create() {
        done.put(Input.Keys.W, false);
        done.put(Input.Keys.A, false);
        done.put(Input.Keys.S, false);
        done.put(Input.Keys.D, false);
    }

    /**
     * Reset all stages
     */
    public void resetAll(LockPickingGui gui) {
        create();
        setActiveStage(Input.Keys.W);
        components.values().forEach(LockpickUiComponent::reset);

        isFinished = false;
        progress = 0.0f;

        gui.reset();
    }

    /**
     * Update this stage
     *
     * @param gui   gui handler
     * @param delta delta
     * @return {@code true} if this stage is finished
     */
    public boolean update(LockPickingGui gui, float delta) {
        if (isFinished) return false;

        this.progress += delta * 8f;

        if (!isDone && component.valid()) {
            isDone = component.update(progress, delta, this);
            done.put(component.key(), isDone);
        }

        if (isDone) {
            if (!isWaiting) {
                waitTime = GameManager.getTick();
                isWaiting = true;
            }

            // wait .35f seconds to next to finalize the animation
            if (GameManager.hasTimeElapsed(waitTime, 0.35f)) {
                next();
            }
        }

        if (isCompleted() && !isFinished) {
            complete(gui);
        } else if (hasFailed()) {
            failed(gui);
        }

        return isDone;
    }

    /**
     * Start this activity
     */
    public void start(LockPickingGui gui, Runnable success, Runnable failure) {
        components.put(Input.Keys.W, gui.createComponent(Input.Keys.W));
        components.put(Input.Keys.A, gui.createComponent(Input.Keys.A));
        components.put(Input.Keys.S, gui.createComponent(Input.Keys.S));
        components.put(Input.Keys.D, gui.createComponent(Input.Keys.D));

        setActiveStage(Input.Keys.W);

        player.disableMovement();
        inProgress = true;

        this.success = success;
        this.failure = failure;
    }

    /**
     * Complete this activity
     *
     * @param gui gui
     */
    private void complete(LockPickingGui gui) {
        isFinished = true;
        gui.showUnlocked();

        GameManager.getTaskManager().schedule(() -> {
            gui.hide();
            success.run();
        }, .5f);
    }

    /**
     * Activity failed
     *
     * @param gui GUI
     */
    private void failed(LockPickingGui gui) {
        // break the players lockpick if they failed
        final LockpickItem item = player.getInventory().getOf(Items.LOCK_PICK);
        if (item != null && item.shouldBreak()) item.destroy(player);

        // if the player has no more lockpicks, then stop this activity
        if (!player.getInventory().containsItem(Items.LOCK_PICK)) {
            GameManager.getGuiManager().getHudComponent().showPlayerHint(PlayerHints.NO_MORE_LOCKPICKS, 3.5f, 10.0f);
            failure.run();
            gui.hide();
        } else {
            resetAll(gui);
        }
    }

    /**
     * Gui was unexpectedly closed
     */
    public void guiClosed() {
        player.enableMovementAfter(0.25f);
        if (inProgress) {
            failure.run();
            inProgress = false;
        }
    }

    /**
     * Go next
     */
    private void next() {
        switch (component.key()) {
            case Input.Keys.W -> setActiveStage(Input.Keys.A);
            case Input.Keys.A -> setActiveStage(Input.Keys.S);
            case Input.Keys.S -> setActiveStage(Input.Keys.D);
        }
    }

    public boolean isCompleted() {
        for (IntMap.Entry<Boolean> entry : done) {
            if (!entry.value) return false;
        }
        return true;
    }

    public boolean hasFailed() {
        return component.failed(progress);
    }

    /**
     * Play the hit sound (in range)
     */
    public void click() {
        GameManager.playSound(Sounds.LOCK_CLICK, 0.15f, 1.0f, 0.0f);
    }

    /**
     * Play the success sound
     */
    public void success() {
        GameManager.playSound(Sounds.LOCK_SUCCESS, 1f, 1.0f, 0.0f);
    }

}
