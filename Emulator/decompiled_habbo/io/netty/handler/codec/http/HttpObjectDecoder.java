/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpExpectationFailedEvent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public abstract class HttpObjectDecoder
extends ByteToMessageDecoder {
    private static final String EMPTY_VALUE = "";
    private final int maxChunkSize;
    private final boolean chunkedSupported;
    protected final boolean validateHeaders;
    private final HeaderParser headerParser;
    private final LineParser lineParser;
    private HttpMessage message;
    private long chunkSize;
    private long contentLength = Long.MIN_VALUE;
    private volatile boolean resetRequested;
    private CharSequence name;
    private CharSequence value;
    private LastHttpContent trailer;
    private State currentState = State.SKIP_CONTROL_CHARS;

    protected HttpObjectDecoder() {
        this(4096, 8192, 8192, true);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, true);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, 128);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders, int initialBufferSize) {
        ObjectUtil.checkPositive(maxInitialLineLength, "maxInitialLineLength");
        ObjectUtil.checkPositive(maxHeaderSize, "maxHeaderSize");
        ObjectUtil.checkPositive(maxChunkSize, "maxChunkSize");
        AppendableCharSequence seq = new AppendableCharSequence(initialBufferSize);
        this.lineParser = new LineParser(seq, maxInitialLineLength);
        this.headerParser = new HeaderParser(seq, maxHeaderSize);
        this.maxChunkSize = maxChunkSize;
        this.chunkedSupported = chunkedSupported;
        this.validateHeaders = validateHeaders;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (this.resetRequested) {
            this.resetNow();
        }
        switch (this.currentState) {
            case SKIP_CONTROL_CHARS: 
            case READ_INITIAL: {
                try {
                    AppendableCharSequence line = this.lineParser.parse(buffer);
                    if (line == null) {
                        return;
                    }
                    String[] initialLine = HttpObjectDecoder.splitInitialLine(line);
                    if (initialLine.length < 3) {
                        this.currentState = State.SKIP_CONTROL_CHARS;
                        return;
                    }
                    this.message = this.createMessage(initialLine);
                    this.currentState = State.READ_HEADER;
                }
                catch (Exception e) {
                    out.add(this.invalidMessage(buffer, e));
                    return;
                }
            }
            case READ_HEADER: {
                try {
                    State nextState = this.readHeaders(buffer);
                    if (nextState == null) {
                        return;
                    }
                    this.currentState = nextState;
                    switch (nextState) {
                        case SKIP_CONTROL_CHARS: {
                            out.add(this.message);
                            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                            this.resetNow();
                            return;
                        }
                        case READ_CHUNK_SIZE: {
                            if (!this.chunkedSupported) {
                                throw new IllegalArgumentException("Chunked messages not supported");
                            }
                            out.add(this.message);
                            return;
                        }
                    }
                    long contentLength = this.contentLength();
                    if (contentLength == 0L || contentLength == -1L && this.isDecodingRequest()) {
                        out.add(this.message);
                        out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                        this.resetNow();
                        return;
                    }
                    assert (nextState == State.READ_FIXED_LENGTH_CONTENT || nextState == State.READ_VARIABLE_LENGTH_CONTENT);
                    out.add(this.message);
                    if (nextState == State.READ_FIXED_LENGTH_CONTENT) {
                        this.chunkSize = contentLength;
                    }
                    return;
                }
                catch (Exception e) {
                    out.add(this.invalidMessage(buffer, e));
                    return;
                }
            }
            case READ_VARIABLE_LENGTH_CONTENT: {
                int toRead = Math.min(buffer.readableBytes(), this.maxChunkSize);
                if (toRead > 0) {
                    ByteBuf content = buffer.readRetainedSlice(toRead);
                    out.add(new DefaultHttpContent(content));
                }
                return;
            }
            case READ_FIXED_LENGTH_CONTENT: {
                int readLimit = buffer.readableBytes();
                if (readLimit == 0) {
                    return;
                }
                int toRead = Math.min(readLimit, this.maxChunkSize);
                if ((long)toRead > this.chunkSize) {
                    toRead = (int)this.chunkSize;
                }
                ByteBuf content = buffer.readRetainedSlice(toRead);
                this.chunkSize -= (long)toRead;
                if (this.chunkSize == 0L) {
                    out.add(new DefaultLastHttpContent(content, this.validateHeaders));
                    this.resetNow();
                } else {
                    out.add(new DefaultHttpContent(content));
                }
                return;
            }
            case READ_CHUNK_SIZE: {
                try {
                    AppendableCharSequence line = this.lineParser.parse(buffer);
                    if (line == null) {
                        return;
                    }
                    int chunkSize = HttpObjectDecoder.getChunkSize(line.toString());
                    this.chunkSize = chunkSize;
                    if (chunkSize == 0) {
                        this.currentState = State.READ_CHUNK_FOOTER;
                        return;
                    }
                    this.currentState = State.READ_CHUNKED_CONTENT;
                }
                catch (Exception e) {
                    out.add(this.invalidChunk(buffer, e));
                    return;
                }
            }
            case READ_CHUNKED_CONTENT: {
                assert (this.chunkSize <= Integer.MAX_VALUE);
                int toRead = Math.min((int)this.chunkSize, this.maxChunkSize);
                if ((toRead = Math.min(toRead, buffer.readableBytes())) == 0) {
                    return;
                }
                DefaultHttpContent chunk = new DefaultHttpContent(buffer.readRetainedSlice(toRead));
                this.chunkSize -= (long)toRead;
                out.add(chunk);
                if (this.chunkSize != 0L) {
                    return;
                }
                this.currentState = State.READ_CHUNK_DELIMITER;
            }
            case READ_CHUNK_DELIMITER: {
                int wIdx = buffer.writerIndex();
                int rIdx = buffer.readerIndex();
                while (wIdx > rIdx) {
                    byte next;
                    if ((next = buffer.getByte(rIdx++)) != 10) continue;
                    this.currentState = State.READ_CHUNK_SIZE;
                    break;
                }
                buffer.readerIndex(rIdx);
                return;
            }
            case READ_CHUNK_FOOTER: {
                try {
                    LastHttpContent trailer = this.readTrailingHeaders(buffer);
                    if (trailer == null) {
                        return;
                    }
                    out.add(trailer);
                    this.resetNow();
                    return;
                }
                catch (Exception e) {
                    out.add(this.invalidChunk(buffer, e));
                    return;
                }
            }
            case BAD_MESSAGE: {
                buffer.skipBytes(buffer.readableBytes());
                break;
            }
            case UPGRADED: {
                int readableBytes = buffer.readableBytes();
                if (readableBytes <= 0) break;
                out.add(buffer.readBytes(readableBytes));
                break;
            }
        }
    }

    @Override
    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        super.decodeLast(ctx, in, out);
        if (this.resetRequested) {
            this.resetNow();
        }
        if (this.message != null) {
            boolean prematureClosure;
            boolean chunked = HttpUtil.isTransferEncodingChunked(this.message);
            if (this.currentState == State.READ_VARIABLE_LENGTH_CONTENT && !in.isReadable() && !chunked) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                this.resetNow();
                return;
            }
            if (this.currentState == State.READ_HEADER) {
                out.add(this.invalidMessage(Unpooled.EMPTY_BUFFER, new PrematureChannelClosureException("Connection closed before received headers")));
                this.resetNow();
                return;
            }
            if (this.isDecodingRequest() || chunked) {
                prematureClosure = true;
            } else {
                boolean bl = prematureClosure = this.contentLength() > 0L;
            }
            if (!prematureClosure) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            }
            this.resetNow();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof HttpExpectationFailedEvent) {
            switch (this.currentState) {
                case READ_CHUNK_SIZE: 
                case READ_VARIABLE_LENGTH_CONTENT: 
                case READ_FIXED_LENGTH_CONTENT: {
                    this.reset();
                    break;
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse res = (HttpResponse)msg;
            int code = res.status().code();
            if (code >= 100 && code < 200) {
                return code != 101 || res.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT) || !res.headers().contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true);
            }
            switch (code) {
                case 204: 
                case 304: {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isSwitchingToNonHttp1Protocol(HttpResponse msg) {
        if (msg.status().code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code()) {
            return false;
        }
        String newProtocol = msg.headers().get(HttpHeaderNames.UPGRADE);
        return newProtocol == null || !newProtocol.contains(HttpVersion.HTTP_1_0.text()) && !newProtocol.contains(HttpVersion.HTTP_1_1.text());
    }

    public void reset() {
        this.resetRequested = true;
    }

    private void resetNow() {
        HttpResponse res;
        HttpMessage message = this.message;
        this.message = null;
        this.name = null;
        this.value = null;
        this.contentLength = Long.MIN_VALUE;
        this.lineParser.reset();
        this.headerParser.reset();
        this.trailer = null;
        if (!this.isDecodingRequest() && (res = (HttpResponse)message) != null && this.isSwitchingToNonHttp1Protocol(res)) {
            this.currentState = State.UPGRADED;
            return;
        }
        this.resetRequested = false;
        this.currentState = State.SKIP_CONTROL_CHARS;
    }

    private HttpMessage invalidMessage(ByteBuf in, Exception cause) {
        this.currentState = State.BAD_MESSAGE;
        in.skipBytes(in.readableBytes());
        if (this.message == null) {
            this.message = this.createInvalidMessage();
        }
        this.message.setDecoderResult(DecoderResult.failure(cause));
        HttpMessage ret = this.message;
        this.message = null;
        return ret;
    }

    private HttpContent invalidChunk(ByteBuf in, Exception cause) {
        this.currentState = State.BAD_MESSAGE;
        in.skipBytes(in.readableBytes());
        DefaultLastHttpContent chunk = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
        chunk.setDecoderResult(DecoderResult.failure(cause));
        this.message = null;
        this.trailer = null;
        return chunk;
    }

    private State readHeaders(ByteBuf buffer) {
        HttpMessage message = this.message;
        HttpHeaders headers = message.headers();
        AppendableCharSequence line = this.headerParser.parse(buffer);
        if (line == null) {
            return null;
        }
        if (line.length() > 0) {
            do {
                char firstChar = line.charAtUnsafe(0);
                if (this.name != null && (firstChar == ' ' || firstChar == '\t')) {
                    String trimmedLine = line.toString().trim();
                    String valueStr = String.valueOf(this.value);
                    this.value = valueStr + ' ' + trimmedLine;
                } else {
                    if (this.name != null) {
                        headers.add(this.name, (Object)this.value);
                    }
                    this.splitHeader(line);
                }
                line = this.headerParser.parse(buffer);
                if (line != null) continue;
                return null;
            } while (line.length() > 0);
        }
        if (this.name != null) {
            headers.add(this.name, (Object)this.value);
        }
        this.name = null;
        this.value = null;
        List<String> values = headers.getAll(HttpHeaderNames.CONTENT_LENGTH);
        int contentLengthValuesCount = values.size();
        if (contentLengthValuesCount > 0) {
            if (contentLengthValuesCount > 1 && message.protocolVersion() == HttpVersion.HTTP_1_1) {
                throw new IllegalArgumentException("Multiple Content-Length headers found");
            }
            this.contentLength = Long.parseLong(values.get(0));
        }
        if (this.isContentAlwaysEmpty(message)) {
            HttpUtil.setTransferEncodingChunked(message, false);
            return State.SKIP_CONTROL_CHARS;
        }
        if (HttpUtil.isTransferEncodingChunked(message)) {
            if (contentLengthValuesCount > 0 && message.protocolVersion() == HttpVersion.HTTP_1_1) {
                this.handleTransferEncodingChunkedWithContentLength(message);
            }
            return State.READ_CHUNK_SIZE;
        }
        if (this.contentLength() >= 0L) {
            return State.READ_FIXED_LENGTH_CONTENT;
        }
        return State.READ_VARIABLE_LENGTH_CONTENT;
    }

    protected void handleTransferEncodingChunkedWithContentLength(HttpMessage message) {
        message.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
        this.contentLength = Long.MIN_VALUE;
    }

    private long contentLength() {
        if (this.contentLength == Long.MIN_VALUE) {
            this.contentLength = HttpUtil.getContentLength(this.message, -1L);
        }
        return this.contentLength;
    }

    private LastHttpContent readTrailingHeaders(ByteBuf buffer) {
        AppendableCharSequence line = this.headerParser.parse(buffer);
        if (line == null) {
            return null;
        }
        LastHttpContent trailer = this.trailer;
        if (line.length() == 0 && trailer == null) {
            return LastHttpContent.EMPTY_LAST_CONTENT;
        }
        CharSequence lastHeader = null;
        if (trailer == null) {
            trailer = this.trailer = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
        }
        while (line.length() > 0) {
            char firstChar = line.charAtUnsafe(0);
            if (lastHeader != null && (firstChar == ' ' || firstChar == '\t')) {
                List<String> current = trailer.trailingHeaders().getAll(lastHeader);
                if (!current.isEmpty()) {
                    int lastPos = current.size() - 1;
                    String lineTrimmed = line.toString().trim();
                    String currentLastPos = current.get(lastPos);
                    current.set(lastPos, currentLastPos + lineTrimmed);
                }
            } else {
                this.splitHeader(line);
                CharSequence headerName = this.name;
                if (!(HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(headerName) || HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(headerName) || HttpHeaderNames.TRAILER.contentEqualsIgnoreCase(headerName))) {
                    trailer.trailingHeaders().add(headerName, (Object)this.value);
                }
                lastHeader = this.name;
                this.name = null;
                this.value = null;
            }
            if ((line = this.headerParser.parse(buffer)) != null) continue;
            return null;
        }
        this.trailer = null;
        return trailer;
    }

    protected abstract boolean isDecodingRequest();

    protected abstract HttpMessage createMessage(String[] var1) throws Exception;

    protected abstract HttpMessage createInvalidMessage();

    private static int getChunkSize(String hex) {
        hex = hex.trim();
        for (int i = 0; i < hex.length(); ++i) {
            char c = hex.charAt(i);
            if (c != ';' && !Character.isWhitespace(c) && !Character.isISOControl(c)) continue;
            hex = hex.substring(0, i);
            break;
        }
        return Integer.parseInt(hex, 16);
    }

    private static String[] splitInitialLine(AppendableCharSequence sb) {
        int aStart = HttpObjectDecoder.findNonSPLenient(sb, 0);
        int aEnd = HttpObjectDecoder.findSPLenient(sb, aStart);
        int bStart = HttpObjectDecoder.findNonSPLenient(sb, aEnd);
        int bEnd = HttpObjectDecoder.findSPLenient(sb, bStart);
        int cStart = HttpObjectDecoder.findNonSPLenient(sb, bEnd);
        int cEnd = HttpObjectDecoder.findEndOfString(sb);
        return new String[]{sb.subStringUnsafe(aStart, aEnd), sb.subStringUnsafe(bStart, bEnd), cStart < cEnd ? sb.subStringUnsafe(cStart, cEnd) : EMPTY_VALUE};
    }

    private void splitHeader(AppendableCharSequence sb) {
        int colonEnd;
        int nameStart;
        char ch;
        int nameEnd;
        int length = sb.length();
        for (nameEnd = nameStart = HttpObjectDecoder.findNonWhitespace(sb, 0, false); nameEnd < length && (ch = sb.charAtUnsafe(nameEnd)) != ':' && (this.isDecodingRequest() || !HttpObjectDecoder.isOWS(ch)); ++nameEnd) {
        }
        if (nameEnd == length) {
            throw new IllegalArgumentException("No colon found");
        }
        for (colonEnd = nameEnd; colonEnd < length; ++colonEnd) {
            if (sb.charAtUnsafe(colonEnd) != ':') continue;
            ++colonEnd;
            break;
        }
        this.name = sb.subStringUnsafe(nameStart, nameEnd);
        int valueStart = HttpObjectDecoder.findNonWhitespace(sb, colonEnd, true);
        if (valueStart == length) {
            this.value = EMPTY_VALUE;
        } else {
            int valueEnd = HttpObjectDecoder.findEndOfString(sb);
            this.value = sb.subStringUnsafe(valueStart, valueEnd);
        }
    }

    private static int findNonSPLenient(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            char c = sb.charAtUnsafe(result);
            if (HttpObjectDecoder.isSPLenient(c)) continue;
            if (Character.isWhitespace(c)) {
                throw new IllegalArgumentException("Invalid separator");
            }
            return result;
        }
        return sb.length();
    }

    private static int findSPLenient(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (!HttpObjectDecoder.isSPLenient(sb.charAtUnsafe(result))) continue;
            return result;
        }
        return sb.length();
    }

    private static boolean isSPLenient(char c) {
        return c == ' ' || c == '\t' || c == '\u000b' || c == '\f' || c == '\r';
    }

    private static int findNonWhitespace(AppendableCharSequence sb, int offset, boolean validateOWS) {
        for (int result = offset; result < sb.length(); ++result) {
            char c = sb.charAtUnsafe(result);
            if (!Character.isWhitespace(c)) {
                return result;
            }
            if (!validateOWS || HttpObjectDecoder.isOWS(c)) continue;
            throw new IllegalArgumentException("Invalid separator, only a single space or horizontal tab allowed, but received a '" + c + "'");
        }
        return sb.length();
    }

    private static int findEndOfString(AppendableCharSequence sb) {
        for (int result = sb.length() - 1; result > 0; --result) {
            if (Character.isWhitespace(sb.charAtUnsafe(result))) continue;
            return result + 1;
        }
        return 0;
    }

    private static boolean isOWS(char ch) {
        return ch == ' ' || ch == '\t';
    }

    private final class LineParser
    extends HeaderParser {
        LineParser(AppendableCharSequence seq, int maxLength) {
            super(seq, maxLength);
        }

        @Override
        public AppendableCharSequence parse(ByteBuf buffer) {
            this.reset();
            return super.parse(buffer);
        }

        @Override
        public boolean process(byte value) throws Exception {
            if (HttpObjectDecoder.this.currentState == State.SKIP_CONTROL_CHARS) {
                char c = (char)(value & 0xFF);
                if (Character.isISOControl(c) || Character.isWhitespace(c)) {
                    this.increaseCount();
                    return true;
                }
                HttpObjectDecoder.this.currentState = State.READ_INITIAL;
            }
            return super.process(value);
        }

        @Override
        protected TooLongFrameException newException(int maxLength) {
            return new TooLongFrameException("An HTTP line is larger than " + maxLength + " bytes.");
        }
    }

    private static class HeaderParser
    implements ByteProcessor {
        private final AppendableCharSequence seq;
        private final int maxLength;
        private int size;

        HeaderParser(AppendableCharSequence seq, int maxLength) {
            this.seq = seq;
            this.maxLength = maxLength;
        }

        public AppendableCharSequence parse(ByteBuf buffer) {
            int oldSize = this.size;
            this.seq.reset();
            int i = buffer.forEachByte(this);
            if (i == -1) {
                this.size = oldSize;
                return null;
            }
            buffer.readerIndex(i + 1);
            return this.seq;
        }

        public void reset() {
            this.size = 0;
        }

        @Override
        public boolean process(byte value) throws Exception {
            char nextByte = (char)(value & 0xFF);
            if (nextByte == '\n') {
                int len = this.seq.length();
                if (len >= 1 && this.seq.charAtUnsafe(len - 1) == '\r') {
                    --this.size;
                    this.seq.setLength(len - 1);
                }
                return false;
            }
            this.increaseCount();
            this.seq.append(nextByte);
            return true;
        }

        protected final void increaseCount() {
            if (++this.size > this.maxLength) {
                throw this.newException(this.maxLength);
            }
        }

        protected TooLongFrameException newException(int maxLength) {
            return new TooLongFrameException("HTTP header is larger than " + maxLength + " bytes.");
        }
    }

    private static enum State {
        SKIP_CONTROL_CHARS,
        READ_INITIAL,
        READ_HEADER,
        READ_VARIABLE_LENGTH_CONTENT,
        READ_FIXED_LENGTH_CONTENT,
        READ_CHUNK_SIZE,
        READ_CHUNKED_CONTENT,
        READ_CHUNK_DELIMITER,
        READ_CHUNK_FOOTER,
        BAD_MESSAGE,
        UPGRADED;

    }
}

