package me.vrekt.oasis.world.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import me.vrekt.oasis.entity.player.local.Player;

/**
 * Handles rendering of worlds, interiors and anything else that requires TiledMaps.
 */
public final class GlobalGameRenderer implements Disposable {

    /**
     * Default scaling for all rendering
     */
    public static final float SCALE = 1 / 16.0f;
    private OrthographicCamera camera;

    private OrthogonalTiledMapRenderer renderer;
    private final Array<TiledMapTileLayer> layers;

    private final Player thePlayer;
    private final SpriteBatch batch;
    private final StretchViewport viewport;

    /**
     * Initialize a new renderer instance
     *
     * @param batch  drawing batch
     * @param player local player
     */
    public GlobalGameRenderer(SpriteBatch batch, Player player) {
        this.layers = new Array<>();
        this.thePlayer = player;
        this.batch = batch;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / (SCALE / 2.0f), Gdx.graphics.getHeight() / (SCALE / 2.0f));
        viewport = new StretchViewport(Gdx.graphics.getWidth() / (SCALE / 2.0f), Gdx.graphics.getHeight() / (SCALE / 2.0f));
    }

    /**
     * Change the map to render
     *
     * @param map the map
     */
    public void setDrawingMap(TiledMap map, float x, float y) {
        if (renderer == null) {
            renderer = new OrthogonalTiledMapRenderer(map, SCALE, batch);
        }

        camera.position.set(x, y, 0f);
        camera.update();

        this.layers.clear();
        this.layers.addAll(map.getLayers().getByType(TiledMapTileLayer.class));
    }

    /**
     * Prepare the local sprite batch
     */
    public void beginRendering() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * Render the world
     */
    public void render() {
        update();

        // update animations and render map.
        AnimatedTiledMapTile.updateAnimationBaseTime();
        renderer.setView(camera);
        for (TiledMapTileLayer layer : layers) {
            renderer.renderTileLayer(layer);
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public StretchViewport getViewport() {
        return viewport;
    }

    /**
     * Update camera position
     */
    private void update() {
        final float x = Interpolation.smooth.apply(camera.position.x, thePlayer.getInterpolated().x, 1f);
        final float y = Interpolation.smooth.apply(camera.position.y, thePlayer.getInterpolated().y, 1f);

        camera.position.set(x, y, 0f);
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
        camera.setToOrtho(false, width / 16f / 2f, height / 16f / 2f);
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        camera = null;
        renderer = null;
    }
}
