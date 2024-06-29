package me.vrekt.oasis.network.player;

import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBufAllocator;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.shared.packet.GamePacket;

import java.util.function.Consumer;

/**
 * An empty dummy connection
 */
public class DummyConnection extends PlayerConnection {

    public DummyConnection() {
        super();
    }

    @Override
    public void updateNetworkInteriorWorldEntered(GameWorldInterior interior) {

    }

    @Override
    public void sendChatMessage(String text) {

    }

    @Override
    public NetworkPlayer getPlayer(int entityId) {
        return null;
    }

    @Override
    public void addPlayer(int entityId, NetworkPlayer player) {

    }

    @Override
    public void removePlayer(int entityId) {

    }

    @Override
    public float getPingMs() {
        return 0.0f;
    }

    @Override
    public void joinWorld(String world, String username) {

    }

    @Override
    public void updateItemEquipped(Item item) {

    }

    @Override
    public void updateArtifactActivated(Artifact artifact) {

    }

    @Override
    public void alive() {

    }

    @Override
    public void attach(int packet, Consumer<GamePacket> consumer) {

    }

    @Override
    public void attachAsync(int packet, Consumer<GamePacket> consumer) {

    }

    @Override
    public void handle(GamePacket packet) {

    }

    @Override
    public <T extends GamePacket> void sendImmediatelyWithCallback(GamePacket packet, long timeout, boolean synchronize, Runnable timeoutHandler, NetworkCallback<T> callback) {

    }

    @Override
    public <T extends GamePacket> void sendImmediatelyWithCallback(GamePacket packet, long timeout, boolean synchronize, NetworkCallback<T> callback) {

    }

    @Override
    public void updatePosition(float x, float y, float rotation) {

    }

    @Override
    public void updatePosition(Vector2 position, float rotation) {

    }

    @Override
    public void updateVelocity(float x, float y, float rotation) {

    }

    @Override
    public void updateVelocity(Vector2 velocity, float rotation) {

    }

    @Override
    public void disconnect(String reason) {

    }

    @Override
    public void updateWorldHasLoaded() {

    }

    @Override
    public void updateInteriorHasLoaded() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public ByteBufAllocator alloc() {
        return null;
    }

    @Override
    public void sendToQueue(GamePacket packet) {

    }

    @Override
    public void sendImmediately(GamePacket packet) {

    }

    @Override
    public void updateSync() {

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }

    @Override
    public void dispose() {

    }
}
