package me.vrekt.oasis.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import lunar.shared.entity.player.LunarEntity;
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
    }

    public Entity(boolean initializeComponents) {
        super(initializeComponents);
    }

    @Override
    public boolean isInView(Camera camera) {
        inView = Renderable.isInViewExtended(view, getX(), getY(), camera.frustum);
        return inView;
    }
}
