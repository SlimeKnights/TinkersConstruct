package tconstruct.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import tconstruct.blocks.logic.LavaTankLogic;
import tconstruct.client.block.TankRender;
import tconstruct.library.TConstructRegistry;
import mantle.blocks.iface.IServantLogic;
import mantle.world.WorldHelper;

public class LavaTankBlock extends BlockContainer
{
    public IIcon[] icons;
    String texturePrefix = "";

    public LavaTankBlock()
    {
        super(Material.field_151576_e);
        func_149711_c(3F);
        func_149752_b(20F);
        func_149647_a(TConstructRegistry.blockTab);
        func_149663_c("TConstruct.LavaTank");
        field_149762_H = Block.field_149778_k;
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

    public void registerIcons (IIconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    public int func_149701_w ()
    {
        return 1;
    }

    @Override
    public boolean func_149662_c ()
    {
        return false;
    }

    @Override
    public boolean func_149686_d ()
    {
        return false;
    }

    @Override
    public boolean func_149646_a (IBlockAccess world, int x, int y, int z, int side)
    {
        //if (side == 0 && world.getBlockMetadata(x, y, z) == 0)
        //return super. func_149646_a(world, x, y, z, side);
        Block b = world.func_147439_a(x, y, z);
        return b == (Block)this ? false : super. func_149646_a(world, x, y, z, side);
        //return true;
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        TileEntity logic = world.func_147438_o(x, y, z);
        if (logic != null && logic instanceof LavaTankLogic)
            return ((LavaTankLogic) logic).getBrightness();
        return 0;
    }

    /*@Override
    public int getRenderBlockPass()
    {
    	return 1;
    }*/

    @Override
    public int func_149645_b ()
    {
        return TankRender.tankModelID;
    }

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
    public TileEntity func_149915_a (World world, int metadata)
    {
        return new LavaTankLogic();
    }

    @Override
    public boolean func_149727_a (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
    {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (heldItem != null)
        {
            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(player.getCurrentEquippedItem());
            LavaTankLogic logic = (LavaTankLogic) world.func_147438_o(x, y, z);
            if (liquid != null)
            {
                int amount = logic.fill(ForgeDirection.UNKNOWN, liquid, false);
                if (amount == liquid.amount)
                {
                    logic.fill(ForgeDirection.UNKNOWN, liquid, true);
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(heldItem));
                    return true;
                }
                else
                    return true;
            }
            else if (FluidContainerRegistry.isBucket(heldItem))
            {
                FluidTankInfo[] tanks = logic.getTankInfo(ForgeDirection.UNKNOWN);
                FluidStack fillFluid = tanks[0].fluid;//getFluid();
                ItemStack fillStack = FluidContainerRegistry.fillFluidContainer(fillFluid, heldItem);
                if (fillStack != null)
                {
                    logic.drain(ForgeDirection.UNKNOWN, FluidContainerRegistry.getFluidForFilledItem(fillStack).amount, true);
                    if (!player.capabilities.isCreativeMode)
                    {
                        if (heldItem.stackSize == 1)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, fillStack);
                        }
                        else
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(heldItem));

                            if (!player.inventory.addItemStackToInventory(fillStack))
                            {
                                player.dropPlayerItem(fillStack);
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
    public TileEntity createNewTileEntity (World world)
    {
        return func_149915_a(world, 0);
    }

    @Override
    public void func_149666_a (Item i, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 3; iter++)
        {
            list.add(new ItemStack(i, 1, iter));
        }
    }

    /* Data */
    public int damageDropped (int meta)
    {
        return meta;
    }

    /* Updates */
    public void onNeighborBlockChange (World world, int x, int y, int z, int nBlockID)
    {
        TileEntity logic = world.func_147438_o(x, y, z);
        if (logic instanceof IServantLogic)
        {
            ((IServantLogic) logic).notifyMasterOfChange();
        }
    }

    @Override
    public boolean removeBlockByPlayer (World world, EntityPlayer player, int x, int y, int z)
    {
        player.addExhaustion(0.025F);
        int meta = world.getBlockMetadata(x, y, z);
        ItemStack stack = new ItemStack(this, 1, meta);
        LavaTankLogic logic = (LavaTankLogic) world.func_147438_o(x, y, z);
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

        return WorldHelper.setBlockToAirBool(world, x, y, z);
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
            entityitem.field_145804_b = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    public void harvestBlock (World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {

    }

    @Override
    public void func_149689_a (World world, int x, int y, int z, EntityLivingBase living, ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound liquidTag = stack.getTagCompound().getCompoundTag("Fluid");
            if (liquidTag != null)
            {
                FluidStack liquid = FluidStack.loadFluidStackFromNBT(liquidTag);
                LavaTankLogic logic = (LavaTankLogic) world.func_147438_o(x, y, z);
                logic.tank.setFluid(liquid);
            }
        }
    }
}
