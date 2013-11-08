package tconstruct.library.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.world.World;

public class MultiblockRegistry
{
    private static Set<MultiblockMasterBaseLogic> masters = new CopyOnWriteArraySet<MultiblockMasterBaseLogic>();
    private static HashMap<Integer, HashMap<Long, List<IMultiblockMember>>> preInitMembers = new HashMap<Integer, HashMap<Long, List<IMultiblockMember>>>();
    private static HashMap<Integer, HashMap<Long, List<IMultiblockMember>>> preInitMasters = new HashMap<Integer, HashMap<Long, List<IMultiblockMember>>>();
    private static HashMap<Integer, HashMap<Long, List<IMultiblockMember>>> loadedMembers = new HashMap<Integer, HashMap<Long, List<IMultiblockMember>>>();

    public static void tick (World world)
    {
        for (MultiblockMasterBaseLogic logic : masters)
        {
            if (logic.worldObj == world && logic.worldObj.isRemote == world.isRemote)
            {
                logic.doMultiblockTick();
            }
        }
    }

    public static void register (MultiblockMasterBaseLogic logic)
    {
        masters.add(logic);
    }

    public static void unregister (MultiblockMasterBaseLogic logic)
    {
        masters.remove(logic);
    }

    public static void onMemberLoad (World world, Long chunkCoord, IMultiblockMember member, boolean master)
    {
        HashMap<Integer, HashMap<Long, List<IMultiblockMember>>> pending = preInitMembers;
        if (master)
        {
            pending = preInitMasters;
        }

        int dimensionId = world.provider.dimensionId;
        putMemberInList(pending, dimensionId, chunkCoord, member);

    }

    public static void onChunkLoaded (World world, long chunkCoord)
    {
        int dimensionId = world.provider.dimensionId;
        List<IMultiblockMember> pending = getMembersFromListForWorldChunk(preInitMasters, dimensionId, chunkCoord);
        if (pending != null)
        {
            for (IMultiblockMember member : pending)
            {
                member.onChunkLoad();
            }
            preInitMasters.get(dimensionId).remove(chunkCoord);
        }

        pending = getMembersFromListForWorldChunk(preInitMembers, dimensionId, chunkCoord);
        if (pending != null)
        {
            for (IMultiblockMember member : pending)
            {
                member.onChunkLoad();
            }
            preInitMembers.get(dimensionId).remove(chunkCoord);
        }
    }

    public static void onChunkUnloaded (World world, long chunkCoord)
    {
        int dimensionId = world.provider.dimensionId;
        List<IMultiblockMember> pending = getMembersFromListForWorldChunk(loadedMembers, dimensionId, chunkCoord);
        if (pending != null)
        {
            for (IMultiblockMember member : pending)
            {
                member.onChunkUnloaded();
            }
            loadedMembers.get(dimensionId).remove(chunkCoord);
        }
    }

    public static void registerMember (World world, long chunkCoord, IMultiblockMember member)
    {
        putMemberInList(loadedMembers, world.provider.dimensionId, chunkCoord, member);
    }

    public static void onWorldUnloaded (World world)
    {
        List<MultiblockMasterBaseLogic> mastersToRemove = new ArrayList<MultiblockMasterBaseLogic>();
        for (MultiblockMasterBaseLogic master : mastersToRemove)
        {
            if (master.worldObj.isRemote == world.isRemote && master.worldObj.provider.dimensionId == world.provider.dimensionId)
            {
                mastersToRemove.add(master);
            }
        }

        masters.removeAll(mastersToRemove);
    }

    private static List<IMultiblockMember> getMembersFromListForWorldChunk (HashMap<Integer, HashMap<Long, List<IMultiblockMember>>> source, int dimensionId, long chunkCoord)
    {
        if (!source.containsKey(dimensionId))
        {
            return null;
        }

        if (!source.get(dimensionId).containsKey(chunkCoord))
        {
            return null;
        }

        return source.get(dimensionId).get(chunkCoord);
    }

    private static void putMemberInList (HashMap<Integer, HashMap<Long, List<IMultiblockMember>>> destList, int dimensionId, long chunkCoord, IMultiblockMember member)
    {
        if (!destList.containsKey(dimensionId))
        {
            destList.put(dimensionId, new HashMap<Long, List<IMultiblockMember>>());
        }

        HashMap<Long, List<IMultiblockMember>> innerMap = destList.get(dimensionId);

        if (!innerMap.containsKey(chunkCoord))
        {
            innerMap.put(chunkCoord, new ArrayList<IMultiblockMember>());
        }

        innerMap.get(chunkCoord).add(member);
    }
}
