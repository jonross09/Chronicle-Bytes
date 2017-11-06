package net.openhft.chronicle.bytes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TextBytes implements Bytes {
    private static final int NUMBER_WRAP = 16;
    private static final int COMMENT_START = NUMBER_WRAP * 3;

    private static final Pattern HEX_PATTERN = Pattern.compile("[0-9a-fA-F]{1,2}");
    private final Bytes base = Bytes.elasticHeapByteBuffer(128);
    private final Bytes text = Bytes.elasticHeapByteBuffer(128);
    private final Bytes comment = Bytes.elasticHeapByteBuffer(64);
    private long startOfLine = 0;
    private int indent = 0;

    public TextBytes() {
    }

    TextBytes(BytesStore base, Bytes text) {
        this.base.write(base);
        this.text.write(text);
    }

    public static TextBytes fromText(Reader reader) {
        TextBytes tb = new TextBytes();
        Reader reader2 = new TextBytesReader(reader, tb.text);
        try (Scanner sc = new Scanner(reader2)) {
            while (sc.hasNext()) {
                if (sc.hasNext(HEX_PATTERN))
                    tb.base.writeUnsignedByte(Integer.parseInt(sc.next(), 16));
                else
                    sc.nextLine(); // assume it's a comment
            }
        }
        return tb;
    }

    public static TextBytes fromText(CharSequence text) {
        return fromText(new StringReader(text.toString()));
    }

    @NotNull
    @Override
    public String toHexString() {
        if (lineLength() > 0)
            newLine();
        return text.toString();
    }

    @Override
    public boolean retainsComments() {
        return true;
    }

    @Override
    public Bytes comment(CharSequence comment) {
        if (this.comment.readRemaining() > 0 || comment.length() == 0)
            newLine();
        this.comment.clear().append(comment);
        return this;
    }

    @Override
    public BytesOut indent(int n) {
        indent += n;
        if (lineLength() > 0)
            newLine();
        return this;
    }

    private long lineLength() {
        return this.text.writePosition() - startOfLine;
    }

    private void newLine() {
        if (this.comment.readRemaining() > 0) {
            while (lineLength() < COMMENT_START - 2)
                this.text.append("   ");
            while (lineLength() < COMMENT_START)
                this.text.append(' ');

            this.text.append("# ");
            this.text.append(comment);
            comment.clear();
        }
        this.text.append('\n');
        startOfLine = this.text.writePosition();
    }

    @Override
    public BytesStore copy() {
        return new TextBytes(base, text);
    }

    @Override
    public boolean isElastic() {
        return base.isElastic();
    }

    @Override
    public void ensureCapacity(long size) throws IllegalArgumentException {
        base.ensureCapacity(size);
    }

    @Override
    @Nullable
    public BytesStore bytesStore() {
        return this;
    }

    @Override
    @NotNull
    public Bytes compact() {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Bytes clear() {
        return base.clear();
    }

    @Override
    public boolean isDirectMemory() {
        return false;
    }

    @Override
    public long capacity() {
        return base.capacity();
    }

    @Override
    public long address(long offset) throws UnsupportedOperationException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean compareAndSwapInt(long offset, int expected, int value) throws BufferOverflowException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean compareAndSwapLong(long offset, long expected, long value) throws BufferOverflowException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable
    public Object underlyingObject() {
        return base.underlyingObject();
    }

    @Override
    public void move(long from, long to, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reserve() throws IllegalStateException {
        base.reserve();
    }

    @Override
    public void release() throws IllegalStateException {
        base.release();
    }

    @Override
    public long refCount() {
        return base.refCount();
    }

    @Override
    @NotNull
    public RandomDataOutput writeByte(long offset, byte i8) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeShort(long offset, short i) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeInt24(long offset, int i) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeInt(long offset, int i) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeOrderedInt(long offset, int i) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeLong(long offset, long i) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeOrderedLong(long offset, long i) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeFloat(long offset, float d) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeDouble(long offset, double d) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeVolatileByte(long offset, byte i8) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeVolatileShort(long offset, short i16) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeVolatileInt(long offset, int i32) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput writeVolatileLong(long offset, long i64) throws BufferOverflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput write(long offsetInRDO, byte[] bytes, int offset, int length) throws BufferOverflowException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(long offsetInRDO, ByteBuffer bytes, int offset, int length) throws BufferOverflowException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public RandomDataOutput write(long offsetInRDO, RandomDataInput bytes, long offset, long length) throws BufferOverflowException, IllegalArgumentException, BufferUnderflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void nativeWrite(long address, long position, long size) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public StreamingDataInput readPosition(long position) throws BufferUnderflowException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public StreamingDataInput readLimit(long limit) throws BufferUnderflowException {
        return base.readLimit(limit);
    }

    @Override
    @NotNull
    public StreamingDataInput readSkip(long bytesToSkip) throws BufferUnderflowException {
        return base.readSkip(bytesToSkip);
    }

    @Override
    public void uncheckedReadSkipOne() {
        base.uncheckedReadSkipOne();
    }

    @Override
    public void uncheckedReadSkipBackOne() {
        base.uncheckedReadSkipBackOne();
    }

    @Override
    public byte readByte() {
        return base.readByte();
    }

    @Override
    public int readUnsignedByte() {
        return base.readUnsignedByte();
    }

    @Override
    public int uncheckedReadUnsignedByte() {
        return base.uncheckedReadUnsignedByte();
    }

    @Override
    public short readShort() throws BufferUnderflowException {
        return base.readShort();
    }

    @Override
    public int readInt() throws BufferUnderflowException {
        return base.readInt();
    }

    @Override
    public long readLong() throws BufferUnderflowException {
        return base.readLong();
    }

    @Override
    public float readFloat() throws BufferUnderflowException {
        return base.readFloat();
    }

    @Override
    public double readDouble() throws BufferUnderflowException {
        return base.readDouble();
    }

    @Override
    public int readVolatileInt() throws BufferUnderflowException {
        return base.readVolatileInt();
    }

    @Override
    public long readVolatileLong() throws BufferUnderflowException {
        return base.readVolatileLong();
    }

    @Override
    public int peekUnsignedByte() {
        return base.peekUnsignedByte();
    }

    @Override
    public void nativeRead(long address, long size) throws BufferUnderflowException {
        base.nativeRead(address, size);
    }

    @Override
    public int lastDecimalPlaces() {
        return base.lastDecimalPlaces();
    }

    @Override
    public void lastDecimalPlaces(int lastDecimalPlaces) {
        base.lastDecimalPlaces(lastDecimalPlaces);
    }

    @Override
    @NotNull
    public StreamingDataOutput writePosition(long position) throws BufferOverflowException {
        return base.writePosition(position);
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeLimit(long limit) throws BufferOverflowException {
        return base.writeLimit(limit);
    }

    @Override
    @NotNull
    public StreamingDataOutput writeSkip(long bytesToSkip) throws BufferOverflowException {
        return base.writeSkip(bytesToSkip);
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeByte(byte i8) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeByte(i8);
        } finally {
            copyToText(pos);
        }
    }

    @Override
    public long writePosition() {
        return base.writePosition();
    }

    private void copyToText(long pos) {
        if (pos < writePosition()) {
            doIndent();
            do {
                int value = base.readUnsignedByte(pos++);
                if (lineLength() >= COMMENT_START - 1) {
                    newLine();
                    doIndent();
                }
                if (lineLength() > 0)
                    text.append(' ');
                if (value < 16)
                    text.append('0');
                text.appendBase(value, 16);
            } while (pos < writePosition());
        }
    }

    private void doIndent() {
        if (lineLength() == 0 && indent > 0) {
            for (int i = 0; i < indent; i++)
                text.append("   ");
            startOfLine = text.writePosition();
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeShort(short i16) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeShort(i16);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeInt(int i) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeInt(i);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeIntAdv(int i, int advance) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeIntAdv(i, advance);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeLong(long i64) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeLong(i64);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeLongAdv(long i64, int advance) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeLongAdv(i64, advance);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeFloat(float f) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeFloat(f);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeDouble(double d) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeDouble(d);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput write(byte[] bytes, int offset, int length) throws BufferOverflowException, IllegalArgumentException {
        long pos = writePosition();
        try {
            return base.write(bytes, offset, length);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeSome(ByteBuffer buffer) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeSome(buffer);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeOrderedInt(int i) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeOrderedInt(i);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    @net.openhft.chronicle.core.annotation.NotNull
    public StreamingDataOutput writeOrderedLong(long i) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.writeOrderedLong(i);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    public void nativeWrite(long address, long size) throws BufferOverflowException {
        long pos = writePosition();
        try {
            base.nativeWrite(address, size);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    public BytesPrepender clearAndPad(long length) throws BufferOverflowException {
        long pos = writePosition();
        try {
            return base.clearAndPad(length);

        } finally {
            copyToText(pos);
        }
    }

    @Override
    @NotNull
    public BytesPrepender prewrite(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public BytesPrepender prewrite(BytesStore bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public BytesPrepender prewriteByte(byte b) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public BytesPrepender prewriteShort(short i) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public BytesPrepender prewriteInt(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public BytesPrepender prewriteLong(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte readByte(long offset) throws BufferUnderflowException {
        return base.readByte(offset);
    }

    @Override
    public int peekUnsignedByte(long offset) {
        return base.peekUnsignedByte(offset);
    }

    @Override
    public short readShort(long offset) throws BufferUnderflowException {
        return base.readShort(offset);
    }

    @Override
    public int readInt(long offset) throws BufferUnderflowException {
        return base.readInt(offset);
    }


    @Override
    public long readLong(long offset) throws BufferUnderflowException {
        return base.readLong(offset);
    }

    @Override
    public float readFloat(long offset) throws BufferUnderflowException {
        return base.readFloat(offset);
    }

    @Override
    public double readDouble(long offset) throws BufferUnderflowException {
        return base.readDouble(offset);
    }

    @Override
    public byte readVolatileByte(long offset) throws BufferUnderflowException {
        return base.readVolatileByte(offset);
    }

    @Override
    public short readVolatileShort(long offset) throws BufferUnderflowException {
        return base.readVolatileShort(offset);
    }

    @Override
    public int readVolatileInt(long offset) throws BufferUnderflowException {
        return base.readVolatileInt(offset);
    }

    @Override
    public long readVolatileLong(long offset) throws BufferUnderflowException {
        return base.readVolatileLong(offset);
    }

    @Override
    public void nativeRead(long position, long address, long size) throws BufferUnderflowException {
        base.nativeRead(position, address, size);
    }

    private static class TextBytesReader extends Reader {
        private final Reader reader;
        private final Bytes base;

        public TextBytesReader(Reader reader, Bytes base) {
            this.reader = reader;
            this.base = base;
        }

        @Override
        public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
            int len2 = reader.read(cbuf, off, len);
            base.append(new String(cbuf, off, len)); // TODO Optimise
            return len2;
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }
}
