/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class BullyReportRequestComposer
extends MessageComposer {
    public static final int START_REPORT = 0;
    public static final int ONGOING_HELPER_CASE = 1;
    public static final int INVALID_REQUESTS = 2;
    public static final int TOO_RECENT = 3;
    private final int errorCode;
    private final int errorCodeType;

    public BullyReportRequestComposer(int errorCode, int errorCodeType) {
        this.errorCode = errorCode;
        this.errorCodeType = errorCodeType;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3463);
        this.response.appendInt(this.errorCode);
        if (this.errorCode == 1) {
            this.response.appendInt(this.errorCodeType);
            this.response.appendInt(1);
            this.response.appendBoolean(true);
            this.response.appendString("admin");
            this.response.appendString("ca-1807-64.lg-3365-78.hr-3370-42-31.hd-3093-1359.ch-3372-65");
            switch (this.errorCodeType) {
                case 3: {
                    this.response.appendString("room Name");
                    break;
                }
                case 1: {
                    this.response.appendString("description");
                }
            }
        }
        return this.response;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public int getErrorCodeType() {
        return this.errorCodeType;
    }
}

