package tconstruct.library.tinkering.modifiers;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents the NBT data saved for a modifier.
 */
public abstract class ModifierNBT {

  protected final String key;

  protected ModifierNBT(String key) {
    this.key = key;
  }

  public abstract void write(NBTTagCompound tag);

  public String getInfo() {
    return "";
  }

  /**
   * Single boolean value
   */
  public static class Boolean extends ModifierNBT {

    public boolean status;

    public Boolean(String key) {
      super(key);
    }

    @Override
    public void write(NBTTagCompound tag) {
      tag.setBoolean(key, status);
    }

    public static Boolean read(NBTTagCompound tag, String key) {
      Boolean data = new Boolean(key);
      data.status = tag.getBoolean(key);
      return data;
    }
  }

  /**
   * Data can be applied multiple times up to a maximum.
   */
  public static class Integer extends ModifierNBT {

    public int current;
    public int max;

    public Integer(String key) {
      super(key);
    }

    @Override
    public void write(NBTTagCompound tag) {
      NBTTagCompound subTag = tag.getCompoundTag(key);
      subTag.setInteger("current", current);
      subTag.setInteger("max", max);
      tag.setTag(key, subTag);
    }

    public static Integer read(NBTTagCompound tag, String key) {
      Integer data = new Integer(key);
      NBTTagCompound subTag = tag.getCompoundTag(key);
      if (subTag != null) {
        data.current = subTag.getInteger("current");
        data.max = subTag.getInteger("max");
      }
      return data;
    }

    @Override
    public String getInfo() {
      if (max > 0) {
        return String.format("%d / %d", current, max);
      }

      return String.valueOf(current);
    }
  }
}
