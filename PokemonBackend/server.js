const WebSocket = require('ws');
const mysql = require('mysql2');
const combatHandler = require('./combatHandler'); // 📦 Importamos tu módulo de combate
const statCalculator = require('./statCalculator'); // 🧠 Importamos el motor matemático relacional
const healingHandler = require('./healingHandler'); // 🏥 Importamos el gestor de curación unificado (Joy + Mochila)

// 1. CONEXIÓN A TU BASE DE DATOS HABBO
const db = mysql.createPool({
    host: '127.0.0.1',
    user: 'root',
    password: '',
    database: 'habbo',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// 2. INICIAR SERVIDOR WEBSOCKET (Puerto 8085)
const wss = new WebSocket.Server({ port: 8085 }, () => {
    console.log('[POKÉMON BACKEND] Servidor corriendo en ws://localhost:8085');
});

wss.on('connection', (ws) => {
    console.log('[POKÉMON] Un usuario se ha conectado al cliente.');

    ws.roomId = null;
    ws.battle = null;           // Referencia en memoria para almacenar batallas activas
    ws.lastWildPokemon = null;  // 🛡️ Almacén seguro del ADN del Pokémon salvaje activo en la baldosa

    // 3. RECEPTOR DE PAQUETES DE RED
    ws.on('message', (message) => {
        try {
            const data = JSON.parse(message);

            switch (data.type) {
                case 'GET_PC_DATA':
                    getUserPokemon(data.userId, (err, pokemonList) => {
                        if (!err) ws.send(JSON.stringify({ type: 'PC_DATA_RESPONSE', pokemon: pokemonList }));
                    });
                    break;

                // =========================================================================
                // 🎒 ESCUCHADOR QUE TRAE LOS ÍTEMS DE LA BASE DE DATOS A TU MOCHILA
                // =========================================================================
                case 'GET_INVENTORY_DATA':
                    getUserInventory(data.userId, (err, inventoryList) => {
                        if (!err) ws.send(JSON.stringify({ type: 'INVENTORY_DATA_RESPONSE', items: inventoryList }));
                    });
                    break;

                case 'SWAP_SLOT':
                    updatePokemonSlot(data.pokemonStorageId, data.newSlot, (err) => {
                        if (!err) ws.send(JSON.stringify({ type: 'SWAP_SUCCESS', id: data.pokemonStorageId, slot: data.newSlot }));
                    });
                    break;

                case 'ENTERED_ROOM':
                    ws.roomId = data.roomId;
                    console.log(`[POKÉMON] Jugador ha entrado a la sala ID: ${data.roomId}`);
                    break;

                case 'USER_STEP':
                    // Validaciones de seguridad: no procesar si no hay sala fija O si ya está combatiendo
                    if (!ws.roomId || ws.battle) return;

                    db.execute('SELECT * FROM pokemon_routes WHERE room_id = ?', [ws.roomId], (err, rows) => {
                        if (err || rows.length === 0) return;

                        // 🎲 Ratio de aparición base (10% de probabilidad por pisada)
                        if (Math.random() < 0.10) {
                            const route = rows[0];
                            const pool = route.pool_pokemon.split(',').map(p => {
                                const [id, chance] = p.split(':');
                                return { pokemonId: parseInt(id), chance: parseInt(chance) };
                            });

                            const rand = Math.floor(Math.random() * 100);
                            let cumulativeChance = 0;
                            let selectedPokemonId = pool[0].pokemonId;

                            for (const p of pool) {
                                cumulativeChance += p.chance;
                                if (rand <= cumulativeChance) {
                                    selectedPokemonId = p.pokemonId;
                                    break;
                                }
                            }

                            const wildLevel = Math.floor(Math.random() * 5) + 3; // Niveles del 3 al 7

                            // 🔍 GENERADOR GENÉTICO INTEGRADO DESDE BASE DE DATOS
                            // Buscamos la plantilla física y el set de habilidades de la especie
                            db.execute('SELECT * FROM pokemon_pokedex WHERE pokemon_id = ?', [selectedPokemonId], (err, pokeRows) => {
                                if (err || pokeRows.length === 0) return;
                                const pokeData = pokeRows[0];

                                // Buscamos una de las 25 naturalezas oficiales de forma aleatoria
                                db.execute('SELECT name FROM pokemon_natures ORDER BY RAND() LIMIT 1', [], (err, natureRows) => {
                                    const natureName = (natureRows && natureRows.length > 0) ? natureRows[0].name : 'Docil';

                                    // 📊 1. Sorteo Genético: Inyección de IVs independientes (0 al 31)
                                    const wildIvs = {
                                        hp: Math.floor(Math.random() * 32),
                                        attack: Math.floor(Math.random() * 32),
                                        defense: Math.floor(Math.random() * 32),
                                        sp_attack: Math.floor(Math.random() * 32),
                                        sp_defense: Math.floor(Math.random() * 32),
                                        speed: Math.floor(Math.random() * 32)
                                    };

                                    // ♀️♂️ 2. Sorteo Biológico: Género basado en el ratio real de la especie
                                    let chosenGender = 0; // Por defecto Macho (0)
                                    if (pokeData.female_ratio === -1) {
                                        chosenGender = 2; // Sin Género (Gendersless)
                                    } else {
                                        const genderRoll = Math.random() * 100;
                                        if (genderRoll <= pokeData.female_ratio) {
                                            chosenGender = 1; // Hembra (1)
                                        }
                                    }

                                    // ✨ 3. Sorteo Cosmético: Factor Shiny (Configurado al 1% para incentivar la economía del hotel)
                                    const SHINY_CHANCE = 0.01;
                                    const isShiny = Math.random() < SHINY_CHANCE ? 1 : 0;

                                    // 🧬 4. Sorteo Evolutivo: Elección de Habilidad (Normales vs Oculta)
                                    let chosenAbilityId = pokeData.ability_1;
                                    const HIDDEN_ABILITY_CHANCE = 0.05; // 5% de probabilidad de despertar con oculta

                                    if (Math.random() < HIDDEN_ABILITY_CHANCE && pokeData.hidden_ability) {
                                        chosenAbilityId = pokeData.hidden_ability;
                                    } else if (pokeData.ability_2 && Math.random() < 0.50) {
                                        chosenAbilityId = pokeData.ability_2; // Sorteo al 50% si tiene dos comunes
                                    }

                                    // 💾 5. Blindaje en Memoria: Registramos la ficha del espécimen en el WebSocket
                                    ws.lastWildPokemon = {
                                        pokemonId: selectedPokemonId,
                                        name: pokeData.name,
                                        level: wildLevel,
                                        nature: natureName,
                                        gender: chosenGender,
                                        isShiny: isShiny,
                                        abilityId: chosenAbilityId,
                                        ivs: wildIvs,
                                        weight: pokeData.weight,
                                        height: pokeData.height,
                                        type1: pokeData.type_1,
                                        type2: pokeData.type_2,
                                        catchRate: pokeData.catch_rate,
                                        baseExperience: pokeData.base_experience
                                    };

                                    console.log(`[POKÉMON] ¡💥 ENCUENTRO DETERMINISTA! ${pokeData.name} Lvl.${wildLevel} [${isShiny ? 'SHINY ✨' : 'Común'}] [Género: ${chosenGender}] en ${route.route_name}`);

                                    // Notificamos al cliente React/Nitro con el ecosistema visual exacto
                                    ws.send(JSON.stringify({
                                        type: 'WILD_ENCOUNTER',
                                        pokemonId: selectedPokemonId,
                                        name: pokeData.name,
                                        level: wildLevel,
                                        gender: chosenGender,
                                        isShiny: isShiny,
                                        type1: pokeData.type_1,
                                        type2: pokeData.type_2,
                                        routeName: route.route_name
                                    }));
                                });
                            });
                        }
                    });
                    break;

                // =========================================================================
                // ⚔️ INYECCIÓN MODULAR DE REQUERIMIENTOS COMBATE (INTERFACE MODE)
                // =========================================================================
                case 'START_PRIVATE_BATTLE':
                    combatHandler.startPrivateBattle(ws, db, data);
                    break;

                case 'BATTLE_ATTACK':
                    combatHandler.processBattleAttack(ws, db, data);
                    break;

                case 'THROW_POKEBALL': // 🔴 ESCUCHADOR: Lanzamiento de Pokéballs reales desde el inventario
                    combatHandler.processThrowBall(ws, db, data);
                    break;

                // =========================================================================
                // 🏥 SISTEMA DE ASISTENCIA MÉDICA UNIFICADO
                // =========================================================================
                case 'HEAL_TEAM_BOT': // 🏥 ESCUCHADOR: Interacción con el bot del Centro Pokémon
                    healingHandler.healTeamAtBot(ws, db, data);
                    break;

                case 'USE_HEALING_ITEM': // 🎒 ESCUCHADOR: Uso de pociones/revivir de la mochila
                    healingHandler.processUseHealingItem(ws, db, data);
                    break;

                case 'CLOSE_BATTLE':
                    console.log(`[COMBATE] Batalla finalizada o abandono voluntario. Socket liberado.`);
                    ws.battle = null;
                    ws.lastWildPokemon = null; // Limpiamos la memoria del encuentro expirado
                    break;
            }
        } catch (e) {
            console.error('[POKÉMON] Error al procesar el paquete', e);
        }
    });
});

// 📋 REFACTORIZACIÓN COMPLETA DEL SISTEMA DE CONSULTA DEL PC
function getUserPokemon(userId, callback) {
    const query = `
        SELECT s.*, p.name, p.type_1, p.type_2, p.base_hp, p.base_attack, p.base_defense, p.base_sp_attack, p.base_sp_defense, p.base_speed 
        FROM pokemon_storage s
        INNER JOIN pokemon_pokedex p ON s.pokemon_id = p.pokemon_id
        WHERE s.user_id = ?
    `;

    db.execute(query, [userId], (err, rows) => {
        if (err) {
            callback(err, null);
            return;
        }

        // Mapeamos cada criatura de la BD para inyectarle su biología real antes de enviarla al cliente Habbo
        const processedList = rows.map(poke => {
            const baseStats = {
                base_hp: poke.base_hp,
                base_attack: poke.base_attack,
                base_defense: poke.base_defense,
                base_sp_attack: poke.base_sp_attack,
                base_sp_defense: poke.base_sp_defense,
                base_speed: poke.base_speed
            };

            // Calculamos sus estadísticas máximas exactas basadas en su genética
            const realStats = statCalculator.calculateStats(baseStats, poke);

            return {
                id: poke.id,
                user_id: poke.user_id,
                pokemon_id: poke.pokemon_id,
                name: poke.name,
                type1: poke.type_1,
                type2: poke.type_2,
                level: poke.level,
                exp: poke.exp,
                hp: poke.hp,              // Vida actual exacta guardada tras los combates
                max_hp: realStats.maxHp,     // 🌟 Compatibilidad total con tu interfaz actual del PC
                maxHp: realStats.maxHp,     // Mapeado con el valor genético oficial calculado
                slot: poke.slot,
                gender: poke.gender,         // 0: Macho, 1: Hembra, 2: Sin Género
                nature: poke.nature,
                ability_id: poke.ability_id,
                is_shiny: poke.is_shiny,
                stats: realStats            // Desglose de stats por si la UI requiere pintarlos
            };
        });

        callback(null, processedList);
    });
}

// =========================================================================
// 🎒 FUNCIÓN CORREGIDA: CONSULTA RELACIONAL DEL INVENTARIO JUNTANDO AMBAS TABLAS
// =========================================================================
function getUserInventory(userId, callback) {
    const query = `
        SELECT 
            p.item_id AS id, 
            p.name, 
            p.description, 
            s.quantity, 
            CASE 
                WHEN p.type = 'potion' THEN 'HEALING'
                WHEN p.type = 'revive' THEN 'REVIVE'
                WHEN p.type = 'ball' THEN 'BALL'
                ELSE UPPER(p.type)
            END AS type,
            CASE 
                WHEN p.item_id = 1 THEN 'hw_pokeball_icon.png'
                WHEN p.item_id = 2 THEN 'superball_icon.png'
                WHEN p.item_id = 3 THEN 'ultraball_icon.png'
                WHEN p.item_id = 10 THEN 'potion_icon.png'
                WHEN p.item_id = 11 THEN 'superpotion_icon.png'
                WHEN p.item_id = 12 THEN 'hyperpotion_icon.png'
                WHEN p.item_id = 13 THEN 'maxpotion_icon.png'
                WHEN p.item_id = 14 THEN 'fullrestore_icon.png'
                WHEN p.item_id = 15 THEN 'revive_icon.png'
                WHEN p.item_id = 16 THEN 'maxrevive_icon.png'
                ELSE 'hw_pokeball_icon.png'
            END AS iconName
        FROM pokemon_inventory s
        INNER JOIN pokemon_items p ON s.item_id = p.item_id
        WHERE s.user_id = ?
    `;

    db.execute(query, [userId], (err, rows) => {
        if (err) {
            console.error('[POKÉMON DB ERROR] Fallo al extraer mochila:', err);
            callback(err, null);
            return;
        }
        callback(null, rows);
    });
}

function updatePokemonSlot(storageId, newSlot, callback) {
    db.execute('UPDATE pokemon_storage SET slot = ? WHERE id = ?', [newSlot, storageId], (err, res) => callback(err));
}