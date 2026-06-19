import { ExtendedProfileChangedMessageEvent, RelationshipStatusInfoEvent, RelationshipStatusInfoMessageParser, RoomEngineObjectEvent, RoomObjectCategory, RoomObjectType, UserCurrentBadgesComposer, UserCurrentBadgesEvent, UserProfileEvent, UserProfileParser, UserRelationshipsComposer } from '@nitrots/nitro-renderer';
import { FC, useState, useEffect } from 'react';
import { CreateLinkEvent, GetRoomSession, GetSessionDataManager, GetUserProfile, LocalizeText, SendMessageComposer } from '../../api';
import { Column, Flex, Grid, NitroCardContentView, NitroCardHeaderView, NitroCardView, Text } from '../../common';
import { useMessageEvent, useRoomEngineEvent } from '../../hooks';
import { BadgesContainerView } from './views/BadgesContainerView';
import { FriendsContainerView } from './views/FriendsContainerView';
import { GroupsContainerView } from './views/GroupsContainerView';
import { UserContainerView } from './views/UserContainerView';

export const UserProfileView: FC<{}> = props => {
    const [userProfile, setUserProfile] = useState<UserProfileParser>(null);
    const [userBadges, setUserBadges] = useState<string[]>([]);
    const [userRelationships, setUserRelationships] = useState<RelationshipStatusInfoMessageParser>(null);
    const [verStats, setVerStats] = useState<boolean>(false);

    // Estado dinámico para los atributos de Bleach
    const [rpgStats, setRpgStats] = useState({
        vitalidad: 0, maxVitalidad: 0,
        reiryoku: 0, maxReiryoku: 0,
        fuerza: 0, defensa: 0, velocidad: 0, voluntad: 0, reiatsu: 0
    });

    // Escucha el evento del interceptor (Paquete 3501)
    useEffect(() => {
        const handleRpgStats = (e: any) => {
            const rawData = e.detail;
            if (!rawData) return;

            const parts = rawData.split(';');
            if (parts.length === 7) {
                const vit = parts[0].split(',');
                const rei = parts[1].split(',');
                setRpgStats({
                    vitalidad: parseInt(vit[0], 10) || 0, maxVitalidad: parseInt(vit[1], 10) || 0,
                    reiryoku: parseInt(rei[0], 10) || 0, maxReiryoku: parseInt(rei[1], 10) || 0,
                    fuerza: parseInt(parts[2], 10) || 0,
                    defensa: parseInt(parts[3], 10) || 0,
                    velocidad: parseInt(parts[4], 10) || 0,
                    voluntad: parseInt(parts[5], 10) || 0,
                    reiatsu: parseInt(parts[6], 10) || 0
                });
            }
        };
        window.addEventListener('RPG_STATS_DATA', handleRpgStats);
        return () => window.removeEventListener('RPG_STATS_DATA', handleRpgStats);
    }, []);

    const onClose = () => {
        setUserProfile(null);
        setUserBadges([]);
        setUserRelationships(null);
        setVerStats(false);
    }

    const onLeaveGroup = () => {
        if (!userProfile || (userProfile.id !== GetSessionDataManager().userId)) return;

        GetUserProfile(userProfile.id);
    }

    useMessageEvent<UserCurrentBadgesEvent>(UserCurrentBadgesEvent, event => {
        const parser = event.getParser();

        if (!userProfile || (parser.userId !== userProfile.id)) return;

        setUserBadges(parser.badges);
    });

    useMessageEvent<RelationshipStatusInfoEvent>(RelationshipStatusInfoEvent, event => {
        const parser = event.getParser();

        if (!userProfile || (parser.userId !== userProfile.id)) return;

        setUserRelationships(parser);
    });

    useMessageEvent<UserProfileEvent>(UserProfileEvent, event => {
        const parser = event.getParser();

        let isSameProfile = false;

        setUserProfile(prevValue => {
            if (prevValue && prevValue.id) isSameProfile = (prevValue.id === parser.id);

            return parser;
        });

        if (!isSameProfile) {
            setUserBadges([]);
            setUserRelationships(null);
        }

        SendMessageComposer(new UserCurrentBadgesComposer(parser.id));
        SendMessageComposer(new UserRelationshipsComposer(parser.id));
    });

    useMessageEvent<ExtendedProfileChangedMessageEvent>(ExtendedProfileChangedMessageEvent, event => {
        const parser = event.getParser();

        if (parser.userId != userProfile?.id) return;

        GetUserProfile(parser.userId);
    });

    useRoomEngineEvent<RoomEngineObjectEvent>(RoomEngineObjectEvent.SELECTED, event => {
        if (!userProfile) return;

        if (event.category !== RoomObjectCategory.UNIT) return;

        const userData = GetRoomSession().userDataManager.getUserDataByIndex(event.objectId);

        if (userData.type !== RoomObjectType.USER) return;

        GetUserProfile(userData.webID);
    });

    if (!userProfile) return null;

    return (
        <NitroCardView uniqueKey="nitro-user-profile" theme="primary-slim" className="user-profile">
            <NitroCardHeaderView
                headerText={
                    <Flex alignItems="center" gap={1}>
                        <span>{userProfile?.username}</span>
                        <button
                            className="btn btn-xs btn-primary text-xs py-0 px-1 ms-2"
                            style={{ fontSize: '10px', height: '16px', lineHeight: '1' }}
                            onClick={(e) => { e.stopPropagation(); setVerStats(!verStats); }}
                        >
                            {verStats ? 'Perfil' : 'STATs'}
                        </button>
                    </Flex>
                }
                onCloseClick={onClose}
            />
            <NitroCardContentView overflow="hidden">
                {!verStats ? (
                    <>
                        <Grid fullHeight={false} gap={2}>
                            <Column size={7} gap={1} className="user-container pe-2">
                                <UserContainerView userProfile={userProfile} />
                                <Grid columnCount={5} fullHeight className="bg-muted rounded px-2 py-1">
                                    <BadgesContainerView fullWidth center badges={userBadges} />
                                </Grid>
                            </Column>
                            <Column size={5}>
                                {userRelationships &&
                                    <FriendsContainerView relationships={userRelationships} friendsCount={userProfile.friendsCount} />}
                            </Column>
                        </Grid>
                        <Flex alignItems="center" className="rooms-button-container px-2 py-1">
                            <Flex alignItems="center" gap={1} onClick={event => CreateLinkEvent(`navigator/search/hotel_view/owner:${userProfile.username}`)}>
                                <i className="icon icon-rooms" />
                                <Text bold underline pointer>{LocalizeText('extendedprofile.rooms')}</Text>
                            </Flex>
                        </Flex>
                        <GroupsContainerView fullWidth itsMe={userProfile.id === GetSessionDataManager().userId} groups={userProfile.groups} onLeaveGroup={onLeaveGroup} />
                    </>
                ) : (
                    <Column gap={1} className="p-1 rounded text-white" style={{ minWidth: '230px' }}>
                        <Text bold center className="border-bottom pb-1 text-uppercase mb-2" style={{ color: '#fcc419', fontSize: '13px' }}>Atributos BHRPG</Text>

                        <Grid columnCount={1} gap={1}>
                            <Flex justifyContent="space-between" className="bg-muted rounded px-2 py-1" alignItems="center">
                                <Text size={6} bold style={{ color: '#ff6b6b' }}>🔴 Vitalidad:</Text>
                                <Text size={6} bold>{rpgStats.vitalidad} / {rpgStats.maxVitalidad}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-muted rounded px-2 py-1" alignItems="center">
                                <Text size={6} bold style={{ color: '#4dadf7' }}>🔵 Reiryoku:</Text>
                                <Text size={6} bold>{rpgStats.reiryoku} / {rpgStats.maxReiryoku}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-muted rounded px-2 py-1" alignItems="center">
                                <Text size={6} bold>⚔️ Fuerza:</Text>
                                <Text size={6} bold>{rpgStats.fuerza}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-muted rounded px-2 py-1" alignItems="center">
                                <Text size={6} bold>🛡️ Defensa:</Text>
                                <Text size={6} bold>{rpgStats.defensa}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-muted rounded px-2 py-1" alignItems="center">
                                <Text size={6} bold style={{ color: '#51cf66' }}>⚡ Velocidad:</Text>
                                <Text size={6} bold>{rpgStats.velocidad}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-muted rounded px-2 py-1" alignItems="center">
                                <Text size={6} bold style={{ color: '#fcc419' }}>🔥 Voluntad:</Text>
                                <Text size={6} bold>{rpgStats.voluntad}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-muted rounded px-2 py-1" alignItems="center">
                                <Text size={6} bold style={{ color: '#cc5de8' }}>✨ Reiatsu:</Text>
                                <Text size={6} bold>{rpgStats.reiatsu}</Text>
                            </Flex>
                        </Grid>
                    </Column>
                )}
            </NitroCardContentView>
        </NitroCardView>
    )
}