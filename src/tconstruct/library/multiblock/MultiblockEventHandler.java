package tconstruct.library.multiblock;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class MultiblockEventHandler
{
    @ForgeSubscribe(priority = EventPriority.NORMAL)
    public void onChunkLoad (ChunkEvent.Load loadEvent)
    {
        Chunk chunk = loadEvent.getChunk();
        World world = loadEvent.world;
        if (world.isRemote)
        {
            return;
        }
        MultiblockRegistry.onChunkLoaded(world, ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition));
    }

    @ForgeSubscribe(priority = EventPriority.NORMAL)
    public void onChunkUnload (ChunkEvent.Unload unloadEvent)
    {
        Chunk chunk = unloadEvent.getChunk();
        World world = unloadEvent.world;
        if (world.isRemote)
        {
            return;
        }
        MultiblockRegistry.onChunkUnloaded(world, ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition));
    }

    @ForgeSubscribe(priority = EventPriority.NORMAL)
    public void onWorldUnload (WorldEvent.Unload unloadWorldEvent)
    {
        MultiblockRegistry.onWorldUnloaded(unloadWorldEvent.world);
    }
}
