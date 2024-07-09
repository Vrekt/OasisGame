package me.vrekt.oasis.gui.guis.lockpicking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.world.lp.LockpickActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Lockpicking activity GUI
 */
public final class LockpickingGui extends Gui {

    private static final float PRE_SCALED_WIDTH = 164;
    private static final float PRE_SCALED_HEIGHT = 132;
    private static final float DEFAULT_PADDING = 6;

    private final ShapeRenderer renderer;
    private final VisTable circleTable;
    private final VisTable keyTable;

    private final Map<Integer, StageResource> resources = new HashMap<>();

    private float width = 0;
    private float height = 0;

    private LockpickActivity activity;
    private Color colorContext;

    public LockpickingGui(GuiManager guiManager) {
        super(GuiType.LOCK_PICKING, guiManager);

        renderer = new ShapeRenderer();
        rootTable.setVisible(false);
        rootTable.setFillParent(true);
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("pause")));

        final VisImage circle = new VisImage(new TextureRegionDrawable(guiManager.getAsset().get("circle")));
        circleTable = new VisTable();

        circleTable.add(circle).size(164 * 2f, 132 * 2f);

        keyTable = new VisTable();

        final TextureRegion wr = guiManager.getAsset().get("lp_w_key");
        final TextureRegion ar = guiManager.getAsset().get("lp_a_key");
        final TextureRegion sr = guiManager.getAsset().get("lp_s_key");
        final TextureRegion dr = guiManager.getAsset().get("lp_d_key");
        final StageResource wResource = new StageResource(new VisImage(wr), wr, guiManager.getAsset().get("lp_w_key_press"), new Color(197 / 255f, 216 / 255f, 199 / 255f, 1f));
        final StageResource aResource = new StageResource(new VisImage(ar), ar, guiManager.getAsset().get("lp_a_key_press"), Color.BLUE);
        final StageResource sResource = new StageResource(new VisImage(sr), sr, guiManager.getAsset().get("lp_s_key_press"), Color.RED);
        final StageResource dResource = new StageResource(new VisImage(dr), dr, guiManager.getAsset().get("lp_d_key_press"), Color.YELLOW);

        resources.put(Input.Keys.W, wResource);
        resources.put(Input.Keys.A, aResource);
        resources.put(Input.Keys.S, sResource);
        resources.put(Input.Keys.D, dResource);

        keyTable.add(wResource.container).padLeft(4f);
        keyTable.add(aResource.container).padLeft(4f);
        keyTable.add(sResource.container).padLeft(4f);
        keyTable.add(dResource.container).padLeft(4f);

        rootTable.add(keyTable);
        rootTable.row();
        rootTable.add(circleTable);
        guiManager.addGui(rootTable);
    }

    @Override
    public void show() {
        super.show();

        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();

        rootTable.setVisible(false);
    }

    public Vector2 getStageLocation(Actor actor) {
        return actor.localToScreenCoordinates(new Vector2(0, 0));
    }

    public void setActiveActivity(LockpickActivity activity) {
        this.activity = activity;
        this.colorContext = resources.get(Input.Keys.W).color;
    }

    /**
     * Update circle progress
     *
     * @param width  width
     * @param height height
     */
    public void updateProgress(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void preDraw(Batch batch) {
        if (activity != null) {
            activity.update();

            final float xOffset = PRE_SCALED_WIDTH - (width / 2f) - (0.5f);
            // offset this position by table height because we had a table above the drawing position
            final float yOffset = PRE_SCALED_HEIGHT + (keyTable.getHeight() + (height / 2f) + DEFAULT_PADDING);

            Gdx.gl.glEnable(GL30.GL_BLEND);
            Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            renderer.setProjectionMatrix(guiManager.getStage().getCamera().combined);
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(colorContext);
            renderer.ellipse(getStageLocation(circleTable).x + xOffset, getStageLocation(circleTable).y - yOffset, width, height);
            renderer.end();

            Gdx.gl.glDisable(GL30.GL_BLEND);
        }
    }

    /**
     * Show press down hint
     *
     * @param key key
     */
    public void showPressDownHint(int key) {
        resources.get(key).down();
    }

    /**
     * Reset the press down hint
     *
     * @param key key
     */
    public void resetPressDownHint(int key) {
        resources.get(key).normal();
    }

    /**
     * Reset all key down hints
     */
    public void resetAll() {
        resources.values().forEach(StageResource::normal);
        colorContext = resources.get(Input.Keys.W).color;
    }

    /**
     * Grab the next color context
     *
     * @param key active key completed
     */
    public void next(int key) {
        colorContext = resources.get(key).color;
    }

    /**
     * Stores information about a key like its resources and color
     */
    private static final class StageResource {
        private final VisImage container;
        private final TextureRegion normal, down;
        private final Color color;

        public StageResource(VisImage container, TextureRegion normal, TextureRegion down, Color color) {
            this.container = container;
            this.normal = normal;
            this.down = down;
            this.color = color;
        }

        void normal() {
            container.setDrawable(new TextureRegionDrawable(normal));
        }

        void down() {
            container.setDrawable(new TextureRegionDrawable(down));
        }

    }

}
