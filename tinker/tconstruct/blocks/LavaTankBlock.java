package tinker.tconstruct.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import tinker.tconstruct.TConstruct;
import tinker.tconstruct.TContent;
import tinker.tconstruct.client.TankRender;
import tinker.tconstruct.logic.LavaTankLogic;

public class LavaTankBlock extends BlockContainer
{
	public LavaTankBlock(int id)
	{
		super(id, 38, Material.rock);
		setHardness(30F);
		setCreativeTab(TConstruct.blockTab);
		setBlockName("TConstruct.LavaTank");
		setStepSound(Block.soundGlassFootstep);
	}

	@Override
	public boolean isOpaqueCube ()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock ()
	{
		return false;
	}

	@Override
	public boolean shouldSideBeRendered (IBlockAccess world, int x, int y, int z, int side)
	{
		//if (side == 0 && world.getBlockMetadata(x, y, z) == 0)
			//return super.shouldSideBeRendered(world, x, y, z, side);
        int bID = world.getBlockId(x, y, z);
        return bID == this.blockID ? false : super.shouldSideBeRendered(world, x, y, z, side);
		//return true;
	}

	@Override
	public String getTextureFile ()
	{
		return TContent.blockTexture;
	}

	@Override
	public int getRenderType ()
	{
		return TankRender.tankModelID;
	}

	public int getBlockTextureFromSideAndMetadata (int side, int meta)
	{
		if (meta == 0)
		{
			if (side == 0 || side == 1)
			{
				return blockIndexInTexture + 1;
			}
			else
			{
				return blockIndexInTexture;
			}
		}
		else
		{
			if (side == 0)
			{
				return blockIndexInTexture + 1 + meta * 3;
			}
			else if (side == 1)
			{
				return blockIndexInTexture + meta * 3;
			}
			else
			{
				return blockIndexInTexture - 1 + meta * 3;
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity (World world, int metadata)
	{
		return new LavaTankLogic();
	}

	@Override
	public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
	{
		LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(player.getCurrentEquippedItem());
		if (liquid != null)
		{
			LavaTankLogic logic = (LavaTankLogic) world.getBlockTileEntity(x, y, z);
			int amount = logic.fill(liquid, true);
			if (amount > 0)
				return true;
			else
				return false;
		}
		else
		{

		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity (World world)
	{
		return createNewTileEntity(world, 0);
	}

	@Override
	public void getSubBlocks (int id, CreativeTabs tab, List list)
	{
		for (int iter = 0; iter < 3; iter++)
		{
			list.add(new ItemStack(id, 1, iter));
		}
	}
	
	/* Data */
	public int damageDropped (int meta)
	{
		return meta;
	}
}
