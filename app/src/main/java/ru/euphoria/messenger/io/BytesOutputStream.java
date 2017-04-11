package ru.euphoria.messenger.io;

import java.io.ByteArrayOutputStream;

/**
 * Optimized version of {@link java.io.ByteArrayOutputStream}
 *
 * @author Igor Morozkin
 */

public class BytesOutputStream extends ByteArrayOutputStream {
    /**
     * Creates a new byte array output stream
     */
    public BytesOutputStream() {
        super(8192);
    }

    /**
     * Creates a new byte array output stream with specified size
     *
     * @param size the initial size in bytes
     */
    public BytesOutputStream(int size) {
        super(size);
    }

    /**
     * Returns the current byte array buffer
     */
    public byte[] getByteArray() {
        return buf;
    }
}
