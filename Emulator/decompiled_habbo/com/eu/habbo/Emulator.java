/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.ConsoleAppender;
import com.eu.habbo.core.CleanerThread;
import com.eu.habbo.core.ConfigurationManager;
import com.eu.habbo.core.CryptoConfig;
import com.eu.habbo.core.DatabaseLogger;
import com.eu.habbo.core.Logging;
import com.eu.habbo.core.TextsManager;
import com.eu.habbo.core.consolecommands.ConsoleCommand;
import com.eu.habbo.database.Database;
import com.eu.habbo.habbohotel.GameEnvironment;
import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.networking.gameserver.GameServer;
import com.eu.habbo.networking.rconserver.RCONServer;
import com.eu.habbo.plugin.PluginManager;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorStartShutdownEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorStoppedEvent;
import com.eu.habbo.threading.ThreadPooling;
import com.eu.habbo.util.imager.badges.BadgeImager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Emulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Emulator.class);
    private static final String OS_NAME = System.getProperty("os.name") != null ? System.getProperty("os.name") : "Unknown";
    private static final String CLASS_PATH = System.getProperty("java.class.path") != null ? System.getProperty("java.class.path") : "Unknown";
    public static final int MAJOR = 3;
    public static final int MINOR = 6;
    public static final int BUILD = 1;
    public static final String PREVIEW = "";
    public static final String version = "Arcturus Morningstar 3.6.1 ";
    private static final String logo = "\n\u2588\u2588\u2588\u2557   \u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2557   \u2588\u2588\u2557\u2588\u2588\u2557\u2588\u2588\u2588\u2557   \u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2557 \n\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2550\u2588\u2588\u2557\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\u2588\u2588\u2588\u2588\u2557  \u2588\u2588\u2551\u2588\u2588\u2551\u2588\u2588\u2588\u2588\u2557  \u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2550\u2550\u255d \u2588\u2588\u2554\u2550\u2550\u2550\u2550\u255d\u255a\u2550\u2550\u2588\u2588\u2554\u2550\u2550\u255d\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\n\u2588\u2588\u2554\u2588\u2588\u2588\u2588\u2554\u2588\u2588\u2551\u2588\u2588\u2551   \u2588\u2588\u2551\u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255d\u2588\u2588\u2554\u2588\u2588\u2557 \u2588\u2588\u2551\u2588\u2588\u2551\u2588\u2588\u2554\u2588\u2588\u2557 \u2588\u2588\u2551\u2588\u2588\u2551  \u2588\u2588\u2588\u2557\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557   \u2588\u2588\u2551   \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2551\u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255d\n\u2588\u2588\u2551\u255a\u2588\u2588\u2554\u255d\u2588\u2588\u2551\u2588\u2588\u2551   \u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\u2588\u2588\u2551\u255a\u2588\u2588\u2557\u2588\u2588\u2551\u2588\u2588\u2551\u2588\u2588\u2551\u255a\u2588\u2588\u2557\u2588\u2588\u2551\u2588\u2588\u2551   \u2588\u2588\u2551\u255a\u2550\u2550\u2550\u2550\u2588\u2588\u2551   \u2588\u2588\u2551   \u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\n\u2588\u2588\u2551 \u255a\u2550\u255d \u2588\u2588\u2551\u255a\u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255d\u2588\u2588\u2551  \u2588\u2588\u2551\u2588\u2588\u2551 \u255a\u2588\u2588\u2588\u2588\u2551\u2588\u2588\u2551\u2588\u2588\u2551 \u255a\u2588\u2588\u2588\u2588\u2551\u255a\u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255d\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2551   \u2588\u2588\u2551   \u2588\u2588\u2551  \u2588\u2588\u2551\u2588\u2588\u2551  \u2588\u2588\u2551\n\u255a\u2550\u255d     \u255a\u2550\u255d \u255a\u2550\u2550\u2550\u2550\u2550\u255d \u255a\u2550\u255d  \u255a\u2550\u255d\u255a\u2550\u255d  \u255a\u2550\u2550\u2550\u255d\u255a\u2550\u255d\u255a\u2550\u255d  \u255a\u2550\u2550\u2550\u255d \u255a\u2550\u2550\u2550\u2550\u2550\u255d \u255a\u2550\u2550\u2550\u2550\u2550\u2550\u255d   \u255a\u2550\u255d   \u255a\u2550\u255d  \u255a\u2550\u255d\u255a\u2550\u255d  \u255a\u2550\u255d\nStill Rocking in 2024.\n";
    public static String build = "";
    public static boolean isReady = false;
    public static boolean isShuttingDown = false;
    public static boolean stopped = false;
    public static boolean debugging = false;
    private static int timeStarted = 0;
    private static Runtime runtime;
    private static ConfigurationManager config;
    private static CryptoConfig crypto;
    private static TextsManager texts;
    private static GameServer gameServer;
    private static RCONServer rconServer;
    private static CameraClient cameraClient;
    private static Logging logging;
    private static Database database;
    private static DatabaseLogger databaseLogger;
    private static ThreadPooling threading;
    private static GameEnvironment gameEnvironment;
    private static PluginManager pluginManager;
    private static BadgeImager badgeImager;
    private static final SecureRandom secureRandom;

    public static void main(String[] args) throws Exception {
        try {
            if (OS_NAME.startsWith("Windows") && !CLASS_PATH.contains("idea_rt.jar")) {
                ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT");
                ConsoleAppender appender = (ConsoleAppender)root.getAppender("Console");
                appender.stop();
                appender.setWithJansi(true);
                appender.start();
            }
            Locale.setDefault(new Locale("en"));
            Emulator.setBuild();
            stopped = false;
            ConsoleCommand.load();
            logging = new Logging();
            System.out.println(logo);
            System.out.println(PREVIEW);
            LOGGER.warn("Arcturus Morningstar 3.x is no longer accepting merge requests. Please target MS4 branches if you wish to contribute.");
            LOGGER.info("Follow our development at https://git.krews.org/morningstar/Arcturus-Community, ");
            System.out.println(PREVIEW);
            LOGGER.info("This project is for educational purposes only. This Emulator is an open-source fork of Arcturus created by TheGeneral.");
            LOGGER.info("Version: {}", (Object)version);
            LOGGER.info("Build: {}", (Object)build);
            long startTime = System.nanoTime();
            runtime = Runtime.getRuntime();
            config = new ConfigurationManager("config.ini");
            crypto = new CryptoConfig(Emulator.getConfig().getBoolean("enc.enabled", false), Emulator.getConfig().getValue("enc.e"), Emulator.getConfig().getValue("enc.n"), Emulator.getConfig().getValue("enc.d"));
            database = new Database(Emulator.getConfig());
            databaseLogger = new DatabaseLogger();
            Emulator.config.loaded = true;
            config.loadFromDatabase();
            threading = new ThreadPooling(Emulator.getConfig().getInt("runtime.threads"));
            Emulator.getDatabase().getDataSource().setMaximumPoolSize(Emulator.getConfig().getInt("runtime.threads") * 2);
            Emulator.getDatabase().getDataSource().setMinimumIdle(10);
            pluginManager = new PluginManager();
            pluginManager.reload();
            Emulator.getPluginManager().fireEvent(new EmulatorConfigUpdatedEvent());
            texts = new TextsManager();
            new CleanerThread();
            gameServer = new GameServer(Emulator.getConfig().getValue("game.host", "127.0.0.1"), Emulator.getConfig().getInt("game.port", 30000));
            rconServer = new RCONServer(Emulator.getConfig().getValue("rcon.host", "127.0.0.1"), Emulator.getConfig().getInt("rcon.port", 30001));
            gameEnvironment = new GameEnvironment();
            gameEnvironment.load();
            gameServer.initializePipeline();
            gameServer.connect();
            rconServer.initializePipeline();
            rconServer.connect();
            badgeImager = new BadgeImager();
            LOGGER.info("Arcturus Morningstar has successfully loaded.");
            LOGGER.info("System launched in: {}ms. Using {} threads!", (Object)((double)(System.nanoTime() - startTime) / 1000000.0), (Object)(Runtime.getRuntime().availableProcessors() * 2));
            LOGGER.info("Memory: {}/{}MB", (Object)((runtime.totalMemory() - runtime.freeMemory()) / 0x100000L), (Object)(runtime.freeMemory() / 0x100000L));
            debugging = Emulator.getConfig().getBoolean("debug.mode");
            if (debugging) {
                ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("ROOT");
                root.setLevel(Level.DEBUG);
                LOGGER.debug("Debugging enabled.");
            }
            Emulator.getPluginManager().fireEvent(new EmulatorLoadedEvent());
            isReady = true;
            timeStarted = Emulator.getIntUnixTimestamp();
            if (Emulator.getConfig().getInt("runtime.threads") < Runtime.getRuntime().availableProcessors() * 2) {
                LOGGER.warn("Emulator settings runtime.threads ({}) can be increased to ({}) to possibly increase performance.", (Object)Emulator.getConfig().getInt("runtime.threads"), (Object)(Runtime.getRuntime().availableProcessors() * 2));
            }
            Emulator.getThreading().run(() -> {}, 1500L);
            if (Emulator.getConfig().getBoolean("console.mode", true)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (!isShuttingDown && isReady) {
                    try {
                        String line = reader.readLine();
                        if (line != null) {
                            ConsoleCommand.handle(line);
                        }
                        System.out.println("Waiting for command: ");
                    }
                    catch (Exception e) {
                        if (e instanceof IOException && e.getMessage().equals("Bad file descriptor")) continue;
                        LOGGER.error("Error while reading command", e);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setBuild() {
        if (Emulator.class.getProtectionDomain().getCodeSource() == null) {
            build = "UNKNOWN";
            return;
        }
        StringBuilder sb = new StringBuilder();
        try {
            String filepath = new File(Emulator.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(filepath);
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();
            for (int i = 0; i < mdbytes.length; ++i) {
                sb.append(Integer.toString((mdbytes[i] & 0xFF) + 256, 16).substring(1));
            }
        }
        catch (Exception e) {
            build = "UNKNOWN";
            return;
        }
        build = sb.toString();
    }

    private static void dispose() {
        Emulator.getThreading().setCanAdd(false);
        isShuttingDown = true;
        isReady = false;
        LOGGER.info("Stopping Arcturus Morningstar {}", (Object)version);
        try {
            if (Emulator.getPluginManager() != null) {
                Emulator.getPluginManager().fireEvent(new EmulatorStartShutdownEvent());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (cameraClient != null) {
                cameraClient.disconnect();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (rconServer != null) {
                rconServer.stop();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (gameEnvironment != null) {
                gameEnvironment.dispose();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (Emulator.getPluginManager() != null) {
                Emulator.getPluginManager().fireEvent(new EmulatorStoppedEvent());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (pluginManager != null) {
                pluginManager.dispose();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (config != null) {
                config.saveToDatabase();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (gameServer != null) {
                gameServer.stop();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        LOGGER.info("Stopped Arcturus Morningstar {}", (Object)version);
        if (database != null) {
            Emulator.getDatabase().dispose();
        }
        stopped = true;
        try {
            if (threading != null) {
                threading.shutDown();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static ConfigurationManager getConfig() {
        return config;
    }

    public static CryptoConfig getCrypto() {
        return crypto;
    }

    public static TextsManager getTexts() {
        return texts;
    }

    public static Database getDatabase() {
        return database;
    }

    public static DatabaseLogger getDatabaseLogger() {
        return databaseLogger;
    }

    public static Runtime getRuntime() {
        return runtime;
    }

    public static GameServer getGameServer() {
        return gameServer;
    }

    public static RCONServer getRconServer() {
        return rconServer;
    }

    @Deprecated
    public static Logging getLogging() {
        return logging;
    }

    public static ThreadPooling getThreading() {
        return threading;
    }

    public static GameEnvironment getGameEnvironment() {
        return gameEnvironment;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static Random getRandom() {
        return ThreadLocalRandom.current();
    }

    public static SecureRandom getRandomDice() {
        return secureRandom;
    }

    public static BadgeImager getBadgeImager() {
        return badgeImager;
    }

    public static CameraClient getCameraClient() {
        return cameraClient;
    }

    public static synchronized void setCameraClient(CameraClient client) {
        cameraClient = client;
    }

    public static int getTimeStarted() {
        return timeStarted;
    }

    public static int getOnlineTime() {
        return Emulator.getIntUnixTimestamp() - timeStarted;
    }

    public static void prepareShutdown() {
        System.exit(0);
    }

    public static int timeStringToSeconds(String timeString) {
        int totalSeconds = 0;
        Matcher m = Pattern.compile("(([0-9]*) (second|minute|hour|day|week|month|year))").matcher(timeString);
        HashMap<String, Integer> map = new HashMap<String, Integer>(){
            {
                this.put("second", 1);
                this.put("minute", 60);
                this.put("hour", 3600);
                this.put("day", 86400);
                this.put("week", 604800);
                this.put("month", 2628000);
                this.put("year", 31536000);
            }
        };
        while (m.find()) {
            try {
                int amount = Integer.parseInt(m.group(2));
                String what = m.group(3);
                totalSeconds += amount * (Integer)map.get(what);
            }
            catch (Exception exception) {}
        }
        return totalSeconds;
    }

    public static Date modifyDate(Date date, String timeString) {
        boolean totalSeconds = false;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Matcher m = Pattern.compile("(([0-9]*) (second|minute|hour|day|week|month|year))").matcher(timeString);
        HashMap<String, Integer> map = new HashMap<String, Integer>(){
            {
                this.put("second", 13);
                this.put("minute", 12);
                this.put("hour", 10);
                this.put("day", 5);
                this.put("week", 4);
                this.put("month", 2);
                this.put("year", 1);
            }
        };
        while (m.find()) {
            try {
                int amount = Integer.parseInt(m.group(2));
                String what = m.group(3);
                c.add((Integer)map.get(what), amount);
            }
            catch (Exception exception) {}
        }
        return c.getTime();
    }

    private static String dateToUnixTimestamp(Date date) {
        String res = PREVIEW;
        Date aux = Emulator.stringToDate("1970-01-01 00:00:00");
        Timestamp aux1 = Emulator.dateToTimeStamp(aux);
        Timestamp aux2 = Emulator.dateToTimeStamp(date);
        long difference = aux2.getTime() - aux1.getTime();
        long seconds = difference / 1000L;
        return res + seconds;
    }

    public static Date stringToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date res = null;
        try {
            res = format.parse(date);
        }
        catch (Exception e) {
            LOGGER.error("Error parsing date", e);
        }
        return res;
    }

    public static Timestamp dateToTimeStamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Date getDate() {
        return new Date(System.currentTimeMillis());
    }

    public static String getUnixTimestamp() {
        return Emulator.dateToUnixTimestamp(Emulator.getDate());
    }

    public static int getIntUnixTimestamp() {
        return (int)(System.currentTimeMillis() / 1000L);
    }

    public static boolean isNumeric(String string) throws IllegalArgumentException {
        boolean isnumeric = false;
        if (string != null && !string.equals(PREVIEW)) {
            char aChar;
            char[] chars;
            isnumeric = true;
            char[] cArray = chars = string.toCharArray();
            int n = cArray.length;
            for (int i = 0; i < n && (isnumeric = Character.isDigit(aChar = cArray[i])); ++i) {
            }
        }
        return isnumeric;
    }

    public int getUserCount() {
        return gameEnvironment.getHabboManager().getOnlineCount();
    }

    public int getRoomCount() {
        return gameEnvironment.getRoomManager().getActiveRooms().size();
    }

    static {
        secureRandom = new SecureRandom();
        Thread hook = new Thread(new Runnable(){

            @Override
            public synchronized void run() {
                Emulator.dispose();
            }
        });
        hook.setPriority(10);
        Runtime.getRuntime().addShutdownHook(hook);
    }
}

