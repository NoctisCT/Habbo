import { FC } from 'react';
import {
    NitroCardContentView,
    NitroCardHeaderView,
    NitroCardView
} from '../../common';
import './ArcadeLeaderboardView.scss';

export interface ArcadeLeaderboardEntry
{
    rank: number;
    username: string;
    score: number;
    level: number;
}

interface ArcadeLeaderboardViewProps
{
    visible: boolean;
    gameName: string;
    levelLabel?: string;
    leaderboard: ArcadeLeaderboardEntry[];
    personalBest: number;
    personalRank: number;
    totalPlayers: number;
    onClose: () => void;
}

const ARCADE_LEADERBOARD_MARKER =
    'BIRIBIRI_ARCADE_LEADERBOARD_MODAL_V1';

export const ArcadeLeaderboardView: FC<ArcadeLeaderboardViewProps> = ({
    visible,
    gameName,
    levelLabel = 'NIVEL',
    leaderboard,
    personalBest,
    personalRank,
    totalPlayers,
    onClose
}) =>
{
    if(!visible) return null;

    return (
        <NitroCardView
            uniqueKey={ `arcade-records-${ gameName }` }
            className="nitro-arcade-leaderboard"
            theme="primary-slim"
            style={ { width: '445px' } }>
            <NitroCardHeaderView
                headerText={ `Récords · ${ gameName }` }
                onCloseClick={ onClose } />

            <NitroCardContentView
                gap={ 0 }
                className="arcade-leaderboard-content">
                <div
                    className="arcade-leaderboard-shell"
                    data-leaderboard-component={
                        ARCADE_LEADERBOARD_MARKER
                    }>
                    <div className="arcade-leaderboard-summary">
                        <span>
                            TU RÉCORD
                            <b>
                                { personalBest
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

                    <div className="arcade-leaderboard-heading">
                        <span>CLASIFICACIÓN GLOBAL</span>
                        <strong>{ gameName.toUpperCase() }</strong>
                    </div>

                    { leaderboard.length > 0
                        ? <div className="arcade-leaderboard-list">
                            { leaderboard.map(entry =>
                                <div
                                    className={
                                        `arcade-leaderboard-row${
                                            entry.rank <= 3
                                                ? ` is-top-${ entry.rank }`
                                                : ''
                                        }`
                                    }
                                    key={
                                        `${ entry.rank }-${ entry.username }`
                                    }>
                                    <span className="arcade-rank-badge">
                                        { entry.rank }
                                    </span>

                                    <span className="arcade-rank-user">
                                        { entry.username }
                                    </span>

                                    <span className="arcade-rank-level">
                                        { levelLabel } { entry.level }
                                    </span>

                                    <strong className="arcade-rank-score">
                                        { entry.score
                                            .toString()
                                            .padStart(5, '0') }
                                    </strong>
                                </div>) }
                        </div>
                        : <div className="arcade-leaderboard-empty">
                            <span className="arcade-empty-star">★</span>
                            <strong>EL PRIMER PUESTO ESTÁ LIBRE</strong>
                            <span>
                                Aún no hay puntuaciones registradas.
                            </span>
                        </div> }

                    { personalRank > 10 &&
                        <div className="arcade-personal-position">
                            <span>TU POSICIÓN</span>
                            <strong>#{ personalRank }</strong>
                            <span>
                                { personalBest
                                    .toString()
                                    .padStart(5, '0') }
                            </span>
                        </div> }
                </div>
            </NitroCardContentView>
        </NitroCardView>
    );
};
