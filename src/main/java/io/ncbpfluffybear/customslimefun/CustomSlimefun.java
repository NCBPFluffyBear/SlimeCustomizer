package io.ncbpfluffybear.customslimefun;

import dev.j3fftw.extrautils.utils.LoreBuilderDynamic;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.CSCoreLibPlugin.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Level;

public class CustomSlimefun extends JavaPlugin implements SlimefunAddon {

    public static CustomSlimefun instance;

    @Override
    public void onEnable() {

        instance = this;

        // Read something from your config.yml
        Config cfg = new Config(this);
        Config machines = new Config(this, "machines.yml");

        if (cfg.getBoolean("options.auto-update")) {
            // You could start an Auto-Updater for example
        }

        Category custom_slimefun = new Category(
            new NamespacedKey(CustomSlimefun.getInstance(), "custom_slimefun"),
            new CustomItem(Material.REDSTONE_LAMP, "&cCustom Slimefun"));

        final File machinesFile = new File(getInstance().getDataFolder(), "machines.yml");
        if (!machinesFile.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/machines.yml"), machinesFile.toPath());
            } catch (IOException e) {
                getInstance().getLogger().log(Level.SEVERE, "Failed to copy default machines.yml file", e);
            }
        }

        for (String machineKey : machines.getKeys()) {

            ItemStack[] ingredients = new ItemStack[9];

            // Validate the config
            Material block = Material.getMaterial(machines.getString(machineKey + ".block-type"));
            Material progressItem = Material.getMaterial(machines.getString(machineKey + ".progress-bar-item"));
            int energyConsumption;
            int energyBuffer;
            HashMap<Pair<ItemStack, ItemStack>, Integer> customRecipe = new HashMap<>();

            if (block == null || !block.isBlock()) {
                disable("The block-type for " + machineKey + " MUST be a block!");
                return;
            }

            if (progressItem == null) {
                disable("The progress-bar-item for " + machineKey + " is not a valid vanilla ID!");
                return;
            }

            try {
                energyConsumption = Integer.parseInt(machines.getString(machineKey + ".stats.energy-consumption"));
                energyBuffer = Integer.parseInt(machines.getString(machineKey + ".stats.energy-buffer"));
            } catch (NumberFormatException e) {
                disable("The energy-consumption and energy-buffer for " + machineKey + " must be a positive integer!");
                return;
            }

            if (energyConsumption < 0 || energyBuffer < 0) {
                disable("The energy-consumption and energy-buffer for " + machineKey + " must be a positive integer!");
                return;
            }

            for (int i = 0; i < 9; i++) {
                String path = machineKey + ".crafting-recipe";
                int configIndex = i + 1;
                // Shift recipe index up 1 so it's easier for the user to read config
                String type = machines.getString(path + "." + configIndex + ".type");
                String material = machines.getString(path + "." + configIndex + ".id");
                if (type.equalsIgnoreCase("NONE")) {
                    ingredients[i] = null;
                } else if (type.equalsIgnoreCase("VANILLA")) {
                    Material vanillaMat = Material.getMaterial(material);
                    if (vanillaMat == null) {
                        disable("Crafting ingredient " + configIndex + " for " + machineKey + " is not a valid " +
                            "vanilla ID!");
                        return;
                    } else {
                        ingredients[i] = new ItemStack(vanillaMat);
                    }
                } else if (type.equalsIgnoreCase("SLIMEFUN")) {
                    SlimefunItem sfMat = SlimefunItem.getByID(material);
                    if (sfMat == null) {
                        disable("Crafting ingredient " + configIndex + " for " + machineKey
                            + " is not a valid Slimefun ID!");
                    } else {
                        ingredients[i] = sfMat.getItem().clone();
                    }
                } else {
                    disable("Crafting ingredient " + configIndex + " for " + machineKey
                        + " can only have a type of VANILLA, SLIMEFUN, or NONE!");
                }
            }

            for (String recipeKey : machines.getKeys(machineKey + ".recipes")) {
                String path = machineKey + ".recipes" + recipeKey;
                int speed;
                ItemStack input = null;
                ItemStack output = null;

                // Validate config
                try {
                    speed = Integer.parseInt(machines.getString(path + ".speed-in-seconds"));
                } catch (NumberFormatException e) {
                    disable("The speed-in-seconds for recipe " + recipeKey + " for " + machineKey
                        + " must be a positive integer!");
                    return;
                }

                if (speed < 0) {
                    disable("The speed-in-seconds for recipe " + recipeKey + " for " + machineKey
                        + " must be a positive integer!");
                    return;
                }

                for (int i = 0; i < 2; i++) {

                    // Run this 2 times for input/output
                    String slot;
                    if (i == 0) {
                        slot = "input";
                    } else {
                        slot = "output";
                    }

                    String type = machines.getString(path + "." + slot + ".type");
                    String material = machines.getString(path + "." + slot + ".id");
                    if (type.equalsIgnoreCase("VANILLA")) {
                        Material vanillaMat = Material.getMaterial(material);
                        if (vanillaMat == null) {
                            disable("The " + slot + "ingredient for recipe" + recipeKey + " for " + machineKey
                                + " is not a valid vanilla ID!");
                            return;
                        } else {
                            if (i == 0) {
                                input = new ItemStack(vanillaMat);
                            } else {
                                output = new ItemStack(vanillaMat);
                            }
                        }
                    } else if (type.equalsIgnoreCase("SLIMEFUN")) {
                        SlimefunItem sfMat = SlimefunItem.getByID(material);
                        if (sfMat == null) {
                            disable("The " + slot + " ingredient for recipe" + recipeKey + " for " + machineKey
                                + " is not a valid Slimefun ID!");
                        } else {
                            if (i == 0) {
                                input = sfMat.getItem().clone();
                            } else {
                                output = sfMat.getItem().clone();
                            }
                        }
                    } else {
                        disable("The " + slot + " ingredient type for recipe" + recipeKey + " for " + machineKey
                            + " can only be VANILLA or SLIMEFUN!");
                    }
                }

                customRecipe.put(new Pair<>(input, output), speed);

            }

            SlimefunItemStack tempStack = new SlimefunItemStack(machineKey,
                block,
                machines.getString(machineKey + ".machine-name"),
                machines.getString(machineKey + ".machine-lore"),
                "",
                LoreBuilderDynamic.powerBuffer(energyBuffer),
                LoreBuilderDynamic.powerPerTick(energyConsumption)
            );

            new CustomMachine(custom_slimefun, tempStack, RecipeType.ENHANCED_CRAFTING_TABLE, ingredients,
                machineKey, progressItem, energyConsumption, energyBuffer, customRecipe).register(this);
        }
    }

    @Override
    public void onDisable() {
        // Logic for disabling the plugin...
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/NCBPFluffyBear/CustomSlimefun/issues";
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    private static CustomSlimefun getInstance() {
        return instance;
    }

    private void disable(String reason) {
        getLogger().log(Level.SEVERE, reason);
        Bukkit.getPluginManager().disablePlugin(this);
    }

}
