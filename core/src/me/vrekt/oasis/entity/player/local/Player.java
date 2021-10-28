package me.vrekt.oasis.entity.player.local;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.impl.LunarPlayer;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.world.LunarWorld;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.animation.PlayerAnimationManager;
import me.vrekt.oasis.entity.player.network.NetworkPlayer;
import me.vrekt.oasis.inventory.PlayerInventory;
import me.vrekt.oasis.quest.type.QuestRewards;
import me.vrekt.oasis.world.AbstractWorld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The local player
 */
public final class Player extends LunarPlayer {

    private final OasisGame game;

    private final PlayerAnimationManager animationManager = new PlayerAnimationManager(this);
    private final List<Integer> inputsDisabled = new CopyOnWriteArrayList<>();
    private final PlayerInventory inventory;

    private AbstractWorld abstractWorldIn;

    // locked initially.
    private boolean pickaxeLocked = true;

    // pending quest rewards
    private final Map<QuestRewards, Integer> rewards = new HashMap<>();

    public Player(OasisGame game, int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId, playerScale, playerWidth, playerHeight, rotation);
        this.game = game;

        setVelocitySendRate(100);
        setPositionSendRate(250);
        setMoveSpeed(12.0f);
        this.inventory = new PlayerInventory(game.items, 18);
    }

    @Override
    public void spawnEntityInWorld(LunarWorld world, float x, float y) {
        super.spawnEntityInWorld(world, x, y);
        abstractWorldIn = (AbstractWorld) world;
    }

    public void loadAnimations() {
        animationManager.loadAnimations(game.asset);
    }

    public PlayerAnimationManager getAnimations() {
        return animationManager;
    }

    public boolean isPickaxeLocked() {
        return pickaxeLocked;
    }

    public void setPickaxeLocked(boolean pickaxeLocked) {
        this.pickaxeLocked = pickaxeLocked;
    }

    public void givePlayerQuestReward(QuestRewards reward, int amount) {
        this.rewards.put(reward, amount);
    }

    public void award(String questName) {
        abstractWorldIn.getGui().showQuestRewards(questName, rewards);
        this.rewards.clear();
    }

    @Override
    protected void pollInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)
                && !inputsDisabled.contains(Input.Keys.A)) {
            velocity.set(-moveSpeed, 0f);
            rotation = Rotation.FACING_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)
                && !inputsDisabled.contains(Input.Keys.D)) {
            velocity.set(moveSpeed, 0f);
            rotation = Rotation.FACING_RIGHT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)
                && !inputsDisabled.contains(Input.Keys.W)) {
            velocity.set(0f, moveSpeed);
            rotation = Rotation.FACING_UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)
                && !inputsDisabled.contains(Input.Keys.S)) {
            velocity.set(0f, -moveSpeed);
            rotation = Rotation.FACING_DOWN;
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        animationManager.update(delta);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (!animationManager.render(batch)) super.render(batch, delta);
    }

    @Override
    public void setConnection(AbstractConnection connection) {
        super.setConnection(connection);

        // TODO: Multiplayer is broken
        // handle incoming network players
        connection.setCreatePlayerHandler(packet -> {
            final NetworkPlayer player = new NetworkPlayer(packet.getEntityId(), (1 / 16.0f),
                    16.0f, 18.0f, Rotation.FACING_UP);
            player.setName(packet.getUsername());

            player.spawnEntityInWorld(worldIn, packet.getX(), packet.getY());
            player.initializePlayerRendererAndLoad(game.asset.getAssets(), true);
        });
    }

    /**
     * Disable inputs
     *
     * @param keys the keys
     */
    public void disableInputs(int... keys) {
        for (int key : keys) inputsDisabled.add(key);
    }

    public boolean hasDisabledInputs() {
        return inputsDisabled.isEmpty();
    }

    /**
     * Enable inputs
     *
     * @param keys the keys
     */
    public void enableInputs(int... keys) {
        for (int key : keys) if (inputsDisabled.contains(key)) inputsDisabled.remove((Integer) key);
    }

    /**
     * Check if an input is disabled
     *
     * @param key the key
     * @return {@code true} if so
     */
    private boolean isInputDisabled(int key) {
        return inputsDisabled.contains(key);
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    @Override
    public void dispose() {
        super.dispose();
        inputsDisabled.clear();
    }
}
