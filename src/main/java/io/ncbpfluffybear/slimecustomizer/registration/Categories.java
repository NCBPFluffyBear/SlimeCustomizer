package io.ncbpfluffybear.slimecustomizer.registration;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.slimecustomizer.SlimeCustomizer;
import io.ncbpfluffybear.slimecustomizer.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link Categories} registers the categories
 * in the categories config file.
 *
 * @author NCBPFluffyBear
 */
public final class Categories {

    private Categories() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean register(@Nonnull Config categories) {
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
                item = SlimefunUtils.getCustomHead(materialString.replace("SKULL", ""));
            }

            ItemGroup tempCategory = new ItemGroup(new NamespacedKey(SlimeCustomizer.getInstance(), categoryKey),
                new CustomItemStack(item, name));

            AtomicBoolean disable = new AtomicBoolean(false);
            SlimeCustomizer.getAllCategories().forEach((key, storedCategory) -> {
                if (key.equalsIgnoreCase(categoryKey)) {
                    Utils.disable("The category " + categoryKey + " has already been registered!");
                    disable.set(true);
                }
            });
            if (disable.get()) {
                return false;
            }

            SlimeCustomizer.getAllCategories().put(categoryKey, tempCategory);
            Utils.notify("Category " + categoryKey + " has been registered!");

        }

        return true;
    }

}
