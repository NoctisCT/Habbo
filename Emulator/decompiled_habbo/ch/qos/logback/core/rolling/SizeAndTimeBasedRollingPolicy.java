/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;

public class SizeAndTimeBasedRollingPolicy<E>
extends TimeBasedRollingPolicy<E> {
    FileSize maxFileSize;

    @Override
    public void start() {
        SizeAndTimeBasedFNATP sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP(SizeAndTimeBasedFNATP.Usage.EMBEDDED);
        if (this.maxFileSize == null) {
            this.addError("maxFileSize property is mandatory.");
            return;
        }
        this.addInfo("Archive files will be limited to [" + this.maxFileSize + "] each.");
        sizeAndTimeBasedFNATP.setMaxFileSize(this.maxFileSize);
        this.timeBasedFileNamingAndTriggeringPolicy = sizeAndTimeBasedFNATP;
        if (!this.isUnboundedTotalSizeCap() && this.totalSizeCap.getSize() < this.maxFileSize.getSize()) {
            this.addError("totalSizeCap of [" + this.totalSizeCap + "] is smaller than maxFileSize [" + this.maxFileSize + "] which is non-sensical");
            return;
        }
        super.start();
    }

    public void setMaxFileSize(FileSize aMaxFileSize) {
        this.maxFileSize = aMaxFileSize;
    }

    @Override
    public String toString() {
        return "c.q.l.core.rolling.SizeAndTimeBasedRollingPolicy@" + this.hashCode();
    }
}

