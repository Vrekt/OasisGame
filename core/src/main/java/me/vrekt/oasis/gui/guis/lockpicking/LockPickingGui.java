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
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.misc.LockpickItem;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.world.interior.misc.LockDifficulty;

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

    private final VisImage lockState;
    private final VisImage component;
    private float progress = 0.0f;

    private final ActiveLockpickStage stage;
    private final TextureRegionDrawable locked, unlocked;

    private boolean finished, inProgress;
    private Runnable successCallback, failureCallback;

    public LockPickingGui(GuiManager guiManager) {
        super(GuiType.LOCK_PICKING, guiManager);
        isShowing = false;

        final VisTable parentTable = new VisTable();

        locked = new TextureRegionDrawable(guiManager.getAsset().get(Resource.NORMAL, "lock"));
        unlocked = new TextureRegionDrawable(guiManager.getAsset().get(Resource.NORMAL, "unlocked"));

        lockState = new VisImage(locked);
        lockState.scaleBy(2.0f);
        lockState.getColor().a = 0.0f;

        component = new VisImage();
        stage = new ActiveLockpickStage();

        // we start at the W stage
        stage.add(Input.Keys.W, createWStage());
        stage.of(Input.Keys.W);

        stage.add(Input.Keys.A, createAStage());
        stage.add(Input.Keys.S, createSStage());
        stage.add(Input.Keys.D, createDStage());

        // don't use setScale here because it doesn't work correctly
        // will of course break things when resizing, but everything gets broken.
        parentTable.add(component).size(164 * 2f, 132 * 2f);
        rootTable.add(lockState).padRight(32);
        rootTable.row();

        rootTable.add(parentTable).size(164 * 2f, 132 * 2f);
        rootTable.setVisible(false);

        guiManager.addGui(rootTable);
    }

    /**
     * Create the W key stage
     *
     * @return the component
     */
    private LockpickUiComponent createWStage() {
        return new LockpickUiComponent(component,
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
        return new LockpickUiComponent(component,
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
        return new LockpickUiComponent(component,
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
        return new LockpickUiComponent(component,
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

    /**
     * Attempt to lock pick an object
     *
     * @param difficulty      the difficulty
     * @param successCallback if the lockpick was successful
     * @param failureCallback if the lockpick failed
     */
    public void attemptLockpick(LockDifficulty difficulty, Runnable successCallback, Runnable failureCallback) {
        guiManager.resetCursor();

        this.successCallback = successCallback;
        this.failureCallback = failureCallback;
        show();
    }

    @Override
    public void update() {
        if (isShowing) {

            progress += Gdx.graphics.getDeltaTime() * 8f;
            stage.update(progress, Gdx.graphics.getDeltaTime());

            // all stages are completed, set unlocked
            if (stage.isCompleted() && !finished) {
                lockState.setDrawable(unlocked);
                finished = true;
                // we finished, no callback to be done in hide()
                inProgress = false;

                successCallback.run();
                hide();
            } else if (stage.failed(progress)) {
                final LockpickItem item = ((LockpickItem) guiManager.player().getInventory().get(Items.LOCK_PICK));
                if (item != null && item.shouldBreak()) item.destroy(guiManager.player());

                // player doesn't have any more lockpicks so just exit out completely.
                if (!guiManager.player().getInventory().containsItem(Items.LOCK_PICK)) {
                    guiManager.getHudComponent().showPlayerHint(PlayerHints.NO_MORE_LOCKPICKS, 3.5f, 10.0f);
                    failureCallback.run();
                    hide();
                } else {
                    reset();
                }
            }
        }
    }

    /**
     * Play the hit sound (in range)
     */
    void click() {
        GameManager.playSound(Sounds.LOCK_CLICK, 0.15f, 1.0f, 0.0f);
    }

    /**
     * Play the success sound
     */
    void success() {
        GameManager.playSound(Sounds.LOCK_SUCCESS, 1f, 1.0f, 0.0f);
    }

    void reset() {
        inProgress = true;

        finished = false;
        progress = 0.0f;

        stage.resetAll();
        stage.of(Input.Keys.W);

        // visible, no alpha, then fade
        lockState.clearActions();

        lockState.addAction(Actions.sequence(
                Actions.visible(true),
                Actions.run(() -> lockState.getColor().a = 0.0f),
                Actions.fadeIn(12f, Interpolation.linear))
        );

        lockState.setDrawable(locked);
    }

    @Override
    public void show() {
        super.show();
        reset();

        guiManager.player().disableMovement();
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();

        if (inProgress) {
            // we were in-progress, call the callback
            if (failureCallback != null) failureCallback.run();
            inProgress = false;
        }

        // stop movement from registering
        // a little to early when successful.
        guiManager.player().enableMovementAfter(0.25f);
        lockState.setVisible(false);
        rootTable.setVisible(false);
    }
}
