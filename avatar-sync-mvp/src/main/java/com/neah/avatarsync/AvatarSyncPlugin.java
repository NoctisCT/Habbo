package com.neah.avatarsync;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ItemInteraction;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;

import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.ICallable;
import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitIdleComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDanceComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;

import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent;
import com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent;
import com.eu.habbo.plugin.events.users.UserDisconnectEvent;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import com.eu.habbo.plugin.events.users.UserTalkEvent;
import com.eu.habbo.plugin.events.users.UserExitRoomEvent;
import com.eu.habbo.plugin.events.users.UserSignEvent;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;

import com.eu.habbo.util.pathfinding.Rotation;

import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Avatar Sync MVP
 *
 * One-way link: leader A -> follower B.
 *
 * Sincroniza:
 * - Movimiento relativo
 * - Giro
 * - Saludo
 * - Bailes
 * - Signos
 */
public class AvatarSyncPlugin extends HabboPlugin implements EventListener {

    /*
     * Modos de sincronizaci\u00f3n.
     *
     * Fase 1:
     * solo MODE 1 est\u00e1 activo.
     *
     * MODE 2 y MODE 3 se implementar\u00e1n despu\u00e9s sobre
     * esta misma estructura.
     */
    private static final int SYNC_MODE_LEADER_FREE = 1;
    private static final int SYNC_MODE_LEADER_LOCKED = 2;
    private static final int SYNC_MODE_ALL_LEADERS = 3;
    /*
     * AvatarSync WIRED Phase 1: backend sessions.
     *
     * Las sesiones WIRED reutilizan exactamente los SyncGroup
     * existentes. Los comandos admin siguen siendo independientes.
     */
    private static volatile AvatarSyncPlugin instance;

    private final ConcurrentHashMap<Integer, WiredSyncSession>
            wiredSessionsByItemId =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, Integer>
            participantToWiredItemId =
            new ConcurrentHashMap<>();

    static AvatarSyncPlugin getInstance() {
        return instance;
    }

    /*
     * Grupos indexados por l\u00edder.
     *
     * En esta primera fase cada grupo sigue teniendo
     * un solo follower efectivo para conservar exactamente
     * el comportamiento estable anterior.
     */
    private final ConcurrentHashMap<Integer, SyncGroup> groupsByLeader = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, Integer> participantToGroupLeader = new ConcurrentHashMap<>();

    /*
     * AvatarSync Phase 4: sync3 peer mode.
     *
     * Marca temporalmente el pr\u00f3ximo paso generado
     * internamente por AvatarSync.
     */
    private final ConcurrentHashMap<Integer, Long> replicatedStepGuards =
            new ConcurrentHashMap<>();

    /*
     * Incoming hooks.
     */
    private final ICallable userActionCallable = this::onUserActionPacket;
    private final ICallable userDanceCallable = this::onUserDancePacket;
    private final ICallable userSitCallable = this::onUserSitPacket;
    private final ICallable userVisualActionCallable = this::onVisualActionPacket;

    /*
     * AvatarSync Phase 3: sync2 movement lock.
     *
     * Bloquea \u00fanicamente RoomUserWalkEvent enviado
     * por el cliente de un follower MODE 2.
     */
    private final ICallable userWalkLockCallable =
            this::onUserWalkLockPacket;

    private volatile boolean userActionHookRegistered = false;
    private volatile boolean userDanceHookRegistered = false;
    private volatile boolean userSitHookRegistered = false;
    private volatile boolean userVisualActionHookRegistered = false;
    private volatile boolean userWalkLockHookRegistered = false;

    @Override
    public void onEnable() throws Exception {

        Emulator.getPluginManager().registerEvents(this, this);
        // AvatarSync WIRED Phase 1: expose active plugin instance.
        instance = this;

        CommandHandler.addCommand(new Sync1Command(this));
        CommandHandler.addCommand(new Sync2Command(this));
        CommandHandler.addCommand(new Sync3Command(this));
        CommandHandler.addCommand(new UnsyncCommand(this));

    }

    @Override
    public void onDisable() throws Exception {

        /*
         * Desregistrar saludo.
         */
        if (userActionHookRegistered && Emulator.getGameServer() != null) {

            Emulator.getGameServer()
                    .getPacketManager()
                    .unregisterCallables(
                            Incoming.RoomUserActionEvent,
                            userActionCallable);

            userActionHookRegistered = false;
        }

        /*
         * Desregistrar baile.
         */
        if (userDanceHookRegistered && Emulator.getGameServer() != null) {

            Emulator.getGameServer()
                    .getPacketManager()
                    .unregisterCallables(
                            Incoming.RoomUserDanceEvent,
                            userDanceCallable);

            userDanceHookRegistered = false;
        }

        /*
         * Desregistrar sit.
         */
        if (userSitHookRegistered && Emulator.getGameServer() != null) {

            Emulator.getGameServer()
                    .getPacketManager()
                    .unregisterCallables(
                            Incoming.RoomUserSitEvent,
                            userSitCallable
                    );

            userSitHookRegistered = false;
        }

        /*
         * Desregistrar acciones visuales.
         */
        if (userVisualActionHookRegistered && Emulator.getGameServer() != null) {

            Emulator.getGameServer()
                    .getPacketManager()
                    .unregisterCallables(
                            Incoming.RoomUserActionEvent,
                            userVisualActionCallable
                    );

            userVisualActionHookRegistered = false;
        }

        groupsByLeader.clear();
        participantToGroupLeader.clear();
        replicatedStepGuards.clear();        wiredSessionsByItemId.clear();
        participantToWiredItemId.clear();

        if (instance == this) {
            instance = null;
        }


    
        if (userWalkLockHookRegistered
                && Emulator.getGameServer() != null) {

            Emulator.getGameServer()
                    .getPacketManager()
                    .unregisterCallables(
                            Incoming.RoomUserWalkEvent,
                            userWalkLockCallable
                    );

            userWalkLockHookRegistered = false;
        }
}

    @Override
    public boolean hasPermission(Habbo habbo, String key) {
        return false;
    }

    /*
     * ============================================================
     * EMULATOR LOADED
     * ============================================================
     *
     * Los callables se registran aqu\u00ed porque GameServer ya existe.
     */
    /*
     * AvatarSync WIRED Phase 1: native WIRED interaction.
     */
    @EventHandler
    public void onLoadItemsManager(
            EmulatorLoadItemsManagerEvent event) {

        Emulator.getGameEnvironment()
                .getItemManager()
                .addItemInteraction(
                        new ItemInteraction(
                                "wf_act_avatar_sync",
                                WiredEffectAvatarSync.class
                        )
                );
        Emulator.getGameEnvironment()
                .getItemManager()
                .addItemInteraction(
                        new ItemInteraction(
                                "wf_act_stop_avatar_sync",
                                WiredEffectStopAvatarSync.class
                        )
                );

    }
    @EventHandler
    public void onEmulatorLoaded(EmulatorLoadedEvent event) {

        /*
         * Saludo.
         */
        if (!userActionHookRegistered) {

            Emulator.getGameServer()
                    .getPacketManager()
                    .registerCallable(
                            Incoming.RoomUserActionEvent,
                            userActionCallable);

            userActionHookRegistered = true;

        }

        /*
         * Bailes.
         */
        if (!userDanceHookRegistered) {

            Emulator.getGameServer()
                    .getPacketManager()
                    .registerCallable(
                            Incoming.RoomUserDanceEvent,
                            userDanceCallable);

            userDanceHookRegistered = true;

        }
    
        /*
         * Acciones visuales adicionales.
         */
        if (!userVisualActionHookRegistered) {

            Emulator.getGameServer()
                    .getPacketManager()
                    .registerCallable(
                            Incoming.RoomUserActionEvent,
                            userVisualActionCallable
                    );

            userVisualActionHookRegistered = true;

        }

        /*
         * SYNC2:
         * interceptar el intento de caminar enviado por el cliente.
         */
        if (!userWalkLockHookRegistered
                && Emulator.getGameServer() != null) {

            Emulator.getGameServer()
                    .getPacketManager()
                    .registerCallable(
                            Incoming.RoomUserWalkEvent,
                            userWalkLockCallable
                    );

            userWalkLockHookRegistered = true;

        }
}

