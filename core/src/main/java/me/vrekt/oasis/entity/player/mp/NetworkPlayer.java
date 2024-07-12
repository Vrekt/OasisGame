package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.interior.InteriorWorldType;

/**
 * Represents any player over the network
 */
public final class NetworkPlayer extends AbstractNetworkPlayer implements ResourceLoader {

    private boolean enteringInterior;
    private InteriorWorldType interiorEntering;
    private float fadingAnimationEnteringAlpha = 1.0f;

    private EntityAnimationComponent animationComponent;
    private TextureRegion activeTexture;

    private float nametagRenderWidth;
    private boolean renderNametag;

    public NetworkPlayer(GameWorld world) {
        super(world);

        disableCollision();
        dynamicSize = false;
    }

    public void setRenderNametag(boolean renderNametag) {
        this.renderNametag = renderNametag;
    }

    public boolean shouldRenderNametag() {
        return renderNametag;
    }

    /**
     * Transfer this player to another world, while they are visible.
     * This in turn will render a "special" effect while the player is entering
     * Otherwise if not visible, transfer now.
     *
     * @param into into interior
     */
    public void transferPlayerToWorldVisible(InteriorWorldType into) {
        enteringInterior = true;
        interiorEntering = into;
    }

    /**
     * Transfer this player immediately
     *
     * @param type type
     */
    public void transferImmediately(InteriorWorldType type) {
        this.interiorEntering = type;
        transfer();
    }

    /**
     * Transfer this player into the interior
     */
    public void transfer() {
        final GameWorldInterior interior = worldIn.findInteriorByType(interiorEntering);
        if (interior != null) {
            // destroy our previous body and create a new one for this interior
            worldIn.removePlayerTemporarily(this);

            setPosition(interior.worldOrigin().x, interior.worldOrigin().y, false);
            createCircleBody(interior.boxWorld(), true);

            interior.spawnPlayerInWorld(this);
            this.worldIn = interior;
        } else {
            GameLogging.warn(this, "Failed to find the interior a player joined! type=%s", type);
        }
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

        if (previousRotation != rotation) setIdleRegionState();
        previousRotation = rotation;
    }

    @Override
    public void updateNetworkPosition(float x, float y, float angle) {
        super.updateNetworkPosition(x, y, angle);
        rotation = EntityRotation.values()[(int) angle];
    }

    @Override
    public void updateNetworkVelocity(float x, float y, float angle) {
        super.updateNetworkVelocity(x, y, angle);
        rotation = EntityRotation.values()[(int) angle];
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.animateMoving(rotation, delta), getScaledWidth(), getScaledHeight());
        } else {
            if (activeTexture != null) {
                draw(batch, activeTexture, getScaledWidth(), getScaledHeight());
            }
        }
    }

    private void draw(SpriteBatch batch, TextureRegion region, float width, float height) {
        if (enteringInterior) {
            batch.setColor(1, 1, 1, fadingAnimationEnteringAlpha);
            fadingAnimationEnteringAlpha -= Gdx.graphics.getDeltaTime() * 2f;

            // we are ready to be transferred since the visual animation completed
            if (fadingAnimationEnteringAlpha <= 0.0f) transfer();
        }

        if (body != null) {
            batch.draw(region, getInterpolatedPosition().x, getInterpolatedPosition().y, width, height);
        }

        if (enteringInterior) {
            if (fadingAnimationEnteringAlpha <= 0.0f) enteringInterior = false;
            batch.setColor(Color.WHITE);
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
        switch (rotation) {
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
    public void createCircleBody(World world, boolean flipped) {
        super.createCircleBody(world, flipped);
        body.setUserData(this);
    }

}
