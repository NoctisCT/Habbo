/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.util.LocationUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class KeyStoreFactoryBean {
    private String location;
    private String provider;
    private String type;
    private String password;

    public KeyStore createKeyStore() throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException {
        if (this.getLocation() == null) {
            throw new IllegalArgumentException("location is required");
        }
        InputStream inputStream = null;
        try {
            URL url = LocationUtil.urlForResource(this.getLocation());
            inputStream = url.openStream();
            KeyStore keyStore = this.newKeyStore();
            keyStore.load(inputStream, this.getPassword().toCharArray());
            KeyStore keyStore2 = keyStore;
            return keyStore2;
        }
        catch (NoSuchProviderException ex) {
            throw new NoSuchProviderException("no such keystore provider: " + this.getProvider());
        }
        catch (NoSuchAlgorithmException ex) {
            throw new NoSuchAlgorithmException("no such keystore type: " + this.getType());
        }
        catch (FileNotFoundException ex) {
            throw new KeyStoreException(this.getLocation() + ": file not found");
        }
        catch (Exception ex) {
            throw new KeyStoreException(this.getLocation() + ": " + ex.getMessage(), ex);
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    private KeyStore newKeyStore() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException {
        return this.getProvider() != null ? KeyStore.getInstance(this.getType(), this.getProvider()) : KeyStore.getInstance(this.getType());
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        if (this.type == null) {
            return "JKS";
        }
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPassword() {
        if (this.password == null) {
            return "changeit";
        }
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

