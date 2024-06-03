package me.vrekt.oasis.save.world.player;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;

/**
 * Player save
 * TODO: Character type
 * TODO: Artifacts
 * TODO: Attributes and effects
 */
public final class PlayerSave {

    @Expose
    private String name;

    @Expose
    private Vector2 position;

}
