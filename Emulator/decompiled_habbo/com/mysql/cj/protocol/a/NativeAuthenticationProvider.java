/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.protocol.a;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.AuthenticationProvider;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.protocol.a.NativeCapabilities;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.authentication.AuthenticationLdapSaslClientPlugin;
import com.mysql.cj.protocol.a.authentication.CachingSha2PasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlClearPasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlNativePasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlOldPasswordPlugin;
import com.mysql.cj.protocol.a.authentication.Sha256PasswordPlugin;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NativeAuthenticationProvider
implements AuthenticationProvider<NativePacketPayload> {
    protected static final int AUTH_411_OVERHEAD = 33;
    private static final String NONE = "none";
    protected String seed;
    private boolean useConnectWithDb;
    private ExceptionInterceptor exceptionInterceptor;
    private PropertySet propertySet;
    private Protocol<NativePacketPayload> protocol;
    private Map<String, AuthenticationPlugin<NativePacketPayload>> authenticationPlugins = null;
    private List<String> disabledAuthenticationPlugins = null;
    private String clientDefaultAuthenticationPlugin = null;
    private String clientDefaultAuthenticationPluginName = null;
    private String serverDefaultAuthenticationPluginName = null;

    @Override
    public void init(Protocol<NativePacketPayload> prot, PropertySet propSet, ExceptionInterceptor excInterceptor) {
        this.protocol = prot;
        this.propertySet = propSet;
        this.exceptionInterceptor = excInterceptor;
    }

    @Override
    public void connect(ServerSession sessState, String user, String password, String database) {
        NativeCapabilities capabilities = (NativeCapabilities)sessState.getCapabilities();
        NativePacketPayload buf = capabilities.getInitialHandshakePacket();
        PropertyDefinitions.SslMode sslMode = (PropertyDefinitions.SslMode)((Object)this.propertySet.getEnumProperty(PropertyKey.sslMode).getValue());
        int capabilityFlags = capabilities.getCapabilityFlags();
        if ((capabilityFlags & 0x800) == 0 && sslMode != PropertyDefinitions.SslMode.DISABLED && sslMode != PropertyDefinitions.SslMode.PREFERRED) {
            throw ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("MysqlIO.15"), this.getExceptionInterceptor());
        }
        if ((capabilityFlags & 0x8000) == 0) {
            throw ExceptionFactory.createException(UnableToConnectException.class, "CLIENT_SECURE_CONNECTION is required", this.getExceptionInterceptor());
        }
        if ((capabilityFlags & 0x80000) == 0) {
            throw ExceptionFactory.createException(UnableToConnectException.class, "CLIENT_PLUGIN_AUTH is required", this.getExceptionInterceptor());
        }
        sessState.setServerDefaultCollationIndex(capabilities.getServerDefaultCollationIndex());
        sessState.setStatusFlags(capabilities.getStatusFlags());
        int authPluginDataLength = capabilities.getAuthPluginDataLength();
        StringBuilder fullSeed = new StringBuilder(authPluginDataLength > 0 ? authPluginDataLength : 20);
        fullSeed.append(capabilities.getSeed());
        fullSeed.append(authPluginDataLength > 0 ? buf.readString(NativeConstants.StringLengthDataType.STRING_FIXED, "ASCII", authPluginDataLength - 8) : buf.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII"));
        this.seed = fullSeed.toString();
        this.useConnectWithDb = database != null && database.length() > 0 && this.propertySet.getBooleanProperty(PropertyKey.createDatabaseIfNotExist).getValue() == false;
        long clientParam = 0x88000 | capabilityFlags & 1 | capabilityFlags & 0x200 | capabilityFlags & 0x2000 | capabilityFlags & 0x20000 | capabilityFlags & 0x40000 | capabilityFlags & 4 | capabilityFlags & 0x1000000 | capabilityFlags & 0x200000 | (this.propertySet.getBooleanProperty(PropertyKey.useCompression).getValue() != false ? capabilityFlags & 0x20 : 0) | (this.useConnectWithDb ? capabilityFlags & 8 : 0) | (this.propertySet.getBooleanProperty(PropertyKey.useAffectedRows).getValue() != false ? 0 : capabilityFlags & 2) | (this.propertySet.getBooleanProperty(PropertyKey.allowLoadLocalInfile).getValue() != false || this.propertySet.getStringProperty(PropertyKey.allowLoadLocalInfileInPath).isExplicitlySet() ? capabilityFlags & 0x80 : 0) | (this.propertySet.getBooleanProperty(PropertyKey.interactiveClient).getValue() != false ? capabilityFlags & 0x400 : 0) | (this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue() != false ? capabilityFlags & 0x10000 : 0) | (this.propertySet.getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords).getValue() != false ? 0 : capabilityFlags & 0x400000) | (NONE.equals(this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue()) ? 0 : capabilityFlags & 0x100000) | (this.propertySet.getEnumProperty(PropertyKey.sslMode).getValue() != PropertyDefinitions.SslMode.DISABLED ? capabilityFlags & 0x800 : 0);
        sessState.setClientParam(clientParam);
        if ((clientParam & 0x800L) != 0L) {
            this.protocol.negotiateSSLConnection();
        }
        if (buf.isOKPacket()) {
            throw ExceptionFactory.createException(Messages.getString("AuthenticationProvider.UnexpectedAuthenticationApproval"), this.getExceptionInterceptor());
        }
        this.proceedHandshakeWithPluggableAuthentication(sessState, user, password, database, buf);
    }

    private void loadAuthenticationPlugins() {
        this.clientDefaultAuthenticationPlugin = this.propertySet.getStringProperty(PropertyKey.defaultAuthenticationPlugin).getValue();
        if (this.clientDefaultAuthenticationPlugin == null || "".equals(this.clientDefaultAuthenticationPlugin.trim())) {
            throw ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadDefaultAuthenticationPlugin", new Object[]{this.clientDefaultAuthenticationPlugin}), this.getExceptionInterceptor());
        }
        String disabledPlugins = this.propertySet.getStringProperty(PropertyKey.disabledAuthenticationPlugins).getValue();
        if (disabledPlugins != null && !"".equals(disabledPlugins)) {
            this.disabledAuthenticationPlugins = new ArrayList<String>();
            List<String> pluginsToDisable = StringUtils.split(disabledPlugins, ",", true);
            Iterator<String> iter = pluginsToDisable.iterator();
            while (iter.hasNext()) {
                this.disabledAuthenticationPlugins.add(iter.next());
            }
        }
        this.authenticationPlugins = new HashMap<String, AuthenticationPlugin<NativePacketPayload>>();
        boolean defaultIsFound = false;
        LinkedList<AuthenticationPlugin> pluginsToInit = new LinkedList<AuthenticationPlugin>();
        pluginsToInit.add(new MysqlNativePasswordPlugin());
        pluginsToInit.add(new MysqlClearPasswordPlugin());
        pluginsToInit.add(new Sha256PasswordPlugin());
        pluginsToInit.add(new CachingSha2PasswordPlugin());
        pluginsToInit.add(new MysqlOldPasswordPlugin());
        pluginsToInit.add(new AuthenticationLdapSaslClientPlugin());
        String authenticationPluginClasses = this.propertySet.getStringProperty(PropertyKey.authenticationPlugins).getValue();
        if (authenticationPluginClasses != null && !"".equals(authenticationPluginClasses)) {
            List<String> pluginsToCreate = StringUtils.split(authenticationPluginClasses, ",", true);
            String className = null;
            try {
                int s = pluginsToCreate.size();
                for (int i = 0; i < s; ++i) {
                    className = pluginsToCreate.get(i);
                    pluginsToInit.add((AuthenticationPlugin)Class.forName(className).newInstance());
                }
            }
            catch (Throwable t) {
                throw ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{className}), t, this.exceptionInterceptor);
            }
        }
        for (AuthenticationPlugin plugin : pluginsToInit) {
            plugin.init(this.protocol);
            if (!this.addAuthenticationPlugin(plugin)) continue;
            defaultIsFound = true;
        }
        if (!defaultIsFound) {
            throw ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.DefaultAuthenticationPluginIsNotListed", new Object[]{this.clientDefaultAuthenticationPlugin}), this.getExceptionInterceptor());
        }
    }

    private boolean addAuthenticationPlugin(AuthenticationPlugin<NativePacketPayload> plugin) {
        boolean disabledByMechanism;
        boolean isDefault = false;
        String pluginClassName = plugin.getClass().getName();
        String pluginProtocolName = plugin.getProtocolPluginName();
        boolean disabledByClassName = this.disabledAuthenticationPlugins != null && this.disabledAuthenticationPlugins.contains(pluginClassName);
        boolean bl = disabledByMechanism = this.disabledAuthenticationPlugins != null && this.disabledAuthenticationPlugins.contains(pluginProtocolName);
        if (disabledByClassName || disabledByMechanism) {
            if (this.clientDefaultAuthenticationPlugin.equals(pluginClassName)) {
                throw ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadDisabledAuthenticationPlugin", new Object[]{disabledByClassName ? pluginClassName : pluginProtocolName}), this.getExceptionInterceptor());
            }
        } else {
            this.authenticationPlugins.put(pluginProtocolName, plugin);
            if (this.clientDefaultAuthenticationPlugin.equals(pluginClassName)) {
                this.clientDefaultAuthenticationPluginName = pluginProtocolName;
                isDefault = true;
            }
        }
        return isDefault;
    }

    private AuthenticationPlugin<NativePacketPayload> getAuthenticationPlugin(String pluginName) {
        AuthenticationPlugin plugin = this.authenticationPlugins.get(pluginName);
        if (plugin != null && !plugin.isReusable()) {
            try {
                plugin = (AuthenticationPlugin)plugin.getClass().newInstance();
                plugin.init(this.protocol);
            }
            catch (Throwable t) {
                throw ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{plugin.getClass().getName()}), t, this.getExceptionInterceptor());
            }
        }
        return plugin;
    }

    private void checkConfidentiality(AuthenticationPlugin<?> plugin) {
        if (plugin.requiresConfidentiality() && !this.protocol.getSocketConnection().isSSLEstablished()) {
            throw ExceptionFactory.createException(Messages.getString("AuthenticationProvider.AuthenticationPluginRequiresSSL", new Object[]{plugin.getProtocolPluginName()}), this.getExceptionInterceptor());
        }
    }

    private void proceedHandshakeWithPluggableAuthentication(ServerSession serverSession, String user, String password, String database, NativePacketPayload challenge) {
        boolean isChangeUser;
        if (this.authenticationPlugins == null) {
            this.loadAuthenticationPlugins();
        }
        boolean bl = isChangeUser = challenge == null;
        String pluginName = isChangeUser ? (this.serverDefaultAuthenticationPluginName == null ? this.clientDefaultAuthenticationPluginName : this.serverDefaultAuthenticationPluginName) : (!this.protocol.versionMeetsMinimum(5, 5, 10) || this.protocol.versionMeetsMinimum(5, 6, 0) && !this.protocol.versionMeetsMinimum(5, 6, 2) ? challenge.readString(NativeConstants.StringLengthDataType.STRING_FIXED, "ASCII", ((NativeCapabilities)serverSession.getCapabilities()).getAuthPluginDataLength()) : challenge.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII"));
        AuthenticationPlugin<NativePacketPayload> plugin = this.getAuthenticationPlugin(pluginName);
        boolean skipPassword = false;
        if (plugin == null) {
            plugin = this.getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
        } else if (pluginName.equals(Sha256PasswordPlugin.PLUGIN_NAME) && !this.protocol.getSocketConnection().isSSLEstablished() && this.propertySet.getStringProperty(PropertyKey.serverRSAPublicKeyFile).getValue() == null && !this.propertySet.getBooleanProperty(PropertyKey.allowPublicKeyRetrieval).getValue().booleanValue()) {
            plugin = this.getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
            skipPassword = !this.clientDefaultAuthenticationPluginName.equals(pluginName);
        }
        this.serverDefaultAuthenticationPluginName = plugin.getProtocolPluginName();
        this.checkConfidentiality(plugin);
        NativePacketPayload fromServer = new NativePacketPayload(StringUtils.getBytes(this.seed));
        boolean old_raw_challenge = false;
        NativePacketPayload last_sent = null;
        NativePacketPayload last_received = challenge;
        ArrayList<NativePacketPayload> toServer = new ArrayList<NativePacketPayload>();
        int counter = 100;
        while (0 < counter--) {
            plugin.setAuthenticationParameters(user, skipPassword ? null : password);
            plugin.nextAuthenticationStep(fromServer, toServer);
            if (toServer.size() > 0) {
                if (isChangeUser) {
                    last_sent = this.createChangeUserPacket(serverSession, user, database, plugin.getProtocolPluginName(), toServer);
                    this.protocol.send(last_sent, last_sent.getPosition());
                    isChangeUser = false;
                } else if (last_received.isAuthMethodSwitchRequestPacket() || last_received.isAuthMoreData() || old_raw_challenge) {
                    for (NativePacketPayload buffer : toServer) {
                        this.protocol.send(buffer, buffer.getPayloadLength());
                    }
                } else {
                    last_sent = this.createHandshakeResponsePacket(serverSession, user, database, plugin.getProtocolPluginName(), toServer);
                    this.protocol.send(last_sent, last_sent.getPosition());
                }
            }
            last_received = this.protocol.checkErrorMessage();
            old_raw_challenge = false;
            if (last_received.isOKPacket()) {
                OkPacket ok = OkPacket.parse(last_received, null);
                serverSession.setStatusFlags(ok.getStatusFlags(), true);
                plugin.destroy();
                break;
            }
            if (last_received.isAuthMethodSwitchRequestPacket()) {
                skipPassword = false;
                pluginName = last_received.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
                if (plugin.getProtocolPluginName().equals(pluginName)) {
                    plugin.reset();
                } else {
                    plugin.destroy();
                    plugin = this.getAuthenticationPlugin(pluginName);
                    if (plugin == null) {
                        throw ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{pluginName}), this.getExceptionInterceptor());
                    }
                }
                this.checkConfidentiality(plugin);
                fromServer = new NativePacketPayload(StringUtils.getBytes(last_received.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII")));
                continue;
            }
            if (!this.protocol.versionMeetsMinimum(5, 5, 16)) {
                old_raw_challenge = true;
                last_received.setPosition(last_received.getPosition() - 1);
            }
            fromServer = new NativePacketPayload(last_received.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
        }
        if (counter == 0) {
            throw ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("CommunicationsException.TooManyAuthenticationPluginNegotiations"), this.getExceptionInterceptor());
        }
        this.protocol.afterHandshake();
        if (!this.useConnectWithDb) {
            this.protocol.changeDatabase(database);
        }
    }

    private Map<String, String> getConnectionAttributesMap(String attStr) {
        HashMap<String, String> attMap = new HashMap<String, String>();
        if (attStr != null) {
            String[] pairs;
            for (String pair : pairs = attStr.split(",")) {
                int keyEnd = pair.indexOf(":");
                if (keyEnd <= 0 || keyEnd + 1 >= pair.length()) continue;
                attMap.put(pair.substring(0, keyEnd), pair.substring(keyEnd + 1));
            }
        }
        attMap.put("_client_name", "MySQL Connector/J");
        attMap.put("_client_version", "8.0.22");
        attMap.put("_runtime_vendor", Constants.JVM_VENDOR);
        attMap.put("_runtime_version", Constants.JVM_VERSION);
        attMap.put("_client_license", "GPL");
        return attMap;
    }

    private void appendConnectionAttributes(NativePacketPayload buf, String attributes, String enc) {
        NativePacketPayload lb = new NativePacketPayload(100);
        Map<String, String> attMap = this.getConnectionAttributesMap(attributes);
        for (String key : attMap.keySet()) {
            lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(key, enc));
            lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(attMap.get(key), enc));
        }
        buf.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, lb.getPosition());
        buf.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, lb.getByteBuffer(), 0, lb.getPosition());
    }

    @Override
    public String getEncodingForHandshake() {
        String enc = this.propertySet.getStringProperty(PropertyKey.characterEncoding).getValue();
        if (enc == null) {
            enc = "UTF-8";
        }
        return enc;
    }

    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    @Override
    public void changeUser(ServerSession serverSession, String userName, String password, String database) {
        this.proceedHandshakeWithPluggableAuthentication(serverSession, userName, password, database, null);
    }

    private NativePacketPayload createHandshakeResponsePacket(ServerSession serverSession, String user, String database, String pluginName, ArrayList<NativePacketPayload> toServer) {
        long clientParam = serverSession.getClientParam();
        String enc = this.getEncodingForHandshake();
        NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * user.length() + (this.useConnectWithDb ? 3 * database.length() : 0));
        last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, clientParam);
        last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, 0xFFFFFFL);
        last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, AuthenticationProvider.getCharsetForHandshake(enc, serverSession.getCapabilities().getServerVersion()));
        last_sent.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, new byte[23]);
        last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(user, enc));
        if ((clientParam & 0x200000L) != 0L) {
            last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, toServer.get(0).readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
        } else {
            last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, toServer.get(0).getPayloadLength());
            last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, toServer.get(0).getByteBuffer());
        }
        if (this.useConnectWithDb) {
            last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(database, enc));
        }
        last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
        if ((clientParam & 0x100000L) != 0L) {
            this.appendConnectionAttributes(last_sent, this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc);
        }
        return last_sent;
    }

    private NativePacketPayload createChangeUserPacket(ServerSession serverSession, String user, String database, String pluginName, ArrayList<NativePacketPayload> toServer) {
        long clientParam = serverSession.getClientParam();
        String enc = this.getEncodingForHandshake();
        NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * user.length() + (this.useConnectWithDb ? 3 * database.length() : 1) + 1);
        last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 17L);
        last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(user, enc));
        if (toServer.get(0).getPayloadLength() < 256) {
            last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, toServer.get(0).getPayloadLength());
            last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, toServer.get(0).getByteBuffer(), 0, toServer.get(0).getPayloadLength());
        } else {
            last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
        }
        if (this.useConnectWithDb) {
            last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(database, enc));
        } else {
            last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
        }
        last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, AuthenticationProvider.getCharsetForHandshake(enc, serverSession.getCapabilities().getServerVersion()));
        last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
        last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
        if ((clientParam & 0x100000L) != 0L) {
            this.appendConnectionAttributes(last_sent, this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc);
        }
        return last_sent;
    }
}

