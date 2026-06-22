/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.protocol.a.authentication;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.sasl.ScramSha1SaslProvider;
import java.security.Security;
import java.util.List;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class AuthenticationLdapSaslClientPlugin
implements AuthenticationPlugin<NativePacketPayload> {
    private AuthenticationMechanisms authMech;
    private SaslClient saslClient;
    private String user;
    private String password;

    @Override
    public void init(Protocol<NativePacketPayload> prot) {
    }

    @Override
    public void reset() {
        if (this.saslClient != null) {
            try {
                this.saslClient.dispose();
                this.saslClient = null;
            }
            catch (SaslException saslException) {
                // empty catch block
            }
        }
        this.authMech = null;
        this.saslClient = null;
        this.user = null;
        this.password = null;
    }

    @Override
    public void destroy() {
        this.reset();
    }

    @Override
    public String getProtocolPluginName() {
        return "authentication_ldap_sasl_client";
    }

    @Override
    public boolean requiresConfidentiality() {
        return false;
    }

    @Override
    public boolean isReusable() {
        return false;
    }

    @Override
    public void setAuthenticationParameters(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
        toServer.clear();
        try {
            byte[] response;
            if (this.saslClient == null) {
                this.authMech = AuthenticationMechanisms.fromValue(fromServer.readString(NativeConstants.StringSelfDataType.STRING_EOF, "ASCII"));
                CallbackHandler cbh = cbs -> {
                    for (Callback cb : cbs) {
                        if (NameCallback.class.isAssignableFrom(cb.getClass())) {
                            ((NameCallback)cb).setName(this.user);
                            continue;
                        }
                        if (PasswordCallback.class.isAssignableFrom(cb.getClass())) {
                            ((PasswordCallback)cb).setPassword(this.password.toCharArray());
                            continue;
                        }
                        throw new UnsupportedCallbackException(cb);
                    }
                };
                this.saslClient = Sasl.createSaslClient(new String[]{this.authMech.getSaslServiceName()}, null, null, null, null, cbh);
                if (this.saslClient == null) {
                    throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.FailCreateSaslClient", new Object[]{this.authMech.getMechName()}));
                }
            }
            if ((response = this.saslClient.evaluateChallenge(fromServer.readBytes(NativeConstants.StringSelfDataType.STRING_EOF))) != null) {
                NativePacketPayload bresp = new NativePacketPayload(response);
                bresp.setPosition(0);
                toServer.add(bresp);
            }
        }
        catch (SaslException e) {
            throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.ErrProcessingAuthIter", new Object[]{this.authMech.getMechName()}), e);
        }
        return true;
    }

    static {
        Security.addProvider(new ScramSha1SaslProvider());
    }

    private static enum AuthenticationMechanisms {
        SCRAM_SHA_1("SCRAM-SHA-1", "MYSQLCJ-SCRAM-SHA-1"),
        GSSAPI("GSSAPI", "GSSAPI");

        private String mechName;
        private String saslServiceName;

        private AuthenticationMechanisms(String mechName, String serviceName) {
            this.mechName = mechName;
            this.saslServiceName = serviceName;
        }

        static AuthenticationMechanisms fromValue(String mechName) {
            for (AuthenticationMechanisms am : AuthenticationMechanisms.values()) {
                if (am == GSSAPI || !am.mechName.equalsIgnoreCase(mechName)) continue;
                return am;
            }
            throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.UnsupportedAuthMech"));
        }

        String getMechName() {
            return this.mechName;
        }

        String getSaslServiceName() {
            return this.saslServiceName;
        }
    }
}

