package me.vrekt.oasis.world.obj.interaction;

import com.badlogic.gdx.maps.MapObject;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;
import me.vrekt.oasis.world.obj.interaction.impl.items.BreakableObjectInteraction;
import me.vrekt.oasis.world.obj.interaction.impl.items.MapItemInteraction;

/**
 * A registry of all interactions within the game
 */
public enum WorldInteractionType {

    READABLE_SIGN("readable_sign"),

    CONTAINER("container") {
        @Override
        public AbstractInteractableWorldObject create(GameWorld world, MapObject object) {
            return new OpenableContainerInteraction(object);
        }
    },

    MAP_ITEM("map_item") {
        @Override
        public AbstractInteractableWorldObject create(GameWorld world, MapObject object) {
            return new MapItemInteraction();
        }
    },

    BREAKABLE_OBJECT("breakable_object") {
        @Override
        public AbstractInteractableWorldObject create(GameWorld world, MapObject object) {
            return new BreakableObjectInteraction(world, object);
        }
    },

    WRYNN_LEVER("wrynn_lever"),
    NONE("none");

    private final String type;

    WorldInteractionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Create a generic non-defined object
     *
     * @param world  world
     * @param object map object
     * @return the object
     */
    public AbstractInteractableWorldObject getKeyless(GameWorld world, MapObject object) {
        return this.create(world, object);
    }

    /**
     * Get an interaction that is keyed - pre-defined.
     *
     * @param key     key
     * @param manager manager
     * @return the object
     */
    public AbstractInteractableWorldObject getKeyed(String key, InteractionManager manager) {
        return manager.getInteraction(this, key);
    }

    /**
     * Parse the interaction type string
     *
     * @param type the type
     * @return the type
     */
    public static WorldInteractionType ofOrNone(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException exception) {
            GameLogging.exceptionThrown("WorldInteractionTypeParse", "No interaction type: %s", exception, type);
            return NONE;
        }
    }

    public AbstractInteractableWorldObject create(GameWorld world, MapObject object) {
        return null;
    }

}
