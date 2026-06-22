/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.encoder;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.encoder.EncoderBase;

public class EchoEncoder<E>
extends EncoderBase<E> {
    String fileHeader;
    String fileFooter;

    @Override
    public byte[] encode(E event) {
        String val = event + CoreConstants.LINE_SEPARATOR;
        return val.getBytes();
    }

    @Override
    public byte[] footerBytes() {
        if (this.fileFooter == null) {
            return null;
        }
        return this.fileFooter.getBytes();
    }

    @Override
    public byte[] headerBytes() {
        if (this.fileHeader == null) {
            return null;
        }
        return this.fileHeader.getBytes();
    }
}

