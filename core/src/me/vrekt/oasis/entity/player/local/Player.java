package me.vrekt.oasis.entity.player.local;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.impl.LunarPlayer;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.world.LunarWorld;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.animation.PlayerAnimationManager;
import me.vrekt.oasis.entity.player.network.NetworkPlayer;
import me.vrekt.oasis.inventory.PlayerInventory;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.quest.type.QuestRewards;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The local player
 */
public final class Player extends LunarPlayer implements InputProcessor {

    private final OasisGame game;

    private final PlayerAnimationManager animationManager = new PlayerAnimationManager(this);
    private final List<Integer> inputsDisabled = new CopyOnWriteArrayList<>();
    private final PlayerInventory inventory;

    private AbstractWorld abstractWorldIn;

    // pending quest rewards
    private final Map<QuestRewards, Integer> rewards = new HashMap<>();

    private ParticleEffect ult;

    private boolean u;
    private long lastSwing;

    public Player(OasisGame game, int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId, playerScale, playerWidth, playerHeight, rotation);
        this.game = game;

        setVelocitySendRate(100);
        setPositionSendRate(250);
        setMoveSpeed(10.0f);
        setIgnoreOtherPlayerCollision(true);
        this.inventory = new PlayerInventory(game.getItemManager(), 18);
    }

    @Override
    public void spawnEntityInWorld(LunarWorld world, float x, float y) {
        super.spawnEntityInWorld(world, x, y);
        abstractWorldIn = (AbstractWorld) world;
    }

    public void loadAnimations(Asset asset) {
        this.ult = new ParticleEffect();
        ult.load(Gdx.files.internal("effects/ult.p"), Gdx.files.internal("effects/"));
        ult.start();
        animationManager.loadAnimations(asset);
    }

    public PlayerAnimationManager getAnimations() {
        return animationManager;
    }

    /**
     * Process mouse click
     *
     * @param button button pressed
     */
    public void processInput(int button) {
        if (button == Input.Buttons.LEFT) {
            final Item item = inventory.getEquippedItem();
            if (item != null && item.isAnimated()) {
                inventory.getEquippedItem().setItemInUse(true, game.getTick(), 10);
            }
        }
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
        } else u = Gdx.input.isKeyPressed(Input.Keys.F);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (u) ult.update(delta);

        final Item item = inventory.getEquippedItem();
        if (item.isAnimated() && item.isUsing()) item.updateAnimation(game.getTick(), delta);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        super.render(batch, delta);

        if (u) {
            ult.setPosition(getX(), getY());
            ult.draw(batch);
        }

        final Item item = inventory.getEquippedItem();
        if (item.isAnimated() && item.isUsing()) item.renderAnimation(batch, this);
    }

    @Override
    public void setConnection(AbstractConnection connection) {
        super.setConnection(connection);

        // handle incoming network players
        connection.setCreatePlayerHandler(packet -> {
            final NetworkPlayer player = new NetworkPlayer(packet.getEntityId(), GlobalGameRenderer.SCALE,
                    16.0f, 18.0f, Rotation.FACING_UP);
            player.setName(packet.getUsername());
            player.setIgnoreOtherPlayerCollision(true);

            player.spawnEntityInWorld(worldIn, packet.getX(), packet.getY());
            player.initializePlayerRendererAndLoad(game.getAsset().getAssets(), true);
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
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        processInput(button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        inputsDisabled.clear();
    }
}
