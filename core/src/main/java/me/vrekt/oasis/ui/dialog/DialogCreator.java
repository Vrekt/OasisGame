package me.vrekt.oasis.ui.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.dialog.InteractableDialogEntry;
import me.vrekt.oasis.entity.dialog.InteractableEntityDialog;
import me.vrekt.oasis.utility.logging.GameLogging;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class DialogCreator extends ScreenAdapter {

    private final Stage stage;
    private final VisTable rootTable;

    private final InteractableEntityDialog jsonData;

    private String name, keyFormat;
    private int dialogIndex;
    private boolean isFirstStage = true;

    private final Gson gson;
    private final Map<String, Float> suggestionStorage = new HashMap<>();

    public DialogCreator(OasisGame game) {
        stage = new Stage();

        jsonData = new InteractableEntityDialog();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        final VisTable left = new VisTable(), right = new VisTable();
        final VisTable buttonTable = getButtonTable(game);
        left.add(buttonTable).left();

        final VisTable inputField = populateInputField(game);
        right.add(inputField);

        final VisSplitPane pane = new VisSplitPane(left, right, false);

        rootTable.add(right);
    }

    private VisTable getButtonTable(OasisGame game) {
        final VisTable buttonTable = new VisTable();
        buttonTable.left();

        final VisImageTextButton createButton = new VisImageTextButton("Create", game.getStyle().getImageTextButtonStyle());
        final VisImageTextButton loadButton = new VisImageTextButton("Load", game.getStyle().getImageTextButtonStyle());
        final VisImageTextButton saveButton = new VisImageTextButton("Save", game.getStyle().getImageTextButtonStyle());

        buttonTable.add(createButton);
        buttonTable.row().padBottom(4f);
        buttonTable.add(loadButton);
        buttonTable.row().padBottom(4f);
        buttonTable.add(saveButton);
        return buttonTable;
    }

    private VisTable populateInputField(OasisGame game) {
        final VisTable root = new VisTable();

        final VisLabel currentKeyLabel = new VisLabel("", game.getStyle().getMediumBlack());
        root.add(currentKeyLabel);
        root.row();

        final VisLabel nameLabel = new VisLabel("Name: ", game.getStyle().getSmallBlack());
        final VisTextField nameField = new VisTextField();

        root.add(nameLabel);
        root.add(nameField);
        root.row().padTop(4f);

        final VisLabel keyFormatLabel = new VisLabel("Key Format: ", game.getStyle().getSmallBlack());
        final VisTextField keyFormatField = new VisTextField();

        root.add(keyFormatLabel);
        root.add(keyFormatField);
        root.row().padTop(4f);

        final VisLabel suggestionLabel = new VisLabel("Suggestion: ", game.getStyle().getSmallBlack());
        final VisTextField suggestionField = new VisTextField();
        final VisImageTextButton nextSuggestionButtion = new VisImageTextButton("NS", game.getStyle().getImageTextButtonStyle());

        root.add(suggestionLabel);
        root.add(suggestionField);
        root.add(nextSuggestionButtion);
        root.row().padTop(4f);

        final VisLabel linksToLabel = new VisLabel("Links To: ", game.getStyle().getSmallBlack());
        final VisTextField linksToField = new VisTextField();

        root.add(linksToLabel);
        root.add(linksToField);
        root.row().padTop(4f);

        final VisLabel k1Label = new VisLabel("K1: ", game.getStyle().getSmallBlack());
        final VisTextArea k1Field = new VisTextArea();

        root.add(k1Label);
        root.add(k1Field).size(400, 200);
        root.row().padTop(4f);

        final VisImageTextButton nextButton = new VisImageTextButton("Next", game.getStyle().getImageTextButtonStyle());
        root.add(nextButton);

        final VisImageTextButton saveButton = new VisImageTextButton("Save", game.getStyle().getImageTextButtonStyle());
        root.row().padTop(4f);
        root.add(saveButton);

        handleNextSuggestionButton(nextSuggestionButtion, suggestionField);
        handleNextButton(nextButton, nameField, keyFormatField, suggestionField, k1Field, linksToField, keyFormatLabel);
        handleSaveButton(saveButton);

        return root;
    }

    private void handleNextButton(VisImageTextButton button,
                                  VisTextField nameField,
                                  VisTextField keyFormatField,
                                  VisTextField suggestionField,
                                  VisTextArea k1Field,
                                  VisTextField linkField,
                                  VisLabel currentKeyLabel) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isFirstStage) {
                    nameField.setVisible(false);
                    keyFormatField.setVisible(false);

                    name = nameField.getText();
                    keyFormat = keyFormatField.getText();
                    isFirstStage = false;

                    jsonData.setName(name);
                    jsonData.setKeyFormat(keyFormat);
                }

                final InteractableDialogEntry data = new InteractableDialogEntry();
                final String key = keyFormat + "_" + dialogIndex;

                data.setKey(key);
                data.setContent(k1Field.getText());
                data.setLink(linkField.getText());

                data.setRequiresInput(!suggestionStorage.isEmpty());

                suggestionStorage.forEach(data::addSuggestion);
                suggestionStorage.clear();

                jsonData.addEntry(key, data);

                suggestionField.clearText();
                k1Field.clearText();
                linkField.clearText();

                dialogIndex++;

                // reflect the new changes next time around
                currentKeyLabel.setText(keyFormat + "_" + dialogIndex);
            }
        });
    }

    private void handleNextSuggestionButton(VisImageTextButton button, VisTextField suggestionField) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final String[] text = StringUtils.split(suggestionField.getText(), ":");
                suggestionStorage.put(text[0], Float.valueOf(text[1]));

                suggestionField.clearText();
            }
        });
    }

    private void handleSaveButton(VisImageTextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final FileHandle handle = Gdx.files.internal("assets/dialog/" + name + ".json");
                try {
                    if (!handle.exists()) Files.createFile(Paths.get(handle.path()));
                    try (FileWriter writer = new FileWriter(handle.file(), false)) {
                        gson.toJson(jsonData, writer);
                    }
                } catch (IOException exception) {
                    GameLogging.exceptionThrown(this, "Failed to write dialog!", exception);
                }

            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        System.exit(0);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(64, 64, 64, 1);

        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

}
