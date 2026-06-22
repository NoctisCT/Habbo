/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.CharacterReader;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Token;
import org.jsoup.parser.Tokeniser;

abstract class TreeBuilder {
    protected Parser parser;
    CharacterReader reader;
    Tokeniser tokeniser;
    protected Document doc;
    protected ArrayList<Element> stack;
    protected String baseUri;
    protected Token currentToken;
    protected ParseSettings settings;
    private Token.StartTag start = new Token.StartTag();
    private Token.EndTag end = new Token.EndTag();

    TreeBuilder() {
    }

    abstract ParseSettings defaultSettings();

    protected void initialiseParse(Reader input, String baseUri, Parser parser) {
        Validate.notNull(input, "String input must not be null");
        Validate.notNull(baseUri, "BaseURI must not be null");
        this.doc = new Document(baseUri);
        this.doc.parser(parser);
        this.parser = parser;
        this.settings = parser.settings();
        this.reader = new CharacterReader(input);
        this.currentToken = null;
        this.tokeniser = new Tokeniser(this.reader, parser.getErrors());
        this.stack = new ArrayList(32);
        this.baseUri = baseUri;
    }

    Document parse(Reader input, String baseUri, Parser parser) {
        this.initialiseParse(input, baseUri, parser);
        this.runParser();
        this.reader.close();
        this.reader = null;
        this.tokeniser = null;
        this.stack = null;
        return this.doc;
    }

    abstract List<Node> parseFragment(String var1, Element var2, String var3, Parser var4);

    protected void runParser() {
        Token token;
        Tokeniser tokeniser = this.tokeniser;
        Token.TokenType eof = Token.TokenType.EOF;
        do {
            token = tokeniser.read();
            this.process(token);
            token.reset();
        } while (token.type != eof);
    }

    protected abstract boolean process(Token var1);

    protected boolean processStartTag(String name) {
        Token.StartTag start = this.start;
        if (this.currentToken == start) {
            return this.process(new Token.StartTag().name(name));
        }
        return this.process(start.reset().name(name));
    }

    public boolean processStartTag(String name, Attributes attrs) {
        Token.StartTag start = this.start;
        if (this.currentToken == start) {
            return this.process(new Token.StartTag().nameAttr(name, attrs));
        }
        start.reset();
        start.nameAttr(name, attrs);
        return this.process(start);
    }

    protected boolean processEndTag(String name) {
        if (this.currentToken == this.end) {
            return this.process(new Token.EndTag().name(name));
        }
        return this.process(this.end.reset().name(name));
    }

    protected Element currentElement() {
        int size = this.stack.size();
        return size > 0 ? this.stack.get(size - 1) : null;
    }

    protected void error(String msg) {
        ParseErrorList errors = this.parser.getErrors();
        if (errors.canAddError()) {
            errors.add(new ParseError(this.reader.pos(), msg));
        }
    }
}

