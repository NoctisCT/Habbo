import { useEffect, useState, useCallback } from 'react';

// 🌍 INSTANCIA ÚNICA GLOBAL (Compartida por todos los componentes del ecosistema)
let globalSocket: WebSocket | null = null;
const stateListeners = new Set<() => void>();

let globalPokemonList: any[] = [];
let globalInventoryList: any[] = []; // 🎒 NUEVO: Inventario unificado global
let globalWildEncounter: any | null = null;
let globalBattleState: any | null = null;

export const usePokemonSocket = (userId: number) => {
    // Inicializamos los estados locales apuntando a las referencias globales compartidas
    const [pokemonList, setPokemonList] = useState<any[]>(globalPokemonList);
    const [inventoryList, setInventoryList] = useState<any[]>(globalInventoryList); // 🎒 NUEVO
    const [wildEncounter, setWildEncounter] = useState<any | null>(globalWildEncounter);
    const [battleState, setBattleState] = useState<any | null>(globalBattleState);

    // 🔄 Sincronizador radial: Propaga los cambios de red a todos los componentes activos
    useEffect(() => {
        const updateLocalStates = () => {
            setPokemonList([...globalPokemonList]); // 🌟 Rompe la referencia para forzar el re-renderizado
            setInventoryList([...globalInventoryList]); // 🎒 NUEVO
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
                ws.send(JSON.stringify({ type: 'GET_INVENTORY_DATA', userId })); // 🎒 NUEVO: Pedimos la mochila al conectar
            };

            ws.onmessage = (event) => {
                const data = JSON.parse(event.data);

                if (data.type === 'PC_DATA_RESPONSE') {
                    globalPokemonList = [...data.pokemon]; // 🌟 Forzamos nueva referencia limpia
                    broadcastStateChange();
                }

                // =========================================================================
                // 🎒 NUEVO ESCUCHADOR: RECEPCIÓN DE INVENTARIO DESDE NODE
                // =========================================================================
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
                // ⚔️ NUEVOS ESCUCHADORES ENTRANTE DE COMBATE
                // =========================================================================
                if (data.type === 'BATTLE_STARTED' || data.type === 'BATTLE_UPDATE') {
                    if (data.log) data.battle.log = data.log;
                    globalBattleState = data.battle;
                    broadcastStateChange();
                }

                if (data.type === 'BATTLE_ERROR') {
                    alert(data.message);
                }

                // =========================================================================
                // 🏥 NUEVOS ESCUCHADORES DE ASISTENCIA MÉDICA Y FARMACIA
                // =========================================================================
                if (data.type === 'TEAM_HEALED_BY_BOT') {
                    window.dispatchEvent(new CustomEvent('pokemon:team_healed', { detail: data.message }));
                }

                if (data.type === 'ITEM_CONSUMED_SUCCESS') {
                    alert(data.message);
                }

                if (data.type === 'HEAL_ERROR') {
                    window.dispatchEvent(new CustomEvent('pokemon:heal_error', { detail: data.message }));
                }

                if (data.type === 'ITEM_ERROR') {
                    alert(data.message);
                }

                // Al recibir la orden de refresco, auto-solicitamos los datos limpios a Node
                if (data.type === 'REFRESH_PC_DATA') {
                    if (globalSocket?.readyState === WebSocket.OPEN) {
                        globalSocket.send(JSON.stringify({ type: 'GET_PC_DATA', userId }));
                        globalSocket.send(JSON.stringify({ type: 'GET_INVENTORY_DATA', userId })); // 🎒 NUEVO: Refresca también mochila
                    }
                }
            };

            ws.onclose = () => {
                console.log('[POKÉMON] Conexión cerrada de WebSockets. Limpiando canal.');
                globalSocket = null;
            };
        } else {
            // Si el canal ya existe y se monta un nuevo componente, refrescamos sus datos al instante
            if (globalSocket.readyState === WebSocket.OPEN) {
                globalSocket.send(JSON.stringify({ type: 'GET_PC_DATA', userId }));
                globalSocket.send(JSON.stringify({ type: 'GET_INVENTORY_DATA', userId })); // 🎒 NUEVO: Sincroniza al montar componentes tardíos
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

    // =========================================================================
    // ⚔️ NUEVAS EMISIONES DE EVENTOS DE COMBATE HACIA NODE
    // =========================================================================

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

    // =========================================================================
    // 🏥 NUEVAS EMISIONES MÉDICAS (BOT JOY + USO MOCHILA)
    // =========================================================================

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
        inventoryList, // 🎒 NUEVO: Exportamos la lista en tiempo real para el inventario
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