/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.ThrowableProxyVO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

public class LoggingEventVO
implements ILoggingEvent,
Serializable {
    private static final long serialVersionUID = 6553722650255690312L;
    private static final int NULL_ARGUMENT_ARRAY = -1;
    private static final String NULL_ARGUMENT_ARRAY_ELEMENT = "NULL_ARGUMENT_ARRAY_ELEMENT";
    private String threadName;
    private String loggerName;
    private LoggerContextVO loggerContextVO;
    private transient Level level;
    private String message;
    private transient String formattedMessage;
    private transient Object[] argumentArray;
    private ThrowableProxyVO throwableProxy;
    private StackTraceElement[] callerDataArray;
    private Marker marker;
    private Map<String, String> mdcPropertyMap;
    private long timeStamp;

    public static LoggingEventVO build(ILoggingEvent le) {
        LoggingEventVO ledo = new LoggingEventVO();
        ledo.loggerName = le.getLoggerName();
        ledo.loggerContextVO = le.getLoggerContextVO();
        ledo.threadName = le.getThreadName();
        ledo.level = le.getLevel();
        ledo.message = le.getMessage();
        ledo.argumentArray = le.getArgumentArray();
        ledo.marker = le.getMarker();
        ledo.mdcPropertyMap = le.getMDCPropertyMap();
        ledo.timeStamp = le.getTimeStamp();
        ledo.throwableProxy = ThrowableProxyVO.build(le.getThrowableProxy());
        if (le.hasCallerData()) {
            ledo.callerDataArray = le.getCallerData();
        }
        return ledo;
    }

    @Override
    public String getThreadName() {
        return this.threadName;
    }

    @Override
    public LoggerContextVO getLoggerContextVO() {
        return this.loggerContextVO;
    }

    @Override
    public String getLoggerName() {
        return this.loggerName;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getFormattedMessage() {
        if (this.formattedMessage != null) {
            return this.formattedMessage;
        }
        this.formattedMessage = this.argumentArray != null ? MessageFormatter.arrayFormat(this.message, this.argumentArray).getMessage() : this.message;
        return this.formattedMessage;
    }

    @Override
    public Object[] getArgumentArray() {
        return this.argumentArray;
    }

    @Override
    public IThrowableProxy getThrowableProxy() {
        return this.throwableProxy;
    }

    @Override
    public StackTraceElement[] getCallerData() {
        return this.callerDataArray;
    }

    @Override
    public boolean hasCallerData() {
        return this.callerDataArray != null;
    }

    @Override
    public Marker getMarker() {
        return this.marker;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public long getContextBirthTime() {
        return this.loggerContextVO.getBirthTime();
    }

    public LoggerContextVO getContextLoggerRemoteView() {
        return this.loggerContextVO;
    }

    @Override
    public Map<String, String> getMDCPropertyMap() {
        return this.mdcPropertyMap;
    }

    @Override
    public Map<String, String> getMdc() {
        return this.mdcPropertyMap;
    }

    @Override
    public void prepareForDeferredProcessing() {
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.level.levelInt);
        if (this.argumentArray != null) {
            int len = this.argumentArray.length;
            out.writeInt(len);
            for (int i = 0; i < this.argumentArray.length; ++i) {
                if (this.argumentArray[i] != null) {
                    out.writeObject(this.argumentArray[i].toString());
                    continue;
                }
                out.writeObject(NULL_ARGUMENT_ARRAY_ELEMENT);
            }
        } else {
            out.writeInt(-1);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int levelInt = in.readInt();
        this.level = Level.toLevel(levelInt);
        int argArrayLen = in.readInt();
        if (argArrayLen != -1) {
            this.argumentArray = new String[argArrayLen];
            for (int i = 0; i < argArrayLen; ++i) {
                Object val = in.readObject();
                if (NULL_ARGUMENT_ARRAY_ELEMENT.equals(val)) continue;
                this.argumentArray[i] = val;
            }
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.message == null ? 0 : this.message.hashCode());
        result = 31 * result + (this.threadName == null ? 0 : this.threadName.hashCode());
        result = 31 * result + (int)(this.timeStamp ^ this.timeStamp >>> 32);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        LoggingEventVO other = (LoggingEventVO)obj;
        if (this.message == null ? other.message != null : !this.message.equals(other.message)) {
            return false;
        }
        if (this.loggerName == null ? other.loggerName != null : !this.loggerName.equals(other.loggerName)) {
            return false;
        }
        if (this.threadName == null ? other.threadName != null : !this.threadName.equals(other.threadName)) {
            return false;
        }
        if (this.timeStamp != other.timeStamp) {
            return false;
        }
        if (this.marker == null ? other.marker != null : !this.marker.equals(other.marker)) {
            return false;
        }
        return !(this.mdcPropertyMap == null ? other.mdcPropertyMap != null : !this.mdcPropertyMap.equals(other.mdcPropertyMap));
    }
}

