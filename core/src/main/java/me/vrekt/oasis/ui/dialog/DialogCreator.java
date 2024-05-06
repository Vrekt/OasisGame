package me.vrekt.oasis.ui.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.OasisGame;

public final class DialogCreator extends ScreenAdapter {

    private final Stage stage;
    private final VisTable rootTable;
    private final Label.LabelStyle large;

    public DialogCreator(OasisGame game) {
        stage = new Stage();
        stage.setViewport(new ScreenViewport());

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        large = new Label.LabelStyle(game.getAsset().getLarge(), Color.BLACK);
        populateCreateDialogComponents();

        //     final VisTextButton newDialogButton = new VisTextButton("New Dialog");
        //  handleNewButton(newDialogButton);

        //   final VisLabel loadText = new VisLabel("File: ");
        //   final VisTextField loadField = new VisTextField("");
        //    final VisTextButton loadButton = new VisTextButton("Load Dialog");
        //    handleLoadButton(loadButton, loadField);

        //  rootTable.add(newDialogButton);
        //  rootTable.row();

        //   rootTable.add(loadText);
        //   rootTable.add(loadField);
        //   rootTable.add(loadButton);
        //    rootTable.row();

    }

    private void populateCreateDialogComponents() {
        final VisTable parent = new VisTable();
        final VisLabel name = new VisLabel("Dialog Name: ", Color.BLACK);
        final VisTextField nameField = new VisTextField("");
        parent.add(name);
        parent.add(nameField);
        parent.row();

        final VisTable scrollContents = new VisTable();
        scrollContents.setFillParent(true);
        final VisScrollPane scrollPane = new VisScrollPane(scrollContents);
        for(int i = 0; i < 10; i++) scrollContents.add(createInputTable()).row();
        parent.add(scrollPane).fill().expand();
        rootTable.add(parent);

    }

    private VisTable createInputTable() {
        final VisTable table = new VisTable();
        final VisLabel label = new VisLabel("Key: ", Color.BLACK);
        final VisTextField field = new VisTextField("");
        final VisLabel text = new VisLabel("Text: ", Color.BLACK);
        final VisTextField textField = new VisTextField("");
        table.add(label);
        table.add(field);
        table.row();
        table.add(text);
        table.add(textField);
        return table;
    }

    private void populateDialogFile() {
        rootTable.clear();
    }

    private void handleNewButton(VisTextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                populateCreateDialogComponents();
            }
        });
    }

    private void handleLoadButton(VisTextButton button, VisTextField input) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                populateDialogFile();
                // Gdx.files.internal(input.getText().trim());
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
