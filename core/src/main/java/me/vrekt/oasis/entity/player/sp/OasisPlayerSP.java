package me.vrekt.oasis.entity.player.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.network.types.ConnectionOption;
import gdx.lunar.protocol.packet.client.CPacketPing;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import gdx.lunar.protocol.packet.server.SPacketPing;
import gdx.lunar.world.LunarWorld;
import lunar.shared.drawing.Rotation;
import lunar.shared.entity.LunarEntity;
import lunar.shared.player.LunarEntityPlayer;
import lunar.shared.player.impl.LunarPlayer;
import lunar.shared.player.mp.LunarNetworkEntityPlayer;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.classes.ClassType;
import me.vrekt.oasis.entity.ai.utilities.PlayerSteeringLocation;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.npc.EntityEnemy;
import me.vrekt.oasis.entity.npc.EntitySpeakable;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.sp.inventory.PlayerInventory;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.item.artifact.ItemArtifact;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.questing.quests.tutorial.TutorialIslandQuest;
import me.vrekt.oasis.save.loading.SaveStateLoader;
import me.vrekt.oasis.save.player.PlayerSaveState;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.instance.OasisWorldInstance;
import me.vrekt.shared.network.ClientEquipItem;
import me.vrekt.shared.network.ClientSwingItem;

/**
 * Represents the local player SP
 */
public final class OasisPlayerSP extends LunarPlayer implements ResourceLoader, Drawable, SaveStateLoader<PlayerSaveState> {

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

    private float health = 100.0f;
    private boolean didUseTutorialFruit, didChopTree;

    private EntitySpeakable entitySpeakingTo;
    private boolean isSpeakingToEntity;
    private long lastPingSent, serverPingTime;

    private ItemWeapon equippedItem;
    private final Artifact[] artifacts = new Artifact[3];

    private EntityRotation rotation = EntityRotation.UP;
    private final PlayerSteeringLocation location;

    public OasisPlayerSP(OasisGame game, String name) {
        super(true);
        this.game = game;

        setEntityName(name);
        setMoveSpeed(6.0f);
        setHasMoved(true);
        setSize(15, 25, OasisGameSettings.SCALE);
        setNetworkSendRatesInMs(0, 0);
        setFixedRotation(true);
        setIgnorePlayerCollision(true);
        this.inventory = new PlayerInventory();

        // starting quest, later this won't be added here but instead on new game.
        this.questManager = new PlayerQuestManager();
        questManager.addActiveQuest(QuestType.TUTORIAL_ISLAND, new TutorialIslandQuest());

        this.location = new PlayerSteeringLocation(this);
    }

    @Override
    public void loadFromSave(PlayerSaveState state) {
        setPosition(state.getPosition(), true);
        inventory.clear();
        inventory.transferItemsFrom(state.getInventoryState().getInventory());
    }

    public void activateArtifact(int which) {
        this.applyArtifact(which, this.artifacts[which]);
    }

    private void applyArtifact(int slot, Artifact artifact) {
        if (artifact == null) return;
        if (artifact.apply(this, gameWorldIn.getCurrentWorldTick()))
            game.getGui().getHud().artifactUsed(slot, artifact.getArtifactCooldown());
    }

    public void equipArtifact(ItemArtifact artifact) {
        getInventory().removeItem(getInventory().getItemSlot(artifact));

        for (int i = 0; i < artifacts.length; i++) {
            if (artifacts[i] == null) {
                artifacts[i] = artifact.getArtifact();
                break;
            }
        }
    }

