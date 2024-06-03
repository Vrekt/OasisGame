package me.vrekt.shared.packet.server.player;

import com.badlogic.gdx.math.Vector2;

/**
 * Network player information
 */
public final class S2CNetworkPlayer {

    public int entityId;
    public String username;
    public Vector2 position;

    public S2CNetworkPlayer(int entityId, String username, Vector2 position) {
        this.entityId = entityId;
        this.username = username;
        this.position = position;
    }

}
