package tinker.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
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
		super(id, 38, Material.iron);
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
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		if (par5 == 0)
			return super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
		return true;
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
		if (side == 0 || side == 1)
		{
			return blockIndexInTexture + 1 + meta * 3;
		}
		/*else if (side == 1)
		{
			return blockIndexInTexture + 1 + meta * 3;
		}*/
		else
		{
			return blockIndexInTexture + meta * 3;
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
}
