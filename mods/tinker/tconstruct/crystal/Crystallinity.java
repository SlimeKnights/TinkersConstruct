package mods.tinker.tconstruct.crystal;

import java.util.ArrayList;
import java.util.HashMap;

import mods.tinker.tconstruct.library.util.ValueCoordTuple;
import mods.tinker.tconstruct.library.util.CoordTuple;

public class Crystallinity
{
    public static HashMap<ValueCoordTuple, CrystalValues> crystallinity = new HashMap<ValueCoordTuple, CrystalValues>();

    public static void updateCrystal (int dim, int posX, int posZ, int value, CrystalType type)
    {
        ValueCoordTuple coord = new ValueCoordTuple(dim, posX / 16, posZ / 16);
        CrystalValues crystal = crystallinity.get(coord);
        if (crystal == null)
            crystal = new CrystalValues();
        crystal.addCrystallinity(value, type);
        crystallinity.put(coord, crystal);
    }
    
    public enum CrystalType
    {
        Light, Life, Time, Stone, Fire, Water
    }

}
