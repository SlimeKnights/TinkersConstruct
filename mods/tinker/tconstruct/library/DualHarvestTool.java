package mods.tinker.tconstruct.library;

import mods.tinker.tconstruct.tools.HarvestTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/* Base class for harvest tools with each head having a different purpose */

public abstract class DualHarvestTool extends HarvestTool
{
	public DualHarvestTool(int itemID, int baseDamage)
	{
		super(itemID, baseDamage);
	}
	
	@Override
	public int getHeadType ()
	{
		return 3;
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		World world = player.worldObj;
		int bID = player.worldObj.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		Block block = Block.blocksList[bID];
		int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
		int shlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getSecondHarvestType());
		
		if (hlvl <= tags.getInteger("HarvestLevel") && shlvl <= tags.getInteger("HarvestLevel2"))
		{
			if (tags.getBoolean("Lava") && block.quantityDropped(meta, 0, random) != 0)
			{
				ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(block.idDropped(bID, random, 0), 1, block.damageDropped(meta)));
				if (result != null)
				{
					//System.out.println("Woo~");
					world.setBlockToAir(x, y, z);
					if (!player.capabilities.isCreativeMode)
						onBlockDestroyed(stack, world, bID, x, y, z, player);
					if (!world.isRemote)
					{
						EntityItem entityitem = new EntityItem(world, x+0.5, y+0.5, z+0.5, result.copy());
						
						entityitem.delayBeforeCanPickup = 10;
						world.spawnEntityInWorld(entityitem);
						world.playAuxSFX(2001, x, y, z, bID + (meta << 12));
					}
					return true;
				}
			}
			return false;
		}
		else
		{
			if (!player.capabilities.isCreativeMode)
				onBlockDestroyed(stack, world, bID, x, y, z, player);
			world.setBlockToAir(x, y, z);
			if (!world.isRemote)
				world.playAuxSFX(2001, x, y, z, bID + (meta << 12));
			return true;
		}
	}

	@Override
	public float getStrVsBlock(ItemStack stack, Block block, int meta)
	{

		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		if (tags.getBoolean("Broken"))
			return 0.1f;
		
		Material[] materials = getEffectiveMaterials();
		for (int i = 0; i < materials.length; i++)
		{
			if (materials[i] == block.blockMaterial )
			{				
				float speed = tags.getInteger("MiningSpeed");
				speed /= 100f;
				int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
				int durability = tags.getInteger("Damage");
				
				float shoddy = tags.getFloat("Shoddy");
				speed += shoddy*durability/100f;
				
				if (hlvl <= tags.getInteger("HarvestLevel"))
					return speed;
				return 0.1f;
			}
		}
		materials = getEffectiveSecondaryMaterials();
		for (int i = 0; i < materials.length; i++)
		{
			if (materials[i] == block.blockMaterial )
			{				
				float speed = tags.getInteger("MiningSpeed2");
				speed /= 100f;
				int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
				int durability = tags.getInteger("Damage");
				
				float shoddy = tags.getFloat("Shoddy");
				speed += shoddy*durability/100f;
				
				if (hlvl <= tags.getInteger("HarvestLevel2"))
					return speed;
				return 0.1f;
			}
		}
		return super.getStrVsBlock(stack, block, meta);
	}
	
	public boolean canHarvestBlock(Block block)
    {
		return true;
    }
	
	protected abstract Material[] getEffectiveSecondaryMaterials();
	protected abstract String getSecondHarvestType();
}
