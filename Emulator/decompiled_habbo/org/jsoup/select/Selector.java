/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.select.Collector;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;

public class Selector {
    private Selector() {
    }

    public static Elements select(String query, Element root) {
        Validate.notEmpty(query);
        return Selector.select(QueryParser.parse(query), root);
    }

    public static Elements select(Evaluator evaluator, Element root) {
        Validate.notNull(evaluator);
        Validate.notNull(root);
        return Collector.collect(evaluator, root);
    }

    public static Elements select(String query, Iterable<Element> roots) {
        Validate.notEmpty(query);
        Validate.notNull(roots);
        Evaluator evaluator = QueryParser.parse(query);
        ArrayList<Element> elements = new ArrayList<Element>();
        IdentityHashMap<Element, Boolean> seenElements = new IdentityHashMap<Element, Boolean>();
        for (Element root : roots) {
            Elements found = Selector.select(evaluator, root);
            for (Element el : found) {
                if (seenElements.containsKey(el)) continue;
                elements.add(el);
                seenElements.put(el, Boolean.TRUE);
            }
        }
        return new Elements((List<Element>)elements);
    }

    static Elements filterOut(Collection<Element> elements, Collection<Element> outs) {
        Elements output = new Elements();
        for (Element el : elements) {
            boolean found = false;
            for (Element out : outs) {
                if (!el.equals(out)) continue;
                found = true;
                break;
            }
            if (found) continue;
            output.add(el);
        }
        return output;
    }

    public static Element selectFirst(String cssQuery, Element root) {
        Validate.notEmpty(cssQuery);
        return Collector.findFirst(QueryParser.parse(cssQuery), root);
    }

    public static class SelectorParseException
    extends IllegalStateException {
        public SelectorParseException(String msg, Object ... params) {
            super(String.format(msg, params));
        }
    }
}

