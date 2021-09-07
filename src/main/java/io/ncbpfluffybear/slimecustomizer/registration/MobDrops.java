package io.ncbpfluffybear.slimecustomizer.registration;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.slimecustomizer.SlimeCustomizer;
import io.ncbpfluffybear.slimecustomizer.Utils;
import io.ncbpfluffybear.slimecustomizer.objects.SCMobDrop;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Level;

/**
 * {@link MobDrops} registers the mob drops
 * in the mob-drops config file.
 *
 * @author NCBPFluffyBear
 */
public class MobDrops {

    public static boolean register(Config drops) {
        for (String dropKey : drops.getKeys()) {
            if (dropKey.equals("EXAMPLE_DROP")) {
                SlimeCustomizer.getInstance().getLogger().log(Level.WARNING, "Your mob-drops.yml file still contains the example mob drop! " +
                    "Did you forget to set up the plugin?");
            }

            String itemType = drops.getString(dropKey + ".item-type");
            String materialString = drops.getString(dropKey + ".item-id").toUpperCase();
            SlimefunItemStack tempStack;
            ItemStack item = null;
            int amount;
            int chance;

            ItemGroup category = Utils.getCategory(drops.getString(dropKey + ".category"), dropKey);
            if (category == null) {return false;}

            try {
                amount = Integer.parseInt(drops.getString(dropKey + ".item-amount"));
            } catch (NumberFormatException e) {
                Utils.disable("The item-amount for " + dropKey + " must be a positive integer!");
                return false;
            }

            try {
                chance = Integer.parseInt(drops.getString(dropKey + ".chance"));
            } catch (NumberFormatException e) {
                Utils.disable("The chance for " + dropKey + " must be a positive integer!");
                return false;
            }

            if (chance < 0 || chance > 100) {
                Utils.disable("The chance for " + dropKey + " must be a between 1 and 100 (inclusive)!");
                return false;
            }

            if (itemType.equalsIgnoreCase("CUSTOM")) {

                Material material = Material.getMaterial(materialString);

                /* Item material type */
                if (material == null && !materialString.startsWith("SKULL")) {
                    Utils.disable("The item-id for " + dropKey + " is invalid!");
                    return false;
                } else if (material != null) {
                    item = new ItemStack(material);
                } else if (materialString.startsWith("SKULL")) {
                    item = SlimefunUtils.getCustomHead(materialString.replace("SKULL", ""));
                }

                item.setAmount(amount);

                // Building lore
                List<String> itemLore = Utils.colorList(drops.getStringList(dropKey + ".item-lore"));

                tempStack = new SlimefunItemStack(dropKey, item, drops.getString(dropKey + ".item-name"));

                // Adding lore
                ItemMeta tempMeta = tempStack.getItemMeta();
                tempMeta.setLore(itemLore);
                tempStack.setItemMeta(tempMeta);
            } else if (itemType.equalsIgnoreCase("SAVEDITEM")) {
                item = Utils.retrieveSavedItem(materialString, amount, true);
                if (item == null) {return false;}

                tempStack = new SlimefunItemStack(dropKey, item);
            } else {
                Utils.disable("The item-id for " + dropKey + " can only be CUSTOM or SAVEDITEM!");
                return false;
            }

            // Get mob type that drops the item
            String mobType = drops.getString(dropKey + ".mob");
            EntityType mob = EntityType.valueOf(mobType);
            String egg = drops.getString(dropKey + ".recipe-display-item");
            Material eggMaterial = Material.getMaterial(egg);

            if (mobType == null) {
                Utils.disable("The mob for " + dropKey + " is invalid!");
                return false;
            }

            if (mob == EntityType.UNKNOWN) {
                Utils.disable("The mob for " + dropKey + " is invalid!");
                return false;
            }

            if (eggMaterial == null) {
                Utils.disable("The recipe-display-item for " + dropKey + " is invalid!");
                return false;
            }

            /* Crafting recipe */
            ItemStack[] recipe = new ItemStack[] {
                    null, null, null,
                    null, new CustomItemStack(eggMaterial, "&b" + Utils.capitalize(mobType), "&7Kill a "
                    + Utils.capitalize(mobType))
            };

            if (itemType.equalsIgnoreCase("CUSTOM")) {
                new SCMobDrop(category, tempStack, RecipeType.MOB_DROP, recipe, chance
                ).register(SlimeCustomizer.getInstance());
            } else {
                new SCMobDrop(category, tempStack, RecipeType.MOB_DROP, recipe, item, chance
                ).register(SlimeCustomizer.getInstance());
            }

            Utils.notify("Mob drop " + dropKey + " has been registered!");
        }

        return true;
    }

}
