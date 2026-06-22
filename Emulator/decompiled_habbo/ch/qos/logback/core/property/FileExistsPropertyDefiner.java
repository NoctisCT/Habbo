/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.property;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;

public class FileExistsPropertyDefiner
extends PropertyDefinerBase {
    String path;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPropertyValue() {
        if (OptionHelper.isEmpty(this.path)) {
            this.addError("The \"path\" property must be set.");
            return null;
        }
        File file = new File(this.path);
        return FileExistsPropertyDefiner.booleanAsStr(file.exists());
    }
}

