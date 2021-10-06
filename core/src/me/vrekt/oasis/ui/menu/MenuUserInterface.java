package me.vrekt.oasis.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Timer;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ui.InterfaceAdapter;

/**
 * A user interface meant for menus and other types that aren't in-game
 */
public abstract class MenuUserInterface extends InterfaceAdapter {

    /**
     * The timer
     */
    private final Timer timer = new Timer();

    /**
     * The scheduled task
     */
    private Timer.Task scheduledTask;

    public MenuUserInterface(OasisGame game) {
        super(game);
        createComponents();
    }

    /**
     * Schedule a new task
     *
     * @param action the action
     * @param delay  the delay
     */
    public Timer.Task schedule(Runnable action, float delay) {
        scheduledTask = timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                action.run();
            }
        }, 1, delay);
        return scheduledTask;
    }

    /**
     * Cancel task.
     */
    protected void cancelTask() {
        scheduledTask.cancel();
    }

    protected abstract void createComponents();

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

}
