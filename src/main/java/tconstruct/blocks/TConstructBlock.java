package tconstruct.blocks;

import java.util.List;

import mantle.blocks.MantleBlock;
import tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TConstructBlock extends MantleBlock
{
    public String[] textureNames;
    public IIcon[] icons;

    public TConstructBlock( Material material, float hardness, String[] tex)
    {
        super( material);
        setHardness(hardness);
        this.func_149647_a(TConstructRegistry.blockTab);
        textureNames = tex;
    }

    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void func_149651_a (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a (int side, int meta)
    {
        return meta < icons.length ? icons[meta] : icons[0];
    }

    @SideOnly(Side.CLIENT)
    public int getSideTextureIndex (int side)
    {
        if (side == 0)
            return 2;
        if (side == 1)
            return 0;

        return 1;
    }

    //TODO getSubBlocks
    @Override
    public void func_149666_a (Item block, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < icons.length; iter++)
        {
            list.add(new ItemStack(block, 1, iter));
        }
    }
}
