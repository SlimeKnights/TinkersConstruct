package tconstruct.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import codechicken.nei.api.IHighlightHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import tconstruct.TConstruct;
import tconstruct.blocks.logic.SignalBusLogic;
import tconstruct.blocks.logic.SignalTerminalLogic;
import tconstruct.client.block.SignalBusRender;
import tconstruct.client.block.SignalTerminalRender;
import tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeDirection.*;
import net.minecraftforge.event.ForgeSubscribe;

public class SignalTerminal extends Block implements ITileEntityProvider
{

    public static class TerminalGeometry
    {
        public static float plate_width_min = 0.25F;
        public static float plate_width_max = 0.75F;
        public static float plate_low_max = 0.15F;
        public static float plate_high_max = 1.0F;
        public static float plate_low_min = 0.0F;
        public static float plate_high_min = 1 - plate_low_max;
        public static float center_min = 0.375F;
        public static float center_max = 0.625F;
        public static float channel_width_min = 0.375F;
        public static float channel_width_max = 0.625F;
        public static float channel_low_max = center_min;
        public static float channel_high_max = plate_high_min;
        public static float channel_low_min = plate_low_max;
        public static float channel_high_min = center_max;
    }

    public static int HITBOXES = 13;
    public static int[] sideBoxMapping = new int[] { -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 };

    public Icon[] icons;
    public Icon[] channelIcons;
    public String[] textureNames = new String[] { "signalbus" };
    public String[] channelTextureNames = new String[] { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red",
            "black" };

    public SignalTerminal(int par1)
    {
        super(par1, Material.circuits);
        this.setHardness(0.1F);
        this.setResistance(1);
        this.setStepSound(soundMetalFootstep);
        setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int metadata)
    {
        return icons[0];
    }

    public Icon getChannelIcon (int channel)
    {
        if (channel < 0 || channel >= channelIcons.length)
        {
            return channelIcons[0];
        }

        return channelIcons[channel];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }

        this.channelIcons = new Icon[channelTextureNames.length];

        for (int i = 0; i < this.channelIcons.length; ++i)
        {
            this.channelIcons[i] = iconRegister.registerIcon("tinker:glass/stainedglass_" + channelTextureNames[i]);
        }
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

