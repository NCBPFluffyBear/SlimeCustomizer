package io.ncbpfluffybear.slimecustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link CustomSCItem} class is a wrapper
 * for the {@link SlimefunItem}.
 *
 * @author NCBPFluffyBear
 */
public class CustomSCItem extends SlimefunItem {

    public CustomSCItem(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack output) {
        // Force the template item to 1 to satisfy Slimefun's registry checks.
        super(category, fixStackSize(item), recipeType, recipe, output);
    }

    public CustomSCItem(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        // Clone the item to preserve the original stack size (e.g., 6) for the recipe output,
        // while the main item gets sanitized to 1 by the primary constructor.
        this(category, item, recipeType, recipe, item.clone().item());
    }

    // Quick fix: Slimefun warns if the base item stack size is > 1.
    private static SlimefunItemStack fixStackSize(SlimefunItemStack item) {
        item.setAmount(1);
        return item;
    }
}