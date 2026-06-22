/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.hawtjni.runtime;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.fusesource.hawtjni.runtime.ClassFlag;

@Documented
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface JniClass {
    public ClassFlag[] flags() default {};

    public String conditional() default "";

    public String name() default "";
}

