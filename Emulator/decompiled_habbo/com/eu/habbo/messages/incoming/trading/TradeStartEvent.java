/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.trading.TradeStartFailComposer;

public class TradeStartEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        if ((long)Emulator.getIntUnixTimestamp() - this.client.getHabbo().getHabboStats().lastTradeTimestamp > 10L) {
            this.client.getHabbo().getHabboStats().lastTradeTimestamp = Emulator.getIntUnixTimestamp();
            int userId = this.packet.readInt();
            Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
            if (room != null && userId >= 0 && userId != this.client.getHabbo().getRoomUnit().getId()) {
                Habbo targetUser = room.getHabboByRoomUnitId(userId);
                boolean tradeAnywhere = this.client.getHabbo().hasPermission(Permission.ACC_TRADE_ANYWHERE);
                if (!RoomTrade.TRADING_ENABLED && !tradeAnywhere) {
                    this.client.sendResponse(new TradeStartFailComposer(1));
                    return;
                }
                if ((room.getTradeMode() == 0 || room.getTradeMode() == 1 && this.client.getHabbo().getHabboInfo().getId() != room.getOwnerId()) && !tradeAnywhere) {
                    this.client.sendResponse(new TradeStartFailComposer(6));
                    return;
                }
                if (targetUser == null) {
                    return;
                }
                if (targetUser.getHabboStats().userIgnored(this.client.getHabbo().getHabboInfo().getId())) {
                    return;
                }
                if (this.client.getHabbo().getRoomUnit().hasStatus(RoomUnitStatus.TRADING)) {
                    this.client.sendResponse(new TradeStartFailComposer(7));
                    return;
                }
                if (!this.client.getHabbo().getHabboStats().allowTrade()) {
                    this.client.sendResponse(new TradeStartFailComposer(2));
                    return;
                }
                if (targetUser.getRoomUnit().hasStatus(RoomUnitStatus.TRADING)) {
                    this.client.sendResponse(new TradeStartFailComposer(8, targetUser.getHabboInfo().getUsername()));
                    return;
                }
                if (!targetUser.getHabboStats().allowTrade()) {
                    this.client.sendResponse(new TradeStartFailComposer(4, targetUser.getHabboInfo().getUsername()));
                    return;
                }
                room.startTrade(this.client.getHabbo(), targetUser);
            }
        }
    }
}

