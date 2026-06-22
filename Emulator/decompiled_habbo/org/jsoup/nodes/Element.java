/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jsoup.helper.ChangeNotifyingArrayList;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.NodeUtils;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Collector;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.NodeFilter;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.jsoup.select.QueryParser;
import org.jsoup.select.Selector;

public class Element
extends Node {
    private static final List<Node> EMPTY_NODES = Collections.emptyList();
    private static final Pattern classSplit = Pattern.compile("\\s+");
    private static final String baseUriKey = Attributes.internalKey("baseUri");
    private Tag tag;
    private WeakReference<List<Element>> shadowChildrenRef;
    List<Node> childNodes;
    private Attributes attributes;

    public Element(String tag) {
        this(Tag.valueOf(tag), "", null);
    }

    public Element(Tag tag, String baseUri, Attributes attributes) {
        Validate.notNull(tag);
        this.childNodes = EMPTY_NODES;
        this.attributes = attributes;
        this.tag = tag;
        if (baseUri != null) {
            this.setBaseUri(baseUri);
        }
    }

    public Element(Tag tag, String baseUri) {
        this(tag, baseUri, null);
    }

    @Override
    protected List<Node> ensureChildNodes() {
        if (this.childNodes == EMPTY_NODES) {
            this.childNodes = new NodeList(this, 4);
        }
        return this.childNodes;
    }

    @Override
    protected boolean hasAttributes() {
        return this.attributes != null;
    }

    @Override
    public Attributes attributes() {
        if (!this.hasAttributes()) {
            this.attributes = new Attributes();
        }
        return this.attributes;
    }

    @Override
    public String baseUri() {
        return Element.searchUpForAttribute(this, baseUriKey);
    }

    private static String searchUpForAttribute(Element start, String key) {
        for (Element el = start; el != null; el = el.parent()) {
            if (!el.hasAttributes() || !el.attributes.hasKey(key)) continue;
            return el.attributes.get(key);
        }
        return "";
    }

    @Override
    protected void doSetBaseUri(String baseUri) {
        this.attributes().put(baseUriKey, baseUri);
    }

    @Override
    public int childNodeSize() {
        return this.childNodes.size();
    }

    @Override
    public String nodeName() {
        return this.tag.getName();
    }

    public String tagName() {
        return this.tag.getName();
    }

    public String normalName() {
        return this.tag.normalName();
    }

    public Element tagName(String tagName) {
        Validate.notEmpty(tagName, "Tag name must not be empty.");
        this.tag = Tag.valueOf(tagName, NodeUtils.parser(this).settings());
        return this;
    }

    public Tag tag() {
        return this.tag;
    }

    public boolean isBlock() {
        return this.tag.isBlock();
    }

    public String id() {
        return this.hasAttributes() ? this.attributes.getIgnoreCase("id") : "";
    }

    @Override
    public Element attr(String attributeKey, String attributeValue) {
        super.attr(attributeKey, attributeValue);
        return this;
    }

    public Element attr(String attributeKey, boolean attributeValue) {
        this.attributes().put(attributeKey, attributeValue);
        return this;
    }

    public Map<String, String> dataset() {
        return this.attributes().dataset();
    }

    @Override
    public final Element parent() {
        return (Element)this.parentNode;
    }

    public Elements parents() {
        Elements parents = new Elements();
        Element.accumulateParents(this, parents);
        return parents;
    }

    private static void accumulateParents(Element el, Elements parents) {
        Element parent = el.parent();
        if (parent != null && !parent.tagName().equals("#root")) {
            parents.add(parent);
            Element.accumulateParents(parent, parents);
        }
    }

    public Element child(int index) {
        return this.childElementsList().get(index);
    }

    public int childrenSize() {
        return this.childElementsList().size();
    }

    public Elements children() {
        return new Elements(this.childElementsList());
    }

    private List<Element> childElementsList() {
        ArrayList<Element> children;
        if (this.shadowChildrenRef == null || (children = (ArrayList<Element>)this.shadowChildrenRef.get()) == null) {
            int size = this.childNodes.size();
            children = new ArrayList<Element>(size);
            for (int i = 0; i < size; ++i) {
                Node node = this.childNodes.get(i);
                if (!(node instanceof Element)) continue;
                children.add((Element)node);
            }
            this.shadowChildrenRef = new WeakReference(children);
        }
        return children;
    }

    @Override
    void nodelistChanged() {
        super.nodelistChanged();
        this.shadowChildrenRef = null;
    }

    public List<TextNode> textNodes() {
        ArrayList<TextNode> textNodes = new ArrayList<TextNode>();
        for (Node node : this.childNodes) {
            if (!(node instanceof TextNode)) continue;
            textNodes.add((TextNode)node);
        }
        return Collections.unmodifiableList(textNodes);
    }

    public List<DataNode> dataNodes() {
        ArrayList<DataNode> dataNodes = new ArrayList<DataNode>();
        for (Node node : this.childNodes) {
            if (!(node instanceof DataNode)) continue;
            dataNodes.add((DataNode)node);
        }
        return Collections.unmodifiableList(dataNodes);
    }

    public Elements select(String cssQuery) {
        return Selector.select(cssQuery, this);
    }

    public Elements select(Evaluator evaluator) {
        return Selector.select(evaluator, this);
    }

    public Element selectFirst(String cssQuery) {
        return Selector.selectFirst(cssQuery, this);
    }

    public Element selectFirst(Evaluator evaluator) {
        return Collector.findFirst(evaluator, this);
    }

    public boolean is(String cssQuery) {
        return this.is(QueryParser.parse(cssQuery));
    }

    public boolean is(Evaluator evaluator) {
        return evaluator.matches(this.root(), this);
    }

    public Element closest(String cssQuery) {
        return this.closest(QueryParser.parse(cssQuery));
    }

    public Element closest(Evaluator evaluator) {
        Validate.notNull(evaluator);
        Element el = this;
        Element root = this.root();
        do {
            if (!evaluator.matches(root, el)) continue;
            return el;
        } while ((el = el.parent()) != null);
        return null;
    }

    public Element appendChild(Node child) {
        Validate.notNull(child);
        this.reparentChild(child);
        this.ensureChildNodes();
        this.childNodes.add(child);
        child.setSiblingIndex(this.childNodes.size() - 1);
        return this;
    }

    public Element appendTo(Element parent) {
        Validate.notNull(parent);
        parent.appendChild(this);
        return this;
    }

    public Element prependChild(Node child) {
        Validate.notNull(child);
        this.addChildren(0, child);
        return this;
    }

    public Element insertChildren(int index, Collection<? extends Node> children) {
        Validate.notNull(children, "Children collection to be inserted must not be null.");
        int currentSize = this.childNodeSize();
        if (index < 0) {
            index += currentSize + 1;
        }
        Validate.isTrue(index >= 0 && index <= currentSize, "Insert position out of bounds.");
        ArrayList<? extends Node> nodes = new ArrayList<Node>(children);
        Node[] nodeArray = nodes.toArray(new Node[0]);
        this.addChildren(index, nodeArray);
        return this;
    }

    public Element insertChildren(int index, Node ... children) {
        Validate.notNull(children, "Children collection to be inserted must not be null.");
        int currentSize = this.childNodeSize();
        if (index < 0) {
            index += currentSize + 1;
        }
        Validate.isTrue(index >= 0 && index <= currentSize, "Insert position out of bounds.");
        this.addChildren(index, children);
        return this;
    }

    public Element appendElement(String tagName) {
        Element child = new Element(Tag.valueOf(tagName, NodeUtils.parser(this).settings()), this.baseUri());
        this.appendChild(child);
        return child;
    }

    public Element prependElement(String tagName) {
        Element child = new Element(Tag.valueOf(tagName, NodeUtils.parser(this).settings()), this.baseUri());
        this.prependChild(child);
        return child;
    }

    public Element appendText(String text) {
        Validate.notNull(text);
        TextNode node = new TextNode(text);
        this.appendChild(node);
        return this;
    }

    public Element prependText(String text) {
        Validate.notNull(text);
        TextNode node = new TextNode(text);
        this.prependChild(node);
        return this;
    }

    public Element append(String html) {
        Validate.notNull(html);
        List<Node> nodes = NodeUtils.parser(this).parseFragmentInput(html, this, this.baseUri());
        this.addChildren(nodes.toArray(new Node[0]));
        return this;
    }

    public Element prepend(String html) {
        Validate.notNull(html);
        List<Node> nodes = NodeUtils.parser(this).parseFragmentInput(html, this, this.baseUri());
        this.addChildren(0, nodes.toArray(new Node[0]));
        return this;
    }

    @Override
    public Element before(String html) {
        return (Element)super.before(html);
    }

    @Override
    public Element before(Node node) {
        return (Element)super.before(node);
    }

    @Override
    public Element after(String html) {
        return (Element)super.after(html);
    }

    @Override
    public Element after(Node node) {
        return (Element)super.after(node);
    }

    @Override
    public Element empty() {
        this.childNodes.clear();
        return this;
    }

    @Override
    public Element wrap(String html) {
        return (Element)super.wrap(html);
    }

    public String cssSelector() {
        if (this.id().length() > 0) {
            return "#" + this.id();
        }
        String tagName = this.tagName().replace(':', '|');
        StringBuilder selector = new StringBuilder(tagName);
        String classes = StringUtil.join(this.classNames(), ".");
        if (classes.length() > 0) {
            selector.append('.').append(classes);
        }
        if (this.parent() == null || this.parent() instanceof Document) {
            return selector.toString();
        }
        selector.insert(0, " > ");
        if (this.parent().select(selector.toString()).size() > 1) {
            selector.append(String.format(":nth-child(%d)", this.elementSiblingIndex() + 1));
        }
        return this.parent().cssSelector() + selector.toString();
    }

    public Elements siblingElements() {
        if (this.parentNode == null) {
            return new Elements(0);
        }
        List<Element> elements = this.parent().childElementsList();
        Elements siblings = new Elements(elements.size() - 1);
        for (Element el : elements) {
            if (el == this) continue;
            siblings.add(el);
        }
        return siblings;
    }

    public Element nextElementSibling() {
        if (this.parentNode == null) {
            return null;
        }
        List<Element> siblings = this.parent().childElementsList();
        int index = Element.indexInList(this, siblings);
        if (siblings.size() > index + 1) {
            return siblings.get(index + 1);
        }
        return null;
    }

    public Elements nextElementSiblings() {
        return this.nextElementSiblings(true);
    }

    public Element previousElementSibling() {
        if (this.parentNode == null) {
            return null;
        }
        List<Element> siblings = this.parent().childElementsList();
        int index = Element.indexInList(this, siblings);
        if (index > 0) {
            return siblings.get(index - 1);
        }
        return null;
    }

    public Elements previousElementSiblings() {
        return this.nextElementSiblings(false);
    }

    private Elements nextElementSiblings(boolean next) {
        Elements els = new Elements();
        if (this.parentNode == null) {
            return els;
        }
        els.add(this);
        return next ? els.nextAll() : els.prevAll();
    }

    public Element firstElementSibling() {
        List<Element> siblings = this.parent().childElementsList();
        return siblings.size() > 1 ? siblings.get(0) : null;
    }

    public int elementSiblingIndex() {
        if (this.parent() == null) {
            return 0;
        }
        return Element.indexInList(this, this.parent().childElementsList());
    }

    public Element lastElementSibling() {
        List<Element> siblings = this.parent().childElementsList();
        return siblings.size() > 1 ? siblings.get(siblings.size() - 1) : null;
    }

    private static <E extends Element> int indexInList(Element search, List<E> elements) {
        int size = elements.size();
        for (int i = 0; i < size; ++i) {
            if (elements.get(i) != search) continue;
            return i;
        }
        return 0;
    }

    public Elements getElementsByTag(String tagName) {
        Validate.notEmpty(tagName);
        tagName = Normalizer.normalize(tagName);
        return Collector.collect(new Evaluator.Tag(tagName), this);
    }

    public Element getElementById(String id) {
        Validate.notEmpty(id);
        Elements elements = Collector.collect(new Evaluator.Id(id), this);
        if (elements.size() > 0) {
            return (Element)elements.get(0);
        }
        return null;
    }

    public Elements getElementsByClass(String className) {
        Validate.notEmpty(className);
        return Collector.collect(new Evaluator.Class(className), this);
    }

    public Elements getElementsByAttribute(String key) {
        Validate.notEmpty(key);
        key = key.trim();
        return Collector.collect(new Evaluator.Attribute(key), this);
    }

    public Elements getElementsByAttributeStarting(String keyPrefix) {
        Validate.notEmpty(keyPrefix);
        keyPrefix = keyPrefix.trim();
        return Collector.collect(new Evaluator.AttributeStarting(keyPrefix), this);
    }

    public Elements getElementsByAttributeValue(String key, String value) {
        return Collector.collect(new Evaluator.AttributeWithValue(key, value), this);
    }

    public Elements getElementsByAttributeValueNot(String key, String value) {
        return Collector.collect(new Evaluator.AttributeWithValueNot(key, value), this);
    }

    public Elements getElementsByAttributeValueStarting(String key, String valuePrefix) {
        return Collector.collect(new Evaluator.AttributeWithValueStarting(key, valuePrefix), this);
    }

    public Elements getElementsByAttributeValueEnding(String key, String valueSuffix) {
        return Collector.collect(new Evaluator.AttributeWithValueEnding(key, valueSuffix), this);
    }

    public Elements getElementsByAttributeValueContaining(String key, String match) {
        return Collector.collect(new Evaluator.AttributeWithValueContaining(key, match), this);
    }

    public Elements getElementsByAttributeValueMatching(String key, Pattern pattern) {
        return Collector.collect(new Evaluator.AttributeWithValueMatching(key, pattern), this);
    }

    public Elements getElementsByAttributeValueMatching(String key, String regex) {
        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        }
        catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
        }
        return this.getElementsByAttributeValueMatching(key, pattern);
    }

    public Elements getElementsByIndexLessThan(int index) {
        return Collector.collect(new Evaluator.IndexLessThan(index), this);
    }

    public Elements getElementsByIndexGreaterThan(int index) {
        return Collector.collect(new Evaluator.IndexGreaterThan(index), this);
    }

    public Elements getElementsByIndexEquals(int index) {
        return Collector.collect(new Evaluator.IndexEquals(index), this);
    }

    public Elements getElementsContainingText(String searchText) {
        return Collector.collect(new Evaluator.ContainsText(searchText), this);
    }

    public Elements getElementsContainingOwnText(String searchText) {
        return Collector.collect(new Evaluator.ContainsOwnText(searchText), this);
    }

    public Elements getElementsMatchingText(Pattern pattern) {
        return Collector.collect(new Evaluator.Matches(pattern), this);
    }

    public Elements getElementsMatchingText(String regex) {
        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        }
        catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
        }
        return this.getElementsMatchingText(pattern);
    }

    public Elements getElementsMatchingOwnText(Pattern pattern) {
        return Collector.collect(new Evaluator.MatchesOwn(pattern), this);
    }

    public Elements getElementsMatchingOwnText(String regex) {
        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        }
        catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
        }
        return this.getElementsMatchingOwnText(pattern);
    }

    public Elements getAllElements() {
        return Collector.collect(new Evaluator.AllElements(), this);
    }

    public String text() {
        final StringBuilder accum = StringUtil.borrowBuilder();
        NodeTraversor.traverse(new NodeVisitor(){

            @Override
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    TextNode textNode = (TextNode)node;
                    Element.appendNormalisedText(accum, textNode);
                } else if (node instanceof Element) {
                    Element element = (Element)node;
                    if (accum.length() > 0 && (element.isBlock() || element.tag.getName().equals("br")) && !TextNode.lastCharIsWhitespace(accum)) {
                        accum.append(' ');
                    }
                }
            }

            @Override
            public void tail(Node node, int depth) {
                Element element;
                if (node instanceof Element && (element = (Element)node).isBlock() && node.nextSibling() instanceof TextNode && !TextNode.lastCharIsWhitespace(accum)) {
                    accum.append(' ');
                }
            }
        }, this);
        return StringUtil.releaseBuilder(accum).trim();
    }

    public String wholeText() {
        final StringBuilder accum = StringUtil.borrowBuilder();
        NodeTraversor.traverse(new NodeVisitor(){

            @Override
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    TextNode textNode = (TextNode)node;
                    accum.append(textNode.getWholeText());
                }
            }

            @Override
            public void tail(Node node, int depth) {
            }
        }, this);
        return StringUtil.releaseBuilder(accum);
    }

    public String ownText() {
        StringBuilder sb = StringUtil.borrowBuilder();
        this.ownText(sb);
        return StringUtil.releaseBuilder(sb).trim();
    }

    private void ownText(StringBuilder accum) {
        for (Node child : this.childNodes) {
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                Element.appendNormalisedText(accum, textNode);
                continue;
            }
            if (!(child instanceof Element)) continue;
            Element.appendWhitespaceIfBr((Element)child, accum);
        }
    }

    private static void appendNormalisedText(StringBuilder accum, TextNode textNode) {
        String text = textNode.getWholeText();
        if (Element.preserveWhitespace(textNode.parentNode) || textNode instanceof CDataNode) {
            accum.append(text);
        } else {
            StringUtil.appendNormalisedWhitespace(accum, text, TextNode.lastCharIsWhitespace(accum));
        }
    }

    private static void appendWhitespaceIfBr(Element element, StringBuilder accum) {
        if (element.tag.getName().equals("br") && !TextNode.lastCharIsWhitespace(accum)) {
            accum.append(" ");
        }
    }

    static boolean preserveWhitespace(Node node) {
        if (node instanceof Element) {
            Element el = (Element)node;
            int i = 0;
            do {
                if (el.tag.preserveWhitespace()) {
                    return true;
                }
                el = el.parent();
            } while (++i < 6 && el != null);
        }
        return false;
    }

    public Element text(String text) {
        Validate.notNull(text);
        this.empty();
        TextNode textNode = new TextNode(text);
        this.appendChild(textNode);
        return this;
    }

    public boolean hasText() {
        for (Node child : this.childNodes) {
            Element el;
            TextNode textNode;
            if (!(child instanceof TextNode ? !(textNode = (TextNode)child).isBlank() : child instanceof Element && (el = (Element)child).hasText())) continue;
            return true;
        }
        return false;
    }

    public String data() {
        StringBuilder sb = StringUtil.borrowBuilder();
        for (Node childNode : this.childNodes) {
            if (childNode instanceof DataNode) {
                DataNode data = (DataNode)childNode;
                sb.append(data.getWholeData());
                continue;
            }
            if (childNode instanceof Comment) {
                Comment comment = (Comment)childNode;
                sb.append(comment.getData());
                continue;
            }
            if (childNode instanceof Element) {
                Element element = (Element)childNode;
                String elementData = element.data();
                sb.append(elementData);
                continue;
            }
            if (!(childNode instanceof CDataNode)) continue;
            CDataNode cDataNode = (CDataNode)childNode;
            sb.append(cDataNode.getWholeText());
        }
        return StringUtil.releaseBuilder(sb);
    }

    public String className() {
        return this.attr("class").trim();
    }

    public Set<String> classNames() {
        String[] names = classSplit.split(this.className());
        LinkedHashSet<String> classNames = new LinkedHashSet<String>(Arrays.asList(names));
        classNames.remove("");
        return classNames;
    }

    public Element classNames(Set<String> classNames) {
        Validate.notNull(classNames);
        if (classNames.isEmpty()) {
            this.attributes().remove("class");
        } else {
            this.attributes().put("class", StringUtil.join(classNames, " "));
        }
        return this;
    }

    public boolean hasClass(String className) {
        if (!this.hasAttributes()) {
            return false;
        }
        String classAttr = this.attributes.getIgnoreCase("class");
        int len = classAttr.length();
        int wantLen = className.length();
        if (len == 0 || len < wantLen) {
            return false;
        }
        if (len == wantLen) {
            return className.equalsIgnoreCase(classAttr);
        }
        boolean inClass = false;
        int start = 0;
        for (int i = 0; i < len; ++i) {
            if (Character.isWhitespace(classAttr.charAt(i))) {
                if (!inClass) continue;
                if (i - start == wantLen && classAttr.regionMatches(true, start, className, 0, wantLen)) {
                    return true;
                }
                inClass = false;
                continue;
            }
            if (inClass) continue;
            inClass = true;
            start = i;
        }
        if (inClass && len - start == wantLen) {
            return classAttr.regionMatches(true, start, className, 0, wantLen);
        }
        return false;
    }

    public Element addClass(String className) {
        Validate.notNull(className);
        Set<String> classes = this.classNames();
        classes.add(className);
        this.classNames(classes);
        return this;
    }

    public Element removeClass(String className) {
        Validate.notNull(className);
        Set<String> classes = this.classNames();
        classes.remove(className);
        this.classNames(classes);
        return this;
    }

    public Element toggleClass(String className) {
        Validate.notNull(className);
        Set<String> classes = this.classNames();
        if (classes.contains(className)) {
            classes.remove(className);
        } else {
            classes.add(className);
        }
        this.classNames(classes);
        return this;
    }

    public String val() {
        if (this.normalName().equals("textarea")) {
            return this.text();
        }
        return this.attr("value");
    }

    public Element val(String value) {
        if (this.normalName().equals("textarea")) {
            this.text(value);
        } else {
            this.attr("value", value);
        }
        return this;
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        if (out.prettyPrint() && this.isFormatAsBlock(out) && !this.isInlineable(out)) {
            if (accum instanceof StringBuilder) {
                if (((StringBuilder)accum).length() > 0) {
                    this.indent(accum, depth, out);
                }
            } else {
                this.indent(accum, depth, out);
            }
        }
        accum.append('<').append(this.tagName());
        if (this.attributes != null) {
            this.attributes.html(accum, out);
        }
        if (this.childNodes.isEmpty() && this.tag.isSelfClosing()) {
            if (out.syntax() == Document.OutputSettings.Syntax.html && this.tag.isEmpty()) {
                accum.append('>');
            } else {
                accum.append(" />");
            }
        } else {
            accum.append('>');
        }
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        if (!this.childNodes.isEmpty() || !this.tag.isSelfClosing()) {
            if (out.prettyPrint() && !this.childNodes.isEmpty() && (this.tag.formatAsBlock() || out.outline() && (this.childNodes.size() > 1 || this.childNodes.size() == 1 && !(this.childNodes.get(0) instanceof TextNode)))) {
                this.indent(accum, depth, out);
            }
            accum.append("</").append(this.tagName()).append('>');
        }
    }

    public String html() {
        StringBuilder accum = StringUtil.borrowBuilder();
        this.html(accum);
        String html = StringUtil.releaseBuilder(accum);
        return NodeUtils.outputSettings(this).prettyPrint() ? html.trim() : html;
    }

    @Override
    public <T extends Appendable> T html(T appendable) {
        int size = this.childNodes.size();
        for (int i = 0; i < size; ++i) {
            this.childNodes.get(i).outerHtml(appendable);
        }
        return appendable;
    }

    public Element html(String html) {
        this.empty();
        this.append(html);
        return this;
    }

    @Override
    public Element clone() {
        return (Element)super.clone();
    }

    @Override
    public Element shallowClone() {
        return new Element(this.tag, this.baseUri(), this.attributes == null ? null : this.attributes.clone());
    }

    @Override
    protected Element doClone(Node parent) {
        Element clone = (Element)super.doClone(parent);
        clone.attributes = this.attributes != null ? this.attributes.clone() : null;
        clone.childNodes = new NodeList(clone, this.childNodes.size());
        clone.childNodes.addAll(this.childNodes);
        clone.setBaseUri(this.baseUri());
        return clone;
    }

    @Override
    public Element clearAttributes() {
        if (this.attributes != null) {
            super.clearAttributes();
            this.attributes = null;
        }
        return this;
    }

    @Override
    public Element removeAttr(String attributeKey) {
        return (Element)super.removeAttr(attributeKey);
    }

    @Override
    public Element root() {
        return (Element)super.root();
    }

    @Override
    public Element traverse(NodeVisitor nodeVisitor) {
        return (Element)super.traverse(nodeVisitor);
    }

    @Override
    public Element filter(NodeFilter nodeFilter) {
        return (Element)super.filter(nodeFilter);
    }

    private boolean isFormatAsBlock(Document.OutputSettings out) {
        return this.tag.formatAsBlock() || this.parent() != null && this.parent().tag().formatAsBlock() || out.outline();
    }

    private boolean isInlineable(Document.OutputSettings out) {
        return this.tag().isInline() && !this.tag().isEmpty() && this.parent().isBlock() && this.previousSibling() != null && !out.outline();
    }

    private static final class NodeList
    extends ChangeNotifyingArrayList<Node> {
        private final Element owner;

        NodeList(Element owner, int initialCapacity) {
            super(initialCapacity);
            this.owner = owner;
        }

        @Override
        public void onContentsChanged() {
            this.owner.nodelistChanged();
        }
    }
}

