/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.camera;

import com.eu.habbo.networking.camera.CameraMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;
import java.io.IOException;
import java.nio.charset.Charset;

public abstract class CameraOutgoingMessage
extends CameraMessage {
    private final ByteBufOutputStream stream;

    public CameraOutgoingMessage(short header) {
        super(header);
        this.stream = new ByteBufOutputStream(this.buffer);
        try {
            this.stream.writeInt(0);
            this.stream.writeShort(header);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void appendRawBytes(byte[] bytes) {
        try {
            this.stream.write(bytes);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void appendString(String obj) {
        try {
            byte[] data = obj.getBytes();
            this.stream.writeInt(data.length);
            this.stream.write(data);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void appendChar(int obj) {
        try {
            this.stream.writeChar(obj);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void appendChars(Object obj) {
        try {
            this.stream.writeChars(obj.toString());
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void appendInt32(Integer obj) {
        try {
            this.stream.writeInt(obj);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void appendInt32(Byte obj) {
        try {
            this.stream.writeInt(obj.byteValue());
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void appendInt32(Boolean obj) {
        try {
            this.stream.writeInt(obj != false ? 1 : 0);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void appendShort(int obj) {
        try {
            this.stream.writeShort((short)obj);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void appendByte(Integer b) {
        try {
            this.stream.writeByte(b);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void appendBoolean(Boolean obj) {
        try {
            this.stream.writeBoolean(obj);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public CameraOutgoingMessage appendResponse(CameraOutgoingMessage obj) {
        try {
            this.stream.write(obj.get().array());
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return this;
    }

    public String getBodyString() {
        ByteBuf buffer = this.stream.buffer().duplicate();
        buffer.setInt(0, buffer.writerIndex() - 4);
        String consoleText = buffer.toString(Charset.forName("UTF-8"));
        for (int i = 0; i < 14; ++i) {
            consoleText = consoleText.replace(Character.toString((char)i), "[" + i + "]");
        }
        buffer.discardSomeReadBytes();
        return consoleText;
    }

    public ByteBuf get() {
        this.buffer.setInt(0, this.buffer.writerIndex() - 4);
        return this.buffer.copy();
    }

    public abstract void compose(Channel var1);
}

