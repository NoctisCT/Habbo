import { FC, useEffect, useState, MouseEvent } from 'react';
import { usePokemonSocket } from '../hooks/usePokemonSocket';
import { GetSessionDataManager } from '../api'; // 🔌 Importación de la sesión nativa de Nitro

const POKEMON_DICTIONARY: Record<number, string> = {
    1: 'Bulbasaur',
    4: 'Charmander',
    7: 'Squirtle',
    25: 'Pikachu',
};

// 🎨 Paleta de colores oficial para los Badges de Tipo
const TYPE_COLORS: Record<string, string> = {
    'Planta': '#22c55e', // Verde
    'Veneno': '#a855f7', // Morado
    'Fuego': '#ef4444', // Rojo
    'Agua': '#3b82f6', // Azul
    'Eléctrico': '#eab308', // Amarillo
    'Normal': '#78716c', // Gris/Piedra
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
    const pcBox = safePokemonList.filter(p => p.slot === 0);

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

    return (
        <>
            <button
                onClick={() => setVisible(prev => !prev)}
                className="position-fixed btn btn-sm btn-dark border-info text-info fw-bold"
                style={{ zIndex: 99999, top: '10px', left: '10px', fontSize: '11px', fontFamily: 'monospace', boxShadow: '0 4px 6px rgba(0,0,0,0.4)' }}
            >
                💻 {visible ? 'OCULTAR PC' : 'SISTEMA PC'}
            </button>

            {visible && (
                <div
                    className="position-fixed card p-3"
                    style={{
                        zIndex: 99999, width: '640px', height: '420px', left: `${position.x}px`, top: `${position.y}px`,
                        backgroundColor: '#111827', borderColor: '#374151', boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.9)',
                        color: '#ffffff', fontFamily: 'monospace', userSelect: 'none'
                    }}
                >
                    <div onMouseDown={startDragging} className="d-flex justify-content-between align-items-center mb-2 border-bottom pb-2" style={{ fontSize: '12px', color: '#22d3ee', fontWeight: 'bold', cursor: isDragging ? 'grabbing' : 'grab' }}>
                        <span>💻 ALMACENAMIENTO (SISTEMA BILL PC)</span>
                        <div className="d-flex align-items-center gap-2">
                            <span className="badge bg-dark border border-info text-info">CAJA: {pcBox.length}</span>
                            <button onClick={() => setVisible(false)} className="btn btn-sm btn-danger py-0 px-2 fw-bold" style={{ fontSize: '10px', cursor: 'pointer' }}>X</button>
                        </div>
                    </div>

                    <div className="d-flex h-100 gap-2" style={{ overflow: 'hidden', paddingBottom: '30px' }}>
                        {/* EQUIPO ACTIVO */}
                        <div className="d-flex flex-column gap-1 pr-2 border-end border-secondary" style={{ width: '45%' }}>
                            <div className="text-center fw-bold mb-1 text-warning" style={{ fontSize: '11px' }}>EQUIPO EN MANO</div>
                            <div className="d-flex flex-column gap-1 overflow-y-auto pr-1">
                                {Array.from({ length: 6 }).map((_, index) => {
                                    const currentSlot = index + 1;
                                    const poke = team.find(p => p.slot === currentSlot);
                                    return (
                                        <div
                                            key={currentSlot} onClick={() => poke && handlePokeClick(poke.id, poke.slot)}
                                            className="p-1 rounded d-flex align-items-center justify-content-between"
                                            style={{ backgroundColor: poke ? '#1f2937' : '#030712', border: `1px solid ${poke ? '#22d3ee' : '#1f2937'}`, cursor: poke ? 'pointer' : 'default', fontSize: '11px', height: '52px', transition: 'all 0.15s ease', opacity: poke ? 1 : 0.6 }}
                                            onMouseEnter={(e) => { if (poke) e.currentTarget.style.backgroundColor = '#374151'; }}
                                            onMouseLeave={(e) => { if (poke) e.currentTarget.style.backgroundColor = '#1f2937'; }}
                                        >
                                            {poke ? (
                                                <>
                                                    <div className="d-flex align-items-center gap-1" style={{ maxWidth: '70%' }}>
                                                        <img src={getPokemonSprite(poke.pokemon_id)} alt="sprite" style={{ width: '40px', height: '40px', objectFit: 'contain' }} />
                                                        <div className="d-flex flex-column" style={{ overflow: 'hidden' }}>
                                                            {/* Nombre + Icono de Género */}
                                                            <span className="fw-bold text-white d-flex align-items-center gap-1" style={{ whiteSpace: 'nowrap' }}>
                                                                {getPokemonName(poke)}
                                                                {poke.gender === 0 && <span style={{ color: '#38bdf8', fontWeight: 'bold' }}>♂</span>}
                                                                {poke.gender === 1 && <span style={{ color: '#f472b6', fontWeight: 'bold' }}>♀</span>}
                                                            </span>

                                                            {/* Fila secundaria: Nivel + Medallas de Tipo inline */}
                                                            <div className="d-flex align-items-center gap-1 flex-wrap mt-0.5">
                                                                <span className="text-muted" style={{ fontSize: '9px' }}>Nv.{poke.level}</span>
                                                                <span style={{ backgroundColor: TYPE_COLORS[poke.type1] || '#4b5563', padding: '0px 4px', borderRadius: '2px', color: '#fff', fontSize: '7px', fontWeight: 'bold', textTransform: 'uppercase' }}>{poke.type1}</span>
                                                                {poke.type2 && (
                                                                    <span style={{ backgroundColor: TYPE_COLORS[poke.type2] || '#4b5563', padding: '0px 4px', borderRadius: '2px', color: '#fff', fontSize: '7px', fontWeight: 'bold', textTransform: 'uppercase' }}>{poke.type2}</span>
                                                                )}
                                                            </div>
                                                        </div>
                                                    </div>
                                                    {/* Vida Sincronizada con max_hp */}
                                                    <span className="text-success fw-bold text-end pr-1" style={{ fontSize: '10px', lineHeight: '11px' }}>
                                                        {poke.hp}/{poke.max_hp}<br />
                                                        <span className="text-muted" style={{ fontSize: '8px' }}>HP</span>
                                                    </span>
                                                </>
                                            ) : (
                                                <span className="text-secondary italic m-auto" style={{ fontSize: '10px' }}>- Vacío -</span>
                                            )}
                                        </div>
                                    );
                                })}
                            </div>
                        </div>

                        {/* CAJA GENERAL */}
                        <div className="d-flex flex-column gap-1 pl-1" style={{ width: '55%', height: '100%' }}>
                            <div className="text-center fw-bold mb-1 text-info" style={{ fontSize: '11px' }}>ALMACÉN DE POKÉMON</div>
                            <div className="d-flex flex-wrap gap-1 p-2 rounded overflow-y-auto align-content-start" style={{ backgroundColor: '#030712', border: '1px solid #111827', height: '295px' }}>
                                {pcBox.map((poke) => (
                                    <div
                                        key={poke.id} onClick={() => handlePokeClick(poke.id, poke.slot)}
                                        className="rounded d-flex flex-column align-items-center justify-content-center cursor-pointer text-center p-1"
                                        style={{ width: '54px', height: '58px', backgroundColor: '#1f2937', border: '1px solid #4b5563', fontSize: '9px', transition: 'all 0.15s ease' }}
                                        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#374151'}
                                        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#1f2937'}
                                    >
                                        <img src={getPokemonSprite(poke.pokemon_id)} alt="ico" style={{ width: '32px', height: '32px', objectFit: 'contain' }} />
                                        {/* Nivel + Género mini en cuadrícula */}
                                        <span className="text-warning fw-bold d-flex align-items-center justify-content-center" style={{ fontSize: '8px', lineHeight: '8px' }}>
                                            Nv.{poke.level}
                                            {poke.gender === 0 && <span style={{ color: '#38bdf8', marginLeft: '1px', fontSize: '8px' }}>♂</span>}
                                            {poke.gender === 1 && <span style={{ color: '#f472b6', marginLeft: '1px', fontSize: '8px' }}>♀</span>}
                                        </span>
                                    </div>
                                ))}
                                {Array.from({ length: Math.max(0, 24 - pcBox.length) }).map((_, index) => (
                                    <div key={`empty-${index}`} className="rounded d-flex align-items-center justify-content-center text-muted" style={{ width: '54px', height: '58px', backgroundColor: 'rgba(31, 41, 55, 0.1)', border: '1px dashed #1f2937', fontSize: '12px' }}>+</div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
};