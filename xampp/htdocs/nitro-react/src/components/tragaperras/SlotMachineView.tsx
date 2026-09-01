import { SlotMachineCloseEvent, SlotMachineOpenEvent, SlotMachineResultEvent, SlotMachineStateEvent } from '@nitrots/nitro-renderer';
import { FC, useEffect, useRef, useState } from 'react';
import { PlaySound, SpinSlotMachine } from '../../api';
import { LayoutCurrencyIcon, NitroCardContentView, NitroCardHeaderView, NitroCardView } from '../../common';
import { useMessageEvent } from '../../hooks/events';
import './SlotMachineView.scss';

type ResultState = 'idle' | 'spinning' | 'lose' | 'win' | 'jackpot' | 'error';

const SLOT_SOUND_PULL = 'holo_slot_pull';
const SLOT_SOUND_SPIN = 'holo_slot_spin';
const SLOT_SOUND_WIN = 'holo_slot_win';
const SLOT_SOUND_JACKPOT = 'holo_slot_jackpot';

interface SlotSymbolProps
{
    id: number;
    spinning: boolean;
}

interface CurrencyAmountProps
{
    amount: number;
    currencyType: number;
    className?: string;
}

const CurrencyAmount: FC<CurrencyAmountProps> = props =>
{
    const { amount, currencyType, className = '' } = props;

    return (
        <span className={ `slot-credit-amount ${ className }`.trim() }>
            <span>{ amount.toLocaleString('es-ES') }</span>
            <LayoutCurrencyIcon
                type={ currencyType }
                classNames={ [ 'slot-credit-icon' ] } />
        </span>
    );
};

