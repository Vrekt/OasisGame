package me.vrekt.oasis.entity.player.local;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.impl.LunarPlayer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The local player
 */
public final class Player extends LunarPlayer {

    private final List<Integer> inputsDisabled = new CopyOnWriteArrayList<>();

    public Player(int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId, playerScale, playerWidth, playerHeight, rotation);

        setVelocitySendRate(100);
        setPositionSendRate(250);
        setMoveSpeed(6.0f);
    }

    @Override
    protected void pollInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)
                && !inputsDisabled.contains(Input.Keys.A)) {
            velocity.set(-moveSpeed, 0f);
            rotation = Rotation.FACING_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)
                && !inputsDisabled.contains(Input.Keys.D)) {
            velocity.set(moveSpeed, 0f);
            rotation = Rotation.FACING_RIGHT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)
                && !inputsDisabled.contains(Input.Keys.W)) {
            velocity.set(0f, moveSpeed);
            rotation = Rotation.FACING_UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)
                && !inputsDisabled.contains(Input.Keys.S)) {
            velocity.set(0f, -moveSpeed);
            rotation = Rotation.FACING_DOWN;
        }
    }

    /**
     * Disable inputs
     *
     * @param keys the keys
     */
    public void disableInputs(int... keys) {
        for (int key : keys) inputsDisabled.add(key);
    }

    /**
     * Enable inputs
     *
     * @param keys the keys
     */
    public void enableInputs(int... keys) {
        for (int key : keys) if (inputsDisabled.contains(key)) inputsDisabled.remove((Integer) key);
    }

    /**
     * Check if an input is disabled
     *
     * @param key the key
     * @return {@code true} if so
     */
    private boolean isInputDisabled(int key) {
        return inputsDisabled.contains(key);
    }

    @Override
    public void dispose() {
        super.dispose();
        inputsDisabled.clear();
    }
}
