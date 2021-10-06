package me.vrekt.oasis.entity.player.network;

import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.impl.LunarNetworkPlayer;

/**
 * A MP entity player
 */
public final class NetworkPlayer extends LunarNetworkPlayer {

    public NetworkPlayer(int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId, playerScale, playerWidth, playerHeight, rotation);
    }
}
