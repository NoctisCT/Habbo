import { RelationshipStatusInfoEvent, RelationshipStatusInfoMessageParser, RoomSessionFavoriteGroupUpdateEvent, RoomSessionUserBadgesEvent, RoomSessionUserFigureUpdateEvent, UserRelationshipsComposer } from '@nitrots/nitro-renderer';
import { Dispatch, FC, FocusEvent, KeyboardEvent, SetStateAction, useEffect, useState } from 'react';
import { FaPencilAlt, FaTimes } from 'react-icons/fa';
import { AvatarInfoUser, CloneObject, GetConfiguration, GetGroupInformation, GetSessionDataManager, GetUserProfile, LocalizeText, SendMessageComposer } from '../../../../../api';
import { Column, Flex, Grid, LayoutAvatarImageView, LayoutBadgeImageView, Text, UserProfileIconView } from '../../../../../common';
import { useMessageEvent, useRoom, useRoomSessionManagerEvent } from '../../../../../hooks';
import { InfoStandWidgetUserRelationshipsView } from './InfoStandWidgetUserRelationshipsView';
import { InfoStandWidgetUserTagsView } from './InfoStandWidgetUserTagsView';

interface InfoStandWidgetUserViewProps {
    avatarInfo: AvatarInfoUser;
    setAvatarInfo: Dispatch<SetStateAction<AvatarInfoUser>>;
    onClose: () => void;
}

