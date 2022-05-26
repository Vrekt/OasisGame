package me.vrekt.oasis.entity.npc.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import lunar.shared.entity.components.position.EntityPositionComponent;
import lunar.shared.entity.components.prop.EntityPropertiesComponent;
import me.vrekt.oasis.entity.component.EntityDialogComponent;

/**
 * Basic system for updating dialog tile animation states
 */
public final class EntityInteractableAnimationSystem extends IntervalSystem {

    private final Engine engine;

    private final Family family;
    private final ComponentMapper<EntityDialogComponent> dialogMapper;

    public EntityInteractableAnimationSystem(Engine engine) {
        super(.7f);
        this.engine = engine;

        this.family = Family.all(
                EntityDialogComponent.class,
                EntityPositionComponent.class,
                EntityPropertiesComponent.class).get();
        this.dialogMapper = ComponentMapper.getFor(EntityDialogComponent.class);
    }

    @Override
    protected void updateInterval() {
        for (Entity entity : engine.getEntitiesFor(family)) {
            // re-calculate distance to player, ONLY if they are in view
            final EntityDialogComponent component = dialogMapper.get(entity);
            if (component.isInView && component.drawDialogAnimationTile) {
                // TODO: This is going to be changing
                component.currentDialogFrame = component.currentDialogFrame >= 3 ? 1 : component.currentDialogFrame + 1;
            }
        }
    }
}
