/**
 * Motor de Combate por Interfaz (Modo Privado) - Módulo de Movimientos, PP, Stats y Captura Oficial
 */
const statCalculator = require('./statCalculator');

// Diccionario estático de movimientos oficiales con su categoría elemental
const MOVES_DICTIONARY = {
    'placaje': { name: 'Placaje', power: 40, maxPp: 35, category: 'physical' },
    'aranazo': { name: 'Arañazo', power: 40, maxPp: 35, category: 'physical' },
    'latigo_cepa': { name: 'Látigo Cepa', power: 45, maxPp: 25, category: 'physical' },
    'impactrueno': { name: 'Impactrueno', power: 40, maxPp: 30, category: 'special' },
    'burbuja': { name: 'Burbuja', power: 40, maxPp: 30, category: 'special' }
};

// Mapeo de sets de movimientos según el ID del Pokémon
const POKEMON_MOVES = {
    1: ['placaje', 'latigo_cepa'],   // Bulbasaur
    4: ['aranazo', 'placaje'],      // Charmander
    7: ['placaje', 'burbuja'],       // Squirtle
    25: ['placaje', 'impactrueno']   // Pikachu
};

/**
 * Ecuación matemática oficial de daño de la saga Pokémon (Game Boy / Consolas)
 */
function calculateOfficialDamage(attackerLevel, movePower, attackerStat, defenderStat) {
    // 🧮 Daño = floor(floor(floor(2 * Nivel / 5 + 2) * Potencia * Atk / Def) / 50) + 2
    const baseValue = Math.floor((2 * attackerLevel) / 5 + 2);
    const splitCalc = Math.floor((baseValue * movePower * attackerStat) / defenderStat);
    const preDamage = Math.floor(splitCalc / 50) + 2;

    // Aplicamos el factor de variación aleatoria oficial (entre 85% y 100%)
    const randomFactor = (Math.floor(Math.random() * 16) + 85) / 100;
    return Math.floor(preDamage * randomFactor);
}

