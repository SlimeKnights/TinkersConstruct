package tconstruct.library.blocks;

import java.util.Random;

import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IFacingLogic;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class InventoryBlock extends BlockContainer
{
    protected Random rand = new Random();

    protected InventoryBlock(int id, Material material)
    {
        super(id, material);
    }

    /* Logic backend */
    public TileEntity createNewTileEntity (World var1)
    {
        return null;
    }

    public abstract TileEntity createTileEntity (World world, int metadata);

    public abstract Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer);

    public abstract Object getModInstance ();

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
    {
        if (player.isSneaking())
            return false;

        Integer integer = getGui(world, x, y, z, player);
        if (integer == null || integer == -1)
        {
            return false;
        }
        else
        {
            if (!world.isRemote)
                player.openGui(getModInstance(), integer, world, x, y, z);
            return true;
        }
    }

    /* Inventory */

    @Override
    public void breakBlock (World par1World, int x, int y, int z, int blockID, int meta)
    {
        TileEntity te = par1World.getBlockTileEntity(x, y, z);

        if (te != null && te instanceof InventoryLogic)
        {
            InventoryLogic logic = (InventoryLogic) te;
            logic.removeBlock();
            for (int iter = 0; iter < logic.getSizeInventory(); ++iter)
            {
                ItemStack stack = logic.getStackInSlot(iter);

                if (stack != null && logic.canDropInventorySlot(iter))
                {
                    float jumpX = rand.nextFloat() * 0.8F + 0.1F;
                    float jumpY = rand.nextFloat() * 0.8F + 0.1F;
                    float jumpZ = rand.nextFloat() * 0.8F + 0.1F;

                    while (stack.stackSize > 0)
                    {
                        int itemSize = rand.nextInt(21) + 10;

                        if (itemSize > stack.stackSize)
                        {
                            itemSize = stack.stackSize;
                        }

                        stack.stackSize -= itemSize;
                        EntityItem entityitem = new EntityItem(par1World, (double) ((float) x + jumpX), (double) ((float) y + jumpY), (double) ((float) z + jumpZ), new ItemStack(stack.itemID,
                                itemSize, stack.getItemDamage()));

                        if (stack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                        }

                        float offset = 0.05F;
                        entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
                        entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
                        entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
                        par1World.spawnEntityInWorld(entityitem);
                    }
                }
            }
        }

        super.breakBlock(par1World, x, y, z, blockID, meta);
    }

    /* Placement */

    int side = -1;

    //This class does not have an actual block placed in the world
    @Override
    public int onBlockPlaced (World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
    {
        this.side = side;
        return meta;
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack)
    {
        TileEntity logic = world.getBlockTileEntity(x, y, z);
        if (logic instanceof IFacingLogic)
        {
            IFacingLogic direction = (IFacingLogic) logic;
            if (side != -1)
            {
                direction.setDirection(side);
                side = -1;
            }
            if (entityliving == null)
            {
                direction.setDirection(0F, 0F, null);
            }
            else
            {
                direction.setDirection(entityliving.rotationYaw * 4F, entityliving.rotationPitch, entityliving);
            }
        }

        if (logic instanceof InventoryLogic)
        {
            InventoryLogic inv = (InventoryLogic) logic;
            inv.placeBlock(entityliving, stack);
            if (stack.hasDisplayName())
            {
                inv.setInvName(stack.getDisplayName());
            }
        }
    }

    public static boolean isActive (IBlockAccess world, int x, int y, int z)
    {
        TileEntity logic = world.getBlockTileEntity(x, y, z);
        if (logic instanceof IActiveLogic)
        {
            return ((IActiveLogic) logic).getActive();
        }
        return false;
    }

    public int damageDropped (int meta)
    {
        return meta;
    }

    /* Textures */
    public Icon[] icons;

    public abstract String[] getTextureNames ();

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }
}
