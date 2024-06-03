package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.*;
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
    private final Vector3 worldPosition = new Vector3();
    private final Vector3 screenPosition = new Vector3();

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

        final GlyphLayout fontLayout = new GlyphLayout(GameManager.getGuiManager().getSmallFont(), name);
        this.nametagRenderWidth = (fontLayout.width / 6f) * OasisGameSettings.SCALE;
        fontLayout.reset();
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
     * @param font        the font to use
     * @param batch       the batch to draw with
     * @param worldCamera the game world camera
     * @param guiCamera   the gui camera
     */
    public void renderNametag(BitmapFont font, Batch batch, Camera worldCamera, Camera guiCamera) {
        worldPosition.set(worldCamera.project(worldPosition.set(getInterpolatedPosition().x - nametagRenderWidth, getInterpolatedPosition().y + 2.25f, 0.0f)));
        screenPosition.set(guiCamera.project(worldPosition));
        font.draw(batch, name(), screenPosition.x, screenPosition.y);
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
