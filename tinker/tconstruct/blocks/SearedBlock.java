package tinker.tconstruct.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tinker.common.InventoryBlock;
import tinker.tconstruct.TConstruct;
import tinker.tconstruct.TContent;
import tinker.tconstruct.client.SearedRender;
import tinker.tconstruct.logic.CastingTableLogic;
import tinker.tconstruct.logic.FaucetLogic;

public class SearedBlock extends InventoryBlock
{
	public SearedBlock(int id)
	{
		super(id, Material.rock);
		this.setCreativeTab(TConstruct.blockTab);
		blockIndexInTexture = 32;
		setHardness(30F);
		setStepSound(soundMetalFootstep);
	}

	@Override
	public TileEntity createNewTileEntity (World world, int metadata)
	{
		switch (metadata)
		{
		case 0:
			return new CastingTableLogic();
		case 1:
			return new FaucetLogic();
		default:
			return null;
		}
	}

	@Override
	public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
	{
		int meta = world.getBlockMetadata(x, y, z);
		switch (meta)
		{
		case 0:
			return null;
		case 1:
			return null;
		default:
			return null;
		}
	}

	@Override
	public Object getModInstance ()
	{
		return TConstruct.instance;
	}

	/* Activation */
	@Override
	public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
	{
		int md = world.getBlockMetadata(x, y, z);
		if (md == 0)
		{
			return activateCastingTable(world, x, y, z, player);
		}
		else if (md == 1)
		{
			if (player.isSneaking())
				return false;

			FaucetLogic logic = (FaucetLogic) world.getBlockTileEntity(x, y, z);
			logic.setActive(true);
			return true;
		}
		else
			return super.onBlockActivated(world, x, y, z, player, side, clickX, clickY, clickZ);
	}

	boolean activateCastingTable (World world, int x, int y, int z, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			//System.out.println("Castses");
			CastingTableLogic logic = (CastingTableLogic) world.getBlockTileEntity(x, y, z);
			if (!logic.isStackInSlot(0) && !logic.isStackInSlot(1))
			{
				ItemStack stack = player.getCurrentEquippedItem();
				stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);
				logic.setInventorySlotContents(0, stack);
			}
			else
			{
				if (logic.isStackInSlot(1))
				{
					ItemStack stack = logic.decrStackSize(1, 1);
					if (stack != null)
						addItemToInventory(player, world, x, y, z, stack);
				}
				else if (logic.isStackInSlot(0))
				{
					ItemStack stack = logic.decrStackSize(0, 1);
					if (stack != null)
						addItemToInventory(player, world, x, y, z, stack);
				}
				/*ItemStack insideStack = logic.takeItemInColumn(0);

				if (insideStack == null)
				{r
				    insideStack = logic.takeItemInColumn(1 - 0);
				}

				if (insideStack != null)
				{
				    this.spawnItem(world, x, y, z, insideStack);
				}*/
			}

			world.markBlockForUpdate(x, y, z);
		}
		return true;
	}

	protected void addItemToInventory (EntityPlayer player, World world, int x, int y, int z, ItemStack stack)
	{
		if (!world.isRemote)
		{
			EntityItem entityitem = new EntityItem(world, (double) x + 0.5D, (double) y + 0.9325D, (double) z + 0.5D, stack);
			world.spawnEntityInWorld(entityitem);
			entityitem.onCollideWithPlayer(player);
		}
	}

	/* Rendering */
	@Override
	public int getRenderType ()
	{
		return SearedRender.searedModel;
	}

	@Override
	public String getTextureFile ()
	{
		return TContent.blockTexture;
	}

	public int getBlockTextureFromSideAndMetadata (int side, int meta)
	{
		if (meta == 0)
			return blockIndexInTexture + meta * 3 + getBlockTextureFromSide(side);
		/*else if (meta == 1)
		{
			if (side == 0 || side == 1)
				return blockIndexInTexture + meta + 3;
			else
				return blockIndexInTexture + meta + 2;
		}*/
		return blockIndexInTexture + meta + 2;
	}

	public int getBlockTextureFromSide (int side)
	{
		if (side == 0)
			return 2;
		if (side == 1)
			return 0;

		return 1;
	}

	@Override
	public boolean renderAsNormalBlock ()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube ()
	{
		return false;
	}

	@Override
	public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return true;
	}

	@Override
	public void getSubBlocks (int id, CreativeTabs tab, List list)
	{
		for (int iter = 0; iter < 2; iter++)
		{
			list.add(new ItemStack(id, 1, iter));
		}
	}

	@Override
	public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0)
		{
			this.setBlockBounds(0, 0, 0, 1, 1, 1);
		}
		if (meta == 1)
		{
			FaucetLogic logic = (FaucetLogic) world.getBlockTileEntity(x, y, z);
			float xMin = 0.25F;
			float xMax = 0.75F;
			float zMin = 0.25F;
			float zMax = 0.75F;

			switch (logic.getRenderDirection())
			{
			case 2:
				zMin = 0.625F;
				zMax = 1.0F;
				break;
			case 3:
				zMax = 0.375F;
				zMin = 0F;
				break;
			case 4:
				xMin = 0.625F;
				xMax = 1.0F;
				break;
			case 5:
				xMax = 0.375F;
				xMin = 0F;
				break;
			}

			this.setBlockBounds(xMin, 0.25F, zMin, xMax, 0.625F, zMax);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0)
		{
			return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z, x+1, y+1, z+1);
		}
		if (meta == 1)
		{
			FaucetLogic logic = (FaucetLogic) world.getBlockTileEntity(x, y, z);
			float xMin = 0.25F;
			float xMax = 0.75F;
			float zMin = 0.25F;
			float zMax = 0.75F;

			switch (logic.getRenderDirection())
			{
			case 2:
				zMin = 0.625F;
				zMax = 1.0F;
				break;
			case 3:
				zMax = 0.375F;
				zMin = 0F;
				break;
			case 4:
				xMin = 0.625F;
				xMax = 1.0F;
				break;
			case 5:
				xMax = 0.375F;
				xMin = 0F;
				break;
			}

			return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) ((float) x + xMin), (double) y + 0.25, (double) ((float) z + zMin), (double) ((float) x + xMax), (double) y + 0.625, (double) ((float) z + zMax));
		}
		
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
}
