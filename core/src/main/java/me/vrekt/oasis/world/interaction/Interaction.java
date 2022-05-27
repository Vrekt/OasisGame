package me.vrekt.oasis.world.interaction;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.environment.EnvironmentObject;

/**
 * Represents an interaction with the game, for example cutting down a tree.
 */
public class Interaction implements Pool.Poolable {

    // location of interaction
    protected Vector2 location = new Vector2();
    protected OasisWorld world;
    // id of this interaction for managing
    protected int id;

    // distance required to start interacting
    protected float interactionDistance = 2.5f;
    // how long it takes to complete interacting
    protected float interactionTime = 1f;
    // when the interaction was started
    protected long interactionStartedTime;
    // state
    protected boolean interactedWith, isInteractable = true;
    protected EnvironmentObject environmentObject;

    public Interaction() {
    }

    public void setLocation(float x, float y) {
        location.set(x, y);
    }

    public void setWorld(OasisWorld world) {
        this.world = world;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEnvironmentObject(EnvironmentObject environmentObject) {
        this.environmentObject = environmentObject;
    }

    public boolean isInteractedWith() {
        return interactedWith;
    }

    public boolean isInteractable() {
        return isInteractable;
    }

    public boolean isWithinInteractionDistance(Vector2 position) {
        return position.dst(location) <= interactionDistance;
    }

    public boolean isWithinUpdateDistance(Vector2 position) {
        return position.dst(location) <= OasisGameSettings.OBJECT_UPDATE_DISTANCE;
    }

    /**
     * interact with this
     */
    public void interact() {
        if (interactedWith) return;

        this.interactionStartedTime = System.currentTimeMillis();
        this.interactedWith = true;
    }

    /**
     * Update the state
     */
    public void update() {
        if ((System.currentTimeMillis() - interactionStartedTime >= interactionTime * 1000)
                && interactedWith) {
            this.interactionFinished();
            this.interactedWith = false;
        }
    }

    /**
     * Invoked when an interaction is finished.
     */
    public void interactionFinished() {

    }

    @Override
    public void reset() {
        location.set(0, 0);
        interactionDistance = 2.5f;
        interactionTime = 1f;
        interactionStartedTime = 0;
        interactedWith = false;
        isInteractable = true;
    }
}
