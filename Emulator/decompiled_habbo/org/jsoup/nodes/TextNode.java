/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.LeafNode;

public class TextNode
extends LeafNode {
    public TextNode(String text) {
        this.value = text;
    }

    @Override
    public String nodeName() {
        return "#text";
    }

    public String text() {
        return StringUtil.normaliseWhitespace(this.getWholeText());
    }

    public TextNode text(String text) {
        this.coreValue(text);
        return this;
    }

    public String getWholeText() {
        return this.coreValue();
    }

    public boolean isBlank() {
        return StringUtil.isBlank(this.coreValue());
    }

    public TextNode splitText(int offset) {
        String text = this.coreValue();
        Validate.isTrue(offset >= 0, "Split offset must be not be negative");
        Validate.isTrue(offset < text.length(), "Split offset must not be greater than current text length");
        String head = text.substring(0, offset);
        String tail = text.substring(offset);
        this.text(head);
        TextNode tailNode = new TextNode(tail);
        if (this.parent() != null) {
            this.parent().addChildren(this.siblingIndex() + 1, tailNode);
        }
        return tailNode;
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        boolean prettyPrint = out.prettyPrint();
        if (prettyPrint && (this.siblingIndex() == 0 && this.parentNode instanceof Element && ((Element)this.parentNode).tag().formatAsBlock() && !this.isBlank() || out.outline() && this.siblingNodes().size() > 0 && !this.isBlank())) {
            this.indent(accum, depth, out);
        }
        boolean normaliseWhite = prettyPrint && !Element.preserveWhitespace(this.parentNode);
        boolean stripWhite = prettyPrint && this.parentNode instanceof Document;
        Entities.escape(accum, this.coreValue(), out, false, normaliseWhite, stripWhite);
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
    }

    @Override
    public String toString() {
        return this.outerHtml();
    }

    @Override
    public TextNode clone() {
        return (TextNode)super.clone();
    }

    public static TextNode createFromEncoded(String encodedText) {
        String text = Entities.unescape(encodedText);
        return new TextNode(text);
    }

    static String normaliseWhitespace(String text) {
        text = StringUtil.normaliseWhitespace(text);
        return text;
    }

    static String stripLeadingWhitespace(String text) {
        return text.replaceFirst("^\\s+", "");
    }

    static boolean lastCharIsWhitespace(StringBuilder sb) {
        return sb.length() != 0 && sb.charAt(sb.length() - 1) == ' ';
    }
}

