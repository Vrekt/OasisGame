package me.vrekt.oasis.world.obj.interaction;

import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * A registry of all interactions within the game
 */
public enum WorldInteractionType {

    READABLE_SIGN("readable_sign"), CONTAINER("container"), ITEM_DROP("item_drop"), MAP_ITEM("map_item");

    private final String type;

    WorldInteractionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public AbstractInteractableWorldObject get(String key, InteractionManager manager) {
        return manager.getInteraction(this, key);
    }

    public static WorldInteractionType of(String type) {
        return valueOf(type.toUpperCase());
    }

}
