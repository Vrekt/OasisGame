package me.vrekt.oasis.network.game;

/**
 * Represents a player network update, their position and velocity
 *
 * @param entityId id
 * @param x        x
 * @param y        y
 * @param vx       velocity x
 * @param vy       velocity y
 * @param rotation rotation ordinal
 */
public record PlayerNetworkUpdate(int entityId, float x, float y, float vx, float vy, int rotation) {
}
