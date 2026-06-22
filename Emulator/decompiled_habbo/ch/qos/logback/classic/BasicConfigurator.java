/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

public class BasicConfigurator
extends ContextAwareBase
implements Configurator {
    @Override
    public void configure(LoggerContext lc) {
        this.addInfo("Setting up default configuration.");
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<ILoggingEvent>();
        ca.setContext(lc);
        ca.setName("console");
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<ILoggingEvent>();
        encoder.setContext(lc);
        TTLLLayout layout = new TTLLLayout();
        layout.setContext(lc);
        layout.start();
        encoder.setLayout(layout);
        ca.setEncoder(encoder);
        ca.start();
        Logger rootLogger = lc.getLogger("ROOT");
        rootLogger.addAppender((Appender<ILoggingEvent>)ca);
    }
}

