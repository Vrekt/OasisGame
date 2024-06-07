package me.vrekt.oasis.world.obj.interaction;

import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Handles registering world interactions and their providers
 */
public final class InteractionManager {

    private final Map<WorldInteractionType, ChildInteractionRegistry> interactionRegistry = new HashMap<>();

    /**
     * Register an interaction that has a base type but differing implementation
     *
     * @param type     the base type
     * @param key      the key of the specific implementation
     * @param provider the provider for that implementation
     */
    public void registerInteraction(WorldInteractionType type, String key, Supplier<AbstractInteractableWorldObject> provider) {
        final ChildInteractionRegistry r = getOrCreateRegistry(type);
        r.registry.put(key, provider);
    }

    /**
     * Get the registry or create if none
     *
     * @param type the base type
     * @return the new or existing registry
     */
    private ChildInteractionRegistry getOrCreateRegistry(WorldInteractionType type) {
        return interactionRegistry.computeIfAbsent(type, c -> new ChildInteractionRegistry());
    }

    /**
     * Get an interaction
     *
     * @param type the type
     * @param key  the subtype
     * @return the object
     */
    public AbstractInteractableWorldObject getInteraction(WorldInteractionType type, String key) {
        return interactionRegistry.get(type).registry.get(key).get();
    }

    private static final class ChildInteractionRegistry {
        private final Map<String, Supplier<AbstractInteractableWorldObject>> registry = new HashMap<>();
    }

}
