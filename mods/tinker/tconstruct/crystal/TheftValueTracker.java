package mods.tinker.tconstruct.crystal;

import java.util.HashMap;

import mods.tinker.tconstruct.library.util.ValueCoordTuple;

public class TheftValueTracker
{
    public static HashMap<ValueCoordTuple, Integer> crystallinity = new HashMap<ValueCoordTuple, Integer>();

    //public static HashMap<Integer, HashMap<ChunkCoordTuple, Integer>> crystallinity = new HashMap<Integer, HashMap<ChunkCoordTuple, Integer>>();

    public static void updateCrystallinity (int dim, int posX, int posZ, int value)
    {
        ValueCoordTuple coord = new ValueCoordTuple(dim, posX / 16, posZ / 16);
        int crystal = crystallinity.get(coord);
        crystal += value;
        crystallinity.put(coord, crystal);
        /*ChunkCoordTuple tuple = new ChunkCoordTuple(posX / 16, posZ / 16);
        HashMap<ChunkCoordTuple, Integer> dimensionMap = crystallinity.get(world);
        if (dimensionMap.containsKey(tuple))
        {
            int level = dimensionMap.get(tuple);
            level += value;
            dimensionMap.put(tuple, level);
        }
        else
        {
            System.out.println("[TConstruct] Missing Theft value data at "+tuple);

            HashMap<ChunkCoordTuple, Integer> crystalMap = TheftValueTracker.crystallinity.get(world);
            if (crystalMap == null)
            {
                crystalMap = new HashMap<ChunkCoordTuple, Integer>();
                TheftValueTracker.crystallinity.put(world, crystalMap);
            }
            crystalMap.put(tuple, value);
            
        }*/
    }
}
