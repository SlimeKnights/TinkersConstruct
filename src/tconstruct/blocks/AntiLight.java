package tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AntiLight extends Block
{
    public AntiLight(int id)
    {
        super(id, Material.air);
        float f = 0.0F;
        float f1 = 0.0F;
        this.setBlockBounds(0.0F + f1, 0.0F + f, 0.0F + f1, 1.0F + f1, 1.0F + f, 1.0F + f1);
    }

    @Override
    public int getLightOpacity (World world, int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public boolean isAirBlock (World world, int x, int y, int z)
    {
        return true;
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
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("tinker:blank");
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        return null;
    }

    public boolean canCollideCheck (int par1, boolean par2)
    {
        return false;
    }

    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return false;
    }

    public void onBlockExploded (World world, int x, int y, int z, Explosion explosion)
    {

    }
}
