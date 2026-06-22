/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.parser.HtmlTreeBuilder;
import org.jsoup.parser.Parser;

final class NodeUtils {
    NodeUtils() {
    }

    static Document.OutputSettings outputSettings(Node node) {
        Document owner = node.ownerDocument();
        return owner != null ? owner.outputSettings() : new Document("").outputSettings();
    }

    static Parser parser(Node node) {
        Document doc = node.ownerDocument();
        return doc != null && doc.parser() != null ? doc.parser() : new Parser(new HtmlTreeBuilder());
    }
}

