package me.vrekt.oasis.world.obj.interaction;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.interactable.EntitySpeakable;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.KeyableAbstractInteractableMouseObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Handles registering world interactions and their providers
 */
public final class InteractionManager {

    private final Map<WorldInteractionType, ChildInteractionRegistry> interactionRegistry = new HashMap<>();

    private final EnumMap<Interaction, InteractionState<?>> interactions = new EnumMap<>(Interaction.class);

    private Interaction activeInteractionType;
    private InteractionState<?> activeInteractionState;

    public InteractionManager() {
        interactions.put(Interaction.ENTITY, new InteractionState<GameEntity>());
        interactions.put(Interaction.INTERIOR, new InteractionState<GameWorldInterior>());
        interactions.put(Interaction.OBJECT, new InteractionState<OpenableContainerInteraction>());
        interactions.put(Interaction.LOCKPICK, new InteractionState<Void>());
    }

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
     * Handle when the player presses the E key
     *
     * @param world the world in
     */
    public boolean handleInteractionKeyPress(GameWorld world) {
        if (activeInteractionType != null) {
            if (activeInteractionType == Interaction.INTERIOR) {
                final InteractionState<GameWorldInterior> state = acquireState(Interaction.INTERIOR);
                state.interactionObject.attemptEnter();

                hideActiveStateIf(Interaction.INTERIOR);
                return true;
            } else if (activeInteractionType == Interaction.ENTITY) {
                final InteractionState<GameEntity> state = acquireState(Interaction.ENTITY);
                if (state.interactionObject instanceof EntitySpeakable speakable) {
                    speakable.speak();
                }

                hideActiveStateIf(Interaction.ENTITY);
                return true;
            } else if (activeInteractionType == Interaction.OBJECT) {
                final InteractionState<AbstractInteractableWorldObject> state = acquireState(Interaction.OBJECT);
                if (!state.interactionObject.wasInteractedWith()) {
                    state.interactionObject.interact();
                }
            }
        }
        return false;
    }

    /**
     * Update objects around us and choose which to display for interaction
     *
     * @param world the world in
     */
    public void updateNearestInteractions(GameWorld world) {
        EntityInteractable nearestEntity = null;
        float nearestEntityDistance = OasisGameSettings.ENTITY_NEARBY_DISTANCE;

        for (GameEntity entity : world.nearbyEntities().values()) {
            if (entity.type().interactable()) {
                if (entity.asInteractable().isSpeakingTo()) continue;
            } else {
                continue;
            }

            if (entity.getDistanceFromPlayer() < nearestEntityDistance) {
                nearestEntityDistance = entity.getDistanceFromPlayer();
                nearestEntity = entity.asInteractable();
            }
        }

        KeyableAbstractInteractableMouseObject nearestObject = null;
        float nearestObjectDistance = 12.0f;
        for (AbstractInteractableWorldObject worldObject : world.interactableWorldObjects().values()) {
            if (!worldObject.isKeyable()
                    || !worldObject.isEnabled()
                    || !worldObject.isInInteractionRange()
                    || worldObject.wasInteractedWith()) {
                continue;
            }

            if (worldObject.distanceToPlayer() < nearestObjectDistance) {
                nearestObjectDistance = worldObject.distanceToPlayer();
                nearestObject = (KeyableAbstractInteractableMouseObject) worldObject;
            }
        }

        updateInteractionStates(nearestEntity, nearestEntityDistance, nearestObject, nearestObjectDistance);
    }

    /**
     * Update the interaction states, display or update them.
     *
     * @param entity         the entity if any
     * @param entityDistance closest entity distance
     * @param object         the object if any
     * @param objectDistance closest object distance
     */
    private void updateInteractionStates(GameEntity entity, float entityDistance, AbstractInteractableWorldObject object, float objectDistance) {
        final boolean hasEntity = entity != null && entityDistance <= 4.0f;
        final boolean hasObject = object != null && object.isInInteractionRange();

        // entity has priority in this case.
        if (hasEntity && ((hasObject && (entityDistance < objectDistance) || object == null))) {
            final InteractionState<GameEntity> state = acquireState(Interaction.ENTITY);
            if (!state.isVisible) {
                state.isVisible = true;
                state.interactionObject = entity;
                GameManager.gui().getInteractionsComponent().populateSpeakingComponent();

                hideOthers(Interaction.ENTITY);
                updateActiveInteraction(Interaction.ENTITY, state);
            }
        } else if (hasObject && (objectDistance < entityDistance || entity == null)) {
            // object has priority in this case
            final InteractionState<AbstractInteractableWorldObject> state = acquireState(Interaction.OBJECT);

            if (!state.isVisible) {
                state.isVisible = true;
                state.interactionObject = object;
                GameManager.gui().getInteractionsComponent().populateWorldObjectComponent(object.getInteractionText());

                hideOthers(Interaction.OBJECT);
                updateActiveInteraction(Interaction.OBJECT, state);
            } else if (state.interactionObject.getType() != object.getType()) {
                state.interactionObject = object;
                GameManager.gui().getInteractionsComponent().populateWorldObjectComponent(object.getInteractionText());
            }
        } else {
            // hide active state otherwise
            if (activeInteractionType == Interaction.OBJECT || activeInteractionType == Interaction.ENTITY) {
                activeInteractionState.isVisible = false;
                activeInteractionState = null;
                activeInteractionType = null;
                GameManager.gui().getInteractionsComponent().hide();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T acquireState(Interaction interaction) {
        return (T) interactions.get(interaction);
    }

    private void hideOthers(Interaction exclusion) {
        interactions.forEach((i, state) -> {
            if (exclusion == null || i != exclusion) state.isVisible = false;
        });
    }


    /**
     * Update the active interaction
     *
     * @param interaction interaction
     * @param state       state
     */
    private void updateActiveInteraction(Interaction interaction, InteractionState<?> state) {
        this.activeInteractionType = interaction;
        this.activeInteractionState = state;
    }

    /**
     * Populate the enter interaction GUI components
     *
     * @param interior the interior
     */
    public void populateEnterInteraction(GameWorldInterior interior) {
        final InteractionState<GameWorldInterior> state = acquireState(Interaction.INTERIOR);
        state.interactionObject = interior;

        updateActiveInteraction(Interaction.INTERIOR, state);
        activeInteractionState.isVisible = true;

        GameManager.gui().getInteractionsComponent().populateEnterInteraction();
    }


    /**
     * Populate the enter interaction GUI components
     */
    public void populateLockpickInteraction() {
        updateActiveInteraction(Interaction.LOCKPICK, acquireState(Interaction.LOCKPICK));
        activeInteractionState.isVisible = true;

        GameManager.gui().getInteractionsComponent().populateLockpickInteraction();
    }

    /**
     * @param interaction the interaction to check
     * @return {@code true} if the interaction is visible.
     */
    public boolean isStateVisible(Interaction interaction) {
        return interactions.get(interaction).isVisible;
    }

    /**
     * Hide interaction if it is the active state
     *
     * @param interaction the interaction
     */
    public void hideActiveStateIf(Interaction interaction) {
        if (activeInteractionType == interaction) {
            activeInteractionState.isVisible = false;
            activeInteractionState = null;
            activeInteractionType = null;

            GameManager.gui().getInteractionsComponent().hide();
        }
    }

    private static final class ChildInteractionRegistry {
        private final Map<String, Supplier<AbstractInteractableWorldObject>> registry = new HashMap<>();
    }

    /**
     * State of an interaction, showing or not, etc.
     */
    private static final class InteractionState<T> {

        boolean isVisible;
        T interactionObject;
    }

}
