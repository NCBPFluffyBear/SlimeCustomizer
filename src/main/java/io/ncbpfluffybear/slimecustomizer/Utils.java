package io.ncbpfluffybear.slimecustomizer;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * {@link Utils} contains utility methods.
 *
 * @author NCBPFluffyBear
 */
public class Utils {

    private static final NamespacedKey SCKEY = new NamespacedKey(SlimeCustomizer.getInstance(), "slimecustomizer_item");
    private static final List<RecipeType> STACK_LIMITED_MACHINES = new ArrayList<>(Arrays.asList(
        RecipeType.ENHANCED_CRAFTING_TABLE,
        RecipeType.ARMOR_FORGE,
        RecipeType.PRESSURE_CHAMBER,
        RecipeType.MAGIC_WORKBENCH
    ));

    public static void send(CommandSender s, String msg) {
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l[&aSlimeCustomizer&a&l]&7 " + msg));
    }

    public static boolean checkPermission(Player p, String permission) {
        if (!p.hasPermission(permission)) {
            Utils.send(p, "&cYou do not have permission to use this command!");
            return false;
        }

        return true;
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void notify(String reason) {
        Bukkit.getLogger().log(Level.INFO, "[SlimeCustomizer] " + ChatColor.GREEN + reason);
    }

    public static void disable(String reason) {
        Bukkit.getLogger().log(Level.SEVERE, "[SlimeCustomizer] " + reason);
        Bukkit.getPluginManager().disablePlugin(SlimeCustomizer.getInstance());
    }

    public static boolean checkFitsStackSize(ItemStack item, String slot, String machineKey, String recipeKey) {
        if (item.getAmount() > item.getMaxStackSize()) {
            disable("The " + slot + "ingredient for recipe" + recipeKey + " for " + machineKey
                + " has a max stack size of " + item.getMaxStackSize() + "!");
            return false;
        }
        return true;
    }


    public static List<String> colorList(List<String> plainList) {
        List<String> coloredList = new ArrayList<>();
        for (String s : plainList) {
            coloredList.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        return coloredList;
    }

    public static ItemStack[] buildCraftingRecipe(Config file, String key, RecipeType recipeType) {
        ItemStack[] recipe = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            String path = key + ".crafting-recipe";
            int configIndex = i + 1;
            // Shift recipe index up 1 so it's easier for the user to read config
            String type = file.getString(path + "." + configIndex + ".type").toUpperCase();
            String material = file.getString(path + "." + configIndex + ".id").toUpperCase();
            int amount;

            try {
                amount = Integer.parseInt(file.getString(path + "." + configIndex + ".amount"));
            } catch (NumberFormatException e) {
                Utils.disable("The amount for " + configIndex + " for " + key + " is not a valid " +
                    "vanilla ID!");
                return null;
            }

            // Only certain multiblock machines can use stack sizes larger than 1
            if (STACK_LIMITED_MACHINES.contains(recipeType) && amount > 1) {
                disable(recipeType.getKey().getKey().toUpperCase() + " can not use items with a greater stack size than 1!" +
                    " Please change the crafting-recipe-type or crafting-recipe.#.amount for " + key + ".");
                return null;
            }

            if (type.equalsIgnoreCase("NONE")) {
                recipe[i] = null;
            } else if (type.equalsIgnoreCase("VANILLA")) {
                Material vanillaMat = Material.getMaterial(material);
                if (vanillaMat == null) {
                    Utils.disable("Crafting ingredient " + configIndex + " for " + key + " is not a valid " +
                        "vanilla ID!");
                    return null;
                } else {
                    recipe[i] = new ItemStack(vanillaMat, amount);
                }
            } else if (type.equalsIgnoreCase("SLIMEFUN")) {
                SlimefunItem sfMat = SlimefunItem.getByID(material);
                if (sfMat == null) {
                    Utils.disable("Crafting ingredient " + configIndex + " for " + key
                        + " is not a valid Slimefun ID!");
                    return null;
                } else {
                    recipe[i] = new CustomItem(sfMat.getItem().clone(), amount);
                }
            } else if (type.equalsIgnoreCase("SAVEDITEM")) {
                recipe[i] = retrieveSavedItem(material, amount, true);
            } else {
                Utils.disable("Crafting ingredient " + configIndex + " for " + key
                    + " can only have a type of VANILLA, SLIMEFUN, SAVEDITEM, or NONE!");
                return null;
            }
        }

        AtomicBoolean invalid = new AtomicBoolean(false);

        SlimeCustomizer.existingRecipes.forEach((itemStacks, recipeTypePair) -> {
            if (Arrays.equals(itemStacks, recipe) && recipeType == recipeTypePair.getFirstValue()) {
                Utils.disable("The crafting recipe for " + key + " is already being used for "
                    + recipeTypePair.getSecondValue());
                invalid.set(true);
            }
        });

        if (invalid.get()) {
            return null;
        }

        SlimeCustomizer.existingRecipes.put(recipe, new Pair<>(recipeType, key));
        return recipe;
    }

    public static void updateLoreFormat(Config config, String key, String machineType) {
        String path = key + "." + machineType + "-lore";
        if (config.getStringList(path).toString().equals("[]")) {
            Bukkit.getLogger().log(Level.WARNING, "Your " + key + " was using the old lore system! Attempting to " +
                "reformat it now... " +
                "Read " + Links.ADDING_YOUR_ITEM + " to learn how to use multiline lore!");

            String lore = config.getString(path);
            config.setValue(path, new ArrayList<>(Collections.singleton(lore)));
            config.save();
        }
    }

    public static void updateCraftingRecipeFormat(Config config, String key) {
        String path = key + ".crafting-recipe";
        for (int i = 0; i < 9; i++) {
            int recipeIndex = i + 1;
            if (config.getString(path + "." + recipeIndex + ".amount") == null) {
                config.setValue(path + "." + recipeIndex + ".amount", 1);
            }
        }

        if (config.getString(key + ".crafting-recipe-type") == null) {
            config.setValue(key + ".crafting-recipe-type", "ENHANCED_CRAFTING_TABLE");
        }

        config.save();
    }

    public static void updateCategoryFormat(Config config, String key) {
        String path = key + ".category";

        if (config.getString(path) == null) {
            config.setValue(path, "slime_customizer");
        }

        config.save();
    }

    public static void updateInputAndOutputFormat(Config config, String key) {
        String path = key + ".recipes";
        for (String recipe : config.getKeys(path)) {
            for (int i = 0; i < 2; i++) {
                String transportType;
                if (i == 0) {
                    transportType = "input";
                } else {
                    transportType = "output";
                }

                String transportPath = path + "." + recipe + "." + transportType;

                // Check if there are sublocations for the input/outputs
                if (config.getString(transportPath + ".1") == null) {

                    // Move old values to their new sublocations
                    config.setValue(transportPath + ".1.type", config.getString(transportPath + ".type"));
                    config.setValue(transportPath + ".1.id", config.getString(transportPath + ".id"));
                    config.setValue(transportPath + ".1.amount", config.getInt(transportPath + ".amount"));

                    // Delete old value keys
                    config.setValue(transportPath + ".type", null);
                    config.setValue(transportPath + ".id", null);
                    config.setValue(transportPath + ".amount", null);

                    // Add the second input/output keys
                    config.setValue(transportPath + ".2.type", "NONE");
                    config.setValue(transportPath + ".2.id", "N/A");
                    config.setValue(transportPath + ".2.amount", 1);

                    Bukkit.getLogger().log(Level.WARNING, "Your " + key + " was using the old input/output system! " +
                        "Attempting to reformat it now... " +
                        "Read " + Links.ADDING_YOUR_MACHINE + " to learn what this new format does!");
                }
            }
        }

        config.save();
    }

    public static ItemStack retrieveSavedItem(String id, int amount, boolean disableIfNull) {
        File serializedItemFile = new File(SlimeCustomizer.getInstance().getDataFolder(), "saveditems/" + id + ".yml");
        if (!serializedItemFile.exists()) {
            if (disableIfNull) {
                disable(id + " could not be found in your saveditems folder! Make sure the file is a text file!");
            }
            return null;
        } else {
            ItemStack item = new Config(serializedItemFile.getPath()).getItem("item");
            item.setAmount(amount);
            return item;
        }
    }

    public static RecipeType getRecipeType(String str, String key) {
        if (str == null) {
            disable("The crafting-recipe-type for " + key + " has to be a multiblock machine!" );
            return null;
        }
        switch (str) {
            default:
                return null;
            case "ENCHANTED_CRAFTING_TABLE":
                Bukkit.getLogger().log(Level.WARNING, "Hey buddy, it's the ENHANCED crafting table, not ENCHANTED. " +
                    "Don't worry, I know what you mean. But you should probably fix that.");
                return RecipeType.ENHANCED_CRAFTING_TABLE;
            case "ENHANCED_CRAFTING_TABLE":
                return RecipeType.ENHANCED_CRAFTING_TABLE;
            case "MAGIC_WORKBENCH":
                return RecipeType.MAGIC_WORKBENCH;
            case "ARMOR_FORGE":
                return RecipeType.ARMOR_FORGE;
            case "COMPRESSOR":
                return RecipeType.COMPRESSOR;
            case "PRESSURE_CHAMBER":
                return RecipeType.PRESSURE_CHAMBER;
            case "SMELTERY":
                return RecipeType.SMELTERY;
            case "ORE_CRUSHER":
                return RecipeType.ORE_CRUSHER;
            case "GRIND_STONE":
                return RecipeType.GRIND_STONE;
            case "NONE":
                return RecipeType.NULL;
        }
    }

    public static Category getCategory(String str, String key) {
        Category category = SlimeCustomizer.allCategories.get(str);
        if (category == null) {
            disable(str + " is not a valid category for " + key + "!");
        }
        return category;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String toOrdinal(int i) {
        switch (i) {
            default:
                return "ERR";
            case 1:
                return "1st";
            case 2:
                return "2nd";
        }
    }

    public static ItemStack keyItem(ItemStack item, int i) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(SCKEY, PersistentDataType.INTEGER, i);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isKeyed(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(SCKEY, PersistentDataType.INTEGER);
    }

    public static int getItemKey(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().get(SCKEY, PersistentDataType.INTEGER);
    }

}
