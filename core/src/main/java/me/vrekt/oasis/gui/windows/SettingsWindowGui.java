package me.vrekt.oasis.gui.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;

public final class SettingsWindowGui extends Gui {

    private final VisLabel entityUpdateDistancePercentage;
    private long lastMultiplayerChecked = System.currentTimeMillis();

    public SettingsWindowGui(GuiManager guiManager) {
        super(GuiType.SETTINGS, guiManager);

        hasParent = true;
        parent = GuiType.PAUSE;
        inheritParentBehaviour = true;

        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("pause")));

        final VisTable primary = new VisTable();
        final VisTable headerTable = new VisTable();
        headerTable.add(new VisLabel("Game Settings", guiManager.getStyle().getLargeWhite())).fillX();

        final VisTable gameSettingsTable = new VisTable();
        final VisTable multiplayerGameContainer = new VisTable();
        multiplayerGameContainer.left();
        final VisTable vsyncContainer = new VisTable();
        vsyncContainer.left();
        final VisTable entityUpdateDistanceContainer = new VisTable();

        multiplayerGameContainer.setBackground(guiManager.getStyle().getTheme());
        vsyncContainer.setBackground(guiManager.getStyle().getTheme());
        entityUpdateDistanceContainer.setBackground(guiManager.getStyle().getTheme());

        final VisCheckBox multiplayerGameCheck = new VisCheckBox("Enable Multiplayer LAN", guiManager.getStyle().getCheckBoxStyle());
        multiplayerGameCheck.setChecked(guiManager.getGame().isLocalMultiplayer());
        multiplayerGameContainer.add(multiplayerGameCheck).left();

        final VisCheckBox vsyncCheck = new VisCheckBox("Enable VSync", guiManager.getStyle().getCheckBoxStyle());
        vsyncCheck.setChecked(OasisGameSettings.V_SYNC);
        vsyncContainer.add(vsyncCheck).left();

        // handle checking and changing options with the checkboxes
        handleCheckBoxComponents(vsyncCheck, multiplayerGameCheck);

        final NinePatch patch = new NinePatch(guiManager.getAsset().get("slider_knob"), 1, 1, 1, 1);
        final NinePatchDrawable drawable = new NinePatchDrawable(patch);

        // TODO: A little ugly but acceptable.
        final VisLabel entityUpdateDistanceLabel = new VisLabel("Entity Update Distance", guiManager.getStyle().getMediumWhite());
        final Slider.SliderStyle sliderStyle = new Slider.SliderStyle(VisUI.getSkin().get("default-horizontal", Slider.SliderStyle.class));
        sliderStyle.background = guiManager.getStyle().getThemePadded();
        sliderStyle.knob = new TextureRegionDrawable(guiManager.getAsset().get("slider_knob"));
        sliderStyle.knobBefore = drawable;

        final VisSlider entityUpdateDistanceSlider = new VisSlider(10.0f, 200.0f, 5.0f, false, sliderStyle);
        entityUpdateDistanceSlider.setValue(OasisGameSettings.ENTITY_UPDATE_DISTANCE);
        entityUpdateDistanceContainer.add(entityUpdateDistanceLabel).padRight(4f);
        entityUpdateDistanceContainer.add(entityUpdateDistanceSlider);
        entityUpdateDistanceContainer.add(entityUpdateDistancePercentage = new VisLabel("(100%)", guiManager.getStyle().getSmallerWhite())).padLeft(2f);
        handleSliderComponent(entityUpdateDistanceSlider);

        gameSettingsTable.add(multiplayerGameContainer).fillX();
        gameSettingsTable.row().padTop(4f);
        gameSettingsTable.add(vsyncContainer).fillX();
        gameSettingsTable.row().padTop(4f);
        gameSettingsTable.add(entityUpdateDistanceContainer).fillX();

        final VisImageTextButton backButton = new VisImageTextButton("Go Back", guiManager.getStyle().getImageTextButtonStyle());
        addHoverComponents(backButton, new Color(64 / 255f, 64 / 255f, 64 / 255f, 1), Color.WHITE, () -> guiManager.showParentGui(this));

        gameSettingsTable.row().padTop(4f);
        gameSettingsTable.add(backButton).fillX();

        primary.add(headerTable);
        primary.row();
        primary.add(gameSettingsTable);

        rootTable.add(primary);
        guiManager.addGui(rootTable);
    }

    /**
     * Handle updating game settings related to the checkbox
     *
     * @param vsync  the checkbox
     * @param mpGame mp game checkbox
     */
    private void handleCheckBoxComponents(VisCheckBox vsync, VisCheckBox mpGame) {
        vsync.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                OasisGameSettings.V_SYNC = vsync.isChecked();
                Gdx.graphics.setVSync(OasisGameSettings.V_SYNC);
            }
        });

        mpGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (System.currentTimeMillis() - lastMultiplayerChecked <= 1000) {
                    // prevent spamming of this checkbox
                    mpGame.setChecked(!mpGame.isChecked());
                    return;
                }

                lastMultiplayerChecked = System.currentTimeMillis();
                OasisGameSettings.ENABLE_MP_LAN = mpGame.isChecked();
                if (mpGame.isChecked()) {
                    guiManager.getGame().enableLocalMultiplayer();
                } else {
                    guiManager.getGame().disableLocalMultiplayer();
                }
            }
        });
    }

    private void handleSliderComponent(VisSlider slider) {
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                OasisGameSettings.ENTITY_UPDATE_DISTANCE = slider.getValue();
                entityUpdateDistancePercentage.setText(" (" + OasisGameSettings.ENTITY_UPDATE_DISTANCE + "%)");
            }
        });
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);

        rootTable.invalidate();
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
    }
}
