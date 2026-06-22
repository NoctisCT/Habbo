/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ipfilter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class RuleBasedIpFilter
extends AbstractRemoteAddressFilter<InetSocketAddress> {
    private final IpFilterRule[] rules;

    public RuleBasedIpFilter(IpFilterRule ... rules) {
        this.rules = ObjectUtil.checkNotNull(rules, "rules");
    }

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
        for (IpFilterRule rule : this.rules) {
            if (rule == null) break;
            if (!rule.matches(remoteAddress)) continue;
            return rule.ruleType() == IpFilterRuleType.ACCEPT;
        }
        return true;
    }
}

