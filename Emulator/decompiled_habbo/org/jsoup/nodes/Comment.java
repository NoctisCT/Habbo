/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.LeafNode;
import org.jsoup.nodes.NodeUtils;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.Parser;

public class Comment
extends LeafNode {
    public Comment(String data) {
        this.value = data;
    }

    @Override
    public String nodeName() {
        return "#comment";
    }

    public String getData() {
        return this.coreValue();
    }

    public Comment setData(String data) {
        this.coreValue(data);
        return this;
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        if (out.prettyPrint() && (this.siblingIndex() == 0 && this.parentNode instanceof Element && ((Element)this.parentNode).tag().formatAsBlock() || out.outline())) {
            this.indent(accum, depth, out);
        }
        accum.append("<!--").append(this.getData()).append("-->");
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
    }

    @Override
    public String toString() {
        return this.outerHtml();
    }

    @Override
    public Comment clone() {
        return (Comment)super.clone();
    }

    public boolean isXmlDeclaration() {
        String data = this.getData();
        return data.length() > 1 && (data.startsWith("!") || data.startsWith("?"));
    }

    public XmlDeclaration asXmlDeclaration() {
        String data = this.getData();
        Document doc = Jsoup.parse("<" + data.substring(1, data.length() - 1) + ">", this.baseUri(), Parser.xmlParser());
        XmlDeclaration decl = null;
        if (doc.children().size() > 0) {
            Element el = doc.child(0);
            decl = new XmlDeclaration(NodeUtils.parser(doc).settings().normalizeTag(el.tagName()), data.startsWith("!"));
            decl.attributes().addAll(el.attributes());
        }
        return decl;
    }
}

