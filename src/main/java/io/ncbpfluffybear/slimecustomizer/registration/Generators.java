package io.ncbpfluffybear.slimecustomizer.registration;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.ncbpfluffybear.slimecustomizer.SlimeCustomizer;
import io.ncbpfluffybear.slimecustomizer.Utils;
import io.ncbpfluffybear.slimecustomizer.objects.CustomGenerator;
import io.ncbpfluffybear.slimecustomizer.objects.SCMachine;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * {@link Generators} registers the generators
 * in the generators config file.
 *
 * @author NCBPFluffyBear
 */
public class Generators {

    public static boolean register(Config generators) {
        for (String generatorKey : generators.getKeys()) {
            if (generatorKey.equals("EXAMPLE_GENERATOR")) {
                SlimeCustomizer.getInstance().getLogger().log(Level.WARNING, "Your generators.yml file still contains" +
                    " the example generator! " +
                    "Did you forget to set up the plugin?");
            }

            SCMachine generator = new SCMachine(generators, generatorKey, "generator");
            if (!generator.isValid()) {return false;}

            ItemGroup category = Utils.getCategory(generators.getString(generatorKey + ".category"), generatorKey);
            if (category == null) {return false;}

            /* Generator recipes */
            List<MachineFuel> customRecipe = new ArrayList<>();

            for (String recipeKey : generators.getKeys(generatorKey + ".recipes")) {
                String path = generatorKey + ".recipes." + recipeKey;
                int time;
                ItemStack input = null;
                ItemStack output = null;

                /* Time */
                try {
                    time = Integer.parseInt(generators.getString(path + ".time-in-seconds"));
                } catch (NumberFormatException e) {
                    Utils.disable("The time-in-seconds for recipe " + recipeKey + " for " + generatorKey
                        + " must be a positive integer!");
                    return false;
                }

                if (time < 0) {
                    Utils.disable("The time-in-seconds for recipe " + recipeKey + " for " + generatorKey
                        + " must be a positive integer!");
                    return false;
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
                        Utils.disable("The the input type for recipe " + recipeKey + " for " + generatorKey
                            + " can only be VANILLA or SLIMEFUN!");
                        return false;
                    }

                    if (i == 0 || !type.equalsIgnoreCase("NONE")) {
                        try {
                            amount = Integer.parseInt(generators.getString(path + "." + slot + ".amount"));
                        } catch (NumberFormatException e) {
                            Utils.disable("The amount of " + slot + "s for recipe " + recipeKey + " for " + generatorKey
                                + " must be a positive integer!");
                            return false;
                        }
                    }

                    if (amount < 0) {
                        Utils.disable("The amount of " + slot + "s for recipe " + recipeKey + " for " + generatorKey
                            + " must be a positive integer!");
                        return false;
                    }

                    if (type.equalsIgnoreCase("VANILLA")) {
                        Material vanillaMat = Material.getMaterial(material);
                        if (vanillaMat == null) {
                            Utils.disable("The " + slot + "ingredient for recipe" + recipeKey + " for " + generatorKey
                                + " is not a valid vanilla ID!");
                            return false;
                        } else {
                            if (i == 0) {
                                input = new ItemStack(vanillaMat);
                                input.setAmount(amount);
                                if (!Utils.checkFitsStackSize(input, slot, generatorKey, recipeKey)) {return false;}
                            } else {
                                output = new ItemStack(vanillaMat);
                                output.setAmount(amount);
                                if (!Utils.checkFitsStackSize(output, slot, generatorKey, recipeKey)) {return false;}
                            }
                        }
                    } else if (type.equalsIgnoreCase("SLIMEFUN")) {
                        SlimefunItem sfMat = SlimefunItem.getById(material);
                        if (sfMat == null) {
                            Utils.disable("The " + slot + " ingredient for recipe" + recipeKey + " for " + generatorKey
                                + " is not a valid Slimefun ID!");
                            return false;
                        } else {
                            if (i == 0) {
                                input = sfMat.getItem().clone();
                                input.setAmount(amount);
                                if (!Utils.checkFitsStackSize(input, slot, generatorKey, recipeKey)) {return false;}
                            } else {
                                output = sfMat.getItem().clone();
                                output.setAmount(amount);
                                if (!Utils.checkFitsStackSize(output, slot, generatorKey, recipeKey)) {return false;}
                            }
                        }
                    } else if (type.equalsIgnoreCase("SAVEDITEM")) {
                        ItemStack savedItem = Utils.retrieveSavedItem(material, amount, true);
                        if (savedItem == null) {return false;}
                        if (i == 0) {
                            input = savedItem.clone();
                            if (!Utils.checkFitsStackSize(input, slot, generatorKey, recipeKey)) {return false;}
                        } else {
                            output = savedItem.clone();
                            if (!Utils.checkFitsStackSize(output, slot, generatorKey, recipeKey)) {return false;}
                        }
                    } else if (i == 0) {
                        Utils.disable("The " + slot + " ingredient type for recipe" + recipeKey + " for " + generatorKey
                            + " can only be VANILLA, SLIMEFUN, or SAVEDITEM!");
                        return false;
                    } else if (!type.equalsIgnoreCase("NONE")) {
                        Utils.disable("The " + slot + " ingredient type for recipe" + recipeKey + " for " + generatorKey
                            + " can only be VANILLA, SLIMEFUN, SAVEDITEM, or NONE!");
                        return false;
                    }
                }

                customRecipe.add(new MachineFuel(time, input, output));

            }

            new CustomGenerator(category, generator.getMachineStack(),
                generator.getRecipeType(), generator.getRecipe(),
                generator.getProgressItem(), generator.getEnergyProduction(),
                generator.getEnergyBuffer(), customRecipe
            ).register(SlimeCustomizer.getInstance());

            Utils.notify("Generator " + generatorKey + " has been registered!");
        }

        return true;
    }
}
