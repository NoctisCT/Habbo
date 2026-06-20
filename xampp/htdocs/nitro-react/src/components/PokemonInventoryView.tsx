import React, { useState } from 'react';
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
}

// 📦 Peso de organización: Controla el orden de aparición en la mochila
const TYPE_ORDER: Record<string, number> = {
    'BALL': 1,     // Primero todas las Poké Balls
    'HEALING': 2,  // Luego las pociones y elixires
    'REVIVE': 3    // Al final los revivir
};

export const PokemonInventoryView: React.FC<PokemonInventoryViewProps> = ({
    isOpen,
    onClose
}) => {
    if (!isOpen) return null;

    const userId = GetSessionDataManager().userId;

    // 🌟 CONEXIÓN REAL: Extraemos los datos que llegan vivos de Node a través del socket
    const { pokemonList, inventoryList, sendUseHealingItem } = usePokemonSocket(userId);

    // 🌟 Filtrar equipo activo (Slots del 1 al 6)
    const activeTeam = pokemonList.filter(p => p.slot >= 1 && p.slot <= 6);

    // Estados de selección
    const [selectedItem, setSelectedItem] = useState<InventoryItem | null>(null);
    const [isTargeting, setIsTargeting] = useState<boolean>(false);

    // =========================================================================
    // 📊 FILTRADO Y ORGANIZACIÓN LÓGICA INTELEGENTE
    // =========================================================================
    const organizedItems = inventoryList
        .filter((item: InventoryItem) => item.quantity > 0) // ❌ Mejora 1: Oculta los objetos agotados (Cantidad 0)
        .sort((a, b) => {
            // 📖 Mejora 3: Clasifica por categorías (Balls -> Healing -> Revive) y luego por ID interno
            const orderA = TYPE_ORDER[a.type] || 99;
            const orderB = TYPE_ORDER[b.type] || 99;
            if (orderA !== orderB) return orderA - orderB;
            return a.id - b.id;
        });

    const handleItemClick = (item: InventoryItem) => {
        if (item.quantity <= 0) return;

        // Si es un objeto curativo o revivir, abrimos el selector de objetivos
        if (item.type === 'HEALING' || item.type === 'REVIVE') {
            setSelectedItem(item);
            setIsTargeting(true);
        } else {
            setSelectedItem(item);
            setIsTargeting(false);
        }
    };

    const handleApplyItem = (pokemonStorageId: number) => {
        if (!selectedItem || !sendUseHealingItem) return;

        // Emitimos la orden directa a Node con el ítem y el Pokémon elegido
        sendUseHealingItem(selectedItem.id, pokemonStorageId);

        // Reseteamos el selector tras confirmar el envío
        setIsTargeting(false);
        setSelectedItem(null);
    };

    return (
        <div className="nitro-mock-modal" style={styles.modalOverlay}>
            <div style={styles.modalWindow}>
                {/* Cabecera */}
                <div style={styles.modalHeader}>
                    <span>Mochila de Entrenador</span>
                    <button onClick={onClose} style={styles.closeButton}>X</button>
                </div>

                {/* Doble panel: Izquierda lista de ítems, Derecha detalle */}
                <div style={styles.modalBody}>
                    <div style={styles.inventoryGrid}>
                        {organizedItems.map((item: InventoryItem) => (
                            <div
                                key={item.id}
                                title={item.name} // 👁️ Mejora 2: Muestra el nombre al poner el ratón encima (Tooltip nativo)
                                style={{
                                    ...styles.itemCard,
                                    border: selectedItem?.id === item.id ? '2px solid #c72e2e' : '1px solid #ccc'
                                }}
                                onClick={() => handleItemClick(item)}
                            >
                                {/* 🌟 Soporta tanto la URL completa del ítem como el nombre del icono plano en la carpeta de las Poké Balls */}
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
                                <h4 style={{ margin: '0 0 5px 0' }}>{selectedItem.name}</h4>
                                <p style={{ margin: 0, color: '#555', fontSize: '11px', lineHeight: '1.4' }}>{selectedItem.description}</p>

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
                                                    <span style={styles.hpIndicator}>HP: {pkmn.currentHp}/{pkmn.maxHp}</span>
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
        </div>
    );
};

// Estilos visuales Nitro/Habbo adaptados
const styles = {
    modalOverlay: { position: 'fixed' as const, top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 9998 },
    modalWindow: { backgroundColor: '#f1f1f1', borderRadius: '6px', border: '2px solid #4a4a4a', width: '500px', boxShadow: '0 4px 15px rgba(0,0,0,0.4)', fontFamily: 'Verdana, Arial, sans-serif' },
    modalHeader: { backgroundColor: '#34495e', color: 'white', padding: '8px 12px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontWeight: 'bold' as const, fontSize: '13px' },
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