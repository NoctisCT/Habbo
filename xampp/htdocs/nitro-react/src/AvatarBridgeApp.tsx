import { AvatarAction, AvatarScaleType, AvatarSetType, ConfigurationEvent, Nitro } from '@nitrots/nitro-renderer';
import { FC, useEffect } from 'react';
import { GetNitroInstance } from './api';

interface AvatarRequest
{
    type: 'avatar-bridge-render';
    id: string | number;
    figure: string;
    gender?: string;
    direction?: number;
    gesture?: string;
}

export const AvatarBridgeApp: FC<{}> = () =>
{
    useEffect(() =>
    {
        let ready = false;
        let disposed = false;

        const post = (message: any) =>
        {
            if(window.parent === window) return;

            window.parent.postMessage(message, window.location.origin);
        };

        const renderAvatar = (request: AvatarRequest, attempt = 0) =>
        {
            if(disposed) return;

            if(!ready)
            {
                if(attempt < 40)
                {
                    window.setTimeout(() => renderAvatar(request, attempt + 1), 100);
                }

                return;
            }

            const nitro = GetNitroInstance();

            if(!nitro || !nitro.avatar)
            {
                if(attempt < 40)
                {
                    window.setTimeout(() => renderAvatar(request, attempt + 1), 100);
                }

                return;
            }

            let avatarImage: any = null;

            try
            {
                avatarImage = nitro.avatar.createAvatarImage(
                    request.figure || '',
                    AvatarScaleType.LARGE,
                    request.gender || 'M',
                    {
                        resetFigure: () =>
                        {
                            if(disposed) return;

                            window.setTimeout(() => renderAvatar(request, 0), 50);
                        },
                        dispose: () => {},
                        disposed: false
                    },
                    null
                );

                if(!avatarImage)
                {
                    if(attempt < 40)
                    {
                        window.setTimeout(() => renderAvatar(request, attempt + 1), 150);
                    }

                    return;
                }

                avatarImage.setDirection(
                    AvatarSetType.FULL,
                    Number.isFinite(request.direction) ? request.direction : 2
                );
                if(request.gesture)
                {
                    avatarImage.initActionAppends();
                    avatarImage.appendAction(
                        AvatarAction.GESTURE,
                        request.gesture
                    );
                    avatarImage.endActionAppends();
                }

                const image = avatarImage.getCroppedImage(AvatarSetType.FULL);

                if(image && image.src)
                {
                    post({
                        type: 'avatar-bridge-result',
                        id: String(request.id),
                        src: image.src
                    });

                    return;
                }

                if(attempt < 40)
                {
                    window.setTimeout(() => renderAvatar(request, attempt + 1), 150);
                }
            }
            catch(error)
            {
                if(attempt < 40)
                {
                    window.setTimeout(() => renderAvatar(request, attempt + 1), 150);
                }
                else
                {
                    post({
                        type: 'avatar-bridge-error',
                        id: String(request.id)
                    });
                }
            }
            finally
            {
                if(avatarImage)
                {
                    try
                    {
                        avatarImage.dispose();
                    }
                    catch(error)
                    {
                    }
                }
            }
        };

        const onMessage = (event: MessageEvent) =>
        {
            if(event.origin !== window.location.origin) return;

            const data = event.data as AvatarRequest;

            if(!data || data.type !== 'avatar-bridge-render') return;

            renderAvatar(data);
        };

        window.addEventListener('message', onMessage);

        if(!GetNitroInstance())
        {
            Nitro.bootstrap();
        }

        const nitro = GetNitroInstance();

        if(!nitro)
        {
            post({ type: 'avatar-bridge-fatal' });

            return () =>
            {
                disposed = true;
                window.removeEventListener('message', onMessage);
            };
        }

        const onConfigurationLoaded = () =>
        {
            if(disposed || ready) return;

            try
            {
                nitro.avatar.init();
                ready = true;

                window.setTimeout(() =>
                {
                    post({ type: 'avatar-bridge-ready' });
                }, 100);
            }
            catch(error)
            {
                post({ type: 'avatar-bridge-fatal' });
            }
        };

        nitro.core.configuration.events.addEventListener(
            ConfigurationEvent.LOADED,
            onConfigurationLoaded
        );

        nitro.core.configuration.init();

        return () =>
        {
            disposed = true;
            window.removeEventListener('message', onMessage);
        };
    }, []);

    return null;
};