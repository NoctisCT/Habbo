/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class LayoutBase<E>
extends ContextAwareBase
implements Layout<E> {
    protected boolean started;
    String fileHeader;
    String fileFooter;
    String presentationHeader;
    String presentationFooter;

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }

    @Override
    public String getFileHeader() {
        return this.fileHeader;
    }

    @Override
    public String getPresentationHeader() {
        return this.presentationHeader;
    }

    @Override
    public String getPresentationFooter() {
        return this.presentationFooter;
    }

    @Override
    public String getFileFooter() {
        return this.fileFooter;
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    public void setFileHeader(String header) {
        this.fileHeader = header;
    }

    public void setFileFooter(String footer) {
        this.fileFooter = footer;
    }

    public void setPresentationHeader(String header) {
        this.presentationHeader = header;
    }

    public void setPresentationFooter(String footer) {
        this.presentationFooter = footer;
    }
}

