/**
 * Motor de Cálculo Estadístico Oficial de Pokémon (Ecuaciones de Game Boy)
 */

// Diccionario de modificadores de Naturaleza oficiales de la saga (+10% / -10%)
const NATURE_MODIFIERS = {
    'Huraña': { increase: 'base_attack', decrease: 'base_defense' },
    'Audaz': { increase: 'base_attack', decrease: 'base_speed' },
    'Firme': { increase: 'base_attack', decrease: 'base_sp_attack' },
    'Pícara': { increase: 'base_attack', decrease: 'base_sp_defense' },
    'Osada': { increase: 'base_defense', decrease: 'base_attack' },
    'Plácida': { increase: 'base_defense', decrease: 'base_speed' },
    'Agitada': { increase: 'base_defense', decrease: 'base_sp_attack' },
    'Floja': { increase: 'base_defense', decrease: 'base_sp_defense' },
    'Modesta': { increase: 'base_sp_attack', decrease: 'base_attack' },
    'Afable': { increase: 'base_sp_attack', decrease: 'base_defense' },
    'Alocada': { increase: 'base_sp_attack', decrease: 'base_speed' },
    'Alocada2': { increase: 'base_sp_attack', decrease: 'base_sp_defense' },
    'Serena': { increase: 'base_sp_defense', decrease: 'base_attack' },
    'Amable': { increase: 'base_sp_defense', decrease: 'base_defense' },
    'Grosera': { increase: 'base_sp_defense', decrease: 'base_speed' },
    'Cauta': { increase: 'base_sp_defense', decrease: 'base_sp_attack' },
    'Miedosa': { increase: 'base_speed', decrease: 'base_attack' },
    'Activa': { increase: 'base_speed', decrease: 'base_defense' },
    'Alegre': { increase: 'base_speed', decrease: 'base_sp_attack' },
    'Ingenua': { increase: 'base_speed', decrease: 'base_sp_defense' },
    // Las naturalezas Neutras (Fuerte, Docil, Tímida, etc.) no mapean nada y devuelven modificador 1.0
};

/**
 * Calcula el set completo de estadísticas reales de un Pokémon.
 * @param {Object} baseStats - Fila directa de la tabla pokemon_pokedex (base_hp, etc.)
 * @param {Object} pokemon - Fila de la instancia del pokemon (level, ivs, evs, nature)
 * @returns {Object} Estadísticas reales listas para usar en combate o UI
 */
function calculateStats(baseStats, pokemon) {
    const level = pokemon.level;
    const nature = pokemon.nature;

    // 🧮 1. FÓRMULA OFICIAL DE PUNTOS DE SALUD (HP)
    // HP = floor(((2 * Base + IV + floor(EV / 4)) * Nivel) / 100) + Nivel + 10
    const calculatedMaxHp = Math.floor(
        ((2 * baseStats.base_hp + pokemon.iv_hp + Math.floor(pokemon.ev_hp / 4)) * level) / 100
    ) + level + 10;

    // Obtener los mapeos de la naturaleza del Pokémon
    const natureRule = NATURE_MODIFIERS[nature] || null;

    // Lista de estadísticas secundarias a procesar de forma iterativa
    const statsToCalculate = ['attack', 'defense', 'sp_attack', 'sp_defense', 'speed'];
    const calculatedStats = {};

    statsToCalculate.forEach(statKey => {
        const baseVal = baseStats[`base_${statKey}`];
        const ivVal = pokemon[`iv_${statKey}`];
        const evVal = pokemon[`ev_${statKey}`];

        // 🧮 2. FÓRMULA OFICIAL SECUNDARIA (Antes de la naturaleza)
        // Stat = floor(((2 * Base + IV + floor(EV / 4)) * Nivel) / 100) + 5
        let statVal = Math.floor(
            ((2 * baseVal + ivVal + Math.floor(evVal / 4)) * level) / 100
        ) + 5;

        // 🎭 3. APLICACIÓN DE MODIFICADORES DE NATURALEZA (+10% / -10%)
        if (natureRule) {
            if (natureRule.increase === `base_${statKey}`) {
                statVal = Math.floor(statVal * 1.1); // Stat beneficiado
            } else if (natureRule.decrease === `base_${statKey}`) {
                statVal = Math.floor(statVal * 0.9); // Stat perjudicado
            }
        }

        calculatedStats[statKey] = statVal;
    });

    return {
        maxHp: calculatedMaxHp,
        attack: calculatedStats.attack,
        defense: calculatedStats.defense,
        spAttack: calculatedStats.sp_attack,
        spDefense: calculatedStats.sp_defense,
        speed: calculatedStats.speed
    };
}

module.exports = {
    calculateStats
};