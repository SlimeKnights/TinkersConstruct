package tconstruct.library.multiblock;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tconstruct.library.util.CoordTuple;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public abstract class MultiblockBaseLogic extends TileEntity implements IMultiblockMember
{

    private MultiblockMasterBaseLogic master;
    private byte visited;
    private static final byte kUnvisited = 0;
    private static final byte kVisited = 1;

    private boolean saveMultiblockData;
    private NBTTagCompound cachedMultiblockData;
    private boolean doUpdate;
    private boolean pendingDestroy = false;

    public MultiblockBaseLogic()
    {
        super();
        master = null;
        visited = kUnvisited;
        saveMultiblockData = false;
        doUpdate = false;
        cachedMultiblockData = null;
    }

    public boolean isCompatible (Object other)
    {
        return other.getClass().equals(this.getClass());
    }

    public void doMultiblockTick ()
    {
    }

    @Override
    public boolean isConnected ()
    {
        return (master != null);
    }

    @Override
    public boolean willConnect (CoordTuple coord)
    {
        return true;
    }

    @Override
    public MultiblockMasterBaseLogic getMultiblockMaster ()
    {
        return master;
    }

    @Override
    public CoordTuple getCoordInWorld ()
    {
        return new CoordTuple(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public void onAttached (MultiblockMasterBaseLogic newMaster)
    {
        this.master = newMaster;
    }

    @Override
    public void onDetached (MultiblockMasterBaseLogic oldMaster)
    {
        if (this.master != oldMaster)
        {
            throw new IllegalArgumentException("Detaching from wrong master");
        }

        this.master = null;
    }

    @Override
    public void createNewMultiblock ()
    {
        MultiblockMasterBaseLogic newMaster = getNewMultiblockMasterObject();
        newMaster.attachBlock(this);
    }

    @Override
    public void onMasterMerged (MultiblockMasterBaseLogic newMaster)
    {
        assert (this.master != newMaster);

        this.master = newMaster;
    }

    @Override
    public void setVisited ()
    {
        this.visited = kVisited;
    }

    @Override
    public void setUnivisted ()
    {
        this.visited = kUnvisited;
    }

    @Override
    public boolean isVisited ()
    {
        return (this.visited != kUnvisited || pendingDestroy);
    }

    @Override
    public void becomeMultiblockSaveDelegate ()
    {
        this.saveMultiblockData = true;
    }

    @Override
    public void forfeitMultiblockSaveDelegate ()
    {
        this.saveMultiblockData = false;
    }

    @Override
    public boolean isMultiblockSaveDelegate ()
    {
        return this.saveMultiblockData;
    }

    @Override
    public IMultiblockMember[] getNeighboringMembers ()
    {
        CoordTuple[] neighbors = getNeighborCoords();
        TileEntity te;
        List<IMultiblockMember> neighborMembers = new LinkedList<IMultiblockMember>();
        for (CoordTuple neighbor : neighbors)
        {
            te = this.worldObj.getBlockTileEntity(neighbor.x, neighbor.y, neighbor.z);
            if (te instanceof IMultiblockMember)
            {
                // Verify compatible member
                if (((IMultiblockMember) te).isCompatible((Object) this) && ((IMultiblockMember) te).willConnect(getCoordInWorld()))
                {
                    neighborMembers.add((IMultiblockMember) te);
                }
            }
        }
        IMultiblockMember[] tmp = new IMultiblockMember[neighborMembers.size()];
        return neighborMembers.toArray(tmp);
    }

    @Override
    public void onBlockAdded (World world, int x, int y, int z)
    {
        if (pendingDestroy)
        {
            return;
        }

        IMultiblockMember[] neighbors = getNeighboringMembers();

        List<MultiblockMasterBaseLogic> masters = new LinkedList<MultiblockMasterBaseLogic>();

        for (IMultiblockMember neighbor : neighbors)
        {
            if (neighbor.isConnected() && !masters.contains(neighbor.getMultiblockMaster()))
            {
                masters.add(neighbor.getMultiblockMaster());
            }
        }

        if (masters.size() > 0)
        {
            MultiblockMasterBaseLogic targetMaster = null;

            for (MultiblockMasterBaseLogic candidateMaster : masters)
            {
                if (targetMaster == null)
                {
                    targetMaster = candidateMaster;
                }
                else
                {
                    int comparison = targetMaster.getReferenceCoord().compareTo(candidateMaster.getReferenceCoord());
                    if (comparison < 0)
                    {
                        continue;
                    }
                    else if (comparison == 0)
                    {
                        throw new IllegalStateException(String.format("Found two controllers (hashes %d,  %d) with identical reference coord %s", targetMaster.hashCode(), candidateMaster.hashCode(),
                                targetMaster.getReferenceCoord().toString()));
                    }
                    else
                    {
                        targetMaster = candidateMaster;
                    }
                }
            }

            assert (targetMaster != null);

            masters.remove(targetMaster);
            targetMaster.attachBlock(this);

            if (masters.size() > 0)
            {
                CoordTuple hostLoc = this.master.getReferenceCoord();
                this.master.beginMerging();
                for (MultiblockMasterBaseLogic masterToMerge : masters)
                {
                    this.master.merge(masterToMerge);
                }
                this.master.endMerging();
            }
        }
        else
        {
            this.createNewMultiblock();
        }
    }

    @Override
    public void onOrphaned ()
    {
        if (this.isConnected() || pendingDestroy)
        {
            // Turns out, we're not an orphan after all
            return;
        }

        createNewMultiblock();
        // Now for fun. Add all neighbors and DFS out into the world.
        Queue<IMultiblockMember> membersToCheck = new LinkedList<IMultiblockMember>();
        // Add all unconnected neighbors
        CoordTuple[] neighborCoords = getNeighborCoords();
        for (CoordTuple coord : neighborCoords)
        {
            TileEntity neighborTE = this.worldObj.getBlockTileEntity(coord.x, coord.y, coord.z);
            if (neighborTE instanceof IMultiblockMember && !((IMultiblockMember) neighborTE).isConnected() && ((IMultiblockMember) neighborTE).willConnect(getCoordInWorld()))
            {
                membersToCheck.add((IMultiblockMember) neighborTE);
            }
        }

        IMultiblockMember member;
        while (!membersToCheck.isEmpty())
        {
            member = membersToCheck.remove();
            if (member.isConnected())
            {
                // This member has better things to do
                continue;
            }

            // We're already connected by virtue of the new master
            this.master.attachBlock(member);

            // Add all unconnected neighbors of this member
            IMultiblockMember[] neighborMembers = member.getNeighboringMembers();
            for (IMultiblockMember neighbor : neighborMembers)
            {
                if (!neighbor.isConnected())
                {
                    membersToCheck.add(neighbor);
                }
            }
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound data)
    {
        super.readFromNBT(data);

        // We can't directly initialize a multiblock master yet, so we cache the data here until
        // we receive a validate() call, which creates the controller and hands off the cached data.
        if (data.hasKey("multiblockData"))
        {
            this.cachedMultiblockData = data.getCompoundTag("multiblockData");
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound data)
    {
        super.writeToNBT(data);

        if (this.saveMultiblockData)
        {
            NBTTagCompound multiblockData = new NBTTagCompound();
            this.master.writeToNBT(multiblockData);
            data.setCompoundTag("multiblockData", multiblockData);
        }
    }

    // Ignore Vanilla TE updates
    @Override
    public boolean canUpdate ()
    {
        return false;
    }

    @Override
    public void invalidate ()
    {
        super.invalidate();

        detachSelf(false);
    }

    @Override
    public void onChunkUnload ()
    {
        super.onChunkUnload();

        if (this.worldObj.isRemote)
        {
            detachSelf(true);
        }
    }

    @Override
    public void onChunkLoad ()
    {
        if (this.cachedMultiblockData != null)
        {
            // We need to create a multiblock BUT we cannot check the world yet.
            // So we do something stupid and special.
            MultiblockMasterBaseLogic newMaster = getNewMultiblockMasterObject();
            newMaster.restore(this.cachedMultiblockData);
            this.cachedMultiblockData = null;
            newMaster.attachBlock(this); // This should grab any other connected blocks in the chunk
        }
        else
        {
            if (!this.isConnected())
            {
                // Ignore blocks that are already connected
                this.onBlockAdded(this.worldObj, xCoord, yCoord, zCoord);
            }
        }
    }

    @Override
    public void onChunkUnloaded ()
    {
        detachSelf(true);
    }

    @Override
    public void validate ()
    {
        super.validate();

        if (!this.worldObj.isRemote)
        {
            MultiblockRegistry.registerMember(this.worldObj, ChunkCoordIntPair.chunkXZ2Int(xCoord >> 4, zCoord >> 4), this);

            if (!this.worldObj.getChunkProvider().chunkExists(xCoord >> 4, zCoord >> 4))
            {
                boolean master = this.cachedMultiblockData != null;
                MultiblockRegistry.onMemberLoad(this.worldObj, ChunkCoordIntPair.chunkXZ2Int(xCoord >> 4, zCoord >> 4), this, master);
            }
        }
    }

    @Override
    public abstract MultiblockMasterBaseLogic getNewMultiblockMasterObject ();

    protected void attachSelf (World world, MultiblockMasterBaseLogic newMaster)
    {
        this.master = newMaster;
        this.master.attachBlock(this);
    }

    protected void detachSelf (boolean chunkUnloading)
    {
        if (this.master != null)
        {
            this.master.detachBlock(this, chunkUnloading);
            this.master = null;
        }
    }

    protected void destroySelf ()
    {
        pendingDestroy = true;
        //        this.detachSelf(false);
        this.getMultiblockMaster().scheduleRemoveAndRevisit(this);
    }

    protected CoordTuple[] getNeighborCoords ()
    {
        return new CoordTuple[] { new CoordTuple(this.xCoord - 1, this.yCoord, this.zCoord), new CoordTuple(this.xCoord, this.yCoord - 1, this.zCoord),
                new CoordTuple(this.xCoord, this.yCoord, this.zCoord - 1), new CoordTuple(this.xCoord, this.yCoord, this.zCoord + 1), new CoordTuple(this.xCoord, this.yCoord + 1, this.zCoord),
                new CoordTuple(this.xCoord + 1, this.yCoord, this.zCoord) };
    }
}
