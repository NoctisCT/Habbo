import { FC, useEffect, useState } from 'react';
import { WiredFurniType } from '../../../../api';
import { Text } from '../../../../common';
import { useWired } from '../../../../hooks';
import { WiredActionBaseView } from './WiredActionBaseView';

export const WiredActionAvatarSyncView: FC<{}> = () =>
{
    const [ mode, setMode ] = useState<number>(1);
    const [ unlimited, setUnlimited ] = useState<boolean>(true);
    const [ duration, setDuration ] = useState<number>(30);
    const [ hydrated, setHydrated ] = useState<boolean>(false);

    const { trigger = null, setStringParam = null } = useWired();

    useEffect(() =>
    {
        if(!hydrated || !setStringParam) return;

        setStringParam(`${ mode };${ unlimited ? 0 : duration }`);
    }, [ mode, unlimited, duration, hydrated, setStringParam ]);

    useEffect(() =>
    {
        if(!trigger || !trigger.stringData) return;

        setHydrated(false);

        const parts = trigger.stringData.split(';');

        if(parts.length < 2) return;

        const savedMode = parseInt(parts[0], 10);
        const savedDuration = parseInt(parts[1], 10);

        if(savedMode >= 1 && savedMode <= 3)
        {
            setMode(savedMode);
        }

        if(savedDuration > 0)
        {
            setUnlimited(false);
            setDuration(savedDuration);
        }
        else
        {
            setUnlimited(true);
            setDuration(30);
        }

        if(setStringParam)
        {
            setStringParam(trigger.stringData);
        }

        setHydrated(true);

    }, [ trigger, setStringParam ]);

    const save = () =>
    {
        if(setStringParam)
        {
            setStringParam(`${ mode };${ unlimited ? 0 : duration }`);
        }
    };

    return (
        <WiredActionBaseView
            requiresFurni={ WiredFurniType.STUFF_SELECTION_OPTION_NONE }
            hasSpecialInput={ true }
            save={ save }>

            <div className="d-flex flex-column gap-3">

                <div className="d-flex flex-column gap-1">

                    <Text bold>
                        Modo de sincronizacion:
                    </Text>

                    <select
                        className="form-select form-select-sm bg-dark text-white border-secondary"
                        value={ mode }
                        onChange={ event =>
                            setMode(parseInt(event.target.value, 10))
                        }>

                        <option value={ 1 }>
                            Modo 1 - Lider y seguidores libres
                        </option>

                        <option value={ 2 }>
                            Modo 2 - Lider y seguidores con movimiento bloqueado
                        </option>

                        <option value={ 3 }>
                            Modo 3 - Todos son lideres
                        </option>

                    </select>

                </div>

                <div className="d-flex flex-column gap-2">

                    <Text bold>
                        Duracion:
                    </Text>

                    <div className="form-check">

                        <input
                            id="avatarsync-unlimited"
                            className="form-check-input"
                            type="checkbox"
                            checked={ unlimited }
                            onChange={ event =>
                                setUnlimited(event.target.checked)
                            }
                        />

                        <label
                            className="form-check-label text-dark"
                            htmlFor="avatarsync-unlimited">

                            Ilimitada

                        </label>

                    </div>

                    { !unlimited &&
                        <div className="d-flex flex-column gap-1">

                            <div className="d-flex justify-content-between align-items-center">

                                <Text>
                                    Tiempo activo
                                </Text>

                                <Text variant="info">
                                    { duration } segundos
                                </Text>

                            </div>

                            <input
                                type="range"
                                className="form-range"
                                min={ 1 }
                                max={ 300 }
                                value={ duration }
                                onChange={ event =>
                                    setDuration(
                                        parseInt(
                                            event.target.value,
                                            10
                                        )
                                    )
                                }
                            />

                            <input
                                type="number"
                                className="form-control form-control-sm bg-dark text-white border-secondary"
                                min={ 1 }
                                max={ 300 }
                                value={ duration }
                                onChange={ event =>
                                {
                                    const value =
                                        parseInt(
                                            event.target.value,
                                            10
                                        );

                                    if(!Number.isNaN(value))
                                    {
                                        setDuration(
                                            Math.max(
                                                1,
                                                Math.min(
                                                    300,
                                                    value
                                                )
                                            )
                                        );
                                    }
                                }}
                            />

                        </div>
                    }

                </div>

                <Text variant="muted">
                    El primer usuario sera el lider en los modos 1 y 2.
                    En el modo 3 todos los participantes podran originar acciones.
                </Text>

            </div>

        </WiredActionBaseView>
    );
};
