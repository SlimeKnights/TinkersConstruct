package mods.tinker.tconstruct.crystal;

import mods.tinker.tconstruct.crystal.Crystallinity.CrystalType;
import net.minecraft.nbt.NBTTagCompound;

public class CrystalValues
{
    int light;
    int time;
    int life;
    int stone;
    int fire;
    int water;

    public void addCrystallinity (int value, CrystalType type)
    {
        switch (type)
        {
        case Light:
            light += value;
        case Time:
            time += value;
        case Life:
            life += value;
        case Stone:
            stone += value;
        case Fire:
            fire += value;
        case Water:
            water += value;
        }
    }

    public int getCrystallinity (CrystalType type)
    {
        switch (type)
        {
        case Light:
            return light;
        case Time:
            return time;
        case Life:
            return life;
        case Stone:
            return stone;
        case Fire:
            return fire;
        case Water:
            return water;
        }
        return 0;
    }
    
    public void setCrystallinity(int[] array)
    {
        light = array[0];
        time = array[1];
        life = array[2];
        stone = array[3];
        fire = array[4];
        water = array[5];
    }
    
    public CrystalValues loadFromNBT(NBTTagCompound tags)
    {
        int[] crystalArray = tags.getIntArray("TConstruct.Crystal");
        if (crystalArray != null && crystalArray.length >= 6)
        {
            this.setCrystallinity(crystalArray);
        }
        return this;
    }
    
    public void saveToNBT(NBTTagCompound tags)
    {
        int[] crystalArray = new int[] {light, time, life, stone, fire, water};
        tags.setIntArray("TConstruct.Crystal", crystalArray);
    }
}