import React, { useState, useEffect } from 'react';
import { GetSessionDataManager } from '../api';
import { usePokemonSocket } from '../hooks/usePokemonSocket';
// 🌟 CLAVE: Importamos el sprite localmente. Vite se encarga de compilarlo y resolver la ruta solo.
import nurseJoySprite from './enfermera_joy.png';

interface PokemonHealingViewProps {
    isOpen: boolean;
    onClose: () => void;
}

type HealingPhase = 'INTRO' | 'HEALING' | 'SUCCESS';

export const PokemonHealingView: React.FC<PokemonHealingViewProps> = ({ isOpen, onClose }) => {
    if (!isOpen) return null;

    const userId = GetSessionDataManager().userId;
    const { sendHealTeam, pokemonList } = usePokemonSocket(userId);

    const activeTeam = pokemonList.filter(p => p.slot >= 1 && p.slot <= 6);
    const totalBallsCount = activeTeam.length > 0 ? activeTeam.length : 6;

    const [phase, setPhase] = useState<HealingPhase>('INTRO');
    const [dialogue, setDialogue] = useState<string>('¡Hola! Bienvenido al Centro Pokémon. ¿Deseas que restaure la salud de todo tu equipo por 50 ₽?');
    const [errorMsg, setErrorMsg] = useState<string | null>(null);

    useEffect(() => {
        const handleSuccess = (e: Event) => {
            // 🎵 AUDIO: Este lo mantenemos de tu ruta de las SWF que responde bien
            const audio = new Audio('/swf/dcr/hof_furni/mp3/pokemon/pokemon_heal.mp3');
            audio.volume = 0.4;
            audio.play().catch(err => console.log('El navegador bloqueó el audio:', err));

            setTimeout(() => {
                setPhase('SUCCESS');
                setDialogue('¡Listo! Tu equipo Pokémon ha sido restaurado por completo y se encuentra en perfectas condiciones. ¡Vuelve cuando quieras! ❤️');
            }, 2500);
        };

        const handleError = (e: Event) => {
            const msg = (e as CustomEvent).detail;
            setErrorMsg(msg);
            setPhase('INTRO');
            setDialogue('¡Hola! Bienvenido al Centro Pokémon. ¿Deseas que restaure la salud de todo tu equipo por 50 ₽?');
        };

        window.addEventListener('pokemon:team_healed', handleSuccess);
        window.addEventListener('pokemon:heal_error', handleError);

        return () => {
            window.removeEventListener('pokemon:team_healed', handleSuccess);
            window.removeEventListener('pokemon:heal_error', handleError);
        };
    }, []);

    const handleConfirmHeal = () => {
        setErrorMsg(null);
        setPhase('HEALING');
        setDialogue('Muy bien, déjame tus Poké Balls por unos instantes...');

        if (sendHealTeam) {
            sendHealTeam();
        }
    };

    const handleCloseModal = () => {
        setPhase('INTRO');
        setDialogue('¡Hola! Bienvenido al Centro Pokémon. ¿Deseas que restaure la salud de todo tu equipo por 50 ₽?');
        setErrorMsg(null);
        onClose();
    };

    return (
        <div className="nitro-mock-modal" style={styles.modalOverlay}>
            <div style={styles.modalWindow}>
                <div style={styles.modalHeader}>
                    <span>Centro Pokémon - Servicio Médico</span>
                    <button onClick={handleCloseModal} style={styles.closeButton} disabled={phase === 'HEALING'}>X</button>
                </div>

                <div style={styles.modalBody}>
                    <div style={styles.avatarArea}>
                        {/* 🌟 ENFERMERA JOY: Usamos la variable del módulo importado de Vite */}
                        <img
                            src={nurseJoySprite}
                            alt="Enfermera Joy"
                            style={styles.nurseSprite}
                        />
                    </div>
                    <div style={styles.textArea}>
                        {errorMsg && <p style={styles.errorLabel}><b>Aviso:</b> {errorMsg}</p>}
                        <p style={styles.dialogueText}><b>Enfermera Joy:</b> {dialogue}</p>
                    </div>
                </div>

                {phase === 'HEALING' && (
                    <div style={styles.machineArea}>
                        <div style={styles.ballsContainer}>
                            {[...Array(totalBallsCount)].map((_, i) => (
                                <img
                                    key={i}
                                    src="/swf/dcr/hof_furni/icons/hw_pokeball_icon.png"
                                    alt="Pokeball"
                                    style={{
                                        ...styles.pokeballSprite,
                                        animation: `pokeball-glow-blink 0.35s infinite alternate`,
                                        animationDelay: `${i * 0.12}s`
                                    }}
                                />
                            ))}
                        </div>
                    </div>
                )}

                <div style={styles.modalFooter}>
                    {phase === 'INTRO' && (
                        <>
                            <button onClick={handleConfirmHeal} style={styles.btnSuccess}>Sí, por favor</button>
                            <button onClick={handleCloseModal} style={styles.btnDanger}>No, gracias</button>
                        </>
                    )}
                    {phase === 'HEALING' && (
                        <span style={styles.loadingText}>Conectando camilla médica... Restaurando HP</span>
                    )}
                    {phase === 'SUCCESS' && (
                        <button onClick={handleCloseModal} style={styles.btnSuccess}>Recoger equipo</button>
                    )}
                </div>
            </div>

            <style>{`
                @keyframes pokeball-glow-blink {
                    from { 
                        filter: brightness(0.4) drop-shadow(0 0 0px rgba(0,0,0,0)); 
                        transform: scale(0.95);
                    }
                    to { 
                        filter: brightness(1.3) drop-shadow(0 0 6px #ff4757); 
                        transform: scale(1.05);
                    }
                }
            `}</style>
        </div>
    );
};

