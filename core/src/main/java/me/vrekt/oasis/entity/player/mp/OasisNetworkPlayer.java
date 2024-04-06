package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.OasisNetworkEntityPlayer;
import me.vrekt.oasis.item.ItemEquippable;
import me.vrekt.oasis.item.ItemRegistry;

/**
 * Represents any player over the network
 */
public final class OasisNetworkPlayer extends OasisNetworkEntityPlayer implements ResourceLoader {

    private EntityAnimationComponent animationComponent;

    private float nametagRenderWidth;
    private final Vector3 worldPosition = new Vector3();
    private final Vector3 screenPosition = new Vector3();

    private boolean renderNametag;

    private ItemEquippable equippedItem;

    private EntityRotation lastRotation = EntityRotation.UP;
    private EntityRotation entityRotation = EntityRotation.UP;

    public OasisNetworkPlayer(boolean initializeComponents) {
        super(initializeComponents);

        setInterpolatePosition(true);
        setSnapToPositionIfDesynced(true);
        setDesyncDistanceToInterpolate(2.5f);
        setInterpolationAlpha(1.0f);
        disablePlayerCollision(true);
    }

    public void setRenderNametag(boolean renderNametag) {
        this.renderNametag = renderNametag;
    }

    public boolean shouldRenderNametag() {
        return renderNametag;
    }

    public void setEquippingItem(int itemId) {
        if (itemId == -1) {
            // player has stopped equipping an item
            this.equippedItem = null;
        } else {
            // TODO
            // this.equippedItem = (ItemEquippable) ItemRegistry.createItemFromId(itemId);
            // this.equippedItem.load(GameManager.getAssets());
        }
    }

    public void setSwingingItem(int id) {
        // TODO
        // if (equippedItem == null || equippedItem.getItemId() != id) return;
        // ((ItemWeapon) equippedItem).swingItem();
    }

    @Override
    public void setName(String name) {
        super.setName(name);

        final GlyphLayout fontLayout = new GlyphLayout(GameManager.getGui().getSmall(), getName());
        this.nametagRenderWidth = (fontLayout.width / 6f) * OasisGameSettings.SCALE;
        fontLayout.reset();
    }

    @Override
    public void load(Asset asset) {
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        addRegion("healer_walking_up_idle", asset.get("healer_walking_up_idle"));
        addRegion("healer_walking_down_idle", asset.get("healer_walking_down_idle"));
        addRegion("healer_walking_left_idle", asset.get("healer_walking_left_idle"));
        addRegion("healer_walking_right_idle", asset.get("healer_walking_right_idle"));
        currentRegion = getRegion("healer_walking_up_idle");

        // up, down, left, right
        animationComponent.registerWalkingAnimation(0, 0.25f, asset.get("healer_walking_up", 1), asset.get("healer_walking_up", 2));
        animationComponent.registerWalkingAnimation(1, 0.25f, asset.get("healer_walking_down", 1), asset.get("healer_walking_down", 2));
        animationComponent.registerWalkingAnimation(2, 0.25f, asset.get("healer_walking_left", 1), asset.get("healer_walking_left", 2));
        animationComponent.registerWalkingAnimation(3, 0.25f, asset.get("healer_walking_right", 1), asset.get("healer_walking_right", 2));
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
    public void updatePosition(float x, float y, float angle) {
        super.updatePosition(x, y, angle);
        entityRotation = EntityRotation.values()[(int) angle];
    }

    @Override
    public void updateVelocity(float x, float y, float angle) {
        super.updateVelocity(x, y, angle);
        entityRotation = EntityRotation.values()[(int) angle];
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        drawEquippedItem(batch);


        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.playWalkingAnimation(entityRotation.ordinal(), delta));
        } else {
            if (currentRegion != null) {
                draw(batch, currentRegion);
            }
        }
    }

    private void drawEquippedItem(SpriteBatch batch) {
        if (ItemRegistry.isWeapon(equippedItem)) {
            equippedItem.calculateItemPositionAndRotation(getInterpolatedPosition(), entityRotation);
            equippedItem.update(Gdx.graphics.getDeltaTime(), entityRotation);
            equippedItem.draw(batch);
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
        font.draw(batch, getName(), screenPosition.x, screenPosition.y);
    }

    private void setIdleRegionState() {
        switch (entityRotation) {
            case UP:
                currentRegion = getRegion("healer_walking_up_idle");
                entityRotation = EntityRotation.UP;
                break;
            case DOWN:
                currentRegion = getRegion("healer_walking_down_idle");
                entityRotation = EntityRotation.DOWN;
                break;
            case LEFT:
                currentRegion = getRegion("healer_walking_left_idle");
                entityRotation = EntityRotation.LEFT;
                break;
            case RIGHT:
                currentRegion = getRegion("healer_walking_right_idle");
                entityRotation = EntityRotation.RIGHT;
                break;
        }
    }

    @Override
    public void defineEntity(World world, float x, float y) {
        super.defineEntity(world, x, y);
        this.body.setUserData(this);
    }
}
