package tconstruct.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.naming.Context;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tconstruct.TConstruct;
import tconstruct.blocks.SignalTerminal.TerminalGeometry;
import tconstruct.blocks.logic.CastingChannelLogic;
import tconstruct.blocks.logic.SignalBusLogic;
import tconstruct.blocks.logic.SignalTerminalLogic;
import tconstruct.client.block.BlockRenderCastingChannel;
import tconstruct.client.block.SignalBusRender;
import tconstruct.items.blocks.SignalBusItem;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.multiblock.IMultiblockMember;
import tconstruct.library.multiblock.MultiblockMasterBaseLogic;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

public class SignalBus extends Block implements ITileEntityProvider {
    public static class Geometry {
        public static double cable_width_min = 0.375D;
        public static double cable_width_max = 1 - cable_width_min;
        public static double cable_low_height = 0.2D;
        public static double cable_low_offset = 0.0D;
        
        public static double cable_high_height = 1.0D;
        public static double cable_high_offset = 1 - cable_low_height;
        
        public static double cable_extend_min = 0.0D;
        public static double cable_extend_max = 1.0D;
        
        public static double cable_corner_min = cable_high_offset - 1;
        public static double cable_corner_max = cable_low_height + 1;
        
        public static double zfight = 0.0000001D;
    }
    
    public static int HITBOXES = 6;
    
    public Icon[] icons;
    public String[] textureNames = new String[] { "signalbus" };

	public SignalBus(int par1) {
		super(par1, Material.circuits);
        this.setHardness(0.1F);
        this.setResistance(1);
        this.setStepSound(soundMetalFootstep);
        setCreativeTab(TConstructRegistry.blockTab);
	}

	@Override
	public void onNeighborTileChange(World world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		TileEntity te = world.getBlockTileEntity(tileX, tileY, tileZ);
		if (te instanceof SignalBusLogic) {
			if (((SignalBusLogic)te).getMultiblockMaster() != null) {
				((SignalBusLogic)te).getMultiblockMaster().detachBlock((IMultiblockMember)te, false);
			}
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof SignalBusLogic) {
			((SignalBusLogic)te).onBlockAdded(world, x, y, z);
		}
	}

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int metadata)
    {
        return icons[0];
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
    }

