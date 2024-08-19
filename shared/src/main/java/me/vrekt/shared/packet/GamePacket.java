package me.vrekt.shared.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.Items;
import me.vrekt.shared.network.state.NetworkEntityState;

import java.nio.charset.StandardCharsets;

/**
 * Represents a game packet.
 * Adapter from LunarGdx.
 */
public abstract class GamePacket {

    protected ByteBuf buffer;

    public GamePacket(ByteBuf buffer) {
        this.buffer = buffer;
        this.decode();
    }

    public GamePacket() {

    }

    /**
     * @return packet ID
     */
    public abstract int getId();

    /**
     * Encode
     */
    public abstract void encode();

    /**
     * Write the ID of this packet to the buffer
     */
    protected void writeId() {
        buffer.writeInt(getId());
    }

    public void alloc(ByteBufAllocator allocator) {
        this.buffer = allocator.ioBuffer();
    }

    public ByteBuf getBuffer() {
        return buffer;
    }

    public void decode() {

    }

    public void release() {
        buffer.release();
    }

    /**
     * Read bytes
     *
     * @param length the length
     * @return the bytes
     */
    protected byte[] readBytes(int length) {
        final byte[] contents = new byte[length];
        buffer.readBytes(contents, 0, length);
        return contents;
    }

    /**
     * Read a string
     *
     * @return the string
     */
    protected String readString() {
        if (buffer.isReadable(4)) {
            final int length = buffer.readInt();
            if (buffer.isReadable(length)) {
                final byte[] contents = readBytes(length);
                return new String(contents, StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    /**
     * Write a string.
     *
     * @param value the value
     */
    protected void writeString(String value) {
        if (value == null) return;

        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    protected void writeVector2(float x, float y) {
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    protected void writeItem(Item item) {
        writeString(item.type().name());
        buffer.writeInt(item.amount());
    }

    protected Item readItem() {
        final String of = readString();
        final Items type = Items.valueOf(of);
        final int amount = buffer.readInt();
        return ItemRegistry.createItem(type, amount);
    }

    /**
     * Write a single entity state
     *
     * @param entity entity
     */
    protected void writeSingleEntityState(NetworkEntityState entity) {
        buffer.writeInt(entity.entityId());
        writeVector2(entity.x(), entity.y());
        writeVector2(entity.vx(), entity.vy());
        writeString(entity.name());
        writeString(entity.key());
        buffer.writeInt(entity.type().ordinal());
    }

    /**
     * Read a single entity state
     *
     * @return the entity state
     */
    protected NetworkEntityState readSingleEntityState() {
        final int entityId = buffer.readInt();
        final float x = buffer.readFloat();
        final float y = buffer.readFloat();
        final float vx = buffer.readFloat();
        final float vy = buffer.readFloat();
        final String name = readString();
        final String key = readString();
        final int ordinal = buffer.readInt();
        return new NetworkEntityState(entityId, name, key, x, y, vx, vy, EntityType.values()[ordinal]);
    }

}
