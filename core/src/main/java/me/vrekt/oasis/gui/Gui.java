package me.vrekt.oasis.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a basic GUI
 */
public abstract class Gui implements Disposable {

    protected final GuiManager guiManager;
    protected boolean isShowing;
    protected float timedUpdateInterval, lastUpdate;

    protected final VisTable rootTable;
    protected final GuiType type;

    // if this gui should be updated while hidden
    protected boolean updateWhileHidden = true;
    protected boolean hasParent;
    // if this gui should also for example, hide the same GUIs
    protected boolean inheritParentBehaviour;
    protected boolean disablePlayerMovement;
    protected GuiType parent;

    protected List<GuiType> hideWhenVisible = new ArrayList<>();

    public Gui(GuiType type, GuiManager guiManager) {
        this.type = type;
        this.guiManager = guiManager;
        this.rootTable = new VisTable(true);
    }

    public void update() {

    }

    public void preDraw(Batch batch) {

    }

    public void draw(Batch batch) {

    }

    public void timedUpdate(float tick) {

    }

    /**
     * Resize the element
     */
    public void resize(int width, int height) {

    }

    /**
     * Show this GUI element
     */
    public void show() {
        isShowing = true;
        hideRelatedGuis();
        hideWhenVisible.forEach(guiManager::hideGui);

        if (disablePlayerMovement) {
            GameManager.getPlayer().disableMovement();
        }
    }

    /**
     * Hide this GUI element
     */
    public void hide() {
        isShowing = false;
        hideWhenVisible.forEach(guiManager::showGui);

        if (disablePlayerMovement) {
            GameManager.getPlayer().enableMovement();
        }
    }

    public void hiddenForChild() {
        isShowing = false;
        rootTable.setVisible(false);
    }

    public void hideRelatedGuis() {

    }

    protected void addHoverComponents(Label label, Color color, Color original, Runnable clickAction) {
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAction.run();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                label.setColor(color);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                label.setColor(original);
            }
        });
    }

    protected void addHoverComponents(VisImageTextButton button, Color color, Color original, Runnable clickAction) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAction.run();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.setColor(color);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setColor(original);
            }
        });
    }

    /**
     * @return if this element is visible.
     */
    public boolean isGuiVisible() {
        return isShowing;
    }

    public GuiType getType() {
        return type;
    }

    protected void fadeIn(Actor actor, float duration) {
        actor.getColor().a = 0.0f;
        actor.addAction(Actions.fadeIn(duration));
    }

    protected TextureRegionDrawable drawable(String resource) {
        return new TextureRegionDrawable(asset(resource));
    }

    protected TextureRegion asset(String resource) {
        return guiManager.getAsset().get(Resource.UI, resource);
    }

    protected TextureRegion asset(String resource, int index) {
        return guiManager.getAsset().get(Resource.UI, resource, index);
    }

    @Override
    public void dispose() {

    }
}
