package me.vrekt.oasis.world.lp;

import com.badlogic.gdx.Input;
import com.kotcrab.vis.ui.widget.VisImage;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.guis.lockpicking.LockpickUiComponent;

public final class LockComplexity {

    /**
     * Create the W key stage
     *
     * @return the component
     */
    public static LockpickUiComponent createWStage(VisImage component, Asset asset) {
        return new LockpickUiComponent(component,
                asset,
                "lockpick_easy",
                "lockpick_easy_success_w",
                4,
                Input.Keys.W,
                1.15f,
                25.0f,
                35.0f);
    }

    /**
     * Create the A key stage
     *
     * @return the component
     */
    public static LockpickUiComponent createAStage(VisImage component, Asset asset) {
        return new LockpickUiComponent(component,
                asset,
                "lockpick_easy_a",
                "lockpick_easy_success_a",
                4,
                Input.Keys.A,
                0.623f,
                45.0f,
                55.0f);
    }

    /**
     * Create the S key stage
     *
     * @return the component
     */
    public static LockpickUiComponent createSStage(VisImage component, Asset asset) {
        return new LockpickUiComponent(component,
                asset,
                "lockpick_easy_s",
                "lockpick_easy_success_s",
                4,
                Input.Keys.S,
                0.7f,
                68.0f,
                80.0f);
    }

    /**
     * Create the D key stage
     *
     * @return the component
     */
    public static LockpickUiComponent createDStage(VisImage component, Asset asset) {
        return new LockpickUiComponent(component,
                asset,
                "lockpick_easy_d",
                "lockpick_easy_success_d",
                4,
                Input.Keys.D,
                0.65f,
                90.0f,
                105.0f);
    }

}
