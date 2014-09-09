package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import tconstruct.blocks.TConstructBlock;

public class GlassBlock extends TConstructBlock
{
    static String blockTextures[] = { "glass_clear", "soulglass", "soulglass_clear" };

    public GlassBlock()
    {
        super(Material.glass, 3f, blockTextures);
    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        Block i1 = par1IBlockAccess.getBlock(par2, par3, par4);
        return i1 == (Block) this ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }

    @Override
    public float getBlockHardness (World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta)
        {
        case 0:
            return 0.3F;
        case 1:
            return 50.0F;
        case 2:
            return 50.0F;
        default:
            return blockHardness;
        }
    }

    @Override
    public float getExplosionResistance (Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta)
        {
        case 0:
            return 1.5F;
        case 1:
            return 2000F;
        case 2:
            return 2000F;
        default:
            return getExplosionResistance(entity);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:glass/" + textureNames[i]);
        }
    }
}
