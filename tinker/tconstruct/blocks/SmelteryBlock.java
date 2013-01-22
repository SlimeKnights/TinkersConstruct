package tinker.tconstruct.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tinker.common.IFacingLogic;
import tinker.common.InventoryBlock;
import tinker.common.InventoryLogic;
import tinker.tconstruct.TConstruct;
import tinker.tconstruct.TConstructContent;
import tinker.tconstruct.logic.SmelteryLogic;

public class SmelteryBlock extends InventoryBlock
{
	Random rand;

	public SmelteryBlock(int id)
	{
		super(id, Material.iron);
		blockIndexInTexture = 64;
		setHardness(1.5F);
		setStepSound(soundMetalFootstep);
		rand = new Random();
		this.setCreativeTab(TConstruct.blockTab);
		this.setBlockName("tconstruct.Smeltery");
	}

	public String getTextureFile ()
	{
		return TConstructContent.blockTexture;
	}

	public int getBlockTextureFromSideAndMetadata (int side, int meta)
	{
		if (side == 0 || side == 1)
		{
			return blockIndexInTexture + 3 + meta * 4;
		}
		if (side == 3)
		{
			return blockIndexInTexture + meta * 4;
		}
		else
		{
			return blockIndexInTexture + 2 + meta * 4;
		}
	}

	public int getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
	{
		InventoryLogic logic = (InventoryLogic) world.getBlockTileEntity(x, y, z);
		short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getDirection() : 0;
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0) //Smeltery
		{
			if (side == 1)
			{
				return blockIndexInTexture + 3 + meta * 4;
			}
			if (side == direction)
			{
				if (isActive(world, x, y, z))
				{
					return blockIndexInTexture + 2 + meta * 4;
				}
				else
				{
					return blockIndexInTexture + 1 + meta * 4;
				}
			}
			else
			{
				return blockIndexInTexture + meta * 4;
			}
		}
		else //Output
		{
			if (side == direction)
				return blockIndexInTexture + 3 + meta*2;
			else
				return blockIndexInTexture + 2 + meta*2;
		}
	}

	public int damageDropped (int meta)
	{
		return meta;
	}

	public int quantityDropped (Random random)
	{
		return 1;
	}

	@Override
	public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
	{
		return 3;
	}

	public void randomDisplayTick (World world, int x, int y, int z, Random random)
	{
		if (isActive(world, x, y, z))
		{
			InventoryLogic logic = (InventoryLogic) world.getBlockTileEntity(x, y, z);
			byte face = 0;
			if (logic instanceof IFacingLogic)
				face = ((IFacingLogic)logic).getDirection();
			float f = (float) x + 0.5F;
			float f1 = (float) y + 0.0F + (random.nextFloat() * 6F) / 16F;
			float f2 = (float) z + 0.5F;
			float f3 = 0.52F;
			float f4 = random.nextFloat() * 0.6F - 0.3F;
			switch (face)
			{
			case 4:
				world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
				break;

			case 5:
				world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
				break;

			case 2:
				world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
				break;

			case 3:
				world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
				break;
			}
		}
	}

	public int getLightValue (IBlockAccess world, int x, int y, int z)
	{
		return !isActive(world, x, y, z) ? 0 : 9;
	}

	//@Override
	/*public void getSubBlocks (int id, CreativeTabs tab, List list)
	{
		for (int iter = 0; iter < 9; iter++)
		{
			list.add(new ItemStack(id, 1, iter));
		}
	}*/

	@Override
	public Object getModInstance ()
	{
		return TConstruct.instance;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) 
	{
		return new SmelteryLogic();
	}
	
	@Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityliving)
	{
		super.onBlockPlacedBy(world, x, y, z, entityliving);
		onBlockPlacedElsewhere(world, x, y, z, entityliving);
	}
	
	public void onBlockPlacedElsewhere(World world, int x, int y, int z, EntityLiving entityliving)
	{
		SmelteryLogic logic = (SmelteryLogic) world.getBlockTileEntity(x, y, z);
		logic.checkValidPlacement();
	}
	
	@Override
	public void breakBlock (World world, int x, int y, int z, int par5, int par6) //Don't drop inventory
	{
		world.removeBlockTileEntity(x, y, z);
	}
}
