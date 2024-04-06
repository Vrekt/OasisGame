package me.vrekt.oasis.gui.rewrite.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.rewrite.Gui;
import me.vrekt.oasis.gui.rewrite.GuiManager;

public final class SettingsWindowGui extends Gui {

    public SettingsWindowGui(GuiManager guiManager) {
        super(GuiType.SETTINGS, guiManager);

        hasParent = true;
        parent = GuiType.PAUSE;
        inheritParentBehaviour = true;

        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("pause")));

        final VisTable left = new VisTable();

        final VisLabel gameHeader = new VisLabel("Game Settings", guiManager.getStyle().getLargeWhite());
        final VisLabel graphicsHeader = new VisLabel("Graphics Settings", guiManager.getStyle().getLargeWhite());

        final VisLabel backLabel = new VisLabel("<- Back", guiManager.getStyle().getMediumWhite());
        final VisLabel entityUpdateDistanceLabel = new VisLabel("Entity Distance", guiManager.getStyle().getMediumWhite());
        final VisLabel entityUpdateDistanceValue = new VisLabel(" (" + OasisGameSettings.ENTITY_UPDATE_DISTANCE + "%)", guiManager.getStyle().getSmallWhite());
        final VisSlider entityUpdateDistanceSlider = handleSliderComponent(entityUpdateDistanceValue);

        new Tooltip.Builder("How far away entities will update from the player")
                .target(entityUpdateDistanceLabel)
                .style(guiManager.getStyle().getTooltipStyle())
                .build();

        left.add(gameHeader).row();
        left.add(entityUpdateDistanceLabel);
        left.add(entityUpdateDistanceValue);
        left.add(entityUpdateDistanceSlider).padLeft(8);

        final VisCheckBox.VisCheckBoxStyle style = VisUI.getSkin().get(VisCheckBox.VisCheckBoxStyle.class);
        style.font = guiManager.getMediumFont();
        style.fontColor = Color.WHITE;

        final VisCheckBox vsyncCheck = new VisCheckBox("VSync", style);
        vsyncCheck.setChecked(OasisGameSettings.V_SYNC);
        handleCheckBoxComponents(vsyncCheck);
        addHoverComponents(backLabel, Color.LIGHT_GRAY, Color.WHITE, () -> guiManager.showParentGui(this));

        left.row();
        left.add(graphicsHeader);
        left.row();
        left.add(vsyncCheck);
        left.row();
        left.add(backLabel);

        rootTable.add(left);
        guiManager.addGui(rootTable);
    }

    /**
     * Handle updating game settings related to the checkbox
     *
     * @param vsync the checkbox
     */
    private void handleCheckBoxComponents(VisCheckBox vsync) {
        vsync.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                OasisGameSettings.V_SYNC = vsync.isChecked();
                Gdx.graphics.setVSync(OasisGameSettings.V_SYNC);
            }
        });
    }

    /**
     * Handle updating game settings related to the slider
     *
     * @param entityUpdateDistanceValue label to update
     * @return the slider
     */
    private VisSlider handleSliderComponent(VisLabel entityUpdateDistanceValue) {
        final VisSlider entityUpdateDistanceSlider = new VisSlider(10.0f, 200.0f, 5.0f, false);
        entityUpdateDistanceSlider.setValue(OasisGameSettings.ENTITY_UPDATE_DISTANCE);

        entityUpdateDistanceSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                OasisGameSettings.ENTITY_UPDATE_DISTANCE = entityUpdateDistanceSlider.getValue();
                entityUpdateDistanceValue.setText(" (" + OasisGameSettings.ENTITY_UPDATE_DISTANCE + "%)");
            }
        });
        return entityUpdateDistanceSlider;
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
}
