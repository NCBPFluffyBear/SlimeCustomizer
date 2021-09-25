package io.ncbpfluffybear.slimecustomizer;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.ncbpfluffybear.slimecustomizer.objects.SCMenu;
import io.ncbpfluffybear.slimecustomizer.objects.SCNotPlaceable;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;

/**
 * {@link Events} holds all the events for
 * the addon.
 *
 * @author NCBPFluffyBear
 */
public class Events implements Listener {

    public Events() {}

    private static final int MACHINE_GUIDE_DISPLAY_SLOT = 16;
    private static final int MACHINE_RECIPE_DISPLAY_SLOT = 4;
    private static final int MENU_SIZE = 54;

    private static final int[] INPUT_BORDER = {18, 19, 20, 21, 27, 30, 36, 37, 38, 39};
    private static final int[] OUTPUT_BORDER = {23, 24, 25, 26, 32, 35, 41, 42, 43, 44};
    private static final int[] INPUT_SLOTS = {28, 29};
    private static final int[] OUTPUT_SLOTS = {33, 34};

    private static final NamespacedKey SF_KEY = new NamespacedKey(Slimefun.getPlugin(Slimefun.class),
        "slimefun_item");

    @EventHandler
    public void onDualRecipeClick(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        Inventory inventory = e.getClickedInventory();

        if (inventory == null) {
            return;
        }

        ItemStack backButton = inventory.getItem(0);

        if (clickedItem == null || backButton == null) {
            return;
        }

        PersistentDataContainer pdc = backButton.getItemMeta().getPersistentDataContainer();

        if (!pdc.has(SF_KEY, PersistentDataType.STRING) || !pdc.get(SF_KEY, PersistentDataType.STRING).equals(
            "_UI_BACK")
            || !Utils.isKeyed(clickedItem)) {
            return;
        }

        // At this point, it has been confirmed that the player clicked a dual input or output item and is in a sf guide
        Player p = (Player) e.getWhoClicked();
        SlimefunItem machine = SlimefunItem.getByItem(e.getClickedInventory().getItem(MACHINE_GUIDE_DISPLAY_SLOT));
        SCMenu menu = new SCMenu(Slimefun.getLocalization().getMessage(p, "guide" +
            ".title.main"));
        SurvivalSlimefunGuide guide = new SurvivalSlimefunGuide(false);
        if (!(machine instanceof AContainer)) {
            return;
        }

        List<MachineRecipe> recipes = ((AContainer) machine).getMachineRecipes();
        int index = Utils.getItemKey(clickedItem);

        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1));
        menu.setSize(MENU_SIZE);
        menu.addBackButton(guide, p, PlayerProfile.find(p).get());
        menu.replaceExistingItem(MACHINE_RECIPE_DISPLAY_SLOT, machine.getItem());
        for (int i : INPUT_BORDER) {
            menu.replaceExistingItem(i, ChestMenuUtils.getInputSlotTexture());
        }
        for (int i : OUTPUT_BORDER) {
            menu.replaceExistingItem(i, ChestMenuUtils.getOutputSlotTexture());
        }
        for (ItemStack item : recipes.get(index).getInput()) {
            menu.pushItem(item, INPUT_SLOTS);
        }
        for (ItemStack item : recipes.get(index).getOutput()) {
            menu.pushItem(item, OUTPUT_SLOTS);
        }

        menu.setBackgroundNonClickable(true);
        menu.setPlayerInventoryClickable(false);

        menu.open(p);
    }

    @EventHandler
    private void onSCNonPlaceablePlace(PlayerRightClickEvent e) {
        Optional<SlimefunItem> optSFItem = e.getSlimefunItem();

        if (!optSFItem.isPresent()) {
            return;
        }

        if (optSFItem.get() instanceof SCNotPlaceable) {
            e.cancel();
        }
    }

}
