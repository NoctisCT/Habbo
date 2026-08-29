import {
    EstadoSubastaEvent,
    FurnitureListComposer,
    FurniInventarioSubastas,
    InventarioSubastasEvent,
    ILinkEventTracker,
    MisSubastasEvent,
    ResultadoPonerSubastaEvent,
    ResultadoPujaSubastaEvent,
    ResultadoRetirarSubastaEvent,
    RoomPreviewer,
    SubastaPropia,
    Vector3d,
    EstadoSesionSubastasEvent,
    AvisoGlobalSubastaEvent
} from '@nitrots/nitro-renderer';
import { FC, useEffect, useMemo, useRef, useState } from 'react';
import {
    AbrirSubastas,
    AddEventLinkTracker,
    GetRoomEngine,
    ObtenerInventarioSubastas,
    ObtenerMisSubastas,
    PonerEnSubasta,
    PujarSubasta,
    RemoveLinkEventTracker,
    RetirarSubasta,
    SendMessageComposer,
    ObtenerEstadoSesionSubastas
} from '../../api';
import {
    AutoGrid,
    Base,
    Button,
    Column,
    Grid,
    LayoutGridItem,
    LayoutRoomPreviewerView,
    NitroCardContentView,
    NitroCardHeaderView,
    NitroCardView,
    Text
} from '../../common';
import { useInventoryFurni } from '../../hooks';
import { useMessageEvent } from '../../hooks/events';

import { HistorialSubastasView } from './HistorialSubastasView';
import { EstadisticasSubastasView } from './EstadisticasSubastasView';
import { RecordsSubastasView } from './RecordsSubastasView';
import { FeedSubastasView } from './FeedSubastasView';
import { EnCursoSubastasView } from './EnCursoSubastasView';
import { ProximaSubastaView } from './ProximaSubastaView';
import { SubastasFurniPreviewSeguro } from './SubastasFurniPreviewSeguro';
import { SubastasFurniImageSeguro } from './SubastasFurniImageSeguro';
type PestanaSubastas = 'en-curso' | 'vender' | 'mis-subastas' | 'historial' | 'estadisticas' | 'records';

const TXT = {
    creditos: 'cr\u00e9ditos',
    posicion: 'Posici\u00f3n',
    limite: 'L\u00edmite',
    simultaneas: 'simult\u00e1neas',
    valida: 'v\u00e1lido',
    minima: 'm\u00ednima',
    aqui: 'Aqu\u00ed'
};

