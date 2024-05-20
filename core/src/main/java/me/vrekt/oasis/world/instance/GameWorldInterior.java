package me.vrekt.oasis.world.instance;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.utility.collision.BasicEntityCollisionHandler;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.InteriorWorldType;

/**
 * Represents an interior within the parent world;
 */
public abstract class GameWorldInterior extends GameWorld {

    private static final float ENTERING_DISTANCE = 3.5f;

    protected final GameWorld parentWorld;
    protected final String interiorMap;
    protected final Cursor cursor;
    protected final InteriorWorldType type;

    protected boolean enterable;
    protected final Rectangle entrance, exit;
    protected boolean isExiting;

    public GameWorldInterior(GameWorld parentWorld, String interiorMap, InteriorWorldType type, Cursor cursor, Rectangle entranceBounds) {
        super(parentWorld.getGame(), parentWorld.getLocalPlayer(), new World(Vector2.Zero, true));

        this.parentWorld = parentWorld;
        this.interiorMap = interiorMap;
        this.type = type;
        this.cursor = cursor;
        this.entrance = entranceBounds;
        this.exit = new Rectangle();
        this.worldName = type.name();
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
        create(game.getAsset().getWorldMap(interiorMap), OasisGameSettings.SCALE);
        game.getMultiplexer().removeProcessor(parentWorld);
        game.setScreen(this);
        isWorldLoaded = true;
    }

    /**
     * Exit
     */
    protected void exit() {
        GameLogging.info(this, "Exiting interior");
        parentWorld.fadeInThenEnter(this);
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

        if (isWorldLoaded) {
            // indicates this instance is already loaded into memory.
            updateRendererMap();
            game.getMultiplexer().addProcessor(this);
            player.removeFromWorld();
            setPlayerState();
            return;
        }

        preLoad();

        TiledMapLoader.loadMapCollision(map, worldScale, world);
        TiledMapLoader.loadMapActions(map, worldScale, spawn, exit);
        createWorldObjects(map, game.getAsset(), worldScale);
        createEntities(game, game.getAsset(), map, worldScale);
        findEntityPathing(map, worldScale);

        updateRendererMap();
        game.getMultiplexer().addProcessor(this);

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
        player.spawnInWorld(this, spawn);
        player.setInInteriorWorld(true);
        player.setInteriorWorldIn(this);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
