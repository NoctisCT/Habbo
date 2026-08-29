import {
    EstadisticasSubastasEvent,
    IDetalleFurniEstadisticas,
    IPuntoPrecioSubasta,
    IResumenFurniEstadisticas
} from '@nitrots/nitro-renderer';
import { FC, useEffect, useMemo, useState } from 'react';
import { Base, Column, Grid, Text } from '../../common';
import { ObtenerEstadisticasSubastas } from '../../api';
import { useMessageEvent } from '../../hooks';
import { SubastasFurniImageSeguro } from './SubastasFurniImageSeguro';

interface EstadisticasSubastasViewProps
{
    visible: boolean;
}

const formatearNumero = (valor: number) =>
{
    return Math.round(valor || 0).toLocaleString();
};

const FurniIcono: FC<{
    spriteId: number;
    tipo: string;
    grande?: boolean;
}> = props =>
{
    const { spriteId, tipo, grande = false } = props;

    return (
        <Base
            className="d-flex align-items-center justify-content-center"
            style={ {
                width: grande ? '58px' : '42px',
                minWidth: grande ? '58px' : '42px',
                height: grande ? '50px' : '40px',
                minHeight: grande ? '50px' : '40px',
                overflow: 'hidden'
            } }>
            <Base
                className="d-flex align-items-center justify-content-center"
                style={ {
                    transform: grande ? 'scale(.92)' : 'scale(.88)',
                    transformOrigin: 'center center'
                } }>
                <SubastasFurniImageSeguro
                    productType={ (tipo || '').toLowerCase() === 'i' ? 'i' : 's' }
                    productClassId={ spriteId }
                />
            </Base>
        </Base>
    );
};

const GraficoPrecio: FC<{ puntos: IPuntoPrecioSubasta[] }> = props =>
{
    const puntos = props.puntos || [];

    const datos = useMemo(() =>
    {
        if(!puntos.length) return null;

        const valores = puntos.map(punto => punto.precioUnidad);
        const minimo = Math.min(...valores);
        const maximo = Math.max(...valores);
        const rango = Math.max(1, maximo - minimo);

        const ancho = 430;
        const alto = 118;
        const margenX = 18;
        const margenY = 14;
        const anchoUtil = ancho - (margenX * 2);
        const altoUtil = alto - (margenY * 2);

        const coords = puntos.map((punto, indice) =>
        {
            const x = puntos.length === 1
                ? ancho / 2
                : margenX + ((indice / (puntos.length - 1)) * anchoUtil);

            const y = margenY + altoUtil -
                (((punto.precioUnidad - minimo) / rango) * altoUtil);

            return { x, y, punto };
        });

        return {
            linea: coords.map(coord => `${ coord.x },${ coord.y }`).join(' '),
            coords,
            ancho,
            alto
        };
    }, [ puntos ]);

    if(!datos)
    {
        return (
            <Base
                className="d-flex align-items-center justify-content-center rounded"
                style={ {
                    height: '126px',
                    minHeight: '126px',
                    background: 'rgba(255,255,255,.24)'
                } }>
                <Text small>{ 'Todav\u00eda no hay ventas suficientes para dibujar la evoluci\u00f3n.' }</Text>
            </Base>
        );
    }

    return (
        <Column gap={ 0 }>
            <Text small fontWeight="bold">{ 'Precio hist\u00f3rico por unidad' }</Text>

            <svg
                viewBox={ `0 0 ${ datos.ancho } ${ datos.alto }` }
                style={ {
                    width: '100%',
                    height: '126px',
                    background: 'rgba(255,255,255,.23)',
                    borderRadius: '5px'
                } }>
                { [ 0.25, 0.5, 0.75 ].map(nivel =>
                    <line
                        key={ nivel }
                        x1="12"
                        y1={ datos.alto * nivel }
                        x2={ datos.ancho - 12 }
                        y2={ datos.alto * nivel }
                        stroke="rgba(65,80,95,.18)"
                        strokeWidth="1"
                    />
                ) }

                <polyline
                    points={ datos.linea }
                    fill="none"
                    stroke="rgba(48,91,126,.95)"
                    strokeWidth="3"
                    strokeLinejoin="round"
                    strokeLinecap="round"
                />

                { datos.coords.map((coord, indice) =>
                    <g key={ `${ coord.punto.fecha }-${ indice }` }>
                        <circle
                            cx={ coord.x }
                            cy={ coord.y }
                            r="3.5"
                            fill="rgba(48,91,126,1)"
                        />

                        { (indice === 0 ||
                           indice === datos.coords.length - 1 ||
                           indice === Math.floor(datos.coords.length / 2)) &&
                            <text
                                x={ coord.x }
                                y={ datos.alto - 3 }
                                fontSize="8"
                                textAnchor="middle"
                                fill="rgba(45,55,65,.85)">
                                { coord.punto.fecha }
                            </text> }
                    </g>
                ) }
            </svg>
        </Column>
    );
};

