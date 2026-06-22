/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.FormatInfo;
import ch.qos.logback.core.pattern.IdentityCompositeConverter;
import ch.qos.logback.core.pattern.ReplacingCompositeConverter;
import ch.qos.logback.core.pattern.parser.Compiler;
import ch.qos.logback.core.pattern.parser.CompositeNode;
import ch.qos.logback.core.pattern.parser.FormattingNode;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.SimpleKeywordNode;
import ch.qos.logback.core.pattern.parser.Token;
import ch.qos.logback.core.pattern.parser.TokenStream;
import ch.qos.logback.core.pattern.util.IEscapeUtil;
import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.ScanException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser<E>
extends ContextAwareBase {
    public static final String MISSING_RIGHT_PARENTHESIS = "http://logback.qos.ch/codes.html#missingRightParenthesis";
    public static final Map<String, String> DEFAULT_COMPOSITE_CONVERTER_MAP = new HashMap<String, String>();
    public static final String REPLACE_CONVERTER_WORD = "replace";
    final List tokenList;
    int pointer = 0;

    Parser(TokenStream ts) throws ScanException {
        this.tokenList = ts.tokenize();
    }

    public Parser(String pattern) throws ScanException {
        this(pattern, new RegularEscapeUtil());
    }

    public Parser(String pattern, IEscapeUtil escapeUtil) throws ScanException {
        try {
            TokenStream ts = new TokenStream(pattern, escapeUtil);
            this.tokenList = ts.tokenize();
        }
        catch (IllegalArgumentException npe) {
            throw new ScanException("Failed to initialize Parser", npe);
        }
    }

    public Converter<E> compile(Node top, Map converterMap) {
        Compiler compiler = new Compiler(top, converterMap);
        compiler.setContext(this.context);
        return compiler.compile();
    }

    public Node parse() throws ScanException {
        return this.E();
    }

    Node E() throws ScanException {
        Node t = this.T();
        if (t == null) {
            return null;
        }
        Node eOpt = this.Eopt();
        if (eOpt != null) {
            t.setNext(eOpt);
        }
        return t;
    }

    Node Eopt() throws ScanException {
        Token next = this.getCurentToken();
        if (next == null) {
            return null;
        }
        return this.E();
    }

    Node T() throws ScanException {
        Token t = this.getCurentToken();
        this.expectNotNull(t, "a LITERAL or '%'");
        switch (t.getType()) {
            case 1000: {
                this.advanceTokenPointer();
                return new Node(0, t.getValue());
            }
            case 37: {
                FormattingNode c;
                this.advanceTokenPointer();
                Token u = this.getCurentToken();
                this.expectNotNull(u, "a FORMAT_MODIFIER, SIMPLE_KEYWORD or COMPOUND_KEYWORD");
                if (u.getType() == 1002) {
                    FormatInfo fi = FormatInfo.valueOf((String)u.getValue());
                    this.advanceTokenPointer();
                    c = this.C();
                    c.setFormatInfo(fi);
                } else {
                    c = this.C();
                }
                return c;
            }
        }
        return null;
    }

    FormattingNode C() throws ScanException {
        Token t = this.getCurentToken();
        this.expectNotNull(t, "a LEFT_PARENTHESIS or KEYWORD");
        int type = t.getType();
        switch (type) {
            case 1004: {
                return this.SINGLE();
            }
            case 1005: {
                this.advanceTokenPointer();
                return this.COMPOSITE(t.getValue().toString());
            }
        }
        throw new IllegalStateException("Unexpected token " + t);
    }

    FormattingNode SINGLE() throws ScanException {
        Token t = this.getNextToken();
        SimpleKeywordNode keywordNode = new SimpleKeywordNode(t.getValue());
        Token ot = this.getCurentToken();
        if (ot != null && ot.getType() == 1006) {
            List optionList = (List)ot.getValue();
            keywordNode.setOptions(optionList);
            this.advanceTokenPointer();
        }
        return keywordNode;
    }

    FormattingNode COMPOSITE(String keyword) throws ScanException {
        CompositeNode compositeNode = new CompositeNode(keyword);
        Node childNode = this.E();
        compositeNode.setChildNode(childNode);
        Token t = this.getNextToken();
        if (t == null || t.getType() != 41) {
            String msg = "Expecting RIGHT_PARENTHESIS token but got " + t;
            this.addError(msg);
            this.addError("See also http://logback.qos.ch/codes.html#missingRightParenthesis");
            throw new ScanException(msg);
        }
        Token ot = this.getCurentToken();
        if (ot != null && ot.getType() == 1006) {
            List optionList = (List)ot.getValue();
            compositeNode.setOptions(optionList);
            this.advanceTokenPointer();
        }
        return compositeNode;
    }

    Token getNextToken() {
        if (this.pointer < this.tokenList.size()) {
            return (Token)this.tokenList.get(this.pointer++);
        }
        return null;
    }

    Token getCurentToken() {
        if (this.pointer < this.tokenList.size()) {
            return (Token)this.tokenList.get(this.pointer);
        }
        return null;
    }

    void advanceTokenPointer() {
        ++this.pointer;
    }

    void expectNotNull(Token t, String expected) {
        if (t == null) {
            throw new IllegalStateException("All tokens consumed but was expecting " + expected);
        }
    }

    static {
        DEFAULT_COMPOSITE_CONVERTER_MAP.put(Token.BARE_COMPOSITE_KEYWORD_TOKEN.getValue().toString(), IdentityCompositeConverter.class.getName());
        DEFAULT_COMPOSITE_CONVERTER_MAP.put(REPLACE_CONVERTER_WORD, ReplacingCompositeConverter.class.getName());
    }
}