    public long getServerPingTime() {
        return serverPingTime;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void modifyHealth(float health) {
        this.health += health;
    }

    public void setDidUseTutorialFruit(boolean didUseTutorialFruit) {
        this.didUseTutorialFruit = didUseTutorialFruit;
    }

    public boolean didUseTutorialFruit() {
        return didUseTutorialFruit;
    }

    public void setDidChopTree(boolean didChopTree) {
        this.didChopTree = didChopTree;
    }

    public boolean didChopTree() {
        return didChopTree;
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

    public PlayerInventory getInventory() {
        return inventory;
    }

    public Artifact[] getArtifacts() {
        return artifacts;
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

    public void equipItem(ItemWeapon item) {
        this.equippedItem = item;

        connection.sendImmediately(new ClientEquipItem(getEntityId(), item.getItemId()));
    }

    public EntityRotation getPlayerRotation() {
        return rotation;
    }

    public PlayerSteeringLocation getSteeringLocation() {
        return location;
    }

    public void setConnectionHandler(PlayerConnection connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.setConnection(connectionHandler);

        connectionHandler.enableOptions(
                ConnectionOption.HANDLE_PLAYER_POSITION,
                ConnectionOption.HANDLE_PLAYER_VELOCITY,
                ConnectionOption.HANDLE_AUTHENTICATION,
                ConnectionOption.HANDLE_PLAYER_FORCE);

        connectionHandler.registerHandlerAsync(ConnectionOption.HANDLE_JOIN_WORLD, packet -> handleWorldJoin(((SPacketJoinWorld) packet)));
        connectionHandler.registerHandlerSync(ConnectionOption.PING, packet -> updatePingTime((SPacketPing) packet));
    }

    private void updatePingTime(SPacketPing packet) {
        final long now = System.currentTimeMillis();
        serverPingTime = now - packet.getClientTime();
    }

    public void handleWorldJoin(SPacketJoinWorld world) {
        Logging.info(this, "Attempting to join world: " + world.getWorldName() + ", entity ID is " + world.getEntityId());
        game.executeMain(() -> {
            setEntityId(world.getEntityId());
            game.loadIntoWorld(game.getWorldManager().getWorld(world.getWorldName()));
        });
    }

    public void setIdleRegionState() {
        switch (Rotation.of(getRotation())) {
            case FACING_UP:
                currentRegionState = getRegion("healer_walking_up_idle");
                break;
            case FACING_DOWN:
                currentRegionState = getRegion("healer_walking_down_idle");
                break;
            case FACING_LEFT:
                currentRegionState = getRegion("healer_walking_left_idle");
                break;
            case FACING_RIGHT:
                currentRegionState = getRegion("healer_walking_right_idle");
                break;
        }
    }

    @Override
    public void load(Asset asset) {
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        putRegion("healer_walking_up_idle", asset.get("healer_walking_up_idle"));
        putRegion("healer_walking_down_idle", asset.get("healer_walking_down_idle"));
        putRegion("healer_walking_left_idle", asset.get("healer_walking_left_idle"));
        putRegion("healer_walking_right_idle", asset.get("healer_walking_right_idle"));
        currentRegionState = getRegion("healer_walking_up_idle");

        // up, down, left, right
        animationComponent.registerWalkingAnimation(0, 0.25f, asset.get("healer_walking_up", 1), asset.get("healer_walking_up", 2));
        animationComponent.registerWalkingAnimation(1, 0.25f, asset.get("healer_walking_down", 1), asset.get("healer_walking_down", 2));
        animationComponent.registerWalkingAnimation(2, 0.25f, asset.get("healer_walking_left", 1), asset.get("healer_walking_left", 2));
        animationComponent.registerWalkingAnimation(3, 0.25f, asset.get("healer_walking_right", 1), asset.get("healer_walking_right", 2));
    }

    @Override
    public void setInterpolated(float x, float y) {
        super.setInterpolated(x - getWidthScaled() / 2f, y - getHeightScaled() / 2f);
    }

    public void pollInput() {

        setVelocity(0.0f, 0.0f, false);

        if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_UP_KEY)) {
            rotation = EntityRotation.UP;
            setVelocity(0.0f, moveSpeed, false);
        } else if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_DOWN_KEY)) {
            rotation = EntityRotation.DOWN;
            setVelocity(0.0f, -moveSpeed, false);
        } else if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_LEFT_KEY)) {
            rotation = EntityRotation.LEFT;
            setVelocity(-moveSpeed, 0.0f, false);
        } else if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_RIGHT_KEY)) {
            rotation = EntityRotation.RIGHT;
            setVelocity(moveSpeed, 0.0f, false);
        }

        setHasMoved(!getVelocity().isZero());

        rotationChanged = getRotation() != rotation.ordinal();
        setRotation(rotation.ordinal());
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        inventory.update();

        if (rotationChanged) {
            setIdleRegionState();
            rotationChanged = false;
        }

        if (System.currentTimeMillis() - lastPingSent >= 2000) {
            lastPingSent = System.currentTimeMillis();
            connection.sendImmediately(new CPacketPing(System.currentTimeMillis()));
        }

        for (Artifact artifact : artifacts) {
            if (artifact != null) {
                artifact.updateArtifact(this, gameWorldIn.getCurrentWorldTick());
            }
        }

    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        updateAndRenderEquippedItem(batch);

        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.playWalkingAnimation(rotation.ordinal(), delta));
        } else {
            if (currentRegionState != null) {
                draw(batch, currentRegionState);
            }
        }

        for (Artifact artifact : artifacts) {
            if (artifact == null) continue;

            if (artifact.drawEffect()) artifact.drawArtifactEffect(batch, delta, gameWorldIn.getCurrentWorldTick());
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

            // update network players
            connection.sendImmediately(new ClientSwingItem(getEntityId(), equippedItem.getItemId()));
        }

        equippedItem.calculateItemPositionAndRotation(getInterpolated(), rotation);
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
        batch.draw(region, getInterpolated().x, getInterpolated().y, region.getRegionWidth() * getScaling(), region.getRegionHeight() * getScaling());
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity>
    void spawnEntityInWorld(LunarWorld<P, N, E> world, float x, float y) {
        super.spawnEntityInWorld(world, x, y);
    }

    @Override
    public void defineEntity(World world, float x, float y) {
        super.defineEntity(world, x, y);
        this.body.setUserData(this);
    }
}
