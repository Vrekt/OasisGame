package me.vrekt.oasis.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.gui.input.Cursor;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.network.connection.client.NetworkCallback;
import me.vrekt.oasis.utility.collision.BasicEntityCollisionHandler;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.oasis.world.interior.misc.LockDifficulty;
import me.vrekt.oasis.world.lp.ActivityManager;
import me.vrekt.oasis.world.tiled.TileMaterialType;
import me.vrekt.oasis.world.utility.Interaction;
import me.vrekt.shared.packet.client.interior.C2STryEnterInteriorWorld;
import me.vrekt.shared.packet.server.interior.S2CEnterInteriorWorld;
import me.vrekt.shared.protocol.Packets;

/**
 * Represents an interior within the parent world;
 */
public abstract class GameWorldInterior extends GameWorld {

    private static final float ENTERING_DISTANCE = 5.0f;

    protected final GameWorld parentWorld;
    protected final String interiorMap;
    protected final Cursor cursor;
    protected final Interior type;

    protected boolean enterable;
    protected final Rectangle entrance, exit;
    protected boolean isExiting;

    protected boolean isWorldActive;

    protected boolean isLocked;
    protected LockDifficulty lockDifficulty;

    // lockpick hint for this interior
    protected boolean isNear;
    protected boolean lockpickUsed;

    protected boolean requiresNearUpdating = true;
    protected boolean doTicking;

    public GameWorldInterior(GameWorld parentWorld, String interiorMap, Interior type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld.getGame(), parentWorld.player(), new World(Vector2.Zero, true));

        this.parentWorld = parentWorld;
        this.interiorMap = interiorMap;
        this.type = type;
        this.cursor = cursor;
        this.entrance = entranceBounds;
        this.exit = new Rectangle();
        this.worldName = type.name();

