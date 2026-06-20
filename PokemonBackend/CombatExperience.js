// CombatExperience.js

/**
 * Calcula la EXP total acumulada necesaria para un nivel según la curva oficial
 */
const getRequiredExpForLevel = (level, growthRate) => {
    const N = level;
    switch (growthRate) {
        case 'fast':
            return Math.floor((4 * Math.pow(N, 3)) / 5);

        case 'medium_slow':
            return Math.floor((6 / 5) * Math.pow(N, 3) - 15 * Math.pow(N, 2) + 100 * N - 140);

        case 'slow':
            return Math.floor((5 * Math.pow(N, 3)) / 4);

        case 'erratic':
            if (N <= 50) return Math.floor((Math.pow(N, 3) * (100 - N)) / 50);
            if (N <= 68) return Math.floor((Math.pow(N, 3) * (150 - N)) / 100);
            if (N <= 98) return Math.floor((Math.pow(N, 3) * Math.floor((1911 - 10 * N) / 3)) / 500);
            return Math.floor((Math.pow(N, 3) * (160 - N)) / 100);

        case 'fluctuating':
            if (N <= 15) return Math.floor((Math.pow(N, 3) * (Math.floor((N + 1) / 3) + 24)) / 50);
            if (N <= 36) return Math.floor((Math.pow(N, 3) * (N + 14)) / 50);
            return Math.floor((Math.pow(N, 3) * (Math.floor(N / 2) + 32)) / 50);

        case 'medium_fast':
        default:
            return Math.floor(Math.pow(N, 3));
    }
};

/**
 * Calcula las estadísticas máximas reales usando Stats Base, IVs y EVs de tu base de datos
 */
const calculateNewStats = (pokemon, level) => {
    // Nota: El objeto 'pokemon' debe traer los campos mapeados de ambas tablas (base + storage)

    const hp = Math.floor(((2 * pokemon.base_hp + pokemon.iv_hp + Math.floor(pokemon.ev_hp / 4)) * level) / 100) + level + 10;
    const atk = Math.floor(((2 * pokemon.base_attack + pokemon.iv_attack + Math.floor(pokemon.ev_attack / 4)) * level) / 100) + 5;
    const def = Math.floor(((2 * pokemon.base_defense + pokemon.iv_defense + Math.floor(pokemon.ev_defense / 4)) * level) / 100) + 5;

    return { hp, atk, def };
};

module.exports = { getRequiredExpForLevel, calculateNewStats };