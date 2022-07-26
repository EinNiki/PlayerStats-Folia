package com.gmail.artemis.the.gr8.playerstats.msg.msgutils;

import com.gmail.artemis.the.gr8.playerstats.enums.Unit;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;

/** A utility class that provides language keys to be put in a TranslatableComponent.*/
public final class LanguageKeyHandler {

    private static HashMap<Statistic, String> statNameKeys;

    public LanguageKeyHandler() {
        statNameKeys = generateStatNameKeys();
    }

    /** Checks if a given Key is the language key "stat_type.minecraft.killed"
     or "commands.kill.success.single" (which results in "Killed %s").*/
    public static boolean isKeyForKillEntity(String statKey) {
        return statKey.equalsIgnoreCase("stat_type.minecraft.killed") ||
                statKey.equalsIgnoreCase("commands.kill.success.single");
    }

    /** Returns a language key to replace the default Statistic.Kill_Entity key.
     @return the key "commands.kill.success.single", which results in "Killed %s" */
    public static String getAlternativeKeyForKillEntity() {
        return "commands.kill.success.single";
    }

    /** Checks if a given Key is the language key "stat_type.minecraft.killed_by"
     or "stat.minecraft.deaths" (which results in "Number of Deaths").*/
    public static boolean isKeyForEntityKilledBy(String statKey) {
        return statKey.equalsIgnoreCase("stat_type.minecraft.killed_by") ||
                statKey.equalsIgnoreCase("stat.minecraft.deaths");
    }

    /** Returns a language key to replace the default Statistic.Entity_Killed_By key.
     @return the key "stat.minecraft.deaths", which results in "Number of Deaths"
     (meant to be followed by {@link #getAlternativeKeyForEntityKilledByArg()})*/
    public static String getAlternativeKeyForEntityKilledBy() {
        return "stat.minecraft.deaths";
    }

    /** Checks if a given Key is the language key "book.byAuthor"
     (which results in "by %s"). */
    public static boolean isKeyForEntityKilledByArg(String statKey) {
        return statKey.equalsIgnoreCase("book.byAuthor");
    }

    /** Returns a language key to complete the alternative key for Statistic.Entity_Killed_By.
     @return the key "book.byAuthor", which results in "by %". If used after
     {@link #getAlternativeKeyForEntityKilledBy()}, you will get "Number of Deaths" "by %s"*/
    public static String getAlternativeKeyForEntityKilledByArg() {
        return "book.byAuthor";
    }

    public static String convertToName(String key) {
        if (key.equalsIgnoreCase("soundCategory.block")) {
            return Unit.BLOCK.getLabel();
        } else if (isKeyForKillEntity(key)) {
            return "times_killed";
        } else if (isKeyForEntityKilledBy(key)) {
            return "number_of_times_killed";
        } else if (isKeyForEntityKilledByArg(key)) {
            return "by";
        }
        String toReplace = "";
        if (key.contains("stat")) {
            if (key.contains("type")) {
                toReplace = "stat_type";
            } else {
                toReplace = "stat";
            }
        } else if (key.contains("entity")) {
            toReplace = "entity";
        } else if (key.contains("block")) {
            toReplace = "block";
        } else if (key.contains("item")) {
            toReplace = "item";
        }
        toReplace = toReplace + ".minecraft.";
        return key.replace(toReplace, "");
    }

    public String getStatKey(@NotNull Statistic statistic) {
        if (statistic.getType() == Statistic.Type.UNTYPED) {
            return "stat.minecraft." + statNameKeys.get(statistic);
        }
        else {
            return "stat_type.minecraft." + statNameKeys.get(statistic);
        }
    }

    /** Get the official Key from the NameSpacedKey for this entityType,
     or return null if no enum constant can be retrieved or entityType is UNKNOWN.*/
    public @Nullable String getEntityKey(EntityType entity) {
        if (entity == null || entity == EntityType.UNKNOWN) return null;
        else {
            return "entity.minecraft." + entity.getKey().getKey();
        }
    }

