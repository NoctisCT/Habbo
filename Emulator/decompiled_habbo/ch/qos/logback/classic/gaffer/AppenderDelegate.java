/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.Closure
 *  groovy.lang.MetaClass
 *  org.codehaus.groovy.reflection.ClassInfo
 *  org.codehaus.groovy.runtime.GeneratedClosure
 *  org.codehaus.groovy.runtime.ScriptBytecodeAdapter
 *  org.codehaus.groovy.runtime.callsite.CallSite
 *  org.codehaus.groovy.runtime.callsite.CallSiteArray
 *  org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation
 */
package ch.qos.logback.classic.gaffer;

import ch.qos.logback.classic.gaffer.ComponentDelegate;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachable;
import groovy.lang.Closure;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class AppenderDelegate
extends ComponentDelegate {
    private Map<String, Appender<?>> appendersByName;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AppenderDelegate(Appender appender) {
        Map map;
        CallSite[] callSiteArray = AppenderDelegate.$getCallSiteArray();
        super((Object)appender);
        this.appendersByName = map = ScriptBytecodeAdapter.createMap((Object[])new Object[0]);
    }

    public AppenderDelegate(Appender appender, List<Appender<?>> appenders) {
        Map map;
        CallSite[] callSiteArray = AppenderDelegate.$getCallSiteArray();
        super((Object)appender);
        this.appendersByName = map = ScriptBytecodeAdapter.createMap((Object[])new Object[0]);
        Object object = callSiteArray[0].call(appenders, (Object)new _closure1(this, this));
        this.appendersByName = (Map)ScriptBytecodeAdapter.castToType((Object)object, Map.class);
    }

    @Override
    public String getLabel() {
        CallSite[] callSiteArray = AppenderDelegate.$getCallSiteArray();
        return "appender";
    }

    public void appenderRef(String name) {
        CallSite[] callSiteArray = AppenderDelegate.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[1].call(AppenderAttachable.class, callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty((Object)this))))) {
            Object errorMessage = callSiteArray[4].call(callSiteArray[5].call(callSiteArray[6].call(callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(callSiteArray[9].callGroovyObjectGetProperty((Object)this))), (Object)" does not implement "), callSiteArray[10].callGetProperty(AppenderAttachable.class)), (Object)".");
            throw (Throwable)callSiteArray[11].callConstructor(IllegalArgumentException.class, errorMessage);
        }
        callSiteArray[12].call(callSiteArray[13].callGroovyObjectGetProperty((Object)this), callSiteArray[14].call(this.appendersByName, (Object)name));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AppenderDelegate.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Map<String, Appender<?>> getAppendersByName() {
        return this.appendersByName;
    }

    public void setAppendersByName(Map<String, Appender<?>> map) {
        this.appendersByName = map;
    }

    public /* synthetic */ String super$3$getLabel() {
        return super.getLabel();
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "collectEntries";
        stringArray[1] = "isAssignableFrom";
        stringArray[2] = "class";
        stringArray[3] = "component";
        stringArray[4] = "plus";
        stringArray[5] = "plus";
        stringArray[6] = "plus";
        stringArray[7] = "name";
        stringArray[8] = "class";
        stringArray[9] = "component";
        stringArray[10] = "name";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "addAppender";
        stringArray[13] = "component";
        stringArray[14] = "getAt";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[15];
        AppenderDelegate.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AppenderDelegate.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AppenderDelegate.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    class _closure1
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure1(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object it) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return ScriptBytecodeAdapter.createMap((Object[])new Object[]{callSiteArray[0].callGetProperty(it), it});
        }

        public Object doCall() {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return this.doCall(null);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (((Object)((Object)this)).getClass() != _closure1.class) {
                return ScriptBytecodeAdapter.initMetaClass((Object)((Object)this));
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(((Object)((Object)this)).getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[1];
            stringArray[0] = "name";
            return new CallSiteArray(_closure1.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure1.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}

