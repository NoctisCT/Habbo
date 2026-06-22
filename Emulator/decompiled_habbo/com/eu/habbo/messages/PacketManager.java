/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ICallable;
import com.eu.habbo.messages.NoAuthMessage;
import com.eu.habbo.messages.PacketNames;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.incoming.achievements.RequestAchievementConfigurationEvent;
import com.eu.habbo.messages.incoming.achievements.RequestAchievementsEvent;
import com.eu.habbo.messages.incoming.ambassadors.AmbassadorAlertCommandEvent;
import com.eu.habbo.messages.incoming.ambassadors.AmbassadorVisitCommandEvent;
import com.eu.habbo.messages.incoming.camera.CameraPublishToWebEvent;
import com.eu.habbo.messages.incoming.camera.CameraPurchaseEvent;
import com.eu.habbo.messages.incoming.camera.CameraRoomPictureEvent;
import com.eu.habbo.messages.incoming.camera.CameraRoomThumbnailEvent;
import com.eu.habbo.messages.incoming.camera.RequestCameraConfigurationEvent;
import com.eu.habbo.messages.incoming.catalog.CatalogBuyClubDiscountEvent;
import com.eu.habbo.messages.incoming.catalog.CatalogBuyItemAsGiftEvent;
import com.eu.habbo.messages.incoming.catalog.CatalogBuyItemEvent;
import com.eu.habbo.messages.incoming.catalog.CatalogRequestClubDiscountEvent;
import com.eu.habbo.messages.incoming.catalog.CatalogSearchedItemEvent;
import com.eu.habbo.messages.incoming.catalog.CatalogSelectClubGiftEvent;
import com.eu.habbo.messages.incoming.catalog.CheckPetNameEvent;
import com.eu.habbo.messages.incoming.catalog.JukeBoxRequestTrackCodeEvent;
import com.eu.habbo.messages.incoming.catalog.JukeBoxRequestTrackDataEvent;
import com.eu.habbo.messages.incoming.catalog.PurchaseTargetOfferEvent;
import com.eu.habbo.messages.incoming.catalog.RedeemVoucherEvent;
import com.eu.habbo.messages.incoming.catalog.RequestCatalogIndexEvent;
import com.eu.habbo.messages.incoming.catalog.RequestCatalogModeEvent;
import com.eu.habbo.messages.incoming.catalog.RequestCatalogPageEvent;
import com.eu.habbo.messages.incoming.catalog.RequestClubDataEvent;
import com.eu.habbo.messages.incoming.catalog.RequestClubGiftsEvent;
import com.eu.habbo.messages.incoming.catalog.RequestDiscountEvent;
import com.eu.habbo.messages.incoming.catalog.RequestGiftConfigurationEvent;
import com.eu.habbo.messages.incoming.catalog.RequestMarketplaceConfigEvent;
import com.eu.habbo.messages.incoming.catalog.RequestPetBreedsEvent;
import com.eu.habbo.messages.incoming.catalog.TargetOfferStateEvent;
import com.eu.habbo.messages.incoming.catalog.marketplace.BuyItemEvent;
import com.eu.habbo.messages.incoming.catalog.marketplace.RequestCreditsEvent;
import com.eu.habbo.messages.incoming.catalog.marketplace.RequestItemInfoEvent;
import com.eu.habbo.messages.incoming.catalog.marketplace.RequestOffersEvent;
import com.eu.habbo.messages.incoming.catalog.marketplace.RequestOwnItemsEvent;
import com.eu.habbo.messages.incoming.catalog.marketplace.RequestSellItemEvent;
import com.eu.habbo.messages.incoming.catalog.marketplace.SellItemEvent;
import com.eu.habbo.messages.incoming.catalog.marketplace.TakeBackItemEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.OpenRecycleBoxEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.RecycleEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.ReloadRecyclerEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.RequestRecyclerLogicEvent;
import com.eu.habbo.messages.incoming.crafting.CraftingAddRecipeEvent;
import com.eu.habbo.messages.incoming.crafting.CraftingCraftItemEvent;
import com.eu.habbo.messages.incoming.crafting.CraftingCraftSecretEvent;
import com.eu.habbo.messages.incoming.crafting.RequestCraftingRecipesAvailableEvent;
import com.eu.habbo.messages.incoming.crafting.RequestCraftingRecipesEvent;
import com.eu.habbo.messages.incoming.events.calendar.AdventCalendarForceOpenEvent;
import com.eu.habbo.messages.incoming.events.calendar.AdventCalendarOpenDayEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorRequestBlockedTilesEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorRequestDoorSettingsEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorSaveEvent;
import com.eu.habbo.messages.incoming.friends.AcceptFriendRequestEvent;
import com.eu.habbo.messages.incoming.friends.ChangeRelationEvent;
import com.eu.habbo.messages.incoming.friends.DeclineFriendRequestEvent;
import com.eu.habbo.messages.incoming.friends.FindNewFriendsEvent;
import com.eu.habbo.messages.incoming.friends.FriendPrivateMessageEvent;
import com.eu.habbo.messages.incoming.friends.FriendRequestEvent;
import com.eu.habbo.messages.incoming.friends.InviteFriendsEvent;
import com.eu.habbo.messages.incoming.friends.RemoveFriendEvent;
import com.eu.habbo.messages.incoming.friends.RequestFriendRequestsEvent;
import com.eu.habbo.messages.incoming.friends.RequestFriendsEvent;
import com.eu.habbo.messages.incoming.friends.RequestInitFriendsEvent;
import com.eu.habbo.messages.incoming.friends.SearchUserEvent;
import com.eu.habbo.messages.incoming.friends.StalkFriendEvent;
import com.eu.habbo.messages.incoming.gamecenter.GameCenterEvent;
import com.eu.habbo.messages.incoming.gamecenter.GameCenterJoinGameEvent;
import com.eu.habbo.messages.incoming.gamecenter.GameCenterLeaveGameEvent;
import com.eu.habbo.messages.incoming.gamecenter.GameCenterLoadGameEvent;
import com.eu.habbo.messages.incoming.gamecenter.GameCenterRequestAccountStatusEvent;
import com.eu.habbo.messages.incoming.gamecenter.GameCenterRequestGameStatusEvent;
import com.eu.habbo.messages.incoming.gamecenter.GameCenterRequestGamesEvent;
import com.eu.habbo.messages.incoming.guardians.GuardianAcceptRequestEvent;
import com.eu.habbo.messages.incoming.guardians.GuardianNoUpdatesWantedEvent;
import com.eu.habbo.messages.incoming.guardians.GuardianVoteEvent;
import com.eu.habbo.messages.incoming.guides.GuideCancelHelpRequestEvent;
import com.eu.habbo.messages.incoming.guides.GuideCloseHelpRequestEvent;
import com.eu.habbo.messages.incoming.guides.GuideHandleHelpRequestEvent;
import com.eu.habbo.messages.incoming.guides.GuideInviteUserEvent;
import com.eu.habbo.messages.incoming.guides.GuideRecommendHelperEvent;
import com.eu.habbo.messages.incoming.guides.GuideReportHelperEvent;
import com.eu.habbo.messages.incoming.guides.GuideUserMessageEvent;
import com.eu.habbo.messages.incoming.guides.GuideUserTypingEvent;
import com.eu.habbo.messages.incoming.guides.GuideVisitUserEvent;
import com.eu.habbo.messages.incoming.guides.RequestGuideAssistanceEvent;
import com.eu.habbo.messages.incoming.guides.RequestGuideToolEvent;
import com.eu.habbo.messages.incoming.guilds.GetHabboGuildBadgesMessageEvent;
import com.eu.habbo.messages.incoming.guilds.GuildAcceptMembershipEvent;
import com.eu.habbo.messages.incoming.guilds.GuildChangeBadgeEvent;
import com.eu.habbo.messages.incoming.guilds.GuildChangeColorsEvent;
import com.eu.habbo.messages.incoming.guilds.GuildChangeNameDescEvent;
import com.eu.habbo.messages.incoming.guilds.GuildChangeSettingsEvent;
import com.eu.habbo.messages.incoming.guilds.GuildConfirmRemoveMemberEvent;
import com.eu.habbo.messages.incoming.guilds.GuildDeclineMembershipEvent;
import com.eu.habbo.messages.incoming.guilds.GuildDeleteEvent;
import com.eu.habbo.messages.incoming.guilds.GuildRemoveAdminEvent;
import com.eu.habbo.messages.incoming.guilds.GuildRemoveFavoriteEvent;
import com.eu.habbo.messages.incoming.guilds.GuildRemoveMemberEvent;
import com.eu.habbo.messages.incoming.guilds.GuildSetAdminEvent;
import com.eu.habbo.messages.incoming.guilds.GuildSetFavoriteEvent;
import com.eu.habbo.messages.incoming.guilds.RequestGuildBuyEvent;
import com.eu.habbo.messages.incoming.guilds.RequestGuildBuyRoomsEvent;
import com.eu.habbo.messages.incoming.guilds.RequestGuildFurniWidgetEvent;
import com.eu.habbo.messages.incoming.guilds.RequestGuildInfoEvent;
import com.eu.habbo.messages.incoming.guilds.RequestGuildJoinEvent;
import com.eu.habbo.messages.incoming.guilds.RequestGuildManageEvent;
import com.eu.habbo.messages.incoming.guilds.RequestGuildMembersEvent;
import com.eu.habbo.messages.incoming.guilds.RequestGuildPartsEvent;
import com.eu.habbo.messages.incoming.guilds.RequestOwnGuildsEvent;
import com.eu.habbo.messages.incoming.guilds.forums.GuildForumDataEvent;
import com.eu.habbo.messages.incoming.guilds.forums.GuildForumListEvent;
import com.eu.habbo.messages.incoming.guilds.forums.GuildForumModerateMessageEvent;
import com.eu.habbo.messages.incoming.guilds.forums.GuildForumModerateThreadEvent;
import com.eu.habbo.messages.incoming.guilds.forums.GuildForumPostThreadEvent;
import com.eu.habbo.messages.incoming.guilds.forums.GuildForumThreadUpdateEvent;
import com.eu.habbo.messages.incoming.guilds.forums.GuildForumThreadsEvent;
import com.eu.habbo.messages.incoming.guilds.forums.GuildForumThreadsMessagesEvent;
import com.eu.habbo.messages.incoming.guilds.forums.GuildForumUpdateSettingsEvent;
import com.eu.habbo.messages.incoming.handshake.CompleteDiffieHandshakeEvent;
import com.eu.habbo.messages.incoming.handshake.InitDiffieHandshakeEvent;
import com.eu.habbo.messages.incoming.handshake.MachineIDEvent;
import com.eu.habbo.messages.incoming.handshake.PingEvent;
import com.eu.habbo.messages.incoming.handshake.ReleaseVersionEvent;
import com.eu.habbo.messages.incoming.handshake.SecureLoginEvent;
import com.eu.habbo.messages.incoming.handshake.UsernameEvent;
import com.eu.habbo.messages.incoming.helper.MySanctionStatusEvent;
import com.eu.habbo.messages.incoming.helper.RequestTalentTrackEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewClaimBadgeRewardEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewDataEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewRequestBadgeRewardEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewRequestBonusRareEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewRequestLTDAvailabilityEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewRequestSecondsUntilEvent;
import com.eu.habbo.messages.incoming.hotelview.RequestNewsListEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryBadgesEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryBotsEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryItemsDelete;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryItemsEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryPetsEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolAlertEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolChangeRoomSettingsEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolCloseTicketEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolIssueChangeTopicEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolIssueDefaultSanctionEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolKickEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolPickTicketEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolReleaseTicketEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolRequestIssueChatlogEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolRequestRoomChatlogEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolRequestRoomInfoEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolRequestRoomUserChatlogEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolRequestRoomVisitsEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolRequestUserChatlogEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolRequestUserInfoEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolRoomAlertEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolSanctionAlertEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolSanctionBanEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolSanctionMuteEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolSanctionTradeLockEvent;
import com.eu.habbo.messages.incoming.modtool.ModToolWarnEvent;
import com.eu.habbo.messages.incoming.modtool.ReportBullyEvent;
import com.eu.habbo.messages.incoming.modtool.ReportCommentEvent;
import com.eu.habbo.messages.incoming.modtool.ReportEvent;
import com.eu.habbo.messages.incoming.modtool.ReportFriendPrivateChatEvent;
import com.eu.habbo.messages.incoming.modtool.ReportPhotoEvent;
import com.eu.habbo.messages.incoming.modtool.ReportThreadEvent;
import com.eu.habbo.messages.incoming.modtool.RequestReportRoomEvent;
import com.eu.habbo.messages.incoming.modtool.RequestReportUserBullyingEvent;
import com.eu.habbo.messages.incoming.navigator.AddSavedSearchEvent;
import com.eu.habbo.messages.incoming.navigator.DeleteSavedSearchEvent;
import com.eu.habbo.messages.incoming.navigator.NavigatorCategoryListModeEvent;
import com.eu.habbo.messages.incoming.navigator.NavigatorCollapseCategoryEvent;
import com.eu.habbo.messages.incoming.navigator.NavigatorUncollapseCategoryEvent;
import com.eu.habbo.messages.incoming.navigator.NewNavigatorActionEvent;
import com.eu.habbo.messages.incoming.navigator.RequestCanCreateRoomEvent;
import com.eu.habbo.messages.incoming.navigator.RequestCreateRoomEvent;
import com.eu.habbo.messages.incoming.navigator.RequestDeleteRoomEvent;
import com.eu.habbo.messages.incoming.navigator.RequestHighestScoreRoomsEvent;
import com.eu.habbo.messages.incoming.navigator.RequestMyRoomsEvent;
import com.eu.habbo.messages.incoming.navigator.RequestNavigatorSettingsEvent;
import com.eu.habbo.messages.incoming.navigator.RequestNewNavigatorDataEvent;
import com.eu.habbo.messages.incoming.navigator.RequestNewNavigatorRoomsEvent;
import com.eu.habbo.messages.incoming.navigator.RequestPopularRoomsEvent;
import com.eu.habbo.messages.incoming.navigator.RequestPromotedRoomsEvent;
import com.eu.habbo.messages.incoming.navigator.RequestRoomCategoriesEvent;
import com.eu.habbo.messages.incoming.navigator.RequestTagsEvent;
import com.eu.habbo.messages.incoming.navigator.SaveWindowSettingsEvent;
import com.eu.habbo.messages.incoming.navigator.SearchRoomsByTagEvent;
import com.eu.habbo.messages.incoming.navigator.SearchRoomsEvent;
import com.eu.habbo.messages.incoming.navigator.SearchRoomsFriendsNowEvent;
import com.eu.habbo.messages.incoming.navigator.SearchRoomsFriendsOwnEvent;
import com.eu.habbo.messages.incoming.navigator.SearchRoomsInGroupEvent;
import com.eu.habbo.messages.incoming.navigator.SearchRoomsMyFavouriteEvent;
import com.eu.habbo.messages.incoming.navigator.SearchRoomsVisitedEvent;
import com.eu.habbo.messages.incoming.navigator.SearchRoomsWithRightsEvent;
import com.eu.habbo.messages.incoming.polls.AnswerPollEvent;
import com.eu.habbo.messages.incoming.polls.CancelPollEvent;
import com.eu.habbo.messages.incoming.polls.GetPollDataEvent;
import com.eu.habbo.messages.incoming.rooms.HandleDoorbellEvent;
import com.eu.habbo.messages.incoming.rooms.RequestRoomDataEvent;
import com.eu.habbo.messages.incoming.rooms.RequestRoomHeightmapEvent;
import com.eu.habbo.messages.incoming.rooms.RequestRoomLoadEvent;
import com.eu.habbo.messages.incoming.rooms.RequestRoomRightsEvent;
import com.eu.habbo.messages.incoming.rooms.RequestRoomSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.RequestRoomWordFilterEvent;
import com.eu.habbo.messages.incoming.rooms.RoomBackgroundEvent;
import com.eu.habbo.messages.incoming.rooms.RoomFavoriteEvent;
import com.eu.habbo.messages.incoming.rooms.RoomMuteEvent;
import com.eu.habbo.messages.incoming.rooms.RoomPlacePaintEvent;
import com.eu.habbo.messages.incoming.rooms.RoomRemoveAllRightsEvent;
import com.eu.habbo.messages.incoming.rooms.RoomRemoveRightsEvent;
import com.eu.habbo.messages.incoming.rooms.RoomRequestBannedUsersEvent;
import com.eu.habbo.messages.incoming.rooms.RoomSettingsSaveEvent;
import com.eu.habbo.messages.incoming.rooms.RoomStaffPickEvent;
import com.eu.habbo.messages.incoming.rooms.RoomUnFavoriteEvent;
import com.eu.habbo.messages.incoming.rooms.RoomVoteEvent;
import com.eu.habbo.messages.incoming.rooms.RoomWordFilterModifyEvent;
import com.eu.habbo.messages.incoming.rooms.SetHomeRoomEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotPickupEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotPlaceEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotSaveSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.items.AdvertisingSaveEvent;
import com.eu.habbo.messages.incoming.rooms.items.CloseDiceEvent;
import com.eu.habbo.messages.incoming.rooms.items.FootballGateSaveLookEvent;
import com.eu.habbo.messages.incoming.rooms.items.MannequinSaveLookEvent;
import com.eu.habbo.messages.incoming.rooms.items.MannequinSaveNameEvent;
import com.eu.habbo.messages.incoming.rooms.items.MoodLightSaveSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.items.MoodLightSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.items.MoodLightTurnOnEvent;
import com.eu.habbo.messages.incoming.rooms.items.MoveWallItemEvent;
import com.eu.habbo.messages.incoming.rooms.items.PostItDeleteEvent;
import com.eu.habbo.messages.incoming.rooms.items.PostItPlaceEvent;
import com.eu.habbo.messages.incoming.rooms.items.PostItRequestDataEvent;
import com.eu.habbo.messages.incoming.rooms.items.PostItSaveDataEvent;
import com.eu.habbo.messages.incoming.rooms.items.RedeemClothingEvent;
import com.eu.habbo.messages.incoming.rooms.items.RedeemItemEvent;
import com.eu.habbo.messages.incoming.rooms.items.RoomPickupChooserEvent;
import com.eu.habbo.messages.incoming.rooms.items.RoomPickupItemEvent;
import com.eu.habbo.messages.incoming.rooms.items.RoomPlaceItemEvent;
import com.eu.habbo.messages.incoming.rooms.items.RotateMoveItemEvent;
import com.eu.habbo.messages.incoming.rooms.items.SavePostItStickyPoleEvent;
import com.eu.habbo.messages.incoming.rooms.items.SetStackHelperHeightEvent;
import com.eu.habbo.messages.incoming.rooms.items.ToggleFloorItemEvent;
import com.eu.habbo.messages.incoming.rooms.items.ToggleWallItemEvent;
import com.eu.habbo.messages.incoming.rooms.items.TriggerColorWheelEvent;
import com.eu.habbo.messages.incoming.rooms.items.TriggerDiceEvent;
import com.eu.habbo.messages.incoming.rooms.items.TriggerOneWayGateEvent;
import com.eu.habbo.messages.incoming.rooms.items.UseRandomStateItemEvent;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.JukeBoxAddSoundTrackEvent;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.JukeBoxEventOne;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.JukeBoxEventTwo;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.JukeBoxRemoveSoundTrackEvent;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.JukeBoxRequestPlayListEvent;
import com.eu.habbo.messages.incoming.rooms.items.lovelock.LoveLockStartConfirmEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentSpaceCancelEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentSpaceEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestPlaylistChange;
import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestPlaylists;
import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestStateChange;
import com.eu.habbo.messages.incoming.rooms.pets.BreedMonsterplantsEvent;
import com.eu.habbo.messages.incoming.rooms.pets.CompostMonsterplantEvent;
import com.eu.habbo.messages.incoming.rooms.pets.ConfirmPetBreedingEvent;
import com.eu.habbo.messages.incoming.rooms.pets.HorseRemoveSaddleEvent;
import com.eu.habbo.messages.incoming.rooms.pets.MovePetEvent;
import com.eu.habbo.messages.incoming.rooms.pets.PetPackageNameEvent;
import com.eu.habbo.messages.incoming.rooms.pets.PetPickupEvent;
import com.eu.habbo.messages.incoming.rooms.pets.PetPlaceEvent;
import com.eu.habbo.messages.incoming.rooms.pets.PetRideEvent;
import com.eu.habbo.messages.incoming.rooms.pets.PetRideSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.pets.PetUseItemEvent;
import com.eu.habbo.messages.incoming.rooms.pets.RequestPetInformationEvent;
import com.eu.habbo.messages.incoming.rooms.pets.RequestPetTrainingPanelEvent;
import com.eu.habbo.messages.incoming.rooms.pets.ScratchPetEvent;
import com.eu.habbo.messages.incoming.rooms.pets.StopBreedingEvent;
import com.eu.habbo.messages.incoming.rooms.pets.ToggleMonsterplantBreedableEvent;
import com.eu.habbo.messages.incoming.rooms.promotions.BuyRoomPromotionEvent;
import com.eu.habbo.messages.incoming.rooms.promotions.RequestPromotionRoomsEvent;
import com.eu.habbo.messages.incoming.rooms.promotions.UpdateRoomPromotionEvent;
import com.eu.habbo.messages.incoming.rooms.users.IgnoreRoomUserEvent;
import com.eu.habbo.messages.incoming.rooms.users.RequestRoomUserTagsEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserActionEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserBanEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserDanceEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserDropHandItemEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserGiveHandItemEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserGiveRespectEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserGiveRightsEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserKickEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserLookAtPoint;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserMuteEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserRemoveRightsEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserShoutEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserSignEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserSitEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserStartTypingEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserStopTypingEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserTalkEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserWalkEvent;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserWhisperEvent;
import com.eu.habbo.messages.incoming.rooms.users.UnIgnoreRoomUserEvent;
import com.eu.habbo.messages.incoming.rooms.users.UnbanRoomUserEvent;
import com.eu.habbo.messages.incoming.trading.TradeAcceptEvent;
import com.eu.habbo.messages.incoming.trading.TradeCancelEvent;
import com.eu.habbo.messages.incoming.trading.TradeCancelOfferItemEvent;
import com.eu.habbo.messages.incoming.trading.TradeCloseEvent;
import com.eu.habbo.messages.incoming.trading.TradeConfirmEvent;
import com.eu.habbo.messages.incoming.trading.TradeOfferItemEvent;
import com.eu.habbo.messages.incoming.trading.TradeOfferMultipleItemsEvent;
import com.eu.habbo.messages.incoming.trading.TradeStartEvent;
import com.eu.habbo.messages.incoming.trading.TradeUnAcceptEvent;
import com.eu.habbo.messages.incoming.unknown.RequestResolutionEvent;
import com.eu.habbo.messages.incoming.unknown.UnknownEvent1;
import com.eu.habbo.messages.incoming.users.ActivateEffectEvent;
import com.eu.habbo.messages.incoming.users.ChangeChatBubbleEvent;
import com.eu.habbo.messages.incoming.users.ChangeNameCheckUsernameEvent;
import com.eu.habbo.messages.incoming.users.ConfirmChangeNameEvent;
import com.eu.habbo.messages.incoming.users.EnableEffectEvent;
import com.eu.habbo.messages.incoming.users.PickNewUserGiftEvent;
import com.eu.habbo.messages.incoming.users.RequestClubCenterEvent;
import com.eu.habbo.messages.incoming.users.RequestMeMenuSettingsEvent;
import com.eu.habbo.messages.incoming.users.RequestProfileFriendsEvent;
import com.eu.habbo.messages.incoming.users.RequestUserCitizinShipEvent;
import com.eu.habbo.messages.incoming.users.RequestUserClubEvent;
import com.eu.habbo.messages.incoming.users.RequestUserCreditsEvent;
import com.eu.habbo.messages.incoming.users.RequestUserDataEvent;
import com.eu.habbo.messages.incoming.users.RequestUserProfileEvent;
import com.eu.habbo.messages.incoming.users.RequestUserWardrobeEvent;
import com.eu.habbo.messages.incoming.users.RequestWearingBadgesEvent;
import com.eu.habbo.messages.incoming.users.SaveBlockCameraFollowEvent;
import com.eu.habbo.messages.incoming.users.SaveIgnoreRoomInvitesEvent;
import com.eu.habbo.messages.incoming.users.SaveMottoEvent;
import com.eu.habbo.messages.incoming.users.SavePreferOldChatEvent;
import com.eu.habbo.messages.incoming.users.SaveUserVolumesEvent;
import com.eu.habbo.messages.incoming.users.SaveWardrobeEvent;
import com.eu.habbo.messages.incoming.users.UpdateUIFlagsEvent;
import com.eu.habbo.messages.incoming.users.UserActivityEvent;
import com.eu.habbo.messages.incoming.users.UserNuxEvent;
import com.eu.habbo.messages.incoming.users.UserSaveLookEvent;
import com.eu.habbo.messages.incoming.users.UserWearBadgeEvent;
import com.eu.habbo.messages.incoming.wired.WiredApplySetConditionsEvent;
import com.eu.habbo.messages.incoming.wired.WiredConditionSaveDataEvent;
import com.eu.habbo.messages.incoming.wired.WiredEffectSaveDataEvent;
import com.eu.habbo.messages.incoming.wired.WiredTriggerSaveDataEvent;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketManager.class);
    private static final List<Integer> logList = new ArrayList<Integer>();
    public static boolean DEBUG_SHOW_PACKETS = false;
    public static boolean MULTI_THREADED_PACKET_HANDLING = false;
    private final THashMap<Integer, Class<? extends MessageHandler>> incoming = new THashMap();
    private final THashMap<Integer, List<ICallable>> callables = new THashMap();
    private final PacketNames names = new PacketNames();

    public PacketManager() throws Exception {
        this.names.initialize();
        this.registerHandshake();
        this.registerCatalog();
        this.registerEvent();
        this.registerFriends();
        this.registerNavigator();
        this.registerUsers();
        this.registerHotelview();
        this.registerInventory();
        this.registerRooms();
        this.registerPolls();
        this.registerUnknown();
        this.registerModTool();
        this.registerTrading();
        this.registerGuilds();
        this.registerPets();
        this.registerWired();
        this.registerAchievements();
        this.registerFloorPlanEditor();
        this.registerAmbassadors();
        this.registerGuides();
        this.registerCrafting();
        this.registerCamera();
        this.registerGameCenter();
    }

    public PacketNames getNames() {
        return this.names;
    }

    @EventHandler
    public static void onConfigurationUpdated(EmulatorConfigUpdatedEvent event) {
        logList.clear();
        for (String s : Emulator.getConfig().getValue("debug.show.headers").split(";")) {
            try {
                logList.add(Integer.parseInt(s));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
    }

    public void registerHandler(Integer header, Class<? extends MessageHandler> handler) throws Exception {
        if (header < 0) {
            return;
        }
        if (this.incoming.containsKey(header)) {
            throw new Exception("Header already registered. Failed to register " + handler.getName() + " with header " + header);
        }
        this.incoming.putIfAbsent(header, handler);
    }

    public void registerCallable(Integer header, ICallable callable) {
        this.callables.putIfAbsent(header, new ArrayList());
        this.callables.get(header).add(callable);
    }

    public void unregisterCallables(Integer header, ICallable callable) {
        if (this.callables.containsKey(header)) {
            this.callables.get(header).remove(callable);
        }
    }

    public void unregisterCallables(Integer header) {
        if (this.callables.containsKey(header)) {
            this.callables.clear();
        }
    }

    public void handlePacket(GameClient client, ClientMessage packet) {
        if (client == null || Emulator.isShuttingDown) {
            return;
        }
        try {
            if (this.isRegistered(packet.getMessageId())) {
                Class<? extends MessageHandler> handlerClass = this.incoming.get(packet.getMessageId());
                if (handlerClass == null) {
                    throw new Exception("Unknown message " + packet.getMessageId());
                }
                if (client.getHabbo() == null && !handlerClass.isAnnotationPresent(NoAuthMessage.class)) {
                    if (DEBUG_SHOW_PACKETS) {
                        LOGGER.warn("Client packet {} requires an authenticated session.", (Object)packet.getMessageId());
                    }
                    return;
                }
                MessageHandler handler = handlerClass.newInstance();
                if (handler.getRatelimit() > 0) {
                    if (client.messageTimestamps.containsKey(handlerClass) && System.currentTimeMillis() - client.messageTimestamps.get(handlerClass) < (long)handler.getRatelimit()) {
                        if (DEBUG_SHOW_PACKETS) {
                            LOGGER.warn("Client packet {} was ratelimited.", (Object)packet.getMessageId());
                        }
                        return;
                    }
                    client.messageTimestamps.put(handlerClass, System.currentTimeMillis());
                }
                if (logList.contains(packet.getMessageId()) && client.getHabbo() != null) {
                    LOGGER.info("User {} sent packet {} with body {}", client.getHabbo().getHabboInfo().getUsername(), packet.getMessageId(), packet.getMessageBody());
                }
                handler.client = client;
                handler.packet = packet;
                if (this.callables.containsKey(packet.getMessageId())) {
                    for (ICallable callable : this.callables.get(packet.getMessageId())) {
                        callable.call(handler);
                    }
                }
                if (!handler.isCancelled) {
                    handler.handle();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    boolean isRegistered(int header) {
        return this.incoming.containsKey(header);
    }

    private void registerAmbassadors() throws Exception {
        this.registerHandler(2996, AmbassadorAlertCommandEvent.class);
        this.registerHandler(2970, AmbassadorVisitCommandEvent.class);
    }

    private void registerCatalog() throws Exception {
        this.registerHandler(398, RequestRecyclerLogicEvent.class);
        this.registerHandler(223, RequestDiscountEvent.class);
        this.registerHandler(418, RequestGiftConfigurationEvent.class);
        this.registerHandler(2597, RequestMarketplaceConfigEvent.class);
        this.registerHandler(1195, RequestCatalogModeEvent.class);
        this.registerHandler(2529, RequestCatalogIndexEvent.class);
        this.registerHandler(412, RequestCatalogPageEvent.class);
        this.registerHandler(1411, CatalogBuyItemAsGiftEvent.class);
        this.registerHandler(3492, CatalogBuyItemEvent.class);
        this.registerHandler(339, RedeemVoucherEvent.class);
        this.registerHandler(1342, ReloadRecyclerEvent.class);
        this.registerHandler(2771, RecycleEvent.class);
        this.registerHandler(3558, OpenRecycleBoxEvent.class);
        this.registerHandler(2105, RequestOwnItemsEvent.class);
        this.registerHandler(434, TakeBackItemEvent.class);
        this.registerHandler(2407, RequestOffersEvent.class);
        this.registerHandler(3288, RequestItemInfoEvent.class);
        this.registerHandler(1603, BuyItemEvent.class);
        this.registerHandler(848, RequestSellItemEvent.class);
        this.registerHandler(3447, SellItemEvent.class);
        this.registerHandler(2650, RequestCreditsEvent.class);
        this.registerHandler(1756, RequestPetBreedsEvent.class);
        this.registerHandler(2109, CheckPetNameEvent.class);
        this.registerHandler(3285, RequestClubDataEvent.class);
        this.registerHandler(487, RequestClubGiftsEvent.class);
        this.registerHandler(2594, CatalogSearchedItemEvent.class);
        this.registerHandler(1826, PurchaseTargetOfferEvent.class);
        this.registerHandler(2041, TargetOfferStateEvent.class);
        this.registerHandler(2276, CatalogSelectClubGiftEvent.class);
        this.registerHandler(869, RequestClubCenterEvent.class);
        this.registerHandler(2462, CatalogRequestClubDiscountEvent.class);
        this.registerHandler(3407, CatalogBuyClubDiscountEvent.class);
    }

    private void registerEvent() throws Exception {
        this.registerHandler(2257, AdventCalendarOpenDayEvent.class);
        this.registerHandler(3889, AdventCalendarForceOpenEvent.class);
    }

    private void registerHandshake() throws Exception {
        this.registerHandler(4000, ReleaseVersionEvent.class);
        this.registerHandler(3110, InitDiffieHandshakeEvent.class);
        this.registerHandler(773, CompleteDiffieHandshakeEvent.class);
        this.registerHandler(2419, SecureLoginEvent.class);
        this.registerHandler(2490, MachineIDEvent.class);
        this.registerHandler(3878, UsernameEvent.class);
        this.registerHandler(295, PingEvent.class);
    }

    private void registerFriends() throws Exception {
        this.registerHandler(1523, RequestFriendsEvent.class);
        this.registerHandler(3768, ChangeRelationEvent.class);
        this.registerHandler(1689, RemoveFriendEvent.class);
        this.registerHandler(1210, SearchUserEvent.class);
        this.registerHandler(3157, FriendRequestEvent.class);
        this.registerHandler(137, AcceptFriendRequestEvent.class);
        this.registerHandler(2890, DeclineFriendRequestEvent.class);
        this.registerHandler(3567, FriendPrivateMessageEvent.class);
        this.registerHandler(2448, RequestFriendRequestsEvent.class);
        this.registerHandler(3997, StalkFriendEvent.class);
        this.registerHandler(2781, RequestInitFriendsEvent.class);
        this.registerHandler(516, FindNewFriendsEvent.class);
        this.registerHandler(1276, InviteFriendsEvent.class);
    }

    private void registerUsers() throws Exception {
        this.registerHandler(357, RequestUserDataEvent.class);
        this.registerHandler(273, RequestUserCreditsEvent.class);
        this.registerHandler(3166, RequestUserClubEvent.class);
        this.registerHandler(2388, RequestMeMenuSettingsEvent.class);
        this.registerHandler(2127, RequestUserCitizinShipEvent.class);
        this.registerHandler(3265, RequestUserProfileEvent.class);
        this.registerHandler(2138, RequestProfileFriendsEvent.class);
        this.registerHandler(2742, RequestUserWardrobeEvent.class);
        this.registerHandler(800, SaveWardrobeEvent.class);
        this.registerHandler(2228, SaveMottoEvent.class);
        this.registerHandler(2730, UserSaveLookEvent.class);
        this.registerHandler(644, UserWearBadgeEvent.class);
        this.registerHandler(2091, RequestWearingBadgesEvent.class);
        this.registerHandler(1367, SaveUserVolumesEvent.class);
        this.registerHandler(1461, SaveBlockCameraFollowEvent.class);
        this.registerHandler(1086, SaveIgnoreRoomInvitesEvent.class);
        this.registerHandler(1262, SavePreferOldChatEvent.class);
        this.registerHandler(2959, ActivateEffectEvent.class);
        this.registerHandler(1752, EnableEffectEvent.class);
        this.registerHandler(3457, UserActivityEvent.class);
        this.registerHandler(1299, UserNuxEvent.class);
        this.registerHandler(1822, PickNewUserGiftEvent.class);
        this.registerHandler(3950, ChangeNameCheckUsernameEvent.class);
        this.registerHandler(2977, ConfirmChangeNameEvent.class);
        this.registerHandler(1030, ChangeChatBubbleEvent.class);
        this.registerHandler(2313, UpdateUIFlagsEvent.class);
    }

    private void registerNavigator() throws Exception {
        this.registerHandler(3027, RequestRoomCategoriesEvent.class);
        this.registerHandler(2758, RequestPopularRoomsEvent.class);
        this.registerHandler(2939, RequestHighestScoreRoomsEvent.class);
        this.registerHandler(2277, RequestMyRoomsEvent.class);
        this.registerHandler(2128, RequestCanCreateRoomEvent.class);
        this.registerHandler(2908, RequestPromotedRoomsEvent.class);
        this.registerHandler(2752, RequestCreateRoomEvent.class);
        this.registerHandler(826, RequestTagsEvent.class);
        this.registerHandler(-1, SearchRoomsByTagEvent.class);
        this.registerHandler(3943, SearchRoomsEvent.class);
        this.registerHandler(1786, SearchRoomsFriendsNowEvent.class);
        this.registerHandler(2266, SearchRoomsFriendsOwnEvent.class);
        this.registerHandler(272, SearchRoomsWithRightsEvent.class);
        this.registerHandler(39, SearchRoomsInGroupEvent.class);
        this.registerHandler(2578, SearchRoomsMyFavouriteEvent.class);
        this.registerHandler(2264, SearchRoomsVisitedEvent.class);
        this.registerHandler(2110, RequestNewNavigatorDataEvent.class);
        this.registerHandler(249, RequestNewNavigatorRoomsEvent.class);
        this.registerHandler(1703, NewNavigatorActionEvent.class);
        this.registerHandler(1782, RequestNavigatorSettingsEvent.class);
        this.registerHandler(3159, SaveWindowSettingsEvent.class);
        this.registerHandler(532, RequestDeleteRoomEvent.class);
        this.registerHandler(1202, NavigatorCategoryListModeEvent.class);
        this.registerHandler(1834, NavigatorCollapseCategoryEvent.class);
        this.registerHandler(637, NavigatorUncollapseCategoryEvent.class);
        this.registerHandler(2226, AddSavedSearchEvent.class);
        this.registerHandler(1954, DeleteSavedSearchEvent.class);
    }

    private void registerHotelview() throws Exception {
        this.registerHandler(105, HotelViewEvent.class);
        this.registerHandler(957, HotelViewRequestBonusRareEvent.class);
        this.registerHandler(1827, RequestNewsListEvent.class);
        this.registerHandler(2912, HotelViewDataEvent.class);
        this.registerHandler(2318, HotelViewRequestBadgeRewardEvent.class);
        this.registerHandler(-1, HotelViewClaimBadgeRewardEvent.class);
        this.registerHandler(410, HotelViewRequestLTDAvailabilityEvent.class);
        this.registerHandler(271, HotelViewRequestSecondsUntilEvent.class);
    }

    private void registerInventory() throws Exception {
        this.registerHandler(2769, RequestInventoryBadgesEvent.class);
        this.registerHandler(3848, RequestInventoryBotsEvent.class);
        this.registerHandler(10018, RequestInventoryItemsDelete.class);
        this.registerHandler(3150, RequestInventoryItemsEvent.class);
        this.registerHandler(3500, RequestInventoryItemsEvent.class);
        this.registerHandler(3095, RequestInventoryPetsEvent.class);
    }

    void registerRooms() throws Exception {
        this.registerHandler(2312, RequestRoomLoadEvent.class);
        this.registerHandler(3898, RequestRoomHeightmapEvent.class);
        this.registerHandler(2300, RequestRoomHeightmapEvent.class);
        this.registerHandler(3582, RoomVoteEvent.class);
        this.registerHandler(2230, RequestRoomDataEvent.class);
        this.registerHandler(1969, RoomSettingsSaveEvent.class);
        this.registerHandler(1258, RoomPlaceItemEvent.class);
        this.registerHandler(248, RotateMoveItemEvent.class);
        this.registerHandler(168, MoveWallItemEvent.class);
        this.registerHandler(3456, RoomPickupItemEvent.class);
        this.registerHandler(10017, RoomPickupChooserEvent.class);
        this.registerHandler(711, RoomPlacePaintEvent.class);
        this.registerHandler(1597, RoomUserStartTypingEvent.class);
        this.registerHandler(1474, RoomUserStopTypingEvent.class);
        this.registerHandler(99, ToggleFloorItemEvent.class);
        this.registerHandler(210, ToggleWallItemEvent.class);
        this.registerHandler(2880, RoomBackgroundEvent.class);
        this.registerHandler(2850, MannequinSaveNameEvent.class);
        this.registerHandler(2209, MannequinSaveLookEvent.class);
        this.registerHandler(924, FootballGateSaveLookEvent.class);
        this.registerHandler(3608, AdvertisingSaveEvent.class);
        this.registerHandler(3129, RequestRoomSettingsEvent.class);
        this.registerHandler(2813, MoodLightSettingsEvent.class);
        this.registerHandler(2296, MoodLightTurnOnEvent.class);
        this.registerHandler(2814, RoomUserDropHandItemEvent.class);
        this.registerHandler(3301, RoomUserLookAtPoint.class);
        this.registerHandler(1314, RoomUserTalkEvent.class);
        this.registerHandler(2085, RoomUserShoutEvent.class);
        this.registerHandler(1543, RoomUserWhisperEvent.class);
        this.registerHandler(2456, RoomUserActionEvent.class);
        this.registerHandler(2235, RoomUserSitEvent.class);
        this.registerHandler(2080, RoomUserDanceEvent.class);
        this.registerHandler(1975, RoomUserSignEvent.class);
        this.registerHandler(3320, RoomUserWalkEvent.class);
        this.registerHandler(2694, RoomUserGiveRespectEvent.class);
        this.registerHandler(808, RoomUserGiveRightsEvent.class);
        this.registerHandler(3182, RoomRemoveRightsEvent.class);
        this.registerHandler(3385, RequestRoomRightsEvent.class);
        this.registerHandler(2683, RoomRemoveAllRightsEvent.class);
        this.registerHandler(2064, RoomUserRemoveRightsEvent.class);
        this.registerHandler(1592, BotPlaceEvent.class);
        this.registerHandler(3323, BotPickupEvent.class);
        this.registerHandler(2624, BotSaveSettingsEvent.class);
        this.registerHandler(1986, BotSettingsEvent.class);
        this.registerHandler(1990, TriggerDiceEvent.class);
        this.registerHandler(1533, CloseDiceEvent.class);
        this.registerHandler(2144, TriggerColorWheelEvent.class);
        this.registerHandler(3115, RedeemItemEvent.class);
        this.registerHandler(2647, PetPlaceEvent.class);
        this.registerHandler(1320, RoomUserKickEvent.class);
        this.registerHandler(3839, SetStackHelperHeightEvent.class);
        this.registerHandler(2765, TriggerOneWayGateEvent.class);
        this.registerHandler(1644, HandleDoorbellEvent.class);
        this.registerHandler(3374, RedeemClothingEvent.class);
        this.registerHandler(2248, PostItPlaceEvent.class);
        this.registerHandler(3964, PostItRequestDataEvent.class);
        this.registerHandler(3666, PostItSaveDataEvent.class);
        this.registerHandler(3336, PostItDeleteEvent.class);
        this.registerHandler(1648, MoodLightSaveSettingsEvent.class);
        this.registerHandler(2946, RentSpaceEvent.class);
        this.registerHandler(1667, RentSpaceCancelEvent.class);
        this.registerHandler(1740, SetHomeRoomEvent.class);
        this.registerHandler(2941, RoomUserGiveHandItemEvent.class);
        this.registerHandler(3637, RoomMuteEvent.class);
        this.registerHandler(1911, RequestRoomWordFilterEvent.class);
        this.registerHandler(3001, RoomWordFilterModifyEvent.class);
        this.registerHandler(1918, RoomStaffPickEvent.class);
        this.registerHandler(2267, RoomRequestBannedUsersEvent.class);
        this.registerHandler(3189, JukeBoxRequestTrackCodeEvent.class);
        this.registerHandler(3082, JukeBoxRequestTrackDataEvent.class);
        this.registerHandler(753, JukeBoxAddSoundTrackEvent.class);
        this.registerHandler(3050, JukeBoxRemoveSoundTrackEvent.class);
        this.registerHandler(1325, JukeBoxRequestPlayListEvent.class);
        this.registerHandler(2304, JukeBoxEventOne.class);
        this.registerHandler(1435, JukeBoxEventTwo.class);
        this.registerHandler(3283, SavePostItStickyPoleEvent.class);
        this.registerHandler(1075, RequestPromotionRoomsEvent.class);
        this.registerHandler(777, BuyRoomPromotionEvent.class);
        this.registerHandler(3991, UpdateRoomPromotionEvent.class);
        this.registerHandler(1117, IgnoreRoomUserEvent.class);
        this.registerHandler(2061, UnIgnoreRoomUserEvent.class);
        this.registerHandler(3485, RoomUserMuteEvent.class);
        this.registerHandler(1477, RoomUserBanEvent.class);
        this.registerHandler(992, UnbanRoomUserEvent.class);
        this.registerHandler(17, RequestRoomUserTagsEvent.class);
        this.registerHandler(336, YoutubeRequestPlaylists.class);
        this.registerHandler(3005, YoutubeRequestStateChange.class);
        this.registerHandler(2069, YoutubeRequestPlaylistChange.class);
        this.registerHandler(3817, RoomFavoriteEvent.class);
        this.registerHandler(3775, LoveLockStartConfirmEvent.class);
        this.registerHandler(309, RoomUnFavoriteEvent.class);
        this.registerHandler(3617, UseRandomStateItemEvent.class);
    }

    void registerPolls() throws Exception {
        this.registerHandler(1773, CancelPollEvent.class);
        this.registerHandler(109, GetPollDataEvent.class);
        this.registerHandler(3505, AnswerPollEvent.class);
    }

    void registerModTool() throws Exception {
        this.registerHandler(707, ModToolRequestRoomInfoEvent.class);
        this.registerHandler(2587, ModToolRequestRoomChatlogEvent.class);
        this.registerHandler(3295, ModToolRequestUserInfoEvent.class);
        this.registerHandler(15, ModToolPickTicketEvent.class);
        this.registerHandler(2067, ModToolCloseTicketEvent.class);
        this.registerHandler(1572, ModToolReleaseTicketEvent.class);
        this.registerHandler(1840, ModToolAlertEvent.class);
        this.registerHandler(-1, ModToolWarnEvent.class);
        this.registerHandler(2582, ModToolKickEvent.class);
        this.registerHandler(3842, ModToolRoomAlertEvent.class);
        this.registerHandler(3260, ModToolChangeRoomSettingsEvent.class);
        this.registerHandler(3526, ModToolRequestRoomVisitsEvent.class);
        this.registerHandler(211, ModToolRequestIssueChatlogEvent.class);
        this.registerHandler(-1, ModToolRequestRoomUserChatlogEvent.class);
        this.registerHandler(1391, ModToolRequestUserChatlogEvent.class);
        this.registerHandler(229, ModToolSanctionAlertEvent.class);
        this.registerHandler(1945, ModToolSanctionMuteEvent.class);
        this.registerHandler(2766, ModToolSanctionBanEvent.class);
        this.registerHandler(3742, ModToolSanctionTradeLockEvent.class);
        this.registerHandler(1392, ModToolIssueChangeTopicEvent.class);
        this.registerHandler(2717, ModToolIssueDefaultSanctionEvent.class);
        this.registerHandler(3267, RequestReportRoomEvent.class);
        this.registerHandler(3786, RequestReportUserBullyingEvent.class);
        this.registerHandler(3060, ReportBullyEvent.class);
        this.registerHandler(1691, ReportEvent.class);
        this.registerHandler(2950, ReportFriendPrivateChatEvent.class);
        this.registerHandler(534, ReportThreadEvent.class);
        this.registerHandler(1412, ReportCommentEvent.class);
        this.registerHandler(2492, ReportPhotoEvent.class);
    }

    void registerTrading() throws Exception {
        this.registerHandler(1481, TradeStartEvent.class);
        this.registerHandler(3107, TradeOfferItemEvent.class);
        this.registerHandler(1263, TradeOfferMultipleItemsEvent.class);
        this.registerHandler(3845, TradeCancelOfferItemEvent.class);
        this.registerHandler(3863, TradeAcceptEvent.class);
        this.registerHandler(1444, TradeUnAcceptEvent.class);
        this.registerHandler(2760, TradeConfirmEvent.class);
        this.registerHandler(2551, TradeCloseEvent.class);
        this.registerHandler(2341, TradeCancelEvent.class);
    }

    void registerGuilds() throws Exception {
        this.registerHandler(798, RequestGuildBuyRoomsEvent.class);
        this.registerHandler(813, RequestGuildPartsEvent.class);
        this.registerHandler(230, RequestGuildBuyEvent.class);
        this.registerHandler(2991, RequestGuildInfoEvent.class);
        this.registerHandler(1004, RequestGuildManageEvent.class);
        this.registerHandler(312, RequestGuildMembersEvent.class);
        this.registerHandler(998, RequestGuildJoinEvent.class);
        this.registerHandler(3137, GuildChangeNameDescEvent.class);
        this.registerHandler(1991, GuildChangeBadgeEvent.class);
        this.registerHandler(1764, GuildChangeColorsEvent.class);
        this.registerHandler(722, GuildRemoveAdminEvent.class);
        this.registerHandler(593, GuildRemoveMemberEvent.class);
        this.registerHandler(3435, GuildChangeSettingsEvent.class);
        this.registerHandler(3386, GuildAcceptMembershipEvent.class);
        this.registerHandler(1894, GuildDeclineMembershipEvent.class);
        this.registerHandler(2894, GuildSetAdminEvent.class);
        this.registerHandler(3549, GuildSetFavoriteEvent.class);
        this.registerHandler(367, RequestOwnGuildsEvent.class);
        this.registerHandler(2651, RequestGuildFurniWidgetEvent.class);
        this.registerHandler(3593, GuildConfirmRemoveMemberEvent.class);
        this.registerHandler(1820, GuildRemoveFavoriteEvent.class);
        this.registerHandler(1134, GuildDeleteEvent.class);
        this.registerHandler(873, GuildForumListEvent.class);
        this.registerHandler(436, GuildForumThreadsEvent.class);
        this.registerHandler(3149, GuildForumDataEvent.class);
        this.registerHandler(3529, GuildForumPostThreadEvent.class);
        this.registerHandler(2214, GuildForumUpdateSettingsEvent.class);
        this.registerHandler(232, GuildForumThreadsMessagesEvent.class);
        this.registerHandler(286, GuildForumModerateMessageEvent.class);
        this.registerHandler(1397, GuildForumModerateThreadEvent.class);
        this.registerHandler(3045, GuildForumThreadUpdateEvent.class);
        this.registerHandler(21, GetHabboGuildBadgesMessageEvent.class);
    }

    void registerPets() throws Exception {
        this.registerHandler(2934, RequestPetInformationEvent.class);
        this.registerHandler(1581, PetPickupEvent.class);
        this.registerHandler(3202, ScratchPetEvent.class);
        this.registerHandler(2161, RequestPetTrainingPanelEvent.class);
        this.registerHandler(1328, PetUseItemEvent.class);
        this.registerHandler(1472, PetRideSettingsEvent.class);
        this.registerHandler(1036, PetRideEvent.class);
        this.registerHandler(186, HorseRemoveSaddleEvent.class);
        this.registerHandler(3379, ToggleMonsterplantBreedableEvent.class);
        this.registerHandler(3835, CompostMonsterplantEvent.class);
        this.registerHandler(1638, BreedMonsterplantsEvent.class);
        this.registerHandler(3449, MovePetEvent.class);
        this.registerHandler(3698, PetPackageNameEvent.class);
        this.registerHandler(2713, StopBreedingEvent.class);
        this.registerHandler(3382, ConfirmPetBreedingEvent.class);
    }

    void registerWired() throws Exception {
        this.registerHandler(1520, WiredTriggerSaveDataEvent.class);
        this.registerHandler(2281, WiredEffectSaveDataEvent.class);
        this.registerHandler(3203, WiredConditionSaveDataEvent.class);
        this.registerHandler(3373, WiredApplySetConditionsEvent.class);
    }

    void registerUnknown() throws Exception {
        this.registerHandler(359, RequestResolutionEvent.class);
        this.registerHandler(196, RequestTalentTrackEvent.class);
        this.registerHandler(1371, UnknownEvent1.class);
        this.registerHandler(2746, MySanctionStatusEvent.class);
    }

    void registerFloorPlanEditor() throws Exception {
        this.registerHandler(875, FloorPlanEditorSaveEvent.class);
        this.registerHandler(1687, FloorPlanEditorRequestBlockedTilesEvent.class);
        this.registerHandler(3559, FloorPlanEditorRequestDoorSettingsEvent.class);
    }

    void registerAchievements() throws Exception {
        this.registerHandler(219, RequestAchievementsEvent.class);
        this.registerHandler(-1, RequestAchievementConfigurationEvent.class);
    }

    void registerGuides() throws Exception {
        this.registerHandler(1922, RequestGuideToolEvent.class);
        this.registerHandler(3338, RequestGuideAssistanceEvent.class);
        this.registerHandler(519, GuideUserTypingEvent.class);
        this.registerHandler(3969, GuideReportHelperEvent.class);
        this.registerHandler(477, GuideRecommendHelperEvent.class);
        this.registerHandler(3899, GuideUserMessageEvent.class);
        this.registerHandler(291, GuideCancelHelpRequestEvent.class);
        this.registerHandler(1424, GuideHandleHelpRequestEvent.class);
        this.registerHandler(234, GuideInviteUserEvent.class);
        this.registerHandler(1052, GuideVisitUserEvent.class);
        this.registerHandler(887, GuideCloseHelpRequestEvent.class);
        this.registerHandler(2501, GuardianNoUpdatesWantedEvent.class);
        this.registerHandler(3365, GuardianAcceptRequestEvent.class);
        this.registerHandler(3961, GuardianVoteEvent.class);
    }

    void registerCrafting() throws Exception {
        this.registerHandler(1173, RequestCraftingRecipesEvent.class);
        this.registerHandler(633, CraftingAddRecipeEvent.class);
        this.registerHandler(3591, CraftingCraftItemEvent.class);
        this.registerHandler(1251, CraftingCraftSecretEvent.class);
        this.registerHandler(3086, RequestCraftingRecipesAvailableEvent.class);
    }

    void registerCamera() throws Exception {
        this.registerHandler(3226, CameraRoomPictureEvent.class);
        this.registerHandler(796, RequestCameraConfigurationEvent.class);
        this.registerHandler(2408, CameraPurchaseEvent.class);
        this.registerHandler(1982, CameraRoomThumbnailEvent.class);
        this.registerHandler(2068, CameraPublishToWebEvent.class);
    }

    void registerGameCenter() throws Exception {
        this.registerHandler(741, GameCenterRequestGamesEvent.class);
        this.registerHandler(3171, GameCenterRequestAccountStatusEvent.class);
        this.registerHandler(1458, GameCenterJoinGameEvent.class);
        this.registerHandler(1054, GameCenterLoadGameEvent.class);
        this.registerHandler(3207, GameCenterLeaveGameEvent.class);
        this.registerHandler(2914, GameCenterEvent.class);
        this.registerHandler(11, GameCenterRequestGameStatusEvent.class);
    }
}

