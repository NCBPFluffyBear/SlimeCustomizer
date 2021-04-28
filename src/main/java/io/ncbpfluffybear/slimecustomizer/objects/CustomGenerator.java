package io.ncbpfluffybear.slimecustomizer.objects;

import dev.j3fftw.extrautils.utils.LoreBuilderDynamic;
import dev.j3fftw.extrautils.utils.Utils;
import io.github.thebusybiscuit.slimefun4.api.events.AsyncGeneratorProcessCompleteEvent;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link CustomGenerator} class is a generified
 * {@link AGenerator}.
 *
 * @author NCBPFluffyBear
 */
public class CustomGenerator extends AGenerator {

    private final ItemStack progressItem;
    private final int energyProduction;
    private final int energyBuffer;
    private final List<MachineFuel> customRecipes;

    public CustomGenerator(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                           Material progressItem, int energyProduction, int energyBuffer,
                           List<MachineFuel> customRecipes) {
        super(category, item, recipeType, recipe);

        this.progressItem = new CustomItem(progressItem, "");
        this.energyProduction = energyProduction;
        this.energyBuffer = energyBuffer;
        this.customRecipes = customRecipes;

        // Gets called in AGenerator, but customRecipes is null at that time.
        registerDefaultFuelTypes();
    }

    @Override
    public int getGeneratedOutput(Location l, Config data) {
        BlockMenu inv = BlockStorage.getInventory(l);

        if (isProcessing(l)) {
            int timeleft = progress.get(l);

            if (timeleft > 0) {
                ChestMenuUtils.updateProgressbar(inv, 22, timeleft, processing.get(l).getTicks(), getProgressBar());

                if (isChargeable()) {
                    int charge = getCharge(l, data);

                    if (getCapacity() - charge >= getEnergyProduction()) {
                        progress.put(l, timeleft - 1);
                        return getEnergyProduction();
                    }

                    return 0;
                } else {
                    progress.put(l, timeleft - 1);
                    return getEnergyProduction();
                }
            } else {

                inv.pushItem(processing.get(l).getOutput(), getOutputSlots());

                inv.replaceExistingItem(22, new CustomItem(Material.BLACK_STAINED_GLASS_PANE, " "));

                Bukkit.getPluginManager().callEvent(new AsyncGeneratorProcessCompleteEvent(l, CustomGenerator.this, getProcessing(l)));

                progress.remove(l);
                processing.remove(l);
                return 0;
            }
        } else {
            Map<Integer, Integer> found = new HashMap<>();
            MachineFuel fuel = findRecipe(inv, found);

            if (fuel != null) {
                for (Map.Entry<Integer, Integer> entry : found.entrySet()) {
                    inv.consumeItem(entry.getKey(), entry.getValue());
                }

                processing.put(l, fuel);
                progress.put(l, fuel.getTicks());
            }

            return 0;
        }
    }

    private MachineFuel findRecipe(BlockMenu menu, Map<Integer, Integer> found) {
        for (MachineFuel fuel : fuelTypes) {
            for (int slot : getInputSlots()) {
                if (fuel.test(menu.getItemInSlot(slot))) {
                    found.put(slot, fuel.getInput().getAmount());
                    return fuel;
                }
            }
        }

        return null;
    }

    @Override
    public ItemStack getProgressBar() {
        return progressItem;
    }

    @Override
    protected void registerDefaultFuelTypes() {
        if (customRecipes == null) {
            return;
        }

        for (MachineFuel fuel : customRecipes) {
            registerFuel(fuel);
        }
    }

    @Override
    public int getEnergyProduction() {
        return energyProduction;
    }

    @Override
    public int getCapacity() {
        return energyBuffer;
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>(customRecipes.size() * 2);

        for (MachineFuel fuel : customRecipes) {
            ItemStack input = fuel.getInput();
            ItemStack customInput = new CustomItem(input, input.getItemMeta().getDisplayName(),
                "&8\u21E8 &7Lasts " + Utils.ticksToSeconds(fuel.getTicks()),
                LoreBuilderDynamic.powerPerTick(getEnergyProduction()),
                "&8\u21E8 &e\u26A1 &7" + fuel.getTicks() * getEnergyProduction() + " J in total"
            );
            displayRecipes.add(customInput);
            if (fuel.getOutput() != null) {
                displayRecipes.add(fuel.getOutput());
            } else {
                displayRecipes.add(new CustomItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&7No Output"));
            }

        }

        return displayRecipes;
    }



}
