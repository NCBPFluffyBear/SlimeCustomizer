package io.ncbpfluffybear.slimecustomizer;

import dev.j3fftw.extrautils.utils.LoreBuilderDynamic;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.CSCoreLibPlugin.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import org.bstats.bukkit.Metrics;
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

public class SlimeCustomizer extends JavaPlugin implements SlimefunAddon {

    public static SlimeCustomizer instance;

    @Override
    public void onEnable() {
        instance = this;

        // Read something from your config.yml
        Config cfg = new Config(this);
        Config machines = new Config(this, "machines.yml");

        if (cfg.getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
            new GitHubBuildsUpdater(this, getFile(), "NCBPFluffyBear/SlimeCustomizer/master/").start();
        }

        final Metrics metrics = new Metrics(this, 9841);

        /* Category */
        Category slime_customizer = new Category(
            new NamespacedKey(SlimeCustomizer.getInstance(), "slime_customizer"),
            new CustomItem(Material.REDSTONE_LAMP, "&cSlimeCustomizer"));

        /* File generation */
        final File machinesFile = new File(getInstance().getDataFolder(), "machines.yml");
        if (!machinesFile.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/machines.yml"), machinesFile.toPath());
            } catch (IOException e) {
                getInstance().getLogger().log(Level.SEVERE, "Failed to copy default machines.yml file", e);
            }
        }

        /* Machine registration */
        for (String machineKey : machines.getKeys()) {
            if (machineKey.equals("EXAMPLE_MACHINE")) {
                getInstance().getLogger().log(Level.WARNING, "Your machines.yml file still contains the example machine! " +
                    "Did you forget to set up the plugin?");
            }

            ItemStack[] ingredients = new ItemStack[9];

            /* Validating the machine */
            Material block = Material.getMaterial(machines.getString(machineKey + ".block-type"));
            Material progressItem = Material.getMaterial(machines.getString(machineKey + ".progress-bar-item"));
            int energyConsumption;
            int energyBuffer;
            HashMap<Pair<ItemStack, ItemStack>, Integer> customRecipe = new HashMap<>();

            /* Machine block type */
            if (block == null || !block.isBlock()) {
                disable("The block-type for " + machineKey + " MUST be a block!");
                return;
            }

            /* Progress bar type */
            if (progressItem == null) {
                disable("The progress-bar-item for " + machineKey + " is not a valid vanilla ID!");
                return;
            }

            /* Energy consumption and Energy buffer */
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

            /* Crafting recipe */
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

            /* Machine recipes */
            for (String recipeKey : machines.getKeys(machineKey + ".recipes")) {
                String path = machineKey + ".recipes." + recipeKey;
                int speed;
                ItemStack input = null;
                ItemStack output = null;

                /* Speed */
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
                    int amount;

                    /* Validate amount */
                    try {
                        amount = Integer.parseInt(machines.getString(path + "." + slot + ".amount"));
                    } catch (NumberFormatException e) {
                        disable("The amount of " + slot + "s for recipe " + recipeKey + " for " + machineKey
                            + " must be a positive integer!");
                        return;
                    }

                    if (amount < 0) {
                        disable("The amount of " + slot + "s for recipe " + recipeKey + " for " + machineKey
                            + " must be a positive integer!");
                        return;
                    }

                    if (type.equalsIgnoreCase("VANILLA")) {
                        Material vanillaMat = Material.getMaterial(material);
                        if (vanillaMat == null) {
                            disable("The " + slot + "ingredient for recipe" + recipeKey + " for " + machineKey
                                + " is not a valid vanilla ID!");
                            return;
                        } else {
                            if (i == 0) {
                                input = new ItemStack(vanillaMat);
                                input.setAmount(amount);
                                checkExceedsStackSize(input, slot, machineKey, recipeKey);
                            } else {
                                output = new ItemStack(vanillaMat);
                                output.setAmount(amount);
                                checkExceedsStackSize(output, slot, machineKey, recipeKey);
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
                                input.setAmount(amount);
                                checkExceedsStackSize(input, slot, machineKey, recipeKey);
                            } else {
                                output = sfMat.getItem().clone();
                                output.setAmount(amount);
                                checkExceedsStackSize(output, slot, machineKey, recipeKey);
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

            new CustomMachine(slime_customizer, tempStack, RecipeType.ENHANCED_CRAFTING_TABLE, ingredients,
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

    private static SlimeCustomizer getInstance() {
        return instance;
    }

    private void disable(String reason) {
        getLogger().log(Level.SEVERE, reason);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    private void checkExceedsStackSize(ItemStack item, String slot, String machineKey, String recipeKey) {
         if (item.getAmount() > item.getMaxStackSize()) {
             disable("The " + slot + "ingredient for recipe" + recipeKey + " for " + machineKey
                 + " has a max stack size of " + item.getMaxStackSize() + "!");
         }
    }

}
