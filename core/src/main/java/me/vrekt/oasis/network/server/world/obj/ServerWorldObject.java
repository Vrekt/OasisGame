package me.vrekt.oasis.network.server.world.obj;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import me.vrekt.oasis.network.server.entity.player.ServerPlayer;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.shared.packet.server.obj.S2CAnimateObject;
import me.vrekt.shared.packet.server.obj.S2CNetworkRemoveWorldObject;

/**
 * Basic wrapper for a server object.
 */
public class ServerWorldObject {

    private final ServerWorld worldIn;
    private final int objectId;

    private final WorldInteractionType type;
    private final Vector2 position;

    private int interactedId;
    private long lastInteractionTime;
    private boolean wasInteracted;

    public ServerWorldObject(ServerWorld worldIn, AbstractInteractableWorldObject object) {
        Preconditions.checkNotNull(worldIn);
        Preconditions.checkNotNull(object);

        this.worldIn = worldIn;
        this.type = object.getType();
        this.position = object.getPosition();
        this.objectId = object.objectId();
    }

    /**
     * @return this object as a breakable one, checks must be done first.
     */
    public ServerBreakableWorldObject asBreakable() {
        return (ServerBreakableWorldObject) this;
    }

    /**
     * @return ID of this object matching host client
     */
    public int objectId() {
        return objectId;
    }

    /**
     * @return type of
     */
    public WorldInteractionType type() {
        return type;
    }

    /**
     * @return position of this object
     */
    public Vector2 position() {
        return position;
    }

    /**
     * @return if this object was interacted with
     */
    public boolean wasInteracted() {
        return wasInteracted;
    }

    /**
     * @return who interacted with this object first.
     */
    public int interactedId() {
        return interactedId;
    }

    /**
     * Ensure proper time has passed.
     *
     * @return {@code true} if so
     */
    public boolean hasInteractionTimeElapsed() {
        return System.currentTimeMillis() - lastInteractionTime >= 75f;
    }

    /**
     * Interact with this object
     * Mainly animation purposes
     *
     * @param interacted who interacted
     * @return {@code true} if the interaction was successful.
     */
    public boolean interact(ServerPlayer interacted) {
        if (wasInteracted) return false;

        worldIn.broadcastImmediatelyExcluded(interacted.entityId(), new S2CAnimateObject(objectId));

        // keep track of this for later.
        this.interactedId = interacted.entityId();
        this.lastInteractionTime = System.currentTimeMillis();
        return wasInteracted = true;
    }

    /**
     * Destroy this object
     */
    public void destroy(ServerPlayer destroyer) {
        worldIn.broadcastImmediatelyExcluded(destroyer.entityId(), new S2CNetworkRemoveWorldObject(objectId));
        worldIn.removeWorldObject(this);
    }

    public void update() {

    }

}
