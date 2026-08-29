import { HistorialSubastasEvent, IHistorialSubasta } from '@nitrots/nitro-renderer';
import { FC, useEffect, useMemo, useState } from 'react';
import { ObtenerHistorialSubastas } from '../../api';
import { Base, Column, Text } from '../../common';
import { useMessageEvent } from '../../hooks';
import { SubastasFurniImageSeguro } from './SubastasFurniImageSeguro';

interface HistorialSubastasViewProps
{
    visible: boolean;
}

const precioUnidad = (precio: number, cantidad: number): string =>
{
    const unidades = Math.max(1, cantidad || 1);
    const valor = precio / unidades;

    return Number.isInteger(valor)
        ? valor.toLocaleString('es-ES')
        : valor.toLocaleString('es-ES', { maximumFractionDigits: 2 });
};

export const HistorialSubastasView: FC<HistorialSubastasViewProps> = props =>
{
    const { visible = false } = props;

    const [ pagina, setPagina ] = useState(1);
    const [ paginas, setPaginas ] = useState(1);
    const [ total, setTotal ] = useState(0);
    const [ subastas, setSubastas ] = useState<IHistorialSubasta[]>([]);
    const [ busqueda, setBusqueda ] = useState('');
    const [ filtro, setFiltro ] = useState('todos');
    const [ cargando, setCargando ] = useState(false);

    useMessageEvent(HistorialSubastasEvent, (event: HistorialSubastasEvent) =>
    {
        const parser = event.getParser();

        setPagina(parser.pagina);
        setPaginas(parser.paginas);
        setTotal(parser.total);
        setSubastas(parser.subastas);
        setCargando(false);
    });

    useEffect(() =>
    {
        if(!visible) return;

        setPagina(1);
    }, [ busqueda, filtro, visible ]);

    useEffect(() =>
    {
        if(!visible) return;

        const temporizador = window.setTimeout(() =>
        {
            setCargando(true);
            ObtenerHistorialSubastas(pagina, busqueda.trim(), filtro);
        }, 250);

        return () => window.clearTimeout(temporizador);
    }, [ visible, pagina, busqueda, filtro ]);

    const textoResultados = useMemo(() =>
    {
        if(cargando) return 'Actualizando...';
        if(total === 1) return '1 resultado';
        return `${ total } resultados`;
    }, [ cargando, total ]);

    if(!visible) return null;

    return (
        <Column fullHeight gap={ 1 } style={ { minHeight: 0 } }>
            <Base
                className="d-flex align-items-center"
                style={ {
                    gap: '5px',
                    height: '34px',
                    minHeight: '34px'
                } }>
                <input
                    className="form-control form-control-sm"
                    value={ busqueda }
                    placeholder="Buscar furni..."
                    onChange={ event => setBusqueda(event.target.value) }
                    style={ { flex: '1 1 auto', minWidth: 0 } }
                />

                <select
                    className="form-select form-select-sm"
                    value={ filtro }
                    onChange={ event => setFiltro(event.target.value) }
                    style={ { width: '128px', minWidth: '128px' } }>
                    <option value="todos">Todos</option>
                    <option value="vendidas">Vendidas</option>
                    <option value="sin_pujas">Sin pujas</option>
                </select>

                <Text
                    small
                    style={ {
                        width: '74px',
                        minWidth: '74px',
                        textAlign: 'right'
                    } }>
                    { textoResultados }
                </Text>
            </Base>

            <Column
                gap={ 1 }
                overflow="auto"
                style={ {
                    flex: '1 1 auto',
                    minHeight: 0
                } }>
                { !cargando && subastas.length === 0 &&
                    <Base
                        className="d-flex align-items-center justify-content-center rounded"
                        style={ {
                            height: '92px',
                            minHeight: '92px',
                            background: 'rgba(210,218,226,.38)'
                        } }>
                        <Text small>
                            { busqueda || filtro !== 'todos'
                                ? 'No hay subastas que coincidan.'
                                : 'Todavía no hay subastas finalizadas.' }
                        </Text>
                    </Base> }

                { subastas.map(subasta =>
                {
                    const vendida = subasta.estado === 'vendida';
                    const precio = vendida ? subasta.precioFinal : subasta.precioInicial;

                    return (
                        <Base
                            key={ subasta.id }
                            className="d-flex align-items-center px-2 rounded"
                            style={ {
                                gap: '8px',
                                height: '62px',
                                minHeight: '62px',
                                maxHeight: '62px',
                                overflow: 'hidden',
                                background: 'rgba(210,218,226,.48)',
                                border: '1px solid rgba(110,125,140,.18)'
                            } }>
                            <Base
                                className="d-flex align-items-center justify-content-center"
                                style={ {
                                    width: '54px',
                                    minWidth: '54px',
                                    height: '54px',
                                    overflow: 'hidden'
                                } }>
                                <SubastasFurniImageSeguro
                                    productType={ (subasta.tipo || '').toLowerCase() === 'i' ? 'i' : 's' }
                                    productClassId={ subasta.spriteId }
                                    scale={ 0.72 }
                                    style={ {
                                        width: '50px',
                                        height: '50px'
                                    } }
                                />
                            </Base>

                            <Column
                                gap={ 0 }
                                style={ {
                                    flex: '1 1 auto',
                                    minWidth: 0
                                } }>
                                <Base className="d-flex align-items-center" style={ { gap: '6px' } }>
                                    <Text
                                        fontWeight="bold"
                                        style={ {
                                            whiteSpace: 'nowrap',
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis'
                                        } }>
                                        { subasta.nombre }{ subasta.cantidad > 1 ? ` x${ subasta.cantidad }` : '' }
                                    </Text>

                                    <Base
                                        className="px-1 rounded"
                                        style={ {
                                            flex: '0 0 auto',
                                            background: vendida
                                                ? 'rgba(55,120,70,.16)'
                                                : 'rgba(115,115,115,.16)'
                                        } }>
                                        <Text small fontWeight="bold">
                                            { vendida ? 'VENDIDA' : 'SIN PUJAS' }
                                        </Text>
                                    </Base>
                                </Base>

                                <Text small>
                                    Vendedor: <b>{ subasta.vendedor }</b>
                                    { vendida && subasta.ganador
                                        ? <> · Comprador: <b>{ subasta.ganador }</b></>
                                        : null }
                                </Text>

                                <Text small>
                                    { vendida
                                        ? <>Precio final: <b>{ subasta.precioFinal } créditos</b></>
                                        : <>Precio inicial: <b>{ subasta.precioInicial } créditos</b></> }

                                    { subasta.cantidad > 1 &&
                                        <> · <b>{ precioUnidad(precio, subasta.cantidad) } créditos/u</b></> }
                                </Text>
                            </Column>

                            <Column
                                gap={ 0 }
                                alignItems="end"
                                style={ {
                                    width: '108px',
                                    minWidth: '108px'
                                } }>
                                <Text small>{ subasta.fecha || '-' }</Text>
                                <Text small>#{ subasta.id }</Text>
                            </Column>
                        </Base>
                    );
                }) }
            </Column>

            <Base
                className="d-flex align-items-center justify-content-center"
                style={ {
                    gap: '8px',
                    height: '30px',
                    minHeight: '30px'
                } }>
                <button
                    className="btn btn-sm btn-secondary"
                    disabled={ pagina <= 1 || cargando }
                    onClick={ () => setPagina(valor => Math.max(1, valor - 1)) }>
                    Anterior
                </button>

                <Text small fontWeight="bold">
                    Página { pagina } de { paginas }
                </Text>

                <button
                    className="btn btn-sm btn-secondary"
                    disabled={ pagina >= paginas || cargando }
                    onClick={ () => setPagina(valor => Math.min(paginas, valor + 1)) }>
                    Siguiente
                </button>
            </Base>
        </Column>
    );
};