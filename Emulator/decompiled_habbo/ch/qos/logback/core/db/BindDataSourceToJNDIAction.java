/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.util.OptionHelper;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.xml.sax.Attributes;

public class BindDataSourceToJNDIAction
extends Action {
    static final String DATA_SOURCE_CLASS = "dataSourceClass";
    static final String URL = "url";
    static final String USER = "user";
    static final String PASSWORD = "password";
    private final BeanDescriptionCache beanDescriptionCache;

    public BindDataSourceToJNDIAction(BeanDescriptionCache beanDescriptionCache) {
        this.beanDescriptionCache = beanDescriptionCache;
    }

    @Override
    public void begin(InterpretationContext ec, String localName, Attributes attributes) {
        String dsClassName = ec.getProperty(DATA_SOURCE_CLASS);
        if (OptionHelper.isEmpty(dsClassName)) {
            this.addWarn("dsClassName is a required parameter");
            ec.addError("dsClassName is a required parameter");
            return;
        }
        String urlStr = ec.getProperty(URL);
        String userStr = ec.getProperty(USER);
        String passwordStr = ec.getProperty(PASSWORD);
        try {
            DataSource ds = (DataSource)OptionHelper.instantiateByClassName(dsClassName, DataSource.class, this.context);
            PropertySetter setter = new PropertySetter(this.beanDescriptionCache, ds);
            setter.setContext(this.context);
            if (!OptionHelper.isEmpty(urlStr)) {
                setter.setProperty(URL, urlStr);
            }
            if (!OptionHelper.isEmpty(userStr)) {
                setter.setProperty(USER, userStr);
            }
            if (!OptionHelper.isEmpty(passwordStr)) {
                setter.setProperty(PASSWORD, passwordStr);
            }
            InitialContext ctx = new InitialContext();
            ctx.rebind("dataSource", (Object)ds);
        }
        catch (Exception oops) {
            this.addError("Could not bind  datasource. Reported error follows.", oops);
            ec.addError("Could not not bind  datasource of type [" + dsClassName + "].");
        }
    }

    @Override
    public void end(InterpretationContext ec, String name) {
    }
}

