/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.fusesource.jansi.AnsiOutputStream;

public class AnsiString
implements CharSequence {
    private final CharSequence encoded;
    private final CharSequence plain;

    public AnsiString(CharSequence str) {
        assert (str != null);
        this.encoded = str;
        this.plain = this.chew(str);
    }

    private CharSequence chew(CharSequence str) {
        assert (str != null);
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        AnsiOutputStream out = new AnsiOutputStream(buff);
        try {
            out.write(str.toString().getBytes());
            out.flush();
            out.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(buff.toByteArray());
    }

    public CharSequence getEncoded() {
        return this.encoded;
    }

    public CharSequence getPlain() {
        return this.plain;
    }

    @Override
    public char charAt(int index) {
        return this.getEncoded().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.getEncoded().subSequence(start, end);
    }

    @Override
    public int length() {
        return this.getPlain().length();
    }

    public boolean equals(Object obj) {
        return this.getEncoded().equals(obj);
    }

    public int hashCode() {
        return this.getEncoded().hashCode();
    }

    @Override
    public String toString() {
        return this.getEncoded().toString();
    }
}

