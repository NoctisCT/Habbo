import { createRoot } from 'react-dom/client';
import { App } from './App';
import { AvatarBridgeApp } from './AvatarBridgeApp';
import './index.scss';

const parameters = new URLSearchParams(window.location.search);
const isAvatarBridge = (parameters.get('avatar-bridge') === '1');

createRoot(document.getElementById('root')).render(
    isAvatarBridge
        ? <AvatarBridgeApp />
        : <App />
);
