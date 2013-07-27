package tconstruct.inventory;

import java.util.Random;

import tconstruct.library.tools.ToolCore;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SlotToolForge extends SlotTool
{
    /** The player that is using the GUI where this slot resides. */
    Random random = new Random();

    public SlotToolForge(EntityPlayer entityplayer, IInventory builder, int par3, int par4, int par5)
    {
        super(entityplayer, builder, par3, par4, par5);
    }

    protected void onCrafting (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.getCompoundTag("InfiTool").hasKey("Built"))
        {
            tags.getCompoundTag("InfiTool").setBoolean("Built", true);
            Boolean full = (inventory.getStackInSlot(2) != null || inventory.getStackInSlot(3) != null);
            for (int i = 2; i <= 4; i++)
                inventory.decrStackSize(i, 1);
            int amount = inventory.getStackInSlot(1).getItem() instanceof ToolCore ? stack.stackSize : 1;
            inventory.decrStackSize(1, amount);
            if (!player.worldObj.isRemote && full)
                player.worldObj.playAuxSFX(1021, (int) player.posX, (int) player.posY, (int) player.posZ, 0);
        }
    }
}
