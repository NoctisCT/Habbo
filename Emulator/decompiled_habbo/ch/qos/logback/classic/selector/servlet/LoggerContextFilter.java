/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package ch.qos.logback.classic.selector.servlet;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextJNDISelector;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.LoggerFactory;

public class LoggerContextFilter
implements Filter {
    public void destroy() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
        ContextJNDISelector sel = null;
        if (selector instanceof ContextJNDISelector) {
            sel = (ContextJNDISelector)selector;
            sel.setLocalContext(lc);
        }
        try {
            chain.doFilter(request, response);
        }
        finally {
            if (sel != null) {
                sel.removeLocalContext();
            }
        }
    }

    public void init(FilterConfig arg0) throws ServletException {
    }
}

