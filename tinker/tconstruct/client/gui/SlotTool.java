package tinker.tconstruct.client.gui;

import tinker.tconstruct.tools.ToolCore;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class SlotTool extends Slot
{
    /** The player that is using the GUI where this slot resides. */
    private EntityPlayer player;
    
    public SlotTool(EntityPlayer entityplayer, IInventory builder, int par3, int par4, int par5)
    {
        super(builder, par3, par4, par5);
        this.player = entityplayer;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack stack)
    {
        return stack.getItem() instanceof ToolCore;
    }

    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack stack)
    {
        this.onCrafting(stack);
        //stack.setItemName("\u00A7f" + toolName);
        super.onPickupFromSlot(par1EntityPlayer, stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(ItemStack stack, int par2)
    {
        //this.field_75228_b += par2;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack stack)
    {
    	NBTTagCompound tags = stack.getTagCompound();
		if (!tags.getCompoundTag("InfiTool").hasKey("Built"))
		{
			tags.getCompoundTag("InfiTool").setBoolean("Built", true);
			for (int i = 1; i <= 3; i++)
				inventory.decrStackSize(i, 1);
			if (!player.worldObj.isRemote)
				player.worldObj.playAuxSFX(1021, (int)player.posX, (int)player.posY, (int)player.posZ, 0);
		}
    }
}
