package me.vrekt.oasis.world.obj.interaction;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.world.GameWorldInterior;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.utility.Interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Handles registering world interactions and their providers
 */
public final class InteractionManager {

    private final Map<WorldInteractionType, ChildInteractionRegistry> interactionRegistry = new HashMap<>();

    // keybinding interactions
    private GameEntity entityToInteractWith;
    private GameWorldInterior interiorToEnter;
    private Interaction active;

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

    /**
     * Show the enter interaction
     */
    public void showEnterInteraction(GameWorldInterior into) {
        GameManager.game().getGuiManager().getInteractionsComponent().showEnterInteraction();
        active = Interaction.ENTER;
        interiorToEnter = into;
    }

    /**
     * Show the speaking interaction
     */
    public void showSpeakingInteraction(GameEntity entity) {
        GameManager.game().getGuiManager().getInteractionsComponent().showSpeakingInteraction();
        active = Interaction.SPEAK;
        entityToInteractWith = entity;
    }

    /**
     * Show the lockpicking interaction
     */
    public void showLockpickingInteraction() {
        GameManager.game().getGuiManager().getInteractionsComponent().showLockpickInteraction();
        active = Interaction.LOCKPICK;
    }

    /**
     * Hide all.
     */
    public void hideInteractions() {
        GameManager.game().getGuiManager().getInteractionsComponent().hide();
        active = null;
    }

    public boolean is(Interaction interaction) {
        return active != null && active == interaction;
    }

    public boolean any() {
        return active() != null;
    }

    /**
     * @return the active interaction
     */
    public Interaction active() {
        return active;
    }

    /**
     * @return the entity that provided the interaction prompt
     */
    public GameEntity entityToInteractWith() {
        return entityToInteractWith;
    }

    /**
     * @return the interior that provided the interaction prompt
     */
    public GameWorldInterior interiorToEnter() {
        return interiorToEnter;
    }

    private static final class ChildInteractionRegistry {
        private final Map<String, Supplier<AbstractInteractableWorldObject>> registry = new HashMap<>();
    }

}
