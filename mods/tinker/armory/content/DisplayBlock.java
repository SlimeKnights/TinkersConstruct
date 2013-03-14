package mods.tinker.armory.content;

import mods.tinker.armory.client.RenderDisplay;
import mods.tinker.common.InventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DisplayBlock extends InventoryBlock
{

	public DisplayBlock(int id, Material material)
	{
		super(id, material);
	}

	@Override
	public TileEntity createTileEntity (World world, int metadata)
	{
		return new ShieldrackLogic();
		//return null;
	}

	@Override
	public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
	{
		return null;
	}

	@Override
	public Object getModInstance ()
	{
		return null;
	}

	public int getRenderType()
    {
        return RenderDisplay.displayModel;
    }
	
	@Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }

	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
	public String[] getTextureNames ()
	{
		// TODO Auto-generated method stub
		 return new String[] { "toolstation_top" };
	}
	
	public void func_94332_a (IconRegister par1IconRegister)
	{
		this.field_94336_cN = par1IconRegister.func_94245_a(Block.blockSteel.getUnlocalizedName());
	}
}
