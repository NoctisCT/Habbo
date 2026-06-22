/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.subst;

import ch.qos.logback.core.spi.PropertyContainer;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.subst.Node;
import ch.qos.logback.core.subst.Parser;
import ch.qos.logback.core.subst.Token;
import ch.qos.logback.core.subst.Tokenizer;
import ch.qos.logback.core.util.OptionHelper;
import java.util.List;
import java.util.Stack;

public class NodeToStringTransformer {
    final Node node;
    final PropertyContainer propertyContainer0;
    final PropertyContainer propertyContainer1;

    public NodeToStringTransformer(Node node, PropertyContainer propertyContainer0, PropertyContainer propertyContainer1) {
        this.node = node;
        this.propertyContainer0 = propertyContainer0;
        this.propertyContainer1 = propertyContainer1;
    }

    public NodeToStringTransformer(Node node, PropertyContainer propertyContainer0) {
        this(node, propertyContainer0, null);
    }

    public static String substituteVariable(String input, PropertyContainer pc0, PropertyContainer pc1) throws ScanException {
        Node node = NodeToStringTransformer.tokenizeAndParseString(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, pc0, pc1);
        return nodeToStringTransformer.transform();
    }

    private static Node tokenizeAndParseString(String value) throws ScanException {
        Tokenizer tokenizer = new Tokenizer(value);
        List<Token> tokens = tokenizer.tokenize();
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    public String transform() throws ScanException {
        StringBuilder stringBuilder = new StringBuilder();
        this.compileNode(this.node, stringBuilder, new Stack<Node>());
        return stringBuilder.toString();
    }

    private void compileNode(Node inputNode, StringBuilder stringBuilder, Stack<Node> cycleCheckStack) throws ScanException {
        Node n = inputNode;
        while (n != null) {
            switch (n.type) {
                case LITERAL: {
                    this.handleLiteral(n, stringBuilder);
                    break;
                }
                case VARIABLE: {
                    this.handleVariable(n, stringBuilder, cycleCheckStack);
                }
            }
            n = n.next;
        }
    }

    private void handleVariable(Node n, StringBuilder stringBuilder, Stack<Node> cycleCheckStack) throws ScanException {
        if (this.haveVisitedNodeAlready(n, cycleCheckStack)) {
            cycleCheckStack.push(n);
            String error = this.constructRecursionErrorMessage(cycleCheckStack);
            throw new IllegalArgumentException(error);
        }
        cycleCheckStack.push(n);
        StringBuilder keyBuffer = new StringBuilder();
        Node payload = (Node)n.payload;
        this.compileNode(payload, keyBuffer, cycleCheckStack);
        String key = keyBuffer.toString();
        String value = this.lookupKey(key);
        if (value != null) {
            Node innerNode = NodeToStringTransformer.tokenizeAndParseString(value);
            this.compileNode(innerNode, stringBuilder, cycleCheckStack);
            cycleCheckStack.pop();
            return;
        }
        if (n.defaultPart == null) {
            stringBuilder.append(key + "_IS_UNDEFINED");
            cycleCheckStack.pop();
            return;
        }
        Node defaultPart = (Node)n.defaultPart;
        StringBuilder defaultPartBuffer = new StringBuilder();
        this.compileNode(defaultPart, defaultPartBuffer, cycleCheckStack);
        cycleCheckStack.pop();
        String defaultVal = defaultPartBuffer.toString();
        stringBuilder.append(defaultVal);
    }

    private String lookupKey(String key) {
        String value = this.propertyContainer0.getProperty(key);
        if (value != null) {
            return value;
        }
        if (this.propertyContainer1 != null && (value = this.propertyContainer1.getProperty(key)) != null) {
            return value;
        }
        value = OptionHelper.getSystemProperty(key, null);
        if (value != null) {
            return value;
        }
        value = OptionHelper.getEnv(key);
        if (value != null) {
            return value;
        }
        return null;
    }

    private void handleLiteral(Node n, StringBuilder stringBuilder) {
        stringBuilder.append((String)n.payload);
    }

    private String variableNodeValue(Node variableNode) {
        Node literalPayload = (Node)variableNode.payload;
        return (String)literalPayload.payload;
    }

    private String constructRecursionErrorMessage(Stack<Node> recursionNodes) {
        StringBuilder errorBuilder = new StringBuilder("Circular variable reference detected while parsing input [");
        for (Node stackNode : recursionNodes) {
            errorBuilder.append("${").append(this.variableNodeValue(stackNode)).append("}");
            if (recursionNodes.lastElement() == stackNode) continue;
            errorBuilder.append(" --> ");
        }
        errorBuilder.append("]");
        return errorBuilder.toString();
    }

    private boolean haveVisitedNodeAlready(Node node, Stack<Node> cycleDetectionStack) {
        for (Node cycleNode : cycleDetectionStack) {
            if (!this.equalNodes(node, cycleNode)) continue;
            return true;
        }
        return false;
    }

    private boolean equalNodes(Node node1, Node node2) {
        if (node1.type != null && !node1.type.equals((Object)node2.type)) {
            return false;
        }
        if (node1.payload != null && !node1.payload.equals(node2.payload)) {
            return false;
        }
        return node1.defaultPart == null || node1.defaultPart.equals(node2.defaultPart);
    }
}

