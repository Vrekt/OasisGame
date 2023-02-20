package me.vrekt.oasis.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.LunarEntity;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
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
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.utility.collision.CollisionShapeCreator;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.oasis.world.obj.WorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents a base world within the game
 */
public abstract class OasisWorld extends LunarWorld<OasisPlayerSP, OasisNetworkPlayer, Entity> implements InputProcessor {

    protected final OasisGame game;
    protected final OasisPlayerSP localPlayer;

    protected TiledMap map;

    private final SpriteBatch batch;
    protected final OasisTiledRenderer renderer;
    protected final Vector3 cursorInWorld = new Vector3();
    protected final Vector3 cursorInScreen = new Vector3();
    protected boolean cursorChanged;

    protected int width, height;

    // pause state
    protected FrameBuffer fbo;
    protected TextureRegion fboTexture;
    protected boolean paused, hasFbo;

    protected GameGui gui;

    protected final ConcurrentHashMap<EntityInteractable, Float> nearbyEntities = new ConcurrentHashMap<>();
    protected final List<ParticleEffect> effects = new ArrayList<>();

    // objects within this world
    protected final CopyOnWriteArraySet<WorldObject> worldObjects = new CopyOnWriteArraySet<>();
    protected final CopyOnWriteArraySet<InteractableWorldObject> interactableWorldObjects = new CopyOnWriteArraySet<>();

    protected final Map<String, Interior> interiors = new HashMap<>();
    protected float updateTime, renderTime;

    public OasisWorld(OasisGame game, OasisPlayerSP player, World world) {
        super(player, world);
        this.localPlayer = player;
        this.game = game;
        this.renderer = game.getRenderer();
        this.batch = game.getBatch();
        this.gui = game.getGui();

        configuration.stepTime = 1 / 240f;
    }

    public OasisPlayerSP getLocalPlayer() {
        return localPlayer;
    }

    public OasisGame getGame() {
        return game;
    }

    public void showWorld() {
        this.renderer.setTiledMap(map, spawn.x, spawn.y);
    }

