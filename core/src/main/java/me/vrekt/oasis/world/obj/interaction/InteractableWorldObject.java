package me.vrekt.oasis.world.obj.interaction;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.obj.WorldObject;

/**
 * Represents a {@link WorldObject} that can be interacted with
 */
public abstract class InteractableWorldObject extends WorldObject implements Interactable, ResourceLoader, Pool.Poolable {

    // worldIn
    protected OasisWorld world;
    protected WorldInteractionType interactionType;

    // interaction location
    protected final Vector2 interaction = new Vector2();

    // config
    protected boolean interactable = true, interactedWith, requiresUpdating = false;
    protected float updateDistance, interactionDistance;
    protected boolean withinUpdateDistance;

    public InteractableWorldObject() {
    }

    /**
     * Hide this interaction if needed.
     */
    protected void hideInteraction() {

    }

    public OasisWorld getWorld() {
        return world;
    }

    public String getRequiredItemTexture() {
        return null;
    }

    @Override
    public WorldInteractionType getInteractionType() {
        return interactionType;
    }

    @Override
    public void setInteractionType(WorldInteractionType type) {
        this.interactionType = type;
    }

    @Override
    public void initialize(OasisWorld world, float x, float y, float width, float height) {
        this.world = world;
        location.set(x, y);
        size.set(width, height);
        this.interactable = true;
    }

    @Override
    public boolean isWithinUpdateDistance(Vector2 other) {
        if (!requiresUpdating) return false;
        return withinUpdateDistance = other.dst2(location) <= updateDistance;
    }

    @Override
    public boolean isWithinUpdateDistanceCache() {
        if (!requiresUpdating) return false;
        return withinUpdateDistance;
    }

    @Override
    public boolean isWithinInteractionDistance(Vector2 other) {
        return other.dst2(location) <= interactionDistance;
    }

    @Override
    public void setInteractionDistance(float interactionDistance) {
        this.interactionDistance = interactionDistance;
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
    public boolean hasRequiredItem() {
        return true;
    }

    @Override
    public void interact() {
        this.interactable = false;
        this.interactedWith = true;
        this.interaction.set(world.getLocalPlayer().getPosition());
    }

    @Override
    public void update() {
        // cancel current interaction if player moved.
        if (interaction.dst2(world.getLocalPlayer().getPosition()) >= 0.25f) {
            this.interactable = true;
            this.interactedWith = false;
            hideInteraction();
        }
    }

    @Override
    public Cursor getCursor() {
        return Cursor.DEFAULT;
    }

    @Override
    public void reset() {
        super.reset();
        location.set(0, 0);
        size.set(0, 0);
        interaction.set(0, 0);
        world = null;
        updateDistance = 50;
        interactionDistance = 6;
        interactable = true;
        interactedWith = false;
    }
}
