/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.SimpleKeywordNode;

public class CompositeNode
extends SimpleKeywordNode {
    Node childNode;

    CompositeNode(String keyword) {
        super(2, keyword);
    }

    public Node getChildNode() {
        return this.childNode;
    }

    public void setChildNode(Node childNode) {
        this.childNode = childNode;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof CompositeNode)) {
            return false;
        }
        CompositeNode r = (CompositeNode)o;
        return this.childNode != null ? this.childNode.equals(r.childNode) : r.childNode == null;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.childNode != null) {
            buf.append("CompositeNode(" + this.childNode + ")");
        } else {
            buf.append("CompositeNode(no child)");
        }
        buf.append(this.printNext());
        return buf.toString();
    }
}

