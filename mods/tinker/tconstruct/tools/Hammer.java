package mods.tinker.tconstruct.tools;

import java.util.ArrayList;

import mods.tinker.tconstruct.AbilityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Hammer extends HarvestTool
{
	public Hammer(int itemID, String tex)
	{
		super(itemID, 1, tex);
		this.setUnlocalizedName("InfiTool.Hammer");
		setupCoords();
	}
	
	@Override
	public int getHeadType ()
	{
		return 1;
	}
	
	@Override
	protected String getHarvestType()
	{
		return "pickaxe";
	}

	@Override
	protected Material[] getEffectiveMaterials()
	{
		return materials;
	}

	static Material[] materials = new Material[] { Material.rock, Material.iron, Material.ice, Material.glass, Material.piston, Material.anvil };
	
	/* Hammer specfic methods */
	
	ArrayList<int[]> coords = new ArrayList<int[]>();
	
	void setupCoords()
	{
		coords.add(new int[] {1, 0, 0 });
		coords.add(new int[] {-1, 0, 0});
		coords.add(new int[] {0, 1, 0 });
		coords.add(new int[] {0, -1, 0});
		coords.add(new int[] {0, 0, 1 });
		coords.add(new int[] {0, 0, -1});
	}
	
	//@Override
	/*public boolean onBlockDestroyed (ItemStack itemstack, World world, int bID, int x, int y, int z, EntityLiving living)
	{
		if (!(itemstack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken")) && living instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)living;
			for (int[] coord : coords)
			{
				int xPos = x+coord[0], yPos = y+coord[1], zPos = z+coord[2];
				Block block = Block.blocksList[world.getBlockId(xPos, yPos, zPos)];
				{
					if (block != null)
					{
						if (block.blockID == Block.stone.blockID)
							world.setBlock(xPos, yPos, zPos, Block.cobblestone.blockID);
						else if (block.blockMaterial == Material.rock)
						{
							int meta = world.getBlockMetadata(xPos, yPos, zPos);
							world.setBlockToAir(x, y, z);
							Block.blocksList[bID].harvestBlock(world, (EntityPlayer)player, xPos, yPos, zPos, meta);
							if (!player.capabilities.isCreativeMode)
								onBlockDestroyed(itemstack, world, bID, xPos, yPos, zPos, player);
						}
					}
				}
			}
		}
		return AbilityHelper.onBlockChanged(itemstack, world, bID, x, y, z, living, random);
	}*/

	@Override
	protected Item getHeadItem ()
	{
		return null;
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
			return "_hammer_head";
		case 1:
			return "_hammer_head_broken";
		case 2:
			return "_hammer_handle";
		case 3:
			return "_hammer_accessory";
		default:
			return "";
		}
	}

	@Override
	public String getEffectSuffix ()
	{
		return "_hammer_effect";
	}

	@Override
	public String getDefaultFolder ()
	{
		return "hammer";
	}
}
