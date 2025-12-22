
package io.ncbpfluffybear.slimecustomizer.objects;

import dev.j3fftw.extrautils.utils.LoreBuilderDynamic;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineTier;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineType;
import io.ncbpfluffybear.slimecustomizer.Utils;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles custom machines and generators.
 *
 * @author NCBPFluffyBear
 */
public class SCMachine {

    private final Config config;
    private final String key;
    private boolean valid = true;

    private final String materialString;
    private final Material progressItem;
    private final String machineType;
    private ItemStack block;
    private int energyConsumption = 0;
    private int energyProduction = 0;
    private int energyBuffer = 0;

    // Internal stats
    // Speed is a double to allow for decimal modifiers (e.g. 0.5x or 1.5x)
    private double speed = 1.0; 
    private MachineTier tier = MachineTier.MEDIUM;

    private final ItemStack[] recipe;
    private final RecipeType recipeType;
    private SlimefunItemStack machineStack;

    public SCMachine(Config config, String key, String machineType) {
        this.config = config;
        this.key = key;

        // Load base config values
        materialString = config.getString(key + ".block-type").toUpperCase();
        progressItem = Material.getMaterial(config.getString(key + ".progress-bar-item").toUpperCase());
        this.machineType = machineType;

        validate();
        Utils.updateCraftingRecipeFormat(config, key);
        Utils.updateCategoryFormat(config, key);

        recipeType = Utils.getRecipeType(config.getString(key + ".crafting-recipe-type"), key);
        if (recipeType == null) valid = false;

        recipe = Utils.buildCraftingRecipe(config, key, recipeType);
        if (recipe == null) valid = false;

        if (valid) {
            buildStack();
        }
    }

    private void validate() {
        // Ensure the block type is valid
        Material material = Material.getMaterial(materialString);
        if ((material == null || !material.isBlock()) && !materialString.startsWith("SKULL")) {
            Utils.disable("Block-type for " + key + " must be a valid block.");
            valid = false;
        } else if (material != null && material.isBlock()) {
            block = new ItemStack(material);
        } else if (materialString.startsWith("SKULL")) {
            block = SlimefunUtils.getCustomHead(materialString.replace("SKULL", "").toLowerCase());
        }

        // Ensure progress bar item is valid
        if (progressItem == null) {
            Utils.disable("Invalid progress-bar-item for " + key);
            valid = false;
        }

        // Parse Machine Tier from config
        if (config.getConfiguration().contains(key + ".tier")) {
            String tierName = config.getString(key + ".tier").toUpperCase(Locale.ROOT);
            try {
                this.tier = MachineTier.valueOf(tierName);
            } catch (IllegalArgumentException e) {
                // Fallback for common aliases or typos in the config
                if (tierName.contains("BASIC")) this.tier = MachineTier.BASIC;
                else if (tierName.contains("ADVANCED")) this.tier = MachineTier.ADVANCED;
                else if (tierName.contains("END")) this.tier = MachineTier.END_GAME;
                else if (tierName.contains("GOOD")) this.tier = MachineTier.GOOD;
                else if (tierName.contains("AVG")) this.tier = MachineTier.AVERAGE;
                else this.tier = MachineTier.MEDIUM;
            }
        }

        if (machineType.equalsIgnoreCase("machine")) {
            try {
                energyConsumption = Integer.parseInt(config.getString(key + ".stats.energy-consumption"));
                energyBuffer = Integer.parseInt(config.getString(key + ".stats.energy-buffer"));

                // Load speed as double to support decimals
                this.speed = config.getConfiguration().getDouble(key + ".stats.speed", 1.0);

            } catch (NumberFormatException e) {
                Utils.disable("Stats for " + key + " must be valid numbers.");
                valid = false;
            }

            // Sanity check: Energy stats cannot be negative, and speed must be > 0
            if (energyConsumption < 0 || energyBuffer < 0 || speed <= 0) {
                Utils.disable("Stats for " + key + " cannot be negative (and speed must be greater than 0).");
                valid = false;
            }

        } else if (machineType.equalsIgnoreCase("generator")) {
            try {
                energyProduction = Integer.parseInt(config.getString(key + ".stats.energy-production"));
                energyBuffer = Integer.parseInt(config.getString(key + ".stats.energy-buffer"));
            } catch (NumberFormatException e) {
                Utils.disable("Generator stats for " + key + " must be integers.");
                valid = false;
            }

            if (energyProduction < 0 || energyBuffer < 0) {
                Utils.disable("Generator stats for " + key + " cannot be negative.");
                valid = false;
            }
        }
    }

    private void buildStack() {
        if (!valid) return;

        Utils.updateLoreFormat(config, key, machineType);

        List<String> statsLore = new ArrayList<>();

        if (machineType.equalsIgnoreCase("machine")) {
            statsLore.add("");
            // Construct visual stats for the lore
            statsLore.add(LoreBuilder.machine(this.tier, MachineType.MACHINE));
            
            // Cast double to float here because LoreBuilder expects a float
            statsLore.add(LoreBuilder.speed((float) this.speed));
            
            statsLore.add(LoreBuilderDynamic.powerPerTick(getEnergyExchange()));
            // Manually add the buffer capacity line
            statsLore.add(LoreBuilder.powerBuffer(energyBuffer));
        } else {
            // Visual stats for generator
            statsLore.add("");
            // [UPDATED] Add Tier display for Generators too
            statsLore.add(LoreBuilder.machine(this.tier, MachineType.GENERATOR)); 
            statsLore.add(LoreBuilder.powerBuffer(energyBuffer));
            // Show Base Energy. The actual output multiplier is shown in the recipe view in CustomGenerator
            statsLore.add(LoreBuilderDynamic.powerPerTick(getEnergyExchange())); 
        }

        // Pull custom lore lines from config if they exist
        List<String> loreFromConfig = config.getConfiguration().getStringList(key + "." + machineType + "-lore");

        // Merge config lore with generated stats lore
        List<String> itemLore = Utils.colorList(Stream.concat(
            loreFromConfig != null ? loreFromConfig.stream() : Stream.empty(),
            statsLore.stream()
        ).collect(Collectors.toList()));

        machineStack = new SlimefunItemStack(key, block, config.getString(key + "." + machineType + "-name"));

        ItemMeta tempMeta = machineStack.getItemMeta();
        if (tempMeta != null) {
            tempMeta.setLore(itemLore);
            machineStack.setItemMeta(tempMeta);
        }
    }

    // --- Getters ---

    public double getSpeed() { return speed; }
    
    public MachineTier getTier() { return tier; }

    private int getEnergyExchange() {
        if (machineType.equalsIgnoreCase("machine")) return energyConsumption;
        if (machineType.equalsIgnoreCase("generator")) return energyProduction;
        return 0;
    }

    public Material getProgressItem() { return progressItem; }
    public int getEnergyProduction() { return energyProduction; }
    public int getEnergyConsumption() { return energyConsumption; }
    public int getEnergyBuffer() { return energyBuffer; }
    public ItemStack[] getRecipe() { return recipe; }
    public SlimefunItemStack getMachineStack() { return machineStack; }
    public RecipeType getRecipeType() { return recipeType; }
    public boolean isValid() { return valid; }
}
