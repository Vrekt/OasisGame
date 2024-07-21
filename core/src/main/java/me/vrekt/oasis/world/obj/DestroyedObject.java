package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * Represents an object that was destroyed, for saving purposes.
 */
public final class DestroyedObject {

    private String key;
    private WorldInteractionType type;
    private Vector2 position;

    public DestroyedObject(String key, WorldInteractionType type, Vector2 position) {
        this.key = key;
        this.type = type;
        this.position = position;
    }

    public DestroyedObject(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public WorldInteractionType type() {
        return type;
    }

    public Vector2 position() {
        return position;
    }
}
