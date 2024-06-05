package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.utility.collision.BasicEntityCollisionHandler;
import me.vrekt.oasis.utility.input.InteriorMouseHandler;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.WorldSaveLoader;

/**
 * Represents an interior within the parent world;
 */
public abstract class GameWorldInterior extends GameWorld {

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

    protected InteriorMouseHandler mouseHandler;
    protected boolean mouseOver;
    protected boolean isWorldActive;

    public GameWorldInterior(GameWorld parentWorld, String interiorMap, InteriorWorldType type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld.getGame(), parentWorld.getLocalPlayer(), new World(Vector2.Zero, true));

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

    public void attachMouseHandler(InteriorMouseHandler mouseHandler) {
        this.mouseHandler = mouseHandler;
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

    @Override
    public float update(float delta) {

        // check if the player entered the exit bounds
        if (!isExiting && exit.contains(player.getPosition())) {
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
    }

    /**
     * Exit
     */
    protected void exit() {
        GameLogging.info(this, "Exiting interior");

        isWorldActive = false;
        game.getMultiplexer().removeProcessor(this);
        GameManager.transitionScreen(this, parentWorld, () -> GameManager.getWorldManager().transfer(player, this, parentWorld));
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
        createEntities(game, game.getAsset(), map, worldScale);

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
    public boolean keyDown(int keycode) {
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (isWorldActive) {
            final boolean anyAction = super.touchDown(screenX, screenY, pointer, button);
            if (!anyAction) {
                player.swingItem();
            }
            return anyAction;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (isWorldActive) {
            return super.mouseMoved(screenX, screenY);
        } else {
            if (mouseHandler != null && parentWorld.shouldUpdateMouseState()) {
                final boolean result = isEnterable() && isMouseWithinBounds(parentWorld.getCursorInWorld());
                if (result) {
                    mouseOver = true;
                    mouseHandler.handle(this, false);
                } else if (mouseOver) {
                    mouseOver = false;
                    mouseHandler.handle(this, true);
                }
            }
        }
        return false;
    }
}