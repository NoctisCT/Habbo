/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

public class Attribute
implements Map.Entry<String, String>,
Cloneable {
    private static final String[] booleanAttributes = new String[]{"allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled", "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize", "noshade", "novalidate", "nowrap", "open", "readonly", "required", "reversed", "seamless", "selected", "sortable", "truespeed", "typemustmatch"};
    private String key;
    private String val;
    Attributes parent;

    public Attribute(String key, String value) {
        this(key, value, null);
    }

    public Attribute(String key, String val, Attributes parent) {
        Validate.notNull(key);
        key = key.trim();
        Validate.notEmpty(key);
        this.key = key;
        this.val = val;
        this.parent = parent;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        int i;
        Validate.notNull(key);
        key = key.trim();
        Validate.notEmpty(key);
        if (this.parent != null && (i = this.parent.indexOfKey(this.key)) != -1) {
            this.parent.keys[i] = key;
        }
        this.key = key;
    }

    @Override
    public String getValue() {
        return Attributes.checkNotNull(this.val);
    }

    public boolean hasDeclaredValue() {
        return this.val != null;
    }

    @Override
    public String setValue(String val) {
        String oldVal = this.val;
        if (this.parent != null) {
            oldVal = this.parent.get(this.key);
            int i = this.parent.indexOfKey(this.key);
            if (i != -1) {
                this.parent.vals[i] = val;
            }
        }
        this.val = val;
        return Attributes.checkNotNull(oldVal);
    }

    public String html() {
        StringBuilder sb = StringUtil.borrowBuilder();
        try {
            this.html(sb, new Document("").outputSettings());
        }
        catch (IOException exception) {
            throw new SerializationException(exception);
        }
        return StringUtil.releaseBuilder(sb);
    }

    protected static void html(String key, String val, Appendable accum, Document.OutputSettings out) throws IOException {
        accum.append(key);
        if (!Attribute.shouldCollapseAttribute(key, val, out)) {
            accum.append("=\"");
            Entities.escape(accum, Attributes.checkNotNull(val), out, true, false, false);
            accum.append('\"');
        }
    }

    protected void html(Appendable accum, Document.OutputSettings out) throws IOException {
        Attribute.html(this.key, this.val, accum, out);
    }

    public String toString() {
        return this.html();
    }

    public static Attribute createFromEncoded(String unencodedKey, String encodedValue) {
        String value = Entities.unescape(encodedValue, true);
        return new Attribute(unencodedKey, value, null);
    }

    protected boolean isDataAttribute() {
        return Attribute.isDataAttribute(this.key);
    }

    protected static boolean isDataAttribute(String key) {
        return key.startsWith("data-") && key.length() > "data-".length();
    }

    protected final boolean shouldCollapseAttribute(Document.OutputSettings out) {
        return Attribute.shouldCollapseAttribute(this.key, this.val, out);
    }

    protected static boolean shouldCollapseAttribute(String key, String val, Document.OutputSettings out) {
        return out.syntax() == Document.OutputSettings.Syntax.html && (val == null || ("".equals(val) || val.equalsIgnoreCase(key)) && Attribute.isBooleanAttribute(key));
    }

    protected static boolean isBooleanAttribute(String key) {
        return Arrays.binarySearch(booleanAttributes, key) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Attribute attribute = (Attribute)o;
        if (this.key != null ? !this.key.equals(attribute.key) : attribute.key != null) {
            return false;
        }
        return this.val != null ? this.val.equals(attribute.val) : attribute.val == null;
    }

    @Override
    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.val != null ? this.val.hashCode() : 0);
        return result;
    }

    public Attribute clone() {
        try {
            return (Attribute)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

