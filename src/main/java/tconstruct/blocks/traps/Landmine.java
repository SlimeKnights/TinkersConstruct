package tconstruct.blocks.traps;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import tconstruct.library.TConstructRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Landmine extends Block
{
    /** The mob type that can trigger this pressure plate. */
    private EnumMobType triggerMobType;

    public Landmine(int par1, EnumMobType par3EnumMobType, Material par4Material)
    {
        super(par1, par4Material);
        this.triggerMobType = EnumMobType.mobs;
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setTickRandomly(true);
        float var5 = 0.0625F;
        this.setBlockBounds(var5, 0.0F, var5, 1.0F - var5, 0.03125F, 1.0F - var5);
    }

    public Icon getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
    {
        Block block = Block.blocksList[world.getBlockId(x, y - 1, z)];
        if (block != null)
        {
            return block.getBlockTexture(world, x, y - 1, z, side);
        }
        return Block.sponge.getIcon(side, world.getBlockMetadata(x, y, z));
    }

    public Icon getIcon (int side, int meta)
    {
        return Block.sponge.getIcon(1, meta);
    }

    public void registerIcons (IconRegister par1IconRegister)
    {

    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate ()
    {
        return 20;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube ()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    public boolean getBlocksMovement (IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return true;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt (World par1World, int par2, int par3, int par4)
    {
        return par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || BlockFence.isIdAFence(par1World.getBlockId(par2, par3 - 1, par4));
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange (World par1World, int par2, int par3, int par4, int par5)
    {
        boolean var6 = false;

        if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !BlockFence.isIdAFence(par1World.getBlockId(par2, par3 - 1, par4)))
        {
            var6 = true;
        }

        if (var6)
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            //par1World.setBlock(par2, par3, par4, 0);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick (World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote)
        {
            if (par1World.getBlockMetadata(par2, par3, par4) != 0)
            {
                this.setStateIfMobInteractsWithPlate(par1World, par2, par3, par4);
            }
        }
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock (World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        if (!par1World.isRemote)
        {
            if (par1World.getBlockMetadata(par2, par3, par4) != 1)
            {
                this.setStateIfMobInteractsWithPlate(par1World, par2, par3, par4);
            }
        }
    }

    /**
     * Checks if there are mobs on the plate. If a mob is on the plate and it is off, it turns it on, and vice versa.
     */
    private void setStateIfMobInteractsWithPlate (World world, int posX, int posY, int posZ)
    {
        boolean var5 = world.getBlockMetadata(posX, posY, posZ) == 1;
        boolean var6 = false;
        float var7 = 0.125F;
        List var8 = null;

        if (this.triggerMobType == EnumMobType.everything)
        {
            var8 = world.getEntitiesWithinAABBExcludingEntity(
                    (Entity) null,
                    AxisAlignedBB.getAABBPool().getAABB((double) ((float) posX + var7), (double) posY, (double) ((float) posZ + var7), (double) ((float) (posX + 1) - var7), (double) posY + 0.25D,
                            (double) ((float) (posZ + 1) - var7)));
        }

        if (this.triggerMobType == EnumMobType.mobs)
        {
            var8 = world.getEntitiesWithinAABB(
                    EntityLiving.class,
                    AxisAlignedBB.getAABBPool().getAABB((double) ((float) posX + var7), (double) posY, (double) ((float) posZ + var7), (double) ((float) (posX + 1) - var7), (double) posY + 0.25D,
                            (double) ((float) (posZ + 1) - var7)));
        }

        if (this.triggerMobType == EnumMobType.players)
        {
            var8 = world.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    AxisAlignedBB.getAABBPool().getAABB((double) ((float) posX + var7), (double) posY, (double) ((float) posZ + var7), (double) ((float) (posX + 1) - var7), (double) posY + 0.25D,
                            (double) ((float) (posZ + 1) - var7)));
        }

        if (!var8.isEmpty())
        {
            Iterator var9 = var8.iterator();

            while (var9.hasNext())
            {
                Entity var10 = (Entity) var9.next();

                if (!var10.doesEntityNotTriggerPressurePlate())
                {
                    var6 = true;
                    break;
                }
            }
        }

        if (var6 && !var5)
        {
            world.setBlock(posX, posY, posZ, 0);
            world.createExplosion((Entity) null, posX, posY, posZ, 2.0F, true);
            /*par1World.setBlockMetadataWithNotify(posX, posY, posZ, 1);
            par1World.notifyBlocksOfNeighborChange(posX, posY, posZ, this.blockID);
            par1World.notifyBlocksOfNeighborChange(posX, posY - 1, posZ, this.blockID);
            par1World.markBlockRangeForRenderUpdate(posX, posY, posZ, posX, posY, posZ);
            par1World.playSoundEffect((double)posX + 0.5D, (double)posY + 0.1D, (double)posZ + 0.5D, "random.click", 0.3F, 0.6F);*/
        }

        /*if (!var6 && var5)
        {
            par1World.setBlockMetadataWithNotify(posX, posY, posZ, 0);
            par1World.notifyBlocksOfNeighborChange(posX, posY, posZ, this.blockID);
            par1World.notifyBlocksOfNeighborChange(posX, posY - 1, posZ, this.blockID);
            par1World.markBlockRangeForRenderUpdate(posX, posY, posZ, posX, posY, posZ);
            par1World.playSoundEffect((double)posX + 0.5D, (double)posY + 0.1D, (double)posZ + 0.5D, "random.click", 0.3F, 0.5F);
        }

        if (var6)
        {
            par1World.scheduleBlockUpdate(posX, posY, posZ, this.blockID, this.tickRate());
        }*/
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock (World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        if (par6 > 0)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState (IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        boolean var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 1;
        float var6 = 0.0625F;

        if (var5)
        {
            this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 0.03125F, 1.0F - var6);
        }
        else
        {
            this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 0.0625F, 1.0F - var6);
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender ()
    {
        float var1 = 0.5F;
        float var2 = 0.125F;
        float var3 = 0.5F;
        this.setBlockBounds(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag ()
    {
        return 1;
    }
}