export const InfoStandWidgetUserView: FC<InfoStandWidgetUserViewProps> = props => {
    const { avatarInfo = null, setAvatarInfo = null, onClose = null } = props;
    const [motto, setMotto] = useState<string>(null);
    const [isEditingMotto, setIsEditingMotto] = useState(false);
    const [relationships, setRelationships] = useState<RelationshipStatusInfoMessageParser>(null);
    const [verStats, setVerStats] = useState<boolean>(false);
    const { roomSession = null } = useRoom();

    // Estado dinámico para los atributos BHRPG
    const [rpgStats, setRpgStats] = useState({
        vitalidad: 0, maxVitalidad: 0,
        reiryoku: 0, maxReiryoku: 0,
        fuerza: 0, defensa: 0, velocidad: 0, voluntad: 0, reiatsu: 0
    });

    // Escucha el paquete binario de STATs 3501
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

    const saveMotto = (motto: string) => {
        if (!isEditingMotto || (motto.length > GetConfiguration<number>('motto.max.length', 38))) return;

        roomSession.sendMottoMessage(motto);

        setIsEditingMotto(false);
    }

    const onMottoBlur = (event: FocusEvent<HTMLInputElement>) => saveMotto(event.target.value);

    const onMottoKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
        event.stopPropagation();

        switch (event.key) {
            case 'Enter':
                saveMotto((event.target as HTMLInputElement).value);
                return;
        }
    }

    useRoomSessionManagerEvent<RoomSessionUserBadgesEvent>(RoomSessionUserBadgesEvent.RSUBE_BADGES, event => {
        if (!avatarInfo || (avatarInfo.webID !== event.userId)) return;

        const oldBadges = avatarInfo.badges.join('');

        if (oldBadges === event.badges.join('')) return;

        setAvatarInfo(prevValue => {
            const newValue = CloneObject(prevValue);

            newValue.badges = event.badges;

            return newValue;
        });
    });

    useRoomSessionManagerEvent<RoomSessionUserFigureUpdateEvent>(RoomSessionUserFigureUpdateEvent.USER_FIGURE, event => {
        if (!avatarInfo || (avatarInfo.roomIndex !== event.roomIndex)) return;

        setAvatarInfo(prevValue => {
            const newValue = CloneObject(prevValue);

            newValue.figure = event.figure;
            newValue.motto = event.customInfo;
            newValue.achievementScore = event.activityPoints;

            return newValue;
        });
    });

    useRoomSessionManagerEvent<RoomSessionFavoriteGroupUpdateEvent>(RoomSessionFavoriteGroupUpdateEvent.FAVOURITE_GROUP_UPDATE, event => {
        if (!avatarInfo || (avatarInfo.roomIndex !== event.roomIndex)) return;

        setAvatarInfo(prevValue => {
            const newValue = CloneObject(prevValue);
            const clearGroup = ((event.status === -1) || (event.habboGroupId <= 0));

            newValue.groupId = clearGroup ? -1 : event.habboGroupId;
            newValue.groupName = clearGroup ? null : event.habboGroupName;
            newValue.groupBadgeId = clearGroup ? null : GetSessionDataManager().getGroupBadge(event.habboGroupId);

            return newValue;
        });
    });

    useMessageEvent<RelationshipStatusInfoEvent>(RelationshipStatusInfoEvent, event => {
        const parser = event.getParser();

        if (!avatarInfo || (avatarInfo.webID !== parser.userId)) return;

        setRelationships(parser);
    });

    useEffect(() => {
        setIsEditingMotto(false);
        setMotto(avatarInfo.motto);
        setVerStats(false); // Resetea a la vista normal si cambias de usuario clicado

        SendMessageComposer(new UserRelationshipsComposer(avatarInfo.webID));

        // ¡AQUÍ LA MAGIA! Mandamos el comando oculto al emulador en segundo plano
        if (roomSession) roomSession.sendChatMessage(":rpgstats " + avatarInfo.webID);

        return () => {
            setIsEditingMotto(false);
            setMotto(null);
            setRelationships(null);
        }
    }, [avatarInfo, roomSession]); // Añadimos roomSession a las dependencias

    if (!avatarInfo) return null;

    return (
        <Column className="nitro-infostand rounded">
            <Column overflow="visible" className="container-fluid content-area" gap={1}>
                <Column gap={1}>
                    <Flex alignItems="center" justifyContent="between">
                        <Flex alignItems="center" gap={1}>
                            <UserProfileIconView userId={avatarInfo.webID} />
                            <Text variant="white" small wrap>{avatarInfo.name}</Text>
                            <button
                                className="btn btn-xs btn-primary text-xs py-0 px-1 ms-1"
                                style={{ fontSize: '9px', height: '15px', lineHeight: '1' }}
                                onClick={(e) => { e.stopPropagation(); setVerStats(!verStats); }}
                            >
                                {verStats ? 'Perfil' : 'STATs'}
                            </button>
                        </Flex>
                        <FaTimes className="cursor-pointer fa-icon" onClick={onClose} />
                    </Flex>
                    <hr className="m-0" />
                </Column>

                {!verStats ? (
                    <>
                        <Column gap={1}>
                            <Flex gap={1}>
                                <Column fullWidth className="body-image" onClick={event => GetUserProfile(avatarInfo.webID)}>
                                    <LayoutAvatarImageView figure={avatarInfo.figure} direction={4} />
                                </Column>
                                <Column grow alignItems="center" gap={0}>
                                    <Flex gap={1}>
                                        <Flex center className="badge-image">
                                            {avatarInfo.badges[0] && <LayoutBadgeImageView badgeCode={avatarInfo.badges[0]} showInfo={true} />}
                                        </Flex>
                                        <Flex center pointer={(avatarInfo.groupId > 0)} className="badge-image" onClick={event => GetGroupInformation(avatarInfo.groupId)}>
                                            {avatarInfo.groupId > 0 &&
                                                <LayoutBadgeImageView badgeCode={avatarInfo.groupBadgeId} isGroup={true} showInfo={true} customTitle={avatarInfo.groupName} />}
                                        </Flex>
                                    </Flex>
                                    <Flex center gap={1}>
                                        <Flex center className="badge-image">
                                            {avatarInfo.badges[1] && <LayoutBadgeImageView badgeCode={avatarInfo.badges[1]} showInfo={true} />}
                                        </Flex>
                                        <Flex center className="badge-image">
                                            {avatarInfo.badges[2] && <LayoutBadgeImageView badgeCode={avatarInfo.badges[2]} showInfo={true} />}
                                        </Flex>
                                    </Flex>
                                    <Flex center gap={1}>
                                        <Flex center className="badge-image">
                                            {avatarInfo.badges[3] && <LayoutBadgeImageView badgeCode={avatarInfo.badges[3]} showInfo={true} />}
                                        </Flex>
                                        <Flex center className="badge-image">
                                            {avatarInfo.badges[4] && <LayoutBadgeImageView badgeCode={avatarInfo.badges[4]} showInfo={true} />}
                                        </Flex>
                                    </Flex>
                                </Column>
                            </Flex>
                            <hr className="m-0" />
                        </Column>
                        <Column gap={1}>
                            <Flex alignItems="center" className="bg-light-dark rounded py-1 px-2">
                                {(avatarInfo.type !== AvatarInfoUser.OWN_USER) &&
                                    <Flex grow alignItems="center" className="motto-content">
                                        <Text fullWidth pointer wrap textBreak small variant="white">{motto}</Text>
                                    </Flex>}
                                {avatarInfo.type === AvatarInfoUser.OWN_USER &&
                                    <Flex grow alignItems="center" gap={2}>
                                        <FaPencilAlt className="small fa-icon" />
                                        <Flex grow alignItems="center" className="motto-content">
                                            {!isEditingMotto &&
                                                <Text fullWidth pointer wrap textBreak small variant="white" onClick={event => setIsEditingMotto(true)}>{motto}&nbsp;</Text>}
                                            {isEditingMotto &&
                                                <input type="text" className="motto-input" maxLength={GetConfiguration<number>('motto.max.length', 38)} value={motto} onChange={event => setMotto(event.target.value)} onBlur={onMottoBlur} onKeyDown={onMottoKeyDown} autoFocus={true} />}
                                        </Flex>
                                    </Flex>}
                            </Flex>
                            <hr className="m-0" />
                        </Column>
                        <Column gap={1}>
                            <Text variant="white" small wrap>
                                {LocalizeText('infostand.text.achievement_score') + ' ' + avatarInfo.achievementScore}
                            </Text>
                            {(avatarInfo.carryItem > 0) &&
                                <>
                                    <hr className="m-0" />
                                    <Text variant="white" small wrap>
                                        {LocalizeText('infostand.text.handitem', ['item'], [LocalizeText('handitem' + avatarInfo.carryItem)])}
                                    </Text>
                                </>}
                        </Column>
                        <Column gap={1}>
                            <InfoStandWidgetUserRelationshipsView relationships={relationships} />
                        </Column>
                        {GetConfiguration('user.tags.enabled') &&
                            <Column gap={1} className="mt-1">
                                <InfoStandWidgetUserTagsView tags={GetSessionDataManager().tags} />
                            </Column>
                        }
                    </>
                ) : (
                    <Column gap={1} className="mt-1 text-white">
                        <Text bold center className="border-bottom pb-1 text-uppercase mb-1" style={{ color: '#fcc419', fontSize: '11px' }}>Atributos BHRPG</Text>

                        <Grid columnCount={1} gap={1}>
                            <Flex justifyContent="space-between" className="bg-light-dark rounded px-2 py-0" alignItems="center" style={{ height: '22px' }}>
                                <Text size={6} bold style={{ color: '#ff6b6b', fontSize: '11px' }}>🔴 Vitalidad:</Text>
                                <Text size={6} bold style={{ fontSize: '11px' }}>{rpgStats.vitalidad} / {rpgStats.maxVitalidad}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-light-dark rounded px-2 py-0" alignItems="center" style={{ height: '22px' }}>
                                <Text size={6} bold style={{ color: '#4dadf7', fontSize: '11px' }}>🔵 Reiryoku:</Text>
                                <Text size={6} bold style={{ fontSize: '11px' }}>{rpgStats.reiryoku} / {rpgStats.maxReiryoku}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-light-dark rounded px-2 py-0" alignItems="center" style={{ height: '22px' }}>
                                <Text size={6} bold style={{ fontSize: '11px' }}>⚔️ Fuerza:</Text>
                                <Text size={6} bold style={{ fontSize: '11px' }}>{rpgStats.fuerza}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-light-dark rounded px-2 py-0" alignItems="center" style={{ height: '22px' }}>
                                <Text size={6} bold style={{ fontSize: '11px' }}>🛡️ Defensa:</Text>
                                <Text size={6} bold style={{ fontSize: '11px' }}>{rpgStats.defensa}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-light-dark rounded px-2 py-0" alignItems="center" style={{ height: '22px' }}>
                                <Text size={6} bold style={{ color: '#51cf66', fontSize: '11px' }}>⚡ Velocidad:</Text>
                                <Text size={6} bold style={{ fontSize: '11px' }}>{rpgStats.velocidad}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-light-dark rounded px-2 py-0" alignItems="center" style={{ height: '22px' }}>
                                <Text size={6} bold style={{ color: '#fcc419', fontSize: '11px' }}>🔥 Voluntad:</Text>
                                <Text size={6} bold style={{ fontSize: '11px' }}>{rpgStats.voluntad}</Text>
                            </Flex>
                            <Flex justifyContent="space-between" className="bg-light-dark rounded px-2 py-0" alignItems="center" style={{ height: '22px' }}>
                                <Text size={6} bold style={{ color: '#cc5de8', fontSize: '11px' }}>✨ Reiatsu:</Text>
                                <Text size={6} bold style={{ fontSize: '11px' }}>{rpgStats.reiatsu}</Text>
                            </Flex>
                        </Grid>
                    </Column>
                )}
            </Column>
        </Column>
    );
}