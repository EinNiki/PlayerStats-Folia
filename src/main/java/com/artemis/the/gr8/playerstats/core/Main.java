package com.artemis.the.gr8.playerstats.core;

import com.artemis.the.gr8.playerstats.api.PlayerStats;
import com.artemis.the.gr8.playerstats.api.StatNumberFormatter;
import com.artemis.the.gr8.playerstats.api.StatTextFormatter;
import com.artemis.the.gr8.playerstats.api.StatManager;
import com.artemis.the.gr8.playerstats.core.commands.*;
import com.artemis.the.gr8.playerstats.core.msg.msgutils.NumberFormatter;
import com.artemis.the.gr8.playerstats.core.multithreading.ThreadManager;
import com.artemis.the.gr8.playerstats.core.statrequest.RequestManager;
import com.artemis.the.gr8.playerstats.core.msg.OutputManager;
import com.artemis.the.gr8.playerstats.core.config.ConfigHandler;
import com.artemis.the.gr8.playerstats.core.listeners.JoinListener;
import com.artemis.the.gr8.playerstats.core.msg.msgutils.LanguageKeyHandler;
import com.artemis.the.gr8.playerstats.core.sharing.ShareManager;
import com.artemis.the.gr8.playerstats.core.utils.MyLogger;
import com.artemis.the.gr8.playerstats.core.utils.OfflinePlayerHandler;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * PlayerStats' Main class
 */
public final class Main extends JavaPlugin implements PlayerStats {

    private static JavaPlugin pluginInstance;
    private static PlayerStats playerStatsAPI;
    private static BukkitAudiences adventure;

    private static ConfigHandler config;
    private static ThreadManager threadManager;
    private static LanguageKeyHandler languageKeyHandler;
    private static OfflinePlayerHandler offlinePlayerHandler;

    private static RequestManager requestManager;
    private static OutputManager outputManager;
    private static ShareManager shareManager;

    @Override
    public void onEnable() {
        initializeMainClasses();
        registerCommands();
        setupMetrics();

        //register the listener
        Bukkit.getPluginManager().registerEvents(new JoinListener(threadManager), this);
        
        //finish up
        this.getLogger().info("Enabled PlayerStats!");
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        this.getLogger().info("Disabled PlayerStats!");
    }

    public void reloadPlugin() {
        config.reload();
        MyLogger.setDebugLevel(config.getDebugLevel());
        languageKeyHandler.reload();
        offlinePlayerHandler.reload();
        outputManager.updateSettings();
        shareManager.updateSettings();
    }

    /**
     *
     * @return the JavaPlugin instance associated with PlayerStats
     * @throws IllegalStateException if PlayerStats is not enabled
     */
    public static @NotNull JavaPlugin getPluginInstance() throws IllegalStateException {
        if (pluginInstance == null) {
            throw new IllegalStateException("PlayerStats is not loaded!");
        }
        return pluginInstance;
    }

    public static @NotNull PlayerStats getPlayerStatsAPI() throws IllegalStateException {
        if (playerStatsAPI == null) {
            throw new IllegalStateException("PlayerStats does not seem to be loaded!");
        }
        return playerStatsAPI;
    }

    /**
     * Initialize all classes that need initializing,
     * and store references to classes that are
     * needed for the Command classes or the API.
     */
    private void initializeMainClasses() {
        pluginInstance = this;
        playerStatsAPI = this;
        adventure = BukkitAudiences.create(this);

        config = ConfigHandler.getInstance();
        languageKeyHandler = LanguageKeyHandler.getInstance();
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();
        shareManager = ShareManager.getInstance();

        outputManager = new OutputManager(adventure);
        requestManager = new RequestManager(outputManager);
        threadManager = new ThreadManager(this, outputManager);
    }

    /**
     * Register all commands and assign the tabCompleter
     * to the relevant commands.
     */
    private void registerCommands() {
        TabCompleter tabCompleter = new TabCompleter();

        PluginCommand statcmd = this.getCommand("statistic");
        if (statcmd != null) {
            statcmd.setExecutor(new StatCommand(outputManager, threadManager));
            statcmd.setTabCompleter(tabCompleter);
        }
        PluginCommand excludecmd = this.getCommand("statisticexclude");
        if (excludecmd != null) {
            excludecmd.setExecutor(new ExcludeCommand(outputManager));
            excludecmd.setTabCompleter(tabCompleter);
        }

        PluginCommand reloadcmd = this.getCommand("statisticreload");
        if (reloadcmd != null) {
            reloadcmd.setExecutor(new ReloadCommand(threadManager));
        }
        PluginCommand sharecmd = this.getCommand("statisticshare");
        if (sharecmd != null) {
            sharecmd.setExecutor(new ShareCommand(outputManager));
        }
    }

    /**
     * Setup bstats
     */
    private void setupMetrics() {
        // Using Folia AsyncScheduler
        AsyncScheduler asyncScheduler = Bukkit.getAsyncScheduler();

        asyncScheduler.runDelayed(this, delay -> {
            final Metrics metrics = new Metrics(pluginInstance, 15923);

            final boolean placeholderExpansionActive;
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                PlaceholderExpansion expansion = PlaceholderAPIPlugin
                        .getInstance()
                        .getLocalExpansionManager()
                        .getExpansion("playerstats");
                placeholderExpansionActive = expansion != null;
            } else {
                placeholderExpansionActive = false;
            }

            metrics.addCustomChart(new SimplePie("using_placeholder_expansion", () -> placeholderExpansionActive ? "yes" : "no"));
        }, 200L, TimeUnit.MILLISECONDS);

    }

    @Override
    public @NotNull String getVersion() {
        return String.valueOf(this.getDescription().getVersion().charAt(0));
    }

    @Override
    public StatManager getStatManager() {
        return requestManager;
    }

    @Override
    public StatTextFormatter getStatTextFormatter() {
        return outputManager.getMainMessageBuilder();
    }

    @Contract(" -> new")
    @Override
    public @NotNull StatNumberFormatter getStatNumberFormatter() {
        return new NumberFormatter();
    }
}