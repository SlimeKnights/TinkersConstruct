package tinker.tconstruct.tools;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tinker.tconstruct.AbilityHelper;

public class Hammer extends HarvestTool
{
	public Hammer(int itemID, String tex)
	{
		super(itemID, 1, tex);
		this.setItemName("InfiTool.Hammer");
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
	
	@Override
	public boolean onBlockDestroyed (ItemStack itemstack, World world, int bID, int x, int y, int z, EntityLiving living)
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
							world.setBlockWithNotify(xPos, yPos, zPos, Block.cobblestone.blockID);
						else if (block.blockMaterial == Material.rock)
						{
							int meta = world.getBlockMetadata(xPos, yPos, zPos);
							world.setBlockWithNotify(xPos, yPos, zPos, 0);
							Block.blocksList[bID].harvestBlock(world, (EntityPlayer)player, xPos, yPos, zPos, meta);
							if (!player.capabilities.isCreativeMode)
								onBlockDestroyed(itemstack, world, bID, xPos, yPos, zPos, player);
						}
					}
				}
			}
		}
		return AbilityHelper.onBlockChanged(itemstack, world, bID, x, y, z, living, random);
	}
}
