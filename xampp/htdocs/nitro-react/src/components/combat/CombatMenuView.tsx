import { FC, useEffect, useState, MouseEvent } from 'react';
// CORRECCIÓN: Importamos el capturador de sesión nativo de tu hotel junto con el despachador
import { SendMessageComposer, GetRoomSession } from '../../api';

class CombatMenuActionComposer {
    private _data: any[];
    constructor(accion: string) {
        this._data = [4502, accion];
    }
    public getMessageArray(): any[] { return this._data; }
    public dispose(): void { this._data = null; }
}

export const CombatMenuView: FC = () => {
    const [visible, setVisible] = useState<boolean>(false);
    const [miTurno, setMiTurno] = useState<boolean>(false);
    const [mov, setMov] = useState<number>(0);
    const [timeLeft, setTimeLeft] = useState<number>(30);

    // NUEVOS ESTADOS: Sincronización matemática del HUD global de combate
    const [myHp, setMyHp] = useState<number>(0);
    const [myMaxHp, setMyMaxHp] = useState<number>(0);
    const [myMp, setMyMp] = useState<number>(0);
    const [myMaxMp, setMyMaxMp] = useState<number>(0);

    const [oppHp, setOppHp] = useState<number>(0);
    const [oppMaxHp, setOppMaxHp] = useState<number>(0);
    const [oppMp, setOppMp] = useState<number>(0);
    const [oppMaxMp, setOppMaxMp] = useState<number>(0);

    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);
    const [position, setPosition] = useState({ x: window.innerWidth - 300, y: window.innerHeight - 520 });
    const [isDragging, setIsDragging] = useState(false);
    const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });

    const botones = ["Atacar", "Comandos", "Habilidades", "Objetos", "Pasar Turno"];

    useEffect(() => {
        const handleMenuData = (event: any) => {
            try {
                const data = JSON.parse(event.detail);
                setVisible(data.mostrar);
                setMiTurno(data.miTurno);
                setMov(data.mov);

                // Mapeo directo y blindado de recursos sin colisiones de ID
                if (data.mostrar) {
                    setMyHp(data.myHp ?? 0);
                    setMyMaxHp(data.myMaxHp ?? 0);
                    setMyMp(data.myMp ?? 0);
                    setMyMaxMp(data.myMaxMp ?? 0);
                    setOppHp(data.oppHp ?? 0);
                    setOppMaxHp(data.oppMaxHp ?? 0);
                    setOppMp(data.oppMp ?? 0);
                    setOppMaxMp(data.oppMaxMp ?? 0);
                }
            } catch (e) {
                console.error("Error procesando JSON del menú de combate", e);
            }
        };

        const handleLocalStep = () => {
            setMov(prev => Math.max(0, prev - 1));
        };

        window.addEventListener('RPG_MENU_DATA' as any, handleMenuData);
        window.addEventListener('RPG_LOCAL_STEP' as any, handleLocalStep);

        return () => {
            window.removeEventListener('RPG_MENU_DATA' as any, handleMenuData);
            window.removeEventListener('RPG_LOCAL_STEP' as any, handleLocalStep);
        };
    }, []);

    useEffect(() => {
        if (!visible) return;
        setTimeLeft(30);
    }, [miTurno, visible]);

    useEffect(() => {
        if (!visible || !miTurno) return;

        if (timeLeft <= 0) {
            manejarAccion("Pasar Turno");
            return;
        }

        const interval = setInterval(() => {
            setTimeLeft(prev => prev - 1);
        }, 1000);

        return () => clearInterval(interval);
    }, [timeLeft, visible, miTurno]);

    const startDragging = (e: MouseEvent) => {
        setIsDragging(true);
        setDragOffset({
            x: e.clientX - position.x,
            y: e.clientY - position.y
        });
    };

    useEffect(() => {
        const handleMouseMove = (e: globalThis.MouseEvent) => {
            if (!isDragging) return;
            setPosition({
                x: e.clientX - dragOffset.x,
                y: e.clientY - dragOffset.y
            });
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

    const manejarAccion = (nombreBoton: string) => {
        if (!miTurno) return;

        if (nombreBoton === "Pasar Turno") {
            console.log("[RPG UI] Click detectado: Pasar Turno -> Canal de chat seguro");
            GetRoomSession().sendChatMessage(":pasar_turno", 0, "");
        }
        else if (nombreBoton === "Atacar") {
            console.log("[RPG UI] Click detectado: Atacar -> Activando modo objetivo");
            GetRoomSession().sendChatMessage(":atacar", 0, "");
        }
        else {
            const accionFormateada = nombreBoton.toLowerCase().replace(" ", "_");
            console.log("[RPG UI] Click detectado en botón: " + nombreBoton + " -> Despachando acción: '" + accionFormateada + "'");
            SendMessageComposer(new CombatMenuActionComposer(accionFormateada));
        }
    };

    if (!visible) return null;

    // Cálculo porcentual seguro de las barras del HUD de Combate
    const myHpPercent = myMaxHp > 0 ? Math.min(100, Math.max(0, (myHp / myMaxHp) * 100)) : 0;
    const myMpPercent = myMaxMp > 0 ? Math.min(100, Math.max(0, (myMp / myMaxMp) * 100)) : 0;
    const oppHpPercent = oppMaxHp > 0 ? Math.min(100, Math.max(0, (oppHp / oppMaxHp) * 100)) : 0;
    const oppMpPercent = oppMaxMp > 0 ? Math.min(100, Math.max(0, (oppMp / oppMaxMp) * 100)) : 0;

    return (
        <div
            className="position-fixed card p-3"
            style={{
                zIndex: 99999,
                width: '260px',
                left: `${position.x}px`,
                top: `${position.y}px`,
                backgroundColor: '#111827',
                borderColor: '#374151',
                boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)',
                color: '#ffffff',
                fontFamily: 'monospace',
                userSelect: 'none'
            }}
        >
            {/* CABECERA */}
            <div
                onMouseDown={startDragging}
                className="d-flex justify-content-between align-items-center mb-2 border-bottom pb-2"
                style={{
                    fontSize: '12px',
                    color: '#22d3ee',
                    fontWeight: 'bold',
                    cursor: isDragging ? 'grabbing' : 'grab'
                }}
            >
                <span>☰ MENÚ DE COMBATE</span>
                <div className="d-flex gap-1 align-items-center">
                    {miTurno && (
                        <span className="badge bg-danger text-white" style={{ fontSize: '10px' }}>
                            ⏱ {timeLeft}s
                        </span>
                    )}
                    <span className="badge bg-dark border border-info text-info">MOV: {mov}</span>
                </div>
            </div>

            {/* BANNER DE INICIATIVA */}
            <div
                className="text-center small fw-bold mb-3 p-1 rounded text-uppercase"
                style={{
                    backgroundColor: miTurno ? '#064e3b' : '#5c1d1d',
                    color: miTurno ? '#34d399' : '#f87171',
                    border: `1px solid ${miTurno ? '#059669' : '#dc2626'}`
                }}
            >
                {miTurno ? '¡Tu Turno!' : 'Esperando Rival...'}
            </div>

            {/* =========================================================================
                NUEVO COMPONENTE: HUD DE ESTADÍSTICAS INTEGRADO (INDISPENSABLE)
                ========================================================================= */}
            <div className="mb-3 p-2 rounded" style={{ backgroundColor: '#1f2937', border: '1px solid #4b5563', fontSize: '11px' }}>
                {/* STATUS JUGADOR LOCAL */}
                <div className="mb-2">
                    <div className="d-flex justify-content-between mb-1 fw-bold">
                        <span style={{ color: '#34d399' }}>● TÚ</span>
                        <span>HP: {myHp}/{myMaxHp}</span>
                    </div>
                    <div className="w-100 rounded-sm" style={{ height: '6px', backgroundColor: '#374151', overflow: 'hidden' }}>
                        <div style={{ width: `${myHpPercent}%`, height: '100%', backgroundColor: '#10b981', transition: 'width 0.2s ease-out' }} />
                    </div>
                    <div className="d-flex justify-content-between mt-1 mb-1" style={{ fontSize: '10px' }}>
                        <span style={{ color: '#9ca3af' }}>Reiryoku</span>
                        <span style={{ color: '#60a5fa' }}>{myMp}/{myMaxMp}</span>
                    </div>
                    <div className="w-100 rounded-sm" style={{ height: '4px', backgroundColor: '#374151', overflow: 'hidden' }}>
                        <div style={{ width: `${myMpPercent}%`, height: '100%', backgroundColor: '#3b82f6', transition: 'width 0.2s ease-out' }} />
                    </div>
                </div>

                {/* STATUS OPONENTE */}
                <div className="pt-2 border-top border-secondary">
                    <div className="d-flex justify-content-between mb-1 fw-bold">
                        <span style={{ color: '#f87171' }}>● RIVAL</span>
                        <span>HP: {oppHp}/{oppMaxHp}</span>
                    </div>
                    <div className="w-100 rounded-sm" style={{ height: '6px', backgroundColor: '#374151', overflow: 'hidden' }}>
                        <div style={{ width: `${oppHpPercent}%`, height: '100%', backgroundColor: '#ef4444', transition: 'width 0.2s ease-out' }} />
                    </div>
                    <div className="d-flex justify-content-between mt-1 mb-1" style={{ fontSize: '10px' }}>
                        <span style={{ color: '#9ca3af' }}>Reiryoku</span>
                        <span style={{ color: '#c084fc' }}>{oppMp}/{oppMaxMp}</span>
                    </div>
                    <div className="w-100 rounded-sm" style={{ height: '4px', backgroundColor: '#374151', overflow: 'hidden' }}>
                        <div style={{ width: `${oppMpPercent}%`, height: '100%', backgroundColor: '#a855f7', transition: 'width 0.2s ease-out' }} />
                    </div>
                </div>
            </div>

            {/* BOTONERA ACCIONES */}
            <div className="d-flex flex-column gap-1">
                {botones.map((boton, index) => (
                    <button
                        key={index}
                        disabled={!miTurno}
                        onClick={() => manejarAccion(boton)}
                        onMouseEnter={() => setHoveredIndex(index)}
                        onMouseLeave={() => setHoveredIndex(null)}
                        className="btn btn-sm text-start ps-3 position-relative"
                        style={{
                            backgroundColor: miTurno ? (hoveredIndex === index ? '#374151' : '#1f2937') : '#030712',
                            borderColor: miTurno ? '#4b5563' : '#111827',
                            color: miTurno ? '#f3f4f6' : '#4b5563',
                            cursor: miTurno ? 'pointer' : 'not-allowed',
                            fontSize: '13px',
                            transition: 'background-color 0.1s ease',
                            opacity: miTurno && hoveredIndex === index ? 0.9 : 1
                        }}
                    >
                        {miTurno && <span className="me-2" style={{ color: '#22d3ee' }}>&gt;</span>}
                        {boton}
                    </button>
                ))}
            </div>
        </div>
    );
};