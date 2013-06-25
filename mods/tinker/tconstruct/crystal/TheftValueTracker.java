package mods.tinker.tconstruct.crystal;

import java.util.ArrayList;
import java.util.HashMap;

import mods.tinker.tconstruct.library.util.ChunkCoordTuple;

public class TheftValueTracker
{
    public static HashMap<ChunkCoordTuple, ArrayList> chunkMap = new HashMap<ChunkCoordTuple, ArrayList>();
    //public static HashMap<ChunkCoordTuple, Integer> crystallinity = new HashMap<ChunkCoordTuple, Integer>();
    public static HashMap<Integer, HashMap<ChunkCoordTuple, Integer>> crystallinity = new HashMap<Integer, HashMap<ChunkCoordTuple, Integer>>();
    
    public static void updateCrystallinity(int posX, int posZ, int world, int value)
    {
        ChunkCoordTuple tuple = new ChunkCoordTuple((int)Math.floor(posX/16), (int)Math.floor(posZ/16));
        HashMap<ChunkCoordTuple, Integer> dimensionMap = crystallinity.get(world);
        int level = dimensionMap.get(tuple);
        level += value;
        dimensionMap.put(tuple, level);
    }
}
