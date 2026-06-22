/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class FilterPrintStream
extends PrintStream {
    private static final String NEWLINE = System.getProperty("line.separator");
    protected final PrintStream ps;

    public FilterPrintStream(PrintStream ps) {
        super(new OutputStream(){

            @Override
            public void write(int b) throws IOException {
                throw new RuntimeException("Direct OutputStream use forbidden: must go through delegate PrintStream");
            }
        });
        this.ps = ps;
    }

    protected boolean filter(int data) {
        return true;
    }

    @Override
    public void write(int data) {
        if (this.filter(data)) {
            this.ps.write(data);
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        for (int i = 0; i < len; ++i) {
            this.write(buf[off + i]);
        }
    }

    @Override
    public boolean checkError() {
        return super.checkError() || this.ps.checkError();
    }

    @Override
    public void close() {
        super.close();
        this.ps.close();
    }

    @Override
    public void flush() {
        super.flush();
        this.ps.flush();
    }

    private void write(char[] buf) {
        for (char c : buf) {
            if (!this.filter(c)) continue;
            this.ps.print(c);
        }
    }

    private void write(String s) {
        char[] buf = new char[s.length()];
        s.getChars(0, s.length(), buf, 0);
        this.write(buf);
    }

    private void newLine() {
        this.write(NEWLINE);
    }

    @Override
    public void print(boolean b) {
        this.write(b ? "true" : "false");
    }

    @Override
    public void print(char c) {
        this.write(String.valueOf(c));
    }

    @Override
    public void print(int i) {
        this.write(String.valueOf(i));
    }

    @Override
    public void print(long l) {
        this.write(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        this.write(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        this.write(String.valueOf(d));
    }

    @Override
    public void print(char[] s) {
        this.write(s);
    }

    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        this.write(s);
    }

    @Override
    public void print(Object obj) {
        this.write(String.valueOf(obj));
    }

    @Override
    public void println() {
        this.newLine();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void println(boolean x) {
        FilterPrintStream filterPrintStream = this;
        synchronized (filterPrintStream) {
            this.print(x);
            this.newLine();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void println(char x) {
        FilterPrintStream filterPrintStream = this;
        synchronized (filterPrintStream) {
            this.print(x);
            this.newLine();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void println(int x) {
        FilterPrintStream filterPrintStream = this;
        synchronized (filterPrintStream) {
            this.print(x);
            this.newLine();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void println(long x) {
        FilterPrintStream filterPrintStream = this;
        synchronized (filterPrintStream) {
            this.print(x);
            this.newLine();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void println(float x) {
        FilterPrintStream filterPrintStream = this;
        synchronized (filterPrintStream) {
            this.print(x);
            this.newLine();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void println(double x) {
        FilterPrintStream filterPrintStream = this;
        synchronized (filterPrintStream) {
            this.print(x);
            this.newLine();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void println(char[] x) {
        FilterPrintStream filterPrintStream = this;
        synchronized (filterPrintStream) {
            this.print(x);
            this.newLine();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void println(String x) {
        FilterPrintStream filterPrintStream = this;
        synchronized (filterPrintStream) {
            this.print(x);
            this.newLine();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void println(Object x) {
        String s = String.valueOf(x);
        FilterPrintStream filterPrintStream = this;
        synchronized (filterPrintStream) {
            this.print(s);
            this.newLine();
        }
    }
}

