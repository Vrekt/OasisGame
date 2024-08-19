package me.vrekt.oasis.network.server.world.obj;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import me.vrekt.oasis.network.server.entity.player.ServerPlayer;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.shared.packet.client.C2SInteractWithObject;
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
    private final Vector2 size;
    private final String key;

    private int interactedId;
    private long lastInteractionTime;
    private boolean wasInteracted;

    private MapObject mapData;

    public ServerWorldObject(ServerWorld worldIn, AbstractInteractableWorldObject object) {
        Preconditions.checkNotNull(worldIn);
        Preconditions.checkNotNull(object);

        this.worldIn = worldIn;
        this.type = object.getType();
        this.position = object.getPosition();
        this.size = object.getSize();
        this.key = object.getKey();
        this.objectId = object.objectId();
        this.mapData = object.object();
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
     * @return the size
     */
    public Vector2 size() {
        return size;
    }

    /**
     * @return the key
     */
    public String key() {
        return key;
    }

    /**
     * @return if this object was interacted with
     */
    public boolean wasInteracted() {
        return wasInteracted;
    }

    /**
     * @return the map object data
     */
    public MapObject mapData() {
        return mapData;
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
     * Mark ownership of this object
     *
     * @param owner owner
     */
    public void markOwnership(ServerPlayer owner) {
        wasInteracted = true;
        this.interactedId = owner.entityId();
    }

    /**
     * Destroy this object
     */
    public void playerDestroyed(ServerPlayer destroyer) {
        worldIn.broadcastImmediatelyExcluded(destroyer.entityId(), new S2CNetworkRemoveWorldObject(objectId));
        worldIn.removeWorldObject(this);
    }

    /**
     * Destroy this object
     */
    public void destroyed() {
        worldIn.broadcastImmediately(new S2CNetworkRemoveWorldObject(objectId));
        worldIn.removeWorldObject(this);
    }

    public void update() {

    }

    /**
     * Check if the provided type is a valid interaction for this object
     * This method should be overridden for special objects
     *
     * @param type the type
     * @return {@code true} if so
     */
    public boolean isValidInteraction(C2SInteractWithObject.InteractionType type) {
        return true;
    }

}
