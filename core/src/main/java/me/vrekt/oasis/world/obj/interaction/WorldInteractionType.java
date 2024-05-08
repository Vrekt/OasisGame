package me.vrekt.oasis.world.obj.interaction;

/**
 * A registry of all interactions within the game
 */
public enum WorldInteractionType {

    READABLE_SIGN("readable_sign"), CONTAINER("container");

    private final String type;
    // specifies this interaction has other interactions within
    private final Class<? extends InteractableWorldObject> poolingClass;

    WorldInteractionType(String type) {
        this.type = type;
        this.poolingClass = null;
    }

    WorldInteractionType(String type, Class<? extends InteractableWorldObject> poolingClass) {
        this.type = type;
        this.poolingClass = poolingClass;
    }

    public String getType() {
        return type;
    }

    public Class<? extends InteractableWorldObject> getPoolingClass() {
        return poolingClass;
    }

    /**
     * Assign a type based on this interaction type
     *
     * @param key     the key or {@code null} if none
     * @param manager the interaction manager
     * @return the type
     */
    public InteractableWorldObject assignType(String key, InteractionManager manager) {
        if (key != null) {
            // this interaction has child interactions
            return manager.getChildInteraction(this, key);
        } else {
            // this interaction is a generic pooled one
            return manager.getPooled(this);
        }
    }

    public static WorldInteractionType getType(String type) {
        return valueOf(type.toUpperCase());
    }

}
