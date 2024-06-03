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
import me.vrekt.oasis.utility.input.InteriorMouseHandler;
import me.vrekt.oasis.utility.collision.BasicEntityCollisionHandler;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.GameWorld;

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

        parentWorld.getGame().getMultiplexer().addProcessor(this);
    }

    public InteriorWorldType type() {
        return type;
    }

    @Override
    protected void loadNetworkComponents() {

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

    /**
     * Enter this interior world
     */
    @Override
    public void enter() {
        GameLogging.info(this, "Entering interior %s", worldName);
        guiManager.resetCursor();

        isExiting = false;
        isWorldActive = true;
        create(game.getAsset().getWorldMap(interiorMap), OasisGameSettings.SCALE);
        game.getMultiplexer().removeProcessor(parentWorld);
        game.setScreen(this);
        isWorldLoaded = true;


    }

    /**
     * Exit
     */
    protected void exit() {
        isWorldActive = false;

        GameLogging.info(this, "Exiting interior");
        GameManager.transitionScreen(this, parentWorld, () -> GameManager.getWorldManager().transfer(player, this, parentWorld));
    }

    /**
     * @return the cursor type
     */
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public void create(TiledMap worldMap, float worldScale) {
        this.map = worldMap;

        if (debugRenderer == null) debugRenderer = new ShapeRenderer();

        if (isWorldLoaded) {
            // indicates this instance is already loaded into memory.
            updateRendererMap();
            player.removeFromWorld();
            setPlayerState();
            return;
        }

        if (world == null) {
            world = new World(Vector2.Zero, true);
        }

        preLoad();

        TiledMapLoader.loadMapCollision(map, worldScale, world);
        TiledMapLoader.loadMapActions(map, worldScale, worldOrigin, exit);
        createWorldObjects(map, game.getAsset(), worldScale);
        buildEntityPathing(map, worldScale);
        createEntities(game, game.getAsset(), map, worldScale);

        updateRendererMap();

        // remove player from parent world
        player.removeFromWorld();
        load();

        world.setContactListener(new BasicEntityCollisionHandler());
        setPlayerState();
        addDefaultWorldSystems();

        isWorldLoaded = true;
        GameLogging.info(this, "Loaded interior successfully.");
    }

    private void setPlayerState() {
        player.createBoxBody(world);
        player.setPosition(worldOrigin, true);
        player.updateWorldState(this);
    }

    @Override
    public void dispose() {
        GameLogging.info(this, "Unloading interior: " + type);
        isWorldLoaded = false;
        super.dispose();
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
