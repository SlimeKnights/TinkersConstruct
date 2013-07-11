package mods.tinker.tconstruct.blocks;

import java.util.Random;

import mods.tinker.tconstruct.blocks.logic.GolemPedestalLogic;
import mods.tinker.tconstruct.library.blocks.InventoryBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GolemPedestalBlock extends InventoryBlock
{
    /*public static int texTop;
    public static int texTopp;
    public static int texSide;
    public static int texBottom;*/

    public GolemPedestalBlock(int i)
    {
        super(i, Material.wood);
        /*texTop = ModLoader.addOverride("/terrain.png", "/GGE/pedestaltop.png");
        texTopp = ModLoader.addOverride("/terrain.png", "/GGE/pedestaltopp.png");
        texSide = ModLoader.addOverride("/terrain.png", "/GGE/pedestalside.png");
        texBottom = ModLoader.addOverride("/terrain.png", "/GGE/pedestalbottom.png");*/
    }

    /*public int getIcon(int i, int j)
    {
        if (i == 1)
        {
            return j == 0 ? texTop : texTopp;
        }
        if (i == 0)
        {
            return texBottom;
        }
        if (i == 2 || i == 4)
        {
            return texSide;
        }
        else
        {
            return texSide;
        }
    }*/

    /*public int idDropped(int i, Random random, int j)
    {
        return super.idDropped(i, random, j);
    }*/

    /*public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving)
    {
        super.onBlockPlacedBy(world, i, j, k, entityliving);
        ItemStack itemstack = ((EntityPlayer)entityliving).getCurrentEquippedItem();
        world.setBlockMetadata(i, j, k, itemstack.getItemDamage());
    }*/

    public void onBlockRemoval (World world, int i, int j, int k)
    {
        Random random = new Random();
        GolemPedestalLogic tileentitygolempedestal = (GolemPedestalLogic) world.getBlockTileEntity(i, j, k);
        if (tileentitygolempedestal != null)
        {
            label0: for (int l = 0; l < tileentitygolempedestal.getSizeInventory(); l++)
            {
                ItemStack itemstack = tileentitygolempedestal.getStackInSlot(l);
                if (itemstack == null)
                {
                    continue;
                }
                float f = random.nextFloat() * 0.8F + 0.1F;
                float f1 = random.nextFloat() * 0.8F + 0.1F;
                float f2 = random.nextFloat() * 0.8F + 0.1F;
                do
                {
                    if (itemstack.stackSize <= 0)
                    {
                        continue label0;
                    }
                    int i1 = random.nextInt(21) + 10;
                    if (i1 > itemstack.stackSize)
                    {
                        i1 = itemstack.stackSize;
                    }
                    itemstack.stackSize -= i1;
                    EntityItem entityitem = new EntityItem(world, (float) i + f, (float) j + f1, (float) k + f2, new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));
                    float f3 = 0.05F;
                    entityitem.motionX = (float) random.nextGaussian() * f3;
                    entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
                    entityitem.motionZ = (float) random.nextGaussian() * f3;
                    world.spawnEntityInWorld(entityitem);
                } while (true);
            }
        }
        //super.onBlockRemoval(world, i, j, k);
    }

    public boolean blockActivated (World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        /*ItemStack itemstack = entityplayer.getCurrentEquippedItem();
        if (world.isRemote)
        {
            return true;
        }
        if (itemstack == null || itemstack != null && (itemstack.itemID >= 256 || itemstack.itemID == 0 || itemstack.itemID == Block.slowSand.blockID))
        {
            ModLoader.openGUI(entityplayer, new GuiGolemPedestal(entityplayer.inventory, world, i, j, k));
            return true;
        }
        else
        {
            return false;
        }*/
        return false;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new GolemPedestalLogic();
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getModInstance ()
    {
        return null;
    }

    @Override
    public String[] getTextureNames ()
    {
        return null;
    }
}
