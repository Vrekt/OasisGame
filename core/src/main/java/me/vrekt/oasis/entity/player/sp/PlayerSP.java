package me.vrekt.oasis.entity.player.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.AbstractLunarEntityPlayer;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.combat.CombatDamageAnimator;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.dialog.DialogueEntry;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.interactable.EntitySpeakable;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.entity.player.sp.attribute.AttributeType;
import me.vrekt.oasis.entity.player.sp.attribute.Attributes;
import me.vrekt.oasis.entity.player.sp.inventory.PlayerInventory;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.item.artifact.ItemArtifact;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.utility.ResourceLoader;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.instance.GameWorldInterior;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the local player SP
 */
public final class PlayerSP extends AbstractLunarEntityPlayer implements ResourceLoader, Drawable {

    private final OasisGame game;

    private EntityAnimationComponent animationComponent;
    private boolean rotationChanged;
    private TextureRegion activeTexture;

    private GameWorld gameWorldIn;

    private PlayerConnection connectionHandler;
    private final PlayerInventory inventory;

    private boolean inInteriorWorld;
    private GameWorldInterior interiorWorldIn;
    private final PlayerQuestManager questManager;

    private ItemWeapon equippedItem;
    private final IntMap<Artifact> artifacts = new IntMap<>(3);

    private EntityRotation rotation = EntityRotation.UP;
    private final Map<AttributeType, Attributes> attributes = new HashMap<>();
    private final CombatDamageAnimator animator = new CombatDamageAnimator();
    // list of enemies attacking us.
    private final Array<EntityEnemy> enemiesAttacking = new Array<>();

    private EntitySpeakable speakable;

    private final Vector3 worldPosition = new Vector3();
    private final Vector3 screenPosition = new Vector3();

    // disable movement listening while in dialogs
    private boolean disableMovement;
    // if the player has moved since some class requested it to be set.
    private boolean hasMoved = true;

    public PlayerSP(OasisGame game) {
        super(true);
        this.game = game;
        create();

        this.inventory = new PlayerInventory();
        this.questManager = new PlayerQuestManager();
    }

    /**
     * Create and initialize the basic properties of the player
     */
    private void create() {
        setName("Player" + OasisGame.GAME_VERSION);
        setMoveSpeed(6.0f);
        setHealth(100);

        setSize(15, 25, OasisGameSettings.SCALE);
        setNetworkSendRateInMs(25, 25);
        getBodyHandler().setHasFixedRotation(true);
        disablePlayerCollision(true);
    }

    @Override
    public void load(Asset asset) {
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        getTextureComponent().add("healer_walking_up_idle", asset.get("healer_walking_up_idle"));
        getTextureComponent().add("healer_walking_down_idle", asset.get("healer_walking_down_idle"));
        getTextureComponent().add("healer_walking_left_idle", asset.get("healer_walking_left_idle"));
        getTextureComponent().add("healer_walking_right_idle", asset.get("healer_walking_right_idle"));
        activeTexture = getTextureComponent().get("healer_walking_up_idle");

        // up, down, left, right
        animationComponent.createMoveAnimation(EntityRotation.UP, 0.25f, asset.get("healer_walking_up", 1), asset.get("healer_walking_up", 2));
        animationComponent.createMoveAnimation(EntityRotation.DOWN, 0.25f, asset.get("healer_walking_down", 1), asset.get("healer_walking_down", 2));
        animationComponent.createMoveAnimation(EntityRotation.LEFT, 0.25f, asset.get("healer_walking_left", 1), asset.get("healer_walking_left", 2));
        animationComponent.createMoveAnimation(EntityRotation.RIGHT, 0.25f, asset.get("healer_walking_right", 1), asset.get("healer_walking_right", 2));
    }

    public AbstractInventory getInventory() {
        return inventory;
    }

    public PlayerQuestManager getQuestManager() {
        return questManager;
    }

