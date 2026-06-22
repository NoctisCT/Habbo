/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.result.AbstractDateTimeValueFactory;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SqlTimestampValueFactory
extends AbstractDateTimeValueFactory<Timestamp> {
    private Calendar cal;

    public SqlTimestampValueFactory(PropertySet pset, Calendar calendar, TimeZone tz) {
        super(pset);
        if (calendar != null) {
            this.cal = (Calendar)calendar.clone();
        } else {
            this.cal = Calendar.getInstance(tz, Locale.US);
            this.cal.setLenient(false);
        }
    }

    @Override
    public Timestamp localCreateFromDate(InternalDate idate) {
        return (Timestamp)this.createFromTimestamp(new InternalTimestamp(idate.getYear(), idate.getMonth(), idate.getDay(), 0, 0, 0, 0, 0));
    }

    @Override
    public Timestamp localCreateFromTime(InternalTime it) {
        if (it.getHours() < 0 || it.getHours() >= 24) {
            throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[]{"" + it.getHours() + ":" + it.getMinutes() + ":" + it.getSeconds()}));
        }
        return (Timestamp)this.createFromTimestamp(new InternalTimestamp(1970, 1, 1, it.getHours(), it.getMinutes(), it.getSeconds(), it.getNanos(), it.getScale()));
    }

    @Override
    public Timestamp localCreateFromTimestamp(InternalTimestamp its) {
        if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
            throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
        }
        Calendar calendar = this.cal;
        synchronized (calendar) {
            try {
                this.cal.set(its.getYear(), its.getMonth() - 1, its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds());
                Timestamp ts = new Timestamp(this.cal.getTimeInMillis());
                ts.setNanos(its.getNanos());
                return ts;
            }
            catch (IllegalArgumentException e) {
                throw ExceptionFactory.createException(WrongArgumentException.class, e.getMessage(), e);
            }
        }
    }

    @Override
    public String getTargetTypeName() {
        return Timestamp.class.getName();
    }
}

