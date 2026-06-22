/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.CrackableReward;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.ItemInteraction;
import com.eu.habbo.habbohotel.items.NewUserGift;
import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.habbohotel.items.interactions.InteractionBackgroundToner;
import com.eu.habbo.habbohotel.items.interactions.InteractionBadgeDisplay;
import com.eu.habbo.habbohotel.items.interactions.InteractionBlackHole;
import com.eu.habbo.habbohotel.items.interactions.InteractionBuildArea;
import com.eu.habbo.habbohotel.items.interactions.InteractionCannon;
import com.eu.habbo.habbohotel.items.interactions.InteractionClothing;
import com.eu.habbo.habbohotel.items.interactions.InteractionColorPlate;
import com.eu.habbo.habbohotel.items.interactions.InteractionColorWheel;
import com.eu.habbo.habbohotel.items.interactions.InteractionCostumeHopper;
import com.eu.habbo.habbohotel.items.interactions.InteractionCrackable;
import com.eu.habbo.habbohotel.items.interactions.InteractionCrackableMaster;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.items.interactions.InteractionDice;
import com.eu.habbo.habbohotel.items.interactions.InteractionEffectGate;
import com.eu.habbo.habbohotel.items.interactions.InteractionEffectGiver;
import com.eu.habbo.habbohotel.items.interactions.InteractionEffectTile;
import com.eu.habbo.habbohotel.items.interactions.InteractionEffectToggle;
import com.eu.habbo.habbohotel.items.interactions.InteractionEffectVendingMachine;
import com.eu.habbo.habbohotel.items.interactions.InteractionEffectVendingMachineNoSides;
import com.eu.habbo.habbohotel.items.interactions.InteractionExternalImage;
import com.eu.habbo.habbohotel.items.interactions.InteractionFXBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionFireworks;
import com.eu.habbo.habbohotel.items.interactions.InteractionGate;
import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.items.interactions.InteractionGroupPressurePlate;
import com.eu.habbo.habbohotel.items.interactions.InteractionGuildFurni;
import com.eu.habbo.habbohotel.items.interactions.InteractionGuildGate;
import com.eu.habbo.habbohotel.items.interactions.InteractionGymEquipment;
import com.eu.habbo.habbohotel.items.interactions.InteractionHabboClubGate;
import com.eu.habbo.habbohotel.items.interactions.InteractionHabboClubHopper;
import com.eu.habbo.habbohotel.items.interactions.InteractionHabboClubTeleportTile;
import com.eu.habbo.habbohotel.items.interactions.InteractionHanditem;
import com.eu.habbo.habbohotel.items.interactions.InteractionHanditemTile;
import com.eu.habbo.habbohotel.items.interactions.InteractionHopper;
import com.eu.habbo.habbohotel.items.interactions.InteractionInformationTerminal;
import com.eu.habbo.habbohotel.items.interactions.InteractionJukeBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionLoveLock;
import com.eu.habbo.habbohotel.items.interactions.InteractionMannequin;
import com.eu.habbo.habbohotel.items.interactions.InteractionMonsterCrackable;
import com.eu.habbo.habbohotel.items.interactions.InteractionMoodLight;
import com.eu.habbo.habbohotel.items.interactions.InteractionMultiHeight;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.items.interactions.InteractionMuteArea;
import com.eu.habbo.habbohotel.items.interactions.InteractionNoSidesVendingMachine;
import com.eu.habbo.habbohotel.items.interactions.InteractionObstacle;
import com.eu.habbo.habbohotel.items.interactions.InteractionOneWayGate;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.items.interactions.InteractionPressurePlate;
import com.eu.habbo.habbohotel.items.interactions.InteractionPuzzleBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionPyramid;
import com.eu.habbo.habbohotel.items.interactions.InteractionRandomState;
import com.eu.habbo.habbohotel.items.interactions.InteractionRedeemableSubscriptionBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionRentableSpace;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoomAds;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoomOMatic;
import com.eu.habbo.habbohotel.items.interactions.InteractionSnowboardSlope;
import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.items.interactions.InteractionStickyPole;
import com.eu.habbo.habbohotel.items.interactions.InteractionSwitch;
import com.eu.habbo.habbohotel.items.interactions.InteractionTalkingFurniture;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleportTile;
import com.eu.habbo.habbohotel.items.interactions.InteractionTent;
import com.eu.habbo.habbohotel.items.interactions.InteractionTileEffectProvider;
import com.eu.habbo.habbohotel.items.interactions.InteractionTileWalkMagic;
import com.eu.habbo.habbohotel.items.interactions.InteractionTrap;
import com.eu.habbo.habbohotel.items.interactions.InteractionTrophy;
import com.eu.habbo.habbohotel.items.interactions.InteractionVendingMachine;
import com.eu.habbo.habbohotel.items.interactions.InteractionVikingCotie;
import com.eu.habbo.habbohotel.items.interactions.InteractionVoteCounter;
import com.eu.habbo.habbohotel.items.interactions.InteractionWater;
import com.eu.habbo.habbohotel.items.interactions.InteractionWaterItem;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredHighscore;
import com.eu.habbo.habbohotel.items.interactions.InteractionYoutubeTV;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiPuck;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTile;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGateBlue;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGateGreen;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGateRed;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGateYellow;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboardBlue;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboardGreen;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboardRed;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboardYellow;
import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootball;
import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootballGate;
import com.eu.habbo.habbohotel.items.interactions.games.football.goals.InteractionFootballGoalBlue;
import com.eu.habbo.habbohotel.items.interactions.games.football.goals.InteractionFootballGoalGreen;
import com.eu.habbo.habbohotel.items.interactions.games.football.goals.InteractionFootballGoalRed;
import com.eu.habbo.habbohotel.items.interactions.games.football.goals.InteractionFootballGoalYellow;
import com.eu.habbo.habbohotel.items.interactions.games.football.scoreboards.InteractionFootballScoreboardBlue;
import com.eu.habbo.habbohotel.items.interactions.games.football.scoreboards.InteractionFootballScoreboardGreen;
import com.eu.habbo.habbohotel.items.interactions.games.football.scoreboards.InteractionFootballScoreboardRed;
import com.eu.habbo.habbohotel.items.interactions.games.football.scoreboards.InteractionFootballScoreboardYellow;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.InteractionFreezeGateBlue;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.InteractionFreezeGateGreen;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.InteractionFreezeGateRed;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.InteractionFreezeGateYellow;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboardBlue;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboardGreen;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboardRed;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboardYellow;
import com.eu.habbo.habbohotel.items.interactions.games.tag.bunnyrun.InteractionBunnyrunField;
import com.eu.habbo.habbohotel.items.interactions.games.tag.bunnyrun.InteractionBunnyrunPole;
import com.eu.habbo.habbohotel.items.interactions.games.tag.icetag.InteractionIceTagField;
import com.eu.habbo.habbohotel.items.interactions.games.tag.icetag.InteractionIceTagPole;
import com.eu.habbo.habbohotel.items.interactions.games.tag.rollerskate.InteractionRollerskateField;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionMonsterPlantSeed;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionNest;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetBreedingNest;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetDrink;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetFood;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetToy;
import com.eu.habbo.habbohotel.items.interactions.totems.InteractionTotemHead;
import com.eu.habbo.habbohotel.items.interactions.totems.InteractionTotemLegs;
import com.eu.habbo.habbohotel.items.interactions.totems.InteractionTotemPlanet;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionDateRangeActive;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionFurniHaveFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionFurniHaveHabbo;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionFurniTypeMatch;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionGroupMember;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionHabboCount;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionHabboHasEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionHabboHasHandItem;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionHabboWearsBadge;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionLessTimeElapsed;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionMatchStatePosition;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionMoreTimeElapsed;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotFurniHaveFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotFurniHaveHabbo;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotFurniTypeMatch;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotHabboCount;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotHabboHasEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotHabboWearsBadge;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotInGroup;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotInTeam;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotMatchStatePosition;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionNotTriggerOnFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionTeamMember;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionTriggerOnFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectAlert;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectBotClothes;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectBotFollowHabbo;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectBotGiveHandItem;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectBotTalk;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectBotTalkToHabbo;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectBotTeleport;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectBotWalkToFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectChangeFurniDirection;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveHandItem;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveRespect;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveReward;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveScore;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveScoreToTeam;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectJoinTeam;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectKickHabbo;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectLeaveTeam;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectMatchFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectMoveFurniAway;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectMoveFurniTo;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectMoveFurniTowards;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectMoveRotateFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectMuteHabbo;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectResetTimers;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectTeleport;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectToggleFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectToggleRandom;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectTriggerStacks;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectWhisper;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredBlob;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredExtraRandom;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredExtraUnseen;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerAtSetTime;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerAtTimeLong;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerBotReachedFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerBotReachedHabbo;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerCollision;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerFurniStateToggled;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerGameEnds;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerGameStarts;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerHabboEntersRoom;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerHabboSaysKeyword;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerHabboWalkOffFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerHabboWalkOnFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerRepeater;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerRepeaterLong;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerScoreAchieved;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerTeamLoses;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerTeamWins;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.highscores.WiredHighscoreManager;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemManager.class);
    public static boolean RECYCLER_ENABLED = true;
    private final TIntObjectMap<Item> items = TCollections.synchronizedMap(new TIntObjectHashMap());
    private final TIntObjectHashMap<CrackableReward> crackableRewards = new TIntObjectHashMap();
    private final THashSet<ItemInteraction> interactionsList = new THashSet();
    private final THashMap<String, SoundTrack> soundTracks = new THashMap();
    private final YoutubeManager youtubeManager = new YoutubeManager();
    private final WiredHighscoreManager highscoreManager = new WiredHighscoreManager();
    private final TreeMap<Integer, NewUserGift> newuserGifts = new TreeMap();

    public void load() {
        Emulator.getPluginManager().fireEvent(new EmulatorLoadItemsManagerEvent());
        long millis = System.currentTimeMillis();
        this.loadItemInteractions();
        this.loadItems();
        this.loadCrackable();
        this.loadSoundTracks();
        this.youtubeManager.load();
        this.highscoreManager.load();
        this.loadNewUserGifts();
        LOGGER.info("Item Manager -> Loaded! ({} MS)", (Object)(System.currentTimeMillis() - millis));
    }

    protected void loadItemInteractions() {
        this.interactionsList.add(new ItemInteraction("default", InteractionDefault.class));
        this.interactionsList.add(new ItemInteraction("gate", InteractionGate.class));
        this.interactionsList.add(new ItemInteraction("guild_furni", InteractionGuildFurni.class));
        this.interactionsList.add(new ItemInteraction("guild_gate", InteractionGuildGate.class));
        this.interactionsList.add(new ItemInteraction("background_toner", InteractionBackgroundToner.class));
        this.interactionsList.add(new ItemInteraction("badge_display", InteractionBadgeDisplay.class));
        this.interactionsList.add(new ItemInteraction("mannequin", InteractionMannequin.class));
        this.interactionsList.add(new ItemInteraction("ads_bg", InteractionRoomAds.class));
        this.interactionsList.add(new ItemInteraction("trophy", InteractionTrophy.class));
        this.interactionsList.add(new ItemInteraction("vendingmachine", InteractionVendingMachine.class));
        this.interactionsList.add(new ItemInteraction("pressureplate", InteractionPressurePlate.class));
        this.interactionsList.add(new ItemInteraction("colorplate", InteractionColorPlate.class));
        this.interactionsList.add(new ItemInteraction("multiheight", InteractionMultiHeight.class));
        this.interactionsList.add(new ItemInteraction("dice", InteractionDice.class));
        this.interactionsList.add(new ItemInteraction("colorwheel", InteractionColorWheel.class));
        this.interactionsList.add(new ItemInteraction("cannon", InteractionCannon.class));
        this.interactionsList.add(new ItemInteraction("teleport", InteractionTeleport.class));
        this.interactionsList.add(new ItemInteraction("teleporttile", InteractionTeleportTile.class));
        this.interactionsList.add(new ItemInteraction("crackable", InteractionCrackable.class));
        this.interactionsList.add(new ItemInteraction("crackable_master", InteractionCrackableMaster.class));
        this.interactionsList.add(new ItemInteraction("nest", InteractionNest.class));
        this.interactionsList.add(new ItemInteraction("pet_drink", InteractionPetDrink.class));
        this.interactionsList.add(new ItemInteraction("pet_food", InteractionPetFood.class));
        this.interactionsList.add(new ItemInteraction("pet_toy", InteractionPetToy.class));
        this.interactionsList.add(new ItemInteraction("breeding_nest", InteractionPetBreedingNest.class));
        this.interactionsList.add(new ItemInteraction("obstacle", InteractionObstacle.class));
        this.interactionsList.add(new ItemInteraction("monsterplant_seed", InteractionMonsterPlantSeed.class));
        this.interactionsList.add(new ItemInteraction("gift", InteractionGift.class));
        this.interactionsList.add(new ItemInteraction("stack_helper", InteractionStackHelper.class));
        this.interactionsList.add(new ItemInteraction("puzzle_box", InteractionPuzzleBox.class));
        this.interactionsList.add(new ItemInteraction("hopper", InteractionHopper.class));
        this.interactionsList.add(new ItemInteraction("costume_hopper", InteractionCostumeHopper.class));
        this.interactionsList.add(new ItemInteraction("effect_gate", InteractionEffectGate.class));
        this.interactionsList.add(new ItemInteraction("club_hopper", InteractionHabboClubHopper.class));
        this.interactionsList.add(new ItemInteraction("club_gate", InteractionHabboClubGate.class));
        this.interactionsList.add(new ItemInteraction("club_teleporttile", InteractionHabboClubTeleportTile.class));
        this.interactionsList.add(new ItemInteraction("onewaygate", InteractionOneWayGate.class));
        this.interactionsList.add(new ItemInteraction("love_lock", InteractionLoveLock.class));
        this.interactionsList.add(new ItemInteraction("clothing", InteractionClothing.class));
        this.interactionsList.add(new ItemInteraction("roller", InteractionRoller.class));
        this.interactionsList.add(new ItemInteraction("postit", InteractionPostIt.class));
        this.interactionsList.add(new ItemInteraction("dimmer", InteractionMoodLight.class));
        this.interactionsList.add(new ItemInteraction("rentable_space", InteractionRentableSpace.class));
        this.interactionsList.add(new ItemInteraction("pyramid", InteractionPyramid.class));
        this.interactionsList.add(new ItemInteraction("musicdisc", InteractionMusicDisc.class));
        this.interactionsList.add(new ItemInteraction("fireworks", InteractionFireworks.class));
        this.interactionsList.add(new ItemInteraction("talking_furni", InteractionTalkingFurniture.class));
        this.interactionsList.add(new ItemInteraction("water_item", InteractionWaterItem.class));
        this.interactionsList.add(new ItemInteraction("water", InteractionWater.class));
        this.interactionsList.add(new ItemInteraction("viking_cotie", InteractionVikingCotie.class));
        this.interactionsList.add(new ItemInteraction("tile_fxprovider_nfs", InteractionTileEffectProvider.class));
        this.interactionsList.add(new ItemInteraction("mutearea", InteractionMuteArea.class));
        this.interactionsList.add(new ItemInteraction("buildarea", InteractionBuildArea.class));
        this.interactionsList.add(new ItemInteraction("information_terminal", InteractionInformationTerminal.class));
        this.interactionsList.add(new ItemInteraction("external_image", InteractionExternalImage.class));
        this.interactionsList.add(new ItemInteraction("youtube", InteractionYoutubeTV.class));
        this.interactionsList.add(new ItemInteraction("jukebox", InteractionJukeBox.class));
        this.interactionsList.add(new ItemInteraction("switch", InteractionSwitch.class));
        this.interactionsList.add(new ItemInteraction("fx_box", InteractionFXBox.class));
        this.interactionsList.add(new ItemInteraction("blackhole", InteractionBlackHole.class));
        this.interactionsList.add(new ItemInteraction("effect_toggle", InteractionEffectToggle.class));
        this.interactionsList.add(new ItemInteraction("room_o_matic", InteractionRoomOMatic.class));
        this.interactionsList.add(new ItemInteraction("effect_tile", InteractionEffectTile.class));
        this.interactionsList.add(new ItemInteraction("sticky_pole", InteractionStickyPole.class));
        this.interactionsList.add(new ItemInteraction("trap", InteractionTrap.class));
        this.interactionsList.add(new ItemInteraction("tent", InteractionTent.class));
        this.interactionsList.add(new ItemInteraction("gym_equipment", InteractionGymEquipment.class));
        this.interactionsList.add(new ItemInteraction("handitem", InteractionHanditem.class));
        this.interactionsList.add(new ItemInteraction("handitem_tile", InteractionHanditemTile.class));
        this.interactionsList.add(new ItemInteraction("effect_giver", InteractionEffectGiver.class));
        this.interactionsList.add(new ItemInteraction("effect_vendingmachine", InteractionEffectVendingMachine.class));
        this.interactionsList.add(new ItemInteraction("effect_vendingmachine_no_sides", InteractionEffectVendingMachineNoSides.class));
        this.interactionsList.add(new ItemInteraction("crackable_monster", InteractionMonsterCrackable.class));
        this.interactionsList.add(new ItemInteraction("snowboard_slope", InteractionSnowboardSlope.class));
        this.interactionsList.add(new ItemInteraction("pressureplate_group", InteractionGroupPressurePlate.class));
        this.interactionsList.add(new ItemInteraction("effect_tile_group", InteractionEffectTile.class));
        this.interactionsList.add(new ItemInteraction("crackable_subscription_box", InteractionRedeemableSubscriptionBox.class));
        this.interactionsList.add(new ItemInteraction("random_state", InteractionRandomState.class));
        this.interactionsList.add(new ItemInteraction("vendingmachine_no_sides", InteractionNoSidesVendingMachine.class));
        this.interactionsList.add(new ItemInteraction("game_timer", InteractionGameTimer.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_walks_on_furni", WiredTriggerHabboWalkOnFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_walks_off_furni", WiredTriggerHabboWalkOffFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_enter_room", WiredTriggerHabboEntersRoom.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_says_something", WiredTriggerHabboSaysKeyword.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_periodically", WiredTriggerRepeater.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_period_long", WiredTriggerRepeaterLong.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_state_changed", WiredTriggerFurniStateToggled.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_at_given_time", WiredTriggerAtSetTime.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_at_time_long", WiredTriggerAtTimeLong.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_collision", WiredTriggerCollision.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_game_starts", WiredTriggerGameStarts.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_game_ends", WiredTriggerGameEnds.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_bot_reached_stf", WiredTriggerBotReachedFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_bot_reached_avtr", WiredTriggerBotReachedHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_score_achieved", WiredTriggerScoreAchieved.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_game_team_win", WiredTriggerTeamWins.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_game_team_lose", WiredTriggerTeamLoses.class));
        this.interactionsList.add(new ItemInteraction("wf_act_toggle_state", WiredEffectToggleFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_act_reset_timers", WiredEffectResetTimers.class));
        this.interactionsList.add(new ItemInteraction("wf_act_match_to_sshot", WiredEffectMatchFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_act_move_rotate", WiredEffectMoveRotateFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_act_give_score", WiredEffectGiveScore.class));
        this.interactionsList.add(new ItemInteraction("wf_act_show_message", WiredEffectWhisper.class));
        this.interactionsList.add(new ItemInteraction("wf_act_teleport_to", WiredEffectTeleport.class));
        this.interactionsList.add(new ItemInteraction("wf_act_join_team", WiredEffectJoinTeam.class));
        this.interactionsList.add(new ItemInteraction("wf_act_leave_team", WiredEffectLeaveTeam.class));
        this.interactionsList.add(new ItemInteraction("wf_act_chase", WiredEffectMoveFurniTowards.class));
        this.interactionsList.add(new ItemInteraction("wf_act_flee", WiredEffectMoveFurniAway.class));
        this.interactionsList.add(new ItemInteraction("wf_act_move_to_dir", WiredEffectChangeFurniDirection.class));
        this.interactionsList.add(new ItemInteraction("wf_act_give_score_tm", WiredEffectGiveScoreToTeam.class));
        this.interactionsList.add(new ItemInteraction("wf_act_toggle_to_rnd", WiredEffectToggleRandom.class));
        this.interactionsList.add(new ItemInteraction("wf_act_move_furni_to", WiredEffectMoveFurniTo.class));
        this.interactionsList.add(new ItemInteraction("wf_act_give_reward", WiredEffectGiveReward.class));
        this.interactionsList.add(new ItemInteraction("wf_act_call_stacks", WiredEffectTriggerStacks.class));
        this.interactionsList.add(new ItemInteraction("wf_act_kick_user", WiredEffectKickHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_act_mute_triggerer", WiredEffectMuteHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_teleport", WiredEffectBotTeleport.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_move", WiredEffectBotWalkToFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_talk", WiredEffectBotTalk.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_give_handitem", WiredEffectBotGiveHandItem.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_follow_avatar", WiredEffectBotFollowHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_clothes", WiredEffectBotClothes.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_talk_to_avatar", WiredEffectBotTalkToHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_act_give_respect", WiredEffectGiveRespect.class));
        this.interactionsList.add(new ItemInteraction("wf_act_alert", WiredEffectAlert.class));
        this.interactionsList.add(new ItemInteraction("wf_act_give_handitem", WiredEffectGiveHandItem.class));
        this.interactionsList.add(new ItemInteraction("wf_act_give_effect", WiredEffectGiveEffect.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_has_furni_on", WiredConditionFurniHaveFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_furnis_hv_avtrs", WiredConditionFurniHaveHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_stuff_is", WiredConditionFurniTypeMatch.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_actor_in_group", WiredConditionGroupMember.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_user_count_in", WiredConditionHabboCount.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_wearing_effect", WiredConditionHabboHasEffect.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_wearing_badge", WiredConditionHabboWearsBadge.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_time_less_than", WiredConditionLessTimeElapsed.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_match_snapshot", WiredConditionMatchStatePosition.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_time_more_than", WiredConditionMoreTimeElapsed.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_furni_on", WiredConditionNotFurniHaveFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_hv_avtrs", WiredConditionNotFurniHaveHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_stuff_is", WiredConditionNotFurniTypeMatch.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_user_count", WiredConditionNotHabboCount.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_wearing_fx", WiredConditionNotHabboHasEffect.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_wearing_b", WiredConditionNotHabboWearsBadge.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_in_group", WiredConditionNotInGroup.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_in_team", WiredConditionNotInTeam.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_match_snap", WiredConditionNotMatchStatePosition.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_trggrer_on", WiredConditionNotTriggerOnFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_actor_in_team", WiredConditionTeamMember.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_trggrer_on_frn", WiredConditionTriggerOnFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_has_handitem", WiredConditionHabboHasHandItem.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_date_rng_active", WiredConditionDateRangeActive.class));
        this.interactionsList.add(new ItemInteraction("wf_xtra_random", WiredExtraRandom.class));
        this.interactionsList.add(new ItemInteraction("wf_xtra_unseen", WiredExtraUnseen.class));
        this.interactionsList.add(new ItemInteraction("wf_blob", WiredBlob.class));
        this.interactionsList.add(new ItemInteraction("wf_highscore", InteractionWiredHighscore.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_tile", InteractionBattleBanzaiTile.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_random_teleport", InteractionBattleBanzaiTeleporter.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_sphere", InteractionBattleBanzaiSphere.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_puck", InteractionBattleBanzaiPuck.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_gate_blue", InteractionBattleBanzaiGateBlue.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_gate_green", InteractionBattleBanzaiGateGreen.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_gate_red", InteractionBattleBanzaiGateRed.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_gate_yellow", InteractionBattleBanzaiGateYellow.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_counter_blue", InteractionBattleBanzaiScoreboardBlue.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_counter_green", InteractionBattleBanzaiScoreboardGreen.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_counter_red", InteractionBattleBanzaiScoreboardRed.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_counter_yellow", InteractionBattleBanzaiScoreboardYellow.class));
        this.interactionsList.add(new ItemInteraction("freeze_block", InteractionFreezeBlock.class));
        this.interactionsList.add(new ItemInteraction("freeze_tile", InteractionFreezeTile.class));
        this.interactionsList.add(new ItemInteraction("freeze_exit", InteractionFreezeExitTile.class));
        this.interactionsList.add(new ItemInteraction("freeze_gate_blue", InteractionFreezeGateBlue.class));
        this.interactionsList.add(new ItemInteraction("freeze_gate_green", InteractionFreezeGateGreen.class));
        this.interactionsList.add(new ItemInteraction("freeze_gate_red", InteractionFreezeGateRed.class));
        this.interactionsList.add(new ItemInteraction("freeze_gate_yellow", InteractionFreezeGateYellow.class));
        this.interactionsList.add(new ItemInteraction("freeze_counter_blue", InteractionFreezeScoreboardBlue.class));
        this.interactionsList.add(new ItemInteraction("freeze_counter_green", InteractionFreezeScoreboardGreen.class));
        this.interactionsList.add(new ItemInteraction("freeze_counter_red", InteractionFreezeScoreboardRed.class));
        this.interactionsList.add(new ItemInteraction("freeze_counter_yellow", InteractionFreezeScoreboardYellow.class));
        this.interactionsList.add(new ItemInteraction("icetag_pole", InteractionIceTagPole.class));
        this.interactionsList.add(new ItemInteraction("icetag_field", InteractionIceTagField.class));
        this.interactionsList.add(new ItemInteraction("bunnyrun_pole", InteractionBunnyrunPole.class));
        this.interactionsList.add(new ItemInteraction("bunnyrun_field", InteractionBunnyrunField.class));
        this.interactionsList.add(new ItemInteraction("rollerskate_field", InteractionRollerskateField.class));
        this.interactionsList.add(new ItemInteraction("football", InteractionFootball.class));
        this.interactionsList.add(new ItemInteraction("football_gate", InteractionFootballGate.class));
        this.interactionsList.add(new ItemInteraction("football_counter_blue", InteractionFootballScoreboardBlue.class));
        this.interactionsList.add(new ItemInteraction("football_counter_green", InteractionFootballScoreboardGreen.class));
        this.interactionsList.add(new ItemInteraction("football_counter_red", InteractionFootballScoreboardRed.class));
        this.interactionsList.add(new ItemInteraction("football_counter_yellow", InteractionFootballScoreboardYellow.class));
        this.interactionsList.add(new ItemInteraction("football_goal_blue", InteractionFootballGoalBlue.class));
        this.interactionsList.add(new ItemInteraction("football_goal_green", InteractionFootballGoalGreen.class));
        this.interactionsList.add(new ItemInteraction("football_goal_red", InteractionFootballGoalRed.class));
        this.interactionsList.add(new ItemInteraction("football_goal_yellow", InteractionFootballGoalYellow.class));
        this.interactionsList.add(new ItemInteraction("snowstorm_tree", null));
        this.interactionsList.add(new ItemInteraction("snowstorm_machine", null));
        this.interactionsList.add(new ItemInteraction("snowstorm_pile", null));
        this.interactionsList.add(new ItemInteraction("vote_counter", InteractionVoteCounter.class));
        this.interactionsList.add(new ItemInteraction("totem_leg", InteractionTotemLegs.class));
        this.interactionsList.add(new ItemInteraction("totem_head", InteractionTotemHead.class));
        this.interactionsList.add(new ItemInteraction("totem_planet", InteractionTotemPlanet.class));
        this.interactionsList.add(new ItemInteraction("tile_walkmagic", InteractionTileWalkMagic.class));
    }

    public void addItemInteraction(ItemInteraction itemInteraction) {
        for (ItemInteraction interaction : this.interactionsList) {
            if (interaction.getType() != itemInteraction.getType() && !interaction.getName().equalsIgnoreCase(itemInteraction.getName())) continue;
            throw new RuntimeException("Interaction Types must be unique. An class with type: " + interaction.getClass().getName() + " was already added OR the key: " + interaction.getName() + " is already in use.");
        }
        this.interactionsList.add(itemInteraction);
    }

    public ItemInteraction getItemInteraction(Class<? extends HabboItem> type) {
        for (ItemInteraction interaction : this.interactionsList) {
            if (interaction.getType() != type) continue;
            return interaction;
        }
        LOGGER.debug("Can't find interaction class: {}", (Object)type.getName());
        return this.getItemInteraction(InteractionDefault.class);
    }

    public ItemInteraction getItemInteraction(String type) {
        for (ItemInteraction interaction : this.interactionsList) {
            if (!interaction.getName().equalsIgnoreCase(type)) continue;
            return interaction;
        }
        return this.getItemInteraction(InteractionDefault.class);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void loadItems() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery("SELECT * FROM items_base ORDER BY id DESC");){
            while (set.next()) {
                try {
                    int id = set.getInt("id");
                    if (!this.items.containsKey(id)) {
                        this.items.put(id, new Item(set));
                        continue;
                    }
                    this.items.get(id).update(set);
                }
                catch (Exception e) {
                    LOGGER.error("Failed to load Item ({})", (Object)set.getInt("id"));
                    LOGGER.error("Caught exception", e);
                }
            }
            return;
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void loadCrackable() {
        this.crackableRewards.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM items_crackable");
             ResultSet set = statement.executeQuery();){
            while (set.next()) {
                CrackableReward reward;
                try {
                    reward = new CrackableReward(set);
                }
                catch (Exception e) {
                    LOGGER.error("Failed to load items_crackable item_id = {}", (Object)set.getInt("item_id"));
                    LOGGER.error("Caught exception", e);
                    continue;
                }
                this.crackableRewards.put(set.getInt("item_id"), reward);
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    public int getCrackableCount(int itemId) {
        if (this.crackableRewards.containsKey(itemId)) {
            return this.crackableRewards.get((int)itemId).count;
        }
        return 0;
    }

    public int calculateCrackState(int count, int max, Item baseItem) {
        return (int)Math.floor(1.0 / ((double)max / (double)count) * (double)baseItem.getStateCount());
    }

    public CrackableReward getCrackableData(int itemId) {
        return this.crackableRewards.get(itemId);
    }

    public Item getCrackableReward(int itemId) {
        return this.getItem(this.crackableRewards.get(itemId).getRandomReward());
    }

    public void loadSoundTracks() {
        this.soundTracks.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM soundtracks");
             ResultSet set = statement.executeQuery();){
            while (set.next()) {
                this.soundTracks.put(set.getString("code"), new SoundTrack(set));
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public SoundTrack getSoundTrack(String code) {
        return this.soundTracks.get(code);
    }

    public SoundTrack getSoundTrack(int id) {
        for (Map.Entry<String, SoundTrack> entry : this.soundTracks.entrySet()) {
            if (entry.getValue().getId() != id) continue;
            return entry.getValue();
        }
        return null;
    }

    /*
     * Exception decompiling
     */
    public HabboItem createItem(int habboId, Item item, int limitedStack, int limitedSells, String extraData) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void loadNewUserGifts() {
        this.newuserGifts.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM nux_gifts");
             ResultSet set = statement.executeQuery();){
            while (set.next()) {
                this.newuserGifts.put(set.getInt("id"), new NewUserGift(set));
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public HabboItem createItemNoSql(int habboId, Item item, int limitedStack, int limitedSells, String extraData) {
        Class<? extends HabboItem> itemClass = item.getInteractionType().getType();
        int id = new Random().nextInt(10000000) + 1000000000;
        if (itemClass != null) {
            try {
                return itemClass.getDeclaredConstructor(Integer.TYPE, Integer.TYPE, Item.class, String.class, Integer.TYPE, Integer.TYPE).newInstance(id, habboId, item, extraData, limitedStack, limitedSells);
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
                return new InteractionDefault(id, habboId, item, extraData, limitedStack, limitedSells);
            }
        }
        return null;
    }

    public void addNewUserGift(NewUserGift gift) {
        this.newuserGifts.put(gift.getId(), gift);
    }

    public void removeNewUserGift(NewUserGift gift) {
        this.newuserGifts.remove(gift.getId());
    }

    public NewUserGift getNewUserGift(int id) {
        return this.newuserGifts.get(id);
    }

    public List<NewUserGift> getNewUserGifts() {
        return new ArrayList<NewUserGift>(this.newuserGifts.values());
    }

    public void deleteItem(HabboItem item) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM items WHERE id = ?");){
            statement.setInt(1, item.getId());
            statement.execute();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public HabboItem handleRecycle(Habbo habbo, String itemId) {
        String extradata = Calendar.getInstance().get(5) + "-" + (Calendar.getInstance().get(2) + 1) + "-" + Calendar.getInstance().get(1);
        InteractionDefault item = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO items (user_id, item_id, extra_data) VALUES (?, ?, ?)", 1);){
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, Emulator.getGameEnvironment().getCatalogManager().ecotronItem.getId());
            statement.setString(3, extradata);
            statement.execute();
            try (ResultSet set = statement.getGeneratedKeys();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO items_presents VALUES (?, ?)");){
                while (set.next() && item == null) {
                    preparedStatement.setInt(1, set.getInt(1));
                    preparedStatement.setInt(2, Integer.parseInt(itemId));
                    preparedStatement.addBatch();
                    item = new InteractionDefault(set.getInt(1), habbo.getHabboInfo().getId(), Emulator.getGameEnvironment().getCatalogManager().ecotronItem, extradata, 0, 0);
                }
                preparedStatement.executeBatch();
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return item;
    }

    public HabboItem handleOpenRecycleBox(Habbo habbo, HabboItem box) {
        HabboItem item;
        block51: {
            Emulator.getThreading().run(new QueryDeleteHabboItem(box.getId()));
            item = null;
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM items_presents WHERE item_id = ? LIMIT 1");){
                statement.setInt(1, box.getId());
                try (ResultSet rewardSet = statement.executeQuery();){
                    if (!rewardSet.next()) break block51;
                    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO items (user_id, item_id) VALUES(?, ?)", 1);){
                        preparedStatement.setInt(1, habbo.getHabboInfo().getId());
                        preparedStatement.setInt(2, rewardSet.getInt("base_item_reward"));
                        preparedStatement.execute();
                        try (ResultSet set = preparedStatement.getGeneratedKeys();){
                            if (!set.next()) break block51;
                            try (PreparedStatement request = connection.prepareStatement("SELECT * FROM items WHERE id = ? LIMIT 1");){
                                request.setInt(1, set.getInt(1));
                                try (ResultSet resultSet = request.executeQuery();){
                                    if (!resultSet.next()) break block51;
                                    try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM items_presents WHERE item_id = ? LIMIT 1");){
                                        deleteStatement.setInt(1, box.getId());
                                        deleteStatement.execute();
                                        item = this.loadHabboItem(resultSet);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
        return item;
    }

    public void insertTeleportPair(int itemOneId, int itemTwoId) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO items_teleports VALUES (?, ?)");){
            statement.setInt(1, itemOneId);
            statement.setInt(2, itemTwoId);
            statement.execute();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void insertHopper(HabboItem hopper) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO items_hoppers VALUES (?, ?)");){
            statement.setInt(1, hopper.getId());
            statement.setInt(2, hopper.getBaseItem().getId());
            statement.execute();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public int[] getTargetTeleportRoomId(HabboItem item) {
        int[] a = new int[]{};
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT items.id, items.room_id FROM items_teleports INNER JOIN items ON items_teleports.teleport_one_id = items.id OR items_teleports.teleport_two_id = items.id WHERE items.id != ? AND items.room_id > 0 LIMIT 1");){
            statement.setInt(1, item.getId());
            try (ResultSet set = statement.executeQuery();){
                if (set.next()) {
                    a = new int[]{set.getInt("room_id"), set.getInt("id")};
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return a;
    }

    public HabboItem loadHabboItem(int itemId) {
        HabboItem item = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE id = ? LIMIT 1");){
            statement.setInt(1, itemId);
            try (ResultSet set = statement.executeQuery();){
                if (set.next()) {
                    item = this.loadHabboItem(set);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        return item;
    }

    public HabboItem loadHabboItem(ResultSet set) throws SQLException {
        Item baseItem = this.getItem(set.getInt("item_id"));
        if (baseItem == null) {
            return null;
        }
        Class<? extends HabboItem> itemClass = baseItem.getInteractionType().getType();
        if (itemClass != null) {
            try {
                Constructor<? extends HabboItem> c = itemClass.getConstructor(ResultSet.class, Item.class);
                c.setAccessible(true);
                return c.newInstance(set, baseItem);
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
        return null;
    }

    public HabboItem createGift(String username, Item item, String extraData, int limitedStack, int limitedSells) {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);
        int userId = 0;
        if (habbo != null) {
            userId = habbo.getHabboInfo().getId();
        } else {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE username = ?");){
                statement.setString(1, username);
                try (ResultSet set = statement.executeQuery();){
                    if (set.next()) {
                        userId = set.getInt(1);
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
        if (userId > 0) {
            return this.createGift(userId, item, extraData, limitedStack, limitedSells);
        }
        return null;
    }

    public HabboItem createGift(int userId, Item item, String extraData, int limitedStack, int limitedSells) {
        Habbo habbo;
        HabboItem gift;
        if (userId == 0) {
            return null;
        }
        if (extraData.length() > 1000) {
            LOGGER.error("Extradata exceeds maximum length of 1000 characters: {}", (Object)extraData);
            extraData = extraData.substring(0, 1000);
        }
        if ((gift = this.createItem(userId, item, limitedStack, limitedSells, extraData)) != null && (habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId)) != null) {
            habbo.getInventory().getItemsComponent().addItem(gift);
            habbo.getClient().sendResponse(new AddHabboItemComposer(gift));
        }
        return gift;
    }

    public Item getItem(int itemId) {
        if (itemId < 0) {
            return null;
        }
        return this.items.get(itemId);
    }

    public TIntObjectMap<Item> getItems() {
        return this.items;
    }

    public Item getItem(String itemName) {
        TIntObjectIterator<Item> item = this.items.iterator();
        int i = this.items.size();
        while (i-- > 0) {
            try {
                item.advance();
                if (!item.value().getName().equalsIgnoreCase(itemName)) continue;
                return item.value();
            }
            catch (NoSuchElementException e) {
                break;
            }
        }
        return null;
    }

    public YoutubeManager getYoutubeManager() {
        return this.youtubeManager;
    }

    public WiredHighscoreManager getHighscoreManager() {
        return this.highscoreManager;
    }

    public void dispose() {
        this.items.clear();
        this.highscoreManager.dispose();
        LOGGER.info("Item Manager -> Disposed!");
    }

    public List<String> getInteractionList() {
        ArrayList<String> interactions = new ArrayList<String>();
        for (ItemInteraction interaction : this.interactionsList) {
            interactions.add(interaction.getName());
        }
        Collections.sort(interactions);
        return interactions;
    }
}