    public void connection(PlayerConnection connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @Override
    public PlayerConnection getConnection() {
        return connectionHandler;
    }

    /**
     * Set the speaking state of the entity
     *
     * @param entity   the entity
     * @param speaking state
     */
    public void speak(EntitySpeakable entity, boolean speaking) {
        this.speakable = speaking ? entity : null;
    }

    /**
     * Apply an attribute to this player
     *
     * @param attribute the attribute
     */
    public void applyAttribute(Attribute attribute) {
        if (attribute.isInstant()) {
            // this attribute is instant and does not expire, so apply now.
            attribute.apply(this);
        } else {
            // this attribute expires and has other special functionality.
            attribute.apply(this);

            game.getGuiManager().getHudComponent().showAttribute(attribute);
            attributes.computeIfAbsent(attribute.getType(), a -> new Attributes())
                    .add(attribute);
        }
    }

    /**
     * Monitor the movement of this player
     * Used for interactions, once the player moves a little hide the interaction
     */
    public void notifyIfMoved() {
        hasMoved = false;
    }

    /**
     * @return if the player moved from the monitoring position
     */
    public boolean movementNotified() {
        return hasMoved;
    }

    /**
     * Enable or disable movement of the playerF
     *
     * @param disableMovement state
     */
    public void disableMovement(boolean disableMovement) {
        this.disableMovement = disableMovement;
    }

    /**
     * Activate an artifact within the given slot
     *
     * @param slotNumber the slot
     */
    public void activateArtifact(int slotNumber) {
        if (artifacts.isEmpty()) {
            GameLogging.warn(this, "Attempted to activate artifact with none in inventory %d", slotNumber);
            return;
        }

        final Artifact artifact = artifacts.get(slotNumber);
        if (artifact == null) {
            GameLogging.error(this, "Found no artifact within slot %d", slotNumber);
            return;
        }

        if (game.isAnyMultiplayer()) connectionHandler.updateArtifactActivated(artifact);
        artifact.apply(this, GameManager.getTick());
        game.getGuiManager().getHudComponent().showArtifactAbilityUsed(slotNumber, artifact.getArtifactCooldown());
    }

    /**
     * Equip an artifact into the artifact inventory
     *
     * @param artifact artifact
     */
    public void equipArtifactToArtifactInventory(ItemArtifact artifact) {
        getInventory().remove(artifact);
        artifacts.put(findEmptyArtifactSlot(), artifact.getArtifact());
    }

    /**
     * Find an empty artifact slot
     * TODO: Can equip artifact/replacing
     *
     * @return the slot
     */
    private int findEmptyArtifactSlot() {
        for (int i = 0; i < 3; i++) {
            if (!artifacts.containsKey(i)) return i;
        }
        return -1;
    }

    public IntMap<Artifact> getArtifacts() {
        return artifacts;
    }

    /**
     * Handle dialog 'F' key press
     */
    public void handleDialogKeyPress() {
        if (speakable != null) {
            final DialogueEntry entry = speakable.getEntry();
            //  waiting to pick an option
            if (entry.suggestions() || !entry.isSkippable()) return;

            // advance this dialog after we close the GUI
            // Basically the dialog is 'finished' but if we go back
            // and speak to the entity they will show a message reminding them what to do
            // So we only show that afterwards.
            if (speakable.advance()) {
                game.getGuiManager().hideGui(GuiType.DIALOG);
                return;
            }

            speakable.next();
            game.getGuiManager().getDialogComponent().showEntityDialog(speakable);
        }
    }

    /**
     * @return list of all enemies attacking us
     */
    public Array<EntityEnemy> getEnemiesAttacking() {
        return enemiesAttacking;
    }

    /**
     * @return {@code true} if the player is in an interior
     */
    public boolean isInInteriorWorld() {
        return inInteriorWorld;
    }

    public void setInInteriorWorld(boolean inInteriorWorld) {
        this.inInteriorWorld = inInteriorWorld;
    }

    /**
     * Update the world state for this player
     *
     * @param world world or interior
     */
    public void updateWorldState(GameWorld world) {
        if (world instanceof GameWorldInterior interior) {
            interiorWorldIn = interior;
            inInteriorWorld = true;
        } else {
            this.gameWorldIn = world;
            this.setInWorld(true);
            this.setWorld(gameWorldIn);
        }
    }

    /**
     * @return the world state of this player
     */
    public GameWorld getWorldState() {
        return inInteriorWorld ? interiorWorldIn : gameWorldIn;
    }

    /**
     * @return activate equipped item
     */
    public ItemWeapon getEquippedItem() {
        return equippedItem;
    }

    /**
     * Remove the active equipped item
     */
    public void removeEquippedItem() {
        this.equippedItem = null;
        if (game.isAnyMultiplayer()) connectionHandler.updateItemEquipped(null);
    }

    /**
     * Equip an item
     *
     * @param item the item
     */
    public void equipItem(ItemWeapon item) {
        this.equippedItem = item;
        if (game.isAnyMultiplayer()) connectionHandler.updateItemEquipped(item);
    }

    public boolean canEquipItem() {
        return this.equippedItem == null;
    }

    @Override
    public void removeFromWorld() {
        super.removeFromWorld();
        this.gameWorldIn = null;
    }

    /**
     * Remove this player from their interior world.
     */
    public void removeFromInteriorWorld() {
        interiorWorldIn.getEntityWorld().destroyBody(body);

        this.body = null;
        this.inInteriorWorld = false;
        this.interiorWorldIn = null;
    }

    /**
     * Set the idle region state for this player when not moving.
     */
    public void setIdleRegionState() {
        switch (rotation) {
            case UP:
                activeTexture = getTextureComponent().get("healer_walking_up_idle");
                break;
            case DOWN:
                activeTexture = getTextureComponent().get("healer_walking_down_idle");
                break;
            case LEFT:
                activeTexture = getTextureComponent().get("healer_walking_left_idle");
                break;
            case RIGHT:
                activeTexture = getTextureComponent().get("healer_walking_right_idle");
                break;
        }
    }

    @Override
    public void update(float delta) {
        pollInput();

        if (this.body != null) this.body.setLinearVelocity(this.getVelocity());

        // handle all attributes currently applied
        // TODO: Only needs to update every second.
        attributes.forEach((type, attr) -> attr.update());

        inventory.update();
        if (rotationChanged) {
            setIdleRegionState();
            rotationChanged = false;
        }

        if (game.isLocalMultiplayer() || game.isMultiplayer()) updateNetworkComponents();
        artifacts.values().forEach(artifact -> artifact.updateIfApplied(this));
    }

    /**
     * Poll input of the user input
     */
    private void pollInput() {
        setVelocity(0.0f, 0.0f);
        if (disableMovement) {
            return;
        }

        final float velY = getVelocityY();
        final float velX = getVelocityX();

        // TODO: Normalize, but fix slow movement
        setVelocity(velX, velY);
        if (velX != 0.0 || velY != 0.0) hasMoved = true;

        rotationChanged = getAngle() != rotation.ordinal();
        setAngle(rotation.ordinal());
    }

    private float getVelocityY() {
        if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_UP_KEY)) {
            rotation = EntityRotation.UP;
            return getMoveSpeed();
        } else if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_DOWN_KEY)) {
            rotation = EntityRotation.DOWN;
            return -getMoveSpeed();
        }
        return 0.0f;
    }

    private float getVelocityX() {
        if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_LEFT_KEY)) {
            rotation = EntityRotation.LEFT;
            return -getMoveSpeed();
        } else if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_RIGHT_KEY)) {
            rotation = EntityRotation.RIGHT;
            return getMoveSpeed();
        }
        return 0.0f;
    }

    /**
     * Update network components and sending of regular packets
     */
    private void updateNetworkComponents() {
        updateNetworkPositionAndVelocity();
        connectionHandler.update();
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        updateAndRenderEquippedItem(batch);

        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.animate(rotation, delta));
        } else {
            if (activeTexture != null) {
                draw(batch, activeTexture);
            }
        }

        // draw all artifact effects
        for (Artifact artifact : artifacts.values()) {
            if (artifact.drawEffect()) artifact.drawArtifactEffect(batch, delta, GameManager.getTick());
            if (artifact.isApplied()) artifact.drawParticleEffect(batch);
        }
    }

    /**
     * Render the current item the player hss equipped
     *
     * @param batch drawing
     */
    private void updateAndRenderEquippedItem(SpriteBatch batch) {
        if (equippedItem == null) return;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            equippedItem.swingItem();
            // TODO
            //   connection.sendImmediately(new ClientPacketSwingItem(getEntityId(), equippedItem.getKey()));
        }

        equippedItem.calculateItemPositionAndRotation(getInterpolatedPosition(), rotation);
        equippedItem.update(Gdx.graphics.getDeltaTime(), rotation);
        equippedItem.draw(batch);

        if (equippedItem.isSwinging()) {
            final float tick = GameManager.getTick();
            if (!equippedItem.isOnSwingCooldown(tick)) {
                equippedItem.setLastSwing(tick);
            } else {
                return;
            }

            final boolean isCritical = equippedItem.isCriticalHit();
            final EntityEnemy hit = getWorldState().hasHitEntity(equippedItem);

            if (hit != null && !enemiesAttacking.contains(hit, true)) enemiesAttacking.add(hit);

            if (hit != null) {
                final float damage = equippedItem.getBaseDamage() + (isCritical ? equippedItem.getCriticalHitDamage() : 0.0f);

                hit.damage(tick, Math.round(damage), equippedItem.getKnockbackMultiplier(), isCritical);
            }
        }

    }

    public void attack(float amount, EntityEnemy by) {
        super.damage(amount);

        animator.accumulateDamage(amount, EntityRotation.DOWN, false);
    }

    private void draw(SpriteBatch batch, TextureRegion region) {
        animator.update(Gdx.graphics.getDeltaTime());

        batch.draw(region, getInterpolatedPosition().x, getInterpolatedPosition().y, region.getRegionWidth() * getWorldScale(), region.getRegionHeight() * getWorldScale());
    }

    public void drawDamage(SpriteBatch batch, Camera worldCamera, Camera guiCamera) {
        worldPosition.set(worldCamera.project(worldPosition.set(getPosition().x + 1.0f, getInterpolatedPosition().y + 2.5f, 0.0f)));
        screenPosition.set(guiCamera.project(worldPosition));

        animator.drawAccumulatedDamage(batch, game.getAsset().getBoxy(), screenPosition.x, screenPosition.y, getWidth());
    }

    @Override
    public void spawnInWorld(LunarWorld world, Vector2 position) {
        this.defineEntity(world.getEntityWorld(), position.x, position.y);
        setBodyPosition(position, true);
    }

    @Override
    public void defineEntity(World world, float x, float y) {
        super.defineEntity(world, x, y);
        this.body.setUserData(this);
    }
}
