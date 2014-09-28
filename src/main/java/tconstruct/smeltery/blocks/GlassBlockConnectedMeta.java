package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import tconstruct.util.config.PHConstruct;

/**
 * @author fuj1n
 * 
 */

public class GlassBlockConnectedMeta extends GlassBlockConnected
{
    public String[] textures;
    public IIcon[][] icons;
    boolean ignoreMetaForConnectedGlass = PHConstruct.connectedTexturesMode == 2;

    public GlassBlockConnectedMeta(String location, boolean hasAlpha, String... textures)
    {
        super(location, hasAlpha);
        this.textures = textures;
        this.icons = new IIcon[textures.length][16];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        int meta = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        if (meta < icons.length)
        {
            return getConnectedBlockTexture(par1IBlockAccess, par2, par3, par4, par5, icons[meta]);
        }
        else
        {
            return getConnectedBlockTexture(par1IBlockAccess, par2, par3, par4, par5, icons[0]);
        }
    }

    @Override
    public boolean shouldConnectToBlock (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, Block par5, int par6)
    {
        return par5 == this && (par6 == par1IBlockAccess.getBlockMetadata(par2, par3, par4) || ignoreMetaForConnectedGlass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int par1, int par2)
    {
        return icons[par2][0];
    }

    @Override
    public void getSubBlocks (Item b, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < textures.length; i++)
        {
            par3List.add(new ItemStack(b, 1, i));
        }
    }

    @Override
    public void registerBlockIcons (IIconRegister par1IconRegister)
    {
        for (int i = 0; i < textures.length; i++)
        {
            icons[i][0] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass");
            icons[i][1] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_1_d");
            icons[i][2] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_1_u");
            icons[i][3] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_1_l");
            icons[i][4] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_1_r");
            icons[i][5] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_2_h");
            icons[i][6] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_2_v");
            icons[i][7] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_2_dl");
            icons[i][8] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_2_dr");
            icons[i][9] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_2_ul");
            icons[i][10] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_2_ur");
            icons[i][11] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_3_d");
            icons[i][12] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_3_u");
            icons[i][13] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_3_l");
            icons[i][14] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_3_r");
            icons[i][15] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_4");
        }
    }

    @Override
    public int damageDropped (int par1)
    {
        return par1;
    }

    @Override
    public boolean canPlaceTorchOnTop (World world, int x, int y, int z)
    {
        return true;
    }

}
