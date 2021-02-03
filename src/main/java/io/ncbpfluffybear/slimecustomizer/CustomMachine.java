package io.ncbpfluffybear.slimecustomizer;

import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomMachine extends AContainer implements RecipeDisplayItem {

    private final String id;
    private final ItemStack progressItem;
    private final int energyConsumption;
    private final int energyBuffer;
    private final HashMap<Pair<ItemStack, ItemStack>, Integer> customRecipes;

    public CustomMachine(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                         String id, Material progressItem, int energyConsumption, int energyBuffer,
                         HashMap<Pair<ItemStack, ItemStack>, Integer> customRecipes) {
        super(category, item, recipeType, recipe);

        this.id = id;
        this.progressItem = new CustomItem(progressItem, "");
        this.energyConsumption = energyConsumption;
        this.energyBuffer = energyBuffer;
        this.customRecipes = customRecipes;

        // Gets called in AContainer, but customRecipes is null at that time.
        registerDefaultRecipes();

        registerBlockHandler(id, (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), getOutputSlots());
                inv.dropItems(b.getLocation(), getInputSlots());
            }

            return true;
        });
    }

    @Override
    public ItemStack getProgressBar() {
        return progressItem;
    }

    @Override
    public int getCapacity() {
        return energyBuffer;
    }

    @Override
    public int getEnergyConsumption() {
        return energyConsumption;
    }

    @Override
    public int getSpeed() {
        return 1;
    }

    @Override
    protected void registerDefaultRecipes() {
        if (customRecipes == null) {
            return;
        }

        customRecipes.forEach((recipe, time) ->
            registerRecipe(time, recipe.getFirstValue().clone(), recipe.getSecondValue().clone())
        );

    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>(recipes.size() * 2);

        for (MachineRecipe recipe : recipes) {
            displayRecipes.add(recipe.getInput()[0]);
            displayRecipes.add(recipe.getOutput()[recipe.getOutput().length - 1]);
        }

        return displayRecipes;
    }

    @Override
    public String getMachineIdentifier() {
        return id;
    }
}
