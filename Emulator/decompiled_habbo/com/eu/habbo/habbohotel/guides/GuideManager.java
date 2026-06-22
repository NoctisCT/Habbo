/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.guides.GuardianTicket;
import com.eu.habbo.habbohotel.guides.GuideRecommendStatus;
import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.guides.GuideSessionAttachedComposer;
import com.eu.habbo.messages.outgoing.guides.GuideSessionDetachedComposer;
import com.eu.habbo.messages.outgoing.guides.GuideSessionEndedComposer;
import com.eu.habbo.messages.outgoing.guides.GuideSessionErrorComposer;
import com.eu.habbo.messages.outgoing.guides.GuideSessionStartedComposer;
import com.eu.habbo.messages.outgoing.guides.GuideToolsComposer;
import com.eu.habbo.threading.runnables.GuardianTicketFindMoreSlaves;
import com.eu.habbo.threading.runnables.GuideFindNewHelper;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.util.Map;

public class GuideManager {
    private final THashSet<GuideTour> activeTours = new THashSet();
    private final THashSet<GuardianTicket> activeTickets = new THashSet();
    private final THashSet<GuardianTicket> closedTickets = new THashSet();
    private final THashMap<Habbo, Boolean> activeHelpers = new THashMap();
    private final THashMap<Habbo, GuardianTicket> activeGuardians = new THashMap();
    private final THashMap<Integer, Integer> tourRequestTiming = new THashMap();

    public void userLogsOut(Habbo habbo) {
        GuideTour tour = this.getGuideTourByHabbo(habbo);
        if (tour != null) {
            this.endSession(tour);
        }
        this.activeHelpers.remove(habbo);
        GuardianTicket ticket = this.getTicketForGuardian(habbo);
        if (ticket != null) {
            ticket.removeGuardian(habbo);
        }
        this.activeGuardians.remove(habbo);
    }

