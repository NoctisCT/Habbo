/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.Closure
 *  groovy.lang.GroovyObject
 *  groovy.lang.MetaClass
 *  groovy.lang.MetaProperty
 *  org.codehaus.groovy.reflection.ClassInfo
 *  org.codehaus.groovy.runtime.BytecodeInterface8
 *  org.codehaus.groovy.runtime.GStringImpl
 *  org.codehaus.groovy.runtime.GeneratedClosure
 *  org.codehaus.groovy.runtime.ScriptBytecodeAdapter
 *  org.codehaus.groovy.runtime.callsite.CallSite
 *  org.codehaus.groovy.runtime.callsite.CallSiteArray
 *  org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation
 *  org.codehaus.groovy.runtime.typehandling.ShortTypeHandling
 */
package ch.qos.logback.classic.gaffer;

import ch.qos.logback.classic.gaffer.NestingType;
import ch.qos.logback.core.joran.util.StringToObjectConverter;
import ch.qos.logback.core.joran.util.beans.BeanUtil;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class PropertyUtil
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public PropertyUtil() {
        MetaClass metaClass;
        CallSite[] callSiteArray = PropertyUtil.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static boolean hasAdderMethod(Object obj, String name) {
        CallSite[] callSiteArray = PropertyUtil.$getCallSiteArray();
        String addMethod = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            GStringImpl gStringImpl = new GStringImpl(new Object[]{callSiteArray[0].callStatic(PropertyUtil.class, (Object)name)}, new String[]{"add", ""});
            addMethod = ShortTypeHandling.castToString((Object)gStringImpl);
        } else {
            GStringImpl gStringImpl = new GStringImpl(new Object[]{PropertyUtil.upperCaseFirstLetter(name)}, new String[]{"add", ""});
            addMethod = ShortTypeHandling.castToString((Object)gStringImpl);
        }
        return DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[1].call(callSiteArray[2].callGetProperty(obj), obj, (Object)addMethod));
    }

    public static NestingType nestingType(Object obj, String name, Object value) {
        CallSite[] callSiteArray = PropertyUtil.$getCallSiteArray();
        Object decapitalizedName = callSiteArray[3].call(BeanUtil.class, (Object)name);
        MetaProperty metaProperty = (MetaProperty)ScriptBytecodeAdapter.castToType((Object)callSiteArray[4].call(obj, decapitalizedName), MetaProperty.class);
        if (ScriptBytecodeAdapter.compareNotEqual((Object)metaProperty, null)) {
            boolean VALUE_IS_A_STRING = value instanceof String;
            if (VALUE_IS_A_STRING && DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[5].call(StringToObjectConverter.class, callSiteArray[6].call((Object)metaProperty)))) {
                return (NestingType)ShortTypeHandling.castToEnum((Object)callSiteArray[7].callGetProperty(NestingType.class), NestingType.class);
            }
            return (NestingType)ShortTypeHandling.castToEnum((Object)callSiteArray[8].callGetProperty(NestingType.class), NestingType.class);
        }
        if (DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[9].callStatic(PropertyUtil.class, obj, (Object)name))) {
            return (NestingType)ShortTypeHandling.castToEnum((Object)callSiteArray[10].callGetProperty(NestingType.class), NestingType.class);
        }
        return (NestingType)ShortTypeHandling.castToEnum((Object)callSiteArray[11].callGetProperty(NestingType.class), NestingType.class);
    }

    public static Object convertByValueMethod(Object component, String name, String value) {
        CallSite[] callSiteArray = PropertyUtil.$getCallSiteArray();
        Object decapitalizedName = callSiteArray[12].call(BeanUtil.class, (Object)name);
        MetaProperty metaProperty = (MetaProperty)ScriptBytecodeAdapter.castToType((Object)callSiteArray[13].call(component, decapitalizedName), MetaProperty.class);
        Method valueOfMethod = (Method)ScriptBytecodeAdapter.castToType((Object)callSiteArray[14].call(StringToObjectConverter.class, callSiteArray[15].call((Object)metaProperty)), Method.class);
        return callSiteArray[16].call((Object)valueOfMethod, null, (Object)value);
    }

    public static void attach(NestingType nestingType, Object component, Object subComponent, String name) {
        block1: {
            CallSite[] callSiteArray;
            NestingType nestingType2;
            block2: {
                block0: {
                    nestingType2 = nestingType;
                    callSiteArray = PropertyUtil.$getCallSiteArray();
                    if (!ScriptBytecodeAdapter.isCase((Object)((Object)nestingType2), (Object)callSiteArray[17].callGetProperty(NestingType.class))) break block0;
                    Object object = callSiteArray[18].call(BeanUtil.class, (Object)name);
                    name = ShortTypeHandling.castToString((Object)object);
                    Object value = callSiteArray[19].callStatic(PropertyUtil.class, component, (Object)name, subComponent);
                    Object object2 = value;
                    ScriptBytecodeAdapter.setProperty((Object)object2, null, (Object)component, (String)ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{name}, new String[]{"", ""})));
                    break block1;
                }
                if (!ScriptBytecodeAdapter.isCase((Object)((Object)nestingType2), (Object)callSiteArray[20].callGetProperty(NestingType.class))) break block2;
                Object object = callSiteArray[21].call(BeanUtil.class, (Object)name);
                name = ShortTypeHandling.castToString((Object)object);
                Object object3 = subComponent;
                ScriptBytecodeAdapter.setProperty((Object)object3, null, (Object)component, (String)ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{name}, new String[]{"", ""})));
                break block1;
            }
            if (!ScriptBytecodeAdapter.isCase((Object)((Object)nestingType2), (Object)callSiteArray[22].callGetProperty(NestingType.class))) break block1;
            String firstUpperName = ShortTypeHandling.castToString((Object)callSiteArray[23].call(PropertyUtil.class, (Object)name));
            ScriptBytecodeAdapter.invokeMethodN(PropertyUtil.class, (Object)component, (String)ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{firstUpperName}, new String[]{"add", ""})), (Object[])new Object[]{subComponent});
        }
    }

    public static String transformFirstLetter(String s, Closure closure) {
        CallSite[] callSiteArray = PropertyUtil.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? ScriptBytecodeAdapter.compareEqual((Object)s, null) || ScriptBytecodeAdapter.compareEqual((Object)callSiteArray[24].call((Object)s), (Object)0) : ScriptBytecodeAdapter.compareEqual((Object)s, null) || ScriptBytecodeAdapter.compareEqual((Object)callSiteArray[25].call((Object)s), (Object)0)) {
            return s;
        }
        String firstLetter = ShortTypeHandling.castToString((Object)callSiteArray[26].callConstructor(String.class, callSiteArray[27].call((Object)s, (Object)0)));
        String modifiedFistLetter = ShortTypeHandling.castToString((Object)callSiteArray[28].call((Object)closure, (Object)firstLetter));
        if (ScriptBytecodeAdapter.compareEqual((Object)callSiteArray[29].call((Object)s), (Object)1)) {
            return modifiedFistLetter;
        }
        return ShortTypeHandling.castToString((Object)callSiteArray[30].call((Object)modifiedFistLetter, callSiteArray[31].call((Object)s, (Object)1)));
    }

    public static String upperCaseFirstLetter(String s) {
        CallSite[] callSiteArray = PropertyUtil.$getCallSiteArray();
        return ShortTypeHandling.castToString((Object)callSiteArray[32].callStatic(PropertyUtil.class, (Object)s, (Object)new GeneratedClosure(PropertyUtil.class, PropertyUtil.class){
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                CallSite[] callSiteArray = _upperCaseFirstLetter_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(String it) {
                CallSite[] callSiteArray = _upperCaseFirstLetter_closure1.$getCallSiteArray();
                return callSiteArray[0].call((Object)it);
            }

            public Object call(String it) {
                CallSite[] callSiteArray = _upperCaseFirstLetter_closure1.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[1].callCurrent((GroovyObject)this, (Object)it);
                }
                return this.doCall(it);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (((Object)((Object)this)).getClass() != _upperCaseFirstLetter_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass((Object)((Object)this));
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(((Object)((Object)this)).getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "toUpperCase";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _upperCaseFirstLetter_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_upperCaseFirstLetter_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _upperCaseFirstLetter_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != PropertyUtil.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty((Object)this, string);
    }

    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty((Object)this, string, object);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "upperCaseFirstLetter";
        stringArray[1] = "respondsTo";
        stringArray[2] = "metaClass";
        stringArray[3] = "toLowerCamelCase";
        stringArray[4] = "hasProperty";
        stringArray[5] = "followsTheValueOfConvention";
        stringArray[6] = "getType";
        stringArray[7] = "SINGLE_WITH_VALUE_OF_CONVENTION";
        stringArray[8] = "SINGLE";
        stringArray[9] = "hasAdderMethod";
        stringArray[10] = "AS_COLLECTION";
        stringArray[11] = "NA";
        stringArray[12] = "toLowerCamelCase";
        stringArray[13] = "hasProperty";
        stringArray[14] = "getValueOfMethod";
        stringArray[15] = "getType";
        stringArray[16] = "invoke";
        stringArray[17] = "SINGLE_WITH_VALUE_OF_CONVENTION";
        stringArray[18] = "toLowerCamelCase";
        stringArray[19] = "convertByValueMethod";
        stringArray[20] = "SINGLE";
        stringArray[21] = "toLowerCamelCase";
        stringArray[22] = "AS_COLLECTION";
        stringArray[23] = "upperCaseFirstLetter";
        stringArray[24] = "length";
        stringArray[25] = "length";
        stringArray[26] = "<$constructor$>";
        stringArray[27] = "getAt";
        stringArray[28] = "call";
        stringArray[29] = "length";
        stringArray[30] = "plus";
        stringArray[31] = "substring";
        stringArray[32] = "transformFirstLetter";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[33];
        PropertyUtil.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(PropertyUtil.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = PropertyUtil.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

