import {
    ArcadeCloseEvent,
    ArcadeGameStartComposer,
    ArcadeGameStartedEvent,
    ArcadeLeaderboardEvent,
    ArcadeScoreSubmitComposer,
    SpaceInvadersOpenEvent
} from '@nitrots/nitro-renderer';
import { FC, useEffect, useRef, useState } from 'react';
import {
    NitroCardContentView,
    NitroCardHeaderView,
    NitroCardView
} from '../../common';
import { SendMessageComposer } from '../../api';
import { useMessageEvent } from '../../hooks/events';
import { ArcadeLeaderboardView } from '../arcade/ArcadeLeaderboardView';
import './SpaceInvadersView.scss';

type GamePhase = 'ready' | 'playing' | 'paused' | 'gameover';

interface Enemy
{
    x: number;
    y: number;
    w: number;
    h: number;
    row: number;
    col: number;
    alive: boolean;
}

interface Bullet
{
    x: number;
    y: number;
    vy: number;
    enemy: boolean;
}

interface Explosion
{
    x: number;
    y: number;
    life: number;
    duration: number;
    variant: 'enemy' | 'player';
}

interface LeaderboardEntry
{
    rank: number;
    username: string;
    score: number;
    level: number;
}

interface GameModel
{
    phase: GamePhase;
    playerX: number;
    playerY: number;
    enemies: Enemy[];
    bullets: Bullet[];
    explosions: Explosion[];
    direction: number;
    enemySpeed: number;
    enemyFireTimer: number;
    fireCooldown: number;
    invulnerable: number;
    damageFlash: number;
    levelBanner: number;
    score: number;
    lives: number;
    level: number;
}

const WIDTH = 640;
const HEIGHT = 480;
const MAX_LIVES = 3;
const GAME_KEY = 'space_invaders';
const UI_MARKER = 'BIRIBIRI_SPACE_INVADERS_V3_ARCADE';

const makeEnemies = (): Enemy[] =>
{
    const result: Enemy[] = [];

    for(let row = 0; row < 5; row++)
    {
        for(let col = 0; col < 10; col++)
        {
            result.push({
                x: 76 + (col * 49),
                y: 70 + (row * 35),
                w: 28,
                h: 18,
                row,
                col,
                alive: true
            });
        }
    }

    return result;
};

const makeGame = (
    level = 1,
    phase: GamePhase = 'ready',
    score = 0,
    lives = MAX_LIVES): GameModel => ({
    phase,
    playerX: WIDTH / 2,
    playerY: HEIGHT - 42,
    enemies: makeEnemies(),
    bullets: [],
    explosions: [],
    direction: 1,
    enemySpeed: 34 + ((level - 1) * 9),
    enemyFireTimer: 0.72,
    fireCooldown: 0,
    invulnerable: 0,
    damageFlash: 0,
    levelBanner: 0,
    score,
    lives,
    level
});

const intersects = (
    ax: number,
    ay: number,
    aw: number,
    ah: number,
    bx: number,
    by: number,
    bw: number,
    bh: number): boolean =>
{
    return (
        ax < bx + bw &&
        ax + aw > bx &&
        ay < by + bh &&
        ay + ah > by
    );
};

const PixelHeart: FC<{ active: boolean }> = ({ active }) =>
{
    return (
        <svg
            className={ `space-life-heart${ active ? ' is-active' : '' }` }
            viewBox="0 0 18 16"
            shapeRendering="crispEdges"
            aria-hidden="true">
            <rect x="3" y="2" width="4" height="2" />
            <rect x="11" y="2" width="4" height="2" />
            <rect x="1" y="4" width="16" height="5" />
            <rect x="3" y="9" width="12" height="2" />
            <rect x="5" y="11" width="8" height="2" />
            <rect x="7" y="13" width="4" height="2" />
            <rect className="heart-shine" x="4" y="4" width="2" height="2" />
        </svg>
    );
};

const PixelAlien: FC<{}> = () =>
{
    return (
        <svg
            className="space-status-alien"
            viewBox="0 0 22 18"
            shapeRendering="crispEdges"
            aria-hidden="true">
            <rect x="5" y="2" width="12" height="2" />
            <rect x="3" y="4" width="16" height="2" />
            <rect x="1" y="6" width="20" height="6" />
            <rect x="3" y="12" width="4" height="2" />
            <rect x="15" y="12" width="4" height="2" />
            <rect x="5" y="14" width="3" height="2" />
            <rect x="14" y="14" width="3" height="2" />
            <rect className="alien-eye" x="6" y="7" width="3" height="3" />
            <rect className="alien-eye" x="13" y="7" width="3" height="3" />
        </svg>
    );
};