const SlotSymbolView: FC<SlotSymbolProps> = props =>
{
    const { id, spinning } = props;

    if(spinning)
    {
        return (
            <div className="slot-symbol-spin" aria-hidden="true">
                <span />
                <span />
                <span />
            </div>
        );
    }

    switch(id)
    {
        case 0:
            return (
                <svg className="slot-symbol-svg" viewBox="0 0 24 24" shapeRendering="crispEdges" aria-label="Cerezas">
                    <rect x="11" y="3" width="2" height="6" className="px-green" />
                    <rect x="12" y="3" width="6" height="2" className="px-green" />
                    <rect x="16" y="4" width="2" height="3" className="px-green" />
                    <rect x="6" y="9" width="6" height="2" className="px-red-dark" />
                    <rect x="4" y="11" width="9" height="7" className="px-red" />
                    <rect x="6" y="18" width="5" height="2" className="px-red-dark" />
                    <rect x="14" y="10" width="6" height="2" className="px-red-dark" />
                    <rect x="12" y="12" width="9" height="7" className="px-red" />
                    <rect x="14" y="19" width="5" height="2" className="px-red-dark" />
                    <rect x="6" y="12" width="2" height="2" className="px-white" />
                    <rect x="14" y="13" width="2" height="2" className="px-white" />
                </svg>
            );

        case 1:
            return (
                <svg className="slot-symbol-svg" viewBox="0 0 24 24" shapeRendering="crispEdges" aria-label="Limon">
                    <rect x="15" y="4" width="5" height="3" className="px-green" />
                    <rect x="7" y="6" width="10" height="2" className="px-yellow-dark" />
                    <rect x="4" y="8" width="15" height="9" className="px-yellow" />
                    <rect x="6" y="17" width="10" height="2" className="px-yellow-dark" />
                    <rect x="7" y="9" width="3" height="2" className="px-white" />
                </svg>
            );

        case 2:
            return (
                <svg className="slot-symbol-svg" viewBox="0 0 24 24" shapeRendering="crispEdges" aria-label="Campana">
                    <rect x="10" y="4" width="4" height="2" className="px-gold-dark" />
                    <rect x="8" y="6" width="8" height="2" className="px-gold" />
                    <rect x="6" y="8" width="12" height="8" className="px-gold" />
                    <rect x="4" y="16" width="16" height="3" className="px-gold-dark" />
                    <rect x="10" y="19" width="4" height="2" className="px-gold-dark" />
                    <rect x="8" y="9" width="2" height="4" className="px-white" />
                </svg>
            );

        case 3:
            return (
                <svg className="slot-symbol-svg" viewBox="0 0 24 24" shapeRendering="crispEdges" aria-label="BAR">
                    <rect x="2" y="7" width="20" height="10" className="px-black" />

                    <rect x="4" y="9" width="1" height="6" className="px-white" />
                    <rect x="5" y="9" width="2" height="1" className="px-white" />
                    <rect x="5" y="11" width="2" height="1" className="px-white" />
                    <rect x="5" y="14" width="2" height="1" className="px-white" />
                    <rect x="7" y="10" width="1" height="1" className="px-white" />
                    <rect x="7" y="12" width="1" height="2" className="px-white" />

                    <rect x="10" y="10" width="1" height="5" className="px-white" />
                    <rect x="12" y="10" width="1" height="5" className="px-white" />
                    <rect x="11" y="9" width="1" height="1" className="px-white" />
                    <rect x="10" y="12" width="3" height="1" className="px-white" />

                    <rect x="15" y="9" width="1" height="6" className="px-white" />
                    <rect x="16" y="9" width="2" height="1" className="px-white" />
                    <rect x="16" y="11" width="2" height="1" className="px-white" />
                    <rect x="18" y="10" width="1" height="1" className="px-white" />
                    <rect x="17" y="12" width="1" height="1" className="px-white" />
                    <rect x="18" y="13" width="1" height="2" className="px-white" />
                </svg>
            );

        case 4:
            return (
                <svg className="slot-symbol-svg" viewBox="0 0 24 24" shapeRendering="crispEdges" aria-label="Diamante">
                    <rect x="8" y="4" width="8" height="2" className="px-cyan-light" />
                    <rect x="5" y="6" width="14" height="3" className="px-cyan" />
                    <rect x="3" y="9" width="18" height="3" className="px-cyan-dark" />
                    <rect x="5" y="12" width="14" height="3" className="px-cyan" />
                    <rect x="8" y="15" width="8" height="3" className="px-cyan" />
                    <rect x="10" y="18" width="4" height="2" className="px-cyan-dark" />
                    <rect x="7" y="7" width="3" height="2" className="px-white" />
                </svg>
            );

        case 5:
            return (
                <svg className="slot-symbol-svg" viewBox="0 0 24 24" shapeRendering="crispEdges" aria-label="Siete">
                    <rect x="5" y="4" width="14" height="4" className="px-red" />
                    <rect x="15" y="8" width="4" height="4" className="px-red-dark" />
                    <rect x="12" y="12" width="5" height="4" className="px-red" />
                    <rect x="10" y="16" width="4" height="5" className="px-red-dark" />
                    <rect x="7" y="6" width="7" height="2" className="px-white" />
                </svg>
            );

        default:
            return (
                <svg className="slot-symbol-svg" viewBox="0 0 24 24" shapeRendering="crispEdges" aria-label="Jackpot">
                    <polygon points="12,3 14,9 20,9 15,13 17,20 12,16 7,20 9,13 4,9 10,9" className="px-gold" />
                    <rect x="11" y="7" width="2" height="8" className="px-white" />
                </svg>
            );
    }
};

