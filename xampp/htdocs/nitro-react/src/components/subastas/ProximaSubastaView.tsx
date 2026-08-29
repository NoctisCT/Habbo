import { ProximaSubastaEvent } from '@nitrots/nitro-renderer';
import { FC, useEffect, useMemo, useState } from 'react';
import { ObtenerProximaSubasta } from '../../api';
import { Base, Column, Text } from '../../common';
import { useMessageEvent } from '../../hooks';
import { SubastasFurniImageSeguro } from './SubastasFurniImageSeguro';

interface ProximaSubastaViewProps
{
    visible: boolean;
}

const formatearInicio = (valor: string): string =>
{
    if(!valor) return '';

    const limpio = valor.trim();

    // Formato nuevo del servidor: dd/MM/yyyy HH:mm
    const europeo = limpio.match(
        /^(\d{2})\/(\d{2})\/(\d{4})\s+(\d{2}):(\d{2})$/
    );

    if(europeo)
    {
        const fecha = new Date(
            Number(europeo[3]),
            Number(europeo[2]) - 1,
            Number(europeo[1]),
            Number(europeo[4]),
            Number(europeo[5]),
            0
        );

        if(!Number.isNaN(fecha.getTime()))
        {
            const diaSemana = new Intl.DateTimeFormat('es-ES', {
                weekday: 'long'
            }).format(fecha);

            return `${ diaSemana.charAt(0).toUpperCase() + diaSemana.slice(1) } ${ europeo[1] }/${ europeo[2] }/${ europeo[3] } · ${ europeo[4] }:${ europeo[5] }`;
        }
    }

    // Compatibilidad con respuestas antiguas: yyyy-MM-dd HH:mm:ss(.0)
    const antiguo = limpio.match(
        /^(\d{4})-(\d{2})-(\d{2})[ T](\d{2}):(\d{2})(?::\d{2})?(?:\.\d+)?$/
    );

    if(antiguo)
    {
        const fecha = new Date(
            Number(antiguo[1]),
            Number(antiguo[2]) - 1,
            Number(antiguo[3]),
            Number(antiguo[4]),
            Number(antiguo[5]),
            0
        );

        if(!Number.isNaN(fecha.getTime()))
        {
            const diaSemana = new Intl.DateTimeFormat('es-ES', {
                weekday: 'long'
            }).format(fecha);

            return `${ diaSemana.charAt(0).toUpperCase() + diaSemana.slice(1) } ${ antiguo[3] }/${ antiguo[2] }/${ antiguo[1] } · ${ antiguo[4] }:${ antiguo[5] }`;
        }
    }

    return limpio;
};

