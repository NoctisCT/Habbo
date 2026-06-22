/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.parser;

public class Node {
    static final int LITERAL = 0;
    static final int SIMPLE_KEYWORD = 1;
    static final int COMPOSITE_KEYWORD = 2;
    final int type;
    final Object value;
    Node next;

    Node(int type) {
        this(type, null);
    }

    Node(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    public Node getNext() {
        return this.next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        Node r = (Node)o;
        return this.type == r.type && (this.value != null ? this.value.equals(r.value) : r.value == null) && (this.next != null ? this.next.equals(r.next) : r.next == null);
    }

    public int hashCode() {
        int result = this.type;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    String printNext() {
        if (this.next != null) {
            return " -> " + this.next;
        }
        return "";
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        switch (this.type) {
            case 0: {
                buf.append("LITERAL(" + this.value + ")");
                break;
            }
            default: {
                buf.append(super.toString());
            }
        }
        buf.append(this.printNext());
        return buf.toString();
    }
}

