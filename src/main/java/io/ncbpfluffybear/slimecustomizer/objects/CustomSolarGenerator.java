package io.ncbpfluffybear.slimecustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.SolarGenerator;
import io.ncbpfluffybear.slimecustomizer.Utils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The {@link CustomSolarGenerator} class is a generified
 * solar generator.
 *
 * @author NCBPFluffyBear
 * @author TheBusyBiscuit
 */
public class CustomSolarGenerator extends SolarGenerator {

    @ParametersAreNonnullByDefault
    public CustomSolarGenerator(ItemGroup category, int dayEnergy, int nightEnergy, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, dayEnergy, nightEnergy, item, recipeType, recipe);
    }

    @Override
    public int getGeneratedOutput(@Nonnull Location location, @Nonnull Config data) {
        World world = location.getWorld();

        if (world.getEnvironment() != World.Environment.NORMAL) {
            return 0;
        } else {
            boolean isDaytime = Utils.isDaytime(world);

            // Performance optimization for daytime-only solar generators
            if (!isDaytime && getNightEnergy() < 1) {
                return 0;
            } else if (!world.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)
                || location.getBlock().getRelative(0, 1, 0).getLightFromSky() < 15) {
                return 0;
            } else {
                return isDaytime ? getDayEnergy() : getNightEnergy();
            }
        }
    }

}