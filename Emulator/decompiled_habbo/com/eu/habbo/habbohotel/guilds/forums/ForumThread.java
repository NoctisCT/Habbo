/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.ForumThreadComment;
import com.eu.habbo.habbohotel.guilds.forums.ForumThreadState;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.plugin.events.guilds.forums.GuildForumThreadBeforeCreated;
import com.eu.habbo.plugin.events.guilds.forums.GuildForumThreadCreated;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForumThread
implements Runnable,
ISerialize {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForumThread.class);
    private static final THashMap<Integer, THashSet<ForumThread>> guildThreadsCache = new THashMap();
    private static final THashMap<Integer, ForumThread> forumThreadsCache = new THashMap();
    private final int threadId;
    private final int guildId;
    private final int openerId;
    private final String subject;
    private final int createdAt;
    private final THashMap<Integer, ForumThreadComment> comments;
    private int postsCount;
    private int updatedAt;
    private ForumThreadState state;
    private boolean pinned;
    private boolean locked;
    private int adminId;
    private boolean needsUpdate;
    private boolean hasCommentsLoaded;
    private int commentIndex;
    private ForumThreadComment lastComment;

    public ForumThread(int threadId, int guildId, int openerId, String subject, int postsCount, int createdAt, int updatedAt, ForumThreadState state, boolean pinned, boolean locked, int adminId, ForumThreadComment lastComment) {
        this.threadId = threadId;
        this.guildId = guildId;
        this.openerId = openerId;
        this.subject = subject;
        this.postsCount = postsCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.state = state;
        this.pinned = pinned;
        this.locked = locked;
        this.adminId = adminId;
        this.lastComment = lastComment;
        this.comments = new THashMap();
        this.needsUpdate = false;
        this.hasCommentsLoaded = false;
        this.commentIndex = 0;
    }

    public ForumThread(ResultSet set) throws SQLException {
        this.threadId = set.getInt("id");
        this.guildId = set.getInt("guild_id");
        this.openerId = set.getInt("opener_id");
        this.subject = set.getString("subject");
        this.postsCount = set.getInt("posts_count");
        this.createdAt = set.getInt("created_at");
        this.updatedAt = set.getInt("updated_at");
        this.state = ForumThreadState.fromValue(set.getInt("state"));
        this.pinned = set.getInt("pinned") > 0;
        this.locked = set.getInt("locked") > 0;
        this.adminId = set.getInt("admin_id");
        this.lastComment = null;
        try {
            this.lastComment = ForumThreadComment.getById(set.getInt("last_comment_id"));
        }
        catch (SQLException e) {
            LOGGER.error("ForumThread last_comment_id exception", e);
        }
        this.comments = new THashMap();
        this.needsUpdate = false;
        this.hasCommentsLoaded = false;
        this.commentIndex = 0;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static ForumThread create(Guild guild, Habbo opener, String subject, String message) throws Exception {
        ForumThread createdThread = null;
        if (Emulator.getPluginManager().fireEvent(new GuildForumThreadBeforeCreated(guild, opener, subject, message)).isCancelled()) {
            return null;
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `guilds_forums_threads`(`guild_id`, `opener_id`, `subject`, `created_at`, `updated_at`) VALUES (?, ?, ?, ?, ?)", 1);){
            int timestamp = Emulator.getIntUnixTimestamp();
            statement.setInt(1, guild.getId());
            statement.setInt(2, opener.getHabboInfo().getId());
            statement.setString(3, subject);
            statement.setInt(4, timestamp);
            statement.setInt(5, timestamp);
            if (statement.executeUpdate() < 1) {
                ForumThread forumThread = null;
                return forumThread;
            }
            ResultSet set = statement.getGeneratedKeys();
            if (!set.next()) return createdThread;
            int threadId = set.getInt(1);
            createdThread = new ForumThread(threadId, guild.getId(), opener.getHabboInfo().getId(), subject, 0, timestamp, timestamp, ForumThreadState.OPEN, false, false, 0, null);
            ForumThread.cacheThread(createdThread);
            ForumThreadComment comment = ForumThreadComment.create(createdThread, opener, message);
            createdThread.addComment(comment);
            Emulator.getPluginManager().fireEvent(new GuildForumThreadCreated(createdThread));
            return createdThread;
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return createdThread;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static THashSet<ForumThread> getByGuildId(int guildId) {
        THashSet<ForumThread> threads = null;
        if (guildThreadsCache.containsKey(guildId)) {
            threads = guildThreadsCache.get(guildId);
        }
        if (threads != null) {
            return threads;
        }
        threads = new THashSet();
        guildThreadsCache.put(guildId, threads);
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT A.*, B.`id` AS `last_comment_id` FROM guilds_forums_threads A JOIN (SELECT * FROM `guilds_forums_comments` WHERE `id` IN (SELECT MAX(id) FROM `guilds_forums_comments` B GROUP BY `thread_id` AND B.`id` ORDER BY B.`id` ) ORDER BY `id` DESC ) B ON A.`id` = B.`thread_id` WHERE A.`guild_id` = ? ORDER BY A.`pinned` DESC, B.`created_at` DESC ");){
            statement.setInt(1, guildId);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    ForumThread thread = new ForumThread(set);
                    THashSet<ForumThread> tHashSet = threads;
                    synchronized (tHashSet) {
                        threads.add(thread);
                    }
                    ForumThread.cacheThread(thread);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return threads;
    }

    public static ForumThread getById(int threadId) throws SQLException {
        ForumThread foundThread = forumThreadsCache.get(threadId);
        if (foundThread != null) {
            return foundThread;
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT A.*, B.`id` AS `last_comment_id` FROM guilds_forums_threads A JOIN (SELECT * FROM `guilds_forums_comments` WHERE `id` IN (SELECT MAX(id) FROM `guilds_forums_comments` B GROUP BY `thread_id` AND b.`id`ORDER BY B.`id` ) ORDER BY `id` DESC ) B ON A.`id` = B.`thread_id` WHERE A.`id` = ? ORDER BY A.`pinned` DESC, B.`created_at` DESC LIMIT 1");){
            statement.setInt(1, threadId);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    foundThread = new ForumThread(set);
                    ForumThread.cacheThread(foundThread);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return foundThread;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void cacheThread(ForumThread thread) {
        THashMap<Integer, ForumThread> tHashMap = forumThreadsCache;
        synchronized (tHashMap) {
            forumThreadsCache.put(thread.threadId, thread);
        }
        THashSet<ForumThread> guildThreads = guildThreadsCache.get(thread.guildId);
        if (guildThreads == null) {
            guildThreads = new THashSet();
            THashMap<Integer, ForumThread> tHashMap2 = forumThreadsCache;
            synchronized (tHashMap2) {
                guildThreadsCache.put(thread.guildId, guildThreads);
            }
        }
        guildThreads.add(thread);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clearCache() {
        for (THashSet<ForumThread> threads : guildThreadsCache.values()) {
            for (ForumThread thread : threads) {
                thread.run();
            }
        }
        THashMap<Integer, THashSet<ForumThread>> tHashMap = forumThreadsCache;
        synchronized (tHashMap) {
            forumThreadsCache.clear();
        }
        tHashMap = guildThreadsCache;
        synchronized (tHashMap) {
            guildThreadsCache.clear();
        }
    }

    public int getThreadId() {
        return this.threadId;
    }

    public int getGuildId() {
        return this.guildId;
    }

    public int getOpenerId() {
        return this.openerId;
    }

    public String getSubject() {
        return this.subject;
    }

    public int getCreatedAt() {
        return this.createdAt;
    }

    public int getPostsCount() {
        return this.postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
        this.needsUpdate = true;
    }

    public int getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
        this.needsUpdate = true;
    }

    public ForumThreadState getState() {
        return this.state;
    }

    public void setState(ForumThreadState state) {
        this.state = state;
        this.needsUpdate = true;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
        this.needsUpdate = true;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        this.needsUpdate = true;
    }

    public int getAdminId() {
        return this.adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
        this.needsUpdate = true;
    }

    public ForumThreadComment getLastComment() {
        return this.lastComment;
    }

    public void setLastComment(ForumThreadComment lastComment) {
        this.lastComment = lastComment;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadComments() {
        if (this.hasCommentsLoaded) {
            return;
        }
        THashMap<Integer, ForumThreadComment> tHashMap = this.comments;
        synchronized (tHashMap) {
            this.hasCommentsLoaded = true;
            this.commentIndex = 0;
            this.comments.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM `guilds_forums_comments` WHERE `thread_id` = ? ORDER BY `id`");){
                statement.setInt(1, this.threadId);
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    ForumThreadComment comment = new ForumThreadComment(set);
                    this.addComment(comment);
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    public void addComment(ForumThreadComment comment) {
        this.comments.put(comment.getCommentId(), comment);
        comment.setIndex(this.commentIndex);
        ++this.commentIndex;
        this.lastComment = comment;
    }

    public Collection<ForumThreadComment> getComments() {
        if (!this.hasCommentsLoaded) {
            this.loadComments();
        }
        return this.comments.values();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Collection<ForumThreadComment> getComments(int limit, int offset) {
        if (!this.hasCommentsLoaded) {
            this.loadComments();
        }
        THashMap<Integer, ForumThreadComment> tHashMap = this.comments;
        synchronized (tHashMap) {
            ArrayList<ForumThreadComment> limitedComments = new ArrayList<ForumThreadComment>();
            ArrayList<ForumThreadComment> comments = new ArrayList<ForumThreadComment>(this.comments.values());
            comments.sort(Comparator.comparingInt(ForumThreadComment::getIndex));
            Iterator iterator = comments.iterator();
            while (offset > 0 && iterator.hasNext()) {
                iterator.next();
                --offset;
            }
            while (limit > 0 && iterator.hasNext()) {
                limitedComments.add((ForumThreadComment)iterator.next());
                --limit;
            }
            return limitedComments;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ForumThreadComment getCommentById(int commentId) {
        if (!this.hasCommentsLoaded) {
            this.loadComments();
        }
        THashMap<Integer, ForumThreadComment> tHashMap = this.comments;
        synchronized (tHashMap) {
            return this.comments.get(commentId);
        }
    }

    @Override
    public void serialize(ServerMessage message) {
        Habbo opener = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.openerId);
        Habbo admin = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.adminId);
        Collection<ForumThreadComment> comments = this.getComments();
        int lastSeenAt = 0;
        int totalComments = comments.size();
        int newComments = 0;
        ForumThreadComment lastComment = this.lastComment;
        if (lastComment == null) {
            for (ForumThreadComment comment : comments) {
                if (comment.getCreatedAt() > lastSeenAt) {
                    ++newComments;
                }
                if (lastComment != null && lastComment.getCreatedAt() >= comment.getCreatedAt()) continue;
                lastComment = comment;
            }
            this.lastComment = lastComment;
        }
        Habbo lastAuthor = lastComment != null ? lastComment.getHabbo() : null;
        int nowTimestamp = Emulator.getIntUnixTimestamp();
        message.appendInt(this.threadId);
        message.appendInt(this.openerId);
        message.appendString(opener != null ? opener.getHabboInfo().getUsername() : "");
        message.appendString(this.subject);
        message.appendBoolean(this.pinned);
        message.appendBoolean(this.locked);
        message.appendInt(nowTimestamp - this.createdAt);
        message.appendInt(totalComments);
        message.appendInt(newComments);
        message.appendInt(1);
        message.appendInt(lastAuthor != null ? lastAuthor.getHabboInfo().getId() : -1);
        message.appendString(lastAuthor != null ? lastAuthor.getHabboInfo().getUsername() : "");
        message.appendInt(nowTimestamp - (lastComment != null ? lastComment.getCreatedAt() : this.updatedAt));
        message.appendByte(this.state.getStateId());
        message.appendInt(this.adminId);
        message.appendString(admin != null ? admin.getHabboInfo().getUsername() : "");
        message.appendInt(this.threadId);
    }

    @Override
    public void run() {
        if (!this.needsUpdate) {
            return;
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE `guilds_forums_threads` SET `posts_count` = ?, `updated_at` = ?, `state` = ?, `pinned` = ?, `locked` = ?, `admin_id` = ? WHERE `id` = ?");){
            statement.setInt(1, this.postsCount);
            statement.setInt(2, this.updatedAt);
            statement.setInt(3, this.state.getStateId());
            statement.setInt(4, this.pinned ? 1 : 0);
            statement.setInt(5, this.locked ? 1 : 0);
            statement.setInt(6, this.adminId);
            statement.setInt(7, this.threadId);
            statement.execute();
            this.needsUpdate = false;
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}

