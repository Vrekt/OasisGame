package me.vrekt.oasis.world.instance;

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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
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
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

public abstract class AbstractInstance extends LunarInstance implements Enterable, InputHandler {

    protected final Timer timer = new Timer();

    // stage for fade in/out effects.
    protected Stage stage;
    protected Table table;

    protected OasisGame game;
    protected final Vector2 entrance, spawn;
    protected final AbstractWorld worldIn;

    protected final Array<EntityInteractable> entities = new Array<>();

    protected boolean enterable = true;
    protected Player player;

    public AbstractInstance(OasisGame game, Vector2 entrance, AbstractWorld worldIn) {
        super(new World(Vector2.Zero, true), 6, 6);
        this.worldIn = worldIn;
        this.entrance = entrance;
        this.game = game;
        this.spawn = new Vector2(0, 0);

        this.stage = new Stage(new ScreenViewport());
        this.table = new Table();

        this.table.setFillParent(true);
        this.stage.addActor(table);

        final Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB888);
        pixmap.setColor(0, 0, 0, 1);

        this.table.setBackground(new TextureRegionDrawable(new Texture(pixmap)));
        pixmap.dispose();
    }

    @Override
    public void show() {
        table.getColor().a = 1f;

        timer.scheduleTask((new Timer.Task() {
            @Override
            public void run() {
                table.getColor().a -= Gdx.graphics.getDeltaTime();
                if (table.getColor().a <= 0) {
                    timer.clear();
                }
            }
        }), 0.0f, 0.02f);
    }

    /**
     * Load collision within this instance
     *
     * @param map map
     */
    protected void loadCollision(TiledMap map) {
        final MapLayer layer = map.getLayers().get("Collision");
        if (layer != null) {
            int total = 0;
            for (MapObject object : layer.getObjects()) {
                CollisionShapeCreator.createCollisionInWorld(object, GlobalGameRenderer.SCALE, world);
                total++;
            }
            Logging.info(this, "Loaded " + total + " collision objects");
        } else {
            Logging.warn(this, "Failed to load collision layer!");
            this.enterable = false;
        }
    }

    /**
     * Load actions like spawn points
     *
     * @param map map
     */
    protected void loadActions(TiledMap map) {
        final MapLayer layer = map.getLayers().get("Actions");
        final RectangleMapObject object = (RectangleMapObject) layer.getObjects().get("Spawn");
        if (object != null) {
            spawn.set(object.getRectangle().x * GlobalGameRenderer.SCALE, object.getRectangle().y * GlobalGameRenderer.SCALE);
        } else {
            Logging.warn(this, "Failed to find actions layer!");
            this.enterable = false;
        }

        loadActions(layer);
    }

    protected abstract void loadActions(MapLayer layer);

    /**
     * Spawn entities within this instance
     */
    protected abstract void spawnEntities();

    /**
     * Update this instance
     */
    protected abstract void update();

    /**
     * Handle E key being pressed.
     */
    protected abstract void handleInteractionKeyPressed();

    /**
     * Indicates this instance is ready, so show it.
     *
     * @param map map to use
     */
    protected void ready(TiledMap map) {
        game.getRenderer().setDrawingMap(map, spawn.x, spawn.y);
        player.spawnEntityInInstance(this, spawn.x, spawn.y, true);
        this.register(game.getMultiplexer());
        game.setScreen(this);
    }

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
        this.game = game;
        return true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stepPhysicsWorld(delta);

        player.update(delta);
        player.interpolate(1.0f);

        updateEntities(delta);
        this.update();

        game.getRenderer().getViewport().apply();
        game.getRenderer().beginRendering();
        game.getRenderer().render();

        player.render(game.getRenderer().getBatch(), delta);
        renderEntities(game.getRenderer().getBatch());

        game.getRenderer().getBatch().end();

        game.getGui().apply();
        game.getGui().render();

        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        game.getGui().resize(width, height);
        game.getRenderer().resize(width, height);
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

        player = null;
        game = null;
    }

}
