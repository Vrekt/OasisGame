package me.vrekt.oasis.world.obj.interaction;

/**
 * A registry of all interactions within the game
 */
public enum WorldInteractionType {

    READABLE_SIGN("readable_sign"), CONTAINER("container");

    private final String type;

    WorldInteractionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public InteractableWorldObject get(String key, InteractionManager manager) {
        return manager.getInteraction(this, key);
    }

    public static WorldInteractionType of(String type) {
        return valueOf(type.toUpperCase());
    }

}