    public void clearCollisionBodies() {
        final Array<Body> bodies = new Array<>();
        world.getBodies(bodies);

        for (int i = 0; i < bodies.size; i++) {
            if (!world.isLocked())
                world.destroyBody(bodies.get(i));
        }
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
     * TODO: Reinit collision when entering back if not done already.
     * TODO: NEw worlds per instance? might be hard with networking probably
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    public void loadWorld(TiledMap worldMap, float worldScale) {
        this.map = worldMap;

        TiledMapLoader.loadMapActions(worldMap, worldScale, spawn, new Rectangle());
        TiledMapLoader.loadMapCollision(worldMap, worldScale, world, this);
        loadInteractableEntities(game, game.getAsset(), worldMap, worldScale);
        loadParticleEffects(worldMap, game.getAsset(), worldScale);
        loadWorldObjects(worldMap, game.getAsset(), worldScale);
        loadWorldInteriors(worldMap, worldScale);

        configuration.worldScale = worldScale;

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
     * Load world NPCs
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    protected void loadInteractableEntities(OasisGame game, Asset asset, TiledMap worldMap, float worldScale) {
        TiledMapLoader.loadMapObjects(worldMap, worldScale, "Entities", (object, rectangle) -> {
            final EntityNPCType type = EntityNPCType.findType(object);
            if (type != null) {
                final EntityInteractable entity = type.create(new Vector2(rectangle.x, rectangle.y), game, this);
                entity.load(asset);
                addEntity(entity);
            } else {
                Logging.warn(this, "Found invalid entity: " + object);
            }
        });

        Logging.info(this, "Loaded " + (entities.size()) + " entities.");
    }

    /**
     * Add an interactable entity to this world
     * Automatically assigns ID and to engine
     *
     * @param entity the entity
     */
    public void addEntity(EntityInteractable entity) {
        entity.setEntityId(this.entities.size() + 1);
        this.entities.put(entity.getEntityId(), entity);
        engine.addEntity(entity.getEntity());
    }

    /**
     * Load particles
     *
     * @param worldMap   the map of the world
     * @param asset      asset
     * @param worldScale the scale of the world
     */
    protected void loadParticleEffects(TiledMap worldMap, Asset asset, float worldScale) {
        final boolean result = TiledMapLoader.loadMapObjects(worldMap, worldScale, "Particles", (object, rectangle) -> {
            final ParticleEffect effect = new ParticleEffect();
            effect.load(Gdx.files.internal("world/asset/" + object.getName()), asset.getAtlasAssets());
            effect.setPosition(rectangle.x, rectangle.y);
            effect.start();
        });

        if (result) Logging.info(this, "Loaded " + (effects.size()) + " particle effects.");
    }

    /**
     * Load world objects
     *
     * @param worldMap   the map of the world
     * @param worldScale the scale of the world
     */
    protected void loadWorldObjects(TiledMap worldMap, Asset asset, float worldScale) {
        TiledMapLoader.loadMapObjects(worldMap, worldScale, "WorldObjects", (object, rectangle) -> {
            // assign n find texture
            final TextureRegion texture = asset.get(object.getProperties().get("texture", String.class));
            final boolean interactable = object.getProperties().get("interactable", Boolean.class);

            if (interactable) {
                final InteractableWorldObject worldObject = Pools.obtain(WorldInteractionType.getInteractionFromName(object.getProperties().get("interaction_type", String.class)));
                worldObject.load(asset);
                worldObject.initialize(this,
                        rectangle.x - ((texture.getRegionWidth() / 2f) * OasisGameSettings.SCALE),
                        rectangle.y - ((texture.getRegionHeight() / 3f) * OasisGameSettings.SCALE),
                        texture.getRegionWidth() * OasisGameSettings.SCALE,
                        texture.getRegionHeight() * OasisGameSettings.SCALE
                );

                worldObject.setTexture(texture);

                loadWorldObjectEffects(worldObject, object, rectangle);
                loadWorldObjectBody(worldObject, rectangle, texture);
                this.interactableWorldObjects.add(worldObject);
            } else {
                final WorldObject wb = Pools.obtain(WorldObject.class);
                wb.load(asset);
                // TODO: init WorldObjectType

                loadWorldObjectEffects(wb, object, rectangle);
                loadWorldObjectBody(wb, rectangle, texture);
                this.worldObjects.add(wb);
            }
        });

        Logging.info(this, "Loaded " + (interactableWorldObjects.size()) + " interactable objects.");
        Logging.info(this, "Loaded " + (worldObjects.size()) + " world objects.");
    }

    /**
     * Load particle effects for a world object, interactable or not.
     *
     * @param wb        wb
     * @param object    obj
     * @param rectangle rect
     */
    private void loadWorldObjectEffects(WorldObject wb, MapObject object, Rectangle rectangle) {
        // load each particle within properties
        object.getProperties().getKeys().forEachRemaining(key -> {
            // find a particle + int identifier
            if (key.contains("particle")) {
                final ParticleEffect effect = new ParticleEffect();
                effect.load(Gdx.files.internal("world/asset/" + object.getProperties().get(key, String.class)), game.getAsset().getAtlasAssets());
                effect.setPosition(rectangle.x, rectangle.y + object.getProperties().get("offset" + (StringUtils.getDigits(key)), 0.0f, float.class));
                effect.start();

                // add this new effect to environ object
                wb.getEffects().add(effect);
            }
        });
    }

    private void loadWorldObjectBody(WorldObject wb, Rectangle rectangle, TextureRegion texture) {
        // create collision body, offset position to fit within bounds.
        final Body body = CollisionShapeCreator.createPolygonShapeInWorld(
                rectangle.x - ((texture.getRegionWidth() / 2f) * OasisGameSettings.SCALE),
                rectangle.y - ((texture.getRegionHeight() / 3f) * OasisGameSettings.SCALE),
                texture.getRegionWidth(),
                texture.getRegionHeight(),
                OasisGameSettings.SCALE,
                true,
                world
        );

        wb.setCollisionBody(body);
    }

    /**
     * Load interiors within this world
     *
     * @param worldMap   map
     * @param worldScale scale
     */
    public void loadWorldInteriors(TiledMap worldMap, float worldScale) {
        final boolean result = TiledMapLoader.loadMapObjects(worldMap, worldScale, "Interior", (object, rectangle) -> {
            final boolean enterable = object.getProperties().get("enterable", true, Boolean.class);
            final String interiorName = object.getProperties().get("interior_name", null, String.class);
            if (interiorName != null && enterable) {
                this.interiors.put(interiorName, new Interior(this, interiorName, object.getProperties().get("cursor", String.class), rectangle));
            }
        });

        if (result) Logging.info(this, "Loaded " + (interiors.size()) + " interiors.");
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
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void render(float delta) {
        final long now = System.currentTimeMillis();

        // handle parent instance if in one
        if (player.getInstanceIn() != null) {
            player.getInstanceIn().render(delta);
            return;
        }

        if (paused && !hasFbo) fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (paused && !hasFbo) {
            // capture fbo.
            renderer.getViewport().apply();
            renderInternal(delta);
            hasFbo = true;
        } else if (paused) {
            // draw fbo.

            gui.getStage().getViewport().apply();
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

        renderTime = System.currentTimeMillis() - now;
    }

    @Override
    public float update(float d) {
        final long now = System.currentTimeMillis();
        float d1 = super.update(d);
        this.player.getInventory().update();

        // update world cursor
        renderer.getCamera().unproject(cursorInWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        game.getGui().getCamera().unproject(cursorInScreen.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        boolean hasEntity = true;

        for (EntityInteractable entityInteractable : nearbyEntities.keySet()) {
            if (entityInteractable.isMouseInEntityBounds(cursorInWorld)) {
                // mouse is over this entity
                if (!cursorChanged) {
                    setCursorInWorld("ui/dialog_cursor.png");
                }
                break;
            } else {
                hasEntity = false;
            }
        }

        boolean hasInterior = true;
        for (Interior interior : interiors.values()) {
            if (interior.isMouseWithinBounds(cursorInWorld)) {
                if (!cursorChanged) {
                    setCursorInWorld(interior.getCursor());
                }
                break;
            } else {
                hasInterior = false;
            }
        }

        // check for environment objects
        boolean hasObj = false;
        if (!hasEntity) {
            for (InteractableWorldObject worldObject : interactableWorldObjects) {
                if (worldObject.clickedOn(cursorInWorld) && worldObject.getCursor() != null) {
                    setCursorInWorld(worldObject.getCursor());
                    hasObj = true;
                    break;
                }
            }
        }

        if (!hasEntity && !hasObj && !hasInterior && cursorChanged) {
            resetCursor();
        }

        updateTime = System.currentTimeMillis() - now;
        return d1;
    }

    public float getUpdateTime() {
        return updateTime;
    }

    public float getRenderTime() {
        return renderTime;
    }

    private void setCursorInWorld(String cursorInWorld) {
        this.cursorChanged = true;
        Pixmap pm = new Pixmap(Gdx.files.internal(cursorInWorld));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();
    }

    private void resetCursor() {
        this.cursorChanged = false;
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
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

        // general world objects
        for (WorldObject object : worldObjects) {
            object.render(batch);
            if (object.playEffects()) {
                object.renderEffects(batch, delta);
            }
        }

        // interactions
        for (InteractableWorldObject worldObject : interactableWorldObjects) {
            worldObject.render(batch);
            if (worldObject.playEffects()) {
                worldObject.renderEffects(batch, delta);
            }

            if (worldObject.isWithinUpdateDistance(player.getPosition())
                    && worldObject.isInteractedWith()) {
                worldObject.update();
            }
        }

        // render particles
        for (ParticleEffect effect : effects) {
            effect.update(delta);
            effect.draw(batch);
        }

        // render local player next
        player.render(batch, delta);
    }

    /**
     * End world rendering and render player UIs
     */
    public void endRender() {
        batch.end();
        gui.render();
    }

    public EntityInteractable getByType(EntityNPCType type) {
        return (EntityInteractable) entities.values()
                .stream()
                .filter(entity -> entity instanceof EntityInteractable && ((EntityInteractable) entity).getType() == type)
                .findFirst()
                .orElse(null);
    }

    public ConcurrentHashMap<EntityInteractable, Float> getNearbyEntities() {
        return nearbyEntities;
    }

    public Vector3 getCursorInWorld() {
        return cursorInWorld;
    }

    public Vector3 getCursorInScreen() {
        return cursorInScreen;
    }

    /**
     * Interact with an entity if they were clicked on.
     * Finds the first entity and does not allow multiple
     */
    protected void interactWithEntity() {
        // find entity clicked on
        final EntityInteractable closest = getNearbyEntities()
                .keySet()
                .stream()
                .filter(entity -> entity.isMouseInEntityBounds(cursorInWorld))
                .findFirst()
                .orElse(null);

        if (closest != null
                && closest.isSpeakable()
                && !closest.isSpeakingTo()) {
            closest.setSpeakingTo(true);

            gui.showEntityDialog(closest);
            gui.hideHud();
        }
    }

    /**
     * Interact with the environment
     */
    protected void interactWithObject() {
        // only check objects that are within our update distance
        // TODO: Still needs optimization? I don't know if it stops at the first filter object
        final InteractableWorldObject worldObject = interactableWorldObjects
                .stream()
                .filter(InteractableWorldObject::isWithinUpdateDistanceCache)
                .filter(wb -> wb.clickedOn(cursorInWorld))
                .findFirst()
                .orElse(null);

        if (worldObject != null
                && worldObject.isWithinInteractionDistance(player.getPosition())
                && worldObject.isInteractable()
                && !worldObject.isInteractedWith()) {
            if (worldObject.hasRequiredItem()) {
                worldObject.interact();
            } else {
                gui.getHud().showMissingItemWarning();
            }
        }
    }


    /**
     * Attempt to enter an interior
     */
    protected void interactWithInterior() {
        final Interior interior = interiors
                .values()
                .stream()
                .filter(e -> e.clickedOn(cursorInWorld))
                .findFirst()
                .orElse(null);

        if (interior != null
                && interior.enterable()
                && interior.isWithinEnteringDistance(player.getPosition())) {
            this.resetCursor();
            interior.enter();
        }
    }

    /**
     * Destroy a world object within this world. Removes box2d collision and object from list
     *
     * @param worldObject the object
     */
    public void destroyWorldObject(InteractableWorldObject worldObject) {
        world.destroyBody(worldObject.getCollisionBody());
        interactableWorldObjects.remove(worldObject);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == OasisKeybindings.QUEST_BOOK_KEY) {
            if (gui.isGuiVisible(GuiType.QUEST)) {
                gui.hideGui(GuiType.QUEST);
            } else {
                gui.showGui(GuiType.QUEST);
            }
            return true;
        } else if (keycode == OasisKeybindings.INVENTORY_KEY) {
            if (gui.isGuiVisible(GuiType.INVENTORY)) {
                gui.hideGui(GuiType.INVENTORY);
            } else {
                gui.showGui(GuiType.INVENTORY);
            }
            return true;
        } else if (keycode == Input.Keys.T) {
            player.getInventory().giveEntityItem(LucidTreeFruitItem.class, 1);
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
            renderer.getCamera().zoom += 0.01f;
        } else {
            renderer.getCamera().zoom -= 0.01f;
        }
        return true;
    }
}
