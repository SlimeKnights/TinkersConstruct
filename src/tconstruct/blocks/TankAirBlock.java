package tconstruct.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.blocks.logic.TankAirLogic;
import tconstruct.client.block.TankAirRender;

public class TankAirBlock extends BlockContainer
{

    public TankAirBlock(int id, Material material)
    {
        super(id, material);
    }

    @Override
    public TileEntity createNewTileEntity (World world)
    {
        return new TankAirLogic();
    }
    
    @Override
    public int getRenderType ()
    {
        return TankAirRender.model;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        
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
}
