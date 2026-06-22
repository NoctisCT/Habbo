/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.util.beans;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.util.beans.BeanDescription;
import ch.qos.logback.core.joran.util.beans.BeanUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.lang.reflect.Method;
import java.util.HashMap;

public class BeanDescriptionFactory
extends ContextAwareBase {
    BeanDescriptionFactory(Context context) {
        this.setContext(context);
    }

    public BeanDescription create(Class<?> clazz) {
        Method[] methods;
        HashMap<String, Method> propertyNameToGetter = new HashMap<String, Method>();
        HashMap<String, Method> propertyNameToSetter = new HashMap<String, Method>();
        HashMap<String, Method> propertyNameToAdder = new HashMap<String, Method>();
        for (Method method : methods = clazz.getMethods()) {
            Method oldAdder;
            String message;
            String propertyName;
            if (method.isBridge()) continue;
            if (BeanUtil.isGetter(method)) {
                propertyName = BeanUtil.getPropertyName(method);
                Method oldGetter = propertyNameToGetter.put(propertyName, method);
                if (oldGetter == null) continue;
                if (oldGetter.getName().startsWith("is")) {
                    propertyNameToGetter.put(propertyName, oldGetter);
                }
                message = String.format("Class '%s' contains multiple getters for the same property '%s'.", clazz.getCanonicalName(), propertyName);
                this.addWarn(message);
                continue;
            }
            if (BeanUtil.isSetter(method)) {
                propertyName = BeanUtil.getPropertyName(method);
                Method oldSetter = propertyNameToSetter.put(propertyName, method);
                if (oldSetter == null) continue;
                message = String.format("Class '%s' contains multiple setters for the same property '%s'.", clazz.getCanonicalName(), propertyName);
                this.addWarn(message);
                continue;
            }
            if (!BeanUtil.isAdder(method) || (oldAdder = propertyNameToAdder.put(propertyName = BeanUtil.getPropertyName(method), method)) == null) continue;
            message = String.format("Class '%s' contains multiple adders for the same property '%s'.", clazz.getCanonicalName(), propertyName);
            this.addWarn(message);
        }
        return new BeanDescription(clazz, propertyNameToGetter, propertyNameToSetter, propertyNameToAdder);
    }
}

