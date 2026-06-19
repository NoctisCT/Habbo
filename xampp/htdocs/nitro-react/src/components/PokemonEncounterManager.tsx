import { FC, useEffect, useRef, useState, MouseEvent } from 'react';
import { usePokemonSocket } from '../hooks/usePokemonSocket';
import { GetRoomSession, GetOwnRoomObject, GetSessionDataManager } from '../api'; // 🔌 Importada la sesión nativa de Nitro

const POKEMON_DICTIONARY: Record<number, string> = {
    1: 'Bulbasaur',
    4: 'Charmander',
    7: 'Squirtle',
    25: 'Pikachu',
};

// 🎨 Paleta de colores oficial para los Badges de Tipo en Interfaz
const TYPE_COLORS: Record<string, string> = {
    'Planta': '#22c55e', // Verde
    'Veneno': '#a855f7', // Morado
    'Fuego': '#ef4444', // Rojo
    'Agua': '#3b82f6', // Azul
    'Eléctrico': '#eab308', // Amarillo
    'Normal': '#78716c', // Gris
};

export const PokemonEncounterManager: FC = () => {
    // 👤 Sincronización dinámica del ID real del personaje conectado
    const realUserId = GetSessionDataManager().userId;
    const {
        wildEncounter, setWildEncounter,
        sendRoomEntry, sendStep,
        battleState, setBattleState,
        startPrivateBattle, sendBattleAttack,
        sendThrowBall, // 🎒 Importamos la acción de captura desde el hook
        sendLeaveBattle
    } = usePokemonSocket(realUserId);

    const lastRoomIdRef = useRef<number | null>(null);
    const lastCoordsRef = useRef<{ x: number; y: number } | null>(null);

    // 🚨 REFERENCIA DE CONGELACIÓN: Se congela si hay alerta O si ya estamos combatiendo
    const wildEncounterRef = useRef<any>(null);

    // 🚨 CONTROL DE MENÚS: Cambia entre las vistas del panel de combate
    const [showMoveMenu, setShowMoveMenu] = useState<boolean>(false);
    const [showBagMenu, setShowBagMenu] = useState<boolean>(false); // 🎒 Control del submenú de inventario

    // 🗺️ ESTADOS DE DESPLAZAMIENTO: Para poder arrastrar la alerta libremente
    const [windowPosition, setWindowPosition] = useState({ x: 0, y: 0 });
    const [isDragging, setIsDragging] = useState(false);
    const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });

    // 🔍 FUNCIÓN MODULAR DE SALIDA: Sincroniza y libera el hilo de pasos en cliente y servidor a la vez
    const handleExitBattle = () => {
        sendLeaveBattle(); // Libera la propiedad ws.battle en el Node
        setBattleState(null); // Desmonta el overlay de combate en React
    };

    // Sincronizamos la referencia y centramos la ventana cada vez que aparezca un NUEVO bicho
    useEffect(() => {
        wildEncounterRef.current = wildEncounter || battleState;

        if (wildEncounter) {
            // Inicializa centrado en la pantalla del usuario automáticamente
            setWindowPosition({
                x: Math.max(10, window.innerWidth / 2 - 140),
                y: Math.max(10, window.innerHeight * 0.25)
            });
        }

        if (!battleState) {
            setShowMoveMenu(false);
            setShowBagMenu(false); // Reseteamos la mochila al limpiar combate
        }
    }, [wildEncounter, battleState]);

    const getPokemonName = (id: number) => POKEMON_DICTIONARY[id] || `Pokémon #${id}`;
    const getPokemonSprite = (id: number) => {
        const paddedId = String(id).padStart(3, '0');
        return `/swf/dcr/hof_furni/icons/pokeweebz${paddedId}_icon.png`;
    };

    // Factoría de color dinámica para las barras de salud (HP Progress Bars)
    const getHpBarColor = (current: number, max: number) => {
        const pct = (current / max) * 100;
        if (pct > 50) return '#22c55e'; // Verde
        if (pct > 20) return '#eab308'; // Amarillo
        return '#ef4444'; // Rojo
    };

    // =========================================================================
    // 🚪 1. CONTROL DE ENTRADA A SALA
    // =========================================================================
    useEffect(() => {
        const roomCheck = setInterval(() => {
            try {
                const session = GetRoomSession();
                if (!session || typeof session.roomId !== 'number') return;

                if (lastRoomIdRef.current !== session.roomId) {
                    const exito = sendRoomEntry(session.roomId);
                    if (exito) {
                        lastRoomIdRef.current = session.roomId;
                        lastCoordsRef.current = null;
                    }
                }
            } catch (e) { }
        }, 1000);

        return () => clearInterval(roomCheck);
    }, [sendRoomEntry]);

    // =========================================================================
    // 👣 2. RADAR DE PASOS NATIVO (Con sistema de bloqueo reactivo)
    // =========================================================================
    useEffect(() => {
        const trackerLoop = setInterval(() => {
            try {
                if (wildEncounterRef.current) return;

                const session = GetRoomSession();
                if (!session || typeof session.roomId !== 'number') return;

                const roomObject = GetOwnRoomObject();
                if (roomObject) {
                    let currentX: number | null = null;
                    let currentY: number | null = null;

                    if (typeof roomObject.getLocation === 'function') {
                        const location = roomObject.getLocation();
                        if (location) {
                            currentX = location.x;
                            currentY = location.y;
                        }
                    } else if ((roomObject as any).location) {
                        currentX = (roomObject as any).location.x;
                        currentY = (roomObject as any).location.y;
                    }

                    if (typeof currentX === 'number' && typeof currentY === 'number') {
                        if (!lastCoordsRef.current || lastCoordsRef.current.x !== currentX || lastCoordsRef.current.y !== currentY) {
                            lastCoordsRef.current = { x: currentX, y: currentY };
                            sendStep();
                        }
                    }
                }
            } catch (err) { }
        }, 150);

        return () => clearInterval(trackerLoop);
    }, [sendStep]);

    // =========================================================================
    // 🖱️ LÓGICA DE ARRASTRE MOUSE
    // =========================================================================
    const startDragging = (e: MouseEvent) => {
        setIsDragging(true);
        setDragOffset({ x: e.clientX - windowPosition.x, y: e.clientY - windowPosition.y });
    };

    useEffect(() => {
        const handleMouseMove = (e: globalThis.MouseEvent) => {
            if (!isDragging) return;
            setWindowPosition({ x: e.clientX - dragOffset.x, y: e.clientY - dragOffset.y });
        };
        const handleMouseUp = () => setIsDragging(false);

        if (isDragging) {
            window.addEventListener('mousemove', handleMouseMove);
            window.addEventListener('mouseup', handleMouseUp);
        }
        return () => {
            window.removeEventListener('mousemove', handleMouseMove);
            window.removeEventListener('mouseup', handleMouseUp);
        };
    }, [isDragging, dragOffset]);

    const handleAttackClick = (index: number) => {
        console.log(`[UI COMBATE] Pulsado ataque en índice numérico: ${index}`);
        sendBattleAttack(index);
        setShowMoveMenu(false);
    };

    // 🔴 Lanzador interactivo de esferas de captura conectado al WebSocket
    const handleUseBallClick = (itemId: number, itemName: string) => {
        console.log(`[UI COMBATE] Ejecutando lanzamiento de Objeto ID: ${itemId} (${itemName})`);
        sendThrowBall(itemId); // 🚀 Dispara el evento real hacia el case 'THROW_POKEBALL' de Node
        setShowBagMenu(false);
    };

    // =========================================================================
    // 🎨 RENDER 1: PANTALLA PRINCIPAL DE COMBATE ACTIVO (MODO INTERFAZ)
    // =========================================================================
    if (battleState) {
        const p = battleState.player;
        const r = battleState.rival;
        const inventory = battleState.inventory || [];

        return (
            <div
                className="position-fixed card p-3"
                style={{
                    zIndex: 999999, width: '480px', height: '360px',
                    left: 'calc(50% - 240px)', top: '20%',
                    backgroundColor: '#111827', borderColor: '#374151',
                    boxShadow: '0 0 40px rgba(0, 0, 0, 0.85)',
                    color: '#ffffff', fontFamily: 'monospace', userSelect: 'none'
                }}
            >
                {/* Cabecera del Estadio */}
                <div className="text-center fw-bold text-info border-bottom pb-1 mb-3" style={{ fontSize: '11px' }}>
                    {r.isShiny === 1 ? '✨ ' : '🏟️ '} ARENA DE COMBATE PRIVADA ({r.routeName})
                </div>

                {/* FILA SUPERIOR: DATOS DEL POKÉMON RIVAL */}
                <div className="d-flex justify-content-between align-items-center bg-dark p-2 rounded mb-3 border border-secondary">
                    <div style={{ width: '65%' }}>
                        <div className="d-flex justify-content-between align-items-center fw-bold text-warning" style={{ fontSize: '12px' }}>
                            {/* Nombre + Género Rival */}
                            <span className="d-flex align-items-center gap-1">
                                🔴 {r.name || getPokemonName(r.pokemonId)}
                                {r.gender === 0 && <span style={{ color: '#38bdf8' }}>♂</span>}
                                {r.gender === 1 && <span style={{ color: '#f472b6' }}>♀</span>}
                            </span>
                            {/* Badges de Tipo Rival */}
                            <div className="d-flex gap-1 align-items-center mx-2">
                                <span style={{ backgroundColor: TYPE_COLORS[r.type1] || '#4b5563', padding: '0px 4px', borderRadius: '2px', color: '#fff', fontSize: '7px', fontWeight: 'bold' }}>{r.type1}</span>
                                {r.type2 && (
                                    <span style={{ backgroundColor: TYPE_COLORS[r.type2] || '#4b5563', padding: '0px 4px', borderRadius: '2px', color: '#fff', fontSize: '7px', fontWeight: 'bold' }}>{r.type2}</span>
                                )}
                            </div>
                            <span>Nv.{r.level}</span>
                        </div>
                        <div className="progress mt-1" style={{ height: '8px', backgroundColor: '#1f2937' }}>
                            <div
                                className="progress-bar"
                                style={{
                                    width: `${(r.hp / r.maxHp) * 100}%`,
                                    backgroundColor: getHpBarColor(r.hp, r.maxHp),
                                    transition: 'width 0.3s ease'
                                }}
                            />
                        </div>
                        <div className="text-muted text-end" style={{ fontSize: '9px' }}>HP: {r.hp}/{r.maxHp}</div>
                    </div>
                    <img src={getPokemonSprite(r.pokemonId)} alt="rival-sprite" style={{ width: '48px', height: '48px', objectFit: 'contain' }} />
                </div>

                {/* FILA CENTRAL: DATOS DE TU POKÉMON ACTIVO (SLOT 1) */}
                <div className="d-flex justify-content-between align-items-center bg-dark p-2 rounded mb-3 border border-info" style={{ marginLeft: '40px' }}>
                    <img src={getPokemonSprite(p.pokemonId)} alt="player-sprite" style={{ width: '48px', height: '48px', objectFit: 'contain' }} />
                    <div style={{ width: '65%' }}>
                        <div className="d-flex justify-content-between align-items-center fw-bold text-success" style={{ fontSize: '12px' }}>
                            {/* Nombre + Género Jugador */}
                            <span className="d-flex align-items-center gap-1">
                                🟢 {p.name || getPokemonName(p.pokemonId)}
                                {p.gender === 0 && <span style={{ color: '#38bdf8' }}>♂</span>}
                                {p.gender === 1 && <span style={{ color: '#f472b6' }}>♀</span>}
                            </span>
                            {/* Badges de Tipo Jugador */}
                            <div className="d-flex gap-1 align-items-center mx-2">
                                <span style={{ backgroundColor: TYPE_COLORS[p.type1] || '#4b5563', padding: '0px 4px', borderRadius: '2px', color: '#fff', fontSize: '7px', fontWeight: 'bold' }}>{p.type1}</span>
                                {p.type2 && (
                                    <span style={{ backgroundColor: TYPE_COLORS[p.type2] || '#4b5563', padding: '0px 4px', borderRadius: '2px', color: '#fff', fontSize: '7px', fontWeight: 'bold' }}>{p.type2}</span>
                                )}
                            </div>
                            <span>Nv.{p.level}</span>
                        </div>
                        <div className="progress mt-1" style={{ height: '8px', backgroundColor: '#1f2937' }}>
                            <div
                                className="progress-bar"
                                style={{
                                    width: `${(p.hp / p.maxHp) * 100}%`,
                                    backgroundColor: getHpBarColor(p.hp, p.maxHp),
                                    transition: 'width 0.3s ease'
                                }}
                            />
                        </div>
                        <div className="text-white text-end fw-bold" style={{ fontSize: '9px' }}>HP: {p.hp}/{p.maxHp}</div>
                    </div>
                </div>

                {/* PIE DE ARENA SINCRETISTA */}
                <div className="mt-auto border-top pt-2" style={{ height: '85px' }}>
                    {battleState.ended ? (
                        <div className="d-flex gap-2 h-100 align-items-center">
                            <div className="bg-black p-2 rounded text-warning small border border-secondary flex-grow-1 h-100 overflow-y-auto" style={{ fontSize: '11px', lineHeight: '14px' }}>
                                {battleState.log}
                            </div>
                            <button onClick={handleExitBattle} className="btn btn-info fw-bold h-100 px-4" style={{ fontSize: '13px', color: '#111827', minWidth: '120px' }}>
                                🚪 CERRAR
                            </button>
                        </div>
                    ) : showMoveMenu ? (
                        <div className="d-flex gap-2 h-100 align-items-center">
                            <div className="d-flex flex-wrap gap-2 flex-grow-1 h-100 align-content-center">
                                {p.moves && p.moves.map((move: any, index: number) => (
                                    <button
                                        key={index}
                                        disabled={battleState.turn !== 'player' || move.pp <= 0}
                                        onClick={() => handleAttackClick(index)}
                                        className="btn btn-sm btn-danger fw-bold text-start px-2 py-1"
                                        style={{ width: 'calc(50% - 4px)', height: '36px', border: '1px solid #ef4444', boxShadow: '0 2px 4px rgba(0,0,0,0.2)' }}
                                    >
                                        <div style={{ fontSize: '11px', lineHeight: '12px' }}>{move.name}</div>
                                        <div className="text-white-50 fw-normal" style={{ fontSize: '8.5px', marginTop: '1px' }}>PP {move.pp}/{move.maxPp}</div>
                                    </button>
                                ))}
                            </div>
                            <button onClick={() => setShowMoveMenu(false)} className="btn btn-secondary fw-bold h-100 px-3" style={{ fontSize: '11px', minWidth: '90px' }}>
                                ↩️ ATRÁS
                            </button>
                        </div>
                    ) : showBagMenu ? (
                        /* 🎒 CUADRÍCULA DE SELECCIÓN DE POKÉBALLS REALES */
                        <div className="d-flex gap-2 h-100 align-items-center">
                            <div className="d-flex flex-wrap gap-2 flex-grow-1 h-100 align-content-center">
                                {inventory.map((item: any) => (
                                    <button
                                        key={item.item_id}
                                        disabled={battleState.turn !== 'player' || item.quantity <= 0}
                                        onClick={() => handleUseBallClick(item.item_id, item.name)}
                                        className="btn btn-sm btn-warning fw-bold text-start px-2 py-1"
                                        style={{ width: 'calc(50% - 4px)', height: '36px', border: '1px solid #ca8a04', boxShadow: '0 2px 4px rgba(0,0,0,0.2)', color: '#111827' }}
                                    >
                                        <div style={{ fontSize: '11px', lineHeight: '12px' }}>🔴 {item.name}</div>
                                        <div className="text-dark-50 fw-normal" style={{ fontSize: '9px', marginTop: '1px' }}>Cantidad: {item.quantity}</div>
                                    </button>
                                ))}
                                {inventory.length === 0 && (
                                    <div className="text-muted italic m-auto" style={{ fontSize: '11px' }}>- Tu mochila está vacía -</div>
                                )}
                            </div>
                            <button onClick={() => setShowBagMenu(false)} className="btn btn-secondary fw-bold h-100 px-3" style={{ fontSize: '11px', minWidth: '90px' }}>
                                ↩️ ATRÁS
                            </button>
                        </div>
                    ) : (
                        /* 🏟️ MENÚ GENERAL DE ACCIONES DE COMBATE ORIGINAL */
                        <div className="d-flex gap-2 h-100 align-items-center">
                            <div className="bg-black p-2 rounded text-success small border border-secondary overflow-y-auto h-100" style={{ width: '65%', fontSize: '11px', lineHeight: '14px', whiteSpace: 'pre-line' }}>
                                {battleState.log}
                            </div>
                            <div className="d-flex flex-column gap-1 h-100 justify-content-center" style={{ width: '35%' }}>
                                <button
                                    onClick={() => setShowMoveMenu(true)}
                                    disabled={battleState.turn !== 'player'}
                                    className="btn btn-sm btn-danger fw-bold w-100 py-1"
                                    style={{ fontSize: '11px', height: '24px' }}
                                >
                                    ⚔️ COMBATIR
                                </button>
                                <button
                                    onClick={() => setShowBagMenu(true)}
                                    disabled={battleState.turn !== 'player'}
                                    className="btn btn-sm btn-warning fw-bold w-100 py-1"
                                    style={{ fontSize: '11px', height: '24px', color: '#111827' }}
                                >
                                    🎒 MOCHILA
                                </button>
                                <button
                                    onClick={handleExitBattle}
                                    className="btn btn-sm btn-outline-warning py-0 text-white border-warning w-100"
                                    style={{ fontSize: '10px', height: '20px' }}
                                >
                                    🏃 HUIR
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        );
    }

    if (!wildEncounter) return null;

    // =========================================================================
    // 🎨 RENDER 2: ALERT BOX DE SPAWN SALVAJE CON TIPOS Y GÉNEROS
    // =========================================================================
    return (
        <div
            className="position-fixed card p-3 text-center"
            style={{
                zIndex: 999999,
                width: '280px',
                left: `${windowPosition.x}px`,
                top: `${windowPosition.y}px`,
                backgroundColor: '#111827',
                borderColor: wildEncounter.isShiny === 1 ? '#eab308' : '#ca8a04',
                boxShadow: wildEncounter.isShiny === 1 ? '0 0 30px rgba(234, 179, 8, 0.7)' : '0 0 25px rgba(202, 138, 4, 0.5)',
                color: '#ffffff',
                fontFamily: 'monospace',
                userSelect: 'none'
            }}
        >
            <div
                onMouseDown={startDragging}
                className="fw-bold text-warning border-bottom pb-2 mb-2"
                style={{
                    fontSize: '12px',
                    cursor: isDragging ? 'grabbing' : 'grab',
                    background: 'rgba(255,255,255,0.03)',
                    borderRadius: '4px'
                }}
            >
                {wildEncounter.isShiny === 1 ? '✨ ¡VARIOPINTO SALVAJE! ☰' : '💥 ¡POKÉMON SALVAJE! ☰'}
            </div>
            <div className="text-muted small mb-1" style={{ fontSize: '10px' }}>{wildEncounter.routeName}</div>

            <img src={getPokemonSprite(wildEncounter.pokemonId)} alt="wild" className="mx-auto my-2" style={{ width: '56px', height: '56px', objectFit: 'contain' }} />

            {/* Nombre + Icono de Género Dinámico */}
            <h5 className="text-info fw-bold mb-0 d-flex align-items-center justify-content-center gap-1" style={{ fontSize: '15px' }}>
                {wildEncounter.name || getPokemonName(wildEncounter.pokemonId)}
                {wildEncounter.gender === 0 && <span style={{ color: '#38bdf8', fontWeight: 'bold' }}>♂</span>}
                {wildEncounter.gender === 1 && <span style={{ color: '#f472b6', fontWeight: 'bold' }}>♀</span>}
            </h5>

            {/* Medallas de Tipo Elementales de la Especie */}
            <div className="d-flex justify-content-center gap-1 my-2">
                <span style={{ backgroundColor: TYPE_COLORS[wildEncounter.type1] || '#4b5563', padding: '1px 6px', borderRadius: '2px', color: '#fff', fontSize: '8px', fontWeight: 'bold', textTransform: 'uppercase' }}>{wildEncounter.type1}</span>
                {wildEncounter.type2 && (
                    <span style={{ backgroundColor: TYPE_COLORS[wildEncounter.type2] || '#4b5563', padding: '1px 6px', borderRadius: '2px', color: '#fff', fontSize: '8px', fontWeight: 'bold', textTransform: 'uppercase' }}>{wildEncounter.type2}</span>
                )}
            </div>

            <p className="text-secondary small mb-3" style={{ fontSize: '11px' }}>Nivel {wildEncounter.level}</p>

            <div className="d-flex flex-column gap-2 justify-content-center">
                <div className="d-flex gap-2 justify-content-center">
                    <button
                        className="btn btn-sm btn-warning fw-bold px-2"
                        style={{ fontSize: '10.5px', color: '#111827', boxShadow: '0 0 10px rgba(234, 179, 8, 0.2)' }}
                        onClick={() => {
                            alert(`Activando framework cRPG en sala para luchar contra ${wildEncounter.name || getPokemonName(wildEncounter.pokemonId)}...`);
                            setWildEncounter(null);
                        }}
                    >
                        ⚔️ EN SALA
                    </button>

                    <button
                        className="btn btn-sm btn-success fw-bold px-2"
                        style={{ fontSize: '10.5px', boxShadow: '0 0 10px rgba(34, 197, 94, 0.2)' }}
                        onClick={() => {
                            startPrivateBattle(wildEncounter.pokemonId, wildEncounter.level, wildEncounter.routeName);
                            setWildEncounter(null);
                        }}
                    >
                        🛡️ EN INTERFAZ
                    </button>
                </div>

                <button
                    className="btn btn-sm btn-outline-danger fw-bold mx-auto mt-1 px-4"
                    style={{ fontSize: '11px', width: 'fit-content' }}
                    onClick={() => setWildEncounter(null)}
                >
                    🏃 HUIR
                </button>
            </div>
        </div>
    );
};