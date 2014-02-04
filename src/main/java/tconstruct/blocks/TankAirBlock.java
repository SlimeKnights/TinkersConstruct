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
    public TileEntity func_149915_a (World world, int i)
    {
        return new TankAirLogic();
    }

    @Override
    public int func_149645_b ()
    {
        return TankAirRender.model;
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a (IIconRegister par1IconRegister)
    {

    }

    @Override
    public boolean func_149662_c ()
    {
        return false;
    }

    @Override
    public boolean func_149686_d ()
    {
        return false;
    }

    @Override
    public AxisAlignedBB func_149668_a (World world, int x, int y, int z)
    {
        TankAirLogic tank = (TankAirLogic) world.func_147438_o(x, y, z);
        if (tank != null && tank.hasItem())
            return super.func_149668_a(world, x, y, z);

        return null;
    }

    public MovingObjectPosition func_149731_a (World world, int x, int y, int z, Vec3 par5Vec3, Vec3 par6Vec3)
    {
        TankAirLogic tank = (TankAirLogic) world.func_147438_o(x, y, z);
        if (tank.hasItem())
            return super.func_149731_a(world, x, y, z, par5Vec3, par6Vec3);

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
    public void func_149666_a (Item i, CreativeTabs tab, List list)
    {
    }

}
