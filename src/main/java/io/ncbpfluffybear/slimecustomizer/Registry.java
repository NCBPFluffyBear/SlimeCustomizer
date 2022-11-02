package io.ncbpfluffybear.slimecustomizer;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores stuff.
 *
 * @author ybw0014
 */
public final class Registry {
    public static final Map<ItemStack[], Pair<RecipeType, String>> existingRecipes = new HashMap<>();
    public static final Map<String, ItemGroup> allItemGroups = new HashMap<>();
}
