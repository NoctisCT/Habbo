/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.FormatInfo;
import ch.qos.logback.core.pattern.parser.Node;

public class FormattingNode
extends Node {
    FormatInfo formatInfo;

    FormattingNode(int type) {
        super(type);
    }

    FormattingNode(int type, Object value) {
        super(type, value);
    }

    public FormatInfo getFormatInfo() {
        return this.formatInfo;
    }

    public void setFormatInfo(FormatInfo formatInfo) {
        this.formatInfo = formatInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof FormattingNode)) {
            return false;
        }
        FormattingNode r = (FormattingNode)o;
        return this.formatInfo != null ? this.formatInfo.equals(r.formatInfo) : r.formatInfo == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.formatInfo != null ? this.formatInfo.hashCode() : 0);
        return result;
    }
}

