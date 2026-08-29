import { FC } from 'react';
import { Base, Button, Column, Grid, Text } from '../../common';
import { FeedSubastasView } from './FeedSubastasView';
import { SubastasFurniImageSeguro } from './SubastasFurniImageSeguro';

interface EnCursoSubastasViewProps
{
    visible: boolean;
    subastaActivaId: number;
    objeto: string;
    vendedor: string;
    precioInicial: number;
    pujaActual: number;
    segundosRestantes: number;
    cantidadPuja: string;
    setCantidadPuja: (valor: string) => void;
    procesandoPuja: boolean;
    mensajePuja: string;
    spriteId: number;
    tipo: string;
    onPujar: () => void;
}

export const EnCursoSubastasView: FC<EnCursoSubastasViewProps> = props =>
{
    const
    {
        visible,
        subastaActivaId,
        objeto,
        vendedor,
        precioInicial,
        pujaActual,
        segundosRestantes,
        cantidadPuja,
        setCantidadPuja,
        procesandoPuja,
        mensajePuja,
        spriteId,
        tipo,
        onPujar
    } = props;

    if(!visible) return null;

    const activa = subastaActivaId > 0;
    const pujaMinima = pujaActual > 0 ? pujaActual + 1 : precioInicial;

    return (
        <Grid fullHeight gap={ 2 } style={ { minHeight: 0 } }>
            <Column size={ 8 } gap={ 2 } style={ { minHeight: 0 } }>
                <Base
                    className="d-flex align-items-center justify-content-between px-2 py-1 rounded"
                    style={ {
                        minHeight: '42px',
                        background: 'rgba(190, 200, 211, 0.92)',
                        border: '1px solid rgba(103, 119, 136, 0.55)',
                        boxShadow: 'inset 0 1px rgba(255,255,255,.38)'
                    } }>
                    <Column gap={ 0 }>
                        <Text fontSize={ 4 } fontWeight="bold">
                            { activa ? objeto : 'Subastas' }
                        </Text>
                        <Text small>
                            { activa ? 'Subasta activa' : 'Esperando la siguiente subasta' }
                        </Text>
                    </Column>

                    <Base
                        className="px-2 py-1 rounded"
                        style={ {
                            background: activa
                                ? 'rgba(83, 164, 103, .22)'
                                : 'rgba(255,255,255,.35)',
                            border: activa
                                ? '1px solid rgba(70, 135, 85, .45)'
                                : '1px solid rgba(110, 120, 130, .34)'
                        } }>
                        <Text small fontWeight="bold">
                            { activa ? 'EN DIRECTO' : 'EN ESPERA' }
                        </Text>
                    </Base>
                </Base>

                <Base
                    className="p-2 rounded"
                    style={ {
                        flex: 1,
                        minHeight: 0,
                        background: 'rgba(190,200,211,.76)',
                        border: '1px solid rgba(103,119,136,.50)',
                        boxShadow: 'inset 0 1px rgba(255,255,255,.30)'
                    } }>
                    <FeedSubastasView visible={ visible } />
                </Base>

                { activa &&
                    <Base
                        className="p-2 rounded"
                        style={ {
                            minHeight: '48px',
                            background: 'rgba(179,190,202,.93)',
                            border: '1px solid rgba(94,111,128,.58)',
                            boxShadow: 'inset 0 1px rgba(255,255,255,.34)'
                        } }>
                        <Base className="d-flex align-items-center" style={ { gap: '6px' } }>
                            <input
                                type="number"
                                min={ pujaMinima }
                                value={ cantidadPuja }
                                onChange={ event => setCantidadPuja(event.target.value) }
                                placeholder={ `${ pujaMinima }` }
                                style={ {
                                    flex: 1,
                                    minWidth: 0,
                                    height: '30px',
                                    padding: '0 8px',
                                    background: 'rgba(255,255,255,.96)',
                                    border: '1px solid rgba(72,88,105,.70)',
                                    borderRadius: '4px'
                                } }
                            />

                            <Button disabled={ procesandoPuja } onClick={ onPujar }>
                                { procesandoPuja ? 'Pujando...' : 'Pujar' }
                            </Button>
                        </Base>

                        { mensajePuja &&
                            <Text small className="mt-1">{ mensajePuja }</Text> }
                    </Base> }
            </Column>

            <Column size={ 4 } gap={ 2 } style={ { minHeight: 0 } }>
                <Base
                    className="p-2 rounded"
                    style={ {
                        height: '100%',
                        minHeight: 0,
                        background: 'linear-gradient(180deg, rgba(196,205,215,.92), rgba(179,190,202,.92))',
                        border: '1px solid rgba(98,114,131,.56)',
                        boxShadow: 'inset 0 1px rgba(255,255,255,.38)'
                    } }>
                    <Base
                        className="d-flex align-items-center justify-content-center rounded"
                        style={ {
                            height: activa ? '235px' : '200px',
                            background: 'rgba(232,237,241,.70)',
                            border: '1px solid rgba(125,139,153,.38)'
                        } }>
                        { activa && spriteId > 0
                            ? <SubastasFurniImageSeguro
                                productType={ tipo.toLowerCase() === 'i' ? 'i' : 's' }
                                productClassId={ spriteId }
                                scale={ 2 }
                            />
                            : <Column alignItems="center" gap={ 1 }>
                                <Text fontSize={ 3 } fontWeight="bold">
                                    { 'Pr\u00f3xima subasta' }
                                </Text>
                                <Text small>
                                    { 'Aqu\u00ed se muestra el pr\u00f3ximo furni.' }
                                </Text>
                            </Column> }
                    </Base>

                    <Column gap={ 1 } className="mt-2">
                        <Text fontSize={ 4 } fontWeight="bold">
                            { activa ? objeto : 'Pr\u00f3xima subasta' }
                        </Text>

                        { activa
                            ? <>
                                <Text small>Vendedor: { vendedor }</Text>

                                <Base
                                    className="d-flex align-items-center justify-content-between px-2 py-1 mt-1 rounded"
                                    style={ { background: 'rgba(255,255,255,.34)' } }>
                                    <Text small>{ 'Puja m\u00ednima' }</Text>
                                    <Text fontWeight="bold">
                                        { pujaMinima } { 'cr\u00e9ditos' }
                                    </Text>
                                </Base>

                                <Base
                                    className="d-flex align-items-center justify-content-between px-2 py-1 rounded"
                                    style={ { background: 'rgba(255,255,255,.34)' } }>
                                    <Text small>Puja actual</Text>
                                    <Text fontWeight="bold">
                                        { pujaActual > 0
                                            ? <>{ pujaActual } { 'cr\u00e9ditos' }</>
                                            : 'Sin pujas' }
                                    </Text>
                                </Base>

                                <Base
                                    className="d-flex align-items-center justify-content-between px-2 py-1 rounded"
                                    style={ { background: 'rgba(255,255,255,.34)' } }>
                                    <Text small>Tiempo restante</Text>
                                    <Text fontWeight="bold">{ segundosRestantes } s</Text>
                                </Base>
                            </>
                            : <Text small>
                                { 'Aqu\u00ed se muestra el pr\u00f3ximo furni de la cola.' }
                            </Text> }
                    </Column>
                </Base>
            </Column>
        </Grid>
    );
};