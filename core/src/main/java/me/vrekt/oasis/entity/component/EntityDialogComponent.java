package me.vrekt.oasis.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Basic component for entity dialog animations
 */
public final class EntityDialogComponent implements Component, Pool.Poolable {

    public float distanceFromPlayer;
    public boolean drawDialogAnimationTile, isInView;
    public int currentDialogFrame = 1;

    @Override
    public void reset() {
        currentDialogFrame = 1;
        isInView = false;
        drawDialogAnimationTile = false;
        distanceFromPlayer = 0.0f;
    }
}
