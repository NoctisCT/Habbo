package com.retro.inventorylock;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class SeguridadPatron
{
    private static final int ITERACIONES = 120000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;

    private SeguridadPatron()
    {
    }

    public static boolean patronValido(String patron)
    {
        if(patron == null || patron.length() < 4 || patron.length() > 9)
        {
            return false;
        }

        boolean[] usados = new boolean[10];

        for(int i = 0; i < patron.length(); i++)
        {
            char caracter = patron.charAt(i);

            if(caracter < '1' || caracter > '9')
            {
                return false;
            }

            int nodo = caracter - '0';

            if(usados[nodo])
            {
                return false;
            }

            usados[nodo] = true;
        }

        return true;
    }

    public static Credenciales crear(String patron) throws Exception
    {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);

        byte[] hash = derivar(patron, salt);

        return new Credenciales(
                Base64.getEncoder().encodeToString(hash),
                Base64.getEncoder().encodeToString(salt)
        );
    }

    public static boolean verificar(String patron, String saltBase64, String hashBase64) throws Exception
    {
        if(patron == null || saltBase64 == null || hashBase64 == null)
        {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(saltBase64);
        byte[] esperado = Base64.getDecoder().decode(hashBase64);
        byte[] obtenido = derivar(patron, salt);

        return MessageDigest.isEqual(esperado, obtenido);
    }

    private static byte[] derivar(String patron, byte[] salt) throws Exception
    {
        PBEKeySpec spec = new PBEKeySpec(
                patron.toCharArray(),
                salt,
                ITERACIONES,
                HASH_BITS
        );

        try
        {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        }
        finally
        {
            spec.clearPassword();
        }
    }

    public static final class Credenciales
    {
        private final String hash;
        private final String salt;

        public Credenciales(String hash, String salt)
        {
            this.hash = hash;
            this.salt = salt;
        }

        public String getHash()
        {
            return this.hash;
        }

        public String getSalt()
        {
            return this.salt;
        }
    }
}