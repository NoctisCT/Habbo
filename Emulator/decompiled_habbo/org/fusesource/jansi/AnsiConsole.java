/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.fusesource.jansi.AnsiOutputStream;
import org.fusesource.jansi.AnsiPrintStream;
import org.fusesource.jansi.FilterPrintStream;
import org.fusesource.jansi.WindowsAnsiOutputStream;
import org.fusesource.jansi.WindowsAnsiPrintStream;
import org.fusesource.jansi.internal.CLibrary;

public class AnsiConsole {
    public static final PrintStream system_out = System.out;
    public static final PrintStream out;
    public static final PrintStream system_err;
    public static final PrintStream err;
    static final boolean IS_WINDOWS;
    static final boolean IS_CYGWIN;
    static final boolean IS_MINGW_XTERM;
    private static JansiOutputType jansiOutputType;
    static final JansiOutputType JANSI_STDOUT_TYPE;
    static final JansiOutputType JANSI_STDERR_TYPE;
    private static int installed;

    private AnsiConsole() {
    }

    @Deprecated
    public static OutputStream wrapOutputStream(OutputStream stream) {
        try {
            return AnsiConsole.wrapOutputStream(stream, CLibrary.STDOUT_FILENO);
        }
        catch (Throwable ignore) {
            return AnsiConsole.wrapOutputStream(stream, 1);
        }
    }

    public static PrintStream wrapSystemOut(PrintStream ps) {
        try {
            return AnsiConsole.wrapPrintStream(ps, CLibrary.STDOUT_FILENO);
        }
        catch (Throwable ignore) {
            return AnsiConsole.wrapPrintStream(ps, 1);
        }
    }

    @Deprecated
    public static OutputStream wrapErrorOutputStream(OutputStream stream) {
        try {
            return AnsiConsole.wrapOutputStream(stream, CLibrary.STDERR_FILENO);
        }
        catch (Throwable ignore) {
            return AnsiConsole.wrapOutputStream(stream, 2);
        }
    }

    public static PrintStream wrapSystemErr(PrintStream ps) {
        try {
            return AnsiConsole.wrapPrintStream(ps, CLibrary.STDERR_FILENO);
        }
        catch (Throwable ignore) {
            return AnsiConsole.wrapPrintStream(ps, 2);
        }
    }

    @Deprecated
    public static OutputStream wrapOutputStream(OutputStream stream, int fileno) {
        if (Boolean.getBoolean("jansi.passthrough")) {
            jansiOutputType = JansiOutputType.PASSTHROUGH;
            return stream;
        }
        if (Boolean.getBoolean("jansi.strip")) {
            jansiOutputType = JansiOutputType.STRIP_ANSI;
            return new AnsiOutputStream(stream);
        }
        if (IS_WINDOWS && !IS_CYGWIN && !IS_MINGW_XTERM) {
            try {
                jansiOutputType = JansiOutputType.WINDOWS;
                return new WindowsAnsiOutputStream(stream, fileno == CLibrary.STDOUT_FILENO);
            }
            catch (Throwable throwable) {
                jansiOutputType = JansiOutputType.STRIP_ANSI;
                return new AnsiOutputStream(stream);
            }
        }
        try {
            boolean forceColored = Boolean.getBoolean("jansi.force");
            if (!forceColored && CLibrary.isatty(fileno) == 0) {
                jansiOutputType = JansiOutputType.STRIP_ANSI;
                return new AnsiOutputStream(stream);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        jansiOutputType = JansiOutputType.RESET_ANSI_AT_CLOSE;
        return new FilterOutputStream(stream){

            @Override
            public void close() throws IOException {
                this.write(AnsiOutputStream.RESET_CODE);
                this.flush();
                super.close();
            }
        };
    }

    public static PrintStream wrapPrintStream(PrintStream ps, int fileno) {
        if (Boolean.getBoolean("jansi.passthrough")) {
            jansiOutputType = JansiOutputType.PASSTHROUGH;
            return ps;
        }
        if (Boolean.getBoolean("jansi.strip")) {
            jansiOutputType = JansiOutputType.STRIP_ANSI;
            return new AnsiPrintStream(ps);
        }
        if (IS_WINDOWS && !IS_CYGWIN && !IS_MINGW_XTERM) {
            try {
                jansiOutputType = JansiOutputType.WINDOWS;
                return new WindowsAnsiPrintStream(ps, fileno == CLibrary.STDOUT_FILENO);
            }
            catch (Throwable throwable) {
                jansiOutputType = JansiOutputType.STRIP_ANSI;
                return new AnsiPrintStream(ps);
            }
        }
        try {
            boolean forceColored = Boolean.getBoolean("jansi.force");
            if (!forceColored && CLibrary.isatty(fileno) == 0) {
                jansiOutputType = JansiOutputType.STRIP_ANSI;
                return new AnsiPrintStream(ps);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        jansiOutputType = JansiOutputType.RESET_ANSI_AT_CLOSE;
        return new FilterPrintStream(ps){

            @Override
            public void close() {
                this.ps.print("\u001b[0m");
                this.ps.flush();
                super.close();
            }
        };
    }

    public static PrintStream out() {
        return out;
    }

    public static PrintStream err() {
        return err;
    }

    public static synchronized void systemInstall() {
        if (++installed == 1) {
            System.setOut(out);
            System.setErr(err);
        }
    }

    public static synchronized void systemUninstall() {
        if (--installed == 0) {
            System.setOut(system_out);
            System.setErr(system_err);
        }
    }

    static {
        system_err = System.err;
        IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");
        IS_CYGWIN = IS_WINDOWS && System.getenv("PWD") != null && System.getenv("PWD").startsWith("/") && !"cygwin".equals(System.getenv("TERM"));
        IS_MINGW_XTERM = IS_WINDOWS && System.getenv("MSYSTEM") != null && System.getenv("MSYSTEM").startsWith("MINGW") && "xterm".equals(System.getenv("TERM"));
        out = AnsiConsole.wrapSystemOut(system_out);
        JANSI_STDOUT_TYPE = jansiOutputType;
        err = AnsiConsole.wrapSystemErr(system_err);
        JANSI_STDERR_TYPE = jansiOutputType;
    }

    static enum JansiOutputType {
        PASSTHROUGH("just pass through, ANSI escape codes are supposed to be supported by terminal"),
        RESET_ANSI_AT_CLOSE("like pass through but reset ANSI attributes when closing the stream"),
        STRIP_ANSI("strip ANSI escape codes, for example when output is not a terminal"),
        WINDOWS("detect ANSI escape codes and transform Jansi-supported ones into a Windows API to get desired effect (since ANSI escape codes are not natively supported by Windows terminals like cmd.exe or PowerShell)");

        private final String description;

        private JansiOutputType(String description) {
            this.description = description;
        }

        String getDescription() {
            return this.description;
        }
    }
}