export const SlotMachineView: FC<{}> = () =>
{
    const [ isVisible, setIsVisible ] = useState(false);
    const [ itemId, setItemId ] = useState(0);
    const [ bet, setBet ] = useState(5);
    const [ jackpot, setJackpot ] = useState(1000);
    const [ balance, setBalance ] = useState(0);
    const [ currencyKey, setCurrencyKey ] = useState('credits');
    const [ currencyType, setCurrencyType ] = useState(-1);
    const [ reels, setReels ] = useState<number[]>([ 0, 1, 5 ]);
    const [ spinning, setSpinning ] = useState(false);
    const [ settling, setSettling ] = useState(false);
    const [ spinVisualKey, setSpinVisualKey ] = useState(0);
    const spinRequestInFlight = useRef(false);
    const itemIdRef = useRef(0);
    const currencyKeyRef = useRef('credits');
    const [ resultState, setResultState ] = useState<ResultState>('idle');
    const [ resultMessage, setResultMessage ] = useState('');
    const [ prize, setPrize ] = useState(0);

    const close = () =>
    {
        spinRequestInFlight.current = false;
        itemIdRef.current = 0;
        setIsVisible(false);
        setSpinning(false);
        setSettling(false);
    };

    useMessageEvent(SlotMachineOpenEvent, (event: SlotMachineOpenEvent) =>
    {
        const parser = event.getParser();

        // Un OPEN autoritativo inicia una sesion cliente limpia.
        spinRequestInFlight.current = false;

        itemIdRef.current = parser.itemId;
        setItemId(parser.itemId);
        setBet(parser.bet);
        setJackpot(parser.jackpot);
        setBalance(parser.balance);
        currencyKeyRef.current = parser.currencyKey;
        setCurrencyKey(parser.currencyKey);
        setCurrencyType(parser.currencyType);
        setPrize(0);
        setResultState('idle');
        setResultMessage('');
        setSpinning(false);
        setSettling(false);
        setIsVisible(true);
    });

    useMessageEvent(SlotMachineCloseEvent, (event: SlotMachineCloseEvent) =>
    {
        const parser = event.getParser();

        // Un CLOSE retrasado de una sesion anterior no puede cerrar
        // una maquina nueva abierta despues.
        if(parser.itemId !== itemIdRef.current) return;

        close();
    });
    useMessageEvent(SlotMachineStateEvent, (event: SlotMachineStateEvent) =>
    {
        const parser = event.getParser();

        // El servidor solo difunde a sesiones de la misma moneda; el ref
        // protege ademas frente a paquetes en vuelo al cambiar de maquina.
        if(parser.currencyKey !== currencyKeyRef.current) return;

        setJackpot(parser.jackpot);
    });

    useMessageEvent(SlotMachineResultEvent, (event: SlotMachineResultEvent) =>
    {
        const parser = event.getParser();

        spinRequestInFlight.current = false;

        setBalance(parser.balanceAfter);
        setJackpot(parser.jackpotAfter);

        if(!parser.success)
        {
            setSpinning(false);
            setSettling(false);
            setPrize(0);
            setResultState('error');
            setResultMessage(parser.message || 'No se pudo completar la tirada.');
            return;
        }

        const won = parser.normalPrize + parser.jackpotPrize;

        // Estado autoritativo: se aplica AHORA, no despues de una animacion.
        setReels([ parser.symbol1, parser.symbol2, parser.symbol3 ]);
        setPrize(won);
        setSpinning(false);
        setSettling(true);
        setResultMessage('');

        if(parser.jackpotHit)
        {
            setResultState('jackpot');
        }
        else if(won > 0)
        {
            setResultState('win');
        }
        else
        {
            setResultState('lose');
        }
    });

    const spin = () =>
    {
        if(
            spinRequestInFlight.current ||
            spinning ||
            settling ||
            itemId <= 0 ||
            balance < bet
        ) return;

        spinRequestInFlight.current = true;

        setSettling(false);
        setSpinning(true);
        setSpinVisualKey(value => value + 1);
        setPrize(0);
        setResultState('spinning');
        setResultMessage('');

        // Restauramos el orden de audio original. Cada sonido esta aislado:
        // aunque falle, la tirada funcional sigue enviandose.
        try
        {
            PlaySound(SLOT_SOUND_PULL);
        }
        catch(error)
        {
        }

        try
        {
            PlaySound(SLOT_SOUND_SPIN);
        }
        catch(error)
        {
        }

        try
        {
            SpinSlotMachine(itemId);
        }
        catch(error)
        {
            spinRequestInFlight.current = false;
            setSpinning(false);
            setSettling(false);
            setResultState('error');
            setResultMessage('No se pudo enviar la tirada al servidor.');
            return;
        }
    };

    if(!isVisible) return null;

    const interactionLocked = spinning || settling;
    const canSpin = !interactionLocked && itemId > 0 && balance >= bet;

    const currencyLabel =
        currencyKey === 'diamonds'
            ? 'Diamantes'
            : currencyKey === 'duckets'
                ? 'Duckets'
                : 'Cr\u00e9ditos';

    const renderResult = () =>
    {
        if(interactionLocked)
        {
            return <span className="slot-result-message">Girando...</span>;
        }

        switch(resultState)
        {
            case 'spinning':
                return <span className="slot-result-message">Girando...</span>;

            case 'lose':
                return <span className="slot-result-message">Sin premio.</span>;

            case 'win':
                return (
                    <span className="slot-result-message slot-result-prize">
                        <span>Premio</span>
                        <CurrencyAmount amount={ prize } currencyType={ currencyType } />
                    </span>
                );

            case 'jackpot':
                return (
                    <span className="slot-result-message slot-result-prize">
                        <span>JACKPOT</span>
                        <CurrencyAmount amount={ prize } currencyType={ currencyType } />
                    </span>
                );

            case 'error':
                return <span className="slot-result-message">{ resultMessage }</span>;

            default:
                return <span className="slot-result-message">Pulsa TIRAR para jugar.</span>;
        }
    };

    return (
        <NitroCardView
            uniqueKey="holo-slot-machine"
            className="nitro-slot-machine"
            theme="primary-slim"
            style={ { width: '414px' } }>
            <NitroCardHeaderView
                headerText={ `Tragaperras \u00b7 ${ currencyLabel }` }
                onCloseClick={ close } />
            <NitroCardContentView gap={ 0 } className="slot-machine-content">
                <div className="slot-machine-shell" data-ui="holo-slot-v2b">
                    <div className="slot-jackpot-box">
                        <span className="slot-jackpot-screw is-top-left" aria-hidden="true" />
                        <span className="slot-jackpot-screw is-top-right" aria-hidden="true" />
                        <span className="slot-jackpot-screw is-bottom-left" aria-hidden="true" />
                        <span className="slot-jackpot-screw is-bottom-right" aria-hidden="true" />
                        <div className="slot-jackpot-label">JACKPOT</div>
                        <CurrencyAmount amount={ jackpot } currencyType={ currencyType } className="slot-jackpot-value" />
                    </div>

                    <div className={ `slot-reels${ spinning ? ' is-spinning' : '' }${ settling ? ' is-settling' : '' }${ !interactionLocked && (resultState === 'win' || resultState === 'jackpot') ? ' is-winning' : '' }` }>
                        { reels.map((symbolId, index) =>
                            <div className="slot-reel" key={ index }>
                                <div className="slot-reel-window">
                                    <SlotSymbolView id={ symbolId } spinning={ false } />

                                    { (spinning || settling) &&
                                        <div
                                            key={ `${ spinVisualKey }-${ index }-${ spinning ? 'spin' : 'settle' }` }
                                            className={ `slot-reel-overlay${ spinning ? ' is-spinning' : ' is-settling' }` }
                                            onAnimationEnd={ event =>
                                            {
                                                if(
                                                    settling &&
                                                    index === 2 &&
                                                    event.currentTarget === event.target
                                                )
                                                {
                                                    setSettling(false);

                                                    if(resultState === 'jackpot')
                                                    {
                                                        PlaySound(SLOT_SOUND_JACKPOT);
                                                    }
                                                    else if(resultState === 'win')
                                                    {
                                                        PlaySound(SLOT_SOUND_WIN);
                                                    }
                                                }
                                            } }>
                                            <SlotSymbolView id={ symbolId } spinning={ true } />
                                        </div> }
                                </div>
                            </div>) }
                    </div>

                    <div className={ `slot-result is-${ interactionLocked ? 'spinning' : resultState }` }>
                        { renderResult() }
                    </div>

                    <div className="slot-machine-readouts">
                        <div className="slot-readout">
                            <div className="slot-readout-label">SALDO</div>
                            <CurrencyAmount amount={ balance } currencyType={ currencyType } className="slot-readout-value" />
                        </div>

                        <div className="slot-readout">
                            <div className="slot-readout-label">APUESTA</div>
                            <CurrencyAmount amount={ bet } currencyType={ currencyType } className="slot-readout-value" />
                        </div>
                    </div>

                    <button
                        type="button"
                        className="slot-spin-button"
                        disabled={ !canSpin }
                        onClick={ spin }>
                        { interactionLocked
                            ? <span>GIRANDO...</span>
                            : <>
                                <span>TIRAR</span>
                                <span className="slot-spin-divider" />
                                <CurrencyAmount amount={ bet } currencyType={ currencyType } className="slot-spin-price" />
                            </> }
                    </button>

                    { balance < bet &&
                        <div className="slot-insufficient">
                            Saldo insuficiente para esta apuesta.
                        </div> }
                </div>
            </NitroCardContentView>
        </NitroCardView>
    );
};