package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.*;
import mantle.blocks.abstracts.*;
import mantle.blocks.iface.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.smeltery.SmelteryProxyCommon;
import tconstruct.smeltery.logic.*;
import tconstruct.smeltery.model.SmelteryRender;
import tconstruct.util.config.PHConstruct;

public class SmelteryBlock extends InventoryBlock
{
    Random rand;
    String texturePrefix = "";

    public SmelteryBlock()
    {
        super(Material.rock);
        setHardness(3F);
        setResistance(20F);
        setStepSound(soundTypeMetal);
        rand = new Random();
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setBlockName("tconstruct.Smeltery");
    }

    public SmelteryBlock(String prefix)
    {
        this();
        texturePrefix = prefix;
    }

    /* Rendering */

    @Override
    public int getRenderType ()
    {
        return PHConstruct.newSmeltery ? 0 : SmelteryRender.smelteryModel;
    }

    @Override
    public String[] getTextureNames ()
    {
        String[] textureNames = { "smeltery_side", "smeltery_inactive", "smeltery_active", "drain_side", "drain_out", "drain_basin", "searedbrick", "searedstone", "searedcobble", "searedpaver", "searedbrickcracked", "searedroad", "searedbrickfancy", "searedbricksquare", "searedcreeper" };

        if (!texturePrefix.equals(""))
            for (int i = 0; i < textureNames.length; i++)
                textureNames[i] = texturePrefix + "_" + textureNames[i];

        return textureNames;
    }

    @Override
    public String getTextureDomain (int textureNameIndex)
    {
        return "tinker";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        if (meta < 2)
        {
            int sideTex = side == 3 ? 1 : 0;
            return icons[sideTex + meta * 3];
        }
        else if (meta == 2)
        {
            return icons[6];
        }
        else if (meta == 11)
        {
            if (side == 0 || side == 1)
                return icons[9];
        }

        return icons[3 + meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity logic = world.getTileEntity(x, y, z);
        short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) // Smeltery
        {
            if (side == direction)
            {
                if (isActive(world, x, y, z))
                {
                    return icons[2];
                }
                else
                {
                    return icons[1];
                }
            }
            else
            {
                return icons[0];
            }
        }
        if (meta == 1) // Drain
        {
            if (side == direction)
                return icons[5];
            else if (side / 2 == direction / 2)
                return icons[4];
            else
                return icons[3];
        }
        else if (meta == 2)
        {
            return icons[6];
        }
        else if (meta == 11)
        {
            if (side == 0 || side == 1)
                return icons[9];
        }
        return icons[3 + meta];

    }

    /*
     * @Override public int getRenderBlockPass() { return 1; }
     */

    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }

    @Override
    public int quantityDropped (Random random)
    {
        return 1;
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        // return -1;
        return SmelteryProxyCommon.smelteryGuiID;
    }

    @Override
    public void randomDisplayTick (World world, int x, int y, int z, Random random)
    {
        if (isActive(world, x, y, z))
        {
            TileEntity logic = world.getTileEntity(x, y, z);
            byte face = 0;
            if (logic instanceof IFacingLogic)
                face = ((IFacingLogic) logic).getRenderDirection();
            float f = (float) x + 0.5F;
            float f1 = (float) y + 0.5F + (random.nextFloat() * 6F) / 16F;
            float f2 = (float) z + 0.5F;
            float f3 = 0.52F;
            float f4 = random.nextFloat() * 0.6F - 0.3F;
            switch (face)
            {
            case 4:
                world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                break;

            case 5:
                world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                break;

            case 2:
                world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                break;

            case 3:
                world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                break;
            }
        }
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        return !isActive(world, x, y, z) ? 0 : 9;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
    {
        if (player.isSneaking() || world.getBlockMetadata(x, y, z) != 0)
            return false;

        Integer integer = getGui(world, x, y, z, player);
        if (integer == null || integer == -1)
        {
            return false;
        }
        else
        {
            // world.markBlockForUpdate(x, y, z);
            player.openGui(getModInstance(), integer, world, x, y, z);
            return true;
        }
    }

    @Override
    public TileEntity createNewTileEntity (World world, int metadata)
    {
        switch (metadata)
        {
        case 0:
            if (PHConstruct.newSmeltery)
                return new AdaptiveSmelteryLogic();
            else
                return new SmelteryLogic();

        case 1:
            if (PHConstruct.newSmeltery)
                return new AdaptiveDrainLogic();
            else
                return new SmelteryDrainLogic();
        case 3:
            return null; // Furnace
        }
        return new MultiServantLogic();
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
        if (world.getBlockMetadata(x, y, z) == 0 && !PHConstruct.newSmeltery)
            onBlockPlacedElsewhere(world, x, y, z, entityliving);
    }

    public void onBlockPlacedElsewhere (World world, int x, int y, int z, EntityLivingBase entityliving)
    {
        SmelteryLogic logic = (SmelteryLogic) world.getTileEntity(x, y, z);
        logic.checkValidPlacement();
    }

    /*
     * @Override public void breakBlock (World world, int x, int y, int z, int
     * par5, int par6) //Don't drop inventory { world.removeBlockTileEntity(x,
     * y, z); }
     */

    @Override
    public void getSubBlocks (Item id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 12; iter++)
        {
            if (iter != 3)
                list.add(new ItemStack(id, 1, iter));
        }
    }

    /* Updating */
    @Override
    public void onNeighborBlockChange (World world, int x, int y, int z, Block block)
    {
        //System.out.println("Neighbor changed");
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof IServantLogic)
        {
            ((IServantLogic) logic).notifyMasterOfChange();
        }
        else if (logic instanceof IMasterLogic)
        {
            ((IMasterLogic) logic).notifyChange(null, x, y, z);
        }
    }

    @Override
    public void breakBlock (World world, int x, int y, int z, Block blockID, int meta)
    {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof IServantLogic)
        {
            ((IServantLogic) logic).notifyMasterOfChange();
        }
        super.breakBlock(world, x, y, z, blockID, meta);
    }

    //Comparator

    public boolean hasComparatorInputOverride ()
    {
        return true;
    }

    public int getComparatorInputOverride (World world, int x, int y, int z, int comparatorSide)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0)
        {
            if (PHConstruct.newSmeltery)
                return 0;
            else
                return Container.calcRedstoneFromInventory(((SmelteryLogic) world.getTileEntity(x, y, z)));
        }
        if (meta == 1)
        {
            if (PHConstruct.newSmeltery)
                return 0;
            else
                return ((SmelteryDrainLogic) world.getTileEntity(x, y, z)).comparatorStrength();
        }
        return 0;
    }
}