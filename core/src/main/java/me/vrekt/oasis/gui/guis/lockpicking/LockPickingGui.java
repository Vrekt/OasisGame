package me.vrekt.oasis.gui.guis.lockpicking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Resource;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.world.lp.LockComplexity;
import me.vrekt.oasis.world.lp.LockpickingActivity;

/**
 * The lock-picking GUI.
 */
public final class LockPickingGui extends Gui {

    private LockpickingActivity activity;

    private final VisImage lockState;
    private final VisImage component;

    private final TextureRegionDrawable locked, unlocked;

    public LockPickingGui(GuiManager guiManager) {
        super(GuiType.LOCK_PICKING, guiManager);

        this.updateWhileHidden = false;

        final VisTable parentTable = new VisTable();
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get(Resource.NORMAL, "pause")));

        locked = new TextureRegionDrawable(guiManager.getAsset().get(Resource.NORMAL, "lock"));
        unlocked = new TextureRegionDrawable(guiManager.getAsset().get(Resource.NORMAL, "unlocked"));

        lockState = new VisImage(locked);
        lockState.scaleBy(2.0f);
        lockState.getColor().a = 0.0f;

        component = new VisImage();

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
     * Create UI components
     *
     * @param key the key
     * @return the component
     */
    public LockpickUiComponent createComponent(int key) {
        return switch (key) {
            case Input.Keys.W -> LockComplexity.createWStage(component, guiManager.getAsset());
            case Input.Keys.A -> LockComplexity.createAStage(component, guiManager.getAsset());
            case Input.Keys.S -> LockComplexity.createSStage(component, guiManager.getAsset());
            case Input.Keys.D -> LockComplexity.createDStage(component, guiManager.getAsset());
            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }

    /**
     * Start playing lockpick activity
     *
     * @param activity activity
     */
    public void playActivity(LockpickingActivity activity) {
        this.activity = activity;
        this.show();
    }

    /**
     * Show unlocked state
     */
    public void showUnlocked() {
        lockState.setDrawable(unlocked);
    }

    @Override
    public void update() {
        activity.update(this, Gdx.graphics.getDeltaTime());
    }

    /**
     * Reset UI components
     */
    public void reset() {
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

        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();

        activity.guiClosed();
        lockState.setVisible(false);
        rootTable.setVisible(false);

        reset();
    }
}
