/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.bots.VisitorBot;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionBackgroundToner;
import com.eu.habbo.habbohotel.items.interactions.InteractionBlackHole;
import com.eu.habbo.habbohotel.items.interactions.InteractionBuildArea;
import com.eu.habbo.habbohotel.items.interactions.InteractionFireworks;
import com.eu.habbo.habbohotel.items.interactions.InteractionGate;
import com.eu.habbo.habbohotel.items.interactions.InteractionGuildFurni;
import com.eu.habbo.habbohotel.items.interactions.InteractionJukeBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionMoodLight;
import com.eu.habbo.habbohotel.items.interactions.InteractionMultiHeight;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.items.interactions.InteractionMuteArea;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.items.interactions.InteractionPyramid;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.InteractionSnowboardSlope;
import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.items.interactions.InteractionStickyPole;
import com.eu.habbo.habbohotel.items.interactions.InteractionTalkingFurniture;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleportTile;
import com.eu.habbo.habbohotel.items.interactions.InteractionTent;
import com.eu.habbo.habbohotel.items.interactions.InteractionTileWalkMagic;
import com.eu.habbo.habbohotel.items.interactions.InteractionWater;
import com.eu.habbo.habbohotel.items.interactions.InteractionWaterItem;
import com.eu.habbo.habbohotel.items.interactions.InteractionWired;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredExtra;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredHighscore;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagPole;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionNest;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetBreedingNest;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetDrink;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetFood;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredBlob;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.RoomBan;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomMoodlightData;
import com.eu.habbo.habbohotel.rooms.RoomPromotion;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.habbohotel.rooms.RoomSpecialTypes;
import com.eu.habbo.habbohotel.rooms.RoomState;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.rooms.RoomTradeUser;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.rooms.TraxManager;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericErrorMessagesComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildInfoComposer;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.AddPetComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.SimplePollAnswerComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.SimplePollStartComposer;
import com.eu.habbo.messages.outgoing.rooms.HideDoorbellComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomAccessDeniedComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomAddRightsListComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomOwnerComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomRemoveRightsListComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomRightsComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomRightsListComposer;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.messages.outgoing.rooms.items.AddFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.AddWallItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.items.ItemStateComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveWallItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RoomFloorItemsComposer;
import com.eu.habbo.messages.outgoing.rooms.items.WallItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitIdleComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDanceComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserHandItemComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserIgnoredComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserNameChangedComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserShoutComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTypingComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserUnbannedComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.MutedWhisperComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureBuildheightEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureMovedEvent;
import com.eu.habbo.plugin.events.furniture.FurniturePickedUpEvent;
import com.eu.habbo.plugin.events.furniture.FurniturePlacedEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureRolledEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureRotatedEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureStackHeightEvent;
import com.eu.habbo.plugin.events.rooms.RoomLoadedEvent;
import com.eu.habbo.plugin.events.rooms.RoomUnloadedEvent;
import com.eu.habbo.plugin.events.rooms.RoomUnloadingEvent;
import com.eu.habbo.plugin.events.users.UserExitRoomEvent;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import com.eu.habbo.plugin.events.users.UserRightsTakenEvent;
import com.eu.habbo.plugin.events.users.UserRolledEvent;
import com.eu.habbo.plugin.events.users.UsernameTalkEvent;
import com.eu.habbo.threading.runnables.YouAreAPirate;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;
import io.netty.util.internal.ConcurrentSet;
import java.awt.Color;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Room
implements Comparable<Room>,
ISerialize,
Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Room.class);
    public static final Comparator SORT_SCORE = (o1, o2) -> {
        if (!(o1 instanceof Room) || !(o2 instanceof Room)) {
            return 0;
        }
        return ((Room)o2).getScore() - ((Room)o1).getScore();
    };
    public static final Comparator SORT_ID = (o1, o2) -> {
        if (!(o1 instanceof Room) || !(o2 instanceof Room)) {
            return 0;
        }
        return ((Room)o2).getId() - ((Room)o1).getId();
    };
    private static final TIntObjectHashMap<RoomMoodlightData> defaultMoodData = new TIntObjectHashMap();
    public static boolean HABBO_CHAT_DELAY = false;
    public static int MAXIMUM_BOTS = 10;
    public static int MAXIMUM_PETS = 10;
    public static int MAXIMUM_FURNI = 2500;
    public static int MAXIMUM_POSTITNOTES = 200;
    public static int HAND_ITEM_TIME = 10;
    public static int IDLE_CYCLES = 240;
    public static int IDLE_CYCLES_KICK = 480;
    public static String PREFIX_FORMAT = "[<font color=\"%color%\">%prefix%</font>] ";
    public static int ROLLERS_MAXIMUM_ROLL_AVATARS = 1;
    public static boolean MUTEAREA_CAN_WHISPER = false;
    public static double MAXIMUM_FURNI_HEIGHT = 40.0;
    public final Object roomUnitLock = new Object();
    public final ConcurrentHashMap<RoomTile, THashSet<HabboItem>> tileCache = new ConcurrentHashMap();
    public final List<Integer> userVotes;
    private final ConcurrentHashMap<Integer, Habbo> currentHabbos = new ConcurrentHashMap(3);
    private final TIntObjectMap<Habbo> habboQueue = TCollections.synchronizedMap(new TIntObjectHashMap(0));
    private final TIntObjectMap<Bot> currentBots = TCollections.synchronizedMap(new TIntObjectHashMap(0));
    private final TIntObjectMap<Pet> currentPets = TCollections.synchronizedMap(new TIntObjectHashMap(0));
    private final THashSet<RoomTrade> activeTrades;
    private final TIntArrayList rights;
    private final TIntIntHashMap mutedHabbos;
    private final TIntObjectHashMap<RoomBan> bannedHabbos;
    private final ConcurrentSet<Game> games;
    private final TIntObjectMap<String> furniOwnerNames;
    private final TIntIntMap furniOwnerCount;
    private final TIntObjectMap<RoomMoodlightData> moodlightData;
    private final THashSet<String> wordFilterWords;
    private final TIntObjectMap<HabboItem> roomItems;
    private final Object loadLock = new Object();
    public volatile boolean preventUnloading = false;
    public volatile boolean preventUncaching = false;
    public ConcurrentSet<ServerMessage> scheduledComposers = new ConcurrentSet();
    public ConcurrentSet<Runnable> scheduledTasks = new ConcurrentSet();
    public String wordQuiz = "";
    public int noVotes = 0;
    public int yesVotes = 0;
    public int wordQuizEnd = 0;
    public ScheduledFuture roomCycleTask;
    private int id;
    private int ownerId;
    private String ownerName;
    private String name;
    private String description;
    private RoomLayout layout;
    private boolean overrideModel;
    private String layoutName;
    private String password;
    private RoomState state;
    private int usersMax;
    private volatile int score;
    private volatile int category;
    private String floorPaint;
    private String wallPaint;
    private String backgroundPaint;
    private int wallSize;
    private int wallHeight;
    private int floorSize;
    private int guild;
    private String tags;
    private volatile boolean publicRoom;
    private volatile boolean staffPromotedRoom;
    private volatile boolean allowPets;
    private volatile boolean allowPetsEat;
    private volatile boolean allowWalkthrough;
    private volatile boolean allowBotsWalk;
    private volatile boolean allowEffects;
    private volatile boolean hideWall;
    private volatile int chatMode;
    private volatile int chatWeight;
    private volatile int chatSpeed;
    private volatile int chatDistance;
    private volatile int chatProtection;
    private volatile int muteOption;
    private volatile int kickOption;
    private volatile int banOption;
    private volatile int pollId;
    private volatile boolean promoted;
    private volatile int tradeMode;
    private volatile boolean moveDiagonally;
    private volatile boolean jukeboxActive;
    private volatile boolean hideWired;
    private RoomPromotion promotion;
    private volatile boolean needsUpdate;
    private volatile boolean loaded;
    private volatile boolean preLoaded;
    private int idleCycles;
    private int idleHostingCycles;
    private volatile int unitCounter;
    private volatile int rollerSpeed;
    private final int muteTime = Emulator.getConfig().getInt("hotel.flood.mute.time", 30);
    private long rollerCycle = System.currentTimeMillis();
    private volatile int lastTimerReset = Emulator.getIntUnixTimestamp();
    private volatile boolean muted;
    private RoomSpecialTypes roomSpecialTypes;
    private TraxManager traxManager;
    private boolean cycleOdd;
    private long cycleTimestamp;
    public Map<String, Long> repeatersLastTick = new HashMap<String, Long>();
    private final Map<RoomUnit, LoopTracker> loopTrackers = new ConcurrentHashMap<RoomUnit, LoopTracker>();

    public Room(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.ownerId = set.getInt("owner_id");
        this.ownerName = set.getString("owner_name");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.password = set.getString("password");
        this.state = RoomState.valueOf(set.getString("state").toUpperCase());
        this.usersMax = set.getInt("users_max");
        this.score = set.getInt("score");
        this.category = set.getInt("category");
        this.floorPaint = set.getString("paper_floor");
        this.wallPaint = set.getString("paper_wall");
        this.backgroundPaint = set.getString("paper_landscape");
        this.wallSize = set.getInt("thickness_wall");
        this.wallHeight = set.getInt("wall_height");
        this.floorSize = set.getInt("thickness_floor");
        this.tags = set.getString("tags");
        this.publicRoom = set.getBoolean("is_public");
        this.staffPromotedRoom = set.getBoolean("is_staff_picked");
        this.allowPets = set.getBoolean("allow_other_pets");
        this.allowPetsEat = set.getBoolean("allow_other_pets_eat");
        this.allowWalkthrough = set.getBoolean("allow_walkthrough");
        this.hideWall = set.getBoolean("allow_hidewall");
        this.chatMode = set.getInt("chat_mode");
        this.chatWeight = set.getInt("chat_weight");
        this.chatSpeed = set.getInt("chat_speed");
        this.chatDistance = set.getInt("chat_hearing_distance");
        this.chatProtection = set.getInt("chat_protection");
        this.muteOption = set.getInt("who_can_mute");
        this.kickOption = set.getInt("who_can_kick");
        this.banOption = set.getInt("who_can_ban");
        this.pollId = set.getInt("poll_id");
        this.guild = set.getInt("guild_id");
        this.rollerSpeed = set.getInt("roller_speed");
        this.overrideModel = set.getString("override_model").equals("1");
        this.layoutName = set.getString("model");
        this.promoted = set.getString("promoted").equals("1");
        this.jukeboxActive = set.getString("jukebox_active").equals("1");
        this.hideWired = set.getString("hidewired").equals("1");
        this.bannedHabbos = new TIntObjectHashMap();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_promotions WHERE room_id = ? AND end_timestamp > ? LIMIT 1");){
            if (this.promoted) {
                statement.setInt(1, this.id);
                statement.setInt(2, Emulator.getIntUnixTimestamp());
                try (ResultSet promotionSet = statement.executeQuery();){
                    this.promoted = false;
                    if (promotionSet.next()) {
                        this.promoted = true;
                        this.promotion = new RoomPromotion(this, promotionSet);
                    }
                }
            }
            this.loadBans(connection);
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        this.tradeMode = set.getInt("trade_mode");
        this.moveDiagonally = set.getString("move_diagonally").equals("1");
        this.preLoaded = true;
        this.allowBotsWalk = true;
        this.allowEffects = true;
        this.furniOwnerNames = TCollections.synchronizedMap(new TIntObjectHashMap(0));
        this.furniOwnerCount = TCollections.synchronizedMap(new TIntIntHashMap(0));
        this.roomItems = TCollections.synchronizedMap(new TIntObjectHashMap(0));
        this.wordFilterWords = new THashSet(0);
        this.moodlightData = new TIntObjectHashMap<RoomMoodlightData>(defaultMoodData);
        for (String s : set.getString("moodlight_data").split(";")) {
            RoomMoodlightData data = RoomMoodlightData.fromString(s);
            this.moodlightData.put(data.getId(), data);
        }
        this.mutedHabbos = new TIntIntHashMap();
        this.games = new ConcurrentSet();
        this.activeTrades = new THashSet(0);
        this.rights = new TIntArrayList();
        this.userVotes = new ArrayList<Integer>();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void loadData() {
        Object object = this.loadLock;
        synchronized (object) {
            if (!this.preLoaded || this.loaded) {
                return;
            }
            this.preLoaded = false;
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();){
                Object object2 = this.roomUnitLock;
                synchronized (object2) {
                    this.unitCounter = 0;
                    this.currentHabbos.clear();
                    this.currentPets.clear();
                    this.currentBots.clear();
                }
                this.roomSpecialTypes = new RoomSpecialTypes();
                try {
                    this.loadLayout();
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
                try {
                    this.loadRights(connection);
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
                try {
                    this.loadItems(connection);
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
                try {
                    this.loadHeightmap();
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
                try {
                    this.loadBots(connection);
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
                try {
                    this.loadPets(connection);
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
                try {
                    this.loadWordFilter(connection);
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
                try {
                    this.loadWiredData(connection);
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
                this.idleCycles = 0;
                this.idleHostingCycles = 0;
                this.loaded = true;
                this.roomCycleTask = Emulator.getThreading().getService().scheduleAtFixedRate(this, 500L, 500L, TimeUnit.MILLISECONDS);
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
            this.traxManager = new TraxManager(this);
            if (this.jukeboxActive) {
                this.traxManager.play(0);
                for (HabboItem item : this.roomSpecialTypes.getItemsOfType(InteractionJukeBox.class)) {
                    item.setExtradata("1");
                    this.updateItem(item);
                }
            }
            for (HabboItem item : this.roomSpecialTypes.getItemsOfType(InteractionFireworks.class)) {
                item.setExtradata("1");
                this.updateItem(item);
            }
        }
        Emulator.getPluginManager().fireEvent(new RoomLoadedEvent(this));
    }

    private synchronized void loadLayout() {
        if (this.layout == null) {
            this.layout = this.overrideModel ? Emulator.getGameEnvironment().getRoomManager().loadCustomLayout(this) : Emulator.getGameEnvironment().getRoomManager().loadLayout(this.layoutName, this);
        }
    }

    private synchronized void loadHeightmap() {
        if (this.layout != null) {
            for (short x = 0; x < this.layout.getMapSizeX(); x = (short)(x + 1)) {
                for (short y = 0; y < this.layout.getMapSizeY(); y = (short)(y + 1)) {
                    RoomTile tile = this.layout.getTile(x, y);
                    if (tile == null) continue;
                    this.updateTile(tile);
                }
            }
        } else {
            LOGGER.error("Unknown Room Layout for Room (ID: {})", (Object)this.id);
        }
    }

    private synchronized void loadItems(Connection connection) {
        this.roomItems.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE room_id = ?");){
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    this.addHabboItem(Emulator.getGameEnvironment().getItemManager().loadHabboItem(set));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        if (this.itemCount() > MAXIMUM_FURNI) {
            LOGGER.error("Room ID: {} has exceeded the furniture limit ({} > {}).", this.getId(), this.itemCount(), MAXIMUM_FURNI);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private synchronized void loadWiredData(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id, wired_data FROM items WHERE room_id = ? AND wired_data<>''");){
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    try {
                        HabboItem item = this.getHabboItem(set.getInt("id"));
                        if (!(item instanceof InteractionWired)) continue;
                        ((InteractionWired)item).loadWiredData(set, this);
                    }
                    catch (SQLException e) {
                        LOGGER.error("Caught SQL exception", e);
                    }
                }
                return;
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return;
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    private synchronized void loadBots(Connection connection) {
        this.currentBots.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username AS owner_name, bots.* FROM bots INNER JOIN users ON bots.user_id = users.id WHERE room_id = ?");){
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    Bot b = Emulator.getGameEnvironment().getBotManager().loadBot(set);
                    if (b == null) continue;
                    b.setRoom(this);
                    b.setRoomUnit(new RoomUnit());
                    b.getRoomUnit().setPathFinderRoom(this);
                    b.getRoomUnit().setLocation(this.layout.getTile((short)set.getInt("x"), (short)set.getInt("y")));
                    if (b.getRoomUnit().getCurrentLocation() == null) {
                        b.getRoomUnit().setLocation(this.getLayout().getDoorTile());
                        b.getRoomUnit().setRotation(RoomUserRotation.fromValue(this.getLayout().getDoorDirection()));
                    } else {
                        b.getRoomUnit().setZ(set.getDouble("z"));
                        b.getRoomUnit().setPreviousLocationZ(set.getDouble("z"));
                        b.getRoomUnit().setRotation(RoomUserRotation.values()[set.getInt("rot")]);
                    }
                    b.getRoomUnit().setRoomUnitType(RoomUnitType.BOT);
                    b.getRoomUnit().setDanceType(DanceType.values()[set.getInt("dance")]);
                    b.getRoomUnit().setInRoom(true);
                    this.giveEffect(b.getRoomUnit(), set.getInt("effect"), Integer.MAX_VALUE);
                    this.addBot(b);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    private synchronized void loadPets(Connection connection) {
        this.currentPets.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username as pet_owner_name, users_pets.* FROM users_pets INNER JOIN users ON users_pets.user_id = users.id WHERE room_id = ?");){
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    try {
                        Pet pet = PetManager.loadPet(set);
                        pet.setRoom(this);
                        pet.setRoomUnit(new RoomUnit());
                        pet.getRoomUnit().setPathFinderRoom(this);
                        pet.getRoomUnit().setLocation(this.layout.getTile((short)set.getInt("x"), (short)set.getInt("y")));
                        if (pet.getRoomUnit().getCurrentLocation() == null) {
                            pet.getRoomUnit().setLocation(this.getLayout().getDoorTile());
                            pet.getRoomUnit().setRotation(RoomUserRotation.fromValue(this.getLayout().getDoorDirection()));
                        } else {
                            pet.getRoomUnit().setZ(set.getDouble("z"));
                            pet.getRoomUnit().setRotation(RoomUserRotation.values()[set.getInt("rot")]);
                        }
                        pet.getRoomUnit().setRoomUnitType(RoomUnitType.PET);
                        pet.getRoomUnit().setCanWalk(true);
                        this.addPet(pet);
                        this.getFurniOwnerNames().put(pet.getUserId(), set.getString("pet_owner_name"));
                    }
                    catch (SQLException e) {
                        LOGGER.error("Caught SQL exception", e);
                    }
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    private synchronized void loadWordFilter(Connection connection) {
        this.wordFilterWords.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_wordfilter WHERE room_id = ?");){
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    this.wordFilterWords.add(set.getString("word"));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void updateTile(RoomTile tile) {
        if (tile != null) {
            tile.setStackHeight(this.getStackHeight(tile.x, tile.y, false));
            tile.setState(this.calculateTileState(tile));
        }
    }

    public void updateTiles(THashSet<RoomTile> tiles) {
        for (RoomTile tile : tiles) {
            this.tileCache.remove(tile);
            tile.setStackHeight(this.getStackHeight(tile.x, tile.y, false));
            tile.setState(this.calculateTileState(tile));
        }
        this.sendComposer(new UpdateStackHeightComposer(this, tiles).compose());
    }

    private RoomTileState calculateTileState(RoomTile tile) {
        return this.calculateTileState(tile, null);
    }

    private RoomTileState calculateTileState(RoomTile tile, HabboItem exclude) {
        if (tile == null || tile.state == RoomTileState.INVALID) {
            return RoomTileState.INVALID;
        }
        RoomTileState result = RoomTileState.OPEN;
        THashSet<HabboItem> items = this.getItemsAt(tile);
        if (items == null) {
            return RoomTileState.INVALID;
        }
        HabboItem tallestItem = null;
        for (HabboItem item : items) {
            if (exclude != null && item == exclude) continue;
            if (item.getBaseItem().allowLay()) {
                return RoomTileState.LAY;
            }
            if (tallestItem != null && tallestItem.getZ() + Item.getCurrentHeight(tallestItem) > item.getZ() + Item.getCurrentHeight(item)) continue;
            result = this.checkStateForItem(item, tile);
            tallestItem = item;
        }
        return result;
    }

    private RoomTileState checkStateForItem(HabboItem item, RoomTile tile) {
        RoomTileState overriddenState;
        RoomTileState result = RoomTileState.BLOCKED;
        if (item.isWalkable()) {
            result = RoomTileState.OPEN;
        }
        if (item.getBaseItem().allowSit()) {
            result = RoomTileState.SIT;
        }
        if (item.getBaseItem().allowLay()) {
            result = RoomTileState.LAY;
        }
        if ((overriddenState = item.getOverrideTileState(tile, this)) != null) {
            result = overriddenState;
        }
        if (this.getItemsAt(tile).stream().anyMatch(i -> i instanceof InteractionTileWalkMagic)) {
            result = RoomTileState.OPEN;
        }
        return result;
    }

    public boolean tileWalkable(RoomTile t) {
        return this.tileWalkable(t.x, t.y);
    }

    public boolean tileWalkable(short x, short y) {
        boolean walkable = this.layout.tileWalkable(x, y);
        RoomTile tile = this.getLayout().getTile(x, y);
        if (walkable && tile != null && tile.hasUnits() && !this.allowWalkthrough) {
            walkable = false;
        }
        return walkable;
    }

    public void pickUpItem(HabboItem item, Habbo picker) {
        Habbo habbo;
        if (item == null) {
            return;
        }
        if (Emulator.getPluginManager().isRegistered(FurniturePickedUpEvent.class, true)) {
            FurniturePickedUpEvent furniturePickedUpEvent = new FurniturePickedUpEvent(item, picker);
            Emulator.getPluginManager().fireEvent(furniturePickedUpEvent);
            if (furniturePickedUpEvent.isCancelled()) {
                return;
            }
        }
        this.removeHabboItem(item.getId());
        item.onPickUp(this);
        item.setRoomId(0);
        item.needsUpdate(true);
        if (item.getBaseItem().getType() == FurnitureType.FLOOR) {
            this.sendComposer(new RemoveFloorItemComposer(item).compose());
            THashSet<RoomTile> updatedTiles = new THashSet<RoomTile>();
            Rectangle rectangle = RoomLayout.getRectangle(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            short x = (short)rectangle.x;
            while ((double)x < (double)rectangle.x + rectangle.getWidth()) {
                short y = (short)rectangle.y;
                while ((double)y < (double)rectangle.y + rectangle.getHeight()) {
                    double stackHeight = this.getStackHeight(x, y, false);
                    RoomTile tile = this.layout.getTile(x, y);
                    if (tile != null) {
                        tile.setStackHeight(stackHeight);
                        updatedTiles.add(tile);
                    }
                    y = (short)(y + 1);
                }
                x = (short)(x + 1);
            }
            this.sendComposer(new UpdateStackHeightComposer(this, updatedTiles).compose());
            this.updateTiles(updatedTiles);
            for (RoomTile tile : updatedTiles) {
                this.updateHabbosAt(tile.x, tile.y);
                this.updateBotsAt(tile.x, tile.y);
            }
        } else if (item.getBaseItem().getType() == FurnitureType.WALL) {
            this.sendComposer(new RemoveWallItemComposer(item).compose());
        }
        Habbo habbo2 = habbo = picker != null && picker.getHabboInfo().getId() == item.getId() ? picker : Emulator.getGameServer().getGameClientManager().getHabbo(item.getUserId());
        if (habbo != null) {
            habbo.getInventory().getItemsComponent().addItem(item);
            habbo.getClient().sendResponse(new AddHabboItemComposer(item));
            habbo.getClient().sendResponse(new InventoryRefreshComposer());
        }
        Emulator.getThreading().run(item);
    }

    public void updateHabbosAt(Rectangle rectangle) {
        for (short i = (short)rectangle.x; i < rectangle.x + rectangle.width; i = (short)(i + 1)) {
            for (short j = (short)rectangle.y; j < rectangle.y + rectangle.height; j = (short)(j + 1)) {
                this.updateHabbosAt(i, j);
            }
        }
    }

    public void updateHabbo(Habbo habbo) {
        this.updateRoomUnit(habbo.getRoomUnit());
    }

    public void updateRoomUnit(RoomUnit roomUnit) {
        HabboItem item = this.getTopItemAt(roomUnit.getX(), roomUnit.getY());
        if (item == null && !roomUnit.cmdSit || item != null && !item.getBaseItem().allowSit()) {
            roomUnit.removeStatus(RoomUnitStatus.SIT);
        }
        double oldZ = roomUnit.getZ();
        if (item != null) {
            if (item.getBaseItem().allowSit()) {
                roomUnit.setZ(item.getZ());
            } else {
                roomUnit.setZ(item.getZ() + Item.getCurrentHeight(item));
            }
            if (oldZ != roomUnit.getZ()) {
                this.scheduledTasks.add(() -> {
                    try {
                        item.onWalkOn(roomUnit, this, null);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                });
            }
        }
        this.sendComposer(new RoomUserStatusComposer(roomUnit).compose());
    }

    public void updateHabbosAt(short x, short y) {
        this.updateHabbosAt(x, y, this.getHabbosAt(x, y));
    }

    public void updateHabbosAt(short x, short y, THashSet<Habbo> habbos) {
        HabboItem item = this.getTopItemAt(x, y);
        THashSet roomUnits = new THashSet();
        for (Habbo habbo : habbos) {
            double oldZ = habbo.getRoomUnit().getZ();
            RoomUserRotation oldRotation = habbo.getRoomUnit().getBodyRotation();
            double z = habbo.getRoomUnit().getCurrentLocation().getStackHeight();
            boolean updated = false;
            if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.SIT) && (item == null && !habbo.getRoomUnit().cmdSit || item != null && !item.getBaseItem().allowSit())) {
                habbo.getRoomUnit().removeStatus(RoomUnitStatus.SIT);
                updated = true;
            }
            if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.LAY) && (item == null && !habbo.getRoomUnit().cmdLay || item != null && !item.getBaseItem().allowLay())) {
                habbo.getRoomUnit().removeStatus(RoomUnitStatus.LAY);
                updated = true;
            }
            if (item != null && (item.getBaseItem().allowSit() || item.getBaseItem().allowLay())) {
                habbo.getRoomUnit().setZ(item.getZ());
                habbo.getRoomUnit().setPreviousLocationZ(item.getZ());
                habbo.getRoomUnit().setRotation(RoomUserRotation.fromValue(item.getRotation()));
            } else {
                habbo.getRoomUnit().setZ(z);
                habbo.getRoomUnit().setPreviousLocationZ(z);
            }
            if (!habbo.getRoomUnit().getCurrentLocation().is(x, y) || oldZ == z && !updated && oldRotation == habbo.getRoomUnit().getBodyRotation()) continue;
            habbo.getRoomUnit().statusUpdate(true);
        }
    }

    public void updateBotsAt(short x, short y) {
        HabboItem topItem = this.getTopItemAt(x, y);
        THashSet<RoomUnit> roomUnits = new THashSet<RoomUnit>();
        for (Bot bot : this.getBotsAt(this.layout.getTile(x, y))) {
            if (topItem != null) {
                if (topItem.getBaseItem().allowSit()) {
                    bot.getRoomUnit().setZ(topItem.getZ());
                    bot.getRoomUnit().setPreviousLocationZ(topItem.getZ());
                    bot.getRoomUnit().setRotation(RoomUserRotation.fromValue(topItem.getRotation()));
                } else {
                    bot.getRoomUnit().setZ(topItem.getZ() + Item.getCurrentHeight(topItem));
                    if (topItem.getBaseItem().allowLay()) {
                        bot.getRoomUnit().setStatus(RoomUnitStatus.LAY, "" + (topItem.getZ() + topItem.getBaseItem().getHeight()));
                    }
                }
            } else {
                bot.getRoomUnit().setZ(bot.getRoomUnit().getCurrentLocation().getStackHeight());
                bot.getRoomUnit().setPreviousLocationZ(bot.getRoomUnit().getCurrentLocation().getStackHeight());
            }
            roomUnits.add(bot.getRoomUnit());
        }
        if (!roomUnits.isEmpty()) {
            this.sendComposer(new RoomUserStatusComposer(roomUnits, true).compose());
        }
    }

    public void updatePetsAt(short x, short y) {
        HabboItem topItem = this.getTopItemAt(x, y);
        THashSet<RoomUnit> roomUnits = new THashSet<RoomUnit>();
        for (Pet pet : this.getPetsAt(this.layout.getTile(x, y))) {
            if (topItem != null) {
                if (topItem.getBaseItem().allowSit()) {
                    pet.getRoomUnit().setZ(topItem.getZ());
                    pet.getRoomUnit().setPreviousLocationZ(topItem.getZ());
                    pet.getRoomUnit().setRotation(RoomUserRotation.fromValue(topItem.getRotation()));
                } else {
                    pet.getRoomUnit().setZ(topItem.getZ() + Item.getCurrentHeight(topItem));
                    if (topItem.getBaseItem().allowLay()) {
                        pet.getRoomUnit().setStatus(RoomUnitStatus.LAY, "" + (topItem.getZ() + topItem.getBaseItem().getHeight()));
                    }
                }
            } else {
                pet.getRoomUnit().setZ(pet.getRoomUnit().getCurrentLocation().getStackHeight());
                pet.getRoomUnit().setPreviousLocationZ(pet.getRoomUnit().getCurrentLocation().getStackHeight());
            }
            roomUnits.add(pet.getRoomUnit());
        }
        if (!roomUnits.isEmpty()) {
            this.sendComposer(new RoomUserStatusComposer(roomUnits, true).compose());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void pickupPetsForHabbo(Habbo habbo) {
        THashSet<Pet> pets = new THashSet<Pet>();
        TIntObjectMap<Pet> tIntObjectMap = this.currentPets;
        synchronized (tIntObjectMap) {
            for (Pet pet : this.currentPets.valueCollection()) {
                if (pet.getUserId() != habbo.getHabboInfo().getId()) continue;
                pets.add(pet);
            }
        }
        for (Pet pet : pets) {
            pet.removeFromRoom();
            Emulator.getThreading().run(pet);
            habbo.getInventory().getPetsComponent().addPet(pet);
            habbo.getClient().sendResponse(new AddPetComposer(pet));
            this.currentPets.remove(pet.getId());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void startTrade(Habbo userOne, Habbo userTwo) {
        RoomTrade trade = new RoomTrade(userOne, userTwo, this);
        THashSet<RoomTrade> tHashSet = this.activeTrades;
        synchronized (tHashSet) {
            this.activeTrades.add(trade);
        }
        trade.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopTrade(RoomTrade trade) {
        THashSet<RoomTrade> tHashSet = this.activeTrades;
        synchronized (tHashSet) {
            this.activeTrades.remove(trade);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RoomTrade getActiveTradeForHabbo(Habbo user) {
        THashSet<RoomTrade> tHashSet = this.activeTrades;
        synchronized (tHashSet) {
            for (RoomTrade trade : this.activeTrades) {
                for (RoomTradeUser habbo : trade.getRoomTradeUsers()) {
                    if (habbo.getHabbo() != user) continue;
                    return trade;
                }
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void dispose() {
        Object object = this.loadLock;
        synchronized (object) {
            if (this.preventUnloading) {
                return;
            }
            if (Emulator.getPluginManager().fireEvent(new RoomUnloadingEvent(this)).isCancelled()) {
                return;
            }
            if (this.loaded) {
                try {
                    if (this.traxManager != null && !this.traxManager.disposed()) {
                        this.traxManager.dispose();
                    }
                    this.roomCycleTask.cancel(false);
                    this.scheduledTasks.clear();
                    this.scheduledComposers.clear();
                    this.loaded = false;
                    this.tileCache.clear();
                    Object object2 = this.mutedHabbos;
                    synchronized (object2) {
                        this.mutedHabbos.clear();
                    }
                    for (InteractionGameTimer timer : this.getRoomSpecialTypes().getGameTimers().values()) {
                        timer.setRunning(false);
                    }
                    for (Game game : this.games) {
                        game.dispose();
                    }
                    this.games.clear();
                    this.removeAllPets(this.ownerId);
                    object2 = this.roomItems;
                    synchronized (object2) {
                        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
                        int i = this.roomItems.size();
                        while (i-- > 0) {
                            try {
                                iterator.advance();
                                if (!iterator.value().needsUpdate()) continue;
                                iterator.value().run();
                            }
                            catch (NoSuchElementException e) {
                                // empty catch block
                                break;
                            }
                        }
                    }
                    if (this.roomSpecialTypes != null) {
                        this.roomSpecialTypes.dispose();
                    }
                    object2 = this.roomItems;
                    synchronized (object2) {
                        this.roomItems.clear();
                    }
                    object2 = this.habboQueue;
                    synchronized (object2) {
                        this.habboQueue.clear();
                    }
                    for (Habbo habbo : this.currentHabbos.values()) {
                        Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this);
                    }
                    this.sendComposer(new HotelViewComposer().compose());
                    this.currentHabbos.clear();
                    TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();
                    int i = this.currentBots.size();
                    while (i-- > 0) {
                        try {
                            botIterator.advance();
                            botIterator.value().needsUpdate(true);
                            Emulator.getThreading().run(botIterator.value());
                        }
                        catch (NoSuchElementException e) {
                            LOGGER.error("Caught exception", e);
                            break;
                        }
                    }
                    this.currentBots.clear();
                    this.currentPets.clear();
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
            }
            try {
                this.wordQuiz = "";
                this.yesVotes = 0;
                this.noVotes = 0;
                this.updateDatabaseUserCount();
                this.preLoaded = true;
                this.layout = null;
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
        Emulator.getPluginManager().fireEvent(new RoomUnloadedEvent(this));
    }

    @Override
    public int compareTo(Room o) {
        if (o.getUserCount() != this.getUserCount()) {
            return o.getCurrentHabbos().size() - this.getCurrentHabbos().size();
        }
        return this.id - o.id;
    }

    @Override
    public void serialize(ServerMessage message) {
        message.appendInt(this.id);
        message.appendString(this.name);
        if (this.isPublicRoom()) {
            message.appendInt(0);
            message.appendString("");
        } else {
            message.appendInt(this.ownerId);
            message.appendString(this.ownerName);
        }
        message.appendInt(this.state.getState());
        message.appendInt(this.getUserCount());
        message.appendInt(this.usersMax);
        message.appendString(this.description);
        message.appendInt(0);
        message.appendInt(this.score);
        message.appendInt(0);
        message.appendInt(this.category);
        String[] tags = (String[])Arrays.stream(this.tags.split(";")).filter(t -> !t.isEmpty()).toArray(String[]::new);
        message.appendInt(tags.length);
        for (String s : tags) {
            message.appendString(s);
        }
        int base = 0;
        if (this.getGuildId() > 0) {
            base |= 2;
        }
        if (this.isPromoted()) {
            base |= 4;
        }
        if (!this.isPublicRoom()) {
            base |= 8;
        }
        if (this.isAllowPets()) {
            base |= 0x10;
        }
        message.appendInt(base);
        if (this.getGuildId() > 0) {
            Guild g = Emulator.getGameEnvironment().getGuildManager().getGuild(this.getGuildId());
            if (g != null) {
                message.appendInt(g.getId());
                message.appendString(g.getName());
                message.appendString(g.getBadge());
            } else {
                message.appendInt(0);
                message.appendString("");
                message.appendString("");
            }
        }
        if (this.promoted) {
            message.appendString(this.promotion.getTitle());
            message.appendString(this.promotion.getDescription());
            message.appendInt((this.promotion.getEndTimestamp() - Emulator.getIntUnixTimestamp()) / 60);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        long millis = System.currentTimeMillis();
        Object object = this.loadLock;
        synchronized (object) {
            if (this.loaded) {
                try {
                    Emulator.getThreading().run(this::cycle);
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
            }
        }
        this.save();
    }

    public void save() {
        if (this.needsUpdate) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET name = ?, description = ?, password = ?, state = ?, users_max = ?, category = ?, score = ?, paper_floor = ?, paper_wall = ?, paper_landscape = ?, thickness_wall = ?, wall_height = ?, thickness_floor = ?, moodlight_data = ?, tags = ?, allow_other_pets = ?, allow_other_pets_eat = ?, allow_walkthrough = ?, allow_hidewall = ?, chat_mode = ?, chat_weight = ?, chat_speed = ?, chat_hearing_distance = ?, chat_protection =?, who_can_mute = ?, who_can_kick = ?, who_can_ban = ?, poll_id = ?, guild_id = ?, roller_speed = ?, override_model = ?, is_staff_picked = ?, promoted = ?, trade_mode = ?, move_diagonally = ?, owner_id = ?, owner_name = ?, jukebox_active = ?, hidewired = ? WHERE id = ?");){
                statement.setString(1, this.name);
                statement.setString(2, this.description);
                statement.setString(3, this.password);
                statement.setString(4, this.state.name().toLowerCase());
                statement.setInt(5, this.usersMax);
                statement.setInt(6, this.category);
                statement.setInt(7, this.score);
                statement.setString(8, this.floorPaint);
                statement.setString(9, this.wallPaint);
                statement.setString(10, this.backgroundPaint);
                statement.setInt(11, this.wallSize);
                statement.setInt(12, this.wallHeight);
                statement.setInt(13, this.floorSize);
                StringBuilder moodLightData = new StringBuilder();
                int id = 1;
                for (RoomMoodlightData data : this.moodlightData.valueCollection()) {
                    data.setId(id);
                    moodLightData.append(data.toString()).append(";");
                    ++id;
                }
                statement.setString(14, moodLightData.toString());
                statement.setString(15, this.tags);
                statement.setString(16, this.allowPets ? "1" : "0");
                statement.setString(17, this.allowPetsEat ? "1" : "0");
                statement.setString(18, this.allowWalkthrough ? "1" : "0");
                statement.setString(19, this.hideWall ? "1" : "0");
                statement.setInt(20, this.chatMode);
                statement.setInt(21, this.chatWeight);
                statement.setInt(22, this.chatSpeed);
                statement.setInt(23, this.chatDistance);
                statement.setInt(24, this.chatProtection);
                statement.setInt(25, this.muteOption);
                statement.setInt(26, this.kickOption);
                statement.setInt(27, this.banOption);
                statement.setInt(28, this.pollId);
                statement.setInt(29, this.guild);
                statement.setInt(30, this.rollerSpeed);
                statement.setString(31, this.overrideModel ? "1" : "0");
                statement.setString(32, this.staffPromotedRoom ? "1" : "0");
                statement.setString(33, this.promoted ? "1" : "0");
                statement.setInt(34, this.tradeMode);
                statement.setString(35, this.moveDiagonally ? "1" : "0");
                statement.setInt(36, this.ownerId);
                statement.setString(37, this.ownerName);
                statement.setString(38, this.jukeboxActive ? "1" : "0");
                statement.setString(39, this.hideWired ? "1" : "0");
                statement.setInt(40, this.id);
                statement.executeUpdate();
                this.needsUpdate = false;
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    private void updateDatabaseUserCount() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET users = ? WHERE id = ? LIMIT 1");){
            statement.setInt(1, this.currentHabbos.size());
            statement.setInt(2, this.id);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    private boolean isOnRoller(RoomUnit unit) {
        RoomTile currentTile = unit.getCurrentLocation();
        for (HabboItem habboItem : this.roomSpecialTypes.getRollers().values()) {
            if (habboItem.getX() != currentTile.x || habboItem.getY() != currentTile.y) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cycle() {
        boolean loaded;
        this.cycleOdd = !this.cycleOdd;
        this.cycleTimestamp = System.currentTimeMillis();
        boolean[] foundRightHolder = new boolean[]{false};
        Object object = this.loadLock;
        synchronized (object) {
            loaded = this.loaded;
        }
        this.tileCache.clear();
        if (loaded) {
            if (!this.scheduledTasks.isEmpty()) {
                ConcurrentSet<Runnable> tasks = this.scheduledTasks;
                this.scheduledTasks = new ConcurrentSet();
                Iterator iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    Runnable runnable = (Runnable)iterator.next();
                    Emulator.getThreading().run(runnable);
                }
            }
            for (ICycleable task : this.roomSpecialTypes.getCycleTasks()) {
                task.cycle(this);
            }
            if (Emulator.getConfig().getBoolean("hotel.rooms.deco_hosting")) {
                if (this.idleHostingCycles < 120) {
                    ++this.idleHostingCycles;
                } else {
                    this.idleHostingCycles = 0;
                    int amount = (int)this.currentHabbos.values().stream().filter(habbo -> habbo.getHabboInfo().getId() != this.ownerId).count();
                    if (amount > 0) {
                        AchievementManager.progressAchievement(this.ownerId, Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoHosting"), amount);
                    }
                }
            }
            if (!this.currentHabbos.isEmpty()) {
                this.idleCycles = 0;
                THashSet<RoomUnit> updatedUnit = new THashSet<RoomUnit>();
                ArrayList<Habbo> toKick = new ArrayList<Habbo>();
                Room room = this;
                long millis = System.currentTimeMillis();
                for (Habbo habbo2 : this.currentHabbos.values()) {
                    RoomUnit unit = habbo2.getRoomUnit();
                    if (!unit.hasStatus(RoomUnitStatus.MOVE) && this.isOnRoller(unit)) {
                        double curZ;
                        double curY;
                        double curX = unit.getX();
                        LoopTracker tracker = this.loopTrackers.computeIfAbsent(unit, arg_0 -> this.lambda$cycle$7(curX, curY = (double)unit.getY(), curZ = unit.getZ(), arg_0));
                        if (tracker.isSamePosition(curX, curY, curZ)) {
                            currentRoller = null;
                            currentTile = this.getLayout().getTile((short)curX, (short)curY);
                            for (InteractionRoller roller2 : this.roomSpecialTypes.getRollers().values()) {
                                if (roller2.getX() != currentTile.x || roller2.getY() != currentTile.y) continue;
                                currentRoller = roller2;
                                break;
                            }
                            Object currentCalculatedNextTile = null;
                            if (currentRoller != null) {
                                RoomTile rollerTile = this.getLayout().getTile(currentRoller.getX(), currentRoller.getY());
                                currentCalculatedNextTile = this.getLayout().getTileInFront(rollerTile, currentRoller.getRotation());
                            }
                            if (currentTile != null && unit.getGoal() != null) {
                                double distanceToGoal = Math.hypot(unit.getGoal().x - currentTile.x, unit.getGoal().y - currentTile.y);
                                double ACCEPTABLE_DISTANCE = 0.5;
                                if (distanceToGoal <= 0.5) {
                                    tracker.update(curX, curY, curZ, (RoomTile)currentCalculatedNextTile);
                                    tracker.counter = 0;
                                    continue;
                                }
                            }
                            boolean nextTileHasRoller = false;
                            if (currentCalculatedNextTile != null) {
                                for (InteractionRoller roller3 : this.roomSpecialTypes.getRollers().values()) {
                                    if (roller3.getX() != ((RoomTile)currentCalculatedNextTile).x || roller3.getY() != ((RoomTile)currentCalculatedNextTile).y) continue;
                                    nextTileHasRoller = true;
                                    break;
                                }
                            }
                            if (currentCalculatedNextTile != null && !nextTileHasRoller) {
                                tracker.update(curX, curY, curZ, (RoomTile)currentCalculatedNextTile);
                                tracker.counter = 0;
                            } else if (unit.getGoal() != null && tracker.nextTile != null && unit.getGoal().x == tracker.nextTile.x && unit.getGoal().y == tracker.nextTile.y) {
                                tracker.counter = 0;
                            } else {
                                ++tracker.counter;
                                int loopThreshold = Math.max(10, 3 + this.getRollerSpeed());
                                if (tracker.counter >= loopThreshold) {
                                    RoomTile doorTile = this.getLayout().getDoorTile();
                                    unit.setBodyRotation(RoomUserRotation.values()[this.getLayout().getDoorDirection()]);
                                    unit.setHeadRotation(RoomUserRotation.values()[this.getLayout().getDoorDirection()]);
                                    this.teleportRoomUnitToLocation(unit, doorTile.x, doorTile.y, doorTile.z);
                                    tracker.counter = 0;
                                }
                            }
                        } else {
                            currentRoller = null;
                            currentTile = this.getLayout().getTile((short)curX, (short)curY);
                            for (InteractionRoller roller4 : this.roomSpecialTypes.getRollers().values()) {
                                if (roller4.getX() != currentTile.x || roller4.getY() != currentTile.y) continue;
                                currentRoller = roller4;
                                break;
                            }
                            RoomTile nextTile = null;
                            if (currentRoller != null) {
                                RoomTile rollerTile = this.getLayout().getTile(currentRoller.getX(), currentRoller.getY());
                                nextTile = this.getLayout().getTileInFront(rollerTile, currentRoller.getRotation());
                            }
                            tracker.update(curX, curY, curZ, nextTile);
                        }
                    }
                    if (!foundRightHolder[0]) {
                        boolean bl = foundRightHolder[0] = habbo2.getRoomUnit().getRightsLevel() != RoomRightLevels.NONE;
                    }
                    if (habbo2.getRoomUnit().getHandItem() > 0 && millis - habbo2.getRoomUnit().getHandItemTimestamp() > (long)(HAND_ITEM_TIME * 1000)) {
                        this.giveHandItem(habbo2, 0);
                    }
                    if (habbo2.getRoomUnit().getEffectId() > 0 && millis / 1000L > (long)habbo2.getRoomUnit().getEffectEndTimestamp()) {
                        this.giveEffect(habbo2, 0, -1);
                    }
                    if (habbo2.getRoomUnit().isKicked) {
                        ++habbo2.getRoomUnit().kickCount;
                        if (habbo2.getRoomUnit().kickCount >= 5) {
                            this.scheduledTasks.add(() -> Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo2, room));
                            continue;
                        }
                    }
                    if (Emulator.getConfig().getBoolean("hotel.rooms.auto.idle")) {
                        if (!habbo2.getRoomUnit().isIdle()) {
                            habbo2.getRoomUnit().increaseIdleTimer();
                            if (habbo2.getRoomUnit().isIdle()) {
                                boolean danceIsNone;
                                boolean bl = danceIsNone = habbo2.getRoomUnit().getDanceType() == DanceType.NONE;
                                if (danceIsNone) {
                                    this.sendComposer(new RoomUnitIdleComposer(habbo2.getRoomUnit()).compose());
                                }
                                if (danceIsNone && !Emulator.getConfig().getBoolean("hotel.roomuser.idle.not_dancing.ignore.wired_idle")) {
                                    WiredHandler.handle(WiredTriggerType.IDLES, habbo2.getRoomUnit(), this, new Object[]{habbo2});
                                }
                            }
                        } else {
                            habbo2.getRoomUnit().increaseIdleTimer();
                            if (!this.isOwner(habbo2) && habbo2.getRoomUnit().getIdleTimer() >= IDLE_CYCLES_KICK) {
                                UserExitRoomEvent event = new UserExitRoomEvent(habbo2, UserExitRoomEvent.UserExitRoomReason.KICKED_IDLE);
                                Emulator.getPluginManager().fireEvent(event);
                                if (!event.isCancelled()) {
                                    toKick.add(habbo2);
                                }
                            }
                        }
                    }
                    if (habbo2.getHabboStats().mutedBubbleTracker && habbo2.getHabboStats().allowTalk()) {
                        habbo2.getHabboStats().mutedBubbleTracker = false;
                        this.sendComposer(new RoomUserIgnoredComposer(habbo2, 3).compose());
                    }
                    if (this.cycleOdd && habbo2.getHabboStats().chatCounter.get() > 0) {
                        habbo2.getHabboStats().chatCounter.decrementAndGet();
                    }
                    if (!this.cycleRoomUnit(habbo2.getRoomUnit(), RoomUnitType.USER)) continue;
                    updatedUnit.add(habbo2.getRoomUnit());
                }
                if (!toKick.isEmpty()) {
                    for (Habbo habbo2 : toKick) {
                        Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo2, this);
                    }
                }
                if (!this.currentBots.isEmpty()) {
                    TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();
                    int i = this.currentBots.size();
                    while (i-- > 0) {
                        try {
                            Bot bot;
                            try {
                                botIterator.advance();
                                bot = botIterator.value();
                            }
                            catch (Exception e) {
                                break;
                            }
                            if (!this.allowBotsWalk && bot.getRoomUnit().isWalking()) {
                                bot.getRoomUnit().stopWalking();
                                updatedUnit.add(bot.getRoomUnit());
                                continue;
                            }
                            botIterator.value().cycle(this.allowBotsWalk);
                            if (!this.cycleRoomUnit(bot.getRoomUnit(), RoomUnitType.BOT)) continue;
                            updatedUnit.add(bot.getRoomUnit());
                        }
                        catch (NoSuchElementException e) {
                            LOGGER.error("Caught exception", e);
                            break;
                        }
                    }
                }
                if (!this.currentPets.isEmpty() && this.allowBotsWalk) {
                    TIntObjectIterator<Pet> petIterator = this.currentPets.iterator();
                    int i = this.currentPets.size();
                    while (i-- > 0) {
                        try {
                            petIterator.advance();
                        }
                        catch (NoSuchElementException e) {
                            LOGGER.error("Caught exception", e);
                            break;
                        }
                        Pet pet = petIterator.value();
                        if (this.cycleRoomUnit(pet.getRoomUnit(), RoomUnitType.PET)) {
                            updatedUnit.add(pet.getRoomUnit());
                        }
                        pet.cycle();
                        if (pet.packetUpdate) {
                            updatedUnit.add(pet.getRoomUnit());
                            pet.packetUpdate = false;
                        }
                        if (!pet.getRoomUnit().isWalking() || pet.getRoomUnit().getPath().size() != 1 || !pet.getRoomUnit().hasStatus(RoomUnitStatus.GESTURE)) continue;
                        pet.getRoomUnit().removeStatus(RoomUnitStatus.GESTURE);
                        updatedUnit.add(pet.getRoomUnit());
                    }
                }
                ArrayList<InteractionRoller> rollers = new ArrayList<InteractionRoller>(this.roomSpecialTypes.getRollers().values());
                rollers.sort((r1, r2) -> {
                    double angle1 = Math.toRadians(r1.getRotation() * 45);
                    double angle2 = Math.toRadians(r2.getRotation() * 45);
                    double vx1 = Math.cos(angle1);
                    double vy1 = Math.sin(angle1);
                    double vx2 = Math.cos(angle2);
                    double vy2 = Math.sin(angle2);
                    double proj1 = (double)r1.getX() * vx1 + (double)r1.getY() * vy1;
                    double proj2 = (double)r2.getX() * vx2 + (double)r2.getY() * vy2;
                    return Double.compare(proj1, proj2);
                });
                if (this.rollerSpeed != -1 && this.rollerCycle >= (long)this.rollerSpeed) {
                    this.rollerCycle = 0L;
                    THashSet messages = new THashSet();
                    ArrayList rollerFurniIds = new ArrayList();
                    ArrayList rolledUnitIds = new ArrayList();
                    this.roomSpecialTypes.getRollers().forEachValue(roller -> {
                        HabboItem newRoller = null;
                        RoomTile rollerTile = this.getLayout().getTile(roller.getX(), roller.getY());
                        if (rollerTile == null) {
                            return true;
                        }
                        THashSet<HabboItem> itemsOnRoller = new THashSet<HabboItem>();
                        for (HabboItem item : this.getItemsAt(rollerTile)) {
                            if (!(item.getZ() >= roller.getZ() + Item.getCurrentHeight(roller))) continue;
                            itemsOnRoller.add(item);
                        }
                        itemsOnRoller.remove(roller);
                        if (!rollerTile.hasUnits() && itemsOnRoller.isEmpty()) {
                            return true;
                        }
                        RoomTile tileInFront = this.layout.getTileInFront(this.layout.getTile(roller.getX(), roller.getY()), roller.getRotation());
                        if (tileInFront == null) {
                            return true;
                        }
                        if (!this.layout.tileExists(tileInFront.x, tileInFront.y)) {
                            return true;
                        }
                        if (tileInFront.state == RoomTileState.INVALID) {
                            return true;
                        }
                        if (!tileInFront.getAllowStack() && !tileInFront.isWalkable() && tileInFront.state != RoomTileState.SIT && tileInFront.state != RoomTileState.LAY) {
                            return true;
                        }
                        if (tileInFront.hasUnits()) {
                            return true;
                        }
                        THashSet<HabboItem> itemsNewTile = new THashSet<HabboItem>();
                        itemsNewTile.addAll(this.getItemsAt(tileInFront));
                        itemsNewTile.removeAll(itemsOnRoller);
                        ArrayList<HabboItem> toRemove = new ArrayList<HabboItem>();
                        for (HabboItem item : itemsOnRoller) {
                            if (item.getX() == roller.getX() && item.getY() == roller.getY() && !rollerFurniIds.contains(item.getId())) continue;
                            toRemove.add(item);
                        }
                        itemsOnRoller.removeAll(toRemove);
                        HabboItem topItem = this.getTopItemAt(tileInFront.x, tileInFront.y);
                        boolean allowUsers = true;
                        boolean allowFurniture = true;
                        boolean stackContainsRoller = false;
                        for (HabboItem item : itemsNewTile) {
                            if (!(item.getBaseItem().allowWalk() || item.getBaseItem().allowSit() || item instanceof InteractionGate && item.getExtradata().equals("1"))) {
                                allowUsers = false;
                            }
                            if (item instanceof InteractionRoller) {
                                newRoller = item;
                                stackContainsRoller = true;
                                if (item.getZ() == roller.getZ() && (itemsNewTile.size() <= 1 || item == topItem) || InteractionRoller.NO_RULES) break;
                                allowUsers = false;
                                allowFurniture = false;
                                continue;
                            }
                            allowFurniture = false;
                        }
                        if (allowFurniture) {
                            allowFurniture = tileInFront.getAllowStack();
                        }
                        double zOffset = 0.0;
                        if (newRoller != null) {
                            if (!itemsNewTile.isEmpty() && itemsNewTile.size() > 1 && !InteractionRoller.NO_RULES) {
                                return true;
                            }
                        } else {
                            zOffset = -Item.getCurrentHeight(roller) + tileInFront.getStackHeight() - (double)rollerTile.z;
                        }
                        if (allowUsers) {
                            Object roomUserRolledEvent = null;
                            if (Emulator.getPluginManager().isRegistered(UserRolledEvent.class, true)) {
                                roomUserRolledEvent = new UserRolledEvent(null, null, null);
                            }
                            ArrayList<RoomUnit> unitsOnTile = new ArrayList<RoomUnit>(rollerTile.getUnits());
                            for (RoomUnit unit : rollerTile.getUnits()) {
                                Pet pet;
                                if (unit.getRoomUnitType() != RoomUnitType.PET || !((pet = this.getPet(unit)) instanceof RideablePet) || ((RideablePet)pet).getRider() == null) continue;
                                unitsOnTile.remove(unit);
                            }
                            HabboItem nextTileChair = this.getTallestChair(tileInFront);
                            THashSet<Integer> usersRolledThisTile = new THashSet<Integer>();
                            for (RoomUnit unit : unitsOnTile) {
                                HabboItem item;
                                RideablePet riding;
                                Habbo rollingHabbo;
                                if (rolledUnitIds.contains(unit.getId())) continue;
                                if (usersRolledThisTile.size() >= ROLLERS_MAXIMUM_ROLL_AVATARS) break;
                                if (stackContainsRoller && !allowFurniture && (topItem == null || !topItem.isWalkable()) || unit.hasStatus(RoomUnitStatus.MOVE)) continue;
                                RoomTile tile = tileInFront.copy();
                                tile.setStackHeight(unit.getZ() + zOffset);
                                if (roomUserRolledEvent != null && unit.getRoomUnitType() == RoomUnitType.USER) {
                                    roomUserRolledEvent = new UserRolledEvent(this.getHabbo(unit), (HabboItem)roller, tile);
                                    Emulator.getPluginManager().fireEvent(roomUserRolledEvent);
                                    if (((Event)roomUserRolledEvent).isCancelled()) continue;
                                }
                                boolean isRiding = false;
                                if (unit.getRoomUnitType() == RoomUnitType.USER && (rollingHabbo = this.getHabbo(unit)) != null && rollingHabbo.getHabboInfo() != null && (riding = rollingHabbo.getHabboInfo().getRiding()) != null) {
                                    RoomUnit ridingUnit = riding.getRoomUnit();
                                    tile.setStackHeight(ridingUnit.getZ() + zOffset);
                                    rolledUnitIds.add(ridingUnit.getId());
                                    updatedUnit.remove(ridingUnit);
                                    messages.add(new RoomUnitOnRollerComposer(ridingUnit, (HabboItem)roller, ridingUnit.getCurrentLocation(), ridingUnit.getZ(), tile, tile.getStackHeight(), room));
                                    isRiding = true;
                                }
                                usersRolledThisTile.add(unit.getId());
                                rolledUnitIds.add(unit.getId());
                                updatedUnit.remove(unit);
                                messages.add(new RoomUnitOnRollerComposer(unit, (HabboItem)roller, unit.getCurrentLocation(), unit.getZ() + (double)(isRiding ? 1 : 0), tile, tile.getStackHeight() + (double)(isRiding ? 1 : 0), room));
                                if (itemsOnRoller.isEmpty() && (item = room.getTopItemAt(tileInFront.x, tileInFront.y)) != null && itemsNewTile.contains(item) && !itemsOnRoller.contains(item)) {
                                    Emulator.getThreading().run(() -> {
                                        if (unit.getGoal() == rollerTile) {
                                            try {
                                                item.onWalkOn(unit, room, new Object[]{rollerTile, tileInFront});
                                            }
                                            catch (Exception e) {
                                                LOGGER.error("Caught exception", e);
                                            }
                                        }
                                    }, this.getRollerSpeed() == 0 ? 250L : (long)InteractionRoller.DELAY);
                                }
                                if (!unit.hasStatus(RoomUnitStatus.SIT)) continue;
                                unit.sitUpdate = true;
                            }
                        }
                        if (!messages.isEmpty()) {
                            for (MessageComposer message : messages) {
                                room.sendComposer(message.compose());
                            }
                            messages.clear();
                        }
                        if (allowFurniture || !stackContainsRoller || InteractionRoller.NO_RULES) {
                            FurnitureRolledEvent furnitureRolledEvent = null;
                            if (Emulator.getPluginManager().isRegistered(FurnitureRolledEvent.class, true)) {
                                furnitureRolledEvent = new FurnitureRolledEvent(null, null, null);
                            }
                            if (newRoller == null || topItem == newRoller) {
                                ArrayList sortedItems = new ArrayList(itemsOnRoller);
                                sortedItems.sort((o1, o2) -> o1.getZ() > o2.getZ() ? -1 : 1);
                                for (HabboItem item : sortedItems) {
                                    if (item.getX() != roller.getX() || item.getY() != roller.getY() || !(zOffset <= 0.0) || item == roller) continue;
                                    if (furnitureRolledEvent != null) {
                                        furnitureRolledEvent = new FurnitureRolledEvent(item, (HabboItem)roller, tileInFront);
                                        Emulator.getPluginManager().fireEvent(furnitureRolledEvent);
                                        if (furnitureRolledEvent.isCancelled()) continue;
                                    }
                                    messages.add(new FloorItemOnRollerComposer(item, (HabboItem)roller, tileInFront, zOffset, room));
                                    rollerFurniIds.add(item.getId());
                                }
                            }
                        }
                        if (!messages.isEmpty()) {
                            for (MessageComposer message : messages) {
                                room.sendComposer(message.compose());
                            }
                            messages.clear();
                        }
                        return true;
                    });
                    int currentTime = (int)(this.cycleTimestamp / 1000L);
                    for (HabboItem pyramid : this.roomSpecialTypes.getItemsOfType(InteractionPyramid.class)) {
                        if (!(pyramid instanceof InteractionPyramid) || ((InteractionPyramid)pyramid).getNextChange() >= currentTime) continue;
                        ((InteractionPyramid)pyramid).change(this);
                    }
                } else {
                    ++this.rollerCycle;
                }
                if (!updatedUnit.isEmpty()) {
                    this.sendComposer(new RoomUserStatusComposer(updatedUnit, true).compose());
                }
                this.traxManager.cycle();
            } else if (this.idleCycles < 60) {
                ++this.idleCycles;
            } else {
                this.dispose();
            }
        }
        TIntObjectMap<Habbo> tIntObjectMap = this.habboQueue;
        synchronized (tIntObjectMap) {
            if (!this.habboQueue.isEmpty() && !foundRightHolder[0]) {
                this.habboQueue.forEachEntry(new TIntObjectProcedure<Habbo>(){

                    @Override
                    public boolean execute(int a, Habbo b) {
                        if (b.isOnline() && b.getHabboInfo().getRoomQueueId() == Room.this.getId()) {
                            b.getClient().sendResponse(new RoomAccessDeniedComposer(""));
                        }
                        return true;
                    }
                });
                this.habboQueue.clear();
            }
        }
        if (!this.scheduledComposers.isEmpty()) {
            for (ServerMessage message : this.scheduledComposers) {
                this.sendComposer(message);
            }
            this.scheduledComposers.clear();
        }
    }

    private boolean cycleRoomUnit(RoomUnit unit, RoomUnitType type) {
        boolean update = unit.needsStatusUpdate();
        if (unit.hasStatus(RoomUnitStatus.SIGN)) {
            this.sendComposer(new RoomUserStatusComposer(unit).compose());
            unit.removeStatus(RoomUnitStatus.SIGN);
        }
        if (unit.isWalking() && unit.getPath() != null && !unit.getPath().isEmpty()) {
            if (!unit.cycle(this)) {
                return true;
            }
        } else {
            if ((unit.hasStatus(RoomUnitStatus.MOVE) || unit.hasStatus(RoomUnitStatus.SNOWWAR_RUN)) && !unit.animateWalk) {
                unit.removeStatus(RoomUnitStatus.MOVE);
                unit.removeStatus(RoomUnitStatus.SNOWWAR_RUN);
                update = true;
            }
            if (!unit.isWalking() && !unit.cmdSit) {
                RoomTile thisTile = this.getLayout().getTile(unit.getX(), unit.getY());
                HabboItem topItem = this.getTallestChair(thisTile);
                if (topItem == null || !topItem.getBaseItem().allowSit()) {
                    if (unit.hasStatus(RoomUnitStatus.SIT)) {
                        unit.removeStatus(RoomUnitStatus.SIT);
                        update = true;
                    }
                } else if (thisTile.state == RoomTileState.SIT && (!unit.hasStatus(RoomUnitStatus.SIT) || unit.sitUpdate)) {
                    this.dance(unit, DanceType.NONE);
                    unit.setStatus(RoomUnitStatus.SIT, "" + Item.getCurrentHeight(topItem) * 1.0);
                    unit.setZ(topItem.getZ());
                    unit.setRotation(RoomUserRotation.values()[topItem.getRotation()]);
                    unit.sitUpdate = false;
                    return true;
                }
            }
        }
        if (!(unit.isWalking() || unit.cmdLay || unit.hasStatus(RoomUnitStatus.SNOWWAR_DIE_BACK))) {
            HabboItem topItem = this.getTopItemAt(unit.getX(), unit.getY());
            if (topItem == null || !topItem.getBaseItem().allowLay()) {
                if (unit.hasStatus(RoomUnitStatus.LAY)) {
                    unit.removeStatus(RoomUnitStatus.LAY);
                    update = true;
                }
            } else if (!unit.hasStatus(RoomUnitStatus.LAY)) {
                unit.setStatus(RoomUnitStatus.LAY, "" + Item.getCurrentHeight(topItem) * 1.0);
                unit.setRotation(RoomUserRotation.values()[topItem.getRotation() % 4]);
                if (topItem.getRotation() == 0 || topItem.getRotation() == 4) {
                    unit.setLocation(this.layout.getTile(unit.getX(), topItem.getY()));
                } else {
                    unit.setLocation(this.layout.getTile(topItem.getX(), unit.getY()));
                }
                update = true;
            }
        }
        if (update) {
            unit.statusUpdate(false);
        }
        return update;
    }

    public int getId() {
        return this.id;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        Guild guild;
        this.name = name;
        if (this.name.length() > 50) {
            this.name = this.name.substring(0, 50);
        }
        if (this.hasGuild() && (guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guild)) != null) {
            guild.setRoomName(name);
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
        if (this.description.length() > 250) {
            this.description = this.description.substring(0, 250);
        }
    }

    public RoomLayout getLayout() {
        return this.layout;
    }

    public void setLayout(RoomLayout layout) {
        this.layout = layout;
    }

    public boolean hasCustomLayout() {
        return this.overrideModel;
    }

    public void setHasCustomLayout(boolean overrideModel) {
        this.overrideModel = overrideModel;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
        if (this.password.length() > 20) {
            this.password = this.password.substring(0, 20);
        }
    }

    public RoomState getState() {
        return this.state;
    }

    public void setState(RoomState state) {
        this.state = state;
    }

    public int getUsersMax() {
        return this.usersMax;
    }

    public void setUsersMax(int usersMax) {
        this.usersMax = usersMax;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCategory() {
        return this.category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getFloorPaint() {
        return this.floorPaint;
    }

    public void setFloorPaint(String floorPaint) {
        this.floorPaint = floorPaint;
    }

    public String getWallPaint() {
        return this.wallPaint;
    }

    public void setWallPaint(String wallPaint) {
        this.wallPaint = wallPaint;
    }

    public String getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(String backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    public int getWallSize() {
        return this.wallSize;
    }

    public void setWallSize(int wallSize) {
        this.wallSize = wallSize;
    }

    public int getWallHeight() {
        return this.wallHeight;
    }

    public void setWallHeight(int wallHeight) {
        this.wallHeight = wallHeight;
    }

    public int getFloorSize() {
        return this.floorSize;
    }

    public void setFloorSize(int floorSize) {
        this.floorSize = floorSize;
    }

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getTradeMode() {
        return this.tradeMode;
    }

    public void setTradeMode(int tradeMode) {
        this.tradeMode = tradeMode;
    }

    public boolean moveDiagonally() {
        return this.moveDiagonally;
    }

    public void moveDiagonally(boolean moveDiagonally) {
        this.moveDiagonally = moveDiagonally;
        this.layout.moveDiagonally(this.moveDiagonally);
        this.needsUpdate = true;
    }

    public int getGuildId() {
        return this.guild;
    }

    public boolean hasGuild() {
        return this.guild != 0;
    }

    public void setGuild(int guild) {
        this.guild = guild;
    }

    public String getGuildName() {
        Guild guild;
        if (this.hasGuild() && (guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guild)) != null) {
            return guild.getName();
        }
        return "";
    }

    public boolean isPublicRoom() {
        return this.publicRoom;
    }

    public void setPublicRoom(boolean publicRoom) {
        this.publicRoom = publicRoom;
    }

    public boolean isStaffPromotedRoom() {
        return this.staffPromotedRoom;
    }

    public void setStaffPromotedRoom(boolean staffPromotedRoom) {
        this.staffPromotedRoom = staffPromotedRoom;
    }

    public boolean isAllowPets() {
        return this.allowPets;
    }

    public void setAllowPets(boolean allowPets) {
        this.allowPets = allowPets;
        if (!allowPets) {
            this.removeAllPets(this.ownerId);
        }
    }

    public boolean isAllowPetsEat() {
        return this.allowPetsEat;
    }

    public void setAllowPetsEat(boolean allowPetsEat) {
        this.allowPetsEat = allowPetsEat;
    }

    public boolean isAllowWalkthrough() {
        return this.allowWalkthrough;
    }

    public void setAllowWalkthrough(boolean allowWalkthrough) {
        this.allowWalkthrough = allowWalkthrough;
    }

    public boolean isAllowBotsWalk() {
        return this.allowBotsWalk;
    }

    public void setAllowBotsWalk(boolean allowBotsWalk) {
        this.allowBotsWalk = allowBotsWalk;
    }

    public boolean isAllowEffects() {
        return this.allowEffects;
    }

    public void setAllowEffects(boolean allowEffects) {
        this.allowEffects = allowEffects;
    }

    public boolean isHideWall() {
        return this.hideWall;
    }

    public void setHideWall(boolean hideWall) {
        this.hideWall = hideWall;
    }

    public Color getBackgroundTonerColor() {
        Color color = new Color(0, 0, 0);
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
        for (int i = this.roomItems.size(); i > 0; --i) {
            try {
                String[] extraData;
                iterator.advance();
                HabboItem object = iterator.value();
                if (!(object instanceof InteractionBackgroundToner) || (extraData = object.getExtradata().split(":")).length != 4 || !extraData[0].equalsIgnoreCase("1")) continue;
                return Color.getHSBColor(Integer.parseInt(extraData[1]), Integer.parseInt(extraData[2]), Integer.parseInt(extraData[3]));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return color;
    }

    public int getChatMode() {
        return this.chatMode;
    }

    public void setChatMode(int chatMode) {
        this.chatMode = chatMode;
    }

    public int getChatWeight() {
        return this.chatWeight;
    }

    public void setChatWeight(int chatWeight) {
        this.chatWeight = chatWeight;
    }

    public int getChatSpeed() {
        return this.chatSpeed;
    }

    public void setChatSpeed(int chatSpeed) {
        this.chatSpeed = chatSpeed;
    }

    public int getChatDistance() {
        return this.chatDistance;
    }

    public void setChatDistance(int chatDistance) {
        this.chatDistance = chatDistance;
    }

    public void removeAllPets() {
        this.removeAllPets(-1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAllPets(int excludeUserId) {
        ArrayList<Pet> toRemovePets = new ArrayList<Pet>();
        ArrayList<Pet> removedPets = new ArrayList<Pet>();
        Iterator iterator = this.currentPets;
        synchronized (iterator) {
            for (Pet pet : this.currentPets.valueCollection()) {
                try {
                    if (pet.getUserId() == excludeUserId) continue;
                    toRemovePets.add(pet);
                }
                catch (NoSuchElementException e) {
                    LOGGER.error("Caught exception", e);
                    break;
                }
            }
        }
        for (Pet pet : toRemovePets) {
            removedPets.add(pet);
            pet.removeFromRoom();
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(pet.getUserId());
            if (habbo != null) {
                habbo.getInventory().getPetsComponent().addPet(pet);
                habbo.getClient().sendResponse(new AddPetComposer(pet));
            }
            pet.needsUpdate = true;
            pet.run();
        }
        for (Pet pet : removedPets) {
            this.currentPets.remove(pet.getId());
        }
    }

    public int getChatProtection() {
        return this.chatProtection;
    }

    public void setChatProtection(int chatProtection) {
        this.chatProtection = chatProtection;
    }

    public int getMuteOption() {
        return this.muteOption;
    }

    public void setMuteOption(int muteOption) {
        this.muteOption = muteOption;
    }

    public int getKickOption() {
        return this.kickOption;
    }

    public void setKickOption(int kickOption) {
        this.kickOption = kickOption;
    }

    public int getBanOption() {
        return this.banOption;
    }

    public void setBanOption(int banOption) {
        this.banOption = banOption;
    }

    public int getPollId() {
        return this.pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public int getRollerSpeed() {
        return this.rollerSpeed;
    }

    public void setRollerSpeed(int rollerSpeed) {
        this.rollerSpeed = rollerSpeed;
        this.rollerCycle = 0L;
        this.needsUpdate = true;
    }

    public String[] filterAnything() {
        return new String[]{this.getOwnerName(), this.getGuildName(), this.getDescription(), this.getPromotionDesc()};
    }

    public long getCycleTimestamp() {
        return this.cycleTimestamp;
    }

    public boolean isPromoted() {
        this.promoted = this.promotion != null && this.promotion.getEndTimestamp() > Emulator.getIntUnixTimestamp();
        this.needsUpdate = true;
        return this.promoted;
    }

    public RoomPromotion getPromotion() {
        return this.promotion;
    }

    public String getPromotionDesc() {
        if (this.promotion != null) {
            return this.promotion.getDescription();
        }
        return "";
    }

    public void createPromotion(String title, String description, int category) {
        this.promoted = true;
        if (this.promotion == null) {
            this.promotion = new RoomPromotion(this, title, description, Emulator.getIntUnixTimestamp() + 7200, Emulator.getIntUnixTimestamp(), category);
        } else {
            this.promotion.setTitle(title);
            this.promotion.setDescription(description);
            this.promotion.setEndTimestamp(Emulator.getIntUnixTimestamp() + 7200);
            this.promotion.setCategory(category);
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO room_promotions (room_id, title, description, end_timestamp, start_timestamp, category) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE title = ?, description = ?, end_timestamp = ?, category = ?");){
            statement.setInt(1, this.id);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setInt(4, this.promotion.getEndTimestamp());
            statement.setInt(5, this.promotion.getStartTimestamp());
            statement.setInt(6, category);
            statement.setString(7, this.promotion.getTitle());
            statement.setString(8, this.promotion.getDescription());
            statement.setInt(9, this.promotion.getEndTimestamp());
            statement.setInt(10, this.promotion.getCategory());
            statement.execute();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        this.needsUpdate = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addGame(Game game) {
        ConcurrentSet<Game> concurrentSet = this.games;
        synchronized (concurrentSet) {
            return this.games.add(game);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean deleteGame(Game game) {
        game.stop();
        game.dispose();
        ConcurrentSet<Game> concurrentSet = this.games;
        synchronized (concurrentSet) {
            return this.games.remove(game);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Game getGame(Class<? extends Game> gameType) {
        if (gameType == null) {
            return null;
        }
        ConcurrentSet<Game> concurrentSet = this.games;
        synchronized (concurrentSet) {
            for (Game game : this.games) {
                if (!gameType.isInstance(game)) continue;
                return game;
            }
        }
        return null;
    }

    public Game getGameOrCreate(Class<? extends Game> gameType) {
        Game game = this.getGame(gameType);
        if (game == null) {
            try {
                game = gameType.getDeclaredConstructor(Room.class).newInstance(this);
                this.addGame(game);
            }
            catch (Exception e) {
                LOGGER.error("Error getting game {}", (Object)gameType.getName(), (Object)e);
            }
        }
        return game;
    }

    public ConcurrentSet<Game> getGames() {
        return this.games;
    }

    public int getUserCount() {
        return this.currentHabbos.size();
    }

    public ConcurrentHashMap<Integer, Habbo> getCurrentHabbos() {
        return this.currentHabbos;
    }

    public Collection<Habbo> getHabbos() {
        return this.currentHabbos.values();
    }

    public TIntObjectMap<Habbo> getHabboQueue() {
        return this.habboQueue;
    }

    public TIntObjectMap<String> getFurniOwnerNames() {
        return this.furniOwnerNames;
    }

    public String getFurniOwnerName(int userId) {
        return this.furniOwnerNames.get(userId);
    }

    public TIntIntMap getFurniOwnerCount() {
        return this.furniOwnerCount;
    }

    public TIntObjectMap<RoomMoodlightData> getMoodlightData() {
        return this.moodlightData;
    }

    public int getLastTimerReset() {
        return this.lastTimerReset;
    }

    public void setLastTimerReset(int lastTimerReset) {
        this.lastTimerReset = lastTimerReset;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToQueue(Habbo habbo) {
        TIntObjectMap<Habbo> tIntObjectMap = this.habboQueue;
        synchronized (tIntObjectMap) {
            this.habboQueue.put(habbo.getHabboInfo().getId(), habbo);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeFromQueue(Habbo habbo) {
        try {
            this.sendComposer(new HideDoorbellComposer(habbo.getHabboInfo().getUsername()).compose());
            TIntObjectMap<Habbo> tIntObjectMap = this.habboQueue;
            synchronized (tIntObjectMap) {
                return this.habboQueue.remove(habbo.getHabboInfo().getId()) != null;
            }
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
            return true;
        }
    }

    public TIntObjectMap<Bot> getCurrentBots() {
        return this.currentBots;
    }

    public TIntObjectMap<Pet> getCurrentPets() {
        return this.currentPets;
    }

    public THashSet<String> getWordFilterWords() {
        return this.wordFilterWords;
    }

    public RoomSpecialTypes getRoomSpecialTypes() {
        return this.roomSpecialTypes;
    }

    public boolean isPreLoaded() {
        return this.preLoaded;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public TIntArrayList getRights() {
        return this.rights;
    }

    public boolean isMuted() {
        return this.muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public TraxManager getTraxManager() {
        return this.traxManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addHabboItem(HabboItem item) {
        if (item == null) {
            return;
        }
        Object object = this.roomItems;
        synchronized (object) {
            try {
                this.roomItems.put(item.getId(), item);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        object = this.furniOwnerCount;
        synchronized (object) {
            this.furniOwnerCount.put(item.getUserId(), this.furniOwnerCount.get(item.getUserId()) + 1);
        }
        object = this.furniOwnerNames;
        synchronized (object) {
            if (!this.furniOwnerNames.containsKey(item.getUserId())) {
                HabboInfo habbo = HabboManager.getOfflineHabboInfo(item.getUserId());
                if (habbo != null) {
                    this.furniOwnerNames.put(item.getUserId(), habbo.getUsername());
                } else {
                    LOGGER.error("Failed to find username for item (ID: {}, UserID: {})", (Object)item.getId(), (Object)item.getUserId());
                }
            }
        }
        object = this.roomSpecialTypes;
        synchronized (object) {
            if (item instanceof ICycleable) {
                this.roomSpecialTypes.addCycleTask((ICycleable)((Object)item));
            }
            if (item instanceof InteractionWiredTrigger) {
                this.roomSpecialTypes.addTrigger((InteractionWiredTrigger)item);
            } else if (item instanceof InteractionWiredEffect) {
                this.roomSpecialTypes.addEffect((InteractionWiredEffect)item);
            } else if (item instanceof InteractionWiredCondition) {
                this.roomSpecialTypes.addCondition((InteractionWiredCondition)item);
            } else if (item instanceof InteractionWiredExtra) {
                this.roomSpecialTypes.addExtra((InteractionWiredExtra)item);
            } else if (item instanceof InteractionBattleBanzaiTeleporter) {
                this.roomSpecialTypes.addBanzaiTeleporter((InteractionBattleBanzaiTeleporter)item);
            } else if (item instanceof InteractionRoller) {
                this.roomSpecialTypes.addRoller((InteractionRoller)item);
            } else if (item instanceof InteractionGameScoreboard) {
                this.roomSpecialTypes.addGameScoreboard((InteractionGameScoreboard)item);
            } else if (item instanceof InteractionGameGate) {
                this.roomSpecialTypes.addGameGate((InteractionGameGate)item);
            } else if (item instanceof InteractionGameTimer) {
                this.roomSpecialTypes.addGameTimer((InteractionGameTimer)item);
            } else if (item instanceof InteractionFreezeExitTile) {
                this.roomSpecialTypes.addFreezeExitTile((InteractionFreezeExitTile)item);
            } else if (item instanceof InteractionNest) {
                this.roomSpecialTypes.addNest((InteractionNest)item);
            } else if (item instanceof InteractionPetDrink) {
                this.roomSpecialTypes.addPetDrink((InteractionPetDrink)item);
            } else if (item instanceof InteractionPetFood) {
                this.roomSpecialTypes.addPetFood((InteractionPetFood)item);
            } else if (item instanceof InteractionMoodLight) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionPyramid) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionMusicDisc) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionBattleBanzaiSphere) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTalkingFurniture) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionWater) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionWaterItem) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionMuteArea) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionBuildArea) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTagPole) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTagField) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionJukeBox) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionPetBreedingNest) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionBlackHole) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionWiredHighscore) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionStickyPole) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof WiredBlob) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTent) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionSnowboardSlope) {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionFireworks) {
                this.roomSpecialTypes.addUndefined(item);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HabboItem getHabboItem(int id) {
        HabboItem item;
        if (this.roomItems == null || this.roomSpecialTypes == null) {
            return null;
        }
        TIntObjectMap<HabboItem> tIntObjectMap = this.roomItems;
        synchronized (tIntObjectMap) {
            item = this.roomItems.get(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getBanzaiTeleporter(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getTrigger(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getEffect(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getCondition(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getGameGate(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getGameScorebord(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getGameTimer(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getFreezeExitTiles().get(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getRoller(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getNest(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getPetDrink(id);
        }
        if (item == null) {
            item = this.roomSpecialTypes.getPetFood(id);
        }
        return item;
    }

    void removeHabboItem(int id) {
        this.removeHabboItem(this.getHabboItem(id));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeHabboItem(HabboItem item) {
        if (item != null) {
            HabboItem i;
            Object object = this.roomItems;
            synchronized (object) {
                i = this.roomItems.remove(item.getId());
            }
            if (i != null) {
                object = this.furniOwnerCount;
                synchronized (object) {
                    TIntObjectMap<String> tIntObjectMap = this.furniOwnerNames;
                    synchronized (tIntObjectMap) {
                        int count = this.furniOwnerCount.get(i.getUserId());
                        if (count > 1) {
                            this.furniOwnerCount.put(i.getUserId(), count - 1);
                        } else {
                            this.furniOwnerCount.remove(i.getUserId());
                            this.furniOwnerNames.remove(i.getUserId());
                        }
                    }
                }
                if (item instanceof ICycleable) {
                    this.roomSpecialTypes.removeCycleTask((ICycleable)((Object)item));
                }
                if (item instanceof InteractionBattleBanzaiTeleporter) {
                    this.roomSpecialTypes.removeBanzaiTeleporter((InteractionBattleBanzaiTeleporter)item);
                } else if (item instanceof InteractionWiredTrigger) {
                    this.roomSpecialTypes.removeTrigger((InteractionWiredTrigger)item);
                } else if (item instanceof InteractionWiredEffect) {
                    this.roomSpecialTypes.removeEffect((InteractionWiredEffect)item);
                } else if (item instanceof InteractionWiredCondition) {
                    this.roomSpecialTypes.removeCondition((InteractionWiredCondition)item);
                } else if (item instanceof InteractionWiredExtra) {
                    this.roomSpecialTypes.removeExtra((InteractionWiredExtra)item);
                } else if (item instanceof InteractionRoller) {
                    this.roomSpecialTypes.removeRoller((InteractionRoller)item);
                } else if (item instanceof InteractionGameScoreboard) {
                    this.roomSpecialTypes.removeScoreboard((InteractionGameScoreboard)item);
                } else if (item instanceof InteractionGameGate) {
                    this.roomSpecialTypes.removeGameGate((InteractionGameGate)item);
                } else if (item instanceof InteractionGameTimer) {
                    this.roomSpecialTypes.removeGameTimer((InteractionGameTimer)item);
                } else if (item instanceof InteractionFreezeExitTile) {
                    this.roomSpecialTypes.removeFreezeExitTile((InteractionFreezeExitTile)item);
                } else if (item instanceof InteractionNest) {
                    this.roomSpecialTypes.removeNest((InteractionNest)item);
                } else if (item instanceof InteractionPetDrink) {
                    this.roomSpecialTypes.removePetDrink((InteractionPetDrink)item);
                } else if (item instanceof InteractionPetFood) {
                    this.roomSpecialTypes.removePetFood((InteractionPetFood)item);
                } else if (item instanceof InteractionMoodLight) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionPyramid) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionMusicDisc) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionBattleBanzaiSphere) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionTalkingFurniture) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionWaterItem) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionWater) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionMuteArea) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionTagPole) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionTagField) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionJukeBox) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionPetBreedingNest) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionBlackHole) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionWiredHighscore) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionStickyPole) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof WiredBlob) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionTent) {
                    this.roomSpecialTypes.removeUndefined(item);
                } else if (item instanceof InteractionSnowboardSlope) {
                    this.roomSpecialTypes.removeUndefined(item);
                }
            }
        }
    }

    public THashSet<HabboItem> getFloorItems() {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
        int i = this.roomItems.size();
        while (i-- > 0) {
            try {
                iterator.advance();
            }
            catch (Exception e) {
                break;
            }
            if (iterator.value().getBaseItem().getType() != FurnitureType.FLOOR) continue;
            items.add(iterator.value());
        }
        return items;
    }

    public THashSet<HabboItem> getWallItems() {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
        int i = this.roomItems.size();
        while (i-- > 0) {
            try {
                iterator.advance();
            }
            catch (Exception e) {
                break;
            }
            if (iterator.value().getBaseItem().getType() != FurnitureType.WALL) continue;
            items.add(iterator.value());
        }
        return items;
    }

    public THashSet<HabboItem> getPostItNotes() {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
        int i = this.roomItems.size();
        while (i-- > 0) {
            try {
                iterator.advance();
            }
            catch (Exception e) {
                break;
            }
            if (iterator.value().getBaseItem().getInteractionType().getType() != InteractionPostIt.class) continue;
            items.add(iterator.value());
        }
        return items;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addHabbo(Habbo habbo) {
        Object object = this.roomUnitLock;
        synchronized (object) {
            habbo.getRoomUnit().setId(this.unitCounter);
            this.currentHabbos.put(habbo.getHabboInfo().getId(), habbo);
            ++this.unitCounter;
            this.updateDatabaseUserCount();
        }
    }

    public void kickHabbo(Habbo habbo, boolean alert) {
        if (alert) {
            habbo.getClient().sendResponse(new GenericErrorMessagesComposer(4008));
        }
        habbo.getRoomUnit().isKicked = true;
        habbo.getRoomUnit().setGoalLocation(this.layout.getDoorTile());
        if (habbo.getRoomUnit().getPath() == null || habbo.getRoomUnit().getPath().size() <= 1 || this.isPublicRoom()) {
            habbo.getRoomUnit().setCanWalk(true);
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this);
        }
    }

    public void removeHabbo(Habbo habbo) {
        this.removeHabbo(habbo, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeHabbo(Habbo habbo, boolean sendRemovePacket) {
        RoomTrade trade;
        HabboItem item;
        if (habbo.getRoomUnit() != null && habbo.getRoomUnit().getCurrentLocation() != null) {
            habbo.getRoomUnit().getCurrentLocation().removeUnit(habbo.getRoomUnit());
        }
        Object object = this.roomUnitLock;
        synchronized (object) {
            this.currentHabbos.remove(habbo.getHabboInfo().getId());
        }
        if (sendRemovePacket && habbo.getRoomUnit() != null && !habbo.getRoomUnit().isTeleporting) {
            this.sendComposer(new RoomUserRemoveComposer(habbo.getRoomUnit()).compose());
        }
        if (habbo.getRoomUnit().getCurrentLocation() != null && (item = this.getTopItemAt(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY())) != null) {
            try {
                item.onWalkOff(habbo.getRoomUnit(), this, new Object[0]);
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
        if (habbo.getHabboInfo().getCurrentGame() != null && this.getGame(habbo.getHabboInfo().getCurrentGame()) != null) {
            this.getGame(habbo.getHabboInfo().getCurrentGame()).removeHabbo(habbo);
        }
        if ((trade = this.getActiveTradeForHabbo(habbo)) != null) {
            trade.stopTrade(habbo);
        }
        if (habbo.getHabboInfo().getId() != this.ownerId) {
            this.pickupPetsForHabbo(habbo);
        }
        this.updateDatabaseUserCount();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addBot(Bot bot) {
        Object object = this.roomUnitLock;
        synchronized (object) {
            bot.getRoomUnit().setId(this.unitCounter);
            this.currentBots.put(bot.getId(), bot);
            ++this.unitCounter;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPet(Pet pet) {
        Object object = this.roomUnitLock;
        synchronized (object) {
            pet.getRoomUnit().setId(this.unitCounter);
            this.currentPets.put(pet.getId(), pet);
            ++this.unitCounter;
            Habbo habbo = this.getHabbo(pet.getUserId());
            if (habbo != null) {
                this.furniOwnerNames.put(pet.getUserId(), this.getHabbo(pet.getUserId()).getHabboInfo().getUsername());
            }
        }
    }

    public Bot getBot(int botId) {
        return this.currentBots.get(botId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Bot getBot(RoomUnit roomUnit) {
        TIntObjectMap<Bot> tIntObjectMap = this.currentBots;
        synchronized (tIntObjectMap) {
            TIntObjectIterator<Bot> iterator = this.currentBots.iterator();
            int i = this.currentBots.size();
            while (i-- > 0) {
                try {
                    iterator.advance();
                }
                catch (NoSuchElementException e) {
                    LOGGER.error("Caught exception", e);
                    break;
                }
                if (iterator.value().getRoomUnit() != roomUnit) continue;
                return iterator.value();
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Bot getBotByRoomUnitId(int id) {
        TIntObjectMap<Bot> tIntObjectMap = this.currentBots;
        synchronized (tIntObjectMap) {
            TIntObjectIterator<Bot> iterator = this.currentBots.iterator();
            int i = this.currentBots.size();
            while (i-- > 0) {
                try {
                    iterator.advance();
                }
                catch (NoSuchElementException e) {
                    LOGGER.error("Caught exception", e);
                    break;
                }
                if (iterator.value().getRoomUnit().getId() != id) continue;
                return iterator.value();
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Bot> getBots(String name) {
        ArrayList<Bot> bots = new ArrayList<Bot>();
        TIntObjectMap<Bot> tIntObjectMap = this.currentBots;
        synchronized (tIntObjectMap) {
            TIntObjectIterator<Bot> iterator = this.currentBots.iterator();
            int i = this.currentBots.size();
            while (i-- > 0) {
                try {
                    iterator.advance();
                }
                catch (NoSuchElementException e) {
                    LOGGER.error("Caught exception", e);
                    break;
                }
                if (!iterator.value().getName().equalsIgnoreCase(name)) continue;
                bots.add(iterator.value());
            }
        }
        return bots;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasBotsAt(final int x, final int y) {
        final boolean[] result = new boolean[]{false};
        TIntObjectMap<Bot> tIntObjectMap = this.currentBots;
        synchronized (tIntObjectMap) {
            this.currentBots.forEachValue(new TObjectProcedure<Bot>(){
                final /* synthetic */ Room this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean execute(Bot object) {
                    if (object.getRoomUnit().getX() == x && object.getRoomUnit().getY() == y) {
                        result[0] = true;
                        return false;
                    }
                    return true;
                }
            });
        }
        return result[0];
    }

    public Pet getPet(int petId) {
        return this.currentPets.get(petId);
    }

    public Pet getPet(RoomUnit roomUnit) {
        TIntObjectIterator<Pet> petIterator = this.currentPets.iterator();
        int i = this.currentPets.size();
        while (i-- > 0) {
            try {
                petIterator.advance();
            }
            catch (NoSuchElementException e) {
                LOGGER.error("Caught exception", e);
                break;
            }
            if (petIterator.value().getRoomUnit() != roomUnit) continue;
            return petIterator.value();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeBot(Bot bot) {
        TIntObjectMap<Bot> tIntObjectMap = this.currentBots;
        synchronized (tIntObjectMap) {
            if (this.currentBots.containsKey(bot.getId())) {
                if (bot.getRoomUnit() != null && bot.getRoomUnit().getCurrentLocation() != null) {
                    bot.getRoomUnit().getCurrentLocation().removeUnit(bot.getRoomUnit());
                }
                this.currentBots.remove(bot.getId());
                bot.getRoomUnit().setInRoom(false);
                bot.setRoom(null);
                this.sendComposer(new RoomUserRemoveComposer(bot.getRoomUnit()).compose());
                bot.setRoomUnit(null);
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void placePet(Pet pet, short x, short y, double z, int rot) {
        TIntObjectMap<Pet> tIntObjectMap = this.currentPets;
        synchronized (tIntObjectMap) {
            RoomTile tile = this.layout.getTile(x, y);
            if (tile == null) {
                tile = this.layout.getDoorTile();
            }
            pet.setRoomUnit(new RoomUnit());
            pet.setRoom(this);
            pet.getRoomUnit().setGoalLocation(tile);
            pet.getRoomUnit().setLocation(tile);
            pet.getRoomUnit().setRoomUnitType(RoomUnitType.PET);
            pet.getRoomUnit().setCanWalk(true);
            pet.getRoomUnit().setPathFinderRoom(this);
            pet.getRoomUnit().setPreviousLocationZ(z);
            pet.getRoomUnit().setZ(z);
            if (pet.getRoomUnit().getCurrentLocation() == null) {
                pet.getRoomUnit().setLocation(this.getLayout().getDoorTile());
                pet.getRoomUnit().setRotation(RoomUserRotation.fromValue(this.getLayout().getDoorDirection()));
            }
            pet.needsUpdate = true;
            this.furniOwnerNames.put(pet.getUserId(), this.getHabbo(pet.getUserId()).getHabboInfo().getUsername());
            this.addPet(pet);
            this.sendComposer(new RoomPetComposer(pet).compose());
        }
    }

    public Pet removePet(int petId) {
        return this.currentPets.remove(petId);
    }

    public boolean hasHabbosAt(int x, int y) {
        for (Habbo habbo : this.getHabbos()) {
            if (habbo.getRoomUnit().getX() != x || habbo.getRoomUnit().getY() != y) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasPetsAt(int x, int y) {
        TIntObjectMap<Pet> tIntObjectMap = this.currentPets;
        synchronized (tIntObjectMap) {
            TIntObjectIterator<Pet> petIterator = this.currentPets.iterator();
            int i = this.currentPets.size();
            while (i-- > 0) {
                try {
                    petIterator.advance();
                }
                catch (NoSuchElementException e) {
                    LOGGER.error("Caught exception", e);
                    break;
                }
                if (petIterator.value().getRoomUnit().getX() != x || petIterator.value().getRoomUnit().getY() != y) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<Bot> getBotsAt(RoomTile tile) {
        THashSet<Bot> bots = new THashSet<Bot>();
        TIntObjectMap<Bot> tIntObjectMap = this.currentBots;
        synchronized (tIntObjectMap) {
            TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();
            int i = this.currentBots.size();
            while (i-- > 0) {
                try {
                    botIterator.advance();
                    if (!botIterator.value().getRoomUnit().getCurrentLocation().equals(tile)) continue;
                    bots.add(botIterator.value());
                }
                catch (Exception e) {
                    // empty catch block
                    break;
                }
            }
        }
        return bots;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<Pet> getPetsAt(RoomTile tile) {
        THashSet<Pet> pets = new THashSet<Pet>();
        TIntObjectMap<Pet> tIntObjectMap = this.currentPets;
        synchronized (tIntObjectMap) {
            TIntObjectIterator<Pet> petIterator = this.currentPets.iterator();
            int i = this.currentPets.size();
            while (i-- > 0) {
                try {
                    petIterator.advance();
                    if (!petIterator.value().getRoomUnit().getCurrentLocation().equals(tile)) continue;
                    pets.add(petIterator.value());
                }
                catch (Exception e) {
                    // empty catch block
                    break;
                }
            }
        }
        return pets;
    }

    public THashSet<Habbo> getHabbosAt(short x, short y) {
        return this.getHabbosAt(this.layout.getTile(x, y));
    }

    public THashSet<Habbo> getHabbosAt(RoomTile tile) {
        THashSet<Habbo> habbos = new THashSet<Habbo>();
        for (Habbo habbo : this.getHabbos()) {
            if (!habbo.getRoomUnit().getCurrentLocation().equals(tile)) continue;
            habbos.add(habbo);
        }
        return habbos;
    }

    public THashSet<RoomUnit> getHabbosAndBotsAt(short x, short y) {
        return this.getHabbosAndBotsAt(this.layout.getTile(x, y));
    }

    public THashSet<RoomUnit> getHabbosAndBotsAt(RoomTile tile) {
        THashSet<RoomUnit> list = new THashSet<RoomUnit>();
        for (Bot bot : this.getBotsAt(tile)) {
            list.add(bot.getRoomUnit());
        }
        for (Habbo habbo : this.getHabbosAt(tile)) {
            list.add(habbo.getRoomUnit());
        }
        return list;
    }

    public THashSet<Habbo> getHabbosOnItem(HabboItem item) {
        THashSet<Habbo> habbos = new THashSet<Habbo>();
        for (short x = item.getX(); x < item.getX() + item.getBaseItem().getLength(); x = (short)(x + 1)) {
            for (short y = item.getY(); y < item.getY() + item.getBaseItem().getWidth(); y = (short)(y + 1)) {
                habbos.addAll(this.getHabbosAt(x, y));
            }
        }
        return habbos;
    }

    public THashSet<Bot> getBotsOnItem(HabboItem item) {
        THashSet<Bot> bots = new THashSet<Bot>();
        for (short x = item.getX(); x < item.getX() + item.getBaseItem().getLength(); x = (short)(x + 1)) {
            for (short y = item.getY(); y < item.getY() + item.getBaseItem().getWidth(); y = (short)(y + 1)) {
                bots.addAll(this.getBotsAt(this.getLayout().getTile(x, y)));
            }
        }
        return bots;
    }

    public void teleportHabboToItem(Habbo habbo, HabboItem item) {
        this.teleportRoomUnitToLocation(habbo.getRoomUnit(), item.getX(), item.getY(), item.getZ() + Item.getCurrentHeight(item));
    }

    public void teleportHabboToLocation(Habbo habbo, short x, short y) {
        this.teleportRoomUnitToLocation(habbo.getRoomUnit(), x, y, 0.0);
    }

    public void teleportRoomUnitToItem(RoomUnit roomUnit, HabboItem item) {
        this.teleportRoomUnitToLocation(roomUnit, item.getX(), item.getY(), item.getZ() + Item.getCurrentHeight(item));
    }

    public void teleportRoomUnitToLocation(RoomUnit roomUnit, short x, short y) {
        this.teleportRoomUnitToLocation(roomUnit, x, y, 0.0);
    }

    public void teleportRoomUnitToLocation(RoomUnit roomUnit, short x, short y, double z) {
        if (this.loaded) {
            RoomTile tile = this.layout.getTile(x, y);
            if (z < (double)tile.z) {
                z = tile.z;
            }
            roomUnit.setLocation(tile);
            roomUnit.setGoalLocation(tile);
            roomUnit.setZ(z);
            roomUnit.setPreviousLocationZ(z);
            this.updateRoomUnit(roomUnit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void muteHabbo(Habbo habbo, int minutes) {
        TIntIntHashMap tIntIntHashMap = this.mutedHabbos;
        synchronized (tIntIntHashMap) {
            this.mutedHabbos.put(habbo.getHabboInfo().getId(), Emulator.getIntUnixTimestamp() + minutes * 60);
        }
    }

    public boolean isMuted(Habbo habbo) {
        if (this.isOwner(habbo) || this.hasRights(habbo)) {
            return false;
        }
        if (this.mutedHabbos.containsKey(habbo.getHabboInfo().getId())) {
            boolean time;
            boolean bl = time = this.mutedHabbos.get(habbo.getHabboInfo().getId()) > Emulator.getIntUnixTimestamp();
            if (!time) {
                this.mutedHabbos.remove(habbo.getHabboInfo().getId());
            }
            return time;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void habboEntered(Habbo habbo) {
        habbo.getRoomUnit().animateWalk = false;
        TIntObjectMap<Bot> tIntObjectMap = this.currentBots;
        synchronized (tIntObjectMap) {
            if (habbo.getHabboInfo().getId() != this.getOwnerId()) {
                return;
            }
            TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();
            int i = this.currentBots.size();
            while (i-- > 0) {
                try {
                    botIterator.advance();
                    if (!(botIterator.value() instanceof VisitorBot)) continue;
                    ((VisitorBot)botIterator.value()).onUserEnter(habbo);
                    break;
                }
                catch (Exception e) {
                    // empty catch block
                    break;
                }
            }
        }
        HabboItem doorTileTopItem = this.getTopItemAt(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY());
        if (doorTileTopItem != null && !(doorTileTopItem instanceof InteractionTeleportTile)) {
            try {
                doorTileTopItem.onWalkOn(habbo.getRoomUnit(), this, new Object[0]);
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
    }

    public void floodMuteHabbo(Habbo habbo, int timeOut) {
        ++habbo.getHabboStats().mutedCount;
        timeOut += timeOut * (int)Math.ceil(Math.pow(habbo.getHabboStats().mutedCount, 2.0));
        habbo.getHabboStats().chatCounter.set(0);
        habbo.mute(timeOut, true);
    }

    public void talk(Habbo habbo, RoomChatMessage roomChatMessage, RoomChatType chatType) {
        this.talk(habbo, roomChatMessage, chatType, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void talk(Habbo habbo, RoomChatMessage roomChatMessage, RoomChatType chatType, boolean ignoreWired) {
        Object message;
        UsernameTalkEvent usernameTalkEvent;
        int chatCounter;
        if (!habbo.getHabboStats().allowTalk()) {
            return;
        }
        if (habbo.getRoomUnit().isInvisible() && Emulator.getConfig().getBoolean("invisible.prevent.chat", false)) {
            if (!CommandHandler.handleCommand(habbo.getClient(), roomChatMessage.getUnfilteredMessage())) {
                habbo.whisper(Emulator.getTexts().getValue("invisible.prevent.chat.error"));
            }
            return;
        }
        if (habbo.getHabboInfo().getCurrentRoom() != this) {
            return;
        }
        long millis = System.currentTimeMillis();
        if (HABBO_CHAT_DELAY && millis - habbo.getHabboStats().lastChat < 750L) {
            return;
        }
        habbo.getHabboStats().lastChat = millis;
        if (roomChatMessage != null && Emulator.getConfig().getBoolean("easter_eggs.enabled") && roomChatMessage.getMessage().equalsIgnoreCase("i am a pirate")) {
            habbo.getHabboStats().chatCounter.addAndGet(1);
            Emulator.getThreading().run(new YouAreAPirate(habbo, this));
            return;
        }
        UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.TALKED, false);
        Emulator.getPluginManager().fireEvent(event);
        if (!event.isCancelled() && !event.idle) {
            this.unIdle(habbo);
        }
        this.sendComposer(new RoomUserTypingComposer(habbo.getRoomUnit(), false).compose());
        if (roomChatMessage == null || roomChatMessage.getMessage() == null || roomChatMessage.getMessage().equals("")) {
            return;
        }
        if (!(habbo.hasPermission(Permission.ACC_NOMUTE) || MUTEAREA_CAN_WHISPER && chatType == RoomChatType.WHISPER)) {
            for (HabboItem area : this.getRoomSpecialTypes().getItemsOfType(InteractionMuteArea.class)) {
                if (!((InteractionMuteArea)area).inSquare(habbo.getRoomUnit().getCurrentLocation())) continue;
                return;
            }
        }
        if (!this.wordFilterWords.isEmpty() && !habbo.hasPermission(Permission.ACC_CHAT_NO_FILTER)) {
            for (String string : this.wordFilterWords) {
                roomChatMessage.setMessage(roomChatMessage.getMessage().replaceAll("(?i)" + Pattern.quote(string), "bobba"));
            }
        }
        if (!habbo.hasPermission(Permission.ACC_NOMUTE)) {
            if (this.isMuted() && !this.hasRights(habbo)) {
                return;
            }
            if (this.isMuted(habbo)) {
                habbo.getClient().sendResponse(new MutedWhisperComposer(this.mutedHabbos.get(habbo.getHabboInfo().getId()) - Emulator.getIntUnixTimestamp()));
                return;
            }
        }
        if (!habbo.hasPermission(Permission.ACC_CHAT_NO_FLOOD) && (chatCounter = habbo.getHabboStats().chatCounter.addAndGet(1)) > 3) {
            boolean floodRights = Emulator.getConfig().getBoolean("flood.with.rights");
            boolean hasRights = this.hasRights(habbo);
            if (floodRights || !hasRights) {
                this.floodMuteHabbo(habbo, this.muteTime);
                return;
            }
        }
        if (chatType != RoomChatType.WHISPER) {
            if (CommandHandler.handleCommand(habbo.getClient(), roomChatMessage.getUnfilteredMessage())) {
                WiredHandler.handle(WiredTriggerType.SAY_COMMAND, habbo.getRoomUnit(), habbo.getHabboInfo().getCurrentRoom(), new Object[]{roomChatMessage.getMessage()});
                roomChatMessage.isCommand = true;
                return;
            }
            if (!ignoreWired && WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, habbo.getRoomUnit(), habbo.getHabboInfo().getCurrentRoom(), new Object[]{roomChatMessage.getMessage()})) {
                habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(roomChatMessage.getMessage(), habbo, habbo, roomChatMessage.getBubble())));
                return;
            }
        }
        ServerMessage prefixMessage = null;
        if (Emulator.getPluginManager().isRegistered(UsernameTalkEvent.class, true) && (usernameTalkEvent = Emulator.getPluginManager().fireEvent(new UsernameTalkEvent(habbo, roomChatMessage, chatType))).hasCustomComposer()) {
            prefixMessage = usernameTalkEvent.getCustomComposer();
        }
        if (prefixMessage == null) {
            prefixMessage = roomChatMessage.getHabbo().getHabboInfo().getRank().hasPrefix() ? new RoomUserNameChangedComposer(habbo, true).compose() : null;
        }
        ServerMessage clearPrefixMessage = prefixMessage != null ? new RoomUserNameChangedComposer(habbo).compose() : null;
        Rectangle tentRectangle = this.roomSpecialTypes.tentAt(habbo.getRoomUnit().getCurrentLocation());
        String trimmedMessage = roomChatMessage.getMessage().replaceAll("\\s+$", "");
        if (trimmedMessage.isEmpty()) {
            trimmedMessage = " ";
        }
        roomChatMessage.setMessage(trimmedMessage);
        if (chatType == RoomChatType.WHISPER) {
            if (roomChatMessage.getTargetHabbo() == null) {
                return;
            }
            RoomChatMessage staffChatMessage = new RoomChatMessage(roomChatMessage);
            staffChatMessage.setMessage("To " + staffChatMessage.getTargetHabbo().getHabboInfo().getUsername() + ": " + staffChatMessage.getMessage());
            ServerMessage message2 = new RoomUserWhisperComposer(roomChatMessage).compose();
            ServerMessage staffMessage = new RoomUserWhisperComposer(staffChatMessage).compose();
            for (Habbo h : this.getHabbos()) {
                if (h == roomChatMessage.getTargetHabbo() || h == habbo) {
                    if (h.getHabboStats().userIgnored(habbo.getHabboInfo().getId())) continue;
                    if (prefixMessage != null) {
                        h.getClient().sendResponse(prefixMessage);
                    }
                    h.getClient().sendResponse(message2);
                    if (clearPrefixMessage == null) continue;
                    h.getClient().sendResponse(clearPrefixMessage);
                    continue;
                }
                if (!h.hasPermission(Permission.ACC_SEE_WHISPERS)) continue;
                h.getClient().sendResponse(staffMessage);
            }
        } else if (chatType == RoomChatType.TALK) {
            message = new RoomUserTalkComposer(roomChatMessage).compose();
            boolean noChatLimit = habbo.hasPermission(Permission.ACC_CHAT_NO_LIMIT);
            for (Habbo h : this.getHabbos()) {
                if ((h.getRoomUnit().getCurrentLocation().distance(habbo.getRoomUnit().getCurrentLocation()) <= (double)this.chatDistance || h.equals(habbo) || this.hasRights(h) || noChatLimit) && (tentRectangle == null || RoomLayout.tileInSquare(tentRectangle, h.getRoomUnit().getCurrentLocation()))) {
                    if (h.getHabboStats().userIgnored(habbo.getHabboInfo().getId())) continue;
                    if (prefixMessage != null && !h.getHabboStats().preferOldChat) {
                        h.getClient().sendResponse(prefixMessage);
                    }
                    h.getClient().sendResponse((ServerMessage)message);
                    if (clearPrefixMessage == null || h.getHabboStats().preferOldChat) continue;
                    h.getClient().sendResponse(clearPrefixMessage);
                    continue;
                }
                this.showTentChatMessageOutsideTentIfPermitted(h, roomChatMessage, tentRectangle);
            }
        } else if (chatType == RoomChatType.SHOUT) {
            message = new RoomUserShoutComposer(roomChatMessage).compose();
            for (Habbo h : this.getHabbos()) {
                if (!h.getHabboStats().userIgnored(habbo.getHabboInfo().getId()) && (tentRectangle == null || RoomLayout.tileInSquare(tentRectangle, h.getRoomUnit().getCurrentLocation()))) {
                    if (prefixMessage != null && !h.getHabboStats().preferOldChat) {
                        h.getClient().sendResponse(prefixMessage);
                    }
                    h.getClient().sendResponse((ServerMessage)message);
                    if (clearPrefixMessage == null || h.getHabboStats().preferOldChat) continue;
                    h.getClient().sendResponse(clearPrefixMessage);
                    continue;
                }
                this.showTentChatMessageOutsideTentIfPermitted(h, roomChatMessage, tentRectangle);
            }
        }
        if (chatType == RoomChatType.TALK || chatType == RoomChatType.SHOUT) {
            message = this.currentBots;
            synchronized (message) {
                TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();
                int i = this.currentBots.size();
                while (i-- > 0) {
                    try {
                        botIterator.advance();
                        Bot bot = botIterator.value();
                        bot.onUserSay(roomChatMessage);
                    }
                    catch (NoSuchElementException e) {
                        LOGGER.error("Caught exception", e);
                        break;
                    }
                }
            }
            if (roomChatMessage.getBubble().triggersTalkingFurniture()) {
                THashSet<HabboItem> items = this.roomSpecialTypes.getItemsOfType(InteractionTalkingFurniture.class);
                for (HabboItem item : items) {
                    int count;
                    if (!(this.layout.getTile(item.getX(), item.getY()).distance(habbo.getRoomUnit().getCurrentLocation()) <= (double)Emulator.getConfig().getInt("furniture.talking.range")) || (count = Emulator.getConfig().getInt(item.getBaseItem().getName() + ".message.count", 0)) <= 0) continue;
                    int randomValue = Emulator.getRandom().nextInt(count + 1);
                    RoomChatMessage itemMessage = new RoomChatMessage(Emulator.getTexts().getValue(item.getBaseItem().getName() + ".message." + randomValue, item.getBaseItem().getName() + ".message." + randomValue + " not found!"), habbo, RoomChatMessageBubbles.getBubble(Emulator.getConfig().getInt(item.getBaseItem().getName() + ".message.bubble", RoomChatMessageBubbles.PARROT.getType())));
                    this.sendComposer(new RoomUserTalkComposer(itemMessage).compose());
                    try {
                        item.onClick(habbo.getClient(), this, new Object[0]);
                        item.setExtradata("1");
                        this.updateItemState(item);
                        Emulator.getThreading().run(() -> {
                            item.setExtradata("0");
                            this.updateItemState(item);
                        }, 2000L);
                        break;
                    }
                    catch (Exception e) {
                        LOGGER.error("Caught exception", e);
                    }
                }
            }
        }
    }

    private void showTentChatMessageOutsideTentIfPermitted(Habbo receivingHabbo, RoomChatMessage roomChatMessage, Rectangle tentRectangle) {
        if (receivingHabbo != null && receivingHabbo.hasPermission(Permission.ACC_SEE_TENTCHAT) && tentRectangle != null && !RoomLayout.tileInSquare(tentRectangle, receivingHabbo.getRoomUnit().getCurrentLocation())) {
            RoomChatMessage staffChatMessage = new RoomChatMessage(roomChatMessage);
            staffChatMessage.setMessage("[" + Emulator.getTexts().getValue("hotel.room.tent.prefix") + "] " + staffChatMessage.getMessage());
            ServerMessage staffMessage = new RoomUserWhisperComposer(staffChatMessage).compose();
            receivingHabbo.getClient().sendResponse(staffMessage);
        }
    }

    public THashSet<RoomTile> getLockedTiles() {
        THashSet<RoomTile> lockedTiles = new THashSet<RoomTile>();
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
        int i = this.roomItems.size();
        while (i-- > 0) {
            RoomTile tile;
            HabboItem item;
            try {
                iterator.advance();
                item = iterator.value();
            }
            catch (Exception e) {
                break;
            }
            if (item.getBaseItem().getType() != FurnitureType.FLOOR) continue;
            boolean found = false;
            for (RoomTile tile2 : lockedTiles) {
                if (tile2.x != item.getX() || tile2.y != item.getY()) continue;
                found = true;
                break;
            }
            if (found) continue;
            if (item.getRotation() == 0 || item.getRotation() == 4) {
                for (int y = 0; y < item.getBaseItem().getLength(); y = (int)((short)(y + 1))) {
                    for (int x = 0; x < item.getBaseItem().getWidth(); x = (int)((short)(x + 1))) {
                        tile = this.layout.getTile((short)(item.getX() + x), (short)(item.getY() + y));
                        if (tile == null) continue;
                        lockedTiles.add(tile);
                    }
                }
                continue;
            }
            for (int y = 0; y < item.getBaseItem().getWidth(); y = (int)((short)(y + 1))) {
                for (int x = 0; x < item.getBaseItem().getLength(); x = (int)((short)(x + 1))) {
                    tile = this.layout.getTile((short)(item.getX() + x), (short)(item.getY() + y));
                    if (tile == null) continue;
                    lockedTiles.add(tile);
                }
            }
        }
        return lockedTiles;
    }

    @Deprecated
    public THashSet<HabboItem> getItemsAt(int x, int y) {
        RoomTile tile = this.getLayout().getTile((short)x, (short)y);
        if (tile != null) {
            return this.getItemsAt(tile);
        }
        return new THashSet<HabboItem>(0);
    }

    public THashSet<HabboItem> getItemsAt(RoomTile tile) {
        return this.getItemsAt(tile, false);
    }

    public THashSet<HabboItem> getItemsAt(RoomTile tile, boolean returnOnFirst) {
        THashSet<HabboItem> cachedItems;
        THashSet<HabboItem> items = new THashSet<HabboItem>(0);
        if (tile == null) {
            return items;
        }
        if (this.loaded && (cachedItems = this.tileCache.get(tile)) != null) {
            return cachedItems;
        }
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
        int i = this.roomItems.size();
        while (i-- > 0) {
            int length;
            int width;
            HabboItem item;
            try {
                iterator.advance();
                item = iterator.value();
            }
            catch (Exception e) {
                break;
            }
            if (item == null || item.getBaseItem().getType() != FurnitureType.FLOOR) continue;
            if (item.getRotation() != 2 && item.getRotation() != 6) {
                width = item.getBaseItem().getWidth() > 0 ? item.getBaseItem().getWidth() : 1;
                length = item.getBaseItem().getLength() > 0 ? item.getBaseItem().getLength() : 1;
            } else {
                width = item.getBaseItem().getLength() > 0 ? item.getBaseItem().getLength() : 1;
                int n = length = item.getBaseItem().getWidth() > 0 ? item.getBaseItem().getWidth() : 1;
            }
            if (tile.x < item.getX() || tile.x > item.getX() + width - 1 || tile.y < item.getY() || tile.y > item.getY() + length - 1) continue;
            items.add(item);
            if (!returnOnFirst) continue;
            return items;
        }
        if (this.loaded) {
            this.tileCache.put(tile, items);
        }
        return items;
    }

    public THashSet<HabboItem> getItemsAt(int x, int y, double minZ) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        for (HabboItem item : this.getItemsAt(x, y)) {
            if (item.getZ() < minZ) continue;
            items.add(item);
        }
        return items;
    }

    public THashSet<HabboItem> getItemsAt(Class<? extends HabboItem> type, int x, int y) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        for (HabboItem item : this.getItemsAt(x, y)) {
            if (!item.getClass().equals(type)) continue;
            items.add(item);
        }
        return items;
    }

    public boolean hasItemsAt(int x, int y) {
        RoomTile tile = this.getLayout().getTile((short)x, (short)y);
        if (tile == null) {
            return false;
        }
        return this.getItemsAt(tile, true).size() > 0;
    }

    public HabboItem getTopItemAt(int x, int y) {
        return this.getTopItemAt(x, y, null);
    }

    public HabboItem getTopItemAt(int x, int y, HabboItem exclude) {
        RoomTile tile = this.getLayout().getTile((short)x, (short)y);
        if (tile == null) {
            return null;
        }
        HabboItem highestItem = null;
        for (HabboItem item : this.getItemsAt(x, y)) {
            if (exclude != null && exclude == item || highestItem != null && highestItem.getZ() + Item.getCurrentHeight(highestItem) > item.getZ() + Item.getCurrentHeight(item)) continue;
            highestItem = item;
        }
        return highestItem;
    }

    public HabboItem getTopItemAt(THashSet<RoomTile> tiles, HabboItem exclude) {
        HabboItem highestItem = null;
        for (RoomTile tile : tiles) {
            if (tile == null) continue;
            for (HabboItem item : this.getItemsAt(tile.x, tile.y)) {
                if (exclude != null && exclude == item || highestItem != null && highestItem.getZ() + Item.getCurrentHeight(highestItem) > item.getZ() + Item.getCurrentHeight(item)) continue;
                highestItem = item;
            }
        }
        return highestItem;
    }

    public double getTopHeightAt(int x, int y) {
        HabboItem item = this.getTopItemAt(x, y);
        if (item != null) {
            return item.getZ() + Item.getCurrentHeight(item) - (double)(item.getBaseItem().allowSit() ? 1 : 0);
        }
        return this.layout.getHeightAtSquare(x, y);
    }

    @Deprecated
    public HabboItem getLowestChair(int x, int y) {
        if (this.layout == null) {
            return null;
        }
        RoomTile tile = this.layout.getTile((short)x, (short)y);
        if (tile != null) {
            return this.getLowestChair(tile);
        }
        return null;
    }

    public HabboItem getLowestChair(RoomTile tile) {
        HabboItem lowestChair = null;
        THashSet<HabboItem> items = this.getItemsAt(tile);
        if (items != null && !items.isEmpty()) {
            for (HabboItem item : items) {
                if (!item.getBaseItem().allowSit() || lowestChair != null && lowestChair.getZ() < item.getZ()) continue;
                lowestChair = item;
            }
        }
        return lowestChair;
    }

    public HabboItem getTallestChair(RoomTile tile) {
        HabboItem lowestChair = null;
        THashSet<HabboItem> items = this.getItemsAt(tile);
        if (items != null && !items.isEmpty()) {
            for (HabboItem item : items) {
                if (!item.getBaseItem().allowSit() || lowestChair != null && lowestChair.getZ() + Item.getCurrentHeight(lowestChair) > item.getZ() + Item.getCurrentHeight(item)) continue;
                lowestChair = item;
            }
        }
        return lowestChair;
    }

    public double getStackHeight(short x, short y, boolean calculateHeightmap, HabboItem exclude) {
        HabboItem item;
        FurnitureStackHeightEvent event;
        if (x < 0 || y < 0 || this.layout == null) {
            return calculateHeightmap ? 32767.0 : 0.0;
        }
        if (Emulator.getPluginManager().isRegistered(FurnitureStackHeightEvent.class, true) && (event = Emulator.getPluginManager().fireEvent(new FurnitureStackHeightEvent(x, y, this))).hasPluginHelper()) {
            return calculateHeightmap ? event.getHeight() * 256.0 : event.getHeight();
        }
        double height = this.layout.getHeightAtSquare(x, y);
        boolean canStack = true;
        THashSet<HabboItem> stackHelpers = this.getItemsAt(InteractionStackHelper.class, (int)x, y);
        stackHelpers.addAll(this.getItemsAt(InteractionTileWalkMagic.class, (int)x, y));
        if (stackHelpers.size() > 0) {
            for (HabboItem item2 : stackHelpers) {
                if (item2 == exclude) continue;
                return calculateHeightmap ? item2.getZ() * 256.0 : item2.getZ();
            }
        }
        if ((item = this.getTopItemAt(x, y, exclude)) != null) {
            canStack = item.getBaseItem().allowStack();
            height = item.getZ() + (item.getBaseItem().allowSit() ? 0.0 : Item.getCurrentHeight(item));
        }
        if (calculateHeightmap) {
            return canStack ? height * 256.0 : 32767.0;
        }
        return canStack ? height : -1.0;
    }

    public double getStackHeight(short x, short y, boolean calculateHeightmap) {
        return this.getStackHeight(x, y, calculateHeightmap, null);
    }

    public boolean hasObjectTypeAt(Class<?> type, int x, int y) {
        THashSet<HabboItem> items = this.getItemsAt(x, y);
        for (HabboItem item : items) {
            if (item.getClass() != type) continue;
            return true;
        }
        return false;
    }

    public boolean canSitOrLayAt(int x, int y) {
        if (this.hasHabbosAt(x, y)) {
            return false;
        }
        THashSet<HabboItem> items = this.getItemsAt(x, y);
        return this.canSitAt(items) || this.canLayAt(items);
    }

    public boolean canSitAt(int x, int y) {
        if (this.hasHabbosAt(x, y)) {
            return false;
        }
        return this.canSitAt(this.getItemsAt(x, y));
    }

    boolean canWalkAt(RoomTile roomTile) {
        if (roomTile == null) {
            return false;
        }
        if (roomTile.state == RoomTileState.INVALID) {
            return false;
        }
        HabboItem topItem = null;
        boolean canWalk = true;
        THashSet<HabboItem> items = this.getItemsAt(roomTile);
        if (items != null) {
            for (HabboItem item : items) {
                if (topItem == null) {
                    topItem = item;
                }
                if (item.getZ() > topItem.getZ()) {
                    topItem = item;
                    canWalk = topItem.isWalkable() || topItem.getBaseItem().allowWalk();
                    continue;
                }
                if (item.getZ() != topItem.getZ() || !canWalk || (topItem.isWalkable() || topItem.getBaseItem().allowWalk()) && (item.getBaseItem().allowWalk() || item.isWalkable())) continue;
                canWalk = false;
            }
        }
        return canWalk;
    }

    boolean canSitAt(THashSet<HabboItem> items) {
        if (items == null) {
            return false;
        }
        HabboItem tallestItem = null;
        for (HabboItem item : items) {
            if (tallestItem != null && tallestItem.getZ() + Item.getCurrentHeight(tallestItem) > item.getZ() + Item.getCurrentHeight(item)) continue;
            tallestItem = item;
        }
        if (tallestItem == null) {
            return false;
        }
        return tallestItem.getBaseItem().allowSit();
    }

    public boolean canLayAt(int x, int y) {
        return this.canLayAt(this.getItemsAt(x, y));
    }

    boolean canLayAt(THashSet<HabboItem> items) {
        if (items == null || items.isEmpty()) {
            return true;
        }
        HabboItem topItem = null;
        for (HabboItem item : items) {
            if (topItem != null && !(item.getZ() > topItem.getZ())) continue;
            topItem = item;
        }
        return topItem == null || topItem.getBaseItem().allowLay();
    }

    public RoomTile getRandomWalkableTile() {
        for (int i = 0; i < 10; ++i) {
            RoomTile tile = this.layout.getTile((short)(Math.random() * (double)this.layout.getMapSizeX()), (short)(Math.random() * (double)this.layout.getMapSizeY()));
            if (tile == null || tile.getState() == RoomTileState.BLOCKED || tile.getState() == RoomTileState.INVALID) continue;
            return tile;
        }
        return null;
    }

    public RoomTile getRandomWalkableTilesAround(RoomUnit roomUnit, RoomTile tile, int radius) {
        if (!this.layout.tileExists(tile.x, tile.y)) {
            tile = this.layout.getTile(roomUnit.getX(), roomUnit.getY());
            this.getBot(roomUnit).needsUpdate(true);
        }
        ArrayList<RoomTile> walkableTiles = new ArrayList<RoomTile>();
        int minX = Math.max(0, tile.x - radius);
        int minY = Math.max(0, tile.y - radius);
        int maxX = Math.min(this.getLayout().getMapSizeX() - 1, tile.x + radius);
        int maxY = Math.min(this.getLayout().getMapSizeY() - 1, tile.y + radius);
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                RoomTile candidateTile = this.getLayout().getTile((short)x, (short)y);
                if (candidateTile == null || candidateTile.getState() == RoomTileState.BLOCKED || candidateTile.getState() == RoomTileState.INVALID) continue;
                walkableTiles.add(candidateTile);
            }
        }
        if (walkableTiles.isEmpty()) {
            return tile;
        }
        Collections.shuffle(walkableTiles);
        return (RoomTile)walkableTiles.get(0);
    }

    public Habbo getHabbo(String username) {
        for (Habbo habbo : this.getHabbos()) {
            if (!habbo.getHabboInfo().getUsername().equalsIgnoreCase(username)) continue;
            return habbo;
        }
        return null;
    }

    public Habbo getHabbo(RoomUnit roomUnit) {
        for (Habbo habbo : this.getHabbos()) {
            if (habbo.getRoomUnit() != roomUnit) continue;
            return habbo;
        }
        return null;
    }

    public Habbo getHabbo(int userId) {
        return this.currentHabbos.get(userId);
    }

    public Habbo getHabboByRoomUnitId(int roomUnitId) {
        for (Habbo habbo : this.getHabbos()) {
            if (habbo.getRoomUnit().getId() != roomUnitId) continue;
            return habbo;
        }
        return null;
    }

    public void sendComposer(ServerMessage message) {
        for (Habbo habbo : this.getHabbos()) {
            if (habbo.getClient() == null) continue;
            habbo.getClient().sendResponse(message);
        }
    }

    public void sendComposerToHabbosWithRights(ServerMessage message) {
        for (Habbo habbo : this.getHabbos()) {
            if (!this.hasRights(habbo)) continue;
            habbo.getClient().sendResponse(message);
        }
    }

    public void petChat(ServerMessage message) {
        for (Habbo habbo : this.getHabbos()) {
            if (habbo.getHabboStats().ignorePets) continue;
            habbo.getClient().sendResponse(message);
        }
    }

    public void botChat(ServerMessage message) {
        if (message == null) {
            return;
        }
        for (Habbo habbo : this.getHabbos()) {
            if (habbo == null || habbo.getHabboStats().ignoreBots) continue;
            habbo.getClient().sendResponse(message);
        }
    }

    private void loadRights(Connection connection) {
        this.rights.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM room_rights WHERE room_id = ?");){
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    this.rights.add(set.getInt("user_id"));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    private void loadBans(Connection connection) {
        this.bannedHabbos.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username, users.id, room_bans.* FROM room_bans INNER JOIN users ON room_bans.user_id = users.id WHERE ends > ? AND room_bans.room_id = ?");){
            statement.setInt(1, Emulator.getIntUnixTimestamp());
            statement.setInt(2, this.id);
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    if (this.bannedHabbos.containsKey(set.getInt("user_id"))) continue;
                    this.bannedHabbos.put(set.getInt("user_id"), new RoomBan(set));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public RoomRightLevels getGuildRightLevel(Habbo habbo) {
        if (this.guild > 0 && habbo.getHabboStats().hasGuild(this.guild)) {
            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guild);
            if (Emulator.getGameEnvironment().getGuildManager().getOnlyAdmins(guild).get(habbo.getHabboInfo().getId()) != null) {
                return RoomRightLevels.GUILD_ADMIN;
            }
            if (guild.getRights()) {
                return RoomRightLevels.GUILD_RIGHTS;
            }
        }
        return RoomRightLevels.NONE;
    }

    @Deprecated
    public int guildRightLevel(Habbo habbo) {
        return this.getGuildRightLevel((Habbo)habbo).level;
    }

    public boolean isOwner(Habbo habbo) {
        return habbo.getHabboInfo().getId() == this.ownerId || habbo.hasPermission(Permission.ACC_ANYROOMOWNER);
    }

    public boolean hasRights(Habbo habbo) {
        return this.isOwner(habbo) || this.rights.contains(habbo.getHabboInfo().getId()) || habbo.getRoomUnit().getRightsLevel() != RoomRightLevels.NONE && this.currentHabbos.containsKey(habbo.getHabboInfo().getId());
    }

    public void giveRights(Habbo habbo) {
        if (habbo != null) {
            this.giveRights(habbo.getHabboInfo().getId());
        }
    }

    public void giveRights(int userId) {
        Habbo habbo;
        if (this.rights.contains(userId)) {
            return;
        }
        if (this.rights.add(userId)) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO room_rights VALUES (?, ?)");){
                statement.setInt(1, this.id);
                statement.setInt(2, userId);
                statement.execute();
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
        if ((habbo = this.getHabbo(userId)) != null) {
            this.refreshRightsForHabbo(habbo);
            this.sendComposer(new RoomAddRightsListComposer(this, habbo.getHabboInfo().getId(), habbo.getHabboInfo().getUsername()).compose());
        } else {
            MessengerBuddy buddy;
            Habbo owner = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.ownerId);
            if (owner != null && (buddy = owner.getMessenger().getFriend(userId)) != null) {
                this.sendComposer(new RoomAddRightsListComposer(this, userId, buddy.getUsername()).compose());
            }
        }
    }

    public void removeRights(int userId) {
        Habbo habbo = this.getHabbo(userId);
        if (Emulator.getPluginManager().fireEvent(new UserRightsTakenEvent(this.getHabbo(this.getOwnerId()), userId, habbo)).isCancelled()) {
            return;
        }
        this.sendComposer(new RoomRemoveRightsListComposer(this, userId).compose());
        if (this.rights.remove(userId)) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ? AND user_id = ?");){
                statement.setInt(1, this.id);
                statement.setInt(2, userId);
                statement.execute();
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
        if (habbo != null) {
            this.ejectUserFurni(habbo.getHabboInfo().getId());
            habbo.getRoomUnit().setRightsLevel(RoomRightLevels.NONE);
            habbo.getRoomUnit().removeStatus(RoomUnitStatus.FLAT_CONTROL);
            this.refreshRightsForHabbo(habbo);
        }
    }

    public void removeAllRights() {
        for (int userId : this.rights.toArray()) {
            this.ejectUserFurni(userId);
        }
        this.rights.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ?");){
            statement.setInt(1, this.id);
            statement.execute();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        this.refreshRightsInRoom();
    }

    void refreshRightsInRoom() {
        Room room = this;
        for (Habbo habbo : this.getHabbos()) {
            if (habbo.getHabboInfo().getCurrentRoom() != room) continue;
            this.refreshRightsForHabbo(habbo);
        }
    }

    public void refreshRightsForHabbo(Habbo habbo) {
        HabboItem item;
        RoomRightLevels flatCtrl = RoomRightLevels.NONE;
        if (habbo.getHabboStats().isRentingSpace() && (item = this.getHabboItem(habbo.getHabboStats().getRentedItemId())) != null) {
            return;
        }
        if (habbo.hasPermission(Permission.ACC_ANYROOMOWNER)) {
            habbo.getClient().sendResponse(new RoomOwnerComposer());
            flatCtrl = RoomRightLevels.MODERATOR;
        } else if (this.isOwner(habbo)) {
            habbo.getClient().sendResponse(new RoomOwnerComposer());
            flatCtrl = RoomRightLevels.MODERATOR;
        } else if (this.hasRights(habbo) && !this.hasGuild()) {
            flatCtrl = RoomRightLevels.RIGHTS;
        } else if (this.hasGuild()) {
            flatCtrl = this.getGuildRightLevel(habbo);
        }
        habbo.getClient().sendResponse(new RoomRightsComposer(flatCtrl));
        habbo.getRoomUnit().setStatus(RoomUnitStatus.FLAT_CONTROL, "" + flatCtrl.level);
        habbo.getRoomUnit().setRightsLevel(flatCtrl);
        habbo.getRoomUnit().statusUpdate(true);
        if (flatCtrl.equals(RoomRightLevels.MODERATOR)) {
            habbo.getClient().sendResponse(new RoomRightsListComposer(this));
        }
    }

    public THashMap<Integer, String> getUsersWithRights() {
        THashMap<Integer, String> rightsMap = new THashMap<Integer, String>();
        if (!this.rights.isEmpty()) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT users.username AS username, users.id as user_id FROM room_rights INNER JOIN users ON room_rights.user_id = users.id WHERE room_id = ?");){
                statement.setInt(1, this.id);
                try (ResultSet set = statement.executeQuery();){
                    while (set.next()) {
                        rightsMap.put(set.getInt("user_id"), set.getString("username"));
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
        return rightsMap;
    }

    public void unbanHabbo(int userId) {
        RoomBan ban = this.bannedHabbos.remove(userId);
        if (ban != null) {
            ban.delete();
        }
        this.sendComposer(new RoomUserUnbannedComposer(this, userId).compose());
    }

    public boolean isBanned(Habbo habbo) {
        boolean banned;
        RoomBan ban = this.bannedHabbos.get(habbo.getHabboInfo().getId());
        boolean bl = banned = ban != null && ban.endTimestamp > Emulator.getIntUnixTimestamp() && !habbo.hasPermission(Permission.ACC_ANYROOMOWNER) && !habbo.hasPermission("acc_enteranyroom");
        if (!banned && ban != null) {
            this.unbanHabbo(habbo.getHabboInfo().getId());
        }
        return banned;
    }

    public TIntObjectHashMap<RoomBan> getBannedHabbos() {
        return this.bannedHabbos;
    }

    public void addRoomBan(RoomBan roomBan) {
        this.bannedHabbos.put(roomBan.userId, roomBan);
    }

    public void makeSit(Habbo habbo) {
        if (habbo.getRoomUnit() == null) {
            return;
        }
        if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.SIT) || !habbo.getRoomUnit().canForcePosture()) {
            return;
        }
        this.dance(habbo, DanceType.NONE);
        habbo.getRoomUnit().cmdSit = true;
        habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[habbo.getRoomUnit().getBodyRotation().getValue() - habbo.getRoomUnit().getBodyRotation().getValue() % 2]);
        habbo.getRoomUnit().setStatus(RoomUnitStatus.SIT, "0.5");
        this.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
    }

    public void makeStand(Habbo habbo) {
        if (habbo.getRoomUnit() == null) {
            return;
        }
        HabboItem item = this.getTopItemAt(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY());
        if (item == null || !item.getBaseItem().allowSit() || !item.getBaseItem().allowLay()) {
            habbo.getRoomUnit().cmdStand = true;
            habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[habbo.getRoomUnit().getBodyRotation().getValue() - habbo.getRoomUnit().getBodyRotation().getValue() % 2]);
            habbo.getRoomUnit().removeStatus(RoomUnitStatus.SIT);
            this.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
        }
    }

    public void giveEffect(Habbo habbo, int effectId, int duration) {
        if (this.currentHabbos.containsKey(habbo.getHabboInfo().getId())) {
            this.giveEffect(habbo.getRoomUnit(), effectId, duration);
        }
    }

    public void giveEffect(RoomUnit roomUnit, int effectId, int duration) {
        duration = duration == -1 || duration == Integer.MAX_VALUE ? Integer.MAX_VALUE : (duration += Emulator.getIntUnixTimestamp());
        if (this.allowEffects && roomUnit != null) {
            roomUnit.setEffectId(effectId, duration);
            this.sendComposer(new RoomUserEffectComposer(roomUnit).compose());
        }
    }

    public void giveHandItem(Habbo habbo, int handItem) {
        this.giveHandItem(habbo.getRoomUnit(), handItem);
    }

    public void giveHandItem(RoomUnit roomUnit, int handItem) {
        roomUnit.setHandItem(handItem);
        this.sendComposer(new RoomUserHandItemComposer(roomUnit).compose());
    }

    public void updateItem(HabboItem item) {
        if (this.isLoaded() && item != null && item.getRoomId() == this.id && item.getBaseItem() != null) {
            if (item.getBaseItem().getType() == FurnitureType.FLOOR) {
                this.sendComposer(new FloorItemUpdateComposer(item).compose());
                this.updateTiles(this.getLayout().getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
            } else if (item.getBaseItem().getType() == FurnitureType.WALL) {
                this.sendComposer(new WallItemUpdateComposer(item).compose());
            }
        }
    }

    public void updateItemState(HabboItem item) {
        if (!item.isLimited()) {
            this.sendComposer(new ItemStateComposer(item).compose());
        } else {
            this.sendComposer(new FloorItemUpdateComposer(item).compose());
        }
        if (item.getBaseItem().getType() == FurnitureType.FLOOR) {
            if (this.layout == null) {
                return;
            }
            this.updateTiles(this.getLayout().getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
            if (item instanceof InteractionMultiHeight) {
                ((InteractionMultiHeight)item).updateUnitsOnItem(this);
            }
        }
    }

    public int getUserFurniCount(int userId) {
        return this.furniOwnerCount.get(userId);
    }

    public int getUserUniqueFurniCount(int userId) {
        THashSet<Item> items = new THashSet<Item>();
        for (HabboItem item : this.roomItems.valueCollection()) {
            if (items.contains(item.getBaseItem()) || item.getUserId() != userId) continue;
            items.add(item.getBaseItem());
        }
        return items.size();
    }

    public void ejectUserFurni(int userId) {
        Habbo habbo;
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
        int i = this.roomItems.size();
        while (i-- > 0) {
            try {
                iterator.advance();
            }
            catch (Exception e) {
                break;
            }
            if (iterator.value().getUserId() != userId) continue;
            items.add(iterator.value());
            iterator.value().setRoomId(0);
        }
        if ((habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId)) != null) {
            habbo.getInventory().getItemsComponent().addItems(items);
            habbo.getClient().sendResponse(new AddHabboItemComposer(items));
        }
        for (HabboItem i2 : items) {
            this.pickUpItem(i2, null);
        }
    }

    public void ejectUserItem(HabboItem item) {
        this.pickUpItem(item, null);
    }

    public void ejectAll() {
        this.ejectAll(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void ejectAll(Habbo habbo) {
        THashMap<Integer, THashSet> userItemsMap = new THashMap<Integer, THashSet>();
        TIntObjectMap<HabboItem> tIntObjectMap = this.roomItems;
        synchronized (tIntObjectMap) {
            TIntObjectIterator<HabboItem> tIntObjectIterator = this.roomItems.iterator();
            int i = this.roomItems.size();
            while (i-- > 0) {
                try {
                    tIntObjectIterator.advance();
                }
                catch (Exception e) {
                    break;
                }
                if (habbo != null && tIntObjectIterator.value().getUserId() == habbo.getHabboInfo().getId() || tIntObjectIterator.value() instanceof InteractionPostIt) continue;
                userItemsMap.computeIfAbsent(tIntObjectIterator.value().getUserId(), k -> new THashSet()).add(tIntObjectIterator.value());
            }
        }
        for (Map.Entry entry : userItemsMap.entrySet()) {
            for (HabboItem i : (THashSet)entry.getValue()) {
                this.pickUpItem(i, null);
            }
            Habbo user = Emulator.getGameEnvironment().getHabboManager().getHabbo((Integer)entry.getKey());
            if (user == null) continue;
            user.getInventory().getItemsComponent().addItems((THashSet)entry.getValue());
            user.getClient().sendResponse(new AddHabboItemComposer((THashSet)entry.getValue()));
        }
    }

    public void refreshGuild(Guild guild) {
        if (guild.getRoomId() == this.id) {
            THashSet<GuildMember> members = Emulator.getGameEnvironment().getGuildManager().getGuildMembers(guild.getId());
            for (Habbo habbo : this.getHabbos()) {
                Optional<GuildMember> member = members.stream().filter(m -> m.getUserId() == habbo.getHabboInfo().getId()).findAny();
                if (!member.isPresent()) continue;
                habbo.getClient().sendResponse(new GuildInfoComposer(guild, habbo.getClient(), false, member.get()));
            }
        }
        this.refreshGuildRightsInRoom();
    }

    public void refreshGuildColors(Guild guild) {
        if (guild.getRoomId() == this.id) {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
            int i = this.roomItems.size();
            while (i-- > 0) {
                try {
                    iterator.advance();
                }
                catch (Exception e) {
                    break;
                }
                HabboItem habboItem = iterator.value();
                if (!(habboItem instanceof InteractionGuildFurni) || ((InteractionGuildFurni)habboItem).getGuildId() != guild.getId()) continue;
                this.updateItem(habboItem);
            }
        }
    }

    public void refreshGuildRightsInRoom() {
        for (Habbo habbo : this.getHabbos()) {
            if (habbo.getHabboInfo().getCurrentRoom() != this || habbo.getHabboInfo().getId() == this.ownerId || habbo.hasPermission(Permission.ACC_ANYROOMOWNER) || habbo.hasPermission(Permission.ACC_MOVEROTATE)) continue;
            this.refreshRightsForHabbo(habbo);
        }
    }

    public void idle(Habbo habbo) {
        habbo.getRoomUnit().setIdle();
        if (habbo.getRoomUnit().getDanceType() != DanceType.NONE) {
            this.dance(habbo, DanceType.NONE);
        }
        this.sendComposer(new RoomUnitIdleComposer(habbo.getRoomUnit()).compose());
        WiredHandler.handle(WiredTriggerType.IDLES, habbo.getRoomUnit(), this, new Object[]{habbo});
    }

    public void unIdle(Habbo habbo) {
        if (habbo == null || habbo.getRoomUnit() == null) {
            return;
        }
        habbo.getRoomUnit().resetIdleTimer();
        this.sendComposer(new RoomUnitIdleComposer(habbo.getRoomUnit()).compose());
        WiredHandler.handle(WiredTriggerType.UNIDLES, habbo.getRoomUnit(), this, new Object[]{habbo});
    }

    public void dance(Habbo habbo, DanceType danceType) {
        this.dance(habbo.getRoomUnit(), danceType);
    }

    public void dance(RoomUnit unit, DanceType danceType) {
        if (unit.getDanceType() != danceType) {
            boolean isDancing = !unit.getDanceType().equals((Object)DanceType.NONE);
            unit.setDanceType(danceType);
            this.sendComposer(new RoomUserDanceComposer(unit).compose());
            if (danceType.equals((Object)DanceType.NONE) && isDancing) {
                WiredHandler.handle(WiredTriggerType.STOPS_DANCING, unit, this, new Object[]{unit});
            } else if (!danceType.equals((Object)DanceType.NONE) && !isDancing) {
                WiredHandler.handle(WiredTriggerType.STARTS_DANCING, unit, this, new Object[]{unit});
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToWordFilter(String word) {
        THashSet<String> tHashSet = this.wordFilterWords;
        synchronized (tHashSet) {
            if (this.wordFilterWords.contains(word)) {
                return;
            }
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO room_wordfilter VALUES (?, ?)");){
                statement.setInt(1, this.getId());
                statement.setString(2, word);
                statement.execute();
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
                return;
            }
            this.wordFilterWords.add(word);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeFromWordFilter(String word) {
        THashSet<String> tHashSet = this.wordFilterWords;
        synchronized (tHashSet) {
            this.wordFilterWords.remove(word);
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM room_wordfilter WHERE room_id = ? AND word = ?");){
                statement.setInt(1, this.getId());
                statement.setString(2, word);
                statement.execute();
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleWordQuiz(Habbo habbo, String answer) {
        List<Integer> list = this.userVotes;
        synchronized (list) {
            if (!this.wordQuiz.isEmpty() && !this.hasVotedInWordQuiz(habbo)) {
                if ((answer = answer.replace(":", "")).equals("0")) {
                    ++this.noVotes;
                } else if (answer.equals("1")) {
                    ++this.yesVotes;
                }
                this.sendComposer(new SimplePollAnswerComposer(habbo.getHabboInfo().getId(), answer, this.noVotes, this.yesVotes).compose());
                this.userVotes.add(habbo.getHabboInfo().getId());
            }
        }
    }

    public void startWordQuiz(String question, int duration) {
        if (!this.hasActiveWordQuiz()) {
            this.wordQuiz = question;
            this.noVotes = 0;
            this.yesVotes = 0;
            this.userVotes.clear();
            this.wordQuizEnd = Emulator.getIntUnixTimestamp() + duration / 1000;
            this.sendComposer(new SimplePollStartComposer(duration, question).compose());
        }
    }

    public boolean hasActiveWordQuiz() {
        return Emulator.getIntUnixTimestamp() < this.wordQuizEnd;
    }

    public boolean hasVotedInWordQuiz(Habbo habbo) {
        return this.userVotes.contains(habbo.getHabboInfo().getId());
    }

    public void alert(String message) {
        this.sendComposer(new GenericAlertComposer(message).compose());
    }

    public int itemCount() {
        return this.roomItems.size();
    }

    public void setJukeBoxActive(boolean jukeBoxActive) {
        this.jukeboxActive = jukeBoxActive;
        this.needsUpdate = true;
    }

    public boolean isHideWired() {
        return this.hideWired;
    }

    public void setHideWired(boolean hideWired) {
        this.hideWired = hideWired;
        if (this.hideWired) {
            Object response = null;
            for (HabboItem habboItem : this.roomSpecialTypes.getTriggers()) {
                this.sendComposer(new RemoveFloorItemComposer(habboItem).compose());
            }
            for (HabboItem habboItem : this.roomSpecialTypes.getEffects()) {
                this.sendComposer(new RemoveFloorItemComposer(habboItem).compose());
            }
            for (HabboItem habboItem : this.roomSpecialTypes.getConditions()) {
                this.sendComposer(new RemoveFloorItemComposer(habboItem).compose());
            }
            for (HabboItem habboItem : this.roomSpecialTypes.getExtras()) {
                this.sendComposer(new RemoveFloorItemComposer(habboItem).compose());
            }
        } else {
            this.sendComposer(new RoomFloorItemsComposer(this.furniOwnerNames, this.roomSpecialTypes.getTriggers()).compose());
            this.sendComposer(new RoomFloorItemsComposer(this.furniOwnerNames, this.roomSpecialTypes.getEffects()).compose());
            this.sendComposer(new RoomFloorItemsComposer(this.furniOwnerNames, this.roomSpecialTypes.getConditions()).compose());
            this.sendComposer(new RoomFloorItemsComposer(this.furniOwnerNames, this.roomSpecialTypes.getExtras()).compose());
        }
    }

    public FurnitureMovementError canPlaceFurnitureAt(HabboItem item, Habbo habbo, RoomTile tile, int rotation) {
        HabboItem rentSpace;
        if (this.itemCount() >= MAXIMUM_FURNI) {
            return FurnitureMovementError.MAX_ITEMS;
        }
        if (tile == null || tile.state == RoomTileState.INVALID) {
            return FurnitureMovementError.INVALID_MOVE;
        }
        rotation %= 8;
        if (this.hasRights(habbo) || this.getGuildRightLevel(habbo).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS) || habbo.hasPermission(Permission.ACC_MOVEROTATE)) {
            return FurnitureMovementError.NONE;
        }
        if (habbo.getHabboStats().isRentingSpace() && (rentSpace = this.getHabboItem(habbo.getHabboStats().rentedItemId)) != null) {
            if (!RoomLayout.squareInSquare(RoomLayout.getRectangle(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), RoomLayout.getRectangle(tile.x, tile.y, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation))) {
                return FurnitureMovementError.NO_RIGHTS;
            }
            return FurnitureMovementError.NONE;
        }
        for (HabboItem area : this.getRoomSpecialTypes().getItemsOfType(InteractionBuildArea.class)) {
            if (!((InteractionBuildArea)area).inSquare(tile) || !((InteractionBuildArea)area).isBuilder(habbo.getHabboInfo().getUsername())) continue;
            return FurnitureMovementError.NONE;
        }
        return FurnitureMovementError.NO_RIGHTS;
    }

    public FurnitureMovementError furnitureFitsAt(RoomTile tile, HabboItem item, int rotation) {
        return this.furnitureFitsAt(tile, item, rotation, true);
    }

    public FurnitureMovementError furnitureFitsAt(RoomTile tile, HabboItem item, int rotation, boolean checkForUnits) {
        if (!this.layout.fitsOnMap(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation)) {
            return FurnitureMovementError.INVALID_MOVE;
        }
        if (item instanceof InteractionStackHelper || item instanceof InteractionTileWalkMagic) {
            return FurnitureMovementError.NONE;
        }
        THashSet<RoomTile> occupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);
        for (RoomTile t : occupiedTiles) {
            if (t.state == RoomTileState.INVALID) {
                return FurnitureMovementError.INVALID_MOVE;
            }
            if (Emulator.getConfig().getBoolean("wired.place.under", false) && (!Emulator.getConfig().getBoolean("wired.place.under", false) || item.isWalkable() || item.getBaseItem().allowSit() || item.getBaseItem().allowLay())) continue;
            if (checkForUnits && this.hasHabbosAt(t.x, t.y)) {
                return FurnitureMovementError.TILE_HAS_HABBOS;
            }
            if (checkForUnits && this.hasBotsAt(t.x, t.y)) {
                return FurnitureMovementError.TILE_HAS_BOTS;
            }
            if (!checkForUnits || !this.hasPetsAt(t.x, t.y)) continue;
            return FurnitureMovementError.TILE_HAS_PETS;
        }
        ArrayList<Pair<RoomTile, THashSet<HabboItem>>> tileFurniList = new ArrayList<Pair<RoomTile, THashSet<HabboItem>>>();
        for (RoomTile t : occupiedTiles) {
            tileFurniList.add(Pair.create(t, this.getItemsAt(t)));
            HabboItem topItem = this.getTopItemAt(t.x, t.y, item);
            if (topItem == null || topItem.getBaseItem().allowStack() || t.getAllowStack()) continue;
            return FurnitureMovementError.CANT_STACK;
        }
        if (!item.canStackAt(this, tileFurniList)) {
            return FurnitureMovementError.CANT_STACK;
        }
        return FurnitureMovementError.NONE;
    }

    public FurnitureMovementError placeFloorFurniAt(HabboItem item, RoomTile tile, int rotation, Habbo owner) {
        FurnitureBuildheightEvent event;
        boolean pluginHelper = false;
        if (Emulator.getPluginManager().isRegistered(FurniturePlacedEvent.class, true)) {
            FurniturePlacedEvent event2 = Emulator.getPluginManager().fireEvent(new FurniturePlacedEvent(item, owner, tile));
            if (event2.isCancelled()) {
                return FurnitureMovementError.CANCEL_PLUGIN_PLACE;
            }
            pluginHelper = event2.hasPluginHelper();
        }
        THashSet<RoomTile> occupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);
        FurnitureMovementError fits = this.furnitureFitsAt(tile, item, rotation);
        if (!fits.equals((Object)FurnitureMovementError.NONE) && !pluginHelper) {
            return fits;
        }
        double height = tile.getStackHeight();
        for (RoomTile tile2 : occupiedTiles) {
            double sHeight = tile2.getStackHeight();
            if (!(sHeight > height)) continue;
            height = sHeight;
        }
        if (Emulator.getPluginManager().isRegistered(FurnitureBuildheightEvent.class, true) && (event = Emulator.getPluginManager().fireEvent(new FurnitureBuildheightEvent(item, owner, 0.0, height))).hasChangedHeight()) {
            height = event.getUpdatedHeight();
        }
        item.setZ(height);
        item.setX(tile.x);
        item.setY(tile.y);
        item.setRotation(rotation);
        if (!this.furniOwnerNames.containsKey(item.getUserId()) && owner != null) {
            this.furniOwnerNames.put(item.getUserId(), owner.getHabboInfo().getUsername());
        }
        item.needsUpdate(true);
        this.addHabboItem(item);
        item.setRoomId(this.id);
        item.onPlace(this);
        this.updateTiles(occupiedTiles);
        this.sendComposer(new AddFloorItemComposer(item, this.getFurniOwnerName(item.getUserId())).compose());
        for (RoomTile t : occupiedTiles) {
            this.updateHabbosAt(t.x, t.y);
            this.updateBotsAt(t.x, t.y);
        }
        Emulator.getThreading().run(item);
        return FurnitureMovementError.NONE;
    }

    public FurnitureMovementError placeWallFurniAt(HabboItem item, String wallPosition, Habbo owner) {
        if (!this.hasRights(owner) && !this.getGuildRightLevel(owner).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS)) {
            return FurnitureMovementError.NO_RIGHTS;
        }
        if (Emulator.getPluginManager().isRegistered(FurniturePlacedEvent.class, true)) {
            FurniturePlacedEvent furniturePlacedEvent = new FurniturePlacedEvent(item, owner, null);
            Emulator.getPluginManager().fireEvent(furniturePlacedEvent);
            if (furniturePlacedEvent.isCancelled()) {
                return FurnitureMovementError.CANCEL_PLUGIN_PLACE;
            }
        }
        item.setWallPosition(wallPosition);
        if (!this.furniOwnerNames.containsKey(item.getUserId()) && owner != null) {
            this.furniOwnerNames.put(item.getUserId(), owner.getHabboInfo().getUsername());
        }
        this.sendComposer(new AddWallItemComposer(item, this.getFurniOwnerName(item.getUserId())).compose());
        item.needsUpdate(true);
        this.addHabboItem(item);
        item.setRoomId(this.id);
        item.onPlace(this);
        Emulator.getThreading().run(item);
        return FurnitureMovementError.NONE;
    }

    public FurnitureMovementError moveFurniTo(HabboItem item, RoomTile tile, int rotation, Habbo actor) {
        return this.moveFurniTo(item, tile, rotation, actor, true, true);
    }

    public FurnitureMovementError moveFurniTo(HabboItem item, RoomTile tile, int rotation, Habbo actor, boolean sendUpdates) {
        return this.moveFurniTo(item, tile, rotation, actor, sendUpdates, true);
    }

    public FurnitureMovementError moveFurniTo(HabboItem item, RoomTile tile, int rotation, Habbo actor, boolean sendUpdates, boolean checkForUnits) {
        FurnitureBuildheightEvent event;
        double height;
        RoomTile oldLocation = this.layout.getTile(item.getX(), item.getY());
        boolean pluginHelper = false;
        if (Emulator.getPluginManager().isRegistered(FurnitureMovedEvent.class, true)) {
            FurnitureMovedEvent event2 = Emulator.getPluginManager().fireEvent(new FurnitureMovedEvent(item, actor, oldLocation, tile));
            if (event2.isCancelled()) {
                return FurnitureMovementError.CANCEL_PLUGIN_MOVE;
            }
            pluginHelper = event2.hasPluginHelper();
        }
        boolean magicTile = item instanceof InteractionStackHelper || item instanceof InteractionTileWalkMagic;
        Optional<HabboItem> stackHelper = this.getItemsAt(tile).stream().filter(i -> i instanceof InteractionStackHelper || i instanceof InteractionTileWalkMagic).findAny();
        THashSet<RoomTile> occupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);
        THashSet<RoomTile> newOccupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);
        HabboItem topItem = this.getTopItemAt(occupiedTiles, null);
        if (stackHelper.isEmpty() && !pluginHelper) {
            if (oldLocation != tile) {
                for (RoomTile roomTile : occupiedTiles) {
                    HabboItem tileTopItem = this.getTopItemAt(roomTile.x, roomTile.y);
                    if (!magicTile && (tileTopItem != null && tileTopItem != item ? roomTile.state.equals((Object)RoomTileState.INVALID) || !roomTile.getAllowStack() || !tileTopItem.getBaseItem().allowStack() : this.calculateTileState(roomTile, item).equals((Object)RoomTileState.INVALID))) {
                        return FurnitureMovementError.CANT_STACK;
                    }
                    if (Emulator.getConfig().getBoolean("wired.place.under", false) && (!Emulator.getConfig().getBoolean("wired.place.under", false) || item.isWalkable() || item.getBaseItem().allowSit() || item.getBaseItem().allowLay()) || !checkForUnits) continue;
                    if (!magicTile && this.hasHabbosAt(roomTile.x, roomTile.y)) {
                        return FurnitureMovementError.TILE_HAS_HABBOS;
                    }
                    if (!magicTile && this.hasBotsAt(roomTile.x, roomTile.y)) {
                        return FurnitureMovementError.TILE_HAS_BOTS;
                    }
                    if (magicTile || !this.hasPetsAt(roomTile.x, roomTile.y)) continue;
                    return FurnitureMovementError.TILE_HAS_PETS;
                }
            }
            ArrayList<Pair<RoomTile, THashSet<HabboItem>>> tileFurniList = new ArrayList<Pair<RoomTile, THashSet<HabboItem>>>();
            for (RoomTile t : occupiedTiles) {
                tileFurniList.add(Pair.create(t, this.getItemsAt(t)));
            }
            if (!magicTile && !item.canStackAt(this, tileFurniList)) {
                return FurnitureMovementError.CANT_STACK;
            }
        }
        THashSet<RoomTile> oldOccupiedTiles = this.layout.getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
        int n = item.getRotation();
        if (n != rotation) {
            item.setRotation(rotation);
            if (Emulator.getPluginManager().isRegistered(FurnitureRotatedEvent.class, true)) {
                FurnitureRotatedEvent furnitureRotatedEvent = new FurnitureRotatedEvent(item, actor, n);
                Emulator.getPluginManager().fireEvent(furnitureRotatedEvent);
                if (furnitureRotatedEvent.isCancelled()) {
                    item.setRotation(n);
                    return FurnitureMovementError.CANCEL_PLUGIN_ROTATE;
                }
            }
            if (!stackHelper.isPresent() && topItem != null && topItem != item && !topItem.getBaseItem().allowStack() || topItem != null && topItem != item && topItem.getZ() + Item.getCurrentHeight(topItem) + Item.getCurrentHeight(item) > MAXIMUM_FURNI_HEIGHT) {
                item.setRotation(n);
                return FurnitureMovementError.CANT_STACK;
            }
        }
        if (stackHelper.isPresent()) {
            height = stackHelper.get().getExtradata().isEmpty() ? Double.parseDouble("0.0") : Double.parseDouble(stackHelper.get().getExtradata()) / 100.0;
        } else if (item == topItem) {
            height = item.getZ();
        } else if (magicTile) {
            if (topItem == null) {
                height = this.getStackHeight(tile.x, tile.y, false, item);
                for (RoomTile til : occupiedTiles) {
                    sHeight = this.getStackHeight(til.x, til.y, false, item);
                    if (!(sHeight > height)) continue;
                    height = sHeight;
                }
            } else {
                height = topItem.getZ() + topItem.getBaseItem().getHeight();
            }
        } else {
            height = this.getStackHeight(tile.x, tile.y, false, item);
            for (RoomTile til : occupiedTiles) {
                sHeight = this.getStackHeight(til.x, til.y, false, item);
                if (!(sHeight > height)) continue;
                height = sHeight;
            }
        }
        boolean cantStack = false;
        boolean pluginHeight = false;
        if (height > MAXIMUM_FURNI_HEIGHT) {
            cantStack = true;
        }
        if (height < (double)this.getLayout().getHeightAtSquare(tile.x, tile.y)) {
            cantStack = true;
        }
        if (Emulator.getPluginManager().isRegistered(FurnitureBuildheightEvent.class, true) && (event = Emulator.getPluginManager().fireEvent(new FurnitureBuildheightEvent(item, actor, 0.0, height))).hasChangedHeight()) {
            height = event.getUpdatedHeight();
            pluginHeight = true;
        }
        if (!pluginHeight && cantStack) {
            return FurnitureMovementError.CANT_STACK;
        }
        item.setX(tile.x);
        item.setY(tile.y);
        item.setZ(height);
        if (magicTile) {
            item.setZ(tile.z);
            item.setExtradata("" + item.getZ() * 100.0);
        }
        if (item.getZ() > MAXIMUM_FURNI_HEIGHT) {
            item.setZ(MAXIMUM_FURNI_HEIGHT);
        }
        item.onMove(this, oldLocation, tile);
        item.needsUpdate(true);
        Emulator.getThreading().run(item);
        if (sendUpdates) {
            this.sendComposer(new FloorItemUpdateComposer(item).compose());
        }
        occupiedTiles.removeAll(oldOccupiedTiles);
        occupiedTiles.addAll(oldOccupiedTiles);
        this.updateTiles(occupiedTiles);
        for (RoomTile t : occupiedTiles) {
            this.updateHabbosAt(t.x, t.y, this.getHabbosAt(t.x, t.y));
            this.updateBotsAt(t.x, t.y);
        }
        if (Emulator.getConfig().getBoolean("wired.place.under", false)) {
            for (RoomTile t : newOccupiedTiles) {
                for (Habbo h : this.getHabbosAt(t.x, t.y)) {
                    try {
                        item.onWalkOn(h.getRoomUnit(), this, null);
                    }
                    catch (Exception exception) {}
                }
            }
        }
        return FurnitureMovementError.NONE;
    }

    public FurnitureMovementError slideFurniTo(HabboItem item, RoomTile tile, int rotation) {
        RoomTile oldLocation = this.layout.getTile(item.getX(), item.getY());
        HabboItem topItem = this.getTopItemAt(tile.x, tile.y);
        boolean magicTile = item instanceof InteractionStackHelper || item instanceof InteractionTileWalkMagic;
        Optional<HabboItem> stackHelper = this.getItemsAt(tile).stream().filter(i -> i instanceof InteractionStackHelper).findAny();
        THashSet<RoomTile> occupiedTiles = this.layout.getTilesAt(tile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);
        ArrayList<Pair<RoomTile, THashSet<HabboItem>>> tileFurniList = new ArrayList<Pair<RoomTile, THashSet<HabboItem>>>();
        for (RoomTile t : occupiedTiles) {
            tileFurniList.add(Pair.create(t, this.getItemsAt(t)));
        }
        if (!magicTile && !item.canStackAt(this, tileFurniList)) {
            return FurnitureMovementError.CANT_STACK;
        }
        THashSet<RoomTile> oldOccupiedTiles = this.layout.getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
        int oldRotation = item.getRotation();
        item.setRotation(rotation);
        if (magicTile) {
            item.setZ(tile.z);
            item.setExtradata("" + item.getZ() * 100.0);
        }
        if (item.getZ() > MAXIMUM_FURNI_HEIGHT) {
            item.setZ(MAXIMUM_FURNI_HEIGHT);
        }
        double offset = this.getStackHeight(tile.x, tile.y, false, item) - item.getZ();
        this.sendComposer(new FloorItemOnRollerComposer(item, null, tile, offset, this).compose());
        for (RoomTile t : occupiedTiles) {
            this.updateHabbosAt(t.x, t.y);
            this.updateBotsAt(t.x, t.y);
        }
        return FurnitureMovementError.NONE;
    }

    public THashSet<RoomUnit> getRoomUnits() {
        return this.getRoomUnits(null);
    }

    public THashSet<RoomUnit> getRoomUnits(RoomTile atTile) {
        THashSet<RoomUnit> units = new THashSet<RoomUnit>();
        for (Habbo habbo : this.currentHabbos.values()) {
            if (habbo == null || habbo.getRoomUnit() == null || habbo.getRoomUnit().getRoom() == null || habbo.getRoomUnit().getRoom().getId() != this.getId() || atTile != null && habbo.getRoomUnit().getCurrentLocation() != atTile) continue;
            units.add(habbo.getRoomUnit());
        }
        for (Pet pet : this.currentPets.valueCollection()) {
            if (pet == null || pet.getRoomUnit() == null || pet.getRoomUnit().getRoom() == null || pet.getRoomUnit().getRoom().getId() != this.getId() || atTile != null && pet.getRoomUnit().getCurrentLocation() != atTile) continue;
            units.add(pet.getRoomUnit());
        }
        for (Bot bot : this.currentBots.valueCollection()) {
            if (bot == null || bot.getRoomUnit() == null || bot.getRoomUnit().getRoom() == null || bot.getRoomUnit().getRoom().getId() != this.getId() || atTile != null && bot.getRoomUnit().getCurrentLocation() != atTile) continue;
            units.add(bot.getRoomUnit());
        }
        return units;
    }

    public Collection<RoomUnit> getRoomUnitsAt(RoomTile tile) {
        THashSet<RoomUnit> roomUnits = this.getRoomUnits();
        return roomUnits.stream().filter(unit -> unit.getCurrentLocation() == tile).collect(Collectors.toSet());
    }

    private /* synthetic */ LoopTracker lambda$cycle$7(double curX, double curY, double curZ, RoomUnit u) {
        RoomTile nextTile = null;
        for (InteractionRoller roller : this.roomSpecialTypes.getRollers().values()) {
            RoomTile rollerTile = this.getLayout().getTile(roller.getX(), roller.getY());
            RoomTile potentialNextTile = this.getLayout().getTileInFront(rollerTile, roller.getRotation());
            if (potentialNextTile == null) continue;
            nextTile = potentialNextTile;
            break;
        }
        return new LoopTracker(curX, curY, curZ, nextTile);
    }

    static {
        for (int i = 1; i <= 3; ++i) {
            RoomMoodlightData data = RoomMoodlightData.fromString("");
            data.setId(i);
            defaultMoodData.put(i, data);
        }
    }

    private static class LoopTracker {
        double x;
        double y;
        double z;
        int counter;
        RoomTile nextTile;

        public LoopTracker(double x, double y, double z, RoomTile nextTile) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.counter = 0;
            this.nextTile = nextTile;
        }

        public boolean isSamePosition(double newX, double newY, double newZ) {
            double EPSILON = 0.001;
            return Math.abs(newX - this.x) < 0.001 && Math.abs(newY - this.y) < 0.001 && Math.abs(newZ - this.z) < 0.001;
        }

        public void update(double newX, double newY, double newZ, RoomTile nextTile) {
            this.x = newX;
            this.y = newY;
            this.z = newZ;
            this.counter = 0;
            this.nextTile = nextTile;
        }
    }
}

