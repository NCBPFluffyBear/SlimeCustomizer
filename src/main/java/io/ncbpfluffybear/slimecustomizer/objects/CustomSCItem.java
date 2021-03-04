package io.ncbpfluffybear.slimecustomizer.objects;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link CustomSCItem} class is a wrapper
 * for the {@link SlimefunItem}.
 *
 * @author NCBPFluffyBear
 */
public class CustomSCItem extends SlimefunItem {

    public CustomSCItem(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack output) {
        super(category, item, recipeType, recipe, output);
    }

    public CustomSCItem(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }
}
