/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.hawtjni.runtime;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.fusesource.hawtjni.runtime.ArgFlag;

@Documented
@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface JniArg {
    public ArgFlag[] flags() default {};

    public String cast() default "";
}

