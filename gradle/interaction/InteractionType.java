package me.vrekt.oasis.world.interaction;

import me.vrekt.oasis.world.interaction.interactions.LucidFruitTreeInteraction;

/**
 * All possible interactions within the game
 */
public enum InteractionType {

    LUCID_FRUIT_TREE(LucidFruitTreeInteraction.class);

    public static Class<WorldInteraction> getInteractionFromName(String name) {
        return valueOf(name.toUpperCase()).interaction;
    }

    private final Class<WorldInteraction> interaction;

    <T extends WorldInteraction> InteractionType(Class<T> interaction) {
        this.interaction = (Class<WorldInteraction>) interaction;
    }

    public Class<WorldInteraction> getInteraction() {
        return interaction;
    }
}
