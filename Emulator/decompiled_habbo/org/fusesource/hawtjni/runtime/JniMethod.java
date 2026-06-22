/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.hawtjni.runtime;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.MethodFlag;

@Documented
@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface JniMethod {
    public String cast() default "";

    public String accessor() default "";

    public MethodFlag[] flags() default {};

    public String copy() default "";

    public String conditional() default "";

    public JniArg[] callbackArgs() default {};
}

