/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.conf.url;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.ConnectionUrlParser;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.HostsListView;
import com.mysql.cj.conf.PropertyKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ReplicationConnectionUrl
extends ConnectionUrl {
    private static final String TYPE_MASTER = "MASTER";
    private static final String TYPE_SLAVE = "SLAVE";
    private List<HostInfo> masterHosts = new ArrayList<HostInfo>();
    private List<HostInfo> slaveHosts = new ArrayList<HostInfo>();

    public ReplicationConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
        super(connStrParser, info);
        this.type = ConnectionUrl.Type.REPLICATION_CONNECTION;
        LinkedList<HostInfo> undefinedHosts = new LinkedList<HostInfo>();
        for (HostInfo hi : this.hosts) {
            Map<String, String> hostProperties = hi.getHostProperties();
            if (hostProperties.containsKey(PropertyKey.TYPE.getKeyName())) {
                if (TYPE_MASTER.equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
                    this.masterHosts.add(hi);
                    continue;
                }
                if (TYPE_SLAVE.equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
                    this.slaveHosts.add(hi);
                    continue;
                }
                undefinedHosts.add(hi);
                continue;
            }
            undefinedHosts.add(hi);
        }
        if (!undefinedHosts.isEmpty()) {
            if (this.masterHosts.isEmpty()) {
                this.masterHosts.add((HostInfo)undefinedHosts.removeFirst());
            }
            this.slaveHosts.addAll(undefinedHosts);
        }
    }

    public ReplicationConnectionUrl(List<HostInfo> masters, List<HostInfo> slaves, Map<String, String> properties) {
        this.originalConnStr = ConnectionUrl.Type.REPLICATION_CONNECTION.getScheme() + "//**internally_generated**" + System.currentTimeMillis() + "**";
        this.originalDatabase = properties.containsKey(PropertyKey.DBNAME.getKeyName()) ? properties.get(PropertyKey.DBNAME.getKeyName()) : "";
        this.type = ConnectionUrl.Type.REPLICATION_CONNECTION;
        this.properties.putAll(properties);
        this.injectPerTypeProperties(this.properties);
        this.setupPropertiesTransformer();
        masters.stream().map(this::fixHostInfo).peek(this.masterHosts::add).forEach(this.hosts::add);
        slaves.stream().map(this::fixHostInfo).peek(this.slaveHosts::add).forEach(this.hosts::add);
    }

    @Override
    public List<HostInfo> getHostsList(HostsListView view) {
        switch (view) {
            case MASTERS: {
                return Collections.unmodifiableList(this.masterHosts);
            }
            case SLAVES: {
                return Collections.unmodifiableList(this.slaveHosts);
            }
        }
        return super.getHostsList(HostsListView.ALL);
    }

    public HostInfo getMasterHostOrSpawnIsolated(String hostPortPair) {
        return super.getHostOrSpawnIsolated(hostPortPair, this.masterHosts);
    }

    public List<String> getMastersListAsHostPortPairs() {
        return this.masterHosts.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList());
    }

    public List<HostInfo> getMasterHostsListFromHostPortPairs(Collection<String> hostPortPairs) {
        return hostPortPairs.stream().map(this::getMasterHostOrSpawnIsolated).collect(Collectors.toList());
    }

    public HostInfo getSlaveHostOrSpawnIsolated(String hostPortPair) {
        return super.getHostOrSpawnIsolated(hostPortPair, this.slaveHosts);
    }

    public List<String> getSlavesListAsHostPortPairs() {
        return this.slaveHosts.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList());
    }

    public List<HostInfo> getSlaveHostsListFromHostPortPairs(Collection<String> hostPortPairs) {
        return hostPortPairs.stream().map(this::getSlaveHostOrSpawnIsolated).collect(Collectors.toList());
    }
}

