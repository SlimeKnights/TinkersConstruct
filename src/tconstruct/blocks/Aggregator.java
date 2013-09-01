package tconstruct.blocks;

import java.util.List;

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
import tconstruct.TConstruct;
import tconstruct.blocks.logic.LightAggregator;
import tconstruct.common.TProxyCommon;
import tconstruct.crystal.TheftValueTracker;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.blocks.InventoryBlock;
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
        //setLightValue(1.0f);
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
            LightAggregator logic = (LightAggregator) iblockaccess.getBlockTileEntity(x, y, z);
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
        /*int range = 9;
        for (int xPos = -range; xPos <= range; xPos++)
        {
            for (int yPos = -range; yPos <= range; yPos++)
            {
                for (int zPos = -range; zPos <= range; zPos++)
                {
                    if (Math.abs(xPos) + Math.abs(yPos) + Math.abs(zPos) <= range)
                    {
                        Block block = Block.blocksList[world.getBlockId(x + xPos, y + yPos, z + zPos)];
                        if (block == null || block.isAirBlock(world, x + xPos, y + yPos, z + zPos))
                            world.setBlock(x + xPos, y + yPos, z + zPos, TContent.darkBlock.blockID, 1, 3);
                    }
                }
            }
        }*/
        /*if (!world.isRemote)
            Crystallinity.updateCrystallinity(world.provider.dimensionId, x, z, 4);*/
    }

    @Override
    public void breakBlock (World world, int x, int y, int z, int par5, int par6)
    {
        super.breakBlock(world, x, y, z, par5, par6);
        /*int range = 20;
        for (int xPos = -range; xPos <= range; xPos++)
        {
            for (int yPos = -range; yPos <= range; yPos++)
            {
                for (int zPos = -range; zPos <= range; zPos++)
                {
                    if (Math.abs(xPos) + Math.abs(yPos) + Math.abs(zPos) <= range)
                    {
                        Block block = Block.blocksList[world.getBlockId(x + xPos, y + yPos, z + zPos)];
                        if (block == TContent.darkBlock || block == TContent.lightCrystalBase)
                            world.setBlock(x + xPos, y + yPos, z + zPos, 0, 0, 3);
                    }
                }
            }
        }*/
        //Crystallinity.updateCrystallinity(world.provider.dimensionId, x, z, -4);
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
        return new LightAggregator();
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

    /*public void updateCrystalValue (World world, int x, int y, int z)
    {
        GlowstoneAggregator logic = (GlowstoneAggregator) world.getBlockTileEntity(x, y, z);
        logic.updateCrystalValue();
    }*/
}
