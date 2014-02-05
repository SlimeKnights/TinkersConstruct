package tconstruct.blocks;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.blocks.logic.TankAirLogic;
import tconstruct.client.block.TankAirRender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TankAirBlock extends BlockContainer
{

    public TankAirBlock(Material material)
    {
        super(material);
    }

    @Override
    public TileEntity createNewTileEntity (World world, int i)
    {
        return new TankAirLogic();
    }

    @Override
    public int getRenderType ()
    {
        return TankAirRender.model;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister par1IconRegister)
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

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        TankAirLogic tank = (TankAirLogic) world.getTileEntity(x, y, z);
        if (tank != null && tank.hasItem())
            return super.getCollisionBoundingBoxFromPool(world, x, y, z);

        return null;
    }

    public MovingObjectPosition collisionRayTrace (World world, int x, int y, int z, Vec3 par5Vec3, Vec3 par6Vec3)
    {
        TankAirLogic tank = (TankAirLogic) world.getTileEntity(x, y, z);
        if (tank.hasItem())
            return super.collisionRayTrace(world, x, y, z, par5Vec3, par6Vec3);

        return null;
    }

    @Override
    public ItemStack getPickBlock (MovingObjectPosition target, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public boolean isReplaceable (IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean canHarvestBlock (EntityPlayer player, int meta)
    {
        return false;
    }

    @Override
    public boolean isAir (IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public void getSubBlocks (Item i, CreativeTabs tab, List list)
    {
    }

}
