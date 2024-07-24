package me.vrekt.shared.packet.server.obj;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * Basic network object.
 */
public final class WorldNetworkObject {

    private final WorldInteractionType type;
    private final String key;
    private final Vector2 position;
    private final Vector2 size;
    private final int objectId;

    private MapObject object;

    public WorldNetworkObject(WorldInteractionType type,
                              String key,
                              Vector2 position,
                              Vector2 size,
                              int objectId) {
        this.type = type;
        this.key = key;
        this.position = position;
        this.size = size;
        this.objectId = objectId;
    }

    public WorldNetworkObject(WorldInteractionType type,
                              String key,
                              Vector2 position,
                              Vector2 size,
                              int objectId,
                              MapObject object) {
        this.type = type;
        this.key = key;
        this.position = position;
        this.size = size;
        this.objectId = objectId;
        this.object = object;
    }

    /**
     * @return interaction type
     */
    public WorldInteractionType type() {
        return type;
    }

    /**
     * @return key or {@code null} if none
     */
    public String key() {
        return key;
    }

    /**
     * @return position including any offsets.
     */
    public Vector2 position() {
        return position;
    }

    /**
     * @return size, already scaled.
     */
    public Vector2 size() {
        return size;
    }

    /**
     * @return ID
     */
    public int objectId() {
        return objectId;
    }

    /**
     * @return the object, or {@code null} if none
     */
    public MapObject mapObject() {
        return object;
    }
}
