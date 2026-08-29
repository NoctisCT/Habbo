import { FC } from 'react';
import { WiredFurniType } from '../../../../api';
import { WiredActionBaseView } from './WiredActionBaseView';

export const WiredActionStopAvatarSyncView: FC<{}> = () =>
{
    const save = () =>
    {
        // Stop Avatar Sync no tiene configuracion propia.
    };

    return (
        <WiredActionBaseView
            requiresFurni={ WiredFurniType.STUFF_SELECTION_OPTION_NONE }
            hasSpecialInput={ true }
            save={ save }>

            <style>
                {`
                    #stop-avatar-sync-text,
                    #stop-avatar-sync-text *,
                    #stop-avatar-sync-text div
                    {
                        color: #000000 !important;
                    }
                `}
            </style>

            <div
                id="stop-avatar-sync-text"
                className="d-flex flex-column gap-2">

                <div className="fw-bold">
                    Stop Avatar Sync
                </div>

                <div>
                    Detiene la sesión Avatar Sync WIRED completa del usuario causante.
                </div>

            </div>

        </WiredActionBaseView>
    );
};