package me.vrekt.oasis.gui.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import me.vrekt.oasis.gui.dialog.DialogGui;

/**
 * A labeled gui button with actions
 */
public final class LabeledGuiButton extends ClickListener {

    private final DialogGui gui;
    private final Label label;
    private String optionName;

    public LabeledGuiButton(DialogGui gui, Skin skin) {
        this.gui = gui;
        this.label = new Label("", skin, "small", Color.WHITE);
        this.label.addListener(this);
    }

    public void setText(String text) {
        this.label.setText(text);
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public Label getLabel() {
        return label;
    }

    public void clicked(InputEvent event, float x, float y) {
        if (this.label.getText().isEmpty()) return;
        gui.handleOptionClicked(optionName);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        this.label.setColor(Color.GRAY);
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        this.label.setColor(Color.BLACK);
    }

}
