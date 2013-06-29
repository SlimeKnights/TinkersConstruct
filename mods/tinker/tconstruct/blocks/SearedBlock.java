package mods.tinker.tconstruct.blocks;

import java.util.List;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.CastingBasinLogic;
import mods.tinker.tconstruct.blocks.logic.CastingTableLogic;
import mods.tinker.tconstruct.blocks.logic.FaucetLogic;
import mods.tinker.tconstruct.client.block.SearedRender;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.blocks.InventoryBlock;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SearedBlock extends InventoryBlock
{
	
	public SearedBlock(int id)
	{
		super(id, Material.rock);
		this.setCreativeTab(TConstructRegistry.blockTab);
		setHardness(12);
		setStepSound(soundMetalFootstep);
	}

	@Override
	public TileEntity createTileEntity (World world, int metadata)
	{
		switch (metadata)
		{
		case 0:
			return new CastingTableLogic();
		case 1:
			return new FaucetLogic();
        case 2:
            return new CastingBasinLogic();
		default:
			return null;
		}
	}
    
    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }

	@Override
	public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
	{
		return null;
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
		if (md == 2)
        {
            return activateCastingBasin(world, x, y, z, player);
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
			CastingTableLogic logic = (CastingTableLogic) world.getBlockTileEntity(x, y, z);
			if (logic.liquid != null)
				return true;
			
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
			}

			world.markBlockForUpdate(x, y, z);
		}
		return true;
	}
	
	boolean activateCastingBasin (World world, int x, int y, int z, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            CastingBasinLogic logic = (CastingBasinLogic) world.getBlockTileEntity(x, y, z);
            if (logic.liquid != null)
                return true;
            
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
            }

            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

	public void addItemToInventory (EntityPlayer player, World world, int x, int y, int z, ItemStack stack)
	{
	    AbilityHelper.spawnItemAtPlayer(player, stack);
		/*if (!world.isRemote)
		{
			EntityItem entityitem = new EntityItem(world, (double) x + 0.5D, (double) y + 0.9325D, (double) z + 0.5D, stack);
			world.spawnEntityInWorld(entityitem);
			entityitem.onCollideWithPlayer(player);
		}*/
	}

	/* Rendering */
	@Override
	public int getRenderType ()
	{
		return SearedRender.searedModel;
	}

	@Override
	public String[] getTextureNames()
	{
		String[] textureNames = { 
			"castingtable_top",
			"castingtable_side",
			"castingtable_bottom",
			"faucet",
            "blockcast_top",
            "blockcast_side",
            "blockcast_bottom"};
		
		return textureNames;
	}

	@Override
	public Icon getIcon (int side, int meta)
	{
		if (meta == 0)
			return icons[getTextureIndex(side)];
		else if (meta == 2)
		    return icons[getTextureIndex(side)+4];
		else
			return icons[3];
	}

	public int getTextureIndex (int side)
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
		for (int iter = 0; iter < 3; iter++)
		{
			list.add(new ItemStack(id, 1, iter));
		}
	}

	@Override
	public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (meta != 1)
		{
			this.setBlockBounds(0, 0, 0, 1, 1, 1);
		}
		else
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
		if (meta != 1)
		{
			return AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 1, z + 1);
		}
		else
		{
			FaucetLogic logic = (FaucetLogic) world.getBlockTileEntity(x, y, z);
			if (logic != null)
			{
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

				return AxisAlignedBB.getAABBPool().getAABB((double) ((float) x + xMin), (double) y + 0.25, (double) ((float) z + zMin), (double) ((float) x + xMax), (double) y + 0.625, (double) ((float) z + zMax));
			}
		}

		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	/* Redstone */
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return world.getBlockMetadata(x, y, z) == 1;
    }
	
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID)
    {
	    if (world.isBlockIndirectlyGettingPowered(x, y, z) && world.getBlockMetadata(x, y, z) == 1)
	    {
	        FaucetLogic logic = (FaucetLogic) world.getBlockTileEntity(x, y, z);
            logic.setActive(true);
	    }
    }
}
