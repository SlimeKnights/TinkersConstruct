package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import tconstruct.smeltery.model.PaneRender;

public class PaneBase extends BlockStainedGlassPane
{
    public String[] textureNames;
    public String folder;
    public IIcon[] icons;
    public IIcon[] sideIcons;

    public PaneBase(Material material, String folder, String[] blockTextures)
    {
        super();
        textureNames = blockTextures;
        this.folder = folder;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];
        this.sideIcons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + folder + textureNames[i]);
            this.sideIcons[i] = iconRegister.registerIcon("tinker:" + folder + textureNames[i] + "_side");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149735_b (int p_149735_1_, int p_149735_2_)
    {
        return icons[p_149735_2_];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_150104_b (int p_150104_1_)
    {
        return sideIcons[p_150104_1_];
    }

    @Override
    public IIcon getIcon (int p_149691_1_, int p_149691_2_)
    {
        return icons[p_149691_2_];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < textureNames.length; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
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
    public int getRenderType ()
    {
        return PaneRender.model;
    }
}
