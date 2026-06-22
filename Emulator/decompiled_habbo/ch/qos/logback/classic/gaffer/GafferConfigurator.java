/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.Binding
 *  groovy.lang.GroovyObject
 *  groovy.lang.GroovyShell
 *  groovy.lang.MetaClass
 *  groovy.lang.Reference
 *  groovy.lang.Script
 *  org.codehaus.groovy.control.CompilerConfiguration
 *  org.codehaus.groovy.control.customizers.ImportCustomizer
 *  org.codehaus.groovy.reflection.ClassInfo
 *  org.codehaus.groovy.runtime.ArrayUtil
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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.gaffer.ConfigurationDelegate;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.OptionHelper;
import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.lang.Script;
import java.io.File;
import java.lang.ref.SoftReference;
import java.net.URL;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class GafferConfigurator
implements GroovyObject {
    private LoggerContext context;
    private static final String DEBUG_SYSTEM_PROPERTY_KEY = "logback.debug";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    /*
     * WARNING - void declaration
     */
    public GafferConfigurator(LoggerContext loggerContext) {
        void context;
        MetaClass metaClass;
        CallSite[] callSiteArray = GafferConfigurator.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        void var4_4 = context;
        this.context = (LoggerContext)ScriptBytecodeAdapter.castToType((Object)var4_4, LoggerContext.class);
    }

    protected void informContextOfURLUsedForConfiguration(URL url) {
        CallSite[] callSiteArray = GafferConfigurator.$getCallSiteArray();
        callSiteArray[0].call(ConfigurationWatchListUtil.class, (Object)this.context, (Object)url);
    }

    public void run(URL url) {
        CallSite[] callSiteArray = GafferConfigurator.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[1].callCurrent((GroovyObject)this, (Object)url);
        } else {
            this.informContextOfURLUsedForConfiguration(url);
        }
        callSiteArray[2].callCurrent((GroovyObject)this, callSiteArray[3].callGetProperty((Object)url));
    }

    public void run(File file) {
        CallSite[] callSiteArray = GafferConfigurator.$getCallSiteArray();
        callSiteArray[4].callCurrent((GroovyObject)this, callSiteArray[5].call(callSiteArray[6].call((Object)file)));
        callSiteArray[7].callCurrent((GroovyObject)this, callSiteArray[8].callGetProperty((Object)file));
    }

    public void run(String dslText) {
        CallSite[] callSiteArray = GafferConfigurator.$getCallSiteArray();
        Binding binding = (Binding)ScriptBytecodeAdapter.castToType((Object)callSiteArray[9].callConstructor(Binding.class), Binding.class);
        callSiteArray[10].call((Object)binding, (Object)"hostname", callSiteArray[11].callGetProperty(ContextUtil.class));
        Object configuration = callSiteArray[12].callConstructor(CompilerConfiguration.class);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[13].call(configuration, callSiteArray[14].callCurrent((GroovyObject)this));
        } else {
            callSiteArray[15].call(configuration, (Object)this.importCustomizer());
        }
        String debugAttrib = ShortTypeHandling.castToString((Object)callSiteArray[16].call(System.class, (Object)DEBUG_SYSTEM_PROPERTY_KEY));
        if (!(DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[17].call(OptionHelper.class, (Object)debugAttrib)) || DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[18].call((Object)debugAttrib, (Object)"false")) || DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[19].call((Object)debugAttrib, (Object)"null")))) {
            callSiteArray[20].call(OnConsoleStatusListener.class, (Object)this.context);
        }
        callSiteArray[21].call(callSiteArray[22].callConstructor(ContextUtil.class, (Object)this.context), callSiteArray[23].call((Object)this.context));
        Reference dslScript = new Reference((Object)((Script)ScriptBytecodeAdapter.castToType((Object)callSiteArray[24].call(callSiteArray[25].callConstructor(GroovyShell.class, (Object)binding, configuration), (Object)dslText), Script.class)));
        callSiteArray[26].call(callSiteArray[27].callGroovyObjectGetProperty((Object)((Script)dslScript.get())), ConfigurationDelegate.class);
        callSiteArray[28].call((Object)((Script)dslScript.get()), (Object)this.context);
        GeneratedClosure generatedClosure = new GeneratedClosure(this, this, dslScript){
            private /* synthetic */ Reference dslScript;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                Reference reference;
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.dslScript = reference = dslScript;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                return this.dslScript.get();
            }

            public Script getDslScript() {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                return (Script)ScriptBytecodeAdapter.castToType((Object)this.dslScript.get(), Script.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (((Object)((Object)this)).getClass() != _run_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass((Object)((Object)this));
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(((Object)((Object)this)).getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[]{};
                return new CallSiteArray(_run_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        };
        ScriptBytecodeAdapter.setProperty((Object)generatedClosure, null, (Object)callSiteArray[29].callGroovyObjectGetProperty((Object)((Script)dslScript.get())), (String)"getDeclaredOrigin");
        callSiteArray[30].call((Object)((Script)dslScript.get()));
    }

    protected ImportCustomizer importCustomizer() {
        CallSite[] callSiteArray = GafferConfigurator.$getCallSiteArray();
        Object customizer = callSiteArray[31].callConstructor(ImportCustomizer.class);
        String core = "ch.qos.logback.core";
        callSiteArray[32].call(customizer, ArrayUtil.createArray((Object)core, (Object)new GStringImpl(new Object[]{core}, new String[]{"", ".encoder"}), (Object)new GStringImpl(new Object[]{core}, new String[]{"", ".read"}), (Object)new GStringImpl(new Object[]{core}, new String[]{"", ".rolling"}), (Object)new GStringImpl(new Object[]{core}, new String[]{"", ".status"}), (Object)"ch.qos.logback.classic.net"));
        callSiteArray[33].call(customizer, callSiteArray[34].callGetProperty(PatternLayoutEncoder.class));
        callSiteArray[35].call(customizer, callSiteArray[36].callGetProperty(Level.class));
        callSiteArray[37].call(customizer, (Object)"off", callSiteArray[38].callGetProperty(Level.class), (Object)"OFF");
        callSiteArray[39].call(customizer, (Object)"error", callSiteArray[40].callGetProperty(Level.class), (Object)"ERROR");
        callSiteArray[41].call(customizer, (Object)"warn", callSiteArray[42].callGetProperty(Level.class), (Object)"WARN");
        callSiteArray[43].call(customizer, (Object)"info", callSiteArray[44].callGetProperty(Level.class), (Object)"INFO");
        callSiteArray[45].call(customizer, (Object)"debug", callSiteArray[46].callGetProperty(Level.class), (Object)"DEBUG");
        callSiteArray[47].call(customizer, (Object)"trace", callSiteArray[48].callGetProperty(Level.class), (Object)"TRACE");
        callSiteArray[49].call(customizer, (Object)"all", callSiteArray[50].callGetProperty(Level.class), (Object)"ALL");
        return (ImportCustomizer)ScriptBytecodeAdapter.castToType((Object)customizer, ImportCustomizer.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GafferConfigurator.class) {
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

    public LoggerContext getContext() {
        return this.context;
    }

    public void setContext(LoggerContext loggerContext) {
        this.context = loggerContext;
    }

    public static final String getDEBUG_SYSTEM_PROPERTY_KEY() {
        return DEBUG_SYSTEM_PROPERTY_KEY;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "setMainWatchURL";
        stringArray[1] = "informContextOfURLUsedForConfiguration";
        stringArray[2] = "run";
        stringArray[3] = "text";
        stringArray[4] = "informContextOfURLUsedForConfiguration";
        stringArray[5] = "toURL";
        stringArray[6] = "toURI";
        stringArray[7] = "run";
        stringArray[8] = "text";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "setProperty";
        stringArray[11] = "localHostName";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "addCompilationCustomizers";
        stringArray[14] = "importCustomizer";
        stringArray[15] = "addCompilationCustomizers";
        stringArray[16] = "getProperty";
        stringArray[17] = "isEmpty";
        stringArray[18] = "equalsIgnoreCase";
        stringArray[19] = "equalsIgnoreCase";
        stringArray[20] = "addNewInstanceToContext";
        stringArray[21] = "addGroovyPackages";
        stringArray[22] = "<$constructor$>";
        stringArray[23] = "getFrameworkPackages";
        stringArray[24] = "parse";
        stringArray[25] = "<$constructor$>";
        stringArray[26] = "mixin";
        stringArray[27] = "metaClass";
        stringArray[28] = "setContext";
        stringArray[29] = "metaClass";
        stringArray[30] = "run";
        stringArray[31] = "<$constructor$>";
        stringArray[32] = "addStarImports";
        stringArray[33] = "addImports";
        stringArray[34] = "name";
        stringArray[35] = "addStaticStars";
        stringArray[36] = "name";
        stringArray[37] = "addStaticImport";
        stringArray[38] = "name";
        stringArray[39] = "addStaticImport";
        stringArray[40] = "name";
        stringArray[41] = "addStaticImport";
        stringArray[42] = "name";
        stringArray[43] = "addStaticImport";
        stringArray[44] = "name";
        stringArray[45] = "addStaticImport";
        stringArray[46] = "name";
        stringArray[47] = "addStaticImport";
        stringArray[48] = "name";
        stringArray[49] = "addStaticImport";
        stringArray[50] = "name";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[51];
        GafferConfigurator.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GafferConfigurator.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GafferConfigurator.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

