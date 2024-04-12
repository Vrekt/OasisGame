package me.vrekt.oasis.gui.rewrite.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.rewrite.Gui;
import me.vrekt.oasis.gui.rewrite.GuiManager;

public final class SettingsWindowGui extends Gui {

    private final VisTable primary;
    private final VisCheckBox lanGameCheck;

    public SettingsWindowGui(GuiManager guiManager) {
        super(GuiType.SETTINGS, guiManager);

        hasParent = true;
        parent = GuiType.PAUSE;
        inheritParentBehaviour = true;

        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("pause")));

        primary = new VisTable();

        final VisLabel gameHeader = new VisLabel("Game Settings", guiManager.getStyle().getLargeWhite());
        final VisLabel graphicsHeader = new VisLabel("Graphics Settings", guiManager.getStyle().getLargeWhite());

        final VisCheckBox.VisCheckBoxStyle style = VisUI.getSkin().get(VisCheckBox.VisCheckBoxStyle.class);
        style.font = guiManager.getAsset().getMedium();
        style.fontColor = Color.WHITE;

        lanGameCheck = new VisCheckBox("Enable Multiplayer LAN", style);
        lanGameCheck.setChecked(OasisGameSettings.ENABLE_MP_LAN);

        final VisLabel backLabel = new VisLabel("<- Back", guiManager.getStyle().getMediumWhite());
        final VisLabel entityUpdateDistanceLabel = new VisLabel("Entity Distance", guiManager.getStyle().getMediumWhite());
        final VisLabel entityUpdateDistanceValue = new VisLabel(" (" + OasisGameSettings.ENTITY_UPDATE_DISTANCE + "%)", guiManager.getStyle().getSmallWhite());
        final VisSlider entityUpdateDistanceSlider = handleSliderComponent(entityUpdateDistanceValue);

        new Tooltip.Builder("How far away entities will update from the player")
                .target(entityUpdateDistanceLabel)
                .style(guiManager.getStyle().getTooltipStyle())
                .build();

        primary.add(gameHeader).row();
        primary.add(entityUpdateDistanceLabel);
        primary.add(entityUpdateDistanceValue);
        primary.add(entityUpdateDistanceSlider).padLeft(8);


        final VisCheckBox vsyncCheck = new VisCheckBox("VSync", style);
        vsyncCheck.setChecked(OasisGameSettings.V_SYNC);
        handleCheckBoxComponents(vsyncCheck, lanGameCheck);
        addHoverComponents(backLabel, Color.LIGHT_GRAY, Color.WHITE, () -> guiManager.showParentGui(this));

        primary.row();
        primary.add(graphicsHeader);
        primary.row();
        primary.add(vsyncCheck);
        primary.row();
        primary.add(backLabel);

        rootTable.add(primary);
        guiManager.addGui(rootTable);
    }

    /**
     * Handle updating game settings related to the checkbox
     *
     * @param vsync   the checkbox
     * @param lanGame lan game checkbox
     */
    private void handleCheckBoxComponents(VisCheckBox vsync, VisCheckBox lanGame) {
        vsync.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                OasisGameSettings.V_SYNC = vsync.isChecked();
                Gdx.graphics.setVSync(OasisGameSettings.V_SYNC);
            }
        });
        lanGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                OasisGameSettings.ENABLE_MP_LAN = lanGame.isChecked();
                if (lanGame.isChecked()) {
                    GameManager.enableMultiplayerLan();
                } else {
                    GameManager.disableMultiplayerLan();
                }
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

        // only add check if game is currently an integrated game
        if (guiManager.getGame().isIntegratedGame()) {
            primary.row();
            primary.add(lanGameCheck);
        } else {
            lanGameCheck.setDisabled(true);
        }
        rootTable.invalidate();
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
    }
}
