package me.vrekt.oasis.world.interaction.type;

import me.vrekt.oasis.world.interaction.Interaction;
import me.vrekt.oasis.world.interaction.plants.TreeInteraction;

public enum InteractionType {

    TREE(TreeInteraction.class);

    private final Class<? extends Interaction> classType;

    public static Class<? extends Interaction> getInteractionFromName(String name) {
        return valueOf(name.toUpperCase()).classType;
    }

    InteractionType(Class<? extends Interaction> classType) {
        this.classType = classType;
    }

    public Class<? extends Interaction> getClassType() {
        return classType;
    }
}
