package me.vrekt.oasis.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.LunarEntity;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.system.EntityInteractableAnimationSystem;
import me.vrekt.oasis.entity.npc.system.EntityUpdateSystem;
import me.vrekt.oasis.entity.player.mp.OasisNetworkPlayer;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.graphics.OasisTiledRenderer;
import me.vrekt.oasis.graphics.Renderable;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.utility.collision.CollisionShapeCreator;
import me.vrekt.oasis.utility.logging.Logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Represents a base world within the game
 */
public abstract class OasisWorld extends LunarWorld<OasisPlayerSP, OasisNetworkPlayer, Entity> implements InputProcessor {

    private final OasisGame game;
    private final SpriteBatch batch;
    protected final OasisTiledRenderer renderer;

    protected int width, height;

    // pause state
    protected FrameBuffer fbo;
    protected TextureRegion fboTexture;
    protected boolean paused, hasFbo;

    protected GameGui gui;

    private final ConcurrentHashMap<EntityInteractable, Float> nearbyEntities = new ConcurrentHashMap<>();
    private final List<ParticleEffect> effects = new ArrayList<>();

    public OasisWorld(OasisGame game, OasisPlayerSP player, World world) {
        super(player, world);
        this.game = game;
        this.renderer = game.getRenderer();
        this.batch = game.getBatch();
        this.gui = game.getGui();
    }

    /**
     * Load this world
     */
    public void loadIntoWorld() {
        if (player.isInWorld()) {
            Logging.info(this, "Removing local player from world.");
            player.removeEntityInWorld(player.getWorldIn());
            player.setGameWorldIn(null);
        }
    }

