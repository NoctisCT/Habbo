/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.LeafNode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class Document
extends Element {
    private OutputSettings outputSettings = new OutputSettings();
    private Parser parser;
    private QuirksMode quirksMode = QuirksMode.noQuirks;
    private String location;
    private boolean updateMetaCharset = false;

    public Document(String baseUri) {
        super(Tag.valueOf("#root", ParseSettings.htmlDefault), baseUri);
        this.location = baseUri;
    }

    public static Document createShell(String baseUri) {
        Validate.notNull(baseUri);
        Document doc = new Document(baseUri);
        doc.parser = doc.parser();
        Element html = doc.appendElement("html");
        html.appendElement("head");
        html.appendElement("body");
        return doc;
    }

    public String location() {
        return this.location;
    }

    public DocumentType documentType() {
        for (Node node : this.childNodes) {
            if (node instanceof DocumentType) {
                return (DocumentType)node;
            }
            if (node instanceof LeafNode) continue;
            break;
        }
        return null;
    }

    public Element head() {
        return this.findFirstElementByTagName("head", this);
    }

    public Element body() {
        return this.findFirstElementByTagName("body", this);
    }

    public String title() {
        Element titleEl = this.getElementsByTag("title").first();
        return titleEl != null ? StringUtil.normaliseWhitespace(titleEl.text()).trim() : "";
    }

    public void title(String title) {
        Validate.notNull(title);
        Element titleEl = this.getElementsByTag("title").first();
        if (titleEl == null) {
            this.head().appendElement("title").text(title);
        } else {
            titleEl.text(title);
        }
    }

    public Element createElement(String tagName) {
        return new Element(Tag.valueOf(tagName, ParseSettings.preserveCase), this.baseUri());
    }

    public Document normalise() {
        Element htmlEl = this.findFirstElementByTagName("html", this);
        if (htmlEl == null) {
            htmlEl = this.appendElement("html");
        }
        if (this.head() == null) {
            htmlEl.prependElement("head");
        }
        if (this.body() == null) {
            htmlEl.appendElement("body");
        }
        this.normaliseTextNodes(this.head());
        this.normaliseTextNodes(htmlEl);
        this.normaliseTextNodes(this);
        this.normaliseStructure("head", htmlEl);
        this.normaliseStructure("body", htmlEl);
        this.ensureMetaCharsetElement();
        return this;
    }

    private void normaliseTextNodes(Element element) {
        ArrayList<TextNode> toMove = new ArrayList<TextNode>();
        for (Node node : element.childNodes) {
            TextNode tn;
            if (!(node instanceof TextNode) || (tn = (TextNode)node).isBlank()) continue;
            toMove.add(tn);
        }
        for (int i = toMove.size() - 1; i >= 0; --i) {
            Node node;
            node = (Node)toMove.get(i);
            element.removeChild(node);
            this.body().prependChild(new TextNode(" "));
            this.body().prependChild(node);
        }
    }

    private void normaliseStructure(String tag, Element htmlEl) {
        Elements elements = this.getElementsByTag(tag);
        Element master = elements.first();
        if (elements.size() > 1) {
            ArrayList<Node> toMove = new ArrayList<Node>();
            for (int i = 1; i < elements.size(); ++i) {
                Node dupe = (Node)elements.get(i);
                toMove.addAll(dupe.ensureChildNodes());
                dupe.remove();
            }
            for (Node dupe : toMove) {
                master.appendChild(dupe);
            }
        }
        if (!master.parent().equals(htmlEl)) {
            htmlEl.appendChild(master);
        }
    }

    private Element findFirstElementByTagName(String tag, Node node) {
        if (node.nodeName().equals(tag)) {
            return (Element)node;
        }
        int size = node.childNodeSize();
        for (int i = 0; i < size; ++i) {
            Element found = this.findFirstElementByTagName(tag, node.childNode(i));
            if (found == null) continue;
            return found;
        }
        return null;
    }

    @Override
    public String outerHtml() {
        return super.html();
    }

    @Override
    public Element text(String text) {
        this.body().text(text);
        return this;
    }

    @Override
    public String nodeName() {
        return "#document";
    }

    public void charset(Charset charset) {
        this.updateMetaCharsetElement(true);
        this.outputSettings.charset(charset);
        this.ensureMetaCharsetElement();
    }

    public Charset charset() {
        return this.outputSettings.charset();
    }

    public void updateMetaCharsetElement(boolean update) {
        this.updateMetaCharset = update;
    }

    public boolean updateMetaCharsetElement() {
        return this.updateMetaCharset;
    }

    @Override
    public Document clone() {
        Document clone = (Document)super.clone();
        clone.outputSettings = this.outputSettings.clone();
        return clone;
    }

    private void ensureMetaCharsetElement() {
        if (this.updateMetaCharset) {
            OutputSettings.Syntax syntax = this.outputSettings().syntax();
            if (syntax == OutputSettings.Syntax.html) {
                Element metaCharset = this.select("meta[charset]").first();
                if (metaCharset != null) {
                    metaCharset.attr("charset", this.charset().displayName());
                } else {
                    Element head = this.head();
                    if (head != null) {
                        head.appendElement("meta").attr("charset", this.charset().displayName());
                    }
                }
                this.select("meta[name=charset]").remove();
            } else if (syntax == OutputSettings.Syntax.xml) {
                Node node = this.childNodes().get(0);
                if (node instanceof XmlDeclaration) {
                    XmlDeclaration decl = (XmlDeclaration)node;
                    if (decl.name().equals("xml")) {
                        decl.attr("encoding", this.charset().displayName());
                        String version = decl.attr("version");
                        if (version != null) {
                            decl.attr("version", "1.0");
                        }
                    } else {
                        decl = new XmlDeclaration("xml", false);
                        decl.attr("version", "1.0");
                        decl.attr("encoding", this.charset().displayName());
                        this.prependChild(decl);
                    }
                } else {
                    XmlDeclaration decl = new XmlDeclaration("xml", false);
                    decl.attr("version", "1.0");
                    decl.attr("encoding", this.charset().displayName());
                    this.prependChild(decl);
                }
            }
        }
    }

    public OutputSettings outputSettings() {
        return this.outputSettings;
    }

    public Document outputSettings(OutputSettings outputSettings) {
        Validate.notNull(outputSettings);
        this.outputSettings = outputSettings;
        return this;
    }

    public QuirksMode quirksMode() {
        return this.quirksMode;
    }

    public Document quirksMode(QuirksMode quirksMode) {
        this.quirksMode = quirksMode;
        return this;
    }

    public Parser parser() {
        return this.parser;
    }

    public Document parser(Parser parser) {
        this.parser = parser;
        return this;
    }

    public static class OutputSettings
    implements Cloneable {
        private Entities.EscapeMode escapeMode = Entities.EscapeMode.base;
        private Charset charset;
        private ThreadLocal<CharsetEncoder> encoderThreadLocal = new ThreadLocal();
        Entities.CoreCharset coreCharset;
        private boolean prettyPrint = true;
        private boolean outline = false;
        private int indentAmount = 1;
        private Syntax syntax = Syntax.html;

        public OutputSettings() {
            this.charset(Charset.forName("UTF8"));
        }

        public Entities.EscapeMode escapeMode() {
            return this.escapeMode;
        }

        public OutputSettings escapeMode(Entities.EscapeMode escapeMode) {
            this.escapeMode = escapeMode;
            return this;
        }

        public Charset charset() {
            return this.charset;
        }

        public OutputSettings charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public OutputSettings charset(String charset) {
            this.charset(Charset.forName(charset));
            return this;
        }

        CharsetEncoder prepareEncoder() {
            CharsetEncoder encoder = this.charset.newEncoder();
            this.encoderThreadLocal.set(encoder);
            this.coreCharset = Entities.CoreCharset.byName(encoder.charset().name());
            return encoder;
        }

        CharsetEncoder encoder() {
            CharsetEncoder encoder = this.encoderThreadLocal.get();
            return encoder != null ? encoder : this.prepareEncoder();
        }

        public Syntax syntax() {
            return this.syntax;
        }

        public OutputSettings syntax(Syntax syntax) {
            this.syntax = syntax;
            return this;
        }

        public boolean prettyPrint() {
            return this.prettyPrint;
        }

        public OutputSettings prettyPrint(boolean pretty) {
            this.prettyPrint = pretty;
            return this;
        }

        public boolean outline() {
            return this.outline;
        }

        public OutputSettings outline(boolean outlineMode) {
            this.outline = outlineMode;
            return this;
        }

        public int indentAmount() {
            return this.indentAmount;
        }

        public OutputSettings indentAmount(int indentAmount) {
            Validate.isTrue(indentAmount >= 0);
            this.indentAmount = indentAmount;
            return this;
        }

        public OutputSettings clone() {
            OutputSettings clone;
            try {
                clone = (OutputSettings)super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            clone.charset(this.charset.name());
            clone.escapeMode = Entities.EscapeMode.valueOf(this.escapeMode.name());
            return clone;
        }

        public static enum Syntax {
            html,
            xml;

        }
    }

    public static enum QuirksMode {
        noQuirks,
        quirks,
        limitedQuirks;

    }
}

