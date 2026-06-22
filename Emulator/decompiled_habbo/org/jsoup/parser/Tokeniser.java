/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import java.util.Arrays;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.CharacterReader;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.Token;
import org.jsoup.parser.TokeniserState;

final class Tokeniser {
    static final char replacementChar = '\ufffd';
    private static final char[] notCharRefCharsSorted = new char[]{'\t', '\n', '\r', '\f', ' ', '<', '&'};
    static final int win1252ExtensionsStart = 128;
    static final int[] win1252Extensions = new int[]{8364, 129, 8218, 402, 8222, 8230, 8224, 8225, 710, 8240, 352, 8249, 338, 141, 381, 143, 144, 8216, 8217, 8220, 8221, 8226, 8211, 8212, 732, 8482, 353, 8250, 339, 157, 382, 376};
    private final CharacterReader reader;
    private final ParseErrorList errors;
    private TokeniserState state = TokeniserState.Data;
    private Token emitPending;
    private boolean isEmitPending = false;
    private String charsString = null;
    private StringBuilder charsBuilder = new StringBuilder(1024);
    StringBuilder dataBuffer = new StringBuilder(1024);
    Token.Tag tagPending;
    Token.StartTag startPending = new Token.StartTag();
    Token.EndTag endPending = new Token.EndTag();
    Token.Character charPending = new Token.Character();
    Token.Doctype doctypePending = new Token.Doctype();
    Token.Comment commentPending = new Token.Comment();
    private String lastStartTag;
    private final int[] codepointHolder = new int[1];
    private final int[] multipointHolder = new int[2];

    Tokeniser(CharacterReader reader, ParseErrorList errors) {
        this.reader = reader;
        this.errors = errors;
    }

    Token read() {
        while (!this.isEmitPending) {
            this.state.read(this, this.reader);
        }
        StringBuilder cb = this.charsBuilder;
        if (cb.length() != 0) {
            String str = cb.toString();
            cb.delete(0, cb.length());
            this.charsString = null;
            return this.charPending.data(str);
        }
        if (this.charsString != null) {
            Token.Character token = this.charPending.data(this.charsString);
            this.charsString = null;
            return token;
        }
        this.isEmitPending = false;
        return this.emitPending;
    }

    void emit(Token token) {
        Validate.isFalse(this.isEmitPending);
        this.emitPending = token;
        this.isEmitPending = true;
        if (token.type == Token.TokenType.StartTag) {
            Token.StartTag startTag = (Token.StartTag)token;
            this.lastStartTag = startTag.tagName;
        } else if (token.type == Token.TokenType.EndTag) {
            Token.EndTag endTag = (Token.EndTag)token;
            if (endTag.attributes != null) {
                this.error("Attributes incorrectly present on end tag");
            }
        }
    }

    void emit(String str) {
        if (this.charsString == null) {
            this.charsString = str;
        } else {
            if (this.charsBuilder.length() == 0) {
                this.charsBuilder.append(this.charsString);
            }
            this.charsBuilder.append(str);
        }
    }

    void emit(char[] chars) {
        this.emit(String.valueOf(chars));
    }

    void emit(int[] codepoints) {
        this.emit(new String(codepoints, 0, codepoints.length));
    }

    void emit(char c) {
        this.emit(String.valueOf(c));
    }

    TokeniserState getState() {
        return this.state;
    }

    void transition(TokeniserState state) {
        this.state = state;
    }

    void advanceTransition(TokeniserState state) {
        this.reader.advance();
        this.state = state;
    }