function startPrivateBattle(ws, db, data) {
    // 🛡️ FILTRO DE SEGURIDAD: Validamos que exista un encuentro genético real guardado en la baldosa
    if (!ws.lastWildPokemon) {
        ws.send(JSON.stringify({
            type: 'BATTLE_ERROR',
            message: '¡No hay ningún Pokémon salvaje frente a ti para iniciar un combate legítimo!'
        }));
        return;
    }

    const wild = ws.lastWildPokemon;

    // 🔍 PASO 1: Cargamos los genes del jugador, su nombre y tipos en una query combinada relacional
    const query = `
        SELECT s.*, p.name, p.type_1, p.type_2, p.base_hp, p.base_attack, p.base_defense, p.base_sp_attack, p.base_sp_defense, p.base_speed 
        FROM pokemon_storage s 
        INNER JOIN pokemon_pokedex p ON s.pokemon_id = p.pokemon_id 
        WHERE s.user_id = ? AND s.slot = 1
    `;

    db.execute(query, [data.userId], (err, rows) => {
        if (err || rows.length === 0) {
            ws.send(JSON.stringify({ type: 'BATTLE_ERROR', message: '¡No tienes ningún Pokémon asignado en tu Slot 1 de la mano!' }));
            return;
        }

        const myActivePoke = rows[0];

        if (myActivePoke.hp <= 0) {
            ws.send(JSON.stringify({ type: 'BATTLE_ERROR', message: `¡Tu Pokémon está debilitado! Cúralo en el PC antes de pelear.` }));
            return;
        }

        // Estructuramos el set de estadísticas base del jugador para el calculador
        const playerBase = {
            base_hp: myActivePoke.base_hp,
            base_attack: myActivePoke.base_attack,
            base_defense: myActivePoke.base_defense,
            base_sp_attack: myActivePoke.base_sp_attack,
            base_sp_defense: myActivePoke.base_sp_defense,
            base_speed: myActivePoke.base_speed
        };

        // 🧮 PASO 2: Calculamos los stats reales en combate del jugador mediante la fórmula oficial
        const playerRealStats = statCalculator.calculateStats(playerBase, myActivePoke);

        // Mapeo e inyección de los PP y movimientos reales en memoria
        const moveKeys = POKEMON_MOVES[myActivePoke.pokemon_id] || ['placaje'];
        const playerMoves = moveKeys.map(key => ({
            key: key,
            name: MOVES_DICTIONARY[key].name,
            power: MOVES_DICTIONARY[key].power,
            category: MOVES_DICTIONARY[key].category,
            pp: MOVES_DICTIONARY[key].maxPp,
            maxPp: MOVES_DICTIONARY[key].maxPp
        }));

        // 🔍 PASO 3: Consultamos la Pokédex usando el ID del bicho seguro que guardó server.js
        db.execute('SELECT * FROM pokemon_pokedex WHERE pokemon_id = ?', [wild.pokemonId], (err, pokedexRows) => {
            if (err || pokedexRows.length === 0) return;
            const rivalBase = pokedexRows[0];

            // Reconstruimos la matriz para el calculador cruzando las estadísticas base con el ADN del WebSocket
            const rivalInstance = {
                level: wild.level,
                nature: wild.nature,
                iv_hp: wild.ivs.hp,
                iv_attack: wild.ivs.attack,
                iv_defense: wild.ivs.defense,
                iv_sp_attack: wild.ivs.sp_attack,
                iv_sp_defense: wild.ivs.sp_defense,
                iv_speed: wild.ivs.speed,
                ev_hp: 0, ev_attack: 0, ev_defense: 0, ev_sp_attack: 0, ev_sp_defense: 0, ev_speed: 0
            };

            // 🧮 PASO 4: Calculamos los stats de combate oficiales para el oponente salvaje determinista
            const rivalRealStats = statCalculator.calculateStats(rivalBase, rivalInstance);

            // Asignamos el pool de ataques del rival
            const rivalMoveKeys = POKEMON_MOVES[wild.pokemonId] || ['placaje'];
            const rivalSelectedMoveKey = rivalMoveKeys[Math.floor(Math.random() * rivalMoveKeys.length)];
            const rivalMove = MOVES_DICTIONARY[rivalSelectedMoveKey];

            // 🎒 PASO 5: Consultamos el inventario real de Pokéballs que posee el usuario en la BD
            const inventoryQuery = `
                SELECT i.item_id, i.name, CAST(i.ball_bonus AS FLOAT) as ball_bonus, inv.quantity
                FROM pokemon_inventory inv
                INNER JOIN pokemon_items i ON inv.item_id = i.item_id
                WHERE inv.user_id = ? AND i.type = 'ball'
            `;

            db.execute(inventoryQuery, [data.userId], (err, inventoryRows) => {
                // Si ocurre un error, instanciamos un inventario vacío de emergencia para evitar caídas
                const currentInventory = (!err && inventoryRows) ? inventoryRows : [];

                // Instanciamos el estado de la arena sincronizando todas las propiedades físicas, genéticas e inventario
                ws.battle = {
                    userId: data.userId,
                    ended: false,
                    player: {
                        id: myActivePoke.id,
                        pokemonId: myActivePoke.pokemon_id,
                        name: myActivePoke.name,        // Sincronizado para pintar en UI
                        gender: myActivePoke.gender,    // Sincronizado para pintar en UI
                        type1: myActivePoke.type_1,      // Sincronizado para pintar en UI
                        type2: myActivePoke.type_2,      // Sincronizado para pintar en UI
                        level: myActivePoke.level,
                        hp: myActivePoke.hp,
                        maxHp: playerRealStats.maxHp,
                        stats: playerRealStats,
                        moves: playerMoves
                    },
                    rival: {
                        pokemonId: wild.pokemonId,
                        name: wild.name,
                        level: wild.level,
                        gender: wild.gender,            // Sincronizado para pintar en UI
                        isShiny: wild.isShiny,          // Sincronizado para pintar en UI
                        abilityId: wild.abilityId,
                        type1: wild.type1,              // Sincronizado para pintar en UI
                        type2: wild.type2,              // Sincronizado para pintar en UI
                        hp: rivalRealStats.maxHp,
                        maxHp: rivalRealStats.maxHp,
                        stats: rivalRealStats,
                        catchRate: rivalBase.catch_rate || 190, // 🎯 Guardado el ratio de captura nativo de la Pokédex
                        routeName: wild.routeName || data.routeName,
                        moveKey: rivalSelectedMoveKey,
                        moveName: rivalMove.name,
                        moveCategory: rivalMove.category,
                        movePower: rivalMove.power
                    },
                    inventory: currentInventory, // 🎒 Inyectado el inventario sincronizado de Pokéballs
                    turn: 'player',
                    log: `¡Un ${wild.name} salvaje apareció en la ruta!`
                };

                console.log(`[COMBATE] Arena vinculada con éxito al ADN del Pokémon e inventario del usuario.`);
                ws.send(JSON.stringify({ type: 'BATTLE_STARTED', battle: ws.battle }));
            });
        });
    });
}

