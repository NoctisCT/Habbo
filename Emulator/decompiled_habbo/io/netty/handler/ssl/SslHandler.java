/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractCoalescingBufferQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.ssl.ApplicationProtocolAccessor;
import io.netty.handler.ssl.ConscryptAlpnSslEngine;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.handler.ssl.SslCloseCompletionEvent;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.handler.ssl.SslHandshakeTimeoutException;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;

public class SslHandler
extends ByteToMessageDecoder
implements ChannelOutboundHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
    private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
    private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
    private static final int MAX_PLAINTEXT_LENGTH = 16384;
    private volatile ChannelHandlerContext ctx;
    private final SSLEngine engine;
    private final SslEngineType engineType;
    private final Executor delegatedTaskExecutor;
    private final boolean jdkCompatibilityMode;
    private final ByteBuffer[] singleBuffer = new ByteBuffer[1];
    private final boolean startTls;
    private boolean sentFirstMessage;
    private boolean flushedBeforeHandshake;
    private boolean readDuringHandshake;
    private boolean handshakeStarted;
    private SslHandlerCoalescingBufferQueue pendingUnencryptedWrites;
    private Promise<Channel> handshakePromise = new LazyChannelPromise();
    private final LazyChannelPromise sslClosePromise = new LazyChannelPromise();
    private boolean needsFlush;
    private boolean outboundClosed;
    private boolean closeNotify;
    private boolean processTask;
    private int packetLength;
    private boolean firedChannelRead;
    private volatile long handshakeTimeoutMillis = 10000L;
    private volatile long closeNotifyFlushTimeoutMillis = 3000L;
    private volatile long closeNotifyReadTimeoutMillis;
    volatile int wrapDataSize = 16384;

    public SslHandler(SSLEngine engine) {
        this(engine, false);
    }

    public SslHandler(SSLEngine engine, boolean startTls) {
        this(engine, startTls, ImmediateExecutor.INSTANCE);
    }

    public SslHandler(SSLEngine engine, Executor delegatedTaskExecutor) {
        this(engine, false, delegatedTaskExecutor);
    }

    public SslHandler(SSLEngine engine, boolean startTls, Executor delegatedTaskExecutor) {
        this.engine = ObjectUtil.checkNotNull(engine, "engine");
        this.delegatedTaskExecutor = ObjectUtil.checkNotNull(delegatedTaskExecutor, "delegatedTaskExecutor");
        this.engineType = SslEngineType.forEngine(engine);
        this.startTls = startTls;
        this.jdkCompatibilityMode = this.engineType.jdkCompatibilityMode(engine);
        this.setCumulator(this.engineType.cumulator);
    }

    public long getHandshakeTimeoutMillis() {
        return this.handshakeTimeoutMillis;
    }

    public void setHandshakeTimeout(long handshakeTimeout, TimeUnit unit) {
        ObjectUtil.checkNotNull(unit, "unit");
        this.setHandshakeTimeoutMillis(unit.toMillis(handshakeTimeout));
    }

    public void setHandshakeTimeoutMillis(long handshakeTimeoutMillis) {
        if (handshakeTimeoutMillis < 0L) {
            throw new IllegalArgumentException("handshakeTimeoutMillis: " + handshakeTimeoutMillis + " (expected: >= 0)");
        }
        this.handshakeTimeoutMillis = handshakeTimeoutMillis;
    }

    public final void setWrapDataSize(int wrapDataSize) {
        this.wrapDataSize = wrapDataSize;
    }

    @Deprecated
    public long getCloseNotifyTimeoutMillis() {
        return this.getCloseNotifyFlushTimeoutMillis();
    }

    @Deprecated
    public void setCloseNotifyTimeout(long closeNotifyTimeout, TimeUnit unit) {
        this.setCloseNotifyFlushTimeout(closeNotifyTimeout, unit);
    }

    @Deprecated
    public void setCloseNotifyTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
        this.setCloseNotifyFlushTimeoutMillis(closeNotifyFlushTimeoutMillis);
    }

    public final long getCloseNotifyFlushTimeoutMillis() {
        return this.closeNotifyFlushTimeoutMillis;
    }

    public final void setCloseNotifyFlushTimeout(long closeNotifyFlushTimeout, TimeUnit unit) {
        this.setCloseNotifyFlushTimeoutMillis(unit.toMillis(closeNotifyFlushTimeout));
    }

    public final void setCloseNotifyFlushTimeoutMillis(long closeNotifyFlushTimeoutMillis) {
        if (closeNotifyFlushTimeoutMillis < 0L) {
            throw new IllegalArgumentException("closeNotifyFlushTimeoutMillis: " + closeNotifyFlushTimeoutMillis + " (expected: >= 0)");
        }
        this.closeNotifyFlushTimeoutMillis = closeNotifyFlushTimeoutMillis;
    }

    public final long getCloseNotifyReadTimeoutMillis() {
        return this.closeNotifyReadTimeoutMillis;
    }

    public final void setCloseNotifyReadTimeout(long closeNotifyReadTimeout, TimeUnit unit) {
        this.setCloseNotifyReadTimeoutMillis(unit.toMillis(closeNotifyReadTimeout));
    }

    public final void setCloseNotifyReadTimeoutMillis(long closeNotifyReadTimeoutMillis) {
        if (closeNotifyReadTimeoutMillis < 0L) {
            throw new IllegalArgumentException("closeNotifyReadTimeoutMillis: " + closeNotifyReadTimeoutMillis + " (expected: >= 0)");
        }
        this.closeNotifyReadTimeoutMillis = closeNotifyReadTimeoutMillis;
    }

    public SSLEngine engine() {
        return this.engine;
    }

    public String applicationProtocol() {
        SSLEngine engine = this.engine();
        if (!(engine instanceof ApplicationProtocolAccessor)) {
            return null;
        }
        return ((ApplicationProtocolAccessor)((Object)engine)).getNegotiatedApplicationProtocol();
    }

    public Future<Channel> handshakeFuture() {
        return this.handshakePromise;
    }

    @Deprecated
    public ChannelFuture close() {
        return this.closeOutbound();
    }

    @Deprecated
    public ChannelFuture close(ChannelPromise promise) {
        return this.closeOutbound(promise);
    }

    public ChannelFuture closeOutbound() {
        return this.closeOutbound(this.ctx.newPromise());
    }

    public ChannelFuture closeOutbound(final ChannelPromise promise) {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx.executor().inEventLoop()) {
            this.closeOutbound0(promise);
        } else {
            ctx.executor().execute(new Runnable(){

                @Override
                public void run() {
                    SslHandler.this.closeOutbound0(promise);
                }
            });
        }
        return promise;
    }

    private void closeOutbound0(ChannelPromise promise) {
        block2: {
            this.outboundClosed = true;
            this.engine.closeOutbound();
            try {
                this.flush(this.ctx, promise);
            }
            catch (Exception e) {
                if (promise.tryFailure(e)) break block2;
                logger.warn("{} flush() raised a masked exception.", (Object)this.ctx.channel(), (Object)e);
            }
        }
    }

    public Future<Channel> sslCloseFuture() {
        return this.sslClosePromise;
    }

    @Override
    public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        if (!this.pendingUnencryptedWrites.isEmpty()) {
            this.pendingUnencryptedWrites.releaseAndFailAll(ctx, new ChannelException("Pending write on removal of SslHandler"));
        }
        this.pendingUnencryptedWrites = null;
        SSLHandshakeException cause = null;
        if (!this.handshakePromise.isDone() && this.handshakePromise.tryFailure(cause = new SSLHandshakeException("SslHandler removed before handshake completed"))) {
            ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
        }
        if (!this.sslClosePromise.isDone()) {
            if (cause == null) {
                cause = new SSLHandshakeException("SslHandler removed before handshake completed");
            }
            this.notifyClosePromise(cause);
        }
        if (this.engine instanceof ReferenceCounted) {
            ((ReferenceCounted)((Object)this.engine)).release();
        }
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.closeOutboundAndChannel(ctx, promise, true);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        this.closeOutboundAndChannel(ctx, promise, false);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (!this.handshakePromise.isDone()) {
            this.readDuringHandshake = true;
        }
        ctx.read();
    }

    private static IllegalStateException newPendingWritesNullException() {
        return new IllegalStateException("pendingUnencryptedWrites is null, handlerRemoved0 called?");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(msg, ByteBuf.class);
            ReferenceCountUtil.safeRelease(msg);
            promise.setFailure(exception);
        } else if (this.pendingUnencryptedWrites == null) {
            ReferenceCountUtil.safeRelease(msg);
            promise.setFailure(SslHandler.newPendingWritesNullException());
        } else {
            this.pendingUnencryptedWrites.add((ByteBuf)msg, promise);
        }
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.startTls && !this.sentFirstMessage) {
            this.sentFirstMessage = true;
            this.pendingUnencryptedWrites.writeAndRemoveAll(ctx);
            this.forceFlush(ctx);
            this.startHandshakeProcessing();
            return;
        }
        if (this.processTask) {
            return;
        }
        try {
            this.wrapAndFlush(ctx);
        }
        catch (Throwable cause) {
            this.setHandshakeFailure(ctx, cause);
            PlatformDependent.throwException(cause);
        }
    }

    private void wrapAndFlush(ChannelHandlerContext ctx) throws SSLException {
        if (this.pendingUnencryptedWrites.isEmpty()) {
            this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, ctx.newPromise());
        }
        if (!this.handshakePromise.isDone()) {
            this.flushedBeforeHandshake = true;
        }
        try {
            this.wrap(ctx, false);
        }
        finally {
            this.forceFlush(ctx);
        }
    }

    /*
     * Exception decompiling
     */
    private void wrap(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 8[CASE]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private void finishWrap(ChannelHandlerContext ctx, ByteBuf out, ChannelPromise promise, boolean inUnwrap, boolean needUnwrap) {
        if (out == null) {
            out = Unpooled.EMPTY_BUFFER;
        } else if (!out.isReadable()) {
            out.release();
            out = Unpooled.EMPTY_BUFFER;
        }
        if (promise != null) {
            ctx.write(out, promise);
        } else {
            ctx.write(out);
        }
        if (inUnwrap) {
            this.needsFlush = true;
        }
        if (needUnwrap) {
            this.readIfNeeded(ctx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean wrapNonAppData(final ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
        block22: {
            ReferenceCounted out = null;
            ByteBufAllocator alloc = ctx.alloc();
            try {
                while (!ctx.isRemoved()) {
                    SSLEngineResult result;
                    if (out == null) {
                        out = this.allocateOutNetBuf(ctx, 2048, 1);
                    }
                    if ((result = this.wrap(alloc, this.engine, Unpooled.EMPTY_BUFFER, (ByteBuf)out)).bytesProduced() > 0) {
                        ctx.write(out).addListener(new ChannelFutureListener(){

                            @Override
                            public void operationComplete(ChannelFuture future) {
                                Throwable cause = future.cause();
                                if (cause != null) {
                                    SslHandler.this.setHandshakeFailureTransportFailure(ctx, cause);
                                }
                            }
                        });
                        if (inUnwrap) {
                            this.needsFlush = true;
                        }
                        out = null;
                    }
                    SSLEngineResult.HandshakeStatus status = result.getHandshakeStatus();
                    switch (status) {
                        case FINISHED: {
                            this.setHandshakeSuccess();
                            boolean bl = false;
                            return bl;
                        }
                        case NEED_TASK: {
                            if (this.runDelegatedTasks(inUnwrap)) break;
                            break block22;
                        }
                        case NEED_UNWRAP: {
                            if (inUnwrap) {
                                boolean bl = false;
                                return bl;
                            }
                            this.unwrapNonAppData(ctx);
                            break;
                        }
                        case NEED_WRAP: {
                            break;
                        }
                        case NOT_HANDSHAKING: {
                            this.setHandshakeSuccessIfStillHandshaking();
                            if (!inUnwrap) {
                                this.unwrapNonAppData(ctx);
                            }
                            boolean bl = true;
                            return bl;
                        }
                        default: {
                            throw new IllegalStateException("Unknown handshake status: " + (Object)((Object)result.getHandshakeStatus()));
                        }
                    }
                    if (result.bytesProduced() == 0 && status != SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    } else if (result.bytesConsumed() != 0 || result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) continue;
                    break;
                }
            }
            finally {
                if (out != null) {
                    out.release();
                }
            }
        }
        return false;
    }

    private SSLEngineResult wrap(ByteBufAllocator alloc, SSLEngine engine, ByteBuf in, ByteBuf out) throws SSLException {
        ReferenceCounted newDirectIn = null;
        try {
            ByteBuffer[] in0;
            int readerIndex = in.readerIndex();
            int readableBytes = in.readableBytes();
            if (in.isDirect() || !this.engineType.wantsDirectBuffer) {
                if (!(in instanceof CompositeByteBuf) && in.nioBufferCount() == 1) {
                    in0 = this.singleBuffer;
                    in0[0] = in.internalNioBuffer(readerIndex, readableBytes);
                } else {
                    in0 = in.nioBuffers();
                }
            } else {
                newDirectIn = alloc.directBuffer(readableBytes);
                ((ByteBuf)newDirectIn).writeBytes(in, readerIndex, readableBytes);
                in0 = this.singleBuffer;
                in0[0] = ((ByteBuf)newDirectIn).internalNioBuffer(((ByteBuf)newDirectIn).readerIndex(), readableBytes);
            }
            while (true) {
                ByteBuffer out0 = out.nioBuffer(out.writerIndex(), out.writableBytes());
                SSLEngineResult result = engine.wrap(in0, out0);
                in.skipBytes(result.bytesConsumed());
                out.writerIndex(out.writerIndex() + result.bytesProduced());
                switch (result.getStatus()) {
                    case BUFFER_OVERFLOW: {
                        out.ensureWritable(engine.getSession().getPacketBufferSize());
                        break;
                    }
                    default: {
                        SSLEngineResult sSLEngineResult = result;
                        return sSLEngineResult;
                    }
                }
            }
        }
        finally {
            this.singleBuffer[0] = null;
            if (newDirectIn != null) {
                newDirectIn.release();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        block2: {
            boolean handshakeFailed = this.handshakePromise.cause() != null;
            ClosedChannelException exception = new ClosedChannelException();
            this.setHandshakeFailure(ctx, exception, !this.outboundClosed, this.handshakeStarted, false);
            this.notifyClosePromise(exception);
            try {
                super.channelInactive(ctx);
            }
            catch (DecoderException e) {
                if (handshakeFailed && e.getCause() instanceof SSLException) break block2;
                throw e;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this.ignoreException(cause)) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify", (Object)ctx.channel(), (Object)cause);
            }
            if (ctx.channel().isActive()) {
                ctx.close();
            }
        } else {
            ctx.fireExceptionCaught(cause);
        }
    }

    private boolean ignoreException(Throwable t) {
        if (!(t instanceof SSLException) && t instanceof IOException && this.sslClosePromise.isDone()) {
            StackTraceElement[] elements;
            String message = t.getMessage();
            if (message != null && IGNORABLE_ERROR_MESSAGE.matcher(message).matches()) {
                return true;
            }
            for (StackTraceElement element : elements = t.getStackTrace()) {
                String classname = element.getClassName();
                String methodname = element.getMethodName();
                if (classname.startsWith("io.netty.") || !"read".equals(methodname)) continue;
                if (IGNORABLE_CLASS_IN_STACK.matcher(classname).matches()) {
                    return true;
                }
                try {
                    Class<?> clazz = PlatformDependent.getClassLoader(this.getClass()).loadClass(classname);
                    if (SocketChannel.class.isAssignableFrom(clazz) || DatagramChannel.class.isAssignableFrom(clazz)) {
                        return true;
                    }
                    if (PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals(clazz.getSuperclass().getName())) {
                        return true;
                    }
                }
                catch (Throwable cause) {
                    if (!logger.isDebugEnabled()) continue;
                    logger.debug("Unexpected exception while loading class {} classname {}", this.getClass(), classname, cause);
                }
            }
        }
        return false;
    }

    public static boolean isEncrypted(ByteBuf buffer) {
        if (buffer.readableBytes() < 5) {
            throw new IllegalArgumentException("buffer must have at least 5 readable bytes");
        }
        return SslUtils.getEncryptedPacketLength(buffer, buffer.readerIndex()) != -2;
    }

    private void decodeJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) throws NotSslRecordException {
        int packetLength = this.packetLength;
        if (packetLength > 0) {
            if (in.readableBytes() < packetLength) {
                return;
            }
        } else {
            int readableBytes = in.readableBytes();
            if (readableBytes < 5) {
                return;
            }
            packetLength = SslUtils.getEncryptedPacketLength(in, in.readerIndex());
            if (packetLength == -2) {
                NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
                in.skipBytes(in.readableBytes());
                this.setHandshakeFailure(ctx, e);
                throw e;
            }
            assert (packetLength > 0);
            if (packetLength > readableBytes) {
                this.packetLength = packetLength;
                return;
            }
        }
        this.packetLength = 0;
        try {
            int bytesConsumed = this.unwrap(ctx, in, in.readerIndex(), packetLength);
            assert (bytesConsumed == packetLength || this.engine.isInboundDone()) : "we feed the SSLEngine a packets worth of data: " + packetLength + " but it only consumed: " + bytesConsumed;
            in.skipBytes(bytesConsumed);
        }
        catch (Throwable cause) {
            this.handleUnwrapThrowable(ctx, cause);
        }
    }

    private void decodeNonJdkCompatible(ChannelHandlerContext ctx, ByteBuf in) {
        try {
            in.skipBytes(this.unwrap(ctx, in, in.readerIndex(), in.readableBytes()));
        }
        catch (Throwable cause) {
            this.handleUnwrapThrowable(ctx, cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleUnwrapThrowable(ChannelHandlerContext ctx, Throwable cause) {
        try {
            if (this.handshakePromise.tryFailure(cause)) {
                ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
            }
            this.wrapAndFlush(ctx);
        }
        catch (SSLException ex) {
            logger.debug("SSLException during trying to call SSLEngine.wrap(...) because of an previous SSLException, ignoring...", ex);
        }
        finally {
            this.setHandshakeFailure(ctx, cause, true, false, true);
        }
        PlatformDependent.throwException(cause);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws SSLException {
        if (this.processTask) {
            return;
        }
        if (this.jdkCompatibilityMode) {
            this.decodeJdkCompatible(ctx, in);
        } else {
            this.decodeNonJdkCompatible(ctx, in);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.channelReadComplete0(ctx);
    }

    private void channelReadComplete0(ChannelHandlerContext ctx) {
        this.discardSomeReadBytes();
        this.flushIfNeeded(ctx);
        this.readIfNeeded(ctx);
        this.firedChannelRead = false;
        ctx.fireChannelReadComplete();
    }

    private void readIfNeeded(ChannelHandlerContext ctx) {
        if (!(ctx.channel().config().isAutoRead() || this.firedChannelRead && this.handshakePromise.isDone())) {
            ctx.read();
        }
    }

    private void flushIfNeeded(ChannelHandlerContext ctx) {
        if (this.needsFlush) {
            this.forceFlush(ctx);
        }
    }

    private void unwrapNonAppData(ChannelHandlerContext ctx) throws SSLException {
        this.unwrap(ctx, Unpooled.EMPTY_BUFFER, 0, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int unwrap(ChannelHandlerContext ctx, ByteBuf packet, int offset, int length) throws SSLException {
        int originalLength = length;
        boolean wrapLater = false;
        boolean notifyClosure = false;
        int overflowReadableBytes = -1;
        ByteBuf decodeOut = this.allocate(ctx, length);
        try {
            block14: while (!ctx.isRemoved()) {
                SSLEngineResult result = this.engineType.unwrap(this, packet, offset, length, decodeOut);
                SSLEngineResult.Status status = result.getStatus();
                SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();
                int produced = result.bytesProduced();
                int consumed = result.bytesConsumed();
                offset += consumed;
                length -= consumed;
                switch (status) {
                    case BUFFER_OVERFLOW: {
                        int readableBytes = decodeOut.readableBytes();
                        int previousOverflowReadableBytes = overflowReadableBytes;
                        overflowReadableBytes = readableBytes;
                        int bufferSize = this.engine.getSession().getApplicationBufferSize() - readableBytes;
                        if (readableBytes > 0) {
                            this.firedChannelRead = true;
                            ctx.fireChannelRead(decodeOut);
                            decodeOut = null;
                            if (bufferSize <= 0) {
                                bufferSize = this.engine.getSession().getApplicationBufferSize();
                            }
                        } else {
                            decodeOut.release();
                            decodeOut = null;
                        }
                        if (readableBytes == 0 && previousOverflowReadableBytes == 0) {
                            throw new IllegalStateException("Two consecutive overflows but no content was consumed. " + SSLSession.class.getSimpleName() + " getApplicationBufferSize: " + this.engine.getSession().getApplicationBufferSize() + " maybe too small.");
                        }
                        decodeOut = this.allocate(ctx, this.engineType.calculatePendingData(this, bufferSize));
                        continue block14;
                    }
                    case CLOSED: {
                        notifyClosure = true;
                        overflowReadableBytes = -1;
                        break;
                    }
                    default: {
                        overflowReadableBytes = -1;
                    }
                }
                switch (handshakeStatus) {
                    case NEED_UNWRAP: {
                        break;
                    }
                    case NEED_WRAP: {
                        if (!this.wrapNonAppData(ctx, true) || length != 0) break;
                        break block14;
                    }
                    case NEED_TASK: {
                        if (this.runDelegatedTasks(true)) break;
                        wrapLater = false;
                        break block14;
                    }
                    case FINISHED: {
                        this.setHandshakeSuccess();
                        wrapLater = true;
                        break;
                    }
                    case NOT_HANDSHAKING: {
                        if (this.setHandshakeSuccessIfStillHandshaking()) {
                            wrapLater = true;
                            continue block14;
                        }
                        if (length != 0) break;
                        break block14;
                    }
                    default: {
                        throw new IllegalStateException("unknown handshake status: " + (Object)((Object)handshakeStatus));
                    }
                }
                if (status != SSLEngineResult.Status.BUFFER_UNDERFLOW && (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK || consumed != 0 || produced != 0)) continue;
                if (handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_UNWRAP) break;
                this.readIfNeeded(ctx);
                break;
            }
            if (this.flushedBeforeHandshake && this.handshakePromise.isDone()) {
                this.flushedBeforeHandshake = false;
                wrapLater = true;
            }
            if (wrapLater) {
                this.wrap(ctx, true);
            }
            if (notifyClosure) {
                this.notifyClosePromise(null);
            }
        }
        finally {
            if (decodeOut != null) {
                if (decodeOut.isReadable()) {
                    this.firedChannelRead = true;
                    ctx.fireChannelRead(decodeOut);
                } else {
                    decodeOut.release();
                }
            }
        }
        return originalLength - length;
    }

    private static ByteBuffer toByteBuffer(ByteBuf out, int index, int len) {
        return out.nioBufferCount() == 1 ? out.internalNioBuffer(index, len) : out.nioBuffer(index, len);
    }

    private static boolean inEventLoop(Executor executor) {
        return executor instanceof EventExecutor && ((EventExecutor)executor).inEventLoop();
    }

    private static void runAllDelegatedTasks(SSLEngine engine) {
        Runnable task;
        while ((task = engine.getDelegatedTask()) != null) {
            task.run();
        }
        return;
    }

    private boolean runDelegatedTasks(boolean inUnwrap) {
        if (this.delegatedTaskExecutor == ImmediateExecutor.INSTANCE || SslHandler.inEventLoop(this.delegatedTaskExecutor)) {
            SslHandler.runAllDelegatedTasks(this.engine);
            return true;
        }
        this.executeDelegatedTasks(inUnwrap);
        return false;
    }

    private void executeDelegatedTasks(boolean inUnwrap) {
        this.processTask = true;
        try {
            this.delegatedTaskExecutor.execute(new SslTasksRunner(inUnwrap));
        }
        catch (RejectedExecutionException e) {
            this.processTask = false;
            throw e;
        }
    }

    private boolean setHandshakeSuccessIfStillHandshaking() {
        if (!this.handshakePromise.isDone()) {
            this.setHandshakeSuccess();
            return true;
        }
        return false;
    }

    private void setHandshakeSuccess() {
        this.handshakePromise.trySuccess(this.ctx.channel());
        if (logger.isDebugEnabled()) {
            SSLSession session = this.engine.getSession();
            logger.debug("{} HANDSHAKEN: protocol:{} cipher suite:{}", this.ctx.channel(), session.getProtocol(), session.getCipherSuite());
        }
        this.ctx.fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
        if (this.readDuringHandshake && !this.ctx.channel().config().isAutoRead()) {
            this.readDuringHandshake = false;
            this.ctx.read();
        }
    }

    private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause) {
        this.setHandshakeFailure(ctx, cause, true, true, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setHandshakeFailure(ChannelHandlerContext ctx, Throwable cause, boolean closeInbound, boolean notify, boolean alwaysFlushAndClose) {
        try {
            block7: {
                this.outboundClosed = true;
                this.engine.closeOutbound();
                if (closeInbound) {
                    try {
                        this.engine.closeInbound();
                    }
                    catch (SSLException e) {
                        String msg;
                        if (!logger.isDebugEnabled() || (msg = e.getMessage()) != null && (msg.contains("possible truncation attack") || msg.contains("closing inbound before receiving peer's close_notify"))) break block7;
                        logger.debug("{} SSLEngine.closeInbound() raised an exception.", (Object)ctx.channel(), (Object)e);
                    }
                }
            }
            if (this.handshakePromise.tryFailure(cause) || alwaysFlushAndClose) {
                SslUtils.handleHandshakeFailure(ctx, cause, notify);
            }
        }
        finally {
            this.releaseAndFailAll(ctx, cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setHandshakeFailureTransportFailure(ChannelHandlerContext ctx, Throwable cause) {
        try {
            SSLException transportFailure = new SSLException("failure when writing TLS control frames", cause);
            this.releaseAndFailAll(ctx, transportFailure);
            if (this.handshakePromise.tryFailure(transportFailure)) {
                ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(transportFailure));
            }
        }
        finally {
            ctx.close();
        }
    }

    private void releaseAndFailAll(ChannelHandlerContext ctx, Throwable cause) {
        if (this.pendingUnencryptedWrites != null) {
            this.pendingUnencryptedWrites.releaseAndFailAll(ctx, cause);
        }
    }

    private void notifyClosePromise(Throwable cause) {
        if (cause == null) {
            if (this.sslClosePromise.trySuccess(this.ctx.channel())) {
                this.ctx.fireUserEventTriggered(SslCloseCompletionEvent.SUCCESS);
            }
        } else if (this.sslClosePromise.tryFailure(cause)) {
            this.ctx.fireUserEventTriggered(new SslCloseCompletionEvent(cause));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void closeOutboundAndChannel(ChannelHandlerContext ctx, ChannelPromise promise, boolean disconnect) throws Exception {
        block8: {
            block7: {
                this.outboundClosed = true;
                this.engine.closeOutbound();
                if (!ctx.channel().isActive()) {
                    if (disconnect) {
                        ctx.disconnect(promise);
                    } else {
                        ctx.close(promise);
                    }
                    return;
                }
                ChannelPromise closeNotifyPromise = ctx.newPromise();
                try {
                    this.flush(ctx, closeNotifyPromise);
                    if (this.closeNotify) break block7;
                    this.closeNotify = true;
                }
                catch (Throwable throwable) {
                    if (!this.closeNotify) {
                        this.closeNotify = true;
                        this.safeClose(ctx, closeNotifyPromise, ctx.newPromise().addListener(new ChannelPromiseNotifier(false, promise)));
                    } else {
                        this.sslClosePromise.addListener(new FutureListener<Channel>(promise){
                            final /* synthetic */ ChannelPromise val$promise;
                            {
                                this.val$promise = channelPromise;
                            }

                            @Override
                            public void operationComplete(Future<Channel> future) {
                                this.val$promise.setSuccess();
                            }
                        });
                    }
                    throw throwable;
                }
                this.safeClose(ctx, closeNotifyPromise, ctx.newPromise().addListener(new ChannelPromiseNotifier(false, promise)));
                break block8;
            }
            this.sslClosePromise.addListener(new /* invalid duplicate definition of identical inner class */);
        }
    }

    private void flush(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (this.pendingUnencryptedWrites != null) {
            this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, promise);
        } else {
            promise.setFailure(SslHandler.newPendingWritesNullException());
        }
        this.flush(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.pendingUnencryptedWrites = new SslHandlerCoalescingBufferQueue(ctx.channel(), 16);
        if (ctx.channel().isActive()) {
            this.startHandshakeProcessing();
        }
    }

    private void startHandshakeProcessing() {
        if (!this.handshakeStarted) {
            this.handshakeStarted = true;
            if (this.engine.getUseClientMode()) {
                this.handshake();
            }
            this.applyHandshakeTimeout();
        }
    }

    public Future<Channel> renegotiate() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException();
        }
        return this.renegotiate(ctx.executor().newPromise());
    }

    public Future<Channel> renegotiate(final Promise<Channel> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException();
        }
        EventExecutor executor = ctx.executor();
        if (!executor.inEventLoop()) {
            executor.execute(new Runnable(){

                @Override
                public void run() {
                    SslHandler.this.renegotiateOnEventLoop(promise);
                }
            });
            return promise;
        }
        this.renegotiateOnEventLoop(promise);
        return promise;
    }

    private void renegotiateOnEventLoop(Promise<Channel> newHandshakePromise) {
        Promise<Channel> oldHandshakePromise = this.handshakePromise;
        if (!oldHandshakePromise.isDone()) {
            oldHandshakePromise.addListener(new PromiseNotifier(newHandshakePromise));
        } else {
            this.handshakePromise = newHandshakePromise;
            this.handshake();
            this.applyHandshakeTimeout();
        }
    }

    private void handshake() {
        if (this.engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            return;
        }
        if (this.handshakePromise.isDone()) {
            return;
        }
        ChannelHandlerContext ctx = this.ctx;
        try {
            this.engine.beginHandshake();
            this.wrapNonAppData(ctx, false);
        }
        catch (Throwable e) {
            this.setHandshakeFailure(ctx, e);
        }
        finally {
            this.forceFlush(ctx);
        }
    }

    private void applyHandshakeTimeout() {
        final Promise<Channel> localHandshakePromise = this.handshakePromise;
        final long handshakeTimeoutMillis = this.handshakeTimeoutMillis;
        if (handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
            return;
        }
        final ScheduledFuture<?> timeoutFuture = this.ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                if (localHandshakePromise.isDone()) {
                    return;
                }
                SslHandshakeTimeoutException exception = new SslHandshakeTimeoutException("handshake timed out after " + handshakeTimeoutMillis + "ms");
                try {
                    if (localHandshakePromise.tryFailure(exception)) {
                        SslUtils.handleHandshakeFailure(SslHandler.this.ctx, exception, true);
                    }
                }
                finally {
                    SslHandler.this.releaseAndFailAll(SslHandler.this.ctx, exception);
                }
            }
        }, handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener<Future<Channel>>)new FutureListener<Channel>(){

            @Override
            public void operationComplete(Future<Channel> f) throws Exception {
                timeoutFuture.cancel(false);
            }
        });
    }

    private void forceFlush(ChannelHandlerContext ctx) {
        this.needsFlush = false;
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!this.startTls) {
            this.startHandshakeProcessing();
        }
        ctx.fireChannelActive();
    }

    private void safeClose(final ChannelHandlerContext ctx, final ChannelFuture flushFuture, final ChannelPromise promise) {
        long closeNotifyTimeout;
        if (!ctx.channel().isActive()) {
            ctx.close(promise);
            return;
        }
        final ScheduledFuture<?> timeoutFuture = !flushFuture.isDone() ? ((closeNotifyTimeout = this.closeNotifyFlushTimeoutMillis) > 0L ? ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                if (!flushFuture.isDone()) {
                    logger.warn("{} Last write attempt timed out; force-closing the connection.", (Object)ctx.channel());
                    SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                }
            }
        }, closeNotifyTimeout, TimeUnit.MILLISECONDS) : null) : null;
        flushFuture.addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                long closeNotifyReadTimeout;
                if (timeoutFuture != null) {
                    timeoutFuture.cancel(false);
                }
                if ((closeNotifyReadTimeout = SslHandler.this.closeNotifyReadTimeoutMillis) <= 0L) {
                    SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                } else {
                    final ScheduledFuture<?> closeNotifyReadTimeoutFuture = !SslHandler.this.sslClosePromise.isDone() ? ctx.executor().schedule(new Runnable(){

                        @Override
                        public void run() {
                            if (!SslHandler.this.sslClosePromise.isDone()) {
                                logger.debug("{} did not receive close_notify in {}ms; force-closing the connection.", (Object)ctx.channel(), (Object)closeNotifyReadTimeout);
                                SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                            }
                        }
                    }, closeNotifyReadTimeout, TimeUnit.MILLISECONDS) : null;
                    SslHandler.this.sslClosePromise.addListener(new FutureListener<Channel>(){

                        @Override
                        public void operationComplete(Future<Channel> future) throws Exception {
                            if (closeNotifyReadTimeoutFuture != null) {
                                closeNotifyReadTimeoutFuture.cancel(false);
                            }
                            SslHandler.addCloseListener(ctx.close(ctx.newPromise()), promise);
                        }
                    });
                }
            }
        });
    }

    private static void addCloseListener(ChannelFuture future, ChannelPromise promise) {
        future.addListener(new ChannelPromiseNotifier(false, promise));
    }

    private ByteBuf allocate(ChannelHandlerContext ctx, int capacity) {
        ByteBufAllocator alloc = ctx.alloc();
        if (this.engineType.wantsDirectBuffer) {
            return alloc.directBuffer(capacity);
        }
        return alloc.buffer(capacity);
    }

    private ByteBuf allocateOutNetBuf(ChannelHandlerContext ctx, int pendingBytes, int numComponents) {
        return this.engineType.allocateWrapBuffer(this, ctx.alloc(), pendingBytes, numComponents);
    }

    private static boolean attemptCopyToCumulation(ByteBuf cumulation, ByteBuf next, int wrapDataSize) {
        int inReadableBytes = next.readableBytes();
        int cumulationCapacity = cumulation.capacity();
        if (wrapDataSize - cumulation.readableBytes() >= inReadableBytes && (cumulation.isWritable(inReadableBytes) && cumulationCapacity >= wrapDataSize || cumulationCapacity < wrapDataSize && ByteBufUtil.ensureWritableSuccess(cumulation.ensureWritable(inReadableBytes, false)))) {
            cumulation.writeBytes(next);
            next.release();
            return true;
        }
        return false;
    }

    private final class LazyChannelPromise
    extends DefaultPromise<Channel> {
        private LazyChannelPromise() {
        }

        @Override
        protected EventExecutor executor() {
            if (SslHandler.this.ctx == null) {
                throw new IllegalStateException();
            }
            return SslHandler.this.ctx.executor();
        }

        @Override
        protected void checkDeadLock() {
            if (SslHandler.this.ctx == null) {
                return;
            }
            super.checkDeadLock();
        }
    }

    private final class SslHandlerCoalescingBufferQueue
    extends AbstractCoalescingBufferQueue {
        SslHandlerCoalescingBufferQueue(Channel channel, int initSize) {
            super(channel, initSize);
        }

        @Override
        protected ByteBuf compose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
            int wrapDataSize = SslHandler.this.wrapDataSize;
            if (cumulation instanceof CompositeByteBuf) {
                CompositeByteBuf composite = (CompositeByteBuf)cumulation;
                int numComponents = composite.numComponents();
                if (numComponents == 0 || !SslHandler.attemptCopyToCumulation(composite.internalComponent(numComponents - 1), next, wrapDataSize)) {
                    composite.addComponent(true, next);
                }
                return composite;
            }
            return SslHandler.attemptCopyToCumulation(cumulation, next, wrapDataSize) ? cumulation : this.copyAndCompose(alloc, cumulation, next);
        }

        @Override
        protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first) {
            if (first instanceof CompositeByteBuf) {
                CompositeByteBuf composite = (CompositeByteBuf)first;
                first = ((SslHandler)SslHandler.this).engineType.wantsDirectBuffer ? allocator.directBuffer(composite.readableBytes()) : allocator.heapBuffer(composite.readableBytes());
                try {
                    first.writeBytes(composite);
                }
                catch (Throwable cause) {
                    first.release();
                    PlatformDependent.throwException(cause);
                }
                composite.release();
            }
            return first;
        }

        @Override
        protected ByteBuf removeEmptyValue() {
            return null;
        }
    }

    private final class SslTasksRunner
    implements Runnable {
        private final boolean inUnwrap;

        SslTasksRunner(boolean inUnwrap) {
            this.inUnwrap = inUnwrap;
        }

        private void taskError(Throwable e) {
            if (this.inUnwrap) {
                try {
                    SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, e);
                }
                catch (Throwable cause) {
                    this.safeExceptionCaught(cause);
                }
            } else {
                SslHandler.this.setHandshakeFailure(SslHandler.this.ctx, e);
                SslHandler.this.forceFlush(SslHandler.this.ctx);
            }
        }

        private void safeExceptionCaught(Throwable cause) {
            try {
                SslHandler.this.exceptionCaught(SslHandler.this.ctx, this.wrapIfNeeded(cause));
            }
            catch (Throwable error) {
                SslHandler.this.ctx.fireExceptionCaught(error);
            }
        }

        private Throwable wrapIfNeeded(Throwable cause) {
            if (!this.inUnwrap) {
                return cause;
            }
            return cause instanceof DecoderException ? cause : new DecoderException(cause);
        }

        private void tryDecodeAgain() {
            try {
                SslHandler.this.channelRead(SslHandler.this.ctx, Unpooled.EMPTY_BUFFER);
            }
            catch (Throwable cause) {
                this.safeExceptionCaught(cause);
            }
            finally {
                SslHandler.this.channelReadComplete0(SslHandler.this.ctx);
            }
        }

        private void resumeOnEventExecutor() {
            assert (SslHandler.this.ctx.executor().inEventLoop());
            SslHandler.this.processTask = false;
            try {
                SSLEngineResult.HandshakeStatus status = SslHandler.this.engine.getHandshakeStatus();
                switch (status) {
                    case NEED_TASK: {
                        SslHandler.this.executeDelegatedTasks(this.inUnwrap);
                        break;
                    }
                    case FINISHED: {
                        SslHandler.this.setHandshakeSuccess();
                    }
                    case NOT_HANDSHAKING: {
                        SslHandler.this.setHandshakeSuccessIfStillHandshaking();
                        try {
                            SslHandler.this.wrap(SslHandler.this.ctx, this.inUnwrap);
                        }
                        catch (Throwable e) {
                            this.taskError(e);
                            return;
                        }
                        if (this.inUnwrap) {
                            SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
                        }
                        SslHandler.this.forceFlush(SslHandler.this.ctx);
                        this.tryDecodeAgain();
                        break;
                    }
                    case NEED_UNWRAP: {
                        try {
                            SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
                        }
                        catch (SSLException e) {
                            SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, e);
                            return;
                        }
                        this.tryDecodeAgain();
                        break;
                    }
                    case NEED_WRAP: {
                        try {
                            if (!SslHandler.this.wrapNonAppData(SslHandler.this.ctx, false) && this.inUnwrap) {
                                SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
                            }
                            SslHandler.this.forceFlush(SslHandler.this.ctx);
                        }
                        catch (Throwable e) {
                            this.taskError(e);
                            return;
                        }
                        this.tryDecodeAgain();
                        break;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
            }
            catch (Throwable cause) {
                this.safeExceptionCaught(cause);
            }
        }

        @Override
        public void run() {
            try {
                SslHandler.runAllDelegatedTasks(SslHandler.this.engine);
                assert (SslHandler.this.engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_TASK);
                SslHandler.this.ctx.executor().execute(new Runnable(){

                    @Override
                    public void run() {
                        SslTasksRunner.this.resumeOnEventExecutor();
                    }
                });
            }
            catch (Throwable cause) {
                this.handleException(cause);
            }
        }

        private void handleException(final Throwable cause) {
            if (SslHandler.this.ctx.executor().inEventLoop()) {
                SslHandler.this.processTask = false;
                this.safeExceptionCaught(cause);
            } else {
                try {
                    SslHandler.this.ctx.executor().execute(new Runnable(){

                        @Override
                        public void run() {
                            SslHandler.this.processTask = false;
                            SslTasksRunner.this.safeExceptionCaught(cause);
                        }
                    });
                }
                catch (RejectedExecutionException ignore) {
                    SslHandler.this.processTask = false;
                    SslHandler.this.ctx.fireExceptionCaught(cause);
                }
            }
        }
    }

    private static enum SslEngineType {
        TCNATIVE(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int readerIndex, int len, ByteBuf out) throws SSLException {
                SSLEngineResult result;
                int nioBufferCount = in.nioBufferCount();
                int writerIndex = out.writerIndex();
                if (nioBufferCount > 1) {
                    ReferenceCountedOpenSslEngine opensslEngine = (ReferenceCountedOpenSslEngine)handler.engine;
                    try {
                        ((SslHandler)handler).singleBuffer[0] = SslHandler.toByteBuffer(out, writerIndex, out.writableBytes());
                        result = opensslEngine.unwrap(in.nioBuffers(readerIndex, len), handler.singleBuffer);
                    }
                    finally {
                        ((SslHandler)handler).singleBuffer[0] = null;
                    }
                } else {
                    result = handler.engine.unwrap(SslHandler.toByteBuffer(in, readerIndex, len), SslHandler.toByteBuffer(out, writerIndex, out.writableBytes()));
                }
                out.writerIndex(writerIndex + result.bytesProduced());
                return result;
            }

            @Override
            ByteBuf allocateWrapBuffer(SslHandler handler, ByteBufAllocator allocator, int pendingBytes, int numComponents) {
                return allocator.directBuffer(((ReferenceCountedOpenSslEngine)handler.engine).calculateMaxLengthForWrap(pendingBytes, numComponents));
            }

            @Override
            int calculatePendingData(SslHandler handler, int guess) {
                int sslPending = ((ReferenceCountedOpenSslEngine)handler.engine).sslPending();
                return sslPending > 0 ? sslPending : guess;
            }

            @Override
            boolean jdkCompatibilityMode(SSLEngine engine) {
                return ((ReferenceCountedOpenSslEngine)engine).jdkCompatibilityMode;
            }
        }
        ,
        CONSCRYPT(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int readerIndex, int len, ByteBuf out) throws SSLException {
                SSLEngineResult result;
                int nioBufferCount = in.nioBufferCount();
                int writerIndex = out.writerIndex();
                if (nioBufferCount > 1) {
                    try {
                        ((SslHandler)handler).singleBuffer[0] = SslHandler.toByteBuffer(out, writerIndex, out.writableBytes());
                        result = ((ConscryptAlpnSslEngine)handler.engine).unwrap(in.nioBuffers(readerIndex, len), handler.singleBuffer);
                    }
                    finally {
                        ((SslHandler)handler).singleBuffer[0] = null;
                    }
                } else {
                    result = handler.engine.unwrap(SslHandler.toByteBuffer(in, readerIndex, len), SslHandler.toByteBuffer(out, writerIndex, out.writableBytes()));
                }
                out.writerIndex(writerIndex + result.bytesProduced());
                return result;
            }

            @Override
            ByteBuf allocateWrapBuffer(SslHandler handler, ByteBufAllocator allocator, int pendingBytes, int numComponents) {
                return allocator.directBuffer(((ConscryptAlpnSslEngine)handler.engine).calculateOutNetBufSize(pendingBytes, numComponents));
            }

            @Override
            int calculatePendingData(SslHandler handler, int guess) {
                return guess;
            }

            @Override
            boolean jdkCompatibilityMode(SSLEngine engine) {
                return true;
            }
        }
        ,
        JDK(false, ByteToMessageDecoder.MERGE_CUMULATOR){

            @Override
            SSLEngineResult unwrap(SslHandler handler, ByteBuf in, int readerIndex, int len, ByteBuf out) throws SSLException {
                int consumed;
                int writerIndex = out.writerIndex();
                ByteBuffer inNioBuffer = SslHandler.toByteBuffer(in, readerIndex, len);
                int position = inNioBuffer.position();
                SSLEngineResult result = handler.engine.unwrap(inNioBuffer, SslHandler.toByteBuffer(out, writerIndex, out.writableBytes()));
                out.writerIndex(writerIndex + result.bytesProduced());
                if (result.bytesConsumed() == 0 && (consumed = inNioBuffer.position() - position) != result.bytesConsumed()) {
                    return new SSLEngineResult(result.getStatus(), result.getHandshakeStatus(), consumed, result.bytesProduced());
                }
                return result;
            }

            @Override
            ByteBuf allocateWrapBuffer(SslHandler handler, ByteBufAllocator allocator, int pendingBytes, int numComponents) {
                return allocator.heapBuffer(handler.engine.getSession().getPacketBufferSize());
            }

            @Override
            int calculatePendingData(SslHandler handler, int guess) {
                return guess;
            }

            @Override
            boolean jdkCompatibilityMode(SSLEngine engine) {
                return true;
            }
        };

        final boolean wantsDirectBuffer;
        final ByteToMessageDecoder.Cumulator cumulator;

        static SslEngineType forEngine(SSLEngine engine) {
            return engine instanceof ReferenceCountedOpenSslEngine ? TCNATIVE : (engine instanceof ConscryptAlpnSslEngine ? CONSCRYPT : JDK);
        }

        private SslEngineType(boolean wantsDirectBuffer, ByteToMessageDecoder.Cumulator cumulator) {
            this.wantsDirectBuffer = wantsDirectBuffer;
            this.cumulator = cumulator;
        }

        abstract SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, int var4, ByteBuf var5) throws SSLException;

        abstract int calculatePendingData(SslHandler var1, int var2);

        abstract boolean jdkCompatibilityMode(SSLEngine var1);

        abstract ByteBuf allocateWrapBuffer(SslHandler var1, ByteBufAllocator var2, int var3, int var4);
    }
}

