import { FC, useEffect, useRef, useState, MouseEvent } from 'react';
import { usePokemonSocket } from '../hooks/usePokemonSocket';
import { GetRoomSession, GetOwnRoomObject, GetSessionDataManager } from '../api'; // 🔌 Importada la sesión nativa de Nitro

const POKEMON_DICTIONARY: Record<number, string> = {
    1: 'Bulbasaur',
    4: 'Charmander',
    7: 'Squirtle',
    25: 'Pikachu',
};

const TYPE_COLORS: Record<string, string> = {
    'Planta': '#2ecc71',
    'Veneno': '#9b59b6',
    'Fuego': '#e74c3c',
    'Agua': '#3498db',
    'Eléctrico': '#f1c40f',
    'Normal': '#95a5a6',
};

export const PokemonEncounterManager: FC = () => {
    const realUserId = GetSessionDataManager().userId;
    const {
        wildEncounter, setWildEncounter,
        sendRoomEntry, sendStep,
        battleState, setBattleState,
        startPrivateBattle, sendBattleAttack,
        sendThrowBall, // 🚀 Conectado nativamente para las capturas inline
        sendUseHealingItem, // 💊 Conectado nativamente para las curaciones inline
        sendLeaveBattle,
        inventoryList, // 🎒 Cargamos tu inventario real completo
        pokemonList    // 🟢 Cargamos tu equipo real activo
    } = usePokemonSocket(realUserId);

    const lastRoomIdRef = useRef<number | null>(null);
    const lastCoordsRef = useRef<{ x: number; y: number } | null>(null);
    const wildEncounterRef = useRef<any>(null);

    // 🚨 CONTROL DE MENÚS INTERNOS
    const [showMoveMenu, setShowMoveMenu] = useState<boolean>(false);
    const [showBagMenu, setShowBagMenu] = useState<boolean>(false);
    const [selectedBattleItem, setSelectedBattleItem] = useState<any>(null);
    const [isHealingTargetMode, setIsHealingTargetMode] = useState<boolean>(false);

    const [windowPosition, setWindowPosition] = useState({ x: 0, y: 0 });
    const [isDragging, setIsDragging] = useState(false);
    const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });

    // 🚨 REPARACIÓN: Cruza tu equipo real activo filtrado del PC (slots 1-6) con el HP actual de la arena viva
    const activeTeam = pokemonList
        .filter(p => p.slot >= 1 && p.slot <= 6)
        .map(p => {
            if (battleState?.player && p.id === battleState.player.id) {
                return {
                    ...p,
                    hp: battleState.player.hp,
                    maxHp: battleState.player.maxHp
                };
            }
            return p;
        });

    const handleExitBattle = () => {
        sendLeaveBattle();
        setBattleState(null);
    };

    useEffect(() => {
        wildEncounterRef.current = wildEncounter || battleState;

        if (wildEncounter) {
            setWindowPosition({
                x: Math.max(10, window.innerWidth / 2 - 240),
                y: Math.max(10, window.innerHeight * 0.15)
            });
        }

        if (!battleState) {
            setShowMoveMenu(false);
            setShowBagMenu(false);
            setSelectedBattleItem(null);
            setIsHealingTargetMode(false);
        }
    }, [wildEncounter, battleState]);

    const getPokemonName = (id: number) => POKEMON_DICTIONARY[id] || `Pokémon #${id}`;
    const getPokemonSprite = (id: number) => {
        const paddedId = String(id).padStart(3, '0');
        return `/swf/dcr/hof_furni/icons/pokeweebz${paddedId}_icon.png`;
    };

    const getHpBarColor = (current: number, max: number) => {
        const pct = (current / max) * 100;
        if (pct > 50) return '#2ecc71';
        if (pct > 20) return '#f1c40f';
        return '#e74c3c';
    };

    // =========================================================================
    // 🚪 TRACERS Y SENSORS DE PASOS NATIVOS DE HABBO (INALTERADOS)
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
        sendBattleAttack(index);
        setShowMoveMenu(false);
    };

    // ⚔️ CONTROL INTERNO DE OBJETOS SEGMENTADO
    const handleSelectBagItem = (item: any) => {
        setSelectedBattleItem(item);
        // Validamos de forma robustecida y case-insensitive con el tipo de la BD
        if (item.type?.toUpperCase() === 'BALL') {
            setIsHealingTargetMode(false); // Es captura, no pide diana de equipo
        } else {
            setIsHealingTargetMode(true);  // Es medicina, pide diana
        }
    };

    const handleExecuteItemAction = (targetId?: number) => {
        if (!selectedBattleItem) return;

        if (selectedBattleItem.type?.toUpperCase() === 'BALL') {
            if (sendThrowBall) sendThrowBall(selectedBattleItem.id);
        } else {
            if (sendUseHealingItem && targetId) sendUseHealingItem(selectedBattleItem.id, targetId);
        }

        setSelectedBattleItem(null);
        setIsHealingTargetMode(false);
        setShowBagMenu(false);
    };

    // =========================================================================
    // 🎨 RENDER 1: COMBATE ACTIVO CON ESTÉTICA HABBO NITRO PREMIUM
    // =========================================================================
    if (battleState) {
        const p = battleState.player;
        const r = battleState.rival;

        return (
            <div
                style={{
                    ...styles.nitroWindow,
                    left: `${windowPosition.x}px`,
                    top: `${windowPosition.y}px`,
                    cursor: isDragging ? 'grabbing' : 'default'
                }}
            >
                {/* Cabecera Oficial Nitro Arrastrable */}
                <div onMouseDown={startDragging} style={styles.nitroHeader}>
                    <span>{r.isShiny === 1 ? '✨ ' : '🏟️ '} Arena de Combate - {r.routeName}</span>
                    <span style={{ fontSize: '10px', color: '#bdc3c7' }}>Turno: {battleState.turn === 'player' ? 'Tuyo' : 'Rival'}</span>
                </div>

                <div style={styles.nitroBody}>

                    {/* 🔴 PANEL SUPERIOR: POKÉMON RIVAL */}
                    <div style={styles.combatRow}>
                        <div style={styles.metadataCard}>
                            <div style={styles.metaHeaderRow}>
                                <span style={styles.pokeNameText}>
                                    🔴 {r.name || getPokemonName(r.pokemonId)}
                                    {r.gender === 0 && <span style={{ color: '#3498db', marginLeft: '4px' }}>♂</span>}
                                    {r.gender === 1 && <span style={{ color: '#e74c3c', marginLeft: '4px' }}>♀</span>}
                                </span>
                                <div style={styles.badgeGroup}>
                                    <span style={{ ...styles.typeBadge, backgroundColor: TYPE_COLORS[r.type1] || '#7f8c8d' }}>{r.type1}</span>
                                    {r.type2 && <span style={{ ...styles.typeBadge, backgroundColor: TYPE_COLORS[r.type2] || '#7f8c8d' }}>{r.type2}</span>}
                                </div>
                                <span style={styles.levelText}>Nv.{r.level}</span>
                            </div>
                            <div style={styles.hpBarContainer}>
                                <div style={{ ...styles.hpBarFill, width: `${(r.hp / r.maxHp) * 100}%`, backgroundColor: getHpBarColor(r.hp, r.maxHp) }} />
                            </div>
                            <div style={styles.hpNumericText}>HP: {r.hp}/{r.maxHp}</div>
                        </div>
                        <img src={getPokemonSprite(r.pokemonId)} alt="rival" style={styles.combatSprite} />
                    </div>

                    {/* 🟢 PANEL CENTRAL: TU POKÉMON ACTIVO */}
                    <div style={{ ...styles.combatRow, flexDirection: 'row-reverse' }}>
                        <div style={styles.metadataCard}>
                            <div style={styles.metaHeaderRow}>
                                <span style={styles.pokeNameText}>
                                    🟢 {p.name || getPokemonName(p.pokemonId)}
                                    {p.gender === 0 && <span style={{ color: '#3498db', marginLeft: '4px' }}>♂</span>}
                                    {p.gender === 1 && <span style={{ color: '#e74c3c', marginLeft: '4px' }}>♀</span>}
                                </span>
                                <div style={styles.badgeGroup}>
                                    <span style={{ ...styles.typeBadge, backgroundColor: TYPE_COLORS[p.type1] || '#7f8c8d' }}>{p.type1}</span>
                                    {p.type2 && <span style={{ ...styles.typeBadge, backgroundColor: TYPE_COLORS[p.type2] || '#7f8c8d' }}>{p.type2}</span>}
                                </div>
                                <span style={styles.levelText}>Nv.{p.level}</span>
                            </div>
                            <div style={styles.hpBarContainer}>
                                <div style={{ ...styles.hpBarFill, width: `${(p.hp / p.maxHp) * 100}%`, backgroundColor: getHpBarColor(p.hp, p.maxHp) }} />
                            </div>
                            <div style={styles.hpNumericText}>HP: {p.hp}/{p.maxHp}</div>
                        </div>
                        <img src={getPokemonSprite(p.pokemonId)} alt="player" style={styles.combatSprite} />
                    </div>

                    {/* 🎒 CONTROLADOR INTEGRADO: INTERFAZ DE ACCIONES */}
                    <div style={styles.actionFooterArea}>
                        {battleState.ended ? (
                            <div style={styles.embeddedFullRow}>
                                <div style={styles.logTerminalBox}>{battleState.log}</div>
                                <button onClick={handleExitBattle} style={{ ...styles.nitroButton, backgroundColor: '#3498db', color: '#fff', height: '100%', minWidth: '100px' }}>🚪 CERRAR</button>
                            </div>
                        ) : showMoveMenu ? (
                            <div style={styles.embeddedFullRow}>
                                <div style={styles.gridMovesContainer}>
                                    {p.moves && p.moves.map((move: any, index: number) => (
                                        <button
                                            key={index}
                                            disabled={battleState.turn !== 'player' || move.pp <= 0}
                                            onClick={() => handleAttackClick(index)}
                                            style={styles.btnMoveCard}
                                        >
                                            <span style={{ fontWeight: 'bold' }}>{move.name}</span>
                                            <span style={{ color: '#7f8c8d', fontSize: '9px' }}>PP {move.pp}/{move.maxPp}</span>
                                        </button>
                                    ))}
                                </div>
                                <button onClick={() => setShowMoveMenu(false)} style={{ ...styles.nitroButton, height: '100%' }}>↩️ VOLVER</button>
                            </div>
                        ) : showBagMenu ? (
                            /* 💼 MOCHILA INTEGRADA: Mapea tu inventario real de forma inline */
                            <div style={styles.embeddedFullRow}>
                                <div style={styles.inlineBagGrid}>
                                    {inventoryList
                                        .filter((item: any) => item.quantity > 0)
                                        .map((item: any) => (
                                            <button
                                                key={item.id}
                                                disabled={battleState.turn !== 'player'}
                                                onClick={() => handleSelectBagItem(item)}
                                                style={{
                                                    ...styles.inlineItemCard,
                                                    border: selectedBattleItem?.id === item.id ? '2px solid #e74c3c' : '1px solid #ccc'
                                                }}
                                            >
                                                <span style={{ fontSize: '10px', fontWeight: 'bold' }}>
                                                    {item.type?.toUpperCase() === 'BALL' ? '🔴' : '💊'} {item.name}
                                                </span>
                                                <span style={{ fontSize: '9px', color: '#7f8c8d' }}>x{item.quantity}</span>
                                            </button>
                                        ))}
                                    {inventoryList.filter((item: any) => item.quantity > 0).length === 0 && (
                                        <div style={styles.emptyTextItalic}>Mochila de combate vacía.</div>
                                    )}
                                </div>

                                {/* Panel Dinámico de Aplicación de Objetos */}
                                <div style={styles.inlineDetailPanel}>
                                    {selectedBattleItem ? (
                                        isHealingTargetMode ? (
                                            <div style={styles.miniTargetSelector}>
                                                <span style={{ fontSize: '9px', fontWeight: 'bold', color: '#e74c3c', textAlign: 'center' }}>¿A quién curar?</span>
                                                <div style={styles.miniTargetScroll}>
                                                    {activeTeam.map((teamPkmn: any) => (
                                                        <button
                                                            key={teamPkmn.id}
                                                            onClick={() => handleExecuteItemAction(teamPkmn.id)}
                                                            style={styles.btnMiniTargetRow}
                                                        >
                                                            <span>Slot {teamPkmn.slot}: <b>{teamPkmn.name || getPokemonName(teamPkmn.pokemonId)}</b></span>
                                                            <span style={{ color: '#2ecc71', fontWeight: 'bold' }}>{teamPkmn.hp}/{teamPkmn.maxHp || teamPkmn.max_hp || 100}</span>
                                                        </button>
                                                    ))}
                                                </div>
                                            </div>
                                        ) : (
                                            <button
                                                onClick={() => handleExecuteItemAction()}
                                                style={{ ...styles.nitroButton, backgroundColor: '#e67e22', color: '#fff', fontSize: '10px', width: '100%', margin: 'auto 0' }}
                                            >
                                                🚀 Lanzar Ball
                                            </button>
                                        )
                                    ) : (
                                        <span style={{ color: '#7f8c8d', fontSize: '9px', textAlign: 'center', margin: 'auto' }}>Elige un objeto</span>
                                    )}
                                </div>
                                <button onClick={() => { setShowBagMenu(false); setSelectedBattleItem(null); setIsHealingTargetMode(false); }} style={{ ...styles.nitroButton, height: '100%' }}>↩️ VOLVER</button>
                            </div>
                        ) : (
                            /* 🏟️ INTERFAZ PRINCIPAL DE ACCIONES ORIGINAL (TEXTO LIMPIO EN HUIR) */
                            <div style={styles.embeddedFullRow}>
                                <div style={styles.logTerminalBox}>{battleState.log}</div>
                                <div style={styles.verticalActionButtons}>
                                    <button onClick={() => setShowMoveMenu(true)} disabled={battleState.turn !== 'player'} style={{ ...styles.nitroButton, backgroundColor: '#e74c3c', color: '#fff' }}>⚔️ ATACAR</button>
                                    <button onClick={() => setShowBagMenu(true)} disabled={battleState.turn !== 'player'} style={{ ...styles.nitroButton, backgroundColor: '#f1c40f', color: '#333' }}>🎒 MOCHILA</button>
                                    <button onClick={handleExitBattle} style={styles.nitroButton}>HUIR</button>
                                </div>
                            </div>
                        )}
                    </div>

                </div>
            </div>
        );
    }

    if (!wildEncounter) return null;

    // =========================================================================
    // 🎨 RENDER 2: ALERT BOX DE SPAWN SALVAJE (TEXTO LIMPIO EN HUIR)
    // =========================================================================
    return (
        <div
            style={{
                ...styles.nitroWindow,
                width: '280px',
                left: `${windowPosition.x}px`,
                top: `${windowPosition.y}px`
            }}
        >
            <div onMouseDown={startDragging} style={{ ...styles.nitroHeader, backgroundColor: wildEncounter.isShiny === 1 ? '#d4af37' : '#34495e' }}>
                <span>{wildEncounter.isShiny === 1 ? '✨ ¡VARIOPINTO APARECIÓ!' : '💥 ¡AVISTAMIENTO POKÉMON!'}</span>
            </div>
            <div style={{ ...styles.nitroBody, padding: '12px', alignItems: 'center', gap: '8px' }}>
                <span style={{ fontSize: '10px', color: '#7f8c8d' }}>{wildEncounter.routeName}</span>
                <img src={getPokemonSprite(wildEncounter.pokemonId)} alt="wild" style={{ width: '56px', height: '56px', objectFit: 'contain', imageRendering: 'pixelated' }} />

                <h5 style={{ fontSize: '14px', fontWeight: 'bold', color: '#333', margin: 0, display: 'flex', alignItems: 'center', gap: '4px' }}>
                    {wildEncounter.name || getPokemonName(wildEncounter.pokemonId)}
                    {wildEncounter.gender === 0 && <span style={{ color: '#3498db' }}>♂</span>}
                    {wildEncounter.gender === 1 && <span style={{ color: '#e74c3c' }}>♀</span>}
                </h5>

                <div style={{ display: 'flex', gap: '4px' }}>
                    <span style={{ ...styles.typeBadge, backgroundColor: TYPE_COLORS[wildEncounter.type1] || '#7f8c8d' }}>{wildEncounter.type1}</span>
                    {wildEncounter.type2 && <span style={{ ...styles.typeBadge, backgroundColor: TYPE_COLORS[wildEncounter.type2] || '#7f8c8d' }}>{wildEncounter.type2}</span>}
                </div>
                <span style={{ fontSize: '11px', fontWeight: 'bold', color: '#e67e22' }}>Nivel {wildEncounter.level}</span>

                <div style={{ display: 'flex', gap: '6px', width: '100%', marginTop: '6px' }}>
                    <button onClick={() => { alert('Activando framework en sala...'); setWildEncounter(null); }} style={{ ...styles.nitroButton, flex: 1, backgroundColor: '#2ecc71', color: '#fff' }}>En Sala</button>
                    <button onClick={() => { startPrivateBattle(wildEncounter.pokemonId, wildEncounter.level, wildEncounter.routeName); setWildEncounter(null); }} style={{ ...styles.nitroButton, flex: 1, backgroundColor: '#3498db', color: '#fff' }}>En Interfaz</button>
                </div>
                <button onClick={() => setWildEncounter(null)} style={{ ...styles.nitroButton, width: '100%', marginTop: '2px' }}>HUIR</button>
            </div>
        </div>
    );
};

