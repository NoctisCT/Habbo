/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi.internal;

import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.FieldFlag;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;
import org.fusesource.hawtjni.runtime.MethodFlag;

@JniClass
public class CLibrary {
    private static final Library LIBRARY = new Library("jansi", CLibrary.class);
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(STDIN_FILENO)")
    public static int STDIN_FILENO;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(STDOUT_FILENO)")
    public static int STDOUT_FILENO;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(STDERR_FILENO)")
    public static int STDERR_FILENO;
    @JniField(flags={FieldFlag.CONSTANT}, accessor="1", conditional="defined(HAVE_ISATTY)")
    public static boolean HAVE_ISATTY;
    @JniField(flags={FieldFlag.CONSTANT}, accessor="1", conditional="defined(HAVE_TTYNAME)")
    public static boolean HAVE_TTYNAME;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(TCSANOW)")
    public static int TCSANOW;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(TCSADRAIN)")
    public static int TCSADRAIN;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(TCSAFLUSH)")
    public static int TCSAFLUSH;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(TIOCGETA)")
    public static long TIOCGETA;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(TIOCSETA)")
    public static long TIOCSETA;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(TIOCGETD)")
    public static long TIOCGETD;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(TIOCSETD)")
    public static long TIOCSETD;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(TIOCGWINSZ)")
    public static long TIOCGWINSZ;
    @JniField(flags={FieldFlag.CONSTANT}, conditional="defined(TIOCSWINSZ)")
    public static long TIOCSWINSZ;

    @JniMethod(flags={MethodFlag.CONSTANT_INITIALIZER})
    private static native void init();

    @JniMethod(conditional="FALSE")
    public static native int isatty(@JniArg int var0);

    @JniMethod(conditional="FALSE")
    public static native String ttyname(@JniArg int var0);

    @JniMethod(conditional="defined(HAVE_OPENPTY)")
    public static native int openpty(@JniArg(cast="int *", flags={ArgFlag.NO_IN}) int[] var0, @JniArg(cast="int *", flags={ArgFlag.NO_IN}) int[] var1, @JniArg(cast="char *", flags={ArgFlag.NO_IN}) byte[] var2, @JniArg(cast="struct termios *", flags={ArgFlag.NO_OUT}) Termios var3, @JniArg(cast="struct winsize *", flags={ArgFlag.NO_OUT}) WinSize var4);

    @JniMethod(conditional="defined(HAVE_TCGETATTR)")
    public static native int tcgetattr(@JniArg int var0, @JniArg(cast="struct termios *", flags={ArgFlag.NO_IN}) Termios var1);

    @JniMethod(conditional="defined(HAVE_TCSETATTR)")
    public static native int tcsetattr(@JniArg int var0, @JniArg int var1, @JniArg(cast="struct termios *", flags={ArgFlag.NO_OUT}) Termios var2);

    @JniMethod(conditional="defined(HAVE_IOCTL)")
    public static native int ioctl(@JniArg int var0, @JniArg long var1, @JniArg int[] var3);

    @JniMethod(conditional="defined(HAVE_IOCTL)")
    public static native int ioctl(@JniArg int var0, @JniArg long var1, @JniArg(flags={ArgFlag.POINTER_ARG}) WinSize var3);

    static {
        LIBRARY.load();
        CLibrary.init();
    }

    @JniClass(flags={ClassFlag.STRUCT}, name="termios", conditional="defined(HAVE_IOCTL)")
    public static class Termios {
        @JniField(flags={FieldFlag.CONSTANT}, accessor="sizeof(struct termios)")
        public static int SIZEOF;
        @JniField(accessor="c_iflag")
        public long c_iflag;
        @JniField(accessor="c_oflag")
        public long c_oflag;
        @JniField(accessor="c_cflag")
        public long c_cflag;
        @JniField(accessor="c_lflag")
        public long c_lflag;
        @JniField(accessor="c_cc")
        public byte[] c_cc = new byte[32];
        @JniField(accessor="c_ispeed")
        public long c_ispeed;
        @JniField(accessor="c_ospeed")
        public long c_ospeed;

        @JniMethod(flags={MethodFlag.CONSTANT_INITIALIZER})
        private static native void init();

        static {
            LIBRARY.load();
            Termios.init();
        }
    }

    @JniClass(flags={ClassFlag.STRUCT}, name="winsize", conditional="defined(HAVE_IOCTL)")
    public static class WinSize {
        @JniField(flags={FieldFlag.CONSTANT}, accessor="sizeof(struct winsize)")
        public static int SIZEOF;
        @JniField(accessor="ws_row")
        public short ws_row;
        @JniField(accessor="ws_col")
        public short ws_col;
        @JniField(accessor="ws_xpixel")
        public short ws_xpixel;
        @JniField(accessor="ws_ypixel")
        public short ws_ypixel;

        @JniMethod(flags={MethodFlag.CONSTANT_INITIALIZER})
        private static native void init();

        public WinSize() {
        }

        public WinSize(short ws_row, short ws_col) {
            this.ws_row = ws_row;
            this.ws_col = ws_col;
        }

        static {
            LIBRARY.load();
            WinSize.init();
        }
    }
}

