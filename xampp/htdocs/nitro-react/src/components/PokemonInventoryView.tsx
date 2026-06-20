import React, { useState } from 'react';
import { GetSessionDataManager } from '../api';
import { usePokemonSocket } from '../hooks/usePokemonSocket'; // 🔌 Tu hook unificado rey

interface InventoryItem {
    id: number;          // ID único del ítem en la DB (para mandar a Node)
    name: string;
    description: string;
    quantity: number;
    type: 'HEALING' | 'REVIVE' | 'BALL'; // Categoría del ítem
    iconUrl: string;     // Tu ruta de XAMPP para el icono del objeto
}

interface PokemonInventoryViewProps {
    isOpen: boolean;
    onClose: () => void;
    // Aquí puedes pasarle los ítems reales desde tus props, tu estado o context.
    // Te dejo este mock de ejemplo para que veas cómo mapea la estructura:
    items?: InventoryItem[];
}

export const PokemonInventoryView: React.FC<PokemonInventoryViewProps> = ({
    isOpen,
    onClose,
    items = [
        { id: 101, name: 'Poción', description: 'Restaura 20 PS de un Pokémon.', quantity: 5, type: 'HEALING', iconUrl: '/swf/dcr/hof_furni/icons/potion_icon.png' },
        { id: 102, name: 'Superpoción', description: 'Restaura 50 PS de un Pokémon.', quantity: 2, type: 'HEALING', iconUrl: '/swf/dcr/hof_furni/icons/superpotion_icon.png' },
        { id: 103, name: 'Revivir', description: 'Debilita el estado debilitado con 50% de HP.', quantity: 1, type: 'REVIVE', iconUrl: '/swf/dcr/hof_furni/icons/revive_icon.png' }
    ]
}) => {
    if (!isOpen) return null;

    const userId = GetSessionDataManager().userId;
    // Extraemos la lista de Pokémon activa y la función emisora
    const { pokemonList, sendUseHealingItem } = usePokemonSocket(userId);

    // 🌟 Filtrar equipo activo (Slots del 1 al 6)
    const activeTeam = pokemonList.filter(p => p.slot >= 1 && p.slot <= 6);

    // Estados de selección
    const [selectedItem, setSelectedItem] = useState<InventoryItem | null>(null);
    const [isTargeting, setIsTargeting] = useState<boolean>(false);

    const handleItemClick = (item: InventoryItem) => {
        if (item.quantity <= 0) return;

        // Si es un objeto curativo, abrimos el selector de objetivos
        if (item.type === 'HEALING' || item.type === 'REVIVE') {
            setSelectedItem(item);
            setIsTargeting(true);
        }
    };

    const handleApplyItem = (pokemonStorageId: number) => {
        if (!selectedItem || !sendUseHealingItem) return;

        // Emitimos la orden directa a Node con el ítem y el Pokémon elegido
        sendUseHealingItem(selectedItem.id, pokemonStorageId);

        // Opcional: Cerramos el selector o restamos cantidad localmente si no quieres esperar al refresh
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
                        {items.map((item) => (
                            <div
                                key={item.id}
                                style={{
                                    ...styles.itemCard,
                                    border: selectedItem?.id === item.id ? '2px solid #c72e2e' : '1px solid #ccc'
                                }}
                                onClick={() => handleItemClick(item)}
                            >
                                <img src={item.iconUrl} alt={item.name} style={styles.itemIcon} />
                                <span style={styles.badgeCount}>X{item.quantity}</span>
                            </div>
                        ))}
                    </div>

                    {/* Panel de detalles del objeto */}
                    <div style={styles.detailPanel}>
                        {selectedItem ? (
                            <>
                                <h4 style={{ margin: '0 0 5px 0' }}>{selectedItem.name}</h4>
                                <p style={{ margin: 0, color: '#555', fontSize: '11px' }}>{selectedItem.description}</p>

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
                                                    {/* Puedes meter su sprite aquí si lo tienes mapeado */}
                                                    <span>Slot {pkmn.slot}: {pkmn.name || 'Pokémon'}</span>
                                                    <span style={styles.hpIndicator}>HP: {pkmn.currentHp}/{pkmn.maxHp}</span>
                                                </button>
                                            ))}
                                            {activeTeam.length === 0 && (
                                                <p style={{ fontStyle: 'italic', fontSize: '11px' }}>No tienes Pokémon en tu equipo activo.</p>
                                            )}
                                        </div>
                                        <button onClick={() => setIsTargeting(false)} style={styles.btnCancel}>Cancelar</button>
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
    btnPokemonTarget: { display: 'flex', justifyContent: 'space-between', width: '100%', padding: '5px 8px', backgroundColor: '#f8f9fa', border: '1px solid #ccc', borderRadius: '3px', cursor: 'pointer', fontSize: '10px', textAlign: 'left' as const },
    hpIndicator: { fontWeight: 'bold' as const, color: '#27ae60' },
    btnCancel: { backgroundColor: '#7f8c8d', color: 'white', border: 'none', padding: '4px', borderRadius: '3px', cursor: 'pointer', fontSize: '11px', marginTop: 'auto' }
};