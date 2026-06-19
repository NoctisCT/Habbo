import { useEffect, useRef, useState, useCallback } from 'react';

export const usePokemonSocket = (userId: number) => {
    const [pokemonList, setPokemonList] = useState<any[]>([]);
    const [wildEncounter, setWildEncounter] = useState<any | null>(null);

    // 🚨 NUEVO: Estado modular para almacenar la información de la batalla activa
    const [battleState, setBattleState] = useState<any | null>(null);

    const socketRef = useRef<WebSocket | null>(null);

    useEffect(() => {
        const ws = new WebSocket('ws://localhost:8085');
        socketRef.current = ws;

        ws.onopen = () => {
            console.log('[POKÉMON] Conectado al backend de WebSockets');
            ws.send(JSON.stringify({ type: 'GET_PC_DATA', userId }));
        };

        ws.onmessage = (event) => {
            const data = JSON.parse(event.data);

            if (data.type === 'PC_DATA_RESPONSE') {
                setPokemonList(data.pokemon);
            }

            if (data.type === 'SWAP_SUCCESS') {
                setPokemonList(prevList =>
                    prevList.map(p => p.id === data.id ? { ...p, slot: data.slot } : p)
                );
            }

            if (data.type === 'WILD_ENCOUNTER') {
                console.log('[POKÉMON] ¡Encuentro salvaje recibido del servidor!', data);
                setWildEncounter(data);
            }

            // =========================================================================
            // ⚔️ NUEVOS ESCUCHADORES ENTRANTES DE COMBATE
            // =========================================================================
            if (data.type === 'BATTLE_STARTED' || data.type === 'BATTLE_UPDATE') {
                // Si el servidor envía un log específico de los golpes, lo acoplamos al estado
                if (data.log) data.battle.log = data.log;
                setBattleState(data.battle);
            }

            if (data.type === 'BATTLE_ERROR') {
                alert(data.message);
            }
        };

        return () => {
            ws.close();
            socketRef.current = null;
        };
    }, [userId]);

    const movePokemon = useCallback((pokemonStorageId: number, newSlot: number) => {
        if (socketRef.current?.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify({
                type: 'SWAP_SLOT',
                pokemonStorageId,
                newSlot
            }));
        }
    }, []);

    const sendRoomEntry = useCallback((roomId: number) => {
        if (socketRef.current?.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify({ type: 'ENTERED_ROOM', roomId }));
            return true;
        }
        return false;
    }, []);

    const sendStep = useCallback(() => {
        if (socketRef.current?.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify({ type: 'USER_STEP' }));
        }
    }, []);

    // =========================================================================
    // ⚔️ NUEVAS EMISIONES DE EVENTOS DE COMBATE HACIA NODE
    // =========================================================================

    // Solicita a Node instanciar una nueva batalla privada usando los datos del spawn
    const startPrivateBattle = useCallback((pokemonId: number, level: number, routeName: string) => {
        if (socketRef.current?.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify({
                type: 'START_PRIVATE_BATTLE',
                userId,
                pokemonId,
                level,
                routeName
            }));
        }
    }, [userId]);

    // Reemplaza únicamente tu función sendBattleAttack dentro de usePokemonSocket.ts:
    const sendBattleAttack = useCallback((moveIndex: number) => {
        if (socketRef.current?.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify({
                type: 'BATTLE_ATTACK',
                moveIndex // Enviamos la ranura del ataque pulsado (0, 1, 2 o 3)
            }));
        }
    }, []);

    // 🔴 NUEVO EMISOR: Envía la Pokéball seleccionada al backend para procesar la captura
    const sendThrowBall = useCallback((itemId: number) => {
        if (socketRef.current?.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify({
                type: 'THROW_POKEBALL',
                itemId
            }));
        }
    }, []);

    // 🚨 NUEVO EMISOR: Envía el paquete de fuga o cierre definitivo al socket de Node
    const sendLeaveBattle = useCallback(() => {
        if (socketRef.current?.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify({ type: 'CLOSE_BATTLE' }));
        }
    }, []);

    return {
        pokemonList,
        movePokemon,
        wildEncounter,
        setWildEncounter,
        sendRoomEntry,
        sendStep,
        // Exportamos las variables y controladores del motor de lucha
        battleState,
        setBattleState,
        startPrivateBattle,
        sendBattleAttack,
        sendThrowBall, // <--- 🎒 EXPORTADO OFICIALMENTE PARA LA INTERFAZ
        sendLeaveBattle
    };
};