import { FC, useEffect, useState } from 'react';
import { LocalizeText, WiredFurniType } from '../../../../api';
import { Text } from '../../../../common';
import { useWired } from '../../../../hooks';
import { WiredActionBaseView } from './WiredActionBaseView';

export const WiredActionVisualModifierView: FC<{}> = props => {
    const [modifierType, setModifierType] = useState<number>(0);
    const [modifierValue, setModifierValue] = useState<string>('2.0');
    const [duration, setDuration] = useState<number>(10);

    const { trigger = null, setStringParam = null } = useWired();

    // EFECTO FIX: Mantiene el stringParam de Nitro sincronizado en tiempo real antes de guardar
    useEffect(() => {
        if (setStringParam) {
            setStringParam(`${modifierType};${duration};${modifierValue}`);
        }
    }, [modifierType, duration, modifierValue, setStringParam]);

    const save = () => {
        // Al estar el useEffect sincronizando el estado, el contexto ya tiene el string correcto.
        // Mantenemos la ejecución nativa limpia.
        setStringParam(`${modifierType};${duration};${modifierValue}`);
    };

    useEffect(() => {
        if (trigger && trigger.stringData) {
            const parts = trigger.stringData.split(';');
            if (parts.length === 3) {
                setModifierType(parseInt(parts[0]) ?? 0);
                setDuration(parseInt(parts[1]) ?? 10);
                setModifierValue(parts[2] ?? '2.0');
            }
        }
    }, [trigger]);

    return (
        <WiredActionBaseView requiresFurni={WiredFurniType.STUFF_SELECTION_OPTION_NONE} hasSpecialInput={true} save={save}>
            <div className="d-flex flex-column gap-2">

                <div className="d-flex flex-column gap-1">
                    <Text bold variant="white">Efecto Visual Casual:</Text>
                    <select
                        className="form-select form-select-sm bg-dark text-white border-secondary"
                        value={modifierType}
                        onChange={event => {
                            const val = parseInt(event.target.value);
                            setModifierType(val);
                            if (val === 0) setModifierValue('2.0');
                            if (val === 1) setModifierValue('0');
                            if (val === 2) setModifierValue('102');
                            if (val === 3) setModifierValue('4');
                        }}
                    >
                        <option value={0}>Cambiar Escala / Tamaño (Gigante/Mini)</option>
                        <option value={1}>Invisibilidad Completa</option>
                        <option value={2}>Convertirse en Objeto (Prop Hunt)</option>
                        <option value={3}>Super Velocidad de Caminata</option>
                    </select>
                </div>

                <div className="d-flex flex-column gap-1">
                    {modifierType === 0 && <Text bold variant="white">Multiplicador de Tamaño (Ej: 0.5 mini, 2.5 gigante):</Text>}
                    {modifierType === 1 && <Text variant="muted">El avatar será invisible para los demás jugadores.</Text>}
                    {modifierType === 2 && <Text bold variant="white">ID de la línea del catálogo del Furni (Disfraz):</Text>}
                    {modifierType === 3 && <Text bold variant="white">Multiplicador de velocidad (Recomendado: 2 a 5):</Text>}

                    {modifierType !== 1 && (
                        <input
                            type="text"
                            className="form-control form-control-sm bg-dark text-white border-secondary"
                            value={modifierValue}
                            onChange={event => setModifierValue(event.target.value)}
                        />
                    )}
                </div>

                <div className="d-flex flex-column gap-1">
                    <div className="d-flex justify-content-between align-items-center">
                        <Text bold variant="white">Duración del efecto:</Text>
                        <Text variant="info">{duration} segundos</Text>
                    </div>
                    <input
                        type="range"
                        className="form-range"
                        min={1}
                        max={300}
                        value={duration}
                        onChange={event => setDuration(parseInt(event.target.value))}
                    />
                </div>

            </div>
        </WiredActionBaseView>
    );
}