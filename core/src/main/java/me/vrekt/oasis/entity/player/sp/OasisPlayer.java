package me.vrekt.oasis.entity.player.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.protocol.packet.client.C2SPacketPing;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.impl.LunarPlayer;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.classes.ClassType;
import me.vrekt.oasis.entity.ai.utilities.PlayerSteeringLocation;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.interactable.EntitySpeakable;
import me.vrekt.oasis.entity.inventory.Inventory;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.sp.inventory.PlayerInventory;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.item.artifact.ItemArtifact;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.questing.quests.tutorial.TutorialIslandQuest;
import me.vrekt.oasis.save.loading.SaveStateLoader;
import me.vrekt.oasis.save.player.PlayerSaveProperties;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.instance.OasisWorldInstance;
import me.vrekt.shared.network.ClientPacketEquipItem;

import java.util.LinkedList;

/**
 * Represents the local player SP
 */
public final class OasisPlayer extends LunarPlayer implements ResourceLoader, Drawable, SaveStateLoader<PlayerSaveProperties> {

    private final OasisGame game;

    private EntityAnimationComponent animationComponent;
    private boolean rotationChanged;

    private OasisWorld gameWorldIn;
    private ClassType classType;

    private PlayerConnection connectionHandler;
    private final PlayerInventory inventory;

    private boolean isInInstance, isInTutorialWorld = true;
    private OasisWorldInstance instanceIn;
    private final PlayerQuestManager questManager;

    private EntitySpeakable entitySpeakingTo;
    private boolean isSpeakingToEntity;
    private long lastPingSent, serverPingTime;

    private ItemWeapon equippedItem;
    private final LinkedList<Artifact> artifactInventory = new LinkedList<>();

    private EntityRotation rotation = EntityRotation.UP;
    private final PlayerSteeringLocation location;

    // disable movement listening while in dialogs
    private boolean disableMovement, moved;

    public OasisPlayer(OasisGame game, String name) {
        super(true);
        this.game = game;

        setName(name);
        setMoveSpeed(6.0f);
        moved = true;
        setMoving(true);
        setSize(15, 25, OasisGameSettings.SCALE);
        setNetworkSendRateInMs(25, 25);
        getBodyHandler().setHasFixedRotation(true);
        disablePlayerCollision(true);
        this.inventory = new PlayerInventory();
        this.questManager = new PlayerQuestManager();
        questManager.addActiveQuest(QuestType.TUTORIAL_ISLAND, new TutorialIslandQuest());
        this.location = new PlayerSteeringLocation(this);
    }

    @Override
    public void loadFromSave(PlayerSaveProperties state) {
        setPosition(state.getPosition(), true);
        inventory.clear();
        inventory.transferItemsFrom(state.getInventoryState().getInventory());
    }

    public void setDisableMovement(boolean disableMovement) {
        this.disableMovement = disableMovement;
    }

    public void activateArtifact(int slotNumber) {
        if (artifactInventory.isEmpty()) {
            GameLogging.warn(this, "Attempted to activate artifact with none in inventory %d", slotNumber);
            return;
        }

        final Artifact artifact = artifactInventory.get(slotNumber);
        if (artifact == null) {
            GameLogging.error(this, "Found no artifact within slot %d", slotNumber);
            return;
        }

        artifact.apply(this, gameWorldIn.getCurrentWorldTick());
        game.getGuiManager().getHudComponent().showArtifactAbilityUsed(slotNumber, artifact.getArtifactCooldown());
    }

    public void equipArtifact(ItemArtifact artifact) {
        getInventory().removeItem(artifact);
        artifactInventory.add(artifact.getArtifact());
    }

    public long getServerPingTime() {
        return serverPingTime;
    }

    public void setServerPingTime(long serverPingTime) {
        this.serverPingTime = serverPingTime;
    }

    public boolean isInInstance() {
        return isInInstance;
    }

    public void setInInstance(boolean inInstance) {
        isInInstance = inInstance;
    }

    public OasisWorldInstance getInstanceIn() {
        return instanceIn;
    }

    public void setInstanceIn(OasisWorldInstance instanceIn) {
        this.instanceIn = instanceIn;
    }

