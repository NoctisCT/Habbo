/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.NativeInetAddress;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

final class NativeDatagramPacketArray {
    private final NativeDatagramPacket[] packets = new NativeDatagramPacket[Limits.UIO_MAX_IOV];
    private final IovArray iovArray = new IovArray();
    private final byte[] ipv4Bytes = new byte[4];
    private final MyMessageProcessor processor = new MyMessageProcessor();
    private int count;

    NativeDatagramPacketArray() {
        for (int i = 0; i < this.packets.length; ++i) {
            this.packets[i] = new NativeDatagramPacket();
        }
    }

    boolean addWritable(ByteBuf buf, int index, int len) {
        return this.add0(buf, index, len, null);
    }

    private boolean add0(ByteBuf buf, int index, int len, InetSocketAddress recipient) {
        if (this.count == this.packets.length) {
            return false;
        }
        if (len == 0) {
            return true;
        }
        int offset = this.iovArray.count();
        if (offset == Limits.IOV_MAX || !this.iovArray.add(buf, index, len)) {
            return false;
        }
        NativeDatagramPacket p = this.packets[this.count];
        p.init(this.iovArray.memoryAddress(offset), this.iovArray.count() - offset, recipient);
        ++this.count;
        return true;
    }

    void add(ChannelOutboundBuffer buffer, boolean connected) throws Exception {
        this.processor.connected = connected;
        buffer.forEachFlushedMessage(this.processor);
    }

    int count() {
        return this.count;
    }

    NativeDatagramPacket[] packets() {
        return this.packets;
    }

    void clear() {
        this.count = 0;
        this.iovArray.clear();
    }

    void release() {
        this.iovArray.release();
    }

    final class NativeDatagramPacket {
        private long memoryAddress;
        private int count;
        private final byte[] addr = new byte[16];
        private int addrLen;
        private int scopeId;
        private int port;

        NativeDatagramPacket() {
        }

        private void init(long memoryAddress, int count, InetSocketAddress recipient) {
            this.memoryAddress = memoryAddress;
            this.count = count;
            if (recipient == null) {
                this.scopeId = 0;
                this.port = 0;
                this.addrLen = 0;
            } else {
                InetAddress address = recipient.getAddress();
                if (address instanceof Inet6Address) {
                    System.arraycopy(address.getAddress(), 0, this.addr, 0, this.addr.length);
                    this.scopeId = ((Inet6Address)address).getScopeId();
                } else {
                    NativeInetAddress.copyIpv4MappedIpv6Address(address.getAddress(), this.addr);
                    this.scopeId = 0;
                }
                this.addrLen = this.addr.length;
                this.port = recipient.getPort();
            }
        }

        DatagramPacket newDatagramPacket(ByteBuf buffer, InetSocketAddress localAddress) throws UnknownHostException {
            InetAddress address;
            if (this.addrLen == NativeDatagramPacketArray.this.ipv4Bytes.length) {
                System.arraycopy(this.addr, 0, NativeDatagramPacketArray.this.ipv4Bytes, 0, this.addrLen);
                address = InetAddress.getByAddress(NativeDatagramPacketArray.this.ipv4Bytes);
            } else {
                address = Inet6Address.getByAddress(null, this.addr, this.scopeId);
            }
            return new DatagramPacket(buffer.writerIndex(this.count), localAddress, new InetSocketAddress(address, this.port));
        }
    }

    private final class MyMessageProcessor
    implements ChannelOutboundBuffer.MessageProcessor {
        private boolean connected;

        private MyMessageProcessor() {
        }

        @Override
        public boolean processMessage(Object msg) {
            if (msg instanceof DatagramPacket) {
                DatagramPacket packet = (DatagramPacket)msg;
                ByteBuf buf = (ByteBuf)packet.content();
                return NativeDatagramPacketArray.this.add0(buf, buf.readerIndex(), buf.readableBytes(), (InetSocketAddress)packet.recipient());
            }
            if (msg instanceof ByteBuf && this.connected) {
                ByteBuf buf = (ByteBuf)msg;
                return NativeDatagramPacketArray.this.add0(buf, buf.readerIndex(), buf.readableBytes(), null);
            }
            return false;
        }
    }
}

