/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.hawtjni.runtime;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.fusesource.hawtjni.runtime.FieldFlag;

@Documented
@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface JniField {
    public String cast() default "";

    public String accessor() default "";

    public String getter() default "";

    public String setter() default "";

    public String conditional() default "";

    public FieldFlag[] flags() default {};
}

