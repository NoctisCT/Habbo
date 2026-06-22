/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package ch.qos.logback.classic;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.ViewStatusMessagesServletBase;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;

public class ViewStatusMessagesServlet
extends ViewStatusMessagesServletBase {
    private static final long serialVersionUID = 443878494348593337L;

    @Override
    protected StatusManager getStatusManager(HttpServletRequest req, HttpServletResponse resp) {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        return lc.getStatusManager();
    }

    @Override
    protected String getPageTitle(HttpServletRequest req, HttpServletResponse resp) {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        return "<h2>Status messages for LoggerContext named [" + lc.getName() + "]</h2>\r\n";
    }
}

