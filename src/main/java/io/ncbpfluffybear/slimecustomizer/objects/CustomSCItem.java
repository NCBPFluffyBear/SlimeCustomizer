package io.ncbpfluffybear.slimecustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The {@link CustomSCItem} class is a wrapper
 * for the {@link SlimefunItem}.
 *
 * @author NCBPFluffyBear
 */
public class CustomSCItem extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public CustomSCItem(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack output) {
        super(category, item, recipeType, recipe, output);
    }

    @ParametersAreNonnullByDefault
    public CustomSCItem(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }
}
