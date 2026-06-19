package com.eu.habbo.bhrpg.combat;

public class UserCombatStats {
    private int vitalidad;
    private int maxVitalidad;
    private int reiryoku;
    private int maxReiryoku;
    private int fuerza;
    private int defensa;
    private int velocidad;
    private int voluntad;
    private int reiatsu;
    private int puntosDisponibles;

    public UserCombatStats(int vit, int maxVit, int rei, int maxRei, int fza, int def, int vel, int vol, int reia,
            int puntos) {
        this.vitalidad = vit;
        this.maxVitalidad = maxVit;
        this.reiryoku = rei;
        this.maxReiryoku = maxRei;
        this.fuerza = fza;
        this.defensa = def;
        this.velocidad = vel;
        this.voluntad = vol;
        this.reiatsu = reia;
        this.puntosDisponibles = puntos;
    }

    public int getVitalidad() {
        return vitalidad;
    }

    public void setVitalidad(int vitalidad) {
        this.vitalidad = vitalidad;
    }

    public int getMaxVitalidad() {
        return maxVitalidad;
    }

    public void setMaxVitalidad(int maxVitalidad) {
        this.maxVitalidad = maxVitalidad;
    }

    public int getReiryoku() {
        return reiryoku;
    }

    public void setReiryoku(int reiryoku) {
        this.reiryoku = reiryoku;
    }

    public int getMaxReiryoku() {
        return maxReiryoku;
    }

    public void setMaxReiryoku(int maxReiryoku) {
        this.maxReiryoku = maxReiryoku;
    }

    public int getFuerza() {
        return fuerza;
    }

    public void setFuerza(int fuerza) {
        this.fuerza = fuerza;
    }

    public int getDefensa() {
        return defensa;
    }

    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public int getVoluntad() {
        return voluntad;
    }

    public void setVoluntad(int voluntad) {
        this.voluntad = voluntad;
    }

    public int getReiatsu() {
        return reiatsu;
    }

    public void setReiatsu(int reiatsu) {
        this.reiatsu = reiatsu;
    }

    public int getPuntosDisponibles() {
        return puntosDisponibles;
    }

    public void setPuntosDisponibles(int puntosDisponibles) {
        this.puntosDisponibles = puntosDisponibles;
    }
}