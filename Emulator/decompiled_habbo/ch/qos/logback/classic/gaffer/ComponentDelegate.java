/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.Closure
 *  groovy.lang.GroovyObject
 *  groovy.lang.MetaClass
 *  org.codehaus.groovy.reflection.ClassInfo
 *  org.codehaus.groovy.runtime.BytecodeInterface8
 *  org.codehaus.groovy.runtime.GStringImpl
 *  org.codehaus.groovy.runtime.ScriptBytecodeAdapter
 *  org.codehaus.groovy.runtime.callsite.CallSite
 *  org.codehaus.groovy.runtime.callsite.CallSiteArray
 *  org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation
 *  org.codehaus.groovy.runtime.typehandling.ShortTypeHandling
 */
package ch.qos.logback.classic.gaffer;

import ch.qos.logback.classic.gaffer.NestingType;
import ch.qos.logback.classic.gaffer.PropertyUtil;
import ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ComponentDelegate
extends ContextAwareBase
implements GroovyObject {
    private final Object component;
    private final List fieldsToCascade;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    /*
     * WARNING - void declaration
     */
    public ComponentDelegate(Object object) {
        void component;
        MetaClass metaClass;
        List list;
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        this.fieldsToCascade = list = ScriptBytecodeAdapter.createList((Object[])new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        void var5_5 = component;
        this.component = var5_5;
    }

    public String getLabel() {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        return "component";
    }

    public String getLabelFistLetterInUpperCase() {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return ShortTypeHandling.castToString((Object)callSiteArray[0].call(callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].callCurrent((GroovyObject)this), (Object)0)), callSiteArray[4].call(callSiteArray[5].callCurrent((GroovyObject)this), (Object)1)));
        }
        return ShortTypeHandling.castToString((Object)callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].call((Object)this.getLabel(), (Object)0)), callSiteArray[9].call((Object)this.getLabel(), (Object)1)));
    }

    public void methodMissing(String name, Object args) {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        NestingType nestingType = (NestingType)ShortTypeHandling.castToEnum((Object)callSiteArray[10].call(PropertyUtil.class, this.component, (Object)name, null), NestingType.class);
        if (ScriptBytecodeAdapter.compareEqual((Object)((Object)nestingType), (Object)callSiteArray[11].callGetProperty(NestingType.class))) {
            callSiteArray[12].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[]{callSiteArray[13].callCurrent((GroovyObject)this), callSiteArray[14].callCurrent((GroovyObject)this), callSiteArray[15].callGetProperty(callSiteArray[16].call(this.component)), name}, new String[]{"", " ", " of type [", "] has no appplicable [", "] property."}));
            return;
        }
        String subComponentName = null;
        Class clazz = null;
        Closure closure = null;
        Object object = callSiteArray[17].callCurrent((GroovyObject)this, args);
        subComponentName = ShortTypeHandling.castToString((Object)callSiteArray[18].call(object, (Object)0));
        clazz = ShortTypeHandling.castToClass((Object)callSiteArray[19].call(object, (Object)1));
        closure = (Closure)ScriptBytecodeAdapter.castToType((Object)callSiteArray[20].call(object, (Object)2), Closure.class);
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual((Object)clazz, null)) {
                Object subComponent = callSiteArray[21].call((Object)clazz);
                if (DefaultTypeTransformation.booleanUnbox((Object)subComponentName) && DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[22].call(subComponent, (Object)name))) {
                    String string = subComponentName;
                    ScriptBytecodeAdapter.setProperty((Object)string, null, (Object)subComponent, (String)"name");
                }
                if (subComponent instanceof ContextAware) {
                    Object object2 = callSiteArray[23].callGroovyObjectGetProperty((Object)this);
                    ScriptBytecodeAdapter.setProperty((Object)object2, null, (Object)subComponent, (String)"context");
                }
                if (DefaultTypeTransformation.booleanUnbox((Object)closure)) {
                    ComponentDelegate subDelegate = (ComponentDelegate)ScriptBytecodeAdapter.castToType((Object)callSiteArray[24].callConstructor(ComponentDelegate.class, subComponent), ComponentDelegate.class);
                    callSiteArray[25].callCurrent((GroovyObject)this, (Object)subDelegate);
                    Object object3 = callSiteArray[26].callGroovyObjectGetProperty((Object)this);
                    ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object3, ComponentDelegate.class, (GroovyObject)subDelegate, (String)"context");
                    callSiteArray[27].callCurrent((GroovyObject)this, subComponent);
                    ComponentDelegate componentDelegate = subDelegate;
                    ScriptBytecodeAdapter.setGroovyObjectProperty((Object)componentDelegate, ComponentDelegate.class, (GroovyObject)closure, (String)"delegate");
                    Object object4 = callSiteArray[28].callGetProperty(Closure.class);
                    ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object4, ComponentDelegate.class, (GroovyObject)closure, (String)"resolveStrategy");
                    callSiteArray[29].call((Object)closure);
                }
                if (subComponent instanceof LifeCycle && DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[30].call(NoAutoStartUtil.class, subComponent))) {
                    callSiteArray[31].call(subComponent);
                }
                callSiteArray[32].call(PropertyUtil.class, (Object)nestingType, this.component, subComponent, (Object)name);
            } else {
                callSiteArray[33].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[]{name, callSiteArray[34].callCurrent((GroovyObject)this), callSiteArray[35].callCurrent((GroovyObject)this), callSiteArray[36].callGetProperty(callSiteArray[37].call(this.component))}, new String[]{"No 'class' argument specified for [", "] in ", " ", " of type [", "]"}));
            }
        } else if (ScriptBytecodeAdapter.compareNotEqual((Object)clazz, null)) {
            Object subComponent = callSiteArray[38].call((Object)clazz);
            if (DefaultTypeTransformation.booleanUnbox((Object)subComponentName) && DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[39].call(subComponent, (Object)name))) {
                String string = subComponentName;
                ScriptBytecodeAdapter.setProperty((Object)string, null, (Object)subComponent, (String)"name");
            }
            if (subComponent instanceof ContextAware) {
                Object object5 = callSiteArray[40].callGroovyObjectGetProperty((Object)this);
                ScriptBytecodeAdapter.setProperty((Object)object5, null, (Object)subComponent, (String)"context");
            }
            if (DefaultTypeTransformation.booleanUnbox((Object)closure)) {
                ComponentDelegate subDelegate = (ComponentDelegate)ScriptBytecodeAdapter.castToType((Object)callSiteArray[41].callConstructor(ComponentDelegate.class, subComponent), ComponentDelegate.class);
                callSiteArray[42].callCurrent((GroovyObject)this, (Object)subDelegate);
                Object object6 = callSiteArray[43].callGroovyObjectGetProperty((Object)this);
                ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object6, ComponentDelegate.class, (GroovyObject)subDelegate, (String)"context");
                callSiteArray[44].callCurrent((GroovyObject)this, subComponent);
                ComponentDelegate componentDelegate = subDelegate;
                ScriptBytecodeAdapter.setGroovyObjectProperty((Object)componentDelegate, ComponentDelegate.class, (GroovyObject)closure, (String)"delegate");
                Object object7 = callSiteArray[45].callGetProperty(Closure.class);
                ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object7, ComponentDelegate.class, (GroovyObject)closure, (String)"resolveStrategy");
                callSiteArray[46].call((Object)closure);
            }
            if (subComponent instanceof LifeCycle && DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[47].call(NoAutoStartUtil.class, subComponent))) {
                callSiteArray[48].call(subComponent);
            }
            callSiteArray[49].call(PropertyUtil.class, (Object)nestingType, this.component, subComponent, (Object)name);
        } else {
            callSiteArray[50].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[]{name, this.getLabel(), this.getComponentName(), callSiteArray[51].callGetProperty(callSiteArray[52].call(this.component))}, new String[]{"No 'class' argument specified for [", "] in ", " ", " of type [", "]"}));
        }
    }

    public void cascadeFields(ComponentDelegate subDelegate) {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        String k = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType((Object)callSiteArray[53].call((Object)this.fieldsToCascade), Iterator.class);
        while (iterator.hasNext()) {
            k = ShortTypeHandling.castToString(iterator.next());
            Object object = ScriptBytecodeAdapter.getGroovyObjectProperty(ComponentDelegate.class, (GroovyObject)this, (String)ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{k}, new String[]{"", ""})));
            ScriptBytecodeAdapter.setProperty((Object)object, null, (Object)callSiteArray[54].callGroovyObjectGetProperty((Object)subDelegate), (String)ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{k}, new String[]{"", ""})));
        }
    }

    public void injectParent(Object subComponent) {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[55].call(subComponent, (Object)"parent"))) {
            Object object = this.component;
            ScriptBytecodeAdapter.setProperty((Object)object, null, (Object)subComponent, (String)"parent");
        }
    }

    public void propertyMissing(String name, Object value) {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        NestingType nestingType = (NestingType)ShortTypeHandling.castToEnum((Object)callSiteArray[56].call(PropertyUtil.class, this.component, (Object)name, value), NestingType.class);
        if (ScriptBytecodeAdapter.compareEqual((Object)((Object)nestingType), (Object)callSiteArray[57].callGetProperty(NestingType.class))) {
            callSiteArray[58].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[]{callSiteArray[59].callCurrent((GroovyObject)this), callSiteArray[60].callCurrent((GroovyObject)this), callSiteArray[61].callGetProperty(callSiteArray[62].call(this.component)), name}, new String[]{"", " ", " of type [", "] has no appplicable [", "] property "}));
            return;
        }
        callSiteArray[63].call(PropertyUtil.class, (Object)nestingType, this.component, value, (Object)name);
    }

    public Object analyzeArgs(Object ... args) {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        String name = null;
        Class clazz = null;
        Closure closure = null;
        if (ScriptBytecodeAdapter.compareGreaterThan((Object)callSiteArray[64].call((Object)args), (Object)3)) {
            callSiteArray[65].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[]{args}, new String[]{"At most 3 arguments allowed but you passed ", ""}));
            return ScriptBytecodeAdapter.createList((Object[])new Object[]{name, clazz, closure});
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (callSiteArray[66].call((Object)args, (Object)-1) instanceof Closure) {
                Object object = callSiteArray[67].call((Object)args, (Object)-1);
                closure = (Closure)ScriptBytecodeAdapter.castToType((Object)object, Closure.class);
                args = (Object[])ScriptBytecodeAdapter.castToType((Object)callSiteArray[68].call((Object)args, callSiteArray[69].call((Object)args, (Object)-1)), Object[].class);
            }
        } else if (BytecodeInterface8.objectArrayGet((Object[])args, (int)-1) instanceof Closure) {
            Object object = BytecodeInterface8.objectArrayGet((Object[])args, (int)-1);
            closure = (Closure)ScriptBytecodeAdapter.castToType((Object)object, Closure.class);
            args = (Object[])ScriptBytecodeAdapter.castToType((Object)callSiteArray[70].call((Object)args, BytecodeInterface8.objectArrayGet((Object[])args, (int)-1)), Object[].class);
        }
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)callSiteArray[71].call((Object)args), (Object)1)) {
                Object object = callSiteArray[72].callCurrent((GroovyObject)this, callSiteArray[73].call((Object)args, (Object)0));
                clazz = ShortTypeHandling.castToClass((Object)object);
            }
        } else if (ScriptBytecodeAdapter.compareEqual((Object)callSiteArray[74].call((Object)args), (Object)1)) {
            Object object = callSiteArray[75].callCurrent((GroovyObject)this, BytecodeInterface8.objectArrayGet((Object[])args, (int)0));
            clazz = ShortTypeHandling.castToClass((Object)object);
        }
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)callSiteArray[76].call((Object)args), (Object)2)) {
                Object object = callSiteArray[77].callCurrent((GroovyObject)this, callSiteArray[78].call((Object)args, (Object)0));
                name = ShortTypeHandling.castToString((Object)object);
                Object object2 = callSiteArray[79].callCurrent((GroovyObject)this, callSiteArray[80].call((Object)args, (Object)1));
                clazz = ShortTypeHandling.castToClass((Object)object2);
            }
        } else if (ScriptBytecodeAdapter.compareEqual((Object)callSiteArray[81].call((Object)args), (Object)2)) {
            Object object = callSiteArray[82].callCurrent((GroovyObject)this, BytecodeInterface8.objectArrayGet((Object[])args, (int)0));
            name = ShortTypeHandling.castToString((Object)object);
            Object object3 = callSiteArray[83].callCurrent((GroovyObject)this, BytecodeInterface8.objectArrayGet((Object[])args, (int)1));
            clazz = ShortTypeHandling.castToClass((Object)object3);
        }
        return ScriptBytecodeAdapter.createList((Object[])new Object[]{name, clazz, closure});
    }

    public Class parseClassArgument(Object arg) {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        if (arg instanceof Class) {
            return ShortTypeHandling.castToClass((Object)arg);
        }
        if (arg instanceof String) {
            return Class.forName(ShortTypeHandling.castToString((Object)arg));
        }
        callSiteArray[84].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[]{callSiteArray[85].callGetProperty(callSiteArray[86].call(arg))}, new String[]{"Unexpected argument type ", ""}));
        return ShortTypeHandling.castToClass(null);
    }

    public String parseNameArgument(Object arg) {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        if (arg instanceof String) {
            return ShortTypeHandling.castToString((Object)arg);
        }
        callSiteArray[87].callCurrent((GroovyObject)this, (Object)"With 2 or 3 arguments, the first argument must be the component name, i.e of type string");
        return ShortTypeHandling.castToString(null);
    }

    public String getComponentName() {
        CallSite[] callSiteArray = ComponentDelegate.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[88].call(this.component, (Object)"name"))) {
            return ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{callSiteArray[89].callGetProperty(this.component)}, new String[]{"[", "]"}));
        }
        return "";
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ComponentDelegate.class) {
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

    public final Object getComponent() {
        return this.component;
    }

    public final List getFieldsToCascade() {
        return this.fieldsToCascade;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "plus";
        stringArray[1] = "toUpperCase";
        stringArray[2] = "getAt";
        stringArray[3] = "getLabel";
        stringArray[4] = "substring";
        stringArray[5] = "getLabel";
        stringArray[6] = "plus";
        stringArray[7] = "toUpperCase";
        stringArray[8] = "getAt";
        stringArray[9] = "substring";
        stringArray[10] = "nestingType";
        stringArray[11] = "NA";
        stringArray[12] = "addError";
        stringArray[13] = "getLabelFistLetterInUpperCase";
        stringArray[14] = "getComponentName";
        stringArray[15] = "canonicalName";
        stringArray[16] = "getClass";
        stringArray[17] = "analyzeArgs";
        stringArray[18] = "getAt";
        stringArray[19] = "getAt";
        stringArray[20] = "getAt";
        stringArray[21] = "newInstance";
        stringArray[22] = "hasProperty";
        stringArray[23] = "context";
        stringArray[24] = "<$constructor$>";
        stringArray[25] = "cascadeFields";
        stringArray[26] = "context";
        stringArray[27] = "injectParent";
        stringArray[28] = "DELEGATE_FIRST";
        stringArray[29] = "call";
        stringArray[30] = "notMarkedWithNoAutoStart";
        stringArray[31] = "start";
        stringArray[32] = "attach";
        stringArray[33] = "addError";
        stringArray[34] = "getLabel";
        stringArray[35] = "getComponentName";
        stringArray[36] = "canonicalName";
        stringArray[37] = "getClass";
        stringArray[38] = "newInstance";
        stringArray[39] = "hasProperty";
        stringArray[40] = "context";
        stringArray[41] = "<$constructor$>";
        stringArray[42] = "cascadeFields";
        stringArray[43] = "context";
        stringArray[44] = "injectParent";
        stringArray[45] = "DELEGATE_FIRST";
        stringArray[46] = "call";
        stringArray[47] = "notMarkedWithNoAutoStart";
        stringArray[48] = "start";
        stringArray[49] = "attach";
        stringArray[50] = "addError";
        stringArray[51] = "canonicalName";
        stringArray[52] = "getClass";
        stringArray[53] = "iterator";
        stringArray[54] = "metaClass";
        stringArray[55] = "hasProperty";
        stringArray[56] = "nestingType";
        stringArray[57] = "NA";
        stringArray[58] = "addError";
        stringArray[59] = "getLabelFistLetterInUpperCase";
        stringArray[60] = "getComponentName";
        stringArray[61] = "canonicalName";
        stringArray[62] = "getClass";
        stringArray[63] = "attach";
        stringArray[64] = "size";
        stringArray[65] = "addError";
        stringArray[66] = "getAt";
        stringArray[67] = "getAt";
        stringArray[68] = "minus";
        stringArray[69] = "getAt";
        stringArray[70] = "minus";
        stringArray[71] = "size";
        stringArray[72] = "parseClassArgument";
        stringArray[73] = "getAt";
        stringArray[74] = "size";
        stringArray[75] = "parseClassArgument";
        stringArray[76] = "size";
        stringArray[77] = "parseNameArgument";
        stringArray[78] = "getAt";
        stringArray[79] = "parseClassArgument";
        stringArray[80] = "getAt";
        stringArray[81] = "size";
        stringArray[82] = "parseNameArgument";
        stringArray[83] = "parseClassArgument";
        stringArray[84] = "addError";
        stringArray[85] = "canonicalName";
        stringArray[86] = "getClass";
        stringArray[87] = "addError";
        stringArray[88] = "hasProperty";
        stringArray[89] = "name";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[90];
        ComponentDelegate.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ComponentDelegate.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ComponentDelegate.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

