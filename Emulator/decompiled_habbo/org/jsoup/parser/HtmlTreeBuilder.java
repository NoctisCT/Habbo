/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.LeafNode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.HtmlTreeBuilderState;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.parser.Token;
import org.jsoup.parser.TokeniserState;
import org.jsoup.parser.TreeBuilder;
import org.jsoup.select.Elements;

public class HtmlTreeBuilder
extends TreeBuilder {
    static final String[] TagsSearchInScope = new String[]{"applet", "caption", "html", "marquee", "object", "table", "td", "th"};
    static final String[] TagSearchList = new String[]{"ol", "ul"};
    static final String[] TagSearchButton = new String[]{"button"};
    static final String[] TagSearchTableScope = new String[]{"html", "table"};
    static final String[] TagSearchSelectScope = new String[]{"optgroup", "option"};
    static final String[] TagSearchEndTags = new String[]{"dd", "dt", "li", "optgroup", "option", "p", "rp", "rt"};
    static final String[] TagSearchSpecial = new String[]{"address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", "br", "button", "caption", "center", "col", "colgroup", "command", "dd", "details", "dir", "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "iframe", "img", "input", "isindex", "li", "link", "listing", "marquee", "menu", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", "p", "param", "plaintext", "pre", "script", "section", "select", "style", "summary", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "ul", "wbr", "xmp"};
    public static final int MaxScopeSearchDepth = 100;
    private HtmlTreeBuilderState state;
    private HtmlTreeBuilderState originalState;
    private boolean baseUriSetFromDoc;
    private Element headElement;
    private FormElement formElement;
    private Element contextElement;
    private ArrayList<Element> formattingElements;
    private List<String> pendingTableCharacters;
    private Token.EndTag emptyEnd;
    private boolean framesetOk;
    private boolean fosterInserts;
    private boolean fragmentParsing;
    private String[] specificScopeTarget = new String[]{null};

    @Override
    ParseSettings defaultSettings() {
        return ParseSettings.htmlDefault;
    }

    @Override
    protected void initialiseParse(Reader input, String baseUri, Parser parser) {
        super.initialiseParse(input, baseUri, parser);
        this.state = HtmlTreeBuilderState.Initial;
        this.originalState = null;
        this.baseUriSetFromDoc = false;
        this.headElement = null;
        this.formElement = null;
        this.contextElement = null;
        this.formattingElements = new ArrayList();
        this.pendingTableCharacters = new ArrayList<String>();
        this.emptyEnd = new Token.EndTag();
        this.framesetOk = true;
        this.fosterInserts = false;
        this.fragmentParsing = false;
    }

    @Override
    List<Node> parseFragment(String inputFragment, Element context, String baseUri, Parser parser) {
        this.state = HtmlTreeBuilderState.Initial;
        this.initialiseParse(new StringReader(inputFragment), baseUri, parser);
        this.contextElement = context;
        this.fragmentParsing = true;
        Node root = null;
        if (context != null) {
            if (context.ownerDocument() != null) {
                this.doc.quirksMode(context.ownerDocument().quirksMode());
            }
            String contextTag = context.normalName();
            if (StringUtil.in(contextTag, "title", "textarea")) {
                this.tokeniser.transition(TokeniserState.Rcdata);
            } else if (StringUtil.in(contextTag, "iframe", "noembed", "noframes", "style", "xmp")) {
                this.tokeniser.transition(TokeniserState.Rawtext);
            } else if (contextTag.equals("script")) {
                this.tokeniser.transition(TokeniserState.ScriptData);
            } else if (contextTag.equals("noscript")) {
                this.tokeniser.transition(TokeniserState.Data);
            } else if (contextTag.equals("plaintext")) {
                this.tokeniser.transition(TokeniserState.Data);
            } else {
                this.tokeniser.transition(TokeniserState.Data);
            }
            root = new Element(Tag.valueOf("html", this.settings), baseUri);
            this.doc.appendChild(root);
            this.stack.add(root);
            this.resetInsertionMode();
            Elements contextChain = context.parents();
            contextChain.add(0, context);
            for (Element parent : contextChain) {
                if (!(parent instanceof FormElement)) continue;
                this.formElement = (FormElement)parent;
                break;
            }
        }
        this.runParser();
        if (context != null) {
            return root.childNodes();
        }
        return this.doc.childNodes();
    }

    @Override
    protected boolean process(Token token) {
        this.currentToken = token;
        return this.state.process(token, this);
    }

    boolean process(Token token, HtmlTreeBuilderState state) {
        this.currentToken = token;
        return state.process(token, this);
    }

    void transition(HtmlTreeBuilderState state) {
        this.state = state;
    }

    HtmlTreeBuilderState state() {
        return this.state;
    }

    void markInsertionMode() {
        this.originalState = this.state;
    }

    HtmlTreeBuilderState originalState() {
        return this.originalState;
    }

    void framesetOk(boolean framesetOk) {
        this.framesetOk = framesetOk;
    }

    boolean framesetOk() {
        return this.framesetOk;
    }

    Document getDocument() {
        return this.doc;
    }

    String getBaseUri() {
        return this.baseUri;
    }

    void maybeSetBaseUri(Element base) {
        if (this.baseUriSetFromDoc) {
            return;
        }
        String href = base.absUrl("href");
        if (href.length() != 0) {
            this.baseUri = href;
            this.baseUriSetFromDoc = true;
            this.doc.setBaseUri(href);
        }
    }

    boolean isFragmentParsing() {
        return this.fragmentParsing;
    }

    void error(HtmlTreeBuilderState state) {
        if (this.parser.getErrors().canAddError()) {
            this.parser.getErrors().add(new ParseError(this.reader.pos(), "Unexpected token [%s] when in state [%s]", new Object[]{this.currentToken.tokenType(), state}));
        }
    }

    Element insert(Token.StartTag startTag) {
        int dupes;
        if (startTag.attributes != null && !startTag.attributes.isEmpty() && (dupes = startTag.attributes.deduplicate(this.settings)) > 0) {
            this.error("Duplicate attribute");
        }
        if (startTag.isSelfClosing()) {
            Element el = this.insertEmpty(startTag);
            this.stack.add(el);
            this.tokeniser.transition(TokeniserState.Data);
            this.tokeniser.emit(this.emptyEnd.reset().name(el.tagName()));
            return el;
        }
        Element el = new Element(Tag.valueOf(startTag.name(), this.settings), null, this.settings.normalizeAttributes(startTag.attributes));
        this.insert(el);
        return el;
    }

    Element insertStartTag(String startTagName) {
        Element el = new Element(Tag.valueOf(startTagName, this.settings), null);
        this.insert(el);
        return el;
    }

    void insert(Element el) {
        this.insertNode(el);
        this.stack.add(el);
    }

    Element insertEmpty(Token.StartTag startTag) {
        Tag tag = Tag.valueOf(startTag.name(), this.settings);
        Element el = new Element(tag, null, this.settings.normalizeAttributes(startTag.attributes));
        this.insertNode(el);
        if (startTag.isSelfClosing()) {
            if (tag.isKnownTag()) {
                if (!tag.isEmpty()) {
                    this.tokeniser.error("Tag cannot be self closing; not a void tag");
                }
            } else {
                tag.setSelfClosing();
            }
        }
        return el;
    }

    FormElement insertForm(Token.StartTag startTag, boolean onStack) {
        Tag tag = Tag.valueOf(startTag.name(), this.settings);
        FormElement el = new FormElement(tag, null, this.settings.normalizeAttributes(startTag.attributes));
        this.setFormElement(el);
        this.insertNode(el);
        if (onStack) {
            this.stack.add(el);
        }
        return el;
    }

    void insert(Token.Comment commentToken) {
        Comment comment = new Comment(commentToken.getData());
        this.insertNode(comment);
    }

    void insert(Token.Character characterToken) {
        Element el = this.currentElement();
        if (el == null) {
            el = this.doc;
        }
        String tagName = el.normalName();
        String data = characterToken.getData();
        LeafNode node = characterToken.isCData() ? new CDataNode(data) : (tagName.equals("script") || tagName.equals("style") ? new DataNode(data) : new TextNode(data));
        el.appendChild(node);
    }

    private void insertNode(Node node) {
        if (this.stack.isEmpty()) {
            this.doc.appendChild(node);
        } else if (this.isFosterInserts()) {
            this.insertInFosterParent(node);
        } else {
            this.currentElement().appendChild(node);
        }
        if (node instanceof Element && ((Element)node).tag().isFormListed() && this.formElement != null) {
            this.formElement.addElement((Element)node);
        }
    }

    Element pop() {
        int size = this.stack.size();
        return (Element)this.stack.remove(size - 1);
    }

    void push(Element element) {
        this.stack.add(element);
    }

    ArrayList<Element> getStack() {
        return this.stack;
    }

    boolean onStack(Element el) {
        return this.isElementInQueue(this.stack, el);
    }

    private boolean isElementInQueue(ArrayList<Element> queue, Element element) {
        for (int pos = queue.size() - 1; pos >= 0; --pos) {
            Element next = queue.get(pos);
            if (next != element) continue;
            return true;
        }
        return false;
    }

    Element getFromStack(String elName) {
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element next = (Element)this.stack.get(pos);
            if (!next.normalName().equals(elName)) continue;
            return next;
        }
        return null;
    }

    boolean removeFromStack(Element el) {
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element next = (Element)this.stack.get(pos);
            if (next != el) continue;
            this.stack.remove(pos);
            return true;
        }
        return false;
    }

    Element popStackToClose(String elName) {
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element el = (Element)this.stack.get(pos);
            this.stack.remove(pos);
            if (!el.normalName().equals(elName)) continue;
            return el;
        }
        return null;
    }

    void popStackToClose(String ... elNames) {
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element next = (Element)this.stack.get(pos);
            this.stack.remove(pos);
            if (StringUtil.inSorted(next.normalName(), elNames)) break;
        }
    }

    void popStackToBefore(String elName) {
        Element next;
        for (int pos = this.stack.size() - 1; pos >= 0 && !(next = (Element)this.stack.get(pos)).normalName().equals(elName); --pos) {
            this.stack.remove(pos);
        }
    }

    void clearStackToTableContext() {
        this.clearStackToContext("table");
    }

    void clearStackToTableBodyContext() {
        this.clearStackToContext("tbody", "tfoot", "thead", "template");
    }

    void clearStackToTableRowContext() {
        this.clearStackToContext("tr", "template");
    }

    private void clearStackToContext(String ... nodeNames) {
        Element next;
        for (int pos = this.stack.size() - 1; pos >= 0 && !StringUtil.in((next = (Element)this.stack.get(pos)).normalName(), nodeNames) && !next.normalName().equals("html"); --pos) {
            this.stack.remove(pos);
        }
    }

    Element aboveOnStack(Element el) {
        assert (this.onStack(el));
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element next = (Element)this.stack.get(pos);
            if (next != el) continue;
            return (Element)this.stack.get(pos - 1);
        }
        return null;
    }

    void insertOnStackAfter(Element after, Element in) {
        int i = this.stack.lastIndexOf(after);
        Validate.isTrue(i != -1);
        this.stack.add(i + 1, in);
    }

    void replaceOnStack(Element out, Element in) {
        this.replaceInQueue(this.stack, out, in);
    }

    private void replaceInQueue(ArrayList<Element> queue, Element out, Element in) {
        int i = queue.lastIndexOf(out);
        Validate.isTrue(i != -1);
        queue.set(i, in);
    }

    void resetInsertionMode() {
        boolean last = false;
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            String name;
            Element node = (Element)this.stack.get(pos);
            if (pos == 0) {
                last = true;
                node = this.contextElement;
            }
            if ("select".equals(name = node.normalName())) {
                this.transition(HtmlTreeBuilderState.InSelect);
                break;
            }
            if ("td".equals(name) || "th".equals(name) && !last) {
                this.transition(HtmlTreeBuilderState.InCell);
                break;
            }
            if ("tr".equals(name)) {
                this.transition(HtmlTreeBuilderState.InRow);
                break;
            }
            if ("tbody".equals(name) || "thead".equals(name) || "tfoot".equals(name)) {
                this.transition(HtmlTreeBuilderState.InTableBody);
                break;
            }
            if ("caption".equals(name)) {
                this.transition(HtmlTreeBuilderState.InCaption);
                break;
            }
            if ("colgroup".equals(name)) {
                this.transition(HtmlTreeBuilderState.InColumnGroup);
                break;
            }
            if ("table".equals(name)) {
                this.transition(HtmlTreeBuilderState.InTable);
                break;
            }
            if ("head".equals(name)) {
                this.transition(HtmlTreeBuilderState.InBody);
                break;
            }
            if ("body".equals(name)) {
                this.transition(HtmlTreeBuilderState.InBody);
                break;
            }
            if ("frameset".equals(name)) {
                this.transition(HtmlTreeBuilderState.InFrameset);
                break;
            }
            if ("html".equals(name)) {
                this.transition(HtmlTreeBuilderState.BeforeHead);
                break;
            }
            if (!last) continue;
            this.transition(HtmlTreeBuilderState.InBody);
            break;
        }
    }

    private boolean inSpecificScope(String targetName, String[] baseTypes, String[] extraTypes) {
        this.specificScopeTarget[0] = targetName;
        return this.inSpecificScope(this.specificScopeTarget, baseTypes, extraTypes);
    }

    private boolean inSpecificScope(String[] targetNames, String[] baseTypes, String[] extraTypes) {
        int bottom = this.stack.size() - 1;
        int top = bottom > 100 ? bottom - 100 : 0;
        for (int pos = bottom; pos >= top; --pos) {
            String elName = ((Element)this.stack.get(pos)).normalName();
            if (StringUtil.inSorted(elName, targetNames)) {
                return true;
            }
            if (StringUtil.inSorted(elName, baseTypes)) {
                return false;
            }
            if (extraTypes == null || !StringUtil.inSorted(elName, extraTypes)) continue;
            return false;
        }
        return false;
    }

    boolean inScope(String[] targetNames) {
        return this.inSpecificScope(targetNames, TagsSearchInScope, null);
    }

    boolean inScope(String targetName) {
        return this.inScope(targetName, null);
    }

    boolean inScope(String targetName, String[] extras) {
        return this.inSpecificScope(targetName, TagsSearchInScope, extras);
    }

    boolean inListItemScope(String targetName) {
        return this.inScope(targetName, TagSearchList);
    }

    boolean inButtonScope(String targetName) {
        return this.inScope(targetName, TagSearchButton);
    }

    boolean inTableScope(String targetName) {
        return this.inSpecificScope(targetName, TagSearchTableScope, null);
    }

    boolean inSelectScope(String targetName) {
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element el = (Element)this.stack.get(pos);
            String elName = el.normalName();
            if (elName.equals(targetName)) {
                return true;
            }
            if (StringUtil.inSorted(elName, TagSearchSelectScope)) continue;
            return false;
        }
        Validate.fail("Should not be reachable");
        return false;
    }

    void setHeadElement(Element headElement) {
        this.headElement = headElement;
    }

    Element getHeadElement() {
        return this.headElement;
    }

    boolean isFosterInserts() {
        return this.fosterInserts;
    }

    void setFosterInserts(boolean fosterInserts) {
        this.fosterInserts = fosterInserts;
    }

    FormElement getFormElement() {
        return this.formElement;
    }

    void setFormElement(FormElement formElement) {
        this.formElement = formElement;
    }

    void newPendingTableCharacters() {
        this.pendingTableCharacters = new ArrayList<String>();
    }

    List<String> getPendingTableCharacters() {
        return this.pendingTableCharacters;
    }

    void generateImpliedEndTags(String excludeTag) {
        while (excludeTag != null && !this.currentElement().normalName().equals(excludeTag) && StringUtil.inSorted(this.currentElement().normalName(), TagSearchEndTags)) {
            this.pop();
        }
    }

    void generateImpliedEndTags() {
        this.generateImpliedEndTags(null);
    }

    boolean isSpecial(Element el) {
        String name = el.normalName();
        return StringUtil.inSorted(name, TagSearchSpecial);
    }

    Element lastFormattingElement() {
        return this.formattingElements.size() > 0 ? this.formattingElements.get(this.formattingElements.size() - 1) : null;
    }

    Element removeLastFormattingElement() {
        int size = this.formattingElements.size();
        if (size > 0) {
            return this.formattingElements.remove(size - 1);
        }
        return null;
    }

    void pushActiveFormattingElements(Element in) {
        Element el;
        int numSeen = 0;
        for (int pos = this.formattingElements.size() - 1; pos >= 0 && (el = this.formattingElements.get(pos)) != null; --pos) {
            if (this.isSameFormattingElement(in, el)) {
                ++numSeen;
            }
            if (numSeen != 3) continue;
            this.formattingElements.remove(pos);
            break;
        }
        this.formattingElements.add(in);
    }

    private boolean isSameFormattingElement(Element a, Element b) {
        return a.normalName().equals(b.normalName()) && a.attributes().equals(b.attributes());
    }

    void reconstructFormattingElements() {
        Element last = this.lastFormattingElement();
        if (last == null || this.onStack(last)) {
            return;
        }
        Element entry = last;
        int size = this.formattingElements.size();
        int pos = size - 1;
        boolean skip = false;
        do {
            if (pos != 0) continue;
            skip = true;
            break;
        } while ((entry = this.formattingElements.get(--pos)) != null && !this.onStack(entry));
        do {
            if (!skip) {
                entry = this.formattingElements.get(++pos);
            }
            Validate.notNull(entry);
            skip = false;
            Element newEl = this.insertStartTag(entry.normalName());
            newEl.attributes().addAll(entry.attributes());
            this.formattingElements.set(pos, newEl);
        } while (pos != size - 1);
    }

    void clearFormattingElementsToLastMarker() {
        Element el;
        while (!this.formattingElements.isEmpty() && (el = this.removeLastFormattingElement()) != null) {
        }
    }

    void removeFromActiveFormattingElements(Element el) {
        for (int pos = this.formattingElements.size() - 1; pos >= 0; --pos) {
            Element next = this.formattingElements.get(pos);
            if (next != el) continue;
            this.formattingElements.remove(pos);
            break;
        }
    }

    boolean isInActiveFormattingElements(Element el) {
        return this.isElementInQueue(this.formattingElements, el);
    }

    Element getActiveFormattingElement(String nodeName) {
        Element next;
        for (int pos = this.formattingElements.size() - 1; pos >= 0 && (next = this.formattingElements.get(pos)) != null; --pos) {
            if (!next.normalName().equals(nodeName)) continue;
            return next;
        }
        return null;
    }

    void replaceActiveFormattingElement(Element out, Element in) {
        this.replaceInQueue(this.formattingElements, out, in);
    }

    void insertMarkerToFormattingElements() {
        this.formattingElements.add(null);
    }

    void insertInFosterParent(Node in) {
        Element fosterParent;
        Element lastTable = this.getFromStack("table");
        boolean isLastTableParent = false;
        if (lastTable != null) {
            if (lastTable.parent() != null) {
                fosterParent = lastTable.parent();
                isLastTableParent = true;
            } else {
                fosterParent = this.aboveOnStack(lastTable);
            }
        } else {
            fosterParent = (Element)this.stack.get(0);
        }
        if (isLastTableParent) {
            Validate.notNull(lastTable);
            lastTable.before(in);
        } else {
            fosterParent.appendChild(in);
        }
    }

    public String toString() {
        return "TreeBuilder{currentToken=" + this.currentToken + ", state=" + (Object)((Object)this.state) + ", currentElement=" + this.currentElement() + '}';
    }
}

