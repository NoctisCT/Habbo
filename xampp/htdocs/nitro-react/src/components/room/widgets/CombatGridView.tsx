import { FC, useEffect, useState } from 'react';
import { GetRoomEngine } from '../../../api';
import { useRoom } from '../../../hooks';

export const CombatGridView: FC = () => {
    const [tiles, setTiles] = useState<{ x: number; y: number }[]>([]);
    const [tick, setTick] = useState<number>(0);
    const { roomSession = null } = useRoom();

    useEffect(() => {
        const handleGridUpdate = (e: any) => {
            try {
                const rawData = e.detail;
                if (!rawData) {
                    setTiles([]);
                    return;
                }
                const parsedTiles: { x: number; y: number }[] = [];
                const pairs = rawData.split(';');
                for (const pair of pairs) {
                    if (!pair) continue;
                    const coords = pair.split(',');
                    if (coords.length === 2) {
                        parsedTiles.push({
                            x: parseInt(coords[0], 10),
                            y: parseInt(coords[1], 10)
                        });
                    }
                }
                setTiles(parsedTiles);
            } catch (e) {
                console.error("Error al procesar los azulejos de la grilla táctica:", e);
            }
        };

        window.addEventListener('RPG_GRID_DATA', handleGridUpdate);
        return () => window.removeEventListener('RPG_GRID_DATA', handleGridUpdate);
    }, []);

    // Bucle de animación continuo para recalcular posiciones (Caminatas, arrastres y zoom de la sala)
    useEffect(() => {
        if (tiles.length === 0) return;
        let animationFrameId: number;
        const updateLoop = () => {
            setTick(prev => prev + 1);
            animationFrameId = requestAnimationFrame(updateLoop);
        };
        animationFrameId = requestAnimationFrame(updateLoop);
        return () => cancelAnimationFrame(animationFrameId);
    }, [tiles]);

    if (!tiles || tiles.length === 0 || !roomSession) return null;

    const roomEngine = GetRoomEngine();
    const activeRoomId = roomEngine.activeRoomId;

    // CORRECCIÓN EN NITRO: Se usa ownRoomIndex para pillar tu ID de objeto en la sala actual
    const ownRoomIndex = roomSession.ownRoomIndex;

    // Buscamos el objeto de tu avatar (Categoría 100 = UNIT) y su posición real en la pantalla
    const ownUserObj = roomEngine.getRoomObject(activeRoomId, ownRoomIndex, 100);
    const screenPos = roomEngine.getRoomObjectScreenLocation(activeRoomId, ownRoomIndex, 100);

    if (!ownUserObj || !screenPos) return null;

    // Soporte para leer la coordenada tanto si viene como propiedad .location o método .getLocation()
    const currentGrid = ownUserObj.location || (typeof (ownUserObj as any).getLocation === 'function' ? (ownUserObj as any).getLocation() : null);
    if (!currentGrid) return null;

    // Escala del lienzo gráfico según el nivel de zoom de la sala
    const scale = roomEngine.getRoomInstanceRenderingCanvasScale(activeRoomId, 1);
    const baseWidth = 64 * scale;
    const baseHeight = 32 * scale;

    return (
        <div className="position-absolute top-0 start-0 w-100 h-100" style={{ pointerEvents: 'none', zIndex: 50 }}>
            <svg className="w-100 h-100" style={{ overflow: 'visible' }}>
                {tiles.map((tile, index) => {
                    // Calculamos la distancia de las baldosas respecto a donde está parado tu Habbo
                    const dx = tile.x - currentGrid.x;
                    const dy = tile.y - currentGrid.y;

                    // Proyección isométrica imantada magnéticamente a los pies de tu personaje
                    const isoX = screenPos.x + (dx - dy) * (baseWidth / 2);
                    const isoY = screenPos.y + (dx + dy) * (baseHeight / 2);

                    const points = [
                        `${isoX},${isoY - baseHeight / 2}`,
                        `${isoX + baseWidth / 2},${isoY}`,
                        `${isoX},${isoY + baseHeight / 2}`,
                        `${isoX - baseWidth / 2},${isoY}`
                    ].join(' ');

                    return (
                        <polygon
                            key={`${index}-${tick}`}
                            points={points}
                            fill="rgba(34, 211, 238, 0.25)"
                            stroke="#22d3ee"
                            strokeWidth="2"
                            style={{ filter: 'drop-shadow(0px 0px 4px rgba(34, 211, 238, 0.6))' }}
                        />
                    );
                })}
            </svg>
        </div>
    );
};