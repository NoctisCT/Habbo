/**
 * Motor de Soporte Médico y Farmacia - Módulo Unificado de Curación (Bot Joy + Mochila Consumibles)
 */
const statCalculator = require('./statCalculator');

// Diccionario de consumibles médicos oficiales indexados en el rango seguro (10-16)
const CONSUMABLES_DICTIONARY = {
    10: { name: 'Poción', healAmount: 20, isRevive: false },
    11: { name: 'Superpoción', healAmount: 50, isRevive: false },
    12: { name: 'Hiperpoción', healAmount: 200, isRevive: false },
    13: { name: 'Poción Máxima', healAmount: 'max', isRevive: false },
    14: { name: 'Restaurar Todo', healAmount: 'max', isRevive: false },
    15: { name: 'Revivir', healPercentage: 0.5, isRevive: true },
    16: { name: 'Max. Revivir', healPercentage: 1.0, isRevive: true }
};

/**
 * MÉTODO A: Restauración masiva instantánea en el Centro Pokémon (Bot/NPC) con coste de Moneda Pokémon
 */
function healTeamAtBot(ws, db, data) {
    const userId = data.userId;
    const PRECIO_CURACION = 50; // 💰 Coste en PokéDollars de la curación del bot

    // 🛡️ Filtro de seguridad: validamos que llegue un ID de usuario legítimo
    if (!userId) {
        if (ws && ws.readyState === 1) {
            ws.send(JSON.stringify({
                type: 'HEAL_ERROR',
                message: '¡No se ha podido identificar al entrenador para iniciar el tratamiento médico!'
            }));
        }
        return;
    }

    // 🛡️ Filtro de seguridad extra: Bloqueamos curación si está en mitad de un combate activo
    if (ws.battle && !ws.battle.ended) {
        if (ws && ws.readyState === 1) {
            ws.send(JSON.stringify({
                type: 'HEAL_ERROR',
                message: '¡No puedes curar a tus Pokémon mientras estás en mitad de un combate en la ruta!'
            }));
        }
        return;
    }

    // 🌟 UNA SOLA CONSULTA MULTI-TABLA: Une carteras con almacenamiento, resta saldo y cura al equipo activo
    const singleQuery = `
        UPDATE pokemon_trainers t
        JOIN pokemon_storage s ON t.user_id = s.user_id
        SET t.money = t.money - ?,
            s.hp = s.max_hp
        WHERE t.user_id = ? AND t.money >= ? AND s.slot BETWEEN 1 AND 6
    `;

    // 🔬 Usamos db.query obligatoriamente para soportar consultas UPDATE compuestas con JOIN sin romper el pool binario
    db.query(singleQuery, [PRECIO_CURACION, userId, PRECIO_CURACION], (err, result) => {
        if (err) {
            console.error('[DATABASE ERROR] Fallo crítico en el tratamiento de la Enfermera Joy:', err);
            if (ws && ws.readyState === 1) {
                ws.send(JSON.stringify({
                    type: 'HEAL_ERROR',
                    message: 'La máquina de curación del Centro Pokémon ha sufrido un cortocircuito. Inténtalo de nuevo.'
                }));
            }
            return;
        }

        // Si affectedRows es 0 significa que el WHERE rechazó la transacción (dinero insuficiente o equipo vacío)
        if (result.affectedRows === 0) {
            if (ws && ws.readyState === 1) {
                ws.send(JSON.stringify({
                    type: 'HEAL_ERROR',
                    message: `No se pudo procesar: O no tienes los ${PRECIO_CURACION} ₽ requeridos o tu equipo activo (Slots 1-6) está vacío.`
                }));
            }
            return;
        }

        console.log(`[CENTRO POKÉMON] Cobrados ${PRECIO_CURACION}₽ y salud restaurada por SQL para el Entrenador ID: ${userId}`);

        // Confirmación para la UI si el socket sigue vivo
        if (ws && ws.readyState === 1) {
            ws.send(JSON.stringify({
                type: 'TEAM_HEALED_BY_BOT',
                message: `¡Tu equipo Pokémon ha sido restaurado por completo por ${PRECIO_CURACION} ₽! ¡Vuelve cuando quieras! ❤️`
            }));

            // 🔄 SINCRONIZACIÓN RADIAL: Forzamos el refresco inmediato de datos en el cliente
            ws.send(JSON.stringify({ type: 'REFRESH_PC_DATA', userId: userId }));
        }
    });
}

/**
 * MÉTODO B: Uso de medicinas individuales desde la Mochila (Pociones/Revivir)
 */
