import { BadgePointLimitsEvent, CerrarSesionInventarioComposer, ConfigurarPatronInventarioComposer, ConsultarBloqueoInventarioComposer, EstadoBloqueoInventarioEvent, ILinkEventTracker, IRoomSession, ResultadoConfigurarPatronEvent, ResultadoPatronInventarioEvent, RoomEngineObjectEvent, RoomEngineObjectPlacedEvent, RoomPreviewer, RoomSessionEvent, VerificarPatronInventarioComposer } from '@nitrots/nitro-renderer';
import { FC, useCallback, useEffect, useState } from 'react';
import { AddEventLinkTracker, GetLocalization, GetRoomEngine, GroupItem, LocalizeText, RemoveLinkEventTracker, SendMessageComposer, isObjectMoverRequested, setObjectMoverRequested } from '../../api';
import { NitroCardContentView, NitroCardHeaderView, NitroCardTabsItemView, NitroCardTabsView, NitroCardView } from '../../common';
import { useInventoryBadges, useInventoryFurni, useInventoryTrade, useInventoryUnseenTracker, useMessageEvent, useRoomEngineEvent, useRoomSessionManagerEvent } from '../../hooks';
import { TABS, TAB_BADGES, TAB_BOTS, TAB_FURNITURE, TAB_PETS, UNSEEN_CATEGORIES } from './constants';
import { InventoryCategoryFilterView } from './views/InventoryCategoryFilterView';
import { InventoryBadgeView } from './views/badge/InventoryBadgeView';
import { InventoryBotView } from './views/bot/InventoryBotView';
import { InventoryFurnitureDeleteView } from './views/furniture/InventoryFurnitureDeleteView';
import { InventoryFurnitureView } from './views/furniture/InventoryFurnitureView';
import { InventoryTradeView } from './views/furniture/InventoryTradeView';
import { InventoryPatternLockView } from './views/InventoryPatternLockView';
import { InventoryPetView } from './views/pet/InventoryPetView';

