package me.vrekt.oasis.world.common;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

/**
 * An interior, place, world, or building that has input listeners.
 */
public interface InputHandler extends InputProcessor {

    /**
     * Register this handler to the multiplexer.
     */
    void register(InputMultiplexer multiplexer);

    /**
     * Unregister this handler from the multiplexer.
     */
    void unregister(InputMultiplexer multiplexer);

    @Override
    default boolean keyDown(int keycode) {
        return false;
    }

    @Override
    default boolean keyUp(int keycode) {
        return false;
    }

    @Override
    default boolean keyTyped(char character) {
        return false;
    }

    @Override
    default boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    default boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    default boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    default boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    default boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
