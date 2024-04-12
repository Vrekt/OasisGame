package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable extends EntitySpeakable implements ResourceLoader {

    protected final OasisPlayer player;
    protected final OasisWorld gameWorldIn;
    protected final OasisGame game;
    protected EntityNPCType type;

    protected Rectangle bounds;

    protected boolean isEnemy;

    public EntityInteractable(String name, Vector2 position, OasisPlayer player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(player);

        setPosition(position.x, position.y, true);
        setName(name);
        this.worldIn = worldIn;
        this.gameWorldIn = worldIn;
        this.player = player;
        this.game = game;
        this.type = type;
        setAngle(EntityRotation.DOWN.ordinal());
    }

    public EntityNPCType getType() {
        return type;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    public void setEnemy(boolean enemy) {
        isEnemy = enemy;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Set facing direction texture of this entity
     *
     * @param rotation the rotation desired
     */
    public void setFacingDirectionTexture(EntityRotation rotation) {
        switch (rotation) {
            case UP:
                currentRegionState = getRegion("facing_up");
                break;
            case DOWN:
                currentRegionState = getRegion("facing_down");
                break;
            case LEFT:
                currentRegionState = getRegion("facing_left");
                break;
            case RIGHT:
                currentRegionState = getRegion("facing_right");
                break;
        }
        setAngle(rotation.ordinal());
    }

    @Override
    public void update(float v) {
        super.update(v);
        speakable = getDistanceFromPlayer() <= speakableDistance;
        entity.getComponent(EntityDialogComponent.class).isInView = inView;
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (currentRegionState != null) {
            batch.draw(currentRegionState, getX(), getY(), getWidth() * getWorldScale(), getHeight() * getWorldScale());
        } else if (currentTextureState != null) {
            batch.draw(currentTextureState, getX(), getY(), getWidth() * getWorldScale(), getHeight() * getWorldScale());
        }
        super.render(batch, delta);
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public EntityInteractable asInteractable() {
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (currentTextureState != null) currentTextureState.dispose();
    }
}
