package me.vrekt.oasis.gui.guis.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Resource;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiManager;

/**
 * Base HUD component
 */
public abstract class HudComponent {

    protected final HudComponentType componentType;
    protected final VisTable rootTable;

    protected final GuiManager guiManager;
    protected final PlayerSP player;

    protected boolean isShowing;

    public HudComponent(HudComponentType componentType, GuiManager manager) {
        this.componentType = componentType;
        this.guiManager = manager;
        this.player = guiManager.player();

        rootTable = new VisTable();
    }

    /**
     * Update this HUD component
     *
     * @param tick current world tick
     */
    public void update(float tick) {

    }

    protected TextureRegion asset(String resource) {
        return guiManager.getAsset().get(Resource.UI, resource);
    }

    protected TextureRegion asset(String resource, int index) {
        return guiManager.getAsset().get(Resource.UI, resource, index);
    }

    public void show() {
        rootTable.setVisible(true);
        isShowing = true;
    }

    public void hide() {
        rootTable.setVisible(false);
        isShowing = false;
    }

    protected void fadeIn(Actor actor, float duration) {
        actor.getColor().a = 0.0f;
        actor.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(duration, Interpolation.smooth)));
    }

    protected void fadeOut(Actor actor, float duration) {
        actor.getColor().a = 1.0f;
        actor.addAction(Actions.sequence(Actions.fadeOut(duration, Interpolation.smooth), Actions.visible(false)));
    }

}
