package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.blocks.iface.IServantLogic;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.smeltery.logic.LavaTankLogic;
import tconstruct.smeltery.model.TankRender;

public class LavaTankBlock extends BlockContainer
{
    public IIcon[] icons;
    String texturePrefix = "";

    public LavaTankBlock()
    {
        super(Material.rock);
        setHardness(3F);
        setResistance(20F);
        setCreativeTab(TConstructRegistry.blockTab);
        this.setBlockName("TConstruct.LavaTank");
        setStepSound(Block.soundTypeGrass);
    }

    public LavaTankBlock(String prefix)
    {
        this();
        texturePrefix = prefix;
    }

    public String[] getTextureNames ()
    {
        String[] textureNames = { "lavatank_side", "lavatank_top", "searedgague_top", "searedgague_side", "searedgague_bottom", "searedwindow_top", "searedwindow_side", "searedwindow_bottom" };

        if (!texturePrefix.equals(""))
            for (int i = 0; i < textureNames.length; i++)
                textureNames[i] = texturePrefix + "_" + textureNames[i];

        return textureNames;
    }

    @Override
    public void registerBlockIcons (IIconRegister IIconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = IIconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 1;
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
    public boolean shouldSideBeRendered (IBlockAccess world, int x, int y, int z, int side)
    {
        Block bID = world.getBlock(x, y, z);
        return bID == this ? false : super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public boolean canRenderInPass (int pass)
    {
        TankRender.renderPass = pass;
        return true;
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic != null && logic instanceof LavaTankLogic)
            return ((LavaTankLogic) logic).getBrightness();
        return 0;
    }

    /*
     * @Override public int getRenderBlockPass() { return 1; }
     */

    @Override
    public int getRenderType ()
    {
        return TankRender.tankModelID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        if (meta >= 3)
            meta = 0;
        if (meta == 0)
        {
            if (side == 0 || side == 1)
            {
                return icons[1];
            }
            else
            {
                return icons[0];
            }
        }
        else
        {
            return icons[meta * 3 + getTextureIndex(side) - 1];
        }
    }

    public int getTextureIndex (int side)
    {
        if (side == 0)
            return 2;
        if (side == 1)
            return 0;

        return 1;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new LavaTankLogic();
    }

    @Override
    public boolean onBlockActivated (World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        ItemStack current = entityplayer.inventory.getCurrentItem();
        if (current != null)
        {
            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(current);
            LavaTankLogic logic = (LavaTankLogic) world.getTileEntity(i, j, k);
            if (liquid != null)
            {
                int amount = logic.fill(ForgeDirection.UNKNOWN, liquid, false);
                if (amount == liquid.amount)
                {
                    logic.fill(ForgeDirection.UNKNOWN, liquid, true);
                    if (!entityplayer.capabilities.isCreativeMode)
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, consumeItem(current));
                    return true;
                }
                else
                    return true;
            }
            else if (FluidContainerRegistry.isBucket(current))
            {
                FluidTankInfo[] tanks = logic.getTankInfo(ForgeDirection.UNKNOWN);
                FluidStack fillFluid = tanks[0].fluid;// getFluid();
                ItemStack fillStack = FluidContainerRegistry.fillFluidContainer(fillFluid, current);
                if (fillStack != null)
                {
                    logic.drain(ForgeDirection.UNKNOWN, FluidContainerRegistry.getFluidForFilledItem(fillStack).amount, true);
                    if (!entityplayer.capabilities.isCreativeMode)
                    {
                        if (current.stackSize == 1)
                        {
                            entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, fillStack);
                        }
                        else
                        {
                            entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, consumeItem(current));

                            if (!entityplayer.inventory.addItemStackToInventory(fillStack))
                            {
                                entityplayer.dropPlayerItemWithRandomChoice(fillStack, false);
                            }
                        }
                    }
                    return true;
                }
                else
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static ItemStack consumeItem (ItemStack stack)
    {
        if (stack.stackSize == 1)
        {
            if (stack.getItem().hasContainerItem())
                return stack.getItem().getContainerItem(stack);
            else
                return null;
        }
        else
        {
            stack.splitStack(1);

            return stack;
        }
    }

    @Override
    public TileEntity createNewTileEntity (World world, int test)
    {
        return createTileEntity(world, 0);
    }

    @Override
    public void getSubBlocks (Item id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 3; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    /* Data */
    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }

    /* Updates */
    public void onNeighborBlockChange (World world, int x, int y, int z, Block nBlockID)
    {
        TileEntity logic = world.getTileEntity(x, y, z);
        if (logic instanceof IServantLogic)
        {
            ((IServantLogic) logic).notifyMasterOfChange();
        }
    }

    @Override
    public boolean removedByPlayer (World world, EntityPlayer player, int x, int y, int z)
    {
        player.addExhaustion(0.025F);
        int meta = world.getBlockMetadata(x, y, z);
        ItemStack stack = new ItemStack(this, 1, meta);
        LavaTankLogic logic = (LavaTankLogic) world.getTileEntity(x, y, z);
        FluidStack liquid = logic.tank.getFluid();
        if (liquid != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound liquidTag = new NBTTagCompound();
            liquid.writeToNBT(liquidTag);
            tag.setTag("Fluid", liquidTag);
            stack.setTagCompound(tag);
        }
        if (!player.capabilities.isCreativeMode || player.isSneaking())
            dropTankBlock(world, x, y, z, stack);

        return world.setBlockToAir(x, y, z);
    }

    protected void dropTankBlock (World world, int x, int y, int z, ItemStack stack)
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

    @Override
    public void harvestBlock (World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {

    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase living, ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound liquidTag = stack.getTagCompound().getCompoundTag("Fluid");
            if (liquidTag != null)
            {
                FluidStack liquid = FluidStack.loadFluidStackFromNBT(liquidTag);
                LavaTankLogic logic = (LavaTankLogic) world.getTileEntity(x, y, z);
                logic.tank.setFluid(liquid);
            }
        }
    }

    //Comparator

    @Override
    public boolean hasComparatorInputOverride ()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride (World world, int x, int y, int z, int comparatorSide)
    {
        return getTankLogic(world, x, y, z).comparatorStrength();
    }

    public static LavaTankLogic getTankLogic (IBlockAccess blockAccess, int par1, int par2, int par3)
    {
        return (LavaTankLogic) blockAccess.getTileEntity(par1, par2, par3);
    }
}