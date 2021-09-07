package io.ncbpfluffybear.slimecustomizer.objects;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.SolarGenerator;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link CustomSolarGenerator} class is a generified
 * solar generator.
 *
 * @author NCBPFluffyBear
 * @author TheBusyBiscuit
 */
public class CustomSolarGenerator extends SolarGenerator {

    public CustomSolarGenerator(ItemGroup category, int dayEnergy, int nightEnergy, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, dayEnergy, nightEnergy, item, recipeType, recipe);
    }

    @Override
    public int getGeneratedOutput(Location l, Config data) {
        World world = l.getWorld();

        if (world.getEnvironment() != World.Environment.NORMAL) {
            return 0;
        } else {
            boolean isDaytime = isDaytime(world);

            // Performance optimization for daytime-only solar generators
            if (!isDaytime && getNightEnergy() < 1) {
                return 0;
            } else if (!world.isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)
                || l.getBlock().getRelative(0, 1, 0).getLightFromSky() < 15) {
                return 0;
            } else {
                return isDaytime ? getDayEnergy() : getNightEnergy();
            }
        }
    }

    private boolean isDaytime(World world) {
        long time = world.getTime();
        return !world.hasStorm() && !world.isThundering() && (time < 12300 || time > 23850);
    }

}