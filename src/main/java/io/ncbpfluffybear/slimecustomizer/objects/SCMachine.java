package io.ncbpfluffybear.slimecustomizer.objects;

import dev.j3fftw.extrautils.utils.LoreBuilderDynamic;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.slimecustomizer.Utils;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class encompasses all machines and generators.
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
    private final ItemStack[] recipe;
    private final RecipeType recipeType;
    private SlimefunItemStack machineStack;


    public SCMachine(Config config, String key, String machineType) {
        this.config = config;
        this.key = key;
        materialString = config.getString(key + ".block-type").toUpperCase();
        progressItem = Material.getMaterial(config.getString(key + ".progress-bar-item").toUpperCase());
        this.machineType = machineType;

        validateMachineSettings();
        Utils.updateCraftingRecipeFormat(config, key);
        Utils.updateCategoryFormat(config, key);
        recipeType = Utils.getRecipeType(config.getString(key + ".crafting-recipe-type"), key);
        if (recipeType == null) {valid = false;}
        recipe = Utils.buildCraftingRecipe(config, key, recipeType);
        if (recipe == null) {valid = false;}
        buildMachineStack();
    }

    private void validateMachineSettings() {
        /* Machine block type */
        Material material = Material.getMaterial(materialString);
        if ((material == null || !material.isBlock()) && !materialString.startsWith("SKULL")) {
            Utils.disable("The block-type for " + key + " MUST be a block!");
        } else if (material != null && material.isBlock()) {
            block = new ItemStack(material);
        } else if (materialString.startsWith("SKULL")) {
            block = SlimefunUtils.getCustomHead(materialString.replace("SKULL", "").toLowerCase());
        }

        /* Progress bar type */
        if (progressItem == null) {
            Utils.disable("The progress-bar-item for " + key + " is not a valid vanilla ID!");
        }

        if (machineType.equalsIgnoreCase("machine")) {
            /* Energy consumption and Energy buffer */
            try {
                energyConsumption = Integer.parseInt(config.getString(key + ".stats.energy-consumption"));
                energyBuffer = Integer.parseInt(config.getString(key + ".stats.energy-buffer"));
            } catch (NumberFormatException e) {
                Utils.disable("The energy-consumption and energy-buffer for " + key + " must be a positive integer!");
            }

            if (energyConsumption < 0 || energyBuffer < 0) {
                Utils.disable("The energy-consumption and energy-buffer for " + key + " must be a positive integer!");
            }

        } else if (machineType.equalsIgnoreCase("generator")) {
            /* Energy production and Energy buffer */
            try {
                energyProduction = Integer.parseInt(config.getString(key + ".stats.energy-production"));
                energyBuffer = Integer.parseInt(config.getString(key + ".stats.energy-buffer"));
            } catch (NumberFormatException e) {
                Utils.disable("The energy-consumption and energy-buffer for " + key + " must be a positive integer!");
            }

            if (energyProduction < 0 || energyBuffer < 0) {
                Utils.disable("The energy-production and energy-buffer for " + key + " must be a positive integer!");
            }
        }

    }

    private void buildMachineStack() {
        // Check and update if the old lore system is still being used
        Utils.updateLoreFormat(config, key, machineType);

        // Building lore
        List<String> itemLore = Utils.colorList(Stream.concat(
            config.getStringList(key + "." + machineType + "-lore").stream(),
            new ArrayList<>(Arrays.asList("", getMachineTag(),
                LoreBuilderDynamic.powerBuffer(energyBuffer),
                LoreBuilderDynamic.powerPerTick(getEnergyExchange()))).stream()
        ).collect(Collectors.toList()));

        // Two types of tempStacks for and without skull textures
        machineStack = new SlimefunItemStack(key, block, config.getString(key + "." + machineType + "-name"));

        // Adding lore
        ItemMeta tempMeta = machineStack.getItemMeta();
        tempMeta.setLore(itemLore);
        machineStack.setItemMeta(tempMeta);
    }

    private String getMachineTag() {
        if (machineType.equalsIgnoreCase("machine")) {
            return "&b" + Utils.capitalize(machineType);
        } else if (machineType.equalsIgnoreCase("generator")) {
            return "&a" + Utils.capitalize(machineType);
        }

        return null;
    }

    /**
     * Returns the energy production or consumption
     * based on the machineType
     *
     * @return energy the energy generated or consumed
     */
    private int getEnergyExchange() {
        if (machineType.equalsIgnoreCase("machine")) {
            return energyConsumption;
        } else if (machineType.equalsIgnoreCase("generator")) {
            return energyProduction;
        }

        return 0;
    }

    public Material getProgressItem() {
        return progressItem;
    }

    /**
     * Returns the energy production
     * Only relevant for generators
     *
     * @return energyProduction the energy production
     */
    public int getEnergyProduction() {
        return energyProduction;
    }

    /**
     * Returns the energy consumption
     * Only relevant for machines
     *
     * @return energyConsumption the energy consumption
     */
    public int getEnergyConsumption() {
        return energyConsumption;
    }

    /**
     * Returns the energy buffer
     * Relevant for both generators and machines
     *
     * @return energyBuffer the energy buffer
     */
    public int getEnergyBuffer() {
        return energyBuffer;
    }

    public ItemStack[] getRecipe() {
        return recipe;
    }

    public SlimefunItemStack getMachineStack() {
        return machineStack;
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public boolean isValid() {
        return valid;
    }

}
