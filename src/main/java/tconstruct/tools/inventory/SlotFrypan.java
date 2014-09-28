package tconstruct.tools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.achievements.TAchievements;

public class SlotFrypan extends Slot
{

    public EntityPlayer player;

    public SlotFrypan(IInventory par1IInventory, int par2, int par3, int par4, EntityPlayer player)
    {
        super(par1IInventory, par2, par3, par4);
        this.player = player;
    }

    @Override
    public void putStack (ItemStack par1ItemStack)
    {
        super.putStack(par1ItemStack);
        if (par1ItemStack != null && par1ItemStack.getItem() != null && par1ItemStack.getItem() instanceof ItemFood && par1ItemStack.hasTagCompound())
        {
            NBTTagCompound stackTagCompound = par1ItemStack.getTagCompound();
            if (stackTagCompound == null || !stackTagCompound.getBoolean("frypanKill"))
            {
                return;
            }

            TAchievements.triggerAchievement(player, "tconstruct.dualConvenience");
        }
    }

}
