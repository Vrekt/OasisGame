package me.vrekt.oasis.entity.npc.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import lunar.shared.entity.components.position.EntityPositionComponent;
import lunar.shared.entity.components.prop.EntityPropertiesComponent;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;

/**
 * Basic system for updating dialog tile animation states
 */
public final class EntityInteractableAnimationSystem extends IntervalSystem {

    private final Engine engine;
    private final OasisPlayerSP player;

    private final Family family;
    private final ComponentMapper<EntityPositionComponent> positionMapper;
    private final ComponentMapper<EntityDialogComponent> dialogMapper;

    public EntityInteractableAnimationSystem(OasisPlayerSP player, Engine engine) {
        super(.7f);
        this.engine = engine;
        this.player = player;

        this.family = Family.all(
                EntityDialogComponent.class,
                EntityPositionComponent.class,
                EntityPropertiesComponent.class).get();
        this.positionMapper = ComponentMapper.getFor(EntityPositionComponent.class);
        this.dialogMapper = ComponentMapper.getFor(EntityDialogComponent.class);
    }

    @Override
    protected void updateInterval() {
        for (Entity entity : engine.getEntitiesFor(family)) {
            // re-calculate distance to player, ONLY if they are in view
            final EntityDialogComponent component = dialogMapper.get(entity);
            if (component.isInView && component.drawDialogAnimationTile) {
                // TODO: This is going to be changing
                component.distanceFromPlayer = player.getPosition().dst2(positionMapper.get(entity).position);
                component.currentDialogFrame = component.currentDialogFrame >= 3 ? 1 : component.currentDialogFrame + 1;
            }
        }
    }
}
