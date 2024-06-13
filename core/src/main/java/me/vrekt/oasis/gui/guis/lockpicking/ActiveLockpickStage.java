package me.vrekt.oasis.gui.guis.lockpicking;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.GameManager;

/**
 * Active lockpick stage
 */
public final class ActiveLockpickStage {

    private final IntMap<Boolean> done = new IntMap<>();
    private final IntMap<LockpickUiComponent> components = new IntMap<>();

    private boolean isDone, isWaiting;
    private float waitTime;
    private LockpickUiComponent component;

    public ActiveLockpickStage() {
        create();
    }

    public void add(int key, LockpickUiComponent component) {
        this.components.put(key, component);
    }

    /**
     * Set the current active stage
     *
     * @param key key
     */
    public void of(int key) {
        this.component = components.get(key);
        this.isWaiting = false;
        this.isDone = false;
    }

    private void create() {
        done.put(Input.Keys.W, false);
        done.put(Input.Keys.A, false);
        done.put(Input.Keys.S, false);
        done.put(Input.Keys.D, false);
    }

    /**
     * Reset all stages
     */
    public void resetAll() {
        create();
        components.values().forEach(LockpickUiComponent::reset);
    }

    /**
     * Update this stage
     *
     * @param progress progress
     * @param delta    delta
     * @return {@code true} if this stage is finished
     */
    public boolean update(float progress, float delta) {
        if (!isDone && component.valid()) {
            isDone = component.update(progress, delta);
            done.put(component.key, isDone);
        }

        if (isDone) {
            if (!isWaiting) {
                waitTime = GameManager.getTick();
                isWaiting = true;
            }

            if (GameManager.hasTimeElapsed(waitTime, 0.35f)) {
                next();
            }
        }

        return isDone;
    }

    private void next() {
        switch (component.key) {
            case Input.Keys.W -> of(Input.Keys.A);
            case Input.Keys.A -> of(Input.Keys.S);
            case Input.Keys.S -> of(Input.Keys.D);
        }
    }

    boolean isCompleted() {
        for (IntMap.Entry<Boolean> entry : done) {
            if (!entry.value) return false;
        }
        return true;
    }

    boolean failed(float progress) {
        return component.failed(progress);
    }

}
