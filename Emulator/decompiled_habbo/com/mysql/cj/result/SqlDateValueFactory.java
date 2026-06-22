/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.WarningListener;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.result.AbstractDateTimeValueFactory;
import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SqlDateValueFactory
extends AbstractDateTimeValueFactory<Date> {
    private WarningListener warningListener;
    private Calendar cal;

    public SqlDateValueFactory(PropertySet pset, Calendar calendar, TimeZone tz) {
        super(pset);
        if (calendar != null) {
            this.cal = (Calendar)calendar.clone();
        } else {
            this.cal = Calendar.getInstance(tz, Locale.US);
            this.cal.set(14, 0);
            this.cal.setLenient(false);
        }
    }

    public SqlDateValueFactory(PropertySet pset, Calendar calendar, TimeZone tz, WarningListener warningListener) {
        this(pset, calendar, tz);
        this.warningListener = warningListener;
    }

    @Override
    public Date localCreateFromDate(InternalDate idate) {
        Calendar calendar = this.cal;
        synchronized (calendar) {
            try {
                if (idate.isZero()) {
                    throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
                }
                this.cal.clear();
                this.cal.set(idate.getYear(), idate.getMonth() - 1, idate.getDay());
                long ms = this.cal.getTimeInMillis();
                return new Date(ms);
            }
            catch (IllegalArgumentException e) {
                throw ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
            }
        }
    }

    @Override
    public Date localCreateFromTime(InternalTime it) {
        if (this.warningListener != null) {
            this.warningListener.warningEncountered(Messages.getString("ResultSet.ImplicitDatePartWarning", new Object[]{"java.sql.Date"}));
        }
        Calendar calendar = this.cal;
        synchronized (calendar) {
            try {
                Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
                c1.set(1970, 0, 1, it.getHours(), it.getMinutes(), it.getSeconds());
                c1.set(14, 0);
                long ms = (long)(it.getNanos() / 1000000) + c1.getTimeInMillis();
                return new Date(ms);
            }
            catch (IllegalArgumentException e) {
                throw ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
            }
        }
    }

    @Override
    public Date localCreateFromTimestamp(InternalTimestamp its) {
        if (this.warningListener != null) {
            this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[]{"java.sql.Date"}));
        }
        return (Date)this.createFromDate(its);
    }

    @Override
    public String getTargetTypeName() {
        return Date.class.getName();
    }
}