//    @Override
//    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
//    {
//        SignalBusLogic tile = (SignalBusLogic) world.getBlockTileEntity(x, y, z);
//        if (!(tile instanceof SignalBusLogic)) return;
//        float minX = 0.375F;
//        float maxX = 0.625F;
//        float minZ = 0.375F;
//        float maxZ = 0.625F;
//        if (tile.isConnected(ForgeDirection.DOWN, ForgeDirection.NORTH))
//            minZ = 0F;
//        if (tile.isConnected(ForgeDirection.DOWN, ForgeDirection.SOUTH))
//            maxZ = 1F;
//        if (tile.isConnected(ForgeDirection.DOWN, ForgeDirection.WEST))
//            minX = 0F;
//        if (tile.isConnected(ForgeDirection.DOWN, ForgeDirection.EAST))
//            maxX = 1F;
//
//        //this.setBlockBounds(minX, 0.0F, minZ, maxX, 0.2F, maxZ);
//        this.setBlockBounds(0, 0, 0, 1, 1, 1);
//    }

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
        return SignalBusRender.renderID;
    }
    
    public void addCollisionBoxesToList (World world, int x, int y, int z, AxisAlignedBB collisionTest, List collisionBoxList, Entity entity)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalBusLogic)
        {
            for (AxisAlignedBB aabb : getBoxes((SignalBusLogic) te))
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
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered (IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @Override
    public MovingObjectPosition collisionRayTrace (World world, int x, int y, int z, Vec3 start, Vec3 end)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalBusLogic)
        {
            MovingObjectPosition closest = null;
            AxisAlignedBB[] boxes = getBoxes((SignalBusLogic)te);
            
            double closestCalc = Double.MAX_VALUE;
            double hitDistance = 0D;

            for (int i = 0; i < boxes.length; i++)
            {
                if (boxes[i] == null)
                {
                    continue;
                }
                this.setBlockBounds((float)boxes[i].minX, (float)boxes[i].minY, (float)boxes[i].minZ, (float)boxes[i].maxX, (float)boxes[i].maxY, (float)boxes[i].maxZ);
                MovingObjectPosition hit = super.collisionRayTrace(world, x, y, z, start, end);
                if (hit != null)
                {
                    hitDistance = start.distanceTo(hit.hitVec);
                    if (hitDistance < closestCalc)
                    {
                        closestCalc = hitDistance;
                        closest = hit;
                    }
                }
            }
            return closest;
        }
        
        return null;
    }
    
    private static AxisAlignedBB[] getBoxes (SignalBusLogic logic)
    {
        boolean placed[] = logic.placedSides();
        boolean connected[];
        boolean corners[];
        boolean renderDir[];

        AxisAlignedBB[] parts = new AxisAlignedBB[HITBOXES];

        double minX;
        double minY;
        double minZ;
        double maxX;
        double maxY;
        double maxZ;
        boolean didRender = false;
        
        if (placed[ForgeDirection.DOWN.ordinal()])
        {
            connected = logic.connectedSides(ForgeDirection.DOWN);
            corners = logic.getRenderCorners(ForgeDirection.DOWN);
            renderDir = new boolean[] {
                    (connected[0] || placed[0] || corners[0]),
                    (connected[1] || placed[1] || corners[1]),
                    (connected[2] || placed[2] || corners[2]),
                    (connected[3] || placed[3] || corners[3]),
                    (connected[4] || placed[4] || corners[4]),
                    (connected[5] || placed[5] || corners[5])
            };
            minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            minY = Geometry.cable_low_offset;
            minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            maxY = Geometry.cable_low_height;
            maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            
            parts[0] = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            didRender = true;
        }
        if (placed[ForgeDirection.UP.ordinal()])
        {
            connected = logic.connectedSides(ForgeDirection.UP);
            corners = logic.getRenderCorners(ForgeDirection.UP);
            renderDir = new boolean[] {
                    (connected[0] || placed[0] || corners[0]),
                    (connected[1] || placed[1] || corners[1]),
                    (connected[2] || placed[2] || corners[2]),
                    (connected[3] || placed[3] || corners[3]),
                    (connected[4] || placed[4] || corners[4]),
                    (connected[5] || placed[5] || corners[5])
            };
            minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            minY = Geometry.cable_high_offset;
            minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            maxY = Geometry.cable_high_height;
            maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            
            parts[1] = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            didRender = true;
        }
        if (placed[ForgeDirection.NORTH.ordinal()])
        {
            connected = logic.connectedSides(ForgeDirection.NORTH);
            corners = logic.getRenderCorners(ForgeDirection.NORTH);
            renderDir = new boolean[] {
                    (connected[0] || placed[0] || corners[0]),
                    (connected[1] || placed[1] || corners[1]),
                    (connected[2] || placed[2] || corners[2]),
                    (connected[3] || placed[3] || corners[3]),
                    (connected[4] || placed[4] || corners[4]),
                    (connected[5] || placed[5] || corners[5])
            };
            minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            minZ = Geometry.cable_low_offset;
            maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            maxZ = Geometry.cable_low_height;
            
            
            parts[2] = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            didRender = true;
        }
        if (placed[ForgeDirection.SOUTH.ordinal()])
        {
            connected = logic.connectedSides(ForgeDirection.SOUTH);
            corners = logic.getRenderCorners(ForgeDirection.SOUTH);
            renderDir = new boolean[] {
                    (connected[0] || placed[0] || corners[0]),
                    (connected[1] || placed[1] || corners[1]),
                    (connected[2] || placed[2] || corners[2]),
                    (connected[3] || placed[3] || corners[3]),
                    (connected[4] || placed[4] || corners[4]),
                    (connected[5] || placed[5] || corners[5])
            };
            minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            minZ = Geometry.cable_high_offset;
            maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            maxZ = Geometry.cable_high_height;

            parts[3] = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            didRender = true;
        }
        if (placed[ForgeDirection.WEST.ordinal()])
        {
            connected = logic.connectedSides(ForgeDirection.WEST);
            corners = logic.getRenderCorners(ForgeDirection.WEST);
            renderDir = new boolean[] {
                    (connected[0] || placed[0] || corners[0]),
                    (connected[1] || placed[1] || corners[1]),
                    (connected[2] || placed[2] || corners[2]),
                    (connected[3] || placed[3] || corners[3]),
                    (connected[4] || placed[4] || corners[4]),
                    (connected[5] || placed[5] || corners[5])
            };
            minX = Geometry.cable_low_offset;
            minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            maxX = Geometry.cable_low_height;
            maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;

            parts[4] = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            didRender = true;
        }
        if (placed[ForgeDirection.EAST.ordinal()])
        {
            connected = logic.connectedSides(ForgeDirection.EAST);
            corners = logic.getRenderCorners(ForgeDirection.EAST);
            renderDir = new boolean[] {
                    (connected[0] || placed[0] || corners[0]),
                    (connected[1] || placed[1] || corners[1]),
                    (connected[2] || placed[2] || corners[2]),
                    (connected[3] || placed[3] || corners[3]),
                    (connected[4] || placed[4] || corners[4]),
                    (connected[5] || placed[5] || corners[5])
            };
            minX = Geometry.cable_high_offset;
            minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? Geometry.cable_extend_min : Geometry.cable_width_min;
            maxX = Geometry.cable_high_height;
            maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? Geometry.cable_extend_max : Geometry.cable_width_max;
            
            parts[5] = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            didRender = true;
        }
        if (!didRender)
        {
            minX = Geometry.cable_width_min;
            minY = Geometry.cable_low_offset;
            minZ = Geometry.cable_width_min;
            maxX = Geometry.cable_width_max;
            maxY = Geometry.cable_low_height;
            maxZ = Geometry.cable_width_max;
            
            parts[0] = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        }

        return parts;
    }

    private static int closestClicked (EntityPlayer player, double reachDistance, SignalBusLogic terminal, AxisAlignedBB[] parts)
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
    @SideOnly(Side.CLIENT)
    @ForgeSubscribe 
    public void onBlockHighlight (DrawBlockHighlightEvent event)
    {
        return;
//        if (event.target.typeOfHit == EnumMovingObjectType.TILE && event.player.worldObj.getBlockId(event.target.blockX, event.target.blockY, event.target.blockZ) == this.blockID)
//        {
//            TileEntity te = event.player.worldObj.getBlockTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ);
//            if (!(te instanceof SignalBusLogic))
//            {
//                return;
//            }
//            AxisAlignedBB[] boxes = getBoxes((SignalBusLogic)te);
//            double reach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
//            int hitbox = closestClicked(event.player, reach, (SignalBusLogic)te, boxes);
//            
//            if (hitbox > 0 && hitbox < HITBOXES)
//            {
//                float f1 = 0.002F;
//                double d0 = event.player.lastTickPosX + (event.player.posX - event.player.lastTickPosX) * (double)event.partialTicks;
//                double d1 = event.player.lastTickPosY + (event.player.posY - event.player.lastTickPosY) * (double)event.partialTicks;
//                double d2 = event.player.lastTickPosZ + (event.player.posZ - event.player.lastTickPosZ) * (double)event.partialTicks;
//                drawOutlinedBoundingBox(boxes[hitbox].expand((double)f1, (double)f1, (double)f1).getOffsetBoundingBox(-d0, -d1, -d2));
//
//            }
//            event.setCanceled(true);
//        }
        
    }

    /**
     * Draws lines for the edges of the bounding box.
     */
    private static void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(3);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        tessellator.startDrawing(3);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        tessellator.startDrawing(1);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.draw();
    }
    
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new SignalBusLogic();
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new SignalBusLogic();
	}
	
	@Override
    public ArrayList<ItemStack> getBlockDropped (World world, int x, int y, int z, int metadata, int fortune)
    {
	    return new ArrayList<ItemStack>();
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalBusLogic)
        {
            NBTTagCompound data = itemStack.stackTagCompound;
            if (data != null && data.hasKey("connectedSide"))
            {
                ((SignalBusLogic) te).addPlacedSide(data.getInteger("connectedSide"));
                itemStack.stackTagCompound = null;
            }
        }
    }

    @Override
    public void onNeighborBlockChange (World par1World, int par2, int par3, int par4, int par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        
        if (par5 == this.blockID || par1World.isRemote)
        {
            return;
        }
        TileEntity te = par1World.getBlockTileEntity(par2, par3, par4);
        if (te instanceof SignalBusLogic)
        {
            ((SignalBusLogic)te).forceNeighborCheck();
        }
    }

    @Override
    public void breakBlock (World world, int x, int y, int z, int id, int meta)
    {
        int dropBus, dropWire = 0;
        float jumpX, jumpY, jumpZ;
        ItemStack tempStack;
        Random rand = new Random();
        
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof SignalBusLogic)
        {
            dropBus = ((SignalBusLogic) te).getDroppedBuses();
            dropWire = ((SignalBusLogic) te).getDroppedWire();
            if (dropBus > 0)
            {
                tempStack = new ItemStack(TConstruct.instance.content.signalBus.blockID, dropBus, 0);
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
            ((SignalBusLogic) te).notifyBreak();
        }

        super.breakBlock(world, x, y, z, id, meta);
    }

}
