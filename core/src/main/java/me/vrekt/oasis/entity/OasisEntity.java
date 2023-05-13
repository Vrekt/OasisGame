package me.vrekt.oasis.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import lunar.shared.entity.AbstractLunarEntity;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;

/**
 * Represents a basic entity within Oasis
 */
public abstract class OasisEntity extends AbstractLunarEntity implements Viewable, Drawable {

    protected boolean isNearby;
    protected NinePatch gradient;

    public OasisEntity(boolean initializeComponents) {
        super(initializeComponents);

        setHealth(100.0f);
    }

    /**
     * Render the health bar of this entity
     * Usually only used for enemies.
     *
     * @param batch the batch
     */
    public void renderHealthBar(SpriteBatch batch) {
        if (getHealth() <= 0 || gradient == null) return;

        final float width = (getHealth() / 100.0f * getWidth()) * OasisGameSettings.SCALE;
        gradient.draw(batch, getX(), getY() + (getScaledHeight() + 0.1f), width, 3.0f * OasisGameSettings.SCALE);
    }

    /**
     * Check if this entity was clicked on
     *
     * @param clicked the vector3 click
     * @return {@code  true} if so
     */
    public boolean isMouseInEntityBounds(Vector3 clicked) {
        return clicked.x > getX() && clicked.x < (getX() + getScaledWidth()) && clicked.y > getY() && clicked.y < (getY() + getScaledHeight());
    }

    public boolean isInteractable() {
        return false;
    }

    public EntityInteractable asInteractable() {
        return null;
    }

    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }

    public void setDistanceToPlayer(float distance) {
        entity.getComponent(EntityDialogComponent.class).distanceFromPlayer = distance;
    }

    public float getDistanceFromPlayer() {
        return entity.getComponent(EntityDialogComponent.class).distanceFromPlayer;
    }

    @Override
    public boolean isInView(Camera camera) {
        return camera.frustum.pointInFrustum(getX(), getY(), 0.0f);
    }
}