        this.saveLoader = new WorldSaveLoader(this);
    }

    public Interior type() {
        return type;
    }

    @Override
    public boolean isInterior() {
        return true;
    }

    /**
     * @return the parent world
     */
    public GameWorld getParentWorld() {
        return parentWorld;
    }

    @Override
    public TileMaterialType getMaterialAt() {
        return mapCache == null ? TileMaterialType.NONE : super.getMaterialAt();
    }

    /**
     * @param clicked the mouse click position
     * @return if the entrance was clicked on.
     */
    public boolean clickedOn(Vector3 clicked) {
        return entrance.contains(clicked.x, clicked.y);
    }

    /**
     * @param clicked the mouse click position
     * @return if the mouse is over the entrance
     */
    public boolean isMouseWithinBounds(Vector3 clicked) {
        return clickedOn(clicked);
    }

    /**
     * @param position player position
     * @return {@code true} if the player is within entering distance
     */
    public boolean isWithinEnteringDistance(Vector2 position) {
        return position.dst2(entrance.x, entrance.y) <= ENTERING_DISTANCE;
    }

    /**
     * @return {@code true} if this interior should be updated while the player is near
     */
    public boolean requiresNearUpdating() {
        return requiresNearUpdating;
    }

    /**
     * @return the entrance area of this interior
     */
    public Rectangle entrance() {
        return entrance;
    }

    /**
     * Set if this interior  can be entered.
     *
     * @param enterable state
     */
    public void setEnterable(boolean enterable) {
        this.enterable = enterable;
    }

    /**
     * @return {@code true} if this interior can be entered.
     */
    public boolean isEnterable() {
        return enterable;
    }

    /**
     * @return if this interior is locked, requires lockpicking.
     */
    public boolean locked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public LockDifficulty lockDifficulty() {
        return lockDifficulty;
    }

    public void setLockDifficulty(LockDifficulty lockDifficulty) {
        this.lockDifficulty = lockDifficulty;
    }

    /**
     * Player is near this interior
     *
     * @param near state
     */
    public void setNear(boolean near) {
        isNear = near;
    }

    /**
     * @return if the player is near this interior
     */
    public boolean isNear() {
        return isNear;
    }

    /**
     * Update this interior if the player is near
     * We use parent interaction manager here because we are not actively within this interior.
     */
    public void updateWhilePlayerIsNear(GameWorld parent) {
        if (locked()
                && !lockpickUsed
                && !parent.interactionManager.is(Interaction.LOCKPICK)
                && player.getInventory().containsItem(Items.LOCK_PICK)) {
            parent.interactionManager.showLockpickingInteraction();
        } else if (parent.interactionManager.active() == Interaction.LOCKPICK
                && !lockpickUsed
                && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            // use the lockpick
            ActivityManager.lockpicking(lockDifficulty, this::handleLockpickSuccess, this::handleCancelOrFailLockpick);
            parent.interactionManager.hideInteractions();
            lockpickUsed = true;
        } else if (!locked()) {
            if (!parent.interactionManager.is(Interaction.ENTER) && !player.isEnteringNewWorld()) {
                parent.interactionManager.showEnterInteraction(this);
            }
        }
    }

    /**
     * Tick this world while the player is not inside
     */
    public void enableTicking() {
        doTicking = true;
    }

    /**
     * @return {@code true} if this world should be ticked.
     */
    public boolean doTicking() {
        return doTicking;
    }

    /**
     * When the player walks away, invalidate anything we may have done
     */
    public void invalidatePlayerNearbyState() {
        lockpickUsed = false;

        parentWorld.interactionManager.hideInteractions();
    }

    /**
     * Player cancelled or failed this lockpick
     */
    private void handleCancelOrFailLockpick() {
        lockpickUsed = false;
    }

    /**
     * Handle success, unlock this interior and play the sound
     */
    private void handleLockpickSuccess() {
        setLocked(false);

        lockpickUsed = false;

        GameManager.playSound(Sounds.LOCKPICK_UNLOCK, 0.5f, 1.0f, 0.0f);
        player.getInventory().removeFirst(Items.LOCK_PICK);
    }

    @Override
    public void tick(float delta) {
        // check if the player entered the exit bounds
        if (!isExiting && exit.overlaps(player.bb())) {
            isExiting = true;
            exit();
        }
    }

    @Override
    public float tickWorldPhysicsSim(float delta) {
        // do not tick physics while we are exiting
        if (isExiting) return delta;
        return super.tickWorldPhysicsSim(delta);
    }

    @Override
    public void loadWorldTiledMap(boolean isGameSave) {
        this.isGameSave = isGameSave;
        loadTiledMap(game.asset().getWorldMap(interiorMap), OasisGameSettings.SCALE);

        if (isGameSave) hasVisited = true;
    }

    /**
     * Enter this interior world
     */
    @Override
    public void enterWorld() {
        if (!isWorldLoaded) throw new UnsupportedOperationException("Interior is not loaded.");

        // where we will spawn
        player.getTransformComponent().position.set(worldOrigin);

        player.removeFromWorld();
        updateEnteringWorldState();

        game.getGuiManager().resetCursor();
        game.multiplexer().addProcessor(this);
        game.worldManager().setActiveWorld(this);

        hasVisited = true;
        isExiting = false;
        isWorldActive = true;
        isWorldLoaded = true;
    }

    /**
     * Exit
     */
    @Override
    public void exit() {
        GameLogging.info(this, "Exiting interior");

        isWorldActive = false;
        game.multiplexer().removeProcessor(this);

        GameManager.transitionWorlds(this, parentWorld, () -> game.worldManager().transferOut(player, this, parentWorld));
    }

    /**
     * @return the cursor type
     */
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public void loadTiledMap(TiledMap worldMap, float worldScale) {
        this.map = worldMap;
        if (isWorldLoaded) return;

        // if world was previously disposed
        if (debugRenderer == null) debugRenderer = new ShapeRenderer();
        if (world == null) world = new World(Vector2.Zero, true);
        boxRenderer = new Box2DDebugRenderer();

        init();

        TiledMapLoader.loadMapCollision(map, worldScale, world);
        TiledMapLoader.loadMapActions(map, worldScale, worldOrigin, exit);
        createWorldObjects(map, game.asset(), worldScale);
        buildEntityPathing(map, worldScale);
        createEntities(game.asset(), map, worldScale);

        world.setContactListener(new BasicEntityCollisionHandler());
        addDefaultWorldSystems();

        finalizeWorld();

        isWorldLoaded = true;
        GameLogging.info(this, "Loaded interior successfully.");
    }

    /**
     * Attempt to enter this interior
     */
    protected void attemptEnter() {
        if (isWithinEnteringDistance(player.getPosition()) && enterable) {
            if (locked()) {
                game.guiManager.getHintComponent().showPlayerHint(PlayerHints.DOOR_LOCKED_HINT, 4.5f, 5.0f);
                GameManager.playSound(Sounds.DOOR_LOCKED, 0.45f, 1.0f, 0.0f);
            } else {
                // check with the server if we can actually enter this interior
                if (game.isInMultiplayerGame()) {
                    NetworkCallback.immediate(new C2STryEnterInteriorWorld(type))
                            .waitFor(Packets.S2C_TRY_ENTER_INTERIOR)
                            .timeoutAfter(2000)
                            .ifTimedOut(() -> GameLogging.warn(this, "Join interior world timed out!"))
                            .sync()
                            .accept(packet -> {
                                // ensure this world is networked
                                this.isNetworked = true;
                                final S2CEnterInteriorWorld response = (S2CEnterInteriorWorld) packet;
                                if (response.isEnterable()) {
                                    parentWorld.enterInterior(this);
                                }
                            }).send();
                } else if (game.isHostingMultiplayerGame()) {
                    // TODO:
                } else {
                    // singleplayer game
                    parentWorld.enterInterior(this);
                }
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        final boolean anyAction = super.touchDown(screenX, screenY, pointer, button);
        if (!anyAction) {
            player.swingItem();
        }

        return anyAction;
    }

    @Override
    public void dispose() {
        GameLogging.info(this, "Unloading interior: " + type);
        isWorldLoaded = false;
        super.dispose();
    }

}
