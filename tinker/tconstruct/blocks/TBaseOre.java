package tinker.tconstruct.blocks;
import java.util.List;
import java.util.Random;

import tinker.tconstruct.TConstruct;
import tinker.tconstruct.TConstructContent;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TBaseOre extends Block
{
    public TBaseOre(int id, int tex)
    {
        super(id, tex, Material.rock);
        enableStats = false;
        setCreativeTab(TConstruct.blockTab);
    }
    
    public int damageDropped(int meta)
    {
        return meta;
    }
    
    /*@Override
    public int idDropped(int par1, Random par2Random, int par3)
    {
    	return TConstructContent.ores.blockID;
    }*/
    
    public int getBlockTextureFromSideAndMetadata(int side, int meta)
    {
        return blockIndexInTexture + meta;
    }
    
    public String getTextureFile()
    {
        return TConstructContent.blockTexture;
    }
    
    @Override
    public void getSubBlocks(int id, CreativeTabs tab, List list)
    {
    	for (int iter = 0; iter < 2; iter++)
    	{
    		list.add(new ItemStack(id, 1, iter));
    	}
    }
}
