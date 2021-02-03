package io.ncbpfluffybear.slimecustomizer;

import dev.j3fftw.extrautils.utils.LoreBuilderDynamic;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class SlimeCustomizer extends JavaPlugin implements SlimefunAddon {

    public static SlimeCustomizer instance;

    @Override
    public void onEnable() {
        instance = this;

        // Read something from your config.yml
        Config cfg = new Config(this);
        Config machines = new Config(this, "machines.yml");
        Config generators = new Config(this, "generators.yml");

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

        final File generatorsFile = new File(getInstance().getDataFolder(), "generators.yml");
        if (!generatorsFile.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/generators.yml"), generatorsFile.toPath());
            } catch (IOException e) {
                getInstance().getLogger().log(Level.SEVERE, "Failed to copy default generators.yml file", e);
            }
        }

        /* ----------------------------
           Custom Machine registration
           ------------------------- */
        for (String machineKey : machines.getKeys()) {
            if (machineKey.equals("EXAMPLE_MACHINE")) {
                getInstance().getLogger().log(Level.WARNING, "Your machines.yml file still contains the example machine! " +
                    "Did you forget to set up the plugin?");
            }

            ItemStack[] ingredients = new ItemStack[9];

            /* Validating the machine */
            String materialString = machines.getString(machineKey + ".block-type").toUpperCase();
            Material block = Material.getMaterial(materialString);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            Material progressItem = Material.getMaterial(machines.getString(machineKey + ".progress-bar-item").toUpperCase());
            int energyConsumption;
            int energyBuffer;
            HashMap<Pair<ItemStack, ItemStack>, Integer> customRecipe = new HashMap<>();

            /* Machine block type */
            if ((block == null || !block.isBlock()) && !materialString.startsWith("SKULL")) {
                disable("The block-type for " + machineKey + " MUST be a block!");
                return;
            }

            if (materialString.startsWith("SKULL")) {
                skull = new CustomItem(SkullItem.fromHash(materialString.replace("SKULL", "")));
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
                String type = machines.getString(path + "." + configIndex + ".type").toUpperCase();
                String material = machines.getString(path + "." + configIndex + ".id").toUpperCase();
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

                    String type = machines.getString(path + "." + slot + ".type").toUpperCase();
                    String material = machines.getString(path + "." + slot + ".id").toUpperCase();
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

            SlimefunItemStack tempStack;

            if (block != null) {
                tempStack = new SlimefunItemStack(machineKey,
                    block,
                    machines.getString(machineKey + ".machine-name"),
                    machines.getString(machineKey + ".machine-lore"),
                    "",
                    "&bMachine",
                    LoreBuilderDynamic.powerBuffer(energyBuffer),
                    LoreBuilderDynamic.powerPerTick(energyConsumption)
                );
            } else {
                tempStack = new SlimefunItemStack(machineKey,
                    skull,
                    machines.getString(machineKey + ".machine-name"),
                    machines.getString(machineKey + ".machine-lore"),
                    "",
                    "&bMachine",
                    LoreBuilderDynamic.powerBuffer(energyBuffer),
                    LoreBuilderDynamic.powerPerTick(energyConsumption)
                );
            }

            new CustomMachine(slime_customizer, tempStack, RecipeType.ENHANCED_CRAFTING_TABLE, ingredients,
                machineKey, progressItem, energyConsumption, energyBuffer, customRecipe).register(this);
        }

        /* ----------------------------
          Custom Generator registration
          --------------------------- */
        for (String generatorKey : generators.getKeys()) {
            if (generatorKey.equals("EXAMPLE_GENERATOR")) {
                getInstance().getLogger().log(Level.WARNING, "Your generators.yml file still contains the example generator! " +
                    "Did you forget to set up the plugin?");
            }

            ItemStack[] ingredients = new ItemStack[9];

            /* Validating the machine */
            String materialString = generators.getString(generatorKey + ".block-type").toUpperCase();
            Material block = Material.getMaterial(materialString);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            Material progressItem = Material.getMaterial(generators.getString(generatorKey + ".progress-bar-item").toUpperCase());
            int energyProduction;
            int energyBuffer;
            List<MachineFuel> customRecipe = new ArrayList<>();

            /* Generator block type */
            if ((block == null || !block.isBlock()) && !materialString.startsWith("SKULL")) {
                disable("The block-type for " + generatorKey + " MUST be a block!");
                return;
            }

            if (materialString.startsWith("SKULL")) {
                skull = new CustomItem(SkullItem.fromHash(materialString.replace("SKULL", "")));
            }

            /* Progress bar type */
            if (progressItem == null) {
                disable("The progress-bar-item for " + generatorKey + " is not a valid vanilla ID!");
                return;
            }

            /* Energy consumption and Energy buffer */
            try {
                energyProduction = Integer.parseInt(generators.getString(generatorKey + ".stats.energy-production"));
                energyBuffer = Integer.parseInt(generators.getString(generatorKey + ".stats.energy-buffer"));
            } catch (NumberFormatException e) {
                disable("The energy-consumption and energy-buffer for " + generatorKey + " must be a positive integer!");
                return;
            }

            if (energyProduction < 0 || energyBuffer < 0) {
                disable("The energy-production and energy-buffer for " + generatorKey + " must be a positive integer!");
                return;
            }

            /* Crafting recipe */
            for (int i = 0; i < 9; i++) {
                String path = generatorKey + ".crafting-recipe";
                int configIndex = i + 1;
                // Shift recipe index up 1 so it's easier for the user to read config
                String type = generators.getString(path + "." + configIndex + ".type").toUpperCase();
                String material = generators.getString(path + "." + configIndex + ".id").toUpperCase();
                if (type.equalsIgnoreCase("NONE")) {
                    ingredients[i] = null;
                } else if (type.equalsIgnoreCase("VANILLA")) {
                    Material vanillaMat = Material.getMaterial(material);
                    if (vanillaMat == null) {
                        disable("Crafting ingredient " + configIndex + " for " + generatorKey + " is not a valid " +
                            "vanilla ID!");
                        return;
                    } else {
                        ingredients[i] = new ItemStack(vanillaMat);
                    }
                } else if (type.equalsIgnoreCase("SLIMEFUN")) {
                    SlimefunItem sfMat = SlimefunItem.getByID(material);
                    if (sfMat == null) {
                        disable("Crafting ingredient " + configIndex + " for " + generatorKey
                            + " is not a valid Slimefun ID!");
                    } else {
                        ingredients[i] = sfMat.getItem().clone();
                    }
                } else {
                    disable("Crafting ingredient " + configIndex + " for " + generatorKey
                        + " can only have a type of VANILLA, SLIMEFUN, or NONE!");
                }
            }

            /* Generator recipes */
            for (String recipeKey : generators.getKeys(generatorKey + ".recipes")) {
                String path = generatorKey + ".recipes." + recipeKey;
                int time;
                ItemStack input = null;
                ItemStack output = null;

                /* Time */
                try {
                    time = Integer.parseInt(generators.getString(path + ".time-in-seconds"));
                } catch (NumberFormatException e) {
                    disable("The time-in-seconds for recipe " + recipeKey + " for " + generatorKey
                        + " must be a positive integer!");
                    return;
                }

                if (time < 0) {
                    disable("The time-in-seconds for recipe " + recipeKey + " for " + generatorKey
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

                    String type = generators.getString(path + "." + slot + ".type").toUpperCase();
                    String material = generators.getString(path + "." + slot + ".id").toUpperCase();
                    int amount = 0;

                    /* Validate amount */
                    if (i == 0 && type.equalsIgnoreCase("NONE")) {
                        disable("The the input type for recipe " + recipeKey + " for " + generatorKey
                            + " can only be VANILLA or SLIMEFUN!");
                        return;
                    }

                    if (i == 0 || !type.equalsIgnoreCase("NONE")) {
                        try {
                            amount = Integer.parseInt(generators.getString(path + "." + slot + ".amount"));
                        } catch (NumberFormatException e) {
                            disable("The amount of " + slot + "s for recipe " + recipeKey + " for " + generatorKey
                                + " must be a positive integer!");
                            return;
                        }
                    }

                    if (amount < 0) {
                        disable("The amount of " + slot + "s for recipe " + recipeKey + " for " + generatorKey
                            + " must be a positive integer!");
                        return; 
                    }

                    if (type.equalsIgnoreCase("VANILLA")) {
                        Material vanillaMat = Material.getMaterial(material);
                        if (vanillaMat == null) {
                            disable("The " + slot + "ingredient for recipe" + recipeKey + " for " + generatorKey
                                + " is not a valid vanilla ID!");
                            return;
                        } else {
                            if (i == 0) {
                                input = new ItemStack(vanillaMat);
                                input.setAmount(amount);
                                checkExceedsStackSize(input, slot, generatorKey, recipeKey);
                            } else {
                                output = new ItemStack(vanillaMat);
                                output.setAmount(amount);
                                checkExceedsStackSize(output, slot, generatorKey, recipeKey);
                            }
                        }
                    } else if (type.equalsIgnoreCase("SLIMEFUN")) {
                        SlimefunItem sfMat = SlimefunItem.getByID(material);
                        if (sfMat == null) {
                            disable("The " + slot + " ingredient for recipe" + recipeKey + " for " + generatorKey
                                + " is not a valid Slimefun ID!");
                        } else {
                            if (i == 0) {
                                input = sfMat.getItem().clone();
                                input.setAmount(amount);
                                checkExceedsStackSize(input, slot, generatorKey, recipeKey);
                            } else {
                                output = sfMat.getItem().clone();
                                output.setAmount(amount);
                                checkExceedsStackSize(output, slot, generatorKey, recipeKey);
                            }
                        }
                    } else if (i == 0) {
                        disable("The " + slot + " ingredient type for recipe" + recipeKey + " for " + generatorKey
                            + " can only be VANILLA or SLIMEFUN!");
                        return;
                    } else if (!type.equalsIgnoreCase("NONE")) {
                        disable("The " + slot + " ingredient type for recipe" + recipeKey + " for " + generatorKey
                            + " can only be VANILLA, SLIMEFUN, or NONE!");
                    }
                }

                customRecipe.add(new MachineFuel(time, input, output));

            }

            SlimefunItemStack tempStack;

            if (block != null) {
                tempStack = new SlimefunItemStack(generatorKey,
                    block,
                    generators.getString(generatorKey + ".generator-name"),
                    generators.getString(generatorKey + ".generator-lore"),
                    "",
                    "&aGenerator",
                    LoreBuilderDynamic.powerBuffer(energyBuffer),
                    LoreBuilderDynamic.powerPerTick(energyProduction)
                );
            } else {
                tempStack = new SlimefunItemStack(generatorKey,
                    skull,
                    generators.getString(generatorKey + ".generator-name"),
                    generators.getString(generatorKey + ".generator-lore"),
                    "",
                    "&aGenerator",
                    LoreBuilderDynamic.powerBuffer(energyBuffer),
                    LoreBuilderDynamic.powerPerTick(energyProduction)
                );
            }



            new CustomGenerator(slime_customizer, tempStack, RecipeType.ENHANCED_CRAFTING_TABLE, ingredients,
                generatorKey, progressItem, energyProduction, energyBuffer, customRecipe).register(this);
        }
    }

    @Override
    public void onDisable() {
        // Logic for disabling the plugin...
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/NCBPFluffyBear/SlimeCustomizer/issues";
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
