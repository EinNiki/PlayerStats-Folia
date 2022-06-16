package com.gmail.artemis.the.gr8.playerstats.config;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.enums.Query;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ConfigHandler {

    private File configFile;
    private FileConfiguration config;
    private final Main plugin;
    private final int configVersion;

    public ConfigHandler(Main p) {
        plugin = p;

        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(configFile);
        configVersion = 3;

        checkConfigVersion();
    }

    /** Reloads the config from file, or creates a new file with default values if there is none. */
    public boolean reloadConfig() {
        try {
            if (!configFile.exists()) {
                saveDefaultConfig();
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            return true;
        }
        catch (Exception e) {
            plugin.getLogger().warning(e.toString());
            return false;
        }
    }

    /** Returns the config setting for include-whitelist-only.
     <p>Default: false</p>*/
    public boolean whitelistOnly() {
        return config.getBoolean("include-whitelist-only", false);
    }

    /** Returns the config setting for exclude-banned-players.
     <p>Default: false</p>*/
    public boolean excludeBanned() {
        return config.getBoolean("exclude-banned-players", false);
    }

    /** Returns the number of maximum days since a player has last been online.
     <p>Default: 0 (which signals not to use this limit)</p>*/
    public int lastPlayedLimit() {
        return config.getInt("number-of-days-since-last-joined", 0);
    }

    /** Whether to use festive formatting, such as pride colors.
     <p>Default: true</p> */
    public boolean useFestiveFormatting() {
        return config.getBoolean("enable-festive-formatting", true);
    }

    /** Gets a String representation of an integer (with or without "!" in front of it) that can determine rainbow phase in Adventure.
     <p>Default: ""</p>*/
    public String getRainbowPhase() {
        return config.getString("rainbow-phase", "");
    }

    /** Whether or not to use HoverComponents in the usage explanation.
     <p>Default: true</p>*/
    public boolean useHoverText() {
        return config.getBoolean("enable-hover-text", true);
    }

    /** Returns the config setting for use-dots.
     <p>Default: true</p>*/
    public boolean useDots() {
        return config.getBoolean("use-dots", true);
    }

    /** Returns the config setting for top-list-max-size.
     <p>Default: 10</p> */
    public int getTopListMaxSize() {
        return config.getInt("top-list-max-size", 10);
    }

    /** Returns a String that represents the title for a server stat.
     <p>Default: "Total on"</p> */
    public String getServerTitle() {
        return config.getString("total-server-stat-title", "Total on");
    }

    /** Returns the specified server name for a server stat title.
     <p>Default: "this server"</p>*/
    public String getServerName() {
        return config.getString("your-server-name", "this server");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <p>Style: "none"</p>
     <p>Color Top: "green"</p>
     <p>Color Individual/Server: "gold"</p>*/
    public String getPlayerNameFormatting(Query selection, boolean isStyle) {
        String def;
        if (selection == Query.TOP) {
            def = "green";
        }
        else {
            def = "gold";
        }
        return getStringFromConfig(selection, isStyle, def, "player-names");
    }

    /** Returns true if playerNames Style is "bold", false if it is not.
     <p>Default: false</p>*/
    public boolean playerNameIsBold() {
        ConfigurationSection style = getRelevantSection(Query.PLAYER);

        if (style != null) {
            String styleString = style.getString("player-names");
            return styleString != null && styleString.equalsIgnoreCase("bold");
        }
        return false;
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "yellow"</p>*/
    public String getStatNameFormatting(Query selection, boolean isStyle) {
        return getStringFromConfig(selection, isStyle, "yellow", "stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or a Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "#FFD52B"</p>*/
    public String getSubStatNameFormatting(Query selection, boolean isStyle) {
        return getStringFromConfig(selection, isStyle, "#FFD52B", "sub-stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color Top: "#55AAFF"</p>
     <p>Color Individual/Server: "#ADE7FF"</p> */
    public String getStatNumberFormatting(Query selection, boolean isStyle) {
        String def;
        if (selection == Query.TOP) {
            def = "#55AAFF";
        }
        else {
            def = "#ADE7FF";
        }
        return getStringFromConfig(selection, isStyle, def,"stat-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color Top: "yellow"</p>
     <p>Color Server: "gold"</p>*/
    public String getTitleFormatting(Query selection, boolean isStyle) {
        String def;
        if (selection == Query.TOP) {
            def = "yellow";
        }
        else {
            def = "gold";
        }
        return getStringFromConfig(selection, isStyle, def, "title");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "gold"</p>*/
    public String getTitleNumberFormatting(boolean isStyle) {
        return getStringFromConfig(Query.TOP, isStyle, "gold", "title-number");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "#FFB80E"</p>*/
    public String getServerNameFormatting(boolean isStyle) {
        return getStringFromConfig(Query.SERVER, isStyle, "#FFB80E", "server-name");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "gold"</p>*/
    public String getRankNumberFormatting(boolean isStyle) {
        return getStringFromConfig(Query.TOP, isStyle, "gold", "rank-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are:
     <p>Style: "none"</p>
     <p>Color: "dark_gray"</p> */
    public String getDotsFormatting(boolean isStyle) {
        return getStringFromConfig(Query.TOP, isStyle, "dark_gray", "dots");
    }

    /** Returns the config value for a color or style option in string-format, the supplied default value, or null if no configSection was found. */
    private @Nullable String getStringFromConfig(Query selection, boolean isStyle, String def, String pathName){
        String path = isStyle ? pathName + "-style" : pathName;
        String defaultValue = isStyle ? "none" : def;

        ConfigurationSection section = getRelevantSection(selection);
        return section != null ? section.getString(path, defaultValue) : null;
    }

    /** Returns the config section that contains the relevant color or style option. */
    private @Nullable ConfigurationSection getRelevantSection(Query selection) {
        switch (selection) {
            case TOP -> {
                return config.getConfigurationSection("top-list");
            }
            case PLAYER -> {
                return config.getConfigurationSection("individual-statistics");
            }
            case SERVER -> {
                return config.getConfigurationSection("total-server");
            }
            default -> {
                return null;
            }
        }
    }

    /** Create a config file if none exists yet (from the config.yml in the plugin's resources). */
    private void saveDefaultConfig() {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    /** Checks the number that "config-version" returns to see if the config needs updating, and if so, send it to the Updater.
     <p></p>
     <p>PlayerStats 1.1: "config-version" doesn't exist.</p>
     <p>PlayerStats 1.2: "config-version" is 2.</p>
     <p>PlayerStats 1.3: "config-version" is 3. </P>*/
    private void checkConfigVersion() {
        if (!config.contains("config-version") || config.getInt("config-version") != configVersion) {
            new ConfigUpdateHandler(plugin, configFile, configVersion);
        }
    }
}