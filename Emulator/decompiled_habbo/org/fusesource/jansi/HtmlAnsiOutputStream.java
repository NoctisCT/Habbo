/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.fusesource.jansi.AnsiOutputStream;

public class HtmlAnsiOutputStream
extends AnsiOutputStream {
    private boolean concealOn = false;
    private static final String[] ANSI_COLOR_MAP = new String[]{"black", "red", "green", "yellow", "blue", "magenta", "cyan", "white"};
    private static final byte[] BYTES_QUOT = "&quot;".getBytes();
    private static final byte[] BYTES_AMP = "&amp;".getBytes();
    private static final byte[] BYTES_LT = "&lt;".getBytes();
    private static final byte[] BYTES_GT = "&gt;".getBytes();
    private final List<String> closingAttributes = new ArrayList<String>();

    @Override
    public void close() throws IOException {
        this.closeAttributes();
        super.close();
    }

    public HtmlAnsiOutputStream(OutputStream os) {
        super(os);
    }

    private void write(String s) throws IOException {
        this.out.write(s.getBytes());
    }

    private void writeAttribute(String s) throws IOException {
        this.write("<" + s + ">");
        this.closingAttributes.add(0, s.split(" ", 2)[0]);
    }

    private void closeAttributes() throws IOException {
        for (String attr : this.closingAttributes) {
            this.write("</" + attr + ">");
        }
        this.closingAttributes.clear();
    }

    @Override
    public void write(int data) throws IOException {
        switch (data) {
            case 34: {
                this.out.write(BYTES_QUOT);
                break;
            }
            case 38: {
                this.out.write(BYTES_AMP);
                break;
            }
            case 60: {
                this.out.write(BYTES_LT);
                break;
            }
            case 62: {
                this.out.write(BYTES_GT);
                break;
            }
            default: {
                super.write(data);
            }
        }
    }

    public void writeLine(byte[] buf, int offset, int len) throws IOException {
        this.write(buf, offset, len);
        this.closeAttributes();
    }

    @Override
    protected void processSetAttribute(int attribute) throws IOException {
        switch (attribute) {
            case 8: {
                this.write("\u001b[8m");
                this.concealOn = true;
                break;
            }
            case 1: {
                this.writeAttribute("b");
                break;
            }
            case 22: {
                this.closeAttributes();
                break;
            }
            case 4: {
                this.writeAttribute("u");
                break;
            }
            case 24: {
                this.closeAttributes();
                break;
            }
            case 7: {
                break;
            }
            case 27: {
                break;
            }
        }
    }

    @Override
    protected void processAttributeRest() throws IOException {
        if (this.concealOn) {
            this.write("\u001b[0m");
            this.concealOn = false;
        }
        this.closeAttributes();
    }

    @Override
    protected void processSetForegroundColor(int color, boolean bright) throws IOException {
        this.writeAttribute("span style=\"color: " + ANSI_COLOR_MAP[color] + ";\"");
    }

    @Override
    protected void processSetBackgroundColor(int color, boolean bright) throws IOException {
        this.writeAttribute("span style=\"background-color: " + ANSI_COLOR_MAP[color] + ";\"");
    }
}