export const InventoryView: FC<{}> = props =>
{
    const [ isVisible, setIsVisible ] = useState(false);
    const [ lockScreenVisible, setLockScreenVisible ] = useState(false);
    const [ isCheckingLock, setIsCheckingLock ] = useState(false);
    const [ isVerifying, setIsVerifying ] = useState(false);
    const [ lockEnabled, setLockEnabled ] = useState(false);
    const [ blockedSeconds, setBlockedSeconds ] = useState(0);
    const [ lockError, setLockError ] = useState('');

    const [ settingsVisible, setSettingsVisible ] = useState(false);
    const [ settingsStep, setSettingsStep ] = useState('');
    const [ settingsError, setSettingsError ] = useState('');
    const [ firstPattern, setFirstPattern ] = useState('');
    const [ currentPattern, setCurrentPattern ] = useState('');
    const [ isConfiguring, setIsConfiguring ] = useState(false);
    const [ pendingConfigAction, setPendingConfigAction ] = useState<number>(null);

    const [ currentTab, setCurrentTab ] = useState<string>(TABS[0]);
    const [ roomSession, setRoomSession ] = useState<IRoomSession>(null);
    const [ roomPreviewer, setRoomPreviewer ] = useState<RoomPreviewer>(null);
    const [ filteredGroupItems, setFilteredGroupItems ] = useState<GroupItem[]>([]);
    const [ filteredBadgeCodes, setFilteredBadgeCodes ] = useState<string[]>([]);

    const { isTrading = false, stopTrading = null } = useInventoryTrade();
    const { getCount = null } = useInventoryUnseenTracker();
    const { groupItems = [] } = useInventoryFurni();
    const { badgeCodes = [] } = useInventoryBadges();

    const resetSettings = useCallback(() =>
    {
        setSettingsVisible(false);
        setSettingsStep('');
        setSettingsError('');
        setFirstPattern('');
        setCurrentPattern('');
        setIsConfiguring(false);
        setPendingConfigAction(null);
    }, []);

    const closeInventory = useCallback((cancelTrade: boolean = false) =>
    {
        if(cancelTrade && isTrading && stopTrading) stopTrading();

        SendMessageComposer(new CerrarSesionInventarioComposer());

        setIsVisible(false);
        setLockScreenVisible(false);
        setIsCheckingLock(false);
        setIsVerifying(false);
        setLockError('');
        setBlockedSeconds(0);
        resetSettings();
    }, [ isTrading, stopTrading, resetSettings ]);

    const requestInventoryOpen = useCallback(() =>
    {
        if(isVisible || lockScreenVisible || isCheckingLock) return;

        setIsCheckingLock(true);
        setLockError('');

        SendMessageComposer(new ConsultarBloqueoInventarioComposer());
    }, [ isVisible, lockScreenVisible, isCheckingLock ]);

    const verifyPattern = (pattern: string) =>
    {
        if(isVerifying || blockedSeconds > 0) return;

        setIsVerifying(true);
        setLockError('');

        SendMessageComposer(new VerificarPatronInventarioComposer(pattern));
    };

    const sendConfiguration = (action: number, oldPattern: string, newPattern: string) =>
    {
        if(isConfiguring || blockedSeconds > 0) return;

        setIsConfiguring(true);
        setPendingConfigAction(action);
        setSettingsError('');

        SendMessageComposer(
            new ConfigurarPatronInventarioComposer(
                action,
                oldPattern,
                newPattern
            )
        );
    };

    const openSettings = () =>
    {
        setSettingsError('');
        setFirstPattern('');
        setCurrentPattern('');

        setSettingsStep(lockEnabled ? 'menu' : 'enable-first');
        setSettingsVisible(true);
    };

    const handleSettingsBack = () =>
    {
        setSettingsError('');
        setFirstPattern('');
        setCurrentPattern('');
        setIsConfiguring(false);
        setPendingConfigAction(null);

        if(lockEnabled)
        {
            setSettingsStep('menu');
            return;
        }

        resetSettings();
    };

    const handleSettingsPattern = (pattern: string) =>
    {
        switch(settingsStep)
        {
            case 'enable-first':
                setFirstPattern(pattern);
                setSettingsError('');
                setSettingsStep('enable-confirm');
                return;

            case 'enable-confirm':
                if(pattern !== firstPattern)
                {
                    setSettingsError('Los patrones no coinciden. Inténtalo otra vez.');
                    return;
                }

                sendConfiguration(0, '', pattern);
                return;

            case 'change-current':
                setCurrentPattern(pattern);
                setSettingsError('');
                setSettingsStep('change-new');
                return;

            case 'change-new':
                setFirstPattern(pattern);
                setSettingsError('');
                setSettingsStep('change-confirm');
                return;

            case 'change-confirm':
                if(pattern !== firstPattern)
                {
                    setSettingsError('Los patrones nuevos no coinciden.');
                    return;
                }

                sendConfiguration(1, currentPattern, pattern);
                return;

            case 'disable-current':
                sendConfiguration(2, pattern, '');
                return;
        }
    };

    useEffect(() =>
    {
        if(blockedSeconds <= 0) return;

        const timer = window.setTimeout(
            () => setBlockedSeconds(value => Math.max(0, value - 1)),
            1000
        );

        return () => window.clearTimeout(timer);
    }, [ blockedSeconds ]);

    useRoomEngineEvent<RoomEngineObjectPlacedEvent>(RoomEngineObjectEvent.PLACED, event =>
    {
        if(!isObjectMoverRequested()) return;

        setObjectMoverRequested(false);

        if(!event.placedInRoom) requestInventoryOpen();
    });

    useRoomSessionManagerEvent<RoomSessionEvent>([
        RoomSessionEvent.CREATED,
        RoomSessionEvent.ENDED
    ], event =>
    {
        switch(event.type)
        {
            case RoomSessionEvent.CREATED:
                setRoomSession(event.session);
                return;

            case RoomSessionEvent.ENDED:
                setRoomSession(null);
                closeInventory(false);
                return;
        }
    });

    useMessageEvent<BadgePointLimitsEvent>(BadgePointLimitsEvent, event =>
    {
        const parser = event.getParser();

        for(const data of parser.data)
        {
            GetLocalization().setBadgePointLimit(data.badgeId, data.limit);
        }
    });

    useMessageEvent<EstadoBloqueoInventarioEvent>(EstadoBloqueoInventarioEvent, event =>
    {
        const parser = event.getParser();

        setIsCheckingLock(false);
        setLockEnabled(parser.enabled);
        setBlockedSeconds(parser.blockedSeconds);

        if(!parser.enabled || parser.unlocked)
        {
            setLockScreenVisible(false);
            setLockError('');
            setIsVisible(true);
            return;
        }

        setIsVisible(false);
        setLockScreenVisible(true);
    });

    useMessageEvent<ResultadoPatronInventarioEvent>(ResultadoPatronInventarioEvent, event =>
    {
        const parser = event.getParser();

        setIsVerifying(false);
        setBlockedSeconds(parser.blockedSeconds);

        if(parser.correcto)
        {
            setLockError('');
            setLockScreenVisible(false);
            setIsVisible(true);
            return;
        }

        setLockError(
            parser.blockedSeconds > 0
                ? ''
                : 'Patrón incorrecto.'
        );
    });

    useMessageEvent<ResultadoConfigurarPatronEvent>(ResultadoConfigurarPatronEvent, event =>
    {
        const parser = event.getParser();
        const action = pendingConfigAction;

        setIsConfiguring(false);
        setBlockedSeconds(parser.blockedSeconds);

        if(parser.success)
        {
            setLockEnabled(parser.enabled);
            setSettingsError('');

            if(action === 2)
            {
                resetSettings();
                return;
            }

            SendMessageComposer(new CerrarSesionInventarioComposer());

            setIsVisible(false);
            setLockScreenVisible(false);
            resetSettings();
            return;
        }

        switch(parser.codigo)
        {
            case 1:
                setSettingsError('El patrón nuevo no es válido.');
                return;

            case 2:
                setSettingsError('El estado de la protección ha cambiado. Cierra y vuelve a abrir el inventario.');
                return;

            case 3:
                setSettingsError('El patrón actual es incorrecto.');

                if(action === 1)
                {
                    setCurrentPattern('');
                    setSettingsStep('change-current');
                }
                else if(action === 2)
                {
                    setSettingsStep('disable-current');
                }

                return;

            case 4:
                setSettingsError('');

                if(action === 1)
                {
                    setCurrentPattern('');
                    setSettingsStep('change-current');
                }
                else if(action === 2)
                {
                    setSettingsStep('disable-current');
                }

                return;

            default:
                setSettingsError('No se pudo cambiar la protección del inventario.');
                return;
        }
    });

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
                        requestInventoryOpen();
                        return;

                    case 'hide':
                        setIsVisible(false);
                        return;

                    case 'toggle':
                        if(isVisible || lockScreenVisible)
                        {
                            closeInventory(false);
                        }
                        else
                        {
                            requestInventoryOpen();
                        }

                        return;
                }
            },
            eventUrlPrefix: 'inventory/'
        };

        AddEventLinkTracker(linkTracker);

        return () => RemoveLinkEventTracker(linkTracker);
    }, [
        isVisible,
        lockScreenVisible,
        requestInventoryOpen,
        closeInventory
    ]);

    useEffect(() =>
    {
        setRoomPreviewer(
            new RoomPreviewer(
                GetRoomEngine(),
                ++RoomPreviewer.PREVIEW_COUNTER
            )
        );

        return () =>
        {
            setRoomPreviewer(prevValue =>
            {
                if(prevValue) prevValue.dispose();

                return null;
            });
        };
    }, []);

    useEffect(() =>
    {
        if(
            isTrading &&
            !isVisible &&
            !lockScreenVisible &&
            !isCheckingLock
        )
        {
            requestInventoryOpen();
        }
    }, [
        isTrading,
        isVisible,
        lockScreenVisible,
        isCheckingLock,
        requestInventoryOpen
    ]);

    if(lockScreenVisible)
    {
        return (
            <NitroCardView
                uniqueKey={ 'inventory' }
                theme="holo-classic"
                className="nitro-inventory no-resize"
            >
                <NitroCardHeaderView
                    headerText="Inventario protegido"
                    onCloseClick={ () => closeInventory(true) }
                />

                <NitroCardContentView overflow="hidden">
                    <InventoryPatternLockView
                        key="inventory-unlock"
                        title="Dibuja tu patrón"
                        subtitle="Desbloquea el inventario para continuar."
                        error={ lockError }
                        blockedSeconds={ blockedSeconds }
                        disabled={ isVerifying }
                        onComplete={ verifyPattern }
                    />
                </NitroCardContentView>
            </NitroCardView>
        );
    }

    if(!isVisible) return null;

    let settingsTitle = '';
    let settingsSubtitle = '';

    switch(settingsStep)
    {
        case 'enable-first':
            settingsTitle = 'Crea un patrón';
            settingsSubtitle = 'Une al menos 4 puntos.';
            break;

        case 'enable-confirm':
            settingsTitle = 'Confirma el patrón';
            settingsSubtitle = 'Dibuja exactamente el mismo patrón.';
            break;

        case 'change-current':
            settingsTitle = 'Patrón actual';
            settingsSubtitle = 'Confirma tu patrón actual.';
            break;

        case 'change-new':
            settingsTitle = 'Nuevo patrón';
            settingsSubtitle = 'Dibuja el nuevo patrón.';
            break;

        case 'change-confirm':
            settingsTitle = 'Confirma el nuevo patrón';
            settingsSubtitle = 'Repite el nuevo patrón.';
            break;

        case 'disable-current':
            settingsTitle = 'Desactivar protección';
            settingsSubtitle = 'Dibuja tu patrón actual para confirmar.';
            break;
    }

    return (
        <NitroCardView
            uniqueKey={ 'inventory' }
            className={ `${ isTrading ? 'nitro-inventory trading no-resize' : 'nitro-inventory' }` }
            theme="holo-classic"
        >
            <NitroCardHeaderView
                headerText={ LocalizeText('inventory.title') }
                onCloseClick={ () => closeInventory(true) }
            />

            { settingsVisible
                ?
                <>
                    { settingsStep === 'menu'
                        ?
                        <NitroCardContentView overflow="hidden">
                            <div className="d-flex align-items-center justify-content-center h-100 p-3">
                                <div
                                    style={{
                                        width: 270,
                                        padding: '16px 18px',
                                        borderRadius: 10,
                                        background: '#eef2f4',
                                        border: '1px solid #c8d0d5',
                                        boxShadow: 'inset 0 1px 0 rgba(255,255,255,0.8)',
                                        textAlign: 'center'
                                    }}
                                >
                                    <div
                                        style={{
                                            fontSize: 16,
                                            lineHeight: '19px',
                                            fontWeight: 700,
                                            color: '#29323b',
                                            marginBottom: 5
                                        }}
                                    >
                                        Seguridad del inventario
                                    </div>

                                    <div
                                        style={{
                                            fontSize: 12,
                                            lineHeight: '15px',
                                            color: '#66727c',
                                            marginBottom: 15
                                        }}
                                    >
                                        La protección mediante patrón está activa.
                                    </div>

                                    <div className="d-flex flex-column align-items-center">
                                        <button
                                            type="button"
                                            className="btn btn-primary mb-2"
                                            style={{ minWidth: 150 }}
                                            onClick={ () =>
                                            {
                                                setSettingsError('');
                                                setCurrentPattern('');
                                                setFirstPattern('');
                                                setSettingsStep('change-current');
                                            } }
                                        >
                                            Cambiar patrón
                                        </button>

                                        <button
                                            type="button"
                                            className="btn btn-danger mb-3"
                                            style={{ minWidth: 150 }}
                                            onClick={ () =>
                                            {
                                                setSettingsError('');
                                                setCurrentPattern('');
                                                setSettingsStep('disable-current');
                                            } }
                                        >
                                            Desactivar protección
                                        </button>

                                        <button
                                            type="button"
                                            className="btn btn-secondary"
                                            style={{ minWidth: 90 }}
                                            onClick={ resetSettings }
                                        >
                                            Volver
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </NitroCardContentView>
                        :
                        <NitroCardContentView overflow="hidden">
                            <InventoryPatternLockView
                                key={ settingsStep }
                                title={ settingsTitle }
                                subtitle={ settingsSubtitle }
                                error={ settingsError }
                                blockedSeconds={ blockedSeconds }
                                disabled={ isConfiguring }
                                onComplete={ handleSettingsPattern }
                                onCancel={ handleSettingsBack }
                            />
                        </NitroCardContentView> }
                </>
                :
                <>
                    <div className="d-flex justify-content-end px-2 pt-2">
                        <button
                            type="button"
                            className="btn btn-sm btn-outline-secondary"
                            onClick={ openSettings }
                        >
                            { lockEnabled ? 'Patrón activo' : 'Proteger inventario' }
                        </button>
                    </div>

                    <NitroCardTabsView>
                        { TABS.map((name, index) =>
                        {
                            return (
                                <NitroCardTabsItemView
                                    key={ index }
                                    isActive={ currentTab === name }
                                    onClick={ event => setCurrentTab(name) }
                                    count={ getCount(UNSEEN_CATEGORIES[index]) }
                                >
                                    { LocalizeText(name) }
                                </NitroCardTabsItemView>
                            );
                        }) }
                    </NitroCardTabsView>

                    <NitroCardContentView overflow="hidden">
                        { (currentTab !== TAB_PETS && currentTab !== TAB_BOTS) &&
                            <InventoryCategoryFilterView
                                currentTab={ currentTab }
                                groupItems={ groupItems }
                                setGroupItems={ setFilteredGroupItems }
                                badgeCodes={ badgeCodes }
                                setBadgeCodes={ setFilteredBadgeCodes }
                            /> }

                        { currentTab === TAB_FURNITURE &&
                            <InventoryFurnitureView
                                roomSession={ roomSession }
                                roomPreviewer={ roomPreviewer }
                                isTrading={ isTrading }
                                filteredGroupItems={ filteredGroupItems }
                            /> }

                        { currentTab === TAB_PETS &&
                            <InventoryPetView
                                roomSession={ roomSession }
                                roomPreviewer={ roomPreviewer }
                                isTrading={ isTrading }
                            /> }

                        { currentTab === TAB_BADGES &&
                            <InventoryBadgeView
                                filteredBadgeCodes={ filteredBadgeCodes }
                            /> }

                        { currentTab === TAB_BOTS &&
                            <InventoryBotView
                                roomSession={ roomSession }
                                roomPreviewer={ roomPreviewer }
                                isTrading={ isTrading }
                            /> }

                        { isTrading &&
                            <InventoryTradeView
                                currentTab={ currentTab }
                                setCurrentTab={ event => setCurrentTab(event) }
                                cancelTrade={ () => closeInventory(true) }
                            /> }
                    </NitroCardContentView>

                    <InventoryFurnitureDeleteView />
                </> }
        </NitroCardView>
    );
};
