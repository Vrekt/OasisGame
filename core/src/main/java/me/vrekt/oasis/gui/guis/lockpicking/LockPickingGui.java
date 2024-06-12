package me.vrekt.oasis.gui.guis.lockpicking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Resource;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;

/**
 * The lock-picking GUI.
 */
public final class LockPickingGui extends Gui {

    private static final float MIN_W = 25.0f;
    private static final float MAX_W = 35.0f;

    private static final float MIN_A = 45.0f;
    private static final float MAX_A = 55.0f;

    private static final float MIN_S = 68.0f;
    private static final float MAX_S = 80.0f;

    private static final float MIN_D = 90.0f;
    private static final float MAX_D = 105.0f;

    private final VisImage lockImage;
    private final VisImage image;
    private float progress = 0.0f;

    private final ActiveLockpickStage stage;

    public LockPickingGui(GuiManager guiManager) {
        super(GuiType.LOCK_PICKING, guiManager);
        isShowing = false;

        final VisTable parentTable = new VisTable();

        lockImage = new VisImage(new TextureRegionDrawable(guiManager.getAsset().get("lock")));
        lockImage.scaleBy(2.0f);
        lockImage.getColor().a = 0.0f;

        image = new VisImage(new TextureRegionDrawable(guiManager.getAsset().get(Resource.LP, "lockpick_easy", 1)));

        stage = new ActiveLockpickStage();
        // we start at the W stage
        stage.add(Input.Keys.W, createWStage());
        stage.of(Input.Keys.W);

        stage.add(Input.Keys.A, createAStage());
        stage.add(Input.Keys.S, createSStage());
        stage.add(Input.Keys.D, createDStage());

        parentTable.add(image).size(164 * 2f, 128 * 2f);
        rootTable.add(lockImage).padRight(32);
        rootTable.row();

        rootTable.add(parentTable).size(164 * 2f, 128 * 2f);
        rootTable.setVisible(false);

        guiManager.addGui(rootTable);
    }

    /**
     * Create the W key stage
     *
     * @return the component
     */
    private LockpickUiComponent createWStage() {
        return new LockpickUiComponent(image,
                guiManager.getAsset(),
                "lockpick_easy",
                "lockpick_easy_success_w",
                4,
                Input.Keys.W,
                1.15f,
                MIN_W,
                MAX_W,
                this);
    }

    /**
     * Create the A key stage
     *
     * @return the component
     */
    private LockpickUiComponent createAStage() {
        return new LockpickUiComponent(image,
                guiManager.getAsset(),
                "lockpick_easy_a",
                "lockpick_easy_success_a",
                4,
                Input.Keys.A,
                0.623f,
                MIN_A,
                MAX_A,
                this);
    }

    /**
     * Create the S key stage
     *
     * @return the component
     */
    private LockpickUiComponent createSStage() {
        return new LockpickUiComponent(image,
                guiManager.getAsset(),
                "lockpick_easy_s",
                "lockpick_easy_success_s",
                4,
                Input.Keys.S,
                0.7f,
                MIN_S,
                MAX_S,
                this);
    }

    /**
     * Create the D key stage
     *
     * @return the component
     */
    private LockpickUiComponent createDStage() {
        return new LockpickUiComponent(image,
                guiManager.getAsset(),
                "lockpick_easy_d",
                "lockpick_easy_success_d",
                4,
                Input.Keys.D,
                0.65f,
                MIN_D,
                MAX_D,
                this);
    }

    @Override
    public void update() {
        if (isShowing) {
            // fade in the lock image over time
            lockImage.addAction(Actions.fadeIn(12f, Interpolation.linear));
            progress += Gdx.graphics.getDeltaTime() * 8f;

            stage.update(progress, Gdx.graphics.getDeltaTime());

            // all stages are completed, set unlocked
            if (stage.isCompleted()) {
                lockImage.setDrawable(new TextureRegionDrawable(guiManager.getAsset().get("unlocked")));
            }
        }
    }

    /**
     * Play the hit sound (in range)
     */
    void hit() {
        GameManager.playSound(Sounds.LOCK_CLICK, 0.15f, 1.0f, 0.0f);
    }

    /**
     * Play the success sound
     */
    void success() {
        GameManager.playSound(Sounds.LOCK_SUCCESS, 1f, 1.0f, 0.0f);
    }

    @Override
    public void show() {
        super.show();

        progress = 0.0f;
        guiManager.getGame().getPlayer().disableMovement(true);
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        guiManager.getGame().getPlayer().disableMovement(false);
        rootTable.setVisible(false);
    }
}
