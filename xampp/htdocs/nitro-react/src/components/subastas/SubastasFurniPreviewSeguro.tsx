import { Component, ErrorInfo, FC, ReactNode } from 'react';
import { Base, Column, Text } from '../../common';
import { SubastasFurniImageSeguro } from './SubastasFurniImageSeguro';

interface ErrorBoundaryProps
{
    fallback: ReactNode;
    children: ReactNode;
}

interface ErrorBoundaryState
{
    error: boolean;
}

class PreviewErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState>
{
    public state: ErrorBoundaryState = { error: false };

    public static getDerivedStateFromError(): ErrorBoundaryState
    {
        return { error: true };
    }

    public componentDidCatch(error: Error, info: ErrorInfo): void
    {
        console.warn('[Subastas] Preview de furni no disponible:', error, info);
    }

    public componentDidUpdate(prevProps: ErrorBoundaryProps): void
    {
        if(prevProps.children !== this.props.children && this.state.error)
        {
            this.setState({ error: false });
        }
    }

    public render(): ReactNode
    {
        if(this.state.error) return this.props.fallback;

        return this.props.children;
    }
}

interface SubastasFurniPreviewSeguroProps
{
    furniId: number;
    tipo: string;
    nombre: string;
    iconUrl?: string;
}

const MiniaturaFallback: FC<{ iconUrl?: string; nombre: string }> = props =>
{
    const { iconUrl = '', nombre = '' } = props;

    if(iconUrl)
    {
        return (
            <img
                src={ iconUrl }
                alt={ nombre || 'Furni' }
                draggable={ false }
                style={ {
                    display: 'block',
                    width: '128px',
                    height: '128px',
                    maxWidth: 'calc(100% - 16px)',
                    maxHeight: 'calc(100% - 16px)',
                    objectFit: 'contain',
                    objectPosition: 'center',
                    imageRendering: 'pixelated',
                    pointerEvents: 'none',
                    userSelect: 'none'
                } }
            />
        );
    }

    return (
        <Column
            gap={ 1 }
            alignItems="center"
            justifyContent="center"
            style={ {
                width: '100%',
                height: '100%',
                padding: '12px',
                textAlign: 'center'
            } }>
            <Text fontWeight="bold">Vista previa no disponible</Text>
            <Text small>El furni se puede subastar igualmente.</Text>
        </Column>
    );
};

export const SubastasFurniPreviewSeguro: FC<SubastasFurniPreviewSeguroProps> = props =>
{
    const { furniId = 0, tipo = 's', nombre = '', iconUrl = '' } = props;

    const fallback = <MiniaturaFallback iconUrl={ iconUrl } nombre={ nombre } />;

    return (
        <Base
            className="d-flex align-items-center justify-content-center rounded"
            style={ {
                position: 'relative',
                width: '100%',
                height: '185px',
                minHeight: '185px',
                maxHeight: '185px',
                overflow: 'hidden',
                background: 'rgba(210,218,226,.65)',
                border: '1px solid rgba(110,125,140,.35)'
            } }>
            <PreviewErrorBoundary
                key={ `${ furniId }-${ tipo }` }
                fallback={ fallback }>
                <Base
                    className="d-flex align-items-center justify-content-center"
                    style={ {
                        width: '164px',
                        height: '164px',
                        minWidth: '164px',
                        minHeight: '164px',
                        maxWidth: '164px',
                        maxHeight: '164px',
                        overflow: 'hidden'
                    } }>
                    <SubastasFurniImageSeguro
                        productType={ (tipo || '').toLowerCase() === 'i' ? 'i' : 's' }
                        productClassId={ furniId }
                        scale={ 1 }
                        style={ {
                            width: '156px',
                            height: '156px',
                            minWidth: '156px',
                            minHeight: '156px',
                            maxWidth: '156px',
                            maxHeight: '156px',
                            backgroundSize: 'contain',
                            backgroundPosition: 'center',
                            backgroundRepeat: 'no-repeat',
                            overflow: 'hidden'
                        } }
                    />
                </Base>
            </PreviewErrorBoundary>
        </Base>
    );
};