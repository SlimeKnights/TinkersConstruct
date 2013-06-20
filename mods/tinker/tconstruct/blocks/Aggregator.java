package mods.tinker.tconstruct.blocks;

import java.util.List;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.GlowstoneAggregator;
import mods.tinker.tconstruct.common.TProxyCommon;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.blocks.InventoryBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Aggregator extends InventoryBlock
{
    public String[] textureNames = { "glowstone_top", "glowstone_top_inactive", "glowstone_side", "glowstone_bottom"};
    public Icon[] icons;
    public Aggregator(int id)
    {
        super(id, Material.iron);
        setHardness(2.0f);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setLightValue(1.0f);
    }

    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:machines/aggregator_"+textureNames[i]);
        }
    }
    
    /*@Override
    public Icon getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side)
    {        
        if (iblockaccess instanceof World)
        {
            System.out.println("Rawr!");
            World world = (World) iblockaccess;
            if (world.canBlockSeeTheSky(x, y, z))
            {
                int level = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) - world.skylightSubtracted;
                System.out.println("Level: "+level);
                if (level < 12)
                    return icons[1];
            }
        }
        return this.getIcon(side, iblockaccess.getBlockMetadata(x, y, z));        
    }*/
    
    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        if (side == 0)
            return icons[3];
        if (side == 1)
            return icons[0];
        return icons[2];
        //return icons[meta];
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(id, 1, 0));
        /*for (int iter = 0; iter < icons.length; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }*/
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new GlowstoneAggregator();
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        return TProxyCommon.glowstoneAggregatorGui;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public String[] getTextureNames ()
    {
        return textureNames;
    }
}