    /** Get the official Key from the NameSpacedKey for this item Material,
     or return null if no enum constant can be retrieved.*/
    public @Nullable String getItemKey(Material item) {
        if (item == null) return null;
        else if (item.isBlock()) {
            return getBlockKey(item);
        }
        else {
            return "item.minecraft." + item.getKey().getKey();
        }
    }

    /** Returns the official Key from the NameSpacedKey for the block Material provided,
     or return null if no enum constant can be retrieved.*/
    public @Nullable String getBlockKey(Material block) {
        if (block == null) return null;
        else if (block.toString().toLowerCase().contains("wall_banner")) {  //replace wall_banner with regular banner, since there is no key for wall banners
            String blockName = block.toString().toLowerCase().replace("wall_", "");
            Material newBlock = EnumHandler.getBlockEnum(blockName);
            return (newBlock != null) ? "block.minecraft." + newBlock.getKey().getKey() : null;
        }
        else {
            return "block.minecraft." + block.getKey().getKey();
        }
    }

    public @Nullable String getUnitKey(Unit unit) {
        if (unit == Unit.BLOCK) {
            return "soundCategory.block";
        } else {
            return null;
        }
    }

    private @NotNull HashMap<Statistic, String> generateStatNameKeys() {
        //get the enum names for all statistics first
        HashMap<Statistic, String> statNames = new HashMap<>(Statistic.values().length);
        Arrays.stream(Statistic.values()).forEach(statistic -> statNames.put(statistic, statistic.toString().toLowerCase()));

        //replace the ones for which the language key is different from the enum name
        statNames.put(Statistic.ARMOR_CLEANED, "clean_armor");
        statNames.put(Statistic.BANNER_CLEANED, "clean_banner");
        statNames.put(Statistic.DROP_COUNT, "drop");
        statNames.put(Statistic.CAKE_SLICES_EATEN, "eat_cake_slice");
        statNames.put(Statistic.ITEM_ENCHANTED, "enchant_item");
        statNames.put(Statistic.CAULDRON_FILLED, "fill_cauldron");
        statNames.put(Statistic.DISPENSER_INSPECTED, "inspect_dispenser");
        statNames.put(Statistic.DROPPER_INSPECTED, "inspect_dropper");
        statNames.put(Statistic.HOPPER_INSPECTED, "inspect_hopper");
        statNames.put(Statistic.BEACON_INTERACTION, "interact_with_beacon");
        statNames.put(Statistic.BREWINGSTAND_INTERACTION, "interact_with_brewingstand");
        statNames.put(Statistic.CRAFTING_TABLE_INTERACTION, "interact_with_crafting_table");
        statNames.put(Statistic.FURNACE_INTERACTION, "interact_with_furnace");
        statNames.put(Statistic.CHEST_OPENED, "open_chest");
        statNames.put(Statistic.ENDERCHEST_OPENED, "open_enderchest");
        statNames.put(Statistic.SHULKER_BOX_OPENED, "open_shulker_box");
        statNames.put(Statistic.NOTEBLOCK_PLAYED, "play_noteblock");
        statNames.put(Statistic.PLAY_ONE_MINUTE, "play_time");
        statNames.put(Statistic.RECORD_PLAYED, "play_record");
        statNames.put(Statistic.FLOWER_POTTED, "pot_flower");
        statNames.put(Statistic.TRAPPED_CHEST_TRIGGERED, "trigger_trapped_chest");
        statNames.put(Statistic.NOTEBLOCK_TUNED, "tune_noteblock");
        statNames.put(Statistic.CAULDRON_USED, "use_cauldron");

        //do the same for the statistics that have a subtype
        statNames.put(Statistic.DROP, "dropped");
        statNames.put(Statistic.PICKUP, "picked_up");
        statNames.put(Statistic.MINE_BLOCK, "mined");
        statNames.put(Statistic.USE_ITEM, "used");
        statNames.put(Statistic.BREAK_ITEM, "broken");
        statNames.put(Statistic.CRAFT_ITEM, "crafted");
        statNames.put(Statistic.KILL_ENTITY, "killed");
        statNames.put(Statistic.ENTITY_KILLED_BY, "killed_by");

        return statNames;
    }
}