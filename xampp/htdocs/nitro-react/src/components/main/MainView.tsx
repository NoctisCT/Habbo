import { HabboWebTools, ILinkEventTracker, RoomSessionEvent } from '@nitrots/nitro-renderer';
import { FC, useEffect, useState } from 'react';
import { AddEventLinkTracker, GetCommunication, RemoveLinkEventTracker } from '../../api';
import { Base, TransitionAnimation, TransitionAnimationTypes } from '../../common';
import { useRoomSessionManagerEvent } from '../../hooks';
import { AchievementsView } from '../achievements/AchievementsView';
import { SubastasView } from '../subastas/SubastasView';
import { AvatarEditorView } from '../avatar-editor/AvatarEditorView';
import { CameraWidgetView } from '../camera/CameraWidgetView';
import { CampaignView } from '../campaign/CampaignView';
import { CatalogView } from '../catalog/CatalogView';
import { ChatHistoryView } from '../chat-history/ChatHistoryView';
import { FloorplanEditorView } from '../floorplan-editor/FloorplanEditorView';
import { FriendsView } from '../friends/FriendsView';
import { GameCenterView } from '../game-center/GameCenterView';
import { GroupsView } from '../groups/GroupsView';
import { GuideToolView } from '../guide-tool/GuideToolView';
import { HcCenterView } from '../hc-center/HcCenterView';
import { HelpView } from '../help/HelpView';
import { HotelView } from '../hotel-view/HotelView';
import { InventoryView } from '../inventory/InventoryView';
import { ModToolsView } from '../mod-tools/ModToolsView';
import { NavigatorView } from '../navigator/NavigatorView';
import { NitrobubbleHiddenView } from '../nitrobubblehidden/NitrobubbleHiddenView';
import { NitropediaView } from '../nitropedia/NitropediaView';
import { RightSideView } from '../right-side/RightSideView';
import { RoomView } from '../room/RoomView';
import { ToolbarView } from '../toolbar/ToolbarView';
import { UserProfileView } from '../user-profile/UserProfileView';
import { UserSettingsView } from '../user-settings/UserSettingsView';
import { WiredView } from '../wired/WiredView';
import { YoutubeTvView } from '../youtube-tv/YoutubeTvView';

// 🚀 INYECCIÓN DE TU PC DE POKÉMON SALVAJE Y ELEMENTOS MÉDICOS
import { PokemonPCView } from '../PokemonPCView';
import { PokemonEncounterManager } from '../PokemonEncounterManager';
import { PokemonHealingView } from '../PokemonHealingView'; // 🏥 Añadimos tu vista médica desacoplada

// 🌟 RUTA CORREGIDA: Apunta a la raíz de components igual que las otras vistas
import { PokemonMenu } from '../PokemonMenu';

export const MainView: FC<{}> = props => {
    const [isReady, setIsReady] = useState(false);
    const [landingViewVisible, setLandingViewVisible] = useState(true);
    const [isHealingOpen, setIsHealingOpen] = useState(false); // 🏥 Estado aislado de visibilidad de la Enfermera

    useRoomSessionManagerEvent<RoomSessionEvent>(RoomSessionEvent.CREATED, event => setLandingViewVisible(false));
    useRoomSessionManagerEvent<RoomSessionEvent>(RoomSessionEvent.ENDED, event => setLandingViewVisible(event.openLandingView));

    useEffect(() => {
        setIsReady(true);

        GetCommunication().connection.onReady();
    }, []);

    useEffect(() => {
        const linkTracker: ILinkEventTracker = {
            linkReceived: (url: string) => {
                const parts = url.split('/');

                if (parts.length < 2) return;

                switch (parts[1]) {
                    case 'open':
                        if (parts.length > 2) {
                            switch (parts[2]) {
                                case 'credits':
                                    //HabboWebTools.openWebPageAndMinimizeClient(this._windowManager.getProperty(ExternalVariables.WEB_SHOP_RELATIVE_URL));
                                    break;
                                default: {
                                    const name = parts[2];
                                    HabboWebTools.openHabblet(name);
                                }
                            }
                        }
                        return;

                    // 🏥 ESCUCHADOR: Permite a tu Java abrir el menú médico ejecutando un "event:pokemon/healing"
                    case 'pokemon':
                        if (parts.length > 2 && parts[2] === 'healing') {
                            setIsHealingOpen(true);
                        }
                        return;
                }
            },
            eventUrlPrefix: 'habblet/'
        };

        AddEventLinkTracker(linkTracker);

        return () => RemoveLinkEventTracker(linkTracker);
    }, []);

    return (
        <Base fit>
            <TransitionAnimation type={TransitionAnimationTypes.FADE_IN} inProp={landingViewVisible} timeout={300}>
                <HotelView />
            </TransitionAnimation>
            <ToolbarView isInRoom={!landingViewVisible} />
            <ModToolsView />
            <RoomView />
            <ChatHistoryView />
            <WiredView />
            <AvatarEditorView />
            <AchievementsView />
 <SubastasView />
            <NavigatorView />
            <NitrobubbleHiddenView />
            <InventoryView />
            <CatalogView />
            <FriendsView />
            <RightSideView />
            <UserSettingsView />
            <UserProfileView />
            <GroupsView />
            <CameraWidgetView />
            <HelpView />
            <NitropediaView />
            <GuideToolView />
            <HcCenterView />
            <CampaignView />
            <GameCenterView />
            <FloorplanEditorView />
            <YoutubeTvView />

            {/* Renderizado directo en el DOM por encima de la interfaz nativa */}
            <PokemonPCView />
            <PokemonEncounterManager />

            {/* 🏥 Renderizado de tu servicio de salud desacoplado */}
            <PokemonHealingView
                isOpen={isHealingOpen}
                onClose={() => setIsHealingOpen(false)}
            />

            {/* 🌟 BARRA VERTICAL: Añadida al final para que flote a la izquierda fijada en pantalla */}
            <PokemonMenu />
        </Base>
    );
}