/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin;

import com.eu.habbo.plugin.EventPriority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface EventHandler {
    public EventPriority priority() default EventPriority.NORMAL;

    public boolean ignoreCancelled() default false;
}

