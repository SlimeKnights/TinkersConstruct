package tconstruct.armor.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.accessory.IAccessory;

public class Knapsack extends CraftingItem implements IAccessory
{

    public Knapsack()
    {
        super(new String[] { "knapsack" }, new String[] { "knapsack" }, "armor/", "tinker", TConstructRegistry.materialTab);
        this.setMaxStackSize(10);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            list.add(StatCollector.translateToLocal("knapsack.tooltip"));
            break;
        }
    }

    @Override
    public boolean canEquipAccessory (ItemStack item, int slot)
    {
        return slot == 2;
    }

    // TODO feel fix this so that stuff ticks in backpacks
    /*
     * @Override public void onArmorTickUpdate (World world, EntityPlayer
     * player, ItemStack itemStack) { TPlayerStats stats =
     * TConstruct.playerTracker.getPlayerStats(player.getDisplayName());
     * KnapsackInventory inv = stats.knapsack;
     * 
     * if (stats != null && inv != null) { for (int i = 0; i <
     * inv.getSizeInventory(); i++) { if (inv.getStackInSlot(i) != null) {
     * inv.getStackInSlot(i).getItem().onUpdate(inv.getStackInSlot(i),
     * player.worldObj, player, i, false); } } } }
     */

}
