/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.parser.FormattingNode;
import java.util.List;

public class SimpleKeywordNode
extends FormattingNode {
    List<String> optionList;

    SimpleKeywordNode(Object value) {
        super(1, value);
    }

    protected SimpleKeywordNode(int type, Object value) {
        super(type, value);
    }

    public List<String> getOptions() {
        return this.optionList;
    }

    public void setOptions(List<String> optionList) {
        this.optionList = optionList;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof SimpleKeywordNode)) {
            return false;
        }
        SimpleKeywordNode r = (SimpleKeywordNode)o;
        return this.optionList != null ? this.optionList.equals(r.optionList) : r.optionList == null;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.optionList == null) {
            buf.append("KeyWord(" + this.value + "," + this.formatInfo + ")");
        } else {
            buf.append("KeyWord(" + this.value + ", " + this.formatInfo + "," + this.optionList + ")");
        }
        buf.append(this.printNext());
        return buf.toString();
    }
}

