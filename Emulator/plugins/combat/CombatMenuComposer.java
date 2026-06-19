package com.eu.habbo.bhrpg.combat;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CombatMenuComposer extends MessageComposer {
    private final boolean mostrar;
    private final boolean miTurno;
    private final int movRestante;
    private final UserCombatStats myStats;
    private final UserCombatStats oppStats;

    // CONSTRUCTOR COMPLETO: Utilizado por el CombatManager para actualizar el HUD
    // con stats reales
    public CombatMenuComposer(boolean mostrar, boolean miTurno, int movRestante, UserCombatStats myStats,
            UserCombatStats oppStats) {
        this.mostrar = mostrar;
        this.miTurno = miTurno;
        this.movRestante = movRestante;
        this.myStats = myStats;
        this.oppStats = oppStats;
    }

    // CONSTRUCTOR SOBRECARGADO: Mantiene la compatibilidad con CombatListener y
    // eventos de movimiento/look
    public CombatMenuComposer(boolean mostrar, boolean miTurno, int movRestante) {
        this.mostrar = mostrar;
        this.miTurno = miTurno;
        this.movRestante = movRestante;
        this.myStats = null;
        this.oppStats = null;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3502);

        // Control de nulidad por seguridad (si viene del constructor de 3 parámetros,
        // rellena con 0)
        int myHp = (this.myStats != null) ? this.myStats.getVitalidad() : 0;
        int myMaxHp = (this.myStats != null) ? this.myStats.getMaxVitalidad() : 0;
        int myMp = (this.myStats != null) ? this.myStats.getReiryoku() : 0;
        int myMaxMp = (this.myStats != null) ? this.myStats.getMaxReiryoku() : 0;

        int oppHp = (this.oppStats != null) ? this.oppStats.getVitalidad() : 0;
        int oppMaxHp = (this.oppStats != null) ? this.oppStats.getMaxVitalidad() : 0;
        int oppMp = (this.oppStats != null) ? this.oppStats.getReiryoku() : 0;
        int oppMaxMp = (this.oppStats != null) ? this.oppStats.getMaxReiryoku() : 0;

        // Construimos un JSON de combate total e indestructible para el TSX
        String json = String.format(
                "{\"mostrar\":%b,\"miTurno\":%b,\"mov\":%d," +
                        "\"myHp\":%d,\"myMaxHp\":%d,\"myMp\":%d,\"myMaxMp\":%d," +
                        "\"oppHp\":%d,\"oppMaxHp\":%d,\"oppMp\":%d,\"oppMaxMp\":%d}",
                this.mostrar, this.miTurno, this.movRestante,
                myHp, myMaxHp, myMp, myMaxMp,
                oppHp, oppMaxHp, oppMp, oppMaxMp);

        this.response.appendString(json);

        return this.response;
    }
}