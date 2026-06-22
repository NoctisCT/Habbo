/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.spi.ElementPath;
import java.util.List;

public class ElementSelector
extends ElementPath {
    public ElementSelector() {
    }

    public ElementSelector(List<String> list) {
        super(list);
    }

    public ElementSelector(String p) {
        super(p);
    }

    public boolean fullPathMatch(ElementPath path) {
        if (path.size() != this.size()) {
            return false;
        }
        int len = this.size();
        for (int i = 0; i < len; ++i) {
            if (this.equalityCheck(this.get(i), path.get(i))) continue;
            return false;
        }
        return true;
    }

    public int getTailMatchLength(ElementPath p) {
        String r;
        String l;
        if (p == null) {
            return 0;
        }
        int lSize = this.partList.size();
        int rSize = p.partList.size();
        if (lSize == 0 || rSize == 0) {
            return 0;
        }
        int minLen = lSize <= rSize ? lSize : rSize;
        int match = 0;
        for (int i = 1; i <= minLen && this.equalityCheck(l = (String)this.partList.get(lSize - i), r = p.partList.get(rSize - i)); ++i) {
            ++match;
        }
        return match;
    }

    public boolean isContainedIn(ElementPath p) {
        if (p == null) {
            return false;
        }
        return p.toStableString().contains(this.toStableString());
    }

    public int getPrefixMatchLength(ElementPath p) {
        String r;
        String l;
        if (p == null) {
            return 0;
        }
        int lSize = this.partList.size();
        int rSize = p.partList.size();
        if (lSize == 0 || rSize == 0) {
            return 0;
        }
        int minLen = lSize <= rSize ? lSize : rSize;
        int match = 0;
        for (int i = 0; i < minLen && this.equalityCheck(l = (String)this.partList.get(i), r = p.partList.get(i)); ++i) {
            ++match;
        }
        return match;
    }

    private boolean equalityCheck(String x, String y) {
        return x.equalsIgnoreCase(y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ElementSelector)) {
            return false;
        }
        ElementSelector r = (ElementSelector)o;
        if (r.size() != this.size()) {
            return false;
        }
        int len = this.size();
        for (int i = 0; i < len; ++i) {
            if (this.equalityCheck(this.get(i), r.get(i))) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hc = 0;
        int len = this.size();
        for (int i = 0; i < len; ++i) {
            hc ^= this.get(i).toLowerCase().hashCode();
        }
        return hc;
    }
}

