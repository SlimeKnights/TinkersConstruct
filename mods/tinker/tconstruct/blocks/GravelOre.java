package mods.tinker.tconstruct.blocks;

import java.util.List;
import java.util.Random;

import mods.tinker.tconstruct.library.TConstructRegistry;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class GravelOre extends BlockSand
{
    public String[] textureNames = new String[] { "iron", "gold", "copper", "tin", "aluminum", "cobalt" };
    public Icon[] icons;
    
	public GravelOre(int id)
	{
		super(id, Material.craftedSnow);
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setStepSound(soundGravelFootstep);
	}
	    
    public void registerIcons(IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:ore_"+textureNames[i]+"_gravel");
        }
    }
    
    @Override
    public Icon getIcon (int side, int meta)
    {
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
	    if (meta == 1)
	        return 0;
        return meta;
    }
	
	public int idDropped(int par1, Random par2Random, int par3)
    {
	    if (par1 == 1)
	        return Item.goldNugget.itemID;
        return this.blockID;
    }
	
	@Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 6; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
