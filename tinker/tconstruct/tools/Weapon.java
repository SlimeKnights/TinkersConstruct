package tinker.tconstruct.tools;

import tinker.tconstruct.ToolMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public abstract class Weapon extends ToolCore
{

	public Weapon(int itemID, int baseDamage, String texture)
	{
		super(itemID, baseDamage, texture);
	}
	
	protected float baseSpeed()
	{
		return 1.5f;
	}
	
	protected float effectiveSpeed()
	{
		return 15f;
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, Block block, int meta)
	{
		if (stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken"))
			return 0.1f;
		
		for (int i = 0; i < web.length; i++)
		{
			if (web[i] == block.blockMaterial )
			{
				return effectiveSpeed();
			}
		}
		return baseSpeed();
	}
	
	/**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.block;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 72000;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    /**
     * Returns if the item (tool) can harvest results from the block type.
     */
    public boolean canHarvestBlock(Block block)
    {
    	for (int i = 0; i < web.length; i++)
		{
    		if (block.blockMaterial == web[i])
    			return true;
		}
        return super.canHarvestBlock(block);
    }
    
    protected Material[] getEffectiveMaterials()
    {
    	return web;
    }
    
    @Override
	public int getHeadType ()
	{
		return 1;
	}
	
	private static Material[] web = new Material[] { Material.web };
}
