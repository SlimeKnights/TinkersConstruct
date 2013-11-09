package tconstruct.blocks.logic;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.text.MaskFormatter;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import tconstruct.TConstruct;
import tconstruct.blocks.component.SignalBusMasterLogic;
import tconstruct.library.component.ISignalBusConnectable;
import tconstruct.library.multiblock.IMultiblockMember;
import tconstruct.library.multiblock.MultiblockBaseLogic;
import tconstruct.library.multiblock.MultiblockMasterBaseLogic;
import tconstruct.library.signal.ISignalTransceiver;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IActiveLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SignalBusLogic extends MultiblockBaseLogic implements IActiveLogic, ISignalBusConnectable
{
    private int ticks = 0;
    private Map<CoordTuple, byte[]> transceivers = new HashMap<CoordTuple, byte[]>();
    private byte[] localHighSignals = new byte[16];
    private byte[] cachedReceivedSignals = new byte[16];
    private int cachedConnectableCount = 0;

    private boolean southboundSignalsChanged = false;
    private boolean northboundSignalsChanged = false;
    private boolean forceCheck = false;

    private boolean[] placedSides = new boolean[] { false, false, false, false, false, false };

    public SignalBusLogic()
    {

    }

    @Override
    public boolean canUpdate ()
    {
        return true;
    }

    @Override
    public IMultiblockMember[] getNeighboringMembers ()
    {
        List<IMultiblockMember> corners = new LinkedList<IMultiblockMember>();

        int i, j;
        int tX, tY, tZ;
        TileEntity te;
        for (i = 0; i < 6; ++i)
        {
            if (!placedSides[i])
            {
                continue;
            }
            for (j = 0; j < 6; ++j)
            {
                if (j == i || j == ForgeDirection.OPPOSITES[i])
                {
                    continue;
                }
                tX = ForgeDirection.VALID_DIRECTIONS[j].offsetX + xCoord;
                tY = ForgeDirection.VALID_DIRECTIONS[j].offsetY + yCoord;
                tZ = ForgeDirection.VALID_DIRECTIONS[j].offsetZ + zCoord;
                if (worldObj.isBlockOpaqueCube(tX, tY, tZ))
                {
                    continue;
                }
                tX += ForgeDirection.VALID_DIRECTIONS[i].offsetX;
                tY += ForgeDirection.VALID_DIRECTIONS[i].offsetY;
                tZ += ForgeDirection.VALID_DIRECTIONS[i].offsetZ;
                te = worldObj.getBlockTileEntity(tX, tY, tZ);
                if (te instanceof IMultiblockMember && ((IMultiblockMember) te).isCompatible((Object) this) && ((IMultiblockMember) te).willConnect(getCoordInWorld()))
                {
                    corners.add((IMultiblockMember) te);
                }
            }
        }

        corners.addAll(Arrays.asList(super.getNeighboringMembers()));
        IMultiblockMember[] tmp = new IMultiblockMember[corners.size()];
        return corners.toArray(tmp);
    }

    public void broadcastSouthboundSignals ()
    {
        if (southboundSignalsChanged)
        {
            TileEntity te = null;

            for (CoordTuple coord : transceivers.keySet())
            {
                if (worldObj.getChunkProvider().chunkExists(coord.x >> 4, coord.z >> 4))
                {
                    te = worldObj.getBlockTileEntity(coord.x, coord.y, coord.z);
                    if (te instanceof ISignalTransceiver)
                    {
                        ((ISignalTransceiver) te).receiveSignalUpdate(cachedReceivedSignals);
                    }
                }
            }

            southboundSignalsChanged = false;
        }
    }

    public void multiBlockTick ()
    {

    }

    @Override
    public void updateEntity ()
    {
        if (worldObj.isRemote)
        {
            return;
        }

        if (!this.isConnected())
        {
            return;
        }
        
        if (forceCheck)
        {
            int connectableCount = this.getNeighboringMembers().length;
            forceCheck = false;
            if (connectableCount != cachedConnectableCount)
            {
                cachedConnectableCount = connectableCount;
                super.onBlockAdded(worldObj, xCoord, yCoord, zCoord);
//                this.getMultiblockMaster().revisitBlocks();
            }
        }

        if (northboundSignalsChanged)
        {

            TileEntity te;
            byte[] signals;

            localHighSignals = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

            for (CoordTuple coord : transceivers.keySet())
            {

                signals = transceivers.get(coord);

                if (signals[0] > localHighSignals[0])
                {
                    localHighSignals[0] = signals[0];
                }
                if (signals[1] > localHighSignals[1])
                {
                    localHighSignals[1] = signals[1];
                }
                if (signals[2] > localHighSignals[2])
                {
                    localHighSignals[2] = signals[2];
                }
                if (signals[3] > localHighSignals[3])
                {
                    localHighSignals[3] = signals[3];
                }
                if (signals[4] > localHighSignals[4])
                {
                    localHighSignals[4] = signals[4];
                }
                if (signals[5] > localHighSignals[5])
                {
                    localHighSignals[5] = signals[5];
                }
                if (signals[6] > localHighSignals[6])
                {
                    localHighSignals[6] = signals[6];
                }
                if (signals[7] > localHighSignals[7])
                {
                    localHighSignals[7] = signals[7];
                }
                if (signals[8] > localHighSignals[8])
                {
                    localHighSignals[8] = signals[8];
                }
                if (signals[9] > localHighSignals[9])
                {
                    localHighSignals[9] = signals[9];
                }
                if (signals[10] > localHighSignals[10])
                {
                    localHighSignals[10] = signals[10];
                }
                if (signals[11] > localHighSignals[11])
                {
                    localHighSignals[11] = signals[11];
                }
                if (signals[12] > localHighSignals[12])
                {
                    localHighSignals[12] = signals[12];
                }
                if (signals[13] > localHighSignals[13])
                {
                    localHighSignals[13] = signals[13];
                }
                if (signals[14] > localHighSignals[14])
                {
                    localHighSignals[14] = signals[14];
                }
                if (signals[15] > localHighSignals[15])
                {
                    localHighSignals[15] = signals[15];
                }
            }

            ((SignalBusMasterLogic) getMultiblockMaster()).updateBusSignals(getCoordInWorld(), localHighSignals);

            northboundSignalsChanged = false;

        }

        broadcastSouthboundSignals();
    }

    public boolean registerTerminal (World world, int x, int y, int z, boolean rehome)
    {
        if (worldObj.isRemote)
        {
            return false;
        }
        if (world == worldObj && world.isRemote == worldObj.isRemote)
        {
            if (worldObj.getBlockTileEntity(x, y, z) instanceof ISignalTransceiver)
            {
                TileEntity te = world.getBlockTileEntity(x, y, z);
                CoordTuple coords = new CoordTuple(x, y, z);
                byte[] signals = null;

                if (transceivers.containsKey(coords))
                {
                    return true;
                }
                int dropWire = ((ISignalTransceiver) te).doUnregister(rehome);
                if (dropWire > 0)
                {
                    Random rand = new Random();
                    ItemStack tempStack = new ItemStack(TConstruct.instance.content.lengthWire, dropWire);
                    float jumpX = rand.nextFloat() * 0.8F + 0.1F;
                    float jumpY = rand.nextFloat() * 0.8F + 0.1F;
                    float jumpZ = rand.nextFloat() * 0.8F + 0.1F;

                    EntityItem entityitem = new EntityItem(world, (double) ((float) xCoord + jumpX), (double) ((float) yCoord + jumpY), (double) ((float) zCoord + jumpZ), tempStack);

                    float offset = 0.05F;
                    entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
                    entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
                    entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
                    world.spawnEntityInWorld(entityitem);
                }
                ((ISignalTransceiver) te).setBusCoords(world, xCoord, yCoord, zCoord);

                signals = ((ISignalTransceiver) te).getReceivedSignals();

                transceivers.put(coords, signals);
                northboundSignalsChanged = true;
                
                updateLocalSignals(((SignalBusMasterLogic)getMultiblockMaster()).getSignals());
                ((ISignalTransceiver) te).receiveSignalUpdate(cachedReceivedSignals);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDetached (MultiblockMasterBaseLogic oldMaster)
    {

        super.onDetached(oldMaster);
    }

    @Override
    public void onAttached (MultiblockMasterBaseLogic newMaster)
    {
        super.onAttached(newMaster);

        if (transceivers != null || !transceivers.isEmpty())
        {
            northboundSignalsChanged = true;
            
            updateLocalSignals(((SignalBusMasterLogic)getMultiblockMaster()).getSignals());
        }
    }

    public boolean isRegisteredTerminal (World world, int x, int y, int z)
    {
        if (worldObj.isRemote)
        {
            return false;
        }
        return transceivers.containsKey(new CoordTuple(x, y, z));
    }

    public boolean unregisterTerminal (World world, int x, int y, int z)
    {
        if (worldObj.isRemote)
        {
            return false;
        }
        if (transceivers.remove(new CoordTuple(x, y, z)) != null)
        {
            northboundSignalsChanged = true;
            return true;
        }
        return false;
    }

    public boolean hasTerminals ()
    {
        return (transceivers.size() > 0);
    }

    @Override
    public boolean isCompatible (Object other)
    {
        return (other.getClass() == this.getClass());
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        int temp = tags.getInteger("placedSides");

        for (int i = 0; i < 6; ++i)
        {
            placedSides[i] = (temp >> i & 1) == 1;
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        int temp = 0;

        for (int i = 0; i < 6; ++i)
        {
            if (placedSides[i])
            {
                temp |= 1 << i;
            }
        }

        tags.setInteger("placedSides", temp);
    }

    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public boolean getActive ()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setActive (boolean flag)
    {
        // TODO Auto-generated method stub

    }

    public boolean[] placedSides ()
    {
        return placedSides.clone();
    }

    public boolean[] connectedSides (ForgeDirection fromFace)
    {
        boolean[] connected = new boolean[6];

        TileEntity te;
        ForgeDirection dir;
        int neighborX;
        int neighborY;
        int neighborZ;
        for (int i = 0; i < 6; ++i)
        {
            if (i == fromFace.ordinal() || i == fromFace.getOpposite().ordinal())
            {
                continue;
            }
            dir = ForgeDirection.getOrientation(i);
            neighborX = xCoord + dir.offsetX;
            neighborY = yCoord + dir.offsetY;
            neighborZ = zCoord + dir.offsetZ;

            te = worldObj.getBlockTileEntity(neighborX, neighborY, neighborZ);
            connected[i] = (te instanceof ISignalBusConnectable && ((ISignalBusConnectable) te).connectableOnFace(fromFace));
        }

        return connected;
    }

    // Face is the face in the block; e.g. DOWN = on top of the block below.
    public boolean connectableOnFace (ForgeDirection side)
    {
        return placedSides[side.ordinal()];
    }

    public boolean isConnected (ForgeDirection side, ForgeDirection dir)
    {
        TileEntity te;

        switch (dir)
        {
        case DOWN:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof SignalBusLogic);
        case NORTH:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof SignalBusLogic);
        case SOUTH:
            return (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof SignalBusLogic);
        case WEST:
            return (this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof SignalBusLogic);
        case EAST:
            return (this.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof SignalBusLogic);
        default:
            return false;
        }
    }

    @Override
    public MultiblockMasterBaseLogic getNewMultiblockMasterObject ()
    {
        return new SignalBusMasterLogic(this.worldObj);
    }

    public byte[] getLocalSignals ()
    {
        return localHighSignals;
    }

    public void updateTransceiverSignals (CoordTuple coords, byte[] signals)
    {
        if (!Arrays.equals(signals, transceivers.get(coords)))
        {
            transceivers.put(coords, signals);
            northboundSignalsChanged = true;
        }
    }

    // Southbound signals update from master
    public void updateLocalSignals (byte[] signals)
    {
        if (!Arrays.equals(signals, cachedReceivedSignals))
        {
            cachedReceivedSignals = signals.clone();
            southboundSignalsChanged = true;
        }
    }

    public void addPlacedSide (int side)
    {
        placedSides[side] = true;
        if (!worldObj.isRemote)
        {
            forceCheck = true;
        }
    }

    public boolean[] getRenderCorners (ForgeDirection fromFace)
    {
        boolean[] corners = new boolean[6];

        TileEntity te;
        ForgeDirection dir;
        int neighborX;
        int neighborY;
        int neighborZ;
        for (int i = 0; i < 6; ++i)
        {
            if (i == fromFace.ordinal() || i == fromFace.getOpposite().ordinal())
            {
                continue;
            }
            dir = ForgeDirection.getOrientation(i);
            neighborX = xCoord + dir.offsetX;
            neighborY = yCoord + dir.offsetY;
            neighborZ = zCoord + dir.offsetZ;
            if (worldObj.isBlockOpaqueCube(neighborX, neighborY, neighborZ))
            {
                continue;
            }
            neighborX += fromFace.offsetX;
            neighborY += fromFace.offsetY;
            neighborZ += fromFace.offsetZ;

            te = worldObj.getBlockTileEntity(neighborX, neighborY, neighborZ);
            corners[i] = (te instanceof ISignalBusConnectable && ((ISignalBusConnectable) te).connectableOnCorner(fromFace.getOpposite(), dir.getOpposite()));
        }

        return corners;
    }

    @Override
    public boolean connectableOnCorner (ForgeDirection side, ForgeDirection turn)
    {
        if (!placedSides[side.ordinal()] && placedSides[turn.ordinal()])
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean willConnect (CoordTuple coord)
    {
        int i, j;
        ForgeDirection iDir, jDir;
        TileEntity te;
        
        for (i = 0; i < 6; ++i)
        {
            iDir = ForgeDirection.VALID_DIRECTIONS[i];
            if (!placedSides[i])
            {
                continue;
            } 

            for (j = 0; j < 6; ++j)
            {
                if (placedSides[j] || j == i || j == iDir.getOpposite().ordinal())
                {
                    continue;
                }
                jDir = ForgeDirection.VALID_DIRECTIONS[j];
                if (xCoord + jDir.offsetX == coord.x && yCoord + jDir.offsetY == coord.y && zCoord + jDir.offsetZ == coord.z)
                {
                    te = worldObj.getBlockTileEntity(xCoord + jDir.offsetX, yCoord + jDir.offsetY, zCoord + jDir.offsetZ);
                    if (te instanceof ISignalBusConnectable && ((ISignalBusConnectable)te).connectableOnFace(iDir))
                    {
                        return true;
                    }
                }
                if (worldObj.isBlockOpaqueCube(xCoord + jDir.offsetX, yCoord + jDir.offsetY, zCoord + jDir.offsetZ))
                {
                    continue;
                }
                if (xCoord + iDir.offsetX + jDir.offsetX == coord.x && yCoord + iDir.offsetY + jDir.offsetY == coord.y && zCoord + iDir.offsetZ + jDir.offsetZ == coord.z)
                {
                    te = worldObj.getBlockTileEntity(xCoord + iDir.offsetX + jDir.offsetX, yCoord + iDir.offsetY + jDir.offsetY, zCoord + iDir.offsetZ + jDir.offsetZ);
                    if (te instanceof ISignalBusConnectable && ((ISignalBusConnectable)te).connectableOnCorner(iDir.getOpposite(), jDir.getOpposite()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected CoordTuple[] getNeighborCoords ()
    {
        List<CoordTuple> cornerCoords = new LinkedList<CoordTuple>();

        int i, j;
        ForgeDirection iDir, jDir;
        CoordTuple coord;

        for (i = 0; i < 6; ++i)
        {
            if (!placedSides[i])
            {
                continue;
            }
            iDir = ForgeDirection.VALID_DIRECTIONS[i];
            for (j = 0; j < 6; ++j)
            {
                if (placedSides[j] || j == i || j == iDir.getOpposite().ordinal())
                {
                    continue;
                }
                jDir = ForgeDirection.VALID_DIRECTIONS[j];
                if (worldObj.isBlockOpaqueCube(xCoord + jDir.offsetX, yCoord + jDir.offsetY, zCoord + jDir.offsetZ))
                {
                    continue;
                }
                cornerCoords.add(new CoordTuple(xCoord + iDir.offsetX + jDir.offsetX, yCoord + iDir.offsetY + jDir.offsetY, zCoord + iDir.offsetZ + jDir.offsetZ));
            }
        }

        cornerCoords.addAll(Arrays.asList(super.getNeighborCoords()));
        CoordTuple[] tmp = new CoordTuple[cornerCoords.size()];
        return cornerCoords.toArray(tmp);
    }

    public void forceNeighborCheck ()
    {
        forceCheck = true;
    }

    public int getDroppedBuses ()
    {
        int calc = 0;
        for (int i = 0; i < 6; ++i)
        {
            if (placedSides[i])
            {
                ++calc;
            }
        }
        
        return calc;
    }

    public int getDroppedWire ()
    {
        int calc = 0;
        TileEntity te;
        for (CoordTuple coord : transceivers.keySet())
        {
            te = worldObj.getBlockTileEntity(coord.x, coord.y, coord.z);
            if (te instanceof ISignalTransceiver)
            {
                calc += ((ISignalTransceiver) te).getDroppedWire();
            }
        }
        
        return calc;
    }
    
    public void notifyBreak ()
    {
        TileEntity te;
        for (CoordTuple coord : transceivers.keySet())
        {
            te = worldObj.getBlockTileEntity(coord.x, coord.y, coord.z);
            if (te instanceof ISignalTransceiver)
            {
                ((ISignalTransceiver) te).doUnregister(true);
            }
        }
        this.destroySelf();
    }

    public boolean canPlaceOnSide (int side)
    {
        for (int i = 0; i < 6; ++i)
        {
            if (i == side || i == ForgeDirection.OPPOSITES[side])
            {
                continue;
            }
            if (placedSides[i])
            {
                return true;
            }
        }
        return false;
    }

    public int checkUnsupportedSides ()
    {
        int dropCount = 0;
        ForgeDirection iDir, sDir;
        for (int i = 0; i < 6; ++i)
        {
            if (!placedSides[i])
            {
                continue;
            }
            iDir = ForgeDirection.VALID_DIRECTIONS[i];
            sDir = ForgeDirection.VALID_DIRECTIONS[i].getOpposite();
            if (sDir == ForgeDirection.NORTH || sDir == ForgeDirection.SOUTH)
            {
                sDir = sDir.getOpposite();
            }
            if (!worldObj.isBlockSolidOnSide(xCoord + iDir.offsetX, yCoord + iDir.offsetY, zCoord + iDir.offsetZ, iDir.getOpposite()))
            {
                placedSides[i] = false;
                ++dropCount;
            }
        }
        
        return dropCount;
    }

    public boolean checkShouldDestroy ()
    {
        for (int i = 0; i < 6; ++i)
        {
            if (placedSides[i])
            {
                return false;
            }
        }
        return true;
    }
}