    /*
     * ============================================================
     * SALUDO
     * ============================================================
     */
    private void onUserActionPacket(MessageHandler handler) {

        try {

            if (handler == null
                    || handler.client == null
                    || handler.client.getHabbo() == null
                    || handler.packet == null) {

                return;
            }

            final Habbo leader = handler.client.getHabbo();

            final int leaderId = leader.getHabboInfo().getId();

            final SyncGroup link = resolveReplicationGroup(leaderId);

            if (link == null) {
                return;
            }

            final Room room = leader.getHabboInfo().getCurrentRoom();

            if (room == null
                    || room.getId() != link.roomId) {

                return;
            }

            /*
             * Clonamos el paquete para no consumir el original
             * antes de que Morningstar lo procese.
             */
            final ClientMessage packetCopy = handler.packet.clone();

            final int action = packetCopy.readInt();

            /*
             * Por ahora solo saludo.
             */
            if (action != RoomUserAction.WAVE.getAction()) {
                return;
            }

            for (Integer followerId : link.replicationTargetIdsSnapshot(leaderId)) {
                final Habbo follower = room.getHabbo(followerId);

                if (follower == null
                        || follower.getRoomUnit() == null
                        || !follower.getRoomUnit().isInRoom()) {

                    continue;
                }

                room.sendComposer(
                        new RoomUserActionComposer(
                                follower.getRoomUnit(),
                                RoomUserAction.WAVE).compose());
            }
        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error sincronizando acci\u00f3n: "
                            + e.getMessage());
        }
    }

    /*
     * ============================================================
     * BAILES
     * ============================================================
     */
    private void onUserDancePacket(MessageHandler handler) {

        try {

            if (handler == null
                    || handler.client == null
                    || handler.client.getHabbo() == null
                    || handler.packet == null) {

                return;
            }

            final Habbo leader = handler.client.getHabbo();

            final int leaderId = leader.getHabboInfo().getId();

            final SyncGroup link = resolveReplicationGroup(leaderId);

            if (link == null) {
                return;
            }

            final Room room = leader.getHabboInfo().getCurrentRoom();

            if (room == null
                    || room.getId() != link.roomId) {

                return;
            }

            /*
             * Igual que con saludo:
             * leer una copia, nunca el paquete original.
             */
            final ClientMessage packetCopy = handler.packet.clone();

            final int danceId = packetCopy.readInt();

            /*
             * Morningstar tiene:
             *
             * 0 NONE
             * 1 HAB_HOP
             * 2 POGO_MOGO
             * 3 DUCK_FUNK
             * 4 THE_ROLLIE
             */
            if (danceId < 0
                    || danceId >= DanceType.values().length) {

                return;
            }

            for (Integer followerId : link.replicationTargetIdsSnapshot(leaderId)) {
                final Habbo follower = room.getHabbo(followerId);

                if (follower == null
                        || follower.getRoomUnit() == null
                        || !follower.getRoomUnit().isInRoom()) {

                    continue;
                }

                final RoomUnit followerUnit = follower.getRoomUnit();

                final DanceType danceType = DanceType.values()[danceId];

                followerUnit.setDanceType(danceType);

                room.unIdle(follower);

                room.sendComposer(
                        new RoomUserDanceComposer(
                                followerUnit).compose());

            }
        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error sincronizando baile: "
                            + e.getMessage());

            e.printStackTrace();
        }
    }


    /*
     * ============================================================
     * SIT - DETECCION DESPUES DEL CORE
     * ============================================================
     *
     * Morningstar ya ha aplicado makeSit() al l\u00edder cuando
     * UserIdleEvent es disparado desde RoomUserSitEvent.
     */
    @EventHandler
    public void onUserIdleForSit(UserIdleEvent event) {

        try {

            if (event == null
                    || event.habbo == null
                    || event.reason != UserIdleEvent.IdleReason.WALKED) {

                return;
            }

            final Habbo leader =
                    event.habbo;

            final RoomUnit leaderUnit =
                    leader.getRoomUnit();

            /*
             * WALKED tambi\u00e9n puede utilizarse en otros contextos.
             * Solo continuamos si el avatar est\u00e1 REALMENTE sentado.
             */
            if (leaderUnit == null
                    || !leaderUnit.isInRoom()
                    || !leaderUnit.getStatusMap()
                            .containsKey(RoomUnitStatus.SIT)) {

                return;
            }

            final int leaderId =
                    leader.getHabboInfo().getId();

            final SyncGroup link = resolveReplicationGroup(leaderId);

            if (link == null) {
                return;
            }

            final Room room =
                    leader.getHabboInfo().getCurrentRoom();

            if (room == null
                    || room.getId() != link.roomId) {

                return;
            }

            for (Integer followerId : link.replicationTargetIdsSnapshot(leaderId)) {
                final Habbo follower =
                        room.getHabbo(followerId);

                if (follower == null
                        || follower.getRoomUnit() == null
                        || !follower.getRoomUnit().isInRoom()) {

                    continue;
                }

                final RoomUnit followerUnit =
                        follower.getRoomUnit();

                if (followerUnit.isWalking()) {
                    followerUnit.stopWalking();
                }

                /*
                 * Misma funci\u00f3n nativa que Morningstar acaba
                 * de ejecutar sobre el l\u00edder.
                 */
                room.makeSit(follower);
                room.unIdle(follower);

            }
        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error SIT UserIdleEvent: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
    /*
     * ============================================================
     * SENTARSE
     * ============================================================
     */

    private void onUserSitPacket(MessageHandler handler) {

        try {

            if (handler == null
                    || handler.client == null
                    || handler.client.getHabbo() == null) {

                return;
            }

            final Habbo leader =
                    handler.client.getHabbo();

            final int leaderId =
                    leader.getHabboInfo().getId();

            final SyncGroup link =
                    groupsByLeader.get(leaderId);

            if (link == null) {
                return;
            }

            final Room room =
                    leader.getHabboInfo().getCurrentRoom();

            if (room == null
                    || room.getId() != link.roomId) {

                return;
            }

            /*
             * El callable se ejecuta antes del handler original.
             * Dejamos unos milisegundos para que el l\u00edder
             * sea sentado por Morningstar primero.
             */
            Emulator.getThreading().run(
                    () -> replicateSit(
                            leaderId,
                            link.roomId
                    ),
                    8
            );

        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error capturando SIT: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    private void replicateSit(
            int leaderId,
            int roomId
    ) {

        try {

            final SyncGroup link = resolveReplicationGroup(leaderId);

            if (link == null
                    || link.roomId != roomId) {

                return;
            }

            final Habbo leader =
                    Emulator.getGameEnvironment()
                            .getHabboManager()
                            .getHabbo(leaderId);

            if (leader == null) {
                return;
            }

            final Room room =
                    leader.getHabboInfo()
                            .getCurrentRoom();

            if (room == null
                    || room.getId() != roomId) {

                return;
            }

            for (Integer followerId : link.replicationTargetIdsSnapshot(leaderId)) {
                final Habbo follower =
                        room.getHabbo(followerId);

                if (follower == null
                        || follower.getRoomUnit() == null
                        || !follower.getRoomUnit().isInRoom()) {

                    continue;
                }

                if (follower.getRoomUnit().isWalking()) {
                    follower.getRoomUnit().stopWalking();
                }

                room.makeSit(follower);
                room.unIdle(follower);

            }
        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error replicando SIT: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }




    /*
     * ============================================================
     * ACCIONES VISUALES
     * ============================================================
     *
     * Captura acciones que Nitro mande directamente:
     *
     * 2 = kiss
     * 3 = laugh
     * 5 = idle
     * 6 = jump
     * 7 = thumb up
     *
     * WAVE queda en su hook existente y NO se toca.
     */

    private void onVisualActionPacket(MessageHandler handler) {

        try {

            if (handler == null
                    || handler.client == null
                    || handler.client.getHabbo() == null
                    || handler.packet == null) {

                return;
            }

            final Habbo leader =
                    handler.client.getHabbo();

            final int leaderId =
                    leader.getHabboInfo().getId();

            final SyncGroup link =
                    groupsByLeader.get(leaderId);

            if (link == null) {
                return;
            }

            final Room room =
                    leader.getHabboInfo().getCurrentRoom();

            if (room == null
                    || room.getId() != link.roomId) {

                return;
            }

            final ClientMessage copy =
                    handler.packet.clone();

            final int actionId =
                    copy.readInt();

            final RoomUserAction action;

            switch (actionId) {

                case 2:
                    action = RoomUserAction.BLOW_KISS;
                    break;

                case 3:
                    action = RoomUserAction.LAUGH;
                    break;

                case 5:
                    action = RoomUserAction.IDLE;
                    break;

                case 6:
                    action = RoomUserAction.JUMP;
                    break;

                case 7:
                    action = RoomUserAction.THUMB_UP;
                    break;

                default:
                    return;
            }

            replicateVisualAction(
                    leaderId,
                    link.roomId,
                    action
            );

        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error capturando acci\u00f3n visual: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    /*
     * ============================================================
     * COMANDOS + EMOCIONES DEL CHAT
     * ============================================================
     */
    @EventHandler
    public void onUserTalkVisualActions(UserTalkEvent event) {

        try {

            if (event == null
                    || event.habbo == null
                    || event.chatMessage == null) {

                return;
            }

            final Habbo leader =
                    event.habbo;

            final int leaderId =
                    leader.getHabboInfo().getId();

            final SyncGroup link =
                    resolveReplicationGroup(
                            leaderId
                    );

            if (link == null) {
                return;
            }

            final Room room =
                    leader.getHabboInfo().getCurrentRoom();

            if (room == null
                    || room.getId() != link.roomId) {

                return;
            }

            final String raw =
                    event.chatMessage.getUnfilteredMessage();

            if (raw == null) {
                return;
            }

            final String command =
                    raw.trim().toLowerCase();

            RoomUserAction action =
                    null;

            /*
             * Comandos visuales.
             */
            if (command.equals(":kiss")) {

                action = RoomUserAction.BLOW_KISS;

            } else if (command.equals(":laugh")) {

                action = RoomUserAction.LAUGH;

            } else if (command.equals(":idle")) {

                action = RoomUserAction.IDLE;

            } else if (command.equals(":jump")) {

                action = RoomUserAction.JUMP;

            } else if (command.equals("_b")) {

                action = RoomUserAction.THUMB_UP;
            }

            if (action != null) {

                final RoomUserAction finalAction =
                        action;

                Emulator.getThreading().run(
                        () -> replicateVisualAction(
                                leaderId,
                                link.roomId,
                                finalAction
                        ),
                        8
                );
            }

            /*
             * Expresiones del chat.
             *
             * Morningstar ya calcul\u00f3 aqu\u00ed el emotion.
             *
             * 1 = smile
             * 2 = angry
             * 3 = surprised
             * 4 = sad
             */
            final int emotion =
                    event.chatMessage.getEmotion();

            if (emotion >= 1
                    && emotion <= 4) {

                final int bubbleType =
                        event.chatMessage
                                .getBubble()
                                .getType();

                Emulator.getThreading().run(
                        () -> replicateChatEmotion(
                                leaderId,
                                link.roomId,
                                emotion,
                                bubbleType
                        ),
                        8
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error en acciones de chat: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    /*
     * Replica una acci\u00f3n visual al follower.
     */
    private void replicateVisualAction(
            int leaderId,
            int roomId,
            RoomUserAction action
    ) {

        try {

            final SyncGroup link = resolveReplicationGroup(leaderId);

            if (link == null
                    || link.roomId != roomId) {

                return;
            }

            final Habbo leader =
                    Emulator.getGameEnvironment()
                            .getHabboManager()
                            .getHabbo(leaderId);

            if (leader == null) {
                return;
            }

            final Room room =
                    leader.getHabboInfo()
                            .getCurrentRoom();

            if (room == null
                    || room.getId() != roomId) {

                return;
            }

            for (Integer followerId : link.replicationTargetIdsSnapshot(leaderId)) {
                final Habbo follower =
                        room.getHabbo(followerId);

                if (follower == null
                        || follower.getRoomUnit() == null
                        || !follower.getRoomUnit().isInRoom()) {

                    continue;
                }

                /*
                 * IDLE REAL.
                 *
                 * Morningstar no usa RoomUserAction.IDLE para mostrar
                 * el estado AFK. El estado real vive en RoomUnit.
                 */
                if (action == RoomUserAction.IDLE) {

                    follower.getRoomUnit().setIdle();

                    room.sendComposer(
                            new RoomUnitIdleComposer(
                                    follower.getRoomUnit()
                            ).compose()
                    );


                    continue;
                }
                /*
                 * Para acciones normales sacamos al follower de AFK.
                 * IDLE es la excepci\u00f3n.
                 */
                if (action != RoomUserAction.IDLE) {
                    room.unIdle(follower);
                }

                room.sendComposer(
                        new RoomUserActionComposer(
                                follower.getRoomUnit(),
                                action
                        ).compose()
                );

            }
        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error replicando acci\u00f3n visual: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    /*
     * Replica \u00fanicamente la EMOCI\u00cd\u201cN facial del chat.
     *
     * Construimos manualmente RoomUserTalkComposer porque
     * el composer normal descarta mensajes vac\u00edos.
     *
     * No repetimos el texto del l\u00edder.
     */
    private void replicateChatEmotion(
            int leaderId,
            int roomId,
            int emotion,
            int bubbleType
    ) {

        try {

            final SyncGroup link = resolveReplicationGroup(leaderId);

            if (link == null
                    || link.roomId != roomId) {

                return;
            }

            final Habbo leader =
                    Emulator.getGameEnvironment()
                            .getHabboManager()
                            .getHabbo(leaderId);

            if (leader == null) {
                return;
            }

            final Room room =
                    leader.getHabboInfo()
                            .getCurrentRoom();

            if (room == null
                    || room.getId() != roomId) {

                return;
            }

            for (Integer followerId : link.replicationTargetIdsSnapshot(leaderId)) {
                final Habbo follower =
                        room.getHabbo(followerId);

                if (follower == null
                        || follower.getRoomUnit() == null
                        || !follower.getRoomUnit().isInRoom()) {

                    continue;
                }

                /*
                 * Generamos primero un mensaje REAL que Morningstar
                 * reconoce y usa para calcular la emoci\u00f3n.
                 *
                 * 1 = sonrisa
                 * 2 = enfado
                 * 3 = sorpresa
                 * 4 = tristeza
                 */
                final String emotionSource;

                switch (emotion) {

                    case 1:
                        emotionSource = ":)";
                        break;

                    case 2:
                        emotionSource = ":@";
                        break;

                    case 3:
                        emotionSource = ":o";
                        break;

                    case 4:
                        emotionSource = ":(";
                        break;

                    default:
                        continue;
                }

                /*
                 * RoomChatMessage calcula aqu\u00ed internamente
                 * el gesture/emotion correcto.
                 */
                final RoomChatMessage visualMessage =
                        new RoomChatMessage(
                                emotionSource,
                                follower,
                                RoomChatMessageBubbles.getBubble(
                                        bubbleType
                                )
                        );

                /*
                 * La emoci\u00f3n YA est\u00e1 calculada.
                 *
                 * Ahora ocultamos el texto sin modificar
                 * el valor emotion almacenado en RoomChatMessage.
                 */
                visualMessage.setMessage(
                        "\u200B"
                );

                /*
                 * Esta vez NO fabricamos el ServerMessage.
                 * Dejamos que Morningstar serialice exactamente
                 * su paquete de chat nativo.
                 */
                room.sendComposer(
                        new RoomUserTalkComposer(
                                visualMessage
                        ).compose()
                );

            }
        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error replicando emoci\u00f3n nativa: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    /*
     * ============================================================
     * COMANDOS DE POSTURA
     * ============================================================
     *
     * Detecta comandos escritos:
     *
     * :sit
     * :stand
     * :lay
     *
     * UserTalkEvent ocurre antes de que Morningstar ejecute
     * el comando del l\u00edder, por eso esperamos unos milisegundos.
     */

    @EventHandler
    /*
     * AvatarSync Phase 6: sync3 peer chat origin
     *
     * En MODE 3 cualquier miembro puede originar
     * comandos y emociones del chat.
     */
    public void onUserTalkPostureCommands(UserTalkEvent event) {

        try {

            if (event == null
                    || event.habbo == null
                    || event.chatMessage == null) {

                return;
            }

            final String raw =
                    event.chatMessage.getUnfilteredMessage();

            if (raw == null) {
                return;
            }

            final String command =
                    raw.trim().toLowerCase();

            if (!command.equals(":sit")
                    && !command.equals(":stand")
                    && !command.equals(":lay")) {

                return;
            }

            final Habbo leader =
                    event.habbo;

            final int leaderId =
                    leader.getHabboInfo().getId();

            final SyncGroup link =
                    resolveReplicationGroup(
                            leaderId
                    );

            if (link == null) {
                return;
            }

            final Room room =
                    leader.getHabboInfo().getCurrentRoom();

            if (room == null
                    || room.getId() != link.roomId) {

                return;
            }


            Emulator.getThreading().run(
                    () -> replicatePostureCommand(
                            leaderId,
                            link.roomId,
                            command
                    ),
                    8
            );

        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error capturando comando de postura: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    private void replicatePostureCommand(
            int leaderId,
            int roomId,
            String command
    ) {

        try {

            final SyncGroup link = resolveReplicationGroup(leaderId);

            if (link == null
                    || link.roomId != roomId) {

                return;
            }

            final Habbo leader =
                    Emulator.getGameEnvironment()
                            .getHabboManager()
                            .getHabbo(leaderId);

            if (leader == null) {
                return;
            }

            final Room room =
                    leader.getHabboInfo()
                            .getCurrentRoom();

            if (room == null
                    || room.getId() != roomId) {

                return;
            }

            for (Integer followerId : link.replicationTargetIdsSnapshot(leaderId)) {
                final Habbo follower =
                        room.getHabbo(followerId);

                if (follower == null
                        || follower.getRoomUnit() == null
                        || !follower.getRoomUnit().isInRoom()) {

                    continue;
                }

                final RoomUnit unit =
                        follower.getRoomUnit();

                /*
                 * ====================================================
                 * :sit
                 * ====================================================
                 */
                if (command.equals(":sit")) {

                    if (unit.isWalking()) {
                        unit.stopWalking();
                    }

                    room.makeSit(follower);
                    room.unIdle(follower);


                    continue;
                }

                /*
                 * ====================================================
                 * :stand
                 * ====================================================
                 */
                if (command.equals(":stand")) {

                    room.makeStand(follower);
                    room.unIdle(follower);


                    continue;
                }

                /*
                 * ====================================================
                 * :lay
                 * ====================================================
                 *
                 * Replica la l\u00f3gica nativa de LayCommand.
                 */
                if (command.equals(":lay")) {

                    if (!unit.canForcePosture()) {


                        continue;
                    }

                    unit.cmdLay = true;

                    room.updateHabbo(follower);

                    unit.cmdSit = true;

                    final int rotation =
                            unit.getBodyRotation().getValue();

                    unit.setBodyRotation(
                            RoomUserRotation.values()[
                                    rotation - rotation % 2
                            ]
                    );

                    final RoomTile tile =
                            unit.getCurrentLocation();

                    if (tile == null) {
                        continue;
                    }

                    /*
                     * Misma comprobaci\u00f3n de espacio
                     * que hace LayCommand de Morningstar.
                     */
                    for (int i = 0; i < 3; i++) {

                        final RoomTile front =
                                room.getLayout()
                                        .getTileInFront(
                                                tile,
                                                unit.getBodyRotation().getValue(),
                                                i
                                        );

                        if (front == null
                                || !front.isWalkable()) {


                            continue;
                        }
                    }

                    unit.setStatus(
                            RoomUnitStatus.LAY,
                            "0.5"
                    );

                    room.sendComposer(
                            new RoomUserStatusComposer(
                                    unit
                            ).compose()
                    );

                    room.unIdle(follower);

                }
            }
        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error replicando comando de postura: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    /*
     * ============================================================
     * SIGNOS
     * ============================================================
     */
    @EventHandler
    public void onUserSign(UserSignEvent event) {

        try {

            if (event == null
                    || event.habbo == null) {

                return;
            }

            final Habbo leader = event.habbo;

            final int leaderId = leader.getHabboInfo().getId();

            final SyncGroup link = resolveReplicationGroup(leaderId);

            if (link == null) {
                return;
            }

            final Room room = leader.getHabboInfo().getCurrentRoom();

            if (room == null
                    || room.getId() != link.roomId) {

                return;
            }

            for (Integer followerId : link.replicationTargetIdsSnapshot(leaderId)) {
                final Habbo follower = room.getHabbo(followerId);

                if (follower == null
                        || follower.getRoomUnit() == null
                        || !follower.getRoomUnit().isInRoom()) {

                    continue;
                }

                final RoomUnit followerUnit = follower.getRoomUnit();

                /*
                 * Aplicar al follower exactamente el mismo signo.
                 */
                followerUnit.setStatus(
                        RoomUnitStatus.SIGN,
                        Integer.toString(event.sign));

                room.unIdle(follower);

                room.sendComposer(
                        new RoomUserStatusComposer(
                                followerUnit).compose());

            }
        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error sincronizando signo: "
                            + e.getMessage());

            e.printStackTrace();
        }
    }

    /*
     * ============================================================
     * MOVIMIENTO
     * ============================================================
     */
    @EventHandler
    public void onUserTakeStep(
            UserTakeStepEvent event) {

        /*
         * AvatarSync Phase 5:
         *
         * Ya NO iniciamos sincronizaci\u00f3n desde UserTakeStepEvent.
         *
         * Este evento ocurre dentro del ciclo de Room y hac\u00eda que
         * el resultado dependiera del orden de procesamiento:
         *
         * ID 5 -> ID 6 -> ID 7
         *
         * El movimiento ahora nace antes del ciclo desde
         * RoomUserWalkEvent.
         *
         * Mantener el handler vac\u00edo evita doble movimiento y,
         * especialmente en MODE 3, evita rebotes.
         */
    }

    /*
     * ============================================================
     * GIRO
     * ============================================================
     */
    @EventHandler
    public void onRoomUnitLookAtPoint(
            RoomUnitLookAtPointEvent event) {

        if (event == null
                || event.room == null
                || event.roomUnit == null
                || event.location == null) {

            return;
        }

        final Habbo leader = event.room.getHabbo(
                event.roomUnit);

        if (leader == null) {
            return;
        }

        final int leaderId = leader.getHabboInfo().getId();

        final SyncGroup link = resolveReplicationGroup(leaderId);

        if (link == null
                || event.room.getId() != link.roomId) {

            return;
        }

        for (Integer followerId : link.replicationTargetIdsSnapshot(leaderId)) {
            final Habbo follower = event.room.getHabbo(
                    followerId);

            if (follower == null) {
                continue;
            }

            final RoomUnit followerUnit = follower.getRoomUnit();

            if (followerUnit == null
                    || !followerUnit.isInRoom()) {

                continue;
            }

            final int rotation = Rotation.Calculate(
                    event.roomUnit.getX(),
                    event.roomUnit.getY(),
                    event.location.x,
                    event.location.y);

            followerUnit.setRotation(
                    RoomUserRotation.values()[rotation]);

            event.room.sendComposer(
                    new RoomUserStatusComposer(
                            followerUnit).compose());
        
        }
}

    /*
     * ============================================================
     * SALIDA DE SALA
     * ============================================================
     */
    @EventHandler
    public void onUserExitRoom(
            UserExitRoomEvent event) {

        if (event != null
                && event.habbo != null) {

            unlinkParticipant(
                    event.habbo
                            .getHabboInfo()
                            .getId(),
                    true);
            // AvatarSync WIRED lifecycle: room exit.
            cleanupWiredParticipant(
                    event.habbo
                            .getHabboInfo()
                            .getId()
            );
        }
    }

    /*
     * ============================================================
     * DESCONEXI\u00cd\u201cN
     * ============================================================
     */
    @EventHandler
    public void onUserDisconnect(
            UserDisconnectEvent event) {

        if (event != null
                && event.habbo != null) {

            unlinkParticipant(
                    event.habbo
                            .getHabboInfo()
                            .getId(),
                    true);
            // AvatarSync WIRED lifecycle: disconnect.
            cleanupWiredParticipant(
                    event.habbo
                            .getHabboInfo()
                            .getId()
            );
        }
    }

    /*
     * ============================================================
     * CREAR GRUPO - MODE 1
     * ============================================================
     */

    /*
     * ============================================================
     * SYNC MODE 2 - MOVEMENT LOCK
     * ============================================================
     *
     * El PacketManager ejecuta este callable antes del handler
     * normal de RoomUserWalkEvent.
     *
     * Cancelamos SOLO el walk enviado directamente por el cliente
     * de un follower perteneciente a MODE 2.
     */
    /*
     * ============================================================
     * AvatarSync Phase 5: pre-tick walk sync
     * ============================================================
     *
     * RoomUserWalkEvent llega ANTES de que Morningstar ejecute
     * los pasos del ciclo de sala.
     *
     * Leemos una COPIA del paquete para no consumir los X/Y que
     * necesita el handler original de Morningstar.
     *
     * MODE 1:
     *   l\u00edder replica movimiento; followers siguen libres.
     *
     * MODE 2:
     *   l\u00edder replica movimiento; followers no pueden caminar
     *   manualmente.
     *
     * MODE 3:
     *   cualquier miembro replica movimiento a todos los dem\u00e1s.
     */
    private void onUserWalkLockPacket(
            MessageHandler handler) {

        try {

            if (handler == null
                    || handler.client == null
                    || handler.client.getHabbo() == null
                    || handler.packet == null) {

                return;
            }

            final Habbo actor =
                    handler.client.getHabbo();

            final int actorId =
                    actor
                            .getHabboInfo()
                            .getId();

            final Integer groupLeaderId =
                    participantToGroupLeader.get(
                            actorId
                    );

            /*
             * Usuario sin AvatarSync:
             * Morningstar sigue normalmente.
             */
            if (groupLeaderId == null) {
                return;
            }

            final SyncGroup group =
                    groupsByLeader.get(
                            groupLeaderId
                    );

            /*
             * Reparar \u00edndice hu\u00e9rfano.
             */
            if (group == null
                    || !group.contains(actorId)) {

                participantToGroupLeader.remove(
                        actorId,
                        groupLeaderId
                );

                return;
            }

            final Room room =
                    actor
                            .getHabboInfo()
                            .getCurrentRoom();

            if (room == null
                    || room.getId() != group.roomId) {

                unlinkParticipant(
                        actorId,
                        false
                );

                return;
            }

            /*
             * ====================================================
             * MODE 2 FOLLOWER
             * ====================================================
             *
             * Sigue bloqueado exactamente como antes.
             */
            if (group.mode == SYNC_MODE_LEADER_LOCKED
                    && actorId != group.leaderId) {

                handler.isCancelled = true;
                return;
            }

            /*
             * ====================================================
             * \u00bfPUEDE ESTE USUARIO ORIGINAR MOVIMIENTO SYNC?
             * ====================================================
             */

            final boolean canReplicateMovement =
                    group.mode == SYNC_MODE_ALL_LEADERS
                    || actorId == group.leaderId;

            /*
             * MODE 1 follower:
             * puede caminar libremente, pero no controla al grupo.
             */
            if (!canReplicateMovement) {
                return;
            }

            final RoomUnit actorUnit =
                    actor.getRoomUnit();

            if (actorUnit == null
                    || !actorUnit.isInRoom()
                    || !actorUnit.canWalk()
                    || actorUnit.isTeleporting
                    || actorUnit.isKicked
                    || actorUnit.cmdTeleport
                    || room.getLayout() == null) {

                return;
            }

            /*
             * MUY IMPORTANTE:
             *
             * clone() usa un buffer duplicado.
             * Leer aqu\u00ed NO mueve el readerIndex del paquete real.
             */
            final ClientMessage walkPacket =
                    handler.packet.clone();

            final int requestedX =
                    walkPacket.readInt();

            final int requestedY =
                    walkPacket.readInt();

            if (requestedX == actorUnit.getX()
                    && requestedY == actorUnit.getY()) {

                return;
            }

            final RoomTile actorTarget =
                    room.getLayout()
                            .getTile(
                                    (short) requestedX,
                                    (short) requestedY
                            );

            if (actorTarget == null) {
                return;
            }

            /*
             * Para movimiento normal preparamos tambi\u00e9n al actor
             * ahora mismo.
             *
             * El handler original de Morningstar se ejecutar\u00e1
             * despu\u00e9s y volver\u00e1 a validar/establecer este destino.
             */
            if (!actorTarget.isWalkable()
                    && !room.canSitOrLayAt(
                            actorTarget.x,
                            actorTarget.y
                    )) {

                /*
                 * Objetos especiales / destinos especiales:
                 * dejamos que Morningstar los resuelva solo.
                 */
                return;
            }

            /*
             * Desplazamiento TOTAL solicitado.
             *
             * Ejemplo:
             *
             * actor      5,5
             * click      8,7
             *
             * dx = +3
             * dy = +2
             *
             * Cada miembro conserva su posici\u00f3n relativa.
             */
            final int dx =
                    requestedX
                            - actorUnit.getX();

            final int dy =
                    requestedY
                            - actorUnit.getY();

            if (dx == 0
                    && dy == 0) {

                return;
            }

            /*
             * Pre-armamos al actor ANTES de devolver el callable.
             *
             * As\u00ed no dependemos de si ID 5, 6 o 7 aparece primero
             * dentro del ciclo de Room.
             */
            actorUnit.setGoalLocation(
                    actorTarget
            );

            /*
             * Ahora todos los dem\u00e1s reciben su destino traducido
             * en el MISMO procesamiento del paquete.
             */
            for (Integer targetId :
                    group.replicationTargetIdsSnapshot(
                            actorId
                    )) {

                if (targetId == null) {
                    continue;
                }

                final Habbo targetHabbo =
                        room.getHabbo(
                                targetId
                        );

                if (targetHabbo == null
                        || targetHabbo.getHabboInfo()
                                .getCurrentRoom() != room) {

                    continue;
                }

                final RoomUnit targetUnit =
                        targetHabbo.getRoomUnit();

                if (targetUnit == null
                        || !targetUnit.isInRoom()
                        || !targetUnit.canWalk()
                        || targetUnit.isTeleporting
                        || targetUnit.isKicked
                        || targetUnit.cmdTeleport) {

                    continue;
                }

                final int translatedX =
                        targetUnit.getX()
                                + dx;

                final int translatedY =
                        targetUnit.getY()
                                + dy;

                final RoomTile translatedTarget =
                        room.getLayout()
                                .getTile(
                                        (short) translatedX,
                                        (short) translatedY
                                );

                if (translatedTarget == null) {
                    continue;
                }

                if (!translatedTarget.isWalkable()
                        && !room.canSitOrLayAt(
                                translatedTarget.x,
                                translatedTarget.y
                        )) {

                    continue;
                }

                targetUnit.setGoalLocation(
                        translatedTarget
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "[AvatarSync] Error en pre-tick movement sync: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
    /*
     * ============================================================
     * AvatarSync Phase 4: sync3 peer mode
     * ============================================================
     */

    private SyncGroup resolveReplicationGroup(
            int participantId) {

        final Integer groupLeaderId =
                participantToGroupLeader.get(
                        participantId
                );

        if (groupLeaderId == null) {
            return null;
        }

        final SyncGroup group =
                groupsByLeader.get(
                        groupLeaderId
                );

        if (group == null) {

            participantToGroupLeader.remove(
                    participantId,
                    groupLeaderId
            );

            return null;
        }

        if (!group.contains(participantId)) {

            participantToGroupLeader.remove(
                    participantId,
                    groupLeaderId
            );

            return null;
        }

        /*
         * MODE 3:
         * cualquier miembro real del grupo puede originar.
         */
        if (group.mode == SYNC_MODE_ALL_LEADERS) {
            return group;
        }

        /*
         * MODE 1 / MODE 2:
         * \u00fanicamente el l\u00edder puede originar r\u00e9plicas.
         */
        if (participantId == group.leaderId) {
            return group;
        }

        return null;
    }

    private void armReplicatedStepGuard(
            int participantId) {

        replicatedStepGuards.put(
                participantId,
                System.currentTimeMillis() + 2000L
        );
    }

    private boolean consumeReplicatedStepGuard(
            int participantId) {

        final Long expiresAt =
                replicatedStepGuards.remove(
                        participantId
                );

        if (expiresAt == null) {
            return false;
        }

        return expiresAt.longValue()
                >= System.currentTimeMillis();
    }

    private synchronized String createMode3Group(
            Habbo actor,
            Habbo peer) {

        if (actor == null
                || peer == null) {

            return "No se pudo crear el grupo.";
        }

        final int actorId =
                actor.getHabboInfo().getId();

        final int peerId =
                peer.getHabboInfo().getId();

        // AvatarSync WIRED: no mezclar comandos y sesiones WIRED.
        cleanupStaleWiredBinding(actorId);
        cleanupStaleWiredBinding(peerId);

        if (participantToWiredItemId.containsKey(actorId)
                || participantToWiredItemId.containsKey(peerId)) {

            return "No puedes mezclar una sincronizacion WIRED con comandos.";
        }
        if (actorId == peerId) {

            return "No puedes sincronizarte contigo mismo.";
        }

        final Room room =
                actor
                        .getHabboInfo()
                        .getCurrentRoom();

        if (room == null
                || peer.getHabboInfo()
                        .getCurrentRoom() != room) {

            return "Los dos avatares deben estar en la misma sala.";
        }

        /*
         * Buscar grupo actual del actor.
         */
        SyncGroup group = null;

        final Integer actorGroupLeaderId =
                participantToGroupLeader.get(
                        actorId
                );

        if (actorGroupLeaderId != null) {

            group =
                    groupsByLeader.get(
                            actorGroupLeaderId
                    );

            if (group == null
                    || !group.contains(actorId)) {

                participantToGroupLeader.remove(
                        actorId,
                        actorGroupLeaderId
                );

                group = null;
            }

            else if (group.roomId
                    != room.getId()) {

                unlinkParticipant(
                        actorId,
                        false
                );

                group = null;
            }

            else if (group.mode
                    != SYNC_MODE_ALL_LEADERS) {

                return "Ya formas parte de una sincronizaci\u00f3n de otro modo. Usa :unsync primero.";
            }
        }

        /*
         * El nuevo peer no puede pertenecer
         * simult\u00e1neamente a otro grupo.
         */
        final Integer peerGroupLeaderId =
                participantToGroupLeader.get(
                        peerId
                );

        if (peerGroupLeaderId != null) {

            final SyncGroup peerGroup =
                    groupsByLeader.get(
                            peerGroupLeaderId
                    );

            if (peerGroup == null
                    || !peerGroup.contains(peerId)) {

                participantToGroupLeader.remove(
                        peerId,
                        peerGroupLeaderId
                );
            }

            else {

                if (group != null
                        && peerGroup == group) {

                    return peer
                            .getHabboInfo()
                            .getUsername()
                            + " ya forma parte del grupo sync3.";
                }

                return peer
                        .getHabboInfo()
                        .getUsername()
                        + " ya est\u00e1 en otra sincronizaci\u00f3n.";
            }
        }

        /*
         * Crear nuevo grupo MODE 3.
         *
         * leaderId aqu\u00ed solo funciona como clave interna.
         * En comportamiento todos son peers.
         */
        if (group == null) {

            if (groupsByLeader.containsKey(actorId)) {

                return "No se pudo crear el grupo sync3 por una clave interna ocupada.";
            }

            group =
                    new SyncGroup(
                            room.getId(),
                            actorId,
                            peerId,
                            SYNC_MODE_ALL_LEADERS
                    );

            groupsByLeader.put(
                    actorId,
                    group
            );

            participantToGroupLeader.put(
                    actorId,
                    actorId
            );

            participantToGroupLeader.put(
                    peerId,
                    actorId
            );

        }

        /*
         * Grupo existente:
         * cualquier peer puede a\u00f1adir otro peer.
         */
        else {

            if (!group.addMember(peerId)) {

                return peer
                        .getHabboInfo()
                        .getUsername()
                        + " ya forma parte del grupo sync3.";
            }

            participantToGroupLeader.put(
                    peerId,
                    group.leaderId
            );

        }

        peer.whisper(
                "Ahora formas parte de un grupo AvatarSync modo 3."
        );

        return "A\u00f1adido a sync3: "
                + peer
                        .getHabboInfo()
                        .getUsername()
                + ". Miembros: "
                + group.size()
                + ".";
    }

    private synchronized String createMode1Group(
            Habbo leader,
            Habbo follower) {

        if (leader == null
                || follower == null) {

            return "No se pudo crear el v\u00ednculo.";
        }

        final int leaderId =
                leader.getHabboInfo().getId();

        final int followerId =
                follower.getHabboInfo().getId();

        // AvatarSync WIRED: no mezclar comandos y sesiones WIRED.
        cleanupStaleWiredBinding(leaderId);
        cleanupStaleWiredBinding(followerId);

        if (participantToWiredItemId.containsKey(leaderId)
                || participantToWiredItemId.containsKey(followerId)) {

            return "No puedes mezclar una sincronizacion WIRED con comandos.";
        }
        if (leaderId == followerId) {

            return "No puedes sincronizarte contigo mismo.";
        }

        final Room room =
                leader.getHabboInfo()
                        .getCurrentRoom();

        if (room == null
                || follower.getHabboInfo()
                        .getCurrentRoom() != room) {

            return "Los dos avatares deben estar en la misma sala.";
        }

        SyncGroup group =
                groupsByLeader.get(
                        leaderId
                );

        final Integer leaderGroupId =
                participantToGroupLeader.get(
                        leaderId
                );

        /*
         * El l\u00edder ya pertenece a otro grupo como follower.
         */
        if (leaderGroupId != null
                && leaderGroupId.intValue() != leaderId) {

            return "Ya formas parte de otra sincronizaci\u00f3n. Usa :unsync primero.";
        }

        /*
         * Reparar \u00edndice hu\u00e9rfano.
         */
        if (leaderGroupId != null
                && group == null) {

            participantToGroupLeader.remove(
                    leaderId,
                    leaderGroupId
            );
        }

        /*
         * Grupo existente:
         * debe ser MODE 1 y estar en la misma sala.
         */
        if (group != null) {

            if (group.roomId != room.getId()) {

                unlinkParticipant(
                        leaderId,
                        false
                );

                group = null;
            }

            else if (group.mode
                    != SYNC_MODE_LEADER_FREE) {

                return "Tu sincronizaci\u00f3n actual no est\u00e1 en modo 1.";
            }
        }

        /*
         * El nuevo follower no puede pertenecer
         * simult\u00e1neamente a otro grupo.
         */
        final Integer followerGroupLeader =
                participantToGroupLeader.get(
                        followerId
                );

        if (followerGroupLeader != null) {

            final SyncGroup followerGroup =
                    groupsByLeader.get(
                            followerGroupLeader
                    );

            /*
             * Reparar \u00edndice hu\u00e9rfano.
             */
            if (followerGroup == null) {

                participantToGroupLeader.remove(
                        followerId,
                        followerGroupLeader
                );
            }

            else {

                if (group != null
                        && followerGroupLeader.intValue() == leaderId
                        && group.contains(
                                followerId
                        )) {

                    return follower
                            .getHabboInfo()
                            .getUsername()
                            + " ya forma parte de tu sincronizaci\u00f3n.";
                }

                return follower
                        .getHabboInfo()
                        .getUsername()
                        + " ya est\u00e1 en otra sincronizaci\u00f3n.";
            }
        }

        /*
         * Primer follower:
         * crear grupo.
         */
        if (group == null) {

            group =
                    new SyncGroup(
                            room.getId(),
                            leaderId,
                            followerId,
                            SYNC_MODE_LEADER_FREE
                    );

            groupsByLeader.put(
                    leaderId,
                    group
            );

            participantToGroupLeader.put(
                    leaderId,
                    leaderId
            );

            participantToGroupLeader.put(
                    followerId,
                    leaderId
            );

        }

        /*
         * Grupo existente:
         * a\u00f1adir follower.
         */
        else {

            if (!group.addMember(
                    followerId
            )) {

                return follower
                        .getHabboInfo()
                        .getUsername()
                        + " ya forma parte de tu sincronizaci\u00f3n.";
            }

            participantToGroupLeader.put(
                    followerId,
                    leaderId
            );

        }

        follower.whisper(
                "Ahora est\u00e1s sincronizado con "
                        + leader
                                .getHabboInfo()
                                .getUsername()
                        + "."
        );

        return "A\u00f1adido a la sincronizaci\u00f3n: "
                + follower
                        .getHabboInfo()
                        .getUsername()
                + ". Miembros: "
                + group.size()
                + ".";
    }

    private synchronized String createMode2Group(
            Habbo leader,
            Habbo follower) {

        if (leader == null
                || follower == null) {

            return "No se pudo crear el v\u00ednculo.";
        }

        final int leaderId =
                leader.getHabboInfo().getId();

        final int followerId =
                follower.getHabboInfo().getId();

        // AvatarSync WIRED: no mezclar comandos y sesiones WIRED.
        cleanupStaleWiredBinding(leaderId);
        cleanupStaleWiredBinding(followerId);

        if (participantToWiredItemId.containsKey(leaderId)
                || participantToWiredItemId.containsKey(followerId)) {

            return "No puedes mezclar una sincronizacion WIRED con comandos.";
        }
        if (leaderId == followerId) {

            return "No puedes sincronizarte contigo mismo.";
        }

        final Room room =
                leader.getHabboInfo()
                        .getCurrentRoom();

        if (room == null
                || follower.getHabboInfo()
                        .getCurrentRoom() != room) {

            return "Los dos avatares deben estar en la misma sala.";
        }

        SyncGroup group =
                groupsByLeader.get(
                        leaderId
                );

        final Integer leaderGroupId =
                participantToGroupLeader.get(
                        leaderId
                );

        /*
         * El l\u00edder ya pertenece a otro grupo como follower.
         */
        if (leaderGroupId != null
                && leaderGroupId.intValue() != leaderId) {

            return "Ya formas parte de otra sincronizaci\u00f3n. Usa :unsync primero.";
        }

        /*
         * Reparar \u00edndice hu\u00e9rfano.
         */
        if (leaderGroupId != null
                && group == null) {

            participantToGroupLeader.remove(
                    leaderId,
                    leaderGroupId
            );
        }

        /*
         * Grupo existente:
         * debe ser MODE 2 y estar en la misma sala.
         */
        if (group != null) {

            if (group.roomId != room.getId()) {

                unlinkParticipant(
                        leaderId,
                        false
                );

                group = null;
            }

            else if (group.mode
                    != SYNC_MODE_LEADER_LOCKED) {

                return "Tu sincronizaci\u00f3n actual no est\u00e1 en modo 2.";
            }
        }

        /*
         * El nuevo follower no puede pertenecer
         * simult\u00e1neamente a otro grupo.
         */
        final Integer followerGroupLeader =
                participantToGroupLeader.get(
                        followerId
                );

        if (followerGroupLeader != null) {

            final SyncGroup followerGroup =
                    groupsByLeader.get(
                            followerGroupLeader
                    );

            /*
             * Reparar \u00edndice hu\u00e9rfano.
             */
            if (followerGroup == null) {

                participantToGroupLeader.remove(
                        followerId,
                        followerGroupLeader
                );
            }

            else {

                if (group != null
                        && followerGroupLeader.intValue() == leaderId
                        && group.contains(
                                followerId
                        )) {

                    return follower
                            .getHabboInfo()
                            .getUsername()
                            + " ya forma parte de tu sincronizaci\u00f3n.";
                }

                return follower
                        .getHabboInfo()
                        .getUsername()
                        + " ya est\u00e1 en otra sincronizaci\u00f3n.";
            }
        }

        /*
         * Primer follower:
         * crear grupo.
         */
        if (group == null) {

            group =
                    new SyncGroup(
                            room.getId(),
                            leaderId,
                            followerId,
                            SYNC_MODE_LEADER_LOCKED
                    );

            groupsByLeader.put(
                    leaderId,
                    group
            );

            participantToGroupLeader.put(
                    leaderId,
                    leaderId
            );

            participantToGroupLeader.put(
                    followerId,
                    leaderId
            );

        }

        /*
         * Grupo existente:
         * a\u00f1adir follower.
         */
        else {

            if (!group.addMember(
                    followerId
            )) {

                return follower
                        .getHabboInfo()
                        .getUsername()
                        + " ya forma parte de tu sincronizaci\u00f3n.";
            }

            participantToGroupLeader.put(
                    followerId,
                    leaderId
            );

        }

        if (follower.getRoomUnit() != null
                && follower.getRoomUnit().isWalking()) {

            follower.getRoomUnit().stopWalking();
        }
        follower.whisper(
                "Ahora est\u00e1s sincronizado con "
                        + leader
                                .getHabboInfo()
                                .getUsername()
                        + "."
        );

        return "A\u00f1adido a la sincronizaci\u00f3n: "
                + follower
                        .getHabboInfo()
                        .getUsername()
                + ". Miembros: "
                + group.size()
                + ".";
    }

    /*
     * ============================================================
     * ELIMINAR LINK
     * ============================================================
     */
    private synchronized boolean unlinkParticipant(
            int participantId,
            boolean notifyOther) {

        replicatedStepGuards.remove(
                participantId
        );

        final Integer groupLeaderId =
                participantToGroupLeader.get(
                        participantId
                );

        if (groupLeaderId == null) {
            return false;
        }

        final SyncGroup group =
                groupsByLeader.get(
                        groupLeaderId
                );

        if (group == null) {

            participantToGroupLeader.remove(
                    participantId
            );

            return false;
        }

        /*
         * ========================================================
         * MODE 3 - TODOS SON PEERS
         * ========================================================
         */
        if (group.mode == SYNC_MODE_ALL_LEADERS) {

            if (!group.contains(participantId)) {

                participantToGroupLeader.remove(
                        participantId,
                        groupLeaderId
                );

                return false;
            }

            final int oldAnchor =
                    group.leaderId;

            final Habbo departing =
                    Emulator.getGameEnvironment()
                            .getHabboManager()
                            .getHabbo(
                                    participantId
                            );

            final String departingName =
                    departing != null
                            ? departing
                                    .getHabboInfo()
                                    .getUsername()
                            : "Un usuario";

            final boolean removed =
                    group.removeMember(
                            participantId
                    );

            participantToGroupLeader.remove(
                    participantId,
                    groupLeaderId
            );

            if (!removed) {
                return false;
            }

            /*
             * Con menos de dos miembros ya no existe sync.
             */
            if (group.size() < 2) {

                groupsByLeader.remove(
                        oldAnchor,
                        group
                );

                for (Integer memberId :
                        group.memberIdsSnapshot()) {

                    participantToGroupLeader.remove(
                            memberId,
                            oldAnchor
                    );

                    replicatedStepGuards.remove(
                            memberId
                    );

                    if (!notifyOther
                            || memberId == null) {

                        continue;
                    }

                    final Habbo member =
                            Emulator.getGameEnvironment()
                                    .getHabboManager()
                                    .getHabbo(
                                            memberId
                                    );

                    if (member != null
                            && member.getRoomUnit() != null
                            && member.getRoomUnit().isInRoom()) {

                    }
                }


                return true;
            }

            /*
             * Si sale la clave interna del grupo,
             * promovemos otra clave SIN cambiar comportamiento:
             * todos siguen siendo peers.
             */
            if (participantId == oldAnchor) {

                final int newAnchor =
                        group.firstMemberId();

                if (newAnchor < 0) {

                    groupsByLeader.remove(
                            oldAnchor,
                            group
                    );

                    return true;
                }

                groupsByLeader.remove(
                        oldAnchor,
                        group
                );

                group.leaderId =
                        newAnchor;

                groupsByLeader.put(
                        newAnchor,
                        group
                );

                for (Integer memberId :
                        group.memberIdsSnapshot()) {

                    participantToGroupLeader.put(
                            memberId,
                            newAnchor
                    );
                }

            }

            if (notifyOther) {

                for (Integer memberId :
                        group.memberIdsSnapshot()) {

                    if (memberId == null) {
                        continue;
                    }

                    final Habbo member =
                            Emulator.getGameEnvironment()
                                    .getHabboManager()
                                    .getHabbo(
                                            memberId
                                    );

                    if (member != null
                            && member.getRoomUnit() != null
                            && member.getRoomUnit().isInRoom()) {

                        member.whisper(
                                departingName
                                        + " ha salido de sync3."
                        );
                    }
                }
            }


            return true;
        }

        /*
         * ========================================================
         * MODE 1 / MODE 2 - SALE EL L\u00cd\u008dDER
         * ========================================================
         */
        if (participantId == group.leaderId) {

            groupsByLeader.remove(
                    group.leaderId,
                    group
            );

            for (Integer memberId :
                    group.memberIdsSnapshot()) {

                participantToGroupLeader.remove(
                        memberId,
                        group.leaderId
                );

                replicatedStepGuards.remove(
                        memberId
                );

                if (!notifyOther
                        || memberId == null
                        || memberId.intValue() == participantId) {

                    continue;
                }

                final Habbo member =
                        Emulator.getGameEnvironment()
                                .getHabboManager()
                                .getHabbo(
                                        memberId
                                );

                if (member != null
                        && member.getRoomUnit() != null
                        && member.getRoomUnit().isInRoom()) {

                }
            }


            return true;
        }

        /*
         * ========================================================
         * MODE 1 / MODE 2 - SALE FOLLOWER
         * ========================================================
         */
        final Habbo departing =
                Emulator.getGameEnvironment()
                        .getHabboManager()
                        .getHabbo(
                                participantId
                        );

        final String departingName =
                departing != null
                        ? departing
                                .getHabboInfo()
                                .getUsername()
                        : "Un usuario";

        final boolean removed =
                group.removeMember(
                        participantId
                );

        participantToGroupLeader.remove(
                participantId,
                group.leaderId
        );

        if (!removed) {
            return false;
        }

        final Habbo leader =
                Emulator.getGameEnvironment()
                        .getHabboManager()
                        .getHabbo(
                                group.leaderId
                        );

        if (group.size() < 2) {

            groupsByLeader.remove(
                    group.leaderId,
                    group
            );

            participantToGroupLeader.remove(
                    group.leaderId,
                    group.leaderId
            );

            replicatedStepGuards.remove(
                    group.leaderId
            );

            if (notifyOther
                    && leader != null
                    && leader.getRoomUnit() != null
                    && leader.getRoomUnit().isInRoom()) {

                leader.whisper(
                        departingName
                                + " ha salido. La sincronizaci\u00f3n ha terminado."
                );
            }


            return true;
        }

        if (notifyOther
                && leader != null
                && leader.getRoomUnit() != null
                && leader.getRoomUnit().isInRoom()) {

            leader.whisper(
                    departingName
                            + " ha salido de la sincronizaci\u00f3n."
            );
        }


        return true;
    }

    /*
     * ============================================================
     * SYNC GROUP DATA
     * ============================================================
     */
    /*
     * ============================================================
     * AVATARSYNC WIRED - SESSION LAYER
     * ============================================================
     *
     * Cada furni WIRED mantiene su propia sesion.
     *
     * Primer causante:
     * - queda esperando.
     * - todavia NO existe SyncGroup.
     * - el reloj NO empieza.
     *
     * Segundo causante:
     * - crea el SyncGroup real.
     * - empieza el reloj si la duracion es temporal.
     *
     * Terceros y posteriores:
     * - entran en el mismo SyncGroup.
     * - heredan el tiempo restante.
     *
     * durationSeconds == 0:
     * - ilimitado.
     *
     * El motor de replicacion sigue siendo SyncGroup.
     */

    synchronized boolean activateWiredAvatarSync(
            int wiredItemId,
            RoomUnit roomUnit,
            Room room,
            int requestedMode,
            int requestedDurationSeconds) {

        if (wiredItemId <= 0
                || roomUnit == null
                || room == null) {

            return false;
        }

        if (requestedMode < SYNC_MODE_LEADER_FREE
                || requestedMode > SYNC_MODE_ALL_LEADERS) {

            return false;
        }

        final int durationSeconds =
                Math.max(
                        0,
                        Math.min(
                                3600,
                                requestedDurationSeconds
                        )
                );

        final Habbo actor =
                room.getHabbo(roomUnit);

        if (actor == null
                || actor.getHabboInfo()
                        .getCurrentRoom() != room) {

            return false;
        }

        final int actorId =
                actor.getHabboInfo().getId();

        /*
         * Reparar binding WIRED antiguo si existiera.
         */
        cleanupStaleWiredBinding(
                actorId
        );

        WiredSyncSession session =
                wiredSessionsByItemId.get(
                        wiredItemId
                );

        /*
         * Mismo item pero sesion antigua de otra sala:
         * eliminarla.
         */
        if (session != null
                && session.roomId != room.getId()) {

            stopWiredAvatarSync(
                    wiredItemId
            );

            session = null;
        }

        /*
         * Expiracion defensiva.
         */
        if (session != null
                && session.expiresAt > 0L
                && session.expiresAt
                        <= System.currentTimeMillis()) {

            expireWiredSession(
                    wiredItemId,
                    session
            );

            session = null;
        }

        /*
         * El grupo pudo desaparecer por lifecycle,
         * :unsync u otra limpieza.
         */
        if (session != null
                && session.group != null
                && !isRegisteredSyncGroup(
                        session.group
                )) {

            wiredSessionsByItemId.remove(
                    wiredItemId,
                    session
            );

            removeWiredSessionMappings(
                    wiredItemId
            );

            session = null;
        }

        /*
         * Si ya pertenece al mismo grupo WIRED,
         * ejecutar de nuevo el efecto no duplica nada.
         */
        if (session != null
                && session.group != null
                && session.group.contains(
                        actorId
                )) {

            participantToWiredItemId.put(
                    actorId,
                    wiredItemId
            );

            return true;
        }

        /*
         * No mezclar grupos creados con comandos
         * con sesiones WIRED.
         */
        if (participantToGroupLeader
                .containsKey(actorId)) {

            return false;
        }

        /*
         * No puede pertenecer simultaneamente
         * a otro furni Avatar Sync.
         */
        final Integer existingWiredItemId =
                participantToWiredItemId.get(
                        actorId
                );

        if (existingWiredItemId != null
                && existingWiredItemId.intValue()
                        != wiredItemId) {

            return false;
        }

        /*
         * ====================================================
         * PRIMER CAUSANTE
         * ====================================================
         *
         * Se crea solamente la sesion.
         * No hay SyncGroup.
         * No hay temporizador.
         */
        if (session == null) {

            session =
                    new WiredSyncSession(
                            wiredItemId,
                            room.getId(),
                            requestedMode,
                            durationSeconds,
                            actorId
                    );

            wiredSessionsByItemId.put(
                    wiredItemId,
                    session
            );

            participantToWiredItemId.put(
                    actorId,
                    wiredItemId
            );


            return true;
        }

        /*
         * Si el WIRED fue reconfigurado mientras solo
         * habia un participante esperando, aceptar la
         * configuracion mas reciente.
         */
        if (session.group == null
                && session.firstParticipantId == actorId
                && (session.mode
                        != requestedMode
                || session.durationSeconds
                        != durationSeconds)) {

            final WiredSyncSession updated =
                    new WiredSyncSession(
                            wiredItemId,
                            room.getId(),
                            requestedMode,
                            durationSeconds,
                            session.firstParticipantId
                    );

            wiredSessionsByItemId.put(
                    wiredItemId,
                    updated
            );

            session = updated;
        }

        /*
         * ====================================================
         * SEGUNDO CAUSANTE
         * ====================================================
         *
         * Aqui nace el SyncGroup real.
         */
        if (session.group == null) {

            if (session.firstParticipantId
                    == actorId) {

                return true;
            }

            final int firstParticipantId =
                    session.firstParticipantId;

            final Habbo firstParticipant =
                    room.getHabbo(
                            firstParticipantId
                    );

            final Integer firstWiredBinding =
                    participantToWiredItemId.get(
                            firstParticipantId
                    );

            /*
             * Si el primer causante ya no es valido,
             * el usuario actual pasa a ser el nuevo primero.
             */
            if (firstParticipant == null
                    || firstParticipant
                            .getHabboInfo()
                            .getCurrentRoom() != room
                    || participantToGroupLeader
                            .containsKey(
                                    firstParticipantId
                            )
                    || firstWiredBinding == null
                    || firstWiredBinding.intValue()
                            != wiredItemId) {

                participantToWiredItemId.remove(
                        firstParticipantId,
                        wiredItemId
                );

                final WiredSyncSession replacement =
                        new WiredSyncSession(
                                wiredItemId,
                                room.getId(),
                                requestedMode,
                                durationSeconds,
                                actorId
                        );

                wiredSessionsByItemId.put(
                        wiredItemId,
                        replacement
                );

                participantToWiredItemId.put(
                        actorId,
                        wiredItemId
                );

                return true;
            }

            final SyncGroup group =
                    new SyncGroup(
                            room.getId(),
                            firstParticipantId,
                            actorId,
                            session.mode
                    );

            /*
             * Registrar el grupo sin sobrescribir
             * accidentalmente otro grupo existente.
             */
            if (groupsByLeader.putIfAbsent(
                    firstParticipantId,
                    group
            ) != null) {

                return false;
            }

            if (participantToGroupLeader
                    .putIfAbsent(
                            firstParticipantId,
                            firstParticipantId
                    ) != null) {

                groupsByLeader.remove(
                        firstParticipantId,
                        group
                );

                return false;
            }

            if (participantToGroupLeader
                    .putIfAbsent(
                            actorId,
                            firstParticipantId
                    ) != null) {

                participantToGroupLeader.remove(
                        firstParticipantId,
                        firstParticipantId
                );

                groupsByLeader.remove(
                        firstParticipantId,
                        group
                );

                return false;
            }

            session.group =
                    group;

            participantToWiredItemId.put(
                    actorId,
                    wiredItemId
            );

            /*
             * MODE 2:
             * el segundo usuario es follower.
             */
            if (session.mode
                    == SYNC_MODE_LEADER_LOCKED) {

                roomUnit.stopWalking();
            }

            /*
             * El temporizador empieza AHORA,
             * cuando ya existen dos usuarios.
             */
            startWiredSessionTimer(
                    session
            );


            return true;
        }

        /*
         * ====================================================
         * TERCER CAUSANTE Y POSTERIORES
         * ====================================================
         */

        final SyncGroup group =
                session.group;

        if (!isRegisteredSyncGroup(
                group
        )) {

            return false;
        }

        if (group.roomId
                != room.getId()) {

            return false;
        }

        if (!group.addMember(
                actorId
        )) {

            return group.contains(
                    actorId
            );
        }

        final Integer previousGroup =
                participantToGroupLeader
                        .putIfAbsent(
                                actorId,
                                group.leaderId
                        );

        if (previousGroup != null) {

            group.removeMember(
                    actorId
            );

            return false;
        }

        participantToWiredItemId.put(
                actorId,
                wiredItemId
        );

        /*
         * MODE 2:
         * cualquier miembro nuevo es follower.
         */
        if (group.mode
                == SYNC_MODE_LEADER_LOCKED) {

            roomUnit.stopWalking();
        }


        return true;
    }

    /*
     * ========================================================
     * REPARAR BINDING WIRED
     * ========================================================
     */
    private void cleanupStaleWiredBinding(
            int participantId) {

        final Integer wiredItemId =
                participantToWiredItemId.get(
                        participantId
                );

        if (wiredItemId == null) {
            return;
        }

        final WiredSyncSession session =
                wiredSessionsByItemId.get(
                        wiredItemId
                );

        if (session == null) {

            participantToWiredItemId.remove(
                    participantId,
                    wiredItemId
            );

            return;
        }

        if (session.expiresAt > 0L
                && session.expiresAt
                        <= System.currentTimeMillis()) {

            expireWiredSession(
                    wiredItemId,
                    session
            );

            return;
        }

        /*
         * Usuario esperando como primer causante.
         */
        if (session.group == null) {

            if (session.firstParticipantId
                    != participantId) {

                participantToWiredItemId.remove(
                        participantId,
                        wiredItemId
                );
            }

            return;
        }

        /*
         * El grupo ya fue destruido.
         */
        if (!isRegisteredSyncGroup(
                session.group
        )) {

            wiredSessionsByItemId.remove(
                    wiredItemId,
                    session
            );

            removeWiredSessionMappings(
                    wiredItemId
            );

            return;
        }

        /*
         * El binding existe pero el usuario ya
         * no es miembro real del SyncGroup.
         */
        if (!session.group.contains(
                participantId
        )) {

            participantToWiredItemId.remove(
                    participantId,
                    wiredItemId
            );
        }
    }

    /*
     * ========================================================
     * LIFECYCLE WIRED
     * ========================================================
     *
     * Se llama DESPUES de unlinkParticipant().
     */
    private synchronized void cleanupWiredParticipant(
            int participantId) {

        final Integer wiredItemId =
                participantToWiredItemId.remove(
                        participantId
                );

        if (wiredItemId == null) {
            return;
        }

        final WiredSyncSession session =
                wiredSessionsByItemId.get(
                        wiredItemId
                );

        if (session == null) {
            return;
        }

        /*
         * Primer causante esperando que sale.
         */
        if (session.group == null) {

            if (session.firstParticipantId
                    == participantId) {

                wiredSessionsByItemId.remove(
                        wiredItemId,
                        session
                );
            }

            return;
        }

        /*
         * Si unlinkParticipant destruyo el grupo,
         * destruir tambien la sesion WIRED.
         *
         * Si MODE 3 hizo rekey y siguen >= 2 miembros,
         * el mismo objeto SyncGroup continua registrado.
         */
        if (!isRegisteredSyncGroup(
                session.group
        )) {

            wiredSessionsByItemId.remove(
                    wiredItemId,
                    session
            );

            removeWiredSessionMappings(
                    wiredItemId
            );
        }
    }

    /*
     * ========================================================
     * STOP WIRED
     * ========================================================
     *
     * Lo usa:
     * - recoger el furni.
     * - futuro WIRED Stop Avatar Sync.
     */
    /*
     * ========================================================
     * STOP AVATAR SYNC POR CAUSANTE
     * ========================================================
     *
     * Solo trabaja con sesiones creadas por WIRED.
     *
     * Los grupos creados mediante comandos:
     * :sync1 / :sync2 / :sync3
     * no usan participantToWiredItemId y no se ven afectados.
     */
    synchronized boolean stopWiredAvatarSyncForParticipant(
            RoomUnit roomUnit,
            Room room) {

        if (roomUnit == null
                || room == null) {

            return false;
        }

        final Habbo actor =
                room.getHabbo(
                        roomUnit
                );

        if (actor == null
                || actor.getHabboInfo()
                        .getCurrentRoom() != room) {

            return false;
        }

        final int actorId =
                actor.getHabboInfo()
                        .getId();

        /*
         * Limpiar primero cualquier binding WIRED obsoleto.
         */
        cleanupStaleWiredBinding(
                actorId
        );

        final Integer wiredItemId =
                participantToWiredItemId.get(
                        actorId
                );

        /*
         * No pertenece a Avatar Sync WIRED.
         *
         * Puede estar:
         * - sin sincronizacion.
         * - en una sincronizacion creada por comando.
         *
         * En ambos casos Stop Avatar Sync no hace nada.
         */
        if (wiredItemId == null) {

            return false;
        }

        final WiredSyncSession session =
                wiredSessionsByItemId.get(
                        wiredItemId
                );

        if (session == null) {

            participantToWiredItemId.remove(
                    actorId,
                    wiredItemId
            );

            return false;
        }

        /*
         * Nunca permitir que un causante de otra sala
         * termine una sesion antigua.
         */
        if (session.roomId
                != room.getId()) {

            participantToWiredItemId.remove(
                    actorId,
                    wiredItemId
            );

            return false;
        }

        /*
         * Terminar la sesion COMPLETA.
         *
         * Esto elimina mappings y disuelve
         * todo el SyncGroup asociado al WIRED.
         */
        stopWiredAvatarSync(
                wiredItemId
        );


        return true;
    }

    synchronized void stopWiredAvatarSync(
            int wiredItemId) {

        final WiredSyncSession session =
                wiredSessionsByItemId.remove(
                        wiredItemId
                );

        if (session == null) {
            return;
        }

        removeWiredSessionMappings(
                wiredItemId
        );

        dissolveWiredGroup(
                session.group
        );

    }

    /*
     * ========================================================
     * COMPROBAR REGISTRO REAL DEL GRUPO
     * ========================================================
     */
    private boolean isRegisteredSyncGroup(
            SyncGroup group) {

        if (group == null) {
            return false;
        }

        return groupsByLeader.get(
                group.leaderId
        ) == group;
    }

    /*
     * ========================================================
     * TEMPORIZADOR
     * ========================================================
     */
    private void startWiredSessionTimer(
            final WiredSyncSession session) {

        if (session == null
                || session.durationSeconds <= 0) {

            return;
        }

        final long delay =
                session.durationSeconds
                        * 1000L;

        session.expiresAt =
                System.currentTimeMillis()
                        + delay;

        Emulator.getThreading().run(
                () -> expireWiredSession(
                        session.itemId,
                        session
                ),
                delay
        );
    }

    /*
     * ========================================================
     * EXPIRAR SESION
     * ========================================================
     */
    private synchronized void expireWiredSession(
            int wiredItemId,
            WiredSyncSession expectedSession) {

        if (expectedSession == null) {
            return;
        }

        if (!wiredSessionsByItemId.remove(
                wiredItemId,
                expectedSession
        )) {

            /*
             * Una sesion nueva puede haber reutilizado
             * posteriormente el mismo furni.
             *
             * Un timer viejo nunca puede destruirla.
             */
            return;
        }

        removeWiredSessionMappings(
                wiredItemId
        );

        dissolveWiredGroup(
                expectedSession.group
        );

    }

    /*
     * ========================================================
     * LIMPIAR INDICES WIRED
     * ========================================================
     */
    private void removeWiredSessionMappings(
            int wiredItemId) {

        for (Integer participantId :
                participantToWiredItemId.keySet()) {

            if (participantId == null) {
                continue;
            }

            final Integer boundItemId =
                    participantToWiredItemId.get(
                            participantId
                    );

            if (boundItemId != null
                    && boundItemId.intValue()
                            == wiredItemId) {

                participantToWiredItemId.remove(
                        participantId,
                        boundItemId
                );
            }
        }
    }

    /*
     * ========================================================
     * DESTRUIR SyncGroup WIRED
     * ========================================================
     *
     * No usa la semantica individual de :unsync.
     * El grupo completo se termina.
     */
    private void dissolveWiredGroup(
            SyncGroup group) {

        if (group == null) {
            return;
        }

        final int anchor =
                group.leaderId;

        if (!groupsByLeader.remove(
                anchor,
                group
        )) {

            return;
        }

        for (Integer memberId :
                group.memberIdsSnapshot()) {

            if (memberId == null) {
                continue;
            }

            participantToGroupLeader.remove(
                    memberId,
                    anchor
            );

            replicatedStepGuards.remove(
                    memberId
            );
        }
    }

    /*
     * ========================================================
     * DATOS DE SESION WIRED
     * ========================================================
     */
    private static final class WiredSyncSession {

        private final int itemId;
        private final int roomId;
        private final int mode;
        private final int durationSeconds;
        private final int firstParticipantId;

        private volatile SyncGroup group;
        private volatile long expiresAt;

        private WiredSyncSession(
                int itemId,
                int roomId,
                int mode,
                int durationSeconds,
                int firstParticipantId) {

            this.itemId =
                    itemId;

            this.roomId =
                    roomId;

            this.mode =
                    mode;

            this.durationSeconds =
                    durationSeconds;

            this.firstParticipantId =
                    firstParticipantId;

            this.group =
                    null;

            this.expiresAt =
                    0L;
        }
    }
    private static final class SyncGroup {

        private final int roomId;

        /*
         * L\u00edder principal.
         *
         * MODE 1 / MODE 2:
         *   es quien origina las acciones.
         *
         * MODE 3:
         *   este campo seguir\u00e1 existiendo como identificador
         *   principal del grupo, aunque todos puedan originar.
         */
        private volatile int leaderId;

        /*
         * Compatibilidad temporal de Fase 1.
         *
         * Todas las rutas actuales todav\u00eda esperan exactamente
         * un follower. En Fase 2 esas rutas pasar\u00e1n a recorrer
         * memberIds y este campo dejar\u00e1 de ser necesario.
         */
        private final int followerId;

        private final int mode;

        /*
         * Miembros reales del grupo.
         *
         * Ya queda preparado para:
         * - varios followers
         * - MODE 3
         * - causantes WIRED
         * - selectors futuros
         */
        private final java.util.Set<Integer> memberIds =
                java.util.concurrent.ConcurrentHashMap.newKeySet();

        /*
         * Constructor de compatibilidad.
         *
         * Todo el c\u00f3digo antiguo que creaba una pareja
         * autom\u00e1ticamente crea ahora un grupo MODE 1.
         */
        private SyncGroup(
                int roomId,
                int leaderId,
                int followerId) {

            this(
                    roomId,
                    leaderId,
                    followerId,
                    SYNC_MODE_LEADER_FREE
            );
        }

        private SyncGroup(
                int roomId,
                int leaderId,
                int followerId,
                int mode) {

            this.roomId = roomId;
            this.leaderId = leaderId;
            this.followerId = followerId;
            this.mode = mode;

            this.memberIds.add(
                    leaderId
            );

            this.memberIds.add(
                    followerId
            );
        }

        private boolean contains(
                int participantId) {

            return this.memberIds.contains(
                    participantId
            );
        }

        /*
         * AvatarSync Phase 2: multi-follower.
         */

        private boolean addMember(
                int participantId) {

            return this.memberIds.add(
                    participantId
            );
        }

        private boolean removeMember(
                int participantId) {

            return this.memberIds.remove(
                    participantId
            );
        }

        private java.util.List<Integer> memberIdsSnapshot() {

            return new java.util.ArrayList<Integer>(
                    this.memberIds
            );
        }

        private java.util.List<Integer> followerIdsSnapshot() {

            final java.util.List<Integer> followers =
                    new java.util.ArrayList<Integer>();

            for (Integer participantId : this.memberIds) {

                if (participantId == null
                        || participantId.intValue() == this.leaderId) {

                    continue;
                }

                followers.add(
                        participantId
                );
            }

            return followers;
        }

        private java.util.List<Integer> replicationTargetIdsSnapshot(
                int actorId) {

            final java.util.List<Integer> targets =
                    new java.util.ArrayList<Integer>();

            /*
             * MODE 3:
             * todos menos el actor que origin\u00f3 la acci\u00f3n.
             */
            if (this.mode == SYNC_MODE_ALL_LEADERS) {

                for (Integer memberId : this.memberIds) {

                    if (memberId == null
                            || memberId.intValue() == actorId) {

                        continue;
                    }

                    targets.add(
                            memberId
                    );
                }

                return targets;
            }

            /*
             * MODE 1 / MODE 2:
             * solo el l\u00edder puede originar,
             * y los targets son sus followers.
             */
            if (actorId != this.leaderId) {
                return targets;
            }

            return followerIdsSnapshot();
        }

        private int firstMemberId() {

            for (Integer memberId : this.memberIds) {

                if (memberId != null) {
                    return memberId.intValue();
                }
            }

            return -1;
        }

        private int size() {
            return this.memberIds.size();
        }
    }

    /*
     * ============================================================
     * :SYNC1
     * ============================================================
     */
    /*
     * AvatarSync admin command gate.
     *
     * :sync1 / :sync2 / :sync3 / :unsync
     * requieren permiso administrativo cmd_shutdown.
     *
     * Estos comandos son herramientas manuales de administraci\u00f3n,
     * NO funcionalidad p\u00fablica para usuarios.
     */
    private static final class Sync1Command
            extends Command {

        private final AvatarSyncPlugin plugin;

        private Sync1Command(
                AvatarSyncPlugin plugin) {

            super("cmd_shutdown",
                    new String[] {
                            "sync1",
                            "sync",
                            "sincronizar"
                    });

            this.plugin = plugin;
        }

        @Override
        public boolean handle(
                GameClient gameClient,
                String[] params) throws Exception {

            final Habbo leader = gameClient.getHabbo();

            final Room room = leader
                    .getHabboInfo()
                    .getCurrentRoom();

            if (room == null) {
                return true;
            }

            if (params.length < 2) {

                leader.whisper(
                        "Uso: :sync1 <usuario>");

                return true;
            }

            final Habbo follower = room.getHabbo(
                    params[1]);

            if (follower == null) {

                leader.whisper(
                        "Ese usuario no est\u00e1 en la sala.");

                return true;
            }

            leader.whisper(
                    plugin.createMode1Group(
                            leader,
                            follower));

            return true;
        }
    }

    private static final class Sync2Command
            extends Command {

        private final AvatarSyncPlugin plugin;

        private Sync2Command(
                AvatarSyncPlugin plugin) {

            super("cmd_shutdown",
                    new String[] {
                            "sync2"
                    });

            this.plugin = plugin;
        }

        @Override
        public boolean handle(
                GameClient gameClient,
                String[] params) throws Exception {

            final Habbo leader = gameClient.getHabbo();

            final Room room = leader
                    .getHabboInfo()
                    .getCurrentRoom();

            if (room == null) {
                return true;
            }

            if (params.length < 2) {

                leader.whisper(
                        "Uso: :sync2 <usuario>");

                return true;
            }

            final Habbo follower = room.getHabbo(
                    params[1]);

            if (follower == null) {

                leader.whisper(
                        "Ese usuario no est\u00e1 en la sala.");

                return true;
            }

            leader.whisper(
                    plugin.createMode2Group(
                            leader,
                            follower));

            return true;
        }
    }

    /*
     * ============================================================
     * :UNSYNC
     * ============================================================
     */
    /*
     * ============================================================
     * :SYNC3
     * ============================================================
     */
    private static final class Sync3Command
            extends Command {

        private final AvatarSyncPlugin plugin;

        private Sync3Command(
                AvatarSyncPlugin plugin) {

            super("cmd_shutdown",
                    new String[] {
                            "sync3"
                    });

            this.plugin = plugin;
        }

        @Override
        public boolean handle(
                GameClient gameClient,
                String[] params) throws Exception {

            final Habbo actor =
                    gameClient.getHabbo();

            final Room room =
                    actor
                            .getHabboInfo()
                            .getCurrentRoom();

            if (room == null) {
                return true;
            }

            if (params.length < 2) {

                actor.whisper(
                        "Uso: :sync3 <usuario>"
                );

                return true;
            }

            final Habbo peer =
                    room.getHabbo(
                            params[1]
                    );

            if (peer == null) {

                actor.whisper(
                        "Ese usuario no est\u00e1 en la sala."
                );

                return true;
            }

            actor.whisper(
                    plugin.createMode3Group(
                            actor,
                            peer
                    )
            );

            return true;
        }
    }

    private static final class UnsyncCommand
            extends Command {

        private final AvatarSyncPlugin plugin;

        private UnsyncCommand(
                AvatarSyncPlugin plugin) {

            super("cmd_shutdown",
                    new String[] {
                            "unsync",
                            "desincronizar"
                    });

            this.plugin = plugin;
        }

        @Override
        public boolean handle(
                GameClient gameClient,
                String[] params) throws Exception {

            final Habbo habbo = gameClient.getHabbo();

            final boolean removed = plugin.unlinkParticipant(
                    habbo
                            .getHabboInfo()
                            .getId(),
                    true);

            habbo.whisper(
                    removed
                            ? "Sincronizaci\u00f3n terminada."
                            : "No est\u00e1s en ninguna sincronizaci\u00f3n.");

            return true;
        }
    }
}