export const SubastasView: FC<{}> = props =>
{
    const [ isVisible, setIsVisible ] = useState(false);
    const [ pestana, setPestana ] = useState<PestanaSubastas>('en-curso');

    const [ activa, setActiva ] = useState(false);
    const [ subastaActivaId, setSubastaActivaId ] = useState(0);
    const [ spriteIdActivo, setSpriteIdActivo ] = useState(0);
    const [ tipoActivo, setTipoActivo ] = useState('s');
    const [ precioInicialActivo, setPrecioInicialActivo ] = useState(0);
    const [ cantidadPuja, setCantidadPuja ] = useState('');
    const [ procesandoPuja, setProcesandoPuja ] = useState(false);
    const [ mensajePuja, setMensajePuja ] = useState('');
    const [ mensajeServidor, setMensajeServidor ] = useState('Esperando respuesta del servidor...');
    const [ objeto, setObjeto ] = useState('');
    const [ vendedor, setVendedor ] = useState('');
    const [ pujaActual, setPujaActual ] = useState(0);
    const [ segundosRestantes, setSegundosRestantes ] = useState(0);

    const [ inventario, setInventario ] = useState<FurniInventarioSubastas[]>([]);
    const [ cargandoInventario, setCargandoInventario ] = useState(false);
    const [ furniSeleccionado, setFurniSeleccionado ] = useState<FurniInventarioSubastas>(null);
    const [ roomPreviewer, setRoomPreviewer ] = useState<RoomPreviewer>(null);
    const [ precioInicial, setPrecioInicial ] = useState('');
    const [ cantidadVenta, setCantidadVenta ] = useState(1);
    const [ procesandoSubasta, setProcesandoSubasta ] = useState(false);
    const [ mensajeAccion, setMensajeAccion ] = useState('');
    const [ sesionDisponible, setSesionDisponible ] = useState(false);
    const [ nombreSesion, setNombreSesion ] = useState('');
    const [ cupoSesion, setCupoSesion ] = useState(0);
    const [ ocupadosSesion, setOcupadosSesion ] = useState(0);
    const [ inicioSesion, setInicioSesion ] = useState('');
    const [ finSesion, setFinSesion ] = useState('');
    const ultimaSubastaProcesada = useRef<number>(0);
    const instanciaPublicando = useRef<number>(0);

    const [ misSubastas, setMisSubastas ] = useState<SubastaPropia[]>([]);
    const [ busquedaMisSubastas, setBusquedaMisSubastas ] = useState('');
    const [ filtroMisSubastas, setFiltroMisSubastas ] = useState('todas');

    const misSubastasFiltradas = useMemo(() =>
    {
        const texto = busquedaMisSubastas.trim().toLowerCase();

        return misSubastas.filter((subasta: any) =>
        {
            const coincideTexto =
                !texto ||
                String(subasta.nombre || '').toLowerCase().includes(texto);

            const estado = String(subasta.estado || '');

            const coincideEstado =
                filtroMisSubastas === 'todas' ||
                (filtroMisSubastas === 'pendientes' && (estado === 'en_cola' || estado === 'activa')) ||
                (filtroMisSubastas === 'vendidas' && estado === 'vendida') ||
                (filtroMisSubastas === 'sin_pujas' && estado === 'sin_vender');

            return coincideTexto && coincideEstado;
        });
    }, [ misSubastas, busquedaMisSubastas, filtroMisSubastas ]);
    const [ cargandoMisSubastas, setCargandoMisSubastas ] = useState(false);
    const [ limiteSubastas, setLimiteSubastas ] = useState(3);
    const [ simultaneas, setSimultaneas ] = useState(0);
    const [ retirandoId, setRetirandoId ] = useState(0);
    const [ mensajeMisSubastas, setMensajeMisSubastas ] = useState('');

    const [ busquedaInventario, setBusquedaInventario ] = useState('');
    const [ tipoInventario, setTipoInventario ] = useState('todos');
    const [ rarezaInventario, setRarezaInventario ] = useState('todas');
    const [ soloApiladosInventario, setSoloApiladosInventario ] = useState(false);
    const { groupItems = [] } = useInventoryFurni();

    const obtenerGrupo = (furni: FurniInventarioSubastas) =>
        groupItems.find(grupo => grupo.getItemById(furni.instanciaId));

    const gruposInventario = useMemo(() =>
    {
        return groupItems
            .map(grupo =>
            {
                const furnis = inventario.filter(furni => !!grupo.getItemById(furni.instanciaId));

                return { grupo, furnis };
            })
            .filter(entrada => entrada.furnis.length > 0);
    }, [ groupItems, inventario ]);
const grupoSeleccionado = useMemo(
        () => furniSeleccionado ? obtenerGrupo(furniSeleccionado) : null,
        [ furniSeleccionado, groupItems ]
    );

    const furnisGrupoSeleccionado = useMemo(() =>
    {
        if(!grupoSeleccionado) return [];

        return inventario.filter(furni => !!grupoSeleccionado.getItemById(furni.instanciaId));
    }, [ grupoSeleccionado, inventario ]);

    const unidadesSeleccionadas = furnisGrupoSeleccionado.length;

    useEffect(() =>
    {
        setCantidadVenta(1);
    }, [ furniSeleccionado?.instanciaId ]);


const nombreFurni = useMemo(() =>
    {
        if(!furniSeleccionado) return '';

        const nombre = (furniSeleccionado.nombre || '').trim();

        if(nombre && !nombre.toLowerCase().endsWith('_name') && nombre !== '0')
        {
            return nombre;
        }

        return (furniSeleccionado.itemName || 'Furni sin nombre')
            .replace(/_/g, ' ')
            .replace(/\s+/g, ' ')
            .trim();
    }, [ furniSeleccionado ]);

    const obtenerNombreCorto = (furni: FurniInventarioSubastas): string =>
    {
        const nombre = (furni.nombre || '').trim();

        if(nombre && !nombre.toLowerCase().endsWith('_name') && nombre !== '0')
        {
            return nombre;
        }

        return (furni.itemName || 'Furni')
            .replace(/_/g, ' ')
            .replace(/\s+/g, ' ')
            .trim();
    };

    const rarezasInventario = useMemo(() =>
    {
        return Array.from(new Set(
            gruposInventario
                .map(({ furnis }) => (furnis[0]?.rareza || '').trim())
                .filter(rareza => !!rareza && rareza !== '0')
        )).sort((a, b) => a.localeCompare(b));
    }, [ gruposInventario ]);

    const gruposInventarioFiltrados = useMemo(() =>
    {
        const busqueda = busquedaInventario.trim().toLowerCase();

        return gruposInventario.filter(({ furnis }) =>
        {
            if(!furnis.length) return false;

            const furni = furnis[0];
            const nombre = obtenerNombreCorto(furni).toLowerCase();
            const itemName = (furni.itemName || '').replace(/_/g, ' ').toLowerCase();
            const tipo = (furni.tipo || '').toLowerCase();
            const rareza = (furni.rareza || '').trim();

            if(busqueda && !nombre.includes(busqueda) && !itemName.includes(busqueda))
            {
                return false;
            }

            if(tipoInventario !== 'todos' && tipo !== tipoInventario)
            {
                return false;
            }

            if(rarezaInventario !== 'todas' && rareza !== rarezaInventario)
            {
                return false;
            }

            if(soloApiladosInventario && furnis.length <= 1)
            {
                return false;
            }

            return true;
        });
    }, [
        gruposInventario,
        busquedaInventario,
        tipoInventario,
        rarezaInventario,
        soloApiladosInventario
    ]);
    const textoEstado = (estado: string): string =>
    {
        switch(estado)
        {
            case 'en_cola': return 'En cola';
            case 'activa': return 'En curso';
            case 'vendida': return 'Vendida';
            case 'sin_vender': return 'Sin vender';
            case 'cancelada': return 'Cancelada';
            case 'no_emitida': return 'No lleg\u00f3 a salir';
            default: return estado;
        }
    };

    useMessageEvent(AvisoGlobalSubastaEvent, (event: AvisoGlobalSubastaEvent) =>
    {
        const mensaje = event.getParser().mensaje;
        const id = 'aviso-global-subastas';

        document.getElementById(id)?.remove();

        const aviso = document.createElement('div');

        aviso.id = id;
        aviso.textContent = mensaje;
        aviso.style.position = 'fixed';
        aviso.style.top = '16px';
        aviso.style.left = '50%';
        aviso.style.transform = 'translateX(-50%)';
        aviso.style.zIndex = '100000';
        aviso.style.width = '320px';
        aviso.style.maxWidth = 'calc(100vw - 32px)';
        aviso.style.padding = '9px 12px';
        aviso.style.borderRadius = '6px';
        aviso.style.background = 'rgba(20, 20, 20, 0.92)';
        aviso.style.color = '#ffffff';
        aviso.style.fontSize = '13px';
        aviso.style.fontWeight = '700';
        aviso.style.lineHeight = '1.3';
        aviso.style.pointerEvents = 'none';
        aviso.style.boxShadow = '0 2px 8px rgba(0, 0, 0, 0.30)';

        document.body.appendChild(aviso);

        window.setTimeout(() =>
        {
            if(aviso.parentElement) aviso.remove();
        }, 6000);
    });
    useMessageEvent(EstadoSesionSubastasEvent, (event: EstadoSesionSubastasEvent) =>
    {
        const parser = event.getParser();

        setSesionDisponible(parser.disponible);
        setNombreSesion(parser.nombre);
        setCupoSesion(parser.cupoMaximo);
        setOcupadosSesion(parser.ocupados);
        setInicioSesion(parser.inicio);
        setFinSesion(parser.fin);

        if(parser.disponible && parser.cupoMaximo > 0 && parser.ocupados >= parser.cupoMaximo)
        {
            setMensajeAccion(
                `No queda cupo disponible en esta sesion (${ parser.ocupados }/${ parser.cupoMaximo }).`
            );
        }
        else
        {
            setMensajeAccion(actual =>
                actual.startsWith('No queda cupo disponible en esta sesion')
                    ? ''
                    : actual
            );
        }
    });
    useMessageEvent(EstadoSubastaEvent, (event: EstadoSubastaEvent) =>
    {
        const parser = event.getParser();

        setActiva(parser.activa);
        setMensajeServidor(parser.mensaje);
        setSubastaActivaId(parser.subastaId);
        setObjeto(parser.objeto);
        setSpriteIdActivo(parser.spriteId);
        setTipoActivo(parser.tipo);
        setVendedor(parser.vendedor);
        setPrecioInicialActivo(parser.precioInicial);
        setPujaActual(parser.pujaActual);
        setSegundosRestantes(parser.segundosRestantes);
    });

    useMessageEvent(ResultadoPujaSubastaEvent, (event: ResultadoPujaSubastaEvent) =>
    {
        const parser = event.getParser();

        setProcesandoPuja(false);
        setMensajePuja(parser.mensaje);

        if(parser.exito)
        {
            setCantidadPuja('');
            setPujaActual(parser.pujaActual);
        }
    });
    useMessageEvent(InventarioSubastasEvent, (event: InventarioSubastasEvent) =>
    {
        const furnis = event.getParser().furnis;

        setInventario(furnis);
        setCargandoInventario(false);

        if(instanciaPublicando.current > 0 &&
            !furnis.some(furni => furni.instanciaId === instanciaPublicando.current))
        {
            instanciaPublicando.current = 0;
            setProcesandoSubasta(false);
            setMensajeAccion('Subasta creada correctamente.');

            window.setTimeout(() =>
            {
                setMensajeAccion(actual =>
                    actual === 'Subasta creada correctamente.' ? '' : actual
                );
            }, 3000);
        }

        if(furnis.length)
        {
            setFurniSeleccionado(actual =>
                actual && furnis.some(f => f.instanciaId === actual.instanciaId)
                    ? actual
                    : furnis[0]
            );
        }
        else
        {
            setFurniSeleccionado(null);
        }
    });

    useMessageEvent(MisSubastasEvent, (event: MisSubastasEvent) =>
    {
        const parser = event.getParser();

        setMisSubastas(parser.subastas);
        setLimiteSubastas(parser.limite);
        setSimultaneas(parser.simultaneas);
        setCargandoMisSubastas(false);
    });

    useMessageEvent(ResultadoPonerSubastaEvent, (event: ResultadoPonerSubastaEvent) =>
    {
        const parser = event.getParser();

        instanciaPublicando.current = 0;

        if(parser.exito && parser.subastaId > 0)
        {
            if(ultimaSubastaProcesada.current === parser.subastaId) return;

            ultimaSubastaProcesada.current = parser.subastaId;
}

        setProcesandoSubasta(false);
        setMensajeAccion(parser.mensaje);
        ObtenerEstadoSesionSubastas();

        if(parser.exito)
        {
            ObtenerEstadoSesionSubastas();
            setPrecioInicial('');
            setFurniSeleccionado(null);
            ObtenerInventarioSubastas();

            window.setTimeout(() =>
            {
                setMensajeAccion(actual => actual === parser.mensaje ? '' : actual);
            }, 3000);
        }
    });

    useMessageEvent(ResultadoRetirarSubastaEvent, (event: ResultadoRetirarSubastaEvent) =>
    {
        const parser = event.getParser();

        setRetirandoId(0);
        setMensajeMisSubastas(parser.mensaje);

        if(parser.exito)
        {
            ObtenerMisSubastas();
        }
    });

    useEffect(() =>
    {
        const previewer = new RoomPreviewer(GetRoomEngine(), ++RoomPreviewer.PREVIEW_COUNTER);

        setRoomPreviewer(previewer);

        return () => previewer.dispose();
    }, []);


    // Preview segura: no usar RoomPreviewer con furnis seleccionados.
useEffect(() =>
    {
        if(!isVisible || !activa) return;

        const intervalo = setInterval(() =>
        {
            setSegundosRestantes(valor => Math.max(0, valor - 1));
        }, 1000);

        return () => clearInterval(intervalo);
    }, [ isVisible, activa ]);

    useEffect(() =>
    {
        if(!isVisible || pestana !== 'vender') return;

        setCargandoInventario(true);
        SendMessageComposer(new FurnitureListComposer());
        ObtenerInventarioSubastas();
        ObtenerEstadoSesionSubastas();
    }, [ isVisible, pestana ]);

    useEffect(() =>
    {
        if(!isVisible) return;

        ObtenerEstadoSesionSubastas();

        const intervaloEstadoSesionSubastas = window.setInterval(() =>
        {
            ObtenerEstadoSesionSubastas();
        }, 2000);

        return () => window.clearInterval(intervaloEstadoSesionSubastas);
    }, [ isVisible ]);
    useEffect(() =>
    {
        if(!isVisible || pestana !== 'mis-subastas') return;

        setCargandoMisSubastas(true);
        ObtenerMisSubastas();

        const intervalo = window.setInterval(() =>
        {
            ObtenerMisSubastas();
        }, 2000);

        return () => window.clearInterval(intervalo);
    }, [ isVisible, pestana ]);

    useEffect(() =>
    {
        const linkTracker: ILinkEventTracker = {
            linkReceived: (url: string) =>
            {
                const parts = url.split('/');

                if(parts.length < 2) return;

                switch(parts[1])
                {
                    case 'show':
                        setIsVisible(true);
                        setMensajeServidor('Consultando al servidor...');
                        AbrirSubastas();
                        return;
                    case 'hide':
                        setIsVisible(false);
                        return;
                    case 'toggle':
                        setIsVisible(prevValue =>
                        {
                            const nuevoEstado = !prevValue;

                            if(nuevoEstado)
                            {
                                setMensajeServidor('Consultando al servidor...');
                                AbrirSubastas();
                            }

                            return nuevoEstado;
                        });
                        return;
                }
            },
            eventUrlPrefix: 'subastas/'
        };

        AddEventLinkTracker(linkTracker);

        return () => RemoveLinkEventTracker(linkTracker);
    }, []);

    const enviarPuja = () =>
    {
        if(!activa || subastaActivaId <= 0 || procesandoPuja) return;

        const cantidad = Number(cantidadPuja);
        const minimo = pujaActual > 0 ? (pujaActual + 1) : precioInicialActivo;

        if(!Number.isInteger(cantidad) || cantidad < minimo)
        {
            setMensajePuja(`La puja m\u00ednima es ${ minimo } ${ TXT.creditos }.`);
            return;
        }

        setProcesandoPuja(true);
        setMensajePuja('Procesando puja...');

        PujarSubasta(subastaActivaId, cantidad);
    };
    const publicarSubasta = () =>
    {
        if(!furniSeleccionado || procesandoSubasta || instanciaPublicando.current > 0) return;

        if(!sesionDisponible)
        {
            setMensajeAccion('No hay una sesion de subastas disponible.');
            ObtenerEstadoSesionSubastas();
            return;
        }

        if(cupoSesion > 0 && ocupadosSesion >= cupoSesion)
        {
            setProcesandoSubasta(false);
            setMensajeAccion(
                `No queda cupo disponible en esta sesion (${ ocupadosSesion }/${ cupoSesion }).`
            );
            ObtenerEstadoSesionSubastas();
            return;
        }

        const precio = Number(precioInicial);

        if(!Number.isInteger(precio) || precio < 1 || precio > 1000000000)
        {
            setMensajeAccion(`Introduce un precio inicial ${ TXT.valida }.`);
            return;
        }

        const cantidadLote = Math.max(
            1,
            Math.min(cantidadVenta, furnisGrupoSeleccionado.length)
        );

        const instanciaIds = furnisGrupoSeleccionado
            .slice(0, cantidadLote)
            .map(furni => furni.instanciaId);

        if(!instanciaIds.length)
        {
            setMensajeAccion('No hay unidades disponibles para crear el lote.');
            return;
        }

        setProcesandoSubasta(true);
        setMensajeAccion(
            cantidadLote > 1
                ? `Creando lote de ${ cantidadLote } unidades...`
                : 'Creando subasta...'
        );
        instanciaPublicando.current = instanciaIds[0];

        PonerEnSubasta(instanciaIds, precio);

        window.setTimeout(() =>
        {
            ObtenerInventarioSubastas();
        }, 700);
    };

    const retirarSubasta = (subastaId: number) =>
    {
        if(retirandoId) return;

        setRetirandoId(subastaId);
        setMensajeMisSubastas('Retirando subasta...');

        RetirarSubasta(subastaId);
    };

    if(!isVisible) return null;

    return (
        <NitroCardView
            uniqueKey="subastas"
            className="nitro-subastas"
            theme="primary-slim"
            style={ { width: '620px', height: '500px' } }>
            <NitroCardHeaderView headerText="Subastas" onCloseClick={ event => setIsVisible(false) } />
            <NitroCardContentView gap={ 2 } overflow="hidden">
                <Base className="d-flex gap-1 flex-wrap">
                    <Base pointer className={ `btn btn-sm ${ pestana === 'en-curso' ? 'btn-primary' : 'btn-secondary' }` } onClick={ () => setPestana('en-curso') }>En curso</Base>
                    <Base pointer className={ `btn btn-sm ${ pestana === 'vender' ? 'btn-primary' : 'btn-secondary' }` } onClick={ () => setPestana('vender') }>Vender</Base>
                    <Base pointer className={ `btn btn-sm ${ pestana === 'mis-subastas' ? 'btn-primary' : 'btn-secondary' }` } onClick={ () => setPestana('mis-subastas') }>Mis subastas</Base>
                    <Base pointer className={ `btn btn-sm ${ pestana === 'historial' ? 'btn-primary' : 'btn-secondary' }` } onClick={ () => setPestana('historial') }>Historial</Base>
                    <Base pointer className={ `btn btn-sm ${ pestana === 'estadisticas' ? 'btn-primary' : 'btn-secondary' }` } onClick={ () => setPestana('estadisticas') }>{ 'Estad\u00edsticas' }</Base>
                    <Base pointer className={ `btn btn-sm ${ pestana === 'records' ? 'btn-primary' : 'btn-secondary' }` } onClick={ () => setPestana('records') }>Records</Base>
                </Base>

                { pestana === 'en-curso' &&
                    (activa
                        ? <EnCursoSubastasView
                    visible={ isVisible }
                    subastaActivaId={ subastaActivaId }
                    objeto={ objeto }
                    vendedor={ vendedor }
                    precioInicial={ precioInicialActivo }
                    pujaActual={ pujaActual }
                    segundosRestantes={ segundosRestantes }
                    cantidadPuja={ cantidadPuja }
                    setCantidadPuja={ setCantidadPuja }
                    procesandoPuja={ procesandoPuja }
                    mensajePuja={ mensajePuja }
                    spriteId={ spriteIdActivo }
                    tipo={ tipoActivo }
                    onPujar={ enviarPuja }
                />
                        : <Column fullHeight gap={ 2 } style={ { minHeight: 0 } }>                            <ProximaSubastaView visible={ isVisible && pestana === 'en-curso' } />

                            <Base
                                style={ {
                                    flex: '1 1 auto',
                                    minHeight: 0,
                                    overflow: 'hidden'
                                } }>
                                <FeedSubastasView visible={ isVisible && pestana === 'en-curso' } />
                            </Base>
                        </Column>) }

{ pestana === 'vender' &&
                    <>
                        <Base
                            className="px-1"
                            style={ { height: '22px', minHeight: '22px', maxHeight: '22px', overflow: 'hidden' } }>
                            { mensajeAccion &&
                                <Text small fontWeight="bold">{ mensajeAccion }</Text> }
                        </Base>

                        <Grid style={ { height: 'calc(100% - 24px)', minHeight: 0 } }>
                            <Column size={ 7 } overflow="hidden">
                                <Text fontSize={ 4 } fontWeight="bold">Tu inventario</Text>
                                <Column gap={ 1 }>
                                    <input
                                        className="form-control form-control-sm"
                                        type="text"
                                        value={ busquedaInventario }
                                        placeholder="Buscar furni..."
                                        onChange={ event => setBusquedaInventario(event.target.value) }
                                    />

                                    <Base
                                        className="d-flex align-items-center flex-wrap"
                                        style={ { gap: '6px' } }>
                                        <select
                                            className="form-select form-select-sm"
                                            style={ { width: '105px' } }
                                            value={ tipoInventario }
                                            onChange={ event => setTipoInventario(event.target.value) }>
                                            <option value="todos">Todos</option>
                                            <option value="s">Suelo</option>
                                            <option value="i">Pared</option>
                                        </select>

                                        <select
                                            className="form-select form-select-sm"
                                            style={ { width: '130px' } }
                                            value={ rarezaInventario }
                                            onChange={ event => setRarezaInventario(event.target.value) }>
                                            <option value="todas">Todas las rarezas</option>
                                            { rarezasInventario.map(rareza =>
                                                <option key={ rareza } value={ rareza }>{ rareza }</option>) }
                                        </select>

                                        <label
                                            className="d-flex align-items-center mb-0"
                                            style={ { gap: '4px', whiteSpace: 'nowrap' } }>
                                            <input
                                                type="checkbox"
                                                checked={ soloApiladosInventario }
                                                onChange={ event => setSoloApiladosInventario(event.target.checked) }
                                            />
                                            <Text small>Varias unidades</Text>
                                        </label>

                                        <Text small>
                                            { gruposInventarioFiltrados.length }/{ gruposInventario.length }
                                        </Text>
                                    </Base>
                                </Column>
                                { sesionDisponible
                                    ? <Column gap={ 0 }>
                                        <Text small fontWeight="bold">
                                            Cupo: { ocupadosSesion }/{ cupoSesion }
                                        </Text>
                                        <Text small>
                                            { nombreSesion } - { inicioSesion } a { finSesion }
                                        </Text>
                                    </Column>
                                    : <Text small>No hay una sesion disponible.</Text> }

                                { cargandoInventario &&
                                    <Text>Consultando inventario...</Text> }

                                { !cargandoInventario && gruposInventario.length === 0 &&
                                    <Text>No tienes furnis comerciables.</Text> }

                                { !cargandoInventario &&
                                    gruposInventario.length > 0 &&
                                    gruposInventarioFiltrados.length === 0 &&
                                    <Text small>No hay furnis que coincidan con los filtros.</Text> }

                                { !cargandoInventario && gruposInventarioFiltrados.length > 0 &&
                                    <Column grow overflow="auto">
                                        <AutoGrid gap={ 1 } columnCount={ 5 }>
                                            { gruposInventarioFiltrados.map(({ grupo, furnis }) =>
                                                <LayoutGridItem
                                                    key={ furnis[0].instanciaId }
                                                    itemActive={ furnis.some(f => f.instanciaId === furniSeleccionado?.instanciaId) }
                                                    itemImage={ grupo.iconUrl || null }
                                                    itemCount={ furnis.length }
                                                    title={ obtenerNombreCorto(furnis[0]) }
                                                    onClick={ () =>
                                                    {
                                                        setCantidadVenta(1);
                                                        setFurniSeleccionado(furnis[0]);
                                                    } }
                                                />) }
                                        </AutoGrid>
                                    </Column> }
                            </Column>

                            <Column size={ 5 } gap={ 1 } overflow="hidden">
                                { furniSeleccionado &&
                                    <>
                                        <SubastasFurniPreviewSeguro
                                            furniId={ furniSeleccionado.furniId }
                                            tipo={ furniSeleccionado.tipo }
                                            nombre={ nombreFurni }
                                            iconUrl={ grupoSeleccionado?.iconUrl || '' }
                                        />

                                        <Base
                                            className="d-flex align-items-center justify-content-between"
                                            style={ { gap: '8px' } }>
                                            <Text fontSize={ 4 } fontWeight="bold">{ nombreFurni }</Text>

                                            { unidadesSeleccionadas > 1 &&
                                                <Base
                                                    className="px-2 py-1 rounded"
                                                    style={ {
                                                        whiteSpace: 'nowrap',
                                                        background: 'rgba(255,255,255,.35)',
                                                        border: '1px solid rgba(110,125,140,.25)'
                                                    } }>
                                                    <Text small fontWeight="bold">
                                                        { unidadesSeleccionadas } disponibles
                                                    </Text>
                                                </Base> }
                                        </Base>

                                        { unidadesSeleccionadas > 1 &&
                                            <Base
                                                className="d-flex align-items-center justify-content-between p-1 rounded"
                                                style={ {
                                                    gap: '6px',
                                                    background: 'rgba(255,255,255,.22)'
                                                } }>
                                                <Text fontWeight="bold">Cantidad</Text>

                                                <Base
                                                    className="d-flex align-items-center"
                                                    style={ { gap: '4px' } }>
                                                    <Button
                                                        disabled={ cantidadVenta <= 1 }
                                                        onClick={ () => setCantidadVenta(valor => Math.max(1, valor - 1)) }>
                                                        -
                                                    </Button>

                                                    <input
                                                        className="form-control form-control-sm text-center"
                                                        type="number"
                                                        min="1"
                                                        max={ unidadesSeleccionadas }
                                                        step="1"
                                                        value={ cantidadVenta }
                                                        style={ { width: '54px' } }
                                                        onChange={ event =>
                                                        {
                                                            const valor = Number(event.target.value);

                                                            setCantidadVenta(
                                                                Number.isInteger(valor)
                                                                    ? Math.max(1, Math.min(unidadesSeleccionadas, valor))
                                                                    : 1
                                                            );
                                                        } }
                                                    />

                                                    <Button
                                                        disabled={ cantidadVenta >= unidadesSeleccionadas }
                                                        onClick={ () => setCantidadVenta(valor =>
                                                            Math.min(unidadesSeleccionadas, valor + 1)) }>
                                                        +
                                                    </Button>

                                                    <Button
                                                        disabled={ cantidadVenta >= unidadesSeleccionadas }
                                                        onClick={ () => setCantidadVenta(unidadesSeleccionadas) }>
                                                        Todas
                                                    </Button>
                                                </Base>
                                            </Base> }

                                        <Text fontWeight="bold">Precio inicial</Text>

                                        <input
                                            className="form-control form-control-sm"
                                            type="number"
                                            min="1"
                                            step="1"
                                            value={ precioInicial }
                                            placeholder="Creditos"
                                            onChange={ event => setPrecioInicial(event.target.value) }
                                        />

                                        <Button
                                            disabled={ procesandoSubasta || !precioInicial || (sesionDisponible && cupoSesion > 0 && ocupadosSesion >= cupoSesion) }
                                            onClick={ publicarSubasta }>
                                            { procesandoSubasta
                                                ? 'Creando...'
                                                : (cantidadVenta > 1
                                                    ? `Poner lote x${ cantidadVenta } en subasta`
                                                    : 'Poner en subasta') }
                                        </Button>
                                    </> }
                            </Column>
                        </Grid>
                    </> }

                { pestana === 'mis-subastas' &&
                    <Column gap={ 2 } overflow="hidden" fullHeight>
                        <Base
                            className="d-flex align-items-center"
                            style={ {
                                gap: '5px',
                                height: '32px',
                                minHeight: '32px'
                            } }>
                            <input
                                className="form-control form-control-sm"
                                value={ busquedaMisSubastas }
                                placeholder="Buscar mis subastas..."
                                onChange={ event => setBusquedaMisSubastas(event.target.value) }
                                style={ { flex: '1 1 auto', minWidth: 0 } }
                            />

                            <select
                                className="form-select form-select-sm"
                                value={ filtroMisSubastas }
                                onChange={ event => setFiltroMisSubastas(event.target.value) }
                                style={ { width: '126px', minWidth: '126px' } }>
                                <option value="todas">Todas</option>
                                <option value="pendientes">Pendientes</option>
                                <option value="vendidas">Vendidas</option>
                                <option value="sin_pujas">Sin pujas</option>
                            </select>
                        </Base>
                        <Base className="d-flex justify-content-between align-items-center gap-2">
                            <Text fontSize={ 4 } fontWeight="bold">Mis subastas</Text>
                            <Text small fontWeight="bold">
                                { simultaneas } / { limiteSubastas } { TXT.simultaneas }
                            </Text>
                        </Base>

                        { mensajeMisSubastas &&
                            <Base className="p-2 bg-muted rounded">
                                <Text fontWeight="bold">{ mensajeMisSubastas }</Text>
                            </Base> }

                        { cargandoMisSubastas &&
                            <Text>Consultando tus subastas...</Text> }

                        { !cargandoMisSubastas && misSubastas.length === 0 &&
                            <Text>No tienes subastas registradas.</Text> }

                        { !cargandoMisSubastas && misSubastas.length > 0 &&
                            <Column gap={ 1 } overflow="auto">
                                { misSubastasFiltradas.map(subasta =>
                                    <Base key={ subasta.id } className="p-2 bg-muted rounded">
                                        <Grid>
                                            <Column size={ 3 } center>
                                                <SubastasFurniImageSeguro
                                                    productType={ subasta.tipo.toLowerCase() === 'i' ? 'i' : 's' }
                                                    productClassId={ subasta.spriteId }
                                                    scale={ 0.8 }
                                                />
                                            </Column>

                                            <Column size={ 9 } gap={ 1 }>
                                                <Base className="d-flex justify-content-between align-items-center gap-2">
                                                    <Text fontWeight="bold">{ subasta.nombre }</Text>
                                                    <Text small fontWeight="bold">{ textoEstado(subasta.estado) }</Text>
                                                </Base>

                                                <Text small>Subasta #{ subasta.id } - Objeto #{ subasta.objetoId }</Text>
                                                <Text small>Puja { TXT.minima }: { subasta.precioInicial } { TXT.creditos }</Text>
                                                { subasta.estado === 'vendida'
                                                    ? <Column gap={ 0 }>
                                                        <Text small fontWeight="bold">
                                                            Vendida a: { subasta.ganador || 'Ganador no disponible' }
                                                        </Text>
                                                        <Text small>
                                                            Precio final: { subasta.pujaActual } { TXT.creditos }
                                                        </Text>
                                                        { subasta.comisionAplicada >= 0 &&
                                                            <Text small>
                                                                { 'Comisi\u00f3n' }: { subasta.comisionAplicada } { TXT.creditos }
                                                            </Text> }
                                                        { subasta.netoVendedor >= 0 &&
                                                            <Text small fontWeight="bold">
                                                                Recibido: { subasta.netoVendedor } { TXT.creditos }
                                                            </Text> }
                                                    </Column>
                                                    : (subasta.pujaActual > 0
                                                        ? <Text small>Puja actual: { subasta.pujaActual } { TXT.creditos }</Text>
                                                        : <Text small>Puja actual: Sin pujas</Text>) }

                                                { subasta.estado === 'en_cola' &&
                                                    <Text small fontWeight="bold">
                                                        { TXT.posicion } en cola: { subasta.posicionCola }
                                                    </Text> }

                                                <Text small>Creada: { subasta.fechaCreacion }</Text>

                                                { subasta.estado === 'en_cola' &&
                                                    <Button
                                                        variant="danger"
                                                        disabled={ retirandoId !== 0 }
                                                        onClick={ () => retirarSubasta(subasta.id) }>
                                                        { retirandoId === subasta.id ? 'Retirando...' : 'Retirar de subasta' }
                                                    </Button> }
                                            </Column>
                                        </Grid>
                                    </Base>) }
                            </Column> }
                    </Column> }

                { pestana === 'historial' && <HistorialSubastasView visible={ isVisible } /> }

                { pestana === 'estadisticas' && <EstadisticasSubastasView visible={ isVisible } /> }

                { pestana === 'records' && <RecordsSubastasView visible={ isVisible && pestana === 'records' } /> }
            </NitroCardContentView>
        </NitroCardView>
    );
};