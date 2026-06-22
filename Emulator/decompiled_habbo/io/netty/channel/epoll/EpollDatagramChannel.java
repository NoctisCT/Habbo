/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollDatagramChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.unix.DatagramSocketAddress;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class EpollDatagramChannel
extends AbstractEpollChannel
implements DatagramChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(true);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(InetSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    private final EpollDatagramChannelConfig config = new EpollDatagramChannelConfig(this);
    private volatile boolean connected;

    public EpollDatagramChannel() {
        this((InternetProtocolFamily)null);
    }

    public EpollDatagramChannel(InternetProtocolFamily family) {
        this(family == null ? LinuxSocket.newSocketDgram(Socket.isIPv6Preferred()) : LinuxSocket.newSocketDgram(family == InternetProtocolFamily.IPv6), false);
    }

    public EpollDatagramChannel(int fd) {
        this(new LinuxSocket(fd), true);
    }

    private EpollDatagramChannel(LinuxSocket fd, boolean active) {
        super(null, fd, active);
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public boolean isActive() {
        return this.socket.isOpen() && (this.config.getActiveOnOpen() && this.isRegistered() || this.active);
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress) {
        return this.joinGroup(multicastAddress, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            return this.joinGroup(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), null, promise);
        }
        catch (IOException e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.joinGroup(multicastAddress, networkInterface, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        return this.joinGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.joinGroup(multicastAddress, networkInterface, source, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        try {
            this.socket.joinGroup(multicastAddress, networkInterface, source);
            promise.setSuccess();
        }
        catch (IOException e) {
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress) {
        return this.leaveGroup(multicastAddress, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            return this.leaveGroup(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), null, promise);
        }
        catch (IOException e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.leaveGroup(multicastAddress, networkInterface, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        return this.leaveGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.leaveGroup(multicastAddress, networkInterface, source, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        try {
            this.socket.leaveGroup(multicastAddress, networkInterface, source);
            promise.setSuccess();
        }
        catch (IOException e) {
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
        return this.block(multicastAddress, networkInterface, sourceToBlock, this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(sourceToBlock, "sourceToBlock");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        promise.setFailure(new UnsupportedOperationException("Multicast not supported"));
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
        return this.block(multicastAddress, sourceToBlock, this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
        try {
            return this.block(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), sourceToBlock, promise);
        }
        catch (Throwable e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollDatagramChannelUnsafe();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        InetSocketAddress socketAddress;
        if (localAddress instanceof InetSocketAddress && (socketAddress = (InetSocketAddress)localAddress).getAddress().isAnyLocalAddress() && socketAddress.getAddress() instanceof Inet4Address && Socket.isIPv6Preferred()) {
            localAddress = new InetSocketAddress(LinuxSocket.INET6_ANY, socketAddress.getPort());
        }
        super.doBind(localAddress);
        this.active = true;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        block2: while (true) {
            Object msg;
            if ((msg = in.current()) == null) {
                this.clearFlag(Native.EPOLLOUT);
                break;
            }
            try {
                if (Native.IS_SUPPORTING_SENDMMSG && in.size() > 1) {
                    NativeDatagramPacketArray array = this.cleanDatagramPacketArray();
                    array.add(in, this.isConnected());
                    int cnt = array.count();
                    if (cnt >= 1) {
                        int offset = 0;
                        NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
                        while (true) {
                            if (cnt <= 0) continue block2;
                            int send = this.socket.sendmmsg(packets, offset, cnt);
                            if (send == 0) {
                                this.setFlag(Native.EPOLLOUT);
                                return;
                            }
                            for (int i = 0; i < send; ++i) {
                                in.remove();
                            }
                            cnt -= send;
                            offset += send;
                        }
                    }
                }
                boolean done = false;
                for (int i = this.config().getWriteSpinCount(); i > 0; --i) {
                    if (!this.doWriteMessage(msg)) continue;
                    done = true;
                    break;
                }
                if (done) {
                    in.remove();
                    continue;
                }
                this.setFlag(Native.EPOLLOUT);
            }
            catch (IOException e) {
                in.remove(e);
                continue;
            }
            break;
        }
    }

    private boolean doWriteMessage(Object msg) throws Exception {
        long writtenBytes;
        InetSocketAddress remoteAddress;
        ByteBuf data;
        if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope envelope = (AddressedEnvelope)msg;
            data = (ByteBuf)envelope.content();
            remoteAddress = (InetSocketAddress)envelope.recipient();
        } else {
            data = (ByteBuf)msg;
            remoteAddress = null;
        }
        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        if (data.hasMemoryAddress()) {
            long memoryAddress = data.memoryAddress();
            writtenBytes = remoteAddress == null ? (long)this.socket.writeAddress(memoryAddress, data.readerIndex(), data.writerIndex()) : (long)this.socket.sendToAddress(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress.getAddress(), remoteAddress.getPort());
        } else if (data.nioBufferCount() > 1) {
            IovArray array = ((EpollEventLoop)this.eventLoop()).cleanIovArray();
            array.add(data, data.readerIndex(), data.readableBytes());
            int cnt = array.count();
            assert (cnt != 0);
            writtenBytes = remoteAddress == null ? this.socket.writevAddresses(array.memoryAddress(0), cnt) : (long)this.socket.sendToAddresses(array.memoryAddress(0), cnt, remoteAddress.getAddress(), remoteAddress.getPort());
        } else {
            ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
            writtenBytes = remoteAddress == null ? (long)this.socket.write(nioData, nioData.position(), nioData.limit()) : (long)this.socket.sendTo(nioData, nioData.position(), nioData.limit(), remoteAddress.getAddress(), remoteAddress.getPort());
        }
        return writtenBytes > 0L;
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        AddressedEnvelope<ByteBuf, InetSocketAddress> e;
        if (msg instanceof DatagramPacket) {
            DatagramPacket packet = (DatagramPacket)msg;
            ByteBuf content = (ByteBuf)packet.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DatagramPacket(this.newDirectBuffer(packet, content), (InetSocketAddress)packet.recipient()) : msg;
        }
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? this.newDirectBuffer(buf) : buf;
        }
        if (msg instanceof AddressedEnvelope && (e = (AddressedEnvelope<ByteBuf, InetSocketAddress>)msg).content() instanceof ByteBuf && (e.recipient() == null || e.recipient() instanceof InetSocketAddress)) {
            ByteBuf content = (ByteBuf)e.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DefaultAddressedEnvelope<ByteBuf, InetSocketAddress>(this.newDirectBuffer(e, content), (InetSocketAddress)e.recipient()) : e;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    @Override
    public EpollDatagramChannelConfig config() {
        return this.config;
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.socket.disconnect();
        this.active = false;
        this.connected = false;
        this.resetCachedAddresses();
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (super.doConnect(remoteAddress, localAddress)) {
            this.connected = true;
            return true;
        }
        return false;
    }

    @Override
    protected void doClose() throws Exception {
        super.doClose();
        this.connected = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean connectedRead(EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, int maxDatagramPacketSize) throws Exception {
        try {
            int localReadAmount;
            int writable = maxDatagramPacketSize != 0 ? Math.min(byteBuf.writableBytes(), maxDatagramPacketSize) : byteBuf.writableBytes();
            allocHandle.attemptedBytesRead(writable);
            int writerIndex = byteBuf.writerIndex();
            if (byteBuf.hasMemoryAddress()) {
                localReadAmount = this.socket.readAddress(byteBuf.memoryAddress(), writerIndex, writerIndex + writable);
            } else {
                ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, writable);
                localReadAmount = this.socket.read(buf, buf.position(), buf.limit());
            }
            if (localReadAmount <= 0) {
                allocHandle.lastBytesRead(localReadAmount);
                boolean buf = false;
                return buf;
            }
            byteBuf.writerIndex(writerIndex + localReadAmount);
            allocHandle.lastBytesRead(maxDatagramPacketSize <= 0 ? localReadAmount : writable);
            DatagramPacket packet = new DatagramPacket(byteBuf, this.localAddress(), this.remoteAddress());
            allocHandle.incMessagesRead(1);
            this.pipeline().fireChannelRead(packet);
            byteBuf = null;
            boolean bl = true;
            return bl;
        }
        finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
        }
    }

    private IOException translateForConnected(Errors.NativeIoException e) {
        if (e.expectedErr() == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
            PortUnreachableException error = new PortUnreachableException(e.getMessage());
            error.initCause(e);
            return error;
        }
        return e;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean scatteringRead(EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, int datagramSize, int numDatagram) throws IOException {
        ArrayList bufferPackets = null;
        try {
            int i;
            int offset = byteBuf.writerIndex();
            NativeDatagramPacketArray array = this.cleanDatagramPacketArray();
            int i2 = 0;
            while (i2 < numDatagram && array.addWritable(byteBuf, offset, datagramSize)) {
                ++i2;
                offset += datagramSize;
            }
            allocHandle.attemptedBytesRead(offset - byteBuf.writerIndex());
            NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
            int received = this.socket.recvmmsg(packets, 0, array.count());
            if (received == 0) {
                allocHandle.lastBytesRead(-1);
                boolean bl = false;
                return bl;
            }
            int bytesReceived = received * datagramSize;
            byteBuf.writerIndex(bytesReceived);
            InetSocketAddress local = this.localAddress();
            if (received == 1) {
                DatagramPacket packet = packets[0].newDatagramPacket(byteBuf, local);
                allocHandle.lastBytesRead(datagramSize);
                allocHandle.incMessagesRead(1);
                this.pipeline().fireChannelRead(packet);
                byteBuf = null;
                boolean bl = true;
                return bl;
            }
            bufferPackets = RecyclableArrayList.newInstance();
            for (i = 0; i < received; ++i) {
                DatagramPacket packet = packets[i].newDatagramPacket(byteBuf.readRetainedSlice(datagramSize), local);
                ((RecyclableArrayList)bufferPackets).add(packet);
            }
            allocHandle.lastBytesRead(bytesReceived);
            allocHandle.incMessagesRead(received);
            for (i = 0; i < received; ++i) {
                this.pipeline().fireChannelRead(((RecyclableArrayList)bufferPackets).set(i, Unpooled.EMPTY_BUFFER));
            }
            ((RecyclableArrayList)bufferPackets).recycle();
            bufferPackets = null;
            boolean bl = true;
            return bl;
        }
        finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
            if (bufferPackets != null) {
                for (int i = 0; i < bufferPackets.size(); ++i) {
                    ReferenceCountUtil.release(bufferPackets.get(i));
                }
                ((RecyclableArrayList)bufferPackets).recycle();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean read(EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, int maxDatagramPacketSize) throws IOException {
        try {
            DatagramSocketAddress remoteAddress;
            int writable = maxDatagramPacketSize != 0 ? Math.min(byteBuf.writableBytes(), maxDatagramPacketSize) : byteBuf.writableBytes();
            allocHandle.attemptedBytesRead(writable);
            int writerIndex = byteBuf.writerIndex();
            if (byteBuf.hasMemoryAddress()) {
                remoteAddress = this.socket.recvFromAddress(byteBuf.memoryAddress(), writerIndex, writerIndex + writable);
            } else {
                ByteBuffer nioData = byteBuf.internalNioBuffer(writerIndex, writable);
                remoteAddress = this.socket.recvFrom(nioData, nioData.position(), nioData.limit());
            }
            if (remoteAddress == null) {
                allocHandle.lastBytesRead(-1);
                boolean nioData = false;
                return nioData;
            }
            InetSocketAddress localAddress = remoteAddress.localAddress();
            if (localAddress == null) {
                localAddress = this.localAddress();
            }
            int received = remoteAddress.receivedAmount();
            allocHandle.lastBytesRead(maxDatagramPacketSize <= 0 ? received : writable);
            byteBuf.writerIndex(writerIndex + received);
            allocHandle.incMessagesRead(1);
            this.pipeline().fireChannelRead(new DatagramPacket(byteBuf, localAddress, remoteAddress));
            byteBuf = null;
            boolean bl = true;
            return bl;
        }
        finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
        }
    }

    private NativeDatagramPacketArray cleanDatagramPacketArray() {
        return ((EpollEventLoop)this.eventLoop()).cleanDatagramPacketArray();
    }

    static /* synthetic */ boolean access$000(EpollDatagramChannel x0, EpollRecvByteAllocatorHandle x1, ByteBuf x2, int x3) throws Exception {
        return x0.connectedRead(x1, x2, x3);
    }

    static /* synthetic */ boolean access$100(EpollDatagramChannel x0, EpollRecvByteAllocatorHandle x1, ByteBuf x2, int x3) throws IOException {
        return x0.read(x1, x2, x3);
    }

    static /* synthetic */ boolean access$200(EpollDatagramChannel x0, EpollRecvByteAllocatorHandle x1, ByteBuf x2, int x3, int x4) throws IOException {
        return x0.scatteringRead(x1, x2, x3, x4);
    }

    static /* synthetic */ IOException access$300(EpollDatagramChannel x0, Errors.NativeIoException x1) {
        return x0.translateForConnected(x1);
    }

    final class EpollDatagramChannelUnsafe
    extends AbstractEpollChannel.AbstractEpollUnsafe {
        EpollDatagramChannelUnsafe() {
        }

        /*
         * Exception decompiling
         */
        @Override
        void epollInReady() {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [7[DOLOOP]], but top level block is 12[SIMPLE_IF_TAKEN]
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        }
    }
}

