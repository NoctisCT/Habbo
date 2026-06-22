/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.Closure
 *  groovy.lang.GroovyObject
 *  groovy.lang.MetaClass
 *  groovy.lang.Reference
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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.gaffer.AppenderDelegate;
import ch.qos.logback.classic.gaffer.ComponentDelegate;
import ch.qos.logback.classic.gaffer.ConfigurationContributor;
import ch.qos.logback.classic.jmx.JMXConfigurator;
import ch.qos.logback.classic.jmx.MBeanUtil;
import ch.qos.logback.classic.joran.ReconfigureOnChangeTask;
import ch.qos.logback.classic.net.ReceiverBase;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.util.CachingDateFormatter;
import ch.qos.logback.core.util.Duration;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.management.ManagementFactory;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ConfigurationDelegate
extends ContextAwareBase
implements GroovyObject {
    private List<Appender> appenderList;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ConfigurationDelegate() {
        MetaClass metaClass;
        List list;
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        this.appenderList = list = ScriptBytecodeAdapter.createList((Object[])new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object getDeclaredOrigin() {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        return this;
    }

    public void scan(String scanPeriodStr) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox((Object)scanPeriodStr)) {
            ReconfigureOnChangeTask rocTask = (ReconfigureOnChangeTask)ScriptBytecodeAdapter.castToType((Object)callSiteArray[0].callConstructor(ReconfigureOnChangeTask.class), ReconfigureOnChangeTask.class);
            callSiteArray[1].call((Object)rocTask, callSiteArray[2].callGroovyObjectGetProperty((Object)this));
            callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty((Object)this), callSiteArray[5].callGetProperty(CoreConstants.class), (Object)rocTask);
            try {
                Duration duration = (Duration)ScriptBytecodeAdapter.castToType((Object)callSiteArray[6].call(Duration.class, (Object)scanPeriodStr), Duration.class);
                ScheduledExecutorService scheduledExecutorService = (ScheduledExecutorService)ScriptBytecodeAdapter.castToType((Object)callSiteArray[7].call(callSiteArray[8].callGroovyObjectGetProperty((Object)this)), ScheduledExecutorService.class);
                ScheduledFuture scheduledFuture = (ScheduledFuture)ScriptBytecodeAdapter.castToType((Object)callSiteArray[9].call((Object)scheduledExecutorService, (Object)rocTask, callSiteArray[10].call((Object)duration), callSiteArray[11].call((Object)duration), callSiteArray[12].callGetProperty(TimeUnit.class)), ScheduledFuture.class);
                callSiteArray[13].call(callSiteArray[14].callGroovyObjectGetProperty((Object)this), (Object)scheduledFuture);
                callSiteArray[15].callCurrent((GroovyObject)this, callSiteArray[16].call((Object)"Setting ReconfigureOnChangeTask scanning period to ", (Object)duration));
            }
            catch (NumberFormatException nfe) {
                callSiteArray[17].callCurrent((GroovyObject)this, callSiteArray[18].call(callSiteArray[19].call((Object)"Error while converting [", (Object)scanPeriodStr), (Object)"] to long"), (Object)nfe);
            }
        }
    }

    public void statusListener(Class listenerClass) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        StatusListener statusListener = (StatusListener)ScriptBytecodeAdapter.castToType((Object)callSiteArray[20].call((Object)listenerClass), StatusListener.class);
        callSiteArray[21].call(callSiteArray[22].callGetProperty(callSiteArray[23].callGroovyObjectGetProperty((Object)this)), (Object)statusListener);
        if (statusListener instanceof ContextAware) {
            callSiteArray[24].call((Object)((ContextAware)ScriptBytecodeAdapter.castToType((Object)statusListener, ContextAware.class)), callSiteArray[25].callGroovyObjectGetProperty((Object)this));
        }
        if (statusListener instanceof LifeCycle) {
            callSiteArray[26].call((Object)((LifeCycle)ScriptBytecodeAdapter.castToType((Object)statusListener, LifeCycle.class)));
        }
        callSiteArray[27].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[]{callSiteArray[28].callGetProperty((Object)listenerClass)}, new String[]{"Added status listener of type [", "]"}));
    }

    public void conversionRule(String conversionWord, Class converterClass) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        String converterClassName = ShortTypeHandling.castToString((Object)callSiteArray[29].call((Object)converterClass));
        Map ruleRegistry = (Map)ScriptBytecodeAdapter.castToType((Object)callSiteArray[30].call(callSiteArray[31].callGroovyObjectGetProperty((Object)this), callSiteArray[32].callGetProperty(CoreConstants.class)), Map.class);
        if (ScriptBytecodeAdapter.compareEqual((Object)ruleRegistry, null)) {
            Object object = callSiteArray[33].callConstructor(HashMap.class);
            ruleRegistry = (Map)ScriptBytecodeAdapter.castToType((Object)object, Map.class);
            callSiteArray[34].call(callSiteArray[35].callGroovyObjectGetProperty((Object)this), callSiteArray[36].callGetProperty(CoreConstants.class), (Object)ruleRegistry);
        }
        callSiteArray[37].callCurrent((GroovyObject)this, callSiteArray[38].call(callSiteArray[39].call(callSiteArray[40].call(callSiteArray[41].call((Object)"registering conversion word ", (Object)conversionWord), (Object)" with class ["), (Object)converterClassName), (Object)"]"));
        callSiteArray[42].call((Object)ruleRegistry, (Object)conversionWord, (Object)converterClassName);
    }

    public void root(Level level, List<String> appenderNames) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual((Object)level, null)) {
            callSiteArray[43].callCurrent((GroovyObject)this, (Object)"Root logger cannot be set to level null");
        } else {
            callSiteArray[44].callCurrent((GroovyObject)this, callSiteArray[45].callGetProperty(org.slf4j.Logger.class), (Object)level, appenderNames);
        }
    }

    public void logger(String name, Level level, List<String> appenderNames, Boolean additivity) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox((Object)name)) {
            Logger logger = (Logger)ScriptBytecodeAdapter.castToType((Object)callSiteArray[46].call((Object)((LoggerContext)ScriptBytecodeAdapter.castToType((Object)callSiteArray[47].callGroovyObjectGetProperty((Object)this), LoggerContext.class)), (Object)name), Logger.class);
            callSiteArray[48].callCurrent((GroovyObject)this, callSiteArray[49].call((Object)new GStringImpl(new Object[]{name}, new String[]{"Setting level of logger [", "] to "}), (Object)level));
            Level level2 = level;
            ScriptBytecodeAdapter.setProperty((Object)level2, null, (Object)logger, (String)"level");
            Reference aName = new Reference(null);
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType((Object)callSiteArray[50].call(appenderNames), Iterator.class);
            while (iterator.hasNext()) {
                aName.set(iterator.next());
                Appender appender = (Appender)ScriptBytecodeAdapter.castToType((Object)callSiteArray[51].call(this.appenderList, (Object)new GeneratedClosure(this, this, aName){
                    private /* synthetic */ Reference aName;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;
                    {
                        Reference reference;
                        CallSite[] callSiteArray = _logger_closure1.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.aName = reference = aName;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _logger_closure1.$getCallSiteArray();
                        return ScriptBytecodeAdapter.compareEqual((Object)callSiteArray[0].callGetProperty(it), (Object)this.aName.get());
                    }

                    public Object getaName() {
                        CallSite[] callSiteArray = _logger_closure1.$getCallSiteArray();
                        return this.aName.get();
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (((Object)((Object)this)).getClass() != _logger_closure1.class) {
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
                        return new CallSiteArray(_logger_closure1.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _logger_closure1.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }), Appender.class);
                if (ScriptBytecodeAdapter.compareNotEqual((Object)appender, null)) {
                    callSiteArray[52].callCurrent((GroovyObject)this, callSiteArray[53].call((Object)new GStringImpl(new Object[]{aName.get()}, new String[]{"Attaching appender named [", "] to "}), (Object)logger));
                    callSiteArray[54].call((Object)logger, (Object)appender);
                    continue;
                }
                callSiteArray[55].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[]{aName.get()}, new String[]{"Failed to find appender named [", "]"}));
            }
            if (ScriptBytecodeAdapter.compareNotEqual((Object)additivity, null)) {
                Boolean bl = additivity;
                ScriptBytecodeAdapter.setProperty((Object)bl, null, (Object)logger, (String)"additive");
            }
        } else {
            callSiteArray[56].callCurrent((GroovyObject)this, (Object)"No name attribute for logger");
        }
    }

    public void appender(String name, Class clazz, Closure closure) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        callSiteArray[57].callCurrent((GroovyObject)this, callSiteArray[58].call(callSiteArray[59].call((Object)"About to instantiate appender of type [", callSiteArray[60].callGetProperty((Object)clazz)), (Object)"]"));
        Appender appender = (Appender)ScriptBytecodeAdapter.castToType((Object)callSiteArray[61].call((Object)clazz), Appender.class);
        callSiteArray[62].callCurrent((GroovyObject)this, callSiteArray[63].call(callSiteArray[64].call((Object)"Naming appender as [", (Object)name), (Object)"]"));
        String string = name;
        ScriptBytecodeAdapter.setProperty((Object)string, null, (Object)appender, (String)"name");
        Object object = callSiteArray[65].callGroovyObjectGetProperty((Object)this);
        ScriptBytecodeAdapter.setProperty((Object)object, null, (Object)appender, (String)"context");
        callSiteArray[66].call(this.appenderList, (Object)appender);
        if (ScriptBytecodeAdapter.compareNotEqual((Object)closure, null)) {
            AppenderDelegate ad = (AppenderDelegate)ScriptBytecodeAdapter.castToType((Object)callSiteArray[67].callConstructor(AppenderDelegate.class, (Object)appender, this.appenderList), AppenderDelegate.class);
            callSiteArray[68].callCurrent((GroovyObject)this, (Object)ad, (Object)appender);
            Object object2 = callSiteArray[69].callGroovyObjectGetProperty((Object)this);
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object2, ConfigurationDelegate.class, (GroovyObject)ad, (String)"context");
            AppenderDelegate appenderDelegate = ad;
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)appenderDelegate, ConfigurationDelegate.class, (GroovyObject)closure, (String)"delegate");
            Object object3 = callSiteArray[70].callGetProperty(Closure.class);
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object3, ConfigurationDelegate.class, (GroovyObject)closure, (String)"resolveStrategy");
            callSiteArray[71].call((Object)closure);
        }
        try {
            callSiteArray[72].call((Object)appender);
        }
        catch (RuntimeException e) {
            callSiteArray[73].callCurrent((GroovyObject)this, callSiteArray[74].call(callSiteArray[75].call((Object)"Failed to start apppender named [", (Object)name), (Object)"]"), (Object)e);
        }
    }

    public void receiver(String name, Class aClass, Closure closure) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        callSiteArray[76].callCurrent((GroovyObject)this, callSiteArray[77].call(callSiteArray[78].call((Object)"About to instantiate receiver of type [", callSiteArray[79].callGetProperty(callSiteArray[80].callGroovyObjectGetProperty((Object)this))), (Object)"]"));
        ReceiverBase receiver = (ReceiverBase)ScriptBytecodeAdapter.castToType((Object)callSiteArray[81].call((Object)aClass), ReceiverBase.class);
        Object object = callSiteArray[82].callGroovyObjectGetProperty((Object)this);
        ScriptBytecodeAdapter.setProperty((Object)object, null, (Object)receiver, (String)"context");
        if (ScriptBytecodeAdapter.compareNotEqual((Object)closure, null)) {
            ComponentDelegate componentDelegate = (ComponentDelegate)ScriptBytecodeAdapter.castToType((Object)callSiteArray[83].callConstructor(ComponentDelegate.class, (Object)receiver), ComponentDelegate.class);
            Object object2 = callSiteArray[84].callGroovyObjectGetProperty((Object)this);
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object2, ConfigurationDelegate.class, (GroovyObject)componentDelegate, (String)"context");
            ComponentDelegate componentDelegate2 = componentDelegate;
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)componentDelegate2, ConfigurationDelegate.class, (GroovyObject)closure, (String)"delegate");
            Object object3 = callSiteArray[85].callGetProperty(Closure.class);
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object3, ConfigurationDelegate.class, (GroovyObject)closure, (String)"resolveStrategy");
            callSiteArray[86].call((Object)closure);
        }
        try {
            callSiteArray[87].call((Object)receiver);
        }
        catch (RuntimeException e) {
            callSiteArray[88].callCurrent((GroovyObject)this, callSiteArray[89].call(callSiteArray[90].call((Object)"Failed to start receiver of type [", callSiteArray[91].call((Object)aClass)), (Object)"]"), (Object)e);
        }
    }

    /*
     * WARNING - void declaration
     */
    private void copyContributions(AppenderDelegate appenderDelegate, Appender appender) {
        void var2_2;
        Reference appenderDelegate2 = new Reference((Object)appenderDelegate);
        Reference appender2 = new Reference((Object)var2_2);
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        if ((Appender)appender2.get() instanceof ConfigurationContributor) {
            ConfigurationContributor cc = (ConfigurationContributor)ScriptBytecodeAdapter.castToType((Object)((Appender)appender2.get()), ConfigurationContributor.class);
            callSiteArray[92].call(callSiteArray[93].call((Object)cc), (Object)new GeneratedClosure(this, this, appenderDelegate2, appender2){
                private /* synthetic */ Reference appenderDelegate;
                private /* synthetic */ Reference appender;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;
                {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _copyContributions_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.appenderDelegate = reference2 = appenderDelegate;
                    this.appender = reference = appender;
                }

                public Object doCall(Object oldName, Object newName) {
                    CallSite[] callSiteArray = _copyContributions_closure2.$getCallSiteArray();
                    Closure closure = ScriptBytecodeAdapter.getMethodPointer((Object)this.appender.get(), (String)ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{oldName}, new String[]{"", ""})));
                    ScriptBytecodeAdapter.setProperty((Object)closure, null, (Object)callSiteArray[0].callGroovyObjectGetProperty(this.appenderDelegate.get()), (String)ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{newName}, new String[]{"", ""})));
                    return closure;
                }

                public Object call(Object oldName, Object newName) {
                    CallSite[] callSiteArray = _copyContributions_closure2.$getCallSiteArray();
                    return callSiteArray[1].callCurrent((GroovyObject)this, oldName, newName);
                }

                public AppenderDelegate getAppenderDelegate() {
                    CallSite[] callSiteArray = _copyContributions_closure2.$getCallSiteArray();
                    return (AppenderDelegate)ScriptBytecodeAdapter.castToType((Object)this.appenderDelegate.get(), AppenderDelegate.class);
                }

                public Appender getAppender() {
                    CallSite[] callSiteArray = _copyContributions_closure2.$getCallSiteArray();
                    return (Appender)ScriptBytecodeAdapter.castToType((Object)this.appender.get(), Appender.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (((Object)((Object)this)).getClass() != _copyContributions_closure2.class) {
                        return ScriptBytecodeAdapter.initMetaClass((Object)((Object)this));
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(((Object)((Object)this)).getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "metaClass";
                    stringArray[1] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _copyContributions_closure2.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_copyContributions_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _copyContributions_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            });
        }
    }

    public void turboFilter(Class clazz, Closure closure) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        callSiteArray[94].callCurrent((GroovyObject)this, callSiteArray[95].call(callSiteArray[96].call((Object)"About to instantiate turboFilter of type [", callSiteArray[97].callGetProperty((Object)clazz)), (Object)"]"));
        TurboFilter turboFilter = (TurboFilter)ScriptBytecodeAdapter.castToType((Object)callSiteArray[98].call((Object)clazz), TurboFilter.class);
        Object object = callSiteArray[99].callGroovyObjectGetProperty((Object)this);
        ScriptBytecodeAdapter.setProperty((Object)object, null, (Object)turboFilter, (String)"context");
        if (ScriptBytecodeAdapter.compareNotEqual((Object)closure, null)) {
            ComponentDelegate componentDelegate = (ComponentDelegate)ScriptBytecodeAdapter.castToType((Object)callSiteArray[100].callConstructor(ComponentDelegate.class, (Object)turboFilter), ComponentDelegate.class);
            Object object2 = callSiteArray[101].callGroovyObjectGetProperty((Object)this);
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object2, ConfigurationDelegate.class, (GroovyObject)componentDelegate, (String)"context");
            ComponentDelegate componentDelegate2 = componentDelegate;
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)componentDelegate2, ConfigurationDelegate.class, (GroovyObject)closure, (String)"delegate");
            Object object3 = callSiteArray[102].callGetProperty(Closure.class);
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)object3, ConfigurationDelegate.class, (GroovyObject)closure, (String)"resolveStrategy");
            callSiteArray[103].call((Object)closure);
        }
        callSiteArray[104].call((Object)turboFilter);
        callSiteArray[105].callCurrent((GroovyObject)this, (Object)"Adding aforementioned turbo filter to context");
        callSiteArray[106].call(callSiteArray[107].callGroovyObjectGetProperty((Object)this), (Object)turboFilter);
    }

    public String timestamp(String datePattern, long timeReference) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        long now = DefaultTypeTransformation.longUnbox((Object)-1);
        if (ScriptBytecodeAdapter.compareEqual((Object)timeReference, (Object)-1)) {
            callSiteArray[108].callCurrent((GroovyObject)this, (Object)"Using current interpretation time, i.e. now, as time reference.");
            Object object = callSiteArray[109].call(System.class);
            now = DefaultTypeTransformation.longUnbox((Object)object);
        } else {
            long l;
            now = l = timeReference;
            callSiteArray[110].callCurrent((GroovyObject)this, callSiteArray[111].call(callSiteArray[112].call((Object)"Using ", (Object)now), (Object)" as time reference."));
        }
        CachingDateFormatter sdf = (CachingDateFormatter)ScriptBytecodeAdapter.castToType((Object)callSiteArray[113].callConstructor(CachingDateFormatter.class, (Object)datePattern), CachingDateFormatter.class);
        return ShortTypeHandling.castToString((Object)callSiteArray[114].call((Object)sdf, (Object)now));
    }

    public void jmxConfigurator(String name) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        Object objectName = null;
        Object contextName = callSiteArray[115].callGetProperty(callSiteArray[116].callGroovyObjectGetProperty((Object)this));
        if (ScriptBytecodeAdapter.compareNotEqual((Object)name, null)) {
            try {
                Object object;
                objectName = object = callSiteArray[117].callConstructor(ObjectName.class, (Object)name);
            }
            catch (MalformedObjectNameException e) {
                String string = name;
                contextName = string;
            }
        }
        if (ScriptBytecodeAdapter.compareEqual(objectName, null)) {
            Object object;
            Object objectNameAsStr = callSiteArray[118].call(MBeanUtil.class, contextName, JMXConfigurator.class);
            objectName = object = callSiteArray[119].call(MBeanUtil.class, callSiteArray[120].callGroovyObjectGetProperty((Object)this), (Object)this, objectNameAsStr);
            if (ScriptBytecodeAdapter.compareEqual((Object)objectName, null)) {
                callSiteArray[121].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[]{objectNameAsStr}, new String[]{"Failed to construct ObjectName for [", "]"}));
                return;
            }
        }
        Object platformMBeanServer = callSiteArray[122].callGetProperty(ManagementFactory.class);
        if (!DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[123].call(MBeanUtil.class, platformMBeanServer, objectName))) {
            JMXConfigurator jmxConfigurator = (JMXConfigurator)ScriptBytecodeAdapter.castToType((Object)callSiteArray[124].callConstructor(JMXConfigurator.class, (Object)ScriptBytecodeAdapter.createPojoWrapper((Object)((LoggerContext)ScriptBytecodeAdapter.castToType((Object)callSiteArray[125].callGroovyObjectGetProperty((Object)this), LoggerContext.class)), LoggerContext.class), platformMBeanServer, objectName), JMXConfigurator.class);
            try {
                callSiteArray[126].call(platformMBeanServer, (Object)jmxConfigurator, objectName);
            }
            catch (Exception all) {
                callSiteArray[127].callCurrent((GroovyObject)this, (Object)"Failed to create mbean", (Object)all);
            }
        }
    }

    public void scan() {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            this.scan(null);
        } else {
            this.scan(null);
        }
    }

    public void root(Level level) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        this.root(level, ScriptBytecodeAdapter.createList((Object[])new Object[0]));
    }

    public void logger(String name, Level level, List<String> appenderNames) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        this.logger(name, level, appenderNames, null);
    }

    public void logger(String name, Level level) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        this.logger(name, level, ScriptBytecodeAdapter.createList((Object[])new Object[0]), null);
    }

    public void appender(String name, Class clazz) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        this.appender(name, clazz, null);
    }

    public void receiver(String name, Class aClass) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        this.receiver(name, aClass, null);
    }

    public void turboFilter(Class clazz) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        this.turboFilter(clazz, null);
    }

    public String timestamp(String datePattern) {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return this.timestamp(datePattern, DefaultTypeTransformation.longUnbox((Object)-1));
        }
        return this.timestamp(datePattern, DefaultTypeTransformation.longUnbox((Object)-1));
    }

    public void jmxConfigurator() {
        CallSite[] callSiteArray = ConfigurationDelegate.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            this.jmxConfigurator(null);
        } else {
            this.jmxConfigurator(null);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ConfigurationDelegate.class) {
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

    public List<Appender> getAppenderList() {
        return this.appenderList;
    }

    public void setAppenderList(List<Appender> list) {
        this.appenderList = list;
    }

    public /* synthetic */ Object super$2$getDeclaredOrigin() {
        return super.getDeclaredOrigin();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "setContext";
        stringArray[2] = "context";
        stringArray[3] = "putObject";
        stringArray[4] = "context";
        stringArray[5] = "RECONFIGURE_ON_CHANGE_TASK";
        stringArray[6] = "valueOf";
        stringArray[7] = "getScheduledExecutorService";
        stringArray[8] = "context";
        stringArray[9] = "scheduleAtFixedRate";
        stringArray[10] = "getMilliseconds";
        stringArray[11] = "getMilliseconds";
        stringArray[12] = "MILLISECONDS";
        stringArray[13] = "addScheduledFuture";
        stringArray[14] = "context";
        stringArray[15] = "addInfo";
        stringArray[16] = "plus";
        stringArray[17] = "addError";
        stringArray[18] = "plus";
        stringArray[19] = "plus";
        stringArray[20] = "newInstance";
        stringArray[21] = "add";
        stringArray[22] = "statusManager";
        stringArray[23] = "context";
        stringArray[24] = "setContext";
        stringArray[25] = "context";
        stringArray[26] = "start";
        stringArray[27] = "addInfo";
        stringArray[28] = "canonicalName";
        stringArray[29] = "getName";
        stringArray[30] = "getObject";
        stringArray[31] = "context";
        stringArray[32] = "PATTERN_RULE_REGISTRY";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "putObject";
        stringArray[35] = "context";
        stringArray[36] = "PATTERN_RULE_REGISTRY";
        stringArray[37] = "addInfo";
        stringArray[38] = "plus";
        stringArray[39] = "plus";
        stringArray[40] = "plus";
        stringArray[41] = "plus";
        stringArray[42] = "put";
        stringArray[43] = "addError";
        stringArray[44] = "logger";
        stringArray[45] = "ROOT_LOGGER_NAME";
        stringArray[46] = "getLogger";
        stringArray[47] = "context";
        stringArray[48] = "addInfo";
        stringArray[49] = "plus";
        stringArray[50] = "iterator";
        stringArray[51] = "find";
        stringArray[52] = "addInfo";
        stringArray[53] = "plus";
        stringArray[54] = "addAppender";
        stringArray[55] = "addError";
        stringArray[56] = "addInfo";
        stringArray[57] = "addInfo";
        stringArray[58] = "plus";
        stringArray[59] = "plus";
        stringArray[60] = "name";
        stringArray[61] = "newInstance";
        stringArray[62] = "addInfo";
        stringArray[63] = "plus";
        stringArray[64] = "plus";
        stringArray[65] = "context";
        stringArray[66] = "add";
        stringArray[67] = "<$constructor$>";
        stringArray[68] = "copyContributions";
        stringArray[69] = "context";
        stringArray[70] = "DELEGATE_FIRST";
        stringArray[71] = "call";
        stringArray[72] = "start";
        stringArray[73] = "addError";
        stringArray[74] = "plus";
        stringArray[75] = "plus";
        stringArray[76] = "addInfo";
        stringArray[77] = "plus";
        stringArray[78] = "plus";
        stringArray[79] = "name";
        stringArray[80] = "clazz";
        stringArray[81] = "newInstance";
        stringArray[82] = "context";
        stringArray[83] = "<$constructor$>";
        stringArray[84] = "context";
        stringArray[85] = "DELEGATE_FIRST";
        stringArray[86] = "call";
        stringArray[87] = "start";
        stringArray[88] = "addError";
        stringArray[89] = "plus";
        stringArray[90] = "plus";
        stringArray[91] = "getName";
        stringArray[92] = "each";
        stringArray[93] = "getMappings";
        stringArray[94] = "addInfo";
        stringArray[95] = "plus";
        stringArray[96] = "plus";
        stringArray[97] = "name";
        stringArray[98] = "newInstance";
        stringArray[99] = "context";
        stringArray[100] = "<$constructor$>";
        stringArray[101] = "context";
        stringArray[102] = "DELEGATE_FIRST";
        stringArray[103] = "call";
        stringArray[104] = "start";
        stringArray[105] = "addInfo";
        stringArray[106] = "addTurboFilter";
        stringArray[107] = "context";
        stringArray[108] = "addInfo";
        stringArray[109] = "currentTimeMillis";
        stringArray[110] = "addInfo";
        stringArray[111] = "plus";
        stringArray[112] = "plus";
        stringArray[113] = "<$constructor$>";
        stringArray[114] = "format";
        stringArray[115] = "name";
        stringArray[116] = "context";
        stringArray[117] = "<$constructor$>";
        stringArray[118] = "getObjectNameFor";
        stringArray[119] = "string2ObjectName";
        stringArray[120] = "context";
        stringArray[121] = "addError";
        stringArray[122] = "platformMBeanServer";
        stringArray[123] = "isRegistered";
        stringArray[124] = "<$constructor$>";
        stringArray[125] = "context";
        stringArray[126] = "registerMBean";
        stringArray[127] = "addError";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[128];
        ConfigurationDelegate.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ConfigurationDelegate.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ConfigurationDelegate.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

