package com.retro.subastas;

import com.eu.habbo.Emulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class ConfiguracionSubastas
{
    private static final long CACHE_MS = 5000L;

    private static volatile long ultimaCarga = 0L;

    private static volatile int duracionSegundos = 60;
    private static volatile int transicionSegundos = 1;
    private static volatile int margenCupoPorcentaje = 85;
    private static volatile int comisionPorcentaje = 0;
    private static volatile int limiteNormal = 3;
    private static volatile int limiteVip = 5;
    private static volatile int antiSnipingUmbral = 5;
    private static volatile int antiSnipingExtension = 5;
    private static volatile int maxMisSubastas = 25;

    private ConfiguracionSubastas()
    {
    }

    private static void cargarSiHaceFalta()
    {
        long ahora = System.currentTimeMillis();

        if((ahora - ultimaCarga) < CACHE_MS)
        {
            return;
        }

        synchronized(ConfiguracionSubastas.class)
        {
            ahora = System.currentTimeMillis();

            if((ahora - ultimaCarga) < CACHE_MS)
            {
                return;
            }

            try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement consulta = conexion.prepareStatement(
                        "SELECT duracion_segundos, transicion_segundos, margen_cupo_porcentaje, " +
                        "comision_porcentaje, limite_normal, limite_vip, " +
                        "anti_sniping_umbral, anti_sniping_extension, max_mis_subastas " +
                        "FROM configuracion_subastas WHERE id = 1 LIMIT 1");
                ResultSet resultado = consulta.executeQuery())
            {
                if(resultado.next())
                {
                    duracionSegundos = Math.max(10, resultado.getInt("duracion_segundos"));
                    transicionSegundos = Math.max(0, resultado.getInt("transicion_segundos"));
                    margenCupoPorcentaje = Math.max(
                            1,
                            Math.min(100, resultado.getInt("margen_cupo_porcentaje"))
                    );
                    comisionPorcentaje = Math.max(
                            0,
                            Math.min(100, resultado.getInt("comision_porcentaje"))
                    );
                    limiteNormal = Math.max(1, resultado.getInt("limite_normal"));
                    limiteVip = Math.max(limiteNormal, resultado.getInt("limite_vip"));
                    antiSnipingUmbral = Math.max(0, resultado.getInt("anti_sniping_umbral"));
                    antiSnipingExtension = Math.max(0, resultado.getInt("anti_sniping_extension"));
                    maxMisSubastas = Math.max(5, resultado.getInt("max_mis_subastas"));
                }

                ultimaCarga = ahora;
            }
            catch(Exception error)
            {
                System.out.println("[Subastas] Aviso: no se pudo recargar configuracion: "
                        + error.getMessage());
            }
        }
    }

    public static int getDuracionSegundos()
    {
        cargarSiHaceFalta();
        return duracionSegundos;
    }

    public static int getTransicionSegundos()
    {
        cargarSiHaceFalta();
        return transicionSegundos;
    }

    public static int getMargenCupoPorcentaje()
    {
        cargarSiHaceFalta();
        return margenCupoPorcentaje;
    }

    public static int getComisionPorcentaje()
    {
        cargarSiHaceFalta();
        return comisionPorcentaje;
    }

    public static int getLimiteNormal()
    {
        cargarSiHaceFalta();
        return limiteNormal;
    }

    public static int getLimiteVip()
    {
        cargarSiHaceFalta();
        return limiteVip;
    }

    public static int getAntiSnipingUmbral()
    {
        cargarSiHaceFalta();
        return antiSnipingUmbral;
    }

    public static int getAntiSnipingExtension()
    {
        cargarSiHaceFalta();
        return antiSnipingExtension;
    }

    public static int getMaxMisSubastas()
    {
        cargarSiHaceFalta();
        return maxMisSubastas;
    }
}