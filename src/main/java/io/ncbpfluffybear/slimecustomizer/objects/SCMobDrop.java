package io.ncbpfluffybear.slimecustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RandomMobDrop;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A custom basic implementation of {@link RandomMobDrop}
 * with a constructor parameter for a chance value.
 */
public class SCMobDrop extends SlimefunItem implements RandomMobDrop {

    private final int chance;

    @ParametersAreNonnullByDefault
    public SCMobDrop(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int chance) {
        super(category, item, recipeType, recipe);
        this.chance = chance;
    }

    @ParametersAreNonnullByDefault
    public SCMobDrop(ItemGroup category,
                     SlimefunItemStack item,
                     RecipeType recipeType,
                     ItemStack[] recipe,
                     ItemStack recipeOutput,
                     int chance
    ) {
        super(category, item, recipeType, recipe, recipeOutput);
        this.chance = chance;
    }

    @Override
    public int getMobDropChance() {
        return chance;
    }
}
