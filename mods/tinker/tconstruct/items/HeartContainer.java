package mods.tinker.tconstruct.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class HeartContainer extends CraftingItem
{

    public HeartContainer(int id)
    {
        super(id, new String[] {"empty", "heart"}, new String[] {"canister_empty", "canister_heart"}, "");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add("Test Item");
    }

}
