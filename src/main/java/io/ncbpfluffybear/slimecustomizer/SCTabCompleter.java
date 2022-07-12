package io.ncbpfluffybear.slimecustomizer;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.ncbpfluffybear.slimecustomizer.objects.CustomGenerator;
import io.ncbpfluffybear.slimecustomizer.objects.CustomMachine;
import io.ncbpfluffybear.slimecustomizer.objects.CustomSCItem;
import io.ncbpfluffybear.slimecustomizer.objects.WindowsExplorerStringComparator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@link SCTabCompleter} serves as a {@link TabCompleter}
 * for SlimeCustomizer related commands.
 *
 * @author NCBPFluffyBear
 */
public class SCTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args){
        List<String> options = new LinkedList<>();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {

                if (player.hasPermission("slimecustomizer.admin")) {
                    options.add("saveitem");
                    options.add("give");
                    options.add("getsaveditem");
                }
            } else if (args[0].equals("give") && player.hasPermission("slimecustomizer.admin")) {
                switch (args.length) {
                    case 2:
                        options.addAll(getOnlinePlayers());
                        break;
                    case 3:
                        options.addAll(getSCItems());
                        break;
                    case 4:
                        Collections.addAll(options, "1", "2", "4", "8", "16", "32", "64");
                        break;
                    default:
                        break;
                }
            } else if (args[0].equals("getsaveditem") && player.hasPermission("slimecustomizer.admin")) {
                if (args.length == 2) {
                    options.add("gui");

                    String[] fileNames = SlimeCustomizer.itemsFolder.list();
                    if (fileNames != null) {
                        for (int i = 0; i < fileNames.length; i++) {
                            fileNames[i] = fileNames[i].replace(".yml", "");
                        }
                        Arrays.sort(fileNames, new WindowsExplorerStringComparator());
                        options.addAll(Arrays.asList(fileNames));
                    }

                } else if (!args[1].equals("gui")) {
                    if (args.length == 3) {
                        options.addAll(getOnlinePlayers());
                    } else if (args.length == 4) {
                        Collections.addAll(options, "1", "2", "4", "8", "16", "32", "64");
                    }
                }
            }
        }
        return options;
    }

    private List<String> getSCItems() {
        List<SlimefunItem> items = Slimefun.getRegistry().getEnabledSlimefunItems();
        List<String> list = new ArrayList<>(items.size());

        for (SlimefunItem item : items) {
            if (item instanceof CustomSCItem
                || item instanceof CustomGenerator
                || item instanceof CustomMachine) {
                list.add(item.getId());
            }
        }

        return list;
    }

    private List<String> getOnlinePlayers() {
        List<String> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(p.getName());
        }

        return players;
    }
}