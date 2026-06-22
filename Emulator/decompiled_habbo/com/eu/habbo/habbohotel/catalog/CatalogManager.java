/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogFeaturedPage;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogLimitedConfiguration;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.catalog.CatalogPageLayouts;
import com.eu.habbo.habbohotel.catalog.ClothItem;
import com.eu.habbo.habbohotel.catalog.ClubOffer;
import com.eu.habbo.habbohotel.catalog.TargetOffer;
import com.eu.habbo.habbohotel.catalog.Voucher;
import com.eu.habbo.habbohotel.catalog.layouts.BadgeDisplayLayout;
import com.eu.habbo.habbohotel.catalog.layouts.BotsLayout;
import com.eu.habbo.habbohotel.catalog.layouts.BuildersClubAddonsLayout;
import com.eu.habbo.habbohotel.catalog.layouts.BuildersClubFrontPageLayout;
import com.eu.habbo.habbohotel.catalog.layouts.BuildersClubLoyaltyLayout;
import com.eu.habbo.habbohotel.catalog.layouts.CatalogRootLayout;
import com.eu.habbo.habbohotel.catalog.layouts.ClubBuyLayout;
import com.eu.habbo.habbohotel.catalog.layouts.ClubGiftsLayout;
import com.eu.habbo.habbohotel.catalog.layouts.ColorGroupingLayout;
import com.eu.habbo.habbohotel.catalog.layouts.Default_3x3Layout;
import com.eu.habbo.habbohotel.catalog.layouts.FrontPageFeaturedLayout;
import com.eu.habbo.habbohotel.catalog.layouts.FrontpageLayout;
import com.eu.habbo.habbohotel.catalog.layouts.GuildForumLayout;
import com.eu.habbo.habbohotel.catalog.layouts.GuildFrontpageLayout;
import com.eu.habbo.habbohotel.catalog.layouts.GuildFurnitureLayout;
import com.eu.habbo.habbohotel.catalog.layouts.InfoDucketsLayout;
import com.eu.habbo.habbohotel.catalog.layouts.InfoLoyaltyLayout;
import com.eu.habbo.habbohotel.catalog.layouts.InfoMonkeyLayout;
import com.eu.habbo.habbohotel.catalog.layouts.InfoNikoLayout;
import com.eu.habbo.habbohotel.catalog.layouts.InfoPetsLayout;
import com.eu.habbo.habbohotel.catalog.layouts.InfoRentablesLayout;
import com.eu.habbo.habbohotel.catalog.layouts.LoyaltyVipBuyLayout;
import com.eu.habbo.habbohotel.catalog.layouts.MadMoneyLayout;
import com.eu.habbo.habbohotel.catalog.layouts.MarketplaceLayout;
import com.eu.habbo.habbohotel.catalog.layouts.MarketplaceOwnItems;
import com.eu.habbo.habbohotel.catalog.layouts.PetCustomizationLayout;
import com.eu.habbo.habbohotel.catalog.layouts.Pets2Layout;
import com.eu.habbo.habbohotel.catalog.layouts.Pets3Layout;
import com.eu.habbo.habbohotel.catalog.layouts.PetsLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RecentPurchasesLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RecyclerInfoLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RecyclerLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RecyclerPrizesLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RoomAdsLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RoomBundleLayout;
import com.eu.habbo.habbohotel.catalog.layouts.SingleBundle;
import com.eu.habbo.habbohotel.catalog.layouts.SoldLTDItemsLayout;
import com.eu.habbo.habbohotel.catalog.layouts.SpacesLayout;
import com.eu.habbo.habbohotel.catalog.layouts.TraxLayout;
import com.eu.habbo.habbohotel.catalog.layouts.TrophiesLayout;
import com.eu.habbo.habbohotel.catalog.layouts.VipBuyLayout;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.catalog.DiscountComposer;
import com.eu.habbo.messages.outgoing.catalog.RedeemVoucherErrorComposer;
import com.eu.habbo.messages.outgoing.catalog.RedeemVoucherOKComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueHandledComposer;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadCatalogManagerEvent;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogManager.class);
    public static final THashMap<String, Class<? extends CatalogPage>> pageDefinitions = new THashMap<String, Class<? extends CatalogPage>>(CatalogPageLayouts.values().length){
        {
            block41: for (CatalogPageLayouts layout : CatalogPageLayouts.values()) {
                switch (layout) {
                    case frontpage: {
                        this.put(layout.name().toLowerCase(), FrontpageLayout.class);
                        continue block41;
                    }
                    case badge_display: {
                        this.put(layout.name().toLowerCase(), BadgeDisplayLayout.class);
                        continue block41;
                    }
                    case spaces_new: {
                        this.put(layout.name().toLowerCase(), SpacesLayout.class);
                        continue block41;
                    }
                    case trophies: {
                        this.put(layout.name().toLowerCase(), TrophiesLayout.class);
                        continue block41;
                    }
                    case bots: {
                        this.put(layout.name().toLowerCase(), BotsLayout.class);
                        continue block41;
                    }
                    case club_buy: {
                        this.put(layout.name().toLowerCase(), ClubBuyLayout.class);
                        continue block41;
                    }
                    case club_gift: {
                        this.put(layout.name().toLowerCase(), ClubGiftsLayout.class);
                        continue block41;
                    }
                    case sold_ltd_items: {
                        this.put(layout.name().toLowerCase(), SoldLTDItemsLayout.class);
                        continue block41;
                    }
                    case single_bundle: {
                        this.put(layout.name().toLowerCase(), SingleBundle.class);
                        continue block41;
                    }
                    case roomads: {
                        this.put(layout.name().toLowerCase(), RoomAdsLayout.class);
                        continue block41;
                    }
                    case recycler: {
                        if (!Emulator.getConfig().getBoolean("hotel.ecotron.enabled")) continue block41;
                        this.put(layout.name().toLowerCase(), RecyclerLayout.class);
                        continue block41;
                    }
                    case recycler_info: {
                        if (Emulator.getConfig().getBoolean("hotel.ecotron.enabled")) {
                            this.put(layout.name().toLowerCase(), RecyclerInfoLayout.class);
                        }
                    }
                    case recycler_prizes: {
                        if (!Emulator.getConfig().getBoolean("hotel.ecotron.enabled")) continue block41;
                        this.put(layout.name().toLowerCase(), RecyclerPrizesLayout.class);
                        continue block41;
                    }
                    case marketplace: {
                        if (!Emulator.getConfig().getBoolean("hotel.marketplace.enabled")) continue block41;
                        this.put(layout.name().toLowerCase(), MarketplaceLayout.class);
                        continue block41;
                    }
                    case marketplace_own_items: {
                        if (!Emulator.getConfig().getBoolean("hotel.marketplace.enabled")) continue block41;
                        this.put(layout.name().toLowerCase(), MarketplaceOwnItems.class);
                        continue block41;
                    }
                    case info_duckets: {
                        this.put(layout.name().toLowerCase(), InfoDucketsLayout.class);
                        continue block41;
                    }
                    case info_pets: {
                        this.put(layout.name().toLowerCase(), InfoPetsLayout.class);
                        continue block41;
                    }
                    case info_rentables: {
                        this.put(layout.name().toLowerCase(), InfoRentablesLayout.class);
                        continue block41;
                    }
                    case info_loyalty: {
                        this.put(layout.name().toLowerCase(), InfoLoyaltyLayout.class);
                        continue block41;
                    }
                    case loyalty_vip_buy: {
                        this.put(layout.name().toLowerCase(), LoyaltyVipBuyLayout.class);
                        continue block41;
                    }
                    case guilds: {
                        this.put(layout.name().toLowerCase(), GuildFrontpageLayout.class);
                        continue block41;
                    }
                    case guild_furni: {
                        this.put(layout.name().toLowerCase(), GuildFurnitureLayout.class);
                        continue block41;
                    }
                    case guild_forum: {
                        this.put(layout.name().toLowerCase(), GuildForumLayout.class);
                        continue block41;
                    }
                    case pets: {
                        this.put(layout.name().toLowerCase(), PetsLayout.class);
                        continue block41;
                    }
                    case pets2: {
                        this.put(layout.name().toLowerCase(), Pets2Layout.class);
                        continue block41;
                    }
                    case pets3: {
                        this.put(layout.name().toLowerCase(), Pets3Layout.class);
                        continue block41;
                    }
                    case soundmachine: {
                        this.put(layout.name().toLowerCase(), TraxLayout.class);
                        continue block41;
                    }
                    case default_3x3_color_grouping: {
                        this.put(layout.name().toLowerCase(), ColorGroupingLayout.class);
                        continue block41;
                    }
                    case recent_purchases: {
                        this.put(layout.name().toLowerCase(), RecentPurchasesLayout.class);
                        continue block41;
                    }
                    case room_bundle: {
                        this.put(layout.name().toLowerCase(), RoomBundleLayout.class);
                        continue block41;
                    }
                    case petcustomization: {
                        this.put(layout.name().toLowerCase(), PetCustomizationLayout.class);
                        continue block41;
                    }
                    case vip_buy: {
                        this.put(layout.name().toLowerCase(), VipBuyLayout.class);
                        continue block41;
                    }
                    case frontpage_featured: {
                        this.put(layout.name().toLowerCase(), FrontPageFeaturedLayout.class);
                        continue block41;
                    }
                    case builders_club_addons: {
                        this.put(layout.name().toLowerCase(), BuildersClubAddonsLayout.class);
                        continue block41;
                    }
                    case builders_club_frontpage: {
                        this.put(layout.name().toLowerCase(), BuildersClubFrontPageLayout.class);
                        continue block41;
                    }
                    case builders_club_loyalty: {
                        this.put(layout.name().toLowerCase(), BuildersClubLoyaltyLayout.class);
                        continue block41;
                    }
                    case monkey: {
                        this.put(layout.name().toLowerCase(), InfoMonkeyLayout.class);
                        continue block41;
                    }
                    case niko: {
                        this.put(layout.name().toLowerCase(), InfoNikoLayout.class);
                        continue block41;
                    }
                    case mad_money: {
                        this.put(layout.name().toLowerCase(), MadMoneyLayout.class);
                        continue block41;
                    }
                    default: {
                        this.put("default_3x3", Default_3x3Layout.class);
                    }
                }
            }
        }
    };
    public static int catalogItemAmount;
    public static int PURCHASE_COOLDOWN;
    public static boolean SORT_USING_ORDERNUM;
    public final TIntObjectMap<CatalogPage> catalogPages;
    public final TIntObjectMap<CatalogFeaturedPage> catalogFeaturedPages;
    public final THashMap<Integer, THashSet<Item>> prizes;
    public final THashMap<Integer, Integer> giftWrappers;
    public final THashMap<Integer, Integer> giftFurnis;
    public final THashSet<CatalogItem> clubItems;
    public final THashMap<Integer, ClubOffer> clubOffers;
    public final THashMap<Integer, TargetOffer> targetOffers;
    public final THashMap<Integer, ClothItem> clothing;
    public final TIntIntHashMap offerDefs;
    public final Item ecotronItem;
    public final THashMap<Integer, CatalogLimitedConfiguration> limitedNumbers;
    private final List<Voucher> vouchers;

    public CatalogManager() {
        long millis = System.currentTimeMillis();
        this.catalogPages = TCollections.synchronizedMap(new TIntObjectHashMap());
        this.catalogFeaturedPages = new TIntObjectHashMap<CatalogFeaturedPage>();
        this.prizes = new THashMap();
        this.giftWrappers = new THashMap();
        this.giftFurnis = new THashMap();
        this.clubItems = new THashSet();
        this.clubOffers = new THashMap();
        this.targetOffers = new THashMap();
        this.clothing = new THashMap();
        this.offerDefs = new TIntIntHashMap();
        this.vouchers = new ArrayList<Voucher>();
        this.limitedNumbers = new THashMap();
        this.initialize();
        this.ecotronItem = Emulator.getGameEnvironment().getItemManager().getItem("ecotron_box");
        LOGGER.info("Catalog Manager -> Loaded! ({} MS)", (Object)(System.currentTimeMillis() - millis));
    }

    public synchronized void initialize() {
        Emulator.getPluginManager().fireEvent(new EmulatorLoadCatalogManagerEvent());
        this.loadLimitedNumbers();
        this.loadCatalogPages();
        this.loadCatalogFeaturedPages();
        this.loadCatalogItems();
        this.loadClubOffers();
        this.loadTargetOffers();
        this.loadVouchers();
        this.loadClothing();
        this.loadRecycler();
        this.loadGiftWrappers();
    }

    private synchronized void loadLimitedNumbers() {
        this.limitedNumbers.clear();
        THashMap limiteds = new THashMap();
        TIntIntHashMap totals = new TIntIntHashMap();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM catalog_items_limited");
             ResultSet set = statement.executeQuery();){
            while (set.next()) {
                if (!limiteds.containsKey(set.getInt("catalog_item_id"))) {
                    limiteds.put(set.getInt("catalog_item_id"), new LinkedList());
                }
                totals.adjustOrPutValue(set.getInt("catalog_item_id"), 1, 1);
                if (set.getInt("user_id") != 0) continue;
                ((LinkedList)limiteds.get(set.getInt("catalog_item_id"))).push(set.getInt("number"));
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        for (Map.Entry set : limiteds.entrySet()) {
            this.limitedNumbers.put((Integer)set.getKey(), new CatalogLimitedConfiguration((Integer)set.getKey(), (LinkedList)set.getValue(), totals.get((Integer)set.getKey())));
        }
    }

    private synchronized void loadCatalogPages() {
        THashMap<Integer, CatalogPage> pages;
        block25: {
            this.catalogPages.clear();
            pages = new THashMap<Integer, CatalogPage>();
            pages.put(-1, new CatalogRootLayout());
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM catalog_pages ORDER BY parent_id, id");){
                ResultSet set = statement.executeQuery();
                block19: while (true) {
                    while (set.next()) {
                        Class<? extends CatalogPage> pageClazz = pageDefinitions.get(set.getString("page_layout"));
                        if (pageClazz == null) {
                            LOGGER.info("Unknown Page Layout: {}", (Object)set.getString("page_layout"));
                            continue;
                        }
                        try {
                            CatalogPage page = pageClazz.getConstructor(ResultSet.class).newInstance(set);
                            pages.put(page.getId(), page);
                            continue block19;
                        }
                        catch (Exception e) {
                            LOGGER.error("Failed to load layout: {}", (Object)set.getString("page_layout"));
                        }
                    }
                    break block25;
                    {
                        continue block19;
                        break;
                    }
                    break;
                }
                finally {
                    if (set != null) {
                        set.close();
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
        pages.forEachValue(object -> {
            CatalogPage page = (CatalogPage)pages.get(object.parentId);
            if (page != null) {
                if (page.id != object.id) {
                    page.addChildPage((CatalogPage)object);
                }
            } else if (object.parentId != -2) {
                LOGGER.info("Parent Page not found for {} (ID: {}, parent_id: {})", object.getPageName(), object.id, object.parentId);
            }
            return true;
        });
        this.catalogPages.putAll(pages);
        LOGGER.info("Loaded {} Catalog Pages!", (Object)this.catalogPages.size());
    }

    private synchronized void loadCatalogFeaturedPages() {
        this.catalogFeaturedPages.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery("SELECT * FROM catalog_featured_pages ORDER BY slot_id ASC");){
            while (set.next()) {
                this.catalogFeaturedPages.put(set.getInt("slot_id"), new CatalogFeaturedPage(set.getInt("slot_id"), set.getString("caption"), set.getString("image"), CatalogFeaturedPage.Type.valueOf(set.getString("type").toUpperCase()), set.getInt("expire_timestamp"), set.getString("page_name"), set.getInt("page_id"), set.getString("product_name")));
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    private synchronized void loadCatalogItems() {
        this.clubItems.clear();
        catalogItemAmount = 0;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery("SELECT * FROM catalog_items");){
            while (set.next()) {
                if (set.getString("item_ids").equals("0")) continue;
                if (set.getString("catalog_name").contains("HABBO_CLUB_")) {
                    this.clubItems.add(new CatalogItem(set));
                    continue;
                }
                CatalogPage page = this.catalogPages.get(set.getInt("page_id"));
                if (page == null) continue;
                CatalogItem item = page.getCatalogItem(set.getInt("id"));
                if (item == null) {
                    ++catalogItemAmount;
                    item = new CatalogItem(set);
                    page.addItem(item);
                    if (item.getOfferId() != -1) {
                        page.addOfferId(item.getOfferId());
                        this.offerDefs.put(item.getOfferId(), item.getId());
                    }
                } else {
                    item.update(set);
                }
                if (!item.isLimited()) continue;
                this.createOrUpdateLimitedConfig(item);
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        for (CatalogPage page : this.catalogPages.valueCollection()) {
            for (Integer id : page.getIncluded()) {
                CatalogPage p = this.catalogPages.get(id);
                if (p == null) continue;
                page.getCatalogItems().putAll(p.getCatalogItems());
            }
        }
    }

    private void loadClubOffers() {
        this.clubOffers.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM catalog_club_offers WHERE enabled = ?");){
            statement.setString(1, "1");
            try (ResultSet set = statement.executeQuery();){
                while (set.next()) {
                    this.clubOffers.put(set.getInt("id"), new ClubOffer(set));
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadTargetOffers() {
        THashMap<Integer, TargetOffer> tHashMap = this.targetOffers;
        synchronized (tHashMap) {
            this.targetOffers.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM catalog_target_offers WHERE end_timestamp > ?");){
                statement.setInt(1, Emulator.getIntUnixTimestamp());
                try (ResultSet set = statement.executeQuery();){
                    while (set.next()) {
                        this.targetOffers.put(set.getInt("id"), new TargetOffer(set));
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadVouchers() {
        List<Voucher> list = this.vouchers;
        synchronized (list) {
            this.vouchers.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet set = statement.executeQuery("SELECT * FROM vouchers");){
                while (set.next()) {
                    this.vouchers.add(new Voucher(set));
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadRecycler() {
        THashMap<Integer, THashSet<Item>> tHashMap = this.prizes;
        synchronized (tHashMap) {
            this.prizes.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet set = statement.executeQuery("SELECT * FROM recycler_prizes");){
                while (set.next()) {
                    Item item = Emulator.getGameEnvironment().getItemManager().getItem(set.getInt("item_id"));
                    if (item != null) {
                        if (this.prizes.get(set.getInt("rarity")) == null) {
                            this.prizes.put(set.getInt("rarity"), new THashSet());
                        }
                        this.prizes.get(set.getInt("rarity")).add(item);
                        continue;
                    }
                    LOGGER.error("Cannot load item with ID: {} as recycler reward!", (Object)set.getInt("item_id"));
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadGiftWrappers() {
        THashMap<Integer, Integer> tHashMap = this.giftWrappers;
        synchronized (tHashMap) {
            THashMap<Integer, Integer> tHashMap2 = this.giftFurnis;
            synchronized (tHashMap2) {
                this.giftWrappers.clear();
                this.giftFurnis.clear();
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet set = statement.executeQuery("SELECT * FROM gift_wrappers ORDER BY sprite_id DESC");){
                    while (set.next()) {
                        switch (set.getString("type")) {
                            case "wrapper": {
                                this.giftWrappers.put(set.getInt("sprite_id"), set.getInt("item_id"));
                                break;
                            }
                            case "gift": {
                                this.giftFurnis.put(set.getInt("sprite_id"), set.getInt("item_id"));
                            }
                        }
                    }
                }
                catch (SQLException e) {
                    LOGGER.error("Caught SQL exception", e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadClothing() {
        THashMap<Integer, ClothItem> tHashMap = this.clothing;
        synchronized (tHashMap) {
            this.clothing.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet set = statement.executeQuery("SELECT * FROM catalog_clothing");){
                while (set.next()) {
                    this.clothing.put(set.getInt("id"), new ClothItem(set));
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    public ClothItem getClothing(String name) {
        for (ClothItem item : this.clothing.values()) {
            if (!item.name.equalsIgnoreCase(name)) continue;
            return item;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Voucher getVoucher(String code) {
        List<Voucher> list = this.vouchers;
        synchronized (list) {
            for (Voucher voucher : this.vouchers) {
                if (!voucher.code.equals(code)) continue;
                return voucher;
            }
        }
        return null;
    }

    public void redeemVoucher(GameClient client, String voucherCode) {
        CatalogItem item;
        Habbo habbo = client.getHabbo();
        if (habbo == null) {
            return;
        }
        Voucher voucher = Emulator.getGameEnvironment().getCatalogManager().getVoucher(voucherCode);
        if (voucher == null) {
            client.sendResponse(new RedeemVoucherErrorComposer(0));
            return;
        }
        if (voucher.isExhausted()) {
            client.sendResponse(new RedeemVoucherErrorComposer(Emulator.getGameEnvironment().getCatalogManager().deleteVoucher(voucher) ? 0 : 1));
            return;
        }
        if (voucher.hasUserExhausted(habbo.getHabboInfo().getId())) {
            client.sendResponse(new ModToolIssueHandledComposer("You have exceeded the limit for redeeming this voucher."));
            return;
        }
        voucher.addHistoryEntry(habbo.getHabboInfo().getId());
        if (voucher.points > 0) {
            client.getHabbo().givePoints(voucher.pointsType, voucher.points);
        }
        if (voucher.credits > 0) {
            client.getHabbo().giveCredits(voucher.credits);
        }
        if (voucher.catalogItemId > 0 && (item = this.getCatalogItem(voucher.catalogItemId)) != null) {
            this.purchaseItem(null, item, client.getHabbo(), 1, "", true);
        }
        client.sendResponse(new RedeemVoucherOKComposer());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    public boolean deleteVoucher(Voucher voucher) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();){
            boolean bl;
            block17: {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM vouchers WHERE code = ?");
                try {
                    statement.setString(1, voucher.code);
                    List<Voucher> list = this.vouchers;
                    synchronized (list) {
                        this.vouchers.remove(voucher);
                    }
                    boolean bl2 = bl = statement.executeUpdate() >= 1;
                    if (statement == null) break block17;
                }
                catch (Throwable throwable) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                statement.close();
            }
            return bl;
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return false;
        }
    }

    public CatalogPage getCatalogPage(int pageId) {
        return this.catalogPages.get(pageId);
    }

    public CatalogPage getCatalogPage(String captionSafe) {
        return this.catalogPages.valueCollection().stream().filter(p -> p != null && p.getPageName() != null && p.getPageName().equalsIgnoreCase(captionSafe)).findAny().orElse(null);
    }

    public CatalogPage getCatalogPageByLayout(String layoutName) {
        return this.catalogPages.valueCollection().stream().filter(p -> p != null && p.isVisible() && p.isEnabled() && p.getRank() < 2 && p.getLayout() != null && p.getLayout().equalsIgnoreCase(layoutName)).findAny().orElse(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CatalogItem getCatalogItem(final int id) {
        final CatalogItem[] item = new CatalogItem[]{null};
        TIntObjectMap<CatalogPage> tIntObjectMap = this.catalogPages;
        synchronized (tIntObjectMap) {
            this.catalogPages.forEachValue(new TObjectProcedure<CatalogPage>(){
                final /* synthetic */ CatalogManager this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean execute(CatalogPage object) {
                    item[0] = object.getCatalogItem(id);
                    return item[0] == null;
                }
            });
        }
        return item[0];
    }

    public List<CatalogPage> getCatalogPages(int parentId, final Habbo habbo) {
        final ArrayList<CatalogPage> pages = new ArrayList<CatalogPage>();
        this.catalogPages.get((int)parentId).childPages.forEachValue(new TObjectProcedure<CatalogPage>(){
            final /* synthetic */ CatalogManager this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public boolean execute(CatalogPage object) {
                boolean clubRightsOkay;
                boolean isVisiblePage = object.visible;
                boolean hasRightRank = object.getRank() <= habbo.getHabboInfo().getRank().getId();
                boolean bl = clubRightsOkay = !object.isClubOnly() || habbo.getHabboInfo().getHabboStats().hasActiveClub();
                if (isVisiblePage && hasRightRank && clubRightsOkay) {
                    pages.add(object);
                }
                return true;
            }
        });
        Collections.sort(pages);
        return pages;
    }

    public TIntObjectMap<CatalogFeaturedPage> getCatalogFeaturedPages() {
        return this.catalogFeaturedPages;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CatalogItem getClubItem(int itemId) {
        THashSet<CatalogItem> tHashSet = this.clubItems;
        synchronized (tHashSet) {
            for (CatalogItem item : this.clubItems) {
                if (item.getId() != itemId) continue;
                return item;
            }
        }
        return null;
    }

    public boolean moveCatalogItem(CatalogItem item, int pageId) {
        CatalogPage page = this.getCatalogPage(item.getPageId());
        if (page == null) {
            return false;
        }
        page.getCatalogItems().remove(item.getId());
        page = this.getCatalogPage(pageId);
        if (page == null) {
            return false;
        }
        page.getCatalogItems().put(item.getId(), item);
        item.setPageId(pageId);
        item.setNeedsUpdate(true);
        item.run();
        return true;
    }

    public Item getRandomRecyclerPrize() {
        int level = 1;
        if (Emulator.getRandom().nextInt(Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.5")) + 1 == Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.5")) {
            level = 5;
        } else if (Emulator.getRandom().nextInt(Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.4")) + 1 == Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.4")) {
            level = 4;
        } else if (Emulator.getRandom().nextInt(Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.3")) + 1 == Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.3")) {
            level = 3;
        } else if (Emulator.getRandom().nextInt(Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.2")) + 1 == Emulator.getConfig().getInt("hotel.ecotron.rarity.chance.2")) {
            level = 2;
        }
        if (this.prizes.containsKey(level) && !this.prizes.get(level).isEmpty()) {
            return (Item)this.prizes.get(level).toArray()[Emulator.getRandom().nextInt(this.prizes.get(level).size())];
        }
        LOGGER.error("No rewards specified for rarity level {}", (Object)level);
        return null;
    }

    public CatalogPage createCatalogPage(String caption, String captionSave, int roomId, int icon, CatalogPageLayouts layout, int minRank, int parentId) {
        CatalogPage catalogPage;
        block38: {
            catalogPage = null;
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO catalog_pages (parent_id, caption, caption_save, icon_image, visible, enabled, min_rank, page_layout, room_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);){
                statement.setInt(1, parentId);
                statement.setString(2, caption);
                statement.setString(3, captionSave);
                statement.setInt(4, icon);
                statement.setString(5, "1");
                statement.setString(6, "1");
                statement.setInt(7, minRank);
                statement.setString(8, layout.name());
                statement.setInt(9, roomId);
                statement.execute();
                try (ResultSet set = statement.getGeneratedKeys();){
                    if (!set.next()) break block38;
                    try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM catalog_pages WHERE id = ?");){
                        stmt.setInt(1, set.getInt(1));
                        try (ResultSet page = stmt.executeQuery();){
                            if (page.next()) {
                                Class<? extends CatalogPage> pageClazz = pageDefinitions.get(page.getString("page_layout"));
                                if (pageClazz != null) {
                                    try {
                                        catalogPage = pageClazz.getConstructor(ResultSet.class).newInstance(page);
                                    }
                                    catch (Exception e) {
                                        LOGGER.error("Caught exception", e);
                                    }
                                } else {
                                    LOGGER.error("Unknown page layout: {}", (Object)page.getString("page_layout"));
                                }
                            }
                        }
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
        if (catalogPage != null) {
            this.catalogPages.put(catalogPage.getId(), catalogPage);
        }
        return catalogPage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CatalogLimitedConfiguration getLimitedConfig(CatalogItem item) {
        THashMap<Integer, CatalogLimitedConfiguration> tHashMap = this.limitedNumbers;
        synchronized (tHashMap) {
            return this.limitedNumbers.get(item.getId());
        }
    }

    public CatalogLimitedConfiguration createOrUpdateLimitedConfig(CatalogItem item) {
        if (item.isLimited()) {
            CatalogLimitedConfiguration limitedConfiguration = this.limitedNumbers.get(item.getId());
            if (limitedConfiguration == null) {
                limitedConfiguration = new CatalogLimitedConfiguration(item.getId(), new LinkedList<Integer>(), 0);
                limitedConfiguration.generateNumbers(1, item.limitedStack);
                this.limitedNumbers.put(item.getId(), limitedConfiguration);
            } else if (limitedConfiguration.getTotalSet() != item.limitedStack) {
                if (limitedConfiguration.getTotalSet() == 0) {
                    limitedConfiguration.setTotalSet(item.limitedStack);
                } else if (item.limitedStack > limitedConfiguration.getTotalSet()) {
                    limitedConfiguration.generateNumbers(item.limitedStack + 1, item.limitedStack - limitedConfiguration.getTotalSet());
                } else {
                    item.limitedStack = limitedConfiguration.getTotalSet();
                }
            }
            return limitedConfiguration;
        }
        return null;
    }

    public void dispose() {
        TIntObjectIterator<CatalogPage> pageIterator = this.catalogPages.iterator();
        while (pageIterator.hasNext()) {
            pageIterator.advance();
            for (CatalogItem item : pageIterator.value().getCatalogItems().valueCollection()) {
                item.run();
                if (!item.isLimited()) continue;
                this.limitedNumbers.get(item.getId()).run();
            }
        }
        LOGGER.info("Catalog Manager -> Disposed!");
    }

    /*
     * Exception decompiling
     */
    public void purchaseItem(CatalogPage page, CatalogItem item, Habbo habbo, int amount, String extradata, boolean free) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [9[TRYBLOCK]], but top level block is 24[WHILELOOP]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
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

    public List<ClubOffer> getClubOffers() {
        ArrayList<ClubOffer> offers = new ArrayList<ClubOffer>();
        for (Map.Entry<Integer, ClubOffer> entry : this.clubOffers.entrySet()) {
            if (entry.getValue().isDeal()) continue;
            offers.add(entry.getValue());
        }
        return offers;
    }

    public TargetOffer getTargetOffer(int offerId) {
        return this.targetOffers.get(offerId);
    }

    private int calculateDiscountedPrice(int originalPrice, int amount, CatalogItem item) {
        if (!CatalogItem.haveOffer(item)) {
            return originalPrice * amount;
        }
        int basicDiscount = amount / DiscountComposer.DISCOUNT_BATCH_SIZE;
        int bonusDiscount = 0;
        if (basicDiscount >= DiscountComposer.MINIMUM_DISCOUNTS_FOR_BONUS) {
            if (amount % DiscountComposer.DISCOUNT_BATCH_SIZE == DiscountComposer.DISCOUNT_BATCH_SIZE - 1) {
                bonusDiscount = 1;
            }
            bonusDiscount += basicDiscount - DiscountComposer.MINIMUM_DISCOUNTS_FOR_BONUS;
        }
        int additionalDiscounts = 0;
        for (int threshold : DiscountComposer.ADDITIONAL_DISCOUNT_THRESHOLDS) {
            if (amount < threshold) continue;
            ++additionalDiscounts;
        }
        int totalDiscountedItems = basicDiscount * DiscountComposer.DISCOUNT_AMOUNT_PER_BATCH + bonusDiscount + additionalDiscounts;
        return Math.max(0, originalPrice * (amount - totalDiscountedItems));
    }

    static {
        PURCHASE_COOLDOWN = 1;
        SORT_USING_ORDERNUM = false;
    }
}

