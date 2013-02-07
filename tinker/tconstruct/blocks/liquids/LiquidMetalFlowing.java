package tinker.tconstruct.blocks.liquids;

import java.util.Random;

import tinker.tconstruct.TContent;
import tinker.tconstruct.client.liquidrender.RenderLiquidMetal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquid;

public abstract class LiquidMetalFlowing extends BlockFluid implements ILiquid
{

	int numAdjacentSources = 0;
	boolean isOptimalFlowDirection[] = new boolean[4];
	int flowCost[] = new int[4];

	public LiquidMetalFlowing(int id)
	{
		super(id, Material.lava);
		setHardness(100F);
	}

	@Override
	public int getRenderType ()
	{
		return RenderLiquidMetal.liquidModel;
	}

	@Override
	public String getTextureFile ()
	{
		return TContent.liquidTexture;
	}

	private void updateFlow (World world, int x, int y, int z)
	{
		int l = world.getBlockMetadata(x, y, z);
		world.setBlockAndMetadata(x, y, z, stillLiquidId(), l);
		world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
		world.markBlockForUpdate(x, y, z);
	}

	@Override
	public void updateTick (World world, int i, int j, int k, Random random)
	{
		int l = getFlowDecay(world, i, j, k);
		byte byte0 = 1;
		boolean flag = true;
		if (l > 0)
		{
			int i1 = -100;
			numAdjacentSources = 0;
			i1 = getSmallestFlowDecay(world, i - 1, j, k, i1);
			i1 = getSmallestFlowDecay(world, i + 1, j, k, i1);
			i1 = getSmallestFlowDecay(world, i, j, k - 1, i1);
			i1 = getSmallestFlowDecay(world, i, j, k + 1, i1);
			int j1 = i1 + byte0;
			if (j1 >= 8 || i1 < 0)
			{
				j1 = -1;
			}
			if (getFlowDecay(world, i, j + 1, k) >= 0)
			{
				int l1 = getFlowDecay(world, i, j + 1, k);
				if (l1 >= 8)
				{
					j1 = l1;
				}
				else
				{
					j1 = l1 + 8;
				}
			}
			if (j1 != l)
			{
				l = j1;
				if (l < 0)
				{
					world.setBlockWithNotify(i, j, k, 0);
				}
				else
				{
					world.setBlockMetadataWithNotify(i, j, k, l);
					world.scheduleBlockUpdate(i, j, k, blockID, tickRate());
					world.notifyBlocksOfNeighborChange(i, j, k, blockID);
				}
			}
			else if (flag)
			{
				updateFlow(world, i, j, k);
			}
		}
		else
		{
			updateFlow(world, i, j, k);
		}
		if (liquidCanDisplaceBlock(world, i, j - 1, k))
		{
			if (l >= 8)
			{
				world.setBlockAndMetadataWithNotify(i, j - 1, k, blockID, l);
			}
			else
			{
				world.setBlockAndMetadataWithNotify(i, j - 1, k, blockID, l + 8);
			}
		}
		else if (l >= 0 && (l == 0 || blockBlocksFlow(world, i, j - 1, k)))
		{
			boolean aflag[] = getOptimalFlowDirections(world, i, j, k);
			int k1 = l + byte0;
			if (l >= 8)
			{
				k1 = 1;
			}
			if (k1 >= 8)
				return;
			if (aflag[0])
			{
				flowIntoBlock(world, i - 1, j, k, k1);
			}
			if (aflag[1])
			{
				flowIntoBlock(world, i + 1, j, k, k1);
			}
			if (aflag[2])
			{
				flowIntoBlock(world, i, j, k - 1, k1);
			}
			if (aflag[3])
			{
				flowIntoBlock(world, i, j, k + 1, k1);
			}
		}
	}

	private void flowIntoBlock (World world, int i, int j, int k, int l)
	{
		if (liquidCanDisplaceBlock(world, i, j, k))
		{
			int i1 = world.getBlockId(i, j, k);
			if (i1 > 0)
			{
				Block.blocksList[i1].dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
			}
			world.setBlockAndMetadataWithNotify(i, j, k, blockID, l);
		}
	}

