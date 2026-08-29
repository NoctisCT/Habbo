import {
    IRecordFurniSubastas,
    IRecordUsuarioSubastas,
    RecordsSubastasEvent
} from '@nitrots/nitro-renderer';
import { FC, useEffect, useState } from 'react';
import { ObtenerRecordsSubastas } from '../../api';
import { Base, Column, LayoutAvatarImageView, Text } from '../../common';
import { useMessageEvent } from '../../hooks';
import { SubastasFurniImageSeguro } from './SubastasFurniImageSeguro';

interface RecordsSubastasViewProps
{
    visible: boolean;
}

type MiniPestanaRecords = 'usuarios' | 'furnis';

const colores = [
    { fondo: 'rgba(63,126,168,.15)', borde: '#3f7ea8' },
    { fondo: 'rgba(82,148,98,.15)', borde: '#529462' },
    { fondo: 'rgba(189,139,53,.16)', borde: '#bd8b35' },
    { fondo: 'rgba(132,91,168,.15)', borde: '#845ba8' }
];

const numero = (valor: number) => Math.round(valor || 0).toLocaleString('es-ES');

const RankingUsuario: FC<{
    titulo: string;
    unidad: string;
    unidadSecundaria?: string;
    filas: IRecordUsuarioSubastas[];
    color: { fondo: string; borde: string };
}> = props =>
{
    const { titulo, unidad, unidadSecundaria = '', filas = [], color } = props;
    const primero = filas[0] || null;
    const resto = [ 1, 2, 3, 4 ].map(indice => filas[indice] || null);

    return (
        <Column
            gap={ 0 }
            className="rounded"
            style={ {
                height: '378px',
                minHeight: '378px',
                maxHeight: '378px',
                overflow: 'hidden',
                background: color.fondo,
                border: `1px solid ${ color.borde }`
            } }>
            <Base
                className="d-flex align-items-center justify-content-center px-1"
                style={ {
                    height: '34px',
                    minHeight: '34px',
                    borderBottom: `3px solid ${ color.borde }`,
                    background: 'rgba(255,255,255,.26)',
                    textAlign: 'center'
                } }>
                <Text small fontWeight="bold">{ titulo }</Text>
            </Base>

            <Column
                gap={ 0 }
                alignItems="center"
                style={ {
                    height: '170px',
                    minHeight: '170px',
                    paddingTop: '5px',
                    borderBottom: '1px solid rgba(80,90,100,.14)'
                } }>
                { primero
                    ? <>
                        <Base
                            className="d-flex align-items-center justify-content-center"
                            style={ {
                                width: '82px',
                                height: '86px',
                                minHeight: '86px',
                                marginBottom: '3px',
                                overflow: 'hidden'
                            } }>
                            <LayoutAvatarImageView
                                figure={ primero.look }
                                gender={ primero.gender || 'M' }
                                direction={ 2 }
                                scale={ 1 }
                            />
                        </Base>

                        <Text
                            fontWeight="bold"
                            style={ {
                                width: '128px',
                                minHeight: '18px',
                                lineHeight: '18px',
                                textAlign: 'center',
                                whiteSpace: 'nowrap',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                                flex: '0 0 auto'
                            } }>
                            { primero.username }
                        </Text>

                        <Text fontSize={ 4 } fontWeight="bold">
                            { numero(primero.valor) }
                        </Text>
                        <Text small>{ unidad }</Text>

                        { unidadSecundaria && primero.secundario > 0 &&
                            <Text small style={ { opacity: .72 } }>
                                { `${ numero(primero.secundario) } ${ unidadSecundaria }` }
                            </Text> }
                    </>
                    : <Column center style={ { height: '100%' } }>
                        <Text small>Sin datos</Text>
                    </Column> }
            </Column>

            <Column gap={ 0 } style={ { flex: '1 1 auto' } }>
                { resto.map((fila, indice) =>
                    <Base
                        key={ indice + 2 }
                        className="d-flex align-items-center px-1"
                        style={ {
                            height: '40px',
                            minHeight: '40px',
                            gap: '4px',
                            borderBottom: indice < 3
                                ? '1px solid rgba(80,90,100,.10)'
                                : 'none'
                        } }>
                        <Text small fontWeight="bold" style={ { width: '18px' } }>
                            #{ indice + 2 }
                        </Text>

                        { fila
                            ? <>
                                <Text
                                    small
                                    fontWeight="bold"
                                    style={ {
                                        minWidth: 0,
                                        flex: '1 1 auto',
                                        whiteSpace: 'nowrap',
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis'
                                    } }>
                                    { fila.username }
                                </Text>
                                <Column gap={ 0 } alignItems="end">
                                    <Text small fontWeight="bold">{ numero(fila.valor) }</Text>
                                    { unidadSecundaria && fila.secundario > 0 &&
                                        <Text small style={ { opacity: .65 } }>
                                            { `${ numero(fila.secundario) } ${ unidadSecundaria }` }
                                        </Text> }
                                </Column>
                            </>
                            : <Text small style={ { opacity: .45 } }>Sin dato</Text> }
                    </Base>
                ) }
            </Column>

            <Text
                small
                style={ {
                    height: '14px',
                    minHeight: '14px',
                    textAlign: 'center',
                    opacity: .6
                } }>
                Top 5 histórico
            </Text>
        </Column>
    );
};

