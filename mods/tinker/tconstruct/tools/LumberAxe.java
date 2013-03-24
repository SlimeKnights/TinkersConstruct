package mods.tinker.tconstruct.tools;

import java.util.List;

import mods.tinker.tconstruct.AbilityHelper;
import mods.tinker.tconstruct.TContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class LumberAxe extends HarvestTool
{
	public LumberAxe(int itemID)
	{
		super(itemID, 3);
		this.setUnlocalizedName("InfiTool.LumberAxe");
	}

	@Override
	public int getHeadType ()
	{
		return 1;
	}

	@Override
	protected Material[] getEffectiveMaterials ()
	{
		return materials;
	}

	@Override
	protected String getHarvestType ()
	{
		return "axe";
	}

	public float getDurabilityModifier ()
	{
		return 2.5f;
	}

	public String getToolName ()
	{
		return "Lumber Axe";
	}
	
	/* Creative mode tools */
	public void getSubItems (int id, CreativeTabs tab, List list)
	{
		
	}

	@Override
	public boolean onBlockDestroyed (ItemStack itemstack, World world, int bID, int x, int y, int z, EntityLiving player)
	{
		Block block = Block.blocksList[bID];
		if (block != null && block.blockMaterial == Material.leaves)
			return false;

		return AbilityHelper.onBlockChanged(itemstack, world, bID, x, y, z, player, random);
	}

	static Material[] materials = { Material.wood, Material.circuits, Material.cactus, Material.pumpkin };

	/* Lumber axes specific */

	@Override
	public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		World world = player.worldObj;
		int woodID = world.getBlockId(x, y, z);
		Block wood = Block.blocksList[woodID];
		if (wood.isWood(world, x, y, z))
		{
			int height = y;
			boolean foundTop = false;
			do
			{
				height++;
				int blockID = world.getBlockId(x, height, z);
				if (blockID != woodID)
				{
					height--;
					foundTop = true;
				}
			} while (!foundTop);

			int numLeaves = 0;
			for (int xPos = x - 1; xPos <= x + 1; xPos++)
			{
				for (int yPos = height - 1; yPos <= height + 1; yPos++)
				{
					for (int zPos = z - 1; zPos <= z + 1; zPos++)
					{
						Block leaves = Block.blocksList[world.getBlockId(xPos, yPos, zPos)];
						if (leaves != null && leaves.isLeaves(world, xPos, yPos, zPos))
							numLeaves++;
					}
				}
			}

			NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
			int meta = world.getBlockMetadata(x, y, z);
			if (numLeaves > 3)
				breakTree(world, x, y, z, stack, tags, woodID, meta, player);
			else
				destroyWood(world, x, y, z, stack, tags, player);
			
			if (!world.isRemote)
				world.playAuxSFX(2001, x, y, z, woodID + (meta << 12));
		}
		return super.onBlockStartBreak(stack, x, y, z, player);
	}

	void breakTree (World world, int x, int y, int z, ItemStack stack, NBTTagCompound tags, int bID, int meta, EntityPlayer player)
	{
		for (int xPos = x-1; xPos <= x+1; xPos++)
		{
			for (int yPos = y-1; yPos <= y+1; yPos++)
			{
				for (int zPos = z-1; zPos <= z+1; zPos++)
				{
					if (!(tags.getBoolean("Broken")))
					if (world.getBlockId(xPos, yPos, zPos) == bID && world.getBlockMetadata(xPos, yPos, zPos) % 4 == meta % 4)
					{
						world.setBlockToAir(x, y, z);
						Block.blocksList[bID].harvestBlock(world, player, xPos, yPos, zPos, meta);
						if (!player.capabilities.isCreativeMode)
							onBlockDestroyed(stack, world, bID, xPos, yPos, zPos, player);
						breakTree(world, xPos, yPos, zPos, stack, tags, bID, meta, player);
					}
				}
			}
		}
	}
	
	void destroyWood(World world, int x, int y, int z, ItemStack stack, NBTTagCompound tags, EntityPlayer player)
	{
		for (int xPos = x-1; xPos <= x+1; xPos++)
		{
			for (int yPos = y-1; yPos <= y+1; yPos++)
			{
				for (int zPos = z-1; zPos <= z+1; zPos++)
				{
					if (!(tags.getBoolean("Broken")))
					{
						int blockID = world.getBlockId(xPos, yPos, zPos);
						Block block = Block.blocksList[blockID];
						if (block != null && block.blockMaterial == Material.wood)
						{
							int meta = world.getBlockMetadata(xPos, yPos, zPos);
							world.setBlockToAir(x, y, z);
							Block.blocksList[blockID].harvestBlock(world, player, xPos, yPos, zPos, meta);
							if (!player.capabilities.isCreativeMode)
								onBlockDestroyed(stack, world, blockID, xPos, yPos, zPos, player);
						}
					}
				}
			}
		}
	}

	@Override
	protected Item getHeadItem ()
	{
		return TContent.lumberHead;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return null;
	}
	
	@Override
	public String getIconSuffix (int partType)
	{
		switch (partType)
		{
		case 0:
			return "_lumberaxe_head";
		case 1:
			return "_lumberaxe_head_broken";
		case 2:
			return "_lumberaxe_handle";
		case 3:
			return "_lumberaxe_accessory";
		default:
			return "";
		}
	}

	@Override
	public String getEffectSuffix ()
	{
		return "_lumber_effect";
	}

	@Override
	public String getDefaultFolder ()
	{
		return "lumberaxe";
	}
}
