import { FC, useEffect, useState, MouseEvent } from 'react';
import { usePokemonSocket } from '../hooks/usePokemonSocket';
import { GetSessionDataManager } from '../api'; // 🔌 Importación de la sesión nativa de Nitro

const POKEMON_DICTIONARY: Record<number, string> = {
    1: 'Bulbasaur',
    4: 'Charmander',
    7: 'Squirtle',
    25: 'Pikachu',
};

// 🎨 Paleta de colores oficial adaptada para los Badges sobre fondo claro
const TYPE_COLORS: Record<string, string> = {
    'Planta': '#2ecc71', // Verde claro
    'Veneno': '#9b59b6', // Morado claro
    'Fuego': '#e74c3c', // Rojo claro
    'Agua': '#3498db', // Azul claro
    'Eléctrico': '#f1c40f', // Amarillo claro
    'Normal': '#95a5a6', // Gris/Piedra
};

export const PokemonPCView: FC = () => {
    // 👤 Enganche dinámico del ID de usuario real de Habbo
    const realUserId = GetSessionDataManager().userId;
    const { pokemonList, movePokemon } = usePokemonSocket(realUserId);

    const [visible, setVisible] = useState<boolean>(false);
    const [position, setPosition] = useState({ x: 120, y: 150 });
    const [isDragging, setIsDragging] = useState(false);
    const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });

    const safePokemonList = Array.isArray(pokemonList) ? pokemonList : [];
    const team = safePokemonList.filter(p => p && p.slot >= 1 && p.slot <= 6);
    const pcBox = safePokemonList.filter(p => p && p.slot === 0);

    const getPokemonName = (poke: any) => poke.name || POKEMON_DICTIONARY[poke.pokemon_id] || `Pokémon #${poke.pokemon_id}`;

    const getPokemonSprite = (id: number) => {
        const paddedId = String(id).padStart(3, '0');
        return `/swf/dcr/hof_furni/icons/pokeweebz${paddedId}_icon.png`;
    };

    const handlePokeClick = (storageId: number, currentSlot: number) => {
        if (currentSlot > 0) {
            movePokemon(storageId, 0);
        } else {
            const takenSlots = team.map(p => p.slot);
            let freeSlot = 1;
            while (takenSlots.includes(freeSlot) && freeSlot <= 6) {
                freeSlot++;
            }
            if (freeSlot <= 6) {
                movePokemon(storageId, freeSlot);
            } else {
                alert("¡Tu equipo activo ya está lleno! Máximo 6.");
            }
        }
    };

    const startDragging = (e: MouseEvent) => {
        setIsDragging(true);
        setDragOffset({ x: e.clientX - position.x, y: e.clientY - position.y });
    };

    // 🌟 ESCUCHADOR: Conecta el PC al botón "Equipo" de tu barra vertical izquierda
    useEffect(() => {
        const handleTogglePC = () => setVisible(prev => !prev);

        window.addEventListener('pokemon:open_pc', handleTogglePC);
        return () => window.removeEventListener('pokemon:open_pc', handleTogglePC);
    }, []);

    useEffect(() => {
        const handleMouseMove = (e: globalThis.MouseEvent) => {
            if (!isDragging) return;
            setPosition({ x: e.clientX - dragOffset.x, y: e.clientY - dragOffset.y });
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

    if (!visible) return null;

    return (
        <div
            style={{
                ...styles.modalWindow,
                left: `${position.x}px`,
                top: `${position.y}px`,
                cursor: isDragging ? 'grabbing' : 'default'
            }}
        >
            {/* Cabecera */}
            <div onMouseDown={startDragging} style={styles.modalHeader}>
                <span>💻 Sistema de Almacenamiento Pokémon</span>
                <button onClick={() => setVisible(false)} style={styles.closeButton}>X</button>
            </div>

            {/* Panel de Doble Columna estilo Nitro */}
            <div style={styles.modalBody}>

                {/* 🎒 COLUMNA IZQUIERDA: EQUIPO EN MANO */}
                <div style={styles.leftColumn}>
                    <div style={styles.columnTitle}>EQUIPO EN MANO</div>
                    <div style={styles.teamListContainer}>
                        {Array.from({ length: 6 }).map((_, index) => {
                            const currentSlot = index + 1;
                            const poke = team.find(p => p.slot === currentSlot);
                            return (
                                <div
                                    key={currentSlot}
                                    onClick={() => poke && handlePokeClick(poke.id, poke.slot)}
                                    style={{
                                        ...styles.pokemonRow,
                                        backgroundColor: poke ? '#fff' : '#e2e2e2',
                                        border: poke ? '1px solid #ccc' : '1px dashed #b2b2b2',
                                        cursor: poke ? 'pointer' : 'default',
                                        opacity: poke ? 1 : 0.7
                                    }}
                                >
                                    {poke ? (
                                        <>
                                            <div style={styles.rowInfoArea}>
                                                <img src={getPokemonSprite(poke.pokemon_id)} alt="sprite" style={styles.pokemonSpriteMini} />
                                                <div style={styles.nameAndTypeRow}>
                                                    <span style={styles.pokemonNameText}>
                                                        {getPokemonName(poke)}
                                                        {/* 🌟 Símbolos con silueta de contorno negra pixelada gruesa para la lista */}
                                                        {poke.gender === 0 && <span style={{ ...styles.listGenderIcon, color: '#3498db' }}>♂</span>}
                                                        {poke.gender === 1 && <span style={{ ...styles.listGenderIcon, color: '#ff3385' }}>♀</span>}
                                                    </span>
                                                    <div style={styles.badgeGroup}>
                                                        <span style={{ ...styles.typeBadge, backgroundColor: TYPE_COLORS[poke.type1] || '#7f8c8d' }}>{poke.type1}</span>
                                                        {poke.type2 && (
                                                            <span style={{ ...styles.typeBadge, backgroundColor: TYPE_COLORS[poke.type2] || '#7f8c8d' }}>{poke.type2}</span>
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                            <div style={styles.hpTextBadgeBox}>
                                                <span style={{ color: '#27ae60', fontWeight: 'bold' }}>{poke.hp}/{poke.max_hp}</span>
                                                <span style={{ color: '#7f8c8d', fontSize: '8px' }}>HP</span>
                                            </div>
                                        </>
                                    ) : (
                                        <span style={styles.emptySlotText}>- Slot Libre -</span>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                </div>

                {/* 💻 COLUMNA DERECHA: REJILLA DEL PC BOX */}
                <div style={styles.rightColumn}>
                    <div style={styles.rightHeaderRow}>
                        <div style={styles.columnTitle}>ALMACÉN GENERAL</div>
                        <span style={styles.boxCountBadge}>Caja: {pcBox.length} Pokémon</span>
                    </div>

                    <div style={styles.pcGridContainer}>
                        {pcBox.map((poke) => (
                            <div
                                key={poke.id}
                                onClick={() => handlePokeClick(poke.id, poke.slot)}
                                title={`${getPokemonName(poke)} (Nv.${poke.level})`}
                                style={styles.gridItemCard}
                                onMouseEnter={(e) => e.currentTarget.style.border = '2px solid #c72e2e'}
                                onMouseLeave={(e) => e.currentTarget.style.border = '1px solid #ccc'}
                            >
                                {/* 🌟 Icono de género flotante con contorno negro puro de 1px */}
                                {poke.gender === 0 && <span style={{ ...styles.floatingGenderIcon, color: '#3498db' }}>♂</span>}
                                {poke.gender === 1 && <span style={{ ...styles.floatingGenderIcon, color: '#ff3385' }}>♀</span>}

                                <img src={getPokemonSprite(poke.pokemon_id)} alt="ico" style={styles.gridSprite} />
                                <span style={styles.gridLevelText}>Nv.{poke.level}</span>
                            </div>
                        ))}

                        {/* Slots vacíos */}
                        {Array.from({ length: Math.max(0, 24 - pcBox.length) }).map((_, index) => (
                            <div key={`empty-${index}`} style={styles.gridItemEmpty}>+</div>
                        ))}
                    </div>
                </div>

            </div>
        </div>
    );
};

// 🎨 Estilos Inline refinados con siluetas de trazo pixel art
const styles = {
    modalWindow: {
        position: 'fixed' as const,
        backgroundColor: '#f1f1f1',
        borderRadius: '6px',
        border: '2px solid #4a4a4a',
        width: '640px',
        height: '420px',
        boxShadow: '0 4px 15px rgba(0,0,0,0.4)',
        fontFamily: 'Verdana, Arial, sans-serif',
        zIndex: 99999,
        display: 'flex',
        flexDirection: 'column' as const,
        userSelect: 'none' as const
    },
    modalHeader: {
        backgroundColor: '#34495e',
        color: 'white',
        padding: '8px 12px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        fontWeight: 'bold' as const,
        fontSize: '13px',
        cursor: 'grab'
    },
    closeButton: { background: 'none', border: 'none', color: 'white', cursor: 'pointer', fontWeight: 'bold' as const, fontSize: '13px' },
    modalBody: { padding: '15px', display: 'flex', gap: '15px', flex: 1, overflow: 'hidden' },

    // Bloques
    leftColumn: { width: '45%', display: 'flex', flexDirection: 'column' as const, gap: '6px' },
    rightColumn: { width: '55%', display: 'flex', flexDirection: 'column' as const, gap: '6px' },
    columnTitle: { fontSize: '11px', fontWeight: 'bold' as const, color: '#333', marginBottom: '2px' },

    // Lista izquierda (Equipo)
    teamListContainer: { display: 'flex', flexDirection: 'column' as const, gap: '5px', overflowY: 'auto' as const, flex: 1, paddingRight: '2px' },
    pokemonRow: {
        borderRadius: '4px',
        height: '52px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '4px 8px',
        boxSizing: 'border-box' as const,
        transition: 'all 0.1s ease'
    },
    rowInfoArea: { display: 'flex', alignItems: 'center', gap: '8px', maxWidth: '75%', overflow: 'hidden' },
    pokemonSpriteMini: { width: '38px', height: '38px', objectFit: 'contain' as const, imageRendering: 'pixelated' as const },
    nameAndTypeRow: { display: 'flex', flexDirection: 'column' as const, gap: '2px', overflow: 'hidden' },
    pokemonNameText: { fontSize: '11px', fontWeight: 'bold' as const, color: '#333', whiteSpace: 'nowrap' as const, display: 'flex', alignItems: 'center' },

    // 🌟 Símbolo del género en lista con delineado negro de alto contraste
    listGenderIcon: {
        marginLeft: '6px',
        fontSize: '14px',
        fontWeight: 'bold' as const,
        textShadow: '-1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000'
    },

    badgeGroup: { display: 'flex', gap: '3px' },
    typeBadge: { color: 'white', fontSize: '7px', fontWeight: 'bold' as const, padding: '1px 4px', borderRadius: '2px', textTransform: 'uppercase' as const },
    hpTextBadgeBox: { display: 'flex', flexDirection: 'column' as const, alignItems: 'flex-end', fontSize: '10px' },
    emptySlotText: { color: '#888', fontStyle: 'italic', fontSize: '11px', margin: 'auto' },

    // Rejilla derecha (Almacén)
    rightHeaderRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
    boxCountBadge: { backgroundColor: '#7f8c8d', color: 'white', fontSize: '9px', padding: '1px 6px', borderRadius: '3px', fontWeight: 'bold' as const },
    pcGridContainer: {
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        borderRadius: '4px',
        flex: 1,
        display: 'grid',
        gridTemplateColumns: 'repeat(4, 1fr)',
        gap: '6px',
        padding: '10px',
        overflowY: 'auto' as const,
        alignContent: 'start'
    },
    gridItemCard: {
        backgroundColor: '#e2e2e2',
        border: '1px solid #ccc',
        borderRadius: '4px',
        height: '62px',
        display: 'flex',
        flexDirection: 'column' as const,
        justifyContent: 'center',
        alignItems: 'center',
        cursor: 'pointer',
        position: 'relative' as const,
        boxSizing: 'border-box' as const
    },

    // 🌟 Símbolo flotante en la rejilla con delineado perimetral negro de 1px (Efecto Sprite grueso)
    floatingGenderIcon: {
        position: 'absolute' as const,
        top: '3px',
        right: '6px',
        fontSize: '14px',
        fontWeight: 'bold' as const,
        lineHeight: '14px',
        textShadow: '-1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000'
    },

    gridSprite: { width: '32px', height: '32px', objectFit: 'contain' as const, imageRendering: 'pixelated' as const, marginTop: '4px' },
    gridLevelText: { fontSize: '8px', fontWeight: 'bold' as const, color: '#e67e22', marginTop: '1px' },

    gridItemEmpty: {
        backgroundColor: 'rgba(226, 226, 226, 0.3)',
        border: '1px solid #e2e2e2',
        borderRadius: '4px',
        height: '62px',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        color: '#aaa',
        fontSize: '14px'
    }
};