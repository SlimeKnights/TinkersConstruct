package mods.tinker.tconstruct.blocks;

import java.util.List;

import mods.tinker.common.IServantLogic;
import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.client.TankRender;
import mods.tinker.tconstruct.logic.LavaTankLogic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class LavaTankBlock extends BlockContainer
{
	public Icon[] icons;
	public LavaTankBlock(int id)
	{
		super(id, Material.rock);
		setHardness(12);
		setCreativeTab(TConstruct.blockTab);
		setUnlocalizedName("TConstruct.LavaTank");
		setStepSound(Block.soundGlassFootstep);
	}
	
	public String[] getTextureNames()
	{
		String[] textureNames = { 
				"lavatank_side",
				"lavatank_top",
				"searedgague_top",
				"searedgague_side",
				"searedgague_bottom",
				"searedwindow_top",
				"searedwindow_side",
				"searedwindow_bottom" };
			
			return textureNames;
	}
	
	public void func_94332_a(IconRegister iconRegister)
    {
		String[] textureNames = getTextureNames();
		this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.func_94245_a("tinker:"+textureNames[i]);
        }
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
	public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
		TileEntity logic = world.getBlockTileEntity(x, y, z);
		if (logic != null && logic instanceof LavaTankLogic)
			return ((LavaTankLogic) logic).getBrightness();
		return 0;
    }
	
	/*@Override
	public int getRenderBlockPass()
    {
		return 1;
    }*/


	@Override
	public int getRenderType ()
	{
		return TankRender.tankModelID;
	}

	public Icon getBlockTextureFromSideAndMetadata (int side, int meta)
	{
		if (meta == 0)
		{
			if (side == 0 || side == 1)
			{
				return icons[1];
			}
			else
			{
				return icons[0];
			}
		}
		else
		{
			return icons[meta*3+getTextureIndex(side)-1];
		}
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
	public TileEntity createTileEntity (World world, int metadata)
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
			int amount = logic.fill(0, liquid, true);
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
		return createTileEntity(world, 0);
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
	
	/* Updates */
	public void onNeighborBlockChange(World world, int x, int y, int z, int nBlockID) 
	{
		TileEntity logic = world.getBlockTileEntity(x, y, z);
		if (logic instanceof IServantLogic)
		{
			((IServantLogic) logic).notifyMasterOfChange();
		}
	}
}
