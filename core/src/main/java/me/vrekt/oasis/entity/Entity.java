package me.vrekt.oasis.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import lunar.shared.entity.player.LunarEntity;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.graphics.Renderable;

/**
 * Represents a basic entity within Oasis
 */
public abstract class Entity extends LunarEntity implements Renderable {

    // describes the view/renderable stuff
    protected final Vector3 view = new Vector3(0, 0, 0);
    protected boolean inView;

    public Entity(com.badlogic.ashley.core.Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
        entity.add(new EntityDialogComponent());
    }

    public Entity(boolean initializeComponents) {
        super(initializeComponents);
        entity.add(new EntityDialogComponent());
    }

    public boolean isInteractable() {
        return false;
    }

    public EntityInteractable asInteractable() {
        return null;
    }

    public void setDistanceToPlayer(float distance) {
        entity.getComponent(EntityDialogComponent.class).distanceFromPlayer = distance;
    }

    public float getDistanceFromPlayer() {
        return entity.getComponent(EntityDialogComponent.class).distanceFromPlayer;
    }

    @Override
    public boolean isInView(Camera camera) {
        inView = Renderable.isInViewExtended(view, getX(), getY(), camera.frustum);
        return inView;
    }
}
