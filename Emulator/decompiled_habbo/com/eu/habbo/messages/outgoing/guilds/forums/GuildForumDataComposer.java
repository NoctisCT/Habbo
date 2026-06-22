/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.habbohotel.guilds.forums.ForumThreadComment;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.handshake.ConnectionErrorComposer;
import gnu.trove.set.hash.THashSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildForumDataComposer
extends MessageComposer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildForumDataComposer.class);
    public final Guild guild;
    public Habbo habbo;

    public GuildForumDataComposer(Guild guild, Habbo habbo) {
        this.guild = guild;
        this.habbo = habbo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void serializeForumData(ServerMessage response, Guild guild, Habbo habbo) {
        THashSet<ForumThread> forumThreads = ForumThread.getByGuildId(guild.getId());
        int lastSeenAt = 0;
        int totalComments = 0;
        int newComments = 0;
        int totalThreads = 0;
        ForumThreadComment lastComment = null;
        THashSet<ForumThread> tHashSet = forumThreads;
        synchronized (tHashSet) {
            for (ForumThread thread : forumThreads) {
                ++totalThreads;
                totalComments += thread.getPostsCount();
                ForumThreadComment comment = thread.getLastComment();
                if (comment == null || lastComment != null && lastComment.getCreatedAt() >= comment.getCreatedAt()) continue;
                lastComment = comment;
            }
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM guilds_forums_threads A JOIN ( SELECT * FROM `guilds_forums_comments` WHERE `id` IN ( SELECT id FROM `guilds_forums_comments` B ORDER BY B.`id` ASC ) ORDER BY `id` DESC ) B ON A.`id` = B.`thread_id` WHERE A.`guild_id` = ? AND B.`created_at` > ?");){
            statement.setInt(1, guild.getId());
            statement.setInt(2, lastSeenAt);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                newComments = set.getInt(1);
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        response.appendInt(guild.getId());
        response.appendString(guild.getName());
        response.appendString(guild.getDescription());
        response.appendString(guild.getBadge());
        response.appendInt(totalThreads);
        response.appendInt(0);
        response.appendInt(totalComments);
        response.appendInt(newComments);
        response.appendInt(lastComment != null ? lastComment.getThreadId() : -1);
        response.appendInt(lastComment != null ? lastComment.getUserId() : -1);
        response.appendString(lastComment != null && lastComment.getHabbo() != null ? lastComment.getHabbo().getHabboInfo().getUsername() : "");
        response.appendInt(lastComment != null ? Emulator.getIntUnixTimestamp() - lastComment.getCreatedAt() : 0);
    }

    @Override
    protected ServerMessage composeInternal() {
        try {
            this.response.init(3011);
            GuildForumDataComposer.serializeForumData(this.response, this.guild, this.habbo);
            GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(this.guild, this.habbo);
            boolean isAdmin = member != null && (member.getRank().type < GuildRank.MEMBER.type || this.guild.getOwnerId() == this.habbo.getHabboInfo().getId());
            boolean isStaff = this.habbo.hasPermission(Permission.ACC_MODTOOL_TICKET_Q);
            String errorRead = "";
            String errorPost = "";
            String errorStartThread = "";
            String errorModerate = "";
            if (this.guild.canReadForum().state == 1 && member == null && !isStaff) {
                errorRead = "not_member";
            } else if (this.guild.canReadForum().state == 2 && !isAdmin && !isStaff) {
                errorRead = "not_admin";
            }
            if (this.guild.canPostMessages().state == 1 && member == null && !isStaff) {
                errorPost = "not_member";
            } else if (this.guild.canPostMessages().state == 2 && !isAdmin && !isStaff) {
                errorPost = "not_admin";
            } else if (this.guild.canPostMessages().state == 3 && this.guild.getOwnerId() != this.habbo.getHabboInfo().getId() && !isStaff) {
                errorPost = "not_owner";
            }
            if (this.guild.canPostThreads().state == 1 && member == null && !isStaff) {
                errorStartThread = "not_member";
            } else if (this.guild.canPostThreads().state == 2 && !isAdmin && !isStaff) {
                errorStartThread = "not_admin";
            } else if (this.guild.canPostThreads().state == 3 && this.guild.getOwnerId() != this.habbo.getHabboInfo().getId() && !isStaff) {
                errorStartThread = "not_owner";
            }
            if (this.guild.canModForum().state == 3 && this.guild.getOwnerId() != this.habbo.getHabboInfo().getId() && !isStaff) {
                errorModerate = "not_owner";
            } else if (!isAdmin && !isStaff) {
                errorModerate = "not_admin";
            }
            this.response.appendInt(this.guild.canReadForum().state);
            this.response.appendInt(this.guild.canPostMessages().state);
            this.response.appendInt(this.guild.canPostThreads().state);
            this.response.appendInt(this.guild.canModForum().state);
            this.response.appendString(errorRead);
            this.response.appendString(errorPost);
            this.response.appendString(errorStartThread);
            this.response.appendString(errorModerate);
            this.response.appendString("");
            this.response.appendBoolean(this.guild.getOwnerId() == this.habbo.getHabboInfo().getId());
            if (this.guild.canModForum().state == 3) {
                this.response.appendBoolean(this.guild.getOwnerId() == this.habbo.getHabboInfo().getId() || isStaff);
            } else {
                this.response.appendBoolean(this.guild.getOwnerId() == this.habbo.getHabboInfo().getId() || isStaff || isAdmin);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ConnectionErrorComposer(500).compose();
        }
        return this.response;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