const styles = {
    modalOverlay: { position: 'fixed' as const, top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 9999 },
    modalWindow: { backgroundColor: '#f1f1f1', borderRadius: '6px', border: '2px solid #4a4a4a', width: '420px', boxShadow: '0 4px 15px rgba(0,0,0,0.4)', fontFamily: 'Verdana, Arial, sans-serif' },
    modalHeader: { backgroundColor: '#c72e2e', color: 'white', padding: '8px 12px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontWeight: 'bold' as const, fontSize: '13px' },
    closeButton: { background: 'none', border: 'none', color: 'white', cursor: 'pointer', fontWeight: 'bold' as const },
    modalBody: { padding: '15px', display: 'flex', gap: '15px', alignItems: 'center', fontSize: '12px', color: '#333' },
    avatarArea: { width: '54px', height: '54px', backgroundColor: '#e2e2e2', borderRadius: '5px', border: '1px solid #ccc', display: 'flex', justifyContent: 'center', alignItems: 'center', overflow: 'hidden' },
    nurseSprite: { width: '100%', height: '100%', objectFit: 'contain' as const, imageRendering: 'pixelated' as const },
    textArea: { flex: 1, lineHeight: '1.5' },
    dialogueText: { margin: 0 },
    errorLabel: { color: '#d32f2f', margin: '0 0 8px 0', fontWeight: 'bold' as const },
    machineArea: { display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '14px', backgroundColor: '#d1d1d1', borderTop: '1px dashed #aaa', borderBottom: '1px dashed #aaa' },
    ballsContainer: { display: 'flex', gap: '12px', justifyContent: 'center', alignItems: 'center' },
    pokeballSprite: { width: '24px', height: '24px', objectFit: 'contain' as const },
    modalFooter: { padding: '10px 15px', backgroundColor: '#e2e2e2', display: 'flex', justifyContent: 'flex-end', gap: '10px', borderTop: '1px solid #ccc', minHeight: '42px', alignItems: 'center' },
    btnSuccess: { backgroundColor: '#4caf50', color: 'white', border: '1px solid #388e3c', padding: '6px 14px', cursor: 'pointer', borderRadius: '3px', fontWeight: 'bold' as const, fontSize: '11px' },
    btnDanger: { backgroundColor: '#f44336', color: 'white', border: '1px solid #d32f2f', padding: '6px 14px', cursor: 'pointer', borderRadius: '3px', fontSize: '11px' },
    loadingText: { color: '#444', fontWeight: 'bold' as const, fontSize: '11px', fontStyle: 'italic' }
};