    public void setOnGuide(Habbo habbo, boolean onDuty) {
        if (onDuty) {
            this.activeHelpers.put(habbo, false);
        } else {
            GuideTour tour = this.getGuideTourByHabbo(habbo);
            if (tour != null) {
                return;
            }
            this.activeHelpers.remove(habbo);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean findHelper(GuideTour tour) {
        THashMap<Habbo, Boolean> tHashMap = this.activeHelpers;
        synchronized (tHashMap) {
            for (Map.Entry<Habbo, Boolean> set : this.activeHelpers.entrySet()) {
                if (set.getValue().booleanValue() || tour.hasDeclined(set.getKey().getHabboInfo().getId())) continue;
                ++tour.checkSum;
                tour.setHelper(set.getKey());
                set.getKey().getClient().sendResponse(new GuideSessionAttachedComposer(tour, true));
                tour.getNoob().getClient().sendResponse(new GuideSessionAttachedComposer(tour, false));
                Emulator.getThreading().run(new GuideFindNewHelper(tour, set.getKey()), 60000L);
                this.activeTours.add(tour);
                return true;
            }
        }
        this.endSession(tour);
        tour.getNoob().getClient().sendResponse(new GuideSessionErrorComposer(1));
        return false;
    }

    public void declineTour(GuideTour tour) {
        Habbo helper = tour.getHelper();
        tour.addDeclinedHelper(tour.getHelper().getHabboInfo().getId());
        tour.setHelper(null);
        helper.getClient().sendResponse(new GuideSessionEndedComposer(1));
        helper.getClient().sendResponse(new GuideSessionDetachedComposer());
        if (!this.findHelper(tour)) {
            this.endSession(tour);
            tour.getNoob().getClient().sendResponse(new GuideSessionErrorComposer(1));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void startSession(GuideTour tour, Habbo helper) {
        THashSet<GuideTour> tHashSet = this.activeTours;
        synchronized (tHashSet) {
            THashMap<Habbo, Boolean> tHashMap = this.activeHelpers;
            synchronized (tHashMap) {
                this.activeHelpers.put(helper, true);
                ServerMessage message = new GuideSessionStartedComposer(tour).compose();
                tour.getNoob().getClient().sendResponse(message);
                tour.getHelper().getClient().sendResponse(message);
                ++tour.checkSum;
                this.tourRequestTiming.put(tour.getStartTime(), Emulator.getIntUnixTimestamp());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void endSession(GuideTour tour) {
        THashSet<GuideTour> tHashSet = this.activeTours;
        synchronized (tHashSet) {
            THashMap<Habbo, Boolean> tHashMap = this.activeHelpers;
            synchronized (tHashMap) {
                tour.getNoob().getClient().sendResponse(new GuideSessionEndedComposer(1));
                tour.end();
                if (tour.getHelper() != null) {
                    this.activeHelpers.put(tour.getHelper(), false);
                    tour.getHelper().getClient().sendResponse(new GuideSessionEndedComposer(1));
                    tour.getHelper().getClient().sendResponse(new GuideSessionDetachedComposer());
                    tour.getHelper().getClient().sendResponse(new GuideToolsComposer(true));
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recommend(GuideTour tour, boolean recommend) {
        THashSet<GuideTour> tHashSet = this.activeTours;
        synchronized (tHashSet) {
            tour.setWouldRecommend(recommend ? GuideRecommendStatus.YES : GuideRecommendStatus.NO);
            tour.getNoob().getClient().sendResponse(new GuideSessionDetachedComposer());
            AchievementManager.progressAchievement(tour.getNoob(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("GuideFeedbackGiver"));
            this.activeTours.remove(tour);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GuideTour getGuideTourByHelper(Habbo helper) {
        THashSet<GuideTour> tHashSet = this.activeTours;
        synchronized (tHashSet) {
            for (GuideTour tour : this.activeTours) {
                if (tour.isEnded() || tour.getHelper() != helper) continue;
                return tour;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GuideTour getGuideTourByNoob(Habbo noob) {
        THashSet<GuideTour> tHashSet = this.activeTours;
        synchronized (tHashSet) {
            for (GuideTour tour : this.activeTours) {
                if (tour.getNoob() != noob) continue;
                return tour;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GuideTour getGuideTourByHabbo(Habbo habbo) {
        THashSet<GuideTour> tHashSet = this.activeTours;
        synchronized (tHashSet) {
            for (GuideTour tour : this.activeTours) {
                if (tour.getHelper() != habbo && tour.getNoob() != habbo) continue;
                return tour;
            }
        }
        return null;
    }

    public int getGuidesCount() {
        return this.activeHelpers.size();
    }

    public int getGuardiansCount() {
        return this.activeGuardians.size();
    }

    public boolean activeGuardians() {
        return this.activeGuardians.size() > 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getAverageWaitingTime() {
        THashMap<Integer, Integer> tHashMap = this.tourRequestTiming;
        synchronized (tHashMap) {
            int total = 0;
            if (this.tourRequestTiming.isEmpty()) {
                return 5;
            }
            for (Map.Entry<Integer, Integer> set : this.tourRequestTiming.entrySet()) {
                total += set.getValue() - set.getKey();
            }
            return total / this.tourRequestTiming.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addGuardianTicket(GuardianTicket ticket) {
        THashSet<GuardianTicket> tHashSet = this.activeTickets;
        synchronized (tHashSet) {
            this.activeTickets.add(ticket);
            this.findGuardians(ticket);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void findGuardians(GuardianTicket ticket) {
        THashMap<Habbo, GuardianTicket> tHashMap = this.activeGuardians;
        synchronized (tHashMap) {
            int count = ticket.getVotedCount();
            THashSet<Habbo> selectedGuardians = new THashSet<Habbo>();
            for (Map.Entry<Habbo, GuardianTicket> set : this.activeGuardians.entrySet()) {
                if (count == 5) break;
                if (set.getKey() == ticket.getReporter() || set.getKey() == ticket.getReported()) continue;
                if (set.getValue() == null && ticket.getVoteForGuardian(set.getKey()) == null) {
                    ticket.requestToVote(set.getKey());
                    selectedGuardians.add(set.getKey());
                }
                ++count;
            }
            for (Habbo habbo : selectedGuardians) {
                this.activeGuardians.put(habbo, ticket);
            }
            if (count < 5) {
                Emulator.getThreading().run(new GuardianTicketFindMoreSlaves(ticket), 3000L);
            }
        }
    }

    public void acceptTicket(Habbo guardian, boolean accepted) {
        GuardianTicket ticket = this.getTicketForGuardian(guardian);
        if (ticket != null) {
            if (!accepted) {
                ticket.removeGuardian(guardian);
                this.findGuardians(ticket);
                this.activeGuardians.put(guardian, null);
            } else {
                ticket.addGuardian(guardian);
                this.activeGuardians.put(guardian, ticket);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GuardianTicket getTicketForGuardian(Habbo guardian) {
        THashMap<Habbo, GuardianTicket> tHashMap = this.activeGuardians;
        synchronized (tHashMap) {
            return this.activeGuardians.get(guardian);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GuardianTicket getRecentTicket(Habbo reporter) {
        GuardianTicket ticket = null;
        THashSet<GuardianTicket> tHashSet = this.activeTickets;
        synchronized (tHashSet) {
            for (GuardianTicket t : this.activeTickets) {
                if (t.getReporter() != reporter) continue;
                return t;
            }
        }
        tHashSet = this.closedTickets;
        synchronized (tHashSet) {
            for (GuardianTicket t : this.closedTickets) {
                if (t.getReporter() != reporter || ticket != null && (long)Emulator.getIntUnixTimestamp() - t.getDate().getTime() / 1000L >= (long)Emulator.getIntUnixTimestamp() - ticket.getDate().getTime() / 1000L) continue;
                ticket = t;
            }
        }
        return ticket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GuardianTicket getOpenReportedHabboTicket(Habbo reported) {
        THashSet<GuardianTicket> tHashSet = this.activeTickets;
        synchronized (tHashSet) {
            for (GuardianTicket t : this.activeTickets) {
                if (t.getReported() != reported) continue;
                return t;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void closeTicket(GuardianTicket ticket) {
        THashSet<GuardianTicket> tHashSet = this.activeTickets;
        synchronized (tHashSet) {
            this.activeTickets.remove(ticket);
        }
        tHashSet = this.closedTickets;
        synchronized (tHashSet) {
            this.closedTickets.add(ticket);
        }
        THashSet<Habbo> toUpdate = new THashSet<Habbo>();
        THashMap<Habbo, GuardianTicket> tHashMap = this.activeGuardians;
        synchronized (tHashMap) {
            for (Map.Entry<Habbo, GuardianTicket> set : this.activeGuardians.entrySet()) {
                if (set.getValue() != ticket) continue;
                toUpdate.add(set.getKey());
            }
            for (Habbo habbo : toUpdate) {
                this.activeGuardians.put(habbo, null);
            }
        }
    }

    public void setOnGuardian(Habbo habbo, boolean onDuty) {
        if (onDuty) {
            this.activeGuardians.put(habbo, null);
        } else {
            GuardianTicket ticket = this.getTicketForGuardian(habbo);
            if (ticket != null) {
                ticket.removeGuardian(habbo);
            }
            this.activeGuardians.remove(habbo);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cleanup() {
        THashSet<Object> tHashSet = this.activeTours;
        synchronized (tHashSet) {
            THashSet<GuideTour> tours = new THashSet<GuideTour>();
            for (GuideTour tour : this.activeTours) {
                if (!tour.isEnded() || Emulator.getIntUnixTimestamp() - tour.getEndTime() <= 300) continue;
                tours.add(tour);
            }
            for (GuideTour tour : tours) {
                this.activeTours.remove(tour);
            }
        }
        tHashSet = this.activeTickets;
        synchronized (tHashSet) {
            THashSet<GuardianTicket> tickets = new THashSet<GuardianTicket>();
            for (GuardianTicket ticket : this.closedTickets) {
                if ((long)Emulator.getIntUnixTimestamp() - ticket.getDate().getTime() / 1000L <= 900L) continue;
                tickets.add(ticket);
            }
            for (GuardianTicket ticket : tickets) {
                this.closedTickets.remove(ticket);
            }
        }
    }
}

