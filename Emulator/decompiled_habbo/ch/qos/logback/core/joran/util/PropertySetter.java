/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.util;

import ch.qos.logback.core.joran.spi.DefaultClass;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.util.StringToObjectConverter;
import ch.qos.logback.core.joran.util.beans.BeanDescription;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.joran.util.beans.BeanUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.AggregationType;
import ch.qos.logback.core.util.PropertySetterException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class PropertySetter
extends ContextAwareBase {
    protected final Object obj;
    protected final Class<?> objClass;
    protected final BeanDescription beanDescription;

    public PropertySetter(BeanDescriptionCache beanDescriptionCache, Object obj) {
        this.obj = obj;
        this.objClass = obj.getClass();
        this.beanDescription = beanDescriptionCache.getBeanDescription(this.objClass);
    }

    public void setProperty(String name, String value) {
        if (value == null) {
            return;
        }
        Method setter = this.findSetterMethod(name);
        if (setter == null) {
            this.addWarn("No setter for property [" + name + "] in " + this.objClass.getName() + ".");
        } else {
            try {
                this.setProperty(setter, name, value);
            }
            catch (PropertySetterException ex) {
                this.addWarn("Failed to set property [" + name + "] to value \"" + value + "\". ", ex);
            }
        }
    }

    private void setProperty(Method setter, String name, String value) throws PropertySetterException {
        Object arg;
        Class<?>[] paramTypes = setter.getParameterTypes();
        try {
            arg = StringToObjectConverter.convertArg(this, value, paramTypes[0]);
        }
        catch (Throwable t) {
            throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed. ", t);
        }
        if (arg == null) {
            throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed.");
        }
        try {
            setter.invoke(this.obj, arg);
        }
        catch (Exception ex) {
            throw new PropertySetterException(ex);
        }
    }

    public AggregationType computeAggregationType(String name) {
        Method setter;
        String cName = this.capitalizeFirstLetter(name);
        Method addMethod = this.findAdderMethod(cName);
        if (addMethod != null) {
            AggregationType type = this.computeRawAggregationType(addMethod);
            switch (type) {
                case NOT_FOUND: {
                    return AggregationType.NOT_FOUND;
                }
                case AS_BASIC_PROPERTY: {
                    return AggregationType.AS_BASIC_PROPERTY_COLLECTION;
                }
                case AS_COMPLEX_PROPERTY: {
                    return AggregationType.AS_COMPLEX_PROPERTY_COLLECTION;
                }
                case AS_BASIC_PROPERTY_COLLECTION: 
                case AS_COMPLEX_PROPERTY_COLLECTION: {
                    this.addError("Unexpected AggregationType " + (Object)((Object)type));
                }
            }
        }
        if ((setter = this.findSetterMethod(name)) != null) {
            return this.computeRawAggregationType(setter);
        }
        return AggregationType.NOT_FOUND;
    }

    private Method findAdderMethod(String name) {
        String propertyName = BeanUtil.toLowerCamelCase(name);
        return this.beanDescription.getAdder(propertyName);
    }

    private Method findSetterMethod(String name) {
        String propertyName = BeanUtil.toLowerCamelCase(name);
        return this.beanDescription.getSetter(propertyName);
    }

    private Class<?> getParameterClassForMethod(Method method) {
        if (method == null) {
            return null;
        }
        Class<?>[] classArray = method.getParameterTypes();
        if (classArray.length != 1) {
            return null;
        }
        return classArray[0];
    }

    private AggregationType computeRawAggregationType(Method method) {
        Class<?> parameterClass = this.getParameterClassForMethod(method);
        if (parameterClass == null) {
            return AggregationType.NOT_FOUND;
        }
        if (StringToObjectConverter.canBeBuiltFromSimpleString(parameterClass)) {
            return AggregationType.AS_BASIC_PROPERTY;
        }
        return AggregationType.AS_COMPLEX_PROPERTY;
    }

    private boolean isUnequivocallyInstantiable(Class<?> clazz) {
        if (clazz.isInterface()) {
            return false;
        }
        try {
            Object o = clazz.newInstance();
            return o != null;
        }
        catch (InstantiationException e) {
            return false;
        }
        catch (IllegalAccessException e) {
            return false;
        }
    }

    public Class<?> getObjClass() {
        return this.objClass;
    }

    public void addComplexProperty(String name, Object complexProperty) {
        Method adderMethod = this.findAdderMethod(name);
        if (adderMethod != null) {
            Class<?>[] paramTypes = adderMethod.getParameterTypes();
            if (!this.isSanityCheckSuccessful(name, adderMethod, paramTypes, complexProperty)) {
                return;
            }
            this.invokeMethodWithSingleParameterOnThisObject(adderMethod, complexProperty);
        } else {
            this.addError("Could not find method [add" + name + "] in class [" + this.objClass.getName() + "].");
        }
    }

    void invokeMethodWithSingleParameterOnThisObject(Method method, Object parameter) {
        Class<?> ccc = parameter.getClass();
        try {
            method.invoke(this.obj, parameter);
        }
        catch (Exception e) {
            this.addError("Could not invoke method " + method.getName() + " in class " + this.obj.getClass().getName() + " with parameter of type " + ccc.getName(), e);
        }
    }

    public void addBasicProperty(String name, String strValue) {
        Object arg;
        if (strValue == null) {
            return;
        }
        Method adderMethod = this.findAdderMethod(name = this.capitalizeFirstLetter(name));
        if (adderMethod == null) {
            this.addError("No adder for property [" + name + "].");
            return;
        }
        Class<?>[] paramTypes = adderMethod.getParameterTypes();
        this.isSanityCheckSuccessful(name, adderMethod, paramTypes, strValue);
        try {
            arg = StringToObjectConverter.convertArg(this, strValue, paramTypes[0]);
        }
        catch (Throwable t) {
            this.addError("Conversion to type [" + paramTypes[0] + "] failed. ", t);
            return;
        }
        if (arg != null) {
            this.invokeMethodWithSingleParameterOnThisObject(adderMethod, strValue);
        }
    }

    public void setComplexProperty(String name, Object complexProperty) {
        Method setter = this.findSetterMethod(name);
        if (setter == null) {
            this.addWarn("Not setter method for property [" + name + "] in " + this.obj.getClass().getName());
            return;
        }
        Class<?>[] paramTypes = setter.getParameterTypes();
        if (!this.isSanityCheckSuccessful(name, setter, paramTypes, complexProperty)) {
            return;
        }
        try {
            this.invokeMethodWithSingleParameterOnThisObject(setter, complexProperty);
        }
        catch (Exception e) {
            this.addError("Could not set component " + this.obj + " for parent component " + this.obj, e);
        }
    }

    private boolean isSanityCheckSuccessful(String name, Method method, Class<?>[] params, Object complexProperty) {
        Class<?> ccc = complexProperty.getClass();
        if (params.length != 1) {
            this.addError("Wrong number of parameters in setter method for property [" + name + "] in " + this.obj.getClass().getName());
            return false;
        }
        if (!params[0].isAssignableFrom(complexProperty.getClass())) {
            this.addError("A \"" + ccc.getName() + "\" object is not assignable to a \"" + params[0].getName() + "\" variable.");
            this.addError("The class \"" + params[0].getName() + "\" was loaded by ");
            this.addError("[" + params[0].getClassLoader() + "] whereas object of type ");
            this.addError("\"" + ccc.getName() + "\" was loaded by [" + ccc.getClassLoader() + "].");
            return false;
        }
        return true;
    }

    private String capitalizeFirstLetter(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public Object getObj() {
        return this.obj;
    }

    Method getRelevantMethod(String name, AggregationType aggregationType) {
        Method relevantMethod;
        if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY_COLLECTION) {
            relevantMethod = this.findAdderMethod(name);
        } else if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY) {
            relevantMethod = this.findSetterMethod(name);
        } else {
            throw new IllegalStateException((Object)((Object)aggregationType) + " not allowed here");
        }
        return relevantMethod;
    }

    <T extends Annotation> T getAnnotation(String name, Class<T> annonationClass, Method relevantMethod) {
        if (relevantMethod != null) {
            return relevantMethod.getAnnotation(annonationClass);
        }
        return null;
    }

    Class<?> getDefaultClassNameByAnnonation(String name, Method relevantMethod) {
        DefaultClass defaultClassAnnon = this.getAnnotation(name, DefaultClass.class, relevantMethod);
        if (defaultClassAnnon != null) {
            return defaultClassAnnon.value();
        }
        return null;
    }

    Class<?> getByConcreteType(String name, Method relevantMethod) {
        Class<?> paramType = this.getParameterClassForMethod(relevantMethod);
        if (paramType == null) {
            return null;
        }
        boolean isUnequivocallyInstantiable = this.isUnequivocallyInstantiable(paramType);
        if (isUnequivocallyInstantiable) {
            return paramType;
        }
        return null;
    }

    public Class<?> getClassNameViaImplicitRules(String name, AggregationType aggregationType, DefaultNestedComponentRegistry registry) {
        Class<?> registryResult = registry.findDefaultComponentType(this.obj.getClass(), name);
        if (registryResult != null) {
            return registryResult;
        }
        Method relevantMethod = this.getRelevantMethod(name, aggregationType);
        if (relevantMethod == null) {
            return null;
        }
        Class<?> byAnnotation = this.getDefaultClassNameByAnnonation(name, relevantMethod);
        if (byAnnotation != null) {
            return byAnnotation;
        }
        return this.getByConcreteType(name, relevantMethod);
    }
}

