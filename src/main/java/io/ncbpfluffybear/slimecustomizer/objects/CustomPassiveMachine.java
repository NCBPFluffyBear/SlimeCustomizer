package io.ncbpfluffybear.slimecustomizer.objects;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class CustomPassiveMachine extends SlimefunItem implements EnergyNetComponent {

    private final String name;
    private final int capacity;
    private final List<ItemStack> products;

    @ParametersAreNonnullByDefault
    public CustomPassiveMachine(ItemGroup category,
                                SlimefunItemStack item,
                                RecipeType recipeType,
                                ItemStack[] recipe,
                                String name,
                                int capacity,
                                List<ItemStack> products
    ) {
        super(category, item, recipeType, recipe);

        this.name = name;
        this.capacity = capacity;
        this.products = products;

        new BlockMenuPreset(getId(), name) {

            @Override
            public void init() {
                // Unrequired
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return false;
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {
            @Override
            public void tick(Block b, SlimefunItem item, Config config) {
                CustomPassiveMachine.this.tick(b, item);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block b, SlimefunItem item) {

    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }



    public String getName() {
        return name;
    }

    public List<ItemStack> getProducts() {
        return products;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