    public OasisGame getGame() {
        return game;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public LinkedList<Artifact> getArtifacts() {
        return artifactInventory;
    }

    public PlayerQuestManager getQuestManager() {
        return questManager;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public OasisWorld getGameWorldIn() {
        return gameWorldIn;
    }

    public void setGameWorldIn(OasisWorld gameWorldIn) {
        this.gameWorldIn = gameWorldIn;
    }

    public void setRotationChanged(boolean rotationChanged) {
        this.rotationChanged = rotationChanged;
    }

    public void setSpeakingToEntity(boolean speakingToEntity) {
        isSpeakingToEntity = speakingToEntity;
    }

    public boolean isSpeakingToEntity() {
        return isSpeakingToEntity;
    }

    public void setEntitySpeakingTo(EntitySpeakable entitySpeakingTo) {
        this.entitySpeakingTo = entitySpeakingTo;
    }

    public boolean isInTutorialWorld() {
        return isInTutorialWorld;
    }

    public void setInTutorialWorld(boolean inTutorialWorld) {
        isInTutorialWorld = inTutorialWorld;
    }

    public EntitySpeakable getEntitySpeakingTo() {
        return entitySpeakingTo;
    }

    public ItemWeapon getEquippedItem() {
        return equippedItem;
    }

    public void removeEquippedItem() {
        this.equippedItem = null;
        // -1 indicates un-equip item
        connection.sendImmediately(new ClientPacketEquipItem(getEntityId(), Items.NO_ITEM.getKey()));
    }

    public void equipItem(ItemWeapon item) {
        this.equippedItem = item;

        connection.sendImmediately(new ClientPacketEquipItem(getEntityId(), item.getKey()));
    }

    public boolean canEquipItem() {
        return this.equippedItem == null;
    }

    public EntityRotation getPlayerRotation() {
        return rotation;
    }

    public void setConnectionHandler(PlayerConnection connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.setConnection(connectionHandler);
    }

    public void setIdleRegionState() {
        switch (rotation) {
            case UP:
                currentRegion = getRegion("healer_walking_up_idle");
                break;
            case DOWN:
                currentRegion = getRegion("healer_walking_down_idle");
                break;
            case LEFT:
                currentRegion = getRegion("healer_walking_left_idle");
                break;
            case RIGHT:
                currentRegion = getRegion("healer_walking_right_idle");
                break;
        }
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

    /**
     * Poll input of the user input
     */
    public void pollInput() {
        setVelocity(0.0f, 0.0f);
        if (disableMovement) {
            return;
        }

        final float velY = getVelocityY();
        final float velX = getVelocityX();
        // TODO: Normalize, but fix slow movement
        setVelocity(velX, velY);

        moved = (!getVelocity().isZero());

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

    @Override
    public void update(float delta) {
        pollInput();

        if (this.body != null) {
            this.body.setLinearVelocity(this.getVelocity());
        }

        inventory.update();

        if (rotationChanged) {
            setIdleRegionState();
            rotationChanged = false;
        }

        if (game.isLocalMultiplayer() || game.isMultiplayer())
            updateNetworkComponents();

        artifactInventory.forEach(artifact -> artifact.updateArtifact(this, gameWorldIn.getCurrentWorldTick()));
    }

    /**
     * Update network components and sending of regular packets
     */
    private void updateNetworkComponents() {
        updateNetworkPositionAndVelocity();

        if (System.currentTimeMillis() - lastPingSent >= 2000) {
            lastPingSent = System.currentTimeMillis();
            connection.sendImmediately(new C2SPacketPing(System.currentTimeMillis()));
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        updateAndRenderEquippedItem(batch);

        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.playWalkingAnimation(rotation.ordinal(), delta));
        } else {
            if (currentRegion != null) {
                draw(batch, currentRegion);
            }
        }

        artifactInventory.forEach(artifact -> {
            if (artifact.drawEffect()) artifact.drawArtifactEffect(batch, delta, gameWorldIn.getCurrentWorldTick());
            if (artifact.isApplied()) artifact.drawParticleEffect(batch);
        });
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
            final float tick = gameWorldIn.getCurrentWorldTick();
            if (!equippedItem.isOnSwingCooldown(tick)) {
                equippedItem.setLastSwing(tick);
            } else {
                return;
            }

            final boolean isCritical = equippedItem.isCriticalHit();
            final EntityEnemy hit = gameWorldIn.hasHitEntity(equippedItem);
            if (hit != null) {
                final float damage = equippedItem.getBaseDamage() + (isCritical ? equippedItem.getCriticalHitDamage() : 0.0f);
                hit.damage(tick, Math.round(damage), equippedItem.getKnockbackMultiplier(), isCritical);
            }
        }

    }

    private void draw(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, getInterpolatedPosition().x, getInterpolatedPosition().y, region.getRegionWidth() * getWorldScale(), region.getRegionHeight() * getWorldScale());
    }

    @Override
    public void spawnInWorld(LunarWorld world, Vector2 position) {
        this.defineEntity(world.getEntityWorld(), position.x, position.y);
        setPosition(position, true);
    }

    @Override
    public void defineEntity(World world, float x, float y) {
        super.defineEntity(world, x, y);
        this.body.setUserData(this);
    }
}
