package tconstruct.blocks;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import mantle.blocks.BlockUtils;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.LandmineExplodeLogic;
import tconstruct.blocks.logic.TileEntityLandmine;
import tconstruct.client.block.RenderLandmine;
import tconstruct.common.TProxyCommon;
import tconstruct.util.landmine.Helper;

/**
 * 
 * @author fuj1n
 * 
 */
public class BlockLandmine extends BlockContainer
{

    // Should explode when broken instead of dropping items(may not actually work
    boolean explodeOnBroken = false;

    public BlockLandmine()
    {
        super(Material.field_151590_u);
        this.func_149675_a(true);
        this.func_149676_a(0.0625F, 0.0F, 0.0625F, 1.0F - 0.0625F, 0.0625F, 1.0F - 0.0625F);
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        if (world.func_147439_a(x, y, z) == (Block) this && world.func_147438_o(x, y, z) instanceof TileEntityLandmine)
        {
            TileEntityLandmine te = (TileEntityLandmine) world.func_147438_o(x, y, z);

            if (te != null)
            {
                if (te.getStackInSlot(3) != null && te.getStackInSlot(3).getItem() instanceof ItemBlock)
                {
                    return (BlockUtils.getBlockFromItem(te.getStackInSlot(3).getItem())).func_149750_m();
                }
            }
        }
        return super.getLightValue(world, x, y, z);
    }

    @Override
    public int func_149738_a (World par1World)
    {
        return 20;
    }

    @Override
    public IIcon func_149673_e (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        TileEntityLandmine te = (TileEntityLandmine) par1IBlockAccess.func_147438_o(par2, par3, par4);

        ItemStack camo = te.getStackInSlot(3);
        if (camo != null)
        {
            return BlockUtils.getBlockFromItem(camo.getItem()).func_149691_a(par5, camo.getItemDamage());
        }
        else
        {
            return this.func_149691_a(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
        }
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
    public int func_149645_b ()
    {
        return RenderLandmine.model;
    }

    @Override
    public void func_149699_a (World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        super.func_149699_a(par1World, par2, par3, par4, par5EntityPlayer);

        if (this.explodeOnBroken)
        {
            checkExplosion(par1World, par2, par3, par4, true);
        }
    }

    @Override
    public void func_149651_a (IIconRegister par1IconRegister)
    {
        this.field_149761_L = par1IconRegister.registerIcon("tinker:Landmine");
    }

    @Override
    public boolean func_149727_a (World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        par5EntityPlayer.openGui(TConstruct.instance, TProxyCommon.landmineID, par1World, par2, par3, par4);
        return true;
    }

    @Override
    public void func_149749_a (World par1World, int par2, int par3, int par4, Block par5Block, int par6)
    {
        TileEntityLandmine tileentity = (TileEntityLandmine) par1World.func_147438_o(par2, par3, par4);

        int metadata = par1World.getBlockMetadata(par2, par3, par4);

        if (tileentity != null && (!explodeOnBroken || !hasItems(par1World, par2, par3, par4)) && par6 != 193 && !tileentity.isExploding)
        {
            if (this != null)
            {
                ItemStack is = new ItemStack(this, 1, func_149692_a(tileentity.triggerType));
                if (tileentity.func_145818_k_())
                {
                    is.func_151001_c(tileentity.func_145825_b());
                }
                func_149642_a(par1World, par2, par3, par4, new ItemStack(this, 1, func_149692_a(tileentity.triggerType)));
            }

            for (int j1 = 0; j1 < tileentity.getSizeInventory(); ++j1)
            {
                ItemStack itemstack = tileentity.getStackInSlot(j1);

                if (itemstack != null)
                {
                    while (itemstack.stackSize > 0)
                    {

                        int ss = itemstack.stackSize;
                        itemstack.stackSize -= ss;
                        EntityItem entityitem = new EntityItem(par1World, (double) ((float) par2), (double) ((float) par3), (double) ((float) par4), new ItemStack(itemstack.getItem(), ss,
                                itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (double) (f3);
                        entityitem.motionY = (double) (f3 + 0.2F);
                        entityitem.motionZ = (double) (f3);
                        par1World.spawnEntityInWorld(entityitem);
                    }
                }
            }

            par1World.func_147449_b(par2, par3, par4, par5Block);
        }
        else if (explodeOnBroken)
        {
            checkExplosion(par1World, par2, par3, par4, true);
        }

        super.func_149749_a(par1World, par2, par3, par4, par5Block, par6);
    }

    @Override
    public void func_149666_a (Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
        par3List.add(new ItemStack(par1, 1, 3));
    }

    @Override
    public boolean func_149655_b (IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return true;
    }

    @Override
    public boolean func_149707_d (World par1World, int par2, int par3, int par4, int par5)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(par5);
        return (dir == DOWN && par1World.isSideSolid(par2, par3 + 1, par4, DOWN)) || (dir == UP && par1World.isSideSolid(par2, par3 - 1, par4, UP))
                || (dir == NORTH && par1World.isSideSolid(par2, par3, par4 + 1, NORTH)) || (dir == SOUTH && par1World.isSideSolid(par2, par3, par4 - 1, SOUTH))
                || (dir == WEST && par1World.isSideSolid(par2 + 1, par3, par4, WEST)) || (dir == EAST && par1World.isSideSolid(par2 - 1, par3, par4, EAST));
    }

    @Override
    public boolean func_149742_c (World par1World, int par2, int par3, int par4)
    {
        return par1World.isSideSolid(par2 - 1, par3, par4, EAST) || par1World.isSideSolid(par2 + 1, par3, par4, WEST) || par1World.isSideSolid(par2, par3, par4 - 1, SOUTH)
                || par1World.isSideSolid(par2, par3, par4 + 1, NORTH) || par1World.isSideSolid(par2, par3 - 1, par4, UP) || par1World.isSideSolid(par2, par3 + 1, par4, DOWN);
    }

    @Override
    public int func_149660_a (World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
    {
        int j1 = par9 & 8;
        int k1 = par9 & 7;
        byte b0 = -1;

        if (par5 == 0 && par1World.isSideSolid(par2, par3 + 1, par4, DOWN))
        {
            b0 = 0;
        }

        if (par5 == 1 && par1World.isSideSolid(par2, par3 - 1, par4, UP))
        {
            b0 = 5;
        }

        if (par5 == 2 && par1World.isSideSolid(par2, par3, par4 + 1, NORTH))
        {
            b0 = 4;
        }

        if (par5 == 3 && par1World.isSideSolid(par2, par3, par4 - 1, SOUTH))
        {
            b0 = 3;
        }

        if (par5 == 4 && par1World.isSideSolid(par2 + 1, par3, par4, WEST))
        {
            b0 = 2;
        }

        if (par5 == 5 && par1World.isSideSolid(par2 - 1, par3, par4, EAST))
        {
            b0 = 1;
        }

        return b0 + j1;
    }

    @Override
    public void func_149689_a (World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4);
        int i1 = l & 7;
        int j1 = l & 8;

        if (i1 == invertMetadata(1))
        {
            if ((MathHelper.floor_double((double) (par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 1) == 0)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 5 | j1, 2);
            }
            else
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 6 | j1, 2);
            }
        }
        else if (i1 == invertMetadata(0))
        {
            if ((MathHelper.floor_double((double) (par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 1) == 0)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 7 | j1, 2);
            }
            else
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 0 | j1, 2);
            }
        }

