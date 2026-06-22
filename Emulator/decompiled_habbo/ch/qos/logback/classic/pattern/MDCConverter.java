/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;

public class MDCConverter
extends ClassicConverter {
    private String key;
    private String defaultValue = "";

    @Override
    public void start() {
        String[] keyInfo = OptionHelper.extractDefaultReplacement(this.getFirstOption());
        this.key = keyInfo[0];
        if (keyInfo[1] != null) {
            this.defaultValue = keyInfo[1];
        }
        super.start();
    }

    @Override
    public void stop() {
        this.key = null;
        super.stop();
    }

    @Override
    public String convert(ILoggingEvent event) {
        Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        if (mdcPropertyMap == null) {
            return this.defaultValue;
        }
        if (this.key == null) {
            return this.outputMDCForAllKeys(mdcPropertyMap);
        }
        String value = mdcPropertyMap.get(this.key);
        if (value != null) {
            return value;
        }
        return this.defaultValue;
    }

    private String outputMDCForAllKeys(Map<String, String> mdcPropertyMap) {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : mdcPropertyMap.entrySet()) {
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return buf.toString();
    }
}

