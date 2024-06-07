package me.vrekt.oasis.world.obj.interaction.impl;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.utility.input.InteractionMouseHandler;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import org.apache.commons.lang3.StringUtils;

/**
 * Base implementation of {@link InteractableWorldObject}
 */
public abstract class AbstractInteractableWorldObject extends AbstractWorldObject implements InteractableWorldObject {

    private static final float INTERACTION_EXIT_DISTANCE = 1.0f;

    protected final WorldInteractionType type;
    protected final Vector2 interactionPoint;

    protected boolean wasInteractedWith, isEnabled = true, updatable = true;
    protected float interactionRange = 4.5f;

    protected InteractionMouseHandler mouseHandler;
    protected boolean handleMouseState = true;
    protected boolean mouseOver;

    public AbstractInteractableWorldObject(WorldInteractionType type, String key) {
        this.type = type;
        this.key = key;
        this.interactionPoint = new Vector2();
    }

    @Override
    public WorldInteractionType getType() {
        return type;
    }

    @Override
    public boolean wasInteractedWith() {
        return wasInteractedWith;
    }

    @Override
    public boolean isInInteractionRange() {
        return world.player().getPosition().dst2(position) <= interactionRange;
    }

    @Override
    public void setInteractionRange(float interactionRange) {
        this.interactionRange = interactionRange;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void enable() {
        this.isEnabled = true;
    }

    @Override
    public void disable() {
        this.isEnabled = false;
    }

    @Override
    public boolean isUpdatable() {
        return updatable;
    }

    @Override
    public void update() {
        // player moved away from this interaction so exit.
        if (world.player().movementNotified()
                || (wasInteractedWith && interactionPoint.dst2(world.player().getPosition())
                >= INTERACTION_EXIT_DISTANCE)) {
            reset();
        }
    }

    /**
     * Render UI components associated with this object
     *
     * @param batch    batch
     * @param font     font
     * @param position projected position
     */
    public void renderUiComponents(SpriteBatch batch, Styles styles, GuiManager manager, BitmapFont font, Vector3 position) {

    }

    @Override
    public void attachMouseHandler(InteractionMouseHandler handler) {
        this.mouseHandler = handler;
    }

    @Override
    public void updateMouseState() {
        if (!handleMouseState) return;

        if (!world.shouldUpdateMouseState() || wasInteractedWith || !isEnabled) return;

        final boolean result = isMouseOver(world.getCursorInWorld());
        if (result) {
            mouseOver = true;
            mouseHandler.handle(this, false);
        } else if (mouseOver) {
            mouseOver = false;
            mouseHandler.handle(this, true);
        }
    }

    @Override
    public void interact() {
        world.getGame().getGuiManager().resetCursor();
        world.player().notifyIfMoved();

        wasInteractedWith = true;
        isEnabled = false;
        interactionPoint.set(world.player().getPosition());
    }

    @Override
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
