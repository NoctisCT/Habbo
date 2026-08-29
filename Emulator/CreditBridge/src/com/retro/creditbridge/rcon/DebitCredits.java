package com.retro.creditbridge.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.google.gson.Gson;
import com.retro.creditbridge.CreditBridgeTransactionExecutor;

public class DebitCredits extends RCONMessage<DebitCredits.Request>
{
    public DebitCredits()
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

        if (habbo == null)
        {
            this.status = HABBO_NOT_FOUND;
            this.message = "not_online";
            return;
        }

        CreditBridgeTransactionExecutor.Result result =
            new CreditBridgeTransactionExecutor()
                .execute(
                    habbo,
                    request.user_id,
                    request.amount,
                    request.transaction_id,
                    "debit"
                );

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