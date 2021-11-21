package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.instance.LunarInstance;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.settings.GameSettings;
import me.vrekt.oasis.utilities.collision.CollisionShapeCreator;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.common.Enterable;
import me.vrekt.oasis.world.common.InputHandler;
import me.vrekt.oasis.world.common.Interactable;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

/**
 * Represents an interior within a world.
 */
public abstract class AbstractInterior extends LunarInstance implements Disposable, Interactable, Enterable, InputHandler {

    protected final Timer timer = new Timer();

    // stage for fade in/out effects.
    protected Stage interiorStage;
    protected Table rootTable;

    protected OasisGame game;
    protected AbstractWorld worldIn;

    protected final Array<EntityInteractable> entities = new Array<>();
    protected final Vector2 spawn = new Vector2(0, 0);
    protected final Vector2 entrance = new Vector2(0, 0);
    protected final Interior interior;
    protected Player player;

    protected GlobalGameRenderer renderer;

    // only true if this interior loaded successfully.
    protected boolean enterable;

    /**
     * Initialize this interior.
     *
     * @param interior the interior map.
     */
    public AbstractInterior(Interior interior, Vector2 entrance, AbstractWorld worldIn) {
        super();
        this.interior = interior;
        this.worldIn = worldIn;
        this.entrance.set(entrance);
    }

    public boolean isEnterable(Player player) {
        return enterable && player.getPosition().dst2(entrance) <= 2f;
    }

    public void setEnterable(boolean enterable) {
        this.enterable = enterable;
    }

    /**
     * Load collision within this interior
     */
    private void loadInteriorCollision(TiledMap map) {
        final MapLayer layer = map.getLayers().get("Collision");
        if (layer != null) {
            int total = 0;
            for (MapObject object : layer.getObjects()) {
                CollisionShapeCreator.createCollisionInWorld(object, GlobalGameRenderer.SCALE, world);
                total++;
            }
            Logging.info("Interior", "Loaded a total of " + total + " collision objects within interior: " + interior);
        } else {
            Logging.warn("Interior", "Failed to load collision layer for interior: " + this.interior);
            this.enterable = false;
        }
    }

    /**
     * Load actions within this interior, like exiting and spawning.
     */
    protected void loadInteriorActions(TiledMap map) {
        final MapLayer layer = map.getLayers().get("Actions");
        final RectangleMapObject object = (RectangleMapObject) layer.getObjects().get("Spawn");
        if (object == null) {
            Logging.warn("Interior", "Failed to load spawn action(s) for interior: " + this.interior);
            this.enterable = false;
        } else {
            spawn.set(object.getRectangle().x * GlobalGameRenderer.SCALE, object.getRectangle().y * GlobalGameRenderer.SCALE);
        }
    }

    protected abstract void spawnEntities(AbstractWorld worldIn);

    protected abstract void update();

    /**
     * Add an NPC within this interior
     *
     * @param entity the entity
     */
    public void addEntityInInterior(EntityInteractable entity) {
        this.entities.add(entity);
    }

    @Override
    public boolean enterInstance(Asset asset, AbstractWorld worldIn, OasisGame game, GlobalGameRenderer renderer, Player thePlayer) {
        this.player = thePlayer;
        this.renderer = renderer;
        this.game = game;

        this.interiorStage = new Stage(new ScreenViewport());
        this.rootTable = new Table();

        this.rootTable.setFillParent(true);
        this.interiorStage.addActor(rootTable);
        this.interiorStage.setDebugAll(true);

        final Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB888);
        pixmap.setColor(0, 0, 0, 1);

        final Image image = new Image(new Texture(pixmap));
        image.setColor(0, 0, 0, 1);

        this.rootTable.add(image);
        pixmap.dispose();

        // cache this interior for 500 seconds.
        TiledMap map;
        try {
            map = asset.loadInterior(interior, 500);
            loadInteriorCollision(map);
            loadInteriorActions(map);

            spawnEntities(worldIn);
        } catch (Exception any) {
            any.printStackTrace();
            return this.enterable = false;
        }

        renderer.setDrawingMap(map, spawn.x, spawn.y);
        thePlayer.spawnEntityInInstance(this, spawn.x, spawn.y, true);
        this.register(game.getMultiplexer());
        game.setScreen(this);
        return true;
    }

    @Override
    public void show() {
        timer.start();
        rootTable.getColor().a = 1f;

        timer.scheduleTask((new Timer.Task() {
            @Override
            public void run() {
                rootTable.getColor().a -= Gdx.graphics.getDeltaTime();
                if (rootTable.getColor().a <= 0) {
                    this.cancel();
                    timer.stop();
                }
            }
        }), .025f, 0.025f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stepPhysicsWorld(delta);

        player.update(delta);
        player.interpolate(1.0f);

        updateEntities(delta);
        this.update();

        renderer.getViewport().apply();
        renderer.beginRendering();
        renderer.render();

        player.render(renderer.getBatch(), delta);
        renderEntities(renderer.getBatch());

        renderer.getBatch().end();

        game.getGui().apply();
        game.getGui().render();

        interiorStage.getViewport().apply();
        interiorStage.act();
        interiorStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        interiorStage.getViewport().update(width, height, true);
        game.getGui().resize(width, height);
        renderer.resize(width, height);
    }

    @Override
    public void register(InputMultiplexer multiplexer) {
        multiplexer.addProcessor(this);
    }

    @Override
    public void unregister(InputMultiplexer multiplexer) {
        multiplexer.removeProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == GameSettings.INTERACTION_KEY) handleInteractionKeyPressed();
        return false;
    }

    /**
     * Render entities
     *
     * @param batch b
     */
    private void renderEntities(SpriteBatch batch) {
        for (EntityInteractable entity : entities) entity.render(batch, GlobalGameRenderer.SCALE);
    }

    /**
     * Update entities within this interior
     *
     * @param delta delta
     */
    private void updateEntities(float delta) {
        for (EntityInteractable entity : entities) {
            entity.updateWithDistance(player, delta);
        }
    }

    /**
     * Exit this interior and return to {@code worldIn}
     */
    @Override
    public void exit() {
        this.world.destroyBody(player.getBody());
        player.setPosition(entrance.x, entrance.y - 0.5f);
        player.setRotation(Rotation.FACING_DOWN);
        game.transitionIntoWorld(this, player, worldIn);
    }

    @Override
    public void dispose() {
        world.dispose();
        entities.clear();
        renderer = null;
        worldIn = null;
        game = null;
    }
}