const GraficoRadar: FC<{ detalle: IDetalleFurniEstadisticas }> = props =>
{
    const detalle = props.detalle;

    if(!detalle) return null;

    const valores = [
        detalle.radarValor,
        detalle.radarDemanda,
        detalle.radarVolumen,
        detalle.radarActividad,
        detalle.radarExito
    ];

    const etiquetas = [ 'Valor', 'Demanda', 'Volumen', 'Actividad', 'Exito' ];

    const ancho = 190;
    const alto = 170;
    const centroX = 95;
    const centroY = 82;
    const radio = 54;

    const punto = (indice: number, porcentaje: number) =>
    {
        const angulo = ((Math.PI * 2) / 5) * indice - (Math.PI / 2);
        const distancia = radio * (porcentaje / 100);

        return {
            x: centroX + (Math.cos(angulo) * distancia),
            y: centroY + (Math.sin(angulo) * distancia)
        };
    };

    const poligono = valores
        .map((valor, indice) =>
        {
            const p = punto(indice, Math.max(0, Math.min(100, valor)));
            return `${ p.x },${ p.y }`;
        })
        .join(' ');

    return (
        <Column gap={ 0 } alignItems="center">
            <Text small fontWeight="bold">Perfil de mercado</Text>

            <svg
                viewBox={ `0 0 ${ ancho } ${ alto }` }
                style={ {
                    width: '190px',
                    height: '170px',
                    overflow: 'visible'
                } }>
                { [ 25, 50, 75, 100 ].map(nivel =>
                    <polygon
                        key={ nivel }
                        points={ [ 0, 1, 2, 3, 4 ].map(indice =>
                        {
                            const p = punto(indice, nivel);
                            return `${ p.x },${ p.y }`;
                        }).join(' ') }
                        fill="none"
                        stroke="rgba(65,80,95,.18)"
                        strokeWidth="1"
                    />
                ) }

                { [ 0, 1, 2, 3, 4 ].map(indice =>
                {
                    const p = punto(indice, 100);

                    return (
                        <line
                            key={ indice }
                            x1={ centroX }
                            y1={ centroY }
                            x2={ p.x }
                            y2={ p.y }
                            stroke="rgba(65,80,95,.16)"
                            strokeWidth="1"
                        />
                    );
                }) }

                <polygon
                    points={ poligono }
                    fill="rgba(48,91,126,.20)"
                    stroke="rgba(48,91,126,.95)"
                    strokeWidth="2"
                />

                { etiquetas.map((etiqueta, indice) =>
                {
                    const borde = punto(indice, 100);

                    const posiciones = [
                        { x: borde.x,      y: borde.y - 9,  anchor: 'middle' },
                        { x: borde.x + 9,  y: borde.y + 4,  anchor: 'start' },
                        { x: borde.x + 6,  y: borde.y + 14, anchor: 'middle' },
                        { x: borde.x - 6,  y: borde.y + 14, anchor: 'middle' },
                        { x: borde.x - 9,  y: borde.y + 4,  anchor: 'end' }
                    ];

                    const posicion = posiciones[indice];

                    return (
                        <text
                            key={ etiqueta }
                            x={ posicion.x }
                            y={ posicion.y }
                            fontSize="10"
                            fontWeight="600"
                            textAnchor={ posicion.anchor as 'start' | 'middle' | 'end' }
                            fill="rgba(45,55,65,.92)">
                            { etiqueta }
                        </text>
                    );
                }) }
            </svg>
        </Column>
    );
};

