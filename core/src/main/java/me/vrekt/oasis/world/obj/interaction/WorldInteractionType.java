package me.vrekt.oasis.world.obj.interaction;

import me.vrekt.oasis.world.obj.interaction.tutorial.TutorialTreeInteraction;

public enum WorldInteractionType {

    LUCID_FRUIT_TREE_TUTORIAL(TutorialTreeInteraction.class);

    private final Class<? extends InteractableWorldObject> classType;

    public static Class<? extends InteractableWorldObject> getInteractionFromName(String name) {
        return valueOf(name.toUpperCase()).classType;
    }

    WorldInteractionType(Class<? extends InteractableWorldObject> classType) {
        this.classType = classType;
    }

    public Class<? extends InteractableWorldObject> getClassType() {
        return classType;
    }

}
