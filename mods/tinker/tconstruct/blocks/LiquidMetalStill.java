package mods.tinker.tconstruct.blocks;

import java.util.Random;

import mods.tinker.tconstruct.blocks.logic.LiquidTextureLogic;
import mods.tinker.tconstruct.common.TContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquid;

public class LiquidMetalStill extends LiquidMetalBase implements ILiquid
{
	public LiquidMetalStill(int id)
	{
		super(id, TContent.liquidMetal);
		//this.setCreativeTab(TConstruct.blockTab);
	}

	@Override
	public int stillLiquidId ()
	{
		return this.blockID;
	}

	public int flowingLiquidID ()
	{
		return TContent.liquidMetalFlowing.blockID;
	}

	@Override
	public boolean isMetaSensitive ()
	{
		return false;
	}

	@Override
	public int stillLiquidMeta ()
	{
		return 0;
	}

	public void updateTick (World par1World, int par2, int par3, int par4, Random par5Random)
	{
		int var6 = par5Random.nextInt(3);
		int var7;
		int var8;

		for (var7 = 0; var7 < var6; ++var7)
		{
			par2 += par5Random.nextInt(3) - 1;
			++par3;
			par4 += par5Random.nextInt(3) - 1;
			var8 = par1World.getBlockId(par2, par3, par4);

			if (var8 == 0)
			{
				if (this.isFlammable(par1World, par2 - 1, par3, par4) || this.isFlammable(par1World, par2 + 1, par3, par4) || this.isFlammable(par1World, par2, par3, par4 - 1) || this.isFlammable(par1World, par2, par3, par4 + 1) || this.isFlammable(par1World, par2, par3 - 1, par4) || this.isFlammable(par1World, par2, par3 + 1, par4))
				{
					par1World.setBlock(par2, par3, par4, Block.fire.blockID);
					return;
				}
			}
			else if (Block.blocksList[var8].blockMaterial.blocksMovement())
			{
				return;
			}
		}

		if (var6 == 0)
		{
			var7 = par2;
			var8 = par4;

			for (int var9 = 0; var9 < 3; ++var9)
			{
				par2 = var7 + par5Random.nextInt(3) - 1;
				par4 = var8 + par5Random.nextInt(3) - 1;

				if (par1World.isAirBlock(par2, par3 + 1, par4) && this.isFlammable(par1World, par2, par3, par4))
				{
					par1World.setBlock(par2, par3 + 1, par4, Block.fire.blockID);
				}
			}
		}

	}

	@Override
	public boolean isBlockReplaceable (World world, int i, int j, int k)
	{
		return true;
	}

	@Override
	public void onNeighborBlockChange (World par1World, int par2, int par3, int par4, int par5)
	{
		this.tryToHarden(par1World, par2, par3, par4);

		if (par1World.getBlockId(par2, par3, par4) == this.blockID)
		{
			this.unsetStationary(par1World, par2, par3, par4);
		}
	}

	private void tryToHarden (World par1World, int par2, int par3, int par4)
	{
		if (par1World.getBlockId(par2, par3, par4) == this.blockID)
		{
			if (this.blockMaterial == Material.lava)
			{
				boolean var5 = false;

				if (var5 || par1World.getBlockMaterial(par2, par3, par4 - 1) == Material.water)
				{
					var5 = true;
				}

				if (var5 || par1World.getBlockMaterial(par2, par3, par4 + 1) == Material.water)
				{
					var5 = true;
				}

				if (var5 || par1World.getBlockMaterial(par2 - 1, par3, par4) == Material.water)
				{
					var5 = true;
				}

				if (var5 || par1World.getBlockMaterial(par2 + 1, par3, par4) == Material.water)
				{
					var5 = true;
				}

				if (var5 || par1World.getBlockMaterial(par2, par3 + 1, par4) == Material.water)
				{
					var5 = true;
				}

				if (var5)
				{
					/*int var6 = par1World.getBlockMetadata(par2, par3, par4);

					if (var6 == 0)
					{
					    par1World.setBlockWithNotify(par2, par3, par4, Block.obsidian.blockID);
					}
					else if (var6 <= 4)
					{
					    par1World.setBlockWithNotify(par2, par3, par4, Block.cobblestone.blockID);
					}*/

					this.triggerLavaMixEffects(par1World, par2, par3, par4);
				}
			}
		}
	}

	/**
	 * Creates fizzing sound and smoke. Used when lava flows over block or mixes with water.
	 */
	protected void triggerLavaMixEffects (World par1World, int par2, int par3, int par4)
	{
		par1World.playSoundEffect((double) ((float) par2 + 0.5F), (double) ((float) par3 + 0.5F), (double) ((float) par4 + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

		for (int var5 = 0; var5 < 8; ++var5)
		{
			par1World.spawnParticle("largesmoke", (double) par2 + Math.random(), (double) par3 + 1.2D, (double) par4 + Math.random(), 0.0D, 0.0D, 0.0D);
		}
	}

	private void unsetStationary (World world, int x, int y, int z)
	{
		
		int meta = world.getBlockMetadata(x, y, z);
		int tex = ((LiquidTextureLogic) world.getBlockTileEntity(x, y, z)).getLiquidType();
        world.setBlock(x, y, z, flowingLiquidID(), meta, 2);
        world.scheduleBlockUpdate(x, y, z, flowingLiquidID(), this.tickRate(world));
		((LiquidTextureLogic) world.getBlockTileEntity(x, y, z)).setLiquidType(tex);
	}

	public boolean getBlocksMovement (IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return this.blockMaterial != Material.lava;
	}

	/**
	 * Checks to see if the block is flammable.
	 */
	private boolean isFlammable (World par1World, int par2, int par3, int par4)
	{
		return par1World.getBlockMaterial(par2, par3, par4).getCanBurn();
	}

	@Override
	public boolean hasTileEntity (int metadata)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity (World world, int metadata)
	{
		return new LiquidTextureLogic();
	}
}
