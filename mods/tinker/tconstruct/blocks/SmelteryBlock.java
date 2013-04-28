package mods.tinker.tconstruct.blocks;

import java.util.List;
import java.util.Random;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.MultiServantLogic;
import mods.tinker.tconstruct.blocks.logic.SmelteryDrainLogic;
import mods.tinker.tconstruct.blocks.logic.SmelteryLogic;
import mods.tinker.tconstruct.client.block.SmelteryRender;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.blocks.InventoryBlock;
import mods.tinker.tconstruct.library.util.IFacingLogic;
import mods.tinker.tconstruct.library.util.IMasterLogic;
import mods.tinker.tconstruct.library.util.IServantLogic;
import mods.tinker.tconstruct.util.network.TGuiHandler;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SmelteryBlock extends InventoryBlock
{
	Random rand;

	public SmelteryBlock(int id)
	{
		super(id, Material.rock);
		setHardness(12F);
		setStepSound(soundMetalFootstep);
		rand = new Random();
		this.setCreativeTab(TConstructRegistry.blockTab);
		this.setUnlocalizedName("tconstruct.Smeltery");
	}
	
	/* Rendering */

	@Override
	public int getRenderType ()
	{
		return SmelteryRender.smelteryModel;
	}
	
	@Override
	public String[] getTextureNames()
	{
		String[] textureNames = { 
			"smeltery_side",
			"smeltery_inactive",
			"smeltery_active",
			"drain_side",
			"drain_out",
			"drain_basin",
			"searedbrick",	
		};
		
		return textureNames;
	}

	public Icon getIcon (int side, int meta)
	{
		if (meta < 2)
		{
			int sideTex = side == 3 ? 1 : 0;
			return icons[sideTex + meta*3];
		}
		else
		{
			return icons[6];
		}
	}

	public Icon getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity logic = world.getBlockTileEntity(x, y, z);
		short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0) //Smeltery
		{
			if (side == direction)
			{
				if (isActive(world, x, y, z))
				{
					return icons[2];
				}
				else
				{
					return icons[1];
				}
			}
			else
			{
				return icons[0];
			}
		}
		if (meta == 1) //Drain
		{
			if (side == direction)
				return icons[5];
			else if (side / 2 == direction / 2)
				return icons[4];
			else
				return icons[3];
		}
		else //Brick
		{
			return icons[6];
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
		return TGuiHandler.smeltery;
	}

	public void randomDisplayTick (World world, int x, int y, int z, Random random)
	{
		if (isActive(world, x, y, z))
		{
			TileEntity logic = world.getBlockTileEntity(x, y, z);
			byte face = 0;
			if (logic instanceof IFacingLogic)
				face = ((IFacingLogic) logic).getRenderDirection();
			float f = (float) x + 0.5F;
			float f1 = (float) y + 0.5F + (random.nextFloat() * 6F) / 16F;
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
	public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
	{		
		if (player.isSneaking() || world.getBlockMetadata(x, y, z) != 0)
			return false;
		
		Integer integer = getGui(world, x, y, z, player);
		if (integer == null || integer == -1)
		{
			return false;
		}
		else
		{
			player.openGui(getModInstance(), integer, world, x, y, z);
			return true;
		}
	}

	@Override
	public TileEntity createTileEntity (World world, int metadata)
	{
		switch (metadata)
		{
		case 0:	return new SmelteryLogic();
		case 1: return new SmelteryDrainLogic();
		case 2: return new MultiServantLogic();
		default: return null;
		}
	}

	@Override
	public void onBlockPlacedBy (World world, int x, int y, int z, EntityLiving entityliving, ItemStack stack)
	{
		super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
		if (world.getBlockMetadata(x, y, z) == 0)
			onBlockPlacedElsewhere(world, x, y, z, entityliving);
	}

	public void onBlockPlacedElsewhere (World world, int x, int y, int z, EntityLiving entityliving)
	{
		SmelteryLogic logic = (SmelteryLogic) world.getBlockTileEntity(x, y, z);
		logic.checkValidPlacement();
	}

	@Override
	public void breakBlock (World world, int x, int y, int z, int par5, int par6) //Don't drop inventory
	{
		world.removeBlockTileEntity(x, y, z);
	}
	
	@Override
	public void getSubBlocks (int id, CreativeTabs tab, List list)
	{
		for (int iter = 0; iter < 3; iter++)
		{
			list.add(new ItemStack(id, 1, iter));
		}
	}
	
	/* Updating */
	public void onNeighborBlockChange(World world, int x, int y, int z, int nBlockID) 
	{
		TileEntity logic = world.getBlockTileEntity(x, y, z);
		if (logic instanceof IServantLogic)
		{
			((IServantLogic) logic).notifyMasterOfChange();
		}
		else if (logic instanceof IMasterLogic)
		{
			((IMasterLogic) logic).notifyChange(x, y, z);
		}
	}
}
