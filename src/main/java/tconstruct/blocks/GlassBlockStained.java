package tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GlassBlockStained extends TConstructBlock
{
    static String blockTextures[] = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };
    String textureName;

    public GlassBlockStained(String tex)
    {
        super(Material.glass, 3f, blockTextures);
        this.textureName = tex;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }

    public boolean isOpaqueCube ()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean  shouldSideBeRendered (IBlockAccess world, int x, int y, int z, int side)
    {
        Block block = world.getBlock(x, y, z);
        return block == this ? false : super. shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:glass/" + textureName + textureNames[i]);
        }
    }
}
