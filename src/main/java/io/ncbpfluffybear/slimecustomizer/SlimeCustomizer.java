package io.ncbpfluffybear.slimecustomizer;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;
import io.ncbpfluffybear.slimecustomizer.objects.SCMenu;
import io.ncbpfluffybear.slimecustomizer.objects.WindowsExplorerStringComparator;
import io.ncbpfluffybear.slimecustomizer.registration.Categories;
import io.ncbpfluffybear.slimecustomizer.registration.Generators;
import io.ncbpfluffybear.slimecustomizer.registration.Items;
import io.ncbpfluffybear.slimecustomizer.registration.Machines;
import lombok.SneakyThrows;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * This used to be a smol boi. Now it has grown.
 *
 * @author NCBPFluffyBear
 */
public class SlimeCustomizer extends JavaPlugin implements SlimefunAddon {

    public static SlimeCustomizer instance;
    public static File itemsFolder;

    public static HashMap<ItemStack[], Pair<RecipeType, String>> existingRecipes = new HashMap<>();
    public static HashMap<String, Category> allCategories = new HashMap<>();

    @Override
    public void onEnable() {

        instance = this;
        itemsFolder = new File(this.getDataFolder(), "saveditems");

        // Read something from your config.yml
        Config cfg = new Config(this);

        if (cfg.getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
            new GitHubBuildsUpdater(this, getFile(), "NCBPFluffyBear/SlimeCustomizer/master/").start();
        }

        final Metrics metrics = new Metrics(this, 9841);

        /* File generation */
        final File categoriesFile = new File(getInstance().getDataFolder(), "categories.yml");
        if (!categoriesFile.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/categories.yml"), categoriesFile.toPath());
            } catch (IOException e) {
                getInstance().getLogger().log(Level.SEVERE, "Failed to copy default categories.yml file", e);
            }
        }