	private int calculateFlowCost (World world, int i, int j, int k, int l, int i1)
	{
		int j1 = 1000;
		for (int k1 = 0; k1 < 4; k1++)
		{
			if (k1 == 0 && i1 == 1 || k1 == 1 && i1 == 0 || k1 == 2 && i1 == 3 || k1 == 3 && i1 == 2)
			{
				continue;
			}
			int l1 = i;
			int i2 = j;
			int j2 = k;
			if (k1 == 0)
			{
				l1--;
			}
			if (k1 == 1)
			{
				l1++;
			}
			if (k1 == 2)
			{
				j2--;
			}
			if (k1 == 3)
			{
				j2++;
			}
			if (blockBlocksFlow(world, l1, i2, j2) || world.getBlockMaterial(l1, i2, j2) == blockMaterial && world.getBlockMetadata(l1, i2, j2) == 0)
			{
				continue;
			}
			if (!blockBlocksFlow(world, l1, i2 - 1, j2))
				return l;
			if (l >= 4)
			{
				continue;
			}
			int k2 = calculateFlowCost(world, l1, i2, j2, l + 1, k1);
			if (k2 < j1)
			{
				j1 = k2;
			}
		}

		return j1;
	}

	private boolean[] getOptimalFlowDirections (World world, int x, int y, int z)
	{
		for (int l = 0; l < 4; l++)
		{
			flowCost[l] = 1000;
			int j1 = x;
			int i2 = y;
			int j2 = z;
			if (l == 0)
			{
				j1--;
			}
			if (l == 1)
			{
				j1++;
			}
			if (l == 2)
			{
				j2--;
			}
			if (l == 3)
			{
				j2++;
			}
			if (blockBlocksFlow(world, j1, i2, j2) || world.getBlockMaterial(j1, i2, j2) == blockMaterial && world.getBlockMetadata(j1, i2, j2) == 0)
			{
				continue;
			}
			if (!blockBlocksFlow(world, j1, i2 - 1, j2))
			{
				flowCost[l] = 0;
			}
			else
			{
				flowCost[l] = calculateFlowCost(world, j1, i2, j2, 1, l);
			}
		}

		int i1 = flowCost[0];
		for (int k1 = 1; k1 < 4; k1++)
		{
			if (flowCost[k1] < i1)
			{
				i1 = flowCost[k1];
			}
		}

		for (int l1 = 0; l1 < 4; l1++)
		{
			isOptimalFlowDirection[l1] = flowCost[l1] == i1;
		}

		return isOptimalFlowDirection;
	}

	private boolean blockBlocksFlow (World world, int x, int y, int z)
	{
		int l = world.getBlockId(x, y, z);
		if (l == Block.doorWood.blockID || l == Block.doorSteel.blockID || l == Block.signPost.blockID || l == Block.ladder.blockID || l == Block.reed.blockID)
			return true;
		if (l == 0)
			return false;
		Material material = Block.blocksList[l].blockMaterial;
		return material.isSolid();
	}

	protected int getSmallestFlowDecay (World world, int i, int j, int k, int l)
	{
		int i1 = getFlowDecay(world, i, j, k);
		if (i1 < 0)
			return l;
		if (i1 >= 8)
		{
			i1 = 0;
		}
		return l >= 0 && i1 >= l ? l : i1;
	}

	private boolean liquidCanDisplaceBlock (World world, int i, int j, int k)
	{
		Material material = world.getBlockMaterial(i, j, k);
		if (material == blockMaterial)
			return false;
		else
			return !blockBlocksFlow(world, i, j, k);
	}

	@Override
	public void onBlockAdded (World world, int i, int j, int k)
	{
		super.onBlockAdded(world, i, j, k);
		if (world.getBlockId(i, j, k) == blockID)
		{
			world.scheduleBlockUpdate(i, j, k, blockID, tickRate());
		}
	}

	@Override
	public int stillLiquidMeta ()
	{
		return 0;
	}
	
	@Override
	public boolean isMetaSensitive ()
	{
		return false;
	}

	@Override
	public boolean isBlockReplaceable (World world, int i, int j, int k)
	{
		return true;
	}

}
