/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.helper;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class W3CDom {
    protected DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public W3CDom() {
        this.factory.setNamespaceAware(true);
    }

    public static org.w3c.dom.Document convert(Document in) {
        return new W3CDom().fromJsoup(in);
    }

    public static String asString(org.w3c.dom.Document doc, Map<String, String> properties) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            if (properties != null) {
                transformer.setOutputProperties(W3CDom.propertiesFromMap(properties));
            }
            if (doc.getDoctype() != null) {
                DocumentType doctype = doc.getDoctype();
                if (!StringUtil.isBlank(doctype.getPublicId())) {
                    transformer.setOutputProperty("doctype-public", doctype.getPublicId());
                }
                if (!StringUtil.isBlank(doctype.getSystemId())) {
                    transformer.setOutputProperty("doctype-system", doctype.getSystemId());
                } else if (doctype.getName().equalsIgnoreCase("html") && StringUtil.isBlank(doctype.getPublicId()) && StringUtil.isBlank(doctype.getSystemId())) {
                    transformer.setOutputProperty("doctype-system", "about:legacy-compat");
                }
            }
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

    static Properties propertiesFromMap(Map<String, String> map) {
        Properties props = new Properties();
        props.putAll(map);
        return props;
    }

    public static HashMap<String, String> OutputHtml() {
        return W3CDom.methodMap("html");
    }

    public static HashMap<String, String> OutputXml() {
        return W3CDom.methodMap("xml");
    }

    private static HashMap<String, String> methodMap(String method) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("method", method);
        return map;
    }

    public org.w3c.dom.Document fromJsoup(Document in) {
        Validate.notNull(in);
        try {
            DocumentBuilder builder = this.factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            org.w3c.dom.Document out = builder.newDocument();
            org.jsoup.nodes.DocumentType doctype = in.documentType();
            if (doctype != null) {
                DocumentType documentType = impl.createDocumentType(doctype.name(), doctype.publicId(), doctype.systemId());
                out.appendChild(documentType);
            }
            out.setXmlStandalone(true);
            this.convert(in, out);
            return out;
        }
        catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public void convert(Document in, org.w3c.dom.Document out) {
        if (!StringUtil.isBlank(in.location())) {
            out.setDocumentURI(in.location());
        }
        org.jsoup.nodes.Element rootEl = in.child(0);
        NodeTraversor.traverse((NodeVisitor)new W3CBuilder(out), rootEl);
    }

    public String asString(org.w3c.dom.Document doc) {
        return W3CDom.asString(doc, null);
    }

    protected static class W3CBuilder
    implements NodeVisitor {
        private static final String xmlnsKey = "xmlns";
        private static final String xmlnsPrefix = "xmlns:";
        private final org.w3c.dom.Document doc;
        private final Stack<HashMap<String, String>> namespacesStack = new Stack();
        private Element dest;

        public W3CBuilder(org.w3c.dom.Document doc) {
            this.doc = doc;
            this.namespacesStack.push(new HashMap());
        }

        @Override
        public void head(Node source, int depth) {
            this.namespacesStack.push(new HashMap(this.namespacesStack.peek()));
            if (source instanceof org.jsoup.nodes.Element) {
                org.jsoup.nodes.Element sourceEl = (org.jsoup.nodes.Element)source;
                String prefix = this.updateNamespaces(sourceEl);
                String namespace = this.namespacesStack.peek().get(prefix);
                String tagName = sourceEl.tagName();
                Element el = namespace == null && tagName.contains(":") ? this.doc.createElementNS("", tagName) : this.doc.createElementNS(namespace, tagName);
                this.copyAttributes(sourceEl, el);
                if (this.dest == null) {
                    this.doc.appendChild(el);
                } else {
                    this.dest.appendChild(el);
                }
                this.dest = el;
            } else if (source instanceof TextNode) {
                TextNode sourceText = (TextNode)source;
                Text text = this.doc.createTextNode(sourceText.getWholeText());
                this.dest.appendChild(text);
            } else if (source instanceof org.jsoup.nodes.Comment) {
                org.jsoup.nodes.Comment sourceComment = (org.jsoup.nodes.Comment)source;
                Comment comment = this.doc.createComment(sourceComment.getData());
                this.dest.appendChild(comment);
            } else if (source instanceof DataNode) {
                DataNode sourceData = (DataNode)source;
                Text node = this.doc.createTextNode(sourceData.getWholeData());
                this.dest.appendChild(node);
            }
        }

        @Override
        public void tail(Node source, int depth) {
            if (source instanceof org.jsoup.nodes.Element && this.dest.getParentNode() instanceof Element) {
                this.dest = (Element)this.dest.getParentNode();
            }
            this.namespacesStack.pop();
        }

        private void copyAttributes(Node source, Element el) {
            for (Attribute attribute : source.attributes()) {
                String key = attribute.getKey().replaceAll("[^-a-zA-Z0-9_:.]", "");
                if (!key.matches("[a-zA-Z_:][-a-zA-Z0-9_:.]*")) continue;
                el.setAttribute(key, attribute.getValue());
            }
        }

        private String updateNamespaces(org.jsoup.nodes.Element el) {
            Attributes attributes = el.attributes();
            for (Attribute attr : attributes) {
                String prefix;
                String key = attr.getKey();
                if (key.equals(xmlnsKey)) {
                    prefix = "";
                } else {
                    if (!key.startsWith(xmlnsPrefix)) continue;
                    prefix = key.substring(xmlnsPrefix.length());
                }
                this.namespacesStack.peek().put(prefix, attr.getValue());
            }
            int pos = el.tagName().indexOf(":");
            return pos > 0 ? el.tagName().substring(0, pos) : "";
        }
    }
}

