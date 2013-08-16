package tconstruct.blocks;

import java.util.List;

import tconstruct.TConstruct;
import tconstruct.blocks.logic.GlowstoneAggregator;
import tconstruct.common.TProxyCommon;
import tconstruct.crystal.TheftValueTracker;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.blocks.InventoryBlock;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Aggregator extends InventoryBlock
{
    public String[] textureNames = { "glowstone_top", "glowstone_top_inactive", "glowstone_side", "glowstone_bottom" };
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
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:machines/aggregator_" + textureNames[i]);
        }
    }

    @Override
    public Icon getBlockTexture (IBlockAccess iblockaccess, int x, int y, int z, int side)
    {
        if (side == 1)
        {
            GlowstoneAggregator logic = (GlowstoneAggregator) iblockaccess.getBlockTileEntity(x, y, z);
            if (logic.getActive())
                return icons[0];
            else
                return icons[1];
        }
        return this.getIcon(side, iblockaccess.getBlockMetadata(x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        if (side == 0)
            return icons[3];
        if (side == 1)
            return icons[1];
        return icons[2];
        //return icons[meta];
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        if (!world.isRemote)
            TheftValueTracker.updateCrystallinity(world.provider.dimensionId, x, z, 4);
    }

    @Override
    public void breakBlock (World world, int x, int y, int z, int par5, int par6)
    {
        super.breakBlock(world, x, y, z, par5, par6);
        TheftValueTracker.updateCrystallinity(world.provider.dimensionId, x, z, -4);
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
        return TProxyCommon.glowstoneAggregatorID;
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

    public void updateCrystalValue (World world, int x, int y, int z)
    {
        GlowstoneAggregator logic = (GlowstoneAggregator) world.getBlockTileEntity(x, y, z);
        logic.updateCrystalValue();
    }
}
