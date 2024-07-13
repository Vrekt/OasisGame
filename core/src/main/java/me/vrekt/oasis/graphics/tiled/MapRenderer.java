package me.vrekt.oasis.graphics.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.sp.PlayerSP;

/**
 * Handles rendering of worlds, interiors and anything else that requires TiledMaps.
 */
public final class MapRenderer implements Disposable {

    private final OrthographicCamera camera;

    private OrthogonalTiledMapRenderer renderer;
    private final Array<TiledMapTileLayer> layers;

    private final PlayerSP thePlayer;
    private final SpriteBatch batch;
    private final ScreenViewport viewport;

    private int width, height;

    private final ObjectMap<String, Integer> layerEntityCache = new ObjectMap<>();
    private boolean cacheInitialized;
    private int specialSize = -1;

    /**
     * Initialize a new renderer instance
     *
     * @param batch  drawing batch
     * @param player local player
     */
    public MapRenderer(SpriteBatch batch, PlayerSP player) {
        this.layers = new Array<>();
        this.thePlayer = player;
        this.batch = batch;

        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(1 / 32f);
    }

    /**
     * Set the current {@link  TiledMap} to render
     *
     * @param map the map
     * @param x   camera X
     * @param y   camera Y
     */
    public void setTiledMap(TiledMap map, float x, float y) {
        if (renderer == null) {
            renderer = new OrthogonalTiledMapRenderer(map, OasisGameSettings.SCALE, batch);
        }

        this.width = map.getProperties().get("width", Integer.class) - 2;
        this.height = map.getProperties().get("height", Integer.class) - 2;
        this.specialSize = -1;
        this.cacheInitialized = false;
        layerEntityCache.clear();

        camera.position.set(x, y, 0f);
        camera.update();

        this.layers.clear();
        this.layers.addAll(map.getLayers().getByType(TiledMapTileLayer.class));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Prepare the local sprite batch
     */
    public void beginRendering() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    /**
     * Render the world
     */
    public void render() {
        update();

        // update animations and render map.
        AnimatedTiledMapTile.updateAnimationBaseTime();
        renderer.setView(camera);

        if (specialSize == -1) {
            specialSize = thePlayer.getWorldState().specialRenderingEntities().size();
        }

        if (!cacheInitialized) {
            for (int i = 0; i < specialSize; i++) {
                // ideally, unique layer names
                final GameEntity entity = thePlayer.getWorldState().specialRenderingEntities().get(i);
                layerEntityCache.put(entity.renderAfterLayer(), entity.entityId());
            }

            cacheInitialized = true;
        }

        boolean renderAfter = false;
        int toRender = -1;
        for (TiledMapTileLayer layer : layers) {
            if (layerEntityCache.containsKey(layer.getName())) {
                renderAfter = true;
                toRender = layerEntityCache.get(layer.getName());
            }

            if (renderAfter && toRender != -1) {
                final GameEntity entity = thePlayer.getWorldState().findEntityById(toRender);
                entity.mapRender(batch, Gdx.graphics.getDeltaTime());

                renderAfter = false;
                toRender = -1;
            }
            renderer.renderTileLayer(layer);
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Clamps camera position, so it doesn't scroll out of bounds
     */
    private void update() {
        viewport.apply();

        final float x = Interpolation.smooth.apply(camera.position.x, thePlayer.getInterpolatedPosition().x, 1f);
        final float y = Interpolation.smooth.apply(camera.position.y, thePlayer.getInterpolatedPosition().y, 1f);
        camera.position.x = MathUtils.clamp(x, camera.viewportWidth / 2f, width - (camera.viewportWidth / 2f));
        camera.position.y = MathUtils.clamp(y, camera.viewportHeight / 2f, height - (camera.viewportHeight / 2f));
        camera.update();
    }

    /**
     * Invoked when a resize happens.
     *
     * @param width  the width
     * @param height the height
     */
    public void resize(int width, int height) {
        viewport.update(width, height, false);

        // TODO: if needed later, camera.setToOrtho(false, width / 16f / 2f, height / 16f / 2f);
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        renderer = null;
    }
}