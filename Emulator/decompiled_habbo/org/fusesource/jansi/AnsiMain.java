/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;
import org.fusesource.hawtjni.runtime.Library;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.internal.CLibrary;

public class AnsiMain {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String ... args) throws IOException {
        System.out.println("Jansi " + AnsiMain.getJansiVersion() + " (Jansi native " + AnsiMain.getPomPropertiesVersion("org.fusesource.jansi/jansi-native") + ", HawtJNI runtime " + AnsiMain.getPomPropertiesVersion("org.fusesource.hawtjni/hawtjni-runtime") + ")");
        System.out.println();
        System.out.println("library.jansi.path= " + System.getProperty("library.jansi.path", ""));
        System.out.println("library.jansi.version= " + System.getProperty("library.jansi.version", ""));
        Library lib = new Library("jansi", CLibrary.class);
        lib.load();
        System.out.println("Jansi native library loaded from " + lib.getNativeLibraryPath());
        if (lib.getNativeLibrarySourceUrl() != null) {
            System.out.println("   which was auto-extracted from " + lib.getNativeLibrarySourceUrl());
        }
        System.out.println();
        System.out.println("os.name= " + System.getProperty("os.name") + ", os.version= " + System.getProperty("os.version") + ", os.arch= " + System.getProperty("os.arch"));
        System.out.println("file.encoding= " + System.getProperty("file.encoding"));
        System.out.println("java.version= " + System.getProperty("java.version") + ", java.vendor= " + System.getProperty("java.vendor") + ", java.home= " + System.getProperty("java.home"));
        System.out.println();
        System.out.println("jansi.passthrough= " + Boolean.getBoolean("jansi.passthrough"));
        System.out.println("jansi.strip= " + Boolean.getBoolean("jansi.strip"));
        System.out.println("jansi.force= " + Boolean.getBoolean("jansi.force"));
        System.out.println(Ansi.DISABLE + "= " + Boolean.getBoolean(Ansi.DISABLE));
        System.out.println();
        System.out.println("IS_WINDOWS: " + AnsiConsole.IS_WINDOWS);
        if (AnsiConsole.IS_WINDOWS) {
            System.out.println("IS_CYGWIN: " + AnsiConsole.IS_CYGWIN);
            System.out.println("IS_MINGW_XTERM: " + AnsiConsole.IS_MINGW_XTERM);
        }
        System.out.println();
        AnsiMain.diagnoseTty(false);
        AnsiMain.diagnoseTty(true);
        AnsiConsole.systemInstall();
        System.out.println();
        System.out.println("Resulting Jansi modes for stout/stderr streams:");
        System.out.println("  - System.out: " + (Object)((Object)AnsiConsole.JANSI_STDOUT_TYPE));
        System.out.println("  - System.err: " + (Object)((Object)AnsiConsole.JANSI_STDERR_TYPE));
        System.out.println("modes description:");
        int n = 1;
        for (AnsiConsole.JansiOutputType type : AnsiConsole.JansiOutputType.values()) {
            System.out.println(n++ + ". " + (Object)((Object)type) + ": " + type.getDescription());
        }
        try {
            File f;
            System.out.println();
            AnsiMain.testAnsi(false);
            AnsiMain.testAnsi(true);
            if (args.length == 0) {
                AnsiMain.printJansiLogoDemo();
                return;
            }
            System.out.println();
            if (args.length == 1 && (f = new File(args[0])).exists()) {
                System.out.println(Ansi.ansi().bold().a("\"" + args[0] + "\" content:").reset());
                AnsiMain.writeFileContent(f);
                return;
            }
            System.out.println(Ansi.ansi().bold().a("original args:").reset());
            int i = 1;
            for (String arg : args) {
                AnsiConsole.system_out.print(i++ + ": ");
                AnsiConsole.system_out.println(arg);
            }
            System.out.println(Ansi.ansi().bold().a("Jansi filtered args:").reset());
            i = 1;
            for (String arg : args) {
                System.out.print(i++ + ": ");
                System.out.println(arg);
            }
        }
        finally {
            AnsiConsole.systemUninstall();
        }
    }

    private static String getJansiVersion() {
        Package p = AnsiMain.class.getPackage();
        return p == null ? null : p.getImplementationVersion();
    }

    private static void diagnoseTty(boolean stderr) {
        int fd = stderr ? CLibrary.STDERR_FILENO : CLibrary.STDOUT_FILENO;
        int isatty = CLibrary.isatty(fd);
        System.out.println("isatty(STD" + (stderr ? "ERR" : "OUT") + "_FILENO): " + isatty + ", System." + (stderr ? "err" : "out") + " " + (isatty == 0 ? "is *NOT*" : "is") + " a terminal");
    }

    private static void testAnsi(boolean stderr) {
        PrintStream s = stderr ? System.err : System.out;
        s.print("test on System." + (stderr ? "err" : "out") + ":");
        for (Ansi.Color c : Ansi.Color.values()) {
            s.print(" " + Ansi.ansi().fg(c) + (Object)((Object)c) + Ansi.ansi().reset());
        }
        s.println();
        s.print("            bright:");
        for (Ansi.Color c : Ansi.Color.values()) {
            s.print(" " + Ansi.ansi().fgBright(c) + (Object)((Object)c) + Ansi.ansi().reset());
        }
        s.println();
        s.print("              bold:");
        for (Ansi.Color c : Ansi.Color.values()) {
            s.print(" " + Ansi.ansi().bold().fg(c) + (Object)((Object)c) + Ansi.ansi().reset());
        }
        s.println();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String getPomPropertiesVersion(String path) throws IOException {
        InputStream in = AnsiMain.class.getResourceAsStream("/META-INF/maven/" + path + "/pom.properties");
        if (in == null) {
            return null;
        }
        try {
            Properties p = new Properties();
            p.load(in);
            String string = p.getProperty("version");
            return string;
        }
        finally {
            AnsiMain.closeQuietly(in);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void printJansiLogoDemo() throws IOException {
        InputStreamReader in = new InputStreamReader(AnsiMain.class.getResourceAsStream("jansi.txt"), "UTF-8");
        try {
            char[] buf = new char[1024];
            int l = 0;
            while ((l = in.read(buf)) >= 0) {
                for (int i = 0; i < l; ++i) {
                    System.out.print(buf[i]);
                }
            }
        }
        finally {
            AnsiMain.closeQuietly(in);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void writeFileContent(File f) throws IOException {
        FileInputStream in = new FileInputStream(f);
        try {
            byte[] buf = new byte[1024];
            int l = 0;
            while ((l = ((InputStream)in).read(buf)) >= 0) {
                System.out.write(buf, 0, l);
            }
        }
        finally {
            AnsiMain.closeQuietly(in);
        }
    }

    private static void closeQuietly(Closeable c) {
        try {
            c.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }
}

