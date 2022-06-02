package me.vrekt.oasis.world.interaction;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents an interaction within the game
 */
public class Interaction implements Interactable, Pool.Poolable, ResourceLoader {

    protected OasisWorld world;

    // location of interaction
    protected final Vector2 location = new Vector2();
    protected float width, height;

    // config
    protected boolean interactable, interactedWith;
    protected float updateDistance, interactionDistance;

    public Interaction() {
    }

    @Override
    public void initialize(OasisWorld world, float x, float y, float width, float height) {
        this.world = world;
        location.set(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean clickedOn(Vector3 clicked) {
        return clicked.x > location.x && clicked.x < (location.x + width) && clicked.y > location.y && clicked.y < (location.y + height);
    }

    @Override
    public boolean isWithinUpdateDistance(Vector2 other) {
        return other.dst2(location) <= updateDistance;
    }

    @Override
    public boolean isWithinInteractionDistance(Vector2 other) {
        return other.dst2(location) <= interactionDistance;
    }

    @Override
    public boolean isInteractedWith() {
        return interactedWith;
    }

    @Override
    public boolean isInteractable() {
        return interactable;
    }

    @Override
    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    @Override
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void interact() {
        this.interactable = false;
        this.interactedWith = true;
    }

    @Override
    public void update() {

    }

    @Override
    public void load(Asset asset) {

    }

    @Override
    public String getCursor() {
        return null;
    }

    @Override
    public void reset() {
        location.set(0, 0);
        world = null;
        width = 0;
        height = 0;
        updateDistance = 50;
        interactionDistance = 6;
        interactable = true;
        interactedWith = false;
    }

}
