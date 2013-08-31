package tconstruct.crystal;

import tconstruct.crystal.Crystallinity.CrystalType;
import net.minecraft.nbt.NBTTagCompound;

public class CrystalValues
{
    int light;
    int time;
    int life;
    int stone;
    int fire;
    int water;

    final String valueType;

    public CrystalValues(String type)
    {
        valueType = type;
    }

    public void addValue (int value, CrystalType type)
    {
        /*light = 0;
        time = 0;
        life = 0;
        stone = 0;
        fire = 0;
        water = 0;*/
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

    public int getValue (CrystalType type)
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

    protected void setValue (int[] array)
    {
        light = array[0];
        time = array[1];
        life = array[2];
        stone = array[3];
        fire = array[4];
        water = array[5];
    }

    public CrystalValues loadFromNBT (NBTTagCompound tags)
    {
        int[] crystalArray = tags.getIntArray("TConstruct.Crystal." + valueType);
        if (crystalArray != null && crystalArray.length >= 6)
        {
            this.setValue(crystalArray);
        }
        return this;
    }

    public void saveToNBT (NBTTagCompound tags)
    {
        int[] crystalArray = new int[] { light, time, life, stone, fire, water };
        tags.setIntArray("TConstruct.Crystal.", crystalArray);
    }
}