function processBattleAttack(ws, db, data) {
    if (!ws.battle || ws.battle.ended || ws.battle.turn !== 'player') return;

    const moveIndex = data.moveIndex;
    const selectedMove = ws.battle.player.moves[moveIndex];
    if (!selectedMove) return;

    if (selectedMove.pp <= 0) {
        ws.send(JSON.stringify({ type: 'BATTLE_ERROR', message: `¡No te quedan PP para usar ${selectedMove.name}!` }));
        return;
    }

    selectedMove.pp--;

    const p = ws.battle.player;
    const r = ws.battle.rival;

    // -------------------------------------------------------------------------
    // ⚔️ TURNO JUGADOR: Ejecución matemática de daño cruzado (Atk vs Def)
    // -------------------------------------------------------------------------
    let atkStat = selectedMove.category === 'special' ? p.stats.spAttack : p.stats.attack;
    let defStat = selectedMove.category === 'special' ? r.stats.spDefense : r.stats.defense;

    const playerDmg = calculateOfficialDamage(p.level, selectedMove.power, atkStat, defStat);
    r.hp -= playerDmg;
    let battleLog = `¡Tu ${p.name} usó ${selectedMove.name}!\nCausó ${playerDmg} de daño al rival.`;

    if (r.hp <= 0) {
        r.hp = 0;
        ws.battle.ended = true;
        battleLog += `\n¡El ${r.name} salvaje se ha debilitado! Has ganado. 🎉`;
        ws.send(JSON.stringify({ type: 'BATTLE_UPDATE', battle: ws.battle, log: battleLog }));
        return;
    }

    // -------------------------------------------------------------------------
    // ⚔️ TURNO RIVAL: IA responde usando sus atributos de ataque y categoría
    // -------------------------------------------------------------------------
    ws.battle.turn = 'rival';
    let rivalAtkStat = r.moveCategory === 'special' ? r.stats.spAttack : r.stats.attack;
    let rivalDefStat = r.moveCategory === 'special' ? p.stats.spDefense : p.stats.defense;

    const rivalDmg = calculateOfficialDamage(r.level, r.movePower, rivalAtkStat, rivalDefStat);
    p.hp -= rivalDmg;
    battleLog += `\n\n¡El ${r.name} rival respondió con ${r.moveName}!\nTe infligió ${rivalDmg} de daño.`;

    if (p.hp <= 0) {
        p.hp = 0;
        ws.battle.ended = true;
        battleLog += `\n¡Tu ${p.name} cayó debilitado! Has perdido. 💀`;

        // 🔄 SINCRONIZACIÓN AUTOMÁTICA: Tu bicho cayó debilitado, informamos al inventario inmediatamente
        db.execute('UPDATE pokemon_storage SET hp = 0 WHERE id = ?', [p.id], () => {
            ws.send(JSON.stringify({ type: 'REFRESH_PC_DATA', userId: ws.battle.userId }));
        });
        ws.send(JSON.stringify({ type: 'BATTLE_UPDATE', battle: ws.battle, log: battleLog }));
        return;
    }

    // -------------------------------------------------------------------------
    // 💾 PERSISTENCIA EN BD Y CONCESIÓN DE RETORNO DE TURNO
    // -------------------------------------------------------------------------
    db.execute(
        'UPDATE pokemon_storage SET hp = ? WHERE id = ?',
        [p.hp, p.id],
        (err) => {
            if (!err) {
                // 🔄 SINCRONIZACIÓN AUTOMÁTICA: Ha recibido daño, refrescamos el inventario en tiempo real
                ws.send(JSON.stringify({ type: 'REFRESH_PC_DATA', userId: ws.battle.userId }));

                ws.battle.turn = 'player';
                ws.send(JSON.stringify({ type: 'BATTLE_UPDATE', battle: ws.battle, log: battleLog }));
            }
        }
    );
}

