/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.GroovyObject
 *  groovy.lang.MetaClass
 *  org.codehaus.groovy.reflection.ClassInfo
 *  org.codehaus.groovy.runtime.ScriptBytecodeAdapter
 *  org.codehaus.groovy.runtime.callsite.CallSite
 *  org.codehaus.groovy.runtime.callsite.CallSiteArray
 *  org.codehaus.groovy.runtime.typehandling.ShortTypeHandling
 *  org.codehaus.groovy.transform.ImmutableASTTransformation
 */
package ch.qos.logback.classic.gaffer;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.transform.ImmutableASTTransformation;

public final class NestingType
extends Enum<NestingType>
implements GroovyObject {
    public static final /* enum */ NestingType NA;
    public static final /* enum */ NestingType SINGLE;
    public static final /* enum */ NestingType SINGLE_WITH_VALUE_OF_CONVENTION;
    public static final /* enum */ NestingType AS_COLLECTION;
    public static final NestingType MIN_VALUE;
    public static final NestingType MAX_VALUE;
    private static final /* synthetic */ NestingType[] $VALUES;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public NestingType(LinkedHashMap __namedArgs) {
        MetaClass metaClass;
        CallSite[] callSiteArray = NestingType.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        if (ScriptBytecodeAdapter.compareEqual((Object)__namedArgs, null)) {
            throw (Throwable)callSiteArray[0].callConstructor(IllegalArgumentException.class, (Object)"One of the enum constants for enum ch.qos.logback.classic.gaffer.NestingType was initialized with null. Please use a non-null value or define your own constructor.");
        }
        callSiteArray[1].callStatic(ImmutableASTTransformation.class, (Object)this, (Object)__namedArgs);
    }

    public NestingType() {
        CallSite[] callSiteArray = NestingType.$getCallSiteArray();
        this((LinkedHashMap)ScriptBytecodeAdapter.castToType((Object)callSiteArray[2].callConstructor(LinkedHashMap.class), LinkedHashMap.class));
    }

    public static final NestingType[] values() {
        CallSite[] callSiteArray = NestingType.$getCallSiteArray();
        return (NestingType[])ScriptBytecodeAdapter.castToType((Object)$VALUES.clone(), NestingType[].class);
    }

    public /* synthetic */ NestingType next() {
        CallSite[] callSiteArray = NestingType.$getCallSiteArray();
        Object ordinal = callSiteArray[3].call(callSiteArray[4].callCurrent((GroovyObject)this));
        if (ScriptBytecodeAdapter.compareGreaterThanEqual((Object)ordinal, (Object)callSiteArray[5].call((Object)$VALUES))) {
            Integer n = 0;
            ordinal = n;
        }
        return (NestingType)ShortTypeHandling.castToEnum((Object)callSiteArray[6].call((Object)$VALUES, ordinal), NestingType.class);
    }

    public /* synthetic */ NestingType previous() {
        CallSite[] callSiteArray = NestingType.$getCallSiteArray();
        Object ordinal = callSiteArray[7].call(callSiteArray[8].callCurrent((GroovyObject)this));
        if (ScriptBytecodeAdapter.compareLessThan((Object)ordinal, (Object)0)) {
            Object object;
            ordinal = object = callSiteArray[9].call(callSiteArray[10].call((Object)$VALUES), (Object)1);
        }
        return (NestingType)ShortTypeHandling.castToEnum((Object)callSiteArray[11].call((Object)$VALUES, ordinal), NestingType.class);
    }

    public static NestingType valueOf(String name) {
        CallSite[] callSiteArray = NestingType.$getCallSiteArray();
        return (NestingType)ShortTypeHandling.castToEnum((Object)callSiteArray[12].callStatic(NestingType.class, NestingType.class, (Object)name), NestingType.class);
    }

    public static final /* synthetic */ NestingType $INIT(Object ... para) {
        NestingType nestingType;
        CallSite[] callSiteArray = NestingType.$getCallSiteArray();
        Object[] objectArray = ScriptBytecodeAdapter.despreadList((Object[])new Object[0], (Object[])new Object[]{para}, (int[])new int[]{0});
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments((Object[])objectArray, (int)-1, NestingType.class)) {
            case -1348271900: {
                NestingType nestingType2;
                nestingType = nestingType2;
                Object[] objectArray2 = objectArray;
                nestingType2 = new NestingType();
                break;
            }
            case -242181752: {
                NestingType nestingType2;
                nestingType = nestingType2;
                Object[] objectArray2 = objectArray;
                nestingType2 = new NestingType((LinkedHashMap)ScriptBytecodeAdapter.castToType((Object)objectArray[2], LinkedHashMap.class));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
        return nestingType;
    }

    static {
        NestingType nestingType;
        NestingType nestingType2;
        Object object = NestingType.$getCallSiteArray()[13].callStatic(NestingType.class, (Object)"NA", (Object)0);
        NA = (NestingType)ShortTypeHandling.castToEnum((Object)object, NestingType.class);
        Object object2 = NestingType.$getCallSiteArray()[14].callStatic(NestingType.class, (Object)"SINGLE", (Object)1);
        SINGLE = (NestingType)ShortTypeHandling.castToEnum((Object)object2, NestingType.class);
        Object object3 = NestingType.$getCallSiteArray()[15].callStatic(NestingType.class, (Object)"SINGLE_WITH_VALUE_OF_CONVENTION", (Object)2);
        SINGLE_WITH_VALUE_OF_CONVENTION = (NestingType)ShortTypeHandling.castToEnum((Object)object3, NestingType.class);
        Object object4 = NestingType.$getCallSiteArray()[16].callStatic(NestingType.class, (Object)"AS_COLLECTION", (Object)3);
        AS_COLLECTION = (NestingType)ShortTypeHandling.castToEnum((Object)object4, NestingType.class);
        MIN_VALUE = nestingType2 = NA;
        MAX_VALUE = nestingType = AS_COLLECTION;
        NestingType[] nestingTypeArray = new NestingType[]{NA, SINGLE, SINGLE_WITH_VALUE_OF_CONVENTION, AS_COLLECTION};
        $VALUES = nestingTypeArray;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (((Object)((Object)this)).getClass() != NestingType.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)((Object)this));
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(((Object)((Object)this)).getClass());
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
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "checkPropNames";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "next";
        stringArray[4] = "ordinal";
        stringArray[5] = "size";
        stringArray[6] = "getAt";
        stringArray[7] = "previous";
        stringArray[8] = "ordinal";
        stringArray[9] = "minus";
        stringArray[10] = "size";
        stringArray[11] = "getAt";
        stringArray[12] = "valueOf";
        stringArray[13] = "$INIT";
        stringArray[14] = "$INIT";
        stringArray[15] = "$INIT";
        stringArray[16] = "$INIT";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[17];
        NestingType.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(NestingType.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = NestingType.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

