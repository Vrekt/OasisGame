package me.vrekt.oasis.world.obj.interaction.impl;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.network.IntegratedGameServer;
import me.vrekt.oasis.network.connection.client.NetworkCallback;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.save.Loadable;
import me.vrekt.oasis.save.Savable;
import me.vrekt.oasis.save.world.obj.WorldObjectSaveState;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.client.C2SAnimateObject;
import me.vrekt.shared.packet.client.C2SDestroyWorldObject;
import me.vrekt.shared.packet.server.obj.S2CAnimateObject;
import me.vrekt.shared.packet.server.obj.S2CDestroyWorldObjectResponse;
import me.vrekt.shared.packet.server.obj.S2CNetworkRemoveWorldObject;
import me.vrekt.shared.protocol.Packets;
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

    protected boolean wasInteractedWith, isEnabled = true, updatable = true, render = true;
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

    /**
     * @return if this object should be rendered.
     */
    public boolean render() {
        return render;
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

    /**
     * Interact with this object
     */
    public void interact() {
        world.getGame().getGuiManager().resetCursor();
        world.player().notifyIfMoved();

        lastInteraction = GameManager.tick();

        wasInteractedWith = true;
        isEnabled = false;
        interactionPoint.set(world.player().getPosition());
    }

    /**
     * Broadcast animation of this object
     */
    protected void broadcastAnimation() {
        if (world.getGame().isHostingMultiplayerGame()) {
            world.getGame().integratedServer().activeWorld().broadcastImmediately(new S2CAnimateObject(objectId));
        } else if (world.getGame().isInMultiplayerGame()) {
            world.player().getConnection().sendImmediately(new C2SAnimateObject(objectId));
        }
    }

    /**
     * Broadcast this object was destroyed.
     * TODO: Fade in spawn item, more smooth.
     */
    protected void broadcastDestroyed() {
        if (world.getGame().isHostingMultiplayerGame()) {
            world.getGame().integratedServer().activeWorld().broadcastImmediately(new S2CNetworkRemoveWorldObject(objectId));
        } else if (world.getGame().isInMultiplayerGame()) {
            final GamePacket packet = new C2SDestroyWorldObject(objectId);

            // wait for a response, go ahead and destroy this object
            // but if the server tells us no, then take it back!

            NetworkCallback.immediate(packet)
                    .waitFor(Packets.S2C_DESTROY_OBJECT_RESPONSE)
                    .timeoutAfter(2000)
                    .ifTimedOut(() -> GameLogging.warn(this, "S2CDestroy timed out "/* unlikely */))
                    .sync()
                    .accept(callback -> {
                        final S2CDestroyWorldObjectResponse authority = (S2CDestroyWorldObjectResponse) callback;
                        if (!authority.valid()) {
                            // rollback
                            world.reinstateWorldObject(this);
                        } else {
                            // destroy.
                            world.removeInteraction(this);
                        }
                    }).send();

            // don't destroy in-case response is no.
            world.invalidateWorldObject(this);
        }
    }

    /**
     * @return {@code true} if this player is a host of the server
     */
    protected boolean isNetworkHost() {
        return GameManager.game().isHostingMultiplayerGame();
    }

    /**
     * @return {@code true} if this player is a player in a mp server
     */
    protected boolean isNetworkPlayer() {
        return GameManager.game().isInMultiplayerGame();
    }

    /**
     * @return active network world
     */
    protected ServerWorld activeNetworkWorld() {
        return GameManager.game().integratedServer().activeWorld();
    }

    /**
     * @return the server
     */
    protected IntegratedGameServer server() {
        return GameManager.game().integratedServer();
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
     * Show this object
     */
    public void show() {
        isEnabled = true;
        updatable = true;
        render = true;
    }

    /**
     * Hide this object
     */
    public void hide() {
        isEnabled = false;
        updatable = false;
        render = false;
    }

    /**
     * Reset this interaction state
     */
    public void reset() {
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