    @Override
    public boolean isBlockSolidOnSide (World world, int x, int y, int z, ForgeDirection side)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null && te instanceof SignalTerminalLogic)
        {
            if (((SignalTerminalLogic) te).getConnectedSides()[side.ordinal()] != -1)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType ()
    {
        return SignalTerminalRender.renderID;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate (World par1World)
    {
        return 1;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide (World world, int x, int y, int z, int side)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        return true || (dir == ForgeDirection.NORTH && world.isBlockSolidOnSide(x, y, z + 1, ForgeDirection.NORTH))
                || (dir == ForgeDirection.SOUTH && world.isBlockSolidOnSide(x, y, z - 1, ForgeDirection.SOUTH))
                || (dir == ForgeDirection.WEST && world.isBlockSolidOnSide(x + 1, y, z, ForgeDirection.WEST))
                || (dir == ForgeDirection.EAST && world.isBlockSolidOnSide(x - 1, y, z, ForgeDirection.EAST)) || (dir == ForgeDirection.UP && world.isBlockSolidOnSide(x, y - 1, z, ForgeDirection.UP))
                || (dir == ForgeDirection.DOWN && world.isBlockSolidOnSide(x, y + 1, z, ForgeDirection.DOWN));
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt (World world, int x, int y, int z)
    {
        return world.isBlockSolidOnSide(x - 1, y, z, ForgeDirection.EAST) || world.isBlockSolidOnSide(x + 1, y, z, ForgeDirection.WEST) || world.isBlockSolidOnSide(x, y, z - 1, ForgeDirection.SOUTH)
                || world.isBlockSolidOnSide(x, y, z + 1, ForgeDirection.NORTH) || world.isBlockSolidOnSide(x, y - 1, z, ForgeDirection.UP)
                || world.isBlockSolidOnSide(x, y + 1, z, ForgeDirection.DOWN);
    }

    /**
    * Updates the blocks bounds based on its current state. Args: world, x, y, z
    */
    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
    {
        float minX = 1;
        float minY = 1;
        float minZ = 1;
        float maxX = 0;
        float maxY = 0;
        float maxZ = 0;

        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalTerminalLogic)
        {
            for (AxisAlignedBB aabb : getBoxes((SignalTerminalLogic) te))
            {
                if (aabb == null)
                {
                    continue;
                }

                minX = Math.min(minX, (float) aabb.minX);
                minY = Math.min(minY, (float) aabb.minY);
                minZ = Math.min(minZ, (float) aabb.minZ);
                maxX = Math.max(maxX, (float) aabb.maxX);
                maxY = Math.max(maxY, (float) aabb.maxY);
                maxZ = Math.max(maxZ, (float) aabb.maxZ);
            }

            setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }
        else
        {
            this.setBlockBounds(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
        }
        return;
    }

    public void addCollisionBoxesToList (World world, int x, int y, int z, AxisAlignedBB collisionTest, List collisionBoxList, Entity entity)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalTerminalLogic)
        {
            for (AxisAlignedBB aabb : getBoxes((SignalTerminalLogic) te))
            {
                if (aabb == null)
                {
                    continue;
                }

                aabb = AxisAlignedBB.getBoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
                aabb.minX += x;
                aabb.minY += y;
                aabb.minZ += z;
                aabb.maxX += x;
                aabb.maxY += y;
                aabb.maxZ += z;

                if (collisionTest.intersectsWith(aabb))
                {
                    collisionBoxList.add(aabb);
                }
            }
        }
        else
        {
            super.addCollisionBoxesToList(world, x, y, z, collisionTest, collisionBoxList, entity);
        }
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower (IBlockAccess world, int x, int y, int z, int localSide)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalTerminalLogic)
        {
            return ((SignalTerminalLogic) te).isProvidingWeakPower(localSide);
        }

        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered (IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower (IBlockAccess world, int x, int y, int z, int localSide)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalTerminalLogic)
        {
            return ((SignalTerminalLogic) te).isProvidingStrongPower(localSide);
        }

        return 0;
    }

    @Override
    public boolean shouldCheckWeakPower (World world, int x, int y, int z, int side)
    {
        return true;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower ()
    {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity (World world)
    {
        return new SignalTerminalLogic();
    }

    @Override
    public void onNeighborBlockChange (World world, int x, int y, int z, int neighborID)
    {
        super.onNeighborBlockChange(world, x, y, z, neighborID);

        //        if (neighborID == blockID) {
        //            return;
        //        }

        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalTerminalLogic)
        {
            ((SignalTerminalLogic) te).onNeighborBlockChange();

        }

    }

    private static AxisAlignedBB[] getBoxes (SignalTerminalLogic logic)
    {
        byte connected[] = logic.getConnectedSides();

        AxisAlignedBB[] parts = new AxisAlignedBB[HITBOXES];

        // Center
        parts[0] = AxisAlignedBB.getBoundingBox(TerminalGeometry.center_min, TerminalGeometry.center_min, TerminalGeometry.center_min, TerminalGeometry.center_max, TerminalGeometry.center_max,
                TerminalGeometry.center_max);

        if (connected[ForgeDirection.DOWN.ordinal()] != -1)
        {
            parts[1] = AxisAlignedBB.getBoundingBox(TerminalGeometry.plate_width_min, TerminalGeometry.plate_low_min, TerminalGeometry.plate_width_min, TerminalGeometry.plate_width_max,
                    TerminalGeometry.plate_low_max, TerminalGeometry.plate_width_max);
            parts[2] = AxisAlignedBB.getBoundingBox(TerminalGeometry.channel_width_min, TerminalGeometry.channel_low_min, TerminalGeometry.channel_width_min, TerminalGeometry.channel_width_max,
                    TerminalGeometry.channel_low_max, TerminalGeometry.channel_width_max);
        }

        if (connected[ForgeDirection.UP.ordinal()] != -1)
        {
            parts[3] = AxisAlignedBB.getBoundingBox(TerminalGeometry.plate_width_min, TerminalGeometry.plate_high_min, TerminalGeometry.plate_width_min, TerminalGeometry.plate_width_max,
                    TerminalGeometry.plate_high_max, TerminalGeometry.plate_width_max);
            parts[4] = AxisAlignedBB.getBoundingBox(TerminalGeometry.channel_width_min, TerminalGeometry.channel_high_min, TerminalGeometry.channel_width_min, TerminalGeometry.channel_width_max,
                    TerminalGeometry.channel_high_max, TerminalGeometry.channel_width_max);
        }

        if (connected[ForgeDirection.NORTH.ordinal()] != -1)
        {
            parts[5] = AxisAlignedBB.getBoundingBox(TerminalGeometry.plate_width_min, TerminalGeometry.plate_width_min, TerminalGeometry.plate_high_min, TerminalGeometry.plate_width_max,
                    TerminalGeometry.plate_width_max, TerminalGeometry.plate_high_max);
            parts[6] = AxisAlignedBB.getBoundingBox(TerminalGeometry.channel_width_min, TerminalGeometry.channel_width_min, TerminalGeometry.channel_high_min, TerminalGeometry.channel_width_max,
                    TerminalGeometry.channel_width_max, TerminalGeometry.channel_high_max);
        }

        if (connected[ForgeDirection.SOUTH.ordinal()] != -1)
        {
            parts[7] = AxisAlignedBB.getBoundingBox(TerminalGeometry.plate_width_min, TerminalGeometry.plate_width_min, TerminalGeometry.plate_low_min, TerminalGeometry.plate_width_max,
                    TerminalGeometry.plate_width_max, TerminalGeometry.plate_low_max);
            parts[8] = AxisAlignedBB.getBoundingBox(TerminalGeometry.channel_width_min, TerminalGeometry.channel_width_min, TerminalGeometry.channel_low_min, TerminalGeometry.channel_width_max,
                    TerminalGeometry.channel_width_max, TerminalGeometry.channel_low_max);
        }

        if (connected[ForgeDirection.WEST.ordinal()] != -1)
        {
            parts[9] = AxisAlignedBB.getBoundingBox(TerminalGeometry.plate_low_min, TerminalGeometry.plate_width_min, TerminalGeometry.plate_width_min, TerminalGeometry.plate_low_max,
                    TerminalGeometry.plate_width_max, TerminalGeometry.plate_width_max);
            parts[10] = AxisAlignedBB.getBoundingBox(TerminalGeometry.channel_low_min, TerminalGeometry.channel_width_min, TerminalGeometry.channel_width_min, TerminalGeometry.channel_low_max,
                    TerminalGeometry.channel_width_max, TerminalGeometry.channel_width_max);
        }

        if (connected[ForgeDirection.EAST.ordinal()] != -1)
        {
            parts[11] = AxisAlignedBB.getBoundingBox(TerminalGeometry.plate_high_min, TerminalGeometry.plate_width_min, TerminalGeometry.plate_width_min, TerminalGeometry.plate_high_max,
                    TerminalGeometry.plate_width_max, TerminalGeometry.plate_width_max);
            parts[12] = AxisAlignedBB.getBoundingBox(TerminalGeometry.channel_high_min, TerminalGeometry.channel_width_min, TerminalGeometry.channel_width_min, TerminalGeometry.channel_high_max,
                    TerminalGeometry.channel_width_max, TerminalGeometry.channel_width_max);
        }

        return parts;
    }

    private static int closestClicked (EntityPlayer player, double reachDistance, SignalTerminalLogic terminal, AxisAlignedBB[] parts)
    {
        int closest = -1;

        Vec3 playerPosition = Vec3.createVectorHelper(player.posX - terminal.xCoord, player.posY - terminal.yCoord + player.getEyeHeight(), player.posZ - terminal.zCoord);
        Vec3 playerLook = player.getLookVec();

        Vec3 playerViewOffset = Vec3.createVectorHelper(playerPosition.xCoord + playerLook.xCoord * reachDistance, playerPosition.yCoord + playerLook.yCoord * reachDistance, playerPosition.zCoord
                + playerLook.zCoord * reachDistance);
        double closestCalc = Double.MAX_VALUE;
        double hitDistance = 0D;

        for (int i = 0; i < parts.length; i++)
        {
            if (parts[i] == null)
            {
                continue;
            }
            MovingObjectPosition hit = parts[i].calculateIntercept(playerPosition, playerViewOffset);
            if (hit != null)
            {
                hitDistance = playerPosition.distanceTo(hit.hitVec);
                if (hitDistance < closestCalc)
                {
                    closestCalc = hitDistance;
                    closest = i;
                }
            }
        }
        return closest;
    }

    @Override
    public boolean onBlockActivated (World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        int meta = par1World.getBlockMetadata(par2, par3, par4);
        TileEntity te = par1World.getBlockTileEntity(par2, par3, par4);

        if (!par1World.isRemote)
        {
            if (te != null && te instanceof SignalTerminalLogic)
            {
                //                ((SignalTerminalLogic) te).nextChannel();
                if (!par1World.isRemote)
                {
                    //                    par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, 1);
                    //                    par1World.markBlockForRenderUpdate(par2, par3, par4);
                    par1World.markBlockForUpdate(par2, par3, par4);
                }
                int boxHit = closestClicked(par5EntityPlayer, 3.0F, (SignalTerminalLogic) te, getBoxes((SignalTerminalLogic) te));
                if (boxHit < 0 || boxHit >= HITBOXES)
                {
                    return false;
                }
                if (boxHit == 0)
                {
                    return false;
                }
                int side = sideBoxMapping[boxHit];

                if (side < 0 || side >= 6)
                {
                    return false;
                }

                if (par5EntityPlayer.isSneaking())
                {
                    ((SignalTerminalLogic) te).prevChannel(side);
                }
                else
                {
                    ((SignalTerminalLogic) te).nextChannel(side);
                }
            }
        }

        if (te instanceof SignalTerminalLogic)
        {
            TConstruct.logger.info(((SignalTerminalLogic) te).debugString());
        }

        TConstruct.logger.info("meta: " + meta);

        //this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);

        return false;
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalTerminalLogic)
        {
            NBTTagCompound data = itemStack.stackTagCompound;
            if (data != null && data.hasKey("connectedSide"))
            {
                ((SignalTerminalLogic) te).addPendingSide(data.getInteger("connectedSide"));
                itemStack.stackTagCompound = null;
            }
            ((SignalTerminalLogic) te).connectPending();
        }
    }
    
    

    @Override
    public ArrayList<ItemStack> getBlockDropped (World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>();
    }

    @Override
    public void breakBlock (World world, int x, int y, int z, int id, int meta)
    {
        int dropTerm, dropWire = 0;
        float jumpX, jumpY, jumpZ;
        ItemStack tempStack;
        Random rand = new Random();
        
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalTerminalLogic)
        {
            dropTerm = ((SignalTerminalLogic) te).getDroppedTerminals();
            dropWire = ((SignalTerminalLogic) te).getDroppedWire();
            
            if (dropTerm > 0)
            {
                tempStack = new ItemStack(TConstruct.instance.content.signalTerminal.blockID, dropTerm, 0);
                jumpX = rand.nextFloat() * 0.8F + 0.1F;
                jumpY = rand.nextFloat() * 0.8F + 0.1F;
                jumpZ = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityitem = new EntityItem(world, (double) ((float) x + jumpX), (double) ((float) y + jumpY), (double) ((float) z + jumpZ), tempStack);

                float offset = 0.05F;
                entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
                entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
                entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
                world.spawnEntityInWorld(entityitem);
            }
            if (dropWire > 0)
            {
                tempStack = new ItemStack(TConstruct.instance.content.lengthWire, dropWire);
                jumpX = rand.nextFloat() * 0.8F + 0.1F;
                jumpY = rand.nextFloat() * 0.8F + 0.1F;
                jumpZ = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityitem = new EntityItem(world, (double) ((float) x + jumpX), (double) ((float) y + jumpY), (double) ((float) z + jumpZ), tempStack);

                float offset = 0.05F;
                entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
                entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
                entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
                world.spawnEntityInWorld(entityitem);
            }
            ((SignalTerminalLogic) te).notifyBreak();
        }

        super.breakBlock(world, x, y, z, id, meta);
    }

}
