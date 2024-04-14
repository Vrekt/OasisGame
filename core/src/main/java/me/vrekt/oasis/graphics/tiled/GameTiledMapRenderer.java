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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.utility.logging.GameLogging;

/**
 * Handles rendering of worlds, interiors and anything else that requires TiledMaps.
 */
public final class GameTiledMapRenderer implements Disposable {

    private final OrthographicCamera camera;

    private OrthogonalTiledMapRenderer renderer;
    private final Array<TiledMapTileLayer> layers;

    private final OasisPlayer thePlayer;
    private final SpriteBatch batch;
    private final ScreenViewport viewport;

    private int width, height;
    private boolean offset;

    /**
     * Initialize a new renderer instance
     *
     * @param batch  drawing batch
     * @param player local player
     */
    public GameTiledMapRenderer(SpriteBatch batch, OasisPlayer player) {
        this.layers = new Array<>();
        this.thePlayer = player;
        this.batch = batch;

        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(1 / 32f);
    }

    public Array<TiledMapTileLayer> getLayers() {
        return layers;
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

    public void endRendering() {
        batch.end();
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

        for (TiledMapTileLayer layer : new Array.ArrayIterator<>(layers)) {
            renderer.renderTileLayer(layer);
        }
    }

    public void renderParallax() {
        camera.update();
        updateParallax();

        // update animations and render map.
        AnimatedTiledMapTile.updateAnimationBaseTime();
        //   renderer.setView(camera);

        for (TiledMapTileLayer layer : new Array.ArrayIterator<>(layers)) {
            renderer.renderTileLayer(layer);
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Clamps camera position so it doesn't scroll out of bounds
     */
    private void update() {
        viewport.apply();

        final float x = Interpolation.smooth.apply(camera.position.x, thePlayer.getInterpolatedPosition().x, 1f);
        final float y = Interpolation.smooth.apply(camera.position.y, thePlayer.getInterpolatedPosition().y, 1f);
        camera.position.x = MathUtils.clamp(x, camera.viewportWidth / 2f, width - (camera.viewportWidth / 2f));
        camera.position.y = MathUtils.clamp(y, camera.viewportHeight / 2f, height - (camera.viewportHeight / 2f));
        camera.update();
    }

    private void updateParallax() {
        if (camera.position.x == 10.0f) camera.position.x = 16.0f;
        //  renderer.setView(camera.combined, camera.position.x, camera.position.y + height, camera.viewportWidth, camera.viewportHeight);

        //   final float x = Interpolation.smooth.apply(camera.position.x, px, 1f);
        //  final float y = Interpolation.smooth.apply(camera.position.y, py, 1f);

        //  final float maxX = MathUtils.clamp(x, camera.viewportWidth / 2f, width - (camera.viewportWidth / 2f));
        //  final float maxY = MathUtils.clamp(y, camera.viewportHeight / 2f, height - (camera.viewportHeight / 2f));


        if (camera.position.y >= 35) {
            offset = true;
        }

        if (!offset) {
            renderer.setView(camera);
        } else {
            float width = camera.viewportWidth;
            float height = camera.viewportHeight;

            float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
            float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
            float x1 = camera.position.x - w / 2;
            float y2 = camera.position.y - h / 2;

            //  x1 -= 5.0f;
            //  w -= 5.0f;

            y2 -= this.height * OasisGameSettings.SCALE;
            h -= this.height * OasisGameSettings.SCALE;
            renderer.setView(camera.combined, x1, y2, w, h);
            offset = false;
        }

        camera.position.y += (Gdx.graphics.getDeltaTime()) * 4f;

    }

    /**
     * Invoked when a resize happens.
     *
     * @param width  the width
     * @param height the height
     */
    public void resize(int width, int height) {
        GameLogging.info("Renderer", "Resized %d %d", width, height);
        viewport.update(width, height, false);
        // TODO: May be required in the future
        // camera.setToOrtho(false, width / 16f / 2f, height / 16f / 2f);
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        renderer = null;
    }
}