/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.sasl;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.util.SaslPrep;
import com.mysql.cj.util.StringUtils;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class ScramSha1SaslClient
implements SaslClient {
    public static final String MECHANISM_NAME = "MYSQLCJ-SCRAM-SHA-1";
    private static final int MINIMUM_ITERATIONS = 4096;
    private static final String SHA1_ALGORITHM = "SHA-1";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String GS2_CBIND_FLAG = "n";
    private static final byte[] CLIENT_KEY = "Client Key".getBytes();
    private static final byte[] SERVER_KEY = "Server Key".getBytes();
    private String authorizationId;
    private String authenticationId;
    private String password;
    private ScramExchangeStage scramStage = ScramExchangeStage.CLIENT_FIRST;
    private String cNonce;
    private String gs2Header;
    private String clientFirstMessageBare;
    private byte[] serverSignature;

    public ScramSha1SaslClient(String authorizationId, String authenticationId, String password) throws SaslException {
        this.authorizationId = StringUtils.isNullOrEmpty(authorizationId) ? "" : authorizationId;
        String string = this.authenticationId = StringUtils.isNullOrEmpty(authenticationId) ? this.authorizationId : authenticationId;
        if (StringUtils.isNullOrEmpty(this.authenticationId)) {
            throw new SaslException("The authenticationId cannot be null or empty.");
        }
        this.password = StringUtils.isNullOrEmpty(password) ? "" : password;
        this.scramStage = ScramExchangeStage.CLIENT_FIRST;
    }

    @Override
    public String getMechanismName() {
        return MECHANISM_NAME;
    }

    @Override
    public boolean hasInitialResponse() {
        return true;
    }

    @Override
    public byte[] evaluateChallenge(byte[] challenge) throws SaslException {
        try {
            switch (this.scramStage) {
                case CLIENT_FIRST: {
                    this.gs2Header = "n," + (StringUtils.isNullOrEmpty(this.authorizationId) ? "" : "a=" + this.prepUserName(this.authorizationId)) + ",";
                    this.cNonce = ScramSha1SaslClient.generateRandomPrintableAsciiString(32);
                    this.clientFirstMessageBare = "n=" + this.prepUserName(this.authenticationId) + ",r=" + this.cNonce;
                    String clientFirstMessage = this.gs2Header + this.clientFirstMessageBare;
                    byte[] byArray = StringUtils.getBytes(clientFirstMessage, "UTF-8");
                    return byArray;
                }
                case SERVER_FIRST_CLIENT_FINAL: {
                    String serverFirstMessage = StringUtils.toString(challenge, "UTF-8");
                    Map<String, String> serverFirstAttributes = this.parseChallenge(serverFirstMessage);
                    if (!(serverFirstAttributes.containsKey("r") && serverFirstAttributes.containsKey("s") && serverFirstAttributes.containsKey("i"))) {
                        throw new SaslException("Missing required SCRAM attribute from server first message.");
                    }
                    String sNonce = serverFirstAttributes.get("r");
                    if (!sNonce.startsWith(this.cNonce)) {
                        throw new SaslException("Invalid server nonce for SCRAM-SHA-1 authentication.");
                    }
                    byte[] salt = Base64.getDecoder().decode(serverFirstAttributes.get("s"));
                    int iterations = Integer.parseInt(serverFirstAttributes.get("i"));
                    if (iterations < 4096) {
                        throw new SaslException("Announced SCRAM-SHA-1 iteration count is too low.");
                    }
                    String clientFinalMessageWithoutProof = "c=" + Base64.getEncoder().encodeToString(StringUtils.getBytes(this.gs2Header, "UTF-8")) + ",r=" + sNonce;
                    byte[] saltedPassword = ScramSha1SaslClient.hi(SaslPrep.prepare(this.password, SaslPrep.StringType.STORED), salt, iterations);
                    byte[] clientKey = ScramSha1SaslClient.hmac(saltedPassword, CLIENT_KEY);
                    byte[] storedKey = ScramSha1SaslClient.h(clientKey);
                    String authMessage = this.clientFirstMessageBare + "," + serverFirstMessage + "," + clientFinalMessageWithoutProof;
                    byte[] clientSignature = ScramSha1SaslClient.hmac(storedKey, StringUtils.getBytes(authMessage, "UTF-8"));
                    byte[] clientProof = (byte[])clientKey.clone();
                    ScramSha1SaslClient.xorInPlace(clientProof, clientSignature);
                    String clientFinalMessage = clientFinalMessageWithoutProof + ",p=" + Base64.getEncoder().encodeToString(clientProof);
                    byte[] serverKey = ScramSha1SaslClient.hmac(saltedPassword, SERVER_KEY);
                    this.serverSignature = ScramSha1SaslClient.hmac(serverKey, StringUtils.getBytes(authMessage, "UTF-8"));
                    byte[] byArray = StringUtils.getBytes(clientFinalMessage, "UTF-8");
                    return byArray;
                }
                case SERVER_FINAL: {
                    String serverFinalMessage = StringUtils.toString(challenge, "UTF-8");
                    Map<String, String> serverFinalAttributes = this.parseChallenge(serverFinalMessage);
                    if (serverFinalAttributes.containsKey("e")) {
                        throw new SaslException("Authentication failed due to server error '" + serverFinalAttributes.get("e") + "'.");
                    }
                    if (!serverFinalAttributes.containsKey("v")) {
                        throw new SaslException("Missing required SCRAM attribute from server final message.");
                    }
                    byte[] verifier = Base64.getDecoder().decode(serverFinalAttributes.get("v"));
                    if (MessageDigest.isEqual(this.serverSignature, verifier)) break;
                    throw new SaslException("SCRAM-SHA-1 server signature could not be verified.");
                }
                default: {
                    throw new SaslException("Unexpected SCRAM authentication message.");
                }
            }
            byte[] clientFirstMessage = null;
            return clientFirstMessage;
        }
        catch (Throwable e) {
            this.scramStage = ScramExchangeStage.TERMINATED;
            throw e;
        }
        finally {
            this.scramStage = this.scramStage.getNext();
        }
    }

    @Override
    public boolean isComplete() {
        return this.scramStage == ScramExchangeStage.TERMINATED;
    }

    @Override
    public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
        throw new IllegalStateException("Integrity and/or privacy has not been negotiated.");
    }

    @Override
    public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
        throw new IllegalStateException("Integrity and/or privacy has not been negotiated.");
    }

    @Override
    public Object getNegotiatedProperty(String propName) {
        return null;
    }

    @Override
    public void dispose() throws SaslException {
    }

    private String prepUserName(String userName) {
        return SaslPrep.prepare(userName, SaslPrep.StringType.QUERY).replace("=", "=2D").replace(",", "=2C");
    }

    private Map<String, String> parseChallenge(String challenge) {
        HashMap<String, String> attributesMap = new HashMap<String, String>();
        for (String attribute : challenge.split(",")) {
            String[] keyValue = attribute.split("=", 2);
            attributesMap.put(keyValue[0], keyValue[1]);
        }
        return attributesMap;
    }

    private static String generateRandomPrintableAsciiString(int length) {
        int first = 33;
        int last = 126;
        int excl = 44;
        int bound = 93;
        SecureRandom random = new SecureRandom();
        char[] result = new char[length];
        int i = 0;
        while (i < length) {
            int randomValue = random.nextInt(93) + 33;
            if (randomValue == 44) continue;
            result[i++] = (char)randomValue;
        }
        return new String(result);
    }

    private static byte[] h(byte[] str) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance(SHA1_ALGORITHM);
            return sha1.digest(str);
        }
        catch (NoSuchAlgorithmException e) {
            throw ExceptionFactory.createException("Failed computing authentication hashes", e);
        }
    }

    private static byte[] hmac(byte[] key, byte[] str) {
        try {
            Mac hmacSha1 = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            hmacSha1.init(new SecretKeySpec(key, HMAC_SHA1_ALGORITHM));
            return hmacSha1.doFinal(str);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw ExceptionFactory.createException("Failed computing authentication hashes", e);
        }
    }

    private static byte[] hi(String str, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(str.toCharArray(), salt, iterations, 160);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw ExceptionFactory.createException(e.getMessage());
        }
    }

    private static byte[] xorInPlace(byte[] inOut, byte[] other) {
        for (int i = 0; i < inOut.length; ++i) {
            int n = i;
            inOut[n] = (byte)(inOut[n] ^ other[i]);
        }
        return inOut;
    }

    private static enum ScramExchangeStage {
        TERMINATED(null),
        SERVER_FINAL(TERMINATED),
        SERVER_FIRST_CLIENT_FINAL(SERVER_FINAL),
        CLIENT_FIRST(SERVER_FIRST_CLIENT_FINAL);

        private ScramExchangeStage next;

        private ScramExchangeStage(ScramExchangeStage next) {
            this.next = next;
        }

        public ScramExchangeStage getNext() {
            return this.next == null ? this : this.next;
        }
    }
}

