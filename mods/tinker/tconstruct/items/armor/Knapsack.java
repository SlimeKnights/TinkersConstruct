package mods.tinker.tconstruct.items.armor;

import java.util.List;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.items.CraftingItem;
import mods.tinker.tconstruct.util.player.ArmorExtended;
import mods.tinker.tconstruct.util.player.TPlayerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Knapsack extends CraftingItem
{

    public Knapsack(int id)
    {
        super(id, new String[] { "knapsack" }, new String[] { "knapsack" }, "armor/");
        this.setMaxStackSize(10);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            list.add("A Knapsack to hold your things.");
            break;
        }
    }

}
