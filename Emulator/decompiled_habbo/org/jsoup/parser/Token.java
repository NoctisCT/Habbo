/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Attributes;

abstract class Token {
    TokenType type;

    private Token() {
    }

    String tokenType() {
        return this.getClass().getSimpleName();
    }

    abstract Token reset();

    static void reset(StringBuilder sb) {
        if (sb != null) {
            sb.delete(0, sb.length());
        }
    }

    final boolean isDoctype() {
        return this.type == TokenType.Doctype;
    }

    final Doctype asDoctype() {
        return (Doctype)this;
    }

    final boolean isStartTag() {
        return this.type == TokenType.StartTag;
    }

    final StartTag asStartTag() {
        return (StartTag)this;
    }

    final boolean isEndTag() {
        return this.type == TokenType.EndTag;
    }

    final EndTag asEndTag() {
        return (EndTag)this;
    }

    final boolean isComment() {
        return this.type == TokenType.Comment;
    }

    final Comment asComment() {
        return (Comment)this;
    }

    final boolean isCharacter() {
        return this.type == TokenType.Character;
    }

    final boolean isCData() {
        return this instanceof CData;
    }

    final Character asCharacter() {
        return (Character)this;
    }

    final boolean isEOF() {
        return this.type == TokenType.EOF;
    }

    public static enum TokenType {
        Doctype,
        StartTag,
        EndTag,
        Comment,
        Character,
        EOF;

    }

    static final class Doctype
    extends Token {
        final StringBuilder name = new StringBuilder();
        String pubSysKey = null;
        final StringBuilder publicIdentifier = new StringBuilder();
        final StringBuilder systemIdentifier = new StringBuilder();
        boolean forceQuirks = false;

        Doctype() {
            this.type = TokenType.Doctype;
        }

        @Override
        Token reset() {
            Doctype.reset(this.name);
            this.pubSysKey = null;
            Doctype.reset(this.publicIdentifier);
            Doctype.reset(this.systemIdentifier);
            this.forceQuirks = false;
            return this;
        }

        String getName() {
            return this.name.toString();
        }

        String getPubSysKey() {
            return this.pubSysKey;
        }

        String getPublicIdentifier() {
            return this.publicIdentifier.toString();
        }

        public String getSystemIdentifier() {
            return this.systemIdentifier.toString();
        }

        public boolean isForceQuirks() {
            return this.forceQuirks;
        }
    }

    static final class StartTag
    extends Tag {
        StartTag() {
            this.type = TokenType.StartTag;
        }

        @Override
        Tag reset() {
            super.reset();
            this.attributes = null;
            return this;
        }

        StartTag nameAttr(String name, Attributes attributes) {
            this.tagName = name;
            this.attributes = attributes;
            this.normalName = Normalizer.lowerCase(this.tagName);
            return this;
        }

        public String toString() {
            if (this.attributes != null && this.attributes.size() > 0) {
                return "<" + this.name() + " " + this.attributes.toString() + ">";
            }
            return "<" + this.name() + ">";
        }
    }

    static final class EndTag
    extends Tag {
        EndTag() {
            this.type = TokenType.EndTag;
        }

        public String toString() {
            return "</" + (this.tagName != null ? this.tagName : "(unset)") + ">";
        }
    }

    static final class Comment
    extends Token {
        private final StringBuilder data = new StringBuilder();
        private String dataS;
        boolean bogus = false;

        @Override
        Token reset() {
            Comment.reset(this.data);
            this.dataS = null;
            this.bogus = false;
            return this;
        }

        Comment() {
            this.type = TokenType.Comment;
        }

        String getData() {
            return this.dataS != null ? this.dataS : this.data.toString();
        }

        final Comment append(String append) {
            this.ensureData();
            if (this.data.length() == 0) {
                this.dataS = append;
            } else {
                this.data.append(append);
            }
            return this;
        }

        final Comment append(char append) {
            this.ensureData();
            this.data.append(append);
            return this;
        }

        private void ensureData() {
            if (this.dataS != null) {
                this.data.append(this.dataS);
                this.dataS = null;
            }
        }

        public String toString() {
            return "<!--" + this.getData() + "-->";
        }
    }

    static final class CData
    extends Character {
        CData(String data) {
            this.data(data);
        }

        @Override
        public String toString() {
            return "<![CDATA[" + this.getData() + "]]>";
        }
    }