        final File itemsFile = new File(getInstance().getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/items.yml"), itemsFile.toPath());
            } catch (IOException e) {
                getInstance().getLogger().log(Level.SEVERE, "Failed to copy default items.yml file", e);
            }
        }

        final File machinesFile = new File(getInstance().getDataFolder(), "machines.yml");
        if (!machinesFile.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/machines.yml"), machinesFile.toPath());
            } catch (IOException e) {
                getInstance().getLogger().log(Level.SEVERE, "Failed to copy default machines.yml file", e);
            }
        }

        final File generatorsFile = new File(getInstance().getDataFolder(), "generators.yml");
        if (!generatorsFile.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/generators.yml"), generatorsFile.toPath());
            } catch (IOException e) {
                getInstance().getLogger().log(Level.SEVERE, "Failed to copy default generators.yml file", e);
            }
        }

        if (!itemsFolder.exists()) {
            try {
                Files.createDirectory(itemsFolder.toPath());
            } catch (IOException e) {
                getInstance().getLogger().log(Level.SEVERE, "Failed to create saveditems folder", e);
            }
        }

        Config categories = new Config(this, "categories.yml");
        Config items = new Config(this, "items.yml");
        Config machines = new Config(this, "machines.yml");
        Config generators = new Config(this, "generators.yml");

        this.getCommand("slimecustomizer").setTabCompleter(new SCTabCompleter());

        Bukkit.getLogger().log(Level.INFO, "[SlimeCustomizer] " + ChatColor.BLUE + "Setting up custom stuff...");
        if (!Categories.register(categories)) {return;}
        if (!Items.register(items)) {return;}
        if (!Machines.register(machines)) {return;}
        if (!Generators.register(generators)) {return;}
        Bukkit.getPluginManager().registerEvents(new Events(), instance);
    }

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            Utils.send(sender, "&eAll commands can be found at &9" + Links.COMMANDS);
        } else if (args[0].equals("saveitem") && sender instanceof Player) {
            Player p = (Player) sender;
            if (!Utils.checkPermission(p, "slimecustomizer.admin")) {
                return true;
            }
            int id = 0;
            File itemFile = new File(getInstance().getDataFolder().getPath() + "/saveditems", id + ".yml");
            while (itemFile.exists()) {
                id++;
                itemFile = new File(getInstance().getDataFolder().getPath() + "/saveditems", id + ".yml");
            }

            if (!itemFile.createNewFile()) {
                getInstance().getLogger().log(Level.SEVERE, "Failed to create config for item " + id);
            }

            Config itemFileConfig = new Config(this, "saveditems/" + id + ".yml");
            itemFileConfig.setValue("item", p.getInventory().getItemInMainHand());
            itemFileConfig.save();
            Utils.send(p, "&eYour item has been saved to " + itemFile.getPath() + ". Please refer to " +
                "&9" + Links.USING_CUSTOM_ITEMS);

        } else if (args[0].equals("give") && args.length > 2 && sender instanceof Player) {
            Player p = (Player) sender;
            if (!Utils.checkPermission(p, "slimecustomizer.admin")) {
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                Utils.send(p, "&cThat player could not be found!");
                return true;
            }

            SlimefunItem sfItem = SlimefunItem.getByID(args[2]);
            if (sfItem == null) {
                Utils.send(p, "&cThat Slimefun item could not be found!");
                return true;
            }

            int amount = 1;
            if (PatternUtils.NUMERIC.matcher(args[3]).matches()) {
                amount = Integer.parseInt(args[3]);
            }

            giveItems(target, sfItem, amount);

        } else if (args[0].equals("getsaveditem") && sender instanceof Player) {
            Player p = (Player) sender;

            if (!Utils.checkPermission(p, "slimecustomizer.admin")) {
                return true;
            }

            if (args[1].equals("gui")) {
                List<Pair<String, ItemStack>> items = new ArrayList<>();
                items.add(new Pair<>(null, null));

                String[] fileNames = itemsFolder.list();
                if (fileNames != null) {
                    for (int i = 0; i < fileNames.length; i++) {
                        fileNames[i] = fileNames[i].replace(".yml", "");
                    }

                    Arrays.sort(fileNames, new WindowsExplorerStringComparator());

                    for (String id : fileNames) {
                        items.add(new Pair<>(id, Utils.retrieveSavedItem(id, 1, false)));
                    }

                    int page = 1;
                    SCMenu menu = new SCMenu(this, "&a&lSaved Items");
                    menu.setSize(54);
                    populateMenu(menu, items, page, p);
                    menu.setPlayerInventoryClickable(false);
                    menu.setBackgroundNonClickable(false);
                    menu.open(p);

                }

            } else {
                if (args.length < 4) {
                    Utils.send(p, "&c/sc getsaveditem gui | <item_id> <player_name> <amount>");
                    return true;
                }

                String itemName = args[1];

                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    Utils.send(p, "&cThat player could not be found!");
                    return true;
                }

                int amount = 1;

                if (PatternUtils.NUMERIC.matcher(args[3]).matches()) {
                    amount = Integer.parseInt(args[3]);
                }
                ItemStack item = Utils.retrieveSavedItem(itemName, amount, false);
                if (item != null) {
                    HashMap<Integer, ItemStack> leftovers = target.getInventory().addItem(item);
                    for (ItemStack leftover : leftovers.values()) {
                        target.getWorld().dropItem(target.getLocation(), leftover);
                    }
                } else {
                    Utils.send(p, "&cThat saveditem could not be found!");
                }
            }
        }
        return true;
    }

    /**
     * Populates the saveditem gui. 45 items per page.
     * @param menu the SCMenu to populate
     * @param items the List of items
     * @param page the page number
     * @param p the player that will be viewing this menu
     */
    private void populateMenu(SCMenu menu, List<Pair<String, ItemStack>> items, int page, Player p) {
        for (int i = 45; i < 54; i++) {
            menu.replaceExistingItem(i, ChestMenuUtils.getBackground());
        }

        menu.wipe(0, 44, true);

        for (int i = 0; i < 45; i++) {
            int itemIndex = i + 1 + (page - 1) * 45;
            ItemStack item = getItemOrNull(items, itemIndex);
            if (item != null) {
                ItemMeta im = item.getItemMeta();
                List<String> lore = im.getLore();

                if (lore == null) {
                    lore = new ArrayList<>(Arrays.asList("", Utils.color("&bID: " + items.get(itemIndex).getFirstValue()),
                        Utils.color("&a> Click to get this item")));
                } else {
                    lore.addAll(new ArrayList<>(Arrays.asList("", Utils.color("&bID: " + items.get(itemIndex).getFirstValue()),
                        Utils.color("&a> Click to get this item"))));
                }

                im.setLore(lore);
                item.setItemMeta(im);
                menu.replaceExistingItem(i, item);
                menu.addMenuClickHandler(i, (pl, s, is, ic, action) -> {
                    HashMap<Integer, ItemStack> leftovers = p.getInventory().addItem(getItemOrNull(items, itemIndex));
                    for (ItemStack leftover : leftovers.values()) {
                        p.getWorld().dropItem(p.getLocation(), leftover);
                    }
                    return false;
                });
            }
        }

        if (page != 1) {
            menu.replaceExistingItem(46, new CustomItem(Material.LIME_STAINED_GLASS_PANE, "&aPrevious Page"));
            menu.addMenuClickHandler(46, (pl, s, is, ic, action) -> {
                populateMenu(menu, items, page - 1, p);
                return false;
            });
        }

        if (getItemOrNull(items, 45 * page) != null) {
            menu.replaceExistingItem(52, new CustomItem(Material.LIME_STAINED_GLASS_PANE, "&aNext Page"));
            menu.addMenuClickHandler(52, (pl, s, is, ic, action) -> {
                populateMenu(menu, items, page + 1, p);
                return false;
            });
        }

    }

    private ItemStack getItemOrNull(List<Pair<String, ItemStack>> items, int index) {
        ItemStack item;
        try {
            item = items.get(index).getSecondValue().clone();
        } catch (IndexOutOfBoundsException e) {
            item = null;
        }
        return item;
    }

    private void giveItems(Player p, SlimefunItem sfItem, int amount) {
        p.getInventory().addItem(new CustomItem(sfItem.getRecipeOutput(), amount));
        Utils.send(p, "&bYou have given " + p.getName() + " &a" + amount + " &7\"&b" + sfItem.getItemName() + "&7\"");
    }

    @Override
    public void onDisable() {
        // Logic for disabling the plugin...
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/NCBPFluffyBear/SlimeCustomizer/issues";
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    public static SlimeCustomizer getInstance() {
        return instance;
    }

}
