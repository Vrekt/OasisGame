package me.vrekt.oasis.entity.player.mp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.world.LunarWorld;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.player.OasisNetworkEntityPlayer;
import me.vrekt.oasis.item.ItemEquippable;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.utility.ResourceLoader;
import me.vrekt.oasis.world.GameWorld;

/**
 * Represents any player over the network
 */
public final class NetworkPlayer extends OasisNetworkEntityPlayer implements ResourceLoader {

    private EntityAnimationComponent animationComponent;
    private TextureRegion activeTexture;

    private float nametagRenderWidth;
    private final Vector3 worldPosition = new Vector3();
    private final Vector3 screenPosition = new Vector3();

    private boolean renderNametag;

    private ItemEquippable equippedItem;

    private EntityRotation lastRotation = EntityRotation.UP;
    private EntityRotation entityRotation = EntityRotation.UP;

    private GameWorld gameWorldIn;

    public NetworkPlayer(boolean initializeComponents) {
        super(initializeComponents);

        setInterpolatePosition(true);
        setSnapToPositionIfDesynced(true);
        setDesyncDistanceToInterpolate(2.5f);
        setInterpolationAlpha(1.0f);
        disablePlayerCollision(true);
    }

    @Override
    public LunarWorld getWorld() {
        return gameWorldIn;
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

    public void spawnPlayerAndSetWorldState(GameWorld world) {
        spawnInWorld(world);
        this.gameWorldIn = world;
    }


    @Override
    public void setName(String name) {
        super.setName(name);

        final GlyphLayout fontLayout = new GlyphLayout(GameManager.getGuiManager().getSmallFont(), getName());
        this.nametagRenderWidth = (fontLayout.width / 6f) * OasisGameSettings.SCALE;
        fontLayout.reset();
    }

    @Override
    public void load(Asset asset) {
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        setSize(24, 32, OasisGameSettings.SCALE);

        getTextureComponent().add("character_a_walking_up_idle", asset.get("character_a_walking_up_idle"));
        getTextureComponent().add("character_a_walking_down_idle", asset.get("character_a_walking_down_idle"));
        getTextureComponent().add("character_a_walking_left_idle", asset.get("character_a_walking_left_idle"));
        getTextureComponent().add("character_a_walking_right_idle", asset.get("character_a_walking_right_idle"));
        activeTexture = getTextureComponent().get("character_a_walking_up_idle");

        // up, down, left, right
        animationComponent.createMoveAnimation(EntityRotation.UP, 0.35f, asset.get("character_a_walking_up", 1), asset.get("character_a_walking_up", 2));
        animationComponent.createMoveAnimation(EntityRotation.DOWN, 0.35f, asset.get("character_a_walking_down", 1), asset.get("character_a_walking_down", 2));
        animationComponent.createMoveAnimation(EntityRotation.LEFT, 0.35f, asset.get("character_a_walking_left", 1), asset.get("character_a_walking_left", 2));
        animationComponent.createMoveAnimation(EntityRotation.RIGHT, 0.35f, asset.get("character_a_walking_right", 1), asset.get("character_a_walking_right", 2));
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
        // drawEquippedItem(batch);

        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.animate(entityRotation, delta), getScaledWidth(), getScaledHeight());
        } else {
            if (activeTexture != null) {
                draw(batch, activeTexture, getScaledWidth(), getScaledHeight());
            }
        }
    }

    private void drawEquippedItem(SpriteBatch batch) {
        if (equippedItem instanceof ItemWeapon) {
            // equippedItem.calculateItemPositionAndRotation(getInterpolatedPosition(), entityRotation);
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
    public void defineEntity(World world, float x, float y) {
        super.defineEntity(world, x, y);
        this.body.setUserData(this);
    }
}
