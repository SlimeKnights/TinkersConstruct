package tinker.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import tinker.tconstruct.TConstruct;
import tinker.tconstruct.client.TankRender;
import tinker.tconstruct.logic.LavaTankLogic;

public class LavaTankBlock extends BlockContainer
{
	public LavaTankBlock(int id)
	{
		super(id, 49, Material.iron);
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
	public int getRenderType ()
	{
		return TankRender.tankModelID;
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

	/*public void addCollidingBlockToList (World world, int x, int y, int z, AxisAlignedBB box, List list, Entity entity)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if (meta < 5)
		{
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
			super.addCollidingBlockToList(world, x, y, z, box, list, entity);
		}

		if (meta != 2 && meta != 4 && meta != 7 && meta != 9)
		{
			setBlockBounds(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F);
			super.addCollidingBlockToList(world, x, y, z, box, list, entity);
		}

		if (meta != 3 && meta != 4 && meta != 8 && meta != 9)
		{
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F);
			super.addCollidingBlockToList(world, x, y, z, box, list, entity);
		}

		if (meta != 3 && meta != 1 && meta != 8 && meta != 6)
		{
			setBlockBounds(0.875F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			super.addCollidingBlockToList(world, x, y, z, box, list, entity);
		}

		if (meta != 1 && meta != 2 && meta != 6 && meta != 7)
		{
			setBlockBounds(0.0F, 0.0F, 0.875F, 1.0F, 1.0F, 1.0F);
			super.addCollidingBlockToList(world, x, y, z, box, list, entity);
		}

		setBlockBoundsForItemRender();
	}

	public void setBlockBoundsForItemRender ()
	{
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}*/
}
