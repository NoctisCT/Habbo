/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.CommandLog;
import com.eu.habbo.habbohotel.commands.AboutCommand;
import com.eu.habbo.habbohotel.commands.AddYoutubePlaylistCommand;
import com.eu.habbo.habbohotel.commands.AlertCommand;
import com.eu.habbo.habbohotel.commands.AllowTradingCommand;
import com.eu.habbo.habbohotel.commands.ArcturusCommand;
import com.eu.habbo.habbohotel.commands.BadgeCommand;
import com.eu.habbo.habbohotel.commands.BanCommand;
import com.eu.habbo.habbohotel.commands.BlockAlertCommand;
import com.eu.habbo.habbohotel.commands.BotsCommand;
import com.eu.habbo.habbohotel.commands.CalendarCommand;
import com.eu.habbo.habbohotel.commands.ChangeNameCommand;
import com.eu.habbo.habbohotel.commands.ChatTypeCommand;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.commands.CommandsCommand;
import com.eu.habbo.habbohotel.commands.ConnectCameraCommand;
import com.eu.habbo.habbohotel.commands.ControlCommand;
import com.eu.habbo.habbohotel.commands.CoordsCommand;
import com.eu.habbo.habbohotel.commands.CreditsCommand;
import com.eu.habbo.habbohotel.commands.DiagonalCommand;
import com.eu.habbo.habbohotel.commands.DisconnectCommand;
import com.eu.habbo.habbohotel.commands.EjectAllCommand;
import com.eu.habbo.habbohotel.commands.EmptyBotsInventoryCommand;
import com.eu.habbo.habbohotel.commands.EmptyInventoryCommand;
import com.eu.habbo.habbohotel.commands.EmptyPetsInventoryCommand;
import com.eu.habbo.habbohotel.commands.EnableCommand;
import com.eu.habbo.habbohotel.commands.EventCommand;
import com.eu.habbo.habbohotel.commands.FacelessCommand;
import com.eu.habbo.habbohotel.commands.FastwalkCommand;
import com.eu.habbo.habbohotel.commands.FilterWordCommand;
import com.eu.habbo.habbohotel.commands.FreezeBotsCommand;
import com.eu.habbo.habbohotel.commands.FreezeCommand;
import com.eu.habbo.habbohotel.commands.GiftCommand;
import com.eu.habbo.habbohotel.commands.GiveRankCommand;
import com.eu.habbo.habbohotel.commands.HabnamCommand;
import com.eu.habbo.habbohotel.commands.HandItemCommand;
import com.eu.habbo.habbohotel.commands.HappyHourCommand;
import com.eu.habbo.habbohotel.commands.HideWiredCommand;
import com.eu.habbo.habbohotel.commands.HotelAlertCommand;
import com.eu.habbo.habbohotel.commands.HotelAlertLinkCommand;
import com.eu.habbo.habbohotel.commands.IPBanCommand;
import com.eu.habbo.habbohotel.commands.InvisibleCommand;
import com.eu.habbo.habbohotel.commands.LayCommand;
import com.eu.habbo.habbohotel.commands.MachineBanCommand;
import com.eu.habbo.habbohotel.commands.MassBadgeCommand;
import com.eu.habbo.habbohotel.commands.MassCreditsCommand;
import com.eu.habbo.habbohotel.commands.MassGiftCommand;
import com.eu.habbo.habbohotel.commands.MassPixelsCommand;
import com.eu.habbo.habbohotel.commands.MassPointsCommand;
import com.eu.habbo.habbohotel.commands.MimicCommand;
import com.eu.habbo.habbohotel.commands.MoonwalkCommand;
import com.eu.habbo.habbohotel.commands.MultiCommand;
import com.eu.habbo.habbohotel.commands.MuteBotsCommand;
import com.eu.habbo.habbohotel.commands.MuteCommand;
import com.eu.habbo.habbohotel.commands.MutePetsCommand;
import com.eu.habbo.habbohotel.commands.PetInfoCommand;
import com.eu.habbo.habbohotel.commands.PickallCommand;
import com.eu.habbo.habbohotel.commands.PingCommand;
import com.eu.habbo.habbohotel.commands.PixelCommand;
import com.eu.habbo.habbohotel.commands.PluginsCommand;
import com.eu.habbo.habbohotel.commands.PointsCommand;
import com.eu.habbo.habbohotel.commands.PromoteTargetOfferCommand;
import com.eu.habbo.habbohotel.commands.PullCommand;
import com.eu.habbo.habbohotel.commands.PushCommand;
import com.eu.habbo.habbohotel.commands.RedeemCommand;
import com.eu.habbo.habbohotel.commands.ReloadRoomCommand;
import com.eu.habbo.habbohotel.commands.RoomAlertCommand;
import com.eu.habbo.habbohotel.commands.RoomBadgeCommand;
import com.eu.habbo.habbohotel.commands.RoomBundleCommand;
import com.eu.habbo.habbohotel.commands.RoomCreditsCommand;
import com.eu.habbo.habbohotel.commands.RoomDanceCommand;
import com.eu.habbo.habbohotel.commands.RoomEffectCommand;
import com.eu.habbo.habbohotel.commands.RoomItemCommand;
import com.eu.habbo.habbohotel.commands.RoomKickCommand;
import com.eu.habbo.habbohotel.commands.RoomMuteCommand;
import com.eu.habbo.habbohotel.commands.RoomPixelsCommand;
import com.eu.habbo.habbohotel.commands.RoomPointsCommand;
import com.eu.habbo.habbohotel.commands.SayAllCommand;
import com.eu.habbo.habbohotel.commands.SayCommand;
import com.eu.habbo.habbohotel.commands.SetMaxCommand;
import com.eu.habbo.habbohotel.commands.SetPollCommand;
import com.eu.habbo.habbohotel.commands.SetSpeedCommand;
import com.eu.habbo.habbohotel.commands.ShoutAllCommand;
import com.eu.habbo.habbohotel.commands.ShoutCommand;
import com.eu.habbo.habbohotel.commands.ShutdownCommand;
import com.eu.habbo.habbohotel.commands.SitCommand;
import com.eu.habbo.habbohotel.commands.SitDownCommand;
import com.eu.habbo.habbohotel.commands.SoftKickCommand;
import com.eu.habbo.habbohotel.commands.StaffAlertCommand;
import com.eu.habbo.habbohotel.commands.StaffOnlineCommand;
import com.eu.habbo.habbohotel.commands.StalkCommand;
import com.eu.habbo.habbohotel.commands.StandCommand;
import com.eu.habbo.habbohotel.commands.SubscriptionCommand;
import com.eu.habbo.habbohotel.commands.SummonCommand;
import com.eu.habbo.habbohotel.commands.SummonRankCommand;
import com.eu.habbo.habbohotel.commands.SuperPullCommand;
import com.eu.habbo.habbohotel.commands.SuperbanCommand;
import com.eu.habbo.habbohotel.commands.TakeBadgeCommand;
import com.eu.habbo.habbohotel.commands.TeleportCommand;
import com.eu.habbo.habbohotel.commands.TestCommand;
import com.eu.habbo.habbohotel.commands.TransformCommand;
import com.eu.habbo.habbohotel.commands.TrashCommand;
import com.eu.habbo.habbohotel.commands.UnbanCommand;
import com.eu.habbo.habbohotel.commands.UnloadRoomCommand;
import com.eu.habbo.habbohotel.commands.UnmuteCommand;
import com.eu.habbo.habbohotel.commands.UpdateAchievements;
import com.eu.habbo.habbohotel.commands.UpdateBotsCommand;
import com.eu.habbo.habbohotel.commands.UpdateCalendarCommand;
import com.eu.habbo.habbohotel.commands.UpdateCatalogCommand;
import com.eu.habbo.habbohotel.commands.UpdateConfigCommand;
import com.eu.habbo.habbohotel.commands.UpdateGuildPartsCommand;
import com.eu.habbo.habbohotel.commands.UpdateHotelViewCommand;
import com.eu.habbo.habbohotel.commands.UpdateItemsCommand;
import com.eu.habbo.habbohotel.commands.UpdateNavigatorCommand;
import com.eu.habbo.habbohotel.commands.UpdatePermissionsCommand;
import com.eu.habbo.habbohotel.commands.UpdatePetDataCommand;
import com.eu.habbo.habbohotel.commands.UpdatePluginsCommand;
import com.eu.habbo.habbohotel.commands.UpdatePollsCommand;
import com.eu.habbo.habbohotel.commands.UpdateTextsCommand;
import com.eu.habbo.habbohotel.commands.UpdateWordFilterCommand;
import com.eu.habbo.habbohotel.commands.UpdateYoutubePlaylistsCommand;
import com.eu.habbo.habbohotel.commands.UserInfoCommand;
import com.eu.habbo.habbohotel.commands.WordQuizCommand;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.permissions.PermissionSetting;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetCommand;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTypingComposer;
import com.eu.habbo.plugin.events.users.UserCommandEvent;
import com.eu.habbo.plugin.events.users.UserExecuteCommandEvent;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);
    private static final THashMap<String, Command> commands = new THashMap(5);
    private static final Comparator<Command> ALPHABETICAL_ORDER = new Comparator<Command>(){

        @Override
        public int compare(Command c1, Command c2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(c1.permission, c2.permission);
            return res != 0 ? res : c1.permission.compareTo(c2.permission);
        }
    };

    public CommandHandler() {
        long millis = System.currentTimeMillis();
        this.reloadCommands();
        LOGGER.info("Command Handler -> Loaded! ({} MS)", (Object)(System.currentTimeMillis() - millis));
    }

    public static void addCommand(Command command) {
        if (command == null) {
            return;
        }
        commands.put(command.getClass().getName(), command);
    }

    public static void addCommand(Class<? extends Command> command) {
        try {
            CommandHandler.addCommand(command.newInstance());
            LOGGER.debug("Added command: {}", (Object)command.getName());
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    public static boolean handleCommand(GameClient gameClient, String commandLine) {
        block18: {
            block19: {
                if (gameClient == null || commandLine == null) break block18;
                if (!commandLine.startsWith(":")) break block19;
                String[] parts = (commandLine = commandLine.replaceFirst(":", "")).split(" ");
                if (parts.length < 1) break block18;
                for (Command command : commands.values()) {
                    for (String s : command.keys) {
                        if (!s.equalsIgnoreCase(parts[0])) continue;
                        boolean succes = false;
                        if (command.permission == null || gameClient.getHabbo().hasPermission(command.permission, gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasRights(gameClient.getHabbo()) || gameClient.getHabbo().hasPermission(Permission.ACC_PLACEFURNI) || gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && gameClient.getHabbo().getHabboInfo().getCurrentRoom().getGuildId() > 0 && gameClient.getHabbo().getHabboInfo().getCurrentRoom().getGuildRightLevel(gameClient.getHabbo()).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS))) {
                            try {
                                UserExecuteCommandEvent userExecuteCommandEvent = new UserExecuteCommandEvent(gameClient.getHabbo(), command, parts);
                                Emulator.getPluginManager().fireEvent(userExecuteCommandEvent);
                                if (userExecuteCommandEvent.isCancelled()) {
                                    return userExecuteCommandEvent.isSuccess();
                                }
                                if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
                                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserTypingComposer(gameClient.getHabbo().getRoomUnit(), false).compose());
                                }
                                UserCommandEvent event = new UserCommandEvent(gameClient.getHabbo(), parts, command.handle(gameClient, parts));
                                Emulator.getPluginManager().fireEvent(event);
                                succes = event.succes;
                            }
                            catch (Exception e) {
                                LOGGER.error("Caught exception", e);
                            }
                            if (gameClient.getHabbo().getHabboInfo().getRank().isLogCommands()) {
                                Emulator.getDatabaseLogger().store(new CommandLog(gameClient.getHabbo().getHabboInfo().getId(), command, commandLine, succes));
                            }
                        }
                        return succes;
                    }
                }
                break block18;
            }
            String[] args = commandLine.split(" ");
            if (args.length <= 1) {
                return false;
            }
            if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
                Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
                if (room.getCurrentPets().isEmpty()) {
                    return false;
                }
                TIntObjectIterator<Pet> petIterator = room.getCurrentPets().iterator();
                int j = room.getCurrentPets().size();
                block6: while (j-- > 0) {
                    try {
                        petIterator.advance();
                    }
                    catch (NoSuchElementException e) {
                        break;
                    }
                    Pet pet = petIterator.value();
                    if (pet == null || !pet.getName().equalsIgnoreCase(args[0])) continue;
                    StringBuilder s = new StringBuilder();
                    for (int i = 1; i < args.length; ++i) {
                        s.append(args[i]).append(" ");
                    }
                    s = new StringBuilder(s.substring(0, s.length() - 1));
                    for (PetCommand command : pet.getPetData().getPetCommands()) {
                        if (!command.key.equalsIgnoreCase(s.toString())) continue;
                        if (pet instanceof RideablePet && ((RideablePet)pet).getRider() != null) {
                            if (((RideablePet)pet).getRider().getHabboInfo().getId() != gameClient.getHabbo().getHabboInfo().getId()) continue block6;
                            ((RideablePet)pet).getRider().getHabboInfo().dismountPet();
                            continue block6;
                        }
                        if (command.level <= pet.getLevel()) {
                            pet.handleCommand(command, gameClient.getHabbo(), args);
                            continue block6;
                        }
                        pet.say(pet.getPetData().randomVocal(PetVocalsType.UNKNOWN_COMMAND));
                        continue block6;
                    }
                }
            }
        }
        return false;
    }

    public static Command getCommand(String key) {
        for (Command command : commands.values()) {
            for (String k : command.keys) {
                if (!key.equalsIgnoreCase(k)) continue;
                return command;
            }
        }
        return null;
    }

    public void reloadCommands() {
        CommandHandler.addCommand(new AboutCommand());
        CommandHandler.addCommand(new AlertCommand());
        CommandHandler.addCommand(new AllowTradingCommand());
        CommandHandler.addCommand(new ArcturusCommand());
        CommandHandler.addCommand(new BadgeCommand());
        CommandHandler.addCommand(new BanCommand());
        CommandHandler.addCommand(new BlockAlertCommand());
        CommandHandler.addCommand(new BotsCommand());
        CommandHandler.addCommand(new CalendarCommand());
        CommandHandler.addCommand(new ChangeNameCommand());
        CommandHandler.addCommand(new ChatTypeCommand());
        CommandHandler.addCommand(new CommandsCommand());
        CommandHandler.addCommand(new ConnectCameraCommand());
        CommandHandler.addCommand(new ControlCommand());
        CommandHandler.addCommand(new CoordsCommand());
        CommandHandler.addCommand(new CreditsCommand());
        CommandHandler.addCommand(new DiagonalCommand());
        CommandHandler.addCommand(new DisconnectCommand());
        CommandHandler.addCommand(new EjectAllCommand());
        CommandHandler.addCommand(new EmptyInventoryCommand());
        CommandHandler.addCommand(new EmptyBotsInventoryCommand());
        CommandHandler.addCommand(new EmptyPetsInventoryCommand());
        CommandHandler.addCommand(new EnableCommand());
        CommandHandler.addCommand(new EventCommand());
        CommandHandler.addCommand(new FacelessCommand());
        CommandHandler.addCommand(new FastwalkCommand());
        CommandHandler.addCommand(new FilterWordCommand());
        CommandHandler.addCommand(new FreezeBotsCommand());
        CommandHandler.addCommand(new FreezeCommand());
        CommandHandler.addCommand(new GiftCommand());
        CommandHandler.addCommand(new GiveRankCommand());
        CommandHandler.addCommand(new HabnamCommand());
        CommandHandler.addCommand(new HandItemCommand());
        CommandHandler.addCommand(new HappyHourCommand());
        CommandHandler.addCommand(new HideWiredCommand());
        CommandHandler.addCommand(new HotelAlertCommand());
        CommandHandler.addCommand(new HotelAlertLinkCommand());
        CommandHandler.addCommand(new InvisibleCommand());
        CommandHandler.addCommand(new IPBanCommand());
        CommandHandler.addCommand(new LayCommand());
        CommandHandler.addCommand(new MachineBanCommand());
        CommandHandler.addCommand(new MassBadgeCommand());
        CommandHandler.addCommand(new RoomBadgeCommand());
        CommandHandler.addCommand(new MassCreditsCommand());
        CommandHandler.addCommand(new MassGiftCommand());
        CommandHandler.addCommand(new MassPixelsCommand());
        CommandHandler.addCommand(new MassPointsCommand());
        CommandHandler.addCommand(new MimicCommand());
        CommandHandler.addCommand(new MoonwalkCommand());
        CommandHandler.addCommand(new MultiCommand());
        CommandHandler.addCommand(new MuteBotsCommand());
        CommandHandler.addCommand(new MuteCommand());
        CommandHandler.addCommand(new MutePetsCommand());
        CommandHandler.addCommand(new PetInfoCommand());
        CommandHandler.addCommand(new PickallCommand());
        CommandHandler.addCommand(new PingCommand());
        CommandHandler.addCommand(new PixelCommand());
        CommandHandler.addCommand(new PluginsCommand());
        CommandHandler.addCommand(new PointsCommand());
        CommandHandler.addCommand(new PromoteTargetOfferCommand());
        CommandHandler.addCommand(new PullCommand());
        CommandHandler.addCommand(new PushCommand());
        CommandHandler.addCommand(new RedeemCommand());
        CommandHandler.addCommand(new ReloadRoomCommand());
        CommandHandler.addCommand(new RoomAlertCommand());
        CommandHandler.addCommand(new RoomBundleCommand());
        CommandHandler.addCommand(new RoomCreditsCommand());
        CommandHandler.addCommand(new RoomDanceCommand());
        CommandHandler.addCommand(new RoomEffectCommand());
        CommandHandler.addCommand(new RoomItemCommand());
        CommandHandler.addCommand(new RoomKickCommand());
        CommandHandler.addCommand(new RoomMuteCommand());
        CommandHandler.addCommand(new RoomPixelsCommand());
        CommandHandler.addCommand(new RoomPointsCommand());
        CommandHandler.addCommand(new SayAllCommand());
        CommandHandler.addCommand(new SayCommand());
        CommandHandler.addCommand(new SetMaxCommand());
        CommandHandler.addCommand(new SetPollCommand());
        CommandHandler.addCommand(new SetSpeedCommand());
        CommandHandler.addCommand(new ShoutAllCommand());
        CommandHandler.addCommand(new ShoutCommand());
        CommandHandler.addCommand(new ShutdownCommand());
        CommandHandler.addCommand(new SitCommand());
        CommandHandler.addCommand(new StandCommand());
        CommandHandler.addCommand(new SitDownCommand());
        CommandHandler.addCommand(new StaffAlertCommand());
        CommandHandler.addCommand(new StaffOnlineCommand());
        CommandHandler.addCommand(new StalkCommand());
        CommandHandler.addCommand(new SummonCommand());
        CommandHandler.addCommand(new SummonRankCommand());
        CommandHandler.addCommand(new SuperbanCommand());
        CommandHandler.addCommand(new SuperPullCommand());
        CommandHandler.addCommand(new TakeBadgeCommand());
        CommandHandler.addCommand(new TeleportCommand());
        CommandHandler.addCommand(new TransformCommand());
        CommandHandler.addCommand(new TrashCommand());
        CommandHandler.addCommand(new UnbanCommand());
        CommandHandler.addCommand(new UnloadRoomCommand());
        CommandHandler.addCommand(new UnmuteCommand());
        CommandHandler.addCommand(new UpdateAchievements());
        CommandHandler.addCommand(new UpdateBotsCommand());
        CommandHandler.addCommand(new UpdateCalendarCommand());
        CommandHandler.addCommand(new UpdateCatalogCommand());
        CommandHandler.addCommand(new UpdateConfigCommand());
        CommandHandler.addCommand(new UpdateGuildPartsCommand());
        CommandHandler.addCommand(new UpdateHotelViewCommand());
        CommandHandler.addCommand(new UpdateItemsCommand());
        CommandHandler.addCommand(new UpdateNavigatorCommand());
        CommandHandler.addCommand(new UpdatePermissionsCommand());
        CommandHandler.addCommand(new UpdatePetDataCommand());
        CommandHandler.addCommand(new UpdatePluginsCommand());
        CommandHandler.addCommand(new UpdatePollsCommand());
        CommandHandler.addCommand(new UpdateTextsCommand());
        CommandHandler.addCommand(new UpdateWordFilterCommand());
        CommandHandler.addCommand(new UserInfoCommand());
        CommandHandler.addCommand(new WordQuizCommand());
        CommandHandler.addCommand(new UpdateYoutubePlaylistsCommand());
        CommandHandler.addCommand(new AddYoutubePlaylistCommand());
        CommandHandler.addCommand(new SoftKickCommand());
        CommandHandler.addCommand(new SubscriptionCommand());
        CommandHandler.addCommand(new TestCommand());
    }

    public List<Command> getCommandsForRank(int rankId) {
        ArrayList<Command> allowedCommands = new ArrayList<Command>();
        if (Emulator.getGameEnvironment().getPermissionsManager().rankExists(rankId)) {
            THashMap<String, Permission> permissions = Emulator.getGameEnvironment().getPermissionsManager().getRank(rankId).getPermissions();
            for (Command command : commands.values()) {
                if (allowedCommands.contains(command) || !permissions.contains(command.permission) || permissions.get((Object)command.permission).setting == PermissionSetting.DISALLOWED) continue;
                allowedCommands.add(command);
            }
        }
        allowedCommands.sort(ALPHABETICAL_ORDER);
        return allowedCommands;
    }

    public void dispose() {
        commands.clear();
        LOGGER.info("Command Handler -> Disposed!");
    }
}

