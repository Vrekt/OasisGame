package me.vrekt.oasis.entity.component.status;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.graphics.Drawable;

/**
 * Basic status
 */
public abstract class EntityStatus implements Drawable {

    protected final GameEntity entity;
    protected final int id;
    protected boolean isPostRender;

    public EntityStatus(GameEntity entity, int id) {
        this.entity = entity;
        this.id = id;
    }

    /**
     * @return unique ID
     */
    public int id() {
        return id;
    }

    /**
     * @return {@code true} if this should be post rendered.
     */
    public boolean isPostRender() {
        return isPostRender;
    }

    /**
     * Update this status
     *
     * @param delta delta
     */
    public abstract void update(float delta);

    @Override
    public void render(SpriteBatch batch, float delta) {

    }

    public void postRender(SpriteBatch batch, Camera worldCamera, Camera guiCamera) {

    }

    /**
     * Exit the status of this state.
     */
    public void exitStatus() {

    }

}
