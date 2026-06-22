/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Attributes;

public class ParseSettings {
    public static final ParseSettings htmlDefault = new ParseSettings(false, false);
    public static final ParseSettings preserveCase = new ParseSettings(true, true);
    private final boolean preserveTagCase;
    private final boolean preserveAttributeCase;

    public boolean preserveTagCase() {
        return this.preserveTagCase;
    }

    public boolean preserveAttributeCase() {
        return this.preserveAttributeCase;
    }

    public ParseSettings(boolean tag, boolean attribute) {
        this.preserveTagCase = tag;
        this.preserveAttributeCase = attribute;
    }

    public String normalizeTag(String name) {
        name = name.trim();
        if (!this.preserveTagCase) {
            name = Normalizer.lowerCase(name);
        }
        return name;
    }

    public String normalizeAttribute(String name) {
        name = name.trim();
        if (!this.preserveAttributeCase) {
            name = Normalizer.lowerCase(name);
        }
        return name;
    }

    Attributes normalizeAttributes(Attributes attributes) {
        if (attributes != null && !this.preserveAttributeCase) {
            attributes.normalize();
        }
        return attributes;
    }
}

