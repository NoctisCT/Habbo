import { MouseEventType, RoomObjectCategory } from '@nitrots/nitro-renderer';
import { Dispatch, FC, PropsWithChildren, SetStateAction, useEffect, useRef } from 'react';
import { CreateLinkEvent, DispatchUiEvent, GetConfiguration, GetRoomEngine, GetRoomSession, GetSessionDataManager, GetUserProfile } from '../../api';
import { Base, Flex, LayoutItemCountView } from '../../common';
import { GuideToolEvent } from '../../events';

interface ToolbarMeViewProps
{
    useGuideTool: boolean;
    unseenAchievementCount: number;
    setMeExpanded: Dispatch<SetStateAction<boolean>>;
}

export const ToolbarMeView: FC<PropsWithChildren<ToolbarMeViewProps>> = props =>
{
    const { useGuideTool = false, unseenAchievementCount = 0, setMeExpanded = null, children = null, ...rest } = props;
    const elementRef = useRef<HTMLDivElement>();

    useEffect(() =>
    {
        const roomSession = GetRoomSession();

        if(!roomSession) return;

        GetRoomEngine().selectRoomObject(roomSession.roomId, roomSession.ownRoomIndex, RoomObjectCategory.UNIT);
    }, []);

    useEffect(() =>
    {
        const onClick = (event: MouseEvent) => setMeExpanded(false);

        document.addEventListener('click', onClick);

        return () => document.removeEventListener(MouseEventType.MOUSE_CLICK, onClick);
    }, [ setMeExpanded ]);

    return (
        <Flex innerRef={ elementRef } alignItems="center" className="nitro-toolbar-me holo-classic-me-menu" gap={ 1 }>
            { (GetConfiguration('guides.enabled') && useGuideTool) &&
                <Flex center pointer className="holo-me-slot" onClick={ event => DispatchUiEvent(new GuideToolEvent(GuideToolEvent.TOGGLE_GUIDE_TOOL)) }>
                    <Base className="navigation-item icon icon-me-helper-tool" />
                </Flex> }

            <Flex center pointer className="holo-me-slot" onClick={ event => CreateLinkEvent('achievements/toggle') }>
                <Base className="navigation-item icon icon-me-achievements">
                    { (unseenAchievementCount > 0) &&
                        <LayoutItemCountView count={ unseenAchievementCount } /> }
                </Base>
            </Flex>

            <Flex center pointer className="holo-me-slot" onClick={ event => GetUserProfile(GetSessionDataManager().userId) }>
                <Base className="navigation-item icon icon-me-profile" />
            </Flex>

            <Flex center pointer className="holo-me-slot" onClick={ event => CreateLinkEvent('navigator/search/myworld_view') }>
                <Base className="navigation-item icon icon-me-rooms" />
            </Flex>

            <Flex center pointer className="holo-me-slot" onClick={ event => CreateLinkEvent('avatar-editor/toggle') }>
                <Base className="navigation-item icon icon-me-clothing" />
            </Flex>

            <Flex center pointer className="holo-me-slot" onClick={ event => CreateLinkEvent('user-settings/toggle') }>
                <Base className="navigation-item icon icon-me-settings" />
            </Flex>

            { children }
        </Flex>
    );
}