export const EstadisticasSubastasView: FC<EstadisticasSubastasViewProps> = props =>
{
    const { visible = false } = props;

    const [ totalVendidas, setTotalVendidas ] = useState(0);
    const [ volumenGlobal, setVolumenGlobal ] = useState(0);
    const [ precioMedioGlobal, setPrecioMedioGlobal ] = useState(0);
    const [ tasaVentaGlobal, setTasaVentaGlobal ] = useState(0);
    const [ recordPrecio, setRecordPrecio ] = useState(0);
    const [ recordNombre, setRecordNombre ] = useState('');
    const [ top, setTop ] = useState<IResumenFurniEstadisticas[]>([]);
    const [ detalle, setDetalle ] = useState<IDetalleFurniEstadisticas>(null);
    const [ busqueda, setBusqueda ] = useState('');
    const mercadoActual = useMemo(() =>
    {
        const historial = detalle?.historial || [];

        if(!historial.length)
        {
            return {
                variacion: null as number | null,
                tendencia: 'Sin datos',
                tendenciaSimbolo: '',
                ultimas: []
            };
        }

        const ultimo = historial[historial.length - 1];
        const anterior = historial.length > 1
            ? historial[historial.length - 2]
            : null;

        const variacion = anterior && anterior.precioUnidad > 0
            ? ((ultimo.precioUnidad - anterior.precioUnidad) / anterior.precioUnidad) * 100
            : null;

        const recientes = historial.slice(-Math.min(5, historial.length));
        const primeroTendencia = recientes[0];
        const ultimoTendencia = recientes[recientes.length - 1];

        const tendenciaPct = recientes.length > 1 && primeroTendencia.precioUnidad > 0
            ? ((ultimoTendencia.precioUnidad - primeroTendencia.precioUnidad) /
                primeroTendencia.precioUnidad) * 100
            : 0;

        const tendencia = recientes.length < 2
            ? 'Sin datos'
            : (tendenciaPct > 3
                ? 'Al alza'
                : (tendenciaPct < -3 ? 'A la baja' : 'Estable'));

        const tendenciaSimbolo = '';

        return {
            variacion,
            tendencia,
            tendenciaSimbolo,
            ultimas: historial.slice(-3).reverse()
        };
    }, [ detalle ]);

    useMessageEvent(EstadisticasSubastasEvent, (event: EstadisticasSubastasEvent) =>
    {
        const parser = event.getParser();

        setTotalVendidas(parser.totalVendidas);
        setVolumenGlobal(parser.volumenGlobal);
        setPrecioMedioGlobal(parser.precioMedioGlobal);
        setTasaVentaGlobal(parser.tasaVentaGlobal);
        setRecordPrecio(parser.recordPrecio);
        setRecordNombre(parser.recordNombre);
        setTop(parser.top);
        setDetalle(parser.detalle);
    });

    useEffect(() =>
    {
        if(!visible) return;

        ObtenerEstadisticasSubastas(0, '');
    }, [ visible ]);

    useEffect(() =>
    {
        if(!visible) return;

        const temporizador = window.setTimeout(() =>
        {
            ObtenerEstadisticasSubastas(detalle?.furniId || 0, busqueda);
        }, 250);

        return () => window.clearTimeout(temporizador);
    }, [ busqueda ]);

    if(!visible) return null;

    return (
        <Grid fullHeight gap={ 2 } style={ { minHeight: 0 } }>
            <Column
                size={ 4 }
                gap={ 1 }
                style={ {
                    minHeight: 0,
                    borderRight: '1px solid rgba(90,105,120,.18)',
                    paddingRight: '4px'
                } }>
                <Text small fontWeight="bold">Buscar en el mercado</Text>

                <input
                    className="form-control form-control-sm"
                    type="text"
                    value={ busqueda }
                    placeholder="Nombre del furni..."
                    onChange={ event => setBusqueda(event.target.value) }
                />

                <Column gap={ 1 } overflow="auto" style={ { minHeight: 0 } }>
                    { top.length === 0 &&
                        <Base
                            className="p-1 rounded"
                            style={ { background: 'rgba(255,255,255,.20)' } }>
                            <Text small>
                                { busqueda
                                    ? 'No hay ventas de furnis que coincidan.'
                                    : 'No hay ventas para mostrar.' }
                            </Text>
                        </Base> }

                    { top.map(furni =>
                        <Base
                            key={ furni.furniId }
                            pointer
                            className="px-1 rounded"
                            style={ {
                                height: '44px',
                                minHeight: '44px',
                                maxHeight: '44px',
                                overflow: 'hidden',
                                background: detalle?.furniId === furni.furniId
                                    ? 'rgba(48,91,126,.18)'
                                    : 'rgba(255,255,255,.23)',
                                border: detalle?.furniId === furni.furniId
                                    ? '1px solid rgba(48,91,126,.45)'
                                    : '1px solid rgba(90,105,120,.18)'
                            } }
                            onClick={ () => ObtenerEstadisticasSubastas(furni.furniId, busqueda) }>
                            <Base className="d-flex align-items-center" style={ { gap: '4px', height: '100%' } }>
                                <FurniIcono
                                    spriteId={ furni.spriteId }
                                    tipo={ furni.tipo }
                                />

                                <Column
                                    gap={ 0 }
                                    style={ {
                                        minWidth: 0,
                                        overflow: 'hidden',
                                        flex: '1 1 auto'
                                    } }>
                                    <Text
                                        small
                                        fontWeight="bold"
                                        style={ {
                                            whiteSpace: 'nowrap',
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis'
                                        } }>
                                        { furni.nombre }
                                    </Text>

                                    <Text
                                        small
                                        style={ {
                                            whiteSpace: 'nowrap',
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis'
                                        } }>
                                        { `${ furni.ventas } ventas - ${ formatearNumero(furni.precioMedioUnidad) }/u` }
                                    </Text>
                                </Column>
                            </Base>
                        </Base>
                    ) }
                </Column>
            </Column>

            <Column size={ 8 } gap={ 1 } overflow="auto" style={ { minHeight: 0 } }>
                <Text small fontWeight="bold">Estadisticas totales de las subastas</Text>

                <Base
                    className="d-flex"
                    style={ {
                        gap: '4px',
                        height: '42px',
                        minHeight: '42px',
                        maxHeight: '42px'
                    } }>
                    <Column
                        gap={ 0 }
                        className="p-1 rounded"
                        style={ { flex: '1 1 0', background: 'rgba(255,255,255,.25)' } }>
                        <Text small>Ventas</Text>
                        <Text fontWeight="bold">{ formatearNumero(totalVendidas) }</Text>
                    </Column>

                    <Column
                        gap={ 0 }
                        className="p-1 rounded"
                        style={ { flex: '1 1 0', background: 'rgba(255,255,255,.25)' } }>
                        <Text small>Volumen total</Text>
                        <Text fontWeight="bold">{ formatearNumero(volumenGlobal) }</Text>
                    </Column>

                    <Column
                        gap={ 0 }
                        className="p-1 rounded"
                        style={ { flex: '1 1 0', background: 'rgba(255,255,255,.25)' } }>
                        <Text small>Precio medio/u</Text>
                        <Text fontWeight="bold">{ formatearNumero(precioMedioGlobal) }</Text>
                    </Column>

                    <Column
                        gap={ 0 }
                        className="p-1 rounded"
                        style={ { flex: '1 1 0', background: 'rgba(255,255,255,.25)' } }>
                        <Text small>Tasa de venta</Text>
                        <Text fontWeight="bold">{ tasaVentaGlobal }%</Text>
                    </Column>
                </Base>

                { !detalle &&
                    <Base className="p-3 rounded" style={ { background: 'rgba(255,255,255,.24)' } }>
                        <Text>{ 'Todav\u00eda no hay suficientes ventas para generar estad\u00edsticas.' }</Text>
                    </Base> }

                { detalle &&
                    <>
                        <Base
                            className="d-flex align-items-center px-1 rounded"
                            style={ {
                                height: '54px',
                                minHeight: '54px',
                                gap: '6px',
                                background: 'rgba(255,255,255,.24)'
                            } }>
                            <FurniIcono
                                spriteId={ detalle.spriteId }
                                tipo={ detalle.tipo }
                                grande
                            />

                            <Column
                                gap={ 0 }
                                style={ {
                                    minWidth: 0,
                                    flex: '1 1 auto'
                                } }>
                                <Text
                                    fontSize={ 4 }
                                    fontWeight="bold"
                                    style={ {
                                        whiteSpace: 'nowrap',
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis'
                                    } }>
                                    { detalle.nombre }
                                </Text>

                                <Text small>
                                    { `${ detalle.ventas } ventas - ${ detalle.unidadesVendidas } unidades vendidas` }
                                </Text>
                            </Column>
                        </Base>

                        <Base
                            className="d-flex"
                            style={ {
                                gap: '4px',
                                height: '40px',
                                minHeight: '40px'
                            } }>
                            { [
                                [ 'Media/u', formatearNumero(detalle.precioMedioUnidad) ],
                                [ 'Ultimo/u', formatearNumero(detalle.ultimoPrecioUnidad) ],
                                [ 'Min/u', formatearNumero(detalle.precioMinimoUnidad) ],
                                [ 'Max/u', formatearNumero(detalle.precioMaximoUnidad) ],
                                [ 'Exito', `${ detalle.tasaVenta }%` ],
                                [ 'Pujas/venta', detalle.pujasMedia.toFixed(1) ]
                            ].map(([ etiqueta, valor ]) =>
                                <Column
                                    key={ etiqueta }
                                    gap={ 0 }
                                    className="px-1 rounded"
                                    style={ {
                                        flex: '1 1 0',
                                        minWidth: 0,
                                        background: 'rgba(255,255,255,.14)'
                                    } }>
                                    <Text small>{ etiqueta }</Text>
                                    <Text fontWeight="bold">{ valor }</Text>
                                </Column>
                            ) }
                        </Base>

                        <Base
                            className="d-flex"
                            style={ {
                                gap: '4px',
                                height: '48px',
                                minHeight: '48px',
                                maxHeight: '48px'
                            } }>
                            <Column
                                gap={ 0 }
                                className="px-1 rounded"
                                style={ {
                                    width: '92px',
                                    minWidth: '92px',
                                    background: 'rgba(255,255,255,.16)'
                                } }>
                                <Text small>Variación</Text>
                                <Text fontWeight="bold">
                                    { mercadoActual.variacion === null
                                        ? '-'
                                        : `${ mercadoActual.variacion > 0 ? '+' : '' }${ mercadoActual.variacion.toFixed(1) }%` }
                                </Text>
                            </Column>

                            <Column
                                gap={ 0 }
                                className="px-1 rounded"
                                style={ {
                                    width: '92px',
                                    minWidth: '92px',
                                    background: 'rgba(255,255,255,.16)'
                                } }>
                                <Text small>Tendencia</Text>
                                <Text fontWeight="bold">
                                    { `${ mercadoActual.tendenciaSimbolo } ${ mercadoActual.tendencia }` }
                                </Text>
                            </Column>

                            <Column
                                gap={ 0 }
                                className="px-1 rounded"
                                style={ {
                                    flex: '1 1 auto',
                                    minWidth: 0,
                                    background: 'rgba(255,255,255,.16)',
                                    overflow: 'hidden'
                                } }>
                                <Text small>Últimas ventas</Text>

                                <Base
                                    className="d-flex align-items-center"
                                    style={ {
                                        gap: '4px',
                                        minWidth: 0,
                                        overflow: 'hidden'
                                    } }>
                                    { mercadoActual.ultimas.length
                                        ? mercadoActual.ultimas.map((venta, indice) =>
                                            <Base
                                                key={ `${ venta.fecha }-${ indice }` }
                                                className="px-1 rounded"
                                                style={ {
                                                    background: 'rgba(48,91,126,.10)',
                                                    whiteSpace: 'nowrap'
                                                } }>
                                                <Text small fontWeight="bold">
                                                    { `${ venta.fecha } · ${ formatearNumero(venta.precioUnidad) }/u` }
                                                </Text>
                                            </Base>)
                                        : <Text small>Sin ventas</Text> }
                                </Base>
                            </Column>
                        </Base>
                        <GraficoPrecio puntos={ detalle.historial } />

                        <Base
                            className="d-flex"
                            style={ {
                                gap: '8px',
                                alignItems: 'stretch'
                            } }>
                            <Base
                                className="d-flex align-items-center justify-content-center rounded"
                                style={ {
                                    width: '198px',
                                    minWidth: '198px',
                                    minHeight: '188px',
                                    background: 'rgba(255,255,255,.18)'
                                } }>
                                <GraficoRadar detalle={ detalle } />
                            </Base>

                            <Column
                                gap={ 1 }
                                style={ {
                                    flex: '1 1 auto',
                                    minWidth: 0
                                } }>
                                <Column
                                    gap={ 0 }
                                    className="p-2 rounded"
                                    style={ {
                                        minHeight: '74px',
                                        background: 'rgba(255,255,255,.20)'
                                    } }>
                                    <Text small>Volumen vendido de este furni</Text>
                                    <Text
                                        fontSize={ 4 }
                                        fontWeight="bold">
                                        { formatearNumero(detalle.volumenCreditos) }
                                    </Text>
                                    <Text small>creditos</Text>
                                </Column>

                                { recordPrecio > 0 &&
                                    <Column
                                        gap={ 0 }
                                        className="p-2 rounded"
                                        style={ {
                                            minHeight: '92px',
                                            background: 'rgba(255,255,255,.20)',
                                            overflow: 'hidden'
                                        } }>
                                        <Text small>Record de venta del mercado</Text>
                                        <Text
                                            fontWeight="bold"
                                            style={ {
                                                whiteSpace: 'normal',
                                                wordBreak: 'break-word',
                                                lineHeight: '1.15'
                                            } }>
                                            { recordNombre }
                                        </Text>
                                        <Text small fontWeight="bold">
                                            { `${ formatearNumero(recordPrecio) } creditos` }
                                        </Text>
                                    </Column> }
                            </Column>
                        </Base>
                    </> }
            </Column>
        </Grid>
    );
};