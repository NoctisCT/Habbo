/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.sasl;

import com.mysql.cj.sasl.ScramSha1SaslClientFactory;
import java.security.AccessController;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.ProviderException;

public final class ScramSha1SaslProvider
extends Provider {
    private static final long serialVersionUID = 728657701379380668L;
    private static final String INFO = "MySQL Connector/J SASL provider (implements a client mechanism for MYSQLCJ-SCRAM-SHA-1)";

    public ScramSha1SaslProvider() {
        super("MySQLScramSha1Sasl", 1.0, INFO);
        AccessController.doPrivileged(() -> {
            this.putService(new ProviderService(this, "SaslClientFactory", "MYSQLCJ-SCRAM-SHA-1", ScramSha1SaslClientFactory.class.getName()));
            return null;
        });
    }

    private static final class ProviderService
    extends Provider.Service {
        public ProviderService(Provider provider, String type, String algorithm, String className) {
            super(provider, type, algorithm, className, null, null);
        }

        @Override
        public Object newInstance(Object constructorParameter) throws NoSuchAlgorithmException {
            String type = this.getType();
            if (constructorParameter != null) {
                throw new InvalidParameterException("constructorParameter not used with " + type + " engines");
            }
            String algorithm = this.getAlgorithm();
            if (type.equals("SaslClientFactory") && algorithm.equals("MYSQLCJ-SCRAM-SHA-1")) {
                return new ScramSha1SaslClientFactory();
            }
            throw new ProviderException("No implementation for " + algorithm + " " + type);
        }
    }
}

