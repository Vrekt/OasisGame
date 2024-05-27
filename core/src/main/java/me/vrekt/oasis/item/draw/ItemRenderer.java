package me.vrekt.oasis.item.draw;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.item.Item;

/**
 * An item renderer
 */
public abstract class ItemRenderer implements Drawable {

    private static final float ANGLE_UP_ROTATION = 0.0f;
    private static final float ANGLE_DOWN_ROTATION = 90.0f;
    private static final float ANGLE_LEFT_ROTATION = 90.0f;
    private static final float ANGLE_RIGHT_ROTATION = 0.0f;

    protected final TextureRegion region;
    protected final Item item;

    protected final Vector2 position
            = new Vector2();

    protected float rotation;

    public ItemRenderer(TextureRegion region, Item item) {
        this.region = region;
        this.item = item;
    }

    public TextureRegion region() {
        return region;
    }

    /**
     * Update the item rotation based on player rotation
     */
    public abstract void updateItemRotation(Vector2 position, EntityRotation rotation);

    protected void applyStaticRotation(EntityRotation rotation) {
        switch (rotation) {
            case UP -> this.rotation = ANGLE_UP_ROTATION;
            case DOWN -> this.rotation = ANGLE_DOWN_ROTATION;
            case LEFT -> this.rotation = ANGLE_LEFT_ROTATION;
            case RIGHT -> this.rotation = ANGLE_RIGHT_ROTATION;
        }
    }

}
