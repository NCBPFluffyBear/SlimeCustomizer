package io.ncbpfluffybear.slimecustomizer.registration;

import io.ncbpfluffybear.slimecustomizer.SlimeCustomizer;
import io.ncbpfluffybear.slimecustomizer.Utils;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link Categories} registers the categories
 * in the categories config file.
 *
 * @author NCBPFluffyBear
 */
public class Categories {

    public static boolean register(Config categories) {
        if (categories.getKeys().isEmpty()) {
            Utils.disable("No categories were found! Please add and use a category from categories.yml");
            return false;
        }

        for (String categoryKey : categories.getKeys()) {
            String name = categories.getString(categoryKey + ".category-name");
            String materialString = categories.getString(categoryKey + ".category-item");
            Material material = Material.getMaterial(materialString);
            ItemStack item = null;

            /* Item material type */
            if ((material == null && !materialString.startsWith("SKULL"))) {
                Utils.disable("The category-item for " + categoryKey + " is invalid!");
                return false;
            } else if (material != null) {
                item = new ItemStack(material);
            } else if (materialString.startsWith("SKULL")) {
                item = SkullItem.fromHash(materialString.replace("SKULL", ""));
            }

            Category tempCategory = new Category(new NamespacedKey(SlimeCustomizer.getInstance(), categoryKey),
                new CustomItem(item, name));

            AtomicBoolean disable = new AtomicBoolean(false);
            SlimeCustomizer.allCategories.forEach((key, storedCategory) -> {
                if (key.equalsIgnoreCase(categoryKey)) {
                    Utils.disable("The category " + categoryKey + " has already been registered!");
                    disable.set(true);
                }
            });
            if (disable.get()) {
                return false;
            }

            SlimeCustomizer.allCategories.put(categoryKey, tempCategory);
            Utils.notify("Category " + categoryKey + " has been registered!");

        }

        return true;
    }

}
