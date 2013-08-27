package mods.tinker.tconstruct.blocks;

import java.util.List;

import mods.tinker.tconstruct.blocks.logic.CrystalLogic;
import mods.tinker.tconstruct.blocks.logic.LightAggregator;
import mods.tinker.tconstruct.client.block.CrystalBlockRender;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.crystal.Crystallinity;
import mods.tinker.tconstruct.crystal.Crystallinity.CrystalType;
import mods.tinker.tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CrystalBlock extends BlockContainer
{
    String[] textureNames = { "lightstone" };
    Icon[] icons;

    public CrystalBlock(int id)
    {
        super(id, Material.glass);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        return icons[0];
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    public int getRenderType ()
    {
        return CrystalBlockRender.model;
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 16; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:machines/" + textureNames[i]);
        }
    }

    @Override
    public TileEntity createNewTileEntity (World world)
    {
        return null;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new CrystalLogic();
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        //if (world.getBlockMetadata(x, y, z) == 0)
        {
            TileEntity logic = world.getBlockTileEntity(x, y, z);

            if (logic != null && logic instanceof CrystalLogic)
            {
                if (((CrystalLogic) logic).getActive())
                {
                    return 15;
                }
            }
        }
        return super.getLightValue(world, x, y, z);
    }

    /*@Override
    public void breakBlock (World world, int x, int y, int z, int par5, int meta)
    {
        if (meta <= 4)
        {
            TileEntity logic = world.getBlockTileEntity(x, y, z);

            if (logic != null && logic instanceof CrystalLogic)
            {
                
            }
        }
    }*/

    public boolean removeBlockByPlayer (World world, EntityPlayer player, int x, int y, int z)
    {
        player.addExhaustion(0.025F);
        int meta = world.getBlockMetadata(x, y, z);
        if (meta <= 5)
        {
            ItemStack stack = new ItemStack(this.blockID, 1, 0);
            CrystalLogic logic = (CrystalLogic) world.getBlockTileEntity(x, y, z);
            NBTTagCompound tag = new NBTTagCompound();
            int value = logic.getCrystalValue();
            tag.setInteger("Value", value);
            Crystallinity.updateTheft(world.provider.dimensionId, x, z, -value, CrystalType.Light);
            tag.setInteger("Value", 120);
            stack.setTagCompound(tag);

            if (logic.growing())
            {
                TileEntity aggregator = world.getBlockTileEntity(x, y - 1, z);
                if (aggregator instanceof LightAggregator)
                {
                    ((LightAggregator) aggregator).harvestCrystal();
                }
            }

            for (int i = 0; i < getCrystalHeight(value); i++)
                world.setBlockToAir(x, y + i, z);

            if (!player.capabilities.isCreativeMode || player.isSneaking())
                dropBlock(world, x, y, z, stack);
        }
        else
        {
            Block below = Block.blocksList[world.getBlockId(x, y - 1, z)];
            if (below == TContent.lightCrystalBase)
            {
                below.removeBlockByPlayer(world, player, x, y - 1, z);
            }
            else
                world.setBlockToAir(x, y, z);
        }

        return true;//world.setBlockToAir(x, y, z);
    }

    public static int getCrystalHeight (int crystalValue)
    {
        if (crystalValue >= 440)
            return 4;
        if (crystalValue >= 224)
            return 3;
        if (crystalValue >= 80)
            return 2;

        return 1;
    }

    protected void dropBlock (World world, int x, int y, int z, ItemStack stack)
    {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float f = 0.7F;
            double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    public void harvestBlock (World world, EntityPlayer player, int x, int y, int z, int meta)
    {
        /*if (meta > 4)
            super.harvestBlock(world, player, x, y, z, meta);*/
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLiving living, ItemStack stack)
    {
        CrystalLogic logic = (CrystalLogic) world.getBlockTileEntity(x, y, z);
        logic.setActive(true);
        if (stack.hasTagCompound())
        {
            int value = stack.getTagCompound().getInteger("Value");
            Crystallinity.updateTheft(world.provider.dimensionId, x, z, value, CrystalType.Light);
            logic.setCrystalValue(value);
        }
    }
}
