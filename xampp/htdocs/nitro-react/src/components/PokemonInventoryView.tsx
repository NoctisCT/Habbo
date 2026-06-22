import React, { useState, useEffect, MouseEvent } from 'react';
import { GetSessionDataManager } from '../api';
import { usePokemonSocket } from '../hooks/usePokemonSocket'; // 🔌 Tu hook unificado rey

interface InventoryItem {
    id: number;          // ID único del ítem en la DB (para mandar a Node)
    name: string;
    description: string;
    quantity: number;
    type: 'HEALING' | 'REVIVE' | 'BALL'; // Categoría del ítem
    iconUrl?: string;    // Ruta web completa opcional
    iconName?: string;   // Nombre del archivo plano opcional (.png)
}

interface PokemonInventoryViewProps {
    isOpen: boolean;
    onClose: () => void;
    isInBattle?: boolean; // ⚔️ Contexto automático desde el Encounter Manager
}

// 📦 Peso de organización: Controla el orden de aparición en la mochila
const TYPE_ORDER: Record<string, number> = {
    'BALL': 1,     // Primero todas las Poké Balls
    'HEALING': 2,  // Luego las pociones y elixires
    'REVIVE': 3    // Al final los revivir
};

export const PokemonInventoryView: React.FC<PokemonInventoryViewProps> = ({
    isOpen,
    onClose,
    isInBattle = false
}) => {
    if (!isOpen) return null;

    const userId = GetSessionDataManager().userId;

    // 🌟 SINCRONIZACIÓN REAL: Extraemos sendThrowBall y battleState del socket unificado
    const { pokemonList, inventoryList, sendUseHealingItem, sendThrowBall, battleState } = usePokemonSocket(userId);

    // 🌟 Filtrar equipo activo (Slots del 1 al 6)
    const activeTeam = pokemonList.filter(p => p.slot >= 1 && p.slot <= 6);

    // Estados de selección y puntería
    const [selectedItem, setSelectedItem] = useState<InventoryItem | null>(null);
    const [isTargeting, setIsTargeting] = useState<boolean>(false);

    // 🗺️ ESTADOS DE DESPLAZAMIENTO: Para hacer la mochila libremente arrastrable estilo Habbo UI
    const [position, setPosition] = useState({ x: 0, y: 0 });
    const [isDragging, setIsDragging] = useState(false);
    const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });

    // 🚨 ANTICHEAT / CONTROL DE FLUJO: Si intentan abrir la mochila desde el menú natural (lateral/externo)
    // estando en mitad de un combate activo, la cerramos fulminantemente de inmediato.
    useEffect(() => {
        if (isOpen && battleState && !isInBattle) {
            console.log("[MOCHILA BLINDADA] Intento de exploit bloqueado: Cerrando inventario externo en batalla.");
            onClose();
        }
    }, [isOpen, battleState, isInBattle, onClose]);

    // Auto-centrar la mochila en la pantalla del usuario en el momento exacto de su apertura legítima
    useEffect(() => {
        if (isOpen) {
            setPosition({
                x: Math.max(10, window.innerWidth / 2 - 250),
                y: Math.max(10, window.innerHeight / 2 - 150)
            });
            setSelectedItem(null);
            setIsTargeting(false);
        }
    }, [isOpen]);

    // Manejador del arrastre por ratón
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

    const startDragging = (e: MouseEvent) => {
        setIsDragging(true);
        setDragOffset({ x: e.clientX - position.x, y: e.clientY - position.y });
    };

    // =========================================================================
    // 📊 FILTRADO Y ORGANIZACIÓN LÓGICA INTELIGENTE
    // =========================================================================
    const organizedItems = inventoryList
        .filter((item: InventoryItem) => item.quantity > 0)
        .sort((a, b) => {
            const orderA = TYPE_ORDER[a.type] || 99;
            const orderB = TYPE_ORDER[b.type] || 99;
            if (orderA !== orderB) return orderA - orderB;
            return a.id - b.id;
        });

    const handleItemClick = (item: InventoryItem) => {
        if (item.quantity <= 0) return;

        setSelectedItem(item);

        if (isInBattle && item.type === 'BALL') {
            setIsTargeting(false);
            return;
        }

        if (item.type === 'HEALING' || item.type === 'REVIVE') {
            setIsTargeting(true);
        } else {
            setIsTargeting(false);
        }
    };

    const handleUseBallInBattle = (item: InventoryItem) => {
        if (sendThrowBall) {
            console.log(`[🎒 MOCHILA COMBATE] Ejecutando sendThrowBall para Objeto ID: ${item.id}`);
            sendThrowBall(item.id);
        }
        setSelectedItem(null);
        onClose();
    };

    const handleApplyItem = (pokemonStorageId: number) => {
        if (!selectedItem) return;

        // Disparamos la curación nativa (El handler de Node se encargará de restar y pasar el turno)
        if (sendUseHealingItem) {
            sendUseHealingItem(selectedItem.id, pokemonStorageId);
        }

        setIsTargeting(false);
        setSelectedItem(null);
        if (isInBattle) onClose();
    };

    return (
        <div
            style={{
                ...styles.modalWindow,
                left: `${position.x}px`,
                top: `${position.y}px`,
                cursor: isDragging ? 'grabbing' : 'default'
            }}
        >
            {/* Cabecera Arrastrable */}
            <div onMouseDown={startDragging} style={styles.modalHeader}>
                <span>{isInBattle ? '⚔️ Mochila de Combate' : 'Mochila de Entrenador'}</span>
                <button onClick={onClose} style={styles.closeButton}>X</button>
            </div>

            {/* Doble panel */}
            <div style={styles.modalBody}>
                <div style={styles.inventoryGrid}>
                    {organizedItems.map((item: InventoryItem) => (
                        <div
                            key={item.id}
                            title={item.name}
                            style={{
                                ...styles.itemCard,
                                border: selectedItem?.id === item.id ? '2px solid #c72e2e' : '1px solid #ccc'
                            }}
                            onClick={() => handleItemClick(item)}
                        >
                            <img
                                src={item.iconUrl || `/swf/dcr/hof_furni/icons/${item.iconName || 'hw_pokeball_icon.png'}`}
                                alt={item.name}
                                style={styles.itemIcon}
                            />
                            <span style={styles.badgeCount}>X{item.quantity}</span>
                        </div>
                    ))}
                    {organizedItems.length === 0 && (
                        <p style={{ gridColumn: 'span 4', fontSize: '11px', color: '#666', fontStyle: 'italic' }}>No tienes objetos utilizables en tu mochila.</p>
                    )}
                </div>

                {/* Panel de detalles del objeto */}
                <div style={styles.detailPanel}>
                    {selectedItem ? (
                        <>
                            <h4 style={{ margin: '0 0 5px 0', color: '#333', fontWeight: 'bold', fontSize: '13px' }}>
                                {selectedItem.name.trim()}
                            </h4>
                            <p style={{ margin: 0, color: '#555', fontSize: '11px', lineHeight: '1.4' }}>
                                {selectedItem.description.trim()}
                            </p>

                            {isInBattle && selectedItem.type === 'BALL' && (
                                <div style={styles.targetZone}>
                                    <button
                                        onClick={() => handleUseBallInBattle(selectedItem)}
                                        style={{ ...styles.btnCancel, backgroundColor: '#e67e22', color: '#fff', fontWeight: 'bold', marginTop: '10px' }}
                                    >
                                        🚀 Lanzar al Pokémon Rival
                                    </button>
                                </div>
                            )}

                            {isTargeting && (
                                <div style={styles.targetZone}>
                                    <p style={styles.targetTitle}>¿Sobre qué Pokémon?</p>
                                    <div style={styles.teamSelectContainer}>
                                        {activeTeam.map((pkmn) => (
                                            <button
                                                key={pkmn.id}
                                                style={styles.btnPokemonTarget}
                                                onClick={() => handleApplyItem(pkmn.id)}
                                            >
                                                <span>Slot {pkmn.slot}: <b>{pkmn.name || 'Pokémon'}</b></span>
                                                <span style={styles.hpIndicator}>
                                                    HP: {pkmn.hp ?? pkmn.currentHp ?? 0}/{pkmn.max_hp ?? pkmn.maxHp ?? 0}
                                                </span>
                                            </button>
                                        ))}
                                        {activeTeam.length === 0 && (
                                            <p style={{ fontStyle: 'italic', fontSize: '11px', color: '#777' }}>No tienes Pokémon en tu equipo activo.</p>
                                        )}
                                    </div>
                                    <button onClick={() => { setIsTargeting(false); setSelectedItem(null); }} style={styles.btnCancel}>Cancelar</button>
                                </div>
                            )}
                        </>
                    ) : (
                        <p style={styles.emptyText}>Selecciona un objeto de la mochila para usarlo.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

const styles = {
    modalWindow: { position: 'fixed' as const, backgroundColor: '#f1f1f1', borderRadius: '6px', border: '2px solid #4a4a4a', width: '500px', boxShadow: '0 4px 15px rgba(0,0,0,0.5)', fontFamily: 'Verdana, Arial, sans-serif', zIndex: 999999, display: 'flex', flexDirection: 'column' as const, userSelect: 'none' as const },
    modalHeader: { backgroundColor: '#34495e', color: 'white', padding: '8px 12px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontWeight: 'bold' as const, fontSize: '13px', cursor: 'grab' },
    closeButton: { background: 'none', border: 'none', color: 'white', cursor: 'pointer', fontWeight: 'bold' as const },
    modalBody: { padding: '15px', display: 'flex', gap: '15px', minHeight: '220px' },
    inventoryGrid: { width: '55%', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '8px', overflowY: 'auto' as const, maxHeight: '240px', alignContent: 'start' },
    itemCard: { backgroundColor: '#e2e2e2', borderRadius: '4px', height: '60px', display: 'flex', justifyContent: 'center', alignItems: 'center', position: 'relative' as const, cursor: 'pointer', boxSizing: 'border-box' as const },
    itemIcon: { width: '32px', height: '32px', objectFit: 'contain' as const, imageRendering: 'pixelated' as const },
    badgeCount: { position: 'absolute' as const, bottom: '2px', right: '4px', backgroundColor: '#7f8c8d', color: 'white', fontSize: '9px', padding: '1px 4px', borderRadius: '3px', fontWeight: 'bold' as const },
    detailPanel: { width: '45%', backgroundColor: '#fff', border: '1px solid #ccc', padding: '10px', borderRadius: '4px', display: 'flex', flexDirection: 'column' as const, fontSize: '12px' },
    emptyText: { color: '#888', fontStyle: 'italic', textAlign: 'center' as const, margin: 'auto' },
    targetZone: { marginTop: '10px', borderTop: '1px dashed #ccc', paddingTop: '10px', display: 'flex', flexDirection: 'column' as const, flex: 1 },
    targetTitle: { margin: '0 0 6px 0', fontWeight: 'bold' as const, color: '#c72e2e', fontSize: '11px' },
    teamSelectContainer: { display: 'flex', flexDirection: 'column' as const, gap: '4px', overflowY: 'auto' as const, maxHeight: '130px', marginBottom: '8px' },
    btnPokemonTarget: { display: 'flex', justifyContent: 'space-between', width: '100%', padding: '6px 8px', backgroundColor: '#f8f9fa', border: '1px solid #ccc', borderRadius: '3px', cursor: 'pointer', fontSize: '10px', textAlign: 'left' as const },
    hpIndicator: { fontWeight: 'bold' as const, color: '#27ae60' },
    btnCancel: { backgroundColor: '#7f8c8d', color: 'white', border: 'none', padding: '5px', borderRadius: '3px', cursor: 'pointer', fontSize: '11px', marginTop: 'auto', textAlign: 'center' as const }
};