export const ProximaSubastaView: FC<ProximaSubastaViewProps> = props =>
{
    const { visible = false } = props;

    const [ cargando, setCargando ] = useState(true);
    const [ hay, setHay ] = useState(false);
    const [ objeto, setObjeto ] = useState('');
    const [ spriteId, setSpriteId ] = useState(0);
    const [ tipo, setTipo ] = useState('s');
    const [ vendedor, setVendedor ] = useState('');
    const [ precioInicial, setPrecioInicial ] = useState(0);
    const [ cantidad, setCantidad ] = useState(0);
    const [ posicionCola, setPosicionCola ] = useState(0);
    const [ sesion, setSesion ] = useState('');
    const [ inicioSesion, setInicioSesion ] = useState('');

    const inicioFormateado = useMemo(
        () => formatearInicio(inicioSesion),
        [ inicioSesion ]
    );

    useMessageEvent(ProximaSubastaEvent, (event: ProximaSubastaEvent) =>
    {
        const parser = event.getParser();

        setHay(parser.hay);
        setObjeto(parser.objeto);
        setSpriteId(parser.spriteId);
        setTipo(parser.tipo);
        setVendedor(parser.vendedor);
        setPrecioInicial(parser.precioInicial);
        setCantidad(parser.cantidad);
        setPosicionCola(parser.posicionCola);
        setSesion(parser.sesion);
        setInicioSesion(parser.inicioSesion);
        setCargando(false);
    });

    useEffect(() =>
    {
        if(!visible) return;

        setCargando(true);
        ObtenerProximaSubasta();

        const intervalo = window.setInterval(() =>
        {
            ObtenerProximaSubasta();
        }, 2000);

        return () => window.clearInterval(intervalo);
    }, [ visible ]);

    if(!visible) return null;

    if(cargando)
    {
        return (
            <Base
                className="d-flex align-items-center justify-content-center rounded"
                style={ {
                    height: '146px',
                    minHeight: '146px',
                    background: 'rgba(210,218,226,.48)',
                    border: '1px solid rgba(110,125,140,.24)'
                } }>
                <Text small>Consultando la próxima subasta...</Text>
            </Base>
        );
    }

    if(!hay)
    {
        return (
            <Base
                className="d-flex align-items-center justify-content-center rounded"
                style={ {
                    height: '82px',
                    minHeight: '82px',
                    background: 'rgba(210,218,226,.48)',
                    border: '1px solid rgba(110,125,140,.24)'
                } }>
                <Column gap={ 0 } alignItems="center">
                    <Text fontWeight="bold">No hay subastas en cola</Text>
                    <Text small>La próxima aparecerá aquí automáticamente.</Text>
                </Column>
            </Base>
        );
    }

    return (
        <Base
            className="rounded"
            style={ {
                height: '156px',
                minHeight: '156px',
                maxHeight: '156px',
                overflow: 'hidden',
                background: 'rgba(210,218,226,.52)',
                border: '1px solid rgba(110,125,140,.28)'
            } }>
            <Base
                className="d-flex align-items-center justify-content-between px-2"
                style={ {
                    height: '30px',
                    minHeight: '30px',
                    borderBottom: '1px solid rgba(110,125,140,.22)',
                    background: 'rgba(255,255,255,.20)'
                } }>
                <Text fontWeight="bold">Próxima subasta</Text>

                <Base
                    className="px-2 rounded"
                    style={ {
                        background: 'rgba(48,91,126,.15)',
                        border: '1px solid rgba(48,91,126,.22)'
                    } }>
                    <Text small fontWeight="bold">Posición #{ posicionCola }</Text>
                </Base>
            </Base>

            <Base
                className="d-flex"
                style={ {
                    height: '126px',
                    minHeight: '126px',
                    padding: '7px 8px',
                    gap: '8px'
                } }>
                <Column
                    gap={ 1 }
                    style={ {
                        flex: '1 1 auto',
                        minWidth: 0,
                        justifyContent: 'center'
                    } }>
                    <Column gap={ 0 }>
                        <Text
                            fontSize={ 5 }
                            fontWeight="bold"
                            style={ {
                                whiteSpace: 'nowrap',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis'
                            } }>
                            { objeto }{ cantidad > 1 ? ` x${ cantidad }` : '' }
                        </Text>
                        <Text small>Siguiente en la cola</Text>
                    </Column>

                    <Base
                        className="d-flex"
                        style={ {
                            gap: '5px',
                            height: '36px',
                            minHeight: '36px'
                        } }>
                        <Column
                            gap={ 0 }
                            className="px-2 rounded"
                            style={ {
                                flex: '1 1 0',
                                minWidth: 0,
                                justifyContent: 'center',
                                background: 'rgba(255,255,255,.28)'
                            } }>
                            <Text small>Vendedor</Text>
                            <Text
                                fontWeight="bold"
                                style={ {
                                    whiteSpace: 'nowrap',
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis'
                                } }>
                                { vendedor || '-' }
                            </Text>
                        </Column>

                        <Column
                            gap={ 0 }
                            className="px-2 rounded"
                            style={ {
                                flex: '1 1 0',
                                minWidth: 0,
                                justifyContent: 'center',
                                background: 'rgba(255,255,255,.28)'
                            } }>
                            <Text small>Precio inicial</Text>
                            <Text fontWeight="bold">{ precioInicial } créditos</Text>
                        </Column>
                    </Base>

                    <Base
                        className="d-flex align-items-center px-2 rounded"
                        style={ {
                            height: '34px',
                            minHeight: '34px',
                            gap: '8px',
                            background: 'rgba(255,255,255,.18)'
                        } }>
                        <Text small fontWeight="bold">
                            { sesion || 'Próxima sesión' }
                        </Text>

                        { inicioFormateado &&
                            <Text
                                small
                                style={ {
                                    marginLeft: 'auto',
                                    whiteSpace: 'nowrap'
                                } }>
                                Comienza: <b>{ inicioFormateado }</b>
                            </Text> }
                    </Base>
                </Column>

                <Base
                    className="d-flex align-items-center justify-content-center rounded"
                    style={ {
                        width: '148px',
                        minWidth: '148px',
                        height: '112px',
                        minHeight: '112px',
                        maxHeight: '112px',
                        overflow: 'hidden',
                        background: 'rgba(224,230,235,.72)',
                        border: '1px solid rgba(110,125,140,.24)'
                    } }>
                    <SubastasFurniImageSeguro
                        productType={ (tipo || '').toLowerCase() === 'i' ? 'i' : 's' }
                        productClassId={ spriteId }
                        scale={ 1 }
                        style={ {
                            width: '104px',
                            height: '104px'
                        } }
                    />
                </Base>
            </Base>
        </Base>
    );
};