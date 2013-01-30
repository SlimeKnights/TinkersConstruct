package tinker.armory.content;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tinker.armory.client.RenderDisplay;
import tinker.common.InventoryBlock;

public class DisplayBlock extends InventoryBlock
{

	public DisplayBlock(int id, Material material)
	{
		super(id, material);
	}

	@Override
	public TileEntity createNewTileEntity (World world, int metadata)
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
}
