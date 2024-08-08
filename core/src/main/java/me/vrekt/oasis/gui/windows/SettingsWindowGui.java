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
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Styles;

public final class SettingsWindowGui extends Gui {

    private final VisLabel entityUpdateDistancePercentage;
    private final VisLabel autoSaveIntervalLabel;
    private final VisLabel volumeLabel;
    private final VisSlider autoSaveSlider;
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
        headerTable.add(new VisLabel("Game Settings", Styles.getLargeWhite())).fillX();

        final VisTable gameSettingsTable = new VisTable();
        final VisTable multiplayerGameContainer = new VisTable();
        multiplayerGameContainer.left();
        final VisTable vsyncContainer = new VisTable();
        vsyncContainer.left();

        final VisTable autoSaveContainer = new VisTable();
        autoSaveContainer.left();

        final VisTable volumeContainer = new VisTable();

        final VisTable entityUpdateDistanceContainer = new VisTable();
        final VisTable autoSaveSliderContainer = new VisTable();

        final VisTable volumeSliderContainer = new VisTable();

        multiplayerGameContainer.setBackground(Styles.getTheme());
        vsyncContainer.setBackground(Styles.getTheme());
        entityUpdateDistanceContainer.setBackground(Styles.getTheme());
        autoSaveContainer.setBackground(Styles.getTheme());
        autoSaveSliderContainer.setBackground(Styles.getTheme());

        volumeContainer.setBackground(Styles.getTheme());
        volumeSliderContainer.setBackground(Styles.getTheme());

        final VisCheckBox multiplayerGameCheck = new VisCheckBox("Enable Multiplayer LAN", Styles.getCheckBoxStyle());
        multiplayerGameCheck.setChecked(guiManager.getGame().isHostingMultiplayerGame());
        multiplayerGameContainer.add(multiplayerGameCheck).left();

        final VisCheckBox vsyncCheck = new VisCheckBox("Enable VSync", Styles.getCheckBoxStyle());
        vsyncCheck.setChecked(OasisGameSettings.V_SYNC);
        vsyncContainer.add(vsyncCheck).left();

        final VisCheckBox autoSaveCheck = new VisCheckBox("Auto Save", Styles.getCheckBoxStyle());
        autoSaveCheck.setChecked(OasisGameSettings.AUTO_SAVE);
        autoSaveContainer.add(autoSaveCheck).left();

        // handle checking and changing options with the checkboxes
        handleCheckBoxComponents(vsyncCheck, multiplayerGameCheck, autoSaveCheck);

        final NinePatch patch = new NinePatch(guiManager.getAsset().get("slider_knob"), 1, 1, 1, 1);
        final NinePatchDrawable drawable = new NinePatchDrawable(patch);

        // TODO: A little ugly but acceptable.
        final VisLabel entityUpdateDistanceLabel = new VisLabel("Entity Update Distance", Styles.getMediumWhite());
        final Slider.SliderStyle sliderStyle = new Slider.SliderStyle(VisUI.getSkin().get("default-horizontal", Slider.SliderStyle.class));
        sliderStyle.background = Styles.getThemePadded();
        sliderStyle.knob = new TextureRegionDrawable(guiManager.getAsset().get("slider_knob"));
        sliderStyle.knobBefore = drawable;

        final VisSlider entityUpdateDistanceSlider = new VisSlider(10.0f, 200.0f, 5.0f, false, sliderStyle);
        entityUpdateDistanceSlider.setValue(OasisGameSettings.ENTITY_UPDATE_DISTANCE);
        entityUpdateDistanceContainer.add(entityUpdateDistanceLabel).padRight(4f);
        entityUpdateDistanceContainer.add(entityUpdateDistanceSlider);
        entityUpdateDistanceContainer.add(entityUpdateDistancePercentage = new VisLabel("(100%)", Styles.getSmallerWhite())).padLeft(2f);

        autoSaveIntervalLabel = new VisLabel("Autosave: " + OasisGameSettings.AUTO_SAVE_INTERVAL_MINUTES + " minutes", Styles.getMediumWhite());
        autoSaveSlider = new VisSlider(1.0f, 60.0f, 1.0f, false, sliderStyle);
        autoSaveSlider.setValue(OasisGameSettings.AUTO_SAVE_INTERVAL_MINUTES);
        autoSaveSliderContainer.add(autoSaveIntervalLabel).padRight(4f);
        autoSaveSliderContainer.add(autoSaveSlider);

        volumeLabel = new VisLabel("SFX Volume: " + OasisGameSettings.VOLUME * 100, Styles.getMediumWhite());
        final VisSlider volumeSlider = new VisSlider(0.0f, 100.0f, 1.0f, false, sliderStyle);
        volumeSlider.setValue(OasisGameSettings.VOLUME * 100.0f);
        volumeSliderContainer.add(volumeLabel).padRight(4f);
        volumeSliderContainer.add(volumeSlider);

        handleSliderComponents(entityUpdateDistanceSlider, autoSaveSlider, volumeSlider);

        gameSettingsTable.add(multiplayerGameContainer).fillX();
        gameSettingsTable.row().padTop(4f);
        gameSettingsTable.add(vsyncContainer).fillX();
        gameSettingsTable.row().padTop(4f);
        gameSettingsTable.add(autoSaveContainer).fillX();
        gameSettingsTable.row().padTop(4f);
        gameSettingsTable.add(autoSaveSliderContainer).fillX();
        gameSettingsTable.row().padTop(4f);
        gameSettingsTable.add(entityUpdateDistanceContainer).fillX();
        gameSettingsTable.row().padTop(4f);
        gameSettingsTable.add(volumeSliderContainer).fillX();

        final VisImageTextButton backButton = new VisImageTextButton("Go Back", Styles.getImageTextButtonStyle());
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
    private void handleCheckBoxComponents(VisCheckBox vsync, VisCheckBox mpGame, VisCheckBox autoSave) {
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

                // start or stop the integrated server.
                if (!OasisGameSettings.ENABLE_MP_LAN) {
                    GameManager.game().startIntegratedServerBlocking(guiManager.player().getWorldState());
                } else {
                    GameManager.game().shutdownIntegratedServer();
                }

                lastMultiplayerChecked = System.currentTimeMillis();
            }
        });

        autoSave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                OasisGameSettings.AUTO_SAVE = autoSave.isChecked();
                autoSaveSlider.setDisabled(!autoSave.isChecked());
            }
        });

    }

    private void handleSliderComponents(VisSlider slider, VisSlider autoSaveSlider, VisSlider volumeSlider) {
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                OasisGameSettings.ENTITY_UPDATE_DISTANCE = slider.getValue();
                entityUpdateDistancePercentage.setText(" (" + OasisGameSettings.ENTITY_UPDATE_DISTANCE + "%)");
            }
        });

        autoSaveSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                OasisGameSettings.AUTO_SAVE_INTERVAL_MINUTES = autoSaveSlider.getValue();
                autoSaveIntervalLabel.setText("Autosave: " + autoSaveSlider.getValue() + " minutes");
                guiManager.getGame().scheduleAutoSave(autoSaveSlider.getValue() * 60);
            }
        });

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                volumeLabel.setText("SFX Volume: " + volumeSlider.getValue());
                OasisGameSettings.VOLUME = volumeSlider.getValue() / 100.0f;
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
