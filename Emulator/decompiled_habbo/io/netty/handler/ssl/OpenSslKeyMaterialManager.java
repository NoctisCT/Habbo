/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslKeyMaterial;
import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;

final class OpenSslKeyMaterialManager {
    static final String KEY_TYPE_RSA = "RSA";
    static final String KEY_TYPE_DH_RSA = "DH_RSA";
    static final String KEY_TYPE_EC = "EC";
    static final String KEY_TYPE_EC_EC = "EC_EC";
    static final String KEY_TYPE_EC_RSA = "EC_RSA";
    private static final Map<String, String> KEY_TYPES = new HashMap<String, String>();
    private final OpenSslKeyMaterialProvider provider;

    OpenSslKeyMaterialManager(OpenSslKeyMaterialProvider provider) {
        this.provider = provider;
    }

    void setKeyMaterialServerSide(ReferenceCountedOpenSslEngine engine) throws SSLException {
        String[] authMethods = engine.authMethods();
        if (authMethods.length == 0) {
            return;
        }
        HashSet<String> aliases = new HashSet<String>(authMethods.length);
        for (String authMethod : authMethods) {
            String alias;
            String type = KEY_TYPES.get(authMethod);
            if (type == null || (alias = this.chooseServerAlias(engine, type)) == null || !aliases.add(alias) || this.setKeyMaterial(engine, alias)) continue;
            return;
        }
    }

    void setKeyMaterialClientSide(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) throws SSLException {
        String alias = this.chooseClientAlias(engine, keyTypes, issuer);
        if (alias != null) {
            this.setKeyMaterial(engine, alias);
        }
    }

    private boolean setKeyMaterial(ReferenceCountedOpenSslEngine engine, String alias) throws SSLException {
        OpenSslKeyMaterial keyMaterial = null;
        try {
            keyMaterial = this.provider.chooseKeyMaterial(engine.alloc, alias);
            boolean bl = keyMaterial == null || engine.setKeyMaterial(keyMaterial);
            return bl;
        }
        catch (SSLException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SSLException(e);
        }
        finally {
            if (keyMaterial != null) {
                keyMaterial.release();
            }
        }
    }

    private String chooseClientAlias(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) {
        X509KeyManager manager = this.provider.keyManager();
        if (manager instanceof X509ExtendedKeyManager) {
            return ((X509ExtendedKeyManager)manager).chooseEngineClientAlias(keyTypes, issuer, engine);
        }
        return manager.chooseClientAlias(keyTypes, issuer, null);
    }

    private String chooseServerAlias(ReferenceCountedOpenSslEngine engine, String type) {
        X509KeyManager manager = this.provider.keyManager();
        if (manager instanceof X509ExtendedKeyManager) {
            return ((X509ExtendedKeyManager)manager).chooseEngineServerAlias(type, null, engine);
        }
        return manager.chooseServerAlias(type, null, null);
    }

    static {
        KEY_TYPES.put(KEY_TYPE_RSA, KEY_TYPE_RSA);
        KEY_TYPES.put("DHE_RSA", KEY_TYPE_RSA);
        KEY_TYPES.put("ECDHE_RSA", KEY_TYPE_RSA);
        KEY_TYPES.put("ECDHE_ECDSA", KEY_TYPE_EC);
        KEY_TYPES.put("ECDH_RSA", KEY_TYPE_EC_RSA);
        KEY_TYPES.put("ECDH_ECDSA", KEY_TYPE_EC_EC);
        KEY_TYPES.put(KEY_TYPE_DH_RSA, KEY_TYPE_DH_RSA);
    }
}