// =========================================================================
// 🎨 HOJA DE ESTILOS INLINE ADAPTADA AL DISEÑO DE INTERFAZ HABBO/NITRO
// =========================================================================
const styles = {
    nitroWindow: {
        position: 'fixed' as const,
        backgroundColor: '#f1f1f1',
        borderRadius: '6px',
        border: '2px solid #4a4a4a',
        width: '500px',
        boxShadow: '0 4px 20px rgba(0,0,0,0.4)',
        fontFamily: 'Verdana, Arial, sans-serif',
        zIndex: 99999,
        display: 'flex',
        flexDirection: 'column' as const,
        userSelect: 'none' as const
    },
    nitroHeader: {
        backgroundColor: '#34495e',
        color: 'white',
        padding: '8px 12px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        fontWeight: 'bold' as const,
        fontSize: '12px',
        borderBottom: '1px solid #2c3e50',
        cursor: 'grab'
    },
    nitroBody: {
        padding: '12px',
        display: 'flex',
        flexDirection: 'column' as const,
        gap: '10px'
    },
    combatRow: {
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        borderRadius: '4px',
        padding: '8px 12px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '10px'
    },
    combatSprite: {
        width: '52px',
        height: '52px',
        objectFit: 'contain' as const,
        imageRendering: 'pixelated' as const
    },
    metadataCard: {
        flex: 1,
        display: 'flex',
        flexDirection: 'column' as const,
        gap: '3px'
    },
    metaHeaderRow: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        fontSize: '11px'
    },
    pokeNameText: {
        fontWeight: 'bold' as const,
        color: '#333'
    },
    levelText: {
        fontWeight: 'bold' as const,
        color: '#e67e22'
    },
    badgeGroup: {
        display: 'flex',
        gap: '3px'
    },
    typeBadge: {
        color: '#fff',
        fontSize: '7px',
        fontWeight: 'bold' as const,
        padding: '1px 4px',
        borderRadius: '2px',
        textTransform: 'uppercase' as const
    },
    hpBarContainer: {
        backgroundColor: '#e2e2e2',
        border: '1px solid #000',
        height: '8px',
        borderRadius: '1px',
        overflow: 'hidden' as const,
        marginTop: '2px'
    },
    hpBarFill: {
        height: '100%',
        transition: 'width 0.3s ease'
    },
    hpNumericText: {
        fontSize: '9px',
        textAlign: 'right' as const,
        color: '#555',
        fontWeight: 'bold' as const
    },
    actionFooterArea: {
        borderTop: '1px dashed #ccc',
        paddingTop: '10px',
        height: '105px',
        display: 'flex'
    },
    embeddedFullRow: {
        display: 'flex',
        width: '100%',
        gap: '8px',
        height: '100%'
    },
    logTerminalBox: {
        flex: 1,
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        borderRadius: '4px',
        padding: '6px',
        fontSize: '10px',
        fontFamily: 'monospace',
        color: '#27ae60',
        overflowY: 'auto' as const,
        whiteSpace: 'pre-line' as const
    },
    verticalActionButtons: {
        width: '32%',
        display: 'flex',
        flexDirection: 'column' as const,
        gap: '4px'
    },
    nitroButton: {
        backgroundColor: '#e2e2e2',
        border: '1px solid #7f8c8d',
        borderRadius: '4px',
        fontSize: '10px',
        fontWeight: 'bold' as const,
        cursor: 'pointer',
        padding: '5px 10px',
        textAlign: 'center' as const,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        boxShadow: 'inset 0 1px 0 rgba(255,255,255,0.2)'
    },
    gridMovesContainer: {
        flex: 1,
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        gap: '5px'
    },
    btnMoveCard: {
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        borderRadius: '4px',
        display: 'flex',
        flexDirection: 'column' as const,
        justifyContent: 'center',
        padding: '4px 8px',
        textAlign: 'left' as const,
        cursor: 'pointer',
        fontSize: '11px'
    },
    inlineBagGrid: {
        width: '55%',
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        borderRadius: '4px',
        padding: '4px',
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        gap: '4px',
        overflowY: 'auto' as const
    },
    inlineItemCard: {
        backgroundColor: '#f9f9f9',
        borderRadius: '3px',
        padding: '4px 6px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        cursor: 'pointer'
    },
    inlineDetailPanel: {
        width: '25%',
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        borderRadius: '4px',
        padding: '4px',
        display: 'flex',
        flexDirection: 'column' as const,
        justifyContent: 'center'
    },
    miniTargetSelector: {
        display: 'flex',
        flexDirection: 'column' as const,
        height: '100%',
        gap: '2px'
    },
    miniTargetScroll: {
        flex: 1,
        overflowY: 'auto' as const,
        display: 'flex',
        flexDirection: 'column' as const,
        gap: '2px'
    },
    btnMiniTargetRow: {
        border: '1px solid #eee',
        backgroundColor: '#fdfdfd',
        fontSize: '8px',
        padding: '2px 4px',
        display: 'flex',
        justifyContent: 'space-between',
        cursor: 'pointer',
        borderRadius: '2px',
        alignItems: 'center'
    },
    emptyTextItalic: {
        gridColumn: 'span 2',
        fontSize: '10px',
        color: '#999',
        fontStyle: 'italic',
        margin: 'auto'
    }
};