package tconstruct.tools.inventory;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.modifier.IModifyable;

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
        if (stack.getItem() instanceof IModifyable)
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(((IModifyable) stack.getItem()).getBaseTagName());
            Boolean full = (inventory.getStackInSlot(2) != null || inventory.getStackInSlot(3) != null);
            for (int i = 2; i <= 3; i++)
                inventory.decrStackSize(i, 1);
            ItemStack compare = inventory.getStackInSlot(1);
            int amount = compare.getItem() instanceof IModifyable ? compare.stackSize : 1;
            inventory.decrStackSize(1, amount);
            if (!player.worldObj.isRemote && full)
                player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "tinker:little_saw", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(this.inventory, player, stack));
        }
        else
        //Simply naming items
        {
            int amount = inventory.getStackInSlot(1).stackSize;
            inventory.decrStackSize(1, amount);
        }
    }
}
