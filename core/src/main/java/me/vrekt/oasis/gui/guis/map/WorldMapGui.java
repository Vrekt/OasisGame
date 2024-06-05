package me.vrekt.oasis.gui.guis.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;

/**
 * Shows the world map
 */
public final class WorldMapGui extends Gui {

    private final Texture mapTexture;
    private final GestureDetector detector;
    private final Camera camera;

    private float camMinX, camMinY, camMaxX, camMaxY;

    public WorldMapGui(GuiManager guiManager) {
        super(GuiType.WORLD_MAP, guiManager);

        mapTexture = new Texture(Gdx.files.internal("world/world_map.png"));

        rootTable.setFillParent(true);
        rootTable.setBackground(new TextureRegionDrawable(mapTexture));
        rootTable.setVisible(false);

        camera = guiManager.getCamera();

        detector = new GestureDetector(new MapPanGestureHandler());
        guiManager.addGui(rootTable);
    }

    /**
     * Calculate bounds
     * <a href="https://stackoverflow.com/questions/12039465/keep-libgdx-camera-inside-boundaries-when-panning-and-zooming">...</a>
     */
    private void calculateBounds() {
        ((OrthographicCamera) guiManager.getStage().getCamera()).zoom = 0.5f;

        camMinX = 0.5f * (camera.viewportWidth / 2);
        camMaxX = camera.viewportWidth - camMinX;

        camMinY = 0.5f * (camera.viewportHeight / 2);
        camMaxY = camera.viewportHeight - camMinY;
    }

    @Override
    public void show() {
        super.show();

        calculateBounds();

        guiManager.getGame().getMultiplexer().addProcessor(detector);
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        ((OrthographicCamera) guiManager.getStage().getCamera()).zoom = 1.0f;
        guiManager.getGame().getMultiplexer().removeProcessor(detector);
        rootTable.setVisible(false);
    }

    private final class MapPanGestureHandler extends GestureDetector.GestureAdapter {
        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            // probably useless but why not
            final float nx = Interpolation.smooth.apply(camera.position.x, camera.position.add(deltaX, 0, 0).x, 1f);
            final float ny = Interpolation.smooth.apply(camera.position.y, camera.position.add(0, deltaY, 0).y, 1f);

            camera.position.set(Math.min(camMaxX, Math.max(nx, camMinX)), Math.min(camMaxY, Math.max(ny, camMinY)), 0);
            camera.update();
            return true;
        }
    }

    @Override
    public void dispose() {
        mapTexture.dispose();
    }
}
