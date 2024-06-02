package me.vrekt.oasis.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * All damage types
 */
public enum DamageType {

    NORMAL(new Color(255, 255, 64, 1.0f)),
    CRITICAL_HIT(new Color(255, 0, 0, 1.0f)),
    POISON(new Color(32 / 255f, 127 / 255f, 10 / 255f, 1.0f));

    final Color color;

    DamageType(Color color) {
        this.color = color;
    }

    public Color color() {
        return color;
    }

    public void useWith(BitmapFont font, float alpha) {
        font.setColor(color.r, color.g, color.b, alpha);
    }

}