        if (par6ItemStack.hasDisplayName())
        {
            ((TileEntityLandmine) par1World.func_147438_o(par2, par3, par4)).setGuiDisplayName(par6ItemStack.getDisplayName());
        }
    }

    public static int invertMetadata (int par0)
    {
        switch (par0)
        {
        case 0:
            return 0;
        case 1:
            return 5;
        case 2:
            return 4;
        case 3:
            return 3;
        case 4:
            return 2;
        case 5:
            return 1;
        default:
            return -1;
        }
    }

    @Override
    public void func_149695_a (World par1World, int par2, int par3, int par4, Block par5Block)
    {
        checkPlacement(par1World, par2, par3, par4, par5Block);

        checkExplosion(par1World, par2, par3, par4, false);
    }

    public void checkPlacement (World par1World, int par2, int par3, int par4, Block par5Block)
    {
        if (this.checkIfAttachedToBlock(par1World, par2, par3, par4))
        {
            int i1 = par1World.getBlockMetadata(par2, par3, par4) & 7;
            boolean flag = false;

            if (!par1World.isSideSolid(par2 - 1, par3, par4, EAST) && i1 == 1)
            {
                flag = true;
            }

            if (!par1World.isSideSolid(par2 + 1, par3, par4, WEST) && i1 == 2)
            {
                flag = true;
            }

            if (!par1World.isSideSolid(par2, par3, par4 - 1, SOUTH) && i1 == 3)
            {
                flag = true;
            }

            if (!par1World.isSideSolid(par2, par3, par4 + 1, NORTH) && i1 == 4)
            {
                flag = true;
            }

            if (!par1World.isSideSolid(par2, par3 - 1, par4, UP) && i1 == 5)
            {
                flag = true;
            }

            if (!par1World.isSideSolid(par2, par3 - 1, par4, UP) && i1 == 6)
            {
                flag = true;
            }

            if (!par1World.isSideSolid(par2, par3 + 1, par4, DOWN) && i1 == 0)
            {
                flag = true;
            }

            if (!par1World.isSideSolid(par2, par3 + 1, par4, DOWN) && i1 == 7)
            {
                flag = true;
            }

            if (flag)
            {
                this.func_149697_b(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                WorldHelper.setBlockToAir(par1World, par2, par3, par4);
            }
        }
    }

    private boolean checkIfAttachedToBlock (World par1World, int par2, int par3, int par4)
    {
        if (!this.func_149742_c(par1World, par2, par3, par4))
        {
            this.func_149697_b(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            WorldHelper.setBlockToAir(par1World, par2, par3, par4);
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void func_149674_a (World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote)
        {
            checkExplosion(par1World, par2, par3, par4, false);
        }
    }

    public void onEntityCollidedWithBlock (World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        if (!par1World.isRemote)
        {
            this.checkExplosion(par1World, par2, par3, par4, false);
        }
    }

    public void checkExplosion (World par1World, int par2, int par3, int par4, boolean skipCheck)
    {
        if (skipCheck || getMineState(par1World, par2, par3, par4) > 0)
        {
            if (hasItems(par1World, par2, par3, par4))
            {
                TileEntityLandmine te = (TileEntityLandmine) par1World.func_147438_o(par2, par3, par4);
                if (te.soundcountything <= 0)
                {
                    par1World.playSoundEffect((double) par2 + 0.5D, (double) par3 + 0.1D, (double) par4 + 0.5D, "random.click", 0.3F, 0.6F);
                    te.setSoundPlayed();
                }
                new LandmineExplodeLogic(par1World, par2, par3, par4, getMineTriggerer(par1World, par2, par3, par4)).explode();
            }
        }
    }

    @Override
    public AxisAlignedBB func_149668_a (World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    private boolean hasItems (World par1World, int par2, int par3, int par4)
    {
        TileEntityLandmine te = (TileEntityLandmine) par1World.func_147438_o(par2, par3, par4);
        if (te != null && te.getStackInSlot(0) != null || te.getStackInSlot(1) != null || te.getStackInSlot(2) != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected int getMineState (World par1World, int par2, int par3, int par4)
    {
        EnumCreatureType triggerMobType;

        TileEntityLandmine te = (TileEntityLandmine) par1World.func_147438_o(par2, par3, par4);

        // Change to return 1 if you want the landmine to blow up when the block
        // holding it is broken
        if (te == null)
        {
            return 0;
        }
        switch (te.triggerType)
        {
        case 0:
            triggerMobType = EnumCreatureType.creature;
            break;
        case 1:
            triggerMobType = EnumCreatureType.monster;
            break;
        case 2:
            triggerMobType = TConstruct.creatureTypePlayer;
            break;
        default:
            triggerMobType = null;
            break;
        }

        if (triggerMobType != null)
        {
            List list = null;

            if (triggerMobType == EnumCreatureType.creature)
            {
                list = par1World.getEntitiesWithinAABBExcludingEntity((Entity) null, getSensitiveAABB(par1World, par2, par3, par4));
            }

            if (triggerMobType == EnumCreatureType.monster)
            {
                list = par1World.getEntitiesWithinAABB(EntityLivingBase.class, getSensitiveAABB(par1World, par2, par3, par4));
            }

            if (triggerMobType == TConstruct.creatureTypePlayer)
            {
                list = par1World.getEntitiesWithinAABB(EntityPlayer.class, this.getSensitiveAABB(par1World, par2, par3, par4));
            }

            if (list != null && !list.isEmpty())
            {
                Iterator iterator = list.iterator();

                while (iterator.hasNext())
                {
                    Entity entity = (Entity) iterator.next();

                    if (!entity.func_145773_az())
                    {
                        return 1;
                    }
                }
            }
        }

        return par1World.isBlockIndirectlyGettingPowered(par2, par3, par4) ? 1 : 0;
    }

    protected AxisAlignedBB getSensitiveAABB (World par1World, int par2, int par3, int par4)
    {
        float f = 0.125F;
        //        return AxisAlignedBB.getAABBPool().getAABB((double)((float)par1 + f), (double)par2, (double)((float)par3 + f), (double)((float)(par1 + 1) - f), (double)par2 + 0.25D, (double)((float)(par3 + 1) - f));

        int l = par1World.getBlockMetadata(par2, par3, par4);
        int i1 = l & 7;
        boolean flag = (l & 8) > 0;

        float minX = par2 + f, minY = par3, minZ = par4 + f, maxX = par2 + 1 - f, maxY = par3 + 0.25F, maxZ = par4 + 1 - f;

        ForgeDirection dir = Helper.convertMetaToForgeOrientation(i1);
        switch (dir)
        {
        case DOWN:
            break;
        case UP:
            minY = par3 + 0.75F;
            maxY = par3 + 1;
            break;
        default:
            minX = par2;
            minY = par3;
            minZ = par4;
            maxX = par2 + 1;
            maxY = par3 + 1;
            maxZ = par4 + 1;
        }
        return AxisAlignedBB.getAABBPool().getAABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public Entity getMineTriggerer (World par1World, int par2, int par3, int par4)
    {

        EnumCreatureType triggerMobType;

        TileEntityLandmine te = (TileEntityLandmine) par1World.func_147438_o(par2, par3, par4);

        // Change to return 1 if you want the landmine to blow up when the
        // block holding it is broken
        if (te == null)
        {
            return null;
        }
        switch (te.triggerType)
        {
        case 0:
            triggerMobType = EnumCreatureType.creature;
            break;
        case 1:
            triggerMobType = EnumCreatureType.monster;
            break;
        case 2:
            triggerMobType = TConstruct.creatureTypePlayer;
            break;
        default:
            triggerMobType = null;
            break;
        }

        if (triggerMobType != null)
        {
            List list = null;

            if (triggerMobType == EnumCreatureType.creature)
            {
                list = par1World.getEntitiesWithinAABBExcludingEntity((Entity) null, AxisAlignedBB.getAABBPool().getAABB(par2 + 0D, par3 + 0D, par4 + 0D, par2 + 1D, par3 + 1D, par4 + 1D));
            }

            if (triggerMobType == EnumCreatureType.monster)
            {
                list = par1World.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getAABBPool().getAABB(par2 + 0D, par3 + 0D, par4 + 0D, par2 + 1D, par3 + 1D, par4 + 1D));
            }

            if (triggerMobType == TConstruct.creatureTypePlayer)
            {
                list = par1World.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(par2 + 0D, par3 + 0D, par4 + 0D, par2 + 1D, par3 + 1D, par4 + 1D));
            }

            if (list != null && !list.isEmpty())
            {
                Iterator iterator = list.iterator();

                while (iterator.hasNext())
                {
                    Entity entity = (Entity) iterator.next();

                    if (!entity.func_145773_az())
                    {
                        return entity;
                    }
                }
            }
        }

        return new EntityTNTPrimed(par1World, par2, par3, par4, (EntityLivingBase) null);
    }

    @Override
    public boolean func_149659_a (Explosion par1Explosion)
    {
        return false;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void func_149719_a (IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        int i1 = l & 7;
        boolean flag = (l & 8) > 0;

        ForgeDirection dir = Helper.convertMetaToForgeOrientation(i1);
        switch (dir)
        {
        case DOWN:
            this.func_149676_a(0.0625F, 0.0F, 0.0625F, 1.0F - 0.0625F, 0.0625F, 1.0F - 0.0625F);
            break;
        case UP:
            this.func_149676_a(0.0625F, 1.0F - 0.0625F, 0.0625F, 1.0F - 0.0625F, 1.0F, 1.0F - 0.0625F);
            break;
        case NORTH:
            this.func_149676_a(0.0625F, 0.0625F, 0.0F, 1.0F - 0.0625F, 1.0F - 0.0625F, 0.0625F);
            break;
        case SOUTH:
            this.func_149676_a(0.0625F, 0.0625F, 1.0F - 0.0625F, 1.0F - 0.0625F, 1.0F - 0.0625F, 1.0F);
            break;
        case EAST:
            this.func_149676_a(1.0F - 0.0625F, 0.0625F, 0.0625F, 1.0F, 1.0F - 0.0625F, 1.0F - 0.0625F);
            break;
        case WEST:
            this.func_149676_a(0.0F, 0.0625F, 0.0625F, 0.0625F, 1.0F - 0.0625F, 1.0F - 0.0625F);
            break;
        }
    }

    @Override
    public Item func_149650_a (int par1, Random par2Random, int par3)
    {
        return null;
    }

    @Override
    public TileEntity func_149915_a (World world, int meta)
    {
        return new TileEntityLandmine();
    }
}
