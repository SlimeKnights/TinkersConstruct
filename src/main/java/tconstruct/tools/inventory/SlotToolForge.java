package tconstruct.tools.inventory;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.modifier.IModifyable;

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
        if (stack.getItem() instanceof IModifyable)
        {
            //NBTTagCompound tags = stack.getTagCompound().getCompoundTag(((IModifyable) stack.getItem()).getBaseTagName());
            Boolean full = (inventory.getStackInSlot(2) != null || inventory.getStackInSlot(3) != null || inventory.getStackInSlot(4) != null);
            for (int i = 2; i <= 4; i++)
                inventory.decrStackSize(i, 1);
            //ItemStack compare = inventory.getStackInSlot(1);
            //int amount = compare.getItem() instanceof IModifyable ? compare.stackSize : 1;
            //inventory.decrStackSize(1, amount);
            if (!player.worldObj.isRemote && full)
            	player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "random.anvil_use", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(this.inventory, player, stack));
        }
        else
        //Simply naming items
        {
            int amount = inventory.getStackInSlot(1).stackSize;
            inventory.decrStackSize(1, amount);

            for(int i = 0; i < inventory.getSizeInventory(); i++) {
                if(inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getItem() == Items.name_tag) {
                    inventory.decrStackSize(i, 1);
                    break;
                }
            }
        }
    }
}
