package mods.tinker.tconstruct.blocks;

import java.util.List;

import mods.tinker.tconstruct.blocks.logic.LavaTankLogic;
import mods.tinker.tconstruct.blocks.logic.PatternChestLogic;
import mods.tinker.tconstruct.client.block.TankRender;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.util.IServantLogic;
import mods.tinker.tconstruct.util.PHConstruct;
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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class LavaTankBlock extends BlockContainer
{
    public Icon[] icons;

    public LavaTankBlock(int id)
    {
        super(id, Material.rock);
        setHardness(12);
        setCreativeTab(TConstructRegistry.blockTab);
        setUnlocalizedName("TConstruct.LavaTank");
        setStepSound(Block.soundGlassFootstep);
    }

    public String[] getTextureNames ()
    {
        String[] textureNames = { "lavatank_side", "lavatank_top", "searedgague_top", "searedgague_side", "searedgague_bottom", "searedwindow_top", "searedwindow_side", "searedwindow_bottom" };

        return textureNames;
    }

    public void registerIcons (IconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
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
        //if (side == 0 && world.getBlockMetadata(x, y, z) == 0)
        //return super.shouldSideBeRendered(world, x, y, z, side);
        int bID = world.getBlockId(x, y, z);
        return bID == this.blockID ? false : super.shouldSideBeRendered(world, x, y, z, side);
        //return true;
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        TileEntity logic = world.getBlockTileEntity(x, y, z);
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
    public int getRenderType ()
    {
        return TankRender.tankModelID;
    }

    public Icon getIcon (int side, int meta)
    {
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
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
    {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (heldItem != null)
        {
            LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(player.getCurrentEquippedItem());
            LavaTankLogic logic = (LavaTankLogic) world.getBlockTileEntity(x, y, z);
            if (liquid != null)
            {
                int amount = logic.fill(0, liquid, false);
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
            else if (LiquidContainerRegistry.isBucket(heldItem))
            {
                ILiquidTank[] tanks = logic.getTanks(ForgeDirection.UNKNOWN);
                LiquidStack fillLiquid = tanks[0].getLiquid();
                ItemStack fillStack = LiquidContainerRegistry.fillLiquidContainer(fillLiquid, heldItem);
                if (fillStack != null)
                {
                    logic.drain(ForgeDirection.UNKNOWN, LiquidContainerRegistry.getLiquidForFilledItem(fillStack).amount, true);
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
                return stack.getItem().getContainerItemStack(stack);
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
        return createTileEntity(world, 0);
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 3; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    /* Data */
    public int damageDropped (int meta)
    {
        return meta;
    }

    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
        player.addExhaustion(0.025F);
        int meta = world.getBlockMetadata(x, y, z);
        ItemStack stack = new ItemStack(this.blockID, 1, meta);
        LavaTankLogic logic = (LavaTankLogic) world.getBlockTileEntity(x, y, z);
        LiquidStack liquid = logic.tank.getLiquid();
        if (liquid != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound liquidTag = new NBTTagCompound();
            liquid.writeToNBT(liquidTag);
            tag.setCompoundTag("Liquid", liquidTag);
            stack.setTagCompound(tag);
        }
        dropTankBlock(world, x, y, z, stack);
        
        return world.setBlockToAir(x, y, z);
    }
    
    protected void dropTankBlock(World world, int x, int y, int z, ItemStack stack)
    {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float f = 0.7F;
            double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }
    
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLiving living, ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound liquidTag = stack.getTagCompound().getCompoundTag("Liquid");
            if (liquidTag != null)
            {
                LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(liquidTag);
                LavaTankLogic logic = (LavaTankLogic) world.getBlockTileEntity(x, y, z);
                logic.tank.setLiquid(liquid);
            }
        }
    }

    /* Updates */
    public void onNeighborBlockChange (World world, int x, int y, int z, int nBlockID)
    {
        TileEntity logic = world.getBlockTileEntity(x, y, z);
        if (logic instanceof IServantLogic)
        {
            ((IServantLogic) logic).notifyMasterOfChange();
        }
    }
}
