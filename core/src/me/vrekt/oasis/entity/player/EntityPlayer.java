package me.vrekt.oasis.entity.player;

import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.LunarEntityPlayer;

/**
 * Represents an entity that is a player.
 */
public abstract class EntityPlayer extends LunarEntityPlayer {

    public EntityPlayer(int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId, playerScale, playerWidth, playerHeight, rotation);
    }

}
