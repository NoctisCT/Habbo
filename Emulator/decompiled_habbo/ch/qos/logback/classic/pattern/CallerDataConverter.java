/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.status.ErrorStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CallerDataConverter
extends ClassicConverter {
    public static final String DEFAULT_CALLER_LINE_PREFIX = "Caller+";
    public static final String DEFAULT_RANGE_DELIMITER = "..";
    private int depthStart = 0;
    private int depthEnd = 5;
    List<EventEvaluator<ILoggingEvent>> evaluatorList = null;
    final int MAX_ERROR_COUNT = 4;
    int errorCount = 0;

    @Override
    public void start() {
        String depthStr = this.getFirstOption();
        if (depthStr == null) {
            return;
        }
        try {
            if (this.isRange(depthStr)) {
                String[] numbers = this.splitRange(depthStr);
                if (numbers.length == 2) {
                    this.depthStart = Integer.parseInt(numbers[0]);
                    this.depthEnd = Integer.parseInt(numbers[1]);
                    this.checkRange();
                } else {
                    this.addError("Failed to parse depth option as range [" + depthStr + "]");
                }
            } else {
                this.depthEnd = Integer.parseInt(depthStr);
            }
        }
        catch (NumberFormatException nfe) {
            this.addError("Failed to parse depth option [" + depthStr + "]", nfe);
        }
        List<String> optionList = this.getOptionList();
        if (optionList != null && optionList.size() > 1) {
            int optionListSize = optionList.size();
            for (int i = 1; i < optionListSize; ++i) {
                Map evaluatorMap;
                EventEvaluator ee;
                String evaluatorStr = optionList.get(i);
                Context context = this.getContext();
                if (context == null || (ee = (EventEvaluator)(evaluatorMap = (Map)context.getObject("EVALUATOR_MAP")).get(evaluatorStr)) == null) continue;
                this.addEvaluator(ee);
            }
        }
    }

    private boolean isRange(String depthStr) {
        return depthStr.contains(this.getDefaultRangeDelimiter());
    }

    private String[] splitRange(String depthStr) {
        return depthStr.split(Pattern.quote(this.getDefaultRangeDelimiter()), 2);
    }

    private void checkRange() {
        if (this.depthStart < 0 || this.depthEnd < 0) {
            this.addError("Invalid depthStart/depthEnd range [" + this.depthStart + ", " + this.depthEnd + "] (negative values are not allowed)");
        } else if (this.depthStart >= this.depthEnd) {
            this.addError("Invalid depthEnd range [" + this.depthStart + ", " + this.depthEnd + "] (start greater or equal to end)");
        }
    }

    private void addEvaluator(EventEvaluator<ILoggingEvent> ee) {
        if (this.evaluatorList == null) {
            this.evaluatorList = new ArrayList<EventEvaluator<ILoggingEvent>>();
        }
        this.evaluatorList.add(ee);
    }

    @Override
    public String convert(ILoggingEvent le) {
        StackTraceElement[] cda;
        StringBuilder buf = new StringBuilder();
        if (this.evaluatorList != null) {
            boolean printCallerData = false;
            for (int i = 0; i < this.evaluatorList.size(); ++i) {
                EventEvaluator<ILoggingEvent> ee = this.evaluatorList.get(i);
                try {
                    if (!ee.evaluate(le)) continue;
                    printCallerData = true;
                    break;
                }
                catch (EvaluationException eex) {
                    ++this.errorCount;
                    if (this.errorCount < 4) {
                        this.addError("Exception thrown for evaluator named [" + ee.getName() + "]", eex);
                        continue;
                    }
                    if (this.errorCount != 4) continue;
                    ErrorStatus errorStatus = new ErrorStatus("Exception thrown for evaluator named [" + ee.getName() + "].", this, eex);
                    errorStatus.add(new ErrorStatus("This was the last warning about this evaluator's errors.We don't want the StatusManager to get flooded.", this));
                    this.addStatus(errorStatus);
                }
            }
            if (!printCallerData) {
                return "";
            }
        }
        if ((cda = le.getCallerData()) != null && cda.length > this.depthStart) {
            int limit = this.depthEnd < cda.length ? this.depthEnd : cda.length;
            for (int i = this.depthStart; i < limit; ++i) {
                buf.append(this.getCallerLinePrefix());
                buf.append(i);
                buf.append("\t at ");
                buf.append(cda[i]);
                buf.append(CoreConstants.LINE_SEPARATOR);
            }
            return buf.toString();
        }
        return CallerData.CALLER_DATA_NA;
    }

    protected String getCallerLinePrefix() {
        return DEFAULT_CALLER_LINE_PREFIX;
    }

    protected String getDefaultRangeDelimiter() {
        return DEFAULT_RANGE_DELIMITER;
    }
}

