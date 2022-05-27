package me.vrekt.oasis.world.interaction;

import me.vrekt.oasis.world.interaction.interactions.LucidFruitTreeInteraction;

/**
 * All possible interactions within the game
 */
public enum InteractionType {

    LUCID_FRUIT_TREE(LucidFruitTreeInteraction.class);

    public static Class<Interaction> getInteractionFromName(String name) {
        return valueOf(name.toUpperCase()).interaction;
    }

    private final Class<Interaction> interaction;

    <T extends Interaction> InteractionType(Class<T> interaction) {
        this.interaction = (Class<Interaction>) interaction;
    }

    public Class<Interaction> getInteraction() {
        return interaction;
    }
}
