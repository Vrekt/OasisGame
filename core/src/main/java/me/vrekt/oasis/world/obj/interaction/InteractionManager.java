package me.vrekt.oasis.world.obj.interaction;

import com.badlogic.gdx.utils.Pools;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Handles registering world interactions and their providers
 */
public final class InteractionManager {

    private final Map<WorldInteractionType, ChildInteractionRegistry> childRegistry = new HashMap<>();

    /**
     * Register an interaction that has a base type but differing implementation
     *
     * @param type     the base type
     * @param childKey the key of the specific implementation
     * @param provider the provider for that implementation
     */
    public void registerChildInteraction(WorldInteractionType type, String childKey, Supplier<InteractableWorldObject> provider) {
        final ChildInteractionRegistry r = getOrCreateRegistry(type);
        r.registry.put(childKey, provider);
    }

    /**
     * Get the registry or create if none
     *
     * @param type the base type
     * @return the new or existing registry
     */
    private ChildInteractionRegistry getOrCreateRegistry(WorldInteractionType type) {
        return childRegistry.computeIfAbsent(type, c -> new ChildInteractionRegistry());
    }

    /**
     * Get a child interaction
     *
     * @param type     the type
     * @param childKey the subtype
     * @return the object
     */
    public InteractableWorldObject getChildInteraction(WorldInteractionType type, String childKey) {
        return childRegistry.get(type).registry.get(childKey).get();
    }

    /**
     * Get a pooled interaction object
     *
     * @param type the type
     * @return the object
     */
    public InteractableWorldObject getPooled(WorldInteractionType type) {
        return Pools.obtain(type.getPoolingClass());
    }

    private static final class ChildInteractionRegistry {
        private final Map<String, Supplier<InteractableWorldObject>> registry = new HashMap<>();
    }

}
