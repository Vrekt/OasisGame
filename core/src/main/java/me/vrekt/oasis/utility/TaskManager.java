package me.vrekt.oasis.utility;

import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.GameManager;

import java.util.Iterator;

/**
 * Handles scheduling tasks that run on the main game thread.
 */
public final class TaskManager {

    private final Array<Task> tasks = new Array<>();

    /**
     * Schedule a task
     *
     * @param task  the task
     * @param delay the delay
     */
    public void schedule(Runnable task, float delay) {
        tasks.add(new Task(task, delay));
    }

    /**
     * Update task manager
     * Ideally invoke after all world update tasks are finished
     */
    public void update() {
        for (Iterator<Task> it = tasks.iterator(); it.hasNext(); ) {
            final Task task = it.next();
            if (GameManager.hasTimeElapsed(task.timeScheduled, task.delay)) {
                GameManager.executeOnMainThread(task.runnable);
                it.remove();
            }
        }
    }

    private static final class Task {
        private final Runnable runnable;
        private final float delay;
        private final float timeScheduled;

        public Task(Runnable runnable, float delay) {
            this.runnable = runnable;
            this.delay = delay;
            this.timeScheduled = GameManager.getTick();
        }
    }

}
