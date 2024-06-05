package me.vrekt.oasis.entity.player.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.combat.DamageType;
import me.vrekt.oasis.entity.component.animation.EntityAnimationBuilder;
import me.vrekt.oasis.entity.component.animation.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.dialog.DialogueEntry;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.interactable.EntitySpeakable;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.entity.player.AbstractPlayer;
import me.vrekt.oasis.entity.player.sp.animation.PlayerCombatAnimator;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.entity.player.sp.attribute.AttributeType;
import me.vrekt.oasis.entity.player.sp.attribute.Attributes;
import me.vrekt.oasis.entity.player.sp.inventory.PlayerInventory;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.item.artifact.ItemArtifact;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.network.player.DummyConnection;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.questing.Quest;
import me.vrekt.oasis.save.Savable;
import me.vrekt.oasis.save.player.ArtifactSave;
import me.vrekt.oasis.save.player.EffectSave;
import me.vrekt.oasis.save.player.PlayerSave;
import me.vrekt.oasis.save.player.QuestSave;
import me.vrekt.oasis.utility.ResourceLoader;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.effects.Effect;
import me.vrekt.oasis.world.interior.GameWorldInterior;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents the local player SP
 */
public final class PlayerSP extends AbstractPlayer implements ResourceLoader, Drawable, Savable<PlayerSave> {

    private static final float VELOCITY_NETWORK_SEND_RATE = 0.1f;
    private static final float POSITION_NETWORK_SEND_RATE = 0.35f;

    private final OasisGame game;

    private EntityAnimationComponent animationComponent;
    private boolean rotationChanged;
    private TextureRegion activeTexture;

    private PlayerConnection connection;
    private float lastPosition, lastVelocity;

    private final PlayerInventory inventory;

    private boolean inInteriorWorld;
    private GameWorldInterior interiorWorldIn;
    private final PlayerQuestManager questManager;

    private ItemWeapon equippedItem;
    private final IntMap<Artifact> artifacts = new IntMap<>(3);
    private final Map<AttributeType, Attributes> attributes = new HashMap<>();
    // list of enemies attacking us.
    private final Array<EntityEnemy> enemiesAttacking = new Array<>();

    private EntitySpeakable speakable;

    private final Vector3 worldPosition = new Vector3();
    private final Vector3 screenPosition = new Vector3();
    private final Rectangle bounds = new Rectangle();

    // disable movement listening while in dialogs
    private boolean disableMovement;
    // if the player has moved since some class requested it to be set.
    private boolean hasMoved = true;

    private ParticleEffect particleEffect;
    private float lastEffectApplied, effectStarted;
    private Effect activeEffect;

    private final PlayerCombatAnimator combatAnimator;
    private final Rectangle itemBounds = new Rectangle();

    public PlayerSP(OasisGame game) {
        this.game = game;
        create();

        this.inventory = new PlayerInventory();
        this.questManager = new PlayerQuestManager();
        this.combatAnimator = new PlayerCombatAnimator();
    }

    @Override
    public void load(PlayerSave playerSave) {
        this.setName(playerSave.name());
        this.setPosition(playerSave.position(), true);
        this.getInventory().transferFrom(playerSave.inventory().inventory());

        loadArtifacts(playerSave.artifacts());
        loadQuests(playerSave.quests());
        loadEffects(playerSave.effects());
    }

    /**
     * Load artifacts
     *
     * @param artifacts artifacts
     */
    private void loadArtifacts(LinkedList<ArtifactSave> artifacts) {
        if (artifacts == null) return;

        for (ArtifactSave save : artifacts) {
            final Artifact artifact = save.type().create();
            artifact.setArtifactLevel(save.level());

            this.artifacts.put(save.slot(), artifact);
        }
    }

    /**
     * Load quests
     *
     * @param quests quests
     */
    private void loadQuests(List<QuestSave> quests) {
        if (quests == null) return;

        for (QuestSave save : quests) {
            final Quest quest = save.type().create();
            quest.setCurrentObjectiveStep(save.objectiveIndex());

            for (int i = 0; i < save.objectives().size(); i++) {
                final QuestSave.QuestObjectiveSave objectiveSave = save.objectives().get(i);
                quest.getObjectives().get(i).setStatus(objectiveSave.unlocked(), objectiveSave.completed());
            }

            questManager.addActiveQuest(save.type(), quest);
        }
    }

    /**
     * Load effects
     *
     * @param effects effects
     */
    private void loadEffects(LinkedList<EffectSave> effects) {
        if (effects == null) return;
        // TODO: Multiple effects
        for (EffectSave save : effects) {
            this.activeEffect = Effect.create(save.type(), save.interval(), save.strength(), save.duration());
            this.activeEffect.setApplied(save.applied());
            this.activeEffect.applyPreviously(this);
        }
    }

    @Override
    public boolean isInWorld() {
        return worldIn != null || interiorWorldIn != null;
    }