    /**
     * Load the local player into this world.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    public void loadWorld(TiledMap worldMap, float worldScale) {
        loadMapActions(worldMap, worldScale);
        loadMapCollision(worldMap, worldScale);
        loadInteractableEntities(game, Asset.get(), worldMap, worldScale);
        loadParticleEffects(worldMap, Asset.get(), worldScale);

        this.renderer.setTiledMap(worldMap, spawn.x, spawn.y);
        this.width = renderer.getWidth();
        this.height = renderer.getHeight();
        game.getMultiplexer().addProcessor(this);

        engine.addSystem(new EntityInteractableAnimationSystem(engine));
        engine.addSystem(new EntityUpdateSystem(game, this));

        // initialize player in this world.
        player.spawnEntityInWorld(this, spawn.x, spawn.y);
        player.setGameWorldIn(this);
    }

    @Override
    public void spawnEntityInWorld(LunarEntity entity, float x, float y) {
        if (entity instanceof OasisPlayerSP) return;
        super.spawnEntityInWorld(entity, x, y);
    }

    /**
     * General functions for collecting rectangle objects and handling them
     *
     * @param worldMap   map
     * @param worldScale scale
     * @param layerName  layer to get
     * @param handler    handler
     * @return the result (if the layer was found)
     */
    protected boolean loadMapObjects(TiledMap worldMap, float worldScale, String layerName, BiConsumer<MapObject, Rectangle> handler) {
        final MapLayer layer = worldMap.getLayers().get(layerName);
        if (layer == null) {
            Logging.warn(this, "Failed to load layer: " + layerName);
            return false;
        }

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                final Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                rectangle.x = rectangle.x * worldScale;
                rectangle.y = rectangle.y * worldScale;
                rectangle.width = rectangle.width * worldScale;
                rectangle.height = rectangle.height * worldScale;
                handler.accept(object, rectangle);
            } else {
                Logging.warn(this, "Unknown map object in layer: " + layerName + " {" + object.getName() + "}");
            }
        }

        return true;
    }

    /**
     * Load map actions like spawn points.
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    protected void loadMapActions(TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Actions", (object, rectangle) -> {
            if (object.getName().equalsIgnoreCase("WorldSpawn")) {
                Logging.info(this, "Found WorldSpawn @ " + rectangle.x + ":" + rectangle.y);
                spawn.set(rectangle.x, rectangle.y);
            }

            // others...
        });

        if (!result) Logging.warn(this, "Failed to find world spawn.");
    }

    /**
     * Load map collision
     *
     * @param worldMap   the map
     * @param worldScale the scale
     */
    protected void loadMapCollision(TiledMap worldMap, float worldScale) {
        final MapLayer layer = worldMap.getLayers().get("Collision");
        if (layer == null) {
            Logging.warn(this, "Failed to find collision layer.");
            return;
        }

        int loaded = 0;
        for (MapObject object : layer.getObjects()) {
            CollisionShapeCreator.createCollisionInWorld(object, worldScale, world);
            loaded++;
        }

        Logging.info(this, "Loaded a total of " + loaded + " collision objects.");
    }

    /**
     * Load world NPCs
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadInteractableEntities(OasisGame game, Asset asset, TiledMap worldMap, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Interactable", (object, rectangle) -> {
            // find who this NPC is.
            final EntityNPCType type = EntityNPCType.valueOf(object.getName().toUpperCase());
            // create it and load
            final EntityInteractable entity = type.create(new Vector2(rectangle.x, rectangle.y), game, this);
            entity.load(asset);

            entity.setPosition(rectangle.x, rectangle.y, true);

            entity.setEntityId(this.entities.size() + 1);
            this.entities.put(entity.getEntityId(), entity);
            engine.addEntity(entity.getEntity());
        });
        if (result) Logging.info(this, "Loaded " + (entities.size()) + " entities.");
    }

    /**
     * Load particles
     *
     * @param worldMap   the map of the world
     * @param asset      asset
     * @param worldScale the scale of the world
     */
    protected void loadParticleEffects(TiledMap worldMap, Asset asset, float worldScale) {
        final boolean result = loadMapObjects(worldMap, worldScale, "Particles", (object, rectangle) -> {
            final ParticleEffect effect = new ParticleEffect();
            effect.load(Gdx.files.internal("world/asset/" + object.getName()), asset.getAtlasAssets());
            effect.setPosition(rectangle.x, rectangle.y);
            effect.start();

            this.effects.add(effect);
        });

        if (result) Logging.info(this, "Loaded " + (effects.size()) + " particle effects.");
        if (!result) Logging.warn(this, "Failed to find particle layer.");
    }

    @Override
    public void show() {
        Logging.info(this, "Showing world.");
    }

    @Override
    public void hide() {
        Logging.info(this, "Hiding world.");
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void render(float delta) {
        if (paused && !hasFbo) fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (paused && !hasFbo) {
            // capture fbo.
            renderer.getViewport().apply();
            renderInternal(delta);
            hasFbo = true;
        } else if (paused) {
            // draw fbo.

            gui.applyStageViewport();
            batch.setProjectionMatrix(gui.getCamera().combined);
            batch.begin();

            if (fboTexture == null) {
                fboTexture = new TextureRegion(fbo.getColorBufferTexture());
                fboTexture.flip(false, true);
            } else if (fboTexture.getTexture() == null) {
                fboTexture.setTexture(fbo.getColorBufferTexture());
            }

            // draw FBO
            batch.draw(fboTexture, 0, 0);
        } else {
            if (fboTexture != null) fboTexture.setTexture(null);
            renderer.getViewport().apply();
            // no pause state, render normally.
            renderInternal(delta);
        }

        // end
        if (paused && hasFbo) fbo.end();
        if (paused && batch.isDrawing()) batch.end();
    }

    @Override
    public float update(float d) {
        return super.update(d);
    }

    private void renderInternal(float delta) {
        this.update(delta);
        this.renderWorld(game.getBatch(), delta);
    }

    /**
     * Render this world
     *
     * @param batch batch
     * @param delta delta
     */
    public void renderWorld(SpriteBatch batch, float delta) {
        renderer.beginRendering();
        renderer.render();

        // render MP players first,
        for (OasisNetworkPlayer player : players.values()) {
            if (player.isInView(renderer.getCamera())) {
                player.render(batch, delta);
            }
        }

        for (LunarEntity value : entities.values()) {
            if (value instanceof Renderable) {
                if (((Renderable) value).isInView(renderer.getCamera())) {
                    ((Renderable) value).render(batch, delta);
                }
            }
        }

        // render particles
        for (ParticleEffect effect : effects) {
            effect.update(delta);
            effect.draw(batch);
        }

        // render local player next
        player.render(batch, delta);

        batch.end();

        // render gui
        gui.applyStageViewport();
        gui.render();
    }

    public EntityInteractable getByType(EntityNPCType type) {
        return (EntityInteractable) entities.values()
                .stream()
                .filter(entity -> entity instanceof EntityInteractable && ((EntityInteractable) entity).getType() == type)
                .findFirst()
                .orElse(null);
    }

    public EntityInteractable getClosest() {
        return Collections.min(nearbyEntities.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public ConcurrentHashMap<EntityInteractable, Float> getNearbyEntities() {
        return nearbyEntities;
    }

    public void handleInteraction() {

    }

    public boolean checkInteractionKeyPress(int code) {
        return code == OasisGameSettings.INTERACTION_KEY;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (checkInteractionKeyPress(keycode)) {
            handleInteraction();
        }
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
        if (amountY > 0) {
            renderer.getCamera().zoom += 0.02f;
        } else {
            renderer.getCamera().zoom -= 0.02f;
        }
        return true;
    }
}
