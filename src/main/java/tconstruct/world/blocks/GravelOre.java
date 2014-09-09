package tconstruct.world.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.*;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tconstruct.library.TConstructRegistry;
import tconstruct.world.TinkerWorld;

public class GravelOre extends BlockSand
{
    public String[] textureNames = new String[] { "iron", "gold", "copper", "tin", "aluminum", "cobalt" };
    public IIcon[] icons;

    public GravelOre()
    {
        this.blockMaterial = Material.craftedSnow;
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setStepSound(soundTypeGravel);
    }

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:ore_" + textureNames[i] + "_gravel");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        if (meta >= textureNames.length)
            return icons[0];
        return icons[meta];
    }

    public float getBlockHardness (World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 5)
            return 10f;
        else
            return 3f;
    }

    @Override
    public int damageDropped (int meta)
    {
        /*if (meta == 1)
            return 0;*/
        return meta;
    }

    @Override
    public Item getItemDropped (int par1, Random par2Random, int par3)
    {
        /*if (par1 == 1)
            return Item.goldNugget.itemID;*/
        return Item.getItemFromBlock(TinkerWorld.oreGravel);
    }

    @Override
    public void getSubBlocks (Item id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 6; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}