export const SpaceInvadersView: FC<{}> = () =>
{
    const canvasRef = useRef<HTMLCanvasElement | null>(null);
    const visibleRef = useRef(false);
    const keysRef = useRef<Set<string>>(new Set());
    const lastFrameRef = useRef(0);
    const gameRef = useRef<GameModel>(makeGame());
    const itemIdRef = useRef(0);
    const recordRef = useRef(0);
    const serverBestRef = useRef(0);
    const runTokenRef = useRef('');
    const submittedRunRef = useRef(false);
    const startPendingRef = useRef(false);
    const soundEnabledRef = useRef(true);
    const audioRef = useRef<AudioContext | null>(null);

    const [ isVisible, setIsVisible ] = useState(false);
    const [ itemId, setItemId ] = useState(0);
    const [ score, setScore ] = useState(0);
    const [ record, setRecord ] = useState(0);
    const [ serverBest, setServerBest ] = useState(0);
    const [ lives, setLives ] = useState(MAX_LIVES);
    const [ level, setLevel ] = useState(1);
    const [ phase, setPhase ] = useState<GamePhase>('ready');
    const [ soundEnabled, setSoundEnabled ] = useState(true);
    const [ startPending, setStartPending ] = useState(false);
    const [ leaderboard, setLeaderboard ] = useState<LeaderboardEntry[]>([]);
    const [ personalRank, setPersonalRank ] = useState(0);
    const [ totalPlayers, setTotalPlayers ] = useState(0);
    const [ resultState, setResultState ] =
        useState<'idle' | 'accepted' | 'rejected'>('idle');
    const [ resultMessage, setResultMessage ] = useState('');
    const [ newServerRecord, setNewServerRecord ] = useState(false);
    const [ recordsOpen, setRecordsOpen ] = useState(false);

    const ensureAudio = (): AudioContext | null =>
    {
        if(!soundEnabledRef.current) return null;

        try
        {
            if(!audioRef.current)
            {
                const AudioContextClass =
                    window.AudioContext ||
                    (window as any).webkitAudioContext;

                if(!AudioContextClass) return null;

                audioRef.current = new AudioContextClass();
            }

            if(audioRef.current.state === 'suspended')
            {
                void audioRef.current.resume();
            }

            return audioRef.current;
        }
        catch
        {
            return null;
        }
    };

    const tone = (
        frequency: number,
        duration: number,
        type: OscillatorType = 'square',
        volume = 0.028,
        delay = 0,
        endFrequency?: number) =>
    {
        const context = ensureAudio();
        if(!context) return;

        const start = context.currentTime + delay;
        const oscillator = context.createOscillator();
        const gain = context.createGain();

        oscillator.type = type;
        oscillator.frequency.setValueAtTime(frequency, start);

        if(endFrequency !== undefined)
        {
            oscillator.frequency.exponentialRampToValueAtTime(
                Math.max(1, endFrequency),
                start + duration
            );
        }

        gain.gain.setValueAtTime(0.0001, start);

        const mixedVolume = Math.min(
            0.12,
            Math.max(0.0001, volume * 2.8)
        );

        gain.gain.exponentialRampToValueAtTime(
            mixedVolume,
            start + 0.008
        );
        gain.gain.exponentialRampToValueAtTime(
            0.0001,
            start + duration
        );

        oscillator.connect(gain);
        gain.connect(context.destination);

        oscillator.start(start);
        oscillator.stop(start + duration + 0.015);
    };

    const playShoot = () =>
    {
        tone(620, 0.07, 'square', 0.018, 0, 360);
    };

    const playEnemyHit = () =>
    {
        tone(190, 0.08, 'square', 0.024, 0, 75);
        tone(105, 0.06, 'sawtooth', 0.012, 0.025, 55);
    };

    const playPlayerHit = () =>
    {
        tone(145, 0.16, 'sawtooth', 0.035, 0, 55);
        tone(90, 0.18, 'square', 0.022, 0.055, 38);
    };

    const playStart = () =>
    {
        tone(220, 0.08, 'square', 0.022, 0);
        tone(330, 0.08, 'square', 0.022, 0.08);
        tone(440, 0.11, 'square', 0.024, 0.16);
    };

    const playLevelUp = () =>
    {
        tone(330, 0.07, 'square', 0.02, 0);
        tone(440, 0.07, 'square', 0.02, 0.07);
        tone(550, 0.07, 'square', 0.02, 0.14);
        tone(660, 0.13, 'square', 0.024, 0.21);
    };

    const playGameOver = () =>
    {
        tone(300, 0.13, 'square', 0.024, 0);
        tone(220, 0.16, 'square', 0.025, 0.13);
        tone(150, 0.23, 'sawtooth', 0.03, 0.29, 60);
    };

    const updateRecord = (nextScore: number) =>
    {
        const candidate =
            Math.max(
                serverBestRef.current,
                nextScore
            );

        if(candidate === recordRef.current) return;

        recordRef.current = candidate;
        setRecord(candidate);
    };

    const syncHud = () =>
    {
        const game = gameRef.current;

        setScore(game.score);
        setLives(game.lives);
        setLevel(game.level);
        setPhase(game.phase);
        updateRecord(game.score);
    };

    const newReadyGame = () =>
    {
        gameRef.current = makeGame(1, 'ready');
        runTokenRef.current = '';
        submittedRunRef.current = false;
        startPendingRef.current = false;
        keysRef.current.clear();
        lastFrameRef.current = 0;
        setStartPending(false);
        setResultState('idle');
        setResultMessage('');
        setNewServerRecord(false);
        syncHud();
    };

    const beginLocalRun = (token: string) =>
    {
        if(!token) return;

        runTokenRef.current = token;
        submittedRunRef.current = false;
        startPendingRef.current = false;
        gameRef.current = makeGame(1, 'playing');
        keysRef.current.clear();
        lastFrameRef.current = 0;
        setStartPending(false);
        setResultState('idle');
        setResultMessage('');
        setNewServerRecord(false);
        syncHud();
        playStart();

        window.setTimeout(() =>
        {
            canvasRef.current?.focus();
        }, 0);
    };

    const requestStartGame = () =>
    {
        if(
            !visibleRef.current ||
            startPendingRef.current ||
            itemIdRef.current <= 0
        ) return;

        ensureAudio();
        startPendingRef.current = true;
        setStartPending(true);
        setResultState('idle');
        setResultMessage('');
        setNewServerRecord(false);

        try
        {
            SendMessageComposer(
                new ArcadeGameStartComposer(
                    itemIdRef.current,
                    GAME_KEY
                )
            );
        }
        catch
        {
            startPendingRef.current = false;
            setStartPending(false);
            setResultState('rejected');
            setResultMessage(
                'No se pudo iniciar la partida.'
            );
        }
    };

    const restartGame = () =>
    {
        requestStartGame();
    };

    const submitRunResult = (
        finalScore: number,
        finalLevel: number) =>
    {
        const token = runTokenRef.current;

        if(
            !token ||
            submittedRunRef.current ||
            itemIdRef.current <= 0
        ) return;

        submittedRunRef.current = true;

        try
        {
            SendMessageComposer(
                new ArcadeScoreSubmitComposer(
                    itemIdRef.current,
                    GAME_KEY,
                    token,
                    finalScore,
                    finalLevel
                )
            );
        }
        catch
        {
            submittedRunRef.current = false;
            setResultState('rejected');
            setResultMessage(
                'No se pudo enviar la puntuación.'
            );
        }
    };

    const togglePause = () =>
    {
        const game = gameRef.current;

        if(game.phase === 'playing')
        {
            game.phase = 'paused';
        }
        else if(game.phase === 'paused')
        {
            game.phase = 'playing';
            lastFrameRef.current = performance.now();
        }
        else
        {
            return;
        }

        setPhase(game.phase);
    };

    const close = () =>
    {
        visibleRef.current = false;
        keysRef.current.clear();
        runTokenRef.current = '';
        submittedRunRef.current = false;
        startPendingRef.current = false;
        setStartPending(false);
        setRecordsOpen(false);
        setIsVisible(false);

        if(gameRef.current.phase === 'playing')
        {
            gameRef.current.phase = 'paused';
        }
    };

    const toggleSound = () =>
    {
        const next = !soundEnabledRef.current;

        soundEnabledRef.current = next;
        setSoundEnabled(next);

        if(next)
        {
            ensureAudio();
            tone(520, 0.06, 'square', 0.018);
        }
    };

    const openRecords = () =>
    {
        if(gameRef.current.phase === 'playing')
        {
            gameRef.current.phase = 'paused';
            setPhase('paused');
            keysRef.current.clear();
        }

        setRecordsOpen(true);
    };

    const closeRecords = () =>
    {
        setRecordsOpen(false);

        window.setTimeout(() =>
        {
            canvasRef.current?.focus();
        }, 0);
    };

    useEffect(() =>
    {
        return () =>
        {
            const context = audioRef.current;

            if(context)
            {
                void context.close().catch(() => undefined);
                audioRef.current = null;
            }
        };
    }, []);

    useMessageEvent(
        SpaceInvadersOpenEvent,
        (event: SpaceInvadersOpenEvent) =>
    {
        const parser = event.getParser();

        itemIdRef.current = parser.itemId;
        setItemId(parser.itemId);
        setLeaderboard([]);
        setPersonalRank(0);
        setTotalPlayers(0);
        serverBestRef.current = 0;
        recordRef.current = 0;
        setServerBest(0);
        setRecord(0);
        setRecordsOpen(false);
        newReadyGame();

        visibleRef.current = true;
        setIsVisible(true);

        window.setTimeout(() =>
        {
            canvasRef.current?.focus();
        }, 0);
    });

    useMessageEvent(
        ArcadeGameStartedEvent,
        (event: ArcadeGameStartedEvent) =>
    {
        const parser = event.getParser();

        if(
            !visibleRef.current ||
            parser.gameKey !== GAME_KEY ||
            parser.itemId !== itemIdRef.current
        ) return;

        startPendingRef.current = false;
        setStartPending(false);

        if(!parser.success)
        {
            setResultState('rejected');
            setResultMessage(
                parser.message ||
                'No se pudo iniciar la partida.'
            );
            return;
        }

        beginLocalRun(parser.token);
    });

    useMessageEvent(
        ArcadeLeaderboardEvent,
        (event: ArcadeLeaderboardEvent) =>
    {
        const parser = event.getParser();

        if(parser.gameKey !== GAME_KEY) return;

        const entries: LeaderboardEntry[] =
            parser.entries.map(entry => ({
                rank: entry.rank,
                username: entry.username,
                score: entry.score,
                level: entry.level
            }));

        setLeaderboard(entries);
        setPersonalRank(parser.personalRank);
        setTotalPlayers(parser.totalPlayers);
        setServerBest(parser.personalBest);

        serverBestRef.current = parser.personalBest;

        const visibleRecord =
            Math.max(
                parser.personalBest,
                gameRef.current.score
            );

        recordRef.current = visibleRecord;
        setRecord(visibleRecord);

        if(parser.context === 1)
        {
            setResultState('accepted');
            setResultMessage(
                parser.message ||
                'Puntuación registrada.'
            );
            setNewServerRecord(parser.newRecord);
        }
        else if(parser.context === 2)
        {
            setResultState('rejected');
            setResultMessage(
                parser.message ||
                'La puntuación no pudo validarse.'
            );
            setNewServerRecord(false);
        }
    });

    useMessageEvent(
        ArcadeCloseEvent,
        (event: ArcadeCloseEvent) =>
    {
        const parser = event.getParser();

        if(
            parser.gameKey !== GAME_KEY ||
            parser.itemId !== itemIdRef.current
        ) return;

        visibleRef.current = false;
        keysRef.current.clear();
        runTokenRef.current = '';
        submittedRunRef.current = false;
        startPendingRef.current = false;
        setStartPending(false);
        setRecordsOpen(false);
        setIsVisible(false);
    });

    useEffect(() =>
    {
        const down = (event: KeyboardEvent) =>
        {
            if(!visibleRef.current) return;

            if([
                'ArrowLeft',
                'ArrowRight',
                'KeyA',
                'KeyD',
                'Space',
                'Enter',
                'KeyP',
                'Escape'
            ].includes(event.code))
            {
                event.preventDefault();
            }

            if(event.code === 'Enter')
            {
                const currentPhase = gameRef.current.phase;

                if(currentPhase === 'ready' ||
                        currentPhase === 'gameover')
                {
                    requestStartGame();
                    return;
                }
            }

            if(event.code === 'KeyP' || event.code === 'Escape')
            {
                togglePause();
                return;
            }

            keysRef.current.add(event.code);
        };

        const up = (event: KeyboardEvent) =>
        {
            keysRef.current.delete(event.code);
        };

        const blur = () =>
        {
            if(
                visibleRef.current &&
                gameRef.current.phase === 'playing'
            )
            {
                gameRef.current.phase = 'paused';
                setPhase('paused');
                keysRef.current.clear();
            }
        };

        window.addEventListener(
            'keydown',
            down,
            { passive: false }
        );
        window.addEventListener('keyup', up);
        window.addEventListener('blur', blur);

        return () =>
        {
            window.removeEventListener('keydown', down);
            window.removeEventListener('keyup', up);
            window.removeEventListener('blur', blur);
        };
    }, []);

    useEffect(() =>
    {
        let frameId = 0;

        const finishGame = () =>
        {
            const game = gameRef.current;

            if(game.phase === 'gameover') return;

            game.phase = 'gameover';
            game.bullets = [];
            setPhase('gameover');
            updateRecord(game.score);
            playGameOver();
            submitRunResult(
                game.score,
                game.level
            );
        };

        const award = (points: number) =>
        {
            const game = gameRef.current;

            game.score += points;
            setScore(game.score);
            updateRecord(game.score);
        };

        const update = (dt: number) =>
        {
            const game = gameRef.current;

            game.explosions.forEach(explosion =>
            {
                explosion.life -= dt;
            });

            game.explosions = game.explosions.filter(
                explosion => explosion.life > 0
            );

            game.damageFlash = Math.max(
                0,
                game.damageFlash - dt
            );

            game.levelBanner = Math.max(
                0,
                game.levelBanner - dt
            );

            if(game.phase !== 'playing') return;

            game.fireCooldown = Math.max(
                0,
                game.fireCooldown - dt
            );

            game.invulnerable = Math.max(
                0,
                game.invulnerable - dt
            );

            const keys = keysRef.current;
            let direction = 0;

            if(keys.has('ArrowLeft') || keys.has('KeyA'))
                direction -= 1;

            if(keys.has('ArrowRight') || keys.has('KeyD'))
                direction += 1;

            game.playerX += direction * 300 * dt;
            game.playerX = Math.max(
                24,
                Math.min(WIDTH - 24, game.playerX)
            );

            if(
                keys.has('Space') &&
                game.fireCooldown <= 0
            )
            {
                const playerBullets =
                    game.bullets.filter(x => !x.enemy).length;

                if(playerBullets < 2)
                {
                    game.bullets.push({
                        x: game.playerX,
                        y: game.playerY - 17,
                        vy: -445,
                        enemy: false
                    });

                    game.fireCooldown = 0.19;
                    playShoot();
                }
            }

            const alive = game.enemies.filter(x => x.alive);

            if(alive.length === 0)
            {
                const nextLevel = game.level + 1;
                const nextScore = game.score + 250;
                const remainingLives = game.lives;

                gameRef.current = makeGame(
                    nextLevel,
                    'playing',
                    nextScore,
                    remainingLives
                );

                gameRef.current.levelBanner = 1.15;

                setLevel(nextLevel);
                setScore(nextScore);
                updateRecord(nextScore);
                playLevelUp();
                return;
            }

            let hitEdge = false;

            for(const enemy of alive)
            {
                const nextX =
                    enemy.x +
                    (
                        game.direction *
                        game.enemySpeed *
                        dt
                    );

                if(
                    nextX < 20 ||
                    nextX + enemy.w > WIDTH - 20
                )
                {
                    hitEdge = true;
                    break;
                }
            }

            if(hitEdge)
            {
                game.direction *= -1;

                for(const enemy of alive)
                    enemy.y += 14;

                tone(
                    game.direction > 0 ? 115 : 98,
                    0.035,
                    'square',
                    0.008
                );
            }
            else
            {
                for(const enemy of alive)
                {
                    enemy.x +=
                        game.direction *
                        game.enemySpeed *
                        dt;
                }
            }

            if(
                alive.some(
                    x => x.y + x.h >= game.playerY - 10
                )
            )
            {
                finishGame();
                return;
            }

            game.enemyFireTimer -= dt;

            if(game.enemyFireTimer <= 0 && alive.length > 0)
            {
                const columns = new Map<number, Enemy>();

                for(const enemy of alive)
                {
                    const previous = columns.get(enemy.col);

                    if(!previous || enemy.y > previous.y)
                        columns.set(enemy.col, enemy);
                }

                const shooters = Array.from(columns.values());
                const shooter =
                    shooters[
                        Math.floor(
                            Math.random() * shooters.length
                        )
                    ];

                game.bullets.push({
                    x: shooter.x + (shooter.w / 2),
                    y: shooter.y + shooter.h + 2,
                    vy: 175 + (game.level * 20),
                    enemy: true
                });

                game.enemyFireTimer =
                    Math.max(
                        0.28,
                        0.86 - (game.level * 0.055)
                    );
            }

            for(const bullet of game.bullets)
                bullet.y += bullet.vy * dt;

            const deadBullets = new Set<Bullet>();

            for(const bullet of game.bullets)
            {
                if(
                    bullet.y < -20 ||
                    bullet.y > HEIGHT + 20
                )
                {
                    deadBullets.add(bullet);
                    continue;
                }

                if(!bullet.enemy)
                {
                    for(const enemy of game.enemies)
                    {
                        if(!enemy.alive) continue;

                        if(intersects(
                            bullet.x - 2,
                            bullet.y - 8,
                            4,
                            12,
                            enemy.x,
                            enemy.y,
                            enemy.w,
                            enemy.h))
                        {
                            enemy.alive = false;
                            deadBullets.add(bullet);

                            game.explosions.push({
                                x: enemy.x + (enemy.w / 2),
                                y: enemy.y + (enemy.h / 2),
                                life: 0.24,
                                duration: 0.24,
                                variant: 'enemy'
                            });

                            if(enemy.row === 0)
                                award(30);
                            else if(enemy.row <= 2)
                                award(20);
                            else
                                award(10);

                            playEnemyHit();
                            break;
                        }
                    }
                }
                else if(game.invulnerable <= 0)
                {
                    if(intersects(
                        bullet.x - 2,
                        bullet.y,
                        4,
                        11,
                        game.playerX - 18,
                        game.playerY - 9,
                        36,
                        18))
                    {
                        deadBullets.add(bullet);
                        game.lives -= 1;
                        game.invulnerable = 1.05;
                        game.damageFlash = 0.18;

                        game.explosions.push({
                            x: game.playerX,
                            y: game.playerY - 2,
                            life: 0.42,
                            duration: 0.42,
                            variant: 'player'
                        });

                        setLives(game.lives);
                        playPlayerHit();

                        if(game.lives <= 0)
                        {
                            finishGame();
                            break;
                        }
                    }
                }
            }

            if(deadBullets.size > 0)
            {
                game.bullets =
                    game.bullets.filter(
                        x => !deadBullets.has(x)
                    );
            }
        };

        const drawAlien = (
            ctx: CanvasRenderingContext2D,
            enemy: Enemy) =>
        {
            const x = Math.round(enemy.x);
            const y = Math.round(enemy.y);

            if(enemy.row === 0)
                ctx.fillStyle = '#ff718e';
            else if(enemy.row <= 2)
                ctx.fillStyle = '#ffd45d';
            else
                ctx.fillStyle = '#6cff86';

            ctx.fillRect(x + 5, y, 18, 4);
            ctx.fillRect(x + 2, y + 4, 24, 4);
            ctx.fillRect(x, y + 8, 28, 6);
            ctx.fillRect(x + 4, y + 14, 5, 4);
            ctx.fillRect(x + 19, y + 14, 5, 4);

            ctx.fillStyle = '#07101b';
            ctx.fillRect(x + 7, y + 7, 4, 4);
            ctx.fillRect(x + 17, y + 7, 4, 4);
        };

        const drawExplosion = (
            ctx: CanvasRenderingContext2D,
            explosion: Explosion) =>
        {
            const progress =
                1 - (explosion.life / explosion.duration);

            const size =
                explosion.variant === 'player'
                    ? 10 + (progress * 22)
                    : 5 + (progress * 13);

            const x = Math.round(explosion.x);
            const y = Math.round(explosion.y);
            const s = Math.max(2, Math.round(size / 4));

            ctx.fillStyle =
                explosion.variant === 'player'
                    ? '#69eaff'
                    : '#ffffff';

            ctx.fillRect(x - s, y - s, s * 2, s * 2);

            ctx.fillStyle =
                explosion.variant === 'player'
                    ? '#ffcf55'
                    : '#ffd45d';

            ctx.fillRect(
                x - Math.round(size),
                y - 2,
                Math.round(size / 2),
                4
            );
            ctx.fillRect(
                x + Math.round(size / 2),
                y - 2,
                Math.round(size / 2),
                4
            );
            ctx.fillRect(
                x - 2,
                y - Math.round(size),
                4,
                Math.round(size / 2)
            );
            ctx.fillRect(
                x - 2,
                y + Math.round(size / 2),
                4,
                Math.round(size / 2)
            );

            ctx.fillStyle = '#ff657e';

            ctx.fillRect(
                x - Math.round(size * .68),
                y - Math.round(size * .68),
                4,
                4
            );
            ctx.fillRect(
                x + Math.round(size * .55),
                y - Math.round(size * .68),
                4,
                4
            );
            ctx.fillRect(
                x - Math.round(size * .68),
                y + Math.round(size * .55),
                4,
                4
            );
            ctx.fillRect(
                x + Math.round(size * .55),
                y + Math.round(size * .55),
                4,
                4
            );
        };

        const draw = () =>
        {
            const canvas = canvasRef.current;
            if(!canvas) return;

            const ctx = canvas.getContext('2d');
            if(!ctx) return;

            const game = gameRef.current;

            ctx.imageSmoothingEnabled = false;

            const background =
                ctx.createLinearGradient(0, 0, 0, HEIGHT);

            background.addColorStop(0, '#030711');
            background.addColorStop(.55, '#050a16');
            background.addColorStop(1, '#07101c');

            ctx.fillStyle = background;
            ctx.fillRect(0, 0, WIDTH, HEIGHT);

            for(let i = 0; i < 94; i++)
            {
                const x = (i * 83 + 17) % WIDTH;
                const y = (i * 47 + 29) % (HEIGHT - 42);
                const bright = i % 11 === 0;
                const size = bright ? 2 : 1;

                ctx.fillStyle =
                    bright
                        ? '#6b9dc8'
                        : '#223d5d';

                ctx.fillRect(x, y, size, size);
            }

            ctx.fillStyle = '#101e36';
            ctx.fillRect(0, HEIGHT - 22, WIDTH, 2);

            ctx.fillStyle = '#354d78';
            for(let x = 14; x < WIDTH; x += 34)
                ctx.fillRect(x, HEIGHT - 16, 1, 1);

            for(const enemy of game.enemies)
            {
                if(enemy.alive)
                    drawAlien(ctx, enemy);
            }

            for(const bullet of game.bullets)
            {
                ctx.fillStyle =
                    bullet.enemy
                        ? '#ff657e'
                        : '#77efff';

                ctx.fillRect(
                    Math.round(bullet.x - 2),
                    Math.round(bullet.y - 7),
                    4,
                    12
                );

                if(!bullet.enemy)
                {
                    ctx.fillStyle = '#ffffff';
                    ctx.fillRect(
                        Math.round(bullet.x - 1),
                        Math.round(bullet.y - 9),
                        2,
                        3
                    );
                }
            }

            for(const explosion of game.explosions)
                drawExplosion(ctx, explosion);

            if(
                game.invulnerable <= 0 ||
                Math.floor(game.invulnerable * 13) % 2 === 0
            )
            {
                const x = Math.round(game.playerX);
                const y = Math.round(game.playerY);

                ctx.fillStyle = '#65e8ff';
                ctx.fillRect(x - 4, y - 15, 8, 5);
                ctx.fillRect(x - 11, y - 10, 22, 5);
                ctx.fillRect(x - 18, y - 5, 36, 10);

                ctx.fillStyle = '#c7fbff';
                ctx.fillRect(x - 3, y - 13, 6, 3);

                ctx.fillStyle = '#268ca8';
                ctx.fillRect(x - 14, y + 1, 7, 3);
                ctx.fillRect(x + 7, y + 1, 7, 3);
            }

            if(game.damageFlash > 0)
            {
                ctx.fillStyle =
                    `rgba(255, 77, 104, ${
                        Math.min(.20, game.damageFlash)
                    })`;

                ctx.fillRect(0, 0, WIDTH, HEIGHT);
            }

            if(game.levelBanner > 0)
            {
                ctx.fillStyle = 'rgba(2, 6, 15, .72)';
                ctx.fillRect(
                    WIDTH / 2 - 86,
                    HEIGHT / 2 - 34,
                    172,
                    68
                );

                ctx.strokeStyle = '#5be8ff';
                ctx.lineWidth = 2;
                ctx.strokeRect(
                    WIDTH / 2 - 86,
                    HEIGHT / 2 - 34,
                    172,
                    68
                );

                ctx.textAlign = 'center';
                ctx.textBaseline = 'middle';
                ctx.font = 'bold 22px monospace';
                ctx.fillStyle = '#ffffff';
                ctx.fillText(
                    `NIVEL ${ game.level }`,
                    WIDTH / 2,
                    HEIGHT / 2
                );
            }
        };

        const frame = (now: number) =>
        {
            if(visibleRef.current)
            {
                const last = lastFrameRef.current;
                lastFrameRef.current = now;

                if(last > 0)
                {
                    const dt = Math.min(
                        0.033,
                        Math.max(
                            0,
                            (now - last) / 1000
                        )
                    );

                    update(dt);
                }

                draw();
            }
            else
            {
                lastFrameRef.current = now;
            }

            frameId = window.requestAnimationFrame(frame);
        };

        frameId = window.requestAnimationFrame(frame);

        return () =>
        {
            window.cancelAnimationFrame(frameId);
        };
    }, []);

    if(!isVisible) return null;

    const phaseLabel =
        startPending
            ? 'CONECTANDO'
            : phase === 'playing'
                ? 'JUGANDO'
                : phase === 'paused'
                    ? 'PAUSA'
                    : phase === 'gameover'
                        ? 'FIN'
                        : 'PREPARADO';

    const formattedScore =
        score.toString().padStart(5, '0');

    const formattedRecord =
        record.toString().padStart(5, '0');

    return (
        <>
        <NitroCardView
            uniqueKey="space-invaders"
            className="nitro-space-invaders"
            theme="primary-slim"
            style={ { width: '780px' } }>
            <NitroCardHeaderView
                headerText="Space Invaders"
                onCloseClick={ close } />

            <NitroCardContentView
                gap={ 0 }
                className="space-invaders-content">
                <div
                    className="space-invaders-shell"
                    data-engine={ UI_MARKER }
                    data-audio-mix="space-audio-mix-v21"
                    data-item-id={ itemId }>
                    <span
                        className="space-cabinet-screw is-top-left"
                        aria-hidden="true" />
                    <span
                        className="space-cabinet-screw is-top-right"
                        aria-hidden="true" />
                    <span
                        className="space-cabinet-screw is-bottom-left"
                        aria-hidden="true" />
                    <span
                        className="space-cabinet-screw is-bottom-right"
                        aria-hidden="true" />

                    <div className="space-invaders-hud">
                        <div className="space-hud-cell is-score">
                            <span className="space-hud-label">
                                PUNTUACIÓN
                            </span>
                            <strong className="space-score-value">
                                { formattedScore }
                            </strong>
                        </div>

                        <div className="space-hud-cell is-level">
                            <span className="space-hud-label">
                                NIVEL
                            </span>
                            <strong className="space-level-value">
                                { level.toString().padStart(2, '0') }
                            </strong>
                        </div>

                        <div className="space-hud-cell is-lives">
                            <span className="space-hud-label">
                                VIDAS
                            </span>
                            <div
                                className="space-life-row"
                                aria-label={ `${ lives } vidas` }>
                                { Array.from(
                                    { length: MAX_LIVES },
                                    (_, index) =>
                                        <PixelHeart
                                            key={ index }
                                            active={ index < lives } />
                                ) }
                            </div>
                        </div>

                        <div
                            className={
                                `space-hud-cell is-status is-${ phase }`
                            }>
                            <PixelAlien />
                            <strong>{ phaseLabel }</strong>
                        </div>
                    </div>

                    <div className="space-invaders-stage">
                        <canvas
                            ref={ canvasRef }
                            className="space-invaders-canvas"
                            width={ WIDTH }
                            height={ HEIGHT }
                            tabIndex={ 0 }
                            aria-label="Space Invaders" />

                        { phase === 'ready' &&
                            <div className="space-game-overlay">
                                <div className="space-overlay-panel">
                                    <span className="space-overlay-kicker">
                                        DEFENSA PLANETARIA
                                    </span>
                                    <strong className="space-overlay-title">
                                        SPACE INVADERS
                                    </strong>
                                    <span className="space-overlay-copy">
                                        Elimina la formación antes de que
                                        alcance tu posición.
                                    </span>
                                    <button
                                        type="button"
                                        className="space-primary-button"
                                        disabled={ startPending }
                                        onClick={ requestStartGame }>
                                        { startPending
                                            ? 'CONECTANDO...'
                                            : 'JUGAR' }
                                    </button>

                                    { resultState === 'rejected' &&
                                        resultMessage &&
                                        <span className="space-start-error">
                                            { resultMessage }
                                        </span> }
                                    <span className="space-overlay-hint">
                                        ENTER también inicia la partida
                                    </span>
                                </div>
                            </div> }

                        { phase === 'paused' &&
                            <div className="space-game-overlay is-pause">
                                <div className="space-overlay-panel is-compact">
                                    <span className="space-overlay-kicker">
                                        PARTIDA DETENIDA
                                    </span>
                                    <strong className="space-overlay-title">
                                        PAUSA
                                    </strong>
                                    <button
                                        type="button"
                                        className="space-primary-button"
                                        onClick={ togglePause }>
                                        CONTINUAR
                                    </button>
                                    <span className="space-overlay-hint">
                                        P o ESC para continuar
                                    </span>
                                </div>
                            </div> }

                        { phase === 'gameover' &&
                            <div className="space-game-overlay is-gameover">
                                <div className="space-overlay-panel">
                                    <span className="space-overlay-kicker">
                                        MISIÓN FINALIZADA
                                    </span>
                                    <strong className="space-overlay-title">
                                        FIN DE LA PARTIDA
                                    </strong>
                                    <div className="space-gameover-results">
                                        <span>
                                            PUNTUACIÓN
                                            <b>{ formattedScore }</b>
                                        </span>
                                        <span>
                                            RÉCORD
                                            <b>{ formattedRecord }</b>
                                        </span>
                                    </div>
                                    <div
                                        className={
                                            `space-score-submit-status is-${
                                                resultState
                                            }`
                                        }>
                                        { resultState === 'accepted'
                                            ? newServerRecord
                                                ? 'NUEVO RÉCORD GLOBAL'
                                                : resultMessage
                                            : resultState === 'rejected'
                                                ? resultMessage
                                                : 'Guardando puntuación...' }
                                    </div>

                                    <button
                                        type="button"
                                        className="space-primary-button"
                                        disabled={ startPending }
                                        onClick={ requestStartGame }>
                                        { startPending
                                            ? 'CONECTANDO...'
                                            : 'JUGAR DE NUEVO' }
                                    </button>
                                </div>
                            </div> }
                    </div>

                    <div className="space-invaders-console">
                        <div className="space-controls-panel">
                            <div className="space-control-group">
                                <span className="space-keycap">←</span>
                                <span className="space-keycap">→</span>
                                <span className="space-control-separator">
                                    /
                                </span>
                                <span className="space-keycap is-letter">
                                    A
                                </span>
                                <span className="space-keycap is-letter">
                                    D
                                </span>
                                <span className="space-control-action">
                                    MOVER
                                </span>
                            </div>

                            <span className="space-console-divider" />

                            <div className="space-control-group">
                                <span className="space-keycap is-space">
                                    ESPACIO
                                </span>
                                <span className="space-control-action">
                                    DISPARAR
                                </span>
                            </div>

                            <span className="space-console-divider" />

                            <div className="space-control-group">
                                <span className="space-keycap is-letter">
                                    P
                                </span>
                                <span className="space-control-action">
                                    PAUSA
                                </span>
                            </div>
                        </div>

                        <div className="space-console-actions">
                            <button
                                type="button"
                                className={
                                    `space-sound-button${
                                        soundEnabled
                                            ? ' is-on'
                                            : ''
                                    }`
                                }
                                onClick={ toggleSound }
                                aria-pressed={ soundEnabled }>
                                <span
                                    className="space-sound-icon"
                                    aria-hidden="true">
                                    ♪
                                </span>
                                <span>
                                    SONIDO
                                    <b>
                                        { soundEnabled ? 'ON' : 'OFF' }
                                    </b>
                                </span>
                            </button>

                            <button
                                type="button"
                                className="space-restart-button"
                                onClick={ restartGame }>
                                <span className="space-restart-shine" />
                                <span>REINICIAR</span>
                            </button>
                        </div>
                    </div>

                    <div className="space-arcade-summary-bar">
                        <div className="space-arcade-summary-brand">
                            <span>RANKING GLOBAL</span>
                            <strong>SPACE INVADERS</strong>
                        </div>

                        <div className="space-arcade-summary-stats">
                            <span>
                                TU RÉCORD
                                <b>
                                    { serverBest
                                        .toString()
                                        .padStart(5, '0') }
                                </b>
                            </span>

                            <span>
                                TU PUESTO
                                <b>
                                    { personalRank > 0
                                        ? `#${ personalRank }`
                                        : '—' }
                                </b>
                            </span>

                            <span>
                                JUGADORES
                                <b>{ totalPlayers }</b>
                            </span>
                        </div>

                        <button
                            type="button"
                            className="space-records-button"
                            onClick={ openRecords }>
                            <span
                                className="space-records-star"
                                aria-hidden="true">
                                ★
                            </span>
                            <span>RÉCORDS</span>
                        </button>
                    </div>
                </div>
            </NitroCardContentView>
        </NitroCardView>

        <ArcadeLeaderboardView
            visible={ recordsOpen }
            gameName="Space Invaders"
            leaderboard={ leaderboard }
            personalBest={ serverBest }
            personalRank={ personalRank }
            totalPlayers={ totalPlayers }
            onClose={ closeRecords } />
        </>
    );
};
