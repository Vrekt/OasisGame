package me.vrekt.shared.packet.server.player;

/**
 * Network player information
 */
public final class S2CNetworkPlayer {

    public int entityId;
    public String username;
    public float x, y;

    public S2CNetworkPlayer(int entityId, String username, float x, float y) {
        this.entityId = entityId;
        this.username = username;
        this.x = x;
        this.y = y;
    }

}
