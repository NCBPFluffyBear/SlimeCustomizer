package io.ncbpfluffybear.slimecustomizer.registration;

import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.ncbpfluffybear.slimecustomizer.Registry;
import io.ncbpfluffybear.slimecustomizer.SlimeCustomizer;
import io.ncbpfluffybear.slimecustomizer.Utils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.time.DateTimeException;
import java.time.Month;
import java.util.List;
import java.util.Locale;

/**
 * {@link Categories} registers the categories
 * in the categories config file.
 *
 * @author NCBPFluffyBear
 * @author ybw0014
 */
public class Categories {

    public static boolean register(Config categories) {
        if (categories.getKeys().isEmpty()) {
            Utils.disable("No categories were found! Please add and use a category from categories.yml");
            return false;
        }

        for (String categoryKey : categories.getKeys()) {
            String itemGroupKey = categoryKey.toLowerCase(Locale.ROOT);
            String type = categories.getString(categoryKey + ".type");
            String name = categories.getString(categoryKey + ".category-name");
            String materialString = categories.getString(categoryKey + ".category-item");
            Material material = Material.getMaterial(materialString);
            ItemStack item = null;

            // update type
            if (type == null) {
                type = "normal";
                categories.setValue(categoryKey + ".type", type);
                categories.save();
            }

            /* Item material type */
            if ((material == null && !materialString.startsWith("SKULL"))) {
                Utils.disable("The category-item for " + categoryKey + " is invalid!");
                return false;
            } else if (material != null) {
                item = new ItemStack(material);
            } else if (materialString.startsWith("SKULL")) {
                item = SlimefunUtils.getCustomHead(materialString.replace("SKULL", ""));
            }
            item = new CustomItemStack(item, name);

            if (Registry.allItemGroups.containsKey(itemGroupKey)) {
                Utils.disable("The category " + categoryKey + " has already been registered!");
                return false;
            }

            ItemGroup tempCategory;
            NamespacedKey key = new NamespacedKey(SlimeCustomizer.getInstance(), itemGroupKey);

            if (type.equalsIgnoreCase("nested")) {
                tempCategory = new NestedItemGroup(key, item);
            } else if (type.equalsIgnoreCase("sub")) {
                String parent = categories.getString(categoryKey + ".parent");
                if (parent == null) {
                    Utils.disable("The category " + categoryKey + " has invalid parent group!");
                    return false;
                }

                ItemGroup parentGroup = Registry.allItemGroups.get(parent);
                if (!(parentGroup instanceof NestedItemGroup)) {
                    Utils.disable("The category " + categoryKey + " has invalid parent group!");
                    return false;
                }

                tempCategory = new SubItemGroup(key, (NestedItemGroup) parentGroup, item);
            } else if (type.equalsIgnoreCase("seasonal")) {
                int monthNum = categories.getInt(categoryKey + ".month");
                int tier = categories.getInt(categoryKey + ".tier");
                Month month;

                try {
                    month = Month.of(monthNum);
                } catch (DateTimeException ex) {
                    Utils.disable("The category " + categoryKey + " has invalid month!");
                    return false;
                }

                tempCategory = new SeasonalItemGroup(key, month, tier, item);
            } else if (type.equalsIgnoreCase("locked")) {
                List<String> parents = categories.getStringList(categoryKey + ".parents");
                NamespacedKey[] parentKeys = new NamespacedKey[parents.size()];
                int i = 0;
                for (String parent : parents) {
                    parentKeys[i++] = NamespacedKey.fromString(parent, SlimeCustomizer.getInstance());
                }

                tempCategory = new LockedItemGroup(key, item, parentKeys);
            } else {
                type = "normal";
                tempCategory = new ItemGroup(key, item);
            }

            Registry.allItemGroups.put(itemGroupKey, tempCategory);
            Utils.notify("Category " + categoryKey + " (" + type + ") has been registered!");

        }

        return true;
    }

}
