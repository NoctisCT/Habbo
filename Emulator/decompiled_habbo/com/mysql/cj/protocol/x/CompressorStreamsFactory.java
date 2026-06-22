/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.protocol.x;

import com.mysql.cj.Messages;
import com.mysql.cj.protocol.x.CompressionAlgorithm;
import com.mysql.cj.protocol.x.CompressionMode;
import com.mysql.cj.protocol.x.ContinuousOutputStream;
import com.mysql.cj.protocol.x.ReusableInputStream;
import com.mysql.cj.protocol.x.ReusableOutputStream;
import com.mysql.cj.util.Util;
import java.io.InputStream;
import java.io.OutputStream;

public class CompressorStreamsFactory {
    private CompressionAlgorithm compressionAlgorithm;
    private InputStream compressorInputStreamInstance = null;
    private ReusableInputStream underlyingInputStream = null;
    private OutputStream compressorOutputStreamInstance = null;
    private ReusableOutputStream underlyingOutputStream = null;

    public CompressorStreamsFactory(CompressionAlgorithm algorithm) {
        this.compressionAlgorithm = algorithm;
    }

    public CompressionMode getCompressionMode() {
        return this.compressionAlgorithm.getCompressionMode();
    }

    public boolean areCompressedStreamsReusable() {
        return this.getCompressionMode() == CompressionMode.STREAM;
    }

    public InputStream getInputStreamInstance(InputStream in) {
        InputStream underlyingIn = in;
        if (this.areCompressedStreamsReusable()) {
            if (this.compressorInputStreamInstance != null) {
                this.underlyingInputStream.setInputStream(underlyingIn);
                return this.compressorInputStreamInstance;
            }
            this.underlyingInputStream = new ReusableInputStream(underlyingIn);
            underlyingIn = this.underlyingInputStream;
        }
        InputStream compressionIn = (InputStream)Util.getInstance(this.compressionAlgorithm.getInputStreamClass().getName(), new Class[]{InputStream.class}, new Object[]{underlyingIn}, null, Messages.getString("Protocol.Compression.IoFactory.0", new Object[]{this.compressionAlgorithm.getInputStreamClass().getName(), this.compressionAlgorithm}));
        if (this.areCompressedStreamsReusable()) {
            this.compressorInputStreamInstance = compressionIn;
        }
        return compressionIn;
    }

    public OutputStream getOutputStreamInstance(OutputStream out) {
        OutputStream underlyingOut = out;
        if (this.areCompressedStreamsReusable()) {
            if (this.compressorOutputStreamInstance != null) {
                this.underlyingOutputStream.setOutputStream(underlyingOut);
                return this.compressorOutputStreamInstance;
            }
            this.underlyingOutputStream = new ReusableOutputStream(underlyingOut);
            underlyingOut = this.underlyingOutputStream;
        }
        OutputStream compressionOut = (OutputStream)Util.getInstance(this.compressionAlgorithm.getOutputStreamClass().getName(), new Class[]{OutputStream.class}, new Object[]{underlyingOut}, null, Messages.getString("Protocol.Compression.IoFactory.1", new Object[]{this.compressionAlgorithm.getOutputStreamClass().getName(), this.compressionAlgorithm}));
        if (this.areCompressedStreamsReusable()) {
            this.compressorOutputStreamInstance = compressionOut = new ContinuousOutputStream(compressionOut);
        }
        return compressionOut;
    }
}