    int[] consumeCharacterReference(Character additionalAllowedCharacter, boolean inAttribute) {
        int numChars;
        boolean found;
        if (this.reader.isEmpty()) {
            return null;
        }
        if (additionalAllowedCharacter != null && additionalAllowedCharacter.charValue() == this.reader.current()) {
            return null;
        }
        if (this.reader.matchesAnySorted(notCharRefCharsSorted)) {
            return null;
        }
        int[] codeRef = this.codepointHolder;
        this.reader.mark();
        if (this.reader.matchConsume("#")) {
            String numRef;
            boolean isHexMode = this.reader.matchConsumeIgnoreCase("X");
            String string = numRef = isHexMode ? this.reader.consumeHexSequence() : this.reader.consumeDigitSequence();
            if (numRef.length() == 0) {
                this.characterReferenceError("numeric reference with no numerals");
                this.reader.rewindToMark();
                return null;
            }
            this.reader.unmark();
            if (!this.reader.matchConsume(";")) {
                this.characterReferenceError("missing semicolon");
            }
            int charval = -1;
            try {
                int base = isHexMode ? 16 : 10;
                charval = Integer.valueOf(numRef, base);
            }
            catch (NumberFormatException base) {
                // empty catch block
            }
            if (charval == -1 || charval >= 55296 && charval <= 57343 || charval > 0x10FFFF) {
                this.characterReferenceError("character outside of valid range");
                codeRef[0] = 65533;
                return codeRef;
            }
            if (charval >= 128 && charval < 128 + win1252Extensions.length) {
                this.characterReferenceError("character is not a valid unicode code point");
                charval = win1252Extensions[charval - 128];
            }
            codeRef[0] = charval;
            return codeRef;
        }
        String nameRef = this.reader.consumeLetterThenDigitSequence();
        boolean looksLegit = this.reader.matches(';');
        boolean bl = found = Entities.isBaseNamedEntity(nameRef) || Entities.isNamedEntity(nameRef) && looksLegit;
        if (!found) {
            this.reader.rewindToMark();
            if (looksLegit) {
                this.characterReferenceError("invalid named reference");
            }
            return null;
        }
        if (inAttribute && (this.reader.matchesLetter() || this.reader.matchesDigit() || this.reader.matchesAny('=', '-', '_'))) {
            this.reader.rewindToMark();
            return null;
        }
        this.reader.unmark();
        if (!this.reader.matchConsume(";")) {
            this.characterReferenceError("missing semicolon");
        }
        if ((numChars = Entities.codepointsForName(nameRef, this.multipointHolder)) == 1) {
            codeRef[0] = this.multipointHolder[0];
            return codeRef;
        }
        if (numChars == 2) {
            return this.multipointHolder;
        }
        Validate.fail("Unexpected characters returned for " + nameRef);
        return this.multipointHolder;
    }

    Token.Tag createTagPending(boolean start) {
        this.tagPending = start ? this.startPending.reset() : this.endPending.reset();
        return this.tagPending;
    }

    void emitTagPending() {
        this.tagPending.finaliseTag();
        this.emit(this.tagPending);
    }

    void createCommentPending() {
        this.commentPending.reset();
    }

    void emitCommentPending() {
        this.emit(this.commentPending);
    }

    void createBogusCommentPending() {
        this.commentPending.reset();
        this.commentPending.bogus = true;
    }

    void createDoctypePending() {
        this.doctypePending.reset();
    }

    void emitDoctypePending() {
        this.emit(this.doctypePending);
    }

    void createTempBuffer() {
        Token.reset(this.dataBuffer);
    }

    boolean isAppropriateEndTagToken() {
        return this.lastStartTag != null && this.tagPending.name().equalsIgnoreCase(this.lastStartTag);
    }

    String appropriateEndTagName() {
        return this.lastStartTag;
    }

    void error(TokeniserState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpected character '%s' in input state [%s]", new Object[]{Character.valueOf(this.reader.current()), state}));
        }
    }

    void eofError(TokeniserState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpectedly reached end of file (EOF) in input state [%s]", new Object[]{state}));
        }
    }

    private void characterReferenceError(String message) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Invalid character reference: %s", message));
        }
    }

    void error(String errorMsg) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), errorMsg));
        }
    }

    boolean currentNodeInHtmlNS() {
        return true;
    }

    String unescapeEntities(boolean inAttribute) {
        StringBuilder builder = StringUtil.borrowBuilder();
        while (!this.reader.isEmpty()) {
            builder.append(this.reader.consumeTo('&'));
            if (!this.reader.matches('&')) continue;
            this.reader.consume();
            int[] c = this.consumeCharacterReference(null, inAttribute);
            if (c == null || c.length == 0) {
                builder.append('&');
                continue;
            }
            builder.appendCodePoint(c[0]);
            if (c.length != 2) continue;
            builder.appendCodePoint(c[1]);
        }
        return StringUtil.releaseBuilder(builder);
    }

    static {
        Arrays.sort(notCharRefCharsSorted);
    }
}

