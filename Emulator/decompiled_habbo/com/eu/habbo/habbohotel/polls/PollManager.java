/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.polls;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.habbohotel.polls.PollQuestion;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PollManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PollManager.class);
    private final THashMap<Integer, Poll> activePolls = new THashMap();

    public PollManager() {
        this.loadPolls();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean donePoll(Habbo habbo, int pollId) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT NULL FROM polls_answers WHERE poll_id = ? AND user_id = ? LIMIT 1");){
            statement.setInt(1, pollId);
            statement.setInt(2, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery();){
                if (!set.isBeforeFirst()) return false;
                boolean bl = true;
                return bl;
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadPolls() {
        THashMap<Integer, Poll> tHashMap = this.activePolls;
        synchronized (tHashMap) {
            this.activePolls.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 Statement statement = connection.createStatement();){
                try (ResultSet set = statement.executeQuery("SELECT * FROM polls");){
                    while (set.next()) {
                        this.activePolls.put(set.getInt("id"), new Poll(set));
                    }
                }
                set = statement.executeQuery("SELECT * FROM polls_questions ORDER BY parent_id, `order` ASC");
                try {
                    while (set.next()) {
                        Poll poll = this.getPoll(set.getInt("poll_id"));
                        if (poll == null) continue;
                        PollQuestion question = new PollQuestion(set);
                        if (set.getInt("parent_id") <= 0) {
                            poll.addQuestion(question);
                        } else {
                            PollQuestion parentQuestion = poll.getQuestion(set.getInt("parent_id"));
                            if (parentQuestion != null) {
                                parentQuestion.addSubQuestion(question);
                            }
                        }
                        poll.lastQuestionId = question.id;
                    }
                }
                finally {
                    if (set != null) {
                        set.close();
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    public Poll getPoll(int pollId) {
        return this.activePolls.get(pollId);
    }
}

