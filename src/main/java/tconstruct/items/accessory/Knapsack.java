package tconstruct.items.accessory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import tconstruct.items.CraftingItem;
import tconstruct.library.accessory.IAccessory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Knapsack extends CraftingItem implements IAccessory
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
            list.add(StatCollector.translateToLocal("knapsack.tooltip"));
            break;
        }
    }

    @Override
    public boolean canEquipAccessory (ItemStack item, int slot)
    {
        return slot == 2;
    }

}
