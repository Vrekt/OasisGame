package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lunar.shared.drawing.Rotation;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.component.EntityDialogComponent;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable extends EntitySpeakable implements ResourceLoader {

    // describes the view/renderable stuff
    protected boolean inView;

    protected final OasisPlayerSP player;
    protected final OasisWorld gameWorldIn;
    protected final OasisGame game;
    protected EntityNPCType type;

    protected Rectangle bounds;

    protected boolean isEnemy;

    public EntityInteractable(String name, Vector2 position, OasisPlayerSP player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(player);

        setPosition(position.x, position.y, true);
        setEntityName(name);
        getWorlds().worldIn = worldIn;
        this.gameWorldIn = worldIn;
        this.player = player;
        this.game = game;
        this.type = type;
        this.rotation = Rotation.FACING_DOWN.ordinal();
    }

    public EntityNPCType getType() {
        return type;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void update(float v) {
        super.update(v);
        this.speakable = getDistanceFromPlayer() <= speakableDistance;
        setInView(this.inView);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        // draw generic texture
        if (currentRegionState != null) {
            batch.draw(currentRegionState, getX(), getY(), getWidth() * getScaling(), getHeight() * getScaling());
        } else if (currentTextureState != null) {
            batch.draw(currentTextureState, getX(), getY(), getWidth() * getScaling(), getHeight() * getScaling());
        }
        super.render(batch, delta);
    }

    public void setInView(boolean inView) {
        entity.getComponent(EntityDialogComponent.class).isInView = inView;
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
    public boolean isInView(Camera camera) {
        return inView = super.isInView(camera);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (currentTextureState != null) currentTextureState.dispose();
    }
}