const RankingFurni: FC<{
    titulo: string;
    unidad: string;
    filas: IRecordFurniSubastas[];
    color: { fondo: string; borde: string };
}> = props =>
{
    const { titulo, unidad, unidadSecundaria = '', filas = [], color } = props;
    const primero = filas[0] || null;
    const resto = [ 1, 2, 3, 4 ].map(indice => filas[indice] || null);

    return (
        <Column
            gap={ 0 }
            className="rounded"
            style={ {
                height: '378px',
                minHeight: '378px',
                maxHeight: '378px',
                overflow: 'hidden',
                background: color.fondo,
                border: `1px solid ${ color.borde }`
            } }>
            <Base
                className="d-flex align-items-center justify-content-center px-1"
                style={ {
                    height: '34px',
                    minHeight: '34px',
                    borderBottom: `3px solid ${ color.borde }`,
                    background: 'rgba(255,255,255,.26)',
                    textAlign: 'center'
                } }>
                <Text small fontWeight="bold">{ titulo }</Text>
            </Base>

            <Column
                gap={ 0 }
                alignItems="center"
                style={ {
                    height: '170px',
                    minHeight: '170px',
                    paddingTop: '5px',
                    borderBottom: '1px solid rgba(80,90,100,.14)'
                } }>
                { primero
                    ? <>
                        <Base
                            className="d-flex align-items-center justify-content-center"
                            style={ {
                                width: '72px',
                                height: '76px',
                                minHeight: '76px',
                                overflow: 'hidden'
                            } }>
                            <SubastasFurniImageSeguro
                                productType={ (primero.tipo || '').toLowerCase() === 'i' ? 'i' : 's' }
                                productClassId={ primero.spriteId }
                                scale={ 0.85 }
                                style={ {
                                    width: '66px',
                                    height: '66px'
                                } }
                            />
                        </Base>

                        <Text
                            fontWeight="bold"
                            style={ {
                                maxWidth: '128px',
                                whiteSpace: 'nowrap',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis'
                            } }>
                            { primero.nombre }
                        </Text>

                        <Text fontSize={ 4 } fontWeight="bold">
                            { numero(primero.valor) }
                        </Text>
                        <Text small>{ unidad }</Text>
                    </>
                    : <Column center style={ { height: '100%' } }>
                        <Text small>Sin datos</Text>
                    </Column> }
            </Column>

            <Column gap={ 0 } style={ { flex: '1 1 auto' } }>
                { resto.map((fila, indice) =>
                    <Base
                        key={ indice + 2 }
                        className="d-flex align-items-center px-1"
                        style={ {
                            height: '40px',
                            minHeight: '40px',
                            gap: '4px',
                            borderBottom: indice < 3
                                ? '1px solid rgba(80,90,100,.10)'
                                : 'none'
                        } }>
                        <Text small fontWeight="bold" style={ { width: '18px' } }>
                            #{ indice + 2 }
                        </Text>

                        { fila
                            ? <>
                                <Text
                                    small
                                    fontWeight="bold"
                                    style={ {
                                        minWidth: 0,
                                        flex: '1 1 auto',
                                        whiteSpace: 'nowrap',
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis'
                                    } }>
                                    { fila.nombre }
                                </Text>
                                <Text small fontWeight="bold">{ numero(fila.valor) }</Text>
                            </>
                            : <Text small style={ { opacity: .45 } }>Sin dato</Text> }
                    </Base>
                ) }
            </Column>

            <Text
                small
                style={ {
                    height: '14px',
                    minHeight: '14px',
                    textAlign: 'center',
                    opacity: .6
                } }>
                Top 5 histórico
            </Text>
        </Column>
    );
};

