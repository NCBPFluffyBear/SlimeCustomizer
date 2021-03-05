package io.ncbpfluffybear.slimecustomizer.registration;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.ncbpfluffybear.slimecustomizer.SlimeCustomizer;
import io.ncbpfluffybear.slimecustomizer.Utils;
import io.ncbpfluffybear.slimecustomizer.objects.CustomSCItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * {@link Items} registers the items
 * in the items config file.
 *
 * @author NCBPFluffyBear
 */
public class Items {

    public static boolean register(Config items) {
        for (String itemKey : items.getKeys()) {
            if (itemKey.equals("EXAMPLE_ITEM")) {
                SlimeCustomizer.getInstance().getLogger().log(Level.WARNING, "Your items.yml file still contains the example item! " +
                    "Did you forget to set up the plugin?");
            }

            String itemType = items.getString(itemKey + ".item-type");
            String materialString = items.getString(itemKey + ".item-id").toUpperCase();
            SlimefunItemStack tempStack;
            ItemStack item = null;
            int amount;

            Category category = Utils.getCategory(items.getString(itemKey + ".category"), itemKey);
            if (category == null) {return false;}

            try {
                amount = Integer.parseInt(items.getString(itemKey + ".item-amount"));
            } catch (NumberFormatException e) {
                Utils.disable("The item-amount for " + itemKey + " must be a positive integer!");
                return false;
            }


            if (itemType.equalsIgnoreCase("CUSTOM")) {

                Material material = Material.getMaterial(materialString);

                /* Item material type */
                if ((material == null && !materialString.startsWith("SKULL"))) {
                    Utils.disable("The item-id for " + itemKey + " is invalid!");
                    return false;
                } else if (material != null) {
                    item = new ItemStack(material);
                } else if (materialString.startsWith("SKULL")) {
                    item = SkullItem.fromHash(materialString.replace("SKULL", ""));
                }

                item.setAmount(amount);

                // Building lore
                List<String> itemLore = Utils.colorList(items.getStringList(itemKey + ".item-lore"));

                tempStack = new SlimefunItemStack(itemKey, item, items.getString(itemKey + ".item-name"));

                // Adding lore
                ItemMeta tempMeta = tempStack.getItemMeta();
                tempMeta.setLore(itemLore);
                tempStack.setItemMeta(tempMeta);
            } else if (itemType.equalsIgnoreCase("SAVEDITEM")) {
                item = Utils.retrieveSavedItem(materialString, amount, true);
                if (item == null) {return false;}

                tempStack = new SlimefunItemStack(itemKey, item);
            } else {
                Utils.disable("The item-id for " + itemKey + " can only be CUSTOM or SAVEDITEM!");
                return false;
            }

            String recipeTypeString = items.getString(itemKey + ".crafting-recipe-type").toUpperCase();
            RecipeType recipeType = Utils.getRecipeType(recipeTypeString, itemKey);
            if (recipeType == null) {
                Utils.disable("The crafting-recipe-type for " + itemKey + " is not valid! Refer to the wiki to see" +
                    " acceptable inputs.");
                return false;
            }

            /* Crafting recipe */
            ItemStack[] recipe = Utils.buildCraftingRecipe(items, itemKey, recipeType);
            if (recipe == null) {return false;}

            // the main item should have an amount of 1
            tempStack.setAmount(1);
            
            // use the item they made/saved as output
            new CustomSCItem(category, tempStack, recipeType, recipe, item).register(SlimeCustomizer.getInstance());

            if (recipeType == RecipeType.COMPRESSOR) {
                // add the recipe to the electric press so it can be automated
                ItemStack finalItem = item;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ((AContainer) Objects.requireNonNull(SlimefunItems.ELECTRIC_PRESS.getItem()))
                                .registerRecipe(6, recipe[0], finalItem);
                    }
                }.runTask(SlimeCustomizer.getInstance());
            }

            Utils.notify("Item " + itemKey + " has been registered!");
        }

        return true;
    }

}