function processUseHealingItem(ws, db, data) {
    const { userId, itemId, pokemonStorageId } = data;

    // 🛡️ FILTRO 1: Validamos que el ítem exista en nuestro diccionario médico
    const itemEffect = CONSUMABLES_DICTIONARY[itemId];
    if (!itemEffect) {
        if (ws && ws.readyState === 1) ws.send(JSON.stringify({ type: 'ITEM_ERROR', message: '¡Este objeto no se puede consumable o no está catalogado!' }));
        return;
    }

    // 🛡️ REGLA MODIFICADA: Quitamos el bloqueo restrictivo tosco. Como el front ya bloquea la mochila ordinaria, 
    // cualquier curación con ws.battle activo es una acción legítima de combate que consume turno.
    if (ws.battle && !ws.battle.ended && ws.battle.type === 'PVP') {
        if (ws && ws.readyState === 1) ws.send(JSON.stringify({ type: 'ITEM_ERROR', message: '¡No puedes usar objetos de soporte médico en batallas clasificatorias PVP contra otros entrenadores!' }));
        return;
    }

    // 🔍 PASO 1: Verificamos que el usuario posee el ítem en la BD
    const invQuery = 'SELECT quantity FROM pokemon_inventory WHERE user_id = ? AND item_id = ?';
    db.execute(invQuery, [userId, itemId], (errInv, invRows) => {
        if (errInv || !invRows || invRows.length === 0 || invRows[0].quantity <= 0) {
            if (ws && ws.readyState === 1) ws.send(JSON.stringify({ type: 'ITEM_ERROR', message: `¡No te quedan unidades de ${itemEffect.name} en la mochila!` }));
            return;
        }

        // 🔍 PASO 2: Consultamos el estado biológico actual del Pokémon objetivo
        const pokeQuery = `
            SELECT s.*, p.base_hp, p.base_attack, p.base_defense, p.base_sp_attack, p.base_sp_defense, p.base_speed
            FROM pokemon_storage s
            INNER JOIN pokemon_pokedex p ON s.pokemon_id = p.pokemon_id
            WHERE s.id = ? AND s.user_id = ?
        `;
        db.execute(pokeQuery, [pokemonStorageId, userId], (errPoke, pokeRows) => {
            if (errPoke || !pokeRows || pokeRows.length === 0) {
                if (ws && ws.readyState === 1) ws.send(JSON.stringify({ type: 'ITEM_ERROR', message: '¡No se ha encontrado al Pokémon objetivo en tu almacenamiento!' }));
                return;
            }

            const pokemon = pokeRows[0];

            // Calculamos sus stats máximos en vivo para saber su tope de HP real actual
            const baseStats = {
                base_hp: pokemon.base_hp,
                base_attack: pokemon.base_attack,
                base_defense: pokemon.base_defense,
                base_sp_attack: pokemon.base_sp_attack,
                base_sp_defense: pokemon.base_sp_defense,
                base_speed: pokemon.base_speed
            };
            const realStats = statCalculator.calculateStats(baseStats, pokemon);
            const maxHp = realStats.maxHp;

            // 🛡️ FILTROS DE ESTADO CLÁSICOS DE LA SAGA POKÉMON
            if (pokemon.hp === 0 && !itemEffect.isRevive) {
                if (ws && ws.readyState === 1) ws.send(JSON.stringify({ type: 'ITEM_ERROR', message: `¡${pokemon.name} está debilitado! Las pociones no le harán efecto. Usa un Revivir.` }));
                return;
            }

            if (pokemon.hp > 0 && itemEffect.isRevive) {
                if (ws && ws.readyState === 1) ws.send(JSON.stringify({ type: 'ITEM_ERROR', message: `¡${pokemon.name} no está debilitado! No puedes usar un Revivir en él.` }));
                return;
            }

            if (pokemon.hp === maxHp) {
                if (ws && ws.readyState === 1) ws.send(JSON.stringify({ type: 'ITEM_ERROR', message: `¡La salud de ${pokemon.name} ya está al máximo de sus capacidades!` }));
                return;
            }

            // 🧮 PASO 3: Ejecución matemática del efecto curativo según categoría
            let finalHp = pokemon.hp;

            if (itemEffect.isRevive) {
                finalHp = Math.floor(maxHp * itemEffect.healPercentage);
            } else {
                if (itemEffect.healAmount === 'max') {
                    finalHp = maxHp;
                } else {
                    finalHp = Math.min(maxHp, pokemon.hp + itemEffect.healAmount);
                }
            }

            // 💾 PASO 4: Transacción asíncrona controlada en la Base de Datos
            db.execute('UPDATE pokemon_inventory SET quantity = quantity - 1 WHERE user_id = ? AND item_id = ?', [userId, itemId], (errSubItem) => {
                if (errSubItem) return;

                db.execute('UPDATE pokemon_storage SET hp = ? WHERE id = ?', [finalHp, pokemonStorageId], (errHeal) => {
                    if (errHeal) return;

                    console.log(`[MOCHILA] Ítem ${itemEffect.name} consumido. Pokémon ID ${pokemonStorageId} sanado a ${finalHp}/${maxHp} HP.`);

                    // ⚔️ PASO 4.5: GASTO DE TURNO EN ARENA (Sincronizamos la RAM del encuentro vivo en el Servidor)
                    if (ws.battle && !ws.battle.ended) {
                        // Si el bicho curado es el que está en combate activo, actualizamos sus HP en la arena
                        if (ws.battle.player && (ws.battle.player.id === pokemonStorageId || ws.battle.player.storageId === pokemonStorageId)) {
                            ws.battle.player.hp = finalHp;
                        }

                        // Inyectamos el suceso en el log global para renderizarlo en el pie de la arena
                        const stringLog = `\n¡Has usado ${itemEffect.name} en ${pokemon.name}! Recuperó salud.`;
                        ws.battle.log = ws.battle.log ? ws.battle.log + stringLog : stringLog;

                        // 🔄 CEDER EL TURNO: Pasamos el token al bot para que ejecute su ataque de respuesta
                        ws.battle.turn = 'rival';

                        // Despachamos la actualización de la arena viva a React para refrescar barras y congelar botones
                        if (ws && ws.readyState === 1) {
                            ws.send(JSON.stringify({
                                type: 'BATTLE_UPDATE',
                                battleState: ws.battle
                            }));
                        }
                    }

                    // 🥳 PASO 5: Éxito y sincronización radial con la UI
                    if (ws && ws.readyState === 1) {
                        ws.send(JSON.stringify({
                            type: 'ITEM_CONSUMED_SUCCESS',
                            message: `¡Has usado ${itemEffect.name} en ${pokemon.name} con éxito! 💊`
                        }));

                        ws.send(JSON.stringify({ type: 'REFRESH_PC_DATA', userId: userId }));
                    }
                });
            });
        });
    });
}

module.exports = {
    healTeamAtBot,
    processUseHealingItem
};