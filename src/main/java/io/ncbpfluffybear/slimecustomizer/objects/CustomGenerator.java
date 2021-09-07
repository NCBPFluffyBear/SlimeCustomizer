package io.ncbpfluffybear.slimecustomizer.objects;

import dev.j3fftw.extrautils.utils.LoreBuilderDynamic;
import dev.j3fftw.extrautils.utils.Utils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link CustomGenerator} class is a generified
 * {@link AGenerator}.
 *
 * @author NCBPFluffyBear
 */
public class CustomGenerator extends SCAGenerator {

    private final ItemStack progressItem;
    private final int energyProduction;
    private final int energyBuffer;
    private final List<MachineFuel> customRecipes;

    public CustomGenerator(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                           Material progressItem, int energyProduction, int energyBuffer,
                           List<MachineFuel> customRecipes) {
        super(category, item, recipeType, recipe);

        this.progressItem = new CustomItemStack(progressItem, "");
        this.energyProduction = energyProduction;
        this.energyBuffer = energyBuffer;
        this.customRecipes = customRecipes;

        getMachineProcessor().setProgressBar(getProgressBar());

        // Gets called in AGenerator, but customRecipes is null at that time.
        registerDefaultFuelTypes();
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
            ItemStack customInput = new CustomItemStack(input, input.getItemMeta().getDisplayName(),
                    "&8\u21E8 &7Lasts " + Utils.ticksToSeconds(fuel.getTicks()),
                    LoreBuilderDynamic.powerPerTick(getEnergyProduction()),
                    "&8\u21E8 &e\u26A1 &7" + fuel.getTicks() * getEnergyProduction() + " J in total"
            );
            displayRecipes.add(customInput);
            if (fuel.getOutput() != null) {
                displayRecipes.add(fuel.getOutput());
            } else {
                displayRecipes.add(new CustomItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&7No Output"));
            }

        }

        return displayRecipes;
    }


}
