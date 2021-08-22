package io.ncbpfluffybear.slimecustomizer.objects;

import io.github.thebusybiscuit.slimefun4.core.attributes.RandomMobDrop;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * A custom basic implementation of {@link RandomMobDrop}
 * with a constructor parameter for a chance value.
 */
public class SCMobDrop extends SlimefunItem implements RandomMobDrop {

    private final int chance;

    public SCMobDrop(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int chance) {
        super(category, item, recipeType, recipe);
        this.chance = chance;
    }

    public SCMobDrop(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                     ItemStack recipeOutput, int chance
    ) {
        super(category, item, recipeType, recipe, recipeOutput);
        this.chance = chance;
    }

    @Override
    public int getMobDropChance() {
        return chance;
    }
}