    static class Character
    extends Token {
        private String data;

        Character() {
            this.type = TokenType.Character;
        }

        @Override
        Token reset() {
            this.data = null;
            return this;
        }

        Character data(String data) {
            this.data = data;
            return this;
        }

        String getData() {
            return this.data;
        }

        public String toString() {
            return this.getData();
        }
    }

    static final class EOF
    extends Token {
        EOF() {
            this.type = TokenType.EOF;
        }

        @Override
        Token reset() {
            return this;
        }
    }

    static abstract class Tag
    extends Token {
        protected String tagName;
        protected String normalName;
        private String pendingAttributeName;
        private StringBuilder pendingAttributeValue = new StringBuilder();
        private String pendingAttributeValueS;
        private boolean hasEmptyAttributeValue = false;
        private boolean hasPendingAttributeValue = false;
        boolean selfClosing = false;
        Attributes attributes;

        Tag() {
        }

        @Override
        Tag reset() {
            this.tagName = null;
            this.normalName = null;
            this.pendingAttributeName = null;
            Tag.reset(this.pendingAttributeValue);
            this.pendingAttributeValueS = null;
            this.hasEmptyAttributeValue = false;
            this.hasPendingAttributeValue = false;
            this.selfClosing = false;
            this.attributes = null;
            return this;
        }

        final void newAttribute() {
            if (this.attributes == null) {
                this.attributes = new Attributes();
            }
            if (this.pendingAttributeName != null) {
                this.pendingAttributeName = this.pendingAttributeName.trim();
                if (this.pendingAttributeName.length() > 0) {
                    String value = this.hasPendingAttributeValue ? (this.pendingAttributeValue.length() > 0 ? this.pendingAttributeValue.toString() : this.pendingAttributeValueS) : (this.hasEmptyAttributeValue ? "" : null);
                    this.attributes.add(this.pendingAttributeName, value);
                }
            }
            this.pendingAttributeName = null;
            this.hasEmptyAttributeValue = false;
            this.hasPendingAttributeValue = false;
            Tag.reset(this.pendingAttributeValue);
            this.pendingAttributeValueS = null;
        }

        final void finaliseTag() {
            if (this.pendingAttributeName != null) {
                this.newAttribute();
            }
        }

        final String name() {
            Validate.isFalse(this.tagName == null || this.tagName.length() == 0);
            return this.tagName;
        }

        final String normalName() {
            return this.normalName;
        }

        final Tag name(String name) {
            this.tagName = name;
            this.normalName = Normalizer.lowerCase(name);
            return this;
        }

        final boolean isSelfClosing() {
            return this.selfClosing;
        }

        final Attributes getAttributes() {
            if (this.attributes == null) {
                this.attributes = new Attributes();
            }
            return this.attributes;
        }

        final void appendTagName(String append) {
            this.tagName = this.tagName == null ? append : this.tagName.concat(append);
            this.normalName = Normalizer.lowerCase(this.tagName);
        }

        final void appendTagName(char append) {
            this.appendTagName(String.valueOf(append));
        }

        final void appendAttributeName(String append) {
            this.pendingAttributeName = this.pendingAttributeName == null ? append : this.pendingAttributeName.concat(append);
        }

        final void appendAttributeName(char append) {
            this.appendAttributeName(String.valueOf(append));
        }

        final void appendAttributeValue(String append) {
            this.ensureAttributeValue();
            if (this.pendingAttributeValue.length() == 0) {
                this.pendingAttributeValueS = append;
            } else {
                this.pendingAttributeValue.append(append);
            }
        }

        final void appendAttributeValue(char append) {
            this.ensureAttributeValue();
            this.pendingAttributeValue.append(append);
        }

        final void appendAttributeValue(char[] append) {
            this.ensureAttributeValue();
            this.pendingAttributeValue.append(append);
        }

        final void appendAttributeValue(int[] appendCodepoints) {
            this.ensureAttributeValue();
            for (int codepoint : appendCodepoints) {
                this.pendingAttributeValue.appendCodePoint(codepoint);
            }
        }

        final void setEmptyAttributeValue() {
            this.hasEmptyAttributeValue = true;
        }

        private void ensureAttributeValue() {
            this.hasPendingAttributeValue = true;
            if (this.pendingAttributeValueS != null) {
                this.pendingAttributeValue.append(this.pendingAttributeValueS);
                this.pendingAttributeValueS = null;
            }
        }
    }
}

