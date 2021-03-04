package io.ncbpfluffybear.slimecustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ClickAction;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * The {@link SCMenu} is a {@link ChestMenu} for
 * showing dual input/outputs.
 *
 * @author NCBPFluffyBear
 */
public class SCMenu extends ChestMenu {

    private static final int BACK_BUTTON_SLOT = 0;

    public SCMenu(Plugin plugin, String title) {
        super(plugin, title);
    }

    public void addBackButton(SurvivalSlimefunGuide guide, Player p, PlayerProfile profile) {
        GuideHistory history = profile.getGuideHistory();

        if (history.size() > 1) {
            this.replaceExistingItem(BACK_BUTTON_SLOT, new CustomItem(ChestMenuUtils.getBackButton(p, "", "&fLeft Click: &7Go back to previous Page", "&fShift + left Click: &7Go back to Main Menu")));

            this.addMenuClickHandler(BACK_BUTTON_SLOT, (pl, s, is, ic, action) -> {
                if (action == ClickAction.SHIFT_LEFT_CLICK) {
                    guide.openMainMenu(profile, 1);
                } else {
                    history.openLastEntry(guide);
                }
                return false;
            });

        } else {
            this.replaceExistingItem(BACK_BUTTON_SLOT, new CustomItem(ChestMenuUtils.getBackButton(p, "", ChatColor.GRAY + SlimefunPlugin.getLocalization().getMessage(p, "guide.back.guide"))));
            this.addMenuClickHandler(BACK_BUTTON_SLOT, (pl, s, is, ic, action) -> {
                guide.openMainMenu(profile, 1);
                return false;
            });
        }
    }

    public void setBackgroundNonClickable(boolean addBackground) {
        for (int i = 0; i < getSize(); i++) {
            if (!hasClickHandler(i)) {
                addMenuClickHandler(i, (pl, s, is, ic, action) -> false);
            }
            if (addBackground && getItemInSlot(i) == null) {
                replaceExistingItem(i, ChestMenuUtils.getBackground());
            }
        }
    }

    public void wipe(int start, int finish, boolean blockClicks) {
        for (int i = start; i <= finish; i++) {
            replaceExistingItem(i, null);

            if (blockClicks) {
                addMenuClickHandler(i, (pl, s, is, ic, action) -> false);
            }
        }
    }

    public boolean hasClickHandler(int slot) {
        return (this.getClickHandlers().containsKey(slot));
    }
}
