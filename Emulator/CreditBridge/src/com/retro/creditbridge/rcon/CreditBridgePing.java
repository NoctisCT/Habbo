package com.retro.creditbridge.rcon;

import com.eu.habbo.messages.rcon.RCONMessage;
import com.google.gson.Gson;

public class CreditBridgePing extends RCONMessage<CreditBridgePing.Request>
{
    public CreditBridgePing()
    {
        super(Request.class);
    }

    @Override
    public void handle(Gson gson, Request request)
    {
        this.status = STATUS_OK;
        this.message = "creditbridge|4";
    }

    public static class Request
    {
        public Request()
        {
        }
    }
}