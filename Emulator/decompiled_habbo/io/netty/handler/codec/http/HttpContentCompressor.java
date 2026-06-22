/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentEncoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;

public class HttpContentCompressor
extends HttpContentEncoder {
    private final int compressionLevel;
    private final int windowBits;
    private final int memLevel;
    private final int contentSizeThreshold;
    private ChannelHandlerContext ctx;

    public HttpContentCompressor() {
        this(6);
    }

    public HttpContentCompressor(int compressionLevel) {
        this(compressionLevel, 15, 8, 0);
    }

    public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel) {
        this(compressionLevel, windowBits, memLevel, 0);
    }

    public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel, int contentSizeThreshold) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        }
        if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        }
        if (contentSizeThreshold < 0) {
            throw new IllegalArgumentException("contentSizeThreshold: " + contentSizeThreshold + " (expected: non negative number)");
        }
        this.compressionLevel = compressionLevel;
        this.windowBits = windowBits;
        this.memLevel = memLevel;
        this.contentSizeThreshold = contentSizeThreshold;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    protected HttpContentEncoder.Result beginEncode(HttpResponse httpResponse, String acceptEncoding) throws Exception {
        String targetContentEncoding;
        if (this.contentSizeThreshold > 0 && httpResponse instanceof HttpContent && ((HttpContent)((Object)httpResponse)).content().readableBytes() < this.contentSizeThreshold) {
            return null;
        }
        String contentEncoding = httpResponse.headers().get(HttpHeaderNames.CONTENT_ENCODING);
        if (contentEncoding != null) {
            return null;
        }
        ZlibWrapper wrapper = this.determineWrapper(acceptEncoding);
        if (wrapper == null) {
            return null;
        }
        switch (wrapper) {
            case GZIP: {
                targetContentEncoding = "gzip";
                break;
            }
            case ZLIB: {
                targetContentEncoding = "deflate";
                break;
            }
            default: {
                throw new Error();
            }
        }
        return new HttpContentEncoder.Result(targetContentEncoding, new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), ZlibCodecFactory.newZlibEncoder(wrapper, this.compressionLevel, this.windowBits, this.memLevel)));
    }

    protected ZlibWrapper determineWrapper(String acceptEncoding) {
        float starQ = -1.0f;
        float gzipQ = -1.0f;
        float deflateQ = -1.0f;
        for (String encoding : acceptEncoding.split(",")) {
            float q = 1.0f;
            int equalsPos = encoding.indexOf(61);
            if (equalsPos != -1) {
                try {
                    q = Float.parseFloat(encoding.substring(equalsPos + 1));
                }
                catch (NumberFormatException e) {
                    q = 0.0f;
                }
            }
            if (encoding.contains("*")) {
                starQ = q;
                continue;
            }
            if (encoding.contains("gzip") && q > gzipQ) {
                gzipQ = q;
                continue;
            }
            if (!encoding.contains("deflate") || !(q > deflateQ)) continue;
            deflateQ = q;
        }
        if (gzipQ > 0.0f || deflateQ > 0.0f) {
            if (gzipQ >= deflateQ) {
                return ZlibWrapper.GZIP;
            }
            return ZlibWrapper.ZLIB;
        }
        if (starQ > 0.0f) {
            if (gzipQ == -1.0f) {
                return ZlibWrapper.GZIP;
            }
            if (deflateQ == -1.0f) {
                return ZlibWrapper.ZLIB;
            }
        }
        return null;
    }
}

