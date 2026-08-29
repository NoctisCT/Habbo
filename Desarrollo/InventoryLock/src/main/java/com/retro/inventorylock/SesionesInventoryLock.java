package com.retro.inventorylock;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SesionesInventoryLock
{
    private static final Set<Integer> DESBLOQUEADOS = ConcurrentHashMap.newKeySet();

    private SesionesInventoryLock()
    {
    }

    public static void desbloquear(int usuarioId)
    {
        DESBLOQUEADOS.add(usuarioId);
    }

    public static void bloquear(int usuarioId)
    {
        DESBLOQUEADOS.remove(usuarioId);
    }

    public static boolean estaDesbloqueado(int usuarioId)
    {
        return DESBLOQUEADOS.contains(usuarioId);
    }

    public static void limpiar()
    {
        DESBLOQUEADOS.clear();
    }
}