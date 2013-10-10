package tconstruct.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import tconstruct.blocks.logic.TankAirLogic;
import tconstruct.client.block.TankAirRender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
    public void registerIcons (IconRegister par1IconRegister)
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
        TankAirLogic tank = (TankAirLogic) world.getBlockTileEntity(x, y, z);
        if (tank.hasItem())
            return super.getCollisionBoundingBoxFromPool(world, x, y, z);

        return null;
    }
    
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 par5Vec3, Vec3 par6Vec3)
    {
        TankAirLogic tank = (TankAirLogic) world.getBlockTileEntity(x, y, z);
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
    public boolean isBlockReplaceable (World world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta)
    {
        return false;
    }
    
    @Override
    public boolean isAirBlock(World world, int x, int y, int z)
    {
        return true;
    }
}
