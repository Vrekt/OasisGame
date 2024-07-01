package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.gui.cursor.MouseListener;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.misc.LockpickItem;
import me.vrekt.oasis.utility.collision.BasicEntityCollisionHandler;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.WorldSaveLoader;
import me.vrekt.oasis.world.interior.misc.LockDifficulty;
import me.vrekt.oasis.world.lp.ActivityManager;

/**
 * Represents an interior within the parent world;
 */
public abstract class GameWorldInterior extends GameWorld implements MouseListener {

    private static final float ENTERING_DISTANCE = 3.5f;
    // unload interiors after 30 seconds
    public static final float UNLOAD_AFTER = 30.0f;

    protected final GameWorld parentWorld;
    protected final String interiorMap;
    protected final Cursor cursor;
    protected final InteriorWorldType type;

    protected boolean enterable;
    protected final Rectangle entrance, exit;
    protected boolean isExiting;

    protected boolean isWorldActive;

    protected boolean isLocked;
    protected LockDifficulty lockDifficulty;

    // lockpick hint for this interior
    protected boolean isNear;
    protected boolean lockpickHint, lockpickUsed;

    public GameWorldInterior(GameWorld parentWorld, String interiorMap, InteriorWorldType type, Cursor cursor, Rectangle entranceBounds) {
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

    public InteriorWorldType type() {
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
     */
    public void updateWhilePlayerIsNear() {
        if (locked() && !lockpickHint && player.getInventory().containsItem(Items.LOCK_PICK)) {
            // show the hint the player can use a lockpick on this interior
            guiManager.getHudComponent().showItemHint(LockpickItem.DESCRIPTOR);
            lockpickHint = true;
        } else if (lockpickHint && !lockpickUsed && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            // use the lockpick
            ActivityManager.lockpicking(lockDifficulty, this::handleLockpickSuccess, this::handleCancelOrFailLockpick);
            guiManager.getHudComponent().removeItemHint();
            lockpickUsed = true;
        }
    }

    /**
     * When the player walks away, invalidate anything we may have done
     */
    public void invalidatePlayerNearbyState() {
        lockpickHint = false;
        lockpickUsed = false;

        guiManager.getHudComponent().removeItemHint();
    }

    /**
     * Player cancelled or failed this lockpick
     */
    private void handleCancelOrFailLockpick() {
        lockpickHint = false;
        lockpickUsed = false;
    }

    /**
     * Handle success, unlock this interior and play the sound
     */
    private void handleLockpickSuccess() {
        setLocked(false);
        invalidatePlayerNearbyState();

        GameManager.playSound(Sounds.LOCKPICK_UNLOCK, 0.55f, 1.0f, 1.0f);
        player.getInventory().removeFirst(Items.LOCK_PICK);
    }

    @Override
    public float update(float delta) {
        // check if the player entered the exit bounds
        if (!isExiting && exit.overlaps(player.getBounds())) {
            isExiting = true;
            exit();
        }

        if (isExiting) return delta;
        return super.update(delta);
    }

    @Override
    public void loadWorld(boolean isGameSave) {
        this.isGameSave = isGameSave;
        loadTiledMap(game.getAsset().getWorldMap(interiorMap), OasisGameSettings.SCALE);

        if (isGameSave) hasVisited = true;
    }

    /**
     * Enter this interior world
     */
    @Override
    public void enter() {
        if (!isWorldLoaded) {
            throw new UnsupportedOperationException("Cannot enter world without it being loaded, this is a bug. fix please!");
        }

        game.getGuiManager().resetCursor();
        game.getMultiplexer().addProcessor(this);
        game.getMultiplexer().removeProcessor(parentWorld);
        game.setScreen(this);

        hasVisited = true;
        isExiting = false;
        isWorldActive = true;
        isWorldLoaded = true;

        if (game.isMultiplayer()) player.getConnection().updateInteriorHasLoaded();
    }

    /**
     * Exit
     */
    protected void exit() {
        GameLogging.info(this, "Exiting interior");

        isWorldActive = false;
        game.getMultiplexer().removeProcessor(this);
        GameManager.transitionScreen(this, parentWorld, () -> GameManager.getWorldManager().transferOut(player, this, parentWorld));
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

        if (isWorldLoaded) {
            updateRendererMap();
            player.removeFromWorld();
            setPlayerState();
            return;
        }

        // if world was previously disposed
        if (debugRenderer == null) debugRenderer = new ShapeRenderer();
        if (world == null) world = new World(Vector2.Zero, true);

        init();

        TiledMapLoader.loadMapCollision(map, worldScale, world);
        TiledMapLoader.loadMapActions(map, worldScale, worldOrigin, exit);
        createWorldObjects(map, game.getAsset(), worldScale);
        buildEntityPathing(map, worldScale);
        createEntities(game.getAsset(), map, worldScale);

        updateRendererMap();
        player.removeFromWorld();

        world.setContactListener(new BasicEntityCollisionHandler());
        setPlayerState();
        addDefaultWorldSystems();

        finalizeWorld();

        isWorldLoaded = true;
        GameLogging.info(this, "Loaded interior successfully.");
    }

    private void setPlayerState() {
        player.createBoxBody(world);
        if (!isGameSave) player.setPosition(worldOrigin, true);
        player.updateWorldState(this);
    }

    @Override
    public void dispose() {
        GameLogging.info(this, "Unloading interior: " + type);
        isWorldLoaded = false;
        super.dispose();
    }

    @Override
    public Cursor enter(Vector3 mouse) {
        return cursor;
    }

    @Override
    public boolean within(Vector3 mouse) {
        return isMouseWithinBounds(mouse);
    }

    @Override
    public boolean clicked(Vector3 mouse) {
        if (isWithinEnteringDistance(player.getPosition()) && enterable) {
            if (locked()) {
                game.guiManager.getHudComponent().showPlayerHint(PlayerHints.DOOR_LOCKED_HINT, 4.5f, 5.0f);
                GameManager.playSound(Sounds.DOOR_LOCKED, 0.45f, 1.0f, 1.0f);
            } else {
                parentWorld.enterInterior(this);
            }
        }

        return true;
    }

    @Override
    public void exit(Vector3 mouse) {

    }

    @Override
    public boolean keyDown(int keycode) {
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        final boolean anyAction = super.touchDown(screenX, screenY, pointer, button);
        if (!anyAction) {
            player.swingItem();
        }

        return anyAction;
    }

}
