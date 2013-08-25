package tconstruct.crystal;

import java.util.HashMap;

import tconstruct.library.util.ValueCoordTuple;

public class Crystallinity
{
    public enum CrystalType
    {
        Light, Life, Time, Stone, Fire, Water
    }
    
    public static HashMap<ValueCoordTuple, CrystalValues> theftValue = new HashMap<ValueCoordTuple, CrystalValues>(); //Value of materials
    public static HashMap<ValueCoordTuple, CrystalValues> charge = new HashMap<ValueCoordTuple, CrystalValues>();     //Balance of crystal

    public static void updateTheft (int dim, int posX, int posZ, int value, CrystalType type)
    {
        ValueCoordTuple coord = new ValueCoordTuple(dim, (int) Math.floor(posX / 16D), (int) Math.floor(posZ / 16D));
        CrystalValues crystal = theftValue.get(coord);
        if (crystal == null)
            crystal = new CrystalValues("Theft");
        crystal.addValue(value, type);
        theftValue.put(coord, crystal);
    }

    public static void updateCharge (int dim, int posX, int posZ, int value, CrystalType type)
    {
        ValueCoordTuple coord = new ValueCoordTuple(dim, (int) Math.floor(posX / 16D), (int) Math.floor(posZ / 16D));
        CrystalValues crystal = charge.get(coord);
        if (crystal == null)
            crystal = new CrystalValues("Charge");
        crystal.addValue(value, type);
        charge.put(coord, crystal);
    }
}