    /**
     * Create and initialize the basic properties of the player
     */
    private void create() {
        setName("Player" + MathUtils.random(1, 99));
        setMoveSpeed(5.25f);
        setHealth(100);

        setSize(24, 28, OasisGameSettings.SCALE);
        bounds.set(getPosition().x, getPosition().y, 24 * OasisGameSettings.SCALE, 28 * OasisGameSettings.SCALE);

        itemBounds.set(bounds);
        itemBounds.x += 3.0f;
        itemBounds.y += 3.0f;

        setHasFixedRotation(true);
        disableCollision();
    }

    @Override
    public void load(Asset asset) {
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        dynamicSize = true;
        combatAnimator.load(asset);

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

    public AbstractInventory getInventory() {
        return inventory;
    }

    public PlayerQuestManager getQuestManager() {
        return questManager;
    }

    /**
     * Set the local connection of this player
     *
     * @param connection handler
     */
    public void connection(PlayerConnection connection) {
        this.connection = connection;
    }

    /**
     * @return network connection
     */
    public PlayerConnection getConnection() {
        if (connection == null) {
            GameLogging.warn(this, "Connection in null, providing DummyConnection");
            connection = new DummyConnection();
        }
        return connection;
    }

    public boolean isProjectileInBounds(Rectangle projectile) {
        return this.bounds.contains(projectile);
    }

    public Rectangle getBounds() {
        return bounds;
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
            attributes.computeIfAbsent(attribute.type(), a -> new Attributes()).add(attribute);
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

        if (game.isAnyMultiplayer()) connection.updateArtifactActivated(artifact);
        artifact.apply(this, GameManager.getTick());
        game.getGuiManager().getHudComponent().showArtifactAbilityUsed(slotNumber, artifact.getArtifactCooldown());
    }

    /**
     * Equip an artifact into the artifact inventory
     *
     * @param artifact artifact
     */
    public void equipArtifactToArtifactInventory(ItemArtifact artifact) {
        if (getInventory().containsItem(artifact.type())) {
            getInventory().remove(artifact);

            final int slot = findEmptyArtifactSlot();
            if (slot == -1) {
                // full, swap something out
                // TODO: Player chooses next time
                final int replaceSlot = 0;
                final Artifact old = artifacts.get(replaceSlot);
                artifacts.put(replaceSlot, artifact.getArtifact());

                if (!getInventory().isFull()) {
                    getInventory().add(old.asItem());
                } else {
                    // TODO: Item deletion!
                    GameLogging.warn(this, "Item deletion, we should drop the item when implemented.");
                }
            } else {
                artifacts.put(slot, artifact.getArtifact());
            }

        } else {
            GameLogging.warn(this, "Attempted to equip an artifact without having it.");
        }
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
                game.guiManager.resetCursor();
                return;
            }

            speakable.next();
            game.getGuiManager().getDialogComponent().showEntityDialog(speakable);
        }
    }

    /**
     * Add an area effect to this player
     * Poison clouds for example
     *
     * @param particleEffect the particle effect
     * @param effect         the effect
     */
    public void givePlayerEffect(ParticleEffect particleEffect, Effect effect) {
        effectStarted = GameManager.getTick();
        this.particleEffect = particleEffect;

        activeEffect = effect;
        particleEffect.reset(false);
        particleEffect.setPosition(centerX(), centerY());
    }

    /**
     * TODO: Multiple effects
     *
     * @return active effect
     */
    public Effect activeEffect() {
        return activeEffect;
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

    /**
     * @return removed after an interior is disposed
     */
    public GameWorldInterior getInteriorWorldIn() {
        return interiorWorldIn;
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
            this.worldIn = world;
        }
    }

    /**
     * @return the world state of this player
     */
    public GameWorld getWorldState() {
        return inInteriorWorld ? interiorWorldIn : worldIn;
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
        if (game.isAnyMultiplayer()) connection.updateItemEquipped(null);
    }

    /**
     * Equip an item
     *
     * @param item the item
     */
    public void equipItem(ItemWeapon item) {
        this.equippedItem = item;
        if (game.isAnyMultiplayer()) connection.updateItemEquipped(item);
    }

    public boolean canEquipItem() {
        return this.equippedItem == null;
    }

    @Override
    public void removeFromWorld() {
        super.removeFromWorld();
        this.worldIn = null;
    }

    /**
     * Remove this player from their interior world.
     */
    public void removeFromInteriorWorld() {
        interiorWorldIn.boxWorld().destroyBody(body);

        this.body = null;
        this.interiorWorldIn = null;
        this.inInteriorWorld = false;
    }

    /**
     * Set the idle region state for this player when not moving.
     */
    public void setIdleRegionState() {
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
    public void update(float delta) {
        pollInput();

        this.body.setLinearVelocity(this.getVelocity());
        bounds.setPosition(body.getPosition());

        // handle all attributes currently applied
        // TODO: Only needs to update every second.
        attributes.forEach((type, attr) -> attr.update());

        inventory.update();
        if (rotationChanged) {
            // TODO: Animation plays again if rotated while one is playing
            // TODO: Doesn't look half bad so keep it for now.
            combatAnimator.resetAnimation(previousRotation);

            setIdleRegionState();
            rotationChanged = false;
        }

        if (game.isLocalMultiplayer() || game.isMultiplayer()) updateNetworkComponents();
        artifacts.values().forEach(artifact -> artifact.updateIfApplied(this));

        updateActiveEffect();
    }

    /**
     * Update the active player effect if any
     */
    private void updateActiveEffect() {
        if (activeEffect != null) {
            final boolean expired = GameManager.hasTimeElapsed(effectStarted, activeEffect.duration(), true);

            if (!expired) {
                if (activeEffect.ready(lastEffectApplied)) {
                    activeEffect.apply(this, GameManager.getTick());
                    lastEffectApplied = GameManager.getTick();
                }
            } else {
                activeEffect.free();
                activeEffect = null;
                particleEffect = null;
            }
        }
    }

    /**
     * Poll input of the user input
     */
    private void pollInput() {
        setVelocity(0.0f, 0.0f, false);
        if (disableMovement) {
            return;
        }

        previousRotation = rotation;
        final float velY = getVelocityY();
        final float velX = getVelocityX();

        // TODO: Normalize, but fix slow movement
        setVelocity(velX, velY, false);
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
        connection.updateSync();
    }

    /**
     * Send updated position and velocity
     */
    private void updateNetworkPositionAndVelocity() {
        final float now = GameManager.getTick();

        if (GameManager.hasTimeElapsed(lastPosition, POSITION_NETWORK_SEND_RATE)) {
            getConnection().updatePosition(getPosition(), getAngle());
            lastPosition = now;
        }

        if (GameManager.hasTimeElapsed(lastVelocity, VELOCITY_NETWORK_SEND_RATE)) {
            getConnection().updateVelocity(getVelocity(), getAngle());
            lastVelocity = now;
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (equippedItem != null
                && equippedItem.isSwinging()) {
            equippedItem.updateItemBoundingSize(rotation);
            final boolean finished = combatAnimator.renderActiveAnimationAt(batch,
                    rotation,
                    getInterpolatedPosition().x,
                    getInterpolatedPosition().y,
                    delta,
                    equippedItem);

            if (finished) {
                equippedItem.resetSwing();
            }
        } else {
            // if not swinging just set item position to where we are.
            if (equippedItem != null) {
                equippedItem.updateItemPosition(getInterpolatedPosition().x, getInterpolatedPosition().y);
                equippedItem.resetBoundingSize();
            }

            if (!getVelocity().isZero()) {
                draw(batch, animationComponent.animateMoving(rotation, delta));
            } else {
                if (activeTexture != null) {
                    draw(batch, activeTexture);
                }
            }
        }

        // draw all artifact effects
        for (Artifact artifact : artifacts.values()) {
            if (artifact.drawEffect()) artifact.drawArtifactEffect(batch, delta, GameManager.getTick());
            if (artifact.isApplied()) artifact.drawParticleEffect(batch);
        }

        if (particleEffect != null) {
            particleEffect.update(delta);
            particleEffect.draw(batch, delta);
        }
    }

    /**
     * Left mouse was pressed, ensured that there was no other interaction
     */
    public void swingItem() {
        if (equippedItem == null) return;

        if (equippedItem.canSwing()) {
            equippedItem.swingItem();
        }

        final boolean critical = equippedItem.isCriticalHit();
        final EntityEnemy hit = getWorldState().hasHitEntity(equippedItem);
        if (hit != null && !hit.isDead()) {
            hit.knockback(rotation, equippedItem.getKnockbackMultiplier());
            final float damage = equippedItem.getBaseDamage() + (critical ? equippedItem.getCriticalHitDamage() : 0.0f);
            hit.damage(damage, critical ? DamageType.CRITICAL_HIT : DamageType.NORMAL);
        }
    }

    private void draw(SpriteBatch batch, TextureRegion region) {
        batch.draw(region,
                getInterpolatedPosition().x,
                getInterpolatedPosition().y,
                region.getRegionWidth() * OasisGameSettings.SCALE,
                region.getRegionHeight() * OasisGameSettings.SCALE);
    }

    public void drawDamage(SpriteBatch batch, Camera worldCamera, Camera guiCamera) {
        worldPosition.set(worldCamera.project(worldPosition.set(getPosition().x + 1.0f, getInterpolatedPosition().y + 2.5f, 0.0f)));
        screenPosition.set(guiCamera.project(worldPosition));

        // animator.drawAccumulatedDamage(batch, game.getAsset().getBoxy(), screenPosition.x, screenPosition.y, getWidth());
    }

    /**
     * @return origin center of the player x
     */
    public float centerX() {
        return getPosition().x + (getScaledWidth() / 2f);
    }

    /**
     * @return origin center of the player y
     */
    public float centerY() {
        return getPosition().y + (getScaledHeight() / 2f);
    }

    @Override
    public void createBoxBody(World world) {
        super.createBoxBody(world);
        this.body.setUserData(this);
    }
}
