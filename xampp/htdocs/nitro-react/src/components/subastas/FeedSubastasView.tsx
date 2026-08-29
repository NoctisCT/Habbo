import { FeedSubastasEvent, IEventoFeedSubasta } from '@nitrots/nitro-renderer';
import { FC, useEffect, useState } from 'react';
import { Base, Column, Text } from '../../common';
import { ObtenerFeedSubastas } from '../../api';
import { useMessageEvent } from '../../hooks';

interface FeedSubastasViewProps
{
    visible: boolean;
}

export const FeedSubastasView: FC<FeedSubastasViewProps> = props =>
{
    const { visible = false } = props;
    const [ eventos, setEventos ] = useState<IEventoFeedSubasta[]>([]);

    useMessageEvent(FeedSubastasEvent, (event: FeedSubastasEvent) =>
    {
        setEventos(event.getParser().eventos);
    });

    useEffect(() =>
    {
        if(!visible) return;

        ObtenerFeedSubastas();

        const intervalo = window.setInterval(() =>
        {
            ObtenerFeedSubastas();
        }, 2000);

        return () => window.clearInterval(intervalo);
    }, [ visible ]);

    if(!visible) return null;

    const etiqueta = (tipo: string) =>
    {
        switch(tipo)
        {
            case 'puja': return 'PUJA';
            case 'venta': return 'VENDIDO';
            case 'sin_pujas': return 'FIN';
            default: return 'INICIO';
        }
    };

    return (
        <Column fullHeight gap={ 1 } style={ { minHeight: 0 } }>
            <Base className="d-flex align-items-center justify-content-between">
                <Text small fontWeight="bold">Actividad en directo</Text>
                <Text small>{ eventos.length } eventos</Text>
            </Base>

            <Column
                fullHeight
                gap={ 1 }
                overflow="auto"
                style={ { minHeight: 0, paddingRight: '3px' } }>
                { eventos.length === 0 &&
                    <Base
                        className="p-2 rounded text-center"
                        style={ { background: 'rgba(255,255,255,.28)' } }>
                        <Text small>Sin actividad reciente.</Text>
                    </Base> }

                { eventos.map(evento =>
                    <Base
                        key={ evento.id }
                        className="d-flex align-items-start px-2 py-1 rounded"
                        style={ {
                            gap: '7px',
                            background: 'rgba(255,255,255,.30)',
                            border: '1px solid rgba(120,134,148,.24)'
                        } }>
                        <Base style={ { minWidth: '48px' } }>
                            <Text small fontWeight="bold">{ evento.hora }</Text>
                        </Base>

                        <Base
                            className="px-1 rounded"
                            style={ {
                                minWidth: '48px',
                                textAlign: 'center',
                                background: 'rgba(75,91,111,.12)'
                            } }>
                            <Text small fontWeight="bold">{ etiqueta(evento.tipo) }</Text>
                        </Base>

                        <Text small>{ evento.mensaje }</Text>
                    </Base>
                ) }
            </Column>
        </Column>
    );
};