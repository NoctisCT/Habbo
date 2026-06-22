/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.server.HardenedLoggingEventInputStream;
import ch.qos.logback.classic.net.server.RemoteAppenderClient;
import ch.qos.logback.core.net.HardenedObjectInputStream;
import ch.qos.logback.core.util.CloseUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

class RemoteAppenderStreamClient
implements RemoteAppenderClient {
    private final String id;
    private final Socket socket;
    private final InputStream inputStream;
    private LoggerContext lc;
    private Logger logger;

    public RemoteAppenderStreamClient(String id, Socket socket) {
        this.id = id;
        this.socket = socket;
        this.inputStream = null;
    }

    public RemoteAppenderStreamClient(String id, InputStream inputStream) {
        this.id = id;
        this.socket = null;
        this.inputStream = inputStream;
    }

    @Override
    public void setLoggerContext(LoggerContext lc) {
        this.lc = lc;
        this.logger = lc.getLogger(this.getClass().getPackage().getName());
    }

    @Override
    public void close() {
        if (this.socket == null) {
            return;
        }
        CloseUtil.closeQuietly(this.socket);
    }

    /*
     * Exception decompiling
     */
    @Override
    public void run() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private HardenedObjectInputStream createObjectInputStream() throws IOException {
        if (this.inputStream != null) {
            return new HardenedLoggingEventInputStream(this.inputStream);
        }
        return new HardenedLoggingEventInputStream(this.socket.getInputStream());
    }

    public String toString() {
        return "client " + this.id;
    }
}

