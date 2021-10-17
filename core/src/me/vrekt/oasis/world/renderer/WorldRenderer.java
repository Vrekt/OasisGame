package me.vrekt.oasis.world.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import me.vrekt.oasis.entity.player.local.Player;

/**
 * Handles rendering of worlds.
 */
public final class WorldRenderer implements Disposable {

    /**
     * Default scaling for all rendering
     */
    public static final float SCALE = 1 / 16.0f;

    /**
     * The camera for this level.
     */
    private OrthographicCamera camera;

    private OrthogonalTiledMapRenderer renderer;
    private final Array<TiledMapTileLayer> layers;

    private final Player thePlayer;
    private final StretchViewport viewport;

    /**
     * Initialize a new renderer instance
     *
     * @param worldMap   the map
     * @param worldScale the scale
     * @param worldSpawn the spawning location
     * @param player     local player
     */
    public WorldRenderer(TiledMap worldMap, float worldScale, Vector2 worldSpawn, SpriteBatch batch, Player player) {
        this.layers = new Array<>(worldMap.getLayers().getByType(TiledMapTileLayer.class));
        this.thePlayer = player;

        renderer = new OrthogonalTiledMapRenderer(worldMap, worldScale, batch);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / (SCALE / 2.0f), Gdx.graphics.getHeight() / (SCALE / 2.0f));
        viewport = new StretchViewport(Gdx.graphics.getWidth() / (SCALE / 2.0f), Gdx.graphics.getHeight() / (SCALE / 2.0f));

        camera.position.set(worldSpawn.x, worldSpawn.y, 0f);
        camera.update();
    }

    /**
     * Render the world
     */
    public void render() {
        update();

        // update animations and render map.
        AnimatedTiledMapTile.updateAnimationBaseTime();
        renderer.setView(camera);
        for (TiledMapTileLayer layer : layers) renderer.renderTileLayer(layer);
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
        camera.position.set(thePlayer.getInterpolated().x, thePlayer.getInterpolated().y, 0f);
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
