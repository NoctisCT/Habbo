import { useEffect, useState, useCallback } from 'react';

// 🌍 INSTANCIA ÚNICA GLOBAL (Compartida por todos los componentes del ecosistema)
let globalSocket: WebSocket | null = null;
const stateListeners = new Set<() => void>();

let globalPokemonList: any[] = [];
let globalInventoryList: any[] = []; // 🎒 Inventario unificado global
let globalWildEncounter: any | null = null;
let globalBattleState: any | null = null;

export const usePokemonSocket = (userId: number) => {
    // Inicializamos los estados locales apuntando a las referencias globales compartidas
    const [pokemonList, setPokemonList] = useState<any[]>(globalPokemonList);
    const [inventoryList, setInventoryList] = useState<any[]>(globalInventoryList);
    const [wildEncounter, setWildEncounter] = useState<any | null>(globalWildEncounter);
    const [battleState, setBattleState] = useState<any | null>(globalBattleState);

    // 🔄 Sincronizador radial: Propaga los cambios de red a todos los componentes activos
    useEffect(() => {
        const updateLocalStates = () => {
            setPokemonList([...globalPokemonList]); // 🌟 Rompe la referencia para forzar el re-renderizado
            setInventoryList([...globalInventoryList]);
            setWildEncounter(globalWildEncounter);
            setBattleState(globalBattleState);
        };
        stateListeners.add(updateLocalStates);
        return () => {
            stateListeners.delete(updateLocalStates);
        };
    }, []);

    const broadcastStateChange = () => stateListeners.forEach(listener => listener());

    useEffect(() => {
        if (!userId) return;

        // Si no hay ninguna conexión global activa, levantamos el socket rey
        if (!globalSocket) {
            const ws = new WebSocket('ws://localhost:8085');
            globalSocket = ws;

            ws.onopen = () => {
                console.log('[POKÉMON] Conectado al backend de WebSockets (Canal Unificado)');
                ws.send(JSON.stringify({ type: 'GET_PC_DATA', userId }));
                ws.send(JSON.stringify({ type: 'GET_INVENTORY_DATA', userId }));
            };

            ws.onmessage = (event) => {
                const data = JSON.parse(event.data);

                if (data.type === 'PC_DATA_RESPONSE') {
                    globalPokemonList = [...data.pokemon]; // 🌟 Forzamos nueva referencia limpia
                    broadcastStateChange();
                }

                if (data.type === 'INVENTORY_DATA_RESPONSE') {
                    globalInventoryList = [...data.items];
                    broadcastStateChange();
                }

                if (data.type === 'SWAP_SUCCESS') {
                    globalPokemonList = globalPokemonList.map(p => p.id === data.id ? { ...p, slot: data.slot } : p);
                    broadcastStateChange();
                }

                if (data.type === 'WILD_ENCOUNTER') {
                    console.log('[POKÉMON] ¡Encuentro salvaje recibido del servidor!', data);
                    globalWildEncounter = data;
                    broadcastStateChange();
                }

                // =========================================================================
                // ⚔️ ESCUCHADORES ENTRANTE DE COMBATE
                // =========================================================================
                if (data.type === 'BATTLE_STARTED' || data.type === 'BATTLE_UPDATE') {
                    if (data.battle) {
                        // 🛠️ PARCHE: Acumulamos el historial de logs para que el flujo de turnos sea continuo
                        const currentLog = globalBattleState?.log || '';
                        const nextLog = data.log || data.battle.log || '';

                        globalBattleState = data.battle;
                        globalBattleState.log = currentLog && nextLog ? `${currentLog}\n${nextLog}` : (nextLog || currentLog);
                        broadcastStateChange();
                    }
                }

                if (data.type === 'BATTLE_ERROR') {
                    alert(data.message);
                }

                // =========================================================================
                // 🏥 ESCUCHADORES DE ASISTENCIA MÉDICA Y FARMACIA
                // =========================================================================
                if (data.type === 'TEAM_HEALED_BY_BOT') {
                    window.dispatchEvent(new CustomEvent('pokemon:team_healed', { detail: data.message }));
                }

                if (data.type === 'ITEM_CONSUMED_SUCCESS') {
                    console.log('[POKÉMON] Objeto aplicado con éxito en combate.');

                    // 🛠️ PARCHE: Al usar un ítem, asimilamos el estado actualizado de la arena que manda Node.
                    // Esto actualiza las barras de vida al instante (Bugs 1 y 2) y desbloquea los movimientos (Bug 4)
                    if (globalBattleState) {
                        const logHistorial = globalBattleState.log || '';
                        const txtCuracion = data.message || 'Se ha utilizado un objeto curativo.';

                        globalBattleState = data.battle ? data.battle : { ...globalBattleState };

                        // Inyectamos el aviso de curación y el posible contraataque del rival en el log de la arena (Bug 3)
                        globalBattleState.log = logHistorial ? `${logHistorial}\n🧪 ${txtCuracion}` : `🧪 ${txtCuracion}`;
                        if (data.log) globalBattleState.log += `\n${data.log}`;
                    } else if (data.battle) {
                        globalBattleState = data.battle;
                        if (data.log) globalBattleState.log = data.log;
                    }
                    broadcastStateChange();
                }

                if (data.type === 'HEAL_ERROR') {
                    window.dispatchEvent(new CustomEvent('pokemon:heal_error', { detail: data.message }));
                }

                if (data.type === 'ITEM_ERROR') {
                    alert(data.message);
                }

                // =========================================================================
                // 🚨 GESTIÓN ANTICOLISIÓN: REFRESCAR DATOS SIN ROMPER LA ARENA
                // =========================================================================
                if (data.type === 'REFRESH_PC_DATA') {
                    if (globalSocket?.readyState === WebSocket.OPEN) {
                        // 🔒 CANDADO DE SEGURIDAD: Si estamos en combate activo, PROHIBIDO pedir GET_PC_DATA.
                        // Evitamos que Node limpie la propiedad temporal de la arena al procesar el PC.
                        if (!globalBattleState) {
                            globalSocket.send(JSON.stringify({ type: 'GET_PC_DATA', userId }));
                        }
                        // La mochila siempre se puede y se debe actualizar para ver las unidades restantes
                        globalSocket.send(JSON.stringify({ type: 'GET_INVENTORY_DATA', userId }));
                    }
                }
            };

            ws.onclose = () => {
                console.log('[POKÉMON] Conexión cerrada de WebSockets. Limpiando canal.');
                globalSocket = null;
            };
        } else {
            if (globalSocket.readyState === WebSocket.OPEN) {
                globalSocket.send(JSON.stringify({ type: 'GET_PC_DATA', userId }));
                globalSocket.send(JSON.stringify({ type: 'GET_INVENTORY_DATA', userId }));
            }
        }
    }, [userId]);

    const movePokemon = useCallback((pokemonStorageId: number, newSlot: number) => {
        if (globalSocket?.readyState === WebSocket.OPEN) {
            globalSocket.send(JSON.stringify({
                type: 'SWAP_SLOT',
                pokemonStorageId,
                newSlot
            }));
        }
    }, []);

    const sendRoomEntry = useCallback((roomId: number) => {
        if (globalSocket?.readyState === WebSocket.OPEN) {
            globalSocket.send(JSON.stringify({ type: 'ENTERED_ROOM', roomId }));
            return true;
        }
        return false;
    }, []);

    const sendStep = useCallback(() => {
        if (globalSocket?.readyState === WebSocket.OPEN) {
            globalSocket.send(JSON.stringify({ type: 'USER_STEP' }));
        }
    }, []);

    const startPrivateBattle = useCallback((pokemonId: number, level: number, routeName: string) => {
        if (globalSocket?.readyState === WebSocket.OPEN) {
            globalSocket.send(JSON.stringify({
                type: 'START_PRIVATE_BATTLE',
                userId,
                pokemonId,
                level,
                routeName
            }));
        }
    }, [userId]);

    const sendBattleAttack = useCallback((moveIndex: number) => {
        if (globalSocket?.readyState === WebSocket.OPEN) {
            globalSocket.send(JSON.stringify({
                type: 'BATTLE_ATTACK',
                moveIndex
            }));
        }
    }, []);

    const sendThrowBall = useCallback((itemId: number) => {
        if (globalSocket?.readyState === WebSocket.OPEN) {
            globalSocket.send(JSON.stringify({
                type: 'THROW_POKEBALL',
                itemId
            }));
        }
    }, []);

    const sendLeaveBattle = useCallback(() => {
        if (globalSocket?.readyState === WebSocket.OPEN) {
            globalSocket.send(JSON.stringify({ type: 'CLOSE_BATTLE' }));
        }
    }, []);

    const sendHealTeam = useCallback(() => {
        if (globalSocket?.readyState === WebSocket.OPEN) {
            globalSocket.send(JSON.stringify({ type: 'HEAL_TEAM_BOT', userId }));
        }
    }, [userId]);

    const sendUseHealingItem = useCallback((itemId: number, pokemonStorageId: number) => {
        if (globalSocket?.readyState === WebSocket.OPEN) {
            globalSocket.send(JSON.stringify({
                type: 'USE_HEALING_ITEM',
                userId,
                itemId,
                pokemonStorageId
            }));
        }
    }, [userId]);

    return {
        pokemonList,
        inventoryList,
        movePokemon,
        wildEncounter,
        setWildEncounter: (val: any) => { globalWildEncounter = val; broadcastStateChange(); },
        sendRoomEntry,
        sendStep,
        battleState,
        setBattleState: (val: any) => { globalBattleState = val; broadcastStateChange(); },
        startPrivateBattle,
        sendBattleAttack,
        sendThrowBall,
        sendLeaveBattle,
        sendHealTeam,
        sendUseHealingItem
    };
};