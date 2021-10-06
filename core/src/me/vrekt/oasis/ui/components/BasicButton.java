package me.vrekt.oasis.ui.components;

import com.badlogic.gdx.math.Rectangle;

/**
 * Represents a basic button for anything.
 */
public final class BasicButton {

    private final Rectangle bounds;

    public BasicButton(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean wasClicked(float x, float y) {
        return bounds.contains(x, y);
    }

}
