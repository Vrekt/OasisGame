package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.animation.EntityAnimationBuilder;
import me.vrekt.oasis.entity.component.animation.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.utility.ResourceLoader;
import me.vrekt.oasis.world.GameWorld;

/**
 * Represents any player over the network
 */
public final class NetworkPlayer extends AbstractNetworkPlayer implements ResourceLoader {

    private EntityAnimationComponent animationComponent;
    private TextureRegion activeTexture;

    private float nametagRenderWidth;
    private boolean renderNametag;

    private EntityRotation lastRotation = EntityRotation.UP;
    private EntityRotation entityRotation = EntityRotation.UP;

    public NetworkPlayer(GameWorld world) {
        super(world);

        setInterpolatePosition(true);
        setSnapToPositionIfDesynced(false);
        setDesyncDistanceToInterpolate(2.5f);

        disableCollision();
        dynamicSize = false;
    }

    public void setRenderNametag(boolean renderNametag) {
        this.renderNametag = renderNametag;
    }

    public boolean shouldRenderNametag() {
        return renderNametag;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.nametagRenderWidth = GameManager.getGuiManager().getStringWidth(name);
    }

    @Override
    public void load(Asset asset) {
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        setSize(24, 28, OasisGameSettings.SCALE);
        getTextureComponent().add("character_a_walking_up_idle", asset.get("character_a_walking_up_idle"));
        getTextureComponent().add("character_a_walking_down_idle", asset.get("character_a_walking_down_idle"));
        getTextureComponent().add("character_a_walking_left_idle", asset.get("character_a_walking_left_idle"));
        getTextureComponent().add("character_a_walking_right_idle", asset.get("character_a_walking_right_idle"));
        activeTexture = getTextureComponent().get("character_a_walking_up_idle");

        final EntityAnimationBuilder builder = new EntityAnimationBuilder(asset)
                .moving(EntityRotation.UP, 0.35f, "character_a_walking_up", 2)
                .add(animationComponent)
                .moving(EntityRotation.DOWN, 0.35f, "character_a_walking_down", 2)
                .add(animationComponent)
                .moving(EntityRotation.LEFT, 0.35f, "character_a_walking_left", 2)
                .add(animationComponent)
                .moving(EntityRotation.RIGHT, 0.35f, "character_a_walking_right", 2)
                .add(animationComponent);
        builder.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (lastRotation != entityRotation) {
            setIdleRegionState();
        }

        lastRotation = entityRotation;
    }

    @Override
    public void updatePositionFromNetwork(float x, float y, float angle) {
        super.updatePositionFromNetwork(x, y, angle);
        entityRotation = EntityRotation.values()[(int) angle];
    }

    @Override
    public void updateVelocityFromNetwork(float x, float y, float angle) {
        super.updateVelocityFromNetwork(x, y, angle);
        entityRotation = EntityRotation.values()[(int) angle];
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.animateMoving(entityRotation, delta), getScaledWidth(), getScaledHeight());
        } else {
            if (activeTexture != null) {
                draw(batch, activeTexture, getScaledWidth(), getScaledHeight());
            }
        }
    }

    /**
     * Render the name tag of this player
     *
     * @param font           the font to use
     * @param batch          the batch to draw with
     * @param screenPosition screen position
     */
    public void renderNametag(BitmapFont font, Batch batch, Vector3 screenPosition) {
        font.draw(batch, name(), screenPosition.x - nametagRenderWidth, screenPosition.y);
    }

    private void setIdleRegionState() {
        switch (entityRotation) {
            case UP:
                activeTexture = getTextureComponent().get("character_a_walking_up_idle");
                break;
            case DOWN:
                activeTexture = getTextureComponent().get("character_a_walking_down_idle");
                break;
            case LEFT:
                activeTexture = getTextureComponent().get("character_a_walking_left_idle");
                break;
            case RIGHT:
                activeTexture = getTextureComponent().get("character_a_walking_right_idle");
                break;
        }
    }

    @Override
    public void createBoxBody(World world) {
        super.createBoxBody(world);
        body.setUserData(this);
    }
}
