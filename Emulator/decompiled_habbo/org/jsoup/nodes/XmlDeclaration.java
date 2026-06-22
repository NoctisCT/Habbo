/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.LeafNode;

public class XmlDeclaration
extends LeafNode {
    private final boolean isProcessingInstruction;

    public XmlDeclaration(String name, boolean isProcessingInstruction) {
        Validate.notNull(name);
        this.value = name;
        this.isProcessingInstruction = isProcessingInstruction;
    }

    @Override
    public String nodeName() {
        return "#declaration";
    }

    public String name() {
        return this.coreValue();
    }

    public String getWholeDeclaration() {
        StringBuilder sb = StringUtil.borrowBuilder();
        try {
            this.getWholeDeclaration(sb, new Document.OutputSettings());
        }
        catch (IOException e) {
            throw new SerializationException(e);
        }
        return StringUtil.releaseBuilder(sb).trim();
    }

    private void getWholeDeclaration(Appendable accum, Document.OutputSettings out) throws IOException {
        for (Attribute attribute : this.attributes()) {
            if (attribute.getKey().equals(this.nodeName())) continue;
            accum.append(' ');
            attribute.html(accum, out);
        }
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        accum.append("<").append(this.isProcessingInstruction ? "!" : "?").append(this.coreValue());
        this.getWholeDeclaration(accum, out);
        accum.append(this.isProcessingInstruction ? "!" : "?").append(">");
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
    }

    @Override
    public String toString() {
        return this.outerHtml();
    }

    @Override
    public XmlDeclaration clone() {
        return (XmlDeclaration)super.clone();
    }
}

