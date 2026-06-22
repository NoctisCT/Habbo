/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class BotSettingsComposer
extends MessageComposer {
    private final Bot bot;
    private final int settingId;

    public BotSettingsComposer(Bot bot, int settingId) {
        this.bot = bot;
        this.settingId = settingId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1618);
        this.response.appendInt(-this.bot.getId());
        this.response.appendInt(this.settingId);
        switch (this.settingId) {
            case 1: {
                this.response.appendString("");
                break;
            }
            case 2: {
                StringBuilder data = new StringBuilder();
                if (this.bot.hasChat()) {
                    for (String s : this.bot.getChatLines()) {
                        data.append(s).append("\r");
                    }
                } else {
                    data.append("${bot.skill.chatter.configuration.text.placeholder}");
                }
                data.append(";#;").append(this.bot.isChatAuto() ? "true" : "false");
                data.append(";#;").append(this.bot.getChatDelay());
                data.append(";#;").append(this.bot.isChatRandom() ? "true" : "false");
                this.response.appendString(data.toString());
                break;
            }
            case 3: {
                this.response.appendString("");
                break;
            }
            case 4: {
                this.response.appendString("");
                break;
            }
            case 5: {
                this.response.appendString(this.bot.getName());
                break;
            }
            case 6: {
                this.response.appendString("");
                break;
            }
            case 9: {
                this.response.appendString(this.bot.getMotto());
            }
        }
        return this.response;
    }

    public Bot getBot() {
        return this.bot;
    }

    public int getSettingId() {
        return this.settingId;
    }
}

