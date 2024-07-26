package me.vrekt.oasis.world.obj.interaction.impl;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.save.Loadable;
import me.vrekt.oasis.save.Savable;
import me.vrekt.oasis.save.world.obj.WorldObjectSaveState;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.shared.packet.server.obj.S2CAnimateObject;
import me.vrekt.shared.packet.server.obj.S2CNetworkRemoveWorldObject;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a world object that can be interacted with
 */
public abstract class AbstractInteractableWorldObject extends AbstractWorldObject implements
        Loadable<WorldObjectSaveState>,
        Savable<WorldObjectSaveState> {

    private static final float INTERACTION_EXIT_DISTANCE = 1.0f;

    protected final WorldInteractionType type;
    protected final Vector2 interactionPoint;
    protected int objectId;

    protected boolean wasInteractedWith, isEnabled = true, updatable = true;
    protected float interactionRange = 4.5f;

    protected boolean isUiComponent;
    protected boolean handleMouseState = true;

    protected boolean isCombatInteraction;

    protected float lastInteraction;
    protected float interactionDelay;

    // if this object has custom save data
    protected boolean saveSerializer;
    // if this object should even be saved at all.
    protected boolean shouldSave = true;

    public AbstractInteractableWorldObject(WorldInteractionType type, String key) {
        this.type = type;
        this.key = key;
        this.interactionPoint = new Vector2();
    }

    public AbstractInteractableWorldObject(WorldInteractionType type) {
        this.type = type;
        this.interactionPoint = new Vector2();
    }

    /**
     * @return the type of this interaction
     */
    public WorldInteractionType getType() {
        return type;
    }

    /**
     * @return unique identifier for networking
     */
    public int objectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    /**
     * @return {@code true} if this object should be saved.
     */
    public boolean hasSaveSerialization() {
        return saveSerializer;
    }

    /**
     * @return if the object should be saved
     */
    public boolean shouldSave() {
        return shouldSave;
    }

    /**
     * @return {@code true} if this object was interacted with.
     */
    public boolean wasInteractedWith() {
        return wasInteractedWith;
    }

    /**
     * @return {@code true} if the player is within interaction range of this object
     */
    public boolean isInInteractionRange() {
        return world.player().within(position, interactionRange);
    }

    /**
     * Set the interaction range
     *
     * @param interactionRange the range
     */
    public void setInteractionRange(float interactionRange) {
        this.interactionRange = interactionRange;
    }

    /**
     * @return interaction range of this object
     */
    public float interactionRange() {
        return interactionRange;
    }

    /**
     * @return if this interaction is enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Enable this interaction
     */
    public void enable() {
        this.isEnabled = true;
    }

    /**
     * Disable this interaction
     */
    public void disable() {
        this.isEnabled = false;
    }

    /**
     * @return {@code true} if this update requires updating.
     */
    public boolean isUpdatable() {
        return updatable;
    }

    @Override
    public boolean ready() {
        return isEnabled && handleMouseState;
    }

    /**
     * Update this object
     */
    public void update() {
        // player moved away from this interaction so exit.
        if (world.player().movementNotified()
                || (wasInteractedWith && interactionPoint.dst2(world.player().getPosition())
                >= INTERACTION_EXIT_DISTANCE)) {
            reset();
        }
    }

    /**
     * @return if this object should be called upon for UI rendering.
     */
    public boolean isUiComponent() {
        return isUiComponent;
    }

    /**
     * Render UI components associated with this object
     *
     * @param batch    batch
     * @param font     font
     * @param position projected position
     */
    public void renderUiComponents(SpriteBatch batch, GuiManager manager, BitmapFont font, Vector3 position) {

    }

    @Override
    public boolean clicked(Vector3 mouse) {
        if (isEnabled
                && isInInteractionRange()
                && !wasInteractedWith
                && !isCombatInteraction
                && (lastInteraction == 0.0f || GameManager.hasTimeElapsed(lastInteraction, interactionDelay))) {
            interact();
            return true;
        }
        return false;
    }

    /**
     * Interact with this object
     */
    public void interact() {
        world.getGame().getGuiManager().resetCursor();
        world.player().notifyIfMoved();

        lastInteraction = GameManager.getTick();

        wasInteractedWith = true;
        isEnabled = false;
        interactionPoint.set(world.player().getPosition());
    }

    /**
     * Broadcast animation of this object
     */
    protected void broadcastAnimation() {
        if (world.getGame().isLocalMultiplayer()) {
            world.getGame().getServer().activeWorld().broadcastImmediately(new S2CAnimateObject(objectId));
        } else if (world.getGame().isMultiplayer()) {
            // TODO: world.player().getConnection().sendImmediately(new C2SInteractWithObject(objectId));
        }
    }

    /**
     * Broadcast this object was destroyed.
     */
    protected void broadcastDestroyed() {
        if (world.getGame().isLocalMultiplayer()) {
            world.getGame().getServer().activeWorld().broadcastImmediately(new S2CNetworkRemoveWorldObject(objectId));
        } else if (world.getGame().isMultiplayer()) {
            // TODO:  world.player().getConnection().sendImmediately(new C2SDestroyWorldObject(objectId));
        }
    }

    /**
     * Check if the provided values match this interaction
     *
     * @param type the type
     * @param key  the key
     * @return {@code true} if so
     */
    public boolean matches(WorldInteractionType type, String key) {
        return this.type == type && StringUtils.equalsIgnoreCase(this.key, key);
    }

    /**
     * Reset this interaction state
     */
    protected void reset() {
        isEnabled = true;
        wasInteractedWith = false;
        exit();
    }

    /**
     * Exit this interaction
     */
    protected void exit() {

    }

}
