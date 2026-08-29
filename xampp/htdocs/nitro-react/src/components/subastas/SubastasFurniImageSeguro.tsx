import { Component, ComponentProps, ErrorInfo, ReactNode } from 'react';
import { Base, LayoutFurniImageView, Text } from '../../common';

type LayoutFurniProps = ComponentProps<typeof LayoutFurniImageView>;

interface BoundaryProps
{
    children: ReactNode;
    fallback: ReactNode;
}

interface BoundaryState
{
    error: boolean;
}

class SubastasFurniBoundary extends Component<BoundaryProps, BoundaryState>
{
    public state: BoundaryState = { error: false };

    public static getDerivedStateFromError(): BoundaryState
    {
        return { error: true };
    }

    public componentDidCatch(error: Error, info: ErrorInfo): void
    {
        console.warn('[Subastas] Imagen de furni protegida no disponible:', error, info);
    }

    public render(): ReactNode
    {
        if(this.state.error) return this.props.fallback;

        return this.props.children;
    }
}

export const SubastasFurniImageSeguro = (props: LayoutFurniProps) =>
{
    const {
        productClassId = -1,
        productType = 's',
        extraData = '',
        scale = 1,
        style = {},
        ...rest
    } = props;

    // No basta con overflow:hidden: un custom enorme puede seguir
    // haciendo crecer la tarjeta por el tamaño físico del div.
    // Ponemos un techo según el contexto/scale.
    const anchoExplicito = (style as any)?.width;
    const altoExplicito = (style as any)?.height;

    const limiteAlto = altoExplicito ||
        (scale > 1 ? '170px' : (scale < 1 ? '72px' : '96px'));

    const limiteAncho = anchoExplicito ||
        (scale > 1 ? '190px' : '100%');

    const fallback = (
        <Base
            className="d-flex align-items-center justify-content-center"
            style={ {
                width: (style as any)?.width || '36px',
                height: (style as any)?.height || '36px',
                maxWidth: '100%',
                maxHeight: '100%',
                overflow: 'hidden',
                opacity: .55
            } }>
            <Text small>?</Text>
        </Base>
    );

    return (
        <SubastasFurniBoundary
            key={ `${ productType }-${ productClassId }-${ extraData }` }
            fallback={ fallback }>
            <Base
                className="d-flex align-items-center justify-content-center"
                style={ {
                    maxWidth: limiteAncho,
                    maxHeight: limiteAlto,
                    overflow: 'hidden'
                } }>
                <LayoutFurniImageView
                    { ...rest }
                    productType={ productType }
                    productClassId={ productClassId }
                    extraData={ extraData }
                    scale={ scale }
                    style={ {
                        ...style,
                        maxWidth: limiteAncho,
                        maxHeight: limiteAlto,
                        backgroundSize: 'contain',
                        backgroundPosition: 'center',
                        backgroundRepeat: 'no-repeat',
                        overflow: 'hidden'
                    } }
                />
            </Base>
        </SubastasFurniBoundary>
    );
};