/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.ha.ReplicationConnectionGroup;
import com.mysql.cj.jdbc.jmx.ReplicationGroupManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class ReplicationConnectionGroupManager {
    private static HashMap<String, ReplicationConnectionGroup> GROUP_MAP = new HashMap();
    private static ReplicationGroupManager mbean = new ReplicationGroupManager();
    private static boolean hasRegisteredJmx = false;

    public static synchronized ReplicationConnectionGroup getConnectionGroupInstance(String groupName) {
        if (GROUP_MAP.containsKey(groupName)) {
            return GROUP_MAP.get(groupName);
        }
        ReplicationConnectionGroup group = new ReplicationConnectionGroup(groupName);
        GROUP_MAP.put(groupName, group);
        return group;
    }

    public static void registerJmx() throws SQLException {
        if (hasRegisteredJmx) {
            return;
        }
        mbean.registerJmx();
        hasRegisteredJmx = true;
    }

    public static ReplicationConnectionGroup getConnectionGroup(String groupName) {
        return GROUP_MAP.get(groupName);
    }

    public static Collection<ReplicationConnectionGroup> getGroupsMatching(String group) {
        if (group == null || group.equals("")) {
            HashSet<ReplicationConnectionGroup> s = new HashSet<ReplicationConnectionGroup>();
            s.addAll(GROUP_MAP.values());
            return s;
        }
        HashSet<ReplicationConnectionGroup> s = new HashSet<ReplicationConnectionGroup>();
        ReplicationConnectionGroup o = GROUP_MAP.get(group);
        if (o != null) {
            s.add(o);
        }
        return s;
    }

    public static void addSlaveHost(String group, String hostPortPair) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(group);
        for (ReplicationConnectionGroup cg : s) {
            cg.addSlaveHost(hostPortPair);
        }
    }

    public static void removeSlaveHost(String group, String hostPortPair) throws SQLException {
        ReplicationConnectionGroupManager.removeSlaveHost(group, hostPortPair, true);
    }

    public static void removeSlaveHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(group);
        for (ReplicationConnectionGroup cg : s) {
            cg.removeSlaveHost(hostPortPair, closeGently);
        }
    }

    public static void promoteSlaveToMaster(String group, String hostPortPair) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(group);
        for (ReplicationConnectionGroup cg : s) {
            cg.promoteSlaveToMaster(hostPortPair);
        }
    }

    public static long getSlavePromotionCount(String group) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(group);
        long promoted = 0L;
        for (ReplicationConnectionGroup cg : s) {
            long tmp = cg.getNumberOfSlavePromotions();
            if (tmp <= promoted) continue;
            promoted = tmp;
        }
        return promoted;
    }

    public static void removeMasterHost(String group, String hostPortPair) throws SQLException {
        ReplicationConnectionGroupManager.removeMasterHost(group, hostPortPair, true);
    }

    public static void removeMasterHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(group);
        for (ReplicationConnectionGroup cg : s) {
            cg.removeMasterHost(hostPortPair, closeGently);
        }
    }

    public static String getRegisteredReplicationConnectionGroups() {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(null);
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (ReplicationConnectionGroup cg : s) {
            String group = cg.getGroupName();
            sb.append(sep);
            sb.append(group);
            sep = ",";
        }
        return sb.toString();
    }

    public static int getNumberOfMasterPromotion(String groupFilter) {
        int total = 0;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(groupFilter);
        for (ReplicationConnectionGroup cg : s) {
            total = (int)((long)total + cg.getNumberOfSlavePromotions());
        }
        return total;
    }

    public static int getConnectionCountWithHostAsSlave(String groupFilter, String hostPortPair) {
        int total = 0;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(groupFilter);
        for (ReplicationConnectionGroup cg : s) {
            total += cg.getConnectionCountWithHostAsSlave(hostPortPair);
        }
        return total;
    }

    public static int getConnectionCountWithHostAsMaster(String groupFilter, String hostPortPair) {
        int total = 0;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(groupFilter);
        for (ReplicationConnectionGroup cg : s) {
            total += cg.getConnectionCountWithHostAsMaster(hostPortPair);
        }
        return total;
    }

    public static Collection<String> getSlaveHosts(String groupFilter) {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(groupFilter);
        ArrayList<String> hosts = new ArrayList<String>();
        for (ReplicationConnectionGroup cg : s) {
            hosts.addAll(cg.getSlaveHosts());
        }
        return hosts;
    }

    public static Collection<String> getMasterHosts(String groupFilter) {
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(groupFilter);
        ArrayList<String> hosts = new ArrayList<String>();
        for (ReplicationConnectionGroup cg : s) {
            hosts.addAll(cg.getMasterHosts());
        }
        return hosts;
    }

    public static long getTotalConnectionCount(String group) {
        long connections = 0L;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(group);
        for (ReplicationConnectionGroup cg : s) {
            connections += cg.getTotalConnectionCount();
        }
        return connections;
    }

    public static long getActiveConnectionCount(String group) {
        long connections = 0L;
        Collection<ReplicationConnectionGroup> s = ReplicationConnectionGroupManager.getGroupsMatching(group);
        for (ReplicationConnectionGroup cg : s) {
            connections += cg.getActiveConnectionCount();
        }
        return connections;
    }
}