export const RecordsSubastasView: FC<RecordsSubastasViewProps> = props =>
{
    const { visible = false } = props;

    const [ miniPestana, setMiniPestana ] = useState<MiniPestanaRecords>('usuarios');

    const [ dineroGanado, setDineroGanado ] = useState<IRecordUsuarioSubastas[]>([]);
    const [ furnisVendidos, setFurnisVendidos ] = useState<IRecordUsuarioSubastas[]>([]);
    const [ dineroGastado, setDineroGastado ] = useState<IRecordUsuarioSubastas[]>([]);
    const [ compras, setCompras ] = useState<IRecordUsuarioSubastas[]>([]);

    const [ masCaros, setMasCaros ] = useState<IRecordFurniSubastas[]>([]);
    const [ masVendidos, setMasVendidos ] = useState<IRecordFurniSubastas[]>([]);
    const [ mayorVolumen, setMayorVolumen ] = useState<IRecordFurniSubastas[]>([]);
    const [ masPujados, setMasPujados ] = useState<IRecordFurniSubastas[]>([]);

    useMessageEvent(RecordsSubastasEvent, (event: RecordsSubastasEvent) =>
    {
        const parser = event.getParser();

        setDineroGanado(parser.dineroGanado);
        setFurnisVendidos(parser.furnisVendidos);
        setDineroGastado(parser.dineroGastado);
        setCompras(parser.compras);

        setMasCaros(parser.masCaros);
        setMasVendidos(parser.masVendidos);
        setMayorVolumen(parser.mayorVolumen);
        setMasPujados(parser.masPujados);
    });

    useEffect(() =>
    {
        if(!visible) return;

        ObtenerRecordsSubastas();
    }, [ visible ]);

    if(!visible) return null;

    return (
        <Column fullHeight gap={ 1 } style={ { minHeight: 0 } }>
            <Base
                className="d-flex align-items-center justify-content-between"
                style={ {
                    height: '34px',
                    minHeight: '34px'
                } }>
                <Text fontSize={ 4 } fontWeight="bold">Récords históricos</Text>

                <Base className="d-flex" style={ { gap: '4px' } }>
                    <Base
                        pointer
                        className={ `btn btn-sm ${ miniPestana === 'usuarios' ? 'btn-primary' : 'btn-secondary' }` }
                        onClick={ () => setMiniPestana('usuarios') }>
                        Usuarios
                    </Base>

                    <Base
                        pointer
                        className={ `btn btn-sm ${ miniPestana === 'furnis' ? 'btn-primary' : 'btn-secondary' }` }
                        onClick={ () => setMiniPestana('furnis') }>
                        Furnis
                    </Base>
                </Base>
            </Base>

            { miniPestana === 'usuarios' &&
                <Base
                    style={ {
                        display: 'grid',
                        gridTemplateColumns: 'repeat(4, minmax(0, 1fr))',
                        gap: '5px',
                        minHeight: 0
                    } }>
                    <RankingUsuario
                        titulo="Más créditos ganados"
                        unidad="créditos"
                        filas={ dineroGanado }
                        color={ colores[0] }
                    />
                    <RankingUsuario
                        titulo="Más furnis vendidos"
                        unidad="furnis"
                        filas={ furnisVendidos }
                        color={ colores[1] }
                    />
                    <RankingUsuario
                        titulo="Más créditos gastados"
                        unidad="créditos"
                        filas={ dineroGastado }
                        color={ colores[2] }
                    />
                    <RankingUsuario
                        titulo="Más compras"
                        unidad="compras"
                        unidadSecundaria="furnis"
                        filas={ compras }
                        color={ colores[3] }
                    />
                </Base> }

            { miniPestana === 'furnis' &&
                <Base
                    style={ {
                        display: 'grid',
                        gridTemplateColumns: 'repeat(4, minmax(0, 1fr))',
                        gap: '5px',
                        minHeight: 0
                    } }>
                    <RankingFurni
                        titulo="Más caro vendido"
                        unidad="créditos/u"
                        filas={ masCaros }
                        color={ colores[0] }
                    />
                    <RankingFurni
                        titulo="Más vendido"
                        unidad="unidades"
                        filas={ masVendidos }
                        color={ colores[1] }
                    />
                    <RankingFurni
                        titulo="Mayor volumen"
                        unidad="créditos"
                        filas={ mayorVolumen }
                        color={ colores[2] }
                    />
                    <RankingFurni
                        titulo="Más pujado"
                        unidad="pujas recibidas"
                        filas={ masPujados }
                        color={ colores[3] }
                    />
                </Base> }
        </Column>
    );
};