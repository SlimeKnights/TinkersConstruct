package tconstruct.blocks.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.blocks.logic.SignalBusLogic;
import tconstruct.library.multiblock.IMultiblockMember;
import tconstruct.library.multiblock.MultiblockMasterBaseLogic;
import tconstruct.library.util.CoordTuple;

public class SignalBusMasterLogic extends MultiblockMasterBaseLogic
{
    private boolean forceUpdate = false;
    private boolean forceSouthboundUpdates = false;
    private boolean signalUpdate = false;
    private byte[] masterSignals = new byte[16];
    private CoordTuple[] signalProviderCoords = new CoordTuple[16];

    //private List<CoordTuple> tetheredBuses = new LinkedList<CoordTuple>(); // Buses that contain linked Terminals
    private Map<CoordTuple, byte[]> tetheredBuses = new HashMap<CoordTuple, byte[]>();

    public SignalBusMasterLogic(World world)
    {
        super(world);

        for (int i = 0; i < 16; i++)
        {
            masterSignals[i] = 0;
        }
    }

    @Override
    public boolean doUpdate ()
    {
        if (worldObj.isRemote || !forceUpdate)
        {
            return false;
        }

        forceUpdate = false;

        TileEntity te;

        // Calculate new signals from last tick's information
        byte[] oldSignals = masterSignals;
        masterSignals = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        for (byte[] signals : tetheredBuses.values())
        {
            calcSignals(signals);
        }

        if (forceSouthboundUpdates || !Arrays.equals(oldSignals, masterSignals))
        {
            // Send updates to SignalBuses
            for (CoordTuple coord : tetheredBuses.keySet())
            {
                if (worldObj.getChunkProvider().chunkExists(coord.x >> 4, coord.z >> 4))
                {
                    te = worldObj.getBlockTileEntity(coord.x, coord.y, coord.z);
                    if (te instanceof SignalBusLogic)
                    {
                        ((SignalBusLogic) te).updateLocalSignals(masterSignals);
                        ((SignalBusLogic) te).multiBlockTick();
                    }
                }
            }
        }

        return true;
    }

    public void updateBusSignals (CoordTuple bus, byte[] signals)
    {
        if (!Arrays.equals(tetheredBuses.get(bus), signals))
        {
            tetheredBuses.put(bus, signals);
            forceUpdate = true;
        }
    }

    public byte[] getSignals ()
    {
        return masterSignals.clone();
    }

    @Override
    protected void onBlockAdded (IMultiblockMember newMember)
    {

    }

    @Override
    protected void onBlockRemoved (IMultiblockMember oldMember)
    {
        if (tetheredBuses.containsKey(oldMember.getCoordInWorld()))
        {
            tetheredBuses.remove(oldMember.getCoordInWorld());
            forceUpdate = true;
        }
    }

    @Override
    protected void onDataMerge (MultiblockMasterBaseLogic newMaster)
    {
        byte[] oldSignals = ((SignalBusMasterLogic) newMaster).getSignals();

        byte[] newMasterSignals = ((SignalBusMasterLogic) newMaster).getSignals();

        if (tetheredBuses.size() > 0)
        {
            ((SignalBusMasterLogic) newMaster).mergeTethered(tetheredBuses);
        }

        ((SignalBusMasterLogic) newMaster).calcSignals(masterSignals);

        ((SignalBusMasterLogic) newMaster).forceUpdate();


    }

    @Override
    public void endMerging ()
    {
        forceSouthboundUpdates = true;
    }

    protected void calcSignals (byte[] signals)
    {
        if (signals[0] > masterSignals[0])
        {
            masterSignals[0] = signals[0];
        }
        if (signals[1] > masterSignals[1])
        {
            masterSignals[1] = signals[1];
        }
        if (signals[2] > masterSignals[2])
        {
            masterSignals[2] = signals[2];
        }
        if (signals[3] > masterSignals[3])
        {
            masterSignals[3] = signals[3];
        }
        if (signals[4] > masterSignals[4])
        {
            masterSignals[4] = signals[4];
        }
        if (signals[5] > masterSignals[5])
        {
            masterSignals[5] = signals[5];
        }
        if (signals[6] > masterSignals[6])
        {
            masterSignals[6] = signals[6];
        }
        if (signals[7] > masterSignals[7])
        {
            masterSignals[7] = signals[7];
        }
        if (signals[8] > masterSignals[8])
        {
            masterSignals[8] = signals[8];
        }
        if (signals[9] > masterSignals[9])
        {
            masterSignals[9] = signals[9];
        }
        if (signals[10] > masterSignals[10])
        {
            masterSignals[10] = signals[10];
        }
        if (signals[11] > masterSignals[11])
        {
            masterSignals[11] = signals[11];
        }
        if (signals[12] > masterSignals[12])
        {
            masterSignals[12] = signals[12];
        }
        if (signals[13] > masterSignals[13])
        {
            masterSignals[13] = signals[13];
        }
        if (signals[14] > masterSignals[14])
        {
            masterSignals[14] = signals[14];
        }
        if (signals[15] > masterSignals[15])
        {
            masterSignals[15] = signals[15];
        }
    }

    protected void mergeTethered (Map<CoordTuple, byte[]> oldMasterTethered)
    {
        tetheredBuses.putAll(oldMasterTethered);
    }

    @Override
    public void writeToNBT (NBTTagCompound data)
    {
        // Nothing important at the moment
    }

    @Override
    public void readFromNBT (NBTTagCompound data)
    {
        // Nothing important at the moment
    }

    @Override
    public void formatDescriptionPacket (NBTTagCompound data)
    {
        // Nothing important at the moment
    }

    @Override
    public void decodeDescriptionPacket (NBTTagCompound data)
    {
        // Nothing important at the moment
    }

    public void forceUpdate ()
    {
        forceUpdate = true;
    }

}
