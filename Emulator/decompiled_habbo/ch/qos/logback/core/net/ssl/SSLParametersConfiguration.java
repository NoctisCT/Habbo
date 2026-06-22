/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.net.ssl.SSLConfigurable;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StringCollectionUtil;
import java.util.ArrayList;
import java.util.Arrays;

public class SSLParametersConfiguration
extends ContextAwareBase {
    private String includedProtocols;
    private String excludedProtocols;
    private String includedCipherSuites;
    private String excludedCipherSuites;
    private Boolean needClientAuth;
    private Boolean wantClientAuth;
    private String[] enabledProtocols;
    private String[] enabledCipherSuites;

    public void configure(SSLConfigurable socket) {
        socket.setEnabledProtocols(this.enabledProtocols(socket.getSupportedProtocols(), socket.getDefaultProtocols()));
        socket.setEnabledCipherSuites(this.enabledCipherSuites(socket.getSupportedCipherSuites(), socket.getDefaultCipherSuites()));
        if (this.isNeedClientAuth() != null) {
            socket.setNeedClientAuth(this.isNeedClientAuth());
        }
        if (this.isWantClientAuth() != null) {
            socket.setWantClientAuth(this.isWantClientAuth());
        }
    }

    private String[] enabledProtocols(String[] supportedProtocols, String[] defaultProtocols) {
        if (this.enabledProtocols == null) {
            this.enabledProtocols = OptionHelper.isEmpty(this.getIncludedProtocols()) && OptionHelper.isEmpty(this.getExcludedProtocols()) ? Arrays.copyOf(defaultProtocols, defaultProtocols.length) : this.includedStrings(supportedProtocols, this.getIncludedProtocols(), this.getExcludedProtocols());
            for (String protocol : this.enabledProtocols) {
                this.addInfo("enabled protocol: " + protocol);
            }
        }
        return this.enabledProtocols;
    }

    private String[] enabledCipherSuites(String[] supportedCipherSuites, String[] defaultCipherSuites) {
        if (this.enabledCipherSuites == null) {
            this.enabledCipherSuites = OptionHelper.isEmpty(this.getIncludedCipherSuites()) && OptionHelper.isEmpty(this.getExcludedCipherSuites()) ? Arrays.copyOf(defaultCipherSuites, defaultCipherSuites.length) : this.includedStrings(supportedCipherSuites, this.getIncludedCipherSuites(), this.getExcludedCipherSuites());
            for (String cipherSuite : this.enabledCipherSuites) {
                this.addInfo("enabled cipher suite: " + cipherSuite);
            }
        }
        return this.enabledCipherSuites;
    }

    private String[] includedStrings(String[] defaults, String included, String excluded) {
        ArrayList<String> values = new ArrayList<String>(defaults.length);
        values.addAll(Arrays.asList(defaults));
        if (included != null) {
            StringCollectionUtil.retainMatching(values, this.stringToArray(included));
        }
        if (excluded != null) {
            StringCollectionUtil.removeMatching(values, this.stringToArray(excluded));
        }
        return values.toArray(new String[values.size()]);
    }

    private String[] stringToArray(String s) {
        return s.split("\\s*,\\s*");
    }

    public String getIncludedProtocols() {
        return this.includedProtocols;
    }

    public void setIncludedProtocols(String protocols) {
        this.includedProtocols = protocols;
    }

    public String getExcludedProtocols() {
        return this.excludedProtocols;
    }

    public void setExcludedProtocols(String protocols) {
        this.excludedProtocols = protocols;
    }

    public String getIncludedCipherSuites() {
        return this.includedCipherSuites;
    }

    public void setIncludedCipherSuites(String cipherSuites) {
        this.includedCipherSuites = cipherSuites;
    }

    public String getExcludedCipherSuites() {
        return this.excludedCipherSuites;
    }

    public void setExcludedCipherSuites(String cipherSuites) {
        this.excludedCipherSuites = cipherSuites;
    }

    public Boolean isNeedClientAuth() {
        return this.needClientAuth;
    }

    public void setNeedClientAuth(Boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    public Boolean isWantClientAuth() {
        return this.wantClientAuth;
    }

    public void setWantClientAuth(Boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }
}

