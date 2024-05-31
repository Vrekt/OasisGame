package me.vrekt.shared.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.StandardCharsets;

/**
 * Represents a game packet.
 * Adapter from LunarGdx.
 */
public abstract class GamePacket {

    protected ByteBuf buffer;
    // response for this packet
    protected int response;

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
     * Set the response for this packet
     *
     * @param response response
     */
    protected void response(int response) {
        this.response = response;
    }

    /**
     * @return get response
     */
    public int response() {
        return response;
    }

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

}
