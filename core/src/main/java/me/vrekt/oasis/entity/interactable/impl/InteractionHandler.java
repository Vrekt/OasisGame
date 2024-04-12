package me.vrekt.oasis.entity.interactable.impl;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.entity.Entity;

public final class InteractionHandler {

    /**
     * Check if this entity was clicked on
     *
     * @param clicked the vector3 click
     * @return {@code  true} if so
     */
    public boolean isMouseInEntityBounds(Entity owner, Vector3 clicked) {
        return clicked.x > owner.getX() && clicked.x < (owner.getX() + owner.getScaledWidth()) && clicked.y > owner.getY() && clicked.y < (owner.getY() + owner.getScaledHeight());
    }

    public boolean isWithinInteractionDistance(Entity owner, Vector2 position, float distance) {
        return false;
    }
    
}
