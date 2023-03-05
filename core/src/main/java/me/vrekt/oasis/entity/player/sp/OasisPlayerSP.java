package me.vrekt.oasis.entity.player.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.network.types.ConnectionOption;
import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
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
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.mp.OasisNetworkPlayer;
import me.vrekt.oasis.entity.player.sp.inventory.PlayerInventory;
import me.vrekt.oasis.graphics.Renderable;
import me.vrekt.oasis.questing.PlayerQuestManager;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.questing.quests.tutorial.TutorialIslandQuest;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.instance.OasisInstance;

/**
 * Represents the local player SP
 */
public final class OasisPlayerSP extends LunarPlayer implements ResourceLoader, Renderable {

    private final OasisGame game;

    private EntityAnimationComponent animationComponent;
    private boolean rotationChanged;

    private OasisWorld gameWorldIn;
    private ClassType classType;

    private PlayerConnectionHandler connectionHandler;
    private final PlayerInventory inventory;

    private boolean isInInstance;
    private OasisInstance instanceIn;
    private final PlayerQuestManager questManager;

    private float health = 100.0f;
    private boolean didUseTutorialFruit;

    private EntityInteractable entitySpeakingTo;
    private boolean isSpeakingToEntity;

    public OasisPlayerSP(OasisGame game, String name) {
        super(true);
        this.game = game;

        setEntityName(name);
        setMoveSpeed(6.0f);
        setHasMoved(true);
        setSize(15, 25, OasisGameSettings.SCALE);
        setNetworkSendRatesInMs(0, 0);
        setFixedRotation(true);

        this.inventory = new PlayerInventory(this);

        // starting quest, later this won't be added here but instead on new game.
        this.questManager = new PlayerQuestManager();
        questManager.addActiveQuest(QuestType.TUTORIAL_ISLAND, new TutorialIslandQuest());
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

    public boolean isInInstance() {
        return isInInstance;
    }

    public void setInInstance(boolean inInstance) {
        isInInstance = inInstance;
    }

    public OasisInstance getInstanceIn() {
        return instanceIn;
    }

    public void setInstanceIn(OasisInstance instanceIn) {
        this.instanceIn = instanceIn;
    }

    public OasisGame getGame() {
        return game;
    }

    public PlayerInventory getInventory() {
        return inventory;
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

    public void setEntitySpeakingTo(EntityInteractable entitySpeakingTo) {
        this.entitySpeakingTo = entitySpeakingTo;
    }

    public EntityInteractable getEntitySpeakingTo() {
        return entitySpeakingTo;
    }

    public void setConnectionHandler(PlayerConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.setConnection(connectionHandler);

        connectionHandler.enableOptions(
                ConnectionOption.HANDLE_PLAYER_POSITION,
                ConnectionOption.HANDLE_PLAYER_VELOCITY,
                ConnectionOption.HANDLE_AUTHENTICATION,
                ConnectionOption.HANDLE_PLAYER_FORCE);

        connectionHandler.registerHandlerAsync(ConnectionOption.HANDLE_JOIN_WORLD, packet -> handleWorldJoin(((SPacketJoinWorld) packet)));
        connectionHandler.registerHandlerAsync(ConnectionOption.HANDLE_PLAYER_JOIN, packet -> handlePlayerJoin((SPacketCreatePlayer) packet));
    }

    public void handleWorldJoin(SPacketJoinWorld world) {
        Logging.info(this, "Attempting to join world: " + world.getWorldName() + ", entity ID is " + world.getEntityId());
        game.executeMain(() -> {
            setEntityId(world.getEntityId());
            game.loadIntoWorld(game.getWorldManager().getWorld(world.getWorldName()));
        });
    }

    public void handlePlayerJoin(SPacketCreatePlayer packet) {
        if (packet.getEntityId() == getEntityId()) return;

        Logging.info(this, "Spawning new player " + packet.getUsername() + ":" + packet.getEntityId());
        if (inWorld) {
            final OasisNetworkPlayer player = new OasisNetworkPlayer(true);
            player.load(game.getAsset());

            player.setProperties(packet.getUsername(), packet.getEntityId());
            player.setSize(24, 24, OasisGameSettings.SCALE);
            player.spawnEntityInWorld(getWorldIn());
        } else {
            Logging.warn(this, "Attempted to spawn player while not in world.");
        }
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
        float rotation = getRotation();
        setVelocity(0.0f, 0.0f, false);

        if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_UP_KEY)) {
            rotation = 0f;
            setVelocity(0.0f, moveSpeed, false);
        } else if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_DOWN_KEY)) {
            rotation = 1f;
            setVelocity(0.0f, -moveSpeed, false);
        } else if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_LEFT_KEY)) {
            rotation = 2f;
            setVelocity(-moveSpeed, 0.0f, false);
        } else if (Gdx.input.isKeyPressed(OasisKeybindings.WALK_RIGHT_KEY)) {
            rotation = 3f;
            setVelocity(moveSpeed, 0.0f, false);
        }

        setHasMoved(!getVelocity().isZero());

        rotationChanged = getRotation() != rotation;
        setRotation(rotation);
    }

    @Override
    public boolean isInView(Camera camera) {
        return true;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (rotationChanged) {
            setIdleRegionState();
            rotationChanged = false;
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.playWalkingAnimation(rotation, delta));
        } else {
            if (currentRegionState != null) {
                draw(batch, currentRegionState);
            }
        }
    }

    private void draw(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, getInterpolated().x, getInterpolated().y, region.getRegionWidth() * getScaling(), region.getRegionHeight() * getScaling());
    }

    @Override
    public void definePlayer(World world, float x, float y) {
        setPosition(x, y, true);
        this.getPrevious().set(x, y);
        this.setInterpolated(x, y);
        this.body = this.definitionHandler.createBodyInWorld(world, x, y, this.getProperties());
        this.definitionHandler.resetDefinition();
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> world, float x, float y) {
        super.spawnEntityInWorld(world, x, y);
        this.getBody().setUserData(this);
    }
}
