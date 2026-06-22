/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.unix.Buffer;
import io.netty.channel.unix.Limits;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

public final class IovArray
implements ChannelOutboundBuffer.MessageProcessor {
    private static final int ADDRESS_SIZE = Buffer.addressSize();
    private static final int IOV_SIZE = 2 * ADDRESS_SIZE;
    private static final int CAPACITY = Limits.IOV_MAX * IOV_SIZE;
    private final ByteBuffer memory;
    private final long memoryAddress;
    private int count;
    private long size;
    private long maxBytes = Limits.SSIZE_MAX;

    public IovArray() {
        this.memory = Buffer.allocateDirectWithNativeOrder(CAPACITY);
        this.memoryAddress = Buffer.memoryAddress(this.memory);
    }

    public void clear() {
        this.count = 0;
        this.size = 0L;
    }

    @Deprecated
    public boolean add(ByteBuf buf) {
        return this.add(buf, buf.readerIndex(), buf.readableBytes());
    }

    public boolean add(ByteBuf buf, int offset, int len) {
        ByteBuffer[] buffers;
        if (this.count == Limits.IOV_MAX) {
            return false;
        }
        if (buf.nioBufferCount() == 1) {
            if (len == 0) {
                return true;
            }
            if (buf.hasMemoryAddress()) {
                return this.add(buf.memoryAddress() + (long)offset, len);
            }
            ByteBuffer nioBuffer = buf.internalNioBuffer(offset, len);
            return this.add(Buffer.memoryAddress(nioBuffer) + (long)nioBuffer.position(), len);
        }
        for (ByteBuffer nioBuffer : buffers = buf.nioBuffers(offset, len)) {
            int remaining = nioBuffer.remaining();
            if (remaining == 0 || this.add(Buffer.memoryAddress(nioBuffer) + (long)nioBuffer.position(), remaining) && this.count != Limits.IOV_MAX) continue;
            return false;
        }
        return true;
    }

    private boolean add(long addr, int len) {
        assert (addr != 0L);
        if (this.maxBytes - (long)len < this.size && this.count > 0) {
            return false;
        }
        int baseOffset = IovArray.idx(this.count);
        int lengthOffset = baseOffset + ADDRESS_SIZE;
        this.size += (long)len;
        ++this.count;
        if (ADDRESS_SIZE == 8) {
            if (PlatformDependent.hasUnsafe()) {
                PlatformDependent.putLong((long)baseOffset + this.memoryAddress, addr);
                PlatformDependent.putLong((long)lengthOffset + this.memoryAddress, len);
            } else {
                this.memory.putLong(baseOffset, addr);
                this.memory.putLong(lengthOffset, len);
            }
        } else {
            assert (ADDRESS_SIZE == 4);
            if (PlatformDependent.hasUnsafe()) {
                PlatformDependent.putInt((long)baseOffset + this.memoryAddress, (int)addr);
                PlatformDependent.putInt((long)lengthOffset + this.memoryAddress, len);
            } else {
                this.memory.putInt(baseOffset, (int)addr);
                this.memory.putInt(lengthOffset, len);
            }
        }
        return true;
    }

    public int count() {
        return this.count;
    }

    public long size() {
        return this.size;
    }

    public void maxBytes(long maxBytes) {
        this.maxBytes = Math.min(Limits.SSIZE_MAX, ObjectUtil.checkPositive(maxBytes, "maxBytes"));
    }

    public long maxBytes() {
        return this.maxBytes;
    }

    public long memoryAddress(int offset) {
        return this.memoryAddress + (long)IovArray.idx(offset);
    }

    public void release() {
        Buffer.free(this.memory);
    }

    @Override
    public boolean processMessage(Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buffer = (ByteBuf)msg;
            return this.add(buffer, buffer.readerIndex(), buffer.readableBytes());
        }
        return false;
    }

    private static int idx(int index) {
        return IOV_SIZE * index;
    }
}

