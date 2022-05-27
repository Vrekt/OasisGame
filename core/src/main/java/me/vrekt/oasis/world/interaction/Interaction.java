package me.vrekt.oasis.world.interaction;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.environment.EnvironmentObject;

/**
 * Represents an interaction with the game, for example cutting down a tree.
 */
public class Interaction implements Pool.Poolable {

    // location of interaction
    protected Vector2 location = new Vector2();
    protected Vector2 interactedLocation = new Vector2();
    protected OasisWorld world;
    // id of this interaction for managing
    protected int id;

    // distance required to start interacting
    protected float interactionDistance = 6.9f;
    // how long it takes to complete interacting
    protected float interactionTime = 1f;
    // when the interaction was started
    protected long interactionStartedTime;
    // state
    protected boolean interactedWith, isInteractable = true;
    protected EnvironmentObject environmentObject;

    protected float distance;

    public Interaction() {
    }

    public void load(Asset asset) {

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

    public void setInteractable(boolean interactable) {
        isInteractable = interactable;
    }

    public float getDistance() {
        return distance;
    }

    public boolean isWithinInteractionDistance(Vector2 position) {
        distance = position.dst2(location);
        return distance <= interactionDistance;
    }

    public boolean isWithinUpdateDistance(Vector2 position) {
        distance = position.dst2(location);
        return distance <= OasisGameSettings.OBJECT_UPDATE_DISTANCE;
    }

    /**
     * interact with this
     */
    public void interact(OasisPlayerSP player) {
        if (interactedWith) return;

        this.interactedLocation.set(player.getPosition());
        this.interactionStartedTime = System.currentTimeMillis();
        this.interactedWith = true;
    }

    /**
     * Update the state
     */
    public void update(OasisPlayerSP player) {
        if (player.getPosition().dst(interactedLocation) >= 0.34f) {
            // player moved, invalidate.
            invalidate();
            return;
        }

        if ((System.currentTimeMillis() - interactionStartedTime >= interactionTime * 1000)
                && interactedWith) {
            this.interactionFinished();
            this.interactedWith = false;
        }
    }

    public void invalidate() {
        this.isInteractable = true;
        this.interactedWith = false;
    }

    public void render(SpriteBatch batch) {

    }

    /**
     * Invoked when an interaction is finished.
     */
    public void interactionFinished() {

    }

    @Override
    public void reset() {
        location.set(0, 0);
        interactedLocation.set(0, 0);
        interactionDistance = 2.5f;
        interactionTime = 1f;
        interactionStartedTime = 0;
        distance = 0;
        interactedWith = false;
        isInteractable = true;
        world = null;
        environmentObject = null;
    }
}
