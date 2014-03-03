package tconstruct.inventory;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.tools.ToolCore;

public class SlotTool extends Slot
{
    /** The player that is using the GUI where this slot resides. */
    public EntityPlayer player;
    Random random = new Random();

    public SlotTool(EntityPlayer entityplayer, IInventory builder, int par3, int par4, int par5)
    {
        super(builder, par3, par4, par5);
        this.player = entityplayer;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid (ItemStack stack)
    {
        return false;
        //return stack.getItem() instanceof ToolCore;
    }

    public void onPickupFromSlot (EntityPlayer par1EntityPlayer, ItemStack stack)
    {
        this.onCrafting(stack);
        //stack.setUnlocalizedName("\u00A7f" + toolName);
        super.onPickupFromSlot(par1EntityPlayer, stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting (ItemStack stack, int par2)
    {
        //this.field_75228_b += par2;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting (ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();
        if (!tags.getCompoundTag("InfiTool").hasKey("Built"))
        {
            tags.getCompoundTag("InfiTool").setBoolean("Built", true);
            Boolean full = (inventory.getStackInSlot(2) != null || inventory.getStackInSlot(3) != null);
            for (int i = 2; i <= 3; i++)
                inventory.decrStackSize(i, 1);
            int amount = inventory.getStackInSlot(1).getItem() instanceof ToolCore ? stack.stackSize : 1;
            inventory.decrStackSize(1, amount);
            if (!player.worldObj.isRemote && full)
                player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "tinker:little_saw", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            //player.worldObj.playAuxSFX(1021, (int)player.posX, (int)player.posY, (int)player.posZ, 0);
            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(this.inventory, player, stack));
        }
    }
}
