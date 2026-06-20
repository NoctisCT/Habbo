import React, { useState } from 'react';
import { GetSessionDataManager } from '../api';
import { usePokemonSocket } from '../hooks/usePokemonSocket';
import { PokemonInventoryView } from './PokemonInventoryView';

export const PokemonMenu: React.FC = () => {
    const userId = GetSessionDataManager().userId;
    const { pokemonList } = usePokemonSocket(userId);

    const activeTeamCount = pokemonList.filter(p => p.slot >= 1 && p.slot <= 6).length;

    // Estados de apertura (La mochila se gestiona aquí, el PC pasa a ser por evento)
    const [isInventoryOpen, setIsInventoryOpen] = useState<boolean>(false);
    const [isPokedexOpen, setIsPokedexOpen] = useState<boolean>(false);

    // 🌟 NUEVO: Lanza la orden de apertura al componente real del PC
    const handleOpenPC = () => {
        window.dispatchEvent(new CustomEvent('pokemon:open_pc'));
    };

    return (
        <>
            {/* 🛠️ BARRA DE HERRAMIENTAS FLOTANTE VERTICAL */}
            <div style={styles.toolbarContainer}>
                <div style={styles.toolbarTitle}>MENÚ POKÉMON</div>

                <div style={styles.buttonGroup}>
                    {/* Botón 🎒 Mochila (Ya está vinculado y abre tu inventario real) */}
                    <button onClick={() => setIsInventoryOpen(true)} style={styles.menuButton}>
                        <span style={styles.icon}>🎒</span>
                        <span style={styles.label}>Mochila</span>
                    </button>

                    {/* Botón 💻 PC / Equipo (Vinculado al PC Real) */}
                    <button onClick={handleOpenPC} style={styles.menuButton}>
                        <span style={styles.icon}>💻</span>
                        <div style={styles.labelContainer}>
                            <span style={styles.label}>Equipo</span>
                            <span style={styles.teamBadge}>{activeTeamCount}/6</span>
                        </div>
                    </button>

                    {/* Botón 📖 Pokédex */}
                    <button onClick={() => setIsPokedexOpen(true)} style={styles.menuButton}>
                        <span style={styles.icon}>📖</span>
                        <span style={styles.label}>Pokédex</span>
                    </button>
                </div>
            </div>

            {/* ========================================================================= */}
            {/* 🪟 INYECCIÓN DE MODALES GESTIONADOS POR LA BARRA */}
            {/* ========================================================================= */}

            {/* 1. Modal de la Mochila Conectada */}
            <PokemonInventoryView
                isOpen={isInventoryOpen}
                onClose={() => setIsInventoryOpen(false)}
            />

            {/* 3. Placeholder para la Pokédex */}
            {isPokedexOpen && (
                <div className="nitro-mock-modal" style={styles.modalOverlay}>
                    <div style={styles.modalWindow}>
                        <div style={styles.modalHeader}>
                            <span>Pokédex Regional</span>
                            <button onClick={() => setIsPokedexOpen(false)} style={styles.closeButton}>X</button>
                        </div>
                        <div style={{ padding: '20px', fontSize: '12px', textAlign: 'center' }}>
                            <p>📖 Próximamente: Registro de avistamientos y capturas de la DB.</p>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
};

// 🎨 Estilos Nitro clásicos adaptados para posicionamiento vertical izquierdo
const styles = {
    toolbarContainer: {
        position: 'fixed' as const,
        left: '20px',
        top: '50%',
        transform: 'translateY(-50%)',
        backgroundColor: '#2c3e50',
        border: '2px solid #1a252f',
        borderRadius: '6px',
        padding: '6px',
        boxShadow: '0 4px 10px rgba(0,0,0,0.5)',
        fontFamily: 'Verdana, Arial, sans-serif',
        zIndex: 999,
        width: '135px'
    },
    toolbarTitle: {
        color: '#bdc3c7',
        fontSize: '9px',
        fontWeight: 'bold' as const,
        textAlign: 'center' as const,
        marginBottom: '6px',
        letterSpacing: '0.5px'
    },
    buttonGroup: {
        display: 'flex',
        flexDirection: 'column' as const,
        gap: '4px'
    },
    menuButton: {
        display: 'flex',
        alignItems: 'center',
        gap: '10px',
        backgroundColor: '#34495e',
        border: '1px solid #2c3e50',
        borderRadius: '4px',
        color: 'white',
        padding: '6px 10px',
        cursor: 'pointer',
        textAlign: 'left' as const,
        fontSize: '11px',
        fontWeight: 'bold' as const,
        transition: 'background 0.2s',
        boxShadow: 'inset 0 1px 0 rgba(255,255,255,0.1)'
    },
    icon: {
        fontSize: '16px'
    },
    labelContainer: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        flex: 1
    },
    label: {
        color: '#fff'
    },
    teamBadge: {
        backgroundColor: '#e74c3c',
        color: 'white',
        fontSize: '9px',
        padding: '1px 5px',
        borderRadius: '10px',
        fontWeight: 'bold' as const
    },
    modalOverlay: { position: 'fixed' as const, top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 9998 },
    modalWindow: { backgroundColor: '#f1f1f1', borderRadius: '6px', border: '2px solid #4a4a4a', width: '400px', boxShadow: '0 4px 15px rgba(0,0,0,0.4)', fontFamily: 'Verdana, Arial, sans-serif' },
    modalHeader: { backgroundColor: '#34495e', color: 'white', padding: '8px 12px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontWeight: 'bold' as const, fontSize: '13px' },
    closeButton: { background: 'none', border: 'none', color: 'white', cursor: 'pointer', fontWeight: 'bold' as const }
};