/**
 * 🔴 LÓGICA DE CAPTURA - Fórmula Matemática Oficial de la 3ª Generación (GBA)
 */
function processThrowBall(ws, db, data) {
    if (!ws.battle || ws.battle.ended || ws.battle.turn !== 'player') return;

    const itemId = data.itemId;
    const ball = ws.battle.inventory.find(i => i.item_id === itemId);

    // 🛡️ Filtro de seguridad: Verificar que el cliente posee la bola seleccionada
    if (!ball || ball.quantity <= 0) {
        ws.send(JSON.stringify({ type: 'BATTLE_ERROR', message: '¡No te quedan esferas de ese tipo en tu mochila!' }));
        return;
    }

    ball.quantity--;

    // 🛡️ Envolvemos secuencialmente el flujo dentro del callback de la actualización del inventario
    db.execute('UPDATE pokemon_inventory SET quantity = quantity - 1 WHERE user_id = ? AND item_id = ?', [ws.battle.userId, itemId], (errInventory) => {
        if (errInventory) {
            console.error('[DATABASE ERROR] Error al restar bola de la mochila:', errInventory);
            return;
        }

        const p = ws.battle.player;
        const r = ws.battle.rival;
        const wild = ws.lastWildPokemon;

        // 🧮 ECUACIÓN DE CAPTURA MATEMÁTICA DE TERCERA GENERACIÓN
        const topFactor = (3 * r.maxHp) - (2 * r.hp);
        const rawRate = Math.floor((topFactor * r.catchRate * ball.ball_bonus) / (3 * r.maxHp));

        // Tiro de dados determinista (0-255)
        const roll = Math.floor(Math.random() * 256);
        let battleLog = `¡Has lanzado una ${ball.name}!\n`;

        if (roll <= rawRate) {
            // 🥳 ¡CAPTURADO CON ÉXITO!
            // Comprobamos cuántos Pokémon tiene el usuario activos en su mano (slots 1 a 6)
            db.execute('SELECT COUNT(*) AS total FROM pokemon_storage WHERE user_id = ? AND slot >= 1 AND slot <= 6', [ws.battle.userId], (err, rows) => {
                const teamCount = (!err && rows.length > 0) ? rows[0].total : 6;

                const destinationSlot = teamCount < 6 ? teamCount + 1 : 0;
                const destinationText = destinationSlot > 0 ? 'tu EQUIPO ACTIVO' : 'el ALMACÉN del PC de Bill';

                const insertQuery = `
                    INSERT INTO pokemon_storage (user_id, pokemon_id, level, hp, max_hp, slot, gender, nature, iv_hp, iv_attack, iv_defense, iv_sp_attack, iv_sp_defense, iv_speed, is_shiny, ability_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                `;

                db.execute(insertQuery, [
                    ws.battle.userId, r.pokemonId, r.level, r.hp, r.maxHp, destinationSlot, r.gender, wild.nature,
                    wild.ivs.hp, wild.ivs.attack, wild.ivs.defense, wild.ivs.sp_attack, wild.ivs.sp_defense, wild.ivs.speed,
                    r.isShiny, r.abilityId
                ], (errInsert) => {
                    if (errInsert) {
                        console.error('[DATABASE ERROR] Fallo crítico al insertar espécimen:', errInsert);
                        return;
                    }

                    battleLog += `¡Chof... Chof... Chof... ¡Yatach!\n¡El ${r.name} salvaje ha sido capturado con éxito! 🎉\n\nSe ha guardado en ${destinationText}.`;

                    ws.battle.ended = true;
                    ws.battle.log = battleLog;

                    // 🔄 SINCRONIZACIÓN AUTOMÁTICA: Bicho capturado, enviamos refresco inmediato para el PC
                    ws.send(JSON.stringify({ type: 'REFRESH_PC_DATA', userId: ws.battle.userId }));

                    // Limpieza de memoria post-captura: el bicho deja de existir en la baldosa del hotel
                    ws.lastWildPokemon = null;

                    ws.send(JSON.stringify({ type: 'BATTLE_UPDATE', battle: ws.battle, log: battleLog }));
                });
            });
        } else {
            // 😢 CAPTURA FALLIDA: El Pokémon se escapa de la bola y consume tu turno (Te contraataca)
            battleLog += `¡Oh, no! El Pokémon se ha liberado de la esfera.\n\n`;
            ws.battle.turn = 'rival';

            let rivalAtkStat = r.moveCategory === 'special' ? r.stats.spAttack : r.stats.attack;
            let rivalDefStat = r.moveCategory === 'special' ? p.stats.spDefense : p.stats.defense;

            const rivalDmg = calculateOfficialDamage(r.level, r.movePower, rivalAtkStat, rivalDefStat);
            p.hp -= rivalDmg;
            battleLog += `¡El ${r.name} salvaje contraatacó con ${r.moveName}!\nTe infligió ${rivalDmg} de daño.`;

            // Si su contraataque te debilita por completo
            if (p.hp <= 0) {
                p.hp = 0;
                ws.battle.ended = true;
                battleLog += `\n¡Tu ${p.name} cayó debilitado! Has perdido el combate. 💀`;

                // 🔄 SINCRONIZACIÓN AUTOMÁTICA: Caíste debilitado tras fallo de captura, refrescamos inventario
                db.execute('UPDATE pokemon_storage SET hp = 0 WHERE id = ?', [p.id], () => {
                    ws.send(JSON.stringify({ type: 'REFRESH_PC_DATA', userId: ws.battle.userId }));
                });
                ws.send(JSON.stringify({ type: 'BATTLE_UPDATE', battle: ws.battle, log: battleLog }));
                return;
            }

            // Si sobreviviste, actualiza tu vida en la BD y devuélvele el turno al jugador
            db.execute('UPDATE pokemon_storage SET hp = ? WHERE id = ?', [p.hp, p.id], (err) => {
                if (!err) {
                    // 🔄 SINCRONIZACIÓN AUTOMÁTICA: Recibiste daño del contraataque salvaje, refrescamos inventario
                    ws.send(JSON.stringify({ type: 'REFRESH_PC_DATA', userId: ws.battle.userId }));

                    ws.battle.turn = 'player';
                    ws.send(JSON.stringify({ type: 'BATTLE_UPDATE', battle: ws.battle, log: battleLog }));
                }
            });
        }
    });
}

module.exports = {
    startPrivateBattle,
    processBattleAttack,
    processThrowBall
};