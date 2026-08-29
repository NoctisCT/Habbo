package com.retro.creditbridge.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.google.gson.Gson;
import com.retro.creditbridge.CreditBridgeTransactionExecutor;
import com.retro.creditbridge.OfflineCreditTransactionExecutor;

public class CreditCredits extends RCONMessage<CreditCredits.Request>
{
    public CreditCredits()
    {
        super(Request.class);
    }

    @Override
    public void handle(Gson gson, Request request)
    {
        if (
            request == null ||
            request.user_id <= 0 ||
            request.amount <= 0 ||
            request.transaction_id == null ||
            !request.transaction_id.matches(
                "[A-Za-z0-9-]{16,64}"
            )
        )
        {
            this.status = STATUS_ERROR;
            this.message = "invalid_request";
            return;
        }

        Habbo habbo = Emulator.getGameEnvironment()
            .getHabboManager()
            .getHabbo(request.user_id);

        CreditBridgeTransactionExecutor.Result result;

        if (habbo != null)
        {
            result =
                new CreditBridgeTransactionExecutor()
                    .execute(
                        habbo,
                        request.user_id,
                        request.amount,
                        request.transaction_id,
                        "credit"
                    );
        }
        else
        {
            result =
                new OfflineCreditTransactionExecutor()
                    .execute(
                        request.user_id,
                        request.amount,
                        request.transaction_id
                    );
        }

        this.status = result.status;
        this.message = result.message;
    }

    public static class Request
    {
        public int user_id;
        public int amount;
        public String transaction_id;

        public Request()
        {
        }